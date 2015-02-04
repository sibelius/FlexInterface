/*
 * ComponentExposedInterfaceEvent.java
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

package OpenCOM.Events;

/**
 * Event generated when an interface is exposed on a composite component.
 * @author  Paul Grace
 * @version 1.3.5
 */
public class ComponentExposedInterface {

        /** Component Reference points to the actual reference of the interface to expose*/
        public String FrameworkName;   
        /** Interface type of the exposed interface */
        public String Intf;        

        /** 
         * Constructor 
         * @param fName Instance of component the interface to be exposed resides upon.
         * @param Interface The types of the interface described by a string.
         */
        public ComponentExposedInterface(String fName, String Interface){
            FrameworkName = fName;
            Intf = Interface;
        }
    }
