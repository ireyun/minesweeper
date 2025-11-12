package com.minesweeper.game.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * boolean[][]数组转换器
 */
@Converter
public class BooleanArray2DConverter implements AttributeConverter<boolean[][], String> {
    @Override
    public String convertToDatabaseColumn(boolean[][] attribute) {
        if (attribute == null) {
            return null;
        }
        return JsonConverter.array2DToJson(attribute);
    }

    @Override
    public boolean[][] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return JsonConverter.jsonToBooleanArray2D(dbData);
    }
}

