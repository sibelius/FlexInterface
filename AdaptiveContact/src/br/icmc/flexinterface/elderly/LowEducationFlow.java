package br.icmc.flexinterface.elderly;

import OpenCOM.ILifeCycle;
import OpenCOM.IMetaInterface;
import OpenCOM.IUnknown;
import br.icmc.flexinterface.FlowScreenComponent;
import br.icmc.flexinterface.IFlowScreen;

public class LowEducationFlow extends FlowScreenComponent implements IFlowScreen, ILifeCycle, IUnknown, IMetaInterface {

	public LowEducationFlow(IUnknown mpIOCM) {
		super(mpIOCM);
	}
}
