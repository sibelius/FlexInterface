/*
 * OpenCOMComponent.java
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
import java.lang.reflect.*;

/**
 * OpenCOMComponent is a public abstract class that an OpenCOM developer can use to implement
 * their components and prevent any code bloat. However, it is not a requirement of an OpenCOM
 * component to extend this class. Remember the only specification of an OpenCOM Component
 * is that it implements IUnknown, and that its interfaces are OpenCOM interfaces. 
 * Therefore, the developer can produce this anyway they see fit. For example, Java does not support 
 * multiple inheritence therefore creating components from classes that extend application level
 * base classes would not be possible (see tutorial to see different methods to develop individual components.
 *  
 * @author  Paul Grace
 * @version 1.3.5
 * @see OpenCOM.IOpenCOM 
 * @see OpenCOM.IMetaInterface 
 * @see OpenCOM.IMetaInterception 
 * @see OpenCOM.IMetaArchitecture
 */
public abstract class OpenCOMComponent implements IUnknown, IMetaInterface{
    
    /** 
     * Component meta data about the interfaces of the component; and the
     * name value meta-data pairs attached to individual interfaces. 
     */
    private MetaInterface Meta;
    
    /**
     * Every OpenCOM component has access to the runtime in which it is executing.
     */
    protected OCM_SingleReceptacle<IOpenCOM> m_PSR_IOpenCOM;
    
    /** Creates a new instance of OpenCOMComponent */
    public OpenCOMComponent(IUnknown mpIOCM) {
       // Create the receptacle to the runtime and then connect it to the runtime 
        m_PSR_IOpenCOM = new OCM_SingleReceptacle<IOpenCOM>(IOpenCOM.class);
        
        // Connect the component without storing the information at the meta-level
        // Every component-to-runtime connection is given the id 0
        m_PSR_IOpenCOM.connectToRecp(mpIOCM, "OpenCOM.IOpenCOM", 0);

        Meta = new MetaInterface((IOpenCOM) m_PSR_IOpenCOM.m_pIntf, this);
    }
    
    /**
     * Obtain a reference to the interface of the type passed as parameter
     * @param InterfaceName a string representing the Java interaface type, equivalent to the IID type in COM.
     * @return an Object representing a reference to the component hosting the interface requested.
     **/
    
    public Object QueryInterface(String InterfaceName) {
        Class c = this.getClass();
        Vector<String> query = new Vector<String>();
        Meta.ReadInterfaceNames(c, query);
        for (int i = 0; i < query.size(); i++) {
            String interfaceName = (String) query.get(i).toString();
            if(interfaceName.equalsIgnoreCase(InterfaceName)){
                return this; 
            }
        }
        return null;
    }
    
    // IMetaInterface Interface Implementation
    /**
    * Returns a Vector of meta-information. Each elements of the Vector is a String describing
    * that interface's type.
    * @param ppIntf a Vector to be filled with interface meta-information.
    * @return an Integer describing the number of interfaces on the component.
    **/
    public int enumIntfs(Vector<Class> ppIntf) {
        return Meta.enumIntfs(this, ppIntf);
    }
    
    /**
    * Returns a Vector of meta-information. Each elements of the Vector is an object of
    * type OCM_RecpMetaInfo_t, which describes the attributes of indiviudal receptacles
    * including: type (single or multiple) & interface type.
    * @param ppRecpMetaInfo a Vector to be filled with receptacle meta-information.
    * @return an Integer describing the number of receptacles on the component.
    **/
    public int enumRecps(Vector<OCM_RecpMetaInfo_t> ppRecpMetaInfo) {
        return Meta.enumRecps((IUnknown) this, ppRecpMetaInfo);
    }
    
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
    public boolean SetAttributeValue(String iid, String Kind, String Name, String Type, Object Value) {
         return Meta.SetAttributeValue(iid, Kind, Name, Type, Value);
    }    
    
    /**
     * Meta-data can be retrieved from each interface/receptacle of a component. This method 
     * retrieves the value of a name attribute on a receptacle or interface.
     * @param iid the type of the interface or receptacle.
     * @param Kind a string saying whether to attach to an interface or a receptacle.
     * @param Name A String describing the attribute name.
     * @return A TypedAttribute object containing the value and type of the meta-data attribute. 
     **/
    public TypedAttribute GetAttributeValue(String iid, String Kind, String Name) {
         return Meta.GetAttributeValue(iid, Kind, Name);
    }    
    
    /**
     * This method retrieves all the meta-data stored on the interface or receptacle.
     * @param iid the type of the interface or receptacle.
     * @param Kind a string saying whether to attach to an interface or a receptacle.
     * @return A hashatable containing all of the attribute-value pairs for the receptacle or interface.
     **/
    public Hashtable<String, TypedAttribute> GetAllValues(String Kind, String iid){
         return Meta.GetAllValues(Kind, iid);
    }
}
