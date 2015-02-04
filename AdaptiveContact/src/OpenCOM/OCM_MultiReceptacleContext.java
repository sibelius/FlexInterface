/*
 * OCM_MultiReceptacleContext.java
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
import java.lang.reflect.*;

/**
 * Programming abstraction for a multi-receptacle with context selection. Multiple components all
 * implementing the same interface type can be connected to this receptacle.
 * Only the connection matching the full set of context  rules is invoked 
 * <p>
 * public OCM_MultiReceptacleContext&lt;IInterfaceType&gt; m_PSR_IIntfType 
 *    = new OCM_MultiReceptacleContext&lt;IInterfaceType&gt;(IInterfaceType.class);
 *<p>
 * m_PSR_IIntfType.setContext("Attr1", "Val1");
 *<p>
 * m_PSR_IIntfType.setContext("Attr2", "Val2");
 * <p>
 * m_PSR_IIntfType.m_pIntf.foo(params);
 *<p>
 * @author  Paul Grace
 * @version 1.3.5
 */

public class OCM_MultiReceptacleContext<InterfaceType> extends OCM_Receptacle implements IReceptacle{
    
    public class Rule {
        public String Attribute; 
        public Object Value;
    
        /** Creates a new instance of Rule */
        public Rule(String att,Object val) {
            Attribute=att;
            Value=val; 
        }
    }
    
    class DebugProxy implements java.lang.reflect.InvocationHandler {
        
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
                // Traverse the list of connections
                for(int i=0; i<numberOfConnections;i++){
                
                    // Get the IMetaInterface from the component at the other end of connection
                    IUnknown component = components.get(i); 
                    IMetaInterface pGetAtts=  (IMetaInterface) component.QueryInterface("OpenCOM.IMetaInterface");
                    boolean match=true;
                    for(int j=0; j<ContextRules.size(); j++){
                        // Read the meta-value from the Interface 
                        TypedAttribute AttrVal =  (TypedAttribute) pGetAtts.GetAttributeValue(iid, "Interface", ContextRules.get(j).Attribute);
                        
        /////////////////// CHANGE PJG
                        // If this matches the given value - then this is the connection index to return
                        if(AttrVal!=null){
                            if(AttrVal.Value.equals(ContextRules.get(j).Value))
                                match=match&&match;
                            else
                                match=false;
                        }  
                        else{
                            match=false;
                        }
       /////////////////// CHANGE END PJG
                    }
                    if(match){
                        result = m.invoke(interfaceList.get(i), args);
                        return result;
                    }
                    else if(ContextRules.size()==0){
                        // call the first connection
                        result = m.invoke(interfaceList.get(0), args);
                        return result;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("unexpected invocation exception: " +
				       e.getMessage());
            }  
            return result;
        }
    }   
    
    /** The receptacle endpoint to be invoked by the programmer. */
    public InterfaceType m_pIntf;
    
    /** List of interface pointers this receptacle is connected to. */
    private Vector<Object> interfaceList;
    
    /** List of connIDS for each connection of this receptacle. */
    private Vector<Long> connIDS;
    
    /** List of components that this receptacle is connected to. */
    private Vector<IUnknown> components;
    private int numberOfConnections;    
    private Vector<Rule> ContextRules;
    
    /** 
     * Constructor creates a new instance of OCM_MultiReceptacleContext object. Usually called
     * from within OpenCOM component constructors.
     * @param cls_type The type of interface to initialse this receptacle to
     */
    public OCM_MultiReceptacleContext(Class<InterfaceType> cls_type) {
        super();
        interfaceList = new Vector<Object>();
        connIDS = new Vector<Long>();
        components = new Vector<IUnknown>();
        ContextRules = new Vector<Rule>();
        numberOfConnections = 0;
        class_type = cls_type;
        iid = class_type.toString().substring(10);
        ClassLoader cl2 = cls_type.getClassLoader();
        m_pIntf = (InterfaceType) Proxy.newProxyInstance(cl2, new Class[] {cls_type}, new DebugProxy((InterfaceType) this));
    }
    
    /** 
     * Add a context rule to this receptacle (its a set of name-value pairs).
     * This will directly influence
     * the selection of a connection to be invoked by the receptacle. i.e. a connection
     * matching all the rules will be invoked.
     * @param Name The Attribute name
     * @param Value The type of interface to initialse this receptacle to
     */
    public void addContext(String Name, Object Value){
        Rule toAdd = new Rule(Name, Value);
        boolean added=false;
        for(int i=0; i<ContextRules.size(); i++){
            Rule r = ContextRules.get(i);
            if(toAdd.Attribute.equals(r.Attribute)){
                ContextRules.add(i, toAdd);
                added=true;
                break;
            }
        }
        if(!added)
            ContextRules.add(toAdd);
    }
    
    /** 
     * Remove a previously entered context rule
     * @param Name The Attribute name
     */
    public void removeContext(String Name){
        for(int i=0; i<ContextRules.size(); i++){
            Rule r = ContextRules.get(i);
            if(r.Attribute.equals(Name)){
                ContextRules.remove(i);
                break;
            }
        }
    }
    
    //! Implementation of IReceptacle interface
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
        components.add(pIUnkSink);       
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
            return true;
	}

	return false;
    }

}
