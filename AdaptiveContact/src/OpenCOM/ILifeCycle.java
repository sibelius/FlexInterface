/**
 * ILifeCycle.java
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
 * Interface implemented by every OpenCOM components. Manages the startup and shutdown
 * of components.
 * @author  Paul Grace
 * @version 1.3.5
 */

public interface ILifeCycle extends IUnknown{
    
    /**
    * Allows a component to take action whenever an instance
    * is created. A pointer to the IOCM of the runtime is passed to allow
    * the instance to store it as a member variable, thus allowing
    * convenient access from member methods.
    * This method should be called directly by the programmer after an
    * instance is created. This separation between creation and 
    * initialisation is needed because startup() typically 
    * invokes receptacle-based intarfaces, which must be connected 
    * after a component instance has been created by the runtime,
    * i.e. before startup can be invoked.
    * @param data Any startup data to be passed to the component when it is activated.
    * @return A boolean indicating the success of the operation
    * @see OpenCOM.IOpenCOM
    **/
    boolean startup(Object data);

    /**
    * Allows a component to take action while it is being deleted.
    * This method SHOULD NOT be directly called by the programmer
    * but it is called by the OpenCOM runtime when a component
    * it deleted by calling deleteInstance().
    * @return A boolean indicating the success of the operation
    **/
     boolean shutdown();
    
}
