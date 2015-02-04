/*
 * OCM_GraphNode_t.java
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

/**
 * The system graph in OpenCOM is a set of OCM_GraphNode_t objects stored in a list.
 * How they are connected together is stored within the information in the node.
 * Every OpenCOM component has a corresponding node in the system graph i.e. its meta
 * representation.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public class OCM_GraphNode_t{
        
    /** An associated unique name for the component. */
    public String name;                 

    /** The reference to the physical instance of the component. */
    public Object pIUnknown;            
    
    /** A description of the component as to whether it is contained inside another or not.*/
    public boolean primitive;
    
    /** The corresponding Java class of the OpenCOM component. */
    public Class clsid;                 
    
    /** list of OCM_GraphRecpInfo_t nodes describing the list of connections on receptacles for the component. */
    public Vector<OCM_GraphRecpInfo_t> pGRecpInfo;    
    
    /** list of OCM_GraphIntfInfo_t nodes describing the list of connections on interfaces for this component. */
    public Vector<OCM_GraphIntfInfo_t> pGIntfInfo;  
    
    /** list of OCM_DelegatorInfo_t nodes describing the list of delegated interfaces for this component. */
    public Vector<OCM_DelegatorInfo> pGDelInfo;            

    /** Constructor creates a new instance of OCM_GraphNode_t object*/
    public OCM_GraphNode_t(String ComponentName, Object ComponentInterface, Class ComponentClass) {
        pGIntfInfo = new Vector<OCM_GraphIntfInfo_t>();
        pGRecpInfo = new Vector<OCM_GraphRecpInfo_t>();
        pGDelInfo = new Vector<OCM_DelegatorInfo>();
        name = ComponentName;
        clsid = ComponentClass;
        pIUnknown = ComponentInterface; 
        primitive=false;
    }
    
}
