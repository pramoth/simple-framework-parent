/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.internship.framework;

import th.co.geniustree.internship.framework.annotation.Service;
import th.co.geniustree.internship.framework.annotation.PostConstruct;
import th.co.geniustree.internship.framework.annotation.Transactional;
import th.co.geniustree.internship.framework.annotation.AutoWired;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author pramoth
 */
public class Application {

    public static void run(Class clazz) throws Exception {
        /** configration for scan class path **/
        ConfigurationBuilder config = new ConfigurationBuilder()
                .addScanners(new FieldAnnotationsScanner())
                .addScanners(new MethodAnnotationsScanner())
                .addUrls(ClasspathHelper.forClass(clazz, clazz.getClassLoader()));
        Reflections reflections = new Reflections(config);
        
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Service.class);
        
        // create manage bean and assign TransactionInterception if found @transactional
        Map<Class, Object> managedObject = typesAnnotatedWith.stream().map(c -> {
            Enhancer e = new Enhancer();
            if (c.getAnnotation(Transactional.class) != null) {
                e.setCallback(new TransactionInterceptor());
            }else{
                e.setCallback(new NoOpCallBack());
            }
            e.setSuperclass(c);
            return new Object[]{c, e};
        }).collect(Collectors.toMap(c -> (Class)c[0], b -> ((Enhancer) b[1]).create()));
        
        //Inject bean for @Autowired
        Set<Field> fieldsAnnotatedWith = reflections.getFieldsAnnotatedWith(AutoWired.class);
        fieldsAnnotatedWith.forEach(f -> {
            Object sourceType = managedObject.get(f.getType());
            Object targetObject = managedObject.get(f.getDeclaringClass());
            if (sourceType == null) {
                throw new IllegalStateException("not found bean of type => " + f.getType());
            }
            f.setAccessible(true);
            try {
                f.set(targetObject, sourceType);
            } catch (Exception ex) {
                throw new IllegalStateException("can not inject bean of type" + sourceType.getClass() + " to instance of class " + targetObject.getClass(), ex);
            }
        });
        
        // Call @PostConstruct
        reflections.getMethodsAnnotatedWith(PostConstruct.class).forEach(m -> {
            Object object = managedObject.get(m.getDeclaringClass());
            if (object == null) {
                throw new IllegalStateException("not found manage bean of type => " + m.getDeclaringClass());
            }
            try {
                m.invoke(object, null);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        });
    }

}
