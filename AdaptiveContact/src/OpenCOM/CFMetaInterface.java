/*
 * CFMetaInterface.java
 *
 * OpenCOMJ is a flexible component model for reconfigurable reflection developed at Lancaster University.
 * Copyright (C) 2005 Paul Grace
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package OpenCOM;

import java.util.*;
import OpenCOM.Events.*;
/**
 * Description of an abstract class defining operations for creating a component framework.
 * An OpenCOM component extends this behaviour to become a component framework.
 * That is if you QI a component and it support ICFMetaInterface then you know it is
 * a component framework rather than a primitive OpenCOM component.
 * @see ICFMetaInterface
 * @see ILifeCycle
 * @author  Paul Grace
 * @version 1.3.5
 **/

public abstract class CFMetaInterface implements ICFMetaInterface, ILifeCycle, IUnknown, IMetaInterface{
    
    /**
     * Fixed reference to the OpenCOM runtime API.
     * @see OpenCOM.IOpenCOM
     */
    public MetaInterface Meta;
    protected OCM_SingleReceptacle<IOpenCOM> m_PSR_IOpenCOM;
    /**
     * Receptacle to plug-in the validation checks for this component framework.
     */
    public OCM_SingleReceptacle<IAccept> m_PSR_IAccept;
    private OCM_SingleReceptacle<IMetaInterface> m_PSR_IMetaInterface;
    private OCM_SingleReceptacle<IMetaInterception> m_PSR_IMetaInterception;
    private OCM_SingleReceptacle<IMetaArchitecture> m_PSR_IMetaArchitecture;
    
    /**
     * Meta-Information store - The list of components in the framework.
     */
    private Vector<IUnknown>  m_ppComps;	
    /** The current number of components in the framework. */
    private int m_pcElems;		

    /**
     * Meta-Information store - Backup of the last good configuration.
     * The list of components in that configuration. Utilised during
     * rollback.
     */
    private Vector<IUnknown>  m_ppCompsback;
    /* Number of backed up components. */
    private int m_pcElemsback;		

    /**
     * Meta-Information store - The list of exposed interfaces in the framework.
     */
    private Vector<ExposedInterface> m_intseq;
    /* Number of exposed interfaces. */
    private int m_piElems;		

    /**
     * Meta-Information store - Backup of the last good configuration.
     * The list of exposed interfaces in that configuration. Utilised during
     * rollback.
     */
    private Vector<ExposedInterface> m_intseqback;
    /* Number of backed up exposed interfaces. */
    private int m_piElemsback;

    /**
     * Meta-Information store - The list of exposed receptacles in the framework.
     */
    private Vector<ExposedReceptacle> m_recpseq;
    /* Number of exposed receptacles. */
    private int m_prElems;		

    /**
     * Meta-Information store - Backup of the last good configuration.
     * The list of exposed receptacles in that configuration. Utilised during
     * rollback.
     */
    private Vector<ExposedReceptacle> m_recpseqback;
    /* Number of backed up exposed receptacles. */
    private int m_prElemsback;

    /**
     * Meta-Information store - Backup of the last good configuration.
     * The list of connections in that configuration. Utilised during
     * rollback.
     */
    private Vector<OCM_ConnInfo_t> m_pConnInfoBack;
     /* Number of backed up connections. */
    private int m_pcConns;
    
    /**
     * The write access lock to the component framework.
     */
    private Semaphore CFlock;
    
    /**
     * The semaphore for updating the reader count.
     */
    private Semaphore ReadersMutex;
    
    /**
     * Integer describing the current number of operations executing within the framework.
     */
    private int ReadersCount;
    

    /** The local object of the framework responsible for implementing the
     * operations of the interface meta-model. */
    private CFInterceptors Interceptors; 
    

    /**
     * Internal implementation of a semaphore fobject or use in frameworks readers/writers lock 
     */
    private class Semaphore {
        private int counter;

        public Semaphore() {
            this(0);
        }

        public Semaphore(int i) {
            if (i < 0) throw new IllegalArgumentException(i + " < 0");
            counter = i;
        }

        /**
         * Increments internal counter, possibly awakening a thread
         * wait()ing in acquire().
         */
        public synchronized void release() {
            if (counter == 0) {
                this.notify();
            }
            counter++;
        }

        /**
         * Decrements internal counter, blocking if the counter is already
         * zero.
         *
         * @exception InterruptedException passed from this.wait().
         */
        public synchronized void acquire() throws InterruptedException {
            while (counter == 0) {
                this.wait();
            }
            counter--;
        }
    }
    
    /**
     * Local class definition for Exposed Interfaces that are stored in the MOPs of the framework.
     */
    public class ExposedInterface{
        /** Reference to the actual component instance that this exposed interface exists upon */
        public IUnknown pCompID;
        /** The interface type of this exposed interface. */
        public String IntfType;        

        /** 
         * Constructor
         */
        public ExposedInterface(IUnknown Comp, String Interface){
            pCompID = Comp;
            IntfType = Interface;
        }
    }
    
    /* Local class definition for Exposed Receptacles that are stored in the MOPs of the framework */ 
    public class ExposedReceptacle{
        /** Reference to the actual component instance that this exposed receptacle exists upon */
        public IUnknown pCompID;
        /** The interface type of this exposed receptacle. */
	public String Intf;   
        /** The receptacle type (e.g. single, multiple). */
	public String recpType;
        /** Reference to the IConnections interface that relates to the actual receptacle. */
	public IConnections pIConnect; 

        /**
         * Constructor
         */
        public ExposedReceptacle(IUnknown Comp, String Interface, String recType, IConnections pConnect){
            pCompID = Comp;
            Intf = Interface;
            recpType = recType;
            pIConnect = pConnect;
        }
    }
    
    /** 
     * The framework stores connection information for use during rollback of configurations. 
     */
    public class ConnectedComponent{
	public IUnknown pCompID;   // Component reference
	public long Connection;    // Connection ID
        
