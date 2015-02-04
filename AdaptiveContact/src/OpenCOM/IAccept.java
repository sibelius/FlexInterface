/*
 * IAccept.java
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
 * The IAccept interface is implemented by components who perform check operations on Component Frameworks.
 * This interface and its syntax is heavily influenced by the C++ CF model proposed by
 * ReMMoC (and hence why it includes what seem like unnecessary parameters).
 * 
 * @author Paul Grace
 * @version 1.3.5
 **/

public interface IAccept extends IUnknown{
    /**
     * This method performs validation checks on CF graphs.
     * @param graph A Vector containing the internal graph of the composite component framework to check
     * @param Intfs A Vector describing the list of interfaces exposed by the component framework
     * @param cComps An integer representing the number of components in the graph
     * @param cIntfs An integer describing the number of exposed interfaces
     * @return A boolean indicating whether the CF contains a valid or invalid configuration
     **/
    boolean isValid(Vector<IUnknown> graph, Vector<CFMetaInterface.ExposedInterface> Intfs, int cComps,  int cIntfs);
}

