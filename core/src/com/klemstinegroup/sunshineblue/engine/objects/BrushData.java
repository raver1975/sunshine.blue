package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.FloatArray;

public class BrushData {

    Array<Color> ba = new Array<>();

    int width = 100;
    public float halfWidth=width/2f;
    int height = 100;
    public float halfHeight=height/2f;
    TextureRegion texture;
    public TextureRegion textureSmall;

    public BrushData() {

        width/=2;
        height/=2;
        Pixmap pixmap2 = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap2.setColor(Color.WHITE);
        Color[][] grid = new Color[width][height];
        Vector2 d1 = new Vector2();
        Vector2 d2 = new Vector2();

        d1.set(width / 2f, height / 2f);
        float maxdist = d1.dst(d2)/2f;
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                grid[xx][yy] = new Color(1,1,1,Math.min(1,Math.max(0,1f-d1.dst(d2.set(xx, yy))/maxdist)));
                pixmap2.setColor(grid[xx][yy]);
                pixmap2.drawPixel(xx, yy);
            }
        }
//        for (int yy = 0; yy < height; yy++) {
//            for (int xx = 0; xx < width; xx++) {
//                ba.add(grid[xx][yy]);
//            }
//        }

        width*=2;
        height*=2;
        Pixmap pixmap1 = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap1.setColor(Color.WHITE);
        grid = new Color[width][height];
//        Vector2 d1 = new Vector2();
//        Vector2 d2 = new Vector2();

        d1.set(width / 2f, height / 2f);
        d2.set(0,0);
        maxdist = d1.dst(d2)/2f;
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                    grid[xx][yy] = new Color(1,1,1,Math.min(1,Math.max(0,1f-d1.dst(d2.set(xx, yy))/maxdist)));
                    pixmap1.setColor(grid[xx][yy]);
                    pixmap1.drawPixel(xx, yy);
            }
        }
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                ba.add(grid[xx][yy]);
            }
        }
        texture = new TextureRegion(new Texture(pixmap1));
        textureSmall = new TextureRegion(new Texture(pixmap2));
    }
}
