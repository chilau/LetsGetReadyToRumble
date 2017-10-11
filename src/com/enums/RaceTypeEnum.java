package com.enums;


import java.util.HashMap;


public class RaceTypeEnum {

    public static final int TYPE_A = 0;
    public static final int TYPE_B = 1;

    protected static HashMap<Integer, String> _dictionary;


    public static HashMap<Integer, String> getDictionary() {
        if (_dictionary == null) {
            _dictionary = new HashMap<>();

            _dictionary.put(TYPE_A, "Тип А");
            _dictionary.put(TYPE_B, "Тип Б");
        }

        return _dictionary;
    }

}
