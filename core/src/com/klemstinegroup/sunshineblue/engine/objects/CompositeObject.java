package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.data.ScreenData;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;
import com.klemstinegroup.sunshineblue.engine.overlays.Touchable;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;

public class CompositeObject extends ScreenObject implements Drawable, Touchable {
    public Array<BaseObject> objects = new Array<>();

    public CompositeObject(Array<BaseObject> objects){
        this.objects.addAll(objects);
    }

    @Override
    public void recenter(Vector2 touchdragcpy) {
        super.recenter(touchdragcpy);
        for (BaseObject bo:objects) {
            if (bo instanceof ScreenObject) {
                ((ScreenObject) bo).recenter(touchdragcpy);
            }
        }
    }

    @Override
    public void transform(Vector2 posDelta, float rotDelta, float scaleDelta) {
        super.transform(posDelta,rotDelta,scaleDelta);
        for (BaseObject bo:objects) {
            if (bo instanceof ScreenObject) {
                ((ScreenObject) bo).transform(posDelta, rotDelta, scaleDelta);
            }
        }
    }

    @Override
    public void invtransform(Vector2 posDelta, float rotDelta, float scaleDelta) {
        super.invtransform(posDelta,rotDelta,scaleDelta);
        for (BaseObject bo:objects) {
            if (bo instanceof ScreenObject) {
                ((ScreenObject) bo).invtransform(posDelta, rotDelta, scaleDelta);
            }
        }
    }

    @Override
    public void draw(Batch batch, float delta,boolean bounds) {
        for (BaseObject bo : objects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(batch, delta, bounds);
            }
        }
    }

    @Override
    public boolean isSelected(Vector2 point) {
        boolean selected = false;
        for (BaseObject bo : objects) {
            if (bo instanceof Touchable) {
                selected = selected | ((Touchable) bo).isSelected(point);
            }
        }
        return selected;
    }

    @Override
    public boolean isSelected(Polygon gon) {
        boolean selected = false;
        for (BaseObject bo : objects) {
            if (bo instanceof Touchable) {
                selected = selected | ((Touchable) bo).isSelected(gon);
            }
        }
        return selected;
    }

    @Override
    public void setBounds() {
        for (BaseObject bo : objects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).setBounds();
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public JsonValue serialize() {
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        JsonValue arr = new JsonValue(JsonValue.ValueType.array);
        val.addChild("array",arr);
        for (BaseObject bo:objects) {
            arr.addChild(bo.serialize());
        }
        val.addChild("class", new JsonValue(CompositeObject.class.getName()));
        return val;
    }


    public static void deserialize(JsonValue json) {
        JsonValue val=json.get("array");
        if (val.isArray()){
            for (JsonValue cal:val){
                try {
                    ClassReflection.getMethod(ClassReflection.forName(cal.getString("class")), "deserialize", JsonValue.class).invoke(null, cal);
                } catch (ReflectionException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void regenerate(AssetManager assetManager) {
        super.regenerate(assetManager);
        for (BaseObject bo:objects){
            bo.regenerate(assetManager);
        }
    }
}
