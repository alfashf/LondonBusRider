package snsgroupsix.londonbusrider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class BusArrival{

	private String lineName;
	private String stopCode;
    private String input;
	private String mainURL = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1";
	private String returnListURL = "&ReturnList=StopCode1,EstimatedTime,ExpireTime,RegistrationNumber";
	private String[] arrivalTime;

    public int httpRespCode;

	public BusArrival(String busID, String stopID){
		lineName = busID;
		stopCode = stopID;
        input = getData();
	}//constructor ends

	private String getData(){
		
		String serverOutput = null;
		
		try {
			URL apiURL = new URL(mainURL+"?LineName="+lineName+"&stopCode1="+stopCode+returnListURL);
            HttpURLConnection httpConn = (HttpURLConnection) apiURL.openConnection();

            httpConn.setConnectTimeout(5000);
            httpRespCode = httpConn.getResponseCode();
			
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
		
		return serverOutput;
		
	}//getData() ends
	
	private void printError(String error){ //Later change to accommodate error printing in android
		
		System.out.println(error);
		
	}//printError() ends
	
	//Parse data and get epoch arrival timing parameter
	private String[] parseData(String input){
		
		String delims1 = "[\\[\\]]+";
		String delims2 = "[, \"]+";
		String[] dataArray = input.split(delims1); //index zero is empty
		String[] records = null;
		ArrayList<String[]> list = new ArrayList<String[]>();
		
		try{
			//Split every record and add to the ArrayList
			for(int i=0;i<dataArray.length;i++){
				records = dataArray[i].split(delims2);
				if(records[0].equals("1")) //add responseType=1 only (prediction data only)
					list.add(records);
			}//for ends
		}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: unexpected API response..");
		}//catch ends
		
		arrivalTime = new String[list.size()];
        
		try{
			//Get arrival epoch time (array column number 3) for each record
			for(int i=0;i<list.size();i++)
				arrivalTime[i]=list.get(i)[3];}//try ends
		
		catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			printError("Error: unexpected API response, array index out of bound..");
		}//catch ends
					
		return arrivalTime;
		
	}//parseData() ends
	
	//Calculate epoch arrival timing to date format, to display remaining time later compare to android sntp client
	private Date epochToDate(String time){
		
		Date date = null;
		
		try{
			date = new Date(Long.parseLong(time));
		}//try ends
		
		catch(NumberFormatException e){
			e.printStackTrace();
			printError("Error: Could not convert time data..");
		}//catch ends
		
		return date;
		
	}//epochToRemaining(String time) ends
	
	//Get all arrival time available for certain bus and stop
	public ArrayList<Date> getArrival(){
		
		ArrayList<Date> dateList = new ArrayList<Date>();
		String[] times = parseData(input);
		
		for(int i=0;i<times.length;i++)
			dateList.add(epochToDate(times[i]));
		
		return dateList;
		
	}//getArrival() ends

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
