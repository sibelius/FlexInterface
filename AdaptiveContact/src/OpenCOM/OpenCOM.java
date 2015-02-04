/*
 * OpenCOM.java
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
import java.lang.reflect.Constructor;
import java.util.Vector;

import OpenCOM.Events.ComponentConnectEvent;
import OpenCOM.Events.ComponentCreateEvent;
import OpenCOM.Events.ComponentDeleteEvent;
import OpenCOM.Events.ComponentDisconnectEvent;
import OpenCOM.Events.EventTypes;
/**
 * OpenCOM is the implementation of the component runtime kernel. It should be instantiated once only - n.b.
 * future versions may implement this class using the singleton pattern. OpenCOM provides a set of runtime
 * services that support the creation, deletetion and binding of software components. Notably, it maintains
 * information about the running component architectures in a system wide graph data structure. Reflective operations
 * can then be performed on this meta-representation i.e. inspection and dynamic reconfiguration. The aim is
 * to support the capabilities of the three meta-models proposed by the Open ORB philosopy i.e. interface,
 * architecture and interception meta-models. Not that this is a prototype version of OpenCOM version 1 as
 * described in Clarke,01. There is a C++ implementation offering identical operations.
 *
 * @author  Paul Grace
 * @version 1.3.5
 * @see OpenCOM.IOpenCOM 
 * @see OpenCOM.IMetaInterface 
 * @see OpenCOM.IMetaInterception 
 * @see OpenCOM.IMetaArchitecture
 */

public class OpenCOM implements IOpenCOM, IDebug, IMetaArchitecture, IUnknown, IMetaInterception, IConnections{

    // Runtime state
    private Vector<OCM_GraphNode_t> mGraph;                 //!< System Graph
    private long mcGraphNodes;                              //!< Number of components in system
    private long mcConnID;                                  //!< Unique connection Identifer counter
    private MetaInterface Meta;
    
    /**
     * To allow causal connections to be maintained from this runtime all system events e.g.
     * create, delete, connect, etc., are upcalled through this event interface. Higher components
     * can then maintain/react to this information.
     */
    public OCM_MultiReceptacle<IKernelDeliver> m_pSR_IDeliver;

    /** Constructor that creates a new instance of the OpenCOM runtime kernel*/
    public OpenCOM() {
        mGraph= new Vector<OCM_GraphNode_t>();
        mcGraphNodes = 0;
        mcConnID = 1;
        m_pSR_IDeliver = new OCM_MultiReceptacle<IKernelDeliver>(IKernelDeliver.class);
        Meta = new MetaInterface((IOpenCOM) this, this);
    }

    // Implementation for the IDebug interface of OpenCOM runtime
    //////////////////////////////////////////////////////////////////////

