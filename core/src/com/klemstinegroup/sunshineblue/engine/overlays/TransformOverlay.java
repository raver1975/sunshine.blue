package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;
import com.klemstinegroup.sunshineblue.engine.util.ColorHelper;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;


public class TransformOverlay extends BaseObject implements Overlay, Touchable, Drawable, Gestureable {

    public final Stage stage;
    private final Group transformGroup;
    private final CheckBox moveButton;
    public int transformButton;
    Vector2 touchdrag = new Vector2();
    Vector2 touchdown = new Vector2();
    Array<CheckBox> checkBoxArray = new Array<>();

    public TransformOverlay() {
        stage = new Stage(SunshineBlue.instance.overlayViewport);
        SunshineBlue.instance.assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = SunshineBlue.instance.assetManager.get("skins/orange/skin/uiskin.json", Skin.class);

//        CheckBox exitButton = new CheckBox("", skin);
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

        TextButton cpyButton = new TextButton("Cpy", skin);
        cpyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (BaseObject ba : SunshineBlue.instance.selectedObjects) {
                    SerializeUtil.copy(ba);
                }
            }
        });
        cpyButton.setPosition(70, 140);
        stage.addActor(cpyButton);

        TextButton delButton = new TextButton("Del", skin);
        delButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (BaseObject ba : SunshineBlue.instance.selectedObjects) {
                    if (SunshineBlue.instance.userObjects.contains(ba, true)) {
                        SunshineBlue.instance.userObjects.removeValue(ba, true);
                    }
                }
                SunshineBlue.instance.selectedObjects.clear();
                Overlay.backOverlay();
            }
        });
        delButton.setPosition(10, 140);
        stage.addActor(delButton);

        TextButton downArrow = new TextButton("v", skin);
        downArrow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (BaseObject ba : SunshineBlue.instance.selectedObjects) {
                    if (ba instanceof ScreenObject) {
                        ScreenObject so = (ScreenObject) ba;
                        so.sd.layer--;
                    }
                }
            }
        });
        downArrow.setPosition(10, 200);
        stage.addActor(downArrow);

        TextButton upArrow = new TextButton("^", skin);
        upArrow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (BaseObject ba : SunshineBlue.instance.selectedObjects) {
                    if (ba instanceof ScreenObject) {
                        ScreenObject so = (ScreenObject) ba;
                        so.sd.layer++;
                    }
                }
            }
        });
        upArrow.setPosition(10, 260);
        stage.addActor(upArrow);

        ButtonGroup<CheckBox> transformButtons = new ButtonGroup();
        moveButton = new CheckBox(" Move", skin);
        moveButton.getStyle().fontColor = Color.RED;
        moveButton.setName("move");
        moveButton.setPosition(10, 0);
        CheckBox rotateButton = new CheckBox(" Rotate", skin);
        rotateButton.setPosition(10, 30);
        rotateButton.setName("rotate");
        CheckBox scaleButton = new CheckBox(" Scale", skin);
        scaleButton.setPosition(10, 60);
        scaleButton.setName("scale");
        CheckBox centerButton = new CheckBox(" Center", skin);
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
                transformButton = 0;
            }
        });
        rotateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                transformButton = 1;
            }
        });
        scaleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                transformButton = 2;
            }
        });
        centerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                transformButton = 3;
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

        SunshineBlue.instance.viewport.unproject(touchdown.set(screenX, screenY));
