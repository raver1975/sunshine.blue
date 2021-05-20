package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.commands.Command;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;


public class LoopOverlay extends ScreenObject implements Overlay, Drawable {

    public final Stage stage;
    public final Slider sliderLoopLength;
    public final Slider sliderLoopStart;
    public final Slider sliderLoopWidth;


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

        CheckBox pauseCB=new CheckBox("Pause",skin,"switch");
        pauseCB.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SunshineBlue.instance.pauseLoop=pauseCB.isChecked();
//                SunshineBlue.instance.autoloadtime= TimeUtils.millis();
            }
        });
        pauseCB.setPosition(10,10);
        pauseCB.setChecked(SunshineBlue.instance.pauseLoop);
        stage.addActor(pauseCB);

//        SunshineBlue.instance.assetManager.finishLoadingAsset("skin-composer-ui/skin-composer-ui.json");
//        Skin skin1 = SunshineBlue.instance.assetManager.get("skin-composer-ui/skin-composer-ui.json", Skin.class);
//        Skin skin1=new Skin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json"));


        sliderLoopLength = new Slider(1, Statics.RECMAXFRAMESMAX, 1, true, skin);
        sliderLoopLength.setPosition(SunshineBlue.instance.overlayViewport.getWorldWidth() - 40, 80);
        sliderLoopLength.setSize(20, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        sliderLoopLength.setValue(Statics.recframes);
        sliderLoopLength.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println(sliderLoopLength.getValue()+"\t"+sliderLoopStart.getValue()+"\t"+sliderLoopWidth.getValue());
                Statics.recframes = (int) sliderLoopLength.getValue();
                float g = Math.max(0, Statics.recframes - sliderLoopWidth.getValue());
                System.out.println("range:"+g);
                sliderLoopStart.setRange(0,g);
                sliderLoopWidth.setRange(0,sliderLoopLength.getValue());
            }
        });
        ;
        stage.addActor(sliderLoopLength);

        sliderLoopStart = new Slider(0, Statics.RECMAXFRAMESMAX, 1, true, skin);
        sliderLoopStart.setPosition(10, 80);
        sliderLoopStart.setSize(20, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        sliderLoopStart.setValue(0);
        sliderLoopStart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                if (sliderLoopStart.getValue()>= sliderLoopWidth.getValue()){
//                    sliderLoopStart.setValue(sliderLoopWidth.getValue());
//                }
                SunshineBlue.instance.loopStart= (int) sliderLoopStart.getValue();
                SunshineBlue.instance.loopEnd= (int) (sliderLoopStart.getValue()+sliderLoopWidth.getValue());
                if (SunshineBlue.instance.frameCount<SunshineBlue.instance.loopStart||SunshineBlue.instance.frameCount>SunshineBlue.instance.loopEnd){
                    Command.setToFrame(SunshineBlue.instance.loopStart);
                }
            }
        });
        ;
        stage.addActor(sliderLoopStart);

        sliderLoopWidth = new Slider(0, Statics.RECMAXFRAMESMAX, 1, true, skin);
        sliderLoopWidth.setPosition(40, 80);
        sliderLoopWidth.setSize(20, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        sliderLoopWidth.setValue(SunshineBlue.instance.loopEnd-SunshineBlue.instance.loopStart);
        sliderLoopWidth.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                if (sliderLoopStart.getValue()>= sliderLoopWidth.getValue()){
//                    sliderLoopWidth.setValue(sliderLoopStart.getValue());
//                }
                sliderLoopStart.setRange(0,Statics.recframes -sliderLoopWidth.getValue());
                SunshineBlue.instance.loopEnd= (int) (sliderLoopStart.getValue()+sliderLoopWidth.getValue());
            }
        });
        ;
        stage.addActor(sliderLoopWidth);

    }

    public void setTouchable(Touchable touchable) {

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
    public void setInput() {
        SunshineBlue.instance.im.addProcessor(stage);
        sliderLoopLength.setValue(Statics.recframes);
        sliderLoopStart.setValue(SunshineBlue.instance.loopStart);
        sliderLoopWidth.setValue(SunshineBlue.instance.loopEnd-SunshineBlue.instance.loopStart);
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
