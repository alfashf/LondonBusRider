package snsgroupsix.londonbusrider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class SingleLegActivity extends Activity 
{
	public static String label;
	public static String mode;
	public static String departure;
	public static String arrival;
	public static String duration;
	public static String departurePoint;
	public static String arrivalPoint;
	public static String instructions;
	public static String directions;
	public static String stops;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_singleleg);
		
		Intent intent = getIntent();
		label = intent.getStringExtra("Tag");
		mode = intent.getStringExtra("mode");
		departure = intent.getStringExtra("departure1");
		arrival = intent.getStringExtra("arrival1");
		duration = intent.getStringExtra("duration1");
		departurePoint = intent.getStringExtra("departPoint");
		arrivalPoint = intent.getStringExtra("point");
		instructions = intent.getStringExtra("instruct");
		directions = intent.getStringExtra("direct");
		stops = intent.getStringExtra("stop");
		
		TextView txt_label = (TextView)findViewById(R.id.LegTag1);
		TextView txt_mode = (TextView)findViewById(R.id.LegMode1);
		TextView txt_depart = (TextView)findViewById(R.id.LegDepart1);
		TextView txt_arrive = (TextView)findViewById(R.id.LegArrive1);
		TextView txt_duration = (TextView)findViewById(R.id.LegDuration1);
		TextView txt_departPoint = (TextView)findViewById(R.id.LegDeparturePoint1);
		TextView txt_point = (TextView)findViewById(R.id.LegPoint1);
		TextView txt_instruction = (TextView)findViewById(R.id.LegInstruction1);
		TextView txt_direction = (TextView)findViewById(R.id.LegDirection1);
		TextView txt_stop = (TextView)findViewById(R.id.LegStop1);
		txt_direction.setMovementMethod(new ScrollingMovementMethod());
		txt_stop.setMovementMethod(new ScrollingMovementMethod());
		
		txt_label.setText(label);
		txt_mode.setText(mode);
		txt_depart.setText(departure);
		txt_arrive.setText(arrival);
		txt_duration.setText(duration);
		txt_departPoint.setText(departurePoint);
		txt_point.setText(arrivalPoint);
		txt_instruction.setText(instructions);
		txt_direction.setText(directions);
		txt_stop.setText(stops);
		
		
	}

}
