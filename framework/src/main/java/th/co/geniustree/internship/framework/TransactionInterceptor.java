/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.internship.framework;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 *
 * @author pramoth
 */
public class TransactionInterceptor implements MethodInterceptor{

    @Override
    public Object intercept(Object o, Method method, Object[] os, MethodProxy mp) throws Throwable {
        System.out.println("--------------DO BEGIN TRANSACTION--------------------");
        Object invokeSuper = mp.invokeSuper(o, os);
        System.out.println("--------------DO COMMIT TRANSACTION--------------------");
        return invokeSuper;
    }
    
}
