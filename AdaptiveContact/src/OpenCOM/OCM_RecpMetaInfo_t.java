/*
 * OCM_RecpMetaInfo_t.java
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
 * This class stores Meta-Information about component receptacles. It is used by Interface meta-model
 * to describe a component's receptacles. Application developers can use this meta-data directly.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */

public class OCM_RecpMetaInfo_t{
    //! Information stored about a Receptacle
    
    /** The interface type of the receptacle .*/
    public String iid;
    /** The receptacle type, either "Single" or "Multiple". */
    public String recpType;
    
    /**
     * Constructor creates a new instance of OCM_RecpMetaInfo_t object. 
     * @param interfaceType The interface type of the receptacle.
     * @param type The receptacle type i.e. either single or multiple.
     */
    public OCM_RecpMetaInfo_t(String interfaceType, String type) {
        iid=interfaceType;
        recpType = type;
    }
    
}
