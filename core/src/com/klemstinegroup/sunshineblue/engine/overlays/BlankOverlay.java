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


public class BlankOverlay extends ScreenObject implements Overlay, Touchable,Drawable {

    private final BasicUIOverlay bo;
    //    public final Stage stage;
    Touchable touchable;

    public BlankOverlay(BasicUIOverlay bo) {
        this.bo=bo;
    }

    public void setTouchable(Touchable touchable) {
        this.touchable = touchable;
    }

    @Override
    public boolean keyDown(int keycode) {
        Overlay.setOverlay(bo);
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
        System.out.println("Blank overlay touchdown");
        Overlay.setOverlay(bo);
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
    public boolean isSelected(Vector2 touch) {
        return false;
    }

    @Override
    public void setBounds() {

    }

    @Override
    public void setInput() {
    }

    @Override
    public void removeInput() {
    }

    @Override
    public void act() {
    }

    @Override
    public void draw(Batch batch) {

    }
}
