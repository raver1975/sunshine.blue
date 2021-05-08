package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.util.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


public class BasicUIOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    Vector2 touchdown = new Vector2();
    Vector2 oldtouch = new Vector2();
    Vector2 touchdownre = new Vector2();
    Vector2 oldtouchre = new Vector2();
    boolean touched = false;
    private int otherIndex = 0;
//    private Texture screenshotPixmap;


    public BasicUIOverlay() {
        SunshineBlue.instance.assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = SunshineBlue.instance.assetManager.get("skins/orange/skin/uiskin.json", Skin.class);
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


//        Skin skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));
        Actor fontButton = new TextButton("Text", skin);
//        fontButton.setColor(Color.WHITE);

        Actor saveButton = new TextButton("Save", skin);
        saveButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 120);
        saveButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SerializeUtil.save();
                SerializeUtil.save();
            }
        });
        stage.addActor(saveButton);

        Actor popButton = new TextButton("Pop", skin);
        popButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 300);
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
        stage.addActor(popButton);

        Actor apngButton = new TextButton("Rec", skin);
        apngButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 240);
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
        stage.addActor(apngButton);

        Actor randomButton = new TextButton("Load", skin);
        randomButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 60);
        randomButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
            //    Preferences prefs = Gdx.app.getPreferences("scenes");
//                for (Map.Entry<String, ?> pref : prefs.get().entrySet()) {
//                    SunshineBlue.instance.otherCIDS.add((String) pref.getValue());
//                }
                if (SunshineBlue.instance.otherCIDS.size() > 0) {

                    Iterator<Map.Entry<String,String>> iter = SunshineBlue.instance.otherCIDS.entrySet().iterator();
                    if (otherIndex>SunshineBlue.instance.otherCIDS.size()-1){
                        otherIndex=SunshineBlue.instance.otherCIDS.size()-1;
                    }
                    for (int i = 0; i < otherIndex; i++) {
                        iter.next();
                    }
                    Map.Entry<String,String> entry = iter.next();
                    String screenshotCID = entry.getValue();
                    String cid=entry.getKey();
                    Gdx.app.log("load",cid+"\t"+screenshotCID);
                    SunshineBlue.nativeNet.downloadPixmap(Statics.IPFSGateway + screenshotCID, new Pixmap.DownloadPixmapResponseListener() {
                        @Override
                        public void downloadComplete(Pixmap pixmap) {
                            Dialog dialog = new Dialog((otherIndex + 1) + " / " + (SunshineBlue.instance.otherCIDS.size()) + " : " + entry.getKey(), skin) {
                                @Override
                                protected void result(Object object) {
                                    if (object.equals(1L)) {
                                        if (--otherIndex < 0) {
                                            otherIndex = SunshineBlue.instance.otherCIDS.size() - 1;
                                        }
                                        InputEvent event1 = new InputEvent();
                                        event1.setType(InputEvent.Type.touchDown);
                                        randomButton.fire(event1);

                                        InputEvent event2 = new InputEvent();
                                        event2.setType(InputEvent.Type.touchUp);
                                        randomButton.fire(event2);
                                    } else if (object.equals(2L)) {
                                        SerializeUtil.load(cid, false);
                                    } else if (object.equals(6L)) {
                                        SerializeUtil.load(cid, true);
                                    } else if (object.equals(7L)) {
                                        SunshineBlue.instance.otherCIDS.remove(cid);
                                    } else if (object.equals(3L)) {
                                        String uri="https://sunshine.blue/?"+cid;
                                        Gdx.app.getClipboard().setContents(uri);
                                        Gdx.net.openURI(uri);
                                    } else if (object.equals(4L)) {

                                    } else if (object.equals(5L)) {
                                        if (++otherIndex > SunshineBlue.instance.otherCIDS.size() - 1) {
                                            otherIndex = 0;
                                        }
                                        InputEvent event1 = new InputEvent();
                                        event1.setType(InputEvent.Type.touchDown);
                                        randomButton.fire(event1);

                                        InputEvent event2 = new InputEvent();
                                        event2.setType(InputEvent.Type.touchUp);
                                        randomButton.fire(event2);
                                    }

                                    this.hide();
                                }
                            };
                            Pixmap pixmap2 = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), pixmap.getFormat());
                            pixmap2.setColor(new Color(0xbb6500ff));
                            pixmap2.fill();
                            pixmap2.drawPixmap(pixmap, 0, 0);

                            pixmap.dispose();
                            dialog.setBackground(new SpriteDrawable(new Sprite(new Texture(pixmap2))));
                            dialog.button("prev", 1L);
                            dialog.button("next", 5L);
                            dialog.button("pop", 3L);
                            dialog.button("load", 2L);
                            dialog.button("merge", 6L);
                            dialog.button("delete", 7L);
                            dialog.button("cancel", 4L);


                            dialog.setModal(true);
                            dialog.show(stage);
//                                    BasicUIOverlay.this.screenshotPixmap=new Texture(pixmap);
                        }

                        @Override
                        public void downloadFailed(Throwable t) {

                        }
                    });

                }
            }
        });
        randomButton.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                Dialog dialog = new Dialog("Erase scene?", skin) {
                    @Override
                    protected void result(Object object) {
                        if (object.equals(2)) {
                            Preferences prefs = Gdx.app.getPreferences("scenes");
                            prefs.clear();
                            prefs.flush();
                            SunshineBlue.instance.otherCIDS.clear();
                        }
                        else if (object.equals(1)){
                            SerializeUtil.save(new IPFSCIDListener(){
                                @Override
                                public void cid(String cid) {
                                    SunshineBlue.instance.selectedObjects.clear();
                                    Iterator<BaseObject> i=SunshineBlue.instance.userObjects.iterator();
                                    while (i.hasNext()){
                                        SunshineBlue.removeUserObj(i.next());
                                    }
                                    SunshineBlue.instance.userObjects.clear();
                                    Overlay.setOverlay(SunshineBlue.instance.BASIC_UI_OVERLAY);
//                                    Preferences prefs = Gdx.app.getPreferences("scenes");
//                                    prefs.remove(cid);
//                                    prefs.flush();
                                    SunshineBlue.instance.otherCIDS.remove(cid);
                                }

                                @Override
                                public void uploadFailed(Throwable t) {

                                }
                            });
                        }
                        hide();
                    }
                };
                dialog.setModal(true);
                dialog.button("Erase Scene", 1);
                dialog.button("Erase All", 2);
                dialog.button("Cancel", 3);
                dialog.show(stage);
                return true;


            }
        });
        stage.addActor(randomButton);


        Actor screenshotButton = new TextButton("Png", skin);
        screenshotButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 180);
        screenshotButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SunshineBlue.instance.batch.begin();
                Pixmap screenshot = FrameBufferUtils.drawObjects(SunshineBlue.instance.batch, SunshineBlue.instance.viewport, SunshineBlue.instance.userObjects);
                SunshineBlue.instance.batch.end();
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

        stage.addActor(screenshotButton);

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
                System.out.println(vec);
                ff.sd.position.set(vec);
                Overlay.setOverlay(SunshineBlue.instance.FONT_OVERLAY);

