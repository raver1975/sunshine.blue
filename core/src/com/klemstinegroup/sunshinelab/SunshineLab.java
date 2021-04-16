package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.anim8.IncrementalAnimatedPNG;
import com.igormaznitsa.jjjvm.impl.JJJVMClassImpl;
import com.igormaznitsa.jjjvm.impl.jse.JSEProviderImpl;
import com.igormaznitsa.jjjvm.model.JJJVMProvider;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.*;
import com.klemstinegroup.sunshinelab.engine.overlays.Drawable;
import com.klemstinegroup.sunshinelab.engine.overlays.Overlay;
import com.klemstinegroup.sunshinelab.engine.util.*;

import java.io.ByteArrayInputStream;
import java.util.Comparator;

import static com.badlogic.gdx.Application.LOG_INFO;

public class SunshineLab extends ApplicationAdapter {

    public static NativeNetworkInterface nativeNet;

    //    Camera camera;
    public SunshineLab() {
        super();
        this.nativeNet = new NativeNetwork();
    }

    public SunshineLab(NativeNetworkInterface nativeIPFS) {
        super();
        this.nativeNet = nativeIPFS;
    }


    public static Matrix4 mx4Batch = new Matrix4();

    JJJVMClassImpl jjjvmClass = null;
    private IncrementalAnimatedPNG apng;
    private MemoryFileHandle mfh;

