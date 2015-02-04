package br.icmc.contact;

import java.util.Locale;

import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class Util implements OnInitListener {
	private TextToSpeech mTTS;
	private Vibrator mVibrator;

	private static Util singleton;
	
	private Util() {
	}
	
	public static Util getUtil() {
		if(singleton == null)
			singleton = new Util();
		return singleton;
	}
	
	// speak the user text
	public void say(String speech) {
		// speak straight away
		if(mTTS != null)
			mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	// Vibrate the cell
	public void vibrate() {
		mVibrator.vibrate(200);
	}

	public void setTTS(TextToSpeech TTS) {
		mTTS = TTS;
	}
	
	public void setVibrator(Vibrator vibrator) {
		mVibrator = vibrator;
	}

	// setup Util
	public void onInit(int initStatus) {
		// check for successful instantiation
		if (initStatus == TextToSpeech.SUCCESS) {
			if(mTTS.isLanguageAvailable(new Locale("pt_BR")) == TextToSpeech.LANG_AVAILABLE) {
				mTTS.setLanguage(new Locale("pt_BR"));
				mTTS.speak("TTS Habilitado", TextToSpeech.QUEUE_FLUSH, null);
			} else {
				Log.d("TTS ERROR", "Linguagem não disponivel");
			}
		} else if (initStatus == TextToSpeech.ERROR) {
			Log.d("TTS ERROR", "Erro");
		}
	}
}
