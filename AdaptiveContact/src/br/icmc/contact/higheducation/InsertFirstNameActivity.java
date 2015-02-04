package br.icmc.contact.higheducation;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import br.icmc.contact.Contact;
import br.icmc.contact.ElderlyActivity;
import br.icmc.contact.R;
import br.icmc.contact.Util;
import br.icmc.flexinterface.InteractionLogging;

public class InsertFirstNameActivity extends ElderlyActivity implements TextWatcher {
	private EditText mFirstName;
	private Button mSave;
	private Button mNext;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.he_insert_first_name);
        
        mFirstName = (EditText) findViewById(R.id.firstName);
        mFirstName.addTextChangedListener(this);
    
        mSave = (Button) findViewById(R.id.save);
        mSave.setEnabled(false);
        
        mNext = (Button) findViewById(R.id.next);
        mNext.setEnabled(false);
        
        mExit.setVisibility(View.INVISIBLE);
        
        Util.getUtil().say(getResources().getString(R.string.error_first_name));
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	mFirstName.setText(Contact.getContact().getFirstName());
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	Contact.getContact().setFirstName(mFirstName.getText().toString());
    	//InteractionLogging.getInstance().log(mFirstName.getTag().toString(), mFirstName.getText().toString());
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	click_exit(null);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
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
	            Contact.getContact().setFirstName(matches.get(0));
	            Util.getUtil().say(matches.get(0));
			}
        } else if (resultCode == 1) { // Contato gravado
			finish();
		}
    }
    
    public void click_next(View view) {
    	InteractionLogging.getInstance().logClick((Button) view);
    	
    	if(mFirstName.getText().toString().equals("")) {
    		String msg = getResources().getString(R.string.error_first_name);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(msg).setNeutralButton("Ok", null).show();
    		Util.getUtil().say(msg);
    	} else {
	    	Contact.getContact().setFirstName(mFirstName.getText().toString());
	    	
	    	startActivityForResult(new Intent(this, InsertFamilyNameActivity.class), 0);
	    	
	    	Util.getUtil().vibrate();
    	}
    }
    
    public void click_save(View view) {
    	String msg;
    	if(mFirstName.getText().toString().equals("")) {
    		msg = getResources().getString(R.string.error_first_name);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(msg).setNeutralButton("Ok", null).show();
    		Util.getUtil().say(msg);
    	} else if(Contact.getContact().getPhone() == null) { //Verifica se preencheu o n�mero do telefone
	    	DialogInterface.OnClickListener dialogClickListener = new
		        	OnClickListener() {
		    			public void onClick(DialogInterface dialog, int which) {
		    				switch(which) {
		    					case DialogInterface.BUTTON_POSITIVE:
		    						//A��o quando o usu�rio clicar no bot�o sim
		    						Contact.getContact().setFirstName(mFirstName.getText().toString());
		    						save_contact();
		    						Util.getUtil().vibrate();
		    						break;
		    					case DialogInterface.BUTTON_NEGATIVE:
		    						//A��o quando o usu�rio clicar no bot�o n�o
		    						break;
		    				}
		    			}
		    		};
		    	
		    	
		    	msg = getResources().getString(R.string.save_without_fone);
		    	
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setMessage(msg)
		        	.setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
		        	.setNegativeButton(getResources().getString(R.string.no), dialogClickListener)
		        	.show();
		        Util.getUtil().say(msg);
    	} else {
	    	Contact.getContact().setFirstName(mFirstName.getText().toString());
	    	
	    	save_contact();
	    	Util.getUtil().vibrate();
    	}
    }
    
    public void click_back(View view) {
		click_exit(view);
	}

    // TextWattcher
	public void afterTextChanged(Editable s) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		InteractionLogging.getInstance().logTextViewBackspace(s, start, after, count);
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		InteractionLogging.getInstance().logTextView(s, start, before, count);
		if(mFirstName.length() > 0) {
			mSave.setEnabled(true);
			mNext.setEnabled(true);
		} else {
			mSave.setEnabled(false);
			mNext.setEnabled(false);
		}	
	}
}
