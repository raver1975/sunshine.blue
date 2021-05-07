package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;
import com.klemstinegroup.sunshineblue.engine.overlays.Touchable;
import com.klemstinegroup.sunshineblue.engine.util.ColorHelper;
import com.klemstinegroup.sunshineblue.engine.util.ParticleUtil;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;
import space.earlygrey.shapedrawer.JoinType;

public class ParticleObject extends ScreenObject implements Drawable, Touchable {

    Vector2 angleCalc = new Vector2();
    float angleRotateAnimAngle = 0;
    public Polygon polygon;
    public String particleFileName;
    public float speed=1;
    public ParticleEffect particleEffect;

    public ParticleObject(String particleFileName) {
        this(particleFileName, new ScreenData(),1f);
    }

    public ParticleObject(String particleFileName, ScreenData sd,float speed) {
        this.particleFileName = particleFileName;
        this.sd = sd;
        this.speed=speed;
        regenerate(SunshineBlue.instance.assetManager);
//        sd.center.set(ParticleUtil.particleFiles.get(particleFileName).getBoundingBox().getCenterX(),ParticleUtil.particleFiles.get(particleFileName).getBoundingBox().getCenterY());
    }



    public void setBounds() {
        if (particleEffect == null) return;
//        nn = new GlyphLayout();
//        nn.setText(font, fd.text);
        BoundingBox bb = particleEffect.getBoundingBox();
        sd.bounds.set(bb.getWidth(), bb.getHeight());
        float cx = sd.center.x + bb.getCenterX();
        float cy = sd.center.y + bb.getCenterY();
        float hx = bb.getWidth() / 2 + cx;
        float hy = bb.getHeight() / 2 + cy;
        float lx = -bb.getWidth() / 2 + cx;
        float ly = -bb.getHeight() / 2 + cy;
        polygon = new Polygon(new float[]{lx, ly, hx, ly, hx, hy, lx, hy, lx, ly});
        polygon.setOrigin(sd.center.x, sd.center.y);
        polygon.setScale(sd.scale, sd.scale);
        polygon.rotate(sd.rotation);
        polygon.translate(sd.position.x - sd.center.x, sd.position.y - sd.center.y);
    }


    @Override
    public void draw(Batch batch,float delta) {
        if (particleEffect == null) return;
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(sd.position.x, sd.position.y, 0)
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
//                        .translate(-center.x, -center.y, 0)
        );

        if (sd.visible) {
            batch.setColor(Color.WHITE);
            particleEffect.setPosition(-sd.center.x, -sd.center.y);
            particleEffect.update(delta * speed);
            if (particleEffect.isComplete()) {
                particleEffect.reset();
            }
            particleEffect.draw(batch);
//            font.draw(batch, fd.text, 0 - sd.center.x, +sd.bounds.y - sd.center.y, Float.MAX_VALUE, Align.left, true);


            setBounds();
            if (SunshineBlue.instance.selectedObjects.contains(this, true)) {
                SunshineBlue.instance.shapedrawer.setColor(ColorHelper.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(this, true) / (float) (SunshineBlue.instance.userObjects.size - 1)).cpy().lerp(Color.WHITE, SunshineBlue.instance.colorFlash));
                float radius = 10 + 10 * SunshineBlue.instance.colorFlash;
                SunshineBlue.instance.shapedrawer.circle(0, 0, radius, 2);
                angleCalc.set(0, radius);
                angleCalc.rotateDeg(angleRotateAnimAngle += 1);
                SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
                angleCalc.rotateDeg(90);
                SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
                angleCalc.rotateDeg(90);
                SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
                angleCalc.rotateDeg(90);
                SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);

                if (polygon != null) {
                    batch.end();
                    batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);
                    batch.begin();
//                SunshineBlue.instance.shapedrawer.setColor(Color.WHITE);
                    SunshineBlue.instance.shapedrawer.setColor(ColorHelper.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(this, true) / ((float) SunshineBlue.instance.userObjects.size - 1)).cpy().lerp(Color.WHITE, SunshineBlue.instance.colorFlash));
                    SunshineBlue.instance.shapedrawer.polygon(polygon,5, JoinType.NONE);
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
        SunshineBlue.addUserObj(new ParticleObject(particle, sd1,speed));
    }

    @Override
    public void regenerate(AssetManager assetManager) {
        particleEffect = new ParticleEffect();
        particleEffect.setEmittersCleanUpBlendFunction(true);
        particleEffect.load(ParticleUtil.particleFiles.get(particleFileName), ParticleUtil.particleAtlas);
        particleEffect.start();
    }


}
