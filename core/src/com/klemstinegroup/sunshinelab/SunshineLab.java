package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.igormaznitsa.jjjvm.impl.JJJVMClassImpl;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.*;

import static com.badlogic.gdx.Application.LOG_INFO;

public class SunshineLab extends ApplicationAdapter implements InputProcessor {

    //    Camera camera;

    Viewport viewport;
    public static Matrix4 mx4Batch = new Matrix4();
    Vector2 touchdown = new Vector2();
    JJJVMClassImpl jjjvmClass = null;

    @Override
    public void create() {
        Gdx.app.setLogLevel(LOG_INFO);
//        img = new Texture("badlogic.jpg");

        Statics.userObjects.add(new RectTextureObject("i.redd.it/0h1nbwj4bto61.jpg"));
        Statics.userObjects.add(new RectTextureObject("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png"));
        ((ScreenObject) Statics.userObjects.get(1)).position.set(-100, -100, 0);

        ((ScreenObject) Statics.userObjects.get(0)).scale = .1f;
        ((ScreenObject) Statics.userObjects.get(1)).scale = .4f;
        viewport = new ScreenViewport();

        InputMultiplexer im = new InputMultiplexer();

        BasicUIOverlay ov = new BasicUIOverlay();
        Gdx.input.setInputProcessor(im);
        im.addProcessor(this);
        im.addProcessor(ov.stage);
        Statics.overlayObjects.add( ov);
        mx4Batch = Statics.batch.getTransformMatrix().cpy();

//--------------------------------------------------------------------------------------------------
/*        JJJVMProvider provider = new JSEProviderImpl() {
            *//*@Override
            public Object invoke(JJJVMClass caller, Object instance, String jvmFormattedClassName, String methodName, String methodSignature, Object[] arguments) throws Throwable {
                if (jvmFormattedClassName.equals("java/io/PrintStream") && methodName.equals("println") && methodSignature.equals("(Ljava/lang/String;)V")){
                    Gdx.app.log("out","<<"+arguments[0]+">>");
                    return null;
                }
                return super.invoke(caller, instance, jvmFormattedClassName, methodName, methodSignature, arguments); //To change body of generated methods, choose Tools | Templates.
            }*//*
        };*/

        Gdx.app.log("out", "invoking");
        /*try {
            byte[] b = new byte[]{-54, -2, -70, -66, 0, 0, 0, 52, 0, 64, 10, 0, 13, 0, 27, 9, 0, 28, 0, 29, 8, 0, 30, 8, 0, 31, 11, 0, 32, 0, 33, 9, 0, 34, 0, 35, 10, 0, 36, 0, 37, 10, 0, 38, 0, 39, 10, 0, 36, 0, 40, 7, 0, 41, 9, 0, 10, 0, 42, 7, 0, 43, 7, 0, 44, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 43, 76, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 59, 1, 0, 4, 109, 97, 105, 110, 1, 0, 22, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 4, 97, 114, 103, 115, 1, 0, 19, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 15, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 46, 106, 97, 118, 97, 12, 0, 14, 0, 15, 7, 0, 45, 12, 0, 46, 0, 47, 1, 0, 3, 111, 117, 116, 1, 0, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 7, 0, 48, 12, 0, 49, 0, 50, 7, 0, 51, 12, 0, 52, 0, 53, 7, 0, 54, 12, 0, 55, 0, 56, 7, 0, 57, 12, 0, 58, 0, 59, 12, 0, 60, 0, 61, 1, 0, 58, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 111, 98, 106, 101, 99, 116, 115, 47, 83, 99, 114, 101, 101, 110, 79, 98, 106, 101, 99, 116, 12, 0, 62, 0, 63, 1, 0, 41, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 20, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 71, 100, 120, 1, 0, 3, 97, 112, 112, 1, 0, 30, 76, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 65, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 59, 1, 0, 28, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 65, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 1, 0, 3, 108, 111, 103, 1, 0, 39, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 45, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 83, 116, 97, 116, 105, 99, 115, 1, 0, 7, 111, 98, 106, 101, 99, 116, 115, 1, 0, 30, 76, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 59, 1, 0, 28, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 1, 0, 7, 116, 111, 65, 114, 114, 97, 121, 1, 0, 21, 40, 41, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 0, 16, 106, 97, 118, 97, 47, 117, 116, 105, 108, 47, 65, 114, 114, 97, 121, 115, 1, 0, 8, 116, 111, 83, 116, 114, 105, 110, 103, 1, 0, 39, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 3, 103, 101, 116, 1, 0, 21, 40, 73, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 0, 8, 114, 111, 116, 97, 116, 105, 111, 110, 1, 0, 1, 70, 0, 33, 0, 12, 0, 13, 0, 0, 0, 0, 0, 2, 0, 1, 0, 14, 0, 15, 0, 1, 0, 16, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 2, 0, 17, 0, 0, 0, 6, 0, 1, 0, 0, 0, 10, 0, 18, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 19, 0, 20, 0, 0, 0, -119, 0, 21, 0, 22, 0, 1, 0, 16, 0, 0, 0, 105, 0, 3, 0, 1, 0, 0, 0, 51, -78, 0, 2, 18, 3, 18, 4, -71, 0, 5, 3, 0, -78, 0, 2, 18, 3, -78, 0, 6, -74, 0, 7, -72, 0, 8, -71, 0, 5, 3, 0, -78, 0, 6, 3, -74, 0, 9, -64, 0, 10, 89, -76, 0, 11, 12, 98, -75, 0, 11, -79, 0, 0, 0, 2, 0, 17, 0, 0, 0, 18, 0, 4, 0, 0, 0, 12, 0, 12, 0, 13, 0, 31, 0, 14, 0, 50, 0, 15, 0, 18, 0, 0, 0, 12, 0, 1, 0, 0, 0, 51, 0, 23, 0, 24, 0, 0, 0, 1, 0, 25, 0, 0, 0, 2, 0, 26};
            jjjvmClass = new JJJVMClassImpl(new ByteArrayInputStream(b), provider);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Gdx.app.log("out", throwable.toString());
        }*/
        //--------------------------------------------------------------------------------------------------
        viewport.apply();
//        gifEncoderFile = new MemoryFileHandle();
//        Statics.gifOptions = new ImageOptions();
//        Statics.gifEncoderA = new AnimatedGifEncoder();
//        Statics.gifEncoderA.setDelay(10);
//        gifEncoderA.setSize(400, 400);
//        Statics.gifEncoderA.start(gifEncoderFile);
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();

        Statics.batch.setProjectionMatrix(viewport.getCamera().combined);
        Statics.batch.begin();
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(Statics.batch);
            }
            Statics.batch.setTransformMatrix(mx4Batch);
        }
        for (BaseObject bo : Statics.overlayObjects) {
            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(Statics.batch);
            }
            Statics.batch.setTransformMatrix(mx4Batch);
        }


