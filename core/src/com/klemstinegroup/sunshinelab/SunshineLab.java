package com.klemstinegroup.sunshinelab;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

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
        Statics.objects.insert(0,ov);
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
        Statics.gifEncoderA.setDelay(10);
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
        for (BaseObject bo : Statics.objects) {

            if (bo instanceof Drawable) {
                ((Drawable) bo).draw(Statics.batch);
//                ((ScreenObject)bo).rotate(MathUtils.random(-.2f,2f));
//                ((ScreenObject)bo).setScale(((ScreenObject)bo).getScale()+MathUtils.random(-.01f,.01f));
            }
            Statics.batch.setTransformMatrix(mx4Batch);
        }

//        int pix[][]=FrameBufferUtils.drawObjectsInt(viewport,Statics.objects,200,200);
//        System.out.println(cntr);
        if (cntr < 170 && cntr > 60) {

//                Statics.gifEncoder.addImage(FrameBufferUtils.drawObjectsInt(viewport, Statics.objects, 400, 400), Statics.gifOptions);
            Statics.gifEncoderA.addFrame(FrameBufferUtils.drawObjectsPix(viewport, Statics.objects, 400, 400));

        } else if (cntr == 170) {
//                Statics.gifEncoder.finishEncoding();
            Statics.gifEncoderA.finish();
            uploadFile(gifEncoderFile.readBytes());
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

    public void uploadFile(byte[] data) {
        Gdx.app.log("upload", data.length + "");
        String url = "https://ipfs.infura.io:5001/api/v0/add";
//        String param = "file";
//        File textFile = new File("/path/to/file.txt");
//        File binaryFile = new File("/path/to/file.bin");
        String boundary = "12345678901234567890"; // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url).timeout(1000000).build();
        request.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        Gdx.app.log("test", "1");
        String out1 = "--" + boundary +
                CRLF + "Content-Disposition: form-data; name=\"file\"" +
                CRLF + "Content-Type: image/gif" +
                CRLF + CRLF;
        String out2 = CRLF + "--" + boundary + "--" + CRLF;
        ByteArray byteArr = new ByteArray();
        byteArr.addAll(new byte[]{45, 45, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 68, 105, 115, 112, 111, 115, 105, 116, 105, 111, 110, 58, 32, 102, 111, 114, 109, 45, 100, 97, 116, 97, 59, 32, 110, 97, 109, 101, 61, 34, 102, 105, 108, 101, 34, 13, 10, 67, 111, 110, 116, 101, 110, 116, 45, 84, 121, 112, 101, 58, 32, 105, 109, 97, 103, 101, 47, 103, 105, 102, 13, 10, 13, 10});
        byteArr.addAll(data);
        byteArr.addAll(new byte[]{13, 10, 45, 45, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 45, 45, 13, 10});
        Gdx.app.log("test", "2");
//        request.setContent(new InputStream() {
//            int cnt = 0;
//
//            @Override
//            public int available(){
//                return byteArr.size;
//            }
//
//            @Override
//            public int read(byte[] b, int off, int len) throws IOException {
//                Gdx.app.log("test",b.length+"");
//                int g=0;
//                for (int i=0;i<b.length;i++){
//                    g=read();
//                    if (g>-1)b[i+off]= (byte)(g&0xff);
//                    else return -1;
//                }
//                return b.length;
//            }
//
//            @Override
//            public int read(byte[] b) throws IOException {
//                Gdx.app.log("test",b.length+"");
//                int g=0;
//                for (int i=0;i<b.length;i++){
//                    g=read();
//                    if (g>-1)b[i]= (byte)(g&0xff);
//                    else return -1;
//                }
//                return b.length;
//            }
//
//            @Override
//            public int read() throws IOException {
//                if (cnt >= byteArr.size) return -1;
//                else return byteArr.get(cnt++) & 0xff;
//            }
//        }, byteArr.size);
        CharArray ca = new CharArray();
//
//        StringBuffer ss=new StringBuffer();
        for (int i = 0; i < data.length / 2; i++) {
            ca.add((char) ((((data[i * 2+1] << 8)) | (data[i * 2]))&0xffff));
        }
//        new String(ca.toArray(),true);
//        String datauri="<html><body><img src='html><body><img src='data:image/gif;base64,"+new String(Base64Coder.encode(data))+"'/></body></html>";
        String datauri="data:image/gif;base64,"+new String(Base64Coder.encode(data));

        request.setContent(out1+datauri+out2);
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
                        Gdx.net.openURI("https://ipfs.io/ipfs/QmWWoB9DUFXz8v1ZVGXT8KjjZ7r7kbUQJPzPDxfpz36ei6?url=" + hash);
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
//
//    public byte[] GetBytes(String str)
//    {
//        char[] chars = str.toCharArray();
//        byte[] bytes = new byte[chars.length * 2];
//        for (int i = 0; i < chars.length; i++)
//        {
//            bytes[i * 2] = (byte) (chars[i] >> 8);
//            bytes[i * 2 + 1] = (byte) chars[i];
//        }
//
//        return bytes;
//    }
//
//    public String GetString(byte[] bytes) {
//        char[] chars2 = new char[bytes.length / 2];
//        for (int i = 0; i < chars2.length; i++)
//            chars2[i] = (char) ((bytes[i * 2+1] << 8) + (bytes[i * 2 ] & 0xFF));
//        return new String(chars2);
//
//    }

    public static String getString2(byte[] bytes, int bytesPerChar) {
        if (bytes == null) throw new IllegalArgumentException("bytes cannot be null");
        if (bytesPerChar < 1) throw new IllegalArgumentException("bytesPerChar must be greater than 1");

        final int length = bytes.length / bytesPerChar;
        StringBuilder sb = new StringBuilder();


        for (int i = 0; i < length; i++) {
            char thisChar = 0;

            for (int j = 0; j < bytesPerChar; j++) {
                int shift = (bytesPerChar - 1 - j) * 8;
                thisChar |= (0x000000FF << shift) & (((int) bytes[i * bytesPerChar + j]) << shift);
            }

            sb.append(thisChar);
        }

        return sb.toString();
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