        // Constructor
        public ConnectedComponent(IUnknown comp, long Conn){
           pCompID = comp;
           Connection = Conn;
        }
    }

    /** Constructor. Creates a new instance of CFMetaInterface */
    public CFMetaInterface(IUnknown pRuntime) {
        
        m_PSR_IOpenCOM = new OCM_SingleReceptacle<IOpenCOM>(IOpenCOM.class);
        m_PSR_IOpenCOM.connectToRecp(pRuntime, "OpenCOM.IOpenCOM", 0);
        Meta = new MetaInterface((IOpenCOM) m_PSR_IOpenCOM.m_pIntf, this);
        m_PSR_IAccept = new OCM_SingleReceptacle<IAccept>(IAccept.class);
        
        // Three receptacle connections to the 3 OpenCOM meta-models
        m_PSR_IMetaInterface= new OCM_SingleReceptacle<IMetaInterface>(IMetaInterface.class);
        m_PSR_IMetaInterception= new OCM_SingleReceptacle<IMetaInterception>(IMetaInterception.class);
        m_PSR_IMetaArchitecture= new OCM_SingleReceptacle<IMetaArchitecture>(IMetaArchitecture.class);
        
        long connID1 = m_PSR_IOpenCOM.m_pIntf.connect(this, pRuntime, "OpenCOM.IMetaInterception");
        long connID2 = m_PSR_IOpenCOM.m_pIntf.connect(this, pRuntime, "OpenCOM.IMetaArchitecture");
        
        CFlock = new Semaphore(1);
        ReadersMutex = new Semaphore(1);
        Interceptors = new CFInterceptors(this);
        ReadersCount=0;
        
        m_ppComps = new Vector<IUnknown> ();
        m_ppCompsback = new Vector<IUnknown> ();
        m_intseq = new Vector<ExposedInterface>();
        m_intseqback = new Vector<ExposedInterface>();
        m_recpseq = new Vector<ExposedReceptacle>();
        m_recpseqback = new Vector<ExposedReceptacle>();
        m_pConnInfoBack = new Vector<OCM_ConnInfo_t>();
    }
    
    //! Interface IUnknown.
    
    /**
    * This method is an extension of QueryInterface to allow exposed interfaces to be found
    * and used. Basically the outer component framework's IUnknown QI method will call this
    * method if it is yet to find the required interface
    * @param InterfaceName A string describing the interface we are looking for
    * @param cfReference A reference to the outer component framework instance
    * @return A Reference of the component hosting the required interface
    */
    public Object QueryInterface(String InterfaceName, Object cfReference) {
        Class c = cfReference.getClass();
        Class[] theInterfaces = c.getInterfaces();
        for (int i = 0; i < theInterfaces.length; i++) {
                String interfaceName = theInterfaces[i].getName();
                if(interfaceName.equalsIgnoreCase(InterfaceName)){
                    return cfReference; //theInterfaces[i];
                }
        }
       
	// QueryInterface() of the matching exposed interface
	for(int index=0; index<m_piElems; index++){
            if(((ExposedInterface) m_intseq.get(index)).IntfType.equalsIgnoreCase(InterfaceName)){
                    Object proxy= ((ExposedInterface)m_intseq.get(index)).pCompID;
                    Delegator del = (Delegator) m_PSR_IMetaInterception.m_pIntf.GetDelegator((IUnknown) proxy, InterfaceName);
                    if(del!=null)
                        return del.HigherObject;
                    else
                        return proxy;
            }
	}
	 return null;
    }
    
    //! Interface ILifeCycle 
    public boolean shutdown() {
        CFlock.release();
        ReadersMutex.release();
        return true;
	
    }
    
    public boolean startup(Object pIOCM) {

        return true;
    }
    
    //! Interface ICFMetaInterface 
    
    /**
     * This method creates the component within the framework. The component is created, stored 
     * in the runtime, and inserted into this framework's meta-data.
     * @param componentType The type of the component to create.
     * @param componentName The unique name of the component to create. 
     * @return A reference to the newly created component instance.
     * @see OpenCOM.IUnknown
     */												
    public IUnknown create_component(String componentType, String componentName) {
        
        //notify any RV client that a component creation is about to be attempted from inside a framework
        m_PSR_IOpenCOM.m_pIntf.notifyEvent(EventTypes.OCMCF_CREATE, new FrameworkComponentCreateEvent(componentName, getComponentName(this)));
           
        //OpenCOM create component call
	IUnknown pIUnknown = (IUnknown) m_PSR_IOpenCOM.m_pIntf.createInstance(componentType, componentName );
        m_PSR_IOpenCOM.m_pIntf.setContained(componentName, true);
	if(pIUnknown == null){
            return null;
	}

        // Check if its already in this CF - if it is return the IUnknown
        // If not then we must report an error
        // Check both components are in local graph
        String Name;
        for(int index=0; index<m_pcElems; index++){
            Name = m_PSR_IOpenCOM.m_pIntf.getComponentName((IUnknown) m_ppComps.get(index)); 
            if (Name.equalsIgnoreCase(componentName)){
                    return (IUnknown) m_ppComps.get(index);
            }
        }
        
	// Add component to CF list
	m_ppComps.add(pIUnknown);
        m_pcElems++;
	
	return pIUnknown;
    }
    
   /**
     * This method inserts a previously instantiated component from the runtime, into
     * the framework instance.
     * @param componentRef The reference of the component instance.
     * @return A boolean indicating if the insert occured or not.
     * @see OpenCOM.IUnknown
     */											
    public boolean insert_component(IUnknown componentRef) {
        String Name = getComponentName(componentRef);
        m_PSR_IOpenCOM.m_pIntf.setContained(Name, true);
        // Check it isn't already in the local graph
	for(int i=0; i<m_pcElems; i++){
            if(componentRef.hashCode()==(m_ppComps.get(i).hashCode()))
                return false;
        }

	// Add component to CF list
	m_ppComps.add(componentRef);
        m_pcElems++;
	
	return true;
    }
	
