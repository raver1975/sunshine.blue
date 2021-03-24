package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class FontObject extends BaseObject implements Drawable{

    static final public FileHandle[] fontList = Gdx.files.internal("fonts").list();;

    BitmapFont font;
    String text="Sunshine Labs";
    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    public Position position=new Position();

    public FontObject(FileHandle fontFile,int size){
generate(fontFile,size);
    }

    private void generate(FileHandle fontFile, int size){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = new Color(0f, 1f, 0f, 1f);
        font = generator.generateFont(parameter);
        generator.dispose();
       setBounds();
    }

    private void setBounds() {
        GlyphLayout nn = new GlyphLayout();
        nn.setText(font, text);
        position.setBounds(nn.width+font.getSpaceXadvance(),font.getLineHeight());
    }

    public void setText(String text){
        this.text=text;
        setBounds();
    }


    @Override
    public void draw(Batch batch) {
        batch.setTransformMatrix(new Matrix4().idt()
                        .translate(position.getCenter().x+ position.getPosition().x, position.getCenter().y + position.getPosition().y, 0)
                        .rotate(0, 0, 1, position.getRotation())
                        .scale(position.getScale(), position.getScale(), 1)
//                .translate(-position.x, -position.y, 0)
                        .translate(-position.getCenter().x , -position.getCenter().y , 0)
        );
        font.draw(batch,text,0,0);
        if (Statics.debug) {
            Statics.shapedrawer.setColor(Color.CYAN);
            Statics.shapedrawer.rectangle(new Rectangle(-font.getSpaceXadvance()/2f, -font.getCapHeight()+font.getDescent(), position.getBounds().x, position.getBounds().y));
            Statics.shapedrawer.setColor(Color.CYAN);
            Statics.shapedrawer.filledCircle(position.getCenter().x,position.getCenter().y,5);
        }
    }
}
