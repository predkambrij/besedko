package si.fri.besedko;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import si.fri.besedko.R.string;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity  extends Activity {
	int helpnum = 0;
	Timer timer = null;
	long start_time = -1;
	
	int corrects = 0;
	int mistakes = 0;
	
	long total_words = 10;
	
	JsonWordlists jwl = null;
	JsonGlobalList gl = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
    	final EditText edittext = (EditText) findViewById(R.id.editText1);
    	edittext.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (Functions.verifyInput(jd.solutions, edittext, GameActivity.this) == true) {
						GameActivity.this.corrects++;
					} else {
						GameActivity.this.mistakes++;
					}
					getNextWord();
					edittext.setText("");
					return true;
				}
				return false;
			}
    	});
        
    	String[] fileList = {"50","200","3000"};

//    	final EditText input = new EditText(this);
        new AlertDialog.Builder(GameActivity.this)
    		//.setView(input)
        	.setSingleChoiceItems(fileList, 0, null)
        	.setTitle("Izberite, s kakšnim številom besed želite tekmovati:")
	        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                dialog.dismiss();
//	                String value = input.getText().toString().trim();
	                String[] fileList2 = {"50","200","3000"};
	                GameActivity.this.total_words = Long.parseLong(fileList2[((AlertDialog)dialog).getListView().getCheckedItemPosition()]);
	                
	                
	                TextView tv = (TextView) GameActivity.this.findViewById(R.id.label_time_name);
	        		tv.setText("Preostali čas");
	        		
	        		start_time = System.currentTimeMillis();
	        		
	        		
	            	GameActivity a = GameActivity.this;
	            	int promised_time;
	            	//promised_time=300000;
	            	promised_time=30000;
	            	timer = new Timer();
	            	TimerTask updateRemainingTime = new UpdateRemainingTime(a, start_time, promised_time);
	            	timer.scheduleAtFixedRate(updateRemainingTime, 0, 1000);
	            	
	            	getNextWord(); // first loop
	            
	            }
	        })
	        .show();	
    		
	}
	
	// natural menu
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_training, menu);
		return true;
	}
	
	public void finishAction() {
		new AsyncTask<Void,Void,Void>(){
    		private ProgressDialog dialog_update;
    		private Exception exception = null;
    	    protected void onPreExecute() {
    	        dialog_update = new ProgressDialog(GameActivity.this);
    	        dialog_update.setTitle("Komunikacija");
    	        dialog_update.setMessage("Pridobivam podatke...");
    	        dialog_update.setIndeterminate(true);
    	        dialog_update.show();
    	    }
    	    protected Void doInBackground(Void... params) {
    	    	try {
    	    		if (jwl == null) {
    	    			Gson gson = new Gson();
        		        String url = Constants.HOSTNAME+Constants.DOC_ROOT+"list_available_wordlists.php";
        		        System.out.println(url);
        				InputStream is = Communicator.GETis(url, null);
        		        jwl = gson.fromJson(new InputStreamReader(is), JsonWordlists.class);
    	    		}
    	    		if (gl == null) {
        		        //global list
        		        TelephonyManager mTelephonyMgr;
       	    		 mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
       	    		String id = mTelephonyMgr.getDeviceId();
       	    		
       	    		Gson gson2 = new Gson();
    		        String url2 = Constants.HOSTNAME+Constants.DOC_ROOT+"global_list.php?id="+id+"&score="+GameActivity.this.corrects;
    		        System.out.println(url2);
    				InputStream is2 = Communicator.GETis(url2, null);
    		        gl = gson2.fromJson(new InputStreamReader(is2), JsonGlobalList.class);
       	    		
    	    		}
				} catch (IOException e) {
					this.exception = e;
					e.printStackTrace();
				}   	    	
    	        return null;
    	    }
    	    protected void onPostExecute(Void result) {
    	    	if (exception != null) {
    	    		// TODO: error message
    	    		System.out.println(exception);
    	    		System.out.println("napaca, napaca");
    	    		return;
    	    	} else {

    	    		 
    	    		
    	    		 
    	    		 
    	    		String[] fileList = jwl.wordlists; //{"a","b","c"};
    		    	String wordListName = fileList[Constants.CHOOSED_WORDLIST];
    	    		try {
    	    			GameActivity.this.timer.cancel();
    	    		} catch (Exception e) {
    	    			
    	    		}
    		    	
    	    		GameActivity.this.timer = null;
    	    		
    	    		String not_p_text = "\nPotrebnih točk za napredovanje: "+gl.b_pos_score;
    	    		if (gl.b_pos_score.equals("-1")) not_p_text = "";
    	    		
    	        	new AlertDialog.Builder(GameActivity.this)
    	        		.setMessage("Skupen čas 5m\n"+
    	        			"Seznam besed: "+wordListName+"\n"+
    	        			"Število besed: "+GameActivity.this.total_words+"\n"+
    	    				"Pravilno v 1. poskusu: "+GameActivity.this.corrects+"\n"+
    	    				"Napačno: "+GameActivity.this.mistakes+"\n"+
    	    				"Uvrstitev:\n"
    	    				+"St tock: "+GameActivity.this.corrects+", največ "+gl.best_score+"\n"
    	    				+"Najboljse mesto: "+gl.best_position
    	    				+not_p_text
    	    				)
    	    				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	                public void onClick(DialogInterface dialog, int whichButton) {
    	                	
    	                    dialog.dismiss();
    	                    GameActivity.this.finish();
    	                }
    	            })
    	            .show();
    	        	
    	    	}
	    		dialog_update.dismiss(); // TODO: dismiss immediately and process images in another asynctask
    	    }
    	}.execute();
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_training_end:
		//startActivityForResult(new Intent(this, AddTodoActivity.class), 2);
		
		//this.listAvailableWordlists();
			
		this.finishAction();
		
		return true;
		}
		return false;
	}
	////
	
	protected void onDestroy () {
		super.onDestroy();
		if (this.timer != null) {
			this.timer.cancel();
		}
		
	}
    
	private JsonData jd = null;
	


    
    public void getNextWord() {
    	new AsyncTask<Void,Void,Void>(){
    		//private Gallery gallery;
    		//private ImageAdapter ia;
    		private ProgressDialog dialog_update;
    		boolean success = false;
    	    protected void onPreExecute() {
    	        dialog_update = new ProgressDialog(GameActivity.this);
    	        dialog_update.setTitle("Komunikacija");
    	        dialog_update.setMessage("Pridobivam podatke...");
    	        dialog_update.setIndeterminate(true);
    	        dialog_update.show();
    	    }
    	    protected Void doInBackground(Void... params) {
    	    	
    	    	
	    		for (int i=0; i<5; i++) {
	    			Gson gson = new Gson();
	    			
	    			// adjustment depending on success
	    			
    		        String randomWord = Functions.getRandomWord(GameActivity.this.total_words);
    		        if (randomWord == null) continue;
    		        String url = Constants.HOSTNAME+Constants.DOC_ROOT+"solve.php?word="+randomWord;
    		        System.out.println(url);
    		        try {
    		        	InputStream is = Communicator.GETis(url, null);
    		        	jd = gson.fromJson(new InputStreamReader(is), JsonData.class);
    		        	if (jd == null) {
    		        		System.out.println("jd is null");
    		        		continue;
    		        	}
    		        	if (jd.solutions == null) {
    		        		System.out.println("jd.solutions is null");
    		        		continue;
    		        	}
    		        	if (jd.solutions.length == 0) {
    		        		System.out.println("jd.solutions.length is zero");
    		        		continue;
    		        	}
    		        	if (jd.solutions[0] == null) {
    		        		System.out.println("jd.solutions[0] is null");
    		        		continue;
    		        	}
    		        	if (jd.requested == null) {
    		        		System.out.println("jd.requested is null");
    		        		continue;
    		        	}
    		        	if (jd.images == null) {
    		        		System.out.println("jd.images is null");
    		        		continue;
    		        	}
//    		        	if (jd.images.length == 0) {
//    		        		System.out.println("images="+jd.images);
//    		        		System.out.println("jd.images.length is zero");
//    		        		continue;
//    		        	}
    		        	success = true;
    		        	break;
    		        } catch (IOException e) {
    					e.printStackTrace();
    					continue;
    				}
	    		}
    		    return null;
    	    }
    	    protected void onPostExecute(Void result) {
    	    	if (success == false) {
    	    		// network error
    	    		Toast.makeText(GameActivity.this, "Network error (word)", Toast.LENGTH_LONG).show();
    	    		dialog_update.dismiss();
    	    		GameActivity.this.finish();
    	    		return;
    	    	} else {
    	    		TextView tv = (TextView) findViewById(R.id.textView1);
    	    		if (jd.requested.length() > 10) {
    	    			tv.setHeight(110);
    	    		} else {
    	    			tv.setHeight(62);
    	    		}
    	    		
    	    		tv.setText(jd.requested);
    	    		getImages();
    	    	}
	    		dialog_update.dismiss(); // TODO: dismiss immediately and process images in another asynctask
    	    }
    	}.execute();
    }
    
    public void getImages() {
    	new AsyncTask<Void,Void,Void>(){
    		private Gallery gallery;
    		private ImageAdapter ia;
    		private Exception exception = null;
    	    protected Void doInBackground(Void... params) {
    	    	try {  		        
    		        Bitmap[] bms = new Bitmap[jd.images.length];
    		        int maxlen = 5;
    		        System.out.println(jd);
    		        System.out.println(jd.images);
    		        System.out.println(jd.images.length);
    		        for (int i=0; i<Math.min(jd.images.length,maxlen); i++) {
    		        	try {
    		        		bms[i] = BitmapFactory.decodeStream((InputStream)new URL(jd.images[i]).getContent());
    		        	} catch (java.net.UnknownHostException e) {
    		        		// skip this image then
    		        		if (maxlen+1 < jd.images.length)
    		        			maxlen++;
    		        	} catch (java.io.FileNotFoundException e) {
    		        		// skip this image then
    		        		if (maxlen+1 < jd.images.length)
    		        			maxlen++;
    		        	}
    		        }
    		        ia = new ImageAdapter(GameActivity.this);
    		        ia.setImages(bms);
    		        gallery = (Gallery) findViewById(R.id.gallery1);
    		        gallery.setSpacing(2);
    		        
    		        
				} catch (IOException e) {
					this.exception = e;
					e.printStackTrace();
				} catch (NullPointerException e) {
					this.exception = e;
					e.printStackTrace();
				}
    	        return null;
    	    }
    	    protected void onPostExecute(Void result) {
    	    	if (exception != null) {
    	    		Toast.makeText(GameActivity.this, "Network error (Images)", Toast.LENGTH_LONG).show();
    	    		System.out.println(exception);
    	    		return;
    	    	} else {  	    		
    	    		gallery.setAdapter(ia);
    	    		if (jd.images.length > 0)
    	    			gallery.setSelection(1);
    	    	}
    	    }
    	}.execute();
    }
}
