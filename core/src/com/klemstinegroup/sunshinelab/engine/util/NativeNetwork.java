package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Pixmap;

public class NativeNetwork implements NativeNetworkInterface {
    @Override
    public void uploadIPFS(byte[] data, IPFSCIDListener listener) {
        IPFSUtils.uploadFile(data, listener);
    }

    @Override
    public void downloadIPFS(String cid, IPFSFileListener listener) {
        IPFSUtils.downloadFromIPFS(cid, listener);
    }

    @Override
    public void downloadFile(String url, IPFSFileListener listener) {
        Gdx.app.log("downloading",url);
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
}
