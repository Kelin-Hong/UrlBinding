package com.kelin.library.viewmodel;

import android.content.Context;

import com.google.dexmaker.Code;
import com.google.dexmaker.DexMaker;
import com.google.dexmaker.FieldId;
import com.google.dexmaker.Local;
import com.google.dexmaker.MethodId;
import com.google.dexmaker.TypeId;
import com.kelin.library.data.JsonData;
import com.kelin.library.utils.ClassUtil;
import com.kelin.library.viewmodel.ListItemPresentationModelParent;
import com.kelin.library.viewmodel.PresentationModelParent;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by kelin on 15-1-16.
 */
public class PresentationModelGen {
    private static final String DEX_PATH = "Lcom/project/kelin/urlbinding/viewmodel/%s;";
    private static final String DEX_PACKAGE_NAME = "com.project.kelin.urlbinding.viewmodel.%s";

    public static Object generatePresentationModel(Context context, String className, JsonData jsonData) throws Exception {
        ArrayList<String> fieldNames = new ArrayList<>();
        fieldNames.addAll(jsonData.getJsonPrimary().keySet());
        fieldNames.addAll(jsonData.getListDataHashMap().keySet());
        String classWithPackageName = String.format(DEX_PACKAGE_NAME, className);
        String classGeneratePath = String.format(DEX_PATH, className);
        DexMaker dexMaker = new DexMaker();
        TypeId<? extends PresentationModelParent> fibonacci = TypeId.get(classGeneratePath);
        String fileName = className + "." + "generated";
        TypeId<PresentationModelParent> parentTypeId = TypeId.get(PresentationModelParent.class);
        dexMaker.declare(fibonacci, fileName, Modifier.PUBLIC, parentTypeId);
        generateConstructors(dexMaker, fibonacci, parentTypeId, PresentationModelParent.class);
        generateField(dexMaker, fibonacci, fieldNames);
        generateGetMethod(dexMaker, fibonacci, fieldNames);
        ClassLoader loader = null;
        loader = dexMaker.generateAndLoad(
                context.getClassLoader(), getDataDirectory(context));
        Class<?> fibonacciClass = loader.loadClass(classWithPackageName);
        Object object = ClassUtil.createObject(fibonacciClass, classWithPackageName, null);
        return object;
    }

    public static Object generatePresentationModel(Context context, String className, Class PresentationModelParentClass, JsonData jsonData) throws Exception {
        ArrayList<String> fieldNames = new ArrayList<>();
        fieldNames.addAll(jsonData.getJsonPrimary().keySet());
        fieldNames.addAll(jsonData.getListDataHashMap().keySet());
        String classWithPackageName = String.format(DEX_PACKAGE_NAME, className);
        String classGeneratePath = String.format(DEX_PATH, className);
        DexMaker dexMaker = new DexMaker();
        TypeId<? extends PresentationModelParent> fibonacci = TypeId.get(classGeneratePath);
        String fileName = className + "." + "generated";
        TypeId<PresentationModelParent> parentTypeId = TypeId.get(PresentationModelParentClass);
        dexMaker.declare(fibonacci, fileName, Modifier.PUBLIC, parentTypeId);
        generateConstructors(dexMaker, fibonacci, parentTypeId, PresentationModelParentClass);
        generateField(dexMaker, fibonacci, fieldNames);
        generateGetMethod(dexMaker, fibonacci, fieldNames);
//        generateSetMethod(dexMaker, fibonacci, fieldNames);
        ClassLoader loader = null;
        loader = dexMaker.generateAndLoad(
                context.getClassLoader(), getDataDirectory(context));
        Class<?> fibonacciClass = loader.loadClass(classWithPackageName);
        Object object = ClassUtil.createObject(fibonacciClass, classWithPackageName, null);
        return object;
    }

    public static Object generateListItemPresentationModel(Context context, String className, Set<String> fieldNames) throws Exception {
        String classWithPackageName = String.format(DEX_PACKAGE_NAME, className);
        String classGeneratePath = String.format(DEX_PATH, className);
        DexMaker dexMaker = new DexMaker();
        TypeId<? extends ListItemPresentationModelParent> fibonacci = TypeId.get(classGeneratePath);
        String fileName = className + "." + "generated";
        TypeId<ListItemPresentationModelParent> parentTypeId = TypeId.get(ListItemPresentationModelParent.class);
        dexMaker.declare(fibonacci, fileName, Modifier.PUBLIC, parentTypeId);
        generateConstructors(dexMaker, fibonacci, parentTypeId, ListItemPresentationModelParent.class);
        generateListItemGetMethod(dexMaker, fibonacci, fieldNames);
//        generateListItemSetMethod(dexMaker, fibonacci, fieldNames);
        ClassLoader loader = null;
        loader = dexMaker.generateAndLoad(
                context.getClassLoader(), getDataDirectory(context));
        Class<?> fibonacciClass = loader.loadClass(classWithPackageName);
        Object object = ClassUtil.createObject(fibonacciClass, classWithPackageName, null);
        return object;
    }

