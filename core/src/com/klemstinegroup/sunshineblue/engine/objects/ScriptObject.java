package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.igormaznitsa.jjjvm.impl.JJJVMClassImpl;
import com.igormaznitsa.jjjvm.model.JJJVMMethod;
import com.igormaznitsa.jjjvm.model.JJJVMObject;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.overlays.Actable;
import com.klemstinegroup.sunshineblue.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshineblue.engine.util.IPFSFileListener;
import com.klemstinegroup.sunshineblue.engine.util.IPFSUtils;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;

public class ScriptObject extends BaseObject implements Actable {

    private JJJVMClassImpl jjjvmClass;
    private JJJVMObject jjjinstance;
    private JJJVMMethod method;

    public ScriptObject(byte[] data) {
        IPFSUtils.uploadFile(data, new IPFSCIDListener() {
            @Override
            public void cid(String cid) {
                ScriptObject.this.cid=cid;
                processBytes(data);
            }

            @Override
            public void uploadFailed(Throwable t) {
                Statics.exceptionLog("script?", t);
            }
        });
    }


    public ScriptObject(String cid) {
        this.cid = cid;
        SunshineBlue.nativeNet.downloadIPFS(cid, new IPFSFileListener() {
            @Override
            public void downloaded(byte[] file) {
                processBytes(file);
            }

            @Override
            public void downloadFailed(Throwable t) {
                Statics.exceptionLog("script2?", t);
            }
        });
    }

    private void processBytes(byte[] file) {
        try {
            System.out.println(Arrays.toString(file));
            jjjvmClass = new JJJVMClassImpl(new ByteArrayInputStream(file), SunshineBlue.instance.JJVMprovider);
            Map<String, JJJVMMethod> map = jjjvmClass.getAllDeclaredMethods();
            for (Map.Entry<String, JJJVMMethod> e : map.entrySet()) {
                System.out.println(e.getKey() + "\t" + e.getValue().getName() + "\t" + e.getValue().getSignature());
            }
            jjjinstance = jjjvmClass.newInstance(true);
            method = jjjvmClass.findMethod("loop", "(Lcom/klemstinegroup/sunshineblue/SunshineBlue;)V");
            Gdx.app.log("method", method.toString());
            Gdx.app.log("instance", jjjinstance.toString());
        } catch (Throwable throwable) {
            Statics.exceptionLog("construct script error", throwable);
        }
    }


    @Override
    public void act() {
        if (jjjinstance != null && method != null) {
            try {
                method.invoke(jjjinstance, null);
            } catch (
                    Throwable throwable) {
                Statics.exceptionLog("script act:", throwable);
            }
        }
    }

    @Override
    public JsonValue serialize() {
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        val.addChild("CID", new JsonValue(cid));
        val.addChild("class", new JsonValue(ScriptObject.class.getName()));
        return val;
    }

    public static void deserialize(JsonValue json) {
        Gdx.app.log("deserialize", json.toJson(JsonWriter.OutputType.minimal));
        String cid = json.getString("CID");
        Gdx.app.log("cidd:", cid);
        ScriptObject stemp=new ScriptObject(cid);
        stemp.uuid=json.getString("UUID",stemp.uuid);
        SunshineBlue.addUserObj(stemp);
    }
}
