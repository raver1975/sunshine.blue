package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.FontObject;
import com.klemstinegroup.sunshinelab.engine.objects.RectTextureObject;

public class SunshineLab extends ApplicationAdapter {

    //    Camera camera;

    Viewport viewport;
    Matrix4 mx4Batch = new Matrix4();

    RectTextureObject badlogic1;
    RectTextureObject badlogic2;
    FontObject fontObject1;
    FontObject fontObject2;

    @Override
    public void create() {
//        img = new Texture("badlogic.jpg");
        badlogic1 = new RectTextureObject(new Texture("badlogic.jpg"));
        badlogic2 = new RectTextureObject(new Texture("badlogic.jpg"));
        fontObject1 = new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 50);
        fontObject2 = new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 50);
        fontObject2.setText("er433434343yeghhghgt");

        mx4Batch = Statics.batch.getTransformMatrix().cpy();
//        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport();

//        generator.dispose(); // don't forget to dispose to avoid memory leaks!

    }

    int x = 0;
    int y = 0;
    int d = 0;
    FileHandle fh;

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        viewport.getCamera().update();
//        batch.setProjectionMatrix(viewport.getCamera().combined);
        viewport.apply();

        Statics.batch.setProjectionMatrix(viewport.getCamera().combined);
        Statics.batch.begin();

        badlogic1.draw(Statics.batch);
        badlogic1.position.translate(new Vector3(-.1f, -.1f, 0));
        badlogic1.position.rotate(10);
        badlogic1.position.setScale(badlogic1.position.getScale() - .001f);

        badlogic2.draw(Statics.batch);
        badlogic2.position.rotate(-1);

        fontObject1.draw(Statics.batch);
        fontObject1.position.rotate(.1f);
        fontObject1.position.setScale(fontObject1.position.getScale() + .001f);

          fontObject2.draw(Statics.batch);
        fontObject2.position.rotate(-.1f);
        fontObject2.position.setScale(fontObject2.position.getScale() - .002f);

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
}
