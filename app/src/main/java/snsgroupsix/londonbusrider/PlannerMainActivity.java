package snsgroupsix.londonbusrider;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PlannerMainActivity extends ActionBarActivity {
	
	private Button DateButton;
	private Button TimeButton;
	private Button SearchButton;
	private EditText origin;
	private EditText destination;
	public static String Origin;
	public static String Destination;
	public static String Date;
	public static String Time;
	
	TextView tv;
	TextView t;
	
	DatePickerFragment d = new DatePickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plannermain);
        
        origin =(EditText)findViewById(R.id.origin);
        destination =(EditText)findViewById(R.id.destination);
        
        DateButton = (Button)findViewById(R.id.date_button);
        tv = (TextView)findViewById(R.id.date_text);
        DateButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v)
			{
				showDatePickerDialog(v);	
			}
        });
        
        TimeButton = (Button)findViewById(R.id.time_button);
        t = (TextView)findViewById(R.id.time_text);
        TimeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTimePickerDialog(v);	
			}
		});   
        
        SearchButton = (Button)findViewById(R.id.search_button);
        SearchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				//call the obtainUserInput method
				obtainUserInput();	
			}
		});
    }
    
    public void showDatePickerDialog(View v)
	{
		DialogFragment Fragment = new DatePickerFragment();
		Fragment.show(getSupportFragmentManager(), "datePicker");			
	}
    
    public void showTimePickerDialog(View v)
    {
    	DialogFragment Fragment = new TimePickerFragment();
		Fragment.show(getSupportFragmentManager(), "timePicker");
    }
    
    //method to obtain user input, send user input to the RouteOptionsActivity and start the RouteOptionsActivity
    @SuppressLint("NewApi") public void obtainUserInput()
    {
    	//getting origin, destination, date and time from the user input and selection
    	Origin = origin.getText().toString();
    	Destination =destination.getText().toString();
    	Date = tv.getText().toString();
    	Time = t.getText().toString();
    	
    	//creating an intent to communicate with the RouteOptionsActivity
    	Intent i = new Intent(PlannerMainActivity.this, RouteOptionsActivity.class);
    	
    	//putting extras of Origin, Destination, Date and Time on the intent to send to the RouteOptionsActivity 
    	i.putExtra(RouteOptionsActivity.ORIGIN, Origin);
    	i.putExtra(RouteOptionsActivity.DESTINATION, Destination);
    	i.putExtra(RouteOptionsActivity.DATE, Date);
    	i.putExtra(RouteOptionsActivity.TIME, Time);
    	
    	//start the RouteOptionsActivity
    	startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
