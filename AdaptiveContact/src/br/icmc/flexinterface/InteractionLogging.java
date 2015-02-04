package br.icmc.flexinterface;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Button;

/**
 * This class capture the interaction data of a user
 * 
 * @author sibelius
 */
public class InteractionLogging {
	
	//Types of Logging
	public static final String BEGIN_INTERACTION = "BEGIN_INTERACTION";
	public static final String END_INTERACTION = "END_INTERACTION";
	
	public static final String CURRENT_SCREEN = "CURRENT_SCREEN"; //Marca a tela atual
	
	public static final String ONCLICK = "ONCLICK";
	public static final String BACKSPACE = "BACKSPACE";
	public static final String KEYPRESSED = "KEYPRESSED";
	
	public static final String GIVEUP = "GIVEUP"; //Desistiu de salvar o contato
	public static final String ERROR = "ERROR";
	
	public static final String SAVE = "SAVE";
	public static final String SAVE_OK = "SAVE_OK";
	public static final String SAVE_ERROR = "SAVE_ERROR";
	public static final String UNSAVED = "UNSAVED";
	
	public static final String BACK_BUTTON = "BACK_BUTTON";
	public static final String EXIT = "EXIT";
	public static final String EXIT_OK = "EXIT_OK";
	public static final String EXIT_CANCEL = "EXIT_CANCEL";
	
	private BufferedOutputStream mWriter = null; //Used to write on the file
	
	private File mLogFile;
	
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy-hh_mm_ss");
	
	
	private static InteractionLogging sSingleton = null;
	/*
	public static InteractionLogging getInstance(File dir) {
		if(sSingleton == null)
			sSingleton = new InteractionLogging(dir);
		return sSingleton;
	}
	*/
	public static InteractionLogging getInstance() {
		if(sSingleton == null)
			sSingleton = new InteractionLogging();
		return sSingleton;
	}
	
	private InteractionLogging() {
	}
	/*
	public InteractionLogging(File dir) {
		//Cria o diretorio de Logs
		File mLogDir = new File(dir, "logging/");
		if(!mLogDir.isDirectory())
			mLogDir.mkdirs();
		
		mLogFile = new File(mLogDir, "interaction" + sdf.format(Calendar.getInstance().getTime()) + ".txt");
		try {
			//Cria o arquivo de logging
			mWriter = new BufferedOutputStream(new FileOutputStream(mLogFile, false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.d("Erro InteractionLogging", e.getMessage());
			mWriter = null;
		} //Cria o arquivo de Log
	}
	*/
	public void setFile(File dir, String name) {
		//Cria o diretorio de Logs
		File mLogDir = new File(dir, "logging/");
		if(!mLogDir.isDirectory())
			mLogDir.mkdirs();

		if(name.length() == 0)
			name = "interaction" + sdf.format(Calendar.getInstance().getTime()) + ".txt";
		mLogFile = new File(mLogDir, name + ".txt");

		try {
			mWriter = new BufferedOutputStream(new FileOutputStream(mLogFile, false));
		} catch (FileNotFoundException e) {
			Log.d("Erro", e.getMessage()); 
			mWriter = null;
		}
	}
	
	private String getFormattedTime() {
		return sdf.format(Calendar.getInstance().getTime());
	}
	
	/**
	 * Log a data with a tag and a message and a associate time
	 * @param tag Tag of this Log
	 * @param msg Message associate with this log
	 */
	public void log(String tag, String msg) {
		if(mWriter == null)
			return;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(getFormattedTime()).append('\t').append(tag).append('\t').append(msg).append('\n');
		
		try {
			mWriter.write(sb.toString().getBytes());
			Log.d(tag, msg);
			//mWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void initCapture() {
		log(BEGIN_INTERACTION, "Inicio da Interacao");
	}
	
	public void finishCapture() {
		log(END_INTERACTION, "Fim da Interacao");
		
		if(mWriter == null)
			return;
		
		try {
			mWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void logClick(Button b) {
		log(ONCLICK, b.getText().toString());
	}
	
	public void logTextView (CharSequence s, int start, int before, int count) {
		
		if( (before == 0) && (s.length() > 0) )
			log(KEYPRESSED, ""+s.subSequence(start, start+count));
		//log(KEYPRESSED, ""+s+";start: "+start+";before: "+before);
	}
	
	public void logTextViewBackspace (CharSequence s, int start, int after, int count) {
		
		if( (after == 0) && (s.length() > 0) && (start != s.length()))
			log(BACKSPACE, ""+s.subSequence(start,  start+count));
		//log(BACKSPACE, ""+s+";start: "+start+";after: "+after);
	}	
}
