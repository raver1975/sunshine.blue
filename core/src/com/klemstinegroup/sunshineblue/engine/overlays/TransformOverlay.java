package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;


public class TransformOverlay extends BaseObject implements Overlay, Touchable, Drawable, Gestureable {

    public final Stage stage;
    private final Group transformGroup;

    Vector2 touchdrag = new Vector2();
    Vector2 touchdown = new Vector2();
    private Vector2 touchdragcpy = new Vector2();

    public TransformOverlay(AssetManager assetManager) {
        stage = new Stage(Statics.overlayViewport);
        assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = assetManager.get("skins/orange/skin/uiskin.json",Skin.class);

//        CheckBox exitButton = new CheckBox("", skin);
        TextButton exitButton = new TextButton("X",skin);
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 55, Statics.overlayViewport.getWorldHeight() - 55);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.backOverlay();
            }
        });
        stage.addActor(exitButton);

        ButtonGroup transformButtons = new ButtonGroup();
        CheckBox moveButton = new CheckBox("Move", skin);
        moveButton.getStyle().fontColor = Color.RED;
        moveButton.setName("move");
        moveButton.setPosition(10, 0);
        CheckBox rotateButton = new CheckBox("Rotate", skin);
        rotateButton.setPosition(10, 30);
        rotateButton.setName("rotate");
        CheckBox scaleButton = new CheckBox("Scale", skin);
        scaleButton.setPosition(10, 60);
        scaleButton.setName("scale");
        CheckBox centerButton = new CheckBox("Center", skin);
        centerButton.setPosition(10, 90);
        centerButton.setName("center");

        transformButtons.add(moveButton);
        transformButtons.add(rotateButton);
        transformButtons.add(scaleButton);
        transformButtons.add(centerButton);
        moveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.transformButton = 0;
            }
        });
        rotateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.transformButton = 1;
            }
        });
        scaleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.transformButton = 2;
            }
        });
        centerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.transformButton = 3;
            }
        });


        transformButtons.setUncheckLast(true);
        transformButtons.setChecked("move");
        transformButtons.setMaxCheckCount(1);
        transformButtons.setMinCheckCount(1);
        transformGroup = new Group();
        transformGroup.addActor(moveButton);
        transformGroup.addActor(scaleButton);
        transformGroup.addActor(rotateButton);
        transformGroup.addActor(centerButton);
        transformGroup.setVisible(true);
        stage.addActor(transformGroup);
//stage.addActor(scaleButton);
//stage.addActor(rotateButton);

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

        Statics.viewport.unproject(touchdown.set(screenX, screenY));
//        Statics.selectedObjects.clear();
//        for (BaseObject bo : Statics.userObjects) {
//            if (bo instanceof Touchable) {
//                if (((Touchable) bo).isSelected(touchdown.cpy())) {
//                    Statics.selectedObjects.add(bo);
//                }
//
//            }
//        }


        for (BaseObject bo : Statics.selectedObjects) {
            if (bo instanceof ScreenObject) {
                ScreenObject so = ((ScreenObject) bo);
                switch (Statics.transformButton) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        so.recenter(touchdown);
//                        so.center.sub(touchdragcpy);
//                        so.position.set(touchdown.cpy());
//                        touchdragcpy.scl(so.scale);
//                        touchdragcpy.rotateDeg(so.rotation);
//                        so.position.sub(touchdragcpy);
//                        touchdragcpy.sub(so.center);
//                        ((ScreenObject)bo).recenter(touchdragcpy);
                        break;
                }

            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        Statics.viewport.unproject(touchdrag.set(screenX, screenY));
        Statics.viewport.unproject(touchdrag.set(screenX, screenY));

        for (BaseObject bo : Statics.selectedObjects) {
            if (bo instanceof ScreenObject) {
                ScreenObject so = ((ScreenObject) bo);
                switch (Statics.transformButton) {
                    case 0:
                        so.sd.position.add(touchdrag.cpy().sub(touchdown));
                        break;
                    case 1:
                        so.sd.rotation += touchdrag.x - touchdown.x;
                        break;
                    case 2:
                        so.sd.scale += (touchdrag.x - touchdown.x) / 200f;
                        break;
                    case 3:
                        break;
                }

            }
        }
        touchdown.set(touchdrag);
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
            Statics.shapedrawer.filledCircle(170 + 30 * i, 20, 10);
        }
        stage.draw();
    }

    @Override
    public boolean isSelected(Vector2 touch) {
        return false;
    }

    @Override
    public void setInput() {
        Statics.im.addProcessor(stage);
//        transformGroup.setVisible(Statics.selectedObjects.size>0);
    }

    @Override
    public void removeInput() {
        Statics.im.removeProcessor(stage);
    }

    @Override
    public void act() {
        stage.act();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        System.out.println("long press");
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
