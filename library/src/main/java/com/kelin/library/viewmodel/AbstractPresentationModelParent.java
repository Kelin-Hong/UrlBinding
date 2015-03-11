package com.kelin.library.viewmodel;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.robobinding.function.Function;
import org.robobinding.function.MethodDescriptor;
import org.robobinding.presentationmodel.AbstractPresentationModelObject;
import org.robobinding.property.DataSetProperty;
import org.robobinding.property.SimpleProperty;

import java.util.Map;
import java.util.Set;

public class AbstractPresentationModelParent
        extends AbstractPresentationModelObject {

    public AbstractPresentationModelParent(PresentationModelParent presentationModelParent) {
        super(presentationModelParent);
    }

    @Override
    public Set<String> propertyNames() {
        return Sets.newHashSet();
    }

    @Override
    public Set<String> dataSetPropertyNames() {
        return Sets.newHashSet();
    }

    @Override
    public Set<MethodDescriptor> eventMethods() {
        return Sets.newHashSet();
    }

    @Override
    public Map<String, Set<String>> propertyDependencies() {
        Map<String, Set<String>> dependencies = Maps.newHashMap();
        return dependencies;
    }

    @Override
    public SimpleProperty tryToCreateProperty(String name) {
        return null;
    }

    @Override
    public DataSetProperty tryToCreateDataSetProperty(String name) {
        return null;
    }

    @Override
    public Function tryToCreateFunction(MethodDescriptor methodDescriptor) {
        return null;
    }

}
