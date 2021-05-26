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
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.colorpicker.GradientDrawable;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.util.*;

import java.util.Collections;
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
        bgPixmap.setColor(Color.CLEAR);
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
        scroll.getStyle().background = textureRegionDrawableBg;

        scroll.setScrollingDisabled(false, true);
        scroll.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (scroll.getScrollX() / scroll.getMaxX() > .99f) {
                    Gdx.app.log("screenshot", "get more screenshots");
                    getMoreScreenshots();
                }
                return true;
            }
        });

        scrollableTable.add(scroll).expand(1, 0).fill();
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

        Actor loadButton = new TextButton("Load", skin);
        loadButton.setPosition(200, SunshineBlue.instance.overlayViewport.getWorldHeight() - 135);
        loadButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                scrollableTable.setVisible(!scrollableTable.isVisible());
                scrollableTable.pack();
                SunshineBlue.instance.BASIC_UI_OVERLAY.adjusthgScreenshot();
                SunshineBlue.instance.BASIC_UI_OVERLAY.getMoreScreenshots();
            }
        });
        hgScene.addActor(loadButton);

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

                hgScene.pack();
                hgObjects.setVisible(false);
                hgObjects.pack();
                hgSettings.setVisible(false);
                hgSettings.pack();
                SunshineBlue.instance.BASIC_UI_OVERLAY.adjusthgScreenshot();
                SunshineBlue.instance.BASIC_UI_OVERLAY.getMoreScreenshots();
                SunshineBlue.instance.BASIC_UI_OVERLAY.getMoreScreenshots();
                SunshineBlue.instance.BASIC_UI_OVERLAY.getMoreScreenshots();
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
        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 15, SunshineBlue.instance.overlayViewport.getWorldHeight() - 27);

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
//        int cnt=0;

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
            }
        }
        getMoreScreenshots();
    }

    private void load(Map.Entry<String, String> entry) {
        String screenshotCID = entry.getValue();
        String cid = entry.getKey();
        Gdx.app.log("load", cid + "\t" + screenshotCID);
        Pixmap.DownloadPixmapResponseListener runrun = new Pixmap.DownloadPixmapResponseListener() {
            @Override
            public void downloadComplete(Pixmap pixmap) {
                Dialog dialog = new Dialog(entry.getKey(), skin) {
                    @Override
                    protected void result(Object object) {
//                        hgScene.setVisible(!hgScene.isVisible());
                        SunshineBlue.instance.BASIC_UI_OVERLAY.adjusthgScreenshot();
                        if (!object.equals(4L)) {
                            scrollableTable.setVisible(false);
                            hgScene.setVisible(false);
                            scrollableTable.pack();
                        }
                        if (object.equals(2L)) {
                            SerializeUtil.load(cid, false);

                        } else if (object.equals(6L)) {
                            SerializeUtil.load(cid, true);
                        } else if (object.equals(7L)) {
                            SunshineBlue.instance.otherCIDS.remove(cid);
                            Preferences prefs = Gdx.app.getPreferences("scenes");
                            prefs.remove(cid);
                            prefs.flush();
                        } else if (object.equals(3L)) {
                            String uri = "https://sunshine.blue/?" + cid;
                            Gdx.app.getClipboard().setContents(uri);
                            Gdx.net.openURI(uri);
                        }
                        hide();
                    }
                };
//                Pixmap pixmap2 = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
//                pixmap2.setColor(SunshineBlue.instance.bgColor);
//                pixmap2.fill();
//                pixmap2.drawPixmap(pixmap, 0, 0);
                Image image = new Image(new SpriteDrawable(new Sprite(new Texture(pixmap))));
                image.setSize(500, 500);
                dialog.getContentTable().add(image);
                dialog.getContentTable().setBackground(new GradientDrawable(Color.CYAN, Color.BLACK, Color.RED, Color.WHITE));
//                dialog.setSize(400,400);
                dialog.button("load", 2L);
                dialog.button("merge", 6L);
                dialog.button("delete", 7L);
                dialog.button("pop", 3L);
                dialog.button("cancel", 4L);
                dialog.setModal(true);
//                dialog.pack();
//                dialog.invalidate();
                dialog.show(stage);
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

        };
        if (pixmapmap.containsKey(cid)) {
            runrun.downloadComplete(pixmapmap.get(cid));
        } else {
            SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + screenshotCID, runrun);
        }

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

    Array<String> pending = new Array<>();

    public void getMoreScreenshots() {
        Array<String> tempsort = new Array<>();
        tempsort.addAll(SunshineBlue.instance.otherCIDS.keySet().toArray(new String[0]));
        tempsort.shuffle();
        for (String s : tempsort) {
            if (!pixmapmap.containsKey(s) && !pending.contains(s, false)) {
                pending.add(s);
                SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + SunshineBlue.instance.otherCIDS.get(s), new Pixmap.DownloadPixmapResponseListener() {
                    @Override
                    public void downloadComplete(Pixmap pixmap) {
                        pending.removeValue(s, false);
                        System.out.println("loaded pixmap! " + s + "\t" + SunshineBlue.instance.otherCIDS.get(s));
//                                            pixmap.setColor(ColorUtil.numberToColorPercentage((float) (cnt2[0]++) / (float) SunshineBlue.instance.otherCIDS.size()));
                        pixmap.setColor(Color.ORANGE);
                        for (int i = 0; i < pixmap.getWidth(); i++) {
                            for (int j = 0; j < pixmap.getHeight(); j++) {

                                pixmap.drawPixel(i, 0);
                                pixmap.drawPixel(i, pixmap.getHeight() - 1);
                                pixmap.drawPixel(0, j);
                                pixmap.drawPixel(pixmap.getWidth() - 1, j);

                                pixmap.drawPixel(i, 1);
                                pixmap.drawPixel(i, pixmap.getHeight() - 2);
                                pixmap.drawPixel(1, j);
                                pixmap.drawPixel(pixmap.getWidth() - 2, j);

                                pixmap.drawPixel(i, 2);
                                pixmap.drawPixel(i, pixmap.getHeight() - 3);
                                pixmap.drawPixel(2, j);
                                pixmap.drawPixel(pixmap.getWidth() - 3, j);

                                pixmap.drawPixel(i, 3);
                                pixmap.drawPixel(i, pixmap.getHeight() - 4);
                                pixmap.drawPixel(3, j);
                                pixmap.drawPixel(pixmap.getWidth() - 4, j);

                                pixmap.drawPixel(i, 4);
                                pixmap.drawPixel(i, pixmap.getHeight() - 5);
                                pixmap.drawPixel(4, j);
                                pixmap.drawPixel(pixmap.getWidth() - 5, j);

                                pixmap.drawPixel(i, 5);
                                pixmap.drawPixel(i, pixmap.getHeight() - 6);
                                pixmap.drawPixel(5, j);
                                pixmap.drawPixel(pixmap.getWidth() - 6, j);

                            }
                        }
                        Preferences prefs = Gdx.app.getPreferences("scenes");
                        if (prefs.contains(s)) {
                            pixmap.fillCircle(15, 15, 8);
                        }
                        Pixmap pixmap1 = new Pixmap(pixmap.getWidth() / 2, pixmap.getHeight() / 2, Pixmap.Format.RGBA8888);
                        pixmap1.drawPixmap(pixmap, 0, 0, pixmap.getWidth(), pixmap.getHeight(), 0, 0, pixmap1.getWidth(), pixmap1.getHeight());


                        pixmap.dispose();
                        if (!pixmapmap.containsKey(s)) {
                            pixmapmap.put(s, pixmap1);
                            Image tt = new Image(new Texture(pixmapmap.get(s)));
                            tt.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    load(new Map.Entry<String, String>() {
                                        @Override
                                        public String getKey() {
                                            return s;
                                        }

                                        @Override
                                        public String getValue() {
                                            return SunshineBlue.instance.otherCIDS.get(s);
                                        }

                                        @Override
                                        public String setValue(String value) {
                                            return null;
                                        }
                                    });
                                }
                            });
                            hgScreenshots.add(tt);
                            hgScreenshots.pack();
                            hgScreenshots.getCell(tt).pad(10);
                            hgScreenshots.invalidate();
                            hgScreenshots.layout();
                        }
                    }

                    @Override
                    public void downloadFailed(Throwable t) {
                        pending.removeValue(s, false);
                        SunshineBlue.instance.otherCIDS.remove(s);
                        Preferences prefs = Gdx.app.getPreferences("scenes");
                        if (prefs.contains(s)) {
                            prefs.remove(s);
                        }
                        prefs.flush();
                    }
                });
                break;
            }

        }
    }
}
