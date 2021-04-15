package com.klemstinegroup.sunshinelab.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.klemstinegroup.sunshinelab.SunshineLab;
import com.klemstinegroup.sunshinelab.engine.Statics;
import com.klemstinegroup.sunshinelab.engine.objects.ImageObject;
import com.klemstinegroup.sunshinelab.engine.objects.ScreenObject;
import com.klemstinegroup.sunshinelab.engine.util.IPFSFileListener;
import sun.security.provider.Sun;

import java.awt.event.KeyEvent;

public class ImageOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final TextArea ta;
    Touchable touchable;

    public ImageOverlay() {
        stage = new Stage(Statics.overlayViewport);
        Skin skin = new Skin(Gdx.files.internal("skins/orange/skin/uiskin.json"));

        TextButton exitButton = new TextButton("X", skin);
        exitButton.setPosition(Statics.overlayViewport.getWorldWidth() - 55, Statics.overlayViewport.getWorldHeight() - 55);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Statics.backOverlay();
            }
        });
        stage.addActor(exitButton);

        ta = new TextArea("", skin);
        TextField.TextFieldListener tfl = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == 13|| c== KeyEvent.VK_PASTE) {
//                    pasteButton.setVisible(false);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    Gdx.app.log("ta", ta.getText());
                    String text = ta.getText().replaceAll("\n", "");
                    if (text.startsWith("data:")) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                ImageObject bg = new ImageObject(text);
                                if (bg != null) {
                                    Statics.userObjects.add(bg);
                                }
                                ta.setText("");
                                Statics.backOverlay();
                            }
                        });

                    } else if (text.startsWith("Q")) {
                        SunshineLab.nativeNet.downloadIPFS(text, new IPFSFileListener() {
                            @Override
                            public void downloaded(byte[] file) {
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        ImageObject bg = new ImageObject(file);
                                        if (bg != null) {
                                            Statics.userObjects.add(bg);
                                        }
                                        ta.setText("");
                                        Statics.backOverlay();

                                    }
                                });

                            }

                            @Override
                            public void downloadFailed(Throwable t) {

                            }
                        });
                    } else {
                        SunshineLab.nativeNet.downloadFile(text, new IPFSFileListener() {
                            @Override
                            public void downloaded(byte[] file) {
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        ImageObject bg = new ImageObject(file);
                                        if (bg != null) {
                                            Statics.userObjects.add(bg);
                                        }
                                        ta.setText("");
                                        Statics.backOverlay();
                                    }
                                });

                            }

                            @Override
                            public void downloadFailed(Throwable t) {

                            }
                        });
                    }
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

        ta.setPosition(10, 10);
        ta.setWidth(Statics.overlayViewport.getWorldWidth() - 20);
        stage.addActor(ta);
        stage.setKeyboardFocus(ta);

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
    }

    @Override
    public boolean isSelected(Vector2 touch) {
        return false;
    }

    @Override
    public void setInput() {
        Statics.im.addProcessor(stage);
        if (touchable != null) Statics.im.addProcessor(touchable);
        stage.setKeyboardFocus(ta);
    }

    @Override
    public void removeInput() {
        Statics.im.removeProcessor(stage);
        if (touchable != null) Statics.im.removeProcessor(touchable);
    }

    @Override
    public void act() {
        stage.act();
    }
}