    /**
    * The dump method is useful for debugging purposes as it allows the entire state of 
    * the system graph to be outputted to the console. Hence, you can check if component
    * configurations are as required.
    */
    public void dump() {
        for(int i = 0; i < mGraph.size(); i++) {
            System.out.println("Index = "+ i);
            if(mGraph.elementAt(i).pIUnknown == null){
                System.out.println("EMPTY SLOT");
                continue;
            }

            if(mGraph.elementAt(i).name!=null)
               System.out.println("Name ="+((OCM_GraphNode_t) mGraph.elementAt(i)).name);
            else
                System.out.println("Name = NULL ");

            
            Vector<OCM_GraphRecpInfo_t> vec = mGraph.elementAt(i).pGRecpInfo;
            
            System.out.println("  INTFS CONN'D TO:   ");
            for(int j=0; j<vec.size();j++){
               System.out.println(vec.elementAt(j).sinkIndex + " " + vec.elementAt(j).iid);
            }
            System.out.println();
            
            Vector<OCM_GraphIntfInfo_t> vec2 =  mGraph.elementAt(i).pGIntfInfo;
            System.out.println("  RECPS CONN'D FROM: ");
            for(int j=0; j<vec2.size();j++){
                    System.out.println(vec2.elementAt(j).sourceIndex + " "+ vec2.elementAt(j).iidName);
            }
            System.out.println();
        }  
    }
    /**
    * The visualise method gives you a graphical display of the OpenCOM runtime. Unlike
    * the remote visualisation of the runtime - this is a snapshot operation performed
    * locally i.e. the visualisation is static for that point in time and will not 
    * be updated. In addition, it will be displayed on the screen of the local device
    * not the remote server's screen. 
    */
    public void visualise(){
    	//Android haven't awt or swing
/*
        Vector<IUnknown> pComps  = new Vector<IUnknown>();
        int noComps = enumComponents(pComps);
  
        Vector<IUnknown> CompsConn = new Vector<IUnknown>();
        
        VisualGraph g = new VisualGraph((IOpenCOM) this, "OpenCOM Runtime");
        
        // Add compoenent to the graph
        for(int i=0;i<noComps;i++){
            IUnknown pComp = pComps.get(i);
            String name = getComponentName(pComp);
            
            // If its not primitive -- ignore
            boolean contained = isContained(name);
            if(!contained){
                CompsConn.add(pComp);
                // Detect if its a framework
                ICFMetaInterface iFrameworkMeta = (ICFMetaInterface) pComp.QueryInterface("OpenCOM.ICFMetaInterface");
                if(iFrameworkMeta==null){
                    g.panel.addComponent(name, pComp);
                }
                //else add framework
                else{
                    g.panel.addFramework(name, pComp);
                }
            }
        }
  
        // Connect components
        for(int i=0;i<CompsConn.size();i++){
            IUnknown pComp = CompsConn.get(i);

            Vector<OCM_RecpMetaInfo_t> ppRecps = new Vector<OCM_RecpMetaInfo_t>();
            IMetaInterface pMeta =  (IMetaInterface) pComp.QueryInterface("OpenCOM.IMetaInterface");
            int noRecps = pMeta.enumRecps(ppRecps);
            for (int j=0; j<noRecps; j++){
                OCM_RecpMetaInfo_t temp = ppRecps.elementAt(j);
                Vector<Long> Recplist = new Vector<Long>();
                int noConns = enumConnsFromRecp(pComp, temp.iid, Recplist);
                for(int k=0;k<noConns;k++){
                    OCM_ConnInfo_t TempConnInfo = getConnectionInfo(Recplist.get(k).longValue());
                    boolean f = false;
                    for(int a=0; a<CompsConn.size();a++){
                        IUnknown tmpComp = CompsConn.get(a);
                        String tmpName = getComponentName(tmpComp);
                        if(tmpName.equalsIgnoreCase(TempConnInfo.sinkComponentName)){
                            g.panel.addLink(TempConnInfo.sourceComponentName, tmpName, TempConnInfo.interfaceType);
                            break;
                        }
                        else{
                            // find if its a contained
                            ICFMetaInterface pMCF = (ICFMetaInterface) tmpComp.QueryInterface("OpenCOM.ICFMetaInterface");
                            if(pMCF!=null){
                                Vector<String> intf = new Vector<String>();
                                int noIntfs = pMCF.get_exposed_interfaces(intf);
                                for(int b = 0; b<noIntfs;b++){
                                    String iid = intf.get(b);
                                    if(iid.equalsIgnoreCase(TempConnInfo.interfaceType)){
                                        g.panel.addLink(TempConnInfo.sourceComponentName, tmpName, TempConnInfo.interfaceType);
                                    }
                                }
                            }
                        }
                    }
                    
                }
            }     
        }
        g.setVisible(true);
        */
    }

    
    // Implementation for the IMetaArchitecture interface of the OpenCOM runtime
    //////////////////////////////////////////////////////////////////////
    /**
    * This method introspects connections on existing components. It tells you how many components
    * are connected to this particular receptacle. For single receptacles this will be 0 or 1; however,
    * multiple receptacles can have 0 to N connections.
    * @param pIUnknown The component hosting the receptacle we wish to introspect.
    * @param riid The interface type of the receptacle.
    * @param ppConnsFromRecp A vector to be filled with unique connection ids of all the connections to this receptacle.
    * @return An integer describing the number of connections to this receptacle.
    */
    public int enumConnsFromRecp(IUnknown pIUnknown, String riid, Vector<Long> ppConnsFromRecp){
        // Traverse the system graph, we are looking for pIUnknown component first
        for (int i = 0; i < mGraph.size(); i++){
            if (mGraph.elementAt(i).pIUnknown.hashCode() == pIUnknown.hashCode()){
                // Found the component, now we will traverse its list of connections on its riid receptacle
                Vector<OCM_GraphRecpInfo_t> RecpIterator = mGraph.elementAt(i).pGRecpInfo;
                OCM_GraphRecpInfo_t tempRecpInfo = null;
                for (int index = 0; index < RecpIterator.size(); index++){
                    tempRecpInfo = RecpIterator.elementAt(index);
                    if (tempRecpInfo.iid.equalsIgnoreCase(riid)){
                        // For matching receptacle-interface type we add the connection ID to the output parameter
                        ppConnsFromRecp.add(new Long(tempRecpInfo.connID));
                    }
                }
            }
        }
        return ppConnsFromRecp.size();
    }

