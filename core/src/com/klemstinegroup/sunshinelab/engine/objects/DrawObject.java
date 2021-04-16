package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.overlays.Drawable;
import com.klemstinegroup.sunshinelab.engine.overlays.Touchable;
import com.klemstinegroup.sunshinelab.engine.util.SerializeUtil;

public class DrawObject extends ScreenObject implements Drawable, Touchable {
    DrawData dd=new DrawData();
    private final Vector2 touch = new Vector2();
    private Polygon polygon;

    Array<Vector2> currentPath = new Array<>();

    public DrawObject(DrawData dd) {
        this.dd=dd;
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
        Statics.viewport.unproject(touch.set(screenX, screenY));
        touch.sub(sd.position.x,sd.position.y);
        touch.rotateDeg(-sd.rotation);
        touch.scl(1f/sd.scale);
        currentPath = new Array<>();
        dd.path.add(currentPath);
        currentPath.add(touch.cpy());
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Statics.viewport.unproject(touch.set(screenX, screenY));
        touch.sub(sd.position.x-sd.center.x,sd.position.y-sd.center.y);
        touch.rotateDeg(-sd.rotation);
        touch.scl(1f/sd.scale);
        currentPath.add(touch.cpy());
        setbounds();
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Statics.viewport.unproject(touch.set(screenX, screenY));
        touch.sub(sd.position.x-sd.center.x,sd.position.y-sd.center.y);
        touch.rotateDeg(-sd.rotation);
        touch.scl(1f/sd.scale);
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
                        .translate( sd.position.x,  sd.position.y, 0)
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
        );
//        Statics.shapedrawer.setTextureRegion(new TextureRegion(((RectTextureObject)Statics.userObjects.get(0)).texture));
        Statics.shapedrawer.setColor(Color.WHITE);
        if (dd.path.size > 0) {
            for (Array<Vector2> partialPath : dd.path) {
                if (partialPath.size > 1) {
                    Statics.shapedrawer.path(partialPath, 10, true);
                }
            }
        }
        Statics.shapedrawer.setColor(Color.RED);
        Statics.shapedrawer.filledCircle(0, 0, 15);
        batch.end();
        batch.setTransformMatrix(SunshineLab.mx4Batch);
        batch.begin();
        setbounds();
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
        for (Array<Vector2> pa : dd.path) {
            for (Vector2 p : pa) {
                verts.add(p.x, p.y);
            }
        }
        ConvexHull ch = new ConvexHull();
        if (verts.size>=6) {
            polygon = new Polygon(ch.computePolygon(verts, false).toArray());
//        polygon.translate(-center.x,-center.y);
            polygon.setOrigin(0,0);
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
        for (Array<Vector2> subpath:dd.path){
            for (Vector2 vec:subpath){
                vec.sub(touchdragcpy);
            }
        }

    }

    @Override
    public JsonValue serialize() {
        JsonValue val=new JsonValue(JsonValue.ValueType.object);
        JsonValue array=new JsonValue(JsonValue.ValueType.array);
        val.addChild("array",array);
        for (Array<Vector2> ar:dd.path){
            array.addChild(SerializeUtil.serialize(ar));
        }
        val.addChild("class",new JsonValue(DrawObject.class.getName()));
        return val;
    }

    public static void deserialize(JsonValue json) {
        JsonValue array=json.get("array");
        DrawData dd=new DrawData();
        for (int i=0;i<array.size;i++){
            JsonValue subarray=array.get(i);
            Array<Vector2> vecAr=SerializeUtil.deserialize(subarray,Array.class);
            dd.path.add(vecAr);
        }
        Statics.userObjects.add(new DrawObject(dd));
    }
}
