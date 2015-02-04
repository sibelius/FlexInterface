package br.icmc.contact;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import br.icmc.flexinterface.InteractionLogging;

public class MyContactActivity extends Activity implements Runnable {
	private int MY_DATA_CHECK_CODE = 0;
	
	public static final int LOW_EDUCATION_PROFILE = 0;
	public static final int HIGH_EDUCATION_PROFILE = 1;
	
	protected int mCurrentProfile;
	public String mElderlyName;
	public EditText mEdtElderlyName;
	
	ArrayList<String> lowEducationFlow = new ArrayList<String>();
	ArrayList<String> highEducationFlow = new ArrayList<String>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.main);
		setTitle("Cadastrar Contato");
		
		mEdtElderlyName = (EditText) findViewById(R.id.edtElderlyName);
		
		// Get the vibrator
		Util.getUtil().setVibrator(
				(Vibrator) getSystemService(Context.VIBRATOR_SERVICE));

		// check for TTS data
		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
		
		Util.getUtil().say(getResources().getString(R.string.choose_profile));
		
		ContactOpenCOM coc = ContactOpenCOM.getInstance();
		initFlows();
		coc.setLowEducationFlow(lowEducationFlow);
		coc.setHighEducationFlow(highEducationFlow);
		
		//Init the Checker
		coc.getFlexAndroid().initChecker(Environment.getExternalStorageDirectory());
		
		new Thread(this).start();
	}
	
	private void initFlows() {
		String pacote = "br.icmc.contact.loweducation.";
		lowEducationFlow.add(pacote + "InsertNameActivity");
		lowEducationFlow.add(pacote + "InsertPhoneActivity");
		lowEducationFlow.add(pacote + "InsertMoreInfoActivity");
		lowEducationFlow.add(pacote + "InsertPhotoActivity");
		
		pacote = "br.icmc.contact.higheducation.";
		highEducationFlow.add(pacote + "InsertFirstNameActivity");
		highEducationFlow.add(pacote + "InsertFamilyNameActivity");
		highEducationFlow.add(pacote + "InsertPhoneActivity");
		highEducationFlow.add(pacote + "InsertAddressActivity");
		highEducationFlow.add(pacote + "InsertEmailActivity");
		highEducationFlow.add(pacote + "InsertPhotoActivity");
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if(!isFinishing())
			InteractionLogging.getInstance().log(InteractionLogging.CURRENT_SCREEN, this.getClass().getSimpleName());
	}

	@Override
	public void onPause() {
		super.onPause();
		// Zera o contato antigo
		Contact.getContact().clear();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		/*
		if(isFinishing()) {
			InteractionLogging.getInstance().finishCapture();
		}*/
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(isFinishing()) {
			InteractionLogging.getInstance().finishCapture();
		}
	}
	
	// act on result of Util data check
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// the user has the necessary data - create the Util
				Util.getUtil().setTTS(
						new TextToSpeech(getApplicationContext(), Util
								.getUtil()));
			} else {
				/*
				// no data - install it now
				Intent installTTSIntent = new Intent();
				installTTSIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installTTSIntent);
				*/
			}
		}
	}

	public void click_low_education(View view) {
		
		ContactOpenCOM.getInstance().connectLowEducationFlow();
		
		InteractionLogging.getInstance().setFile(Environment.getExternalStoragePublicDirectory(""), mEdtElderlyName.getText().toString());
		InteractionLogging.getInstance().initCapture();
		InteractionLogging.getInstance().logClick((Button)view);
		
		try {
			startActivityForResult(new Intent(this, 
				Class.forName(ContactOpenCOM.getInstance().getFlexAndroid().getCurrentScreen())),
				0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Util.getUtil().vibrate();
	}

	public void click_high_education(View view) {
		ContactOpenCOM.getInstance().connectHighEducationFlow();
		
		InteractionLogging.getInstance().setFile(Environment.getExternalStoragePublicDirectory(""), mEdtElderlyName.getText().toString());
		InteractionLogging.getInstance().initCapture();
		InteractionLogging.getInstance().logClick((Button)view);
		
		try {
			startActivityForResult(new Intent(this, 
				Class.forName(ContactOpenCOM.getInstance().getFlexAndroid().getCurrentScreen())),
				0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Util.getUtil().vibrate();
	}
	
	// ProfileChecker is run periodically
		public void run() {
			ContactOpenCOM coc = ContactOpenCOM.getInstance();
			
			while(true) {
				int newProfile = coc.getFlexAndroid().checker();
				
				//Verifica se � necess�rio trocar o perfil
				if(newProfile != mCurrentProfile) {
					mCurrentProfile = newProfile;
					
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("Seu aplicativo ser� adaptado para melhor atender as suas necessidades")
						.setNeutralButton("Ok", null).show();
					
					//->LowEducation
					if(mCurrentProfile == LOW_EDUCATION_PROFILE)
						coc.connectLowEducationFlow();
					else if(mCurrentProfile == HIGH_EDUCATION_PROFILE)
						coc.connectHighEducationFlow();
					
					coc.getFlexAndroid().setCurrentScreen(0);
					
					try {
						startActivityForResult(new Intent(this, 
								Class.forName(ContactOpenCOM.getInstance().getFlexAndroid().getCurrentScreen())),
								0);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					finish();
				}
				
				//Call this method each five seconds
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
}