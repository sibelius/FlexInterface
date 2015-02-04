/*
 * IMetaArchitecture.java
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
 * Interface implemented by the OpenCOM runtime. Provides operations to determine which
 * components are connected to a particular component's interfaces and receptacles. i.e.
 * it provides introspection operations.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public interface IMetaArchitecture extends IUnknown {

    /**
    * Returns an array of connection identifiers detailing all the
    * connecitons established on the specified interface of the target
    * component instance.
    * @param pIUnknown A reference of the component whose connections are to be inspected.
    * @param riid A String represting the interface type to be inspected.
    * @param ppConnsToIntf a list of unqiue connection identifiers of connections to this interface.
    * @return An integer describing the number of connections to the interface.
    **/
    int enumConnsToIntf(IUnknown pIUnknown, String riid, Vector<Long> ppConnsToIntf);

    /**
    * Returns an array of connection identifiers detailing all the 
    * connections established by the  receptacle of the specified 
    * interface type on the target component instance.
    * @param pIUnknown A reference of the component whose connections are to be inspected.
    * @param riid A String represting the interface type to be inspected.
    * @param ppConnsFromRecp a list of unqiue connection identifiers of connections from this receptacle.
    * @return An integer describing the number of connections from the receptacle.
    **/
    int enumConnsFromRecp(IUnknown pIUnknown, String riid,  Vector<Long> ppConnsFromRecp);
}
