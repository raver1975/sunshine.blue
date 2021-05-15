package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.commands.Command;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ImageObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;
import com.klemstinegroup.sunshineblue.engine.overlays.SerialInterface;
import sun.security.provider.Sun;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class SerializeUtil {
    public static Json json = new Json();

    static {
        json.setSerializer(Vector2.class, new Json.Serializer<Vector2>() {

            @Override
            public void write(Json json, Vector2 v, Class knownType) {
                json.writeObjectStart();
                json.writeValue("v", Float.floatToIntBits(v.x) + "," + Float.floatToIntBits(v.y));
                json.writeObjectEnd();
            }

            @Override
            public Vector2 read(Json json, JsonValue jsonData, Class type) {
                Vector2 v = new Vector2();
                String bs = jsonData.child().asString();
                String[] bd = bs.split(",");
                float a = Float.intBitsToFloat(Integer.parseInt(bd[0]));
                float b = Float.intBitsToFloat(Integer.parseInt(bd[1]));
                v.set(a, b);
                return v;
            }
        });
    }

    public static JsonReader jsonReader = new JsonReader();

    public static JsonValue serialize(Object o) {
        return jsonReader.parse(json.toJson(o));
    }


    public static void deserializeScene(JsonValue val, boolean merge) {
        Gdx.app.log("scene", val.toJson(JsonWriter.OutputType.minimal));
        if (!merge) {
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

    public static String lastLoaded = "";

    public static void load(String cid, boolean merge) {
        if (cid == null || cid.isEmpty() || !cid.startsWith("Q")) {
            return;
        }
        if (SunshineBlue.instance.autoload && cid.equals(lastLoaded)) {
            return;
        }
        Command.setToFrame(0);
        lastLoaded = cid;

        Gdx.app.log("name:", cid + "\t" + cid);
        SunshineBlue.nativeNet.downloadIPFS(cid, new IPFSFileListener() {
            @Override
            public void downloaded(byte[] file) {
                JsonReader reader = new JsonReader();
                String st = new String(file);

                for (BaseObject bo : SunshineBlue.instance.userObjects) {
                    String nuuid = UUID.randomUUID().toString();
                    st = st.replaceAll(bo.uuid, nuuid);
                }
                JsonValue val = reader.parse(new String(st));
                if (val != null) {
                    Gdx.app.log("val", val.toJson(JsonWriter.OutputType.minimal));
                    JsonValue commandArray = val.get("commands");
                    if (!merge) {
                        SunshineBlue.instance.commands.clear();
                    } /*else {
                        HashMap<String, String> replaceUUID = new HashMap<>();
                        for (BaseObject bo : SunshineBlue.instance.userObjects) {
                            String newuuid = UUID.randomUUID().toString();
                            bo.uuid = newuuid;
                            replaceUUID.put(bo.uuid, newuuid);
                        }
                        for (Map.Entry<Integer, Array<Command>> entry : SunshineBlue.instance.commands.entrySet()) {
                            for (Command c : entry.getValue()) {
                                if (replaceUUID.containsKey(c.actionOnUUID)) {
                                    c.actionOnUUID = replaceUUID.get(c.actionOnUUID);
                                }
                            }
                        }
                    }*/
                    Array<Command> unpacked = new Array<>();
                    if (commandArray != null) {
                        for (int i = 0; i < commandArray.size; i++) {
                            try {
                                Command command = deserialize(commandArray.get(i), Command.class);
                                unpacked.add(command);
                            } catch (Exception e) {
                                Statics.exceptionLog("command error", e);
                            }
                        }
                    }
                    unpacked.sort(new Comparator<Command>() {
                        @Override
                        public int compare(Command o1, Command o2) {
                            if (o1.framePos < o2.framePos) {
                                return -1;
                            } else if (o1.framePos == o2.framePos && o1.arrayPos < o2.arrayPos) {
                                return -1;
                            } else return 1;
                        }
                    });
                    for (Command c : unpacked) {
                        Command.insert(c.framePos, c, Command.getBaseObject(c.actionOnUUID));
                    }
                    SunshineBlue.nativeNet.doneSavingScene(cid, val.getString("screenshot"));
                    try {
                        SunshineBlue.instance.bgColor.set(val.getInt("bgColor"));
                    } catch (Exception e) {
                    }
                    try {
                        SunshineBlue.instance.viewport.getCamera().position.set(val.getFloat("cam_position_x"), val.getFloat("cam_position_y"), val.getFloat("cam_position_z"));
                        ((OrthographicCamera) SunshineBlue.instance.viewport.getCamera()).zoom = val.getFloat("cam_zoom");
                    } catch (Exception e) {
                    }
                    if (!merge) {
                        try {
                            Statics.RECMAXFRAMES = val.getInt("looplength");
                            SunshineBlue.instance.loopEnd = Statics.RECMAXFRAMES;
                        } catch (Exception e) {

                        }
                    }
                    deserializeScene(val, merge);
                }
            }

            @Override
            public void downloadFailed(Throwable t) {
                Statics.exceptionLog("load fail", t);
            }
        });
    }

    public static void save(IPFSCIDListener ipfscidListener) {
        Command.setToFrame(0);
//        SunshineBlue.instance.batch.begin();
        SunshineBlue.instance.takingScreenshot = true;
        Pixmap screenshot = FrameBufferUtils.drawObjectsPix(SunshineBlue.instance.batch, SunshineBlue.instance.viewport, SunshineBlue.instance.userObjects, 400 * SunshineBlue.instance.viewport.getScreenWidth() / SunshineBlue.instance.viewport.getScreenHeight(), 400, true);
        SunshineBlue.instance.takingScreenshot = false;
//        SunshineBlue.instance.batch.end();
        JsonValue val = serializeScene();
        Command.setToFrame(SunshineBlue.instance.frameCount);

        Array<Command> packed = new Array<>();
        for (Map.Entry<Integer, Array<Command>> entry : SunshineBlue.instance.commands.entrySet()) {
            int cnt = 0;
            for (Command c : entry.getValue()) {
                c.framePos = entry.getKey();
                c.arrayPos = cnt++;
                packed.add(c);
            }
        }
        val.addChild("commands", jsonReader.parse(json.toJson(packed)));
        IPFSUtils.uploadPngtoIPFS(screenshot, new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                val.addChild("screenshot", new JsonValue(cid));
                val.addChild("bgColor", new JsonValue(Color.rgb888(SunshineBlue.instance.bgColor)));
                val.addChild("cam_zoom", new JsonValue(((OrthographicCamera) SunshineBlue.instance.viewport.getCamera()).zoom));
                val.addChild("cam_position_x", new JsonValue(SunshineBlue.instance.viewport.getCamera().position.x));
                val.addChild("cam_position_y", new JsonValue(SunshineBlue.instance.viewport.getCamera().position.y));
                val.addChild("cam_position_z", new JsonValue(SunshineBlue.instance.viewport.getCamera().position.z));
                val.addChild("loopLength", new JsonValue(Statics.RECMAXFRAMES));

                SunshineBlue.instance.viewport.getCamera().position.set(val.getFloat("cam_position_x"), val.getFloat("cam_position_y"), val.getFloat("cam_position_z"));
                ((OrthographicCamera) SunshineBlue.instance.viewport.getCamera()).zoom = val.getFloat("cam_zoom");
                //replace uuids
                String out = val.toJson(JsonWriter.OutputType.javascript);

                SunshineBlue.nativeNet.uploadIPFS(out.getBytes(StandardCharsets.UTF_8), new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        if (cid != null && !cid.isEmpty()) {
                            Preferences prefs = Gdx.app.getPreferences("scenes");
//                            prefs.remove("current");
//                            if (!prefs.get().values().contains(cid)) {
//                                prefs.putString(name, cid);
//                            }
                            prefs.putString("current", cid);
                            if (!prefs.get().keySet().contains(cid)) {
                                prefs.putString(cid, val.getString("screenshot"));
                            }
                            prefs.flush();
                            if (!SunshineBlue.instance.otherCIDS.containsKey(cid)) {
                                SunshineBlue.instance.otherCIDS.put(cid, val.getString("screenshot"));
                            }
                            SunshineBlue.nativeNet.doneSavingScene(cid, val.getString("screenshot"));
                            if (ipfscidListener != null) {
                                ipfscidListener.cid(cid);
                            }
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

        for (BaseObject bo : SunshineBlue.instance.userObjects) {
//            if (bo instanceof ScreenObject) {
//                ((ScreenObject) bo).sd.layer = cnt++;
//            }
            Gdx.app.log("scene", "adding:" + bo.getClass());
            JsonValue temp = ((SerialInterface) bo).serialize();
            temp.addChild("UUID", new JsonValue(bo.uuid));
            array.addChild(temp);
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


    public static void infromGWTotherCID(String cids) {
        if (cids != null && !cids.isEmpty() & cids.indexOf(',') > 1) {
            String[] cidsplit = cids.split(",");
            Gdx.app.log("infromGWTothercids", cids);
            SunshineBlue.instance.otherCIDS.put(cidsplit[0], cidsplit[1]);
        }
    }

    public static void infromGWT(String cid) {
        Gdx.app.log("infromGWT", cid);
        SunshineBlue.instance.loadCid = null;
        load(cid, false);
        ImageObject.load(cid);
    }

    public static void save() {
        save(null);
    }

    public static void deserializePixmapPacker(MemoryFileHandle mfh, AtlasDownloadListener listener) {
        new CustomTextureAtlas(new CustomTextureAtlas.TextureAtlasData(mfh, mfh, false), listener);
    }

    public static MemoryFileHandle serializePixmapPacker(PixmapPacker packer, AtlasUploadListener listener) {
        MemoryFileHandle mfh = new MemoryFileHandle("f");
        try {
            new PixmapPackerIO().save(mfh, packer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int[] cnt = {(mfh.children.size)};
        String[] cids = new String[mfh.children.size + 1];
        for (int i = 0; i < mfh.children.size; i++) {
            int finalI = i;
            SunshineBlue.nativeNet.uploadIPFS(mfh.children.getValueAt(i).readBytes(), new IPFSCIDListener() {
                @Override
                public void cid(String cid) {
                    cids[finalI + 1] = cid;
                    --cnt[0];
                    if (cnt[0] == 0) {
                        SunshineBlue.nativeNet.uploadIPFS(mfh.readBytes(), new IPFSCIDListener() {
                            @Override
                            public void cid(String cid) {
                                System.out.println("-----------------");
                                cids[0] = cid;
                                for (String s : cids) {
                                    System.out.println(s);
                                }
                                if (listener != null) {
                                    listener.atlas(new Array<String>(cids));
                                }
                                System.out.println("-----------------");
                            }

                            @Override
                            public void uploadFailed(Throwable t) {

                            }
                        });
                    }
                }

                @Override
                public void uploadFailed(Throwable t) {

                }
            });
        }
        return mfh;
    }


    public static void deserializePixmapPacker(String[] jsoncids, AtlasDownloadListener listener) {
        Pixmap[] pixmaps = new Pixmap[jsoncids.length - 1];
        System.out.println(Arrays.toString(jsoncids));
        SunshineBlue.nativeNet.downloadIPFS(jsoncids[0], new IPFSFileListener() {
            @Override
            public void downloaded(byte[] file) {
                final int[] cnt = {jsoncids.length - 1};
                for (int i = 1; i < jsoncids.length; i++) {
                    int finalI = i;
                    Timer.instance().scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + jsoncids[finalI], new Pixmap.DownloadPixmapResponseListener() {
                                @Override
                                public void downloadComplete(Pixmap pixmap) {
                                    pixmaps[finalI - 1] = pixmap;
                                    cnt[0]--;
                                    System.out.println("loaded pixmap " + (finalI - 1));
                                    if (cnt[0] == 0) {
                                        try {
                                            listener.atlas(new CustomTextureAtlas(new MemoryFileHandle(file), pixmaps, false).getRegions());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void downloadFailed(Throwable t) {

                                    Statics.exceptionLog("su", t);
                                }
                            });
                        }
                    }, 1 * i);
                }
            }

            @Override
            public void downloadFailed(Throwable t) {

            }
        });
    }
}
