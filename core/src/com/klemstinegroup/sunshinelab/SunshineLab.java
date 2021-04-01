package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.igormaznitsa.jjjvm.impl.JJJVMClassImpl;
import com.igormaznitsa.jjjvm.impl.jse.JSEProviderImpl;
import com.igormaznitsa.jjjvm.model.JJJVMProvider;
import com.klemstinegroup.sunshinelab.engine.util.FrameBufferUtils;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.*;
import com.klemstinegroup.sunshinelab.engine.util.MemoryFileHandle;
import com.klemstinegroup.sunshinelab.engine.util.UUID;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.squareup.gifencoder.ImageOptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.badlogic.gdx.Application.LOG_INFO;
import static com.klemstinegroup.sunshinelab.engine.Statics.gifEncoderA;
import static com.klemstinegroup.sunshinelab.engine.Statics.gifEncoderFile;

public class SunshineLab extends ApplicationAdapter implements InputProcessor {

    //    Camera camera;

    Viewport viewport;
    public static Matrix4 mx4Batch = new Matrix4();
    Vector2 touchdown = new Vector2();
    JJJVMClassImpl jjjvmClass = null;
    int cntr = 0;
    int[][] dataFrame;

    @Override
    public void create() {
        Gdx.app.setLogLevel(LOG_INFO);
//        img = new Texture("badlogic.jpg");

        Statics.objects.add(new RectTextureObject("i.redd.it/0h1nbwj4bto61.jpg"));
        Statics.objects.add(new RectTextureObject("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/PNG_Test.png/477px-PNG_Test.png"));
        ((ScreenObject) Statics.objects.get(1)).position.set(-100, -100, 0);

        ((ScreenObject) Statics.objects.get(0)).scale = .1f;
        ((ScreenObject) Statics.objects.get(1)).scale = .4f;
//        FontObject fo = null;
//        for (int i=-1;i<2;i++) {
//            for (int j = -1; j < 2; j++) {
//         fo = new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], MathUtils.random(30, 60));
//        fo.position.set((i) * 200 - fo.center.x, (j) * 100 - fo.center.y,0);
//        fo.scale=.7f;
//        Statics.objects.add(fo);
////
//            }
//
//        }
//        fo.center.set(0,0,0);
        viewport = new ScreenViewport();

        InputMultiplexer im = new InputMultiplexer();

        BasicUIOverlay ov = new BasicUIOverlay();
        Gdx.input.setInputProcessor(im);
        im.addProcessor(this);
        im.addProcessor(ov.stage);
        Statics.objects.add(ov);
        mx4Batch = Statics.batch.getTransformMatrix().cpy();
//        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


//        generator.dispose(); // don't forget to dispose to avoid memory leaks!

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
            byte[] b = new byte[]{-54, -2, -70, -66, 0, 0, 0, 52, 0, 64, 10, 0, 13, 0, 27, 9, 0, 28, 0, 29, 8, 0, 30, 8, 0, 31, 11, 0, 32, 0, 33, 9, 0, 34, 0, 35, 10, 0, 36, 0, 37, 10, 0, 38, 0, 39, 10, 0, 36, 0, 40, 7, 0, 41, 9, 0, 10, 0, 42, 7, 0, 43, 7, 0, 44, 1, 0, 6, 60, 105, 110, 105, 116, 62, 1, 0, 3, 40, 41, 86, 1, 0, 4, 67, 111, 100, 101, 1, 0, 15, 76, 105, 110, 101, 78, 117, 109, 98, 101, 114, 84, 97, 98, 108, 101, 1, 0, 18, 76, 111, 99, 97, 108, 86, 97, 114, 105, 97, 98, 108, 101, 84, 97, 98, 108, 101, 1, 0, 4, 116, 104, 105, 115, 1, 0, 43, 76, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 59, 1, 0, 4, 109, 97, 105, 110, 1, 0, 22, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 4, 97, 114, 103, 115, 1, 0, 19, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 15, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 46, 106, 97, 118, 97, 12, 0, 14, 0, 15, 7, 0, 45, 12, 0, 46, 0, 47, 1, 0, 3, 111, 117, 116, 1, 0, 11, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 7, 0, 48, 12, 0, 49, 0, 50, 7, 0, 51, 12, 0, 52, 0, 53, 7, 0, 54, 12, 0, 55, 0, 56, 7, 0, 57, 12, 0, 58, 0, 59, 12, 0, 60, 0, 61, 1, 0, 58, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 111, 98, 106, 101, 99, 116, 115, 47, 83, 99, 114, 101, 101, 110, 79, 98, 106, 101, 99, 116, 12, 0, 62, 0, 63, 1, 0, 41, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 72, 101, 108, 108, 111, 87, 111, 114, 108, 100, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 20, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 71, 100, 120, 1, 0, 3, 97, 112, 112, 1, 0, 30, 76, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 65, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 59, 1, 0, 28, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 65, 112, 112, 108, 105, 99, 97, 116, 105, 111, 110, 1, 0, 3, 108, 111, 103, 1, 0, 39, 40, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 41, 86, 1, 0, 45, 99, 111, 109, 47, 107, 108, 101, 109, 115, 116, 105, 110, 101, 103, 114, 111, 117, 112, 47, 115, 117, 110, 115, 104, 105, 110, 101, 108, 97, 98, 47, 101, 110, 103, 105, 110, 101, 47, 83, 116, 97, 116, 105, 99, 115, 1, 0, 7, 111, 98, 106, 101, 99, 116, 115, 1, 0, 30, 76, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 59, 1, 0, 28, 99, 111, 109, 47, 98, 97, 100, 108, 111, 103, 105, 99, 47, 103, 100, 120, 47, 117, 116, 105, 108, 115, 47, 65, 114, 114, 97, 121, 1, 0, 7, 116, 111, 65, 114, 114, 97, 121, 1, 0, 21, 40, 41, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 0, 16, 106, 97, 118, 97, 47, 117, 116, 105, 108, 47, 65, 114, 114, 97, 121, 115, 1, 0, 8, 116, 111, 83, 116, 114, 105, 110, 103, 1, 0, 39, 40, 91, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110, 103, 59, 1, 0, 3, 103, 101, 116, 1, 0, 21, 40, 73, 41, 76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 59, 1, 0, 8, 114, 111, 116, 97, 116, 105, 111, 110, 1, 0, 1, 70, 0, 33, 0, 12, 0, 13, 0, 0, 0, 0, 0, 2, 0, 1, 0, 14, 0, 15, 0, 1, 0, 16, 0, 0, 0, 47, 0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 2, 0, 17, 0, 0, 0, 6, 0, 1, 0, 0, 0, 10, 0, 18, 0, 0, 0, 12, 0, 1, 0, 0, 0, 5, 0, 19, 0, 20, 0, 0, 0, -119, 0, 21, 0, 22, 0, 1, 0, 16, 0, 0, 0, 105, 0, 3, 0, 1, 0, 0, 0, 51, -78, 0, 2, 18, 3, 18, 4, -71, 0, 5, 3, 0, -78, 0, 2, 18, 3, -78, 0, 6, -74, 0, 7, -72, 0, 8, -71, 0, 5, 3, 0, -78, 0, 6, 3, -74, 0, 9, -64, 0, 10, 89, -76, 0, 11, 12, 98, -75, 0, 11, -79, 0, 0, 0, 2, 0, 17, 0, 0, 0, 18, 0, 4, 0, 0, 0, 12, 0, 12, 0, 13, 0, 31, 0, 14, 0, 50, 0, 15, 0, 18, 0, 0, 0, 12, 0, 1, 0, 0, 0, 51, 0, 23, 0, 24, 0, 0, 0, 1, 0, 25, 0, 0, 0, 2, 0, 26};
            jjjvmClass = new JJJVMClassImpl(new ByteArrayInputStream(b), provider);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Gdx.app.log("out", throwable.toString());
        }
        //--------------------------------------------------------------------------------------------------
        viewport.apply();
        gifEncoderFile = new MemoryFileHandle();

//        OutputStream outputStream = gifEncoderFile.write(false);
        Statics.gifOptions = new ImageOptions();
//            Statics.gifEncoder = new GifEncoder(outputStream, 400, 400, 0);
        Statics.gifEncoderA = new AnimatedGifEncoder();
        Statics.gifEncoderA.setDelay(100);
        gifEncoderA.setSize(400, 400);
        Statics.gifEncoderA.start(gifEncoderFile);
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();

