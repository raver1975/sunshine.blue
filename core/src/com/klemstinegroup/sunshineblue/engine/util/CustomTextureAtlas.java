package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.*;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;

public class CustomTextureAtlas implements Disposable {
    private final ObjectSet<Texture> textures = new ObjectSet(4);
    private final Array<AtlasRegion> regions = new Array();

    public CustomTextureAtlas() {
    }

    public CustomTextureAtlas(MemoryFileHandle packFile, Pixmap[] pixmaps, boolean flip) throws Exception {
        TextureAtlasData tad=new TextureAtlasData();
        tad.load(packFile,flip);
        int cnt = 0;
        for (TextureAtlasData.Page page : tad.pages) {
            page.texture = new Texture(pixmaps[cnt++]);
            page.texture.setFilter(page.minFilter, page.magFilter);
            page.texture.setWrap(page.uWrap, page.vWrap);
            textures.add(page.texture);

        }
        regions.ensureCapacity(tad.getRegions().size);
        Array<TextureAtlasData.Region> dd = tad.getRegions();
        for (TextureAtlasData.Region region : dd) {
            AtlasRegion atlasRegion = new AtlasRegion(region.page.texture, region.left, region.top, //
                    region.rotate ? region.height : region.width, //
                    region.rotate ? region.width : region.height);
            atlasRegion.index = region.index;
            atlasRegion.name = region.name;
            atlasRegion.offsetX = region.offsetX;
            atlasRegion.offsetY = region.offsetY;
            atlasRegion.originalHeight = region.originalHeight;
            atlasRegion.originalWidth = region.originalWidth;
            atlasRegion.rotate = region.rotate;
            atlasRegion.degrees = region.degrees;
            atlasRegion.names = region.names;
            atlasRegion.values = region.values;
            if (region.flip) atlasRegion.flip(false, true);
            regions.add(atlasRegion);
        }
    }

    public Array<AtlasRegion> getRegions() {
        return regions;
    }

    public ObjectSet<Texture> getTextures() {
        return textures;
    }

    public CustomTextureAtlas(TextureAtlasData textureAtlasData, AtlasDownloadListener listener) {
        load(textureAtlasData, listener);
    }

