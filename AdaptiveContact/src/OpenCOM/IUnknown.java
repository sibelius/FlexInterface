/*
 * IUnknown.java
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
import java.lang.String.*;
import java.lang.reflect.*;

/**
 * The IUnknown interface is a central aspect of OpenCOM. However,
 * it is not actually required in the Java version. Standard Interface/Object behaviour
 * in Java negates the need for QueryInterface. Furthermore, the garbage collector
 * means addref and release are not required. However, IUnknown remains in this
 * version to present the same (okay similar) programming model to traditional
 * OpenCOM. 
 *
 * Note: QI is an improvement over object casting, like OpenCOM you can detect if the
 * interface is available on the component before invoking it. That is, we retain
 * one of the programming model benefits of COM.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public interface IUnknown {
    /**
     * Obtain a reference to the interface of the type passed as parameter
     * @param interfaceName a string representing the Java interaface type, equivalent to the IID type in COM.
     * @return an Object representing a reference to the component hosting the interface requested.
     **/
    Object QueryInterface(String interfaceName);
}

