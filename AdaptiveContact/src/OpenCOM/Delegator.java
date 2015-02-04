
/*
 * Delegator.java
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
 * Class of individual delegator objects that are attached to
 * each interface for the purpose of pre and post method interception.
 * Note: The implementation mimics OpenCOM's dummy QI redirection using
 * Java dynamic proxies instead.
 * @see OpenCOM.IDelegator
 * @see java.lang.reflect.InvocationHandler
 * @author  Paul Grace
 * @version 1.3.5
 */
public class Delegator implements java.lang.reflect.InvocationHandler, IDelegator{
    
  /** The original Component that we are delegating from */
  public Object obj;    
  /** Static reference to this delegator */
  public static Object ThisObject;  
  
  /** List of pre methods stored on this delegator */
  Vector<methodList> PreMethods;    
  
  /** List of post methods stored on this delegator */
  Vector<methodList> PostMethods;   
  /** The Outer Proxy of this delegator */
  public Object HigherObject; 
  
  private boolean InterceptionEnabled=false;
  
  /**
   * Pointer to MetaInterception runtime.
   * @see OpenCOM.IMetaInterception
   */
  private IMetaInterception pOCMIMetaInterception;   
  
  /** Meta data attached to this receptacle */
  private Hashtable<String, TypedAttribute> MetaData;
   
  /**
   * Class describes how pre and post methods are stored on the delegator.
   */
  private class methodList{
     /** Java Reflect method corresponding to the physical method implementation. */
     public Method method;
     /** Object instance hosting the pre/post method */
     public Object object;
     /** A string name of the pre/post method */
     public String name; 
     
     /** 
      * Constructor
      * @param newMethod The reflection method of the corresponding physical method.
      * @param InterceptorObject Reference to instance hosting the interceptor method.
      * @param MethodName Name of the method as a string.
      */
     
     public methodList(Method newMethod, Object InterceptorObject, String MethodName){
        method = newMethod;
        object = InterceptorObject;
        name = MethodName;
     }
  }
  
  /** 
   * The dynamic proxy creation operation - takes the original component and wraps the 
   * dynamic invocation handler around it.
   */
    public static Object newInstance(Object obj) {
     Object ParentObject= java.lang.reflect.Proxy.newProxyInstance(
             obj.getClass().getClassLoader(),
             obj.getClass().getInterfaces(),
             (java.lang.reflect.InvocationHandler) ThisObject);
     return ParentObject;
    }
    
    /*
     * Constructor for new delegator instance.
     * @see OpenCOM.IMetaInterception
     */
    public Delegator(Object obj, IMetaInterception pIOCM) {
          this.obj = obj;
          PreMethods= new Vector<methodList>();
          PostMethods = new Vector<methodList>();
          ThisObject = this;
          pOCMIMetaInterception = pIOCM;
          MetaData = new Hashtable<String, TypedAttribute>();
    }
    
    public void SetInterception(boolean parameter){
        InterceptionEnabled=parameter;
    }
    
