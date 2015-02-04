/*
 * ICFMetaInterface.java
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
import java.util.*;

/**
 * The ICFMetaInterface interface is implemented by all composite components. It supports operations to inspect
 * and make changes to a composite component framework's internal meta-representation of component
 * architecture. Hence, it supports the architecture meta-model for invidual component frameworks. 
 *
 * @author Paul Grace
 * @version 1.3.5
 */

public interface ICFMetaInterface extends IUnknown{

    //! Operations for Inspection	
        boolean find_component(IUnknown pComp);
	
	/**
         * This method fills the vector passed as a parameter with the references of all
         * the components that reside locally within this framework.
         * @param ppComps A vector to be filled with the references of inner components.
         * @return The number of components within the framework.
         */	
	int get_internal_components(Vector<IUnknown> ppComps); 
        
        /**
        * This method returns the component reference for a given unique component name in the framework.
        * @param CompName The name of the component instance.
        * @return A reference to the component instance.
        */
        IUnknown getComponentPIUnknown(String CompName);

        /**
        * This method returns the unique component name of a given component reference.
        * @param CompIUnk The component unique reference.
        * @return The components's unique string name.
        */
        String getComponentName(IUnknown CompIUnk);
        
	/**
         * This method produces a list of components that are connected to a 
         * particular component within the framework.
         * @param comp Instance of the component we wish to find what is connected to it.
         * @param ppConnections Vector to be filled with the list of components that are connected to this component.
         * @return An integer describing the number of components connected to this component.
         */
	int get_bound_components(IUnknown comp, Vector<CFMetaInterface.ConnectedComponent> ppConnections);

	/**
         * This method returns all of the internal connections between components that are wholly within
         * the framework.
         * @param pConnIDS A vector to be filled with long values describing the unique id of each connection.
         * @return An integer describing the number of connections within the framework.
         */	
	int get_internal_bindings(Vector<Long> pConnIDS);

	/**
         * This method fills the vector passed as a parameter with the list of interfaces
         * exposed by this framework. Its behaviour is similar to that provided by
         * enumIntfs of OpenCOM.
         * @param ppIntfs A vector to be filled with the list of interfaces.
         * @return The number of interfaces exposed by this framework.
         */
	int get_detailed_exposed_interfaces(Vector<CFMetaInterface.ExposedInterface> ppIntfs);

        /**
         * This method fills the vector passed as a parameter with the list of interfaces
         * exposed by this framework. Its behaviour is similar to that provided by
         * enumIntfs of OpenCOM.
         * @param ppIntfs A vector to be filled with the list of interfaces.
         * @return The number of interfaces exposed by this framework.
         */
	int get_exposed_interfaces(Vector<String> ppIntfs);
        
	/**
         * This method fills the vector passed as a parameter with the list of receptacles
         * exposed by this framework. Its behaviour is similar to that provided by
         * enumRecps of OpenCOM.
         * @param ppComps A vector to be filled with the list of receptacles.
         * @return The number of interfaces exposed by this framework.
         */
	int get_exposed_receptacles(Vector<CFMetaInterface.ExposedReceptacle> ppComps);

	//! Operations for reconfiguration
	
	/**
         * This method binds together two components only if they both reside in the framework.
         * @param pIUnkSource The source component with the receptacle.
         * @param pIUnkSink The sink component with the interface.
         * @param InterfaceType The interface type to make the connection on.
         * @return A long describing the unique ID of this new connection. -1 indicates failure to connect.
         * @see OpenCOM.IUnknown
         */	
	long local_bind( IUnknown pIUnkSource, IUnknown pIUnkSink, String InterfaceType);

	/**
         * This method disconnects two components only if they both reside in the framework
         * and are connected.
         * @param connID The unique ID of the connection to break.
         * @return A boolean indicating if the disconnection was made.
         */	
	boolean break_local_bind( long connID);

	/**
         * This method creates the component within the framework. The component is created, stored 
         * in the runtime, and inserted into this framework's meta-data.
         * @param componentType The type of the component to create.
         * @param componentName The unique name of the component to create. 
         * @return A reference to the newly created component instance.
         * @see OpenCOM.IUnknown
         */	
	IUnknown create_component(String componentType, String componentName);
        
