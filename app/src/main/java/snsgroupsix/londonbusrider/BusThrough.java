package snsgroupsix.londonbusrider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by alfa on 12/19/14.
 */
public class BusThrough {

    private String stopId;
    private String mainURL = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1";
    private String input;
    private String[] busThroughList;
    private ArrayList<Integer> indexCounter = new ArrayList<>();

    public String[] busThroughArr;
    public String[] busDestinationArr;
    public int httpRespCode;

    public BusThrough(String busStopCode){
        stopId = busStopCode;
        input = getData();
        if (!(input.equals(null))) {
            getBusThrough();
            getBusDestination();
        }//if ends
    }//constructor ends

    private String getData(){
        String serverOutput = null;

        try {
            URL apiURL = new URL(mainURL+"?StopCode1="+stopId+"&ReturnList=LineName,DestinationText");
            HttpURLConnection httpConn = (HttpURLConnection) apiURL.openConnection();

            httpConn.setConnectTimeout(5000);
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
        catch (SocketTimeoutException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            printError("Could not read data from TFL API..");
        }//catch IOException ends


        return serverOutput;
    }//getData() ends

    private ArrayList<String> parseData(String input, int dataType){

        String delims1 = "[\\[\\]]+";
        String delims2 = "[,\"]+";
        String[] dataArray = input.split(delims1); //index zero is empty
        String[] records = null;
        ArrayList<String> lineNumberList = new ArrayList<>();
        ArrayList<String> destinationList = new ArrayList<>();

        try{
            //Split every record and add to the ArrayList
            for(int i=0;i<dataArray.length;i++){
                records = dataArray[i].split(delims2);
                if(records[0].equals("1")) { //add responseType=1 only (prediction data only)
                    lineNumberList.add(records[1]); //get and add bus/line number only to list
                    destinationList.add(records[2]); //get and add destination for each line number
                }//if ends
            }//for ends
        }//try ends

        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            printError("Error: unexpected API response..");
        }//catch ends


        if (dataType == 0)
            return lineNumberList;
        else
            return destinationList;

    }//parseData() ends

    private void getBusThrough(){

        ArrayList<String> parsedString = parseData(input, 0);

        ArrayList<String> busThroughList = new ArrayList<>();
        boolean isDuplicate = false;

        for(int i=0; i<parsedString.size(); i++){

            if(i==0) {
                busThroughList.add(parsedString.get(0));
                indexCounter.add(i);
            }//if ends

            for(int j=0; j<busThroughList.size(); j++){

                isDuplicate = false;

                if(parsedString.get(i).equals(busThroughList.get(j))) {
                    isDuplicate = true;
                    break;
                }//if ends

            }//for ends

            if(!isDuplicate) {
                busThroughList.add(parsedString.get(i));
                indexCounter.add(i);
            }//if ends
        }//for ends

        busThroughArr = new String[busThroughList.size()];

        for(int i=0; i<busThroughList.size(); i++){
            busThroughArr[i] = busThroughList.get(i);
        }//for ends

        busThroughList.clear();

    }//getBusThrough ends

    private void getBusDestination(){

        ArrayList<String> parsedString = parseData(input, 1);

        busDestinationArr = new String[indexCounter.size()];

        for(int i = 0; i<indexCounter.size(); i++){
            busDestinationArr[i] = parsedString.get(indexCounter.get(i));
        }

        indexCounter.clear();

    }//getBusDestination ends

    private void printError(String error){ //Later change to accommodate error printing in android

        System.out.println(error);

    }//printError() ends

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

}