    public void load(TextureAtlasData data, AtlasDownloadListener listener) {
        textures.ensureCapacity(data.getPages().size);
        final int[] cnt = {data.getPages().size};
        for (TextureAtlasData.Page page : data.getPages()) {
            if (page.texture == null) {
                SunshineBlue.nativeNet.uploadIPFS(page.textureFile.readBytes(), new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + cid, new Pixmap.DownloadPixmapResponseListener() {
                            @Override
                            public void downloadComplete(Pixmap pixmap) {
                                page.texture = new Texture(pixmap);
                                page.texture.setFilter(page.minFilter, page.magFilter);
                                page.texture.setWrap(page.uWrap, page.vWrap);
                                textures.add(page.texture);
                                cnt[0]--;
                                if (cnt[0] == 0) {

                                    regions.ensureCapacity(data.getRegions().size);
                                    Array<TextureAtlasData.Region> dd = data.getRegions();
                                    for (TextureAtlasData.Region region : dd) {
                                        AtlasRegion atlasRegion = new AtlasRegion(region.page.texture, region.left, region.top, //
                                                region.rotate ? region.height : region.width, //
                                                region.rotate ? region.width : region.height);
                                        atlasRegion.index = region.index;
                                        atlasRegion.name = region.name;
                                        atlasRegion.offsetX = region.offsetX;
                                        atlasRegion.offsetY = region.offsetY;
                                        atlasRegion.originalHeight = region.originalHeight;
                                        atlasRegion.originalWidth = region.originalWidth;
                                        atlasRegion.rotate = region.rotate;
                                        atlasRegion.degrees = region.degrees;
                                        atlasRegion.names = region.names;
                                        atlasRegion.values = region.values;
                                        if (region.flip) atlasRegion.flip(false, true);
                                        regions.add(atlasRegion);
                                    }
                                    if (listener != null) {
                                        listener.atlas(regions);
                                    }
                                }
                            }

                            @Override
                            public void downloadFailed(Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void uploadFailed(Throwable t) {

                    }
                });

            }
        }

    }

    @Override
    public void dispose() {
        for (Texture texture : textures)
            texture.dispose();
        textures.clear(0);
    }

    static public class TextureAtlasData {
        final Array<TextureAtlasData.Page> pages = new Array();
        final Array<Region> regions = new Array();

        public TextureAtlasData() {
        }

        public TextureAtlasData(MemoryFileHandle packFile, MemoryFileHandle imagesDir, boolean flip) {
            load(packFile, imagesDir, flip);
        }


        public void load(MemoryFileHandle packFile,boolean flip){
            final String[] entry = new String[5];

            ObjectMap<String, TextureAtlasData.Field<TextureAtlasData.Page>> pageFields = new ObjectMap(15, 0.99f); // Size needed to avoid collisions.
            pageFields.put("size", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    page.width = Integer.parseInt(entry[1]);
                    page.height = Integer.parseInt(entry[2]);
                }
            });
            pageFields.put("format", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    page.format = Pixmap.Format.valueOf(entry[1]);
                }
            });
            pageFields.put("filter", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    page.minFilter = Texture.TextureFilter.valueOf(entry[1]);
                    page.magFilter = Texture.TextureFilter.valueOf(entry[2]);
                    page.useMipMaps = page.minFilter.isMipMap();
                }
            });
            pageFields.put("repeat", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    if (entry[1].indexOf('x') != -1) page.uWrap = Texture.TextureWrap.Repeat;
                    if (entry[1].indexOf('y') != -1) page.vWrap = Texture.TextureWrap.Repeat;
                }
            });
            pageFields.put("pma", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    page.pma = entry[1].equals("true");
                }
            });

            final boolean[] hasIndexes = {false};
            ObjectMap<String, TextureAtlasData.Field<TextureAtlasData.Region>> regionFields = new ObjectMap(127, 0.99f); // Size needed to avoid collisions.
            regionFields.put("xy", new TextureAtlasData.Field<TextureAtlasData.Region>() { // Deprecated, use bounds.
                public void parse(TextureAtlasData.Region region) {
                    region.left = Integer.parseInt(entry[1]);
                    region.top = Integer.parseInt(entry[2]);
                }
            });
            regionFields.put("size", new TextureAtlasData.Field<TextureAtlasData.Region>() { // Deprecated, use bounds.
                public void parse(TextureAtlasData.Region region) {
                    region.width = Integer.parseInt(entry[1]);
                    region.height = Integer.parseInt(entry[2]);
                }
            });
            regionFields.put("bounds", new TextureAtlasData.Field<TextureAtlasData.Region>() {
                public void parse(TextureAtlasData.Region region) {
                    region.left = Integer.parseInt(entry[1]);
                    region.top = Integer.parseInt(entry[2]);
                    region.width = Integer.parseInt(entry[3]);
                    region.height = Integer.parseInt(entry[4]);
                }
            });
            regionFields.put("offset", new TextureAtlasData.Field<TextureAtlasData.Region>() { // Deprecated, use offsets.
                public void parse(TextureAtlasData.Region region) {
                    region.offsetX = Integer.parseInt(entry[1]);
                    region.offsetY = Integer.parseInt(entry[2]);
                }
            });
            regionFields.put("orig", new TextureAtlasData.Field<TextureAtlasData.Region>() { // Deprecated, use offsets.
                public void parse(TextureAtlasData.Region region) {
                    region.originalWidth = Integer.parseInt(entry[1]);
                    region.originalHeight = Integer.parseInt(entry[2]);
                }
            });
            regionFields.put("offsets", new TextureAtlasData.Field<TextureAtlasData.Region>() {
                public void parse(TextureAtlasData.Region region) {
                    region.offsetX = Integer.parseInt(entry[1]);
                    region.offsetY = Integer.parseInt(entry[2]);
                    region.originalWidth = Integer.parseInt(entry[3]);
                    region.originalHeight = Integer.parseInt(entry[4]);
                }
            });
            regionFields.put("rotate", new TextureAtlasData.Field<TextureAtlasData.Region>() {
                public void parse(TextureAtlasData.Region region) {
                    String value = entry[1];
                    if (value.equals("true"))
                        region.degrees = 90;
                    else if (!value.equals("false")) //
                        region.degrees = Integer.parseInt(value);
                    region.rotate = region.degrees == 90;
                }
            });
            regionFields.put("index", new TextureAtlasData.Field<TextureAtlasData.Region>() {
                public void parse(TextureAtlasData.Region region) {
                    region.index = Integer.parseInt(entry[1]);
                    if (region.index != -1) hasIndexes[0] = true;
                }
            });

            BufferedReader reader = new BufferedReader(new InputStreamReader(packFile.read()), 1024);
            try {
                String line = reader.readLine();
                // Ignore empty lines before first entry.
                while (line != null && line.trim().length() == 0)
                    line = reader.readLine();
                // Header entries.
                while (true) {
                    if (line == null || line.trim().length() == 0) break;
                    if (readEntry(entry, line) == 0) break; // Silently ignore all header fields.
                    line = reader.readLine();
                }
                // Page and region entries.
                TextureAtlasData.Page page = null;
                Array<Object> names = null, values = null;
                while (true) {
                    if (line == null) break;
                    if (line.trim().length() == 0) {
                        page = null;
                        line = reader.readLine();
                    } else if (page == null) {
                        page = new TextureAtlasData.Page();
                        Gdx.app.log("line", line);
//                        page.textureFile = (MemoryFileHandle) imagesDir.child(line);
//                        Gdx.app.log("page size", "" + page.textureFile.ba.size);
                        while (true) {
                            if (readEntry(entry, line = reader.readLine()) == 0) break;
                            TextureAtlasData.Field field = pageFields.get(entry[0]);
                            if (field != null) field.parse(page); // Silently ignore unknown page fields.
                        }
                        pages.add(page);
                    } else {
                        TextureAtlasData.Region region = new TextureAtlasData.Region();
                        region.page = page;
                        region.name = line.trim();
                        if (flip) region.flip = true;
                        while (true) {
                            int count = readEntry(entry, line = reader.readLine());
                            if (count == 0) break;
                            TextureAtlasData.Field field = regionFields.get(entry[0]);
                            if (field != null)
                                field.parse(region);
                            else {
                                if (names == null) {
                                    names = new Array(8);
                                    values = new Array(8);
                                }
                                names.add(entry[0]);
                                int[] entryValues = new int[count];
                                for (int i = 0; i < count; i++) {
                                    try {
                                        entryValues[i] = Integer.parseInt(entry[i + 1]);
                                    } catch (NumberFormatException ignored) { // Silently ignore non-integer values.
                                    }
                                }
                                values.add(entryValues);
                            }
                        }
                        if (region.originalWidth == 0 && region.originalHeight == 0) {
                            region.originalWidth = region.width;
                            region.originalHeight = region.height;
                        }
                        if (names != null && names.size > 0) {
                            region.names = names.toArray(String.class);
                            region.values = values.toArray(int[].class);
                            names.clear();
                            values.clear();
                        }
                        regions.add(region);
                    }
                }
            } catch (Exception ex) {
                throw new GdxRuntimeException("Error reading texture atlas file: " + packFile, ex);
            } finally {
                StreamUtils.closeQuietly(reader);
            }

            if (hasIndexes[0]) {
                regions.sort(new Comparator<TextureAtlasData.Region>() {
                    public int compare(TextureAtlasData.Region region1, TextureAtlasData.Region region2) {
                        int i1 = region1.index;
                        if (i1 == -1) i1 = Integer.MAX_VALUE;
                        int i2 = region2.index;
                        if (i2 == -1) i2 = Integer.MAX_VALUE;
                        return i1 - i2;
                    }
                });
            }
            Gdx.app.log("load", "done");

