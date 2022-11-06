package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.data.DrawData;
import com.klemstinegroup.sunshineblue.engine.data.FontData;
import com.klemstinegroup.sunshineblue.engine.data.ScreenData;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;

public class ScreenObject extends BaseObject {
    public ScreenData sd=new ScreenData();

    public void recenter(Vector2 touchdragcpy) {
        touchdragcpy.sub(sd.position);
        sd.position.add(touchdragcpy);
        touchdragcpy.scl(1f / sd.scale);
        touchdragcpy.rotateDeg(-sd.rotation);
        sd.center.add(touchdragcpy);
    }

    public void transform(Vector2 posDelta, float rotDelta, float scaleDelta) {
        sd.position.add(posDelta);
        sd.rotation+=rotDelta;
        sd.scale+=scaleDelta;
    }

    public void invtransform(Vector2 posDelta, float rotDelta, float scaleDelta) {
        sd.position.sub(posDelta);
        sd.rotation-=rotDelta;
        sd.scale-=scaleDelta;
    }

    @Override
    public JsonValue serialize() {
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        val.addChild("screenData", SerializeUtil.serialize(sd));
        val.addChild("class", new JsonValue(DrawObject.class.getName()));
        val.addChild("UUID", new JsonValue(uuid));
        return val;
    }

    public static void deserialize(JsonValue json) {
        ScreenData sd1 = SerializeUtil.deserialize(json.get("screenData"), ScreenData.class);
        ScreenObject ftemp=new ScreenObject();
        ftemp.sd=sd1;
        ftemp.uuid=json.getString("UUID", ftemp.uuid);
        SunshineBlue.addUserObj(ftemp);
//        }
    }
}
