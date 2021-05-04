package com.klemstinegroup.sunshineblue.engine.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.klemstinegroup.sunshineblue.engine.objects.ParticleObject;

import java.util.Locale;

public class ParticleUtil {
    private static PixmapPacker pixmapPacker = new PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 3, true);;
    private static TextureAtlas particleAtlas= new TextureAtlas();;

    /*private void initializeParticles() {
        for (EventData particleEvent : particleEvents) {
            Tuple<String, FileHandle> selectedParticleFile = null;
            for (Tuple<String, FileHandle> particleFile : particleFiles) {
                if (particleEvent.getString().equals(particleFile.x)) {
                    selectedParticleFile = particleFile;
                    break;
                }
            }

            if (selectedParticleFile == null) selectedParticleFile = particleFiles.first();

            loadParticleEffect(particleEvent, selectedParticleFile);
        }

        prepareParticleAtlas();
    }
*/
    private static void loadParticleEffect(final Tuple<String, FileHandle> selected) {
        for (FileHandle fileHandle : selected.y.parent().list()) {
            String extension = fileHandle.extension().toLowerCase(Locale.ROOT);
            if (extension.equals("png")) {
                if (pixmapPacker.getRect(fileHandle.nameWithoutExtension()) == null) {
                    packPixmap(fileHandle);
                }
            }
        }
    }

    private static void packPixmap(FileHandle fileHandle) {
        Pixmap pixmap = new Pixmap(fileHandle);
        pixmapPacker.pack(fileHandle.nameWithoutExtension(), pixmap);
    }

    private static void prepareParticleAtlas() {
        pixmapPacker.updateTextureAtlas(particleAtlas, Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false, false);

    }

    public static ArrayMap<String, ParticleEffect> particleFiles= new ArrayMap<String, ParticleEffect>();
    public static ArrayMap<String, ParticleEffect>  getParticleFiles() {
        FileHandle sceneFolder = Gdx.files.internal("ParticlePark_data");
        for (FileHandle particleFolder : sceneFolder.list()) {
//            System.out.println("partfold:"+particleFolder.path());
            for (FileHandle fileHandle1 : particleFolder.list()) {
                for (FileHandle fileHandle : fileHandle1.list()) {
//                    System.out.println("file:" + fileHandle.path());
                    String extension = fileHandle.extension().toLowerCase(Locale.ROOT);
                    if (extension.equals("p")) {
                        loadParticleEffect(new Tuple<String, FileHandle>(fileHandle.nameWithoutExtension(), fileHandle));
                        break;
                    }
                }
            }
            prepareParticleAtlas();
            for (FileHandle fileHandle1 : particleFolder.list()) {
                for (FileHandle fileHandle : fileHandle1.list()) {
//                    System.out.println("file:" + fileHandle.path());
                    String extension = fileHandle.extension().toLowerCase(Locale.ROOT);
                    if (extension.equals("p")) {
                        ParticleEffect particleEffect = new ParticleEffect();
                        particleEffect.setEmittersCleanUpBlendFunction(false);
                        particleEffect.load(fileHandle, particleAtlas);
                        particleFiles.put(fileHandle.nameWithoutExtension(),particleEffect);
                        System.out.println(fileHandle.path());
                        break;
                    }
                }
            }
        }
        System.out.println("size of part files:"+particleFiles.size);
        return particleFiles;
    }
}
