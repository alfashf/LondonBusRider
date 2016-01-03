package snsgroupsix.londonbusrider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.maps.GoogleMap.*;


public class MapsActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnMarkerClickListener, OnMapClickListener, OnMyLocationButtonClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location previousSavedLocation;
    private LocationRequest mLocationRequest;
    private LatLng londonCoordinate = new LatLng(51.5286416,-0.1015987); //make the map centered at London
    private LatLng userCoordinate;
    private Marker stopMarker;

    boolean mRequestingLocationUpdates = true;
    boolean isArrivalTimeShown = false;
    boolean isConnectionPoor = false;

    String slidingPanelTitle = new String();
    String stopCode = new String();
    String[] busStopList;
    String[] busStopName;
    String[] busStopCode;
    String[] busStopIndicator;
    String[] lineNumber;
    String[] busDestination;

    ArrayList<LatLng> latlngList = new ArrayList<>();
    ArrayList<String> arrivalParamPass= new ArrayList<>();
    ArrayList<Date> arrivalDate = new ArrayList<>();
    Timer timer = new Timer();

    LinearLayout slidePanelLayout;
    ListView infoListView;
    SlidingUpPanelLayout slidingLayout;
    TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        slidePanelLayout = (LinearLayout) findViewById(R.id.slidepanel_layout);
        infoListView = (ListView) findViewById(R.id.info_lv);
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        titleTextView = (TextView) findViewById(R.id.title_tv);

        slidingLayout.hidePanel();
        slidingLayout.setDragView(titleTextView);

        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        //setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onPause(){
        super.onPause();

        if((isArrivalTimeShown) && lineNumber!=null){
            showBusListView(lineNumber, busDestination);
            isArrivalTimeShown=false;

            if (!timer.equals(null))
                timer.cancel();
        }
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

        else
            setUpMap();
    }


    private void setUpMap() {

        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(londonCoordinate, 11));

        latlngList.clear();

        if(isNetworkAvailable())
            new GetBusStopLocation().execute(mLastLocation);
        else
            Toast.makeText(getApplicationContext(), "No Network Connection, try again later..", Toast.LENGTH_LONG).show();
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }//buildGoogleApiClient ends

    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if(mLastLocation!=null) {

            if(previousSavedLocation!=null) {
                if (previousSavedLocation.distanceTo(mLastLocation)>=100) {
                    userCoordinate = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    if(stopMarker!=null)
                        stopMarker.remove();
                    setUpMapIfNeeded();
                }
            }
            else {
                userCoordinate = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                setUpMapIfNeeded();
            }
        }//if ends

        else
            Toast.makeText(getApplicationContext(), "Unable to get location..", Toast.LENGTH_LONG).show();

    }//onConnected() ends

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    private void setMarker(){

        String markerTitle;

        if (busStopList.length > 0) {

            int idx = 0;

            for (int i = 0; i < (busStopList.length/2); i++) {
                markerTitle = busStopName[i] + ", Stop " + busStopIndicator[i];
                latlngList.add(new LatLng((Double.parseDouble(busStopList[idx])), (Double.parseDouble(busStopList[idx+1]))));
                mMap.setOnMarkerClickListener(this);
                stopMarker = mMap.addMarker(new MarkerOptions().position(latlngList.get(i)).title(markerTitle));

                idx=idx+2;
            }//for ends
        }//if ends

        else
            Toast.makeText(getApplicationContext(), R.string.notfound_toast, Toast.LENGTH_LONG).show();

        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationButtonClickListener(this);

    }//setMarker() ends

    private boolean isNetworkAvailable(){

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }//checkInternetConnection() ends

    private void showBusListView(final String[] lineNumberArray, final String[] destinationArray){

        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        for (int i=0; i<lineNumberArray.length; i++) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("data", lineNumberArray[i]);
            datum.put("subdata", "towards " +destinationArray[i]);
            data.add(datum);
        }//for ends

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_list_item_2,
                new String[] {"data", "subdata"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});

        if(slidingLayout.isPanelHidden())
            slidingLayout.showPanel();
        if(!slidingLayout.isPanelExpanded())
            slidingLayout.expandPanel();

        infoListView.setAdapter(adapter);
        infoListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arrivalParamPass.clear();
                arrivalParamPass.add(lineNumberArray[position]);
                arrivalParamPass.add(stopCode);

                final Handler handler = new Handler();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isNetworkAvailable())
                                    new GetArrivalTime().execute(arrivalParamPass);
                                else
                                    Toast.makeText(getApplicationContext(), "No Network Connection, try again later..", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                };
                timer = new Timer();
                timer.schedule(task, 0, 30000);
            }
        });

    }//showBusListView() ends

    private void showBusArrivalTime(ArrayList<Date> arrivalDate){

        Calendar c = Calendar.getInstance();
        Date currentDate = c.getTime();
        ArrayList<String> diffMinutesList = new ArrayList<>();

        Collections.sort(arrivalDate);

        for(int i=0; i<arrivalDate.size(); i++){
            long diff = arrivalDate.get(i).getTime() - currentDate.getTime();
            long diffMinutes = diff/(60*1000)%60;

            if(diffMinutes>0)
                diffMinutesList.add(i+1 + ". " + diffMinutes + " minutes");
            else
                diffMinutesList.add(i+1 + ". "+ "due");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, diffMinutesList);
        infoListView.setAdapter(adapter);
        infoListView.setOnItemClickListener(null);
        titleTextView.setText("Arrival time for " + arrivalParamPass.get(0));
        isArrivalTimeShown = true;

    }//showBusArrivalTime() ends

    private void showOperationDialog(int operation){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (operation==0){

            builder.setTitle("ERROR!");
            builder.setMessage("Sorry there was an error getting data from the Internet..");

            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    new GetBusStopLocation().execute(mLastLocation);
                }
            });

            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    finish();
                }
            });
        }//if ends

        else if (operation==1){

            builder.setTitle("ERROR!");
            builder.setMessage("Sorry there was an error getting data from the Internet, try again later..");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    finish();
                }
            });
        }//if ends

        else if (operation==2){

            builder.setTitle("ERROR!");
            builder.setMessage("London bus is not available in this location..");

            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                    finish();
                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

    }//showOperationDialog() ends

    @Override
    public void onBackPressed(){
        if((isArrivalTimeShown) && lineNumber!=null){
            titleTextView.setText("Bus available at " +slidingPanelTitle);
            showBusListView(lineNumber, busDestination);
            isArrivalTimeShown=false;

            if (!timer.equals(null))
                timer.cancel();
        }
        else if(slidingLayout.isPanelExpanded())
            slidingLayout.collapsePanel();
        else if(!slidingLayout.isPanelHidden())
            slidingLayout.hidePanel();
        else
        super.onBackPressed();
    }//onBackPressed() ends

    @Override
    public boolean onMarkerClick(Marker marker) {

        String markerId = marker.getId().replaceAll("m","");

        slidingPanelTitle = marker.getTitle();
        isArrivalTimeShown = false;

        if(!timer.equals(null))
            timer.cancel();

        if(isNetworkAvailable())
            new GetLineNumber().execute(Integer.parseInt(markerId));
        else
            Toast.makeText(getApplicationContext(), "No Network Connection, try again later..", Toast.LENGTH_LONG).show();

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        if(!slidingLayout.isPanelHidden()) {
            slidingLayout.hidePanel();
            isArrivalTimeShown = false;

            if(!timer.equals(null))
                timer.cancel();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {

        previousSavedLocation = mLastLocation;

        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();

        return false;
    }

    private class GetBusStopLocation extends AsyncTask<Location, Void, String[]>{

        private ProgressDialog dialog = new ProgressDialog(MapsActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading nearby bus stop data...");
            this.dialog.show();
            this.dialog.setCancelable(false);
        }

        @Override
        protected String[] doInBackground(Location... lastLocation) {

            BusStop busStop = new BusStop(lastLocation[0].getLatitude(), lastLocation[0].getLongitude(), 750);

            if(busStop.isHttpSuccessful()) {
                busStopList = busStop.getGeoCodeList();
                busStopIndicator = busStop.getStopIndicatorList();
                busStopCode = busStop.getStopCodeList();

                busStopName = new String[busStopCode.length];

                for (int i = 0; i < busStopCode.length; i++) {
                    busStopName[i] = busStop.getStopName(busStopCode[i]);
                }//for ends

                if (busStopList!=null)
                    return busStopList;
                else
                    return null;
            }//if ends

            else if(!(busStop.isHttpReplied())) {
                isConnectionPoor = true;
                return null;
            }

            else
            return null;

        }//doInBackground() ends

        protected void onPostExecute(String[] busStopList) {

            if(dialog.isShowing())
                dialog.dismiss();

            if(busStopList != null) {
                setMarker();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userCoordinate, 14));
            }//if ends

            else {
                if(isConnectionPoor) {
                    showOperationDialog(0);
                    isConnectionPoor = false;
                }
                else
                    showOperationDialog(2);
            }//else ends
        }//onPostExecute() End
    }//GetStopLocation Class ends

    private class GetLineNumber extends AsyncTask<Integer, Void, BusThrough> {

        @Override
        protected BusThrough doInBackground(Integer... markerIdAsync) {

            BusThrough busThrough = new BusThrough(busStopCode[markerIdAsync[0]]);

            if(busThrough.isHttpSuccessful()) {
                stopCode = busStopCode[markerIdAsync[0]];
                return busThrough;
            }//if ends

            else
                return null;

        }//doInBackground(ends)

        protected  void onPostExecute(final BusThrough busThrough){

            if(busThrough != null) {
                if (busThrough.busThroughArr.length > 0 && !slidingPanelTitle.equals(null))
                    titleTextView.setText("Bus available at " + slidingPanelTitle);
                else if (!slidingPanelTitle.equals(null))
                    titleTextView.setText("No bus currently available at " + slidingPanelTitle);

                lineNumber = busThrough.busThroughArr;
                busDestination = busThrough.busDestinationArr;
                showBusListView(lineNumber, busDestination);
            }

            else
                showOperationDialog(1);

        }//onPostExecute() ends
    }//GetLineNumber class ends

    private class GetArrivalTime extends AsyncTask<ArrayList<String>, Void, ArrayList<Date>>{

        @Override
        protected ArrayList<Date> doInBackground(ArrayList<String>... arrivalAsyncPass) {

            BusArrival busArrival = new BusArrival(arrivalAsyncPass[0].get(0), arrivalAsyncPass[0].get(1));

            if(busArrival.isHttpSuccessful()) {
                arrivalDate = busArrival.getArrival();
                return arrivalDate;
            }

            else {
                timer.cancel();
                return null;
            }
        }//doInBackground() ends

        protected void onPostExecute(ArrayList<Date> arrivalDate){

            if(arrivalDate != null)
                showBusArrivalTime(arrivalDate);
            else
                showOperationDialog(1);

        }//onPostExecute() ends

    }//GetArrivalTime Class ends

}//MapsActivity Class ends
