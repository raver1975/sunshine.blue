package com.klemstinegroup.sunshinelab.engine.util;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngWriter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.*;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class IPFSUtils {

    public static void pinFile(String cid){
        String url="https://api.pinata.cloud/pinning/pinByHash";
        String authorization="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiI5NjMyZTdmMC1lODRiLTRjNzYtYTU2Yy0xZGE2YjgwNGI0YzAiLCJlbWFpbCI6InBhdWxrbGVtc3RpbmVAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBpbl9wb2xpY3kiOnsicmVnaW9ucyI6W3siaWQiOiJOWUMxIiwiZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjF9XSwidmVyc2lvbiI6MX0sIm1mYV9lbmFibGVkIjpmYWxzZX0sImF1dGhlbnRpY2F0aW9uVHlwZSI6InNjb3BlZEtleSIsInNjb3BlZEtleUtleSI6ImI5MTJhMjE1MTJlMDYzNmZhZjRkIiwic2NvcGVkS2V5U2VjcmV0IjoiNWYwZGYwODIwOTQzM2NiY2ZmNjU0MDg4MzMxMDI3OWZlYjYxYWU0ODk4NzAyMWQ5ZTVhODNiMTU1MWQ5NTQxZiIsImlhdCI6MTYxNzk3NTEyNX0.1Mpg1X9X8XTxoLuiEdvcNW3Z7iMEkkhsSJn7hyexXvM";

        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url).timeout(1000000).build();
        request.setHeader("Authorization","Bearer "+authorization);
        String sss="{\"hashToPin\": \""+cid+"\"}";
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Content-Length", sss.length()+"");
        request.setContent(sss);
        Gdx.app.log("hashtopin",sss);
        Net.HttpResponseListener listener = new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String res = httpResponse.getResultAsString();
                Gdx.app.log("post",request.getContent());
                Gdx.app.log("pin",res);
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

    public static void uploadFile(byte[] data, String mime, IPFSResponseListener listen) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
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
                String datauri = "data:" + mime + ";base64," + new String(Base64Coder.encode(data));
                request.setContent(out1 + datauri + out2);
                Net.HttpResponseListener listener = new Net.HttpResponseListener() {
                    @Override
                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                        String res = httpResponse.getResultAsString();
                        if (httpResponse.getStatus().getStatusCode() == HttpStatus.SC_OK) {
                            JsonReader jsonReader = new JsonReader();
                            JsonValue jons = jsonReader.parse(res);
                            String hash = jons.getString("Hash");
                            if (hash != null && listen != null) {
                                listen.qid(hash);
                                pinFile(hash);
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
        });
    }

    public static void writePng(Pixmap pixmap, Vector2 bounds,FileHandle mfh) {
        writePng(pixmap,bounds,mfh,null);
    }
    public static void writePng(Pixmap pixmap, Vector2 bounds, FileHandle mfh,IPFSResponseListener listener) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
//                MemoryFileHandle mfh = new MemoryFileHandle();
                ImageInfo imi = new ImageInfo((int)bounds.x, (int)bounds.y, 8, true);
                PngWriter pngw = new PngWriter(mfh.write(false), imi);
                int[] temp = new int[((int)bounds.x) * 4];
                Color col = new Color();
                for (int i = 0; i < (int)bounds.y; i++) {
                    for (int j = 0; j < (int)bounds.x; j++) {
                        int c = pixmap.getPixel(j, i);
                        col.set(c);
                        temp[j * 4 + 0] = (int) (col.r * 255);
                        temp[j * 4 + 1] = (int) (col.g * 255);
                        temp[j * 4 + 2] = (int) (col.b * 255);
                        temp[j * 4 + 3] = (int) (col.a * 255);
                    }
                    pngw.writeRowInt(temp);
                }
                pngw.end();
                if (listener!=null){
                    uploadFile(mfh.readBytes(), "image/png", listener);
                }
            }
//        });

        });
    }

    public static void uploadPng(Pixmap pixmap,Vector2 bounds, IPFSResponseListener listener) {
        MemoryFileHandle mfh = new MemoryFileHandle();
        writePng(pixmap, bounds,mfh,listener);
    }

    public static void uploadPng(Pixmap pixmap, Vector2 bounds) {
        uploadPng(pixmap,bounds, new IPFSResponseListener() {
            @Override
            public void qid(String qid) {
                Gdx.app.log("qid",qid);
                openIPFSViewer(qid);
            }
        });
    }

    public static void openIPFSViewer(String qid) {
        Gdx.net.openURI(Statics.IPFSGateway + Statics.IPFSMediaViewer + "?url=" + qid);
    }
}
