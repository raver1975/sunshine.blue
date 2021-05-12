package com.klemstinegroup.sunshineblue.engine.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.FloatArray;

public class BrushData {

    FloatArray ba = new FloatArray();

    int width = 10;
    public float halfWidth = width / 2f;
    int height = 10;
    public float halfHeight = height / 2f;
//    TextureRegion texture;
    ArrayMap<Integer,TextureRegion> brushes=new ArrayMap<>();
//    public TextureRegion textureSmall;

    public BrushData() {

//        width/=2;
//        height/=2;
//        Pixmap pixmap2 = new Pixmap(width, height, Pixmap.Format.RGBA8888);
//        pixmap2.setColor(Color.WHITE);
//        Color[][] grid = new Color[width][height];
//        Vector2 d1 = new Vector2();
//        Vector2 d2 = new Vector2();
//
//        d1.set(width / 2f, height / 2f);
//        float maxdist = d1.dst(d2)/2f;
//        for (int yy = 0; yy < height; yy++) {
//            for (int xx = 0; xx < width; xx++) {
//                grid[xx][yy] = new Color(1,1,1,Math.min(1,Math.max(0,1f-d1.dst(d2.set(xx, yy))/maxdist)));
//                pixmap2.setColor(grid[xx][yy]);
//                pixmap2.drawPixel(xx, yy);
//            }
//        }
//        for (int yy = 0; yy < height; yy++) {
//            for (int xx = 0; xx < width; xx++) {
//                ba.add(grid[xx][yy]);
//            }
//        }

//        width*=2;
//        height*=2;
        generate(width);
    }

    public void generate(int n) {
        this.width=n;
        this.height =n;
        this.halfWidth=width/2;
        this.halfHeight= this.height /2;
        Pixmap pixmap1 = new Pixmap(width, this.height, Pixmap.Format.RGBA8888);
        pixmap1.setColor(Color.WHITE);
        float[][] grid = new float[width][this.height];
        Vector2 d1 = new Vector2();
        Vector2 d2 = new Vector2();

        d1.set(width / 2f, this.height / 2f);
        d2.set(0, 0);
        float maxdist = d1.dst(d2);
        for (int yy = 0; yy < this.height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                grid[xx][yy] = Math.min(1, Math.max(0, (1f - d1.dst(d2.set(xx, yy)) / maxdist)));
                if (d1.dst(d2.set(xx, yy))>(.5f*n))grid[xx][yy]=0;
                if (d1.dst(d2.set(xx, yy))<(.45f*n))grid[xx][yy]=1;
                pixmap1.setColor(1, 1, 1, grid[xx][yy]);
//                pixmap1.setColor(1, 1, 1, .8f);
                pixmap1.drawPixel(xx, yy);
            }
        }
        for (int yy = 0; yy < this.height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                ba.add(grid[xx][yy]);
            }
        }
        brushes.put(n,new TextureRegion(new Texture(pixmap1)));
    }

    public TextureRegion getTexture(int ss) {
        if (!brushes.containsKey(ss)) {
            generate(ss);
        }
        return brushes.get(ss);
    }
    public void clear(){
        brushes.clear();
    }
}
