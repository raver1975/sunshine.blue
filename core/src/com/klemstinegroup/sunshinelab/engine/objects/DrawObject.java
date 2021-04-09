package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.igormaznitsa.jjjvm.impl.JJJVMClassFieldImpl;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;

import java.util.Arrays;

public class DrawObject extends ScreenObject implements Drawable, Touchable {
    Array<Array<Vector2>> path = new Array<Array<Vector2>>();
    private final Vector2 touch = new Vector2();
    private Polygon polygon;

    Array<Vector2> currentPath = new Array<>();

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
        touch.sub(position.x,position.y);
        touch.rotateDeg(-rotation);
        touch.scl(1f/scale);
        currentPath = new Array<>();
        path.add(currentPath);
        currentPath.add(touch.cpy());
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Statics.viewport.unproject(touch.set(screenX, screenY));
        touch.sub(position.x,position.y);
        touch.rotateDeg(-rotation);
        touch.scl(1f/scale);
        currentPath.add(touch.cpy());
        setbounds();
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Statics.viewport.unproject(touch.set(screenX, screenY));
        touch.sub(position.x,position.y);
        touch.rotateDeg(-rotation);
        touch.scl(1f/scale);
        currentPath.add(touch.cpy());
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
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(center.x + position.x, center.y + position.y, 0)
                        .rotate(0, 0, 1, rotation)
                        .scale(scale, scale, 1)
//                .translate(-position.x, -position.y, 0)
                        .translate(-center.x, -center.y, 0)
        );
//        Statics.shapedrawer.setTextureRegion(new TextureRegion(((RectTextureObject)Statics.userObjects.get(0)).texture));
        Statics.shapedrawer.setColor(Color.WHITE);
        if (path.size > 0) {
            for (Array<Vector2> partialPath : path) {
                if (partialPath.size > 1) {
                    Statics.shapedrawer.path(partialPath, 10, true);
                }
            }
        }
        Statics.shapedrawer.setColor(Color.GREEN);
        batch.end();
        batch.setTransformMatrix(SunshineLab.mx4Batch);
        batch.begin();
        if (polygon != null) {
            Statics.shapedrawer.polygon(polygon);
        }
    }

    @Override
    public boolean isSelected(Vector2 touch) {

//        polygon.translate(s);
        setbounds();
//        System.out.println(Arrays.toString(polygon.getTransformedVertices()));
        if (polygon != null) {
            return polygon.contains(touch);
        }
        return false;
    }

    private void setbounds() {
        FloatArray verts = new FloatArray();
        for (Array<Vector2> pa : path) {
            for (Vector2 p : pa) {
                verts.add(p.x, p.y);
            }
        }
        ConvexHull ch = new ConvexHull();
        if (verts.size>=6) {
            polygon = new Polygon(ch.computePolygon(verts, false).toArray());
//        polygon.translate(center.x,center.y);
            polygon.setOrigin(center.x, center.y);
            polygon.setScale(scale, scale);
            bounds.set(polygon.getBoundingRectangle().width, polygon.getBoundingRectangle().height);
            polygon.rotate(rotation);
            polygon.translate(position.x, position.y);
        }
    }
}
