package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.colorpicker.DialogColorPicker;
import com.klemstinegroup.sunshineblue.colorpicker.Spinner;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;
import sun.security.provider.Sun;


public class LoopOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    public final Slider sliderLoopLength;
    public final Slider sliderLoopStart;
    public final Slider sliderLoopEnd;
    public final DialogColorPicker picker;
    Touchable touchable;
    BaseObject drawObject;
    Vector2 touchdrag = new Vector2();
    Vector2 touchdown = new Vector2();


    public LoopOverlay() {
        stage = new Stage(SunshineBlue.instance.overlayViewport);
        SunshineBlue.instance.assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = SunshineBlue.instance.assetManager.get("skins/orange/skin/uiskin.json", Skin.class);
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


        SunshineBlue.instance.assetManager.finishLoadingAsset("skin-composer-ui/skin-composer-ui.json");
        Skin skin1 = SunshineBlue.instance.assetManager.get("skin-composer-ui/skin-composer-ui.json", Skin.class);
//        Skin skin1=new Skin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json"));

        Button.ButtonStyle buttonMinusStyle = skin1.get("spinner-minus-h", Button.ButtonStyle.class);
        Button.ButtonStyle buttonPlusStyle = skin1.get("spinner-plus-h", Button.ButtonStyle.class);
        TextField.TextFieldStyle textFieldStyle = skin1.get("spinner", TextField.TextFieldStyle.class);
        Spinner.SpinnerStyle style = new Spinner.SpinnerStyle(buttonMinusStyle, buttonPlusStyle, textFieldStyle);

        skin1.add("default", style);
        picker = new DialogColorPicker("main", skin1, new DialogColorPicker.ColorListener() {
            @Override
            public void selected(Color color) {
                SunshineBlue.instance.bgColor = color.cpy();
            }
        }, SunshineBlue.instance.bgColor);
//        picker.setResizable(true);
//        picker.setScale(.9f);

        picker.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SunshineBlue.instance.bgColor = picker.getSelectedColor().cpy();
            }
        });


        sliderLoopLength = new Slider(1, 600, 1, true, skin);
        sliderLoopLength.setPosition(SunshineBlue.instance.overlayViewport.getWorldWidth() - 40, 80);
        sliderLoopLength.setSize(20, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        sliderLoopLength.setValue(Statics.RECMAXFRAMES);
        sliderLoopLength.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Statics.RECMAXFRAMES= (int) sliderLoopLength.getValue();
                sliderLoopStart.setRange(0,sliderLoopLength.getValue());
                sliderLoopEnd.setRange(0,sliderLoopLength.getValue());
            }
        });
        ;
        stage.addActor(sliderLoopLength);

        sliderLoopStart = new Slider(0, 600, 1, true, skin);
        sliderLoopStart.setPosition(10, 80);
        sliderLoopStart.setSize(20, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        sliderLoopStart.setValue(0);
        sliderLoopStart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (sliderLoopStart.getValue()>= sliderLoopEnd.getValue()){
                    sliderLoopStart.setValue(sliderLoopEnd.getValue()-1);
                }
                SunshineBlue.instance.loopStart= (int) sliderLoopStart.getValue();
            }
        });
        ;
        stage.addActor(sliderLoopStart);

        sliderLoopEnd = new Slider(0, 600, 1, true, skin);
        sliderLoopEnd.setPosition(40, 80);
        sliderLoopEnd.setSize(20, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        sliderLoopEnd.setValue(Statics.RECMAXFRAMES);
        sliderLoopEnd.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (sliderLoopStart.getValue()>= sliderLoopEnd.getValue()){
                    sliderLoopEnd.setValue(sliderLoopStart.getValue()+1);
                }
                SunshineBlue.instance.loopEnd= (int) sliderLoopEnd.getValue();
            }
        });
        ;
        stage.addActor(sliderLoopEnd);

    }

    public void setTouchable(Touchable touchable) {
        this.touchable = touchable;
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
        touchdown.set(screenX, -screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (BaseObject bo : SunshineBlue.instance.selectedObjects) {
            if (bo instanceof Touchable) {
                ((Touchable) bo).setBounds();
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchdrag.set(screenX, -screenY);
        ((OrthographicCamera)SunshineBlue.instance.viewport.getCamera()).translate(touchdown.sub(touchdrag));
        touchdown.set(touchdrag);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    float cflash=0;
    @Override
    public void draw(Batch batch, float delta) {
        cflash=(cflash+delta)%1.0f;
        /*if (SunshineBlue.instance.recordRect!=null){
            SunshineBlue.instance.shapedrawer.rectangle(SunshineBlue.instance.recordRect,ColorHelper.numberToColorPercentage(cflash),6);
        }
        else{
            SunshineBlue.instance.recordRect = new Rectangle(SunshineBlue.instance.viewport.getScreenX(), SunshineBlue.instance.viewport.getScreenY(), SunshineBlue.instance.viewport.getWorldWidth(), SunshineBlue.instance.viewport.getWorldHeight());
        }*/
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
        if (touchable != null) SunshineBlue.instance.im.addProcessor(touchable);
        sliderLoopLength.setValue(Statics.RECMAXFRAMES);
        sliderLoopStart.setValue(SunshineBlue.instance.loopStart);
        sliderLoopEnd.setValue(SunshineBlue.instance.loopEnd);
    }

    @Override
    public void removeInput() {
        SunshineBlue.instance.im.removeProcessor(stage);
        if (touchable != null) SunshineBlue.instance.im.removeProcessor(touchable);
    }

    @Override
    public void setObject(BaseObject doi) {
        this.drawObject = doi;
    }
}
