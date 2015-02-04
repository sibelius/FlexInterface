/*
 * LCM_ConnInfo_t.java
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

/**
 * The LCM_ConnInfo_t class represents Meta-Information stored in the LCM runtime about every 
 * connection between components.
 *
 * @author Paul Grace
 * @version 1.3.5 (New to 1.3.4)
 **/

public class LCM_ConnInfo_t implements java.io.Serializable{
    /** A string describing the unique name of the component hosting the receptacle. **/
    public String sourceComponentName; 

    /** A string describing the unique name of the component hosting the receptacle. **/
    public String sinkComponentName; 

    /** A string describing the interface type of the connection. **/
    public String interfaceType; 
    
    /** 
     * Default constructor that allows the source component, sink component & interface type information to be set
     **/
    public LCM_ConnInfo_t(String srcComponentName, String skComponentName, String iidType) {
        sourceComponentName = srcComponentName;
        sinkComponentName = skComponentName;
        interfaceType = iidType;
    }
    
}
