package br.icmc.contact;

import br.icmc.flexinterface.InteractionLogging;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ElderlyActivity extends Activity {
	protected static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	public static final int LOW_EDUCATION_PROFILE = 0;
	public static final int HIGH_EDUCATION_PROFILE = 1;
	
	protected TextView mTitle;
	protected Button mExit;
	protected int mCurrentProfile;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.custom_title);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        mTitle = (TextView) findViewById(R.id.title);
        mExit = (Button) findViewById(R.id.btn_exit);
        
        if(Contact.getContact().getName() != null)
        	mTitle.setText(Contact.getContact().getName());
        else
        	if(Contact.getContact().getFirstName() != null)
        		if(Contact.getContact().getFamilyName() != null)
        			mTitle.setText(Contact.getContact().getFirstName() + " " + Contact.getContact().getFamilyName());
        		else
        			mTitle.setText(Contact.getContact().getFirstName());
        
        //Create a Thread to profile Checker
        //new Thread(this).start();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	InteractionLogging.getInstance().log(InteractionLogging.CURRENT_SCREEN, this.getClass().getSimpleName());
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	click_back(null);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
    
    /**
     * A refactored method do enable a button based on the content of a edittext
    */
    protected void enableButton(EditText edt, Button btnAtivado) {
		if(edt.length() > 0 )
			btnAtivado.setEnabled(true);
		else
			btnAtivado.setEnabled(false);
	}
    
    public void click_back(View view) {
    	InteractionLogging.getInstance().log(InteractionLogging.BACK_BUTTON, "Voltar");
    	
    	ContactOpenCOM.getInstance().getFlexAndroid().previousScreen();
    	finish();
    }
    
    public void click_exit(View view) {
    	InteractionLogging.getInstance().log(InteractionLogging.EXIT, "Tentando Sair");
    	
        DialogInterface.OnClickListener dialogClickListener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					InteractionLogging.getInstance().log(InteractionLogging.EXIT_OK, "Saiu com Sucesso");
					InteractionLogging.getInstance().log(InteractionLogging.UNSAVED, "Contato não salvo");
					
					// Aï¿½ï¿½o quando o usuï¿½rio clicar no botï¿½o sim
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
					Contact.getContact().clear();
					setResult(1);
					ContactOpenCOM.getInstance().getFlexAndroid().previousScreen();
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					InteractionLogging.getInstance().log(InteractionLogging.EXIT_CANCEL, "Desistiu de Sair");
					// Aï¿½ï¿½o quando o usuï¿½rio clicar no botï¿½o nï¿½o
					break;
				}
			}
		};

		String msg = getResources().getString(R.string.cancel_action);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg)
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
		Util.getUtil().say(msg);        
    }
    
    protected void save_contact() {
    	InteractionLogging.getInstance().log(InteractionLogging.SAVE, "Salvando Contato");
    	
    	int status = Contact.getContact().save(getContentResolver());
    	
    	String msg;
    	
    	if(status == 1) {
    		msg = getResources().getString(R.string.contact_saved);
    		
    		Contact.getContact().clear();
    		
    		InteractionLogging.getInstance().log(InteractionLogging.SAVE_OK, "Contato salvo com sucesso");
    	} else {
    		msg = getResources().getString(R.string.contact_unsaved);
    		
    		InteractionLogging.getInstance().log(InteractionLogging.SAVE_ERROR, "Contato nao salvo");
    	}
    	
    	DialogInterface.OnClickListener dialogClickListener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ContactOpenCOM.getInstance().getFlexAndroid().previousScreen();
		    	finish();
			}
		};
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setNeutralButton("Ok", dialogClickListener).show();
		
		Util.getUtil().say(msg);
		
    	setResult(status);
    }
    
    public void click_voice(View view) {
    	InteractionLogging.getInstance().log(InteractionLogging.ONCLICK, "VOICE_RECOGNITION");
    	
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		
		startVoiceRecognitionActivity();
	}
    
    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Reconhecimento de Voz");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
/*
    // ProfileChecker is run periodically
	@Override
	public void run() {
		ContactOpenCOM coc = ContactOpenCOM.getInstance();
		
		while(true) {
			int newProfile = coc.getFlexAndroid().checker();
			
			//Verifica se ï¿½ necessï¿½rio trocar o perfil
			if(newProfile != mCurrentProfile) {
				mCurrentProfile = newProfile;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Seu aplicativo serï¿½ adaptado para melhor atender as suas necessidades")
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
	*/
}
