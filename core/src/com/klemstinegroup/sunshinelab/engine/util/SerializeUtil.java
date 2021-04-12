package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.BaseObject;
import com.klemstinegroup.sunshinelab.engine.objects.SerialInterface;

public class SerializeUtil {
    public static Json json = new Json();
    public static JsonReader jsonReader = new JsonReader();

    public static JsonValue serialize(Object o) {
        return jsonReader.parse(json.toJson(o));
    }


    public static void deserializeScene(JsonValue val){
        Array<BaseObject> arrabo=new Array<>();
        JsonValue array=val.get("userObjects");
        for (int i=0;i<array.size;i++){
                JsonValue jv=array.get(i);
            Gdx.app.log("scene",jv.toJson(JsonWriter.OutputType.minimal));
            try {
                arrabo.add((BaseObject) ClassReflection.getMethod(ClassReflection.forName(jv.getString("class")),"deserialize",JsonValue.class).invoke(null,jv));
            } catch (ReflectionException e) {
                e.printStackTrace();
            }
        }
        Statics.userObjects.clear();
        Statics.userObjects.addAll(arrabo);
    }

    public static JsonValue serializeScene() {
        Gdx.app.log("scene","serializing");
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        JsonValue array = new JsonValue(JsonValue.ValueType.array);
        val.addChild("userObjects", array);
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof SerialInterface) {
                Gdx.app.log("scene","adding:"+bo.getClass());
                array.addChild(((SerialInterface) bo).serialize());
            }
        }
        Gdx.app.log("scene","serialized");
        return val;
    }

    public static <T> T deserialize(JsonValue val, Class<T> t) {
        return json.fromJson(t, val.toJson(JsonWriter.OutputType.json));
    }

    public static <T extends BaseObject> BaseObject copy(T si) {
        Gdx.app.log("copy class",si.getClass().getName());
        JsonValue temp = si.serialize();
//        Gdx.app.log("json",temp.toJson(JsonWriter.OutputType.json));
        try {
            Method method = ClassReflection.getMethod(ClassReflection.forName(si.getClass().getName()), "deserialize", JsonValue.class);
            Gdx.app.log("method",method.getName());
            return (T)method.invoke(null,temp);
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
