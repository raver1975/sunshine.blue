package com.klemstinegroup.sunshineblue.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshineblue.engine.util.IPFSUtils;
import com.klemstinegroup.sunshineblue.engine.util.NativeJava;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ByteCodeUploader {
    @SuppressWarnings("NewApi")
    public static void main(String[] args) throws IOException {
        LwjglAWTCanvas canvas = new LwjglAWTCanvas(new SunshineBlue());
        File file=new File("./core/build/classes/java/main/com/klemstinegroup/sunshineblue/plugins/");
        System.out.println("isDirectory?"+file.isDirectory());
        File[] files=file.listFiles();
        NativeJava nativeJava = new NativeJava();
        assert files != null;
        final int[] cnt = {0,0};
        Array<String> array=new Array<>();
        for (File f:files){
            cnt[0]++;
            nativeJava.uploadIPFS(Files.readAllBytes(f.toPath()), new IPFSCIDListener() {
                @Override
                public void cid(String cid) {
                    array.add(f.getName()+"\t"+cid);
                    cnt[1]++;
                }

                @Override
                public void uploadFailed(Throwable t) {

                }
            });
        }
        while(cnt[0]!=cnt[1]){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("------------------------------------");
        for (String s:array){
            System.out.println(s);
        }
        System.out.println("------------------------------------");
    }
}