    /**
     * invoke is called on this dynamic proxy whenever a method of the "inner" component
     * is invoked. Therefore, it will ensure that the pre methods are called before the actual
     * operation and the post methods afterwards.
     * @param proxy the proxy component.
     * @param m The method to be invoked.
     * @param args An object array with all the arguments of the original invocation.
     * @return An object holding the result of the invocation.
     */
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
    {
        if(!InterceptionEnabled){
             return m.invoke(obj, args);
        }
 
        Object result = new Integer(-1);
        try {
            // QI is a special case not to intercept
            if(m.getName().equalsIgnoreCase("QueryInterface")){
                result = m.invoke(obj, args);
                String intfName = (String) args[0].toString();
                 if((intfName.equalsIgnoreCase("OpenCOM.IConnections"))||
                        (intfName.equalsIgnoreCase("OpenCOM.IMetaInterface"))||
                        (intfName.equalsIgnoreCase("OpenCOM.ILifeCycle"))){
                           
                 }
                 else{
                    if (result!=null){
                        // Ensure the QI passes back the proxy object not the original component
                       Delegator del = (Delegator) pOCMIMetaInterception.GetDelegator((IUnknown) proxy, args[0].toString());
                       if(del!=null){
                           result = del.HigherObject;
                       }
                    }
                    else
                        result=null;
                 }
            }
            else{
                // Invoke each of the pre-methods in order (list traversal)
                for(int i=0; i<PreMethods.size();i++){
                    methodList pre = (methodList) PreMethods.elementAt(i);
                    Object[] params = new Object[2];
                    params[0] = m.getName();
                    params[1] = args;
                    Integer res = (Integer) pre.method.invoke(pre.object, params);
                    if(res.intValue()!=0)
                        throw new Exception("PreMethod halted invocation");
                }
                // Invoke the actual method
                Exception postExceptionParameter=null;
                try{
                    result = m.invoke(obj, args);
                }
                catch(Exception e){
                    postExceptionParameter = e;
                    e.printStackTrace();
                }
                 // Invoke each of the post-methods in order (list traversal)
                 for(int i=0; i<PostMethods.size();i++){
                    methodList post = (methodList) PostMethods.elementAt(i);
                    Object[] params = new Object[4];
                    params[0] = m.getName();
                    params[1] = result;
                    params[2] = args;
                    params[3] = postExceptionParameter;
                    result = post.method.invoke(post.object, params);
                }
            }
        } catch (InvocationTargetException e) {
             throw e.getTargetException();
        } catch (Exception e) {
                if(e.getMessage().equalsIgnoreCase("PreMethod halted invocation")){
                    System.out.println("A pre-method has prevented the original method from being invoked");
                    return null;
                }
                throw new RuntimeException("unexpected invocation exception: " +e.getMessage());
         } 
         return result;
    }
    
    //! Implements IDelegator interface of OpenCOM
    
    public boolean addPreMethod(Object Interceptorobject, String methodName) {
        if(!InterceptionEnabled)
            InterceptionEnabled=true;
        
        // Extract the method off the Interceptor object
        Class cls = Interceptorobject.getClass();
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = Object[].class;
        try{
            Method methodPre = cls.getMethod(methodName, parameterTypes);
            methodList val = new methodList(methodPre, Interceptorobject, methodName);

            PreMethods.add(val);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }
    
    public boolean addPostMethod(Object interceptorObject, String methodName) {
        if(!InterceptionEnabled)
            InterceptionEnabled=true;
        
        Class cls = interceptorObject.getClass();
        Class[] parameterTypes = new Class[4];
        parameterTypes[0] = String.class;
        parameterTypes[1] = Object.class;
        parameterTypes[2] = Object[].class;
        parameterTypes[3] = Exception.class;
        try{
            Method methodPost = cls.getMethod(methodName, parameterTypes);
            methodList val = new methodList(methodPost, interceptorObject, methodName);

            PostMethods.add(val);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }
    
    
    public boolean delPostMethod(String methodName) {
        for(int i=0; i<PostMethods.size();i++){
            if(((methodList)PostMethods.elementAt(i)).name.equalsIgnoreCase(methodName)){
                PostMethods.remove(i);
                return true;
            }
        }
        if((PostMethods.size()==0)&&(PreMethods.size()==0))
            InterceptionEnabled=false;
        return false;
    }
    
    public boolean delPreMethod(String methodName) {
        for(int i=0; i<PreMethods.size();i++){
            if(((methodList)PreMethods.elementAt(i)).name.equalsIgnoreCase(methodName)){
                PreMethods.remove(i);
                return true;
            }
        }
        if((PostMethods.size()==0)&&(PreMethods.size()==0))
            InterceptionEnabled=false;
        return false;
    }
    

    public long viewPostMethods(String[] methodNames) {
        int i=0;
        for(i=0; i<PostMethods.size();i++){
            methodNames[i] = ((methodList)PostMethods.elementAt(i)).name;
        }
        return i;
    }
    
    public long viewPreMethods(String[] methodNames) {
        int i=0;
        for(i=0; i<PreMethods.size();i++){
            methodNames[i] = ((methodList)PreMethods.elementAt(i)).name;
        }
        return i;
    }
        
    public boolean SetAttributeValue(String Name, String Type, Object Value){
        TypedAttribute newAtrr = new TypedAttribute(Type, Value);
        MetaData.put(Name, newAtrr);
        return true;
    }
    
    public TypedAttribute GetAttributeValue(String Name){
        return (TypedAttribute) MetaData.get(Name);
    }
    
    /**
     * This method returns all name-value meta-data pairs on this interface instance.
     * @return A Hashtable storing the pairs.
     */
    public Hashtable<String, TypedAttribute> getValues() {
        return MetaData;
    }
    
}
