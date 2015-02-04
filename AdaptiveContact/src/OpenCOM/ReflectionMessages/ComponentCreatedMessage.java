/*
  * ComponentCreatedMessage.java
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
 * Event message stating that a new component has been created
 * 
 * @author Paul Grace
 * @version 1.3.5
 */
public class ComponentCreatedMessage extends ReflectionMessage implements java.io.Serializable{
   
    static final long serialVersionUID =  -6313484228497727480L;
    
    /**
     * This is a standard component
     */
   public static final int TYPE_COMPONENT = 300;
   
   /**
    * This is a component inside a framework
    */
   public static final int TYPE_FRAMEWORK = 301;
   
   /**
    * Name of component
    */
   public String componentName;
   
   /**
    * List of interfaces on the component
    */ 
   public String[] interfaces;
   
   /**
    * List of receptacles on the component
    */
   public String[] receptacles;
   
   public String clsType;
   
   /**
    * Framework name this component resides within
    */
   public String inFramework;
   
   public int type;
   
   public long frameworkID;
   
   public String NodeID;
  
   }