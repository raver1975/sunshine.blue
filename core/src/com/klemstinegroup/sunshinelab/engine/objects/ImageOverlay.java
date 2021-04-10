package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class ImageOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final TextArea ta;
    Touchable touchable;

    public ImageOverlay() {
        stage = new Stage(Statics.overlayViewport);
        Skin skin = new Skin(Gdx.files.internal("skins/comic/skin/comic-ui.json"));

        CheckBox exitButton = new CheckBox("", skin);
        exitButton.setSize(150, 150);

        exitButton.setChecked(true);
        exitButton.setDisabled(true);
        exitButton.getStyle().fontColor = Color.RED;
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 40, Statics.overlayViewport.getWorldHeight() - 40);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.backOverlay();
            }
        });
        stage.addActor(exitButton);

        ta = new TextArea("", skin);
        TextField.TextFieldListener tfl = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == 13) {
//                    pasteButton.setVisible(false);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    Gdx.app.log("ta", ta.getText());
                    ImageObject bg = new ImageObject(ta.getText().replaceAll("\n", ""));
                    if (bg != null) {
                        Statics.userObjects.add(bg);
                    }
                    ta.setText("");
                }
            }
        };
        ta.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                ta.setText(Gdx.app.getClipboard().getContents());
                tfl.keyTyped(ta, (char) 13);
                return true;// super.longPress(actor, x, y);
            }

        });
//        Actor pasteButton=new TextButton("paste",skin);
        ta.setTextFieldListener(tfl);

        ta.setPosition(10, 10);
        ta.setWidth(Statics.overlayViewport.getWorldWidth());
        stage.addActor(ta);
        stage.setKeyboardFocus(ta);

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
        if (touchable != null) Statics.im.addProcessor(touchable);
        stage.setKeyboardFocus(ta);
    }

    @Override
    public void removeInput() {
        Statics.im.removeProcessor(stage);
        if (touchable != null) Statics.im.removeProcessor(touchable);
    }

    @Override
    public void act() {
        stage.act();
    }
}
