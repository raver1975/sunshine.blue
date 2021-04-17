package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;


public class DrawOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    Touchable touchable;

    public DrawOverlay(AssetManager assetManager) {
        stage = new Stage(Statics.overlayViewport);
        assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = assetManager.get("skins/orange/skin/uiskin.json",Skin.class);
        TextButton exitButton = new TextButton("X",skin);
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 55, Statics.overlayViewport.getWorldHeight() - 55);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.backOverlay();
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
        if (touchable!=null) Statics.im.addProcessor(touchable);
    }

    @Override
    public void removeInput() {
        Statics.im.removeProcessor(stage);
        if (touchable!=null)Statics.im.removeProcessor(touchable);
    }

    @Override
    public void act() {
        stage.act();
    }
}
