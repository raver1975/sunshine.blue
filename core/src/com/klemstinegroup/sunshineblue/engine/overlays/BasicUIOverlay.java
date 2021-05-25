package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.util.*;

import java.util.Iterator;
import java.util.Map;


public class BasicUIOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    public final TextButton autoloadButton;
    private final Table hgScreenshots;
    private final Table scrollableTable;
    private final Skin skin;
    private final HorizontalGroup hgScene;
    Vector2 touchdown = new Vector2();
    Vector2 oldtouch = new Vector2();
    Vector2 touchdownre = new Vector2();
    Vector2 oldtouchre = new Vector2();
    boolean touched = false;
    private ArrayMap<String, Pixmap> pixmapmap = new ArrayMap<>();


    public BasicUIOverlay() {
        SunshineBlue.instance.assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        skin = SunshineBlue.instance.assetManager.get("skins/orange/skin/uiskin.json", Skin.class);
        stage = new Stage(SunshineBlue.instance.overlayViewport);
        TextButton exitButton = new TextButton("X", skin);
        exitButton.setPosition(SunshineBlue.instance.overlayViewport.getWorldWidth() - 60, 10);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.backOverlay();
            }
        });
        stage.addActor(exitButton);


        hgScreenshots = new Table();
        hgScreenshots.setPosition(0, 0);
        scrollableTable = new Table();
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(Color.BLACK);
        bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
//        NinePatch patch = new NinePatch(new Texture(bgPixmap),3, 3, 3, 3);
//        NinePatchDrawable background = new NinePatchDrawable(patch);
//        scrollableTable.setBackground(background);
       /* Pixmap bgPixmap1 = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap1.setColor(Color.RED);
        bgPixmap1.fill();
        TextureRegionDrawable textureRegionDrawableBg1 = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap1)));*/
        scrollableTable.setBackground(textureRegionDrawableBg);
        hgScreenshots.setBackground(textureRegionDrawableBg);
        scrollableTable.setVisible(false);
        scrollableTable.setFillParent(true);
        final ScrollPane scroll = new ScrollPane(hgScreenshots, skin);
        scrollableTable.add(scroll).expand().fill();
        scroll.setFlickScroll(true);
        stage.addActor(scrollableTable);

        hgScene = new HorizontalGroup();
        hgScene.setVisible(false);
        hgScene.space(10);
        hgScene.setPosition(70, SunshineBlue.instance.overlayViewport.getWorldHeight() - 75);
        stage.addActor(hgScene);

        HorizontalGroup hgSettings = new HorizontalGroup();
        hgSettings.setVisible(false);
        hgSettings.space(10);
        hgSettings.setPosition(70, 70);
        stage.addActor(hgSettings);

        HorizontalGroup hgObjects = new HorizontalGroup();
        hgObjects.setVisible(false);
        hgObjects.space(10);
        hgObjects.setPosition(70, 10);
        stage.addActor(hgObjects);

        Actor newButton = new TextButton("New", skin);
        newButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 135);
        newButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dialog = new Dialog("Erase scene?", skin) {
                    @Override
                    protected void result(Object object) {
                        if (object.equals(2)) {
                            Preferences prefs = null;
                            do {
                                prefs = Gdx.app.getPreferences("scenes");
                                for (String s : prefs.get().keySet()) {
                                    prefs.remove(s);
                                    prefs.flush();
                                }
                            }
                            while (prefs != null && prefs.get().size() > 0);
                            SunshineBlue.instance.otherCIDS.clear();
                        }
                        if (!object.equals(3)) {
                            SunshineBlue.instance.selectedObjects.clear();
                            Iterator<BaseObject> i = SunshineBlue.instance.userObjects.iterator();
                            while (i.hasNext()) {
                                SunshineBlue.removeUserObj(i.next());
                            }
                            SunshineBlue.instance.userObjects.clear();
                            SunshineBlue.instance.selectedObjects.clear();
                            SunshineBlue.instance.commands.clear();
                            Overlay.setOverlay(SunshineBlue.instance.BASIC_UI_OVERLAY);
                            SunshineBlue.instance.otherCIDS.remove(cid);
                        }
                        hide();
                    }
                };
                dialog.setModal(true);
                dialog.button("Erase Scene", 1);
                dialog.button("Erase All", 2);
                dialog.button("Cancel", 3);
                dialog.show(stage);
            }
        });
        hgScene.addActor(newButton);


        Actor saveButton = new TextButton("Save", skin);
        saveButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 135);
        saveButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SerializeUtil.save(new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                adjusthgScreenshot();
                            }
                        });

                    }

                    @Override
                    public void uploadFailed(Throwable t) {

                    }
                });

            }
        });
        hgScene.addActor(saveButton);


        TextButton sceneButton = new TextButton(" / ", skin);
        sceneButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hgScene.setVisible(!hgScene.isVisible());
                scrollableTable.setVisible(!scrollableTable.isVisible());
                scrollableTable.pack();
                hgScene.pack();
                hgObjects.setVisible(false);
                hgObjects.pack();
                hgSettings.setVisible(false);
                hgSettings.pack();
                SunshineBlue.instance.BASIC_UI_OVERLAY.adjusthgScreenshot();
            }
        });
        sceneButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 75);
        stage.addActor(sceneButton);

        TextButton starButton = new TextButton(" * ", skin);
        starButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hgSettings.setVisible(!hgSettings.isVisible());
                hgSettings.pack();
                hgObjects.setVisible(false);
                hgObjects.pack();
                hgScene.setVisible(false);
                hgScene.pack();
                scrollableTable.setVisible(false);
                scrollableTable.pack();
