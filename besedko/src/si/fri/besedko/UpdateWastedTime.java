package si.fri.besedko;

import java.util.TimerTask;

import android.app.Activity;
import android.widget.TextView;

public class UpdateWastedTime extends TimerTask {
	
	private Activity a;
	long start_time;
	
	public UpdateWastedTime(Activity a, long start_time) {
		this.a = a;
		this.start_time = start_time;
	}
	
	public void run() {
		this.a.runOnUiThread(new Runnable() {
			public void run() {
				TextView tv = (TextView) a.findViewById(R.id.label_time_value);
				tv.setText(Functions.calculateTime(UpdateWastedTime.this.start_time));
			}
		});
		
		
	}
}
