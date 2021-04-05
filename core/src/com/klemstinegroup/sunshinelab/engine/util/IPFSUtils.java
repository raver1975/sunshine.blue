package com.klemstinegroup.sunshinelab.engine.util;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngWriter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.*;

public class IPFSUtils {
    public static void uploadFile(byte[] data, String mime,IPFSResponseListener listen) {
        Gdx.app.log("upload", data.length + "");
        String url = "https://ipfs.infura.io:5001/api/v0/add";
        String boundary = "12345678901234567890"; // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url).timeout(1000000).build();
        request.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        String out1 = "--" + boundary +
                CRLF + "Content-Disposition: form-data; name=\"file\"" +
                CRLF + "Content-Type: text/plain" +
                CRLF + CRLF;
        String out2 = CRLF + "--" + boundary + "--" + CRLF;
        String datauri = "data:"+mime+";base64," + new String(Base64Coder.encode(data));
        request.setContent(out1 + datauri + out2);
        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
                    JsonReader jsonReader = new JsonReader();
                    JsonValue jons = jsonReader.parse(res);
                    String hash = jons.getString("Hash");
                    if (hash != null) {
                        listen.qid(hash);
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
        Gdx.net.sendHttpRequest(request, listener);
    }


    public static void writePng(Pixmap pixmap) {
        MemoryFileHandle mfh = new MemoryFileHandle();
        ImageInfo imi = new ImageInfo(pixmap.getHeight(), pixmap.getWidth(), 8, true);
        PngWriter pngw = new PngWriter(mfh.write(false), imi);
        int[] temp = new int[pixmap.getHeight()*4];
        Color col=new Color();
        for (int i = 0; i < pixmap.getWidth(); i++) {
            for (int j = 0; j < pixmap.getHeight(); j++) {
                int c=pixmap.getPixel(j,i);
                col.set(c);
                temp[j*4+0]= (int) (col.r*255);
                temp[j*4+1]= (int) (col.g*255);
                temp[j*4+2]= (int) (col.b*255);
                temp[j*4+3]= (int) (col.a*255);
            }
            pngw.writeRowInt(temp);
        }
        pngw.end();

        uploadFile(mfh.readBytes(),"image/png", new IPFSResponseListener() {
            @Override
            public void qid(String qid) {
                Gdx.net.openURI("https://ipfs.io/ipfs/QmWWoB9DUFXz8v1ZVGXT8KjjZ7r7kbUQJPzPDxfpz36ei6?url=" + qid);
            }
        });

    }
}