    /**
     * This method deletas the component from the framework. The component is disconnected, 
     * deleted from the runtime, and this framework's meta-data is updated.
     * @param pIUnknown The component instance to delete 
     * @return A boolean indicating if the component was deleted or not.
     * @see OpenCOM.IUnknown
     */	
    public boolean delete_component(IUnknown pIUnknown) {

	// Delete component if its in the CF list
	for(int index=0;index<m_pcElems; index++){
            String compName = m_PSR_IOpenCOM.m_pIntf.getComponentName(m_ppComps.get(index));
            String compName2 = m_PSR_IOpenCOM.m_pIntf.getComponentName(pIUnknown);
            if(compName.equalsIgnoreCase(compName2)){
                // Remove Composite elements
                ICFMetaInterface pTempMeta = (ICFMetaInterface) pIUnknown.QueryInterface("OpenCOM.Framework.ICFMetaInterface");
                if(pTempMeta!=null){
                    Vector<IUnknown> list = new Vector<IUnknown>();
                    int noComps = pTempMeta.get_internal_components(list);
                    for(int i=0; i<noComps; i++){
                        pTempMeta.delete_component(list.get(i));
                    }
                }
                
                // delete from runtime - this will disconnect it first.
                if(m_PSR_IOpenCOM.m_pIntf.deleteInstance((IUnknown) m_ppComps.get(index))==false)
                        return false;
                m_ppComps.remove(index);
                m_pcElems--;
                return true;
            }
	}
        // Cannot delete it if its not in the list
	return false;
    }
      
    /**
     * This method binds together two components only if they both reside in the framework.
     * @param pIUnkSource The source component with the receptacle.
     * @param pIUnkSink The sink component with the interface.
     * @param InterfaceType The interface type to make the connection on.
     * @return A long describing the unique ID of this new connection. -1 indicates failure to connect.
     * @see OpenCOM.IUnknown
     */	
     public long local_bind(IUnknown pIUnkSource, IUnknown pIUnkSink, String InterfaceType) {
        boolean Source=false, Sink=false;

	// Check both components are in local graph
	for(int index=0; index<m_pcElems; index++){
            if (m_ppComps.get(index)==pIUnkSource){
                    Source=true;
            }
            if (m_ppComps.get(index)==pIUnkSink){
                    Sink=true;
            }
	}
	
        // If Source or Sink is outside then fail local bind
	if((Source==false)||(Sink==false))
            return -1;

        // Connect the two components through the runtime
	long ConnID = m_PSR_IOpenCOM.m_pIntf.connect(pIUnkSource, pIUnkSink, InterfaceType);

	return ConnID;
    }
     
    /**
     * This method disconnects two components only if they both reside in the framework
     * and are connected.
     * @param connID The unique ID of the connection to break.
     * @return A boolean indicating if the disconnection was made.
     */	
    public boolean break_local_bind(long connID) {
        Vector<Long> ConnectionList = new Vector<Long>();
	int connsCount=0;
	boolean inList=false;

	// check the connection exists in the local structure
	connsCount = get_internal_bindings(ConnectionList);

	// Check the ID is a connection in list
	for (int index=0; index<connsCount; index++){
		if(ConnectionList.elementAt(index).longValue()==connID){
			inList=true;
			break;
		}
	}

        // Connection doesn't exist therefore, cannot break
	if(inList==false)
		return false;

        return m_PSR_IOpenCOM.m_pIntf.disconnect(connID);
      
    }
    
    public boolean find_component(IUnknown pComp){
        // check if its composite
        for(int i=0; i<m_ppComps.size(); i++){
            if((IUnknown) m_ppComps.get(i)==pComp){
                return true;
            }
            else{
                IUnknown pFr = (IUnknown) m_ppComps.get(i);
                ICFMetaInterface pFrTest = (ICFMetaInterface) pFr.QueryInterface("OpenCOM.ICFMetaInterface");
                if(pFrTest!=null){
                    if (pFrTest.find_component(pComp))
                        return true;
                }
            }
        }
        return false;
    }
    
    public IUnknown find_component(String intfType){
        // check if its composite
        for(int i=0; i<m_ppComps.size(); i++){
            IUnknown cmp = (IUnknown) m_ppComps.get(i);
            if((cmp.QueryInterface(intfType)!=null)&&(cmp.QueryInterface("OpenCOM.ICFMetaInterface")==null)){
                return cmp;
            }
            else{
                ICFMetaInterface pFrTest = (ICFMetaInterface) cmp.QueryInterface("OpenCOM.ICFMetaInterface");
                if(pFrTest!=null){
                    IUnknown pRse = pFrTest.find_component(intfType);
                    if(pRse!=null)
                        return pRse;
                }
            }
        }
        return null;
    }
    /**
     * This method takes the interface from one of the framework's internal components
     * and then makes it one of its own functional interfaces.
     * @param rintf The interface type that will be exposed.
     * @param pComp The internal component hosting the the interface.
     * @return A boolean describing if the interface was exposed.
     * @see OpenCOM.IUnknown
     */	
    public boolean expose_interface(String rintf, IUnknown pComp) {
        
        // Check Component holding the interface to expose is in the CF
	boolean inCFGraph=find_component(pComp);
	IUnknown pTest;
        
        // Cannot expose interface of component not in the framework
	if(inCFGraph==false){
		return false;
	}

	// Check the interface isn't already exposed
	boolean inCFInts=false;

	for(int index=0; index<m_piElems; index++){
            if (((ExposedInterface) m_intseq.get(index)).IntfType.equalsIgnoreCase(rintf)){
                inCFInts=true;
                break;
            }
	}
	
        // If its already exposed - fail
	if(inCFInts==true){
		return false;
	}
        
        // Add the readers/writers lock to the exposed interface 
        IDelegator pIDel = m_PSR_IMetaInterception.m_pIntf.GetDelegator(pComp, rintf);
        
        if(pIDel==null){
            pComp = find_component(rintf);
        }
        else{
            String[] Names = new String[100];
            long noNames = pIDel.viewPreMethods(Names);
            boolean Intercepted=false;
            for(int i=0; i<noNames; i++){
                if(Names[i].equalsIgnoreCase("Pre0")){
                    // already added
                }
            }
            if(!Intercepted){
                pIDel.addPreMethod(Interceptors, "Pre0");
                pIDel.addPostMethod(Interceptors, "Post0");
            }
        }
        
        // Create the meta-data about the exposed interface
	ExposedInterface expIntf = new ExposedInterface(pComp, rintf);

        // Store meta data about the new exposed interface
	m_intseq.add(expIntf);				
	m_piElems++;
	
        m_PSR_IOpenCOM.m_pIntf.notifyEvent(EventTypes.OCMCF_EXPOSE_INTERFACE, 
                    new ComponentExposedInterface(getComponentName(this), rintf));

	return true;
        
    }

