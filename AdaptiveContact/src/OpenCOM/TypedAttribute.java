/*
 * TypedAttribute.java
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

/**
 * TypedAttribute stores a Java object representing an attribute value
 * with its explicit type information. It can be reused across name-value
 * pair implementations. However, it is fundamental in the implementation
 * of the OpenCOM IMetaInterface meta-model.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public class TypedAttribute{
    /* Java class description as a String describing the type of an attribute. */
    public String Type;   
    /* Java Object holding the attribute value */
    public Object Value;    
    
    /** 
     * Constructor creates a new instance of TypedAttribute.
     * @param type The String description of the Java class of the attribute object.
     * @param value The Java object representing the value of the attribute.
     */
    public TypedAttribute(String type, Object value) {
        Type = type;
        Value=value;
    }
}
