package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.DrawObject;
import com.klemstinegroup.sunshineblue.engine.objects.FontObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;
import com.klemstinegroup.sunshineblue.engine.util.FrameBufferUtils;
import com.klemstinegroup.sunshineblue.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshineblue.engine.util.IPFSUtils;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;


public class BasicUIOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    Vector2 touchdown = new Vector2();

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
        saveButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 60);
        saveButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SerializeUtil.save();
            }
        });
        stage.addActor(saveButton);

        Actor popButton = new TextButton("Pop", skin);
        popButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 120);
        popButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SerializeUtil.save("pop", new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        if (Statics.debug && Gdx.app.getType() == Application.ApplicationType.Desktop) {
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

        Actor apngButton = new TextButton("REC", skin);
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

        Actor screenshotButton = new TextButton("PNG", skin);
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
                SunshineBlue.instance.FONT_OVERLAY.setFontObject(ff);
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


        Actor imageButton = new TextButton("Image", skin);
        imageButton.setPosition(100, 10);
//        fontButton.setColor(Color.WHITE);


        //                    pasteButton.setVisible(false);


        imageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.setOverlay(SunshineBlue.instance.IMAGE_OVERLAY);
            }
        });

        Actor drawButton = new TextButton("Draw", skin);
        drawButton.setPosition(200, 10);
        drawButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.input.setOnscreenKeyboardVisible(false);
                DrawObject doi = new DrawObject();
                SunshineBlue.instance.addUserObj(doi);
                SunshineBlue.instance.DRAW_OVERLAY.setTouchable(doi);
                Overlay.setOverlay(SunshineBlue.instance.DRAW_OVERLAY);
//                pasteButton.setVisible(true);
            }
        });
        stage.addActor(drawButton);
        stage.addActor(imageButton);
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

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        SunshineBlue.instance.viewport.unproject(touchdown.set(screenX, screenY));
        SunshineBlue.instance.selectedObjects.clear();
        for (BaseObject bo : SunshineBlue.instance.userObjects) {
            if (bo instanceof Touchable) {
                if (((Touchable) bo).isSelected(touchdown.cpy())) {
                    SunshineBlue.instance.selectedObjects.add(bo);
                }

            }
        }
        if (SunshineBlue.instance.selectedObjects.size > 0) {
            Overlay.setOverlay(SunshineBlue.instance.TRANSFORM_OVERLAY);
        } else {
//            Overlay.setOverlay(this);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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
    public void draw(Batch batch) {

//        mx4Overlay.set(mx4Overlay.idt());
//        mx4Overlay.setToOrtho2D(0, 0, 100, 100);
//        mx4Overlay.

//        SunshineBlue.instance.batch.setProjectionMatrix(mx4Overlay.idt());
        for (int i = 0; i < SunshineBlue.instance.selectedObjects.size; i++) {
            SunshineBlue.instance.shapedrawer.filledCircle(170 + 30 * i, 20, 10);
        }
        stage.draw();
    }

    @Override
    public void setInput() {
        SunshineBlue.instance.im.addProcessor(stage);
        SunshineBlue.instance.selectedObjects.clear();
        if (SunshineBlue.instance.isRecording){SunshineBlue.instance.stopRecording();}
    }

    @Override
    public boolean isSelected(Vector2 touch) {
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
    public void act() {
        stage.act();
    }
}
