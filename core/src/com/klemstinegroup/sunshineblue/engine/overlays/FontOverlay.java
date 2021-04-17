package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.colorpicker.DialogColorPicker;
import com.klemstinegroup.sunshineblue.colorpicker.Spinner;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.objects.FontObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class FontOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final TransformOverlay to;
    public FontObject fontObject;
    private Vector2 touchdown=new Vector2();

    public FontOverlay(AssetManager assetManager, TransformOverlay to) {
        this.to=to;
        new BitmapFont();
        stage = new Stage(Statics.overlayViewport);
        assetManager.finishLoadingAsset("skins/orange/skin/uiskin.json");
        Skin skin = assetManager.get("skins/orange/skin/uiskin.json",Skin.class);
        TextButton exitButton = new TextButton("X",skin);
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 55, Statics.overlayViewport.getWorldHeight() - 55);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Overlay.backOverlay();
            }
        });

        /*//picker creation
        ColorPicker picker = new ColorPicker(new ColorPickerAdapter() {

            @Override
            public void canceled(Color oldColor) {
                if (fontObject != null) fontObject.setColor(oldColor);
            }

            @Override
            public void changed(Color newColor) {
                if (fontObject != null) fontObject.setColor(newColor);
            }

            @Override
            public void reset(Color previousColor, Color newColor) {
                if (fontObject != null) fontObject.setColor(newColor);
            }

            @Override
            public void finished(Color newColor) {
                if (fontObject != null) fontObject.setColor(newColor);
            }
        });
        picker.setVisible(true);
        picker.setAllowAlphaEdit(true);
        picker.setCloseAfterPickingFinished(true);
        picker.setShowHexFields(true);
        picker.setModal(false);
        picker.setCenterOnAdd(true);
        picker.setColor(Color.WHITE);
//        picker.setResizable(true);

//...

//        stage.addActor(colorPicker);
*/
        Gdx.app.log("color-picker","here");
//        "default": {
//            "buttonMinusStyle": "spinner-minus-h",
//                    "buttonPlusStyle": "spinner-plus-h",
//                    "textFieldStyle": "spinner"
//        }
        Skin skin1=new Skin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json"));

        Button.ButtonStyle buttonMinusStyle = skin1.get("spinner-minus-h", Button.ButtonStyle.class);
        Button.ButtonStyle buttonPlusStyle = skin1.get("spinner-plus-h", Button.ButtonStyle.class);
        TextField.TextFieldStyle textFieldStyle = skin1.get("spinner", TextField.TextFieldStyle.class);
        Spinner.SpinnerStyle style=new Spinner.SpinnerStyle(buttonMinusStyle,buttonPlusStyle,textFieldStyle);

        skin1.add("default",style);
        DialogColorPicker picker = new DialogColorPicker("default", skin1, new DialogColorPicker.ColorListener() {
            @Override
            public void selected(Color color) {
                if (fontObject != null) fontObject.setColor(color);
            }
        }, Color.RED);

        picker.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fontObject != null) fontObject.setColor(picker.getSelectedColor());
            }
        });
//        picker.setScale(.7f);
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
        colorButton.setPosition(Statics.overlayViewport.getWorldWidth() - 55, 10);
        stage.addActor(colorButton);

        List list = new List(skin);
        list.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fontObject != null) {
                    ((FontObject) fontObject).setFont((String)((List) actor).getSelected());
                    ((FontObject) fontObject).generate();
                }
            }
        });
        ScrollPane scrollPane = new ScrollPane(list,skin);
        Array<String> fontListStr = new Array<>();
        FileHandle[] fontList = Gdx.files.internal("fonts").list();
        for (FileHandle fh : fontList) {
            fontListStr.add(fh.nameWithoutExtension());
        }
        fontListStr.sort();
        list.setItems(fontListStr);
        list.layout();

        scrollPane.setSize(200, Statics.overlayViewport.getWorldHeight());
        scrollPane.setPosition(0, 0);
        scrollPane.setFlickScroll(true);
        scrollPane.setScrollingDisabled(true, false);
        boolean enable_x = false;
        boolean enable_y = true;
        scrollPane.setForceScroll(enable_x,enable_y);

        scrollPane.layout();
        /*final IntSpinnerModel intModel = new IntSpinnerModel(60, 1, 1000, 1);
        Spinner sizeSpinner = new Spinner("", intModel);
        sizeSpinner.setPosition(Statics.overlayViewport.getWorldWidth() - 100, 100);
        sizeSpinner.setRound(true);
        sizeSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fontObject.size = intModel.getValue();
                fontObject.generate();
            }
        });
        stage.addActor(sizeSpinner);*/
        Slider slider = new Slider(1, 500, 1, true, skin);
        slider.setPosition(Statics.overlayViewport.getWorldWidth() - 40,80);
        slider.setSize(20,Statics.overlayViewport.getWorldHeight()-150);
        slider.setValue(50);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fontObject.setSize((int)slider.getValue());
            }
        });
        stage.addActor(slider);
        stage.addActor(exitButton);
        stage.addActor(scrollPane);
    }

    public void setFontObject(FontObject fontObject) {
        if (this.fontObject!=null){
            this.fontObject.edit=false;
        }
        this.fontObject = fontObject;
        if (this.fontObject!=null){
            this.fontObject.edit=true;
        }
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
        if (fontObject.isSelected(touchdown)) {
            Overlay.setOverlay(to);
            to.touchDown(screenX, screenY, pointer, button);
            Statics.selectedObjects.clear();
            Statics.selectedObjects.add(fontObject);
        };
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
        stage.draw();
        if (fontObject != null) {
            fontObject.draw(batch);
        }
    }

    @Override
    public boolean isSelected(Vector2 touch) {
        return false;
    }

    @Override
    public void setInput() {
        Statics.im.addProcessor(stage);
        if (fontObject != null) Statics.im.addProcessor(fontObject);
    }

    @Override
    public void removeInput() {
        Statics.im.removeProcessor(stage);
        if (fontObject != null) Statics.im.removeProcessor(fontObject);
    }

    @Override
    public void act() {
        stage.act();
    }


}