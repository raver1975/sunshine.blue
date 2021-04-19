package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.ImageObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;


public class ImageOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final TextArea ta;
    Touchable touchable;

    public ImageOverlay(AssetManager assetManager) {
        stage = new Stage(Statics.overlayViewport);
        assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = assetManager.get("skins/orange/skin/uiskin.json", Skin.class);

        TextButton exitButton = new TextButton("X", skin);
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 55, Statics.overlayViewport.getWorldHeight() - 55);
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
        submitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 100, 10);
        submitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                tfl.keyTyped(ta,(char)13);
            }
        });
        stage.addActor(submitButton);

        ta = new TextArea(null, skin, "default");


        TextField tfield = new TextField("JPG,PNG,GIF,IPFS,or DATA url", skin);
        tfield.setDisabled(true);
        tfield.setPosition(10, 110);
        tfield.setWidth(270);
        stage.addActor(tfield);
        ta.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                ta.setText(Gdx.app.getClipboard().getContents());
                return true;// super.longPress(actor, x, y);
            }

        });
//        Actor pasteButton=new TextButton("paste",skin);
        ta.setTextFieldListener(tfl);

        ta.setPosition(10, 10);
        ta.setWidth(Statics.overlayViewport.getWorldWidth() - 100);
        ta.setHeight(100);
//        ta.setMessageText("Accepts http urls for PNG GIF,and JPG. also base64 Data URI's and IPFS addresses");
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
    public void setBounds() {

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
