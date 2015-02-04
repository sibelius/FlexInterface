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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import br.icmc.contact.Contact;
import br.icmc.contact.ElderlyActivity;
import br.icmc.contact.R;
import br.icmc.contact.Util;
import br.icmc.flexinterface.InteractionLogging;

public class InsertFamilyNameActivity extends ElderlyActivity implements TextWatcher {
	private EditText mFamilyName;

	private Button mSave;
	private Button mNext;

	/** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.he_insert_family_name);

		mFamilyName = (EditText) findViewById(R.id.familyName);
		mFamilyName.addTextChangedListener(this);
		
		mSave = (Button) findViewById(R.id.save); 
		mSave.setEnabled(false);
		 
		mNext = (Button) findViewById(R.id.next); 
		mNext.setEnabled(false);
		 
		Util.getUtil()
				.say(getResources().getString(R.string.error_family_name));

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	
	public void onResume() {
		super.onResume();

		mFamilyName.setText(Contact.getContact().getFamilyName());
	}

	
	public void onPause() {
		super.onPause();

		Contact.getContact().setFamilyName(mFamilyName.getText().toString());
		//InteractionLogging.getInstance().log(mFamilyName.getTag().toString(), mFamilyName.getText().toString());
	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Fill the list view with the strings the recognizer thought it
				// could have heard
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				// mList.setAdapter(new ArrayAdapter<String>(this,
				// android.R.layout.simple_list_item_1,
				// matches));
				Contact.getContact().setFamilyName(matches.get(0));
				Util.getUtil().say(matches.get(0));
			}
		} else if (resultCode == 1) { // Contato gravado
			setResult(resultCode);
			finish();
		}
	}

	public void click_next(View view) {
		InteractionLogging.getInstance().logClick((Button) view);
		
		/*if(mFamilyName.getText().toString().equals("")) {
    		String msg = getResources().getString(R.string.error_family_name);
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(msg).setNeutralButton("Ok", null).show();
    		Util.getUtil().say(msg);
    	*/
		Contact.getContact().setFamilyName(mFamilyName.getText().toString());
		startActivityForResult(new Intent(this, InsertPhoneActivity.class), 0);
		Util.getUtil().vibrate();
    }

	public void click_save(View view) {
		/*
		 * if(mFamilyName.getText().toString().equals("")) { String msg =
		 * getResources().getString(R.string.error_family_name);
		 * 
		 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 * builder.setMessage(msg).setNeutralButton("Ok", null).show();
		 * Util.getUtil().say(msg); } else {
		 */
		if(Contact.getContact().getPhone() == null) { //Verifica se preencheu o n�mero do telefone
	    	DialogInterface.OnClickListener dialogClickListener = new
		        	OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				switch(which) {
		    					case DialogInterface.BUTTON_POSITIVE:
		    						//A��o quando o usu�rio clicar no bot�o sim
		    						Contact.getContact().setFirstName(mFamilyName.getText().toString());
		    						save_contact();
		    						Util.getUtil().vibrate();
		    						break;
		    					case DialogInterface.BUTTON_NEGATIVE:
		    						//A��o quando o usu�rio clicar no bot�o n�o
		    						break;
		    				}
		    			}
		    		};
		    	
		    	
		    	String msg = getResources().getString(R.string.save_without_fone);
		    	
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setMessage(msg)
		        	.setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
		        	.setNegativeButton(getResources().getString(R.string.no), dialogClickListener)
		        	.show();
		        Util.getUtil().say(msg);
    	} else {
    	
    		Contact.getContact().setFamilyName(mFamilyName.getText().toString());
    	
    		startActivityForResult(new Intent(this, InsertPhoneActivity.class), 0);
    		Util.getUtil().vibrate();
    	}
	}
	
	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		InteractionLogging.getInstance().logTextViewBackspace(s, start, after, count);
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		InteractionLogging.getInstance().logTextView(s, start, before, count);
		
		if(mFamilyName.length() > 0) {
			 mSave.setEnabled(true); 
			 mNext.setEnabled(true); 
		} else {
			 mSave.setEnabled(false);
			 mNext.setEnabled(false);
		}
	}
}
