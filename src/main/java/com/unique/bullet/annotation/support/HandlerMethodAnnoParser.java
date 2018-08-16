package com.unique.bullet.annotation.support;

import com.unique.bullet.annotation.MessageProperties;
import com.unique.bullet.annotation.MessageProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class HandlerMethodAnnoParser {

    private final static Logger logger = LogManager.getLogger(HandlerMethodAnnoParser.class);

    /**
     * 解析方法参数注解,获取属性值
     *
     * @param method to invoke method
     * @param args   method args
     * @return 过滤标识key/value
     */
    public static Map<String, String> parseMessageProperty(Method method, Object args[]) {
        Class<?>[] types = method.getParameterTypes();

        if (types == null || types.length == 0) {
            return null;
        }
        Map<String, String> filterPropertyMap = new HashMap<String, String>();
        for (int i = 0; i < types.length; i++) {
            MethodParameter methodParam = new MethodParameter(method, i);
            //为了兼容spring2.5
            Annotation[] paramAnns = (Annotation[]) methodParam.getParameterAnnotations();

            for (Annotation paramAnn : paramAnns) {
                if (MessageProperties.class.isInstance(paramAnn)) {
                    MessageProperties messageProperties = (MessageProperties) paramAnn;
                    MessageProperty[] properties = messageProperties.value();
                    if (properties != null && properties.length > 0) {
                        for (MessageProperty property : properties) {
                            filterPropertyMap.put(property.name(), parseMessagePropertyValue(args[i], property));
                        }
                    }
                } else {
                    if (MessageProperty.class.isInstance(paramAnn)) {
                        MessageProperty property = (MessageProperty) paramAnn;
                        filterPropertyMap.put(property.name(), parseMessagePropertyValue(args[i], property));
                    }
                }
            }

        }
        return filterPropertyMap;
    }

    /**
     * 解析方法参数注解,获取属性值
     * 不使用MethodParameter，用于兼容spring 2.5 版本
     * @param method to invoke method
     * @param args   method args
     * @return 过滤标识key/value
     */
    public static Map<String, String> parseMessagePropertyExt(Method method, Object args[]) {
        Class<?>[] types = method.getParameterTypes();

        if (types == null || types.length == 0) {
            return null;
        }
        Map<String, String> filterPropertyMap = new HashMap<String, String>();
        Annotation[][] annotationArray = method.getParameterAnnotations();
        for (int i = 0; i < types.length; i++) {
            if (i >= annotationArray.length) {
                break;
            }
            Annotation[] paramAnns = annotationArray[i];
            for (Annotation paramAnn : paramAnns) {
                if (MessageProperties.class.isInstance(paramAnn)) {
                    MessageProperties messageProperties = (MessageProperties) paramAnn;
                    MessageProperty[] properties = messageProperties.value();
                    if (properties != null && properties.length > 0) {
                        for (MessageProperty property : properties) {
                            filterPropertyMap.put(property.name(), parseMessagePropertyValue(args[i], property));
                        }
                    }
                } else {
                    if (MessageProperty.class.isInstance(paramAnn)) {
                        MessageProperty property = (MessageProperty) paramAnn;
                        filterPropertyMap.put(property.name(), parseMessagePropertyValue(args[i], property));
                    }
                }
            }

        }
        return filterPropertyMap;
    }

    /**
     * 匹配获取对象参数的属性值
     *
     * @param obj      方法参数
     * @param property 参数注解
     * @return 属性值
     */
    private static String parseMessagePropertyValue(Object obj, MessageProperty property) {
        if (obj == null || property == null) {
            return null;
        }
        try {
            if (String.class.isInstance(obj)) {
                return String.valueOf(obj);
            }
            if (Integer.class.isInstance(obj)) {
                return String.valueOf(obj);
            }
            if (Long.class.isInstance(obj)) {
                return String.valueOf(obj);
            }
            String jxpath = property.jxpath();
            if (StringUtils.isAnyEmpty(jxpath)) {
                return property.value();
            }
            jxpath = jxpath.substring(0, 1).toUpperCase() + jxpath.substring(1);
            Object value = obj.getClass().getMethod("get" + jxpath).invoke(obj);
            return value == null ? null : String.valueOf(value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error(e);
        }
        return property.value();
    }
}