//                        regions.add(region);

        }
        public void load(MemoryFileHandle packFile, MemoryFileHandle imagesDir,boolean flip) {
            final String[] entry = new String[5];

            ObjectMap<String, TextureAtlasData.Field<TextureAtlasData.Page>> pageFields = new ObjectMap(15, 0.99f); // Size needed to avoid collisions.
            pageFields.put("size", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    page.width = Integer.parseInt(entry[1]);
                    page.height = Integer.parseInt(entry[2]);
                }
            });
            pageFields.put("format", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    page.format = Pixmap.Format.valueOf(entry[1]);
                }
            });
            pageFields.put("filter", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    page.minFilter = Texture.TextureFilter.valueOf(entry[1]);
                    page.magFilter = Texture.TextureFilter.valueOf(entry[2]);
                    page.useMipMaps = page.minFilter.isMipMap();
                }
            });
            pageFields.put("repeat", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    if (entry[1].indexOf('x') != -1) page.uWrap = Texture.TextureWrap.Repeat;
                    if (entry[1].indexOf('y') != -1) page.vWrap = Texture.TextureWrap.Repeat;
                }
            });
            pageFields.put("pma", new TextureAtlasData.Field<TextureAtlasData.Page>() {
                public void parse(TextureAtlasData.Page page) {
                    page.pma = entry[1].equals("true");
                }
            });

            final boolean[] hasIndexes = {false};
            ObjectMap<String, TextureAtlasData.Field<TextureAtlasData.Region>> regionFields = new ObjectMap(127, 0.99f); // Size needed to avoid collisions.
            regionFields.put("xy", new TextureAtlasData.Field<TextureAtlasData.Region>() { // Deprecated, use bounds.
                public void parse(TextureAtlasData.Region region) {
                    region.left = Integer.parseInt(entry[1]);
                    region.top = Integer.parseInt(entry[2]);
                }
            });
            regionFields.put("size", new TextureAtlasData.Field<TextureAtlasData.Region>() { // Deprecated, use bounds.
                public void parse(TextureAtlasData.Region region) {
                    region.width = Integer.parseInt(entry[1]);
                    region.height = Integer.parseInt(entry[2]);
                }
            });
            regionFields.put("bounds", new TextureAtlasData.Field<TextureAtlasData.Region>() {
                public void parse(TextureAtlasData.Region region) {
                    region.left = Integer.parseInt(entry[1]);
                    region.top = Integer.parseInt(entry[2]);
                    region.width = Integer.parseInt(entry[3]);
                    region.height = Integer.parseInt(entry[4]);
                }
            });
            regionFields.put("offset", new TextureAtlasData.Field<TextureAtlasData.Region>() { // Deprecated, use offsets.
                public void parse(TextureAtlasData.Region region) {
                    region.offsetX = Integer.parseInt(entry[1]);
                    region.offsetY = Integer.parseInt(entry[2]);
                }
            });
            regionFields.put("orig", new TextureAtlasData.Field<TextureAtlasData.Region>() { // Deprecated, use offsets.
                public void parse(TextureAtlasData.Region region) {
                    region.originalWidth = Integer.parseInt(entry[1]);
                    region.originalHeight = Integer.parseInt(entry[2]);
                }
            });
            regionFields.put("offsets", new TextureAtlasData.Field<TextureAtlasData.Region>() {
                public void parse(TextureAtlasData.Region region) {
                    region.offsetX = Integer.parseInt(entry[1]);
                    region.offsetY = Integer.parseInt(entry[2]);
                    region.originalWidth = Integer.parseInt(entry[3]);
                    region.originalHeight = Integer.parseInt(entry[4]);
                }
            });
            regionFields.put("rotate", new TextureAtlasData.Field<TextureAtlasData.Region>() {
                public void parse(TextureAtlasData.Region region) {
                    String value = entry[1];
                    if (value.equals("true"))
                        region.degrees = 90;
                    else if (!value.equals("false")) //
                        region.degrees = Integer.parseInt(value);
                    region.rotate = region.degrees == 90;
                }
            });
            regionFields.put("index", new TextureAtlasData.Field<TextureAtlasData.Region>() {
                public void parse(TextureAtlasData.Region region) {
                    region.index = Integer.parseInt(entry[1]);
                    if (region.index != -1) hasIndexes[0] = true;
                }
            });

            BufferedReader reader = new BufferedReader(new InputStreamReader(packFile.read()), 1024);
            try {
                String line = reader.readLine();
                // Ignore empty lines before first entry.
                while (line != null && line.trim().length() == 0)
                    line = reader.readLine();
                // Header entries.
                while (true) {
                    if (line == null || line.trim().length() == 0) break;
                    if (readEntry(entry, line) == 0) break; // Silently ignore all header fields.
                    line = reader.readLine();
                }
                // Page and region entries.
                TextureAtlasData.Page page = null;
                Array<Object> names = null, values = null;
                while (true) {
                    if (line == null) break;
                    if (line.trim().length() == 0) {
                        page = null;
                        line = reader.readLine();
                    } else if (page == null) {
                        page = new TextureAtlasData.Page();
                        Gdx.app.log("line", line);
                        page.textureFile = (MemoryFileHandle) imagesDir.child(line);
                        Gdx.app.log("page size", "" + page.textureFile.ba.size);
                        while (true) {
                            if (readEntry(entry, line = reader.readLine()) == 0) break;
                            TextureAtlasData.Field field = pageFields.get(entry[0]);
                            if (field != null) field.parse(page); // Silently ignore unknown page fields.
                        }
                        pages.add(page);
                    } else {
                        TextureAtlasData.Region region = new TextureAtlasData.Region();
                        region.page = page;
                        region.name = line.trim();
                        if (flip) region.flip = true;
                        while (true) {
                            int count = readEntry(entry, line = reader.readLine());
                            if (count == 0) break;
                            TextureAtlasData.Field field = regionFields.get(entry[0]);
                            if (field != null)
                                field.parse(region);
                            else {
                                if (names == null) {
                                    names = new Array(8);
                                    values = new Array(8);
                                }
                                names.add(entry[0]);
                                int[] entryValues = new int[count];
                                for (int i = 0; i < count; i++) {
                                    try {
                                        entryValues[i] = Integer.parseInt(entry[i + 1]);
                                    } catch (NumberFormatException ignored) { // Silently ignore non-integer values.
                                    }
                                }
                                values.add(entryValues);
                            }
                        }
                        if (region.originalWidth == 0 && region.originalHeight == 0) {
                            region.originalWidth = region.width;
                            region.originalHeight = region.height;
                        }
                        if (names != null && names.size > 0) {
                            region.names = names.toArray(String.class);
                            region.values = values.toArray(int[].class);
                            names.clear();
                            values.clear();
                        }
                        regions.add(region);
                    }
                }
            } catch (Exception ex) {
                throw new GdxRuntimeException("Error reading texture atlas file: " + packFile, ex);
            } finally {
                StreamUtils.closeQuietly(reader);
            }

            if (hasIndexes[0]) {
                regions.sort(new Comparator<TextureAtlasData.Region>() {
                    public int compare(TextureAtlasData.Region region1, TextureAtlasData.Region region2) {
                        int i1 = region1.index;
                        if (i1 == -1) i1 = Integer.MAX_VALUE;
                        int i2 = region2.index;
                        if (i2 == -1) i2 = Integer.MAX_VALUE;
                        return i1 - i2;
                    }
                });
            }
            Gdx.app.log("load", "done");
        }

        public Array<CustomTextureAtlas.TextureAtlasData.Page> getPages() {
            return pages;
        }

        //
        public Array<Region> getRegions() {
            return regions;
        }

        static int readEntry(String[] entry, @Null String line) throws IOException {
            if (line == null) return 0;
            line = line.trim();
            if (line.length() == 0) return 0;
            int colon = line.indexOf(':');
            if (colon == -1) return 0;
            entry[0] = line.substring(0, colon).trim();
            for (int i = 1, lastMatch = colon + 1; ; i++) {
                int comma = line.indexOf(',', lastMatch);
                if (comma == -1) {
                    entry[i] = line.substring(lastMatch).trim();
                    return i;
                }
                entry[i] = line.substring(lastMatch, comma).trim();
                lastMatch = comma + 1;
                if (i == 4) return 4;
            }
        }

        static interface Field<T> {
            public void parse(T object);
        }

        static public class Page {
            /**
             * May be null if this page isn't associated with a file. In that case, {@link #texture} must be set.
             */
            public @Null
            MemoryFileHandle textureFile;
            /**
             * May be null if the texture is not yet loaded.
             */
            public @Null
            Texture texture;
            public float width, height;
            public boolean useMipMaps;
            public Pixmap.Format format = Pixmap.Format.RGBA8888;
            public Texture.TextureFilter minFilter = Texture.TextureFilter.Nearest, magFilter = Texture.TextureFilter.Nearest;
            public Texture.TextureWrap uWrap = Texture.TextureWrap.ClampToEdge, vWrap = Texture.TextureWrap.ClampToEdge;
            public boolean pma;
        }

        static public class Region {
            public TextureAtlasData.Page page;
            public String name;
            public int left, top, width, height;
            public float offsetX, offsetY;
            public int originalWidth, originalHeight;
            public int degrees;
            public boolean rotate;
            public int index = -1;
            public @Null
            String[] names;
            public @Null
            int[][] values;
            public boolean flip;

            public @Null
            int[] findValue(String name) {
                if (names != null) {
                    for (int i = 0, n = names.length; i < n; i++)
                        if (name.equals(names[i])) return values[i];
                }
                return null;
            }
        }
    }

    static public class AtlasRegion extends TextureRegion {
        /**
         * The number at the end of the original image file name, or -1 if none.<br>
         * <br>
         * When sprites are packed, if the original file name ends with a number, it is stored as the index and is not considered as
         * part of the sprite's name. This is useful for keeping animation frames in order.
         * //         * @see CustomTextureAtlas#findRegions(String)
         */
        public int index = -1;

        /**
         * The name of the original image file, without the file's extension.<br>
         * If the name ends with an underscore followed by only numbers, that part is excluded: underscores denote special
         * instructions to the texture packer.
         */
        public String name;

        /**
         * The offset from the left of the original image to the left of the packed image, after whitespace was removed for
         * packing.
         */
        public float offsetX;

        /**
         * The offset from the bottom of the original image to the bottom of the packed image, after whitespace was removed for
         * packing.
         */
        public float offsetY;

        /**
         * The width of the image, after whitespace was removed for packing.
         */
        public int packedWidth;

        /**
         * The height of the image, after whitespace was removed for packing.
         */
        public int packedHeight;

        /**
         * The width of the image, before whitespace was removed and rotation was applied for packing.
         */
        public int originalWidth;

        /**
         * The height of the image, before whitespace was removed for packing.
         */
        public int originalHeight;

        /**
         * If true, the region has been rotated 90 degrees counter clockwise.
         */
        public boolean rotate;

        /**
         * The degrees the region has been rotated, counter clockwise between 0 and 359. Most atlas region handling deals only with
         * 0 or 90 degree rotation (enough to handle rectangles). More advanced texture packing may support other rotations (eg, for
         * tightly packing polygons).
         */
        public int degrees;

        /**
         * Names for name/value pairs other than the fields provided on this class, each entry corresponding to {@link #values}.
         */
        public @Null
        String[] names;

        /**
         * Values for name/value pairs other than the fields provided on this class, each entry corresponding to {@link #names}.
         */
        public @Null
        int[][] values;

        public AtlasRegion(Texture texture, int x, int y, int width, int height) {
            super(texture, x, y, width, height);
            originalWidth = width;
            originalHeight = height;
            packedWidth = width;
            packedHeight = height;
        }

        public AtlasRegion(AtlasRegion region) {
            setRegion(region);
            index = region.index;
            name = region.name;
            offsetX = region.offsetX;
            offsetY = region.offsetY;
            packedWidth = region.packedWidth;
            packedHeight = region.packedHeight;
            originalWidth = region.originalWidth;
            originalHeight = region.originalHeight;
            rotate = region.rotate;
            degrees = region.degrees;
            names = region.names;
            values = region.values;
        }

        public AtlasRegion(TextureRegion region) {
            setRegion(region);
            packedWidth = region.getRegionWidth();
            packedHeight = region.getRegionHeight();
            originalWidth = packedWidth;
            originalHeight = packedHeight;
        }

        @Override
        /** Flips the region, adjusting the offset so the image appears to be flipped as if no whitespace has been removed for
         * packing. */
        public void flip(boolean x, boolean y) {
            super.flip(x, y);
            if (x) offsetX = originalWidth - offsetX - getRotatedPackedWidth();
            if (y) offsetY = originalHeight - offsetY - getRotatedPackedHeight();
        }

        /**
         * Returns the packed width considering the {@link #rotate} value, if it is true then it returns the packedHeight,
         * otherwise it returns the packedWidth.
         */
        public float getRotatedPackedWidth() {
            return rotate ? packedHeight : packedWidth;
        }

        /**
         * Returns the packed height considering the {@link #rotate} value, if it is true then it returns the packedWidth,
         * otherwise it returns the packedHeight.
         */
        public float getRotatedPackedHeight() {
            return rotate ? packedWidth : packedHeight;
        }

        public @Null
        int[] findValue(String name) {
            if (names != null) {
                for (int i = 0, n = names.length; i < n; i++)
                    if (name.equals(names[i])) return values[i];
            }
            return null;
        }

        public String toString() {
            return name;
        }
    }


}


