package com.klemstinegroup.sunshineblue.engine;

import com.badlogic.gdx.Gdx;

public class Statics {


    public static int RECMAXFRAMES = 600;
    public static String IPFSGateway = "https://ipfs.io/ipfs/";
    public static final String CORSGateway="https://api.codetabs.com/v1/proxy?quest=";
    public static String IPFSMediaViewer="Qmd98X8gq4j722Zvy6Hmx4UEzCmyQP4pdREggjiF2dYZMQ";
    public static String splashCID="QmUABcxLTJtwG7ecTTa6zt6T2tUi3KB2KPTdTCxTxs65Y2";

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
