package com.minesweeper.game.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON转换工具类
 * 用于将复杂对象转换为JSON字符串存储到数据库
 */
public class JsonConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象转换为JSON字符串
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * 将JSON字符串转换为对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }

    /**
     * 将JSON字符串转换为List
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            CollectionType listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, listType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to List", e);
        }
    }

    /**
     * 将二维数组转换为JSON字符串
     */
    public static String array2DToJson(int[][] array) {
        return toJson(array);
    }

    /**
     * 将JSON字符串转换为二维int数组
     */
    public static int[][] jsonToIntArray2D(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, int[][].class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to int[][]", e);
        }
    }

    /**
     * 将二维boolean数组转换为JSON字符串
     */
    public static String array2DToJson(boolean[][] array) {
        return toJson(array);
    }

    /**
     * 将JSON字符串转换为二维boolean数组
     */
    public static boolean[][] jsonToBooleanArray2D(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, boolean[][].class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to boolean[][]", e);
        }
    }

    /**
     * 将二维Integer数组转换为JSON字符串
     */
    public static String array2DToJson(Integer[][] array) {
        return toJson(array);
    }

    /**
     * 将JSON字符串转换为二维Integer数组
     */
    public static Integer[][] jsonToIntegerArray2D(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, Integer[][].class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to Integer[][]", e);
        }
    }
}