    /**
    * This method introspects connections on existing components. It tells you how many components
    * are connected to this particular INTERFACE. 
    * @param pIUnknown The component hosting the interface we wish to introspect.
    * @param riid The interface type of the interface.
    * @param ppConnsToIntf A vector to be filled with unique connection ids of all the connections to this interface.
    * @return An integer describing the number of connections to this interface.
    */
    public int enumConnsToIntf(IUnknown pIUnknown, String riid, Vector<Long> ppConnsToIntf){
        // Traverse the system graph, we are looking for pIUnknown component first
        for (int i = 0; i < mGraph.size(); i++){
            if (mGraph.elementAt(i).pIUnknown.hashCode() == pIUnknown.hashCode()){
                // Found the component, now we will traverse its list of interfaces
                Vector<OCM_GraphIntfInfo_t> InterfaceIterator = mGraph.elementAt(i).pGIntfInfo;
                OCM_GraphIntfInfo_t tempIntfInfo = null;
                for (int index = 0; index < InterfaceIterator.size(); index++){
                    tempIntfInfo = InterfaceIterator.elementAt(index);
                    if (tempIntfInfo.iidName.equalsIgnoreCase(riid)){
                        // Found the interface type, add the connection ID to the output parameter
                        ppConnsToIntf.add(new Long(tempIntfInfo.connID));
                    }
                }
            }
        }
        return ppConnsToIntf.size();
    }

    //! Implementation for the IUnknown interface 
    //////////////////////////////////////////////////////////////////////

    /**
    * This method returns a reference point to the runtime, based upon the
    * requested interface type. If the interface isn't hosted then the
    * null indicates that those operations are not available on the runtime.
    * @param InterfaceName A string describing the interface requested.
    * @return A reference to the runtime if the interface is available, otherwise null is returned.
    */
    public Object QueryInterface(String InterfaceName) {
        Class componentType = this.getClass();
        Vector<String> query = new Vector<String>();
        Meta.ReadInterfaceNames(componentType, query);
        String interfaceName = null;
        for (int i = 0; i < query.size(); i++) {
            interfaceName = (String) query.get(i).toString();
            if(interfaceName.equalsIgnoreCase(InterfaceName)){
                    return this; 
            }
        }
        return null;
    }


    //! Implementation for the IOpenCOM interface 
    //////////////////////////////////////////////////////////////////////

    /**
    * Connects the source component hosting the receptacle to the
    * sink component hosting the interface on the given interface type.
    * @param pIUnkSource The source component of the connection (hosts receptacle).
    * @param pIUnkSink The sink component of the connection (provides interface).
    * @param iid The interface type to make the connection on.
    * @return A long representing the unique ID of this connection.
    */
    public synchronized long connect(IUnknown pIUnkSource, IUnknown pIUnkSink, String iid) {

        // Get pIConnections interface from the source component
        IConnections pIConnections = (IConnections) pIUnkSource.QueryInterface("OpenCOM.IConnections");

        // Register the information about the new connection to the system graph
        boolean success = registerConnection(pIUnkSource, pIUnkSink, iid, mcConnID);
        if(!success){
                return -1;
        }
        // Make the connection between the two components
        success = pIConnections.connect(pIUnkSink, iid, mcConnID);

        // If the connection fails we must remove the meta-data
        if(!success) {
                deRegisterConnection(mcConnID);
                return -1;
        }	

        long pConnID = mcConnID;

        // Upload events notifying new kernel connection
        ComponentConnectEvent cSend = new ComponentConnectEvent(getComponentName(pIUnkSource),getComponentName(pIUnkSink), iid, pConnID);   
        for(int i=0; i<m_pSR_IDeliver.interfaceList.size();i++){
            m_pSR_IDeliver.interfaceList.get(i).KernelEventDeliver(EventTypes.OCM_CONNECT, cSend);
        }
        
        //Increment unique ConnID as connection succeeded
        mcConnID++;
        return pConnID;		// Return the ID of the created connection
    }

