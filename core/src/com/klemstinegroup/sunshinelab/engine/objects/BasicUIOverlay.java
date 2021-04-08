package com.klemstinegroup.sunshinelab.engine.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.klemstinegroup.sunshinelab.engine.Statics;

import static com.klemstinegroup.sunshinelab.engine.Statics.viewport;

public class BasicUIOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    Vector2 touchdown = new Vector2();

    public BasicUIOverlay() {
        Viewport viewport = new FitViewport(800f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight() * Gdx.graphics.getDensity(), 800 * Gdx.graphics.getDensity());
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
        Skin skin = new Skin(Gdx.files.internal("skins/comic/skin/comic-ui.json"));
//        Skin skin = new Skin(Gdx.files.internal("skins/default/skin/uiskin.json"));
        Actor fontButton = new TextButton("Text", skin);
//        fontButton.setColor(Color.WHITE);
        TextArea ta = new TextArea("", skin);
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
                Statics.FONT_OVERLAY.setTouchable(ff);
                Statics.setOverlay(Statics.FONT_OVERLAY);
                ff.position.set(-ff.center.x, -ff.center.y);
                ta.setVisible(false);
//        ((ScreenObject) Statics.objects.get(0)).position.set(-((ScreenObject) Statics.objects.get(0)).bounds.x/2, -((ScreenObject) Statics.objects.get(0)).bounds.y/2, 0);
            }
        });
        stage.addActor(fontButton);


        Actor imageButton = new TextButton("Image", skin);
        imageButton.setPosition(0, 50);
//        fontButton.setColor(Color.WHITE);


        //                    pasteButton.setVisible(false);
        TextFieldListener tfl = new TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == 13) {
                    ta.setVisible(false);
                    ta.setVisible(false);
//                    pasteButton.setVisible(false);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    Gdx.app.log("ta", ta.getText());
                    RectTextureObject bg = new RectTextureObject(ta.getText().replaceAll("\n", ""));
                    if (bg != null) {
                        Statics.userObjects.add(bg);
                    }
                    ta.setText("");
                }
            }
        };
        ta.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                ta.setText(Gdx.app.getClipboard().getContents());
                tfl.keyTyped(ta, (char) 13);
                return true;// super.longPress(actor, x, y);
            }

        });
//        Actor pasteButton=new TextButton("paste",skin);
        ta.setTextFieldListener(tfl);

        ta.setPosition(imageButton.getX() + imageButton.getWidth() + 10, imageButton.getY());
        ta.setWidth(Gdx.graphics.getWidth() / 2);
        ta.setVisible(false);


        imageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                boolean vis = !ta.isVisible();
                Gdx.input.setOnscreenKeyboardVisible(vis);
                ta.setVisible(vis);
                if (vis) {
                    stage.setKeyboardFocus(ta);
                }
            }
        });

        Actor drawButton = new TextButton("Draw", skin);
        drawButton.setPosition(0, 100);
        drawButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.input.setOnscreenKeyboardVisible(false);
                ta.setVisible(false);
                DrawObject doi = new DrawObject();
                Statics.userObjects.add(doi);
                Statics.DRAW_OVERLAY.setTouchable(doi);
                Statics.setOverlay(Statics.DRAW_OVERLAY);
//                pasteButton.setVisible(true);
            }
        });
        stage.addActor(drawButton);
        stage.addActor(ta);
        stage.addActor(imageButton);
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
        viewport.unproject(touchdown.set(screenX, screenY));
        Statics.selectedObjects.clear();
        for (BaseObject bo : Statics.userObjects) {
            if (bo instanceof Touchable) {
                if (((Touchable) bo).isSelected(touchdown.cpy())) {
                    Statics.selectedObjects.add(bo);
                }

            }
        }
        if (Statics.selectedObjects.size > 0) {
            Statics.setOverlay(Statics.TRANSFORM_OVERLAY);
        } else {
            Statics.setOverlay(Statics.BASIC_UI_OVERLAY);
        }
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
    public void setInput() {
        Statics.im.addProcessor(stage);
    }

    @Override
    public boolean isSelected(Vector2 touch) {
        return false;
    }

    @Override
    public void removeInput() {
        Statics.im.removeProcessor(stage);
    }
}
