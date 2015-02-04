/*
 * OCM_ConnInfo_t.java
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

/**
 * The OCM_ConnInfo_t class represents Meta-Information stored in the OpenCOM runtime about every 
 * connection between components.
 *
 * @author  Paul Grace
 * @version 1.3.5
 **/

public class OCM_ConnInfo_t{
    /** A string describing the unique name of the component hosting the receptacle. **/
    public String sourceComponentName; 
    /** 
     * A reference to the component instance hosting the receptacle. 
     * @see OpenCOM.IUnknown
     **/
    public IUnknown sourceComponent; 
 
    /** A string describing the unique name of the component hosting the receptacle. **/
    public String sinkComponentName; 
    /** 
     *A reference to the component instance hosting the interface. 
     * @see OpenCOM.IUnknown
     **/ 
    public IUnknown sinkComponent; 

    /** A string describing the interface type of the connection. **/
    public String interfaceType; 
    
    /** 
     * Default constructor that allows the source component, sink component & interface type information to be set
     * @see OpenCOM.IUnknown
     **/
    public OCM_ConnInfo_t(String srcComponentName, IUnknown srcComponent,  
                            String skComponentName, IUnknown skComponent, String iidType) {
        sourceComponentName = srcComponentName;
        sourceComponent = srcComponent;
        sinkComponentName = skComponentName;
        sinkComponent = skComponent;
        interfaceType = iidType;
    }
    
}
