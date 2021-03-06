package com.klemstinegroup.sunshineblue.engine;

import com.badlogic.gdx.Gdx;

public class Statics {


    public static final long AUTOLOADTIME = 10000;
    public static final int RECMAXFRAMESMAX = 600;
    public static int recframes = 30;
    public static String IPFSGateway = "https://ipfs.io/ipfs/";
    public static final String CORSGateway="https://api.codetabs.com/v1/proxy?quest=";
    public static String IPFSMediaViewerGIF="Qmd98X8gq4j722Zvy6Hmx4UEzCmyQP4pdREggjiF2dYZMQ";
    public static String IPFSMediaViewerPNG="QmeCsyCdHqwwmAyj4RZnBeZsx1a6EsdbEwsQwyHr6QFXwg";
    public static String firstSceneJson="{screenData:{scale:8.764999999999995,bounds:{v:\"1121241064,1121370550\"},layer:8},particle:\"Particle Park Hallucinogen Full\",speed:1,class:\"com.klemstinegroup.sunshineblue.engine.objects.ParticleObject\",UUID:\"d8bf9910-45a4-41de-8384-f1c831144602\"}";
//    public static String splashCID="QmQYequwMG6uxCNiskz6u6hZXxGcVnWKjWaH6Zyv2jXGK6";

    public Statics(){}

    public static void exceptionLog(String tag,Exception e) {
        Gdx.app.log(tag,e.toString());
        StackTraceElement[] st=e.getStackTrace();
        for (StackTraceElement s:st){
            Gdx.app.log(tag,s.toString());
        }
    }

    public static void exceptionLog(String tag, Throwable t) {
        exceptionLog(tag,(Exception)t);
    }

}
