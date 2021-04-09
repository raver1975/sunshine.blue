package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshinelab.engine.Statics;


public class TransformOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final Group transformGroup;

    Vector2 touchdrag = new Vector2();
    Vector2 touchdown = new Vector2();
    private Vector2 touchdragcpy=new Vector2();

    public TransformOverlay() {
        stage = new Stage(Statics.overlayViewport);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Statics.fontList[MathUtils.random(Statics.fontList.length - 1)]);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
//        int a = MathUtils.randomBoolean() ? 0 : 1;
//        int b = MathUtils.randomBoolean() ? 0 : 1;
        parameter.color = Color.CYAN;
        textButtonStyle.font = generator.generateFont(parameter);
        textButtonStyle.overFontColor = Color.WHITE;
        Skin skin = new Skin(Gdx.files.internal("skins/comic/skin/comic-ui.json"));
//        Skin skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));

        CheckBox exitButton = new CheckBox("",skin);
        exitButton.setChecked(true);
        exitButton.setDisabled(true);
        exitButton.getStyle().fontColor = Color.RED;
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth()-40, Statics.overlayViewport.getWorldHeight()-40);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.backOverlay();
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

        Statics.selectedObjects.clear();
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                if (((Touchable) bo).isSelected(touchdown.cpy())) {
                    Statics.selectedObjects.add(bo);
                }

            }
        }


        for (BaseObject bo : Statics.selectedObjects) {
            if (bo instanceof ScreenObject) {
                ScreenObject so=((ScreenObject) bo);
                switch (Statics.transformButton) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        touchdragcpy.set(touchdown);

                        touchdragcpy.sub(so.position.x+so.center.x,so.position.y+so.center.y);
                        touchdragcpy.rotateDeg(-so.rotation);
                        touchdragcpy.scl(1f/so.scale);

                        System.out.println("oldcenter:"+((ScreenObject)bo).center);
                        System.out.println("newcenter:"+touchdragcpy);
                        Vector2 temp=touchdragcpy.add(so.center).cpy();
//                        ((ScreenObject)bo).center.set(touchdragcpy);
                        ((ScreenObject) bo).touchSpot.set(temp);
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
                ScreenObject so=((ScreenObject) bo);
                switch (Statics.transformButton) {
                    case 0:
                        so.position.add(touchdrag.cpy().sub(touchdown));
                        break;
                    case 1:
                        so.rotation += touchdrag.x - touchdown.x;
                        break;
                    case 2:
                        so.scale += (touchdrag.x - touchdown.x) / 200f;
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
}