        /**
         * This method inserts a previously instantiated component from the runtime, into
         * the framework instance.
         * @param pCompReference The reference of the component instance.
         * @return A boolean indicating if the insert occured or not.
         * @see OpenCOM.IUnknown
         */	
	boolean insert_component(IUnknown pCompReference);
	
	 /**
         * This method deletas the component from the framework. The component is disconnected, 
         * deleted from the runtime, and this framework's meta-data is updated.
         * @param pIUnknown The component instance to delete 
         * @return A boolean indicating if the component was deleted or not.
         * @see OpenCOM.IUnknown
         */	
	boolean delete_component(IUnknown pIUnknown);

	/**
         * This method takes the interface from one of the framework's internal components
         * and then makes it one of its own functional interfaces.
         * @param rintf The interface type that will be exposed.
         * @param pComp The internal component hosting the the interface.
         * @return A boolean describing if the interface was exposed.
         * @see OpenCOM.IUnknown
         */	
	boolean expose_interface(String rintf, IUnknown pComp);

	/**
         * This method removes the exposed interface from the outer component framework,
         * @param rintf The interface type that will be removed.
         * @param pComp The internal component hosting the the interface.
         * @return A boolean describing if the interface has been removed.
         */
	boolean unexpose_interface(String rintf,  IUnknown pComp);

	/**
         * This method removes all exposed interfaces.
         * @return A boolean describing if all the interfaces have been removed.
         */
	boolean unexpose_all_interfaces();

	/**
         * This method takes the receptacle from one of the framework's internal components
         * and then makes it one of its own receptacles.
         * @param rintf The interface type that will be exposed.
         * @param pComp The internal component hosting the the interface.
         * @param recpType The type of the receptacle.
         * @return A boolean describing if the receptacle was exposed.
         * @see OpenCOM.IUnknown
         */	
	boolean expose_receptacle(String rintf,  IUnknown pComp, String recpType);

	/**
         * This method removes the exposed receptacle from the outer component framework,
         * @param rintf The interface type that will be removed.
         * @param pComp The internal component hosting the the receptacle.
         * @return A boolean describing if the receptacle has been removed.
         */
	boolean unexpose_receptacle(String rintf,  IUnknown pComp);

	/**
         * This method removes all exposed receptacles.
         * @return A boolean describing if all the receptacle have been removed.
         */
	boolean unexpose_all_receptacles();

	/**
         * All reconfigurations must be performed as part of a transaction. Therefore,
         * the reconfigure agent must first call this method before subsequent write 
         * operations.
         * @return A boolean describing if the transaction can continue. 
         */  
	boolean init_arch_transaction();

	/**
         * This method must be called by the reconfiguration agent at the end of the reconfiguration
         * transaction. It forces a check on the new configuration, which is commited or not based upon the result.
         * @return The boolean describes of the new configuration was commited. A false means that the
         * last good configuration was rolled back to.
         */  
	boolean commit_arch_transaction();

	/**
         * Rolls the configuration back to its previous state - ideally should not be called
         * directly; maybe if faults are being detected is a supposedly valid architecture
         * you may wish to try returning to a stable version. 
         * @return A boolean describing if the roll back was a success.
         */
	boolean rollback_arch_transaction();

        /**
        * Each component framework implemnts a lock to prevent reconfiguration
        * during functional operation. This method attempts to get read or write
        * access to the lock based upon the input. The locking mechanism is readers,
        * writers with priority for readers.
        * @param index accessType - an integer describing access type: 0 for read, 1 for write.
        * @return A boolean describing if the lock has processed this request or not.
        **/
	boolean access_CF_graph_lock(int index);

	/**
        * Releases the lock, previously acquired.
        * @param index - accessType An integer describing acces type: 0 for read, 1 for write.
        * @return A boolean describing if the lock has processed this request or not.
        **/
	boolean release_CF_graph_lock(int index);

	/**
        * Update the CF's locks readers count. Do not use - only used by runtime.
        * @param Value increment amount.
        * @return An integer describing the new reader count.
        **/
        int update_readers_count(int Value);
        
          IUnknown find_component(String pIntf);
}
