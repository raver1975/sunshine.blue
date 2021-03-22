package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.objexts.RectTexture;

public class SunshineLab extends ApplicationAdapter {
    Batch batch;
    //    Camera camera;

    Viewport viewport;
    BitmapFont font12;
    FreeTypeFontGenerator generator;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    Matrix4 mx4Font = new Matrix4();
    Matrix4 mx4Batch = new Matrix4();
    private FileHandle[] fontList;

    RectTexture badlogic;

    @Override
    public void create() {
//        img = new Texture("badlogic.jpg");
        badlogic=new RectTexture(new Texture("badlogic.jpg"));

        batch = new PolygonSpriteBatch();
        mx4Batch = batch.getTransformMatrix().cpy();
//        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new ScreenViewport();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/42-Regular.ttf"));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        parameter.color = new Color(0f, 1f, 0f, 1f);
        font12 = generator.generateFont(parameter); // font size 12 pixels
        FileHandle dirHandle = Gdx.files.internal("fonts");
        fontList = dirHandle.list();
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

        if (Math.random() > .5f) {
            parameter.size += 1f;
        } else {
            parameter.size -= 1f;
        }
        parameter.color = new Color(1f, 1f, 0f, 1f);
        if (d % 1 == 0) {
            generator.dispose();
            fh=fontList[MathUtils.random(fontList.length - 1)];
            generator = new FreeTypeFontGenerator(fh);
        }
        try {
            font12.dispose();
        } catch (Exception e) {
            e.printStackTrace();
//            generator.dispose();
//            generator = new FreeTypeFontGenerator(fontList[MathUtils.random(fontList.length - 1)]);
//            font12 = generator.generateFont(parameter);
        }
        try {
            font12 = generator.generateFont(parameter);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(fh.name());
//            generator.dispose();
//            generator = new FreeTypeFontGenerator(fontList[MathUtils.random(fontList.length - 1)]);
//            font12 = generator.generateFont(parameter);
            try {
                generator.dispose();
            }
            catch (Exception e1){

            }

                generator = new FreeTypeFontGenerator(fontList[MathUtils.random(fontList.length - 1)]);
            return;
        }
        batch.setProjectionMatrix(viewport.getCamera().combined);
//        viewport.setScreenPosition(x++,y++);
        batch.begin();
//        batch.draw(img, 0, 0);
        badlogic.draw(batch);
        badlogic.translate(new Vector3(-1f,-1f,0));
        badlogic.rotate(10);
        mx4Font.setToRotation(new Vector3(0, 0, 1), d++);
        batch.setTransformMatrix(mx4Font);
        font12.draw(batch, "hello", 10, 10);
        batch.end();
        batch.setTransformMatrix(mx4Batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