    /**
     * This method takes the receptacle from one of the framework's internal components
     * and then makes it one of its own receptacles.
     * @param rintf The interface type that will be exposed.
     * @param pComp The internal component hosting the the interface.
     * @param recpType The type of the receptacle.
     * @return A boolean describing if the receptacle was exposed.
     * @see OpenCOM.IUnknown
     */	
    public boolean expose_receptacle(String rintf, IUnknown pComp, String recpType) {
        
        // Check Component is in the CF
	boolean inCFGraph=false;

	for(int index=0; index<m_pcElems; index++){
		if (m_ppComps.get(index)==pComp){
			inCFGraph=true;
			break;
		}
	}
	
        // Cannot expose receptacle of component not in the framework
	if(inCFGraph==false){
		return false;
	}

	// Check the receptacle isn't already exposed
	boolean inCFInts=false;

	for(int index=0; index<m_prElems; index++){
		if (((ExposedReceptacle) m_recpseq.get(index)).Intf.equalsIgnoreCase(rintf)){
			inCFInts=true;
			break;
		}
	}
	
        // Fail if receptacle already exposed
	if(inCFInts==true){
		return false;
	}

        // Find the IConnect Interface
	IConnections pIConnect = (IConnections) pComp.QueryInterface("OpenCOM.IConnections");

        // Create and store the meta data about the new exposed receptacle
        ExposedReceptacle expRecp = new ExposedReceptacle(pComp, rintf, recpType, pIConnect);

	m_recpseq.add(expRecp);				
	m_prElems++;
	
        m_PSR_IOpenCOM.m_pIntf.notifyEvent(EventTypes.OCMCF_EXPOSE_RECEPTACLE, 
                    new ComponentExposedReceptacle(getComponentName(this), rintf));    
	
	return true;	
    }
    
    /**
     * This method removes all exposed interfaces.
     * @return A boolean describing if all the interfaces have been removed.
     */
     public boolean unexpose_all_interfaces() {
        
        for(int index=0;index<m_piElems;index++){
		// For each exposed interface remove its delegator
            ExposedInterface e = (ExposedInterface) m_intseq.get(index);
            if(unexpose_interface(e.IntfType, e.pCompID)==false)
                return false;
	}
	m_piElems=0;

	return true;
        
    }
     
    /**
     * This method removes all exposed receptacles.
     * @return A boolean describing if all the receptacle have been removed.
     */
    public boolean unexpose_all_receptacles() {
        
        for(int index=0;index<m_prElems;index++){
            ExposedReceptacle r = (ExposedReceptacle) m_recpseq.get(index);
            if(unexpose_receptacle(r.recpType, r.pCompID)==false)
                return false;
	}
	m_prElems=0;

	return true;
        
    }
    
    /**
     * This method removes the exposed interface from the outer component framework,
     * @param rintf The interface type that will be removed.
     * @param pComp The internal component hosting the the interface.
     * @return A boolean describing if the interface has been removed.
     * @see OpenCOM.IUnknown
     */
    public boolean unexpose_interface(String rintf, IUnknown pComp) {
        boolean foundIntf=false;

	// Find Interface in the internal components
	for(int index=0;index<m_piElems;index++){
            ExposedInterface e = (ExposedInterface) m_intseq.get(index);
            if (e.IntfType==rintf){
		foundIntf=true;
                // Remove from the meta-data
                m_intseq.remove(e);
                break;	
            }
		
	}

	if(foundIntf==false){
		return false;
	}
	// reduce list length by one
	m_piElems--;
        
        m_PSR_IOpenCOM.m_pIntf.notifyEvent(EventTypes.OCMCF_UNEXPOSE_INTERFACE, 
                    new ComponentUnExposeInterface(getComponentName(this), rintf)); 

	return true;
    }
    
