package com.kelin.library.viewmodel;

import android.content.Context;

import com.google.common.collect.Sets;
import com.google.dexmaker.stock.ProxyBuilder;
import com.kelin.library.data.JsonData;

import org.robobinding.function.Function;
import org.robobinding.function.MethodDescriptor;
import org.robobinding.itempresentationmodel.RefreshableItemPresentationModel;
import org.robobinding.itempresentationmodel.RefreshableItemPresentationModelFactory;
import org.robobinding.property.AbstractGetSet;
import org.robobinding.property.DataSetProperty;
import org.robobinding.property.ListDataSet;
import org.robobinding.property.PropertyDescriptor;
import org.robobinding.property.SimpleProperty;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kelin on 15-1-16.
 */
public class AbstractPresentationModelObjectGen {
    private final static String METHOD_PROPERTY_NAME = "propertyNames";
    private final static String METHOD_CREATE_DATA_SET_PROPERTY = "tryToCreateDataSetProperty";
    private final static String METHOD_DATA_SET_PROPERTY_NAEM = "dataSetPropertyNames";
    private final static String METHOD_CREATE_PROPERTY = "tryToCreateProperty";
    private final static String METHOD_CREATE_FUNCTION = "tryToCreateFunction";
    private final static String METHOD_EVENT_METHODS = "eventMethods";


