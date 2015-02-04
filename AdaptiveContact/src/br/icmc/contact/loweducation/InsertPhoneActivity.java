package br.icmc.contact.loweducation;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import br.icmc.contact.Contact;
import br.icmc.contact.ContactOpenCOM;
import br.icmc.contact.ElderlyActivity;
import br.icmc.contact.R;
import br.icmc.contact.Util;
import br.icmc.flexinterface.InteractionLogging;

public class InsertPhoneActivity extends ElderlyActivity implements OnClickListener, TextWatcher {
	private EditText mPhone;
	
	private Button mFinished;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.le_insert_phone);

		mPhone = (EditText) findViewById(R.id.phone);
		mPhone.addTextChangedListener(this);
		
		mFinished = (Button) findViewById(R.id.finished);
		mFinished.setEnabled(false);
		
		Util.getUtil().say(getResources().getString(R.string.error_phone));
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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

	public void click_finished(View view) {
		if(view instanceof Button)
			InteractionLogging.getInstance().logClick((Button) view);
		else
			InteractionLogging.getInstance().log(InteractionLogging.ONCLICK, ((TextView)view).getText().toString() );
		
		String msg;
		if (mPhone.getText().toString().equals("")) {
			msg = getResources().getString(R.string.error_phone);
			InteractionLogging.getInstance().log(InteractionLogging.ERROR, msg);			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(msg).setNeutralButton("Ok", null).show();
			Util.getUtil().say(msg);
		} else if (mPhone.getText().length() < 3) { // Verifica se o telefone �
													// v�lido
			DialogInterface.OnClickListener dialogClickListener = new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						// A��o quando o usu�rio clicar no bot�o sim
						Contact.getContact().setPhone(
								mPhone.getText().toString());

						save_contact();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						InteractionLogging.getInstance().log(InteractionLogging.GIVEUP, "Desistiu de Salvar");
						// A��o quando o usu�rio clicar no bot�o n�o
						break;
					}
				}
			};
			msg = getResources().getString(R.string.error_phone1);
			InteractionLogging.getInstance().log(InteractionLogging.ERROR, msg);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(msg)
					.setPositiveButton(getResources().getString(R.string.yes),
							dialogClickListener)
					.setNegativeButton(getResources().getString(R.string.no),
							dialogClickListener).show();
			Util.getUtil().say(msg);
		} else {

			Contact.getContact().setPhone(mPhone.getText().toString());

			save_contact();
		}
	}

	public void click_more_info(View view) {
		InteractionLogging.getInstance().log(InteractionLogging.ONCLICK, "Informe outras informacoes");
		
		String msg;
		if (mPhone.getText().toString().equals("")) {
			msg = getResources().getString(R.string.error_phone);
			InteractionLogging.getInstance().log(InteractionLogging.ERROR, msg);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(msg).setNeutralButton("Ok", null).show();
			Util.getUtil().say(msg);
		} else if(mPhone.getText().length() < 3) { // Verifica se o telefone �
			
			msg = getResources().getString(R.string.error_phone1);
			InteractionLogging.getInstance().log(InteractionLogging.ERROR, msg);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(msg)
					.setPositiveButton(getResources().getString(R.string.yes),
							this)
					.setNegativeButton(getResources().getString(R.string.no),
							this).show();
			Util.getUtil().say(msg);
		} else {
			Contact.getContact().setPhone(mPhone.getText().toString());

			try {
				String str = ContactOpenCOM.getInstance().getFlexAndroid().nextScreen();
				Log.d("Erro", str);
				startActivityForResult(new Intent(this, 
					Class.forName(str)),
					0);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			Util.getUtil().vibrate();
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				// A��o quando o usu�rio clicar no bot�o sim
				Contact.getContact().setPhone(mPhone.getText().toString());

				try {
					startActivityForResult(new Intent(this, 
						Class.forName(ContactOpenCOM.getInstance().getFlexAndroid().nextScreen())),
						0);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
								
				Util.getUtil().vibrate();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				// A��o quando o usu�rio clicar no bot�o n�o
				InteractionLogging.getInstance().log(InteractionLogging.GIVEUP, "Desistiu");
				break;
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
		enableButton(mPhone, mFinished);
	}
}