    /**
     * This method removes the exposed receptacle from the outer component framework,
     * @param rintf The interface type that will be removed.
     * @param pComp The internal component hosting the the receptacle.
     * @return A boolean describing if the receptacle has been removed.
     * @see OpenCOM.IUnknown
     */
    public boolean unexpose_receptacle(String rintf, IUnknown pComp) {
        boolean foundIntf=false;

	// Find Interface
	for(int index=0;index<m_prElems;index++){
            ExposedReceptacle r = (ExposedReceptacle) m_recpseq.get(index);
            if (r.Intf==rintf){
		foundIntf=true;
		m_recpseq.remove(r);	
                break;
            }	
	}

	if(foundIntf==false)
		return false;

	// reduce list length by one
	m_prElems--;
        
        m_PSR_IOpenCOM.m_pIntf.notifyEvent(EventTypes.OCMCF_UNEXPOSE_RECEPTACLE, 
                    new ComponentUnExposeReceptacle(getComponentName(this), rintf)); 
 
	return true;
    }
    
    
    /**
     * This method produces a list of components that are connected to a 
     * particular component within the framework.
     * @param comp Instance of the component we wish to find what is connected to it.
     * @param ppConnections Vector to be filled with the list of components that are connected to this component.
     * @return An integer describing the number of components connected to this component.
     * @see OpenCOM.IUnknown
     */
    public int get_bound_components(IUnknown comp, Vector<ConnectedComponent> ppConnections) {
        
        boolean foundComp=false;
	Vector<OCM_RecpMetaInfo_t> pRecps = new Vector<OCM_RecpMetaInfo_t>();
	Vector<Class> pIntfs = new Vector<Class>();
 
	int count =0;
	int nextSpace =0;
	Vector<Long> pConnsIDS =  new Vector<Long>();
	OCM_ConnInfo_t buffer;
	int recpsConnCount=0;

	// check component is in the CF graph
	for(int index=0; index<m_pcElems; index++){
		if (comp==(IUnknown) m_ppComps.get(index)){
			foundComp=true;
			break;
		}
	}

	if(foundComp==false)
		return 0;	// The component to inspect is not in framework

	long connID = m_PSR_IOpenCOM.m_pIntf.connect(this, comp, "OpenCOM.IMetaInterface");
	// Enumerate Interfaces on component
	count = m_PSR_IMetaInterface.m_pIntf.enumIntfs(pIntfs);

	for(int i = 0; i < count; i++){
		// Get Components connected to each interface
            Object temp = pIntfs.get(i);
            String riid = temp.toString();
            StringTokenizer st = new StringTokenizer(riid); 
            String fname = st.nextToken();
            if (fname.equalsIgnoreCase("Interface"))
                riid = st.nextToken();
            pConnsIDS.clear();
            int intfsConnCount =0;
            if(!riid.equalsIgnoreCase("OpenCOM.IMetaInterface"))
                intfsConnCount = m_PSR_IMetaArchitecture.m_pIntf.enumConnsToIntf(comp, riid , pConnsIDS);
            if (intfsConnCount!=0){
                for(int index=0; index<intfsConnCount;index++){
                    long id = pConnsIDS.get(index).longValue();
                    buffer = m_PSR_IOpenCOM.m_pIntf.getConnectionInfo(id) ;

                    // Allocate memory for output list of connections
                    ConnectedComponent c = new ConnectedComponent(buffer.sourceComponent, id);
                    ppConnections.add(c);
                }
            }
	}


	// Enumerate Receptacles on component
	count = m_PSR_IMetaInterface.m_pIntf.enumRecps(pRecps);

	for(int i = 0; i < count; i++){	
		// Get Components connected to interfaces
                pConnsIDS.clear();
		recpsConnCount = m_PSR_IMetaArchitecture.m_pIntf.enumConnsFromRecp(comp, ((OCM_RecpMetaInfo_t) pRecps.get(i)).iid, pConnsIDS);

		if (recpsConnCount!=0){
			for(int index=0; index<recpsConnCount;index++){
                            long id = ((Long) pConnsIDS.get(index)).longValue();
                            buffer = m_PSR_IOpenCOM.m_pIntf.getConnectionInfo(id) ;

                            // Allocate memory for output list of connections
                            ConnectedComponent c = new ConnectedComponent(buffer.sinkComponent, id);
                            ppConnections.add(c);
			}
		}
	}
	m_PSR_IOpenCOM.m_pIntf.disconnect(connID);
	return ppConnections.size();
        
    }
    
    /**
     * This method fills the vector passed as a parameter with the list of interfaces
     * exposed by this framework. Its behaviour is similar to that provided by
     * enumIntfs of OpenCOM.
     * @param ppIntfs A vector to be filled with the list of interfaces.
     * @return The number of interfaces exposed by this framework.
     */
    public int get_exposed_interfaces(Vector<String> ppIntfs) {
	for(int index=0; index<m_piElems; index++){
            ppIntfs.add(((ExposedInterface) m_intseq.elementAt(index)).IntfType);
	}

	return m_piElems;
        
    }
    
    /**
     * This method fills the vector passed as a parameter with the list of interfaces
     * exposed by this framework. Its behaviour is similar to that provided by
     * enumIntfs of OpenCOM.
     * @param ppIntfs A vector to be filled with the list of interfaces.
     * @return The number of interfaces exposed by this framework.
     */
    public int get_detailed_exposed_interfaces(Vector<CFMetaInterface.ExposedInterface> ppIntfs) {
	for(int index=0; index<m_piElems; index++){
            ppIntfs.add(m_intseq.elementAt(index));
	}

	return m_piElems;
        
    }
    
    /**
     * This method fills the vector passed as a parameter with the list of receptacles
     * exposed by this framework. Its behaviour is similar to that provided by
     * enumRecps of OpenCOM.
     * @param ppComps A vector to be filled with the list of receptacles.
     * @return The number of interfaces exposed by this framework.
     */	
    public int get_exposed_receptacles(Vector<ExposedReceptacle> ppComps) {
 
	for(int index=0; index<m_prElems; index++){
            IUnknown pCompID  = m_recpseq.get(index).pCompID;
            String Intf = m_recpseq.get(index).Intf;
            String recpType = m_recpseq.get(index).recpType;
            IConnections pIConnect =  m_recpseq.get(index).pIConnect;
            ExposedReceptacle r = new ExposedReceptacle(pCompID, Intf, recpType, pIConnect);
            ppComps.add(r);
	}
	return m_prElems;
    }
    
    /**
     * This method returns all of the internal connections between components that are wholly within
     * the framework.
     * @param pConnIDS A vector to be filled with long values describing the unique id of each connection.
     * @return An integer describing the number of connections within the framework.
     */	
    public int get_internal_bindings(Vector<Long> pConnIDS) {
        ConnectedComponent pConns;
	int ConnsCount=0;
	int nextSpace=0;
	boolean alreadyInList=false;
	
        Vector<ConnectedComponent> components = new Vector<ConnectedComponent>();
	// for each component in the Component Framework list its connections
	for (int index=0; index<m_pcElems; index++){
                
		ConnsCount = get_bound_components((IUnknown) m_ppComps.get(index), components);
		// For each of returned connections extract the Connection ID
		for(int index2=0; index2<ConnsCount; index2++){
			ConnectedComponent Value = (ConnectedComponent) components.get(index2);

			// check that Value isn't already in list
			for(int index3=0; index3<pConnIDS.size(); index3++){
				if(Value.Connection==((Long) pConnIDS.get(index3)).longValue()){
					alreadyInList=true;
					break;
				}
			}
			
			if(alreadyInList==false){
				// Copy the ID into the list
				pConnIDS.add(new Long(Value.Connection));
			}
			alreadyInList=false;
		}
	}
	return pConnIDS.size();
        
    }
    
