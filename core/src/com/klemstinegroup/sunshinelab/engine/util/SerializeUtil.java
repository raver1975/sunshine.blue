package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.BaseObject;
import com.klemstinegroup.sunshinelab.engine.objects.SerialInterface;

public class SerializeUtil {
    public static Json json=new Json();
    public static JsonReader jsonReader = new JsonReader();

    public static JsonValue serialize(Object o){
        return jsonReader.parse(json.toJson(o));
    }

    public static void serialize(){
        for (BaseObject bo: Statics.userObjects){

        }

    }

    public static <T> T deserialize(JsonValue val, Class<T> t){
        return json.fromJson(t,val.toJson(JsonWriter.OutputType.minimal));
    }

    public static <T> T copy(SerialInterface si){
        return (T)si.deserialize(si.serialize());
    }

}