    /**
    * Private method of the runtime, which is used to add meta-data information abot connections
    * to the run-time graph. This is only ever invoked by the connect method of OpenCOM.
    */
    private boolean registerConnection(IUnknown pIUnkSource, IUnknown
       pIUnkSink, String riid, long connID){

       // Add receptacle info to front of pGRecpInfo on source
       OCM_GraphRecpInfo_t pGRecpInfo = new OCM_GraphRecpInfo_t(connID, pIUnkSink, riid);

       // Add interface info to front of pGIntfInfo on sink
       OCM_GraphIntfInfo_t pGIntfInfo = new OCM_GraphIntfInfo_t(connID, pIUnkSource, riid);

       for (int i = 0; i < mGraph.size(); i++){
           // Copy IntfInfo onto list
           if (mGraph.elementAt(i).pIUnknown.hashCode() == pIUnkSink.hashCode()){
               OCM_GraphNode_t tempP =  mGraph.elementAt(i);
               tempP.pGIntfInfo.add(pGIntfInfo);
               mGraph.setElementAt(tempP, i);
               break;
           }
       }
       for (int i = 0; i < mGraph.size(); i++){
           // Copy RecpInfo onto list
           if (mGraph.elementAt(i).pIUnknown.hashCode() == pIUnkSource.hashCode()){
               OCM_GraphNode_t tempP = mGraph.elementAt(i);

               for(int k=0; k<tempP.pGRecpInfo.size();k++){
                   OCM_GraphRecpInfo_t tmpInfo = tempP.pGRecpInfo.get(k);
                   if(tmpInfo.iid.equalsIgnoreCase(riid)){
                       if(tmpInfo.sinkIndex.hashCode()==pIUnkSink.hashCode())
                           return false;
                   }
               }

               tempP.pGRecpInfo.add(pGRecpInfo);
               mGraph.setElementAt(tempP, i);
               break;
           }
       }

       return true;
   } 


    /**
     * Determines whether the interface passed as a parameter is a valid OpenCOM interface i.e.
     * it inherits IUnknown and returns a boolean indicator.
     * @param Interface The class of the java interface to be checked.
     * @return true if this is an OpenCOM interface else return false.
     */
    private boolean OpenCOMInterface(Class Interface){
        boolean IUnknown = false;
        // base of recursion
        if(Interface==IUnknown.class)
            return true;
        else{
            Class[] Intfs = Interface.getInterfaces();
            for(int i=0; i<Intfs.length; i++){
                if(OpenCOMInterface(Intfs[i]))
                    return true;
            }
            return false;
        }
    }

