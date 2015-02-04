/*
 * LCM_GraphNode_t.java
 *
 * LCM is a flexible component model for reconfigurable reflection developed at Lancaster University.
 * Copyright (C) 2005 Paul Grace
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package OpenCOM.ReflectionMessages;
import java.lang.reflect.*;
import java.util.*;
import OpenCOM.*;
/**
 * The graph in LCM's architecture meta-model component is a set of LCM_GraphNode_t objects stored in a list.
 * How they are connected together is stored within the information in the node.
 * Every Lancaster component has a corresponding node in the system graph i.e. its meta
 * representation.
 *
 * @author Paul Grace
 * @version 1.3.5 (New to 1.3.4)
 */
public class LCM_GraphNode_t implements java.io.Serializable{
        
    /** An associated unique name for the component. */
    public String name;                            
    
    /** The corresponding Java class of the Lancaster component. */
    public Class clsid;                 
    
    /** list of LCM_GraphRecpInfo_t nodes describing the list of receptacles for the component. */
    public Vector<LCM_GraphRecpInfo_t> pGRecpInfo;    
    
    /** list of LCM_GraphIntfInfo_t nodes describing the list of interfaces for this component. */
    public Vector<LCM_GraphIntfInfo_t> pGIntfInfo;  
    
    /** list of LCM_DelegatorInfo_t nodes describing the list of delegated interfaces for this component. */
    //public Vector<LCM_DelegatorInfo_t> pGDelInfo;            

    public LCM_GraphNode_t(String ComponentName, Class ComponentClass){
        pGRecpInfo = new Vector<LCM_GraphRecpInfo_t>();
        pGIntfInfo = new Vector<LCM_GraphIntfInfo_t>();
        name = ComponentName;
        clsid = ComponentClass;
    }
    
    /** Constructor creates a new instance of OCM_GraphNode_t object*/
     public LCM_GraphNode_t(String ComponentName, Object ComponentInterface, Class ComponentClass) {
        pGRecpInfo = new Vector<LCM_GraphRecpInfo_t>();
        pGIntfInfo = new Vector<LCM_GraphIntfInfo_t>();
        name = ComponentName;
        clsid = ComponentClass;
    }
     
    public LCM_GraphNode_t(IOpenCOM pRuntime, OCM_GraphNode_t OCMNode){
        name = OCMNode.name;
        clsid = OCMNode.clsid;
        pGRecpInfo = new Vector<LCM_GraphRecpInfo_t>();
        pGIntfInfo = new Vector<LCM_GraphIntfInfo_t>();
        
        for(int i=0; i< OCMNode.pGRecpInfo.size();i++){
            OCM_GraphRecpInfo_t pRecpInfo = OCMNode.pGRecpInfo.get(i);
            LCM_GraphRecpInfo_t pTemp = new LCM_GraphRecpInfo_t(pRecpInfo.connID, pRuntime.getComponentName(pRecpInfo.sinkIndex), pRecpInfo.iid);
            pGRecpInfo.add(pTemp);
        }
        
        for(int i=0; i< OCMNode.pGIntfInfo.size();i++){
            OCM_GraphIntfInfo_t pIntfInfo = OCMNode.pGIntfInfo.get(i);
            LCM_GraphIntfInfo_t pTemp = new LCM_GraphIntfInfo_t(pIntfInfo.connID, pRuntime.getComponentName(pIntfInfo.sourceIndex), pIntfInfo.iidName);
            pGIntfInfo.add(pTemp);
        }
    }
}
