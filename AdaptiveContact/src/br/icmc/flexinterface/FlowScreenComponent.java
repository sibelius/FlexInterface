package br.icmc.flexinterface;

import java.util.ArrayList;

import android.util.Log;
import OpenCOM.ILifeCycle;
import OpenCOM.IMetaInterface;
import OpenCOM.IUnknown;
import OpenCOM.OpenCOMComponent;

public class FlowScreenComponent extends OpenCOMComponent implements IFlowScreen, ILifeCycle, IUnknown, IMetaInterface {

	public FlowScreenComponent(IUnknown mpIOCM) {
		super(mpIOCM);
	}

	// ILifeCycle
	public boolean startup(Object data) {
		return true;
	}

	public boolean shutdown() {
		return true;
	}

	// IFlowScreen
	protected ArrayList<String> mScreens;
	protected int mCurrentScreen;
	
	@SuppressWarnings("unchecked")
	private boolean isScreensAvailable() {
		if(mScreens == null) {
			mScreens = (ArrayList<String>) GetAttributeValue("br.icmc.flexinterface.IFlowScreen", 
				"Interface", "Screens").Value;
			
			mCurrentScreen = 0;
		}
		
		if(mScreens != null)
			return true;
		else
			return false;
	}

	public void setCurrentScreen(String screen) {
		if(!isScreensAvailable())
			return;
		
		int newCurrent = mScreens.indexOf(screen);
		if(newCurrent != -1)
			mCurrentScreen = newCurrent;
	}
	
	public void setCurrentScreen(int screen) {
		mCurrentScreen = screen;
	}

	public String getCurrentScreen() {
		if(!isScreensAvailable())
			return null;
		
		return mScreens.get(mCurrentScreen);
	}

	public String nextScreen() {
		//Debug
		//Log.d("mCurrentScreen", String.valueOf(mCurrentScreen+1));
				
		if( (!isScreensAvailable()) || (mCurrentScreen >= mScreens.size()) )
			return null;
		
		return mScreens.get(++mCurrentScreen);
	}

	public String previousScreen() {
		//Debug
		//Log.d("mCurrentScreen", String.valueOf(mCurrentScreen-1));
		
		if( (!isScreensAvailable()) || (mCurrentScreen == 0) )
			return null;
		
		return mScreens.get(--mCurrentScreen);
	}	
}