    public static AbstractPresentationModelParent generateAbstractPresentationModel(final Context context, final JsonData jsonData, final PresentationModelParent presentationModel, Class functionPresentationModelClass) throws Exception {
        final Method[] methods = functionPresentationModelClass.getDeclaredMethods();

        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals(METHOD_PROPERTY_NAME)) {
                    return generatePropertyNames(jsonData);
                } else if (method.getName().equals(METHOD_CREATE_PROPERTY)) {
                    return generateTryToCreateProperty(proxy, (String) args[0], jsonData);
                } else if (method.getName().equals(METHOD_DATA_SET_PROPERTY_NAEM)) {
                    return generateDataSetPropertyNames(jsonData);
                } else if (method.getName().equals(METHOD_CREATE_DATA_SET_PROPERTY)) {
                    return generateTryToCreateDataSetProperty(context, proxy, (String) args[0], jsonData);
                } else if (method.getName().equals(METHOD_CREATE_FUNCTION)) {
                    return tryToCreateFunction((MethodDescriptor) args[0], methods, presentationModel);
                } else if (method.getName().equals(METHOD_EVENT_METHODS)) {
                    return eventMethods(methods);
                }
                Object result = ProxyBuilder.callSuper(proxy, method, args);
                return result;
            }
        };
        return ProxyBuilder.forClass(AbstractPresentationModelParent.class).dexCache(context.getApplicationContext().getDir("dx", Context.MODE_PRIVATE)).handler(handler).constructorArgTypes(PresentationModelParent.class).constructorArgValues(presentationModel).build();
    }

    public static AbstractListItemPresentationModelParent generateAbstractListItemPresentationModel(Context context, final JsonData jsonData, final String listName) throws Exception {
        final ListItemPresentationModelParent listItemPresentationModelParent = (ListItemPresentationModelParent) PresentationModelGen.generateListItemPresentationModel(context, "ListItemModel", jsonData.getListDataHashMap().get(listName).getAllFieldWithName());
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals(METHOD_PROPERTY_NAME)) {
                    return generateListItemPropertyNames(listName, jsonData);
                } else if (method.getName().equals(METHOD_CREATE_PROPERTY)) {
                    return generateTryToCreateListItemProperty(proxy, listItemPresentationModelParent, (String) args[0], jsonData, listName);
                }
                Object result = ProxyBuilder.callSuper(proxy, method, args);
                return result;
            }
        };
        return ProxyBuilder.forClass(AbstractListItemPresentationModelParent.class).dexCache(context.getApplicationContext().getDir("dx", Context.MODE_PRIVATE)).handler(handler).constructorArgTypes(ListItemPresentationModelParent.class).constructorArgValues(listItemPresentationModelParent).build();
    }

    private static Set<String> generatePropertyNames(JsonData jsonData) {
        Set<String> set = Sets.newHashSet();
        for (String name : jsonData.getJsonPrimary().keySet()) {
            set.add(name);
        }
        return set;
    }

    private static Set<String> generateListItemPropertyNames(String listName, JsonData jsonData) {
        Set<String> set = Sets.newHashSet();
        for (String name : jsonData.getListDataHashMap().get(listName).getAllFieldWithName()) {
            set.add(name);
        }
        return set;


    }

    private static SimpleProperty generateTryToCreateListItemProperty(Object proxy, final ListItemPresentationModelParent presentationModelParent, final String name, final JsonData jsonData, final String arrayName) {
        if (jsonData.getListDataHashMap().get(arrayName).getAllFieldWithName().contains(name)) {
            final String key = name;
            Class type = presentationModelParent.getJsonListItem().get(key).getClass();
            PropertyDescriptor descriptor = new PropertyDescriptor(Object.class, type, name, true, true);
            AbstractGetSet<?> getSet = new AbstractGetSet<Object>(descriptor) {
                private Object oldValue;

                @Override
                public Object getValue() {
                    return presentationModelParent.getJsonListItem().get(key);
                }

                @Override
                public void setValue(Object newValue) {
                    String key = name;
                    if (presentationModelParent.getJsonListItem().getItemUri() != null) {
                        if (newValue instanceof String) {
                            if ((Math.abs(((String) newValue).length() - ((String) oldValue).length()) == 1)) {
                                presentationModelParent.getJsonListItem().update(key, newValue);
                            }
                        }
                        presentationModelParent.getJsonListItem().updateAddChangeDB(key, newValue);
                    } else {
                        presentationModelParent.getJsonListItem().update(key, newValue);
                    }
                }

            };
            return new SimpleProperty((AbstractListItemPresentationModelParent) proxy, descriptor, getSet);
        }
        return null;
    }

    private static SimpleProperty generateTryToCreateProperty(Object proxy, final String name, final JsonData jsonData) {
        Class type = jsonData.getJsonPrimary().get(name).getClass();
        PropertyDescriptor descriptor = new PropertyDescriptor(Object.class, type, name, true, true);
        AbstractGetSet<?> getSet = new AbstractGetSet<Object>(descriptor) {
            @Override
            public Object getValue() {
                return jsonData.getJsonPrimary().get(name);
            }

            @Override
            public void setValue(Object newValue) {
                if (jsonData.getJsonPrimary().getmUris().size()>0) {
                    jsonData.getJsonPrimary().updateAndChangeDB(name, newValue);
                } else {
                    jsonData.getJsonPrimary().update(name, newValue);
                }
            }

        };
        return new SimpleProperty((AbstractPresentationModelParent) proxy, descriptor, getSet);
    }

    private static Set<String> generateDataSetPropertyNames(JsonData jsonData) {
        Set<String> set = Sets.newHashSet();
        for (String name : jsonData.getListDataHashMap().keySet()) {
            set.add(name);
        }
        return set;
    }

    private static DataSetProperty generateTryToCreateDataSetProperty(final Context context, Object proxy, final String name, final JsonData jsonData) {
        PropertyDescriptor descriptor = new PropertyDescriptor(Object.class, String.class, name, true, false);
        final List<Integer> index = new ArrayList<Integer>();
        for (int i = 0; i < jsonData.getListDataHashMap().get(name).getSize(); i++) {
            index.add(i);
        }
        AbstractGetSet<?> getSet = new AbstractGetSet<List>(descriptor) {
            @Override
            public List getValue() {
                return jsonData.getListDataHashMap().get(name).getJsonListItems();
            }
        };
        RefreshableItemPresentationModelFactory factory = new RefreshableItemPresentationModelFactory() {
            @Override
            public RefreshableItemPresentationModel create() {
                try {
                    return generateAbstractListItemPresentationModel(context, jsonData, name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        };
        return new DataSetProperty((AbstractPresentationModelParent) proxy, descriptor, new ListDataSet(factory, getSet));
    }

    public static Function tryToCreateFunction(MethodDescriptor methodDescriptor, Method[] methods, final PresentationModelParent presentationModel) {
        for (final Method method : methods) {
            if (methodDescriptor.getName().equals(method.getName())) {
                return new Function() {
                    @Override
                    public Object call(Object... args) {
                        try {
                            if (args.length > 0) {
                                method.invoke(presentationModel, args[0]);
                            } else {
                                method.invoke(presentationModel);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
            }
        }
        return null;
    }

    public static Set<MethodDescriptor> eventMethods(Method[] methods) {
        Set set = new HashSet();
        for (Method method : methods) {
            if (method.getParameterTypes().length <= 0) {
                set.add(new MethodDescriptor(method.getName(), new Class[0]));
            } else {
                set.add(new MethodDescriptor(method.getName(), method.getParameterTypes()));
            }
        }
        return set;
    }


}
