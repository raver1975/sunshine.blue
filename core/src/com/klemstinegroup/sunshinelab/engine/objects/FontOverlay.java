package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

public class FontOverlay extends ScreenObject implements Overlay, Touchable, Drawable{

    public final Stage stage;
    FontObject fontObject;

    public FontOverlay() {

        stage = new Stage(Statics.overlayViewport);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Statics.fontList[MathUtils.random(Statics.fontList.length - 1)]);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
//        int a = MathUtils.randomBoolean() ? 0 : 1;
//        int b = MathUtils.randomBoolean() ? 0 : 1;
        textButtonStyle.font = generator.generateFont(parameter);
        textButtonStyle.overFontColor = Color.WHITE;
        Skin skin = new Skin(Gdx.files.internal("skins/comic/skin/comic-ui.json"));
//        Skin skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));

        CheckBox exitButton = new CheckBox("", skin);
        exitButton.setChecked(true);
        exitButton.setDisabled(true);
        exitButton.getStyle().fontColor = Color.RED;
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 40, Statics.overlayViewport.getWorldHeight() - 40);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.backOverlay();
            }
        });

        //picker creation
        ColorPicker picker = new ColorPicker(new ColorPickerAdapter() {

            @Override
            public void canceled(Color oldColor) {
                if (fontObject!=null)fontObject.setColor(oldColor);
            }

            @Override
            public void changed(Color newColor) {
                if (fontObject!=null)fontObject.setColor(newColor);
            }

            @Override
            public void reset(Color previousColor, Color newColor) {
                if (fontObject!=null)    fontObject.setColor(newColor);
            }

            @Override
            public void finished (Color newColor) {
                if (fontObject!=null)fontObject.setColor(newColor);
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
TextButton showPickerButton=new TextButton("color",skin);
        showPickerButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                //displaying picker with fade in animation
                if (stage.getActors().contains(picker,true)){
                    picker.remove();
                }
                else{
                    stage.addActor(picker.fadeIn());
                }
            }
        });
        showPickerButton.setPosition(Statics.overlayViewport.getWorldWidth() - 200,10);
        stage.addActor(showPickerButton);
//        stage.addActor(colorPicker);


        List list=new List(skin);
        list.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(fontObject !=null) {
                    ((FontObject) fontObject).setFont(((List) actor).getSelectedIndex());
                    ((FontObject) fontObject).generate();
                }
            }
        });
        ScrollPane scrollPane=new ScrollPane(list,skin);
        Array<String> fontList=new Array<>();
        for (FileHandle fh: Statics.fontList){
            fontList.add(fh.nameWithoutExtension());
        }
        fontList.sort();
        list.setItems(fontList);
        list.layout();
        scrollPane.setSize(200,Statics.overlayViewport.getWorldHeight());
        scrollPane.setPosition(0,0);
        scrollPane.setFlickScroll(true);
        scrollPane.setScrollingDisabled(true,false);
scrollPane.layout();
        stage.addActor(exitButton);
        stage.addActor(scrollPane);
    }

    public void setFontObject(FontObject fontObject) {
        this.fontObject = fontObject;
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
        if (fontObject!=null){
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
        if (fontObject !=null)Statics.im.addProcessor(fontObject);
    }

    @Override
    public void removeInput() {
        Statics.im.removeProcessor(stage);
        if (fontObject !=null)Statics.im.removeProcessor(fontObject);
    }

    @Override
    public void act() {
        stage.act();
    }
}
