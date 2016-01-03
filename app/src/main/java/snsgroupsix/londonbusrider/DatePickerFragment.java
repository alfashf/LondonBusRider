package snsgroupsix.londonbusrider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements OnDateSetListener
{
	private WeakReference<PlannerMainActivity>m;
	
	
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		m = new WeakReference<PlannerMainActivity>((PlannerMainActivity) getActivity());
		
		return new DatePickerDialog(getActivity(), this, year, month, day);
		
	}
	
	@SuppressLint("SimpleDateFormat") @Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
	{
		// TODO Auto-generated method stub
		//short text to show user selected date
		Toast.makeText(getActivity(), String.valueOf(year)+String.valueOf(monthOfYear+1)+String.valueOf(dayOfMonth), Toast.LENGTH_SHORT).show();
		
		Calendar c = Calendar.getInstance();
		c.set(year, monthOfYear, dayOfMonth);
		
		//setting date format
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String formattedDate = df.format(c.getTime());
		PlannerMainActivity target = m.get();
		if(target != null)
			target.tv.setText(formattedDate);
	}
	
}
