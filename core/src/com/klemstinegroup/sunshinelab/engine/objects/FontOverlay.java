package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class FontOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    Touchable touchable;

    public FontOverlay() {
        Viewport viewport = new FitViewport(800f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight() * Gdx.graphics.getDensity(), 800 * Gdx.graphics.getDensity());
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
        Skin skin = new Skin(Gdx.files.internal("skins/comic/skin/comic-ui.json"));
//        Skin skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));

        CheckBox exitButton = new CheckBox("", skin);
        exitButton.setChecked(true);
        exitButton.setDisabled(true);
        exitButton.getStyle().fontColor = Color.RED;
        exitButton.setPosition(viewport.getScreenWidth() - 40, viewport.getScreenHeight() - 40);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.backOverlay();
            }
        });
        stage.addActor(exitButton);
    }

    public void setTouchable(Touchable touchable) {
        this.touchable = touchable;
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
        stage.draw();
    }

    @Override
    public boolean isSelected(Vector2 touch) {
        return false;
    }

    @Override
    public void setInput() {
        Statics.im.addProcessor(stage);
        if (touchable!=null)Statics.im.addProcessor(touchable);
    }

    @Override
    public void removeInput() {
        Statics.im.removeProcessor(stage);
        if (touchable!=null)Statics.im.removeProcessor(touchable);
    }
}