//        ((ScreenObject) Statics.objects.get(0)).position.set(-((ScreenObject) Statics.objects.get(0)).bounds.x/2, -((ScreenObject) Statics.objects.get(0)).bounds.y/2, 0);
            }
        });
        fontButton.setPosition(10, 10);
        stage.addActor(fontButton);


        CheckBox autoload=new CheckBox("Autoload",skin,"switch");
        autoload.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SunshineBlue.instance.autoload=true;
                SunshineBlue.instance.autoloadtime= TimeUtils.millis();
            }
        });
        autoload.setPosition(65,SunshineBlue.instance.overlayViewport.getWorldHeight() - 30);
        stage.addActor(autoload);

        Actor imageButton = new TextButton("Image", skin);
        imageButton.setPosition(70, 10);
//        fontButton.setColor(Color.WHITE);


        //                    pasteButton.setVisible(false);


        imageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.setOverlay(SunshineBlue.instance.IMAGE_OVERLAY);
            }
        });
        stage.addActor(imageButton);

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
        stage.addActor(drawButton);
        Actor fxButton = new TextButton("FX", skin);
        fxButton.setPosition(195, 10);
        fxButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ParticleObject po = new ParticleObject(ParticleUtil.particleFiles.getKeyAt(MathUtils.random(ParticleUtil.particleFiles.size-1)));
//                doi.setSize((int) SunshineBlue.instance.DRAW_OVERLAY.slider.getValue());
//                doi.setColor(SunshineBlue.instance.DRAW_OVERLAY.picker.getSelectedColor());
                SunshineBlue.addUserObj(po);

                SunshineBlue.instance.PARTICLE_OVERLAY.setObject(po);
//                SunshineBlue.instance.PARTICLE_OVERLAY.setTouchable(doi);
                Overlay.setOverlay(SunshineBlue.instance.PARTICLE_OVERLAY);
//                pasteButton.setVisible(true);
            }
        });
        stage.addActor(fxButton);
        Actor backgroundButton = new TextButton("BG", skin);
        backgroundButton.setPosition(10, 70);
        backgroundButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.setOverlay(SunshineBlue.instance.BACKGROUND_OVERLAY);
            }
        });
        stage.addActor(backgroundButton);

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
    public void draw(Batch batch,float delta) {

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
        SunshineBlue.instance.font.setColor(Color.BLACK);
        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 29, SunshineBlue.instance.overlayViewport.getWorldHeight() - 45);
        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 30, SunshineBlue.instance.overlayViewport.getWorldHeight() - 44);
        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 31, SunshineBlue.instance.overlayViewport.getWorldHeight() - 45);
        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 30, SunshineBlue.instance.overlayViewport.getWorldHeight() - 46);
        SunshineBlue.instance.font.setColor(Color.CYAN);
        SunshineBlue.instance.font.draw(batch, "" + SunshineBlue.instance.otherCIDS.size(), 30, SunshineBlue.instance.overlayViewport.getWorldHeight() - 45);

    }

    @Override
    public void setInput() {
        SunshineBlue.instance.im.addProcessor(stage);
        SunshineBlue.instance.selectedObjects.clear();
        if (SunshineBlue.instance.isRecording) {
            SunshineBlue.instance.stopRecording();
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
}
