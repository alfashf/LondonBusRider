package snsgroupsix.londonbusrider;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
	private WeakReference<PlannerMainActivity>m;
	
	public Dialog onCreateDialog(Bundle savedInstancestate)
	{
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		m = new WeakReference<PlannerMainActivity>((PlannerMainActivity) getActivity());
		
		return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute)
	{
		// TODO Auto-generated method stub
		//short text to show user selected time
		Toast.makeText(getActivity(), String.valueOf(hourOfDay) + 
				String.valueOf(minute), Toast.LENGTH_SHORT).show();
		

		PlannerMainActivity target = m.get();
		if(target != null)
			target.t.setText(String.valueOf(hourOfDay) + 
					String.valueOf(minute));
		}
}
