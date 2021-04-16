package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.BaseObject;
import com.klemstinegroup.sunshinelab.engine.objects.ScreenObject;
import com.klemstinegroup.sunshinelab.engine.overlays.SerialInterface;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public class SerializeUtil {
    public static Json json = new Json();
    public static JsonReader jsonReader = new JsonReader();

    public static JsonValue serialize(Object o) {
        return jsonReader.parse(json.toJson(o));
    }


    public static void deserializeScene(JsonValue val) {
        Gdx.app.log("scene", val.toJson(JsonWriter.OutputType.minimal));
        Statics.userObjects.clear();
        JsonValue array = val.get("userObjects");
        for (int i = 0; i < array.size; i++) {
            JsonValue jv = array.get(i);
            Gdx.app.log("scene", jv.toJson(JsonWriter.OutputType.minimal));
            try {
                ClassReflection.getMethod(ClassReflection.forName(jv.getString("class")), "deserialize", JsonValue.class).invoke(null, jv);
            } catch (ReflectionException e) {
                e.printStackTrace();
            }
        }
    }

    public static void load(String name) {
        String cid=Statics.prefs.getString(name);
        if (cid != null) {
            SunshineLab.nativeNet.downloadIPFS(cid, new IPFSFileListener() {
                @Override
                public void downloaded(byte[] file) {
                    JsonReader reader = new JsonReader();
                    JsonValue val = reader.parse(new String(file));
                    deserializeScene(val);
                }

                @Override
                public void downloadFailed(Throwable t) {
                    Statics.exceptionLog("load fail",t);
                }
            });
        }
    }

    public static void save(String name) {
        JsonValue val = serializeScene();
        SunshineLab.nativeNet.uploadIPFS(val.toJson(JsonWriter.OutputType.javascript).getBytes(StandardCharsets.UTF_8), new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Statics.prefs.putString(name, cid);
                        Statics.prefs.putString("current", cid);
                        Statics.prefs.flush();
                    }
                });

            }

            @Override
            public void uploadFailed(Throwable t) {

            }
        });

    }

    public static JsonValue serializeScene() {
        Gdx.app.log("scene", "serializing");
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        JsonValue array = new JsonValue(JsonValue.ValueType.array);
        val.addChild("userObjects", array);
        int cnt=0;
        for (BaseObject bo : Statics.userObjects) {
                if (bo instanceof ScreenObject){
                    ((ScreenObject)bo).sd.layer=cnt++;
                }
                Gdx.app.log("scene", "adding:" + bo.getClass());
                array.addChild(((SerialInterface) bo).serialize());
        }
        Gdx.app.log("scene", "serialized");
        return val;
    }

    public static <T> T deserialize(JsonValue val, Class<T> t) {
        return json.fromJson(t, val.toJson(JsonWriter.OutputType.json));
    }

    public static <T extends BaseObject> void copy(T si) {
        Gdx.app.log("copy class", si.getClass().getName());
        JsonValue temp = si.serialize();
//        Gdx.app.log("json",temp.toJson(JsonWriter.OutputType.json));
        try {
            Method method = ClassReflection.getMethod(ClassReflection.forName(si.getClass().getName()), "deserialize", JsonValue.class);
            Gdx.app.log("method", method.getName());
            method.invoke(null, temp);
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
    }

}
