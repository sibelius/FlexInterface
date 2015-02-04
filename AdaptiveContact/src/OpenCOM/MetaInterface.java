/*
 * MetaInterface.java
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
import java.lang.reflect.*;
import java.util.*;
/**
 * Each OpenCOM component contains this object to implement the interface meta-model.
 * Methods for introspecting interfaces, and receptacles are available. Furthermore,
 * meta-data can be attached and read as name-value pairs from both the interfaces
 * and the receptacles.
 *
 * @author  Paul Grace.
 * @version 1.3.5
 */
public class MetaInterface{
    
    private class Interface{
        public String iid;
        public IDelegator idel;
        
        public Interface(String a, IDelegator b){
            iid = a;
            idel=b;
        }
    }
    
    private IOpenCOM m_pRTintf;
    private IUnknown m_Comp;
    private Vector<String> Interfaces;
    private boolean ready;
    private Interface[] Delegators;
    private boolean buildDels;
    
    /** Creates a new instance of MetaInterface */
    public MetaInterface(IOpenCOM pRTintf, IUnknown component) {
        m_pRTintf = pRTintf;
        m_Comp = component;
        Interfaces = new Vector<String>();
        ready=false;
        ReadInterfaceNames(m_Comp.getClass(), Interfaces);
        ready=true;
        buildDels=false;
    }
    
    boolean OpenCOMInterface(Class Interface){
        boolean IUnknown = false;
        // base of recursion
        if(Interface==IUnknown.class)
            return true;
        else{
            Class[] Intfs = Interface.getInterfaces();
            for(int i=0; i<Intfs.length; i++){
                if(OpenCOMInterface(Intfs[i]))
                    return true;
            }
            return false;
        }
    }
    
    /** 
     * Get the interfaces (as Java classes) of a component and stores them in the given vector.
     * The operation is recursive to find inherited interfaces.
     * @param compClass The class of the component.
     * @param intfList The Vector to be filled with the components interfaces.
     */
    public void GetInterfaces(Class compClass, Vector<Class> intfList){
        Class[] theInterfaces = compClass.getInterfaces();
        if(theInterfaces.length==0){
            //base case
            return;
        }
        else{
            for(int i=0; i<theInterfaces.length; i++){
                boolean found=false;
                for(int j=0; j<intfList.size();j++){
                    if(intfList.get(j)==theInterfaces[i])
                        found=true;
                }
                if(!found){
                    if(OpenCOMInterface(theInterfaces[i]))
                        intfList.add(theInterfaces[i]);
                }
                    
                GetInterfaces(theInterfaces[i], intfList);
            }
        }
    }
    
    public void ReadInterfaceNames(Class c, Vector<String> a){
        if(!ready){
            Class[] theInterfaces = c.getInterfaces();
            if(theInterfaces.length==0){
                //base case
                return;
            }
            else{
                for(int i=0; i<theInterfaces.length; i++){
                    boolean found=false;
                    for(int j=0; j<a.size();j++){
                        String VectorString = (String) a.get(j);
                        if(VectorString.equalsIgnoreCase(theInterfaces[i].getName()))
                            found=true;
                    }
                    if(!found){
                        if(OpenCOMInterface(theInterfaces[i]))
                            a.add(theInterfaces[i].getName());
                    }
                    ReadInterfaceNames(theInterfaces[i], a);
                }
            }
        }
        else{
            for(int i=0; i<Interfaces.size();i++){
                a.add(Interfaces.get(i));
            }
        }
            
    }
    
    /** 
     * Get the interfaces (as Java classes) of a component and stores them in the given vector.
     * @param compRef The reference to the instance of the component.
     * @param intfList The Vector to be filled with the components interfaces.
     * @return An integer describing the number of interfaces on this component.
     */
    public int enumIntfs(Object compRef, java.util.Vector<Class> intfList) {
        Class d = compRef.getClass();
        GetInterfaces(d, intfList);
        return intfList.size();
    }
    
    
    /** 
     * Get the receptacles of a component and stores them in the given vector.
     * @param compRef The reference to the instance of the component.
     * @param recpList The Vector to be filled with the component's receptacles.
     * @return An integer describing the number of receptacles on this component.
     * @see OpenCOM.OCM_RecpMetaInfo_t
     */
    public int enumRecps(IUnknown compRef, java.util.Vector<OCM_RecpMetaInfo_t> recpList) {
        
        int count=0;
        // Receptacles are public fields in each component - so we first read the component's fields
        Class compType = compRef.getClass();
        Field[] publicFields = compType.getFields();
        
        Class fieldType=null;
        Class typeClass=null;
        String type;
        Field[] publicFields1 =null;

        for (int i = 0; i < publicFields.length; i++) {
            typeClass = publicFields[i].getType();
            fieldType = typeClass.getSuperclass();
            if(fieldType==OCM_Receptacle.class){
                try{
                    // Extract the 
                    OCM_Receptacle Value= (OCM_Receptacle) publicFields[i].get(compRef);
                    String iidFieldValue = Value.iid;
		    String iid = iidFieldValue.substring(10);
                    OCM_RecpMetaInfo_t pp= new OCM_RecpMetaInfo_t(iid, typeClass.getName());
                    recpList.add(pp);
                }
                catch(Exception e){
                    e.printStackTrace();
                    return count;
                }
                count++;
            }
        }

        return count;
    }
    
