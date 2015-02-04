/*
 * IReceptacle.java
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
import java.util.*;

/**
 * Interface implemented only by receptacles. Provides operations to manipulate the receptacles
 * themselves. That is, connect and disconnect them, add meta-data to them and so on...
 *
 * @author Paul Grace
 * @version 1.3.5
 */

public interface IReceptacle{
    /**
     * This method connects the recpetacle to given component on the given interface type.
     * @param pIUnkSink Reference to the sink component who hosts the interface that the receptacle is to be connected to.
     * @param riid A string representing the interface type of the connection.
     * @param provConID A long representing the generated unqiue ID of this particular connection.
     * @return A boolean indicating the success of this operation
     **/
    boolean connectToRecp(IUnknown pIUnkSink, String riid, long provConID);
    /**
     * This method disconnects a given receptacle
     * @param connID A long representing the generated unqiue ID of this particular connection.
     * @return A boolean indicating the success of this operation
     **/
    boolean disconnectFromRecp(long connID);
    
    /**
     * This method attaches a name-value pair element of meta-data to the receptacle
     * @param Name A String describing the attribute name.
     * @param Type A String describing the attribute name.
     * @param Value An object representing the attribute value.
     * @return A boolean indicating the success of this operation
     **/
    public boolean putData(String Name, String Type, Object Value);
    
    /**
     * This method retrieves the value of a name attribute from the receptacle.
     * @param Name A String describing the attribute name.
     * @return A TypedAttribute object containing the value of the meta-data attribute. 
     **/
    public TypedAttribute getValue(String Name);
    
     /**
     * This method retrieves all the meta-data stored on the  receptacle.
     * @return A hashatable containing all of the attribute-value pairs for the receptacle.
     **/
    public Hashtable<String, TypedAttribute> getValues();
}
