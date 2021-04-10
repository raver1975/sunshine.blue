package com.klemstinegroup.sunshinelab.engine.util;

import com.klemstinegroup.sunshinelab.engine.util.IPFSResponseListener;
import com.klemstinegroup.sunshinelab.engine.util.IPFSUtils;
import com.klemstinegroup.sunshinelab.engine.util.NativeIPFSInterface;

public class NativeIPFS implements NativeIPFSInterface {
    @Override
    public void uploadFile(byte[] data, String mime,IPFSResponseListener listener) {
        IPFSUtils.uploadFile(data, mime, listener);
    }
}