    /**
     * This method fills the vector passed as a parameter with the references of all
     * the components that reside locally within this framework.
     * @param ppComps A vector to be filled with the references of inner components.
     * @return The number of components within the framework.
     */	
    public int get_internal_components(Vector<IUnknown> ppComps) {
       
	for(int index=0; index<m_pcElems; index++){
		ppComps.add(m_ppComps.elementAt(index));
	}

	return m_pcElems;
    }
    
    /**
    * This method returns the component reference for a given unique component name in the framework.
    * @param CompName The name of the component instance.
    * @return A reference to the component instance.
    */
    public IUnknown getComponentPIUnknown(String CompName) {
        for(int i = 0; i < m_ppComps.size(); i++) {
            String cName = m_PSR_IOpenCOM.m_pIntf.getComponentName(m_ppComps.elementAt(i));
            if(cName.equalsIgnoreCase(CompName)){
                return (IUnknown) m_ppComps.elementAt(i);
            }
        }
        return null;
    }
    
    
    /**
    * This method returns the unique component name of a given component reference.
    * @param Comp The component unique reference.
    * @return The components's unique string name.
    */
    public String getComponentName(IUnknown Comp) {
        return m_PSR_IOpenCOM.m_pIntf.getComponentName(Comp);    
    }
    
    /**
     * All reconfigurations must be performed as part of a transaction. Therefore,
     * the reconfigure agent must first call this method before subsequent write 
     * operations.
     * @return A boolean describing if the transaction can continue. 
     */ 	
    public boolean init_arch_transaction() {
        // First get the CF lock for write access
        try{
            CFlock.acquire();
        }
        catch(java.lang.InterruptedException e){
            // Interupted before lock received
            return false;
        }
        
	// Store the current settings
	m_pcElemsback= m_pcElems;
	m_piElemsback= m_piElems;
	m_prElemsback= m_prElems;
        
        //Empty the vectors
        m_ppCompsback.removeAllElements();
        m_intseqback.removeAllElements();
        m_recpseqback.removeAllElements();
        m_pConnInfoBack.removeAllElements();

        // Store the current data in the backup structures
	for(int index=0;index<m_pcElems; index++)
		m_ppCompsback.add(m_ppComps.get(index));
	for(int index=0;index<m_piElems; index++)
		m_intseqback.add(m_intseq.get(index));
	for(int index=0;index<m_prElems; index++)
		m_recpseqback.add(m_recpseq.get(index));

	// Get the backup connection info
	Vector<Long> Connections = new Vector<Long>();
	int count = get_internal_bindings(Connections);

	for(int i=0; i<count; i++){
            OCM_ConnInfo_t t = m_PSR_IOpenCOM.m_pIntf.getConnectionInfo(Connections.elementAt(i).longValue());
            m_pConnInfoBack.add(t);
	}
	m_pcConns = count;

        m_PSR_IOpenCOM.m_pIntf.notifyEvent(EventTypes.OCMCF_INIT, new FrameworkInitEvent(getComponentName(this)));

	return true;
        
    }
     
    /**
     * This method must be called by the reconfiguration agent at the end of the reconfiguration
     * transaction. It forces a check on the new configuration, which is commited or not based upon the result.
     * @return The boolean describes of the new configuration was commited. A false means that the
     * last good configuration was rolled back to.
     */ 	
    public boolean commit_arch_transaction() {
        // There is no validation plug-in. So we allow anything - change to false and rollback
        // if you want stronger architectures.
        if((m_PSR_IAccept.m_pIntf==null)||(((IAccept) m_PSR_IAccept.m_pIntf).isValid(m_ppComps,m_intseq, m_pcElems , m_piElems))){
            CFlock.release();
            Vector<Long> Connections = new Vector<Long>();
            int count = get_internal_bindings(Connections);
            Vector<OCM_ConnInfo_t> listConns = new Vector<OCM_ConnInfo_t>();
            for(int i=0; i<count; i++){
                OCM_ConnInfo_t t = m_PSR_IOpenCOM.m_pIntf.getConnectionInfo(Connections.elementAt(i).longValue());
                listConns.add(t);
            }
            m_PSR_IOpenCOM.m_pIntf.notifyEvent(EventTypes.OCMCF_COMMIT, new FrameworkCommitEvent(getComponentName(this), m_ppComps));
            return true; 
        }
        else{
            // We have created an invalid configuration - force a rollback
            rollback_arch_transaction();
            CFlock.release();
            return false;
        }
        
    }
    
    /**
     * Rolls the configuration back to its previous state - ideally should not be called
     * directly; maybe if faults are being detected is a supposedly valid architecture
     * you may wish to try returning to a stable version. 
     * @return A boolean describing if the roll back was a success.
     */
    public boolean rollback_arch_transaction() {
        // Check there is a backup configuration to roll back to 
	if (m_pcElemsback<0)
		return false;
	else{
            Vector<Long> Connections = new Vector<Long>();
            int count = get_internal_bindings(Connections);
            for(int i=0; i<count; i++){
                break_local_bind(Connections.elementAt(i).longValue());
            }
            int y = m_pcElems;
            for(int i=0; i<y; i++){
                delete_component((IUnknown) m_ppComps.firstElement());
            }
            m_ppComps.removeAllElements();
            unexpose_all_interfaces();
            unexpose_all_receptacles();
            
            m_intseq.removeAllElements();
            m_recpseq.removeAllElements();
            
            m_pcElems= m_pcElemsback;
            m_piElems= m_piElemsback;
            m_prElems= m_prElemsback;

            for(int index=0;index<m_pcElems; index++)
		m_ppComps.add(m_ppCompsback.get(index));
            for(int index=0;index<m_piElems; index++)
                m_intseq.add(m_intseqback.get(index));
            for(int index=0;index<m_prElems; index++)
                m_recpseq.add(m_recpseqback.get(index));
		
            long newConnID;
		//reconnect components
            for(int i=0; i<m_pcConns; i++){
                OCM_ConnInfo_t connInfo = m_pConnInfoBack.get(i);
		newConnID = local_bind(connInfo.sourceComponent, connInfo.sinkComponent, connInfo.interfaceType);
            }
		return true;
	}
        
    }
    
