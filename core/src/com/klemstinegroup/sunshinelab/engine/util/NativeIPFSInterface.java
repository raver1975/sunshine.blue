package com.klemstinegroup.sunshinelab.engine.util;

public interface NativeIPFSInterface {
    public void uploadFile(byte[] data, String mime,IPFSResponseListener listener);
}
