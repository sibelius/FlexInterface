/*
 * OCM_GraphRecpInfo_t.java
 *
 * Created on 19 July 2004, 15:02
 */

package OpenCOM.ReflectionMessages;
/**
 * This class is used only within the inner working of the Lancaster graph and its corresponding 
 * meta-operations. Generally, these object are attached to the component nodes in the system
 * graph. Each component will have a number of these objects dependent upon the number of 
 * receptacles that they host.
 *
 * @author Paul Grace
 * @version 1.3.5 (New to 1.3.4)
 */

public class LCM_GraphRecpInfo_t implements java.io.Serializable{
    /**  Connection id of the connection this receptacle is part of. */
    public long connID;  
    
    /** The interface type of the receptacle */
    public String iid;
    
    /** The receptacle host */
    public String Sink;
    
    /** Constructor creates a new instance of OCM_GraphRecpInfo_t object to store in system graph */
    public LCM_GraphRecpInfo_t(long connectionID, String snk, String name) {
        connID=connectionID;
        iid=name;
        Sink = snk;
    }
    
}
