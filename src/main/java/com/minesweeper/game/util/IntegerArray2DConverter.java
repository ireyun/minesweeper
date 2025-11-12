package com.minesweeper.game.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Integer[][]数组转换器
 */
@Converter
public class IntegerArray2DConverter implements AttributeConverter<Integer[][], String> {
    @Override
    public String convertToDatabaseColumn(Integer[][] attribute) {
        if (attribute == null) {
            return null;
        }
        return JsonConverter.array2DToJson(attribute);
    }

    @Override
    public Integer[][] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return JsonConverter.jsonToIntegerArray2D(dbData);
    }
}

