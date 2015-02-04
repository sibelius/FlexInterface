/*
 * IDelegator.java
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
 * Description: Interface describing operation available on individual
 * delegators. Its operations are identical to the original pre and post
 * interception of OpenCOM. 
 * 
 * Note: I have not implemented hooking as I do not believe it offers
 * anything beneficial. However, if anyone wants to add it - it would
 * be relatively straightforward, just follow the same dynamic proxy 
 * approach.
 * @author  Paul Grace
 * @version 1.3.5
 */
public interface IDelegator{
    /**
     * Inserts a pre-method on this delegator. All subsequent invocation of interface operations
     * first pass through this method. Multiple pre-methods can be inserted, they are traversed in 
     * the order they were inserted.
     * @param methodHost A Java object containing the pre-method to insert.
     * @param methodName A String describing the name of the pre-method.
     * @return A boolean indicating the success of the operation. 
     **/
    boolean addPreMethod(Object methodHost, String methodName);
    
    /**
     * Deletes a specified pre-method from this delegator. 
     * @param methodName A String describing the name of the pre-method.
     * @return A boolean indicating the success of the operation. 
     **/
    boolean delPreMethod(String methodName);

    /**
     * Inserts a post-method on this delegator. All subsequent invocation of interface operations
     * pass through this method after invocation. Multiple post-methods can be inserted, they are traversed in 
     * the order they were inserted.
     * @param methodHost A Java object containing the pre-method to insert.
     * @param methodName A String describing the name of the pre-method.
     * @return A boolean indicating the success of the operation. 
     **/
    boolean addPostMethod(Object methodHost, String methodName);

    /**
     * Deletes a specified post-method from this delegator. 
     * @param methodName A String describing the name of the pre-method.
     * @return A boolean indicating the success of the operation. 
     **/
    boolean delPostMethod(String methodName);

    /**
     * A Meta-Inspection operation. Returns a pointer to an array of strings representing the current
     * list of pre-methods attached to the delegator.
     * @param methodNames A String array to be filled with the names of pre-methods.
     * @return A long describing the number of pre-methods in the list. 
     **/
    long viewPreMethods(String[] methodNames);

    /**
     * A Meta-Inspection operation. Returns a pointer to an array of strings representing the current
     * list of post-methods attached to the delegator.
     * @param methodNames A String array to be filled with the names of post-methods.
     * @return A long describing the number of post-methods in the list. 
     **/
    long viewPostMethods(String[] methodNames); 
    
    /**
     * For simplicity this version of OpenCOM attaches interface meta-data to the delagator.
     * For a cleaner separation see the Java implementation of OpenCOM v2.
     * This method sets the name-value pair.
     * @param Name A string describing the meta-data attribute.
     * @param Type A string describing the meta-data type.
     * @param Value An Object holding the value of the attribute.
     * @return A boolean indicating if the attribute was added/updated or not.
     **/
    boolean SetAttributeValue(String Name, String Type, Object Value);
    
    /**
     * This method retrieves the value of a name-value pair.
     * @param Name A string describing the meta-data attribute.
     * @return A TypedAttribute Object holding the value and type of the attribute.
     **/
    TypedAttribute GetAttributeValue(String Name);
    
    /**
     * This method retrieves all the meta-data stored on the interface.
     * @return A hashatable containing all of the attribute-value pairs for the interface.
     **/
    public Hashtable<String, TypedAttribute> getValues();
    
    /**
     * Enable the interface as intercepted i.e. the delegator becomes active.
     * @param parameter true or false to turn interception on or off.
     */
    public void SetInterception(boolean parameter);
}
