/*
 * OCM_Receptacle.java
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
import java.util.*;
import java.lang.*;
/**
 * Base class of all receptacles.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public abstract class OCM_Receptacle{
  
    /* The reference point of the interface this receptacle is connected to. */
    public String iid;
    public Class class_type;
    protected Hashtable<String, TypedAttribute> meta_Data;        // Meta-data information stored by this receptacle
    
    /** 
     * Creates a new instance of the OCM_SingleReceptacle class. Typically this is only
     * performed within the constructor of OpenCOM components.
     * @param cls_type The type of interface to initialse this receptacle to
     */
    public OCM_Receptacle() {
        meta_Data = new Hashtable<String, TypedAttribute>();
    }

   /**
     * This method adds meta-data name-value pair attributes to the receptacle instance.
     * @param name The attribute name.
     * @param type The attribute name.
     * @param value An Object holding the attribute value.
     * @return A boolean describing if the pair was added or not.
     */
    public boolean putData(String name, String type, Object value){
        try{
            TypedAttribute aNewAttr= new TypedAttribute(type, value);
            meta_Data.put(name,aNewAttr);
        }
        catch(NullPointerException n){
            return false;
        }
        return true;
    }
    
    /**
     * This method gets the value of a named meta-data attribute.
     * @param name The attribute name.
     * @return The TypedAttribute object storing the value.
     */
    public TypedAttribute getValue(String name){
        return  (TypedAttribute) meta_Data.get(name);
    }
    
    /**
     * This method returns all name-value meta-data pairs on this receptacle instance.
     * @return A Hashtable storing the pairs.
     */
    public Hashtable<String, TypedAttribute> getValues() {
        return meta_Data;
    }
    
}


