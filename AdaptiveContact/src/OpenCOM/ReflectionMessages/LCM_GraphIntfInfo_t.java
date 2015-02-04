/*
 * LCM_GraphIntfInfo_t.java
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

/**
 * This class is used  within the architecture meta model component. Basically, each object stores information
 * about component's interfaces i.e. their type and if they are connected to a particular receptacle.
 * Generally, a component node in the graph will contain a list of these objects.
 *
 * @author Paul Grace
 * @version 1.3.5 (New to 1.3.4)
 */
public class LCM_GraphIntfInfo_t implements java.io.Serializable{
    /** Connection id of the connection this interface is part of. */
    public long connID;   

    /** The interface type of this interface. */
    public String iidName;
    
    public String Source;
    
    /** Constructor creates a new instance of OCM_GraphIntfInfo_t node*/
    public LCM_GraphIntfInfo_t(long connectionID, String src, String name) {
        connID=connectionID;
        iidName= name;
        Source = src;
    }
    
}