    /** 
    * Creates a component in the OpenCOM runtime. A component is created, delegators are attached to
    * support interface interception, and finally meta-data about the component is initialised in the
    * system graph.
    * @param componentType This string describes the Java class type of the component to create.
    * @param componentName The unique user-defined name for the new component.
    * @return A reference to a newly created instance of the component.
    */
    public synchronized Object createInstance(String componentType, String componentName) {
        Object pIUnknown = null;                                        // Original version of component before delegators added
        Class componentClass = null;                                    // Java Class type corresponding to componentType string
        Vector<String> InterfaceList = new Vector<String>();    	// List of interface types provided by the new component
        boolean ValidComponent = false;                                 // Indicator if the componentType is a valid OpenCOM component
        Object delegatedComponent = null;                               // Version of component after delegators are added

        //Has an optional name been supplied ?
        if(componentName != null) {	
            //Make sure name is unique
            for(int index = 0; index < mGraph.size(); index++) {
                OCM_GraphNode_t VectorElement = (OCM_GraphNode_t) mGraph.elementAt(index);
                if(VectorElement.name.equalsIgnoreCase(componentName)){
                    return VectorElement.pIUnknown;
                }
            }
        }
        //Now Create component
        try{
            componentClass = Class.forName(componentType);
            Class[] intArgsClass = new Class[] {IUnknown.class};
            Object[] intArgs = new Object[] {(IUnknown) this};
            Constructor intArgsConstructor;
            // Use java reflection the instantiate an instance of this component type
            
            intArgsConstructor = componentClass.getConstructor(intArgsClass);
            pIUnknown = (IUnknown) intArgsConstructor.newInstance(intArgs);
            
//            Class[] theInterfaces = componentClass.getInterfaces();
//            for (int i = 0; i < theInterfaces.length; i++) {
//                if(!OpenCOMInterface(theInterfaces[i])){
//                    System.err.println("Invalid OpenCOM interfaces on Component");
//                    return null;
//                }
//            }
            // Find the interfaces of the component type, we need to make sure this is a valid 
            // OpenCOM component i.e. it implements the IUnknown interface
            AddInterfaces(componentClass,InterfaceList);
            String traverseString = null;
            for (int i = 0; i < InterfaceList.size(); i++) {
                traverseString = (String)InterfaceList.get(i);
                if (traverseString.equalsIgnoreCase("OpenCOM.IUnknown")){
                    ValidComponent=true;
                    break;
                }
            }
        }
        catch(Exception e){
            System.err.println("Error constructing OpenCOM component "+e);
            e.printStackTrace();
        }

        // If it's a valid component then we can add delegators and place it in the graph 
       if(ValidComponent){
            //Record it at next free position on graph
            OCM_GraphNode_t newVectorElement= new OCM_GraphNode_t(componentName, pIUnknown, componentClass); 
            mGraph.add(newVectorElement);	
            int index = mGraph.indexOf(newVectorElement);
            for (int i = 0; i < InterfaceList.size(); i++) {
                String intfName = (String) InterfaceList.get(i);
                if((intfName.equalsIgnoreCase("OpenCOM.IConnections"))||
                        (intfName.equalsIgnoreCase("OpenCOM.IMetaInterface"))||
                        (intfName.equalsIgnoreCase("OpenCOM.ILifeCycle"))){
                    // do not attach delegators to standard OpenCOM interfaces
                }
                else{
                    // We now need to attach delegators to each of the "non-component" interfaces
                    // Create a new delegator to be attached
                    Delegator del = new Delegator(pIUnknown, (IMetaInterception) this);
                    IDelegator pDel = (IDelegator) del;
                    Object tempDelegatedComponent = del.newInstance(pIUnknown);
                    del.HigherObject = tempDelegatedComponent;
                    if (intfName.equalsIgnoreCase("OpenCOM.IUnknown")){
                        // This is the special case - we need to replace the Component reference
                        // in the graph with the delegated component reference
                        delegatedComponent = tempDelegatedComponent;
                        mGraph.elementAt(index).pIUnknown = delegatedComponent;
                        pDel.SetInterception(true);
                    }

                    //Create OCM_DelegatorInfo structure for new list entry
                    OCM_DelegatorInfo info = new OCM_DelegatorInfo(pDel, intfName);

                    //Add it to the list
                    mGraph.elementAt(index).pGDelInfo.add(info);
                }
            }

            // Upload events notifying new kernel create
            ComponentCreateEvent cSend = new ComponentCreateEvent(componentName, componentClass, (IUnknown) delegatedComponent);   
            for(int i=0; i< m_pSR_IDeliver.interfaceList.size();i++){
                m_pSR_IDeliver.interfaceList.get(i).KernelEventDeliver(EventTypes.OCM_CREATE, cSend);
            }
        } 
        else
            return null;
        return delegatedComponent;
    }

    /**
    * private method that is used only by the createInstance method of OpenCOM. It allows
    * the list of interfaces of a particular component to be stored in the vector passed
    * as a parameter. 
    */
    private void AddInterfaces(Class component, Vector<String> interfaceList){
        Class[] theInterfaces = component.getInterfaces();
        if(theInterfaces.length==0){
            //base case
            return;
        }
        else{
            for(int i=0; i<theInterfaces.length; i++){
                boolean found=false;
                for(int j=0; j<interfaceList.size();j++){
                    String VectorString = (String) interfaceList.get(j);
                    if(VectorString.equalsIgnoreCase(theInterfaces[i].getName()))
                        found=true;
                }
                if(!found){
                    interfaceList.add(theInterfaces[i].getName());
                }
                AddInterfaces(theInterfaces[i], interfaceList);
            }
        }
    }

