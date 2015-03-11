package com.kelin.library.utils;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

public class ClassUtil {

    public static Logger log = Logger.getLogger(ClassUtil.class.getCanonicalName());

    /**
     * @param initargs If arguments count more than 1, all arguments can't be null and all arguments types must be references
     */
    public static <T> T createObject(Class<T> t, String className, Object... initargs) {
        if (initargs == null) {
            return createObject(t, className, null, null);
        }
        Class<?>[] parameterTypes = new Class<?>[initargs.length];
        for (int i = 0; i < initargs.length; i++) {
            parameterTypes[i] = initargs[i].getClass();
        }
        return createObject(t, className, parameterTypes, initargs);
    }

    public static <T> T createObject(Class<T> t, String className, Class<?>[] parameterTypes, Object[] initargs) {
        try {
            Class<?> instanceClass = t;
            int ii=instanceClass.getConstructors().length;
            if (parameterTypes != null && initargs != null) {
                if (parameterTypes.length == initargs.length) {
                    Constructor<?> constructor = instanceClass.getConstructor(parameterTypes);
                    return t.cast(constructor.newInstance(initargs));
                } else {
                    throw new IllegalArgumentException("Argument arrays lengths are not match");
                }
            } else if (parameterTypes == null && initargs == null) {
                return t.cast(instanceClass.newInstance());
            } else {
                throw new IllegalArgumentException("Argument arrays must be both null or both not null");
            }
        } catch (Exception e) {
            int iii=0;
//            log.log(Level.SEVERE, CommonUtils.exceptionStack2String(e));
        }
        return null;
    }
}
