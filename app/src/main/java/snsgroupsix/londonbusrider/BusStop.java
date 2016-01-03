package snsgroupsix.londonbusrider;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BusStop{

	private double lat;
	private double lon;
	private int rad;
	private String mainURL = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1";
	private String returnListURL = "&ReturnList=StopCode1,Bearing,StopPointIndicator,StopPointType,Latitude,Longitude";
    private String input;
	private String[] stopIndicator;
	private String[] stopCode;
	private String[] latArr;
	private String[] lonArr;
	
	public String[] stopCodeRet;
    public int httpRespCode;

	public BusStop(double latitude, double longitude, int radius){
		lat = latitude;
		lon = longitude;
		rad = radius;
        input = getData();
	}//constructor ends

    //get all bus stop data related string from TFL API
	private String getData(){
		
		String serverOutput = null;
		
		try {
			URL apiURL = new URL(mainURL+"?Circle="+lat+","+lon+","+rad+"&stopPointState=0"+returnListURL);
            HttpURLConnection httpConn = (HttpURLConnection) apiURL.openConnection();

            httpConn.setConnectTimeout(15000);
            httpRespCode = httpConn.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			
			String inputLine;
			while((inputLine = in.readLine()) != null)
				serverOutput=serverOutput+inputLine;
			in.close();
		}//try ends
		
		catch (MalformedURLException e) {
			e.printStackTrace();
			printError("Could not access TFL API.. Try again later..");
		}//catch URLException ends 
		catch (IOException e) {
			e.printStackTrace();
			printError("Could not read data from TFL API..");

		}//catch IOException ends
		
		return serverOutput;
		
	}//getData() ends
	
	private void printError(String error){ //Later change to accommodate error printing in android
		
		System.out.println(error);
		
	}//printError() ends
	
	//Parse data and get bus stop parameters
	private String[] parseData(String input, String datatype){
		
		String delims1 = "[\\[\\]]+";
		String delims2 = "[,]+";
		String[] dataArray = input.split(delims1); //index zero is empty
		String[] records = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try{
			//Split every record and add to the ArrayList
			for(int i=0;i<dataArray.length;i++){
				records = dataArray[i].split(delims2);
				if((records[0].equals("0")) && (!records[1].equals("null"))) //Get only stop data & eliminating imaginary bus stops
					list.add(records);
			}//for ends
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: unexpected API response..");
		}//catch ends
		
		stopIndicator = new String[list.size()];
		stopCode = new String[list.size()];
		latArr = new String[list.size()];
		lonArr = new String[list.size()];
        
		try{
		//Get all appropriate records from list
			for(int i=0;i<list.size();i++){
				stopCode[i]=list.get(i)[1];
				stopIndicator[i]=list.get(i)[4];
				latArr[i]=list.get(i)[5];
				lonArr[i]=list.get(i)[6];	
			}//for ends
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: array index out of bound, unexpected API response..");
		}//catch ends
		
		if(datatype.equals("stopIndicator"))
			return stopIndicator;
		else if(datatype.equals("stopCode"))
			return stopCode;
		else if(datatype.equals("latArr"))
			return latArr;
		else
			return lonArr;
						
	}//parseData() ends

	public String[] getStopIndicatorList(){
		
		ArrayList<String> stopIndicatorList = new ArrayList<String>();

        if(stopIndicator==null)
		    stopIndicator = parseData(input, "stopIndicator");
		
		try{
            for(int i=0;i<stopIndicator.length;i++) {
                if (stopIndicator[i].equals("null"))
                    stopIndicator[i] = "Request";
                stopIndicatorList.add(stopIndicator[i].replaceAll("\"", ""));
            }//for ends
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: array index out of bound..");
		}//catch ends
		
		String[] stopIndicatorArray = new String[stopIndicatorList.size()];
		stopIndicatorArray = stopIndicatorList.toArray(stopIndicatorArray);

        return stopIndicatorArray;
		
	}//getStopIndicatorList() ends
	
	public String[] getStopCodeList(){
		
		ArrayList<String> stopCodeList = new ArrayList<String>();

        if(stopCode==null)
		    stopCode = parseData(input, "stopCode");
		
		try{
		for(int i=0;i<stopCode.length;i++)
			stopCodeList.add(stopCode[i].replaceAll("\"", ""));
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: array index out of bound..");
		}//catch ends
		
		//String[] stopCodeArray = new String[stopCodeList.size()];
		//stopCodeArray = stopCodeList.toArray(stopCodeArray);
		
		String[] stopCodeRet= new String[stopCodeList.size()];
		stopCodeRet = stopCodeList.toArray(stopCodeRet);
		
		return stopCodeRet;
		
	}//getStopCodeList() ends
	
	public String[] getGeoCodeList(){ //later convert to LatLng class in android
		
		ArrayList<String> geoCodeList = new ArrayList<String>();

        if(latArr==null || lonArr==null) {
            latArr = parseData(input, "latArr");
            lonArr = parseData(input, "lonArr");
        }
		
		try{
			for(int i=0;i<latArr.length;i++){
				geoCodeList.add(latArr[i].replaceAll("\"", ""));
				geoCodeList.add(lonArr[i].replaceAll("\"", ""));
			}
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: array index out of bound..");
		}//catch ends
		
		String [] geoCodeArray = new String[geoCodeList.size()];
		geoCodeArray = geoCodeList.toArray(geoCodeArray);
		
		return geoCodeArray;
		
	}//getStopCodeList() ends

    public String getStopName(String stopCode){

        String serverOutput = null;

        try {
            URL apiURL = new URL(mainURL+"?StopCode1="+stopCode+"&stopPointState=0"+"&ReturnList=StopPointName");

            BufferedReader in = new BufferedReader(new InputStreamReader(apiURL.openStream()));

            String inputLine;
            while((inputLine = in.readLine()) != null)
                serverOutput=serverOutput+inputLine;
            in.close();
        }//try ends

        catch (MalformedURLException e) {
            e.printStackTrace();
            printError("Could not access TFL API.. Try again later..");
        }//catch URLException ends
        catch (IOException e) {
            e.printStackTrace();
            printError("Could not read data from TFL API..");
        }//catch IOException ends

        String delims1 = "[\\[\\]]+";
        String delims2 = "[,]+";
        String[] dataArray = serverOutput.split(delims1); //index zero is empty
        String[] records = null;
        ArrayList<String[]> list = new ArrayList<String[]>();

        try{
            //Split every record and add to the ArrayList
            for(int i=0;i<dataArray.length;i++){
                dataArray[i].replaceAll(", ", "; ");
                records = dataArray[i].split(delims2);
                if((records[0].equals("0"))) //Get only stop data
                    list.add(records);
            }//for ends

        }//try ends

        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            printError("Error: unexpected API response..");
        }//catch ends

        String stopName = new String();

        try{
            stopName=list.get(0)[1].replaceAll("\"", "");}
        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            printError("Error: unexpected API response..");
        }

        return stopName;

    }//getStopName ends


    public boolean isHttpSuccessful(){
        if (httpRespCode == 200)
                return true;
        else
            return false;
    }

    public boolean isHttpReplied(){
        if (httpRespCode == 0)
            return false;
        else
            return true;
    }


}//BusArrival ends
