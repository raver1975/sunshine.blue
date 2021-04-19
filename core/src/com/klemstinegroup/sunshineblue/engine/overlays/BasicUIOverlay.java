package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import com.klemstinegroup.sunshineblue.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;


public class BasicUIOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final FontOverlay fo;
    private final DrawOverlay dor;
    private final ImageOverlay io;
    private final TransformOverlay to;
    Vector2 touchdown = new Vector2();

    public BasicUIOverlay(AssetManager assetManager, FontOverlay fo,ImageOverlay io, DrawOverlay dor, TransformOverlay to) {
        this.fo=fo;
        this.dor=dor;
        this.io=io;
        this.to=to;
        assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = assetManager.get("skins/orange/skin/uiskin.json",Skin.class);
        stage = new Stage(Statics.overlayViewport);
        TextButton exitButton = new TextButton("X",skin);
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 60, 10);
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
        saveButton.setPosition(10, Statics.overlayViewport.getWorldHeight()-60);
        saveButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
               SerializeUtil.save();
            }
        });
        stage.addActor(saveButton);

        Actor popButton = new TextButton("Pop", skin);
        popButton.setPosition(10, Statics.overlayViewport.getWorldHeight()-120);
        popButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                SerializeUtil.save("pop", new IPFSCIDListener() {
                    @Override
                    public void cid(String cid) {
                        Gdx.net.openURI("https://sunshine.blue/?"+cid);
                    }

                    @Override
                    public void uploadFailed(Throwable t) {

                    }
                });

            }
        });
        stage.addActor(popButton);

        fontButton.addListener(new ClickListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.input.setOnscreenKeyboardVisible(true);
//                Statics.objects.add(new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 40));
//            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.input.setOnscreenKeyboardVisible(true);
                FontObject ff = new FontObject();
                Statics.addUserObj(ff);
                fo.setFontObject(ff);
                fo.generate(assetManager,ff);
                fo.setList();
//                ff.sd.position.set(-ff.sd.center.x, -ff.sd.center.y);
                Vector2 vec = new Vector2(50,50);
                Statics.viewport.unproject(Statics.overlayViewport.project(vec));
//                Statics.viewport.project(Statics.overlayViewport.unproject(vec));
//                Statics.overlayViewport.unproject(Statics.viewport.project(vec));
//                Statics.overlayViewport.project(Statics.viewport.unproject(vec));
                System.out.println(vec);
                ff.sd.position.set(vec);
                Overlay.setOverlay(fo);

//        ((ScreenObject) Statics.objects.get(0)).position.set(-((ScreenObject) Statics.objects.get(0)).bounds.x/2, -((ScreenObject) Statics.objects.get(0)).bounds.y/2, 0);
            }
        });
        fontButton.setPosition(10,10);
        stage.addActor(fontButton);


        Actor imageButton = new TextButton("Image", skin);
        imageButton.setPosition(100, 10);
//        fontButton.setColor(Color.WHITE);


        //                    pasteButton.setVisible(false);



        imageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.setOverlay(io);
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
                Statics.addUserObj(doi);
                dor.setTouchable(doi);
                Overlay.setOverlay(dor);
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
        Statics.viewport.unproject(touchdown.set(screenX, screenY));
        Statics.selectedObjects.clear();
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                if (((Touchable) bo).isSelected(touchdown.cpy())) {
                    Statics.selectedObjects.add(bo);
                }

            }
        }
        if (Statics.selectedObjects.size > 0) {
            Overlay.setOverlay(to);
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

//        Statics.batch.setProjectionMatrix(mx4Overlay.idt());
        for (int i = 0; i < Statics.selectedObjects.size; i++) {
            SunshineBlue.shapedrawer.filledCircle(170 + 30 * i, 20, 10);
        }
        stage.draw();
    }

    @Override
    public void setInput() {
        Statics.im.addProcessor(stage);
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
        Statics.im.removeProcessor(stage);
    }

    @Override
    public void act() {
        stage.act();
    }
}
