package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ImageObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;


public class ImageOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final TextArea ta;

    public ImageOverlay() {
        stage = new Stage(SunshineBlue.instance.overlayViewport);
        SunshineBlue.instance.assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = SunshineBlue.instance.assetManager.get("skins/orange/skin/uiskin.json", Skin.class);

        TextButton exitButton = new TextButton("X", skin);
        exitButton.setPosition(SunshineBlue.instance.overlayViewport.getWorldWidth() - 60, 10);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.backOverlay();
            }
        });
        stage.addActor(exitButton);

        TextField.TextFieldListener tfl = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == 13) {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    Gdx.app.log("ta", ta.getText());
                    String text = ta.getText().replaceAll("\n", "");
                    ImageObject.load(text);
                    ta.setText(null);
                    Overlay.backOverlay();
                }
            }
        };

        TextButton submitButton = new TextButton("Shine!", skin);
        submitButton.setSize(100, 100);
        submitButton.setPosition(SunshineBlue.instance.overlayViewport.getWorldWidth() - 100, SunshineBlue.instance.overlayViewport.getWorldHeight() - 130);
        submitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                tfl.keyTyped(ta, (char) 13);
            }
        });
        stage.addActor(submitButton);

        ta = new TextArea(null, skin, "default");


        Label tfield = new Label("JPG,PNG,GIF,IPFS,or DATA url", skin);
        tfield.setPosition(250, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        tfield.setWidth(270);
        stage.addActor(tfield);


        ta.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                ta.setText(Gdx.app.getClipboard().getContents());

                return true;
            }

        });
        ta.setTextFieldListener(tfl);

        ta.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 130);
        ta.setWidth(SunshineBlue.instance.overlayViewport.getWorldWidth() - 110);
        ta.setHeight(100);

        TextButton clearButton = new TextButton("x", skin);
        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ta.setText("");
            }
        });
        clearButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 130);
        clearButton.setSize(20, 20);

//ta.setZIndex(1);
//        ta.setMessageText("Accepts http urls for PNG GIF,and JPG. also base64 Data URI's and IPFS addresses");
        stage.addActor(ta);
        stage.addActor(clearButton);
        stage.setKeyboardFocus(ta);
    }

//    public void setTouchable(Touchable touchable) {
//        this.touchable = touchable;
//    }

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
    public void draw(Batch batch, float delta,boolean bounds) {
        stage.act();
        stage.draw();
    }

    @Override
    public boolean isSelected(Vector2 touch) {
        return false;
    }

    @Override
    public boolean isSelected(Polygon gon) {
        return false;
    }

    @Override
    public void setBounds() {

    }

    @Override
    public void setInput() {
        SunshineBlue.instance.im.addProcessor(stage);
//        if (touchable != null) Statics.im.addProcessor(touchable);
        stage.setKeyboardFocus(ta);
    }

    @Override
    public void removeInput() {
        SunshineBlue.instance.im.removeProcessor(stage);
//        if (touchable != null) Statics.im.removeProcessor(touchable);
    }

    @Override
    public void setObject(BaseObject bo) {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
