package com.klemstinegroup.sunshineblue.engine.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.FloatArray;

public class BrushData {

    FloatArray ba = new FloatArray();
    int width = 100;
    public float halfWidth=width/2f;
    int height = 100;
    public float halfHeight=height/2f;
    float percent = 1f;
    Texture texture;

    public BrushData() {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        boolean[][] test = new boolean[width][height];
        float[][] grid = new float[width][height];
        Vector2 d1 = new Vector2();
        Vector2 d2 = new Vector2();

        d1.set(width / 2f, height / 2f);
        float maxdist = d1.dst2(d2)/40f;
        for (int i = 0; i < width * height * percent; i++) {
            int x = MathUtils.random(width - 1);
            int y = MathUtils.random(height - 1);
            if (test[x][y]) {
                i--;
            } else {
                test[x][y] = true;
                grid[x][y] = 1f;
                pixmap.setColor(MathUtils.random(), MathUtils.random(), 1, Math.max(0,1f-d1.dst2(d2.set(x, y))/maxdist));
                pixmap.drawPixel(x, y);
            }
        }
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                ba.add(grid[xx][yy]);
            }
        }
        texture = new Texture(pixmap);
    }
}