//                hgSettings.setVisible(false);hgSettings.pack();
            }
        });
        starButton.setPosition(10, 70);
        stage.addActor(starButton);

        TextButton plusButton = new TextButton(" + ", skin);
        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hgObjects.setVisible(!hgObjects.isVisible());
                hgObjects.pack();
//                hgObjects.setVisible(false);hgObjects.pack();
                hgScene.setVisible(false);
                hgScene.pack();
                scrollableTable.setVisible(false);
                scrollableTable.pack();
                hgSettings.setVisible(false);
                hgSettings.pack();
            }
        });
        plusButton.setPosition(10, 10);
        stage.addActor(plusButton);


        Actor fontButton = new TextButton("Text", skin);


        Actor apngButton = new TextButton("Gif", skin);
        apngButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 255);
        apngButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (SunshineBlue.instance.isRecording) {
                    SunshineBlue.instance.stopRecording();

                } else {
                    SunshineBlue.instance.startRecording();
                    apngButton.setName("Stop");
                    Overlay.setOverlay(SunshineBlue.instance.BLANK_OVERLAY);
                }
            }
        });
        hgSettings.addActor(apngButton);

   /*     Actor loadButton = new TextButton("Load", skin);
        loadButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 75);
        loadButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });
        loadButton.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) {

                return true;


            }
        });
        hgScene.addActor(loadButton);*/


        Actor screenshotButton = new TextButton("Png", skin);
        screenshotButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 195);
        screenshotButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
//                SunshineBlue.instance.batch.begin();
                Pixmap screenshot = FrameBufferUtils.drawObjectsPix(SunshineBlue.instance.batch, SunshineBlue.instance.viewport, SunshineBlue.instance.userObjects, SunshineBlue.instance.overlayViewport.getScreenWidth(), SunshineBlue.instance.overlayViewport.getScreenHeight(), true);
