package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.overlays.Drawable;
import com.klemstinegroup.sunshineblue.engine.overlays.Touchable;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;
import space.earlygrey.shapedrawer.JoinType;
import sun.security.provider.Sun;

public class DrawObject extends ScreenObject implements Drawable, Touchable {
    DrawData dd = new DrawData();
    private final Vector2 touch = new Vector2();
    private Polygon polygon;
    Vector2 angleCalc = new Vector2();
    float angleRotateAnimAngle = 0;
    Array<Vector2> currentPath = new Array<>();
    private Color color=Color.WHITE;
    private int size=5;

    public DrawObject(DrawData dd) {
        this.dd = dd;
    }

    public DrawObject() {

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
        SunshineBlue.instance.viewport.unproject(touch.set(screenX, screenY));
        touch.sub(sd.position.x, sd.position.y);
        touch.rotateDeg(-sd.rotation);
        touch.scl(1f / sd.scale);
        currentPath = new Array<>();
        dd.path.add(new PathObject(color.cpy(), size, currentPath));
        currentPath.add(touch.cpy());
        return false;
    }

    Vector2 last = new Vector2();

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        SunshineBlue.instance.viewport.unproject(touch.set(screenX, screenY));
        touch.sub(sd.position.x - sd.center.x, sd.position.y - sd.center.y);
        touch.rotateDeg(-sd.rotation);
        touch.scl(1f / sd.scale);


        setBounds();
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        SunshineBlue.instance.viewport.unproject(touch.set(screenX, screenY));
        touch.sub(sd.position.x - sd.center.x, sd.position.y - sd.center.y);
        touch.rotateDeg(-sd.rotation);
        touch.scl(1f / sd.scale);
        if (currentPath.size>0 && currentPath.get(currentPath.size - 1).dst(touch) > 1f && Math.abs(currentPath.get(currentPath.size - 1).y - touch.y)>1f) {
            currentPath.add(touch.cpy());
        }
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
                .translate(sd.position.x, sd.position.y, 0)
                .rotate(0, 0, 1, sd.rotation)
                .scale(sd.scale, sd.scale, 1)
        );
//        Statics.shapedrawer.setTextureRegion(new TextureRegion(((RectTextureObject)Statics.userObjects.get(0)).texture));
        if (dd.path.size > 0) {
            for (PathObject partialPath : dd.path) {
                if (partialPath.size > 1) {
                    SunshineBlue.instance.shapedrawer.setColor(partialPath.color);
                    SunshineBlue.instance.shapedrawer.path(partialPath.path, partialPath.size, JoinType.SMOOTH, true);
                }
            }
        }
        if (SunshineBlue.instance.selectedObjects.contains(this, true)) {
            SunshineBlue.instance.shapedrawer.setColor(Color.RED);
            SunshineBlue.instance.shapedrawer.circle(0, 0, 15, 2);
            angleCalc.set(0, 15);
            angleCalc.rotateDeg(angleRotateAnimAngle += 3);
            SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
            angleCalc.rotateDeg(90);
            SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
            angleCalc.rotateDeg(90);
            SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
            angleCalc.rotateDeg(90);
            SunshineBlue.instance.shapedrawer.line(new Vector2(), angleCalc, 2);
        }
//        batch.end();
//        batch.setTransformMatrix(SunshineBlue.instance.mx4Batch);
        /*batch.begin();
        setBounds();
        if (polygon != null) {
            SunshineBlue.instance.shapedrawer.polygon(polygon);
        }*/
    }

    @Override
    public boolean isSelected(Vector2 touch) {

//        polygon.translate(s);
        setBounds();
//        System.out.println(Arrays.toString(polygon.getTransformedVertices()));
        if (polygon != null) {
            return polygon.contains(touch);
        }
        return false;
    }

    @Override
    public void setBounds() {
        FloatArray verts = new FloatArray();
        for (PathObject pa : dd.path) {
            for (Vector2 p : pa.path) {
                verts.add(p.x, p.y);
            }
        }
        ConvexHull ch = new ConvexHull();
        if (verts.size >= 6) {
            polygon = new Polygon(ch.computePolygon(verts, false).toArray());
//        polygon.translate(-center.x,-center.y);
            polygon.setOrigin(0, 0);
            polygon.setScale(sd.scale, sd.scale);
            sd.bounds.set(polygon.getBoundingRectangle().width, polygon.getBoundingRectangle().height);
//            center.set(bounds.x/2f,bounds.y/2f);
            polygon.rotate(sd.rotation);
            polygon.translate(sd.position.x, sd.position.y);
        }
    }

    @Override
    public void recenter(Vector2 touchdown) {
        Vector2 touchdragcpy = touchdown.cpy();
        super.recenter(touchdragcpy);
        for (PathObject subpath : dd.path) {
            for (Vector2 vec : subpath.path) {
                vec.sub(touchdragcpy);
            }
        }

    }

    @Override
    public JsonValue serialize() {
        JsonValue val = new JsonValue(JsonValue.ValueType.object);
        JsonValue array = new JsonValue(JsonValue.ValueType.array);
        val.addChild("array", array);
        for (PathObject ar : dd.path) {
            array.addChild(SerializeUtil.serialize(ar));
        }
        val.addChild("class", new JsonValue(DrawObject.class.getName()));
        return val;
    }

    public static void deserialize(JsonValue json) {
        JsonValue array = json.get("array");
        DrawData dd = new DrawData();
        for (int i = 0; i < array.size; i++) {
            JsonValue subarray = array.get(i);
            PathObject vecAr = SerializeUtil.deserialize(subarray, PathObject.class);
            dd.path.add(vecAr);
        }
        SunshineBlue.addUserObj(new DrawObject(dd));
    }

    public void setColor(Color color) {
        this.color=color;
    }

    public void setSize(int size) {
        this.size=size;
    }
}
