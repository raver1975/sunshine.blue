package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.Gdx;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.Drawable;
import com.klemstinegroup.sunshinelab.engine.objects.ScreenObject;

import java.util.Arrays;

public class HelloWorld {
    public static void main(final String... args) {
        Gdx.app.log("out", "hello world");
        Gdx.app.log("out", Arrays.toString(Statics.objects.toArray()));
        ((ScreenObject)Statics.objects.get(0)).rotation+=1;
    }
}