package br.icmc.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ParserLoggingActivity extends Activity {

	private File mPath = new File(Environment.getExternalStoragePublicDirectory("") + "/logging/");
	private String[] mFileList;
	private static final String FTYPE = ".txt";
	
	private TextView mTimeToCompleteTask;
	private TextView mBackspaceCount;
	private TextView mBackButtonCount;
	private TextView mKeypressedCount;
	private TextView mTaskSuceed;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser_logging);
        
        mTimeToCompleteTask = (TextView) findViewById(R.id.timeToCompleteTaskValue);
        mBackspaceCount = (TextView) findViewById(R.id.backspaceCountValue);
        mBackButtonCount = (TextView) findViewById(R.id.backCountValue);
        mKeypressedCount = (TextView) findViewById(R.id.keypressedValue);
        mTaskSuceed = (TextView) findViewById(R.id.taskSucessedValue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_parser_logging, menu);
        return true;
    }
    
    private String[] loadFileList() {
    	if(mPath.exists()) {
    		FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					return filename.contains(FTYPE) || sel.isDirectory();
				}
			};
			mFileList = mPath.list(filter);
    	} else
    		mFileList = new String[0];
    	
    	return mFileList;
    }
    
    private int index=0;
    
    //Load Logging click
    public void loadLogging_click(View view) {
    	AlertDialog.Builder builder = new Builder(ParserLoggingActivity.this);
    	builder.setTitle("Escolha o arquivo de logging");
    	
    	builder.setSingleChoiceItems(loadFileList(), 0, new DialogInterface.OnClickListener() {	
			public void onClick(DialogInterface dialog, int which) {
				index = which;
			}
		})
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					StatisticsGenerator.getInstance().generateStatistics(mFileList[index]);
					populateViews();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
    	builder.show();
    }

	protected void populateViews() {
		StatisticsGenerator sg = StatisticsGenerator.getInstance();
		StringBuilder interationTimeStr = new StringBuilder();
		
		long hours = sg.getInterationTime(Calendar.HOUR_OF_DAY);
		long minutes = sg.getInterationTime(Calendar.MINUTE);
		long seconds = sg.getInterationTime(Calendar.SECOND);
		long miliseconds = sg.getInterationTime(Calendar.MILLISECOND);
		
		if(hours % 24 > 0)
			interationTimeStr.append(hours % 24).append("h");
		
		if(minutes % 60 > 0)
			interationTimeStr.append(minutes % 60).append("m");
		
		if(seconds % 60> 0)
			interationTimeStr.append(seconds % 60).append("s");
		
		if(miliseconds % 100 > 0)
			interationTimeStr.append(miliseconds % 100).append("ms");
		
		Log.d("InteractionTime", interationTimeStr.toString());
		mTimeToCompleteTask.setText(interationTimeStr);
		
		mBackspaceCount.setText(""+sg.getBackspaceCount());
		mBackButtonCount.setText(""+sg.getBackButtonCount());
		mKeypressedCount.setText(""+sg.getKeypressedCount());
		mTaskSuceed.setText(""+sg.getTaskSucessed());		
	}
}
