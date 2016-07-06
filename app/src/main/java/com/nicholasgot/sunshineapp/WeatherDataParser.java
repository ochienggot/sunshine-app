package com.nicholasgot.sunshineapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ngot on 26/03/2016.
 */
public class WeatherDataParser {

    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
        throws JSONException {
        // TODO
        JSONObject jsonObject = new JSONObject(weatherJsonStr);
        System.out.println(jsonObject.get("list"));
        return -1;
    }

}
