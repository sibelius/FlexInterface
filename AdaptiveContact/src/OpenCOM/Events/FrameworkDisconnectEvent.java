/*
 * FrameworkDisconnectEvent.java
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
 * Event generated by the kernel to indicate a a connection within a framework has been destroyed. This
 * event is then fired to all the kernel's listeners.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public class FrameworkDisconnectEvent {
    /** The User generated name for the newly created component. */
    public String componentName;

    public String FrameworkName;
    
    /** Creates a new instance of the FrameworkDisconnectEvent */
    public FrameworkDisconnectEvent(String CompName, String fName) {
        componentName=CompName;
        FrameworkName=fName;
        
    }

}
