package si.fri.besedko;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;




public class MainActivity extends Activity implements OnClickListener {
	private JsonWordlists jwl = null;
	
	Button training = null;
	Button game = null;
	Button wordlists = null;
	
//	public boolean onOptionsItemSelected(MenuItem item) {
//		Intent i;
//	    // Handle item selection
//	    switch (item.getItemId()) {
//	    case R.id.menu_choose_wordlist:
////	    	startActivityForResult(new Intent(this, AddTodoActivity.class), 2);
////	    	updateList();
//	    	
//	    	//this.listAvailableWordlists();
//	    	
//	        return true;
//	    case R.id.menu_game:
//	    	i = new Intent(this, GameActivity.class);
//			startActivityForResult(i, 1);
//	    	
//	    	
//	    	
//	    	
//	        return true;
//	    case R.id.menu_training:
////	    	i = new Intent(this, PushToServerActivity.class);
////			startActivityForResult(i, 1);
////	    	updateList();
//	    	new AlertDialog.Builder(MainActivity.this)
////	    	.setMessage("Skupen čas 15m 43s\n"+
////	    				"Pravilno brez pomoči: 5\n"+
////	    				"S pomočnjo: 3")
//	    	.setMessage("Skupen čas 5m\n"+
//	    				"Seznam besed: \"Basic english\"\n"+
//	    				"Število besed 200\n"+
//	    				"Pravilno v 1. poskusu: 8\n"+
//	    				"Napačno: 4\n"+
//	    				"Uvrstitev: 4. mesto\n")	    				
// 	        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
// 	            public void onClick(DialogInterface dialog, int whichButton) {
// 	                dialog.dismiss();
// 	                //h
// 	            }
// 	        })
// 	        .show();
//	    	
//	    	return true;	
//	    default:
//	        return false;
//	    }
//	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if(resultCode == RESULT_OK){
				String result=data.getStringExtra("result");
			}
		}

		if (resultCode == RESULT_CANCELED) {
			//Write your code on no result return 
		}
	}//onAcrivityResult
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
        training = (Button)findViewById(R.id.button_training);
        training.setOnClickListener(this);
        training.setText("Trening");
        
        game = (Button)findViewById(R.id.button_game);
        game.setOnClickListener(this);
        game.setText("Tekma");
        
        wordlists = (Button)findViewById(R.id.button_wordlists);
        wordlists.setOnClickListener(this);
        wordlists.setText("Nabor besed");
    }
    
    ///
	private void listAvailableWordlists() {
		
    	new AsyncTask<Void,Void,Void>(){
    		private ProgressDialog dialog_update;
    		private Exception exception = null;
    	    protected void onPreExecute() {
    	        dialog_update = new ProgressDialog(MainActivity.this);
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
    		    	
    	 	       new AlertDialog.Builder(MainActivity.this)
    	 	        .setSingleChoiceItems(fileList, Constants.CHOOSED_WORDLIST, null)
    	 	        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	 	            public void onClick(DialogInterface dialog, int whichButton) {
    	 	                dialog.dismiss();
    	 	                Constants.CHOOSED_WORDLIST = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
    	 	            }
    	 	        })
    	 	        .show();	
    	    	}
	    		dialog_update.dismiss(); // TODO: dismiss immediately and process images in another asynctask
    	    }
    	}.execute();
	}

	@Override
	public void onClick(View v) {
		if(v == training){
			startActivityForResult(new Intent(this, TrainingActivity.class), 1);
		} else if (v == game) {
			startActivityForResult(new Intent(this, GameActivity.class), 2);
		} else if (v == wordlists) {
			listAvailableWordlists();
		} 
			
	} // end of onClick method

}
