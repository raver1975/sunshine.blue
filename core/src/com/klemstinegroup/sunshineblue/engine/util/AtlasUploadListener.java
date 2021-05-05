package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.utils.Array;

public interface AtlasUploadListener {
    void atlas(Array<String> strings);
    void failed(Throwable t);
}
