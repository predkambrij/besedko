package si.fri.besedko;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TimerTask;

import com.google.gson.Gson;

import android.app.Activity;
import android.widget.TextView;

public class UpdateRemainingTime extends TimerTask {
	
	private GameActivity a;
	long start_time;
	int promised_time;
	String ret = null;
	
	public UpdateRemainingTime(GameActivity a, long start_time,int promised_time) {
		this.a = a;
		this.start_time = start_time;
		this.promised_time = promised_time;
	}
	
	@Override
	public boolean cancel() {
		boolean ret =  super.cancel();
		System.out.println("poklican cancel");
		return ret;
	}
	
	public void run() {
		this.a.runOnUiThread(new Runnable() {
			public void run() {
				TextView tv = (TextView) a.findViewById(R.id.label_time_value);
				UpdateRemainingTime.this.ret  =Functions.calculateRemainingTime(UpdateRemainingTime.this.start_time, UpdateRemainingTime.this.promised_time);
				tv.setText(ret);
				
				if (ret == null) {
					a.finishAction();
				}
				
			}
		});
		
		
	}
}