    /**
    * This method deletes the information about a component from the system graph. It does not attempt
    * to delete the instance of the component itself. At present this is left to Garbage Collection. In
    * future versions, a reference counter a la COM may provide more control of memory management; at present
    * it is very easy to leave references to components in place after they are no longer needed.
    * @param pCompToDelete Reference to the component instance to be deleted.
    * @return A boolean describing whether the information about the component was removed from the graph or not.
    */
    public synchronized boolean deleteInstance(IUnknown pCompToDelete){
        for (int i = 0; i < mGraph.size(); i++){
            // Find the component in the system graph
            if (((OCM_GraphNode_t)mGraph.elementAt(i)).pIUnknown.hashCode() == pCompToDelete.hashCode()){
                ComponentDeleteEvent cSend = new ComponentDeleteEvent(getComponentName(pCompToDelete), pCompToDelete);
                            
                // Once found, first Call its shutdown() method
                ILifeCycle pILifeCycle = (ILifeCycle)   pCompToDelete.QueryInterface("OpenCOM.ILifeCycle");
                pILifeCycle.shutdown();
                // Delete all Connections made to its Interfaces
                IMetaInterface pIMetaI = (IMetaInterface)   pCompToDelete.QueryInterface("OpenCOM.IMetaInterface");
                // First enumerate the interfaces
                Vector<Class> ppIntf = new Vector<Class>();
                int length = pIMetaI.enumIntfs(ppIntf);
                // For each interface find if its connected
                Class interfaceClass = null;
                String interfaceName = null;
                for (int y = 0; y < length; y++){
                        interfaceClass = (Class)ppIntf.elementAt(y);
                        interfaceName = interfaceClass.getName();
                        Vector<Long> list = new Vector<Long>();
                        int connections = enumConnsToIntf(pCompToDelete, interfaceName, list);
                        // connections tells us how many connections to delete for this interface
                        for (int z = 0; z < connections; z++){
                                long connID = ((Long)list.get(z)).longValue();
                                disconnect(connID);
                        }
                }
                //Disconnect all of connections on the receptacles of this component
                IConnections pIConnections = (IConnections) pCompToDelete.QueryInterface("OpenCOM.IConnections");
                if (pIConnections == null){
                        // Can safely remove component node from graph
                        // Upload events notifying new kernel delete
                        for(int x=0; x<m_pSR_IDeliver.interfaceList.size();x++){
                            m_pSR_IDeliver.interfaceList.get(x).KernelEventDeliver(EventTypes.OCM_DELETE, cSend);
                        }
                        
                        mGraph.remove(i);
                        return true;
                }
                // If we are here, the IConnections interface IS implemented by the component
                Vector<OCM_GraphRecpInfo_t> recpListVector = mGraph.elementAt(i).pGRecpInfo;
                // Travese the list, disconnecting connections directly and removing the meta-data
                while (recpListVector.size() != 0 ){
                    long connID = recpListVector.firstElement().connID;
                    disconnect(connID);
                }
                // Disconnect all receptacles bound to this components interfaces.			
                Vector<OCM_GraphIntfInfo_t> IntfListVector = mGraph.elementAt(i).pGIntfInfo;
                IUnknown pParent = null;
                IConnections pIntfConnections = null;
                while (IntfListVector.size() != 0){
                    long connID = IntfListVector.firstElement().connID;
                    disconnect(connID);
                }
                
                // Upload events notifying new kernel delete
               for(int k=0; k<m_pSR_IDeliver.interfaceList.size();k++){
                    m_pSR_IDeliver.interfaceList.get(k).KernelEventDeliver(EventTypes.OCM_DELETE, cSend);
                }
                // Remove component node from graph
                mGraph.remove(i);
                
                return true;
            }
        }
        return false; // Component not in the graph
    }

    /**
    * This operation disconnects two previously connected components based upon the unqiue ID of this connection.
    * @param connID A long representing the unqie identifier of the connection to destroy.
    * @return boolean A boolean describing whether the disconnect operation succeeded.
    */ 
    public synchronized boolean disconnect(long connID) {

            OCM_ConnInfo_t pConnInfo;
            IConnections pIConnections;

            // Obtain meta-information about the connection (source, sink, type) using the id
            pConnInfo = getConnectionInfo(connID);
            
            // This connection has already been removed
            if((pConnInfo==null)||(pConnInfo.sourceComponent==null))
                return false;

            // Get the connections interface of receptacle component so we can call its disconnect operation
            pIConnections = (IConnections) pConnInfo.sourceComponent.QueryInterface("OpenCOM.IConnections");

            // Make the physical disconnection
            pIConnections.disconnect(pConnInfo.interfaceType, connID);
            
            // Upload events notifying new kernel delete
            ComponentDisconnectEvent cSend = new ComponentDisconnectEvent(pConnInfo.sourceComponentName, pConnInfo.sinkComponentName, pConnInfo.interfaceType, connID);
            for(int i=0; i<m_pSR_IDeliver.interfaceList.size();i++){
                m_pSR_IDeliver.interfaceList.get(i).KernelEventDeliver(EventTypes.OCM_DISCONNECT, cSend);
            }
            
            //If we were able to disconnect then the connection must exist, i.e. deregister cannot fail
            long hr = deRegisterConnection(connID); // Remove information from the graph about the connection

            return true;
    }

