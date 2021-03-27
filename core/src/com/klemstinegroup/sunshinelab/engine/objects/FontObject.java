package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class FontObject extends ScreenObject implements Drawable, Touchable {

    static final public FileHandle[] fontList = Gdx.files.internal("fonts").list();

    boolean editing = true;
    BitmapFont font;
    String text = "Sunshine Labs";
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private int caretFlash = 0;

    public FontObject(FileHandle fontFile, int size) {
        generate(fontFile, size);
    }

    private void generate(FileHandle fontFile, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        int a = MathUtils.randomBoolean() ? 0 : 1;
        int b = MathUtils.randomBoolean() ? 0 : 1;
        parameter.color = new Color(a, b, 1 - (a + b) / 2f, 1f);
        font = generator.generateFont(parameter);
        generator.dispose();
        setBounds();
    }

    private void setBounds() {
        GlyphLayout nn = new GlyphLayout();
        nn.setText(font, text);
        bounds.set(nn.width, nn.height, 0);
        center.set(bounds.x / 2f, bounds.y / 2f, 0);
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
        String b = (caretFlash++ % 10 <= 3) ? "|" : "";
        font.draw(batch, text + b, 0, +bounds.y);
        if (Statics.debug) {
            Statics.shapedrawer.setColor(Color.CYAN);
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, bounds.x, bounds.y));
            Statics.shapedrawer.setColor(Color.CYAN);

//            center.add(position).rotate(0,0,1,rotation).scl(scale,scale,1).sub(center.x,center.y,0);
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(center.x, center.y, 5);
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
        if (character == '\b'){
            if (!text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
            }
        } else {
            text = text + character;
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
}
