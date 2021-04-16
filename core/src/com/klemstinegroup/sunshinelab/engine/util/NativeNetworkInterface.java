package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.utils.ByteArray;

public interface NativeNetworkInterface {
    public void uploadIPFS(byte[] data, String mime, IPFSCIDListener listener);
    public void downloadIPFS(String cid, IPFSFileListener listener);
    public void downloadFile(String url, IPFSFileListener listener);
}