package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class BasicUIOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;

    public BasicUIOverlay() {
        Viewport viewport = new FitViewport(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        stage = new Stage(viewport);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)]);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
//        int a = MathUtils.randomBoolean() ? 0 : 1;
//        int b = MathUtils.randomBoolean() ? 0 : 1;
        parameter.color = Color.CYAN;
        textButtonStyle.font = generator.generateFont(parameter);
        textButtonStyle.overFontColor = Color.WHITE;
//        Skin skin=new Skin(Gdx.files.internal("skins/comic/skin/comic-ui.json"));
        Skin skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));
        Actor fontButton = new TextButton("Tt", skin);
//        fontButton.setColor(Color.WHITE);

        fontButton.addListener(new ClickListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.input.setOnscreenKeyboardVisible(true);
//                Statics.objects.add(new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 40));
//            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.input.setOnscreenKeyboardVisible(true);
                FontObject ff = new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 40);
                Statics.objects.add(ff);
                ff.position.set(-ff.center.x,-ff.center.y,0);
//        ((ScreenObject) Statics.objects.get(0)).position.set(-((ScreenObject) Statics.objects.get(0)).bounds.x/2, -((ScreenObject) Statics.objects.get(0)).bounds.y/2, 0);
            }
        });
        stage.addActor(fontButton);
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
    public void draw(Batch batch) {

//        mx4Overlay.set(mx4Overlay.idt());
//        mx4Overlay.setToOrtho2D(0, 0, 100, 100);
//        mx4Overlay.

//        Statics.batch.setProjectionMatrix(mx4Overlay.idt());
        for (int i = 0; i < 10; i++) {
            Statics.shapedrawer.filledCircle(30 * i, 10, 10);
        }
        stage.draw();
    }

    @Override
    public boolean isSelected(Vector2 touch) {
        return false;
    }
}
