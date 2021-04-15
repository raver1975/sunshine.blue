package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.overlays.Drawable;
import com.klemstinegroup.sunshinelab.engine.overlays.Touchable;
import com.klemstinegroup.sunshinelab.engine.util.SerializeUtil;

public class FontObject extends ScreenObject implements Drawable, Touchable {

    public FontData fd=new FontData();

    private final FileHandle[] fontList;
    BitmapFont font;

    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private int caretFlash = 0;
    private GlyphLayout nn;
    public Polygon polygon;

    public FontObject(FontData fd, ScreenData sd) {
        fontList = Gdx.files.internal("fonts").list();
        this.fd=fd;
        this.sd=sd;
        generate();
    }

    public void setFont(int index){
        fd.fontName= fontList[index].nameWithoutExtension();
    }
    public void setSize(int size){
        fd.size=size;
        generate();
    }
    public FontObject() {
        fontList = Gdx.files.internal("fonts").list();
        generate();
    }

    public void generate() {
        FileHandle ff = fontList[MathUtils.random(fontList.length - 1)];
        if (fd.fontName==null) {
            fd.fontName=ff.nameWithoutExtension();
        }
        for (FileHandle fh: fontList){
            if (fh.nameWithoutExtension().equals(fd.fontName)){ff=fh;}
        }
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(ff);
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fd.size;
//        int a = MathUtils.randomBoolean() ? 0 : 1;
//        int b = MathUtils.randomBoolean() ? 0 : 1;
        parameter.color = fd.color;
        font = generator.generateFont(parameter);
        generator.dispose();
        setBounds();
        sd.center.set(sd.bounds.x / 2f, sd.bounds.y / 2f);
    }

    public void setBounds() {
        nn = new GlyphLayout();
        nn.setText(font, fd.text);
        sd.bounds.set(nn.width, nn.height);

    }

    public void setText(String text) {
        fd.text = text;
        setBounds();
    }


    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate( sd.position.x, sd.position.y, 0)
                        .rotate(0, 0, 1, sd.rotation)
                        .scale(sd.scale, sd.scale, 1)
//                .translate(-position.x, -position.y, 0)
//                        .translate(-center.x, -center.y, 0)
        );

        font.draw(batch, fd.text, 0-sd.center.x, +sd.bounds.y-sd.center.y, Float.MAX_VALUE, Align.left, true);

        if (Statics.overlay==Statics.FONT_OVERLAY && this ==Statics.FONT_OVERLAY.fontObject) {
            boolean b = (caretFlash++ % 50 <= 15);
            Statics.shapedrawer.setColor(parameter.color);
            if (b) {
                float off = nn.runs.size > 0 ? nn.runs.get(nn.runs.size - 1).width - sd.center.x : 0 + font.getSpaceXadvance() / 2f - sd.center.x;
                if (fd.text.length() > 0 && fd.text.charAt(fd.text.length() - 1) == '\n') {
                    off = font.getSpaceXadvance() / 2f - sd.center.x;
                }
                Statics.shapedrawer.filledRectangle(off, -sd.center.y, 5, font.getCapHeight());
            }
        }

        if (Statics.debug || Statics.selectedObjects.contains(this, true)) {
            Statics.shapedrawer.rectangle(new Rectangle(-sd.center.x, -sd.center.y, sd.bounds.x, sd.bounds.y));
            Statics.shapedrawer.filledCircle(0, 0, 15);
        }
        batch.end();
        batch.setTransformMatrix(SunshineLab.mx4Batch);
        batch.begin();
        if (polygon != null) {
            Statics.shapedrawer.setColor(Color.WHITE);
            Statics.shapedrawer.polygon(polygon);
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
        if (character == 13) {
            fd.text = fd.text + '\n';
        } else if (character == '\b') {
            if (!fd.text.isEmpty()) {
                fd.text = fd.text.substring(0, fd.text.length() - 1);
            }

        } else if (character == Input.Keys.SHIFT_LEFT || character == Input.Keys.SHIFT_RIGHT || character < 13) {

        } else {
            fd.text = fd.text + character;
//            System.out.println((int)character);
        }
        setBounds();

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
    public boolean isSelected(Vector2 touch) {
        polygon = new Polygon(new float[]{0, 0, sd.bounds.x, 0, sd.bounds.x, sd.bounds.y, 0,  sd.bounds.y, 0, 0});
        polygon.setOrigin(sd.center.x,sd.center.y);
        polygon.setScale(sd.scale,sd.scale);
        polygon.rotate(sd.rotation);
        polygon.translate(sd.position.x-sd.center.x,sd.position.y-sd.center.y);
        return polygon.contains(touch);
    }

    public void setColor(Color newColor) {
        if (newColor!=null) {
            fd.color = newColor;
            font.setColor(fd.color);
        }
    }
    @Override
    public JsonValue serialize() {
        JsonValue val=new JsonValue(JsonValue.ValueType.object);
        val.addChild("screenData", SerializeUtil.serialize(sd));
        val.addChild("fontData",SerializeUtil.serialize(fd));
        val.addChild("class",new JsonValue(FontObject.class.getName()));
        return val;
    }


    public static FontObject deserialize(JsonValue json) {
        FontData fd1=SerializeUtil.deserialize(json.get("fontData"),FontData.class);
        ScreenData sd1=SerializeUtil.deserialize(json.get("screenData"),ScreenData.class);
        return new FontObject(fd1,sd1);
    }

}
