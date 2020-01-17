package de.fzi.dream.ploc.utility;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fzi.dream.ploc.data.structure.Creator;

/**
 * Converter class to transform list objects to strings and backwards for saving it to the
 * SQLite room database.
 *
 * @author Felix Melcher
 */
public class Converters {
    @TypeConverter
    public static List<String> stringToList(String value) {
        return new Gson().fromJson(value, new TypeToken<ArrayList<String>>() {}.getType());
    }

    @TypeConverter
    public static String listToString(List<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<Integer> integerToList(String value) {
        if(!value.contains("null")){
            if(value.contains("[")){
                return new Gson().fromJson(value, new TypeToken<ArrayList<Integer>>(){}.getType());
            } else {
                return Arrays.asList(Integer.valueOf(value));
            }
        }
        return Collections.emptyList();
    }

    @TypeConverter
    public static String listToInteger(List<Integer> list) {
        return new Gson().toJson(list);
    }
}