////        int pix[][]=FrameBufferUtils.drawObjectsInt(viewport,Statics.objects,200,200);
////        System.out.println(cntr);
//        if (cntr < 170 && cntr > 60) {
//
////                Statics.gifEncoder.addImage(FrameBufferUtils.drawObjectsInt(viewport, Statics.objects, 400, 400), Statics.gifOptions);
//            Statics.gifEncoderA.addFrame(FrameBufferUtils.drawObjectsPix(viewport, Statics.objects, 400, 400));
//
//        } else if (cntr == 170) {
////                Statics.gifEncoder.finishEncoding();
//            Statics.gifEncoderA.finish();
//            IPFSResponseListener irl = new IPFSResponseListener() {
//                @Override
//                public void qid(String qid) {
//                    IPFSUtils.openIPFSViewer(qid);
//                }
//            };
//            IPFSUtils.uploadFile(gifEncoderFile.readBytes(),"image/gif", irl);
////            Gdx.net.openURI(data);
////                System.exit(0);
//
//        }
//        cntr++;
        Statics.batch.end();

        /*//------------------------------------------------------------
        try {
            jjjvmClass.findMethod("main", "([Ljava/lang/String;)V").invoke(null, null);
        } catch (Throwable throwable) {
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
        viewport.update(width, height);
    }

    @Override
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

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        viewport.unproject(touchdown.set(screenX, screenY));
        System.out.println(touchdown);
        Statics.selectedObjects.clear();
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                if (((Touchable) bo).isSelected(touchdown.cpy())) {
                    Statics.selectedObjects.add(bo);
                }
                ;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
