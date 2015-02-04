/*
 * ComponentExposedReceptacleEvent.java
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

package OpenCOM.Events;

/**
 * The ComponentExposedReceptacleEvent class is used to describe the receptacles exposed by individual component
 * frameworks. 
 *
 * @author  Paul Grace
 * @version 1.3.5
 */

/* Local class definition for Exposed Receptacles that are stored in the MOPs of the framework */ 
public class ComponentExposedReceptacle{
       /** The component reference which the physical receptacle exists upon */
       public String FrameworkName;       
	/** The interface type of the receptacle */
       public String Intf;            
       
        /** 
         * Constructor
         * @param fName The reference of component instance the new exposed receptacle is physically hosted by.
         * @param Interface The interface type of the new exposed receptacle.
         */
        public ComponentExposedReceptacle(String fName, String Interface){
            FrameworkName = fName;
            Intf = Interface;
        }
    }