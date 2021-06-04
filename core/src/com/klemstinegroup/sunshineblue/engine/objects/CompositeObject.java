package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.commands.Command;
import com.klemstinegroup.sunshineblue.engine.data.ScreenData;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;
import com.klemstinegroup.sunshineblue.engine.overlays.Touchable;
import com.klemstinegroup.sunshineblue.engine.util.ColorUtil;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;
import space.earlygrey.shapedrawer.JoinType;

public class CompositeObject extends ScreenObject implements Drawable, Touchable {
    public Array<BaseObject> objects = new Array<>();
    private Vector2 angleCalc = new Vector2();
    private int angleRotateAnimAngle;

    public CompositeObject(Array<BaseObject> objects) {
        this.objects.addAll(objects);
    }

    @Override
    public void recenter(Vector2 touchdragcpy) {
//        super.recenter(touchdragcpy);
        for (BaseObject bo : objects) {
            if (bo instanceof ScreenObject) {
                ((ScreenObject) bo).recenter(touchdragcpy);
            }
        }
    }

    @Override
    public void transform(Vector2 posDelta, float rotDelta, float scaleDelta) {
//        super.transform(posDelta, rotDelta, scaleDelta);
        for (BaseObject bo : objects) {
            if (bo instanceof ScreenObject) {
                ((ScreenObject) bo).transform(posDelta, rotDelta, scaleDelta);
            }
        }
    }

    @Override
    public void invtransform(Vector2 posDelta, float rotDelta, float scaleDelta) {
//        super.invtransform(posDelta, rotDelta, scaleDelta);
        for (BaseObject bo : objects) {
            if (bo instanceof ScreenObject) {
                ((ScreenObject) bo).invtransform(posDelta, rotDelta, scaleDelta);
            }
        }
    }

    @Override
    public void draw(Batch batch, float delta, boolean bounds) {
        batch.setTransformMatrix(new Matrix4().idt()
                .translate(sd.position.x, sd.position.y, 0)
                .rotate(0, 0, 1, sd.rotation)
                .scale(sd.scale, sd.scale, 1)
        );
        for (BaseObject bo : objects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(batch, delta, bounds);
            }
        }
        setBounds();
        /*if (bounds) {
            SunshineBlue.instance.shapedrawer.setColor(ColorUtil.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(this, true) / (float) (SunshineBlue.instance.userObjects.size - 1)).cpy());
            SunshineBlue.instance.shapedrawer.circle(0, 0, 10, 2);
            angleCalc.set(0, 10);
            angleCalc.rotateDeg(angleRotateAnimAngle += 1);
            SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
            angleCalc.rotateDeg(90);
            SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
            angleCalc.rotateDeg(90);
            SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
            angleCalc.rotateDeg(90);
            SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);

            *//*if (polygon != null) {
                batch.end();
                batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);
                batch.begin();
//                SunshineBlue.instance.shapedrawer.setColor(Color.WHITE);
                SunshineBlue.instance.shapedrawer.setColor(ColorUtil.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(this, true) / ((float) SunshineBlue.instance.userObjects.size - 1)));
                SunshineBlue.instance.shapedrawer.polygon(polygon,5, JoinType.NONE);
            }*//*
        }*/
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
        val.addChild("array", arr);
        for (BaseObject bo : objects) {
            arr.addChild(bo.serialize());
        }
        val.addChild("class", new JsonValue(CompositeObject.class.getName()));
        val.addChild("UUID", new JsonValue(uuid));
        return val;
    }


    public static void deserialize(JsonValue json) {
        Array<String> uuids = new Array<>();
        JsonValue val = json.get("array");
        if (val.isArray()) {
            for (JsonValue cal : val) {
                try {
                    ClassReflection.getMethod(ClassReflection.forName(cal.getString("class")), "deserialize", JsonValue.class).invoke(null, cal);
                    uuids.add(cal.getString("UUID"));
                } catch (ReflectionException e) {
                    e.printStackTrace();
                }
            }
        }

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Array<BaseObject> tempob = new Array<>();
                for (String s : uuids) {
                    System.out.println(s+"\t"+Command.getBaseObject(s));
                    if (Command.getBaseObject(s) == null) {
                        return;
                    }
                    tempob.add(Command.getBaseObject(s));
                }
                SunshineBlue.addUserObj(new CompositeObject(tempob));
                this.cancel();
            }
        }, .1f, .1f);

    }

    @Override
    public void regenerate(AssetManager assetManager) {
        super.regenerate(assetManager);
        for (BaseObject bo : objects) {
            bo.regenerate(assetManager);
        }
    }
}