    /** 
    * This is a private method of the OpenCOM runtime that removes information about connections from the graph.
    * It is only called by the OpenCOM disconnect operation.
    */
    private long deRegisterConnection( long connID){
        OCM_ConnInfo_t pConnInfo;

        pConnInfo = getConnectionInfo(connID);
        Vector<OCM_GraphIntfInfo_t> v1 = new Vector<OCM_GraphIntfInfo_t>();
        // Find the sink component (hosting the interface)
        // and remove its meta data for this connection ID
        for(int index=0; index<mGraph.size();index++){
            if (mGraph.elementAt(index).pIUnknown.hashCode() == pConnInfo.sinkComponent.hashCode()){
                // Extract the list of interface connection information stored for the sink component
                OCM_GraphNode_t tempP = mGraph.elementAt(index);
                v1 = tempP.pGIntfInfo;
                break;
            }
        }
        // Find the Interface information element within the list and remove it
        for (int i=0; i< v1.size();i++){
            if(v1.elementAt(i).connID==connID){
                v1.remove(i);
                break;
            }
        }

        // Find the source component (hosting the receptacle) of this connection ID
        // and remove its meta data for this connection ID
        Vector<OCM_GraphRecpInfo_t> v2 = new Vector<OCM_GraphRecpInfo_t>();
        for(int i=0; i<mGraph.size();i++){
            if (mGraph.elementAt(i).pIUnknown.hashCode() == pConnInfo.sourceComponent.hashCode()){
                OCM_GraphNode_t tempP = mGraph.elementAt(i);
                v2 = tempP.pGRecpInfo;
                break;
            }
        }
        for (int i=0; i< v2.size();i++){
            if( v2.elementAt(i).connID==connID){
                v2.remove(i);
                break;
            }
        }

        return 0;
    }

    /**
    * This method enumerates all components currently registetred with the runtime and places their
    * references into the given vector parameter.
    * @param ppComps The list to be filled with components from the graph.
    * @return An integer describing the number of components in the graph.
    */
    public synchronized int enumComponents(Vector<IUnknown> ppComps) {

        int cComp = mGraph.size();

        if(cComp > 0) {	
            for(int i = 0; i < cComp; i++) {
                OCM_GraphNode_t grphNode = mGraph.elementAt(i);
                ppComps.add((IUnknown) grphNode.pIUnknown);
            }		
        } 	
        return cComp;
    }

    /**
    * This method returns the Component type as a java class for a given instance of a component.
    * @param pIUnknown The reference of the component instance.
    * @return A java class describing the component type.
    */
    public synchronized Class getComponentCLSID(IUnknown pIUnknown) {
        for(int i = 0; i < mGraph.size(); i++) {
            if(mGraph.elementAt(i).pIUnknown.hashCode() == pIUnknown.hashCode()) {
                return mGraph.elementAt(i).clsid;

            }
        }
        return null;
    }

    /**
    * This method returns the unqiue component name  for a given instance of a component.
    * @param pIUnknown The reference of the component instance.
    * @return A string describing the component name.
    */
    public synchronized String getComponentName(IUnknown pIUnknown) {
        for(int i = 0; i < mGraph.size(); i++) {
            if(mGraph.elementAt(i).pIUnknown.hashCode() == pIUnknown.hashCode()) {
                return mGraph.elementAt(i).name;

            }
        }
        return null;
    }	
    
    /**
     * Return the meta-data stored in the architectural meta model about an individual component
     * @param pIUnknown an IUnknown reference describing a component instantiation.
     * @return - OCM_GraphNode_t data structure describing the component
     * @see OpenCOM.OCM_GraphNode_t
     */
    public OCM_GraphNode_t getComponentMetaData(IUnknown pIUnknown){
        for(int i = 0; i < mGraph.size(); i++) {
            if(mGraph.elementAt(i).pIUnknown.hashCode() == pIUnknown.hashCode()) {
                return mGraph.elementAt(i);
            }
        }
        return null;
    }
    
    /**
    * This method returns the component reference for a given unique component name.
    * @param CompName The name of the component instance.
    * @return A reference to the component instance.
    */
    public synchronized IUnknown getComponentPIUnknown(String CompName) {
        for(int i = 0; i < mGraph.size(); i++) {
            if(mGraph.elementAt(i).name.equalsIgnoreCase(CompName)){
                return (IUnknown) mGraph.elementAt(i).pIUnknown;
            }
        }
        return null;
    }


