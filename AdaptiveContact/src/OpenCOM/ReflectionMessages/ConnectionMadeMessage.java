/*
* ConnectionMadeMessage.java
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
package OpenCOM.ReflectionMessages;

/**
 * Event message stating that a connection has been created
 *
 * @author Paul Grace
 * @version 1.3.5 (New to 1.3.4)
 */
public class ConnectionMadeMessage extends ReflectionMessage implements java.io.Serializable{
   
     static final long serialVersionUID = -2025469067791150528L;
     
    /**
     * Receptacle host
     */
    public String sourceComponentName;
    /**
     * Interface host
     */
    public String sinkComponentName;
    /**
     * Interface type
     */
    public String iid;

    /** 
     * Framework connection resides in
     */
    public String inFramework;
   
}