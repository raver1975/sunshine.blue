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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.crashinvaders.vfx.effects.*;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.colorpicker.DialogColorPicker;
import com.klemstinegroup.sunshineblue.colorpicker.Spinner;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class BackgroundOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    public final Slider slider;
    public final DialogColorPicker picker;
    Touchable touchable;
    BaseObject drawObject;
    Vector2 touchdrag = new Vector2();
    Vector2 touchdown = new Vector2();


    public BackgroundOverlay() {
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
                if (color != null) {
                    SunshineBlue.instance.bgColor = color.cpy();
                }
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
        TextButton colorButton = new TextButton("color", skin);
        colorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //displaying picker with fade in animation
                if (stage.getActors().contains(picker, true)) {
                    picker.remove();
                } else {
                    picker.show(stage);
                }
            }
        });
        colorButton.setPosition(10, 10);
        stage.addActor(colorButton);

        slider = new Slider(0f, 9.99f, .01f, true, skin);
        slider.setPosition(SunshineBlue.instance.overlayViewport.getWorldWidth() - 40, 80);
        slider.setSize(20, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        slider.setValue(9);
//slider.setWidth(100);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               /* if (!slider.isDragging()) {
                    ((DrawObject) drawObject).setSize((int) (slider.getValue()));
                    setBounds();
                }*/
                ((OrthographicCamera) SunshineBlue.instance.viewport.getCamera()).zoom = 10f - slider.getValue();
            }
        });

        stage.addActor(slider);

        Array<ChainVfxEffect> effects = new Array<>();
        effects.add(new BloomEffect());
        effects.add(new NfaaEffect(true));
        effects.add(new FxaaEffect());
        effects.add(new CrtEffect());
//        effects.add(new LensFlareEffect());
//        effects.add(new RadialBlurEffect(1));
//        effects.add(new RadialDistortionEffect());
        effects.add(new OldTvEffect());
        effects.add(new FisheyeEffect());
//        effects.add(new FilmGrainEffect());
        effects.add(new ChromaticAberrationEffect(10));
        effects.add(new GaussianBlurEffect());
//        effects.add(new LevelsEffect());
        effects.add(new WaterDistortionEffect(2, 1));
//        effects.add(new MotionBlurEffect(Pixmap.Format.RGBA8888,MixEffect.Method.MIX,.5f));
//        effects.add(new GammaThresholdEffect(GammaThresholdEffect.Type.RGBA));
        for (ChainVfxEffect fx : effects) {
            SunshineBlue.instance.vfxManager.addEffect(fx);
            fx.setDisabled(true);
        }
        effects.get(0).setDisabled(false);
        effects.get(1).setDisabled(false);
        for (int i = 0; i < effects.size; i++) {
            CheckBox cb = new CheckBox(" " + effects.get(i).getClass().getSimpleName().replaceAll("Effect", ""), skin, "switch");
            cb.setPosition(10, 70 + i * 30);
            cb.setChecked(!effects.get(i).isDisabled());
            stage.addActor(cb);
            int finalI = i;
            cb.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    effects.get(finalI).setDisabled(!cb.isChecked());
                }
            });

        }
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
        ((OrthographicCamera) SunshineBlue.instance.viewport.getCamera()).translate(touchdown.sub(touchdrag));
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

    float cflash = 0;

    @Override
    public void draw(Batch batch, float delta) {
        cflash = (cflash + delta) % 1.0f;
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
        slider.setValue(10f - ((OrthographicCamera) SunshineBlue.instance.viewport.getCamera()).zoom);
    }

    @Override
    public void removeInput() {
        SunshineBlue.instance.im.removeProcessor(stage);
        if (touchable != null) SunshineBlue.instance.im.removeProcessor(touchable);
    }

    @Override
    public void setObject1(BaseObject doi) {
        this.drawObject = doi;
        SunshineBlue.instance.selectedObjects.clear();
        SunshineBlue.instance.selectedObjects.add(doi);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
