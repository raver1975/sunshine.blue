package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.utils.ByteArray;

public class NativeIPFS implements NativeIPFSInterface {
    @Override
    public void uploadFile(byte[] data, String mime, IPFSCIDListener listener) {
        IPFSUtils.uploadFile(data, mime, listener);
    }

    @Override
    public void downloadFile(String cid,IPFSFileListener listener) {
        IPFSUtils.downloadFromIPFS(cid,listener);
    }
}