   /**
    * Each component framework implemnts a lock to prevent reconfiguration
    * during functional operation. This method attempts to get read or write
    * access to the lock based upon the input. The locking mechanism is readers,
    * writers with priority for readers.
    * @param index An integer describing acces type: 0 for read, 1 for write.
    * @return A boolean describing if the lock has processed this request or not.
    **/	
    public boolean access_CF_graph_lock(int index) {
        // Write Access
        if (index == 0){
            try{
               CFlock.acquire();  
            }
            catch(java.lang.InterruptedException e){
                return false;
            }
	}
        // Read Access
	else if(index==1){
            try{
               ReadersMutex.acquire();
            }
            catch(java.lang.InterruptedException e){
                return false;
            }
	}
	else
            return false;
	return true;
    }
    
    /**
    * Releases the lock, previously acquired.
    * @param index An integer describing acces type: 0 for read, 1 for write.
    * @return A boolean describing if the lock has processed this request or not.
    **/	
    public boolean release_CF_graph_lock(int index) {
        if (index == 0){
            try{
               CFlock.release();  
            }
            catch(Exception e){
                return false;
            }
	}
        // Read Access
	else if(index==1){
            try{
               ReadersMutex.release();
            }
            catch(Exception e){
                return false;
            }
	}
	else
            return false;

	return true;
    }
    
    /**
    * Update the CF's locks readers count. Do not use - only used by runtime.
    * @param Value increment amount.
    * @return An integer describing the new reader count.
    **/	
    public int update_readers_count(int Value) {
        ReadersCount=ReadersCount+Value;
	return ReadersCount;
    }
    
    // IMetaInterface Interface
    /**
    * Returns a Vector of meta-information. Each elements of the Vector is a String describing
    * that interface's type.
    * @param ppIntfs a Vector to be filled with interface meta-information.
    * @return an Integer describing the number of interfaces on the component.
    **/
    public int enumIntfs(Vector<Class> ppIntfs) {
        
	// Get CFs custom receptacles
        int length = Meta.enumIntfs(this, ppIntfs);

	// Get exposed receptacles
        Vector<String> ppExpIntfs = new Vector<String>();
        int length2 = get_exposed_interfaces(ppExpIntfs);
        
        for(int i=0; i<length2; i++){
            try{
                Class IntfTemp = Class.forName(ppExpIntfs.get(i));
                ppIntfs.add(IntfTemp);
            }
            catch(Exception e){
                continue;
            }
        }
		
        return length+length2;
    }

    
    /**
    * Returns a Vector of meta-information. Each elements of the Vector is an object of
    * type OCM_RecpMetaInfo_t, which describes the attributes of indiviudal receptacles
    * including: type (single or multiple) & interface type.
    * @param ppRecpMetaInfo a Vector to be filled with receptacle meta-information.
    * @return an Integer describing the number of receptacles on the component.
    **/
    public int enumRecps(Vector<OCM_RecpMetaInfo_t> ppRecpMetaInfo) {
        // Get CFs custom receptacles
        int length = Meta.enumRecps(this, ppRecpMetaInfo);

	// Get exposed receptacles
        Vector<CFMetaInterface.ExposedReceptacle>  pExpRecps= new Vector<CFMetaInterface.ExposedReceptacle>();
        int length2 = get_exposed_receptacles(pExpRecps);
        for(int i=0; i<length2; i++){
            CFMetaInterface.ExposedReceptacle pExpRec = pExpRecps.get(i);
            OCM_RecpMetaInfo_t pTemp = new OCM_RecpMetaInfo_t(pExpRec.Intf, pExpRec.recpType);
            ppRecpMetaInfo.add(pTemp);
        }
	return length+length2; 
    }
    
    /**
     * Meta-data can be attached to each interface/receptacle of a component. This method adds a name
     * value pair to a given interface or receptacle instance.
     * @param iid the type of the interface or receptacle.
     * @param Kind a string saying whether to attach to an interface or a receptacle.
     * @param Name A String describing the attribute name.
     * @param Type A String describing the attribute type.
     * @param Value An object representing the attribute value.
     * @return A boolean indicating the success of the operation. 
     **/
    public boolean SetAttributeValue(String iid, String Kind, String Name, String Type, Object Value) {
        if(Kind.equalsIgnoreCase("Interface")){
            for(int i=0; i<m_intseq.size(); i++){
                ExposedInterface tInt = m_intseq.get(i);
                if(tInt.IntfType.equalsIgnoreCase(iid)){
                    IMetaInterface pMeta = (IMetaInterface) tInt.pCompID.QueryInterface("OpenCOM.IMetaInterface");
                    return pMeta.SetAttributeValue(iid, Kind, Name, Type, Value);
                }
            }
        }
        if(Kind.equalsIgnoreCase("Receptacle")){
            for(int i=0; i<m_recpseq.size(); i++){
                ExposedReceptacle tRecp = m_recpseq.get(i);
                if(tRecp.Intf.equalsIgnoreCase(iid)){
                    IMetaInterface pMeta = (IMetaInterface) tRecp.pCompID.QueryInterface("OpenCOM.IMetaInterface");
                    return pMeta.SetAttributeValue(iid, Kind, Name, Type, Value);
                }
            }
        }
        
        return Meta.SetAttributeValue(iid, Kind, Name, Type, Value);
    }    
    
