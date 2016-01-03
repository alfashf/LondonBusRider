package snsgroupsix.londonbusrider;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class URLRequest 
{	
	public static final String TAG = "URLRequest";
	public static String response;
	public static String jsonString;
	public static int id;
	public static String Id;
	public static String Label;
	public static String Departure;
	public static String Arrival;
	public static String Duration;

	JSONArray journeys;
	
	//hold list of journeys and information
	ArrayList<HashMap<String, String>> journeyList;

	public String getUrl(String url) throws IOException
	{
		URL apiUrl = new URL(url);
		
		//establishing a connection to the apiURL
		HttpURLConnection connection = (HttpURLConnection)apiUrl.openConnection();
		try
		{	
			//setting up an InputStream to the urlConnection
			InputStream in = connection.getInputStream();
			
			//return null if HTTP connection status is not OK (i.e code is not 200)
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				return null;
			}
			
			//if HTTP connection status is OK (i.e code is 200)
			
			//buffered reader to read and buffer input from https connection
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);
			StringBuilder builder = new StringBuilder();
			String inputLine = null;
			while((inputLine = reader.readLine())!=null)
			{
				builder.append(inputLine +"\n");
			}
			in.close(); // close input stream
			response = builder.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally
		{
			connection.disconnect();//disconnect httpConnection
		}
		return response;
	}
	public void getComponents(String url) //throws IOException 
	{		

		journeyList = new ArrayList<HashMap<String, String>>();//create new HashMap arrayList to hold journeys
		try
		{	String jsonString = getUrl(url);
		
			JSONObject root = new JSONObject(jsonString);
			
			//JSONArray node for journey
			journeys = root.getJSONArray("journeys");
			
			//looping through all journeys in JSON data
			for(int i=0; i<journeys.length(); i++)
			{
				//temporary hash map for single journey
				HashMap<String, String> journey = new HashMap<String, String>();
				
				id = i;
				Id = ""+id;
				Log.v(TAG, Id);
				Label = "Journey #"+(i+1);
				JSONObject jn = journeys.getJSONObject(i);
				Departure = jn.getString("startDateTime");
				Arrival = jn.getString("arrivalDateTime");
				int d = jn.getInt("duration");
				Duration = d+"minutes";
				journey.put("Tag", Label);
				journey.put("Index", Id);
				journey.put("Depart", "Departure Date and time: "+Departure);
				journey.put("Arrive", "Arrival Date and Time: " +Arrival);
				journey.put("Length", "Duration: " +Duration);
				
				//adding journey HashMap to journeyList HashMap
				journeyList.add(journey);
			}//end of journey for loop
		}
		catch (IOException ioe)
		{
			Log.e(TAG, "Failed to get json content", ioe);
		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			Log.e(TAG, "Failed to create JSONObject", e);
		}		
	}	
	
	//method to return journeyList
	public ArrayList<HashMap<String, String>> obtainJourneyList()
	{
		return journeyList;
	}
}

