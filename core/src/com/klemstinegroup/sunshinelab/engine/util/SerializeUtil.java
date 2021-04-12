package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.BaseObject;
import com.klemstinegroup.sunshinelab.engine.objects.ScreenObject;
import com.klemstinegroup.sunshinelab.engine.objects.SerialInterface;

import java.lang.reflect.InvocationTargetException;

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
            try {
                arrabo.add((BaseObject) ClassReflection.forName(jv.getString("class")).getMethod("deserialize",JsonValue.class).invoke(null,jv));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ReflectionException e) {
                e.printStackTrace();
            }
        }
        Statics.userObjects.clear();
        Statics.userObjects.addAll(arrabo);
    }

    public static JsonValue serializeScene() {
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        JsonValue array = new JsonValue(JsonValue.ValueType.array);
        val.addChild("userObjects", array);
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof SerialInterface) {
                System.out.println("adding:"+bo.getClass());
                array.addChild(((SerialInterface) bo).serialize());
            }
        }
        return val;
    }

    public static <T> T deserialize(JsonValue val, Class<T> t) {
        return json.fromJson(t, val.toJson(JsonWriter.OutputType.json));
    }

    public static <T> T copy(SerialInterface si) {
        JsonValue temp = si.serialize();
        System.out.println(temp.toJson(JsonWriter.OutputType.json));
        try {
            return (T) ClassReflection.forName(si.getClass().getName()).getMethod("deserialize",JsonValue.class).invoke(null,temp);
        } catch (ReflectionException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
