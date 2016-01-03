package snsgroupsix.londonbusrider;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint("NewApi") public class RouteOptionsActivity extends ListActivity
{
	public static String TAG = "RouteOptionsActivity";
	public static String ORIGIN = "com.example.android.busplanner.origin_is";
	public static String DESTINATION = "com.example.android.busplanner.destination_is";
	public static String DATE = "com.example.android.busplanner.date_is";
	public static String TIME = "com.example.android.busplanner.time_is";

	//private static final String TAG = "RouteOptionsActivity";
	public static final String APP_Key = "fd374b3d6f60e68914204c413172ab2b";//appKEY from tfl
	public static final String APP_Id = "cc63687a";//appID from tfl
	
	public static String Origin;
	public static String Destination;
	public static String Date;
	public static String Time;
	public static String tflAPIURL;
	public static String baseURL = "http://api.tfl.gov.uk/Journey/JourneyResults/from/to/to?";
	public static String jsonString;
	public static String Departure;
	public static String Arrival;
	public static String Duration;

	String message;
	private ProgressDialog dialog;
	
	//Hash map for journey ListView
	ArrayList<HashMap<String, String>> journeysList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routeoptions);
		
		//creating an intent to get Intent Extras from the PlannerMainActivity, to use for http url.
		Intent i = getIntent();
		Bundle b = new Bundle();
		b = i.getExtras();
		if(b!=null)
		{
			Origin = i.getStringExtra(ORIGIN);
			Destination =  i.getStringExtra(DESTINATION);
			Date = i.getStringExtra(DATE);
			Time = i.getStringExtra(TIME);
		}
		ListView lv = getListView();
		
		//journeysList HashMap to hold journeyList information obtained from URLRequest class
		journeysList = new ArrayList<HashMap<String, String>>();
		
		//on click of an item on the ListView
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				// TODO Auto-generated method stub				
				String label = ((TextView) view.findViewById(R.id.JourneyLabel)).getText().toString();
				String Id = ((TextView) view.findViewById(R.id.Id)).getText().toString();
				String departure = ((TextView) view.findViewById(R.id.DepartureTime)).getText().toString();
				String arrival = ((TextView) view.findViewById(R.id.ArrivalTime)).getText().toString();
				String duration = ((TextView) view.findViewById(R.id.Duration)).getText().toString();
				String leg = ((TextView)view.findViewById(R.id.Leg)).getText().toString();
				
				//creating a new Intent to communicate with the SingleRouteActivity
				Intent in = new Intent(getApplicationContext(), SingleRouteActivity.class);
				in.putExtra("Origin", Origin);
				in.putExtra("Destinaion", Destination);
				in.putExtra("Label", label);
				in.putExtra("Index", Id);
				in.putExtra("Departure", departure);
				in.putExtra("Arrival", arrival);
				in.putExtra("Duration", duration);
				in.putExtra("Leg", leg);
				
				//starting the SingleRouteActivity
				startActivity(in);
			}

		});
		
		//calling AsyncTask to obtain journey information(i.e journeyList) from URLRequest class
		new GetRoutes().execute();
	}
	
	
	
	private class GetRoutes extends AsyncTask <Void, Void, Void>
	{
		@Override
		protected void onPreExecute()
		{	super.onPreExecute();
		//show progress dialog
			dialog = new ProgressDialog(RouteOptionsActivity.this);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();
		}
		@Override
		protected Void doInBackground(Void... params)
		{
			//creating complete http url using baseURL and appending request parameters, appID and appKey
			String url = Uri.parse(baseURL).buildUpon().appendQueryParameter("from", Origin).
				appendQueryParameter("to", Destination).appendQueryParameter("date", Date).
				appendQueryParameter("time", Time).appendQueryParameter("timeIs", "Departing").
				appendQueryParameter("mode", "bus").appendQueryParameter("app_id", APP_Id).
				appendQueryParameter("app_key", APP_Key).build().toString();
			
			//new URLRequest object
				URLRequest request = new URLRequest();
				request.getComponents(url);
				
				//calling obtainJourneyList method in URLRequest class to get jouneyList HashMap holding Journey 
				//Information
				journeysList = request.obtainJourneyList();
				return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			if(dialog.isShowing());
			{
				//dismiss progress dialog
				dialog.dismiss();
			}
			
			//Key of strings in the journeysList HashMap
			String[] from = new String[] {"Tag", "Index", "Depart", "Arrive", "Length", "Leg"};
			
			//Ids of the textview to display the information on
			int [] to = new int[] {R.id.JourneyLabel, R.id.Id, R.id.DepartureTime, R.id.ArrivalTime, R.id.Duration, R.id.Leg};
			
			//updating the parsed json information stored in journeysList to ListView
			ListAdapter adapter = new SimpleAdapter(RouteOptionsActivity.this, journeysList, R.layout.routeoptions, from, to);
			setListAdapter(adapter);
		}	
	}
}