//                SunshineBlue.instance.batch.end();
                IPFSUtils.uploadPngtoIPFS(screenshot, new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        IPFSUtils.openIPFSViewer(cid);
                    }

                    @Override
                    public void uploadFailed(Throwable t) {

                    }
                });
            }
        });

        hgSettings.addActor(screenshotButton);

        fontButton.addListener(new ClickListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.input.setOnscreenKeyboardVisible(true);
//                SunshineBlue.instance.objects.add(new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 40));
//            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.input.setOnscreenKeyboardVisible(true);
                FontObject ff = new FontObject();
                SunshineBlue.addUserObj(ff);
                SunshineBlue.instance.FONT_OVERLAY.setObject(ff);
                SunshineBlue.instance.FONT_OVERLAY.generate(SunshineBlue.instance.assetManager, ff);
                SunshineBlue.instance.FONT_OVERLAY.setList();
//                ff.sd.position.set(-ff.sd.center.x, -ff.sd.center.y);
                Vector2 vec = new Vector2(100, 200);
                SunshineBlue.instance.viewport.unproject(SunshineBlue.instance.overlayViewport.project(vec));
//                Statics.viewport.project(Statics.overlayViewport.unproject(vec));
//                Statics.overlayViewport.unproject(Statics.viewport.project(vec));
//                Statics.overlayViewport.project(Statics.viewport.unproject(vec));
                ff.sd.position.set(vec);
                Overlay.setOverlay(SunshineBlue.instance.FONT_OVERLAY);

//        ((ScreenObject) Statics.objects.get(0)).position.set(-((ScreenObject) Statics.objects.get(0)).bounds.x/2, -((ScreenObject) Statics.objects.get(0)).bounds.y/2, 0);
            }
        });
        fontButton.setPosition(10, 10);
//        stage.addActor(fontButton);
        hgObjects.addActor(fontButton);

        autoloadButton = new TextButton("See", skin);
        autoloadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SunshineBlue.instance.autoload = true;
                Overlay.backOverlay();
                SunshineBlue.instance.autoloadtime = TimeUtils.millis();
            }
        });
        autoloadButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 375);
        autoloadButton.setChecked(SunshineBlue.instance.autoload);
        hgScene.addActor(autoloadButton);

        Actor popButton = new TextButton("Pop", skin);
        popButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 315);
        popButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SerializeUtil.save(new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
                            Gdx.net.openURI("http://localhost:8080/?" + cid);
                        } else {
                            Gdx.net.openURI("https://sunshine.blue/?" + cid);
                        }
                    }

                    @Override
                    public void uploadFailed(Throwable t) {

                    }
                });

            }
        });
        hgScene.addActor(popButton);

        Actor imageButton = new TextButton("Image", skin);
        imageButton.setPosition(70, 10);

        imageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.setOverlay(SunshineBlue.instance.IMAGE_OVERLAY);
            }
        });
        hgObjects.addActor(imageButton);

        Actor drawButton = new TextButton("Draw", skin);
        drawButton.setPosition(135, 10);
        drawButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.input.setOnscreenKeyboardVisible(false);
                DrawObject doi = new DrawObject();
                doi.setSize((int) SunshineBlue.instance.DRAW_OVERLAY.slider.getValue());
                doi.setColor(SunshineBlue.instance.DRAW_OVERLAY.picker.getSelectedColor());
                SunshineBlue.addUserObj(doi);

                SunshineBlue.instance.DRAW_OVERLAY.setObject(doi);
                SunshineBlue.instance.DRAW_OVERLAY.setTouchable(doi);
                Overlay.setOverlay(SunshineBlue.instance.DRAW_OVERLAY);
//                pasteButton.setVisible(true);
            }
        });
        hgObjects.addActor(drawButton);
        Actor fxButton = new TextButton("FX", skin);
        fxButton.setPosition(195, 10);
        fxButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ParticleObject po = new ParticleObject(ParticleUtil.particleFiles.getKeyAt(MathUtils.random(ParticleUtil.particleFiles.size - 1)));
//                doi.setSize((int) SunshineBlue.instance.DRAW_OVERLAY.slider.getValue());
//                doi.setColor(SunshineBlue.instance.DRAW_OVERLAY.picker.getSelectedColor());
                SunshineBlue.addUserObj(po);

                SunshineBlue.instance.PARTICLE_OVERLAY.setObject(po);
//                SunshineBlue.instance.PARTICLE_OVERLAY.setTouchable(doi);
                Overlay.setOverlay(SunshineBlue.instance.PARTICLE_OVERLAY);
