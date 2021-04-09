package com.klemstinegroup.sunshinelab;

import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.ScreenObject;

public class HelloWorld {
    public static void main(final String... args) {
        ((ScreenObject)Statics.userObjects.get(1)).rotation+=1;
    }
}