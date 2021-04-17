package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.graphics.Pixmap;

public interface NativeNetworkInterface {
    public void uploadIPFS(byte[] data, IPFSCIDListener listener);
    public void downloadIPFS(String cid, IPFSFileListener listener);
    public void downloadFile(String url, boolean useCors, IPFSFileListener listener);
    public void downloadPixmap(String url, Pixmap.DownloadPixmapResponseListener listener);
}