//                pasteButton.setVisible(true);
            }
        });
        hgObjects.addActor(fxButton);
        Actor loopButton = new TextButton("Loop", skin);
        loopButton.setPosition(70, 70);
        loopButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.setOverlay(SunshineBlue.instance.LOOP_OVERLAY);
            }
        });
        hgSettings.addActor(loopButton);

        Actor backgroundButton = new TextButton("BG", skin);
        backgroundButton.setPosition(10, 70);
        backgroundButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.setOverlay(SunshineBlue.instance.BACKGROUND_OVERLAY);
            }
        });
        hgSettings.addActor(backgroundButton);

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        SunshineBlue.instance.overlayViewport.unproject(oldtouch.set(screenX, screenY));
        SunshineBlue.instance.overlayViewport.unproject(touchdown.set(screenX, screenY));
        SunshineBlue.instance.viewport.unproject(oldtouchre.set(screenX, screenY));
        touched = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (touched) {
            SunshineBlue.instance.viewport.unproject(touchdownre.set(screenX, screenY));
            SunshineBlue.instance.selectedObjects.clear();
            for (BaseObject bo : SunshineBlue.instance.userObjects) {
                if (bo instanceof Touchable) {
                    Polygon box = new Polygon();
                    box.setVertices(new float[]{oldtouchre.x, oldtouchre.y, oldtouchre.x, touchdownre.y, touchdownre.x, touchdownre.y, touchdownre.x, oldtouchre.y, oldtouchre.x, oldtouchre.y});
                    if (((Touchable) bo).isSelected(box)) {
                        SunshineBlue.instance.selectedObjects.add(bo);
                    }

                }
            }

            if (SunshineBlue.instance.selectedObjects.size > 0) {
                Overlay.setOverlay(SunshineBlue.instance.TRANSFORM_OVERLAY);
            } else {
//            Overlay.setOverlay(this);
            }
        }
        touched = false;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        SunshineBlue.instance.overlayViewport.unproject(touchdown.set(screenX, screenY));
        SunshineBlue.instance.viewport.unproject(touchdownre.set(screenX, screenY));
        SunshineBlue.instance.selectedObjects.clear();
        for (BaseObject bo : SunshineBlue.instance.userObjects) {
            if (bo instanceof Touchable) {
                Polygon box = new Polygon();
                box.setVertices(new float[]{oldtouchre.x, oldtouchre.y, oldtouchre.x, touchdownre.y, touchdownre.x, touchdownre.y, touchdownre.x, oldtouchre.y, oldtouchre.x, oldtouchre.y});
                if (((Touchable) bo).isSelected(box)) {
                    SunshineBlue.instance.selectedObjects.add(bo);
                }

            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void draw(Batch batch, float delta) {

//        mx4Overlay.set(mx4Overlay.idt());
//        mx4Overlay.setToOrtho2D(0, 0, 100, 100);
//        mx4Overlay.

//        SunshineBlue.instance.batch.setProjectionMatrix(SunshineBlue.instance.mx4Batch);
        if (touched) {
            SunshineBlue.instance.shapedrawer.rectangle(oldtouch.x, oldtouch.y, touchdown.x - oldtouch.x, touchdown.y - oldtouch.y, Color.WHITE);
        }
//        if (screenshotPixmap!=null){
//            batch.draw(screenshotPixmap,50,50);
//        }
        stage.act();
        stage.draw();
//        SunshineBlue.instance.font.setColor(Color.BLACK);
//        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 29, SunshineBlue.instance.overlayViewport.getWorldHeight() - 60);
//        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 30, SunshineBlue.instance.overlayViewport.getWorldHeight() - 59);
//        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 31, SunshineBlue.instance.overlayViewport.getWorldHeight() - 60);
//        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 30, SunshineBlue.instance.overlayViewport.getWorldHeight() - 61);
        SunshineBlue.instance.font.setColor(Color.CYAN);
        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 45, SunshineBlue.instance.overlayViewport.getWorldHeight() - 27);

    }

    @Override
    public void setInput() {
        SunshineBlue.instance.im.addProcessor(stage);
        SunshineBlue.instance.selectedObjects.clear();
        if (SunshineBlue.instance.isRecording) {
            SunshineBlue.instance.stopRecording();
        }


        adjusthgScreenshot();

//        sp.invalidate();
    }

    private void adjusthgScreenshot() {
        hgScreenshots.clear();
        for (Map.Entry<String, String> entry : SunshineBlue.instance.otherCIDS.entrySet()) {
            if (pixmapmap.containsKey(entry.getKey())) {
                Image tt = new Image(new Texture(pixmapmap.get(entry.getKey())));
                tt.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        load(entry);
                    }
                });
//                tt.setScale(.1f);
                hgScreenshots.add(tt);
                hgScreenshots.pack();
                hgScreenshots.getCell(tt).pad(10);
                hgScreenshots.invalidate();
                hgScreenshots.layout();
            } else {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + entry.getValue(), new Pixmap.DownloadPixmapResponseListener() {
                            @Override
                            public void downloadComplete(Pixmap pixmap) {
                                System.out.println("loaded pixmap! " + entry.getKey() + "\t" + entry.getValue());
                                pixmap.setColor(Color.RED);
                                for (int i=0;i<pixmap.getWidth();i++) {
                                    for (int j = 0; j < pixmap.getHeight(); j++) {

                                        pixmap.drawPixel(i,0);
                                        pixmap.drawPixel(i,pixmap.getHeight()-1);
                                        pixmap.drawPixel(0,j);
                                        pixmap.drawPixel(pixmap.getWidth()-1,j);

                                        pixmap.drawPixel(i,1);
                                        pixmap.drawPixel(i,pixmap.getHeight()-2);
                                        pixmap.drawPixel(1,j);
                                        pixmap.drawPixel(pixmap.getWidth()-2,j);

                                    }
                                }
                                pixmapmap.put(entry.getKey(), pixmap);
                                Image tt = new Image(new Texture(pixmapmap.get(entry.getKey())));
                                tt.addListener(new ClickListener() {
                                    @Override
                                    public void clicked(InputEvent event, float x, float y) {
                                        load(entry);
                                    }
                                });
//                        tt.setScale(.1f);
                                hgScreenshots.add(tt);
                                hgScreenshots.pack();
                                hgScreenshots.getCell(tt).pad(10);
                                hgScreenshots.invalidate();
                                hgScreenshots.layout();
                            }

                            @Override
                            public void downloadFailed(Throwable t) {

                            }
                        });
                    }
                });
            }
        }
    }

    private void load(Map.Entry<String, String> entry) {
//        Preferences prefs = Gdx.app.getPreferences("scenes");
//        for (Map.Entry<String, ?> pref : prefs.get().entrySet()) {
//            if (!pref.getKey().equals("current")) {
//                SunshineBlue.instance.otherCIDS.put(pref.getKey(), (String) pref.getValue());
//            }
//        }
//        if (SunshineBlue.instance.otherCIDS.size() > 0) {
//
//            Iterator<Map.Entry<String, String>> iter = SunshineBlue.instance.otherCIDS.entrySet().iterator();
//            if (otherIndex > SunshineBlue.instance.otherCIDS.size() - 1) {
//                otherIndex = SunshineBlue.instance.otherCIDS.size() - 1;
//            }
//            for (int i = 0; i < otherIndex; i++) {
//                iter.next();
//            }
//            Map.Entry<String, String> entry = iter.next();
        String screenshotCID = entry.getValue();
        String cid = entry.getKey();
        Gdx.app.log("load", cid + "\t" + screenshotCID);
        SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + screenshotCID, new Pixmap.DownloadPixmapResponseListener() {
            @Override
            public void downloadComplete(Pixmap pixmap) {
                Dialog dialog = new Dialog(entry.getKey(), skin) {
                    @Override
                    protected void result(Object object) {
//                        hgScene.setVisible(!hgScene.isVisible());
                        scrollableTable.setVisible(false);
                        hgScene.setVisible(false);
                        scrollableTable.pack();
//                        hgScene.pack();
//                        hgObjects.setVisible(false);
//                        hgObjects.pack();
//                        hgSettings.setVisible(false);
//                        hgSettings.pack();
                        SunshineBlue.instance.BASIC_UI_OVERLAY.adjusthgScreenshot();
                        /*if (object.equals(1L)) {
                            if (--otherIndex < 0) {
                                otherIndex = SunshineBlue.instance.otherCIDS.size() - 1;
                            }
                               *//* InputEvent event1 = new InputEvent();
                                event1.setType(InputEvent.Type.touchDown);
                                loadButton.fire(event1);

                                InputEvent event2 = new InputEvent();
                                event2.setType(InputEvent.Type.touchUp);
                                loadButton.fire(event2);*//*
                        } else*/
                        if (object.equals(2L)) {
                            SerializeUtil.load(cid, false);

                        } else if (object.equals(6L)) {
                            SerializeUtil.load(cid, true);
                        } else if (object.equals(7L)) {
                            SunshineBlue.instance.otherCIDS.remove(cid);
                            Preferences prefs = Gdx.app.getPreferences("scenes");
                            prefs.remove(cid);
                            prefs.flush();
                                /*InputEvent ie1 = new InputEvent();
                                ie1.setType(InputEvent.Type.touchDown);
                                loadButton.fire(ie1);
                                InputEvent ie2 = new InputEvent();
                                ie2.setType(InputEvent.Type.touchUp);
                                loadButton.fire(ie2);*/
                        } else if (object.equals(3L)) {
                            String uri = "https://sunshine.blue/?" + cid;
                            Gdx.app.getClipboard().setContents(uri);
                            Gdx.net.openURI(uri);
                        } else if (object.equals(4L)) {

                        }/* else if (object.equals(5L)) {
                            if (++otherIndex > SunshineBlue.instance.otherCIDS.size() - 1) {
                                otherIndex = 0;
                            }
                               *//* InputEvent event1 = new InputEvent();
                                event1.setType(InputEvent.Type.touchDown);
                                loadButton.fire(event1);

                                InputEvent event2 = new InputEvent();
                                event2.setType(InputEvent.Type.touchUp);
                                loadButton.fire(event2);*//*
                        }*/

                        this.hide();
                    }
                };
                Pixmap pixmap2 = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
                pixmap2.setColor(new Color(0xbb6500ff));
                pixmap2.fill();
                pixmap2.drawPixmap(pixmap, 0, 0);

                pixmap.dispose();
                dialog.setBackground(new SpriteDrawable(new Sprite(new Texture(pixmap2))));
//                    dialog.button("prev", 1L);
//                    dialog.button("next", 5L);
                dialog.button("load", 2L);
                dialog.button("merge", 6L);
                dialog.button("delete", 7L);
                dialog.button("pop", 3L);
                dialog.button("cancel", 4L);


                dialog.setModal(true);
                dialog.show(stage);
//                                    BasicUIOverlay.this.screenshotPixmap=new Texture(pixmap);
            }

            @Override
            public void downloadFailed(Throwable t) {
                Gdx.app.log("downloading screenshot failed, taking evasive manuevers", "eek!");
//                otherIndex++;
//                if (otherIndex > SunshineBlue.instance.otherCIDS.size() - 1) {
//                    otherIndex = 0;
//                }
                SunshineBlue.instance.otherCIDS.remove(cid);
//                InputEvent ie1 = new InputEvent();
                    /*ie1.setType(InputEvent.Type.touchDown);
                    loadButton.fire(ie1);
                    InputEvent ie2 = new InputEvent();
                    ie2.setType(InputEvent.Type.touchUp);
                    loadButton.fire(ie2);*/
            }

        });

    }

    @Override
    public boolean isSelected(Vector2 touch) {
        return false;
    }

    @Override
    public boolean isSelected(Polygon gon) {
        return false;
    }

    @Override
    public void setBounds() {

    }

    @Override
    public void removeInput() {
        SunshineBlue.instance.im.removeProcessor(stage);
    }


    @Override
    public void setObject(BaseObject bo) {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
