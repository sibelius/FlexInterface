/*
 * EventTypes.java
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

package OpenCOM.Events;

/**
 * Identifiers for the set of events generated from the OpenCOMJ kernel
 * @author  Paul Grace
 * @version 1.3.5
 */
public class EventTypes {
    
    public static final int OCM_CREATE = 0;        
    public static final int OCM_CONNECT = 1;
    public static final int OCM_DELETE = 2;
    public static final int OCM_DISCONNECT = 3;
    public static final int OCMCF_CREATE = 4;
    public static final int OCMCF_EXPOSE_INTERFACE = 5;
    public static final int OCMCF_EXPOSE_RECEPTACLE = 6;
    public static final int OCMCF_UNEXPOSE_INTERFACE = 7;
    public static final int OCMCF_UNEXPOSE_RECEPTACLE = 8;
    public static final int OCMCF_DELETE = 9;
    public static final int OCMCF_BIND = 10;
    public static final int OCMCF_UNBIND = 11;
    public static final int OCMCF_INIT = 10;
    public static final int OCMCF_COMMIT = 11;
}
