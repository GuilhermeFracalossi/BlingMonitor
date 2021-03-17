package org.json;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonWriter
{

    public static void main(JSONObject fullJson)
    {
        try (FileWriter file = new FileWriter("camerasIndexed.json")) {
            file.write(fullJson.toJSONString());
            file.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
