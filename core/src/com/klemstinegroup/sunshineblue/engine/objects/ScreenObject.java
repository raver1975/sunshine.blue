package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.klemstinegroup.sunshineblue.SunshineBlue;

public class ScreenObject extends BaseObject {
    public ScreenData sd = new ScreenData();
    public VfxManager vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
    public VfxFrameBuffer vfxFrameBuffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
    public TextureRegion tr;

    public void recenter(Vector2 touchdragcpy) {
        touchdragcpy.sub(sd.position.x, sd.position.y);
        sd.position.add(touchdragcpy);
        touchdragcpy.scl(1f / sd.scale);
        touchdragcpy.rotateDeg(-sd.rotation);
        sd.center.add(touchdragcpy);
    }

    public void setupTexture() {
        if (!vfxFrameBuffer.isInitialized()) {
            vfxFrameBuffer.initialize(SunshineBlue.instance.viewport.getScreenWidth(), SunshineBlue.instance.viewport.getScreenHeight());
        }
        tr = new TextureRegion(vfxFrameBuffer.getTexture());
        tr.flip(false, true);
    }

    public void startBatch(Batch batch) {
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(sd.position.x, sd.position.y, 0)
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
//                        .translate(-center.x, -center.y, 0)
        );
        batch.begin();
        batch.setColor(Color.WHITE);
    }

    public void endBatch(Batch batch) {
        batch.end();
        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToFbo(vfxFrameBuffer);
    }
}
