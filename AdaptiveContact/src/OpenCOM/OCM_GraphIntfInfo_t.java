/*
 * OCM_GraphIntfInfo_t.java
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
 * This class is used only within the system graph of OpenCOM. Basically, each object stores information
 * about component's interfaces i.e. their type and if they are connected to a particular receptacle.
 * Generally, a component node in the graph will contain a list of these objects.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public class OCM_GraphIntfInfo_t{
    /** Connection id of the connection this interface is part of. */
    public long connID;   
    /** Source component (receptacle host) that this interface is connected to. */
    public IUnknown sourceIndex;
    /** The interface type of this interface. */
    public String iidName;
    
    /** Constructor creates a new instance of OCM_GraphIntfInfo_t node*/
    public OCM_GraphIntfInfo_t(long connectionID, IUnknown indexinGraph, String name) {
        connID=connectionID;
        sourceIndex=indexinGraph;
        iidName= name;
    }
    
}
