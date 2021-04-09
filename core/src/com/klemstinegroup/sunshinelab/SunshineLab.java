package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.igormaznitsa.jjjvm.impl.JJJVMClassImpl;
import com.igormaznitsa.jjjvm.impl.jse.JSEProviderImpl;
import com.igormaznitsa.jjjvm.model.JJJVMProvider;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.*;
import com.klemstinegroup.sunshinelab.engine.util.FrameBufferUtils;
import com.klemstinegroup.sunshinelab.engine.util.IPFSResponseListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSUtils;
import com.klemstinegroup.sunshinelab.engine.util.MemoryFileHandle;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

import java.io.ByteArrayInputStream;

import static com.badlogic.gdx.Application.LOG_INFO;

public class SunshineLab extends ApplicationAdapter {

    //    Camera camera;


    public static Matrix4 mx4Batch = new Matrix4();

    JJJVMClassImpl jjjvmClass = null;
    private MemoryFileHandle gifEncoderFile;

    @Override
    public void create() {
        Gdx.input.setInputProcessor(Statics.im);
        Statics.setOverlay(Statics.BASIC_UI_OVERLAY);
        Gdx.app.setLogLevel(LOG_INFO);
//        img = new Texture("badlogic.jpg");

        Statics.userObjects.add(new ImageObject("https://i.redd.it/0h1nbwj4bto61.jpg"));
//        Statics.userObjects.add(new RectTextureObject("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png"));
        Statics.userObjects.add(new ImageObject("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png"));
        ((ScreenObject) Statics.userObjects.get(1)).position.set(-100, -100);

        ((ScreenObject) Statics.userObjects.get(0)).scale = .1f;
        ((ScreenObject) Statics.userObjects.get(1)).scale = .4f;
        Statics.viewport = new ScreenViewport();
//        Statics.overlayViewport = new FitViewport((800f *Gdx.graphics.getWidth() / Gdx.graphics.getHeight() )/ Gdx.graphics.getDensity(), 800 / Gdx.graphics.getDensity());




//        Statics.im.addProcessor(this);

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
            byte[] b = new byte[]{-54, -2, -70, -66, 0, 0, 0, 52, 0, 38, 10, 0, 7, 0, 21, 9, 0, 22, 0, 23, 10, 0, 24, 0, 25, 7, 0, 26, 9, 0, 4, 0, 27, 7, 0, 28, 7, 0, 29, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 43, 76, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 59, 1, 0, 4, 109, 97, 105, 110, 1, 0, 22, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 4, 97, 114, 103, 115, 1, 0, 19, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 15, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 46, 106, 97, 118, 97, 12, 0, 8, 0, 9, 7, 0, 30, 12, 0, 31, 0, 32, 7, 0, 33, 12, 0, 34, 0, 35, 1, 0, 58, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 111, 98, 106, 101, 99, 116, 115, 47, 83, 99, 114, 101, 101, 110, 79, 98, 106, 101, 99, 116, 12, 0, 36, 0, 37, 1, 0, 41, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 45, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 83, 116, 97, 116, 105, 99, 115, 1, 0, 11, 117, 115, 101, 114, 79, 98, 106, 101, 99, 116, 115, 1, 0, 30, 76, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 59, 1, 0, 28, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 1, 0, 3, 103, 101, 116, 1, 0, 21, 40, 73, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 0, 8, 114, 111, 116, 97, 116, 105, 111, 110, 1, 0, 1, 70, 0, 33, 0, 6, 0, 7, 0, 0, 0, 0, 0, 2, 0, 1, 0, 8, 0, 9, 0, 1, 0, 10, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 2, 0, 11, 0, 0, 0, 6, 0, 1, 0, 0, 0, 6, 0, 12, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 13, 0, 14, 0, 0, 0, -119, 0, 15, 0, 16, 0, 1, 0, 10, 0, 0, 0, 66, 0, 3, 0, 1, 0, 0, 0, 20, -78, 0, 2, 4, -74, 0, 3, -64, 0, 4, 89, -76, 0, 5, 12, 98, -75, 0, 5, -79, 0, 0, 0, 2, 0, 11, 0, 0, 0, 10, 0, 2, 0, 0, 0, 8, 0, 19, 0, 9, 0, 12, 0, 0, 0, 12, 0, 1, 0, 0, 0, 20, 0, 17, 0, 18, 0, 0, 0, 1, 0, 19, 0, 0, 0, 2, 0, 20};

            jjjvmClass = new JJJVMClassImpl(new ByteArrayInputStream(b), provider);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Gdx.app.log("out", throwable.toString());
        }


        //--------------------------------------------------------------------------------------------------
        Statics.viewport.apply();
        gifEncoderFile = new MemoryFileHandle();
//        Statics.gifOptions = new ImageOptions();
        Statics.gifEncoderA = new AnimatedGifEncoder();
        Statics.gifEncoderA.setDelay(10);
        Statics.gifEncoderA.start(gifEncoderFile);
    }


    int cnt=100;
    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Statics.viewport.apply();



        Statics.batch.setProjectionMatrix(Statics.viewport.getCamera().combined);
        Statics.batch.begin();
        if (Statics.gif) {
            if (cnt-- > 0) {
                Statics.gifEncoderA.addFrame(FrameBufferUtils.drawObjectsPix(Statics.viewport, Statics.userObjects, 400, 400));
            }
            if (cnt == 0) {
                Statics.gifEncoderA.finish();
                IPFSUtils.uploadFile(gifEncoderFile.readBytes(), "image/gif", new IPFSResponseListener() {
                    @Override
                    public void qid(String qid) {
                        IPFSUtils.openIPFSViewer(qid);
                    }
                });
            }
        }
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(Statics.batch);
            }
            Statics.batch.setTransformMatrix(mx4Batch);
        }
        if (Statics.overlay!=null){
            ((Drawable) Statics.overlay).draw(Statics.batch);
        }
        Statics.batch.setTransformMatrix(mx4Batch);

        Statics.batch.end();

   /* //------------------------------------------------------------
        try

    {
        jjjvmClass.findMethod("main", "([Ljava/lang/String;)V").invoke(null, null);
    } catch(
    Throwable throwable)

    {
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
        Statics.viewport.update(width, height);
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
