package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.commands.Command;
import com.klemstinegroup.sunshineblue.engine.commands.MoveCommand;
import com.klemstinegroup.sunshineblue.engine.commands.RotateCommand;
import com.klemstinegroup.sunshineblue.engine.commands.ScaleCommand;
import com.klemstinegroup.sunshineblue.engine.objects.*;
import com.klemstinegroup.sunshineblue.engine.util.ColorHelper;
import com.klemstinegroup.sunshineblue.engine.util.IPFSCIDListener;
import com.klemstinegroup.sunshineblue.engine.util.SerializeUtil;

import java.util.Iterator;


public class TransformOverlay extends BaseObject implements Overlay, Touchable, Drawable, Gestureable {

    public final Stage stage;
    private final Group transformGroup;
    private final ButtonGroup<CheckBox> transformButtons;
    private final CheckBox recButton;
    public int transformButton;
    Vector2 touchdrag = new Vector2();
    Vector2 touchdown = new Vector2();
    Vector2 tempVec=new Vector2();
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
        cpyButton.setPosition(10, 140);
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
        delButton.setPosition(10, 200);

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
        downArrow.setPosition(10, 260);
        stage.addActor(downArrow);

        recButton = new CheckBox("Rec", skin, "switch");
        recButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (recButton.isChecked()) {
                } else {
                }
            }
        });
        recButton.setPosition(10, 380);
        stage.addActor(recButton);

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
        upArrow.setPosition(10, 320);
        stage.addActor(upArrow);

        transformButtons = new ButtonGroup();
        CheckBox moveButton = new CheckBox(" Move", skin);
        moveButton.getStyle().fontColor = Color.RED;
        moveButton.setPosition(10, 0);
        CheckBox rotateButton = new CheckBox(" Rotate", skin);
        rotateButton.setPosition(10, 30);
        CheckBox scaleButton = new CheckBox(" Scale", skin);
        scaleButton.setPosition(10, 60);
        CheckBox centerButton = new CheckBox(" Center", skin);
        centerButton.setPosition(10, 90);


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
        transformButtons.setChecked(" Move");
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
        transformButtons.setChecked(" Move");
        transformButton = 0;
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
                        tempVec.set(touchdrag.cpy().sub(touchdown));
                        if (recButton.isChecked()){
                            Command.insert(new MoveCommand(tempVec.cpy(),bo.uuid),bo);
                        }
                        so.sd.position.add(tempVec);
                        break;
                    case 1:
                        float rot=touchdrag.x - touchdown.x;
                        if (recButton.isChecked()){
                            Command.insert(new RotateCommand(rot,bo.uuid),bo);
                        }
                        so.sd.rotation +=rot;
                        break;
                    case 2:
                        float scal=(touchdrag.x - touchdown.x) / 200f;
                        if (recButton.isChecked()){
                            Command.insert(new ScaleCommand(scal,bo.uuid),bo);
                        }
                        so.sd.scale += scal;
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
    public void draw(Batch batch, float delta) {

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
        stage.act();
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
        int cnt = 0;
        for (BaseObject ba : SunshineBlue.instance.selectedObjects) {
            CheckBox cb = new CheckBox("", skin, "switch");
            cb.setUserObject(ba);
//            ((TextureRegionDrawable)cb.getImage().getDrawable()).tint(Color.BLACK);
            cb.getImage().setColor(ColorHelper.numberToColorPercentage((float) SunshineBlue.instance.userObjects.indexOf(ba, true) / ((float) SunshineBlue.instance.userObjects.size - 1)).cpy().lerp(Color.WHITE, .3f));
//            cb.setColor(Color.BLACK);
            cb.setChecked(((ScreenObject) ba).sd.visible);
            cb.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
//                    cb.setChecked(!cb.isChecked());
                    if (cb.isChecked()) {
                        SunshineBlue.instance.selectedObjects.add(ba);
                    } else {
                        SunshineBlue.instance.selectedObjects.removeValue(ba, true);
                    }

//                    cb.setStyle(new CheckBox.CheckBoxStyle(((TextureRegionDrawable)skin.getDrawable("switch")).tint(((ScreenObject) ba).sd.visible ? Color.WHITE : Color.GRAY), ((TextureRegionDrawable)skin.getDrawable("switch-off")).tint(((ScreenObject) ba).sd.visible ? Color.WHITE : Color.GRAY), cb.getStyle().font, ((ScreenObject) ba).sd.visible ? Color.WHITE : Color.GRAY));
                }
            });
            cb.addListener(new ActorGestureListener() {
                @Override
                public boolean longPress(Actor actor, float x, float y) {
                    cb.setChecked(!cb.isChecked());
                    ((ScreenObject) ba).sd.visible = !((ScreenObject) ba).sd.visible;
//                        cb.setVisible(false);

                    return true;
                }
            });
            cb.setPosition(sx + xc * 50, sy + yc * 30);
            stage.addActor(cb);
            checkBoxArray.add(cb);
            xc++;
            if (sx + xc * 50 > SunshineBlue.instance.overlayViewport.getWorldWidth() - 90) {
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
        if (SunshineBlue.instance.selectedObjects.size == 1) {
            if (SunshineBlue.instance.selectedObjects.get(0) instanceof FontObject) {
                SunshineBlue.instance.FONT_OVERLAY.setObject(SunshineBlue.instance.selectedObjects.get(0));
                Overlay.setOverlay(SunshineBlue.instance.FONT_OVERLAY);
            }
            if (SunshineBlue.instance.selectedObjects.get(0) instanceof DrawObject) {
                SunshineBlue.instance.DRAW_OVERLAY.setObject(SunshineBlue.instance.selectedObjects.get(0));
                Overlay.setOverlay(SunshineBlue.instance.DRAW_OVERLAY);
            }
            if (SunshineBlue.instance.selectedObjects.get(0) instanceof ParticleObject) {
                SunshineBlue.instance.PARTICLE_OVERLAY.setObject(SunshineBlue.instance.selectedObjects.get(0));
                Overlay.setOverlay(SunshineBlue.instance.PARTICLE_OVERLAY);
            }
        }
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
