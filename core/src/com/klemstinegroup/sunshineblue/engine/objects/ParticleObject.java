package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.data.ScreenData;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;
import com.klemstinegroup.sunshineblue.engine.overlays.Touchable;
import com.klemstinegroup.sunshineblue.engine.util.ColorUtil;
import com.klemstinegroup.sunshineblue.engine.util.ParticleUtil;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;
import space.earlygrey.shapedrawer.JoinType;

public class ParticleObject extends ScreenObject implements Drawable, Touchable {

    Vector2 angleCalc = new Vector2();
    float angleRotateAnimAngle = 0;
    public Polygon polygon;
    public String particleFileName;
    public float speed = 1;
    public ParticleEffect particleEffect;
    public Vector2 transformVec = new Vector2();
    Vector3 out1 = new Vector3();
    Vector3 out2 = new Vector3();
    Vector3 out3 = new Vector3();
    Vector3 out4 = new Vector3();


    public ParticleObject(String particleFileName) {
        this(particleFileName, new ScreenData(), 1f);
    }

    public ParticleObject(String particleFileName, ScreenData sd, float speed) {
        this.particleFileName = particleFileName;
        this.sd = sd;
        this.speed = speed;
        regenerate(SunshineBlue.instance.assetManager);
//        sd.center.set(ParticleUtil.particleFiles.get(particleFileName).getBoundingBox().getCenterX(),ParticleUtil.particleFiles.get(particleFileName).getBoundingBox().getCenterY());
    }


    public void setBounds() {
        if (particleEffect == null) return;
        BoundingBox bb = particleEffect.getBoundingBox();
        sd.bounds.set(bb.getWidth(), bb.getHeight());
        bb.getCorner100(out1);
        bb.getCorner000(out2);
        bb.getCorner010(out3);
        bb.getCorner110(out4);
        polygon = new Polygon(new float[]{out1.x, out1.y, out2.x, out2.y, out3.x, out3.y, out4.x, out4.y, out1.x, out1.y});
        polygon.translate(-(bb.getCenterX() + sd.center.x), -(bb.getCenterY() + sd.center.y));
        polygon.setOrigin(0, 0);
    }


    @Override
    public void draw(Batch batch, float delta,boolean bounds) {
        if (particleEffect == null) return;
        transformVec.set(sd.center.x, sd.center.y);
        transformVec.scl(1f / sd.scale);
        transformVec.rotateDeg(-sd.rotation);
        batch.setTransformMatrix(new Matrix4().idt()
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
        );
        if (sd.visible) {
            batch.setColor(Color.WHITE);
            transformVec.set(sd.position);
            transformVec.rotateDeg(-sd.rotation);
            transformVec.scl(1f / sd.scale);
            transformVec.sub(sd.center);
//            transformVec.set(sd.position);
            particleEffect.setPosition(transformVec.x, transformVec.y);
            particleEffect.update(delta * speed);
            if (particleEffect.isComplete()) {
                particleEffect.reset();
            }
            particleEffect.draw(batch);


            setBounds();
            if (bounds) {
                batch.setTransformMatrix(new Matrix4().idt()
                                .translate(sd.position.x, sd.position.y, 0)
                                .rotate(0, 0, 1, sd.rotation)
                                .scale(sd.scale, sd.scale, 1)
                );
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

                if (polygon != null) {
                    SunshineBlue.instance.shapedrawer.setColor(ColorUtil.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(this, true) / ((float) SunshineBlue.instance.userObjects.size - 1)).cpy());
                    SunshineBlue.instance.shapedrawer.polygon(polygon, 5, JoinType.NONE);
                }
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
    public boolean isSelected(Polygon box) {
        setBounds();
        if (polygon != null) {
            box.translate(-sd.position.x, -sd.position.y);
            return Intersector.overlapConvexPolygons(box, polygon);
        }
        return false;
    }

    @Override
    public boolean isSelected(Vector2 touch) {
        setBounds();
        if (polygon != null) {
            return polygon.contains(touch);
        }
        return false;
    }


    @Override
    public JsonValue serialize() {
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        val.addChild("screenData", SerializeUtil.serialize(sd));
        val.addChild("particle", new JsonValue(particleFileName));
        val.addChild("speed", new JsonValue(speed));
        val.addChild("class", new JsonValue(ParticleObject.class.getName()));
        return val;
    }


    public static void deserialize(JsonValue json) {
        String particle = json.getString("particle");
        float speed = json.getFloat("speed");
        ScreenData sd1 = SerializeUtil.deserialize(json.get("screenData"), ScreenData.class);
        ParticleObject ptemp = new ParticleObject(particle, sd1, speed);
        ptemp.uuid = json.getString("UUID", ptemp.uuid);
        SunshineBlue.addUserObj(ptemp);
    }

    @Override
    public void regenerate(AssetManager assetManager) {
        particleEffect = new ParticleEffect();
        particleEffect.setEmittersCleanUpBlendFunction(true);
        particleEffect.load(ParticleUtil.particleFiles.get(particleFileName), ParticleUtil.particleAtlas);
        for (ParticleEmitter pe : particleEffect.getEmitters()) {
            pe.setAttached(false);
        }
        particleEffect.start();
    }

/*//    @Override
    public void transform(Vector2 posDelta, float rotDelta, float scaleDelta) {
        super.transform(posDelta, rotDelta, scaleDelta);
        Array<ParticleEmitter> emitters = particleEffect.getEmitters();
        for (ParticleEmitter emitter : emitters) {
            transformVec.set( emitter.getX()+posDelta.x,  emitter.getY()+posDelta.y);
            transformVec.rotateDeg(rotDelta);
            transformVec.scl(scaleDelta);
            emitter.setPosition(transformVec.x,transformVec.y);
        }
    }*/

}

