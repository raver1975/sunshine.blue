package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.igormaznitsa.jjjvm.impl.JJJVMClassFieldImpl;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class DrawObject extends ScreenObject implements Drawable, Touchable {
    Array<Vector2> path = new Array<Vector2>();
    private Vector2 touch=new Vector2();


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
        Statics.viewport.unproject(touch.set(screenX, screenY));
        path.add(touch.cpy());
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Statics.viewport.unproject(touch.set(screenX, screenY));
        path.add(touch.cpy());
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Statics.viewport.unproject(touch.set(screenX, screenY));
        path.add(touch.cpy());
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

    Vector2 temp = new Vector2();

    @Override
    public void draw(Batch batch) {
        if (path.size > 1) {
            temp = path.get(0);
            for (int i = 1; i < path.size; i++) {
                Statics.shapedrawer.line(temp, path.get(i));
                temp = path.get(i);
            }
        }

    }

    @Override
    public boolean isSelected(Vector3 touch) {
        return false;
    }
}
