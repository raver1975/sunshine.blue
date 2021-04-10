package com.klemstinegroup.sunshinelab.engine.util;

public interface IPFSFileListener {
    void downloaded(byte[] file);
    void downloadFailed(Throwable t);
}
