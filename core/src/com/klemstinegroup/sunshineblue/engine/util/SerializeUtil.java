package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ImageObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;
import com.klemstinegroup.sunshineblue.engine.overlays.SerialInterface;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public class SerializeUtil {
    public static Json json = new Json();
    static {
        json.setSerializer(Vector2.class, new Json.Serializer<Vector2>(){

            @Override
            public void write(Json json, Vector2 v, Class knownType) {
                json.writeObjectStart();
                json.writeValue("v", Float.floatToIntBits(v.x)+","+Float.floatToIntBits(v.y));
                json.writeObjectEnd();
            }

            @Override
            public Vector2 read(Json json, JsonValue jsonData, Class type) {
                Vector2 v=new Vector2();
                String bs=jsonData.child().asString();
                String[] bd=bs.split(",");
                float a=Float.intBitsToFloat(Integer.parseInt(bd[0]));
                float b=Float.intBitsToFloat(Integer.parseInt(bd[1]));
                v.set(a,b);
                return v;
            }
        });
    }
    public static JsonReader jsonReader = new JsonReader();

    public static JsonValue serialize(Object o) {
        return jsonReader.parse(json.toJson(o));
    }


    public static void deserializeScene(JsonValue val,boolean merge) {
        Gdx.app.log("scene", val.toJson(JsonWriter.OutputType.minimal));
        if (!merge){
            SunshineBlue.instance.userObjects.clear();
        }
        JsonValue array = val.get("userObjects");
        if (array != null) {
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
        SunshineBlue.instance.userObjects.sort(new Comparator<BaseObject>() {
            @Override
            public int compare(BaseObject o1, BaseObject o2) {
                if (o1 instanceof ScreenObject && o2 instanceof ScreenObject) {
                    return Float.compare(((ScreenObject) o1).sd.layer, ((ScreenObject) o2).sd.layer);
                } else if (o1 instanceof ScreenObject) {
                    return -1;
                } else if (o2 instanceof ScreenObject) {
                    return 1;
                }
                return 0;
            }
        });
    }

    public static void load(String cid,boolean merge) {
        if (cid == null || cid.isEmpty() || !cid.startsWith("Q")) {
            return;
        }
        SunshineBlue.nativeNet.doneSavingScene(cid);
        Gdx.app.log("name:", cid + "\t" + cid);
        SunshineBlue.nativeNet.downloadIPFS(cid, new IPFSFileListener() {
            @Override
            public void downloaded(byte[] file) {
                JsonReader reader = new JsonReader();
                JsonValue val = reader.parse(new String(file));
                if (val!=null) {
                    Gdx.app.log("val", val.toJson(JsonWriter.OutputType.minimal));
                    deserializeScene(val,merge);
                }
            }

            @Override
            public void downloadFailed(Throwable t) {
                Statics.exceptionLog("load fail", t);
            }
        });
    }

    public static void save(String name, IPFSCIDListener ipfscidListener) {
        SunshineBlue.instance.batch.begin();
        Pixmap screenshot = FrameBufferUtils.drawObjectsPix(SunshineBlue.instance.batch, SunshineBlue.instance.viewport, SunshineBlue.instance.userObjects,400*SunshineBlue.instance.viewport.getScreenWidth()/SunshineBlue.instance.viewport.getScreenHeight(),400,true);
        SunshineBlue.instance.batch.end();
        JsonValue val = serializeScene();
        IPFSUtils.uploadPngtoIPFS(screenshot, new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                val.addChild("screenshot", new JsonValue(cid));
                SunshineBlue.nativeNet.uploadIPFS(val.toJson(JsonWriter.OutputType.javascript).getBytes(StandardCharsets.UTF_8), new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        if (cid != null && !cid.isEmpty()) {
                            Preferences prefs = Gdx.app.getPreferences("scenes");
                            prefs.putString(name, cid);
                            prefs.putString("current", cid);
                            prefs.flush();
                            SunshineBlue.instance.otherCIDS.add(cid);
                            if (ipfscidListener != null) {
                                ipfscidListener.cid(cid);
                            }
                            SunshineBlue.nativeNet.doneSavingScene(cid);
                        }
                    }

                    @Override
                    public void uploadFailed(Throwable t) {
                        ipfscidListener.uploadFailed(t);
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
        int cnt = 0;
        for (BaseObject bo : SunshineBlue.instance.userObjects) {
            if (bo instanceof ScreenObject) {
                ((ScreenObject) bo).sd.layer = cnt++;
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
//        if (si instanceof ScreenObject){
//            ((ScreenObject)si).sd.position.add(100,100);
//        }
//        Gdx.app.log("json",temp.toJson(JsonWriter.OutputType.json));
        try {
            Method method = ClassReflection.getMethod(ClassReflection.forName(si.getClass().getName()), "deserialize", JsonValue.class);
            Gdx.app.log("method", method.getName());
            method.invoke(null, temp);
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
    }

    public static void save(String name) {
        save(name, null);
    }


    public static void infromGWTotherCID(String cid){
        Gdx.app.log("infromGWTothercids", cid);
        SunshineBlue.instance.otherCIDS.add(cid);
    }

    public static void infromGWT(String cid) {
        Gdx.app.log("infromGWT", cid);
        SunshineBlue.instance.loadCid=null;
        load(cid,false);
        ImageObject.load(cid);
    }

    public static void save() {
        save("autosave-" + TimeUtils.millis());
    }



}