    private static void generateGetMethod(DexMaker dexMaker, TypeId<? extends PresentationModelParent> fibonacci, ArrayList<String> fieldNames) {
        for (String fieldName : fieldNames) {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            MethodId<?, String> fib = fibonacci.getMethod(TypeId.STRING, methodName);
            Code code = dexMaker.declare(fib, Modifier.PUBLIC);
            Local<String> result = code.newLocal(TypeId.STRING);
            code.loadConstant(result, "hello urlBinding!!");
            code.returnValue(result);
        }
    }

    private static void generateSetMethod(DexMaker dexMaker, TypeId<? extends PresentationModelParent> fibonacci, ArrayList<String> fieldNames) {
        for (String fieldName : fieldNames) {
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            MethodId<?, Void> fib = fibonacci.getMethod(TypeId.VOID, methodName, TypeId.STRING);
            Code code = dexMaker.declare(fib, Modifier.PUBLIC);
            code.returnVoid();
        }
    }


    private static void generateListItemGetMethod(DexMaker dexMaker, TypeId<? extends ListItemPresentationModelParent> fibonacci, Set<String> fieldNames) {
        for (String fieldName : fieldNames) {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            MethodId<?, String> fib = fibonacci.getMethod(TypeId.STRING, methodName);
            Code code = dexMaker.declare(fib, Modifier.PUBLIC);
            Local<String> result = code.newLocal(TypeId.STRING);
            code.loadConstant(result, "hello urlBinding!!");
            code.returnValue(result);
        }
    }

    private static void generateListItemSetMethod(DexMaker dexMaker, TypeId<? extends ListItemPresentationModelParent> fibonacci, Set<String> fieldNames) {
        for (String fieldName : fieldNames) {
            String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            MethodId<?, Void> fib = fibonacci.getMethod(TypeId.VOID, methodName, TypeId.STRING);
            Code code = dexMaker.declare(fib, Modifier.PUBLIC);
            code.returnVoid();
        }
    }

    private static void generateField(DexMaker dexMaker, TypeId<? extends PresentationModelParent> fibonacci, ArrayList<String> fieldNames) {
        for (String fieldStr : fieldNames) {
            FieldId<?, String> field = fibonacci.getField(TypeId.STRING, fieldStr);
            dexMaker.declare(field, Modifier.PRIVATE, null);
        }
    }

    private static File getDataDirectory(Context context) throws Exception {
        File dexCache = context.getApplicationContext().getDir("dx", Context.MODE_PRIVATE);
        return dexCache;
    }

    private static <T, G extends T> void generateConstructors(DexMaker dexMaker,
                                                              TypeId<G> generatedType, TypeId<T> superType, Class<T> superClass) {
        for (Constructor<T> constructor : getConstructorsToOverwrite(superClass)) {
            if (constructor.getModifiers() == Modifier.FINAL) {
                continue;
            }
            TypeId<?>[] types = classArrayToTypeArray(constructor.getParameterTypes());
            MethodId<?, ?> method = generatedType.getConstructor(types);
            Code constructorCode = dexMaker.declare(method, Modifier.PUBLIC);
            Local<G> thisRef = constructorCode.getThis(generatedType);
            Local<?>[] params = new Local[types.length];
            for (int i = 0; i < params.length; ++i) {
                params[i] = constructorCode.getParameter(i, types[i]);
            }
            MethodId<T, ?> superConstructor = superType.getConstructor(types);
            constructorCode.invokeDirect(superConstructor, null, thisRef, params);
            constructorCode.returnVoid();
        }
    }

    // The type parameter on Constructor is the class in which the constructor is declared.
    // The getDeclaredConstructors() method gets constructors declared only in the given class,
    // hence this cast is safe.
    @SuppressWarnings("unchecked")
    private static <T> Constructor<T>[] getConstructorsToOverwrite(Class<T> clazz) {
        return (Constructor<T>[]) clazz.getDeclaredConstructors();
    }

    private static TypeId<?>[] classArrayToTypeArray(Class<?>[] input) {
        TypeId<?>[] result = new TypeId[input.length];
        for (int i = 0; i < input.length; ++i) {
            result[i] = TypeId.get(input[i]);
        }
        return result;
    }
}
