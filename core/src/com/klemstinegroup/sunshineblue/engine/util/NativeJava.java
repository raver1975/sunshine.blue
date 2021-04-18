package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;

public class NativeJava implements NativeInterface {
    @Override
    public void uploadIPFS(byte[] data, IPFSCIDListener listener) {
        IPFSUtils.uploadFile(data, listener);
    }

    @Override
    public void downloadIPFS(String cid, IPFSFileListener listener) {
        IPFSUtils.downloadFromIPFS(cid, listener);
    }

    @Override
    public void downloadFile(String url, boolean b, IPFSFileListener listener) {
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl(url);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.log("downloaded",url);
                byte[] b=httpResponse.getResult();
                listener.downloaded(b);
            }

            @Override
            public void failed(Throwable t) {
                listener.downloadFailed(t);
            }

            @Override
            public void cancelled() {

            }
        });
    }

    @Override
    public void downloadPixmap(String url, Pixmap.DownloadPixmapResponseListener listener) {
        Pixmap.downloadFromUrl(url,listener);
    }

    @Override
    public void doneSavingScene(String cid) {
        Gdx.app.log("scene saved",cid);
    }
}
