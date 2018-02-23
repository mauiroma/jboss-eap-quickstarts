package it.mauiroma.tests;

import org.json.simple.JSONObject;

public class JsonTest {


    public static void main(String[] args) {
        String message;
        JSONObject json = new JSONObject();

        json.put("test1", "value1");

        JSONObject jsonObj = new JSONObject();

        jsonObj.put("id", 0);
        jsonObj.put("name", "testName");
        json.put("test2", jsonObj);
        message = json.toString();
        System.out.println(message);
    }
}