    @Override
    public void create() {

//        VisUI.load(VisUI.SkinScale.X2);
        Gdx.input.setInputProcessor(Statics.im);
        Statics.setOverlay(Statics.BASIC_UI_OVERLAY);
        Gdx.app.setLogLevel(LOG_INFO);
/*//        img = new Texture("badlogic.jpg");


//        Statics.userObjects.add(new ImageObject("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png"));
        Statics.userObjects.add(new ImageObject("QmZkvRWdSeksERDHhWSja9W7tja6wYQbk5KEfSxTbH87Va"));
        Statics.userObjects.add(new ImageObject("QmZkvRWdSeksERDHhWSja9W7tja6wYQbk5KEfSxTbH87Va"));
        ((ScreenObject) Statics.userObjects.get(1)).sd.position.set(-200, -200);
        ((ScreenObject) Statics.userObjects.get(0)).sd.center.set(100, 100);
//        ((ScreenObject) Statics.userObjects.get(0)).scale = .1f;
        ((ScreenObject) Statics.userObjects.get(0)).sd.scale = .4f;
        ((ScreenObject) Statics.userObjects.get(1)).sd.scale = .4f;
        ((ScreenObject) Statics.userObjects.get(0)).sd.rotation = 45;


        ScreenData sd = new ScreenData();
        FontData fd = new FontData();
        fd.text = "tesT";
        FontObject fo = new FontObject(fd, sd);
        Statics.userObjects.add(fo);
        for (int i = 0; i < 10; i++) {
            FontObject focpo = (FontObject) SerializeUtil.copy((FontObject) Statics.userObjects.get(Statics.userObjects.size - 1));
            focpo.sd.position.add(20, 20);
            focpo.sd.rotation += 10;
            Statics.userObjects.add(focpo);
        }
//        Statics.overlayViewport = new FitViewport((800f *Gdx.graphics.getWidth() / Gdx.graphics.getHeight() )/ Gdx.graphics.getDensity(), 800 / Gdx.graphics.getDensity());


//        Statics.im.addProcessor(this);*/

//        SunshineLab.nativeIPFS.downloadFile("QmQ2r6iMNpky5f1m4cnm3Yqw8VSvjuKpTcK1X7dBR1LkJF/cat.gif", new IPFSFileListener() {
//        Statics.userObjects.add(new ImageObject("https://i.redd.it/0h1nbwj4bto61.jpg"));




        Statics.viewport = new ScreenViewport();
        mx4Batch = Statics.batch.getTransformMatrix().cpy();


//--------------------------------------------------------------------------------------------------
        JJJVMProvider provider = new JSEProviderImpl() {
            /*@Override
            public Object invoke(JJJVMClass caller, Object instance, String jvmFormattedClassName, String methodName, String methodSignature, Object[] arguments) throws Throwable {
                if (jvmFormattedClassName.equals("java/io/PrintStream") && methodName.equals("println") && methodSignature.equals("(Ljava/lang/String;)V")){
                    Gdx.app.log("out","<<"+arguments[0]+">>");
                    return null;
                }
                return super.invoke(caller, instance, jvmFormattedClassName, methodName, methodSignature, arguments); //To change body of generated methods, choose Tools | Templates.
            }*/
        };

        Gdx.app.log("out", "invoking");
        try {
//            byte[] b = new byte[]{-54, -2, -70, -66, 0, 0, 0, 50, 0, 35, 1, 0, 10, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 7, 0, 1, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 7, 0, 3, 1, 0, 4, 109, 97, 105, 110, 1, 0, 22, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 19, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 7, 0, 7, 1, 0, 45, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 83, 116, 97, 116, 105, 99, 115, 7, 0, 9, 1, 0, 11, 117, 115, 101, 114, 79, 98, 106, 101, 99, 116, 115, 1, 0, 30, 76, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 59, 12, 0, 11, 0, 12, 9, 0, 10, 0, 13, 1, 0, 28, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 7, 0, 15, 1, 0, 3, 103, 101, 116, 1, 0, 21, 40, 73, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 12, 0, 17, 0, 18, 10, 0, 16, 0, 19, 1, 0, 58, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 111, 98, 106, 101, 99, 116, 115, 47, 83, 99, 114, 101, 101, 110, 79, 98, 106, 101, 99, 116, 7, 0, 21, 1, 0, 8, 114, 111, 116, 97, 116, 105, 111, 110, 1, 0, 1, 70, 12, 0, 23, 0, 24, 9, 0, 22, 0, 25, 6, 63, -71, -103, -103, -103, -103, -103, -102, 1, 0, 13, 83, 116, 97, 99, 107, 77, 97, 112, 84, 97, 98, 108, 101, 1, 0, 4, 67, 111, 100, 101, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 12, 0, 31, 0, 32, 10, 0, 4, 0, 33, 0, 33, 0, 2, 0, 4, 0, 0, 0, 0, 0, 2, 0, 9, 0, 5, 0, 6, 0, 1, 0, 30, 0, 0, 0, 63, 0, 5, 0, 1, 0, 0, 0, 24, -78, 0, 14, 4, -74, 0, 20, -64, 0, 22, 89, -76, 0, 26, -115, 20, 0, 27, 103, -112, -75, 0, 26, -79, 0, 0, 0, 1, 0, 29, 0, 0, 0, 21, 0, 3, 67, 7, 0, 16, -1, 0, 11, 0, 1, 7, 0, 8, 0, 2, 7, 0, 22, 3, 7, 0, 1, 0, 31, 0, 32, 0, 1, 0, 30, 0, 0, 0, 25, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 34, -79, 0, 0, 0, 1, 0, 29, 0, 0, 0, 2, 0, 0, 0, 0};
            byte[] b = new byte[]{-54, -2, -70, -66, 0, 0, 0, 52, 0, 44, 10, 0, 8, 0, 22, 9, 0, 23, 0, 24, 10, 0, 25, 0, 26, 7, 0, 27, 9, 0, 4, 0, 28, 9, 0, 29, 0, 30, 7, 0, 31, 7, 0, 32, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 43, 76, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 59, 1, 0, 4, 109, 97, 105, 110, 1, 0, 22, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 4, 97, 114, 103, 115, 1, 0, 19, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 15, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 46, 106, 97, 118, 97, 12, 0, 9, 0, 10, 7, 0, 33, 12, 0, 34, 0, 35, 7, 0, 36, 12, 0, 37, 0, 38, 1, 0, 58, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 111, 98, 106, 101, 99, 116, 115, 47, 83, 99, 114, 101, 101, 110, 79, 98, 106, 101, 99, 116, 12, 0, 39, 0, 40, 7, 0, 41, 12, 0, 42, 0, 43, 1, 0, 41, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 45, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 83, 116, 97, 116, 105, 99, 115, 1, 0, 11, 117, 115, 101, 114, 79, 98, 106, 101, 99, 116, 115, 1, 0, 30, 76, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 59, 1, 0, 28, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 1, 0, 3, 103, 101, 116, 1, 0, 21, 40, 73, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 0, 2, 115, 100, 1, 0, 58, 76, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 111, 98, 106, 101, 99, 116, 115, 47, 83, 99, 114, 101, 101, 110, 68, 97, 116, 97, 59, 1, 0, 56, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 111, 98, 106, 101, 99, 116, 115, 47, 83, 99, 114, 101, 101, 110, 68, 97, 116, 97, 1, 0, 8, 114, 111, 116, 97, 116, 105, 111, 110, 1, 0, 1, 70, 0, 33, 0, 7, 0, 8, 0, 0, 0, 0, 0, 2, 0, 1, 0, 9, 0, 10, 0, 1, 0, 11, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 2, 0, 12, 0, 0, 0, 6, 0, 1, 0, 0, 0, 6, 0, 13, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 14, 0, 15, 0, 0, 0, -119, 0, 16, 0, 17, 0, 1, 0, 11, 0, 0, 0, 69, 0, 3, 0, 1, 0, 0, 0, 23, -78, 0, 2, 4, -74, 0, 3, -64, 0, 4, -76, 0, 5, 89, -76, 0, 6, 12, 98, -75, 0, 6, -79, 0, 0, 0, 2, 0, 12, 0, 0, 0, 10, 0, 2, 0, 0, 0, 8, 0, 22, 0, 9, 0, 13, 0, 0, 0, 12, 0, 1, 0, 0, 0, 23, 0, 18, 0, 19, 0, 0, 0, 1, 0, 20, 0, 0, 0, 2, 0, 21};

            jjjvmClass = new JJJVMClassImpl(new ByteArrayInputStream(b), provider);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Gdx.app.log("out", throwable.toString());
        }


        //--------------------------------------------------------------------------------------------------
        Statics.viewport.apply();
//        Statics.gifOptions = new ImageOptions();

//        Statics.gifEncoderA = new AnimatedGifEncoder();
//        Statics.gifEncoderA.setDelay(10);
//        Statics.gifEncoderA.start(gifEncoderFile);
//        Statics.apng=new AnimatedPNG();
        apng = new IncrementalAnimatedPNG();
        apng.setFlipY(true);
        mfh = new MemoryFileHandle();
        apng.start(mfh, (short) 10, 400, 400);


    }


