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
    Vector3 touchdown=new Vector3();


    @Override
    public void create() {
//        img = new Texture("badlogic.jpg");

        Statics.objects.add(new RectTextureObject("i.redd.it/0h1nbwj4bto61.jpg"));
        Statics.objects.add(new RectTextureObject("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png"));
        ((ScreenObject)Statics.objects.get(1)).translate(500,500);
        for (int i=-4;i<4;i++) {
            for (int j = -4; j < 4; j++) {
                FontObject fo=new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], MathUtils.random(30,60));
                fo.setPosition((i-1)*150-fo.getCenter().x,(j-1)*100-fo.getCenter().y);
                Statics.objects.add(fo);

            }
        }
        mx4Batch = Statics.batch.getTransformMatrix().cpy();
//        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport();
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
        boolean flip=true;
        for (BaseObject bo : Statics.objects) {

            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(Statics.batch);
                ((ScreenObject)bo).rotate(flip?.2f:-.2f);
                flip=!flip;
//                ((ScreenObject)bo).rotate(MathUtils.random(-.2f,2f));
//                ((ScreenObject)bo).setScale(((ScreenObject)bo).getScale()+MathUtils.random(-.01f,.01f));
            }
        }
        Statics.batch.end();
        Statics.batch.setTransformMatrix(mx4Batch);
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
        Gdx.app.log("Screenxy",screenX+","+screenY);
        viewport.unproject(touchdown.set(screenX,screenY,0));
        Gdx.app.log("worldxy",touchdown.x+","+touchdown.y);

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
