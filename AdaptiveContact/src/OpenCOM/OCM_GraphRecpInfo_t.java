/*
 * OCM_GraphRecpInfo_t.java
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

/**
 * This class is used only within the inner working of the OpenCOM graph and its corresponding 
 * meta-operations. Generally, these object are attached to the component nodes in the system
 * graph. Each component will have a number of these objects dependent upon the number of 
 * receptacles that they host.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */

public class OCM_GraphRecpInfo_t{
    /**  Connection id of the connection this receptacle is part of. */
    public long connID;    
    /**  The index in the system graph of the sink component. That is, which component the
     *  component is connected; its pointing to a OCM_GraphNode_t object.
     */
    public IUnknown sinkIndex;
    /** The interface type of the receptacle */
    public String iid;
    
    /** Constructor creates a new instance of OCM_GraphRecpInfo_t object to store in system graph */
    public OCM_GraphRecpInfo_t(long connectionID, IUnknown indexinGraph, String name) {
        connID=connectionID;
        sinkIndex=indexinGraph;
        iid=name;
    }
    
}
