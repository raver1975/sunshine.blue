package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
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

public class FontObject extends ScreenObject implements Drawable{

    static final public FileHandle[] fontList = Gdx.files.internal("fonts").list();;

    BitmapFont font;
    String text="Sunshine Labs";
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    public FontObject(FileHandle fontFile,int size){
generate(fontFile,size);
    }

    private void generate(FileHandle fontFile, int size){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        int a= MathUtils.randomBoolean()?0:1;
        int b= MathUtils.randomBoolean()?0:1;
        parameter.color = new Color(a, b, 1-(a+b)/2f, 1f);
        font = generator.generateFont(parameter);
        generator.dispose();
       setBounds();
    }

    private void setBounds() {
        GlyphLayout nn = new GlyphLayout();
        nn.setText(font, text);
        setBounds(nn.width,nn.height);
        setCenter(getBounds().x/2f,getBounds().y/2f);
        setPosition(-getCenter().x,-getCenter().y);
    }

    public void setText(String text){
        this.text=text;
        setBounds();
    }


    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(getCenter().x+ getPosition().x, getCenter().y + getPosition().y, 0)
                        .rotate(0, 0, 1, getRotation())
                        .scale(getScale(), getScale(), 1)
//                .translate(-position.x, -position.y, 0)
                        .translate(-getCenter().x , -getCenter().y , 0)
        );
        font.draw(batch,text,0,+getBounds().y);
        if (Statics.debug) {
            Statics.shapedrawer.setColor(Color.CYAN);
            Statics.shapedrawer.rectangle(new Rectangle(0, 0, getBounds().x, getBounds().y));
            Statics.shapedrawer.setColor(Color.CYAN);

            getCenter().add(getCenter().x+ getPosition().x, getCenter().y+getPosition().y, 0).rotate(0,0,1,getRotation()).scl(getScale(),getScale(),1).sub(getCenter().x,getCenter().y,0);
            Statics.shapedrawer.setColor(Color.RED);
            Statics.shapedrawer.filledCircle(getCenter().x,getCenter().y,5);
        }
    }
}