    int cnt = 3000;

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Statics.viewport.apply();


        Statics.batch.setProjectionMatrix(Statics.viewport.getCamera().combined);
        Statics.batch.begin();

        if (cnt--==2800){
            ImageObject.load("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png");
//            SunshineLab.nativeNet.downloadIPFS("QmPfaw52jwB8WGPDMG8Xuo2vx94LRAHb3iB6L9RW9oruFj", new IPFSFileListener() {
//                @Override
//                public void downloaded(byte[] file) {
//                    Statics.userObjects.add(new ImageObject(file));
//                }
//
//                @Override
//                public void downloadFailed(Throwable t) {
//
//                }
//            });
//            SunshineLab.nativeNet.downloadIPFS("QmbSq4P8MaQqUBzoobcKbQ78MtQujrq9bAxi6MRDi11BWc", new IPFSFileListener() {
//                @Override
//                public void downloaded(byte[] file) {
//                    ImageObject bb = new ImageObject(file);
//                    Statics.userObjects.add(bb);
//                    bb.sd.position.sub(500,500);
//                }
//
//                @Override
//                public void downloadFailed(Throwable t) {
//
//                }
//            });
//            SunshineLab.nativeNet.downloadFile("https://s0.2mdn.net/9340650/2719916979880523/Linux_Servers_Unmatched_Resiliency_2_300x250.png", new IPFSFileListener() {
//            SunshineLab.nativeNet.downloadFile("https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/18cf621f-daff-4f65-8a94-a08911234091/d5msnyk-2dd09fd1-6d15-4602-b059-2ba3a5f899ba.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOiIsImlzcyI6InVybjphcHA6Iiwib2JqIjpbW3sicGF0aCI6IlwvZlwvMThjZjYyMWYtZGFmZi00ZjY1LThhOTQtYTA4OTExMjM0MDkxXC9kNW1zbnlrLTJkZDA5ZmQxLTZkMTUtNDYwMi1iMDU5LTJiYTNhNWY4OTliYS5wbmcifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6ZmlsZS5kb3dubG9hZCJdfQ.GXhVLfaMuEi6BlvpXbmu1BoV6yNjdxHHb9gi1s3dFJs", new IPFSFileListener() {
//            SunshineLab.nativeNet.downloadFile("https://media.tenor.com/images/3c6f16ee7048e074dd823b9262538806/tenor.gif", new IPFSFileListener() {
//            SunshineLab.nativeNet.downloadFile("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcReZeHB-40uL8fsio3gVfg8J6UkFO_S7YIzJy5q2C2NhvUW1J-zSylg6p6GdEBYOTFaWqc&usqp=CAU", new IPFSFileListener() {
              /*  @Override
                public void downloaded(byte[] file) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Gdx.app.log("downloaded",file.length+"");
                            ImageObject bb = new ImageObject(file);
                            Statics.userObjects.add(bb);
                            bb.sd.position.sub(0,0);
                        }
                    });

                }

                @Override
                public void downloadFailed(Throwable t) {

                }
            });*/

        }