    /**
     * Meta-data can be retrieved from each interface/receptacle of a component. This method 
     * retrieves the value of a name attribute on a receptacle or interface.
     * @param iid the type of the interface or receptacle.
     * @param Kind a string saying whether to attach to an interface or a receptacle.
     * @param Name A String describing the attribute name.
     * @return A TypedAttribute object containing the value and type of the meta-data attribute. 
     **/
    public TypedAttribute GetAttributeValue(String iid, String Kind, String Name) {
        if(Kind.equalsIgnoreCase("Interface")){
            for(int i=0; i<m_intseq.size(); i++){
                ExposedInterface tInt = m_intseq.get(i);
                if(tInt.IntfType.equalsIgnoreCase(iid)){
                    IMetaInterface pMeta = (IMetaInterface) tInt.pCompID.QueryInterface("OpenCOM.IMetaInterface");
                    return pMeta.GetAttributeValue(iid, Kind, Name);
                }
            }
        }
        if(Kind.equalsIgnoreCase("Receptacle")){
            for(int i=0; i<m_recpseq.size(); i++){
                ExposedReceptacle tRecp = m_recpseq.get(i);
                if(tRecp.Intf.equalsIgnoreCase(iid)){
                    IMetaInterface pMeta = (IMetaInterface) tRecp.pCompID.QueryInterface("OpenCOM.IMetaInterface");
                    return pMeta.GetAttributeValue(iid, Kind, Name);
                }
            }
        }
        
        return Meta.GetAttributeValue(iid, Kind, Name);
    }    
    
    /**
     * This method retrieves all the meta-data stored on the interface or receptacle.
     * @param iid the type of the interface or receptacle.
     * @param Kind a string saying whether to attach to an interface or a receptacle.
     * @return A hashatable containing all of the attribute-value pairs for the receptacle or interface.
     **/
    public Hashtable<String, TypedAttribute> GetAllValues(String Kind, String iid){
        if(Kind.equalsIgnoreCase("Interface")){
            for(int i=0; i<m_intseq.size(); i++){
                ExposedInterface tInt = m_intseq.get(i);
                if(tInt.IntfType.equalsIgnoreCase(iid)){
                    IMetaInterface pMeta = (IMetaInterface) tInt.pCompID.QueryInterface("OpenCOM.IMetaInterface");
                    return pMeta.GetAllValues(Kind, iid);
                }

            }
        }
        if(Kind.equalsIgnoreCase("Receptacle")){
            for(int i=0; i<m_recpseq.size(); i++){
                ExposedReceptacle tRecp = m_recpseq.get(i);
                if(tRecp.Intf.equalsIgnoreCase(iid)){
                    IMetaInterface pMeta = (IMetaInterface) tRecp.pCompID.QueryInterface("OpenCOM.IMetaInterface");
                    return pMeta.GetAllValues(Kind, iid);
                }
            }
        }
        return Meta.GetAllValues(Kind, iid);
    }
    
    // IConnections Interface
    public boolean connect(IUnknown pSinkIntf, String riid, long provConnID) {
        if(riid.toString().equalsIgnoreCase("OpenCOM.IAccept")){
            return m_PSR_IAccept.connectToRecp(pSinkIntf, riid, provConnID);
	}
        if(riid.toString().equalsIgnoreCase("OpenCOM.IMetaInterface")){
            return m_PSR_IMetaInterface.connectToRecp(pSinkIntf, riid, provConnID);
	}
        if(riid.toString().equalsIgnoreCase("OpenCOM.IMetaInterception")){
            return m_PSR_IMetaInterception.connectToRecp(pSinkIntf, riid, provConnID);
	}    
        if(riid.toString().equalsIgnoreCase("OpenCOM.IMetaArchitecture")){
            return m_PSR_IMetaArchitecture.connectToRecp(pSinkIntf, riid, provConnID);
	}
        else{
            // Need to check that the exposed interface meets the requirements
            // ppVals is the set R of rules governing the connection to an interaction type (PIP)
            // Enumerate the exposed Interfaces of this receptacle
            Vector<ExposedReceptacle> ppIntfs = new Vector<ExposedReceptacle>();
            int noIntfs = get_exposed_receptacles(ppIntfs);
            for(int i=0 ; i< noIntfs; i++){
                ExposedReceptacle ppIntf = ppIntfs.get(i);
                if(ppIntf.Intf.equalsIgnoreCase(riid)){
                   long id= m_PSR_IOpenCOM.m_pIntf.connect(ppIntf.pCompID, pSinkIntf, riid); 
                   if(id>0)
                       return true;
                }
            }
        }    

	return false;
    }
    
    public boolean disconnect(String riid, long connID) {  
	if(riid.toString().equalsIgnoreCase("OpenCOM.IAccept")){
            return m_PSR_IAccept.disconnectFromRecp(connID);
	}
        if(riid.toString().equalsIgnoreCase("OpenCOM.IMetaInterface")){
            return m_PSR_IMetaInterface.disconnectFromRecp(connID);
	}
        if(riid.toString().equalsIgnoreCase("OpenCOM.IMetaInterception")){
            return m_PSR_IMetaInterception.disconnectFromRecp(connID);
	}
        if(riid.toString().equalsIgnoreCase("OpenCOM.IMetaArchitecture")){
            return m_PSR_IMetaArchitecture.disconnectFromRecp(connID);
	}
        else{
            // Need to check that the exposed interface meets the requirements
            // ppVals is the set R of rules governing the connection to an interaction type (PIP)
            // Enumerate the exposed Interfaces of this receptacle
            Vector<ExposedReceptacle> ppIntfs = new Vector<ExposedReceptacle>();
            int noIntfs = get_exposed_receptacles(ppIntfs);
            for(int i=0 ; i< noIntfs; i++){
                ExposedReceptacle ppIntf = ppIntfs.get(i);
                if(ppIntf.Intf.equalsIgnoreCase(riid)){
                    return ppIntf.pIConnect.disconnect(riid, connID);
                }
            }
        }    
	return false;
    }
    
}