//        SunshineBlue.instance.selectedObjects.clear();
//        for (BaseObject bo : SunshineBlue.instance.userObjects) {
//            if (bo instanceof Touchable) {
//                if (((Touchable) bo).isSelected(touchdown.cpy())) {
//                    SunshineBlue.instance.selectedObjects.add(bo);
//                }
//
//            }
//        }


        for (BaseObject bo : SunshineBlue.instance.selectedObjects) {
            if (bo instanceof ScreenObject) {
                ScreenObject so = ((ScreenObject) bo);
                switch (transformButton) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        so.recenter(touchdown.cpy());
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
        for (BaseObject bo : SunshineBlue.instance.selectedObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).setBounds();
            }
        }
        InputEvent event1 = new InputEvent();
        event1.setType(InputEvent.Type.touchDown);
        moveButton.fire(event1);

        InputEvent event2 = new InputEvent();
        event2.setType(InputEvent.Type.touchUp);
        moveButton.fire(event2);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        Statics.viewport.unproject(touchdrag.set(screenX, screenY));
        SunshineBlue.instance.viewport.unproject(touchdrag.set(screenX, screenY));

        for (BaseObject bo : SunshineBlue.instance.selectedObjects) {
            if (bo instanceof ScreenObject) {
                ScreenObject so = ((ScreenObject) bo);
                switch (transformButton) {
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
                        so.recenter(touchdown.cpy());
                        break;
                }
                if (bo instanceof Touchable) {
                    ((Touchable) bo).setBounds();
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
//        for (int i = 0; i < SunshineBlue.instance.selectedObjects.size; i++) {
//            SunshineBlue.instance.shapedrawer.setColor(ColorHelper.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(SunshineBlue.instance.selectedObjects.get(i), true) / ((float) SunshineBlue.instance.userObjects.size-1)).cpy().lerp(Color.WHITE,SunshineBlue.instance.colorFlash));
//            SunshineBlue.instance.shapedrawer.filledCircle(170 + 30 * i, 20, 10);
//        }
//        for (CheckBox cb:checkBoxArray){
//            cb.getImage().setColor(ColorHelper.numberToColorPercentage((float) checkBoxArray.indexOf(cb,true) / ((float) SunshineBlue.instance.userObjects.size-1)).cpy().lerp(Color.WHITE,SunshineBlue.instance.colorFlash));
//            cb.setColor(ColorHelper.numberToColorPercentage((float) checkBoxArray.indexOf(cb,true) / ((float) SunshineBlue.instance.userObjects.size-1)).cpy().lerp(Color.WHITE,SunshineBlue.instance.colorFlash));
//        }
        stage.draw();
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
    public void setInput() {
        SunshineBlue.instance.im.addProcessor(stage);
        Skin skin = SunshineBlue.instance.assetManager.get("skins/orange/skin/uiskin.json", Skin.class);
//        Skin skin = SunshineBlue.instance.assetManager.get("skin-composer-ui/skin-composer-ui.json", Skin.class);
        int sx = 150, sy = 10;
        int xc = 0;
        int yc = 0;
        int cnt=0;
        for (BaseObject ba : SunshineBlue.instance.selectedObjects) {
            CheckBox cb = new CheckBox("", skin, "switch");
            cb.setUserObject(ba);
//            ((TextureRegionDrawable)cb.getImage().getDrawable()).tint(Color.BLACK);
            cb.getImage().setColor(ColorHelper.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(ba, true) / ((float) SunshineBlue.instance.userObjects.size - 1)).cpy().lerp(Color.WHITE, .3f));
//            cb.setColor(Color.BLACK);
            cb.setChecked(true);
            cb.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
//                        ((ScreenObject)ba).sd.visible=cb.isChecked();
                    if (cb.isChecked()) {
                        SunshineBlue.instance.selectedObjects.add(ba);
                    } else {
                        SunshineBlue.instance.selectedObjects.removeValue(ba, true);
                    }
                }
            });
            cb.addListener(new ActorGestureListener(){
                @Override
                public boolean longPress(Actor actor, float x, float y) {
                    cb.setChecked(!cb.isChecked());

                    for (CheckBox cb1:checkBoxArray) {
                        if (!cb.getUserObject().equals(cb1.getUserObject())) {
                            stage.getActors().removeValue(cb1, true);
                            //checkBoxArray.removeValue(cb1, true);
                        }
                    }
                    SunshineBlue.instance.selectedObjects.clear();
//                    SunshineBlue.instance.selectedObjects.add((BaseObject) cb.getUserObject());
                    return true;
                }
            });
            cb.setPosition(sx + xc * 50, sy + yc * 30);
            stage.addActor(cb);
            checkBoxArray.add(cb);
            xc++;
            if (sx + xc * 50>SunshineBlue.instance.overlayViewport.getWorldWidth() - 90) {
                xc = 0;
                yc++;
            }

        }
//        transformGroup.setVisible(Statics.selectedObjects.size>0);
    }

    @Override
    public void removeInput() {
        SunshineBlue.instance.im.removeProcessor(stage);
        for (CheckBox cb : checkBoxArray) {
            stage.getActors().removeValue(cb, true);
        }
        checkBoxArray.clear();
    }

    @Override
    public void act() {
        stage.act();
    }

    @Override
    public void setObject(BaseObject bo) {

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
