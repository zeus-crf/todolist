package com.example.todolist.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.ParameterDescriptor;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Utils {

    public static void copyNonNullProperties(Object source, Object target){
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames(Object source){
        final BeanWrapper props = new BeanWrapperImpl(source);

        PropertyDescriptor[] pds = props.getPropertyDescriptors();

        Set<String> empytNames = new HashSet<>();

        for (PropertyDescriptor pd : pds ){
            Object propsValue = props.getPropertyValue(pd.getName());
            if (propsValue == null){
                empytNames.add(pd.getName());
            }
        }

        String[] result = new String[empytNames.size()];
        return empytNames.toArray(result);
    }


}
