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
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class FontObject extends ScreenObject implements Drawable, Touchable {

    private final FileHandle[] fontList;
    BitmapFont font;
    String text="";
    String fontName;
    Color color=Color.WHITE;
    int size=50;
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private int caretFlash = 0;
    private GlyphLayout nn;
    public Polygon polygon;

    public void setFont(int index){
        fontName= fontList[index].nameWithoutExtension();
    }
    public void setSize(int size){
        this.size=size;
        generate();
    }
    public FontObject() {
        fontList = Gdx.files.internal("fonts").list();
        generate();
    }

    void generate() {
        FileHandle ff = fontList[MathUtils.random(fontList.length - 1)];
        if (fontName==null) {
            fontName=ff.nameWithoutExtension();
        }
        for (FileHandle fh: fontList){
            if (fh.nameWithoutExtension().equals(fontName)){ff=fh;}
        }
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(ff);
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
//        int a = MathUtils.randomBoolean() ? 0 : 1;
//        int b = MathUtils.randomBoolean() ? 0 : 1;
        parameter.color = color;
        font = generator.generateFont(parameter);
        generator.dispose();
        setBounds();
        center.set(bounds.x / 2f, bounds.y / 2f);
    }

    public void setBounds() {
        nn = new GlyphLayout();
        nn.setText(font, text);
        bounds.set(nn.width, nn.height);

    }

    public void setText(String text) {
        this.text = text;
        setBounds();
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

        font.draw(batch, text, 0, +bounds.y, Float.MAX_VALUE, Align.left, true);

        boolean b = (caretFlash++ % 50 <= 15);
        Statics.shapedrawer.setColor(parameter.color);
        if (b) {
            Statics.shapedrawer.filledRectangle(nn.runs.size > 0 ? nn.runs.get(nn.runs.size - 1).width : 0 + font.getSpaceXadvance() / 2f, 0, 5, font.getCapHeight());
        }

        if (Statics.debug || Statics.selectedObjects.contains(this, true)) {
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, bounds.x, bounds.y));
            Statics.shapedrawer.filledCircle(center.x, center.y, 5);
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
        System.out.println((int) character);

        if (character == 13) {
            text = text + '\n';
        } else if (character == '\b') {
            if (!text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
            }

        } else if (character == Input.Keys.SHIFT_LEFT || character == Input.Keys.SHIFT_RIGHT || character < 13) {

        } else {
            text = text + character;
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
        polygon = new Polygon(new float[]{0, 0, bounds.x, 0, bounds.x, bounds.y, 0,  bounds.y, 0, 0});
        polygon.setOrigin(center.x,center.y);
        polygon.setScale(scale,scale);
        polygon.rotate(rotation);
        polygon.translate(position.x,position.y);
        return polygon.contains(touch);
    }

    public void setColor(Color newColor) {
        this.color=newColor;
        font.setColor(this.color);
    }
}
