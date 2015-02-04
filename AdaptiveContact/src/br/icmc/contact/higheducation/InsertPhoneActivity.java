package br.icmc.contact.higheducation;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

public class InsertPhoneActivity extends ElderlyActivity implements TextWatcher {
	private EditText mPhone;
	private Button mSave;
	private Button mNext;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.he_insert_phone);
        
        mPhone = (EditText) findViewById(R.id.phone);
        mPhone.addTextChangedListener(this);
        
        mSave = (Button) findViewById(R.id.save);
        mSave.setEnabled(false);
        
        mNext = (Button) findViewById(R.id.next);
        mNext.setEnabled(false);
        
        Util.getUtil().say(getResources().getString(R.string.error_phone));
                
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	mPhone.setText(Contact.getContact().getPhone());
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	Contact.getContact().setPhone(mPhone.getText().toString());
    	//InteractionLogging.getInstance().log(mPhone.getTag().toString(), mPhone.getText().toString());
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
	            Contact.getContact().setPhone(matches.get(0));
	            Util.getUtil().say(matches.get(0));
			}
        } else if (resultCode == 1) { // Contato gravado
			setResult(resultCode);
        	finish();
		}
    }
    	
    public void click_next(View view) {
    	InteractionLogging.getInstance().logClick((Button) view);
    	if(mPhone.getText().toString().equals("")) {
    		String msg = getResources().getString(R.string.error_phone);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(msg).setNeutralButton("Ok", null).show();
    		Util.getUtil().say(msg);
    	} else {
    		Contact.getContact().setPhone(mPhone.getText().toString());
    	
    		startActivityForResult(new Intent(this, InsertAddressActivity.class), 0);
    		Util.getUtil().vibrate();
    	}
    }
    
    public void click_save(View view) {
    	if(mPhone.getText().toString().equals("")) {
    		String msg = getResources().getString(R.string.error_phone);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(msg).setNeutralButton("Ok", null).show();
    		Util.getUtil().say(msg);
    	} else {
    		Contact.getContact().setPhone(mPhone.getText().toString());
    	
    		save_contact();
    	}
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
		if(mPhone.length() > 0) {
			mSave.setEnabled(true);
			mNext.setEnabled(true);
		} else {
			mSave.setEnabled(false);
			mNext.setEnabled(false);
		}	
	}
}
