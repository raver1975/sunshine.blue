package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Statement;

public class SunshineLab extends ApplicationAdapter implements InputProcessor {

    //    Camera camera;

    Viewport viewport;
    Matrix4 mx4Batch = new Matrix4();
    Vector3 touchdown = new Vector3();


    @Override
    public void create() {
//        img = new Texture("badlogic.jpg");

        Statics.objects.add(new RectTextureObject("i.redd.it/0h1nbwj4bto61.jpg"));
        Statics.objects.add(new RectTextureObject("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png"));
        ((ScreenObject) Statics.objects.get(0)).position.set(-200, -100,0);
        ((ScreenObject) Statics.objects.get(1)).position.set(0, 100,0);

        ((ScreenObject) Statics.objects.get(0)).scale=.1f;
        ((ScreenObject) Statics.objects.get(1)).scale=.4f;
        FontObject fo = null;
        for (int i=-1;i<2;i++) {
            for (int j = -1; j < 2; j++) {
         fo = new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], MathUtils.random(30, 60));
        fo.position.set((i) * 200 - fo.center.x, (j) * 100 - fo.center.y,0);
        fo.scale=.7f;
        Statics.objects.add(fo);
//
            }

        }
        fo.center.set(0,0,0);
        viewport = new ScreenViewport();
        Statics.objects.add(new BasicUIOverlay());
        mx4Batch = Statics.batch.getTransformMatrix().cpy();
//        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(this);


//        generator.dispose(); // don't forget to dispose to avoid memory leaks!

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();

        Statics.batch.setProjectionMatrix(viewport.getCamera().combined);
        Statics.batch.begin();
        boolean flip = true;
        for (BaseObject bo : Statics.objects) {

            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(Statics.batch);
                if (!(bo instanceof Overlay)) {
                    ((ScreenObject) bo).rotation += flip ? .2f : -.2f;
                    ((ScreenObject) bo).scale = flip ? 2f : .5f;
                    flip = !flip;
                }
//                ((ScreenObject)bo).rotate(MathUtils.random(-.2f,2f));
//                ((ScreenObject)bo).setScale(((ScreenObject)bo).getScale()+MathUtils.random(-.01f,.01f));
            }
            Statics.batch.setTransformMatrix(mx4Batch);
        }


        Statics.batch.end();
    }

    @Override
    public void dispose() {
        Statics.batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public boolean keyDown(int keycode) {
        for (BaseObject bo:Statics.objects){
            if (bo instanceof Touchable){
                ((Touchable) bo).keyDown(keycode);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        for (BaseObject bo:Statics.objects){
            if (bo instanceof Touchable){
                ((Touchable) bo).keyTyped(character);
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {


//        Gdx.input.setOnscreenKeyboardVisible(true, Input.OnscreenKeyboardType.URI);
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
}
