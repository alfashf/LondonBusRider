package snsgroupsix.londonbusrider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SingleRouteActivity extends ListActivity 
{
	public static String Origin;
	public static String Destination;
	public static String Label;
	public static String index;
	public static String Departure;
	public static String Arrival;
	public static String Duration;
	public static String Departure1;
	public static String Arrival1;
	public static String Duration1;
	public static String Instructions;
	public static String ArrivalPoint;
	public static String Mode;
	public static String DeparturePoint;
	public static String Description;
	public static String stop;
	public static String Leg;
	public static final String TAG = "SingleRouteActivity";
	
	public static final String APP_Key = "fd374b3d6f60e68914204c413172ab2b";//appKEY from tfl
	public static final String APP_Id = "cc63687a";//appID from tfl
	public static String baseURL = "http://api.tfl.gov.uk/Journey/JourneyResults/from/to/to?";
	private ProgressDialog dialog;
	JSONArray journeys;
	String label;
	
	ArrayList<HashMap<String, String>> legList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleroute);
		
		Intent intent = getIntent();
		Origin = intent.getStringExtra("Origin");
		Destination=intent.getStringExtra("Destination");
		Label = intent.getStringExtra("Label");
		index = intent.getStringExtra("Index");
		Departure = intent.getStringExtra("Departure");
		Arrival = intent.getStringExtra("Arrival");
		Duration = intent.getStringExtra("Duration");
		Leg = intent.getStringExtra("Leg");
		
		TextView txt_Label = (TextView)findViewById(R.id.Label_single);
		TextView txt_Departure = (TextView)findViewById(R.id.Departure);
		TextView txt_Arrival = (TextView)findViewById(R.id.Arrival);
		TextView txt_Duration = (TextView)findViewById(R.id.DurationLength);
		//TextView txt_Leg = (TextView)findViewById(R.id.leg_List);
		
		//txt_Leg.setMovementMethod(new ScrollingMovementMethod());
		
		txt_Label.setText(Label);
		txt_Departure.setText(Departure);
		txt_Arrival.setText(Arrival);
		txt_Duration.setText(Duration);
		//txt_Leg.setText(Leg);
		
		ListView lv = getListView();
		//on click of an item on the ListView
		lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) 
			{
				String label = ((TextView)view.findViewById(R.id.LegTag)).getText().toString();
				String mode = ((TextView)view.findViewById(R.id.LegMode)).getText().toString();
				String departure1 = ((TextView)view.findViewById(R.id.LegDepart)).getText().toString();
				String arrival1 = ((TextView)view.findViewById(R.id.LegArrive)).getText().toString();
				String duration1 = ((TextView)view.findViewById(R.id.LegDuration)).getText().toString();
				String departurePoint = ((TextView)view.findViewById(R.id.LegDeparturePoint)).getText().toString();
				String arrivalPoint = ((TextView)view.findViewById(R.id.LegPoint)).getText().toString();
				String instructions = ((TextView)view.findViewById(R.id.LegInstruction)).getText().toString();
				String directions = ((TextView)view.findViewById(R.id.LegDirection)).getText().toString();
				String stops = ((TextView)view.findViewById(R.id.LegStop)).getText().toString();
				
				Intent in = new Intent(getApplicationContext(), SingleLegActivity.class);
				in.putExtra("Tag", label);
				in.putExtra("mode", mode);
				in.putExtra("departure1", departure1);
				in.putExtra("arrival1", arrival1);
				in.putExtra("duration1", duration1);
				in.putExtra("departPoint", departurePoint);
				in.putExtra("point", arrivalPoint);
				in.putExtra("instruct", instructions);
				in.putExtra("direct", directions);
				in.putExtra("stop", stops);
				
				startActivity(in);	
			}
	});

		
		new getLegs().execute();
	}	
	
	private class getLegs extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute()
		{	super.onPreExecute();
		//show progress dialog
			dialog = new ProgressDialog(SingleRouteActivity.this);
			dialog.setMessage("Loading Journey Information...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			// TODO Auto-generated method stub

			String[] parts = Departure.split("T");
			assert parts.length == 2;
			String date = parts[0];
			String Time = parts[1];
			String[] dateParts = date.split(":");
			assert dateParts.length == 2;
			String Date = dateParts[1];
			String searchDate = Date.replace("-", "");
			String Time2 = Time.replace(":", "");
			String searchTime = Time2.replace("00", "");
			
			String url = Uri.parse(baseURL).buildUpon().appendQueryParameter("from", Origin).
					appendQueryParameter("to", Destination).appendQueryParameter("date", searchDate).
					appendQueryParameter("time", searchTime).appendQueryParameter("timeIs", "Departing").
					appendQueryParameter("mode", "bus").appendQueryParameter("app_id", APP_Id).
					appendQueryParameter("app_key", APP_Key).build().toString();
			URLRequest request = new URLRequest();
			try 
			{
				int Index = Integer.parseInt(index);
				String jsonData = request.getUrl(url);
				
				JSONObject root = new JSONObject(jsonData);
				journeys = root.getJSONArray("journeys");
				
				JSONObject jn = journeys.getJSONObject(Index);
				JSONArray legs = jn.getJSONArray("legs");
				legList = new ArrayList<HashMap<String, String>>();
				//looping through all legs of each journey
				for(int j=0; j<legs.length(); j++)
				{
					HashMap<String, String> legMap = new HashMap<String, String>();

					 label = "Leg #"+(j+1);
					 JSONObject lg = legs.getJSONObject(j);
						Departure1 = lg.getString("departureTime");
						Arrival1 = lg.getString("arrivalTime");
						int du = lg.getInt("duration");
						Duration1 = du+"minutes";
						
						JSONObject inst = lg.getJSONObject("instruction");
						Instructions = inst.getString("detailed");
						
						JSONObject arrive = lg.getJSONObject("arrivalPoint");
						ArrivalPoint = arrive.getString("commonName");
						
						JSONObject md = lg.getJSONObject("mode");
						Mode = md.getString("name");
						
						JSONObject dP = lg.getJSONObject("departurePoint");
						DeparturePoint = dP.getString("commonName");
					 
					 legMap.put("Tag", label);
					 legMap.put("Means", "Mode of Travel: " +Mode);
					 legMap.put("Depart", "Departure Date and Time: " +Departure1);
					 legMap.put("Arrive", "Arrival Date and Time: " +Arrival1);
					 legMap.put("Length", "Duration: " +Duration1);
					 legMap.put("Instruct", "Instruction: " +Instructions);
					 legMap.put("DeparturePoint", "Departure Point: " +DeparturePoint);
					 legMap.put("Point", "Point of Arrival: " +ArrivalPoint);
					 
					 
					 JSONArray instructionSteps = inst.getJSONArray("steps");
					 String instructing = new String();//String to hold instruction information for each leg
						//looping through all instructions of each leg
						for(int l=0; l<instructionSteps.length(); l++)
						{
							JSONObject inStp = instructionSteps.getJSONObject(l);
							Description = inStp.getString("description");
							
							instructing+=(Description + "\n");
						}//end of instructions for loop
						legMap.put("Direct", "Directions: "+"\n" +instructing);
						
						JSONObject p = lg.getJSONObject("path");//JSONObject pointing at the path label in JSON data
						
						//JSONArray node for stops in bus mode
						JSONArray stp = p.getJSONArray("stopPoints");
						String stopping = new String();//String to hold stops for bus mode legs
						//looping through all stops
						for(int k=0; k<stp.length(); k++)
						{
							JSONObject s = stp.getJSONObject(k);
							stop = s.getString("name");
							//adding all stops to string holding stops
							stopping+=(stop + "\n");
						}//end of stops for loop
						legMap.put("stops", "Stops: " +"\n" +stopping);
						legList.add(legMap);
				}
				
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			String[] from = new String[] {"Tag", "Means", "Depart", "Arrive", "Length", "DeparturePoint", "Point", "Instruct",  "Direct", "stops"};
			
			//Ids of the textview to display the information on
			int [] to = new int[] {R.id.LegTag, R.id.LegMode, R.id.LegDepart, R.id.LegArrive, R.id.LegDuration, R.id.LegDeparturePoint, R.id.LegPoint, R.id.LegInstruction,  R.id.LegDirection, R.id.LegStop};
			
			//updating the parsed json information stored in journeysList to ListView
			ListAdapter adapter = new SimpleAdapter(SingleRouteActivity.this, legList, R.layout.singleroute, from, to);
			setListAdapter(adapter);
		}
		
	}
}
