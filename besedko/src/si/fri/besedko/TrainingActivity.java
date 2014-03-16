package si.fri.besedko;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TrainingActivity  extends Activity implements AnimationListener  {
	int helpnum = 0;
	Timer timer = null;
	long start_time = -1;
	
	int corrects = 0;
	int mistakes = 0;
	
	long total_words = 10;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        getNextWord();
        
    	final EditText edittext = (EditText) findViewById(R.id.editText1);
    	edittext.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (Functions.verifyInput(jd.solutions, edittext, TrainingActivity.this) == true) {
						if (TrainingActivity.this.helpnum != 0) {
							TrainingActivity.this.mistakes++;
						} else {
							TrainingActivity.this.corrects++;
						}
						TrainingActivity.this.helpnum = 0;
						startAnimationDriveOut(null);
						getNextWord();
					} else {
						edittext.setText("");
						// wrong, do help TODO 
						TrainingActivity.this.helpnum++;
						if (TrainingActivity.this.helpnum == 1) { // for first case
							if (jd.solutions[0].length()>=2) {
								edittext.setText(jd.solutions[0].substring(0, 1)+
										String.format(String.format("%%0%dd", jd.solutions[0].length()-1), 0).replace("0","*"));
							}
							
						} else { // for all other cases
							String ensStr = "";
							// first n chars
							ensStr+=jd.solutions[0].substring(0, Math.min(TrainingActivity.this.helpnum-1, jd.solutions[0].length()-1)); // first
							
							// stars
							if (jd.solutions[0].length()-TrainingActivity.this.helpnum > 0) {
								ensStr+= String.format(String.format("%%0%dd",
										jd.solutions[0].length()-TrainingActivity.this.helpnum
										), 0).replace("0","*");
							}
							
							// last
							ensStr+= jd.solutions[0].substring(jd.solutions[0].length()-1, jd.solutions[0].length());
							
							
							edittext.setText(ensStr);
						}
					}
					return true;
				}
				return false;
			}
    	});
        
        TextView tv = (TextView) this.findViewById(R.id.label_time_name);
		tv.setText("Skupen čas");
		
		start_time = System.currentTimeMillis();
		
		
    	Activity a = this;
    	
    	timer = new Timer();
    	TimerTask updateRemainingTime = new UpdateWastedTime(a, start_time);
    	timer.scheduleAtFixedRate(updateRemainingTime, 0, 1000);
    	
    	
	}
	public void startAnimationDriveOut(View view) {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.drive_out);
        animation.setAnimationListener(this);
        View animatedView = findViewById(R.id.skup_id);
        animatedView.startAnimation(animation);
    }
	public void startAnimationDriveIn(View view) {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.drive_in);
        animation.setAnimationListener(this);
        View animatedView = findViewById(R.id.skup_id);
        animatedView.startAnimation(animation);
    }
	
	
	// natural menu
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_training, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_training_end:
		//startActivityForResult(new Intent(this, AddTodoActivity.class), 2);
		
		//this.listAvailableWordlists();
			
			String total_time = Functions.calculateTime(TrainingActivity.this.start_time);
			this.timer.cancel();
			this.timer = null;
			
	    	new AlertDialog.Builder(TrainingActivity.this)
    	.setMessage("Skupen čas "+total_time+"\n"+
    				"Pravilno brez pomoči: "+this.corrects+"\n"+
    				"S pomočnjo: "+this.mistakes)
	        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                dialog.dismiss();
	                TrainingActivity.this.finish();
	            }
	        })
	        .show();

		
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
    	        dialog_update = new ProgressDialog(TrainingActivity.this);
    	        dialog_update.setTitle("Komunikacija");
    	        dialog_update.setMessage("Pridobivam podatke...");
    	        dialog_update.setIndeterminate(true);
    	        dialog_update.show();
    	    }
    	    protected Void doInBackground(Void... params) {
    	    	
    	    	
	    		for (int i=0; i<5; i++) {
	    			Gson gson = new Gson();
	    			
	    			// adjustment depending on success
	    			if (TrainingActivity.this.mistakes+TrainingActivity.this.corrects > 1) {
	    				long words_before = TrainingActivity.this.total_words;
	    				double ratio = (float)TrainingActivity.this.corrects/(float)(
		    					TrainingActivity.this.mistakes+TrainingActivity.this.corrects);
		    			
		    			if (ratio > 0.8) {
		    				TrainingActivity.this.total_words+=10;
		    			}
		    			if (ratio <0.3){
		    				if (TrainingActivity.this.total_words > 30) {
		    					TrainingActivity.this.total_words-=10;
		    				} else if (TrainingActivity.this.total_words > 10) {
		    					TrainingActivity.this.total_words-=3;
		    				} else if (TrainingActivity.this.total_words > 5) {
		    					TrainingActivity.this.total_words-=1;
		    				}
		    			}
		    			if (TrainingActivity.this.total_words != words_before) {
		    				System.out.println("words changed from "+words_before+"to "+TrainingActivity.this.total_words);
		    				System.out.println("ratio: "+ratio);
		    			}
	    			}
	    			
    		        String randomWord = Functions.getRandomWord(TrainingActivity.this.total_words);
    		        if (randomWord == null) continue;
    		        if (randomWord.equals("")) continue;
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
    	    		Toast.makeText(TrainingActivity.this, "Network error (word)", Toast.LENGTH_LONG).show();
    	    		dialog_update.dismiss();
    	    		TrainingActivity.this.finish();
    	    		return;
    	    	} else {
    	    		TextView tv = (TextView) findViewById(R.id.textView1);
    	    		tv.setText(jd.requested);
//    	    		tv.setText("1234567890123");
    	    		if (jd.requested.length() > 10) {
    	    			tv.setHeight(110);
    	    		} else {
    	    			tv.setHeight(62);
    	    		}
    	    		getImages();
    	    	}
	    		dialog_update.dismiss(); // TODO: dismiss immediately and process images in another asynctask
	    		startAnimationDriveIn(null);
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
    		        ia = new ImageAdapter(TrainingActivity.this);
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
    	    		Toast.makeText(TrainingActivity.this, "Network error (Images)", Toast.LENGTH_LONG).show();
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

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		// TODO Auto-generated method stub
		
	}
}