        Statics.batch.setProjectionMatrix(viewport.getCamera().combined);
        Statics.batch.begin();
        boolean flip = true;
        for (BaseObject bo : Statics.objects) {

            if (bo instanceof Drawable) {
                if (!(bo instanceof Overlay)) {
                    ((ScreenObject) bo).rotation += flip ? .001f : -.001f;
                    ((ScreenObject) bo).scale -= .00002f;
                    flip = !flip;
//                    byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

// This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
//                    for (int i = 4; i <= pixels.length; i += 4) {
//                        pixels[i - 1] = (byte) 255;
//                    }

//                    Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
//                    Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//                    BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
//                    MemoryFileHandle mfh=new MemoryFileHandle();

//                    PixmapIO.writePNG(mfh, pixmap);

//Statics.batch.draw(new Texture(pixmap),-100,-100);
//                    pixmap.dispose();
                }
                ((Drawable) bo).draw(Statics.batch);
//                ((ScreenObject)bo).rotate(MathUtils.random(-.2f,2f));
//                ((ScreenObject)bo).setScale(((ScreenObject)bo).getScale()+MathUtils.random(-.01f,.01f));
            }
            Statics.batch.setTransformMatrix(mx4Batch);
        }

//        int pix[][]=FrameBufferUtils.drawObjectsInt(viewport,Statics.objects,200,200);
//        System.out.println(cntr);
        if (cntr < 400 && cntr > 380) {

//                Statics.gifEncoder.addImage(FrameBufferUtils.drawObjectsInt(viewport, Statics.objects, 400, 400), Statics.gifOptions);
            Statics.gifEncoderA.addFrame(FrameBufferUtils.drawObjectsPix(viewport, Statics.objects, 400, 400));

        } else if (cntr == 400) {
//                Statics.gifEncoder.finishEncoding();
            Statics.gifEncoderA.finish();
            try {
                uploadFile(gifEncoderFile.readBytes(), UUID.randomUUID().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
//            Gdx.net.openURI(data);
//                System.exit(0);

        }
        cntr++;
        Statics.batch.end();
//        try {
//            jjjvmClass.findMethod("main", "([Ljava/lang/String;)V").invoke(null, null);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
    }

    public void uploadFile(byte[] data, String filename) throws IOException {
        Gdx.app.log("upload", data.length + "");
        String url = "https://ipfs.infura.io:5001/api/v0/add";
        String charset = "US-ASCII";
//        String param = "file";
//        File textFile = new File("/path/to/file.txt");
//        File binaryFile = new File("/path/to/file.bin");
        String boundary = "12345678901234567890"; // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
//        request.setHeader("Content-Transfer-Encoding", "base64");
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        ByteArrayOutputStream ba=new ByteArrayOutputStream();
//        PipedInputStream pi=new PipedInputStream();
//        BufferedOutputStream po=new BufferedOutputStream(new PipedOutputStream(pi));
//        ByteArrayOutputStream ba = new ByteArrayOutputStream(data.length+400);
//MemoryFileHandle ma=new MemoryFileHandle();
//        OutputStreamWriter writer = new OutputStreamWriter(ba);
        // Send normal param.
        Gdx.app.log("test", "1");
        String out="--" + boundary + CRLF+"Content-Disposition: form-data; name=\"file\"" + CRLF+"Content-Type: image/gif" + CRLF+CRLF+new String(data,"US-ASCII")+CRLF+"--" + boundary + "--" + CRLF;
        Gdx.app.log("test", "2");
//        ma.writeBytes(("--" + boundary + CRLF).getBytes("ISO-8859-1"),false);
//        Gdx.app.log("test", "2");
//        ma.writeBytes(("Content-Disposition: form-data; name=\"file\"" + CRLF).getBytes("ISO-8859-1"),true);
//        Gdx.app.log("test", "3");
//        ma.writeBytes(("Content-Type: image/gif" + CRLF).getBytes("ISO-8859-1"),true);
//        Gdx.app.log("test", "4");
//        ma.writeBytes(CRLF.getBytes("ISO-8859-1"),true);
//        Gdx.app.log("test", "5");
//        ma.writeBytes(data,true);
//        Gdx.app.log("test", "6");
//        ma.writeBytes(CRLF.getBytes("ISO-8859-1"),true);
//        ma.writeBytes(("--" + boundary + "--" + CRLF).getBytes("ISO-8859-1"),true);
//        writer.close();
//        int ff=0;
//        while(po.read()!=-1){
//            ff++;
//            System.out.println(ff+"\t"+(ff-data.length));
//        }
//        Gdx.app.log("tr", new String(ma.ba.toArray()));
        request.setContent(new BufferedInputStream(new ByteArrayInputStream(out.getBytes("US-ASCII"))),out.getBytes().length);
//        ba.close();
        Gdx.app.log("here", "here");
        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                Gdx.app.log("response", res);
                if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
//                    Gdx.app.log("response",httpResponse.getResultAsString());
//                    JsonParser jsonParser = new JsonParser();
                    JsonReader jsonReader = new JsonReader();

                    Gdx.app.log("code", res);
                    JsonValue jons = jsonReader.parse(res);
                    Gdx.app.log("code", jons.toString());
                    String hash = jons.getString("Hash");
                    if (hash != null) {
                        Gdx.net.openURI("https://ipfs.io/ipfs/" + hash + "?filename=" + filename);
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("response", t.toString());
            }

            @Override
            public void cancelled() {

            }
        };
        Gdx.app.log("here", "here");
        Gdx.net.sendHttpRequest(request, listener);
        Gdx.app.log("here", "here1");
//

    }

    public static String stringFromBytes(byte byteData[]) {
        char charData[] = new char[byteData.length];
        for(int i = 0; i < charData.length; i++) {
            charData[i] = (char) (((int) byteData[i]) & 0xFF);
        }
        return new String(charData);
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
        for (BaseObject bo : Statics.objects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).keyDown(keycode);
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (BaseObject bo : Statics.objects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).keyDown(keycode);
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        for (BaseObject bo : Statics.objects) {
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
        Statics.selectedobjects.clear();
        for (BaseObject bo : Statics.objects) {
            if (bo instanceof Touchable) {
                if (((Touchable) bo).isSelected(touchdown.cpy())) {
                    Statics.selectedobjects.add(bo);
                }
                ;
            }
        }
//        Gdx.input.setOnscreenKeyboardVisible(true, Input.OnscreenKeyboardType.URI);
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
