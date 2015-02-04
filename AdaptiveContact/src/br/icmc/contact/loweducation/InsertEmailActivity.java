package br.icmc.contact.loweducation;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import br.icmc.contact.Contact;
import br.icmc.contact.ElderlyActivity;
import br.icmc.contact.R;
import br.icmc.contact.Util;
import br.icmc.flexinterface.InteractionLogging;

public class InsertEmailActivity extends ElderlyActivity implements TextWatcher {
	private EditText mEmail;
	private Button mFinished;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.le_insert_email);
        
        mEmail = (EditText) findViewById(R.id.email);
        mEmail.addTextChangedListener(this);
        mFinished = (Button) findViewById(R.id.finished);
		mFinished.setEnabled(false);
		
		Util.getUtil().say(getResources().getString(R.string.error_email));
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    @Override
	public void onPause() {
		super.onPause();

		//InteractionLogging.getInstance().log(mEmail.getTag().toString(), mEmail.getText().toString());
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			if(resultCode == RESULT_OK) {
	            // Fill the list view with the strings the recognizer thought it could have heard
	            ArrayList<String> matches = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	            //mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
	            //        matches));
	            Contact.getContact().setEmail(matches.get(0));
	            Util.getUtil().say(matches.get(0));
			}
        }
	}
    
    public void click_finished(View view) {
    	String msg;
    	
    	if(mEmail.getText().toString().equals("")) {
    		msg = getResources().getString(R.string.error_email);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(msg).setNeutralButton("Ok", null).show();
    		Util.getUtil().say(msg);
    	} else if(!mEmail.getText().toString().contains("@")) {
    		msg = getResources().getString(R.string.error_email1);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(msg).setNeutralButton("Ok", null).show();
    		Util.getUtil().say(msg);
    	} else {
	    
	    	DialogInterface.OnClickListener dialogClickListener = new
	        	OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				switch(which) {
	    					case DialogInterface.BUTTON_POSITIVE:
	    						//A��o quando o usu�rio clicar no bot�o sim
	    						Util.getUtil().vibrate();
	    						finish();
	    						break;
	    					case DialogInterface.BUTTON_NEGATIVE:
	    						//A��o quando o usu�rio clicar no bot�o n�o
	    						save_contact();
	    						break;
	    				}
	    			}
	    		};
	    	
	    	Contact.getContact().setEmail(mEmail.getText().toString());
	        
	    	msg = getResources().getString(R.string.add_more_info);
	    	
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage(msg)
	        	.setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
	        	.setNegativeButton(getResources().getString(R.string.no), dialogClickListener)
	        	.show();
	        Util.getUtil().say(msg);
    	}
    }

    // TextWatter
	public void afterTextChanged(Editable s) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		InteractionLogging.getInstance().logTextViewBackspace(s, start, after, count);
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		InteractionLogging.getInstance().logTextView(s, start, before, count);
		enableButton(mEmail, mFinished);
	}
}
