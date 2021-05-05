package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.utils.Array;

public interface AtlasDownloadListener {
    void atlas(Array<CustomTextureAtlas.AtlasRegion> regions);
    void failed(Throwable t);
}