    /** 
     * Set the value of a name value pair on either an interface or receptacle.
     * @param iid The type of the interface or receptacle.
     * @param Kind A string which is either "Interface" or "Receptacle".
     * @param Name A string describing the attribute name.
     * @param Type A string describing the attribute type.
     * @param Value An object holding the attribute value.
     * @return A boolean indicating if the attribute value was added.
     */
    public boolean SetAttributeValue(String iid, String Kind, String Name, String Type, Object Value){
        if(Kind.equalsIgnoreCase("Interface")){
            // Get meta interception interface
            IMetaInterception pMetaIc = (IMetaInterception) m_pRTintf.QueryInterface("OpenCOM.IMetaInterception");
            IDelegator pIDel =  pMetaIc.GetDelegator(m_Comp, iid);
            return pIDel.SetAttributeValue(Name, Type, Value);
        }
        else  if(Kind.equalsIgnoreCase("Receptacle")){
            Class c = m_Comp.getClass();
            Field[] publicFields = c.getFields();
            for (int i = 0; i < publicFields.length; i++) {
                Class typeClass = publicFields[i].getType().getSuperclass();
                if(typeClass==OCM_Receptacle.class){
                    try{
                        IReceptacle recp = (IReceptacle) publicFields[i].get(m_Comp);
                        OCM_Receptacle rt = (OCM_Receptacle) publicFields[i].get(m_Comp);
                        String intfType = rt.iid;
                        if(intfType.equalsIgnoreCase("interface "+iid)){
                            recp.putData(Name, Type, Value);
                            return true;
                        }
                    }
                    catch(Exception e){
                        return false;
                    }
                }  
            }
        }
        return false;
        
    }
    
    /** 
     * Retrieve the value of a name value pair on either an interface or receptacle.
     * @param iid The type of the interface or receptacle.
     * @param Kind A string which is either "Interface" or "Receptacle".
     * @param Name A string describing the attribute name.
     * @return An object holding the value of the attribute.
     */
    public TypedAttribute GetAttributeValue(String iid, String Kind, String Name){
        if(!buildDels){
            int next=0;
            Delegators = new Interface[Interfaces.size()];
            for(int i=0; i<Interfaces.size();i++){
                IDelegator pIDel = ((IMetaInterception) m_pRTintf).GetDelegator(m_Comp, Interfaces.get(i));
                if(pIDel!=null)
                    Delegators[next++] = new Interface(Interfaces.get(i),pIDel);
            }
            buildDels=true;
        }
        if(Kind.equalsIgnoreCase("Interface")){
            for(int j=0; j<Delegators.length;j++){
                if (Delegators[j].iid.equalsIgnoreCase(iid)){
                    return Delegators[j].idel.GetAttributeValue(Name);
                }
            }
        }
        else  if(Kind.equalsIgnoreCase("Receptacle")){
            Class c = m_Comp.getClass();
            Field[] publicFields = c.getFields();
            for (int i = 0; i < publicFields.length; i++) {
                Class typeClass = publicFields[i].getType().getSuperclass();
                if(typeClass==OCM_Receptacle.class){
                    try{
                        IReceptacle recp = (IReceptacle) publicFields[i].get(m_Comp);
                        OCM_Receptacle rt = (OCM_Receptacle) publicFields[i].get(m_Comp);
                        String intfType = rt.iid;
                        if(intfType.equalsIgnoreCase("interface "+iid)){
                            return (TypedAttribute) recp.getValue(Name);
                        }
                    }
                    catch(Exception e){
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /** 
     * Retrieve all the  name value pairs on either an interface or receptacle.
     * @param iid The type of the interface or receptacle.
     * @param Kind A string which is either "Interface" or "Receptacle".
     * @return An object holding the value of the attribute.
     */
    public Hashtable<String, TypedAttribute> GetAllValues(String Kind, String iid){
        if(Kind.equalsIgnoreCase("Interface")){
            IMetaInterception pMetaIc = (IMetaInterception) m_pRTintf.QueryInterface("OpenCOM.IMetaInterception");
            IDelegator pIDel =  pMetaIc.GetDelegator(m_Comp, iid);
            return pIDel.getValues();
        }
        else  if(Kind.equalsIgnoreCase("Receptacle")){
            Class c = m_Comp.getClass();
            Field[] publicFields = c.getFields();
            for (int i = 0; i < publicFields.length; i++) {
                 Class typeClass = publicFields[i].getType().getSuperclass();
                if(typeClass==OCM_Receptacle.class){
                    try{
                        IReceptacle recp = (IReceptacle) publicFields[i].get(m_Comp);
                        OCM_Receptacle rt = (OCM_Receptacle) publicFields[i].get(m_Comp);
                        String intfType = rt.iid;
                        if(intfType.equalsIgnoreCase("interface "+iid)){
                            return recp.getValues();
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
