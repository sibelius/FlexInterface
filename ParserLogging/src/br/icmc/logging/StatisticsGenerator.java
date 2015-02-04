package br.icmc.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import android.os.Environment;
import android.util.Log;

public class StatisticsGenerator {
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

	public enum TOKEN {
		//Types of Logging
		BEGIN_INTERACTION("BEGIN_INTERACTION"),
		END_INTERACTION("END_INTERACTION"),

		CURRENT_SCREEN("CURRENT_SCREEN"),

		ONCLICK("ONCLICK"),
		BACKSPACE("BACKSPACE"),
		KEYPRESSED("KEYPRESSED"),
		
		GIVEUP("GIVEUP"), //Desistiu de salvar o contato
		ERROR("ERROR"),
		SAVE("SAVE"),
		SAVE_OK("SAVE_OK"),
		SAVE_ERROR("SAVE_ERROR"),
		UNSAVED("UNSAVED"),
		
		BACK_BUTTON("BACK_BUTTON"),
		EXIT("EXIT"),
		EXIT_OK("EXIT_OK"),
		EXIT_CANCEL("EXIT_CANCEL");

		private String mText;

		TOKEN(String text) {
			mText = text;
		}

		public String getText() {
			return mText;
		}

		public static TOKEN fromString(String text) {
			if(text != null) {
				for(TOKEN t : TOKEN.values())
					if(t.mText.equalsIgnoreCase(text))
						return t;
			}
			return null;
		}
	}
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy-hh_mm_ss");

	//Estatisticas
	private Calendar mBeginInteractionTime=null; //Tempo de inicio da interação
	private Calendar mEndInteractionTime=null; //Tempo de fim da interação
	private int mBackspaceCount=0;
	private int mBackButtonCount=0;
	private int mKeypressedCount=0;
	private boolean mSucess=false;

	private static StatisticsGenerator sSingleton = null;
	private File mFile=null;

	public static StatisticsGenerator getInstance() {
		if(sSingleton == null)
			sSingleton = new StatisticsGenerator();
		return sSingleton;
	}

	private StatisticsGenerator() {
	}

	public void generateStatistics(String filename) throws FileNotFoundException  {
		mFile = new File(Environment.getExternalStoragePublicDirectory(""), "/logging/" + filename);

		mBackspaceCount = 0;
		mBackButtonCount = 0;
		mKeypressedCount = 0;
		mSucess = false;

		Log.d("Arquivo Log", mFile.getName());
		Scanner scanner = new Scanner(new FileReader(mFile));
		try {
			while( scanner.hasNextLine() )
				processLine(scanner.nextLine());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	private void processLine(String line) throws ParseException {
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter("\t");

		//Get the tokens
		Calendar time = new GregorianCalendar();

		if(scanner.hasNext())
			time.setTime(sdf.parse(scanner.next()));

		String tagStr="";
		if(scanner.hasNext())
			tagStr = scanner.next();

		Log.d("tagStr", tagStr);

		TOKEN tag = TOKEN.fromString(tagStr);

		String msg="";
		if(scanner.hasNext())
			msg = scanner.next();

		if(tag == null) {
			Log.d("TAG", "Unrecognized TAG");
			return;
		}

		Log.d("time", sdf.format(time.getTime()));
		Log.d("tag", tag.getText());
		Log.d("msg", msg);

		switch(tag) {
			case BEGIN_INTERACTION:
				mBeginInteractionTime = (Calendar) time.clone();
				break;
			case END_INTERACTION:
				mEndInteractionTime = (Calendar) time.clone();
				break;
			case CURRENT_SCREEN:
				break;
			case ONCLICK:
				break;
			case BACKSPACE:
				mBackspaceCount++;
				break;
			case BACK_BUTTON:
				mBackButtonCount++;
				break;
			case KEYPRESSED:
				mKeypressedCount++;
				break;
			case GIVEUP:
				break;
			case ERROR:
				break;
			case SAVE:
				break;
			case SAVE_OK:
				mSucess = true;
				break;
			case SAVE_ERROR:
				mSucess = false;
				break;
			case UNSAVED:
				mSucess = false;
				break;
			case EXIT:
				break;
			case EXIT_OK:
				break;
			case EXIT_CANCEL:
				break;
			default:
				break;
		}
	}

	public long getInterationTime(int unit) {
		if(mBeginInteractionTime != null && mEndInteractionTime != null) {
			return CalendarUtils.difference(mBeginInteractionTime, mEndInteractionTime, unit);
		}
		return -1;
	}

	//Quantas vezes utilizou o Backspace
	public int getBackspaceCount() {
		return mBackspaceCount;
	}

	//Quantas vezes utilizou o botão voltar
	public int getBackButtonCount() {
		return mBackButtonCount;
	}

	public int getKeypressedCount() {
		return mKeypressedCount;
	}

	public boolean getTaskSucessed() {
		return mSucess;
	}
}
