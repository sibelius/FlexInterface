/**
 *  IConnections.java
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
import OpenCOM.IUnknown;
/**
 * The IConnection interface is implemented by every component with a
 * receptacle. This interface should never be called by the application, and 
 * should only be utilised by the runtime kernel.
 * @author  Paul Grace
 * @version 1.3.5
 */
public interface IConnections extends IUnknown{
    
    /**
     * Connects the specified sink interface to a receptacle of the
     * specified type in the target component instance.
     *  @param pSinkIntf a component instance to connect to.
     *  @param riid a string representing the Interface type of the connection.
     *  @param provConnID a long representing the unique identifier of the connection.
     *  @return a boolean indicating the success of the method.
     *  @see OpenCOM.IUnknown
     */
    boolean connect(IUnknown pSinkIntf, String riid, long provConnID);
	
    /** 
     * Takes a previously established connection's connection identifier
     * and disconnects the corresponding receptacle and interface by 
     * resetting the receptacle's internal interface pointer.
     *  @param riid a string representing the Interface type of the connection.
     *  @param connID a long representing the unique identifier of the connection.
     *  @return a boolean indicating the success of the method.
     */
    boolean disconnect(String riid, long connID);
    
}