/*
 * OCM_MultiReceptacleParallel.java
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
import OpenCOM.IReceptacle;
import OpenCOM.IUnknown;
import java.util.*;
import java.lang.reflect.*;

/**
 * Programming abstraction for a multi-receptacle with parallel invocations. Multiple components all
 * implementing the same interface type can be connected to this receptacle.
 * When invoked each connection executes in a separate thread. Note, there are no return values; hence
 * void methods are appropriate. We advocate the use of callbacks to handle the return of results
 * from multiple executing methods.
 * 
 * <p>
 * public OCM_MultiReceptacleParallel<IInterfaceType> m_PSR_IIntfType 
 *    = new OCM_MultiReceptacleParallel<IInterfaceType>(IInterfaceType.class);
 * <p>
 * m_PSR_IIntfType.m_pIntf.foo(params);
 *
 * @author  Paul Grace
 * @version 1.3.5
 */

public class OCM_MultiReceptacleParallel<InterfaceType> extends OCM_Receptacle implements IReceptacle{
    
    private class DebugProxy implements java.lang.reflect.InvocationHandler {

        class invocationThread extends Thread{
            int index;
            Method method;
            Object[] args;
            
            public invocationThread(int val, Method m, Object[] arguments){
                this.index=val;
                this.method=m;
                this.args=arguments;
            }
            
            public void run(){
                try{
                    method.invoke(interfaceList.get(index), args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        private Object obj;

        public Object newInstance(Object obj) {
            return java.lang.reflect.Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new DebugProxy(obj));
        }

        private DebugProxy(Object obj) {
            this.obj = obj;
        }

        public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable
            {
            Object result=null;
            try {
                for(int i=0; i<interfaceList.size();i++ ){
                    invocationThread newThr = new invocationThread(i, m, args);
                    newThr.start();
                }
            } catch (Exception e) {
                throw new RuntimeException("unexpected invocation exception: " +
				       e.getMessage());
            }  
            return result;
        }
    }   
    
    public InterfaceType m_pIntf;
    
    /** List of interface pointers this receptacle is connected to. */
    private Vector<Object> interfaceList;
    
    /** List of connIDS for each connection of this receptacle. */
    private Vector<Long> connIDS;
    
    private int numberOfConnections;    
    
    /** 
     * Constructor creates a new instance of OCM_MultiReceptacle object. Usually called
     * from within OpenCOM component constructors.
     * @param cls_type The type of interface to initialse this receptacle to
     */ 
    public OCM_MultiReceptacleParallel(Class<InterfaceType> cls_type) {
        super();
        interfaceList = new Vector<Object>();
        connIDS = new Vector<Long>();
        numberOfConnections = 0;
        class_type = cls_type;
        ClassLoader cl2 = cls_type.getClassLoader();
        m_pIntf = (InterfaceType) Proxy.newProxyInstance(cl2,
                new Class[] {cls_type}, new DebugProxy((InterfaceType) this));
        iid = cls_type.toString();        
    }
    
    //! Implementation of IReceptacle interface
    ////////////////////////////////////////////////////////////////////////////////
    /**
     * This method connects the recpetacle to given component on the given interface type.
     * @param pIUnkSink Reference to the sink component who hosts the interface that the receptacle is to be connected to.
     * @param riid A string representing the interface type of the connection.
     * @param provConnID A long representing the generated unqiue ID of this particular connection.
     * @return A boolean indicating the success of this operation
     **/
    public boolean connectToRecp(IUnknown pIUnkSink, String riid, long provConnID) {
        // Get the reference to the component hosting the interface
        try{
            InterfaceType pIntfRef = (InterfaceType) pIUnkSink.QueryInterface(riid);
            interfaceList.add(pIntfRef);
        }
        catch(ClassCastException e){
            System.err.println("Connect Failed: Connecting Receptacle and Interface of different types");
            return false;
        }

        // Add the component, reference and id to the receptacles object stores      
        connIDS.add(new Long( provConnID));

        numberOfConnections++;
        return true;
    }
    
    /**
     * This method disconnects a given receptacle
     * @param connID A long representing the generated unqiue ID of this particular connection.
     * @return A boolean indicating the success of this operation
     **/
    public boolean disconnectFromRecp(long connID) {
        // Traverse the receptacle data looking for the required connection ID
        for(int i = 0; i < numberOfConnections ; i++) {
            Long vecConnID = connIDS.elementAt(i);
            if(vecConnID.longValue() == connID) {
                // Found it - now remove all pieces of information about that connection
                numberOfConnections--;
                interfaceList.remove(i);
                connIDS.remove(i);
            }
            if(numberOfConnections ==0) {
                m_pIntf = null;
                return true;
            }
            return true;
	}

	return false;
    }
}
