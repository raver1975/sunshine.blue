package com.klemstinegroup.sunshinelab.engine.util;

import com.badlogic.gdx.utils.ByteArray;

public interface NativeIPFSInterface {
    public void uploadFile(byte[] data, String mime, IPFSCIDListener listener);
    public void downloadFile(String cid,IPFSFileListener listener);
}