/*

            TextureAtlasData tad = new TextureAtlasData();
            final String[] entry = new String[5];
            BufferedReader reader = new BufferedReader(new InputStreamReader(packFile.read()), 1024);
//        ObjectMap<String, TextureAtlasData.Field<TextureAtlasData.Page>> pageFields = new ObjectMap(15, 0.99f); // Size needed to avoid collisions.
//        ObjectMap<String, TextureAtlasData.Field<TextureAtlasData.Region>> regionFields = new ObjectMap(127, 0.99f); // Size needed to avoid collisions.
            try {
                String line = reader.readLine();
                // Ignore empty lines before first entry.
                while (line != null && line.trim().length() == 0)
                    line = reader.readLine();
                // Header entries.
                while (true) {
                    if (line == null || line.trim().length() == 0) break;
                    if (TextureAtlasData.readEntry(entry, line) == 0) break; // Silently ignore all header fields.
                    line = reader.readLine();
                }
                // Page and region entries.
                TextureAtlasData.Page page = null;
                Array<Object> names = null, values = null;
                while (true) {
                    if (line == null) break;
                    if (line.trim().length() == 0) {
                        page = null;
                        line = reader.readLine();
                    } else if (page == null) {
                        page = new TextureAtlasData.Page();
                        Gdx.app.log("line", line);
//                    page.textureFile = (MemoryFileHandle) imagesDir.child(line);
//                    Gdx.app.log("page size", "" + page.textureFile.ba.size);
                        while (true) {
                            if (TextureAtlasData.readEntry(entry, line = reader.readLine()) == 0) break;
                            TextureAtlasData.Field field = pageFields.get(entry[0]);
                            if (field != null) field.parse(page); // Silently ignore unknown page fields.
                        }
                        tad.pages.add(page);
                    } else {
                        TextureAtlasData.Region region = new TextureAtlasData.Region();
                        region.page = page;
                        region.name = line.trim();
                        if (flip) region.flip = true;
                        while (true) {
                            int count = TextureAtlasData.readEntry(entry, line = reader.readLine());
                            if (count == 0) break;
                            TextureAtlasData.Field field = regionFields.get(entry[0]);
                            if (field != null)
                                field.parse(region);
                            else {
                                if (names == null) {
                                    names = new Array(8);
                                    values = new Array(8);
                                }
                                names.add(entry[0]);
                                int[] entryValues = new int[count];
                                for (int i = 0; i < count; i++) {
                                    try {
                                        entryValues[i] = Integer.parseInt(entry[i + 1]);
                                    } catch (NumberFormatException ignored) { // Silently ignore non-integer values.
                                    }
                                }
                                values.add(entryValues);
                            }
                        }
                        if (region.originalWidth == 0 && region.originalHeight == 0) {
                            region.originalWidth = region.width;
                            region.originalHeight = region.height;
                        }
                        if (names != null && names.size > 0) {
                            region.names = names.toArray(String.class);
                            region.values = values.toArray(int[].class);
                            names.clear();
                            values.clear();
                        }
                        int cnt = 0;
                        for (TextureAtlasData.Page p : tad.pages) {
                            page.texture = new Texture(pixmaps[cnt++]);
                            page.texture.setFilter(page.minFilter, page.magFilter);
                            page.texture.setWrap(page.uWrap, page.vWrap);
                            textures.add(page.texture);

                        }
                        regions.ensureCapacity(tad.getRegions().size);
                        Array<TextureAtlasData.Region> dd = tad.getRegions();
                        for (TextureAtlasData.Region region1 : dd) {
                            AtlasRegion atlasRegion = new AtlasRegion(region1.page.texture, region1.left, region1.top, //
                                    region1.rotate ? region1.height : region1.width, //
                                    region1.rotate ? region1.width : region1.height);
                            atlasRegion.index = region1.index;
                            atlasRegion.name = region1.name;
                            atlasRegion.offsetX = region1.offsetX;
                            atlasRegion.offsetY = region1.offsetY;
                            atlasRegion.originalHeight = region1.originalHeight;
                            atlasRegion.originalWidth = region1.originalWidth;
                            atlasRegion.rotate = region1.rotate;
                            atlasRegion.degrees = region1.degrees;
                            atlasRegion.names = region1.names;
                            atlasRegion.values = region1.values;
                            if (region.flip) atlasRegion.flip(false, true);
                            regions.add(atlasRegion);
                        }
//                        regions.add(region);
                    }
                }

            } catch (
                    Exception ex) {
                throw new GdxRuntimeException("Error reading texture atlas file: " + packFile, ex);
            } finally {
                StreamUtils.closeQuietly(reader);
            }

        if (hasIndexes[0]) {
            regions.sort(new Comparator<TextureAtlasData.Region>() {
                public int compare (TextureAtlasData.Region region1, TextureAtlasData.Region region2) {
                    int i1 = region1.index;
                    if (i1 == -1) i1 = Integer.MAX_VALUE;
                    int i2 = region2.index;
                    if (i2 == -1) i2 = Integer.MAX_VALUE;
                    return i1 - i2;
                }
            });
        }
 */