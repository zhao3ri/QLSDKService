package com.qinglan.sdk.server.common;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonMapper {

    private static final Logger logger = LoggerFactory.getLogger(JsonMapper.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            logger.error("write " + object + " to string exception", e);
        }
        return null;
    }

    public static ObjectMapper getMapper() {
        return new ObjectMapper();
    }

    public static <T> T toObject(String jsonStr, Class<T> classz) {
        try {
            return mapper.readValue(jsonStr, classz);
        } catch (Exception e) {
            logger.error("json string  to " + classz + " exception", e);
        }
        return null;
    }

    public static <T> List<T> stringToList(String jsonStr, Class<T> beanClassz) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, beanClassz);
            return mapper.readValue(jsonStr, javaType);
        } catch (Exception e) {
            logger.error("json string  to  List<" + beanClassz + "> exception", e);
        }
        return null;
    }
}
