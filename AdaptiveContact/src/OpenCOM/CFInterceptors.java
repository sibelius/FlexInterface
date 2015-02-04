/*
 * CFInterceptors.java
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
 * This class holds generic interceptors for the component framework model. Each
 * CF will create an instance of this object; this is because the interceptors
 * directly access individual locks of the component framework. The interceptors
 * are used to create a readers-writers solution for framework access, with
 * priority given to readers.
 *
 * @author  Paul Grace
 * @version 1.3.5
 */
public class CFInterceptors {
    
    /**
     * The reference to the interface of this object's corresponding component
     * framework. Used to invoke the access lock methods.
     **/
    private ICFMetaInterface pMeta ;
    
    /** 
     * Creates a new instance of CFInterceptors and sets the reference to the
     * CF's API.
     */
    public CFInterceptors(ICFMetaInterface ThisComponentFramework) {
        pMeta = (ICFMetaInterface) ThisComponentFramework;
    }
    
    /**
     * Pre0 is inserted onto every exposed interface of the component framework automatically.
     * If you are using fixed functional interfaces from your framework then you may wish to
     * add these interceptors yourself. Every operation invoked on the interface 
     * forces the readers count to be incremented. If we are the first reader we also get the
     * write lock to prevent write access while there are readers.
     * @param method The method intercepted - we don't utilise.
     * @param args The list of paramaters of the invocation - not utilised.
     * @return An integer telling the runtime how to proceed. 0 tells the run-time we
     * can continue with calling the actual operation.
     */
    public synchronized int Pre0(String method, Object[] args){
        // access 1 means we want the readers count sempahore
	pMeta.access_CF_graph_lock(1);
        // Got it, now update the readers counter
	int rc = pMeta.update_readers_count(1);
        // We are the first reader so we must acquire the CF lock sempahore
        if (rc==1){
            // access 0 means we want the CF lock semaphore
            pMeta.access_CF_graph_lock(0);
	}
        // We've finished with the readers count so release the readers count semaphore
	pMeta.release_CF_graph_lock(1);
	return 0;
    }

    /**
     * Post0 is inserted onto every exposed interface of the component framework automatically.
     * It is executed after every operation of the intercepted interface. It first 
     * decrements the readers count as we've finished read access. The write lock is
     * released if we are the last reader.
     * @param method The method intercepted - we don't utilise.
     * @param args The list of paramaters of the invocation - not utilised.
     * @return An Object holding the result of the intercepted operation.
     */
     public synchronized Object Post0(String method, Object result, Object[] args, Exception e){
        // access 1 means we want the readers count sempahore
        pMeta.access_CF_graph_lock(1);
        // Got it, now update the readers counter
	int rc = pMeta.update_readers_count(-1);
        // We are the last reader so we must release the CF lock sempahore
        if (rc==0){
            pMeta.release_CF_graph_lock(0);
	}
        // We've finished with the readers count so release the readers count semaphore
	pMeta.release_CF_graph_lock(1);
	return result;
			
    }
     
    
}

