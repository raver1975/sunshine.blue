package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.github.tommyettinger.anim8.GifDecoder;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.overlays.Drawable;
import com.klemstinegroup.sunshinelab.engine.overlays.Touchable;
import com.klemstinegroup.sunshinelab.engine.util.MemoryFileHandle;
import com.klemstinegroup.sunshinelab.engine.util.SerializeUtil;

public class AnimatedImageObject extends ScreenObject implements Drawable, Touchable {
    public Animation<TextureRegion> textures;
    public Pixmap pixmap;
    private Polygon polygon;
    private String cid;
    private float stateTime;


    public AnimatedImageObject(byte[] data,boolean gifOrApng) {
        if (gifOrApng){
            GifDecoder gifDecoder = new GifDecoder();
            gifDecoder.read(new MemoryFileHandle(data).read());
            textures =gifDecoder.getAnimation(Animation.PlayMode.LOOP);
            setBound();
        }
    }

    private void setBound() {
        sd.bounds.set(new Vector2(textures.getKeyFrame(0).getRegionWidth(), textures.getKeyFrame(0).getRegionHeight()));
        sd.position.add(-sd.center.x, -sd.center.y);
    }


    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(sd.position.x,  sd.position.y, 0)
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
//                .translate(-x, -y, 0)
//                        .translate(-center.x, -center.y, 0)
        );
        if (textures != null) {
            stateTime += Gdx.graphics.getDeltaTime();
            batch.draw( textures.getKeyFrame(stateTime,true), -sd.center.x,-sd.center.y);

        }

        if (Statics.debug || Statics.selectedObjects.contains(this, true)) {
//            batch.setColor(Color.RED);

            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(0, 0, 15);
        }
        batch.end();
        batch.setTransformMatrix(SunshineLab.mx4Batch);
        batch.begin();

        if (polygon != null) {
            Statics.shapedrawer.setColor(Color.WHITE);
            Statics.shapedrawer.polygon(polygon);
        }

    }


    @Override
    public boolean isSelected(Vector2 touch) {
        polygon = new Polygon(new float[]{0, 0, sd.bounds.x, 0, sd.bounds.x, sd.bounds.y, 0, sd.bounds.y, 0, 0});
        polygon.setOrigin(sd.center.x,sd.center.y);
        polygon.setScale(sd.scale, sd.scale);
        polygon.rotate(sd.rotation);
        polygon.translate(sd.position.x-sd.center.x, sd.position.y-sd.center.y);
//        polygon.translate(-center.x*scale,-center.y*scale);
        return polygon.contains(touch);
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
        JsonValue val=new JsonValue(JsonValue.ValueType.object);
        val.addChild("screenData", SerializeUtil.serialize(sd));
//        MemoryFileHandle mfh=new MemoryFileHandle();
//        IPFSUtils.writePng(pixmap, mfh, null);
//        String data="data:image/png;base64,"+new String(Base64Coder.encode(mfh.ba.toArray()));
        val.addChild("pngCID",new JsonValue(cid));
        val.addChild("class",new JsonValue(AnimatedImageObject.class.getName()));
        return val;
    }

   /* public static AnimatedImageObject deserialize(JsonValue json) {
//        Gdx.app.log("deserialize",json.toJson(JsonWriter.OutputType.minimal));
        ScreenData sd1=SerializeUtil.deserialize(json.get("screenData"),ScreenData.class);
        String cid=json.getString("pngCID");
        AnimatedImageObject io=new AnimatedImageObject(cid);
        io.sd=sd1;
        return io;
    }*/
}
