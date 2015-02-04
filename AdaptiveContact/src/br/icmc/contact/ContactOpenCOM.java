package br.icmc.contact;

import java.util.ArrayList;

import OpenCOM.ILifeCycle;
import OpenCOM.IMetaInterface;
import OpenCOM.IOpenCOM;
import OpenCOM.IUnknown;
import OpenCOM.OpenCOM;
import br.icmc.flexinterface.IFlexComp;

public class ContactOpenCOM {
	private IOpenCOM pIOCM;
	private long ConnID;
	private IFlexComp pIFlexAndroid;
	private IUnknown pFlexAndroidIUnk;
	private IUnknown pHighEducationFlowIUnk;
	private IUnknown pLowEducationFlowIUnk;
	private IUnknown pElderlyProfileCheckerIUnk;
	
	private static ContactOpenCOM mInstance;
	
	public static ContactOpenCOM getInstance() {
		if(mInstance == null)
			mInstance = new ContactOpenCOM();
		
		return mInstance;
	}
	
	private ContactOpenCOM() {
		createOpenCOMRuntime();
		
		//pIOCM.QueryInterface("OpenCOM.IDebug");
		
		createFlexAndroid();
		getFlexAndroidInterface();
		
		createHighEducationFlowComponent();
		createLowEducationFlowComponent();
		createElderlyProfileChecker();
		
		connectElderlyProfileChecker();
	}
	
	public IFlexComp getFlexAndroid() {
		return pIFlexAndroid;
	}
	
	public void setHighEducationFlow(ArrayList<String> screens) {
		IMetaInterface pMeta = (IMetaInterface) pHighEducationFlowIUnk.QueryInterface("OpenCOM.IMetaInterface");
		
		pMeta.SetAttributeValue("br.icmc.flexinterface.IFlowScreen", "Interface", "Screens", 
			"java.util.ArrayList", (ArrayList<String>) screens);
	}
	
	public void setLowEducationFlow(ArrayList<String> screens) {
		IMetaInterface pMeta = (IMetaInterface) pLowEducationFlowIUnk.QueryInterface("OpenCOM.IMetaInterface");
		
		pMeta.SetAttributeValue("br.icmc.flexinterface.IFlowScreen", "Interface", "Screens", 
			"java.util.ArrayList", (ArrayList<String>) screens);
		
		//System.out.println(b);
	}
	
	private void createOpenCOMRuntime() {
		OpenCOM runtime = new OpenCOM();
		pIOCM = (IOpenCOM) runtime.QueryInterface("OpenCOM.IOpenCOM");
	}
	
	private void createFlexAndroid() {
		pFlexAndroidIUnk = (IUnknown) pIOCM.createInstance(
				"br.icmc.flexinterface.elderly.FlexAndroid", "FlexAndroid");
		ILifeCycle pILife = (ILifeCycle) pFlexAndroidIUnk.QueryInterface("OpenCOM.ILifeCycle");
		pILife.startup(pIOCM);
	}
	
	private void getFlexAndroidInterface() {
		pIFlexAndroid = (IFlexComp) 
	    		pFlexAndroidIUnk.QueryInterface(
	    				"br.icmc.flexinterface.IFlexComp");
	}
	
	private void createHighEducationFlowComponent() {
		pHighEducationFlowIUnk = (IUnknown) pIOCM.createInstance(
			"br.icmc.flexinterface.elderly.HighEducationFlow", "HighEducationFlow");
		ILifeCycle pILife = (ILifeCycle) pHighEducationFlowIUnk.QueryInterface("OpenCOM.ILifeCycle");
		pILife.startup(pIOCM);
	}
	
	private void createLowEducationFlowComponent() {
		pLowEducationFlowIUnk = (IUnknown) pIOCM.createInstance(
			"br.icmc.flexinterface.elderly.LowEducationFlow", "LowEducationFlow");
		ILifeCycle pILife = (ILifeCycle) pLowEducationFlowIUnk.QueryInterface("OpenCOM.ILifeCycle");
		pILife.startup(pIOCM);
	}
	
	private void createElderlyProfileChecker() {
		pElderlyProfileCheckerIUnk = (IUnknown) pIOCM.createInstance(
				"br.icmc.flexinterface.elderly.ElderlyProfileChecker", "ElderlyProfileChecker");
		ILifeCycle pILife = (ILifeCycle) pElderlyProfileCheckerIUnk.QueryInterface("OpenCOM.ILifeCycle");
		pILife.startup(pIOCM);
	}
	
	public void connectHighEducationFlow() {
		connect(pHighEducationFlowIUnk, "br.icmc.flexinterface.IFlowScreen");
	}
	
	public void connectLowEducationFlow() {
		connect(pLowEducationFlowIUnk, "br.icmc.flexinterface.IFlowScreen");
	}
	
	public void connectElderlyProfileChecker() {
		pIOCM.connect(pFlexAndroidIUnk, pElderlyProfileCheckerIUnk, "br.icmc.flexinterface.IProfileChecker");
    	
    	//pIDebug.dump();
	}
	
	private void connect(IUnknown pComponent, String Interface) {
		pIOCM.disconnect(ConnID);
    	
    	ConnID = pIOCM.connect(pFlexAndroidIUnk, pComponent, Interface);
    	
    	//pIDebug.dump();
	}
}
