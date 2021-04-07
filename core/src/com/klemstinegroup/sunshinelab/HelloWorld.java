package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.Gdx;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.ScreenObject;

import java.util.Arrays;

public class HelloWorld {
    public static void main(final String... args) {
        Gdx.app.log("out", "hello world");
        Gdx.app.log("out", Arrays.toString(Statics.userObjects.toArray()));
        ((ScreenObject)Statics.userObjects.get(1)).rotation+=1;
    }
}