package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;
import com.klemstinegroup.sunshineblue.engine.overlays.Touchable;

public class CompositeObject extends ScreenObject implements Drawable, Touchable {
    public Array<BaseObject> objects = new Array<>();

    public CompositeObject(Array<BaseObject> objects){
        this.objects.addAll(objects);
    }


    @Override
    public void draw(Batch batch, float delta,boolean bounds) {
        for (BaseObject bo : objects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(batch, delta, bounds);
            }
        }
    }

    @Override
    public boolean isSelected(Vector2 point) {
        boolean selected = false;
        for (BaseObject bo : objects) {
            if (bo instanceof Touchable) {
                selected = selected | ((Touchable) bo).isSelected(point);
            }
        }
        return selected;
    }

    @Override
    public boolean isSelected(Polygon gon) {
        boolean selected = false;
        for (BaseObject bo : objects) {
            if (bo instanceof Touchable) {
                selected = selected | ((Touchable) bo).isSelected(gon);
            }
        }
        return selected;
    }

    @Override
    public void setBounds() {
        for (BaseObject bo : objects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).setBounds();
            }
        }
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
}
