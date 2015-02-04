/*
 * IMetaInterface.java
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
import OpenCOM.IUnknown;
/**
 * This interface is implemented by every OpenCOM component. It support the interface meta-model
 * of OpenCOM. Allow instrospection and manipualtion of interfaces and receptacles on the component
 * plus the corresponding meta-data attachments.
 *
 * @author  Paul Grace
 * @version 1.3.5
 * 
 */
public interface IMetaInterface extends IUnknown{
    /**
    * Returns a Vector of meta-information. Each elements of the Vector is an object of
    * type OCM_RecpMetaInfo_t, which describes the attributes of indiviudal receptacles
    * including: type (single or multiple) & interface type.
    * @param ppRecpMetaInfo a Vector to be filled with receptacle meta-information.
    * @return an Integer describing the number of receptacles on the component.
    **/
    int enumRecps( Vector<OCM_RecpMetaInfo_t> ppRecpMetaInfo);

    /**
    * Returns a Vector of meta-information. Each elements of the Vector is a String describing
    * that interface's type.
    * @param ppIntf a Vector to be filled with interface meta-information.
    * @return an Integer describing the number of interfaces on the component.
    **/
    int enumIntfs( Vector<Class> ppIntf);
    
    /**
     * Meta-data can be attached to each interface/receptacle of a component. This method adds a name
     * value pair to a given interface or receptacle instance.
     * @param iid the type of the interface or receptacle.
     * @param Kind a string saying whether to attach to an interface or a receptacle.
     * @param Name A String describing the attribute name.
     * @param Type A String describing the attribute type.
     * @param Value An object representing the attribute value.
     * @return A boolean indicating the success of the operation. 
     **/
    boolean SetAttributeValue(String iid, String Kind, String Name, String Type, Object Value);
    
    /**
     * Meta-data can be retrieved from each interface/receptacle of a component. This method 
     * retrieves the value of a name attribute on a receptacle or interface.
     * @param iid the type of the interface or receptacle.
     * @param Kind a string saying whether to attach to an interface or a receptacle.
     * @param Name A String describing the attribute name.
     * @return A TypedAttribute object containing the value and type of the meta-data attribute. 
     **/
    TypedAttribute GetAttributeValue(String iid, String Kind, String Name);
    
    /**
     * This method retrieves all the meta-data stored on the interface or receptacle.
     * @param iid the type of the interface or receptacle.
     * @param Kind a string saying whether to attach to an interface or a receptacle.
     * @return A hashatable containing all of the attribute-value pairs for the receptacle or interface.
     **/
    Hashtable<String, TypedAttribute> GetAllValues(String Kind, String iid);
    
}
