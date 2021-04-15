package com.klemstinegroup.sunshinelab.colorpicker;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

public class TenPatchData {
        public IntArray horizontalStretchAreas = new IntArray();
        public IntArray verticalStretchAreas = new IntArray();
        public int contentLeft;
        public int contentRight;
        public int contentTop;
        public int contentBottom;
        public boolean tile;
        public String colorName;
        public String color1Name;
        public String color2Name;
        public String color3Name;
        public String color4Name;
        public int offsetX;
        public int offsetY;
        public int offsetXspeed;
        public int offsetYspeed;
        public float frameDuration;
        public Array<String> regionNames = new Array<>();
        public transient Array<TextureRegion> regions;
        public int playMode;
    
        public TenPatchData() {
            clear();
        }
        
        public TenPatchData(TenPatchData other) {
            set(other);
        }
    
        public void clear() {
            horizontalStretchAreas.clear();
            verticalStretchAreas.clear();
            contentLeft = 0;
            contentRight = 0;
            contentTop = 0;
            contentBottom = 0;
            tile = false;
            colorName = null;
            color1Name = null;
            color2Name = null;
            color3Name = null;
            color4Name = null;
            offsetX = 0;
            offsetY = 0;
            offsetXspeed = 0;
            offsetYspeed = 0;
            frameDuration = .03f;
            playMode = TenPatchDrawable.PlayMode.LOOP;
        }
    
        public void removeInvalidStretchAreas(boolean horizontal) {
            IntArray stretchAreas = horizontal ? horizontalStretchAreas : verticalStretchAreas;
            for (int i = 0; i + 1 < stretchAreas.size; i += 2) {
                if (stretchAreas.get(i) > stretchAreas.get(i + 1)) {
                    stretchAreas.removeRange(i, i + 1);
                    i -= 2;
                }
            }
        }
    
        public void combineContiguousSretchAreas(boolean horizontal) {
            IntArray stretchAreas = horizontal ? horizontalStretchAreas : verticalStretchAreas;
            for (int i = 1; i + 1 < stretchAreas.size; i += 2) {
            
                if (stretchAreas.get(i) == stretchAreas.get(i + 1) - 1) {
                    stretchAreas.removeRange(i, i + 1);
                    i -= 2;
                }
            }
        }
    
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TenPatchData) {
                TenPatchData other = (TenPatchData) obj;
                return horizontalStretchAreas.equals(other.horizontalStretchAreas) &&
                        verticalStretchAreas.equals(other.verticalStretchAreas) &&
                        contentLeft == other.contentLeft && contentRight == other.contentRight &&
                        contentTop == other.contentTop && contentBottom == other.contentBottom && tile == other.tile &&
                        offsetX == other.offsetX && offsetY == other.offsetY && offsetXspeed == other.offsetXspeed && offsetYspeed == other.offsetYspeed &&
                        (colorName == null && other.colorName == null || colorName != null && colorName.equals(other.colorName)) &&
                        (color1Name == null && other.color1Name == null || color1Name != null && color1Name.equals(other.color1Name)) &&
                        (color2Name == null && other.color2Name == null || color2Name != null && color2Name.equals(other.color2Name)) &&
                        (color3Name == null && other.color3Name == null || color3Name != null && color3Name.equals(other.color3Name)) &&
                        (color4Name == null && other.color4Name == null || color4Name != null && color4Name.equals(other.color4Name));
            } else {
                return false;
            }
        }
    
        public void set(TenPatchData other) {
            horizontalStretchAreas = new IntArray(other.horizontalStretchAreas);
            verticalStretchAreas = new IntArray(other.verticalStretchAreas);
            contentLeft = other.contentLeft;
            contentRight = other.contentRight;
            contentTop = other.contentTop;
            contentBottom = other.contentBottom;
            tile = other.tile;
            colorName = other.colorName;
            color1Name = other.color1Name;
            color2Name = other.color2Name;
            color3Name = other.color3Name;
            color4Name = other.color4Name;
            offsetX = other.offsetX;
            offsetY = other.offsetY;
            offsetXspeed = other.offsetXspeed;
            offsetYspeed = other.offsetYspeed;
            frameDuration = other.frameDuration;
            regionNames = new Array<>(other.regionNames);
            regions = other.regions == null ? null : new Array<>(other.regions);
            playMode = other.playMode;
        }
    }