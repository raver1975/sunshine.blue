package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.igormaznitsa.jjjvm.impl.JJJVMClassImpl;
import com.igormaznitsa.jjjvm.impl.jse.JSEProviderImpl;
import com.igormaznitsa.jjjvm.model.JJJVMClass;
import com.igormaznitsa.jjjvm.model.JJJVMProvider;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.*;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

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
            byte[] b = new byte[]{-54, -2, -70, -66, 0, 0, 0, 50, 0, 35, 1, 0, 10, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 7, 0, 1, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 7, 0, 3, 1, 0, 4, 109, 97, 105, 110, 1, 0, 22, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 19, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 7, 0, 7, 1, 0, 45, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 83, 116, 97, 116, 105, 99, 115, 7, 0, 9, 1, 0, 11, 117, 115, 101, 114, 79, 98, 106, 101, 99, 116, 115, 1, 0, 30, 76, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 59, 12, 0, 11, 0, 12, 9, 0, 10, 0, 13, 1, 0, 28, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 7, 0, 15, 1, 0, 3, 103, 101, 116, 1, 0, 21, 40, 73, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 12, 0, 17, 0, 18, 10, 0, 16, 0, 19, 1, 0, 58, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 111, 98, 106, 101, 99, 116, 115, 47, 83, 99, 114, 101, 101, 110, 79, 98, 106, 101, 99, 116, 7, 0, 21, 1, 0, 8, 114, 111, 116, 97, 116, 105, 111, 110, 1, 0, 1, 70, 12, 0, 23, 0, 24, 9, 0, 22, 0, 25, 6, 63, -71, -103, -103, -103, -103, -103, -102, 1, 0, 13, 83, 116, 97, 99, 107, 77, 97, 112, 84, 97, 98, 108, 101, 1, 0, 4, 67, 111, 100, 101, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 12, 0, 31, 0, 32, 10, 0, 4, 0, 33, 0, 33, 0, 2, 0, 4, 0, 0, 0, 0, 0, 2, 0, 9, 0, 5, 0, 6, 0, 1, 0, 30, 0, 0, 0, 63, 0, 5, 0, 1, 0, 0, 0, 24, -78, 0, 14, 4, -74, 0, 20, -64, 0, 22, 89, -76, 0, 26, -115, 20, 0, 27, 103, -112, -75, 0, 26, -79, 0, 0, 0, 1, 0, 29, 0, 0, 0, 21, 0, 3, 67, 7, 0, 16, -1, 0, 11, 0, 1, 7, 0, 8, 0, 2, 7, 0, 22, 3, 7, 0, 1, 0, 31, 0, 32, 0, 1, 0, 30, 0, 0, 0, 25, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 34, -79, 0, 0, 0, 1, 0, 29, 0, 0, 0, 2, 0, 0, 0, 0};

            String testString1 ="import com.badlogic.gdx.Gdx;\n" +
                    "import com.klemstinegroup.sunshinelab.engine.Statics;\n" +
                    "import com.klemstinegroup.sunshinelab.engine.objects.ScreenObject;\n" +
                    "\n" +
                    "import java.util.Arrays;\n" +
                    "import java.lang.String;\n" +
                    "\n" +
                    "public class HelloWorld{\n" +
                    "    public static void main(final String[] args) {\n" +
                    "        ((ScreenObject)Statics.userObjects.get(1)).rotation-=.1;\n" +
                    "    }\n" +
                    "}";
            SimpleCompiler sc = new SimpleCompiler();
            try {
                sc.cook(testString1);
                b=sc.getBytecodes().get("HelloWorld");
//                FileOutputStream fis=new FileOutputStream("HelloWorld.class");
//                fis.write(b);
//                fis.close();
            } catch (CompileException e) {
                e.printStackTrace();
            }

            System.out.println(Arrays.toString(b));




//            byte[] b = new byte[]{-54, -2, -70, -66, 0, 3, 0, 45, 0, 26, 7, 0, 2, 1, 0, 10, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 7, 0, 4, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 4, 109, 97, 105, 110, 1, 0, 22, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 8, 0, 8, 1, 0, 90, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 35, 109, 97, 105, 110, 40, 91, 76, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 116, 114, 105, 110, 103, 59, 41, 107, 111, 97, 108, 97, 46, 100, 121, 110, 97, 109, 105, 99, 106, 97, 118, 97, 46, 105, 110, 116, 101, 114, 112, 114, 101, 116, 101, 114, 46, 84, 114, 101, 101, 67, 108, 97, 115, 115, 76, 111, 97, 100, 101, 114, 64, 53, 102, 100, 48, 100, 53, 97, 101, 10, 0, 10, 0, 12, 7, 0, 11, 1, 0, 45, 107, 111, 97, 108, 97, 47, 100, 121, 110, 97, 109, 105, 99, 106, 97, 118, 97, 47, 105, 110, 116, 101, 114, 112, 114, 101, 116, 101, 114, 47, 84, 114, 101, 101, 73, 110, 116, 101, 114, 112, 114, 101, 116, 101, 114, 12, 0, 13, 0, 14, 1, 0, 12, 105, 110, 118, 111, 107, 101, 77, 101, 116, 104, 111, 100, 1, 0, 75, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 0, 4, 67, 111, 100, 101, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 8, 0, 19, 1, 0, 73, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 35, 60, 105, 110, 105, 116, 62, 40, 41, 107, 111, 97, 108, 97, 46, 100, 121, 110, 97, 109, 105, 99, 106, 97, 118, 97, 46, 105, 110, 116, 101, 114, 112, 114, 101, 116, 101, 114, 46, 84, 114, 101, 101, 67, 108, 97, 115, 115, 76, 111, 97, 100, 101, 114, 64, 53, 102, 100, 48, 100, 53, 97, 101, 10, 0, 10, 0, 21, 12, 0, 22, 0, 23, 1, 0, 18, 105, 110, 116, 101, 114, 112, 114, 101, 116, 65, 114, 103, 117, 109, 101, 110, 116, 115, 1, 0, 58, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 41, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 10, 0, 3, 0, 25, 12, 0, 16, 0, 17, 0, 33, 0, 1, 0, 3, 0, 0, 0, 0, 0, 2, 0, 9, 0, 5, 0, 6, 0, 1, 0, 15, 0, 0, 0, 29, 0, 6, 0, 2, 0, 0, 0, 17, 4, -67, 0, 3, 76, 43, 3, 42, 83, 18, 7, 1, 43, -72, 0, 9, -79, 0, 0, 0, 0, 0, 1, 0, 16, 0, 17, 0, 1, 0, 15, 0, 0, 0, 36, 0, 6, 0, 3, 0, 0, 0, 24, 3, -67, 0, 3, 77, 18, 18, 44, -72, 0, 20, 76, 42, -73, 0, 24, 18, 18, 42, 44, -72, 0, 9, -79, 0, 0, 0, 0, 0, 0};
            jjjvmClass = new JJJVMClassImpl(new ByteArrayInputStream(b), provider);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Gdx.app.log("out", throwable.toString());
        }




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

        //------------------------------------------------------------
        try {
            jjjvmClass.findMethod("main", "([Ljava/lang/String;)V").invoke(null, null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //------------------------------------------------------------
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
