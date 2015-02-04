/*
 * IMetaInterception.java
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
 * This interface is implemented by the OpenCOM runtime. It is used to obtain delegator interfaces
 * for perform meta-interception operations.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */

public interface IMetaInterception extends IUnknown{
    
	/**
         * Returns the delegator of a given interface type on a given instantiated component.
         * @param pIUnkParent an IUnknown reference describing a component instantiation.
         * @param riid a string representing the interface type of the requested delegator.
         * @return the IDelegator interface.
         * @see OpenCOM.IUnknown
         * @see OpenCOM.IDelegator
         */
        IDelegator GetDelegator(IUnknown pIUnkParent, String riid); 
};