package com.klemstinegroup.sunshinelab.engine.util;

public interface IPFSCIDListener {
    void cid(String cid);
    void uploadFailed(Throwable t);
}