    /** 
    * Returns meta-information about a connection (i.e. the source and sink components, and the
    * type of interface these components are conected by.
    * @param connID The unique connection identifier of the connection to inspect.
    * @return An object holding the meta data about the connection.
    * @see OCM_ConnInfo_t 
    */
    public synchronized OCM_ConnInfo_t getConnectionInfo( long connID) {
        OCM_ConnInfo_t ppConnInfo=null;

        Vector<OCM_GraphRecpInfo_t> recpInfos= null;
        for(int i = 0; i < mGraph.size(); i++) {
            recpInfos = mGraph.elementAt(i).pGRecpInfo;
            for(int j=0; j<recpInfos.size();j++){
                if(recpInfos.elementAt(j).connID == connID) {   
                    String cName = mGraph.elementAt(i).name;
                    IUnknown cIUnknown = (IUnknown)  mGraph.elementAt(i).pIUnknown;
                    IUnknown pSinkID = recpInfos.elementAt(j).sinkIndex;
                    String c2Name = getComponentName(pSinkID);
                    String cIntfType =  recpInfos.elementAt(j).iid;

                    ppConnInfo = new OCM_ConnInfo_t(cName,cIUnknown  ,c2Name, pSinkID, cIntfType);
                    return ppConnInfo;
                }
            }
        }

        return null;
    }
    
    /**
     * Find out if a component is primitive or it resides within a framework
     * @param componentID The unique id of the component
     * @return true if the component is contained, false if it is isn't
     */
    public synchronized boolean isContained(String componentID){
        if(componentID != null) {	
            for(int index = 0; index < mGraph.size(); index++) {
                OCM_GraphNode_t VectorElement = (OCM_GraphNode_t) mGraph.elementAt(index);
                if(VectorElement.name.equalsIgnoreCase(componentID)){
                    return VectorElement.primitive;
                }
            }
        }
        return false;
    }
    
    /**
     * Set a component to be primitive i.e. it resides within no framework
     * , or to be contained.
     * @param componentID The unique ID of the component to set the value of
     * @param boolValue True if the component is inside a framework, false if the component isn't
     */
    public void setContained(String componentID, boolean boolValue){
        if(componentID != null) {	
            for(int index = 0; index < mGraph.size(); index++) {
                OCM_GraphNode_t VectorElement = (OCM_GraphNode_t) mGraph.elementAt(index);
                if(VectorElement.name.equalsIgnoreCase(componentID)){
                    VectorElement.primitive=boolValue;
                    return;
                }
            }
        }
    }

    //! Implementation of the IMetaInterception interface for OpenCOM runtime
    //////////////////////////////////////////////////////////////////////////

    /**
    * This method get the delegator for a particular component interface. This can then
    * be used to attache pre and post methods. Each component has a set of delegators - one for
    * each functional interface and one for the IUnknown interface. The developer uses this
    * method to pinpoint the correct one.
    * @param pIUnkParent The component reference we want to get a delegator from.
    * @param riid The interface type we want to get the delegator for.
    * @return The IDelegator interface of the selected delegator instance.
    * @see OpenCOM.IDelegator
    */
    public IDelegator GetDelegator(IUnknown pIUnkParent, String riid) {
        //Search components
        for(int i = 0; i < mGraph.size(); i++) {
            //Look for specified component
            if(mGraph.elementAt(i).pIUnknown.hashCode() == pIUnkParent.hashCode()){
                //Component found!

                //Search the component's pGDelInfo list
                Vector<OCM_DelegatorInfo> pGDelInfo = mGraph.elementAt(i).pGDelInfo;
                for(int j = 0; j<pGDelInfo.size();j++){
                    OCM_DelegatorInfo delinfo = pGDelInfo.elementAt(j);
                    String value = delinfo.iid;
                    if(value.equalsIgnoreCase(riid)){
                        //There is already a delegator associated to the 
                        //specified interface of this component

                        //Return existing delegator component interface
                        return delinfo.pIDelegator;
                    }
                }
            }
        }
        return null;
    }     

    /**
     * Notify the kernel that an external event has occured.
     * @param EventType The type of event generated.
     * @param Event The instance of the event object.
     */
    public void notifyEvent(int EventType, Object Event){
        for(int i=0; i<m_pSR_IDeliver.interfaceList.size(); i++){
            m_pSR_IDeliver.interfaceList.get(i).KernelEventDeliver(EventType, Event);
        }
    }
    
    /*----------------------- IConnections Interface -------------------------------------*/
    public boolean connect(IUnknown pSinkIntf, String riid, long provConnID) {
        if(riid.toString().equalsIgnoreCase("OpenCOM.IKernelDeliver")){
            return m_pSR_IDeliver.connectToRecp(pSinkIntf, riid, provConnID);
	}
	return false;
    }
    
    public boolean disconnect(String riid, long connID) {
	if(riid.toString().equalsIgnoreCase("OpenCOM.IKernelDeliver")){
            return m_pSR_IDeliver.disconnectFromRecp(connID);
	}
	return false;
    }
}
