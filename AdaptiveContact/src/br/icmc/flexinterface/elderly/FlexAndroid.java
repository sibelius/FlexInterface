package br.icmc.flexinterface.elderly;

import java.io.File;

import OpenCOM.IConnections;
import OpenCOM.ILifeCycle;
import OpenCOM.IMetaInterface;
import OpenCOM.IUnknown;
import OpenCOM.OCM_SingleReceptacle;
import OpenCOM.OpenCOMComponent;
import br.icmc.flexinterface.IFlexComp;
import br.icmc.flexinterface.IFlowScreen;
import br.icmc.flexinterface.IProfileChecker;

public class FlexAndroid extends OpenCOMComponent
	implements IFlexComp, IConnections, ILifeCycle, IUnknown, IMetaInterface {

	/**
	 * Require interface of type IFlowScreen
	 */
	public OCM_SingleReceptacle<IFlowScreen> m_PSR_IFlowScreen;
	
	public OCM_SingleReceptacle<IProfileChecker> m_PSR_IProfileChecker;
	
	public FlexAndroid(IUnknown mpIOCM) {
		super(mpIOCM);
		
		//Initiate the receptacles
		m_PSR_IFlowScreen = new OCM_SingleReceptacle<IFlowScreen>(IFlowScreen.class);
		m_PSR_IProfileChecker = new OCM_SingleReceptacle<IProfileChecker>(IProfileChecker.class);
	}
	
	// ILifeCycle
	public boolean startup(Object data) {
		return true;
	}

	public boolean shutdown() {
		return true;
	}

	// IConnections
	public boolean connect(IUnknown pSinkIntf, String riid, long provConnID) {
		if(riid.equalsIgnoreCase("br.icmc.flexinterface.IFlowScreen")) {
			return m_PSR_IFlowScreen.connectToRecp(pSinkIntf, riid, provConnID);
		} else if(riid.equalsIgnoreCase("br.icmc.flexinterface.IProfileChecker")) {
			return m_PSR_IProfileChecker.connectToRecp(pSinkIntf, riid, provConnID);
		}
		
		return false;
	}

	public boolean disconnect(String riid, long connID) {
		if(riid.equalsIgnoreCase("br.icmc.flexinterface.IFlowScreen")) {
			return m_PSR_IFlowScreen.disconnectFromRecp(connID);
		} else if(riid.equalsIgnoreCase("br.icmc.flexinterface.IProfileChecker")) {
			return m_PSR_IProfileChecker.disconnectFromRecp(connID);
		}

		return false;
	}


	//IFlexComp
	public void setCurrentScreen(String screen) {
		m_PSR_IFlowScreen.m_pIntf.setCurrentScreen(screen);
	}
	
	public void setCurrentScreen(int screen) {
		m_PSR_IFlowScreen.m_pIntf.setCurrentScreen(screen);
	}

	public String getCurrentScreen() {
		return m_PSR_IFlowScreen.m_pIntf.getCurrentScreen();
	}

	public String nextScreen() {
		return m_PSR_IFlowScreen.m_pIntf.nextScreen();
	}

	public String previousScreen() {
		return m_PSR_IFlowScreen.m_pIntf.previousScreen();
	}

	//IProfileChecker
	public void initChecker(File interaction) {
		m_PSR_IProfileChecker.m_pIntf.initChecker(interaction);
	}

	public void finishChecker() {
		m_PSR_IProfileChecker.m_pIntf.finishChecker();
	}

	public int checker() {
		return m_PSR_IProfileChecker.m_pIntf.checker();
	}	
}
