package si.fri.besedko;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Functions {
	
    public static boolean verifyInput(String[] solutions, EditText input, Activity activity) {
		for(int i=0; i<solutions.length; i++) {
			if (solutions[i].toLowerCase(Locale.US).compareTo(input.getText().toString().toLowerCase(Locale.US).trim())==0) {
					input.setText("");
				Toast.makeText(activity, "PRAVILNO!", Toast.LENGTH_LONG).show();
				// getNextWord();
				return true;
			}
		}

		Toast.makeText(activity, "Narobe!", Toast.LENGTH_SHORT).show();
    	return false;
    }
    
    public static String getRandomWord(long numberOfWords) {
    	try {
	        Gson gson = new Gson();
	        String url = (Constants.HOSTNAME+Constants.DOC_ROOT+"get_random_word.php"
	        				+"?wordlist="+Constants.CHOOSED_WORDLIST+"&number_of_words="+numberOfWords);
	        System.out.println(url);
			InputStream is = Communicator.GETis(url, null);
	        JsonRandomString jd = gson.fromJson(new InputStreamReader(is), JsonRandomString.class);
	        if (jd == null) {
	        	System.out.println("jd is null");//media
	        	return null;
	        }
	        if (jd.string == null){
	        	System.out.println("js.string is null");
	        	return null;
	        }
	        	
	        return jd.string;
		} catch (IOException e) {
			System.out.println("fail");
			e.printStackTrace();
			System.out.println("end of fail");
		}
    	return null;
    }

    public static String calculateTime(long start_time) {
    	long curTime = System.currentTimeMillis();
		long wasted_time = curTime-start_time;
		
    	String ret = "";
		
		ret = (wasted_time/1000) % 60 +"s"+ ret; // seconds
		
		if (wasted_time/1000/60 > 0) {
			ret = (wasted_time/1000/60) % 60 +"m "+ ret; // minutes
			if (wasted_time/1000/60/60 > 0) {
				ret = (wasted_time/1000/60/60) +"h " + ret; // hours
			}
		}
		return ret;
    }
    public static String calculateRemainingTime(long start_time, int promised_time) {
    	long curTime = System.currentTimeMillis();
		long wasted_time = start_time+promised_time-curTime;
		
		
    	String ret = "";
		if (wasted_time<=0) {
			return null;
		} else {
			ret = (wasted_time/1000) % 60 +"s"+ ret; // seconds
			
			if (wasted_time/1000/60 > 0) {
				ret = (wasted_time/1000/60) % 60 +"m "+ ret; // minutes
				if (wasted_time/1000/60/60 > 0) {
					ret = (wasted_time/1000/60/60) +"h " + ret; // hours
				}
			}
		}
		return ret;
    }

}
