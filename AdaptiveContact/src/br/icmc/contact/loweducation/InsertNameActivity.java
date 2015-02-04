package br.icmc.contact.loweducation;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import br.icmc.contact.ContactOpenCOM;
import br.icmc.contact.ElderlyActivity;
import br.icmc.contact.R;
import br.icmc.contact.Util;
import br.icmc.flexinterface.InteractionLogging;

public class InsertNameActivity extends ElderlyActivity implements TextWatcher {
	private EditText mName;
	private Button mContinue;
	//private ImageButton mVoice;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.le_insert_name);

		mName = (EditText) findViewById(R.id.name);
		mName.addTextChangedListener(this);
		
		mContinue  = (Button) findViewById(R.id.insert_number);
		mContinue.setEnabled(false);
		
		mCurrentProfile = LOW_EDUCATION_PROFILE;
		//mVoice = (ImageButton) findViewById(R.id.voice);
/*
		// Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mVoice.setEnabled(false);
        }
	*/	
		Util.getUtil().say(getResources().getString(R.string.error_name));
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	@Override
	public void onResume() {
		super.onResume();

		mName.setText(Contact.getContact().getName());
	}

	@Override
	public void onPause() {
		super.onPause();

		//Contact.getContact().setName(mName.getText().toString());
		//InteractionLogging.getInstance().log(mName.getTag().toString(), mName.getText().toString());
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
	            Contact.getContact().setName(matches.get(0));
	            Util.getUtil().say(matches.get(0));
			}
        } else if (resultCode == 1) { // Contato gravado
			finish();
		}
	}

	public void click_insert_number(View view) {
		InteractionLogging.getInstance().logClick((Button)view);
		
		if (mName.getText().toString().equals("")) {
			String msg = getResources().getString(R.string.error_name);
			InteractionLogging.getInstance().log(InteractionLogging.ERROR, msg);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(msg).setNeutralButton("Ok", null).show();
			Util.getUtil().say(msg);
		} else {
			Contact.getContact().setName(mName.getText().toString());
			/*
			startActivityForResult(new Intent(this, InsertPhoneActivity.class),
					0);
*/
			try {
				startActivityForResult(new Intent(this, 
					Class.forName(ContactOpenCOM.getInstance().getFlexAndroid().nextScreen())),
					0);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			
			Util.getUtil().vibrate();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	click_exit(null);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
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
		enableButton(mName, mContinue);
	}
}
