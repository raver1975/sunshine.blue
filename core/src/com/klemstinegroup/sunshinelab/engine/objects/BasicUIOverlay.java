package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.Statics;

public class BasicUIOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final Group transformGroup;

    public BasicUIOverlay() {
        Viewport viewport = new FitViewport(800f*Gdx.graphics.getWidth()/Gdx.graphics.getHeight()*Gdx.graphics.getDensity() , 800*Gdx.graphics.getDensity() );
        stage = new Stage(viewport);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)]);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
//        int a = MathUtils.randomBoolean() ? 0 : 1;
//        int b = MathUtils.randomBoolean() ? 0 : 1;
        parameter.color = Color.CYAN;
        textButtonStyle.font = generator.generateFont(parameter);
        textButtonStyle.overFontColor = Color.WHITE;
        Skin skin=new Skin(Gdx.files.internal("skins/comic/skin/comic-ui.json"));
//        Skin skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));
        Actor fontButton = new TextButton("Text", skin);
//        fontButton.setColor(Color.WHITE);
        TextArea ta=new TextArea("",skin);
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
                FontObject ff = new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 40);
                Statics.userObjects.add(ff);
                ff.position.set(-ff.center.x,-ff.center.y,0);
                ta.setVisible(false);
//        ((ScreenObject) Statics.objects.get(0)).position.set(-((ScreenObject) Statics.objects.get(0)).bounds.x/2, -((ScreenObject) Statics.objects.get(0)).bounds.y/2, 0);
            }
        });
        stage.addActor(fontButton);



        Actor imageButton = new TextButton("Image", skin);
        imageButton.setPosition(0,50);
//        fontButton.setColor(Color.WHITE);


        //                    pasteButton.setVisible(false);
        TextFieldListener tfl = new TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == 13) {
                    ta.setVisible(false);
//                    pasteButton.setVisible(false);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    Gdx.app.log("ta",ta.getText());
                    RectTextureObject bg = new RectTextureObject(ta.getText().replaceAll("\n", ""));
                    if (bg!=null){
                        Statics.userObjects.add(bg);
                    }
                    ta.setText("");
                }
            }
        };
        ta.addListener(new ActorGestureListener(){
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                ta.setText(Gdx.app.getClipboard().getContents());
                tfl.keyTyped(ta,(char)13);
                return true;// super.longPress(actor, x, y);
            }

        });
//        Actor pasteButton=new TextButton("paste",skin);
        ta.setTextFieldListener(tfl);

        ta.setPosition(imageButton.getX()+imageButton.getWidth()+10,imageButton.getY());
        ta.setWidth(Gdx.graphics.getWidth()/2);
        ta.setVisible(false);


        /*pasteButton.addListener(new ClickListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.input.setOnscreenKeyboardVisible(true);
//                Statics.objects.add(new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 40));
//            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ta.setText(Gdx.app.getClipboard().getContents());
                InputEvent ie2 = new InputEvent();
                ie2.setType(InputEvent.Type.keyDown);
                ie2.setKeyCode(13);
                ta.fire(ie2);
                InputEvent ie1 = new InputEvent();
                ie1.setType(InputEvent.Type.keyUp);
                ie1.setKeyCode(13);
                ta.fire(ie1);
//                event1.setType(InputEvent.Type.touchUp);
//                ta.fire(event1);
            }
        });*/
        imageButton.addListener(new ClickListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.input.setOnscreenKeyboardVisible(true);
//                Statics.objects.add(new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 40));
//            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                boolean vis=!ta.isVisible();
                Gdx.input.setOnscreenKeyboardVisible(vis);
                ta.setVisible(vis);
                if (vis){
                    stage.setKeyboardFocus(ta);
                }
//                pasteButton.setVisible(true);
            }
        });
//        pasteButton.setVisible(false);
//        pasteButton.setPosition(ta.getX()+ta.getWidth()+10,imageButton.getY());


        Actor drawButton = new TextButton("Draw", skin);
        drawButton.setPosition(0,100);
        drawButton.addListener(new ClickListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.input.setOnscreenKeyboardVisible(true);
//                Statics.objects.add(new FontObject(FontObject.fontList[MathUtils.random(FontObject.fontList.length - 1)], 40));
//            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.input.setOnscreenKeyboardVisible(false);
                ta.setVisible(false);
                Statics.userObjects.add(new DrawObject());
//                pasteButton.setVisible(true);
            }
        });
stage.addActor(drawButton);
        stage.addActor(ta);
//        stage.addActor(pasteButton);
        stage.addActor(imageButton);

        ButtonGroup transformButtons=new ButtonGroup();
                CheckBox moveButton = new CheckBox("Move",  skin);
        moveButton.getStyle().fontColor=Color.RED;
        moveButton.setName("move");
        moveButton.setPosition(500,0);
        CheckBox rotateButton = new CheckBox("Rotate", skin);
        rotateButton.setPosition(500,30);
        rotateButton.setName("rotate");
        CheckBox scaleButton = new CheckBox("Scale", skin);
        scaleButton.setPosition(500,60);
        scaleButton.setName("scale");

transformButtons.add(moveButton);
transformButtons.add(rotateButton);
transformButtons.add(scaleButton);
moveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
          Statics.transformButton=0;
            }
        });
        rotateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.transformButton=1;
            }
        });
        scaleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.transformButton=2;
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
        transformGroup.setVisible(false);
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

        if (Statics.selectedObjects.size>0){
            transformGroup.setVisible(true);
        }
        else{
            transformGroup.setVisible(false);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (Statics.selectedObjects.size>0){
            transformGroup.setVisible(true);
        }
        else{
            transformGroup.setVisible(false);
        }return false;
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
            Statics.shapedrawer.filledCircle(170+30*i , 20, 10);
        }
        stage.draw();
    }

    @Override
    public boolean isSelected(Vector3 touch) {
        return false;
    }
}
