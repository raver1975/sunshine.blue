package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.colorpicker.DialogColorPicker;
import com.klemstinegroup.sunshineblue.colorpicker.Spinner;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.FontObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;

public class FontOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    //    private final List<String> list;
    private final SelectBox selectBox;
    private final Slider slider;
    public BaseObject fontObject;
    private Vector2 touchdown = new Vector2();
    Vector2 touchdrag = new Vector2();

    public FontOverlay() {
        FileHandle[] fontList = Gdx.files.internal("fonts").list();
        new BitmapFont();
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
//        "default": {
//            "buttonMinusStyle": "spinner-minus-h",
//                    "buttonPlusStyle": "spinner-plus-h",
//                    "textFieldStyle": "spinner"
//        }
        SunshineBlue.instance.assetManager.finishLoadingAsset("skin-composer-ui/skin-composer-ui.json");
        Skin skin1 = SunshineBlue.instance.assetManager.get("skin-composer-ui/skin-composer-ui.json", Skin.class);
//        Skin skin1=new Skin(Gdx.files.internal("skin-composer-ui/skin-composer-ui.json"));

        Button.ButtonStyle buttonMinusStyle = skin1.get("spinner-minus-h", Button.ButtonStyle.class);
        Button.ButtonStyle buttonPlusStyle = skin1.get("spinner-plus-h", Button.ButtonStyle.class);
        TextField.TextFieldStyle textFieldStyle = skin1.get("spinner", TextField.TextFieldStyle.class);
        Spinner.SpinnerStyle style = new Spinner.SpinnerStyle(buttonMinusStyle, buttonPlusStyle, textFieldStyle);

        skin1.add("default", style);
        DialogColorPicker picker = new DialogColorPicker("main", skin1, new DialogColorPicker.ColorListener() {
            @Override
            public void selected(Color color) {
                if (fontObject != null) ((FontObject)fontObject).setColor(color);
            }
        }, Color.WHITE);
//        picker.setResizable(true);
//        picker.setScale(.9f);

        picker.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fontObject != null) ((FontObject)fontObject).setColor(picker.getSelectedColor());
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
        colorButton.setPosition(10, 70);
        stage.addActor(colorButton);

        TextButton keyButton = new TextButton("key", skin);
        keyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SunshineBlue.nativeNet.openKeyboard();
            }
        });
        keyButton.setPosition(10, 130);
        stage.addActor(keyButton);


//        list = new List(skin);
//        list.addListener(new ChangeListener() {
//
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if (fontObject != null) {
//                    fontObject.setFont((String)((List) actor).getSelected());
//                    generate();
//                }
//            }
//        });
        selectBox = new SelectBox<String>(skin);
        selectBox.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fontObject != null) {
                    ((FontObject)fontObject).setFont((String) ((SelectBox<String>) actor).getSelected());
                    generate(SunshineBlue.instance.assetManager, ((FontObject)fontObject));
                }
            }
        });
        Array<String> fontListStr = new Array<>();

        for (FileHandle fh : fontList) {
            fontListStr.add(fh.nameWithoutExtension());
        }
        fontListStr.sort();
//        list.setItems(fontListStr);
//        list.layout();

//        scrollPane.setHeight(Statics.overlayViewport.getWorldHeight());
        selectBox.setPosition(10, 10);
//        scrollPane.setFlickScroll(true);
        selectBox.setWidth(200);
        selectBox.setScrollingDisabled(false);
        selectBox.setItems(fontListStr);
        selectBox.layout();

        TextButton downArrow = new TextButton("v", skin);
        downArrow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = selectBox.getSelectedIndex();
                if (index == selectBox.getItems().size-1) {index = -1;}
                selectBox.setSelectedIndex(index+1);
            }
        });
        downArrow.setPosition(selectBox.getX() + selectBox.getWidth() + 10, 10);
        stage.addActor(downArrow);

        TextButton upArrow = new TextButton("^", skin);
        upArrow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = selectBox.getSelectedIndex();
                if (index == 0) {index = selectBox.getItems().size ;}
                selectBox.setSelectedIndex(index-1);
            }
        });
        upArrow.setPosition(selectBox.getX() + selectBox.getWidth() + 10, 70);
        stage.addActor(upArrow);

         slider = new Slider(1, 218, 1, true, skin);
        slider.setPosition(SunshineBlue.instance.overlayViewport.getWorldWidth() - 40, 80);
        slider.setSize(20, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        slider.setValue(50);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!slider.isDragging()) {


                    ((FontObject)fontObject).setSize((int) (slider.getValue()));
                    generate(SunshineBlue.instance.assetManager, ((FontObject)fontObject));
                    setBounds();
                }
            }
        });
        ;
        stage.addActor(slider);
        stage.addActor(exitButton);
        stage.addActor(selectBox);
    }

    @Override
    public void setObject(BaseObject fontObject) {
        this.fontObject = fontObject;
        SunshineBlue.instance.selectedObjects.clear();
        SunshineBlue.instance.selectedObjects.add(fontObject);
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
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        ((FontObject)fontObject).setBounds();
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        SunshineBlue.instance.viewport.unproject(touchdrag.set(screenX, screenY));

        ((FontObject)fontObject).sd.position.add(touchdrag.cpy().sub(touchdown));
        ((FontObject)fontObject).setBounds();

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
    public void draw(Batch batch,float delta,boolean bounds) {
        stage.act();
        stage.draw();
//        if (fontObject != null) {
//            ((FontObject)fontObject).draw(batch);
//        }
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
        if (fontObject != null) {
            SunshineBlue.instance.im.addProcessor(((FontObject) fontObject));
            generate(SunshineBlue.instance.assetManager, ((FontObject) fontObject));
            selectBox.setSelected(((FontObject) fontObject).fd.fontName);
            slider.setValue(((FontObject) fontObject).fd.size);
        }

    }

    @Override
    public void removeInput() {
        SunshineBlue.instance.im.removeProcessor(stage);
        if (fontObject != null) SunshineBlue.instance.im.removeProcessor(((FontObject)fontObject));
    }

    public static void generate(AssetManager assetManager, FontObject fontObject) {
        FileHandle[] fontList = Gdx.files.internal("fonts").list();
        FileHandle ff = fontList[MathUtils.random(fontList.length - 1)];
        if (fontObject.fd.fontName == null) {
            fontObject.fd.fontName = ff.nameWithoutExtension();
        }
        for (FileHandle fh : fontList) {
            if (fh.nameWithoutExtension().equals(fontObject.fd.fontName)) {
                ff = fh;
            }
        }
//        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(ff);
        FreetypeFontLoader.FreeTypeFontLoaderParameter parameter;
        parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        parameter.fontParameters.size = fontObject.fd.size;
        parameter.fontParameters.color = Color.WHITE;
        parameter.fontFileName = ff.path();
        String random = ff.pathWithoutExtension() + "-" + fontObject.fd.size + ".ttf";

        assetManager.load(random, BitmapFont.class, parameter);
        assetManager.finishLoadingAsset(random);
        fontObject.font = assetManager.get(random, BitmapFont.class);
        fontObject.setBounds();
//        sd.center.set(sd.bounds.x / 2f, sd.bounds.y / 2f);
    }

    public void setList() {
        String name = ((FontObject)fontObject).fd.fontName;
        Gdx.app.log("name", name);
        int dotIndex = name.lastIndexOf('.');

        selectBox.setSelected(dotIndex == -1 ? name : name.substring(0, dotIndex));
        selectBox.layout();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
