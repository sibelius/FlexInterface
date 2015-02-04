/*
 * OCM_SingleReceptacle.java
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
import java.lang.reflect.*;
import java.util.*;
import java.lang.*;
/**
 * OpenCOM defines a Single Receptacle to be " a single pointer to an
 * interface". 
 *
 * To define a component receptacle use:
 * OCM_SingleReceptacle<IInterfaceType> m_PSR_IIntfType = new OCM_SingleReceptacle<IInterfaceType>(IInterfaceType.class);
 *
 * To call an interface from within the component code:
 * return m_PSR_IIntfType.m_pIntf.foo(params);
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public class OCM_SingleReceptacle<InterfaceType> extends OCM_Receptacle implements IReceptacle{
  
    /* The reference point of the interface this receptacle is connected to. */
    public InterfaceType m_pIntf;  
    private long m_connID;                  			// Unique connection identifier 
    
    /** 
     * Creates a new instance of the OCM_SingleReceptacle class. Typically this is only
     * performed within the constructor of OpenCOM components.
     * @param cls_type The type of interface to initialse this receptacle to
     */
    public OCM_SingleReceptacle(Class<InterfaceType> cls_type) {
        super();
        m_connID = -1;
        meta_Data = new Hashtable<String, TypedAttribute>();
        class_type = cls_type;
        iid = cls_type.toString();
    }
    
    //! Implementation of IReceptacle interface
    ////////////////////////////////////////////////////////////////////////////////
    //! This method stores the reference to the component hosting the interface
    //!
    public boolean connectToRecp(IUnknown pIUnkSink, String riid, long provConnID) {

	if(m_pIntf == null) {
            try{
                InterfaceType pIntfRef = (InterfaceType) pIUnkSink.QueryInterface(riid);
                if(pIntfRef==null)
                    return false;
                m_pIntf = pIntfRef;
            }
            catch(ClassCastException e){
                System.err.println("Connect Failed: Connecting Receptacle and Interface of different types");
                return false;
            }
            m_connID = provConnID;
            return true;
	} 
	else
            return false;
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    //! This method destroys the existing connection data
    //!
    public boolean disconnectFromRecp(long connID) {
        if(m_connID != connID)
		return false;

	if(m_pIntf != null) {
            m_connID = -1;
            m_pIntf = null;
            return true;
	} 
	else
            return false;
    }
}