        if (Statics.gif) {
            if (cnt-- > 0 && cnt < 10) {
//                Statics.gifEncoderA.addFrame(FrameBufferUtils.drawObjectsPix(Statics.viewport, Statics.userObjects, 400, 400));
                apng.write(FrameBufferUtils.drawObjectsPix(Statics.viewport, Statics.userObjects, 400, 400));
                Gdx.app.log("count", cnt + "");
                if (cnt == 4) {
                    int gg = Statics.userObjects.size;
                    for (int draw = 0; draw < gg; draw++) {
                        if (Statics.userObjects.get(draw) != null) {
                            Gdx.app.log("class", Statics.userObjects.get(draw).getClass().getName());
                            SerializeUtil.copy(Statics.userObjects.get(draw));
                        }
                    }
                }


            }
//            if (cnt == 2000) {
//                String name = Statics.prefs.getString("current");
//                if (name != null) {
//                    Gdx.app.log("loading:", name);
//                    SerializeUtil.load(name);
//                }
//            }
            if (cnt == 1000) {
                SerializeUtil.save("test");
            }
            if (cnt == 100) {
                SerializeUtil.load("test");
            }
            if (cnt == 0) {
                nativeNet.downloadIPFS("QmZtmD2qt6fJot32nabSP3CUjicnypEBz7bHVDhPQt9aAy", new IPFSFileListener() {
                    @Override
                    public void downloaded(byte[] file) {
                        Gdx.app.log("text", new String(file));
                    }

                    @Override
                    public void downloadFailed(Throwable t) {
                        Gdx.app.log("textError", t.getMessage());
                    }
                });

//                Statics.gifEncoderA.finish();
                apng.end();
                //                IPFSUtils.uploadFile(mfh.readBytes(), "image/apng", new IPFSResponseListener() {
                SunshineLab.nativeNet.uploadIPFS(mfh.readBytes(),  new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        IPFSUtils.openIPFSViewer(cid);
                    }

                    @Override
                    public void uploadFailed(Throwable t) {

                    }
                });
            }
        }
        Statics.userObjects.sort(new Comparator<BaseObject>() {
            @Override
            public int compare(BaseObject o1, BaseObject o2) {
                if (o1 instanceof ScreenObject && o2 instanceof ScreenObject){
                    return Integer.compare(((ScreenObject)o1).sd.layer,((ScreenObject)o2).sd.layer);
                }
                else return 0;
            }
        });
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(Statics.batch);
            }
            Statics.batch.setTransformMatrix(mx4Batch);
        }
        if (Statics.overlay != null) {
            ((Overlay) Statics.overlay).act();
            ((Drawable) Statics.overlay).draw(Statics.batch);
        }
        Statics.batch.setTransformMatrix(mx4Batch);

        Statics.batch.end();

/*        //------------------------------------------------------------
        try {
            jjjvmClass.findMethod("main", "([Ljava/lang/String;)V").invoke(null, null);
        } catch (
                Throwable throwable) {
            throwable.printStackTrace();
        }
        //------------------------------------------------------------*/
    }


    @Override
    public void dispose() {
        Statics.batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("resize",width+"\t"+height);
//        int WORLD_WIDTH=(550*width)/height;
//        int WORLD_HEIGHT=550;
        Statics.viewport.update(width, height);
        Statics.overlayViewport.update(width,height);
//        Statics.overlayViewport.getCamera().viewportWidth = WORLD_WIDTH;
//        Statics.overlayViewport.getCamera().viewportHeight = WORLD_HEIGHT;
//        Statics.overlayViewport.getCamera().position.set(WORLD_WIDTH/2,WORLD_HEIGHT/2, 0);
//        Statics.overlayViewport.getCamera().update();

        /*Statics stat=new Statics();
        Field[] fields = ClassReflection.getFields(Statics.class);
        for (Field f:fields){
            if (Overlay.class.isAssignableFrom(f.getType())){
                if (f.getType().isInterface()){continue;}
                try {
                    Object o=f.get(stat);
                    System.out.println(f.getType().getName());
                    Field stageField=ClassReflection.getField(f.getType(),"stage");
                    Stage st=(Stage) stageField.get(o);
                    st.getViewport().update(width,height);
//                    st.getCamera().viewportWidth = WORLD_WIDTH;
//                    st.getCamera().viewportHeight = WORLD_HEIGHT;
//                    st.getCamera().position.set(st.getCamera().viewportWidth / 2, st.getCamera().viewportHeight / 2, 0);
                    st.getCamera().update();
//                    st.setViewport(Statics.overlayViewport);
//                    st.getViewport().apply(true);
                } catch (ReflectionException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    /*@Override
    public boolean keyDown(int keycode) {
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).keyDown(keycode);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).keyDown(keycode);
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).keyTyped(character);
            }
        }
        return false;
    }

//

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).touchUp(screenX, screenY, pointer, button);
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        viewport.unproject(touchdrag.set(screenX, screenY, 0));
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).touchDragged(screenX, screenY, pointer);
            }
        }
        for (BaseObject bo : Statics.selectedObjects) {
            if (bo instanceof ScreenObject) {
                switch (Statics.transformButton) {
                    case 0:
                        ((ScreenObject) bo).position.add(touchdrag.cpy().sub(touchdown));
                        break;
                    case 1:
                        ((ScreenObject) bo).rotation += touchdrag.x - touchdown.x;
                        break;
                    case 2:
                        ((ScreenObject) bo).scale += (touchdrag.x - touchdown.x) / 200f;
                        break;
                }

            }
        }
        touchdown.set(touchdrag);


        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }*/
}
