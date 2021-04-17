package com.klemstinegroup.sunshineblue;

import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class HelloWorld {
    public static void main(final String... args) {
        ((ScreenObject)Statics.userObjects.get(1)).sd.rotation+=1;
    }
}