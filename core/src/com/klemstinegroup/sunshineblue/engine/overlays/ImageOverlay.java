package com.klemstinegroup.sunshineblue.engine.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.objects.BaseObject;
import com.klemstinegroup.sunshineblue.engine.objects.ImageObject;
import com.klemstinegroup.sunshineblue.engine.objects.ScreenObject;


public class ImageOverlay extends ScreenObject implements Overlay, Touchable, Drawable {

    public final Stage stage;
    private final TextArea ta;

    public ImageOverlay() {
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

        TextField.TextFieldListener tfl = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == 13) {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    Gdx.app.log("ta", ta.getText());
                    String text = ta.getText().replaceAll("\n", "");
                    if (!text.isEmpty()) {
                        if (text.startsWith("*")) {
                            text = text.substring(1);
                            int width = 256;
                            int height = 256;
                            String[] split = text.split(":");
                            if (split.length == 3) {
                                width = Integer.parseInt(split[0]);
                                height = Integer.parseInt(split[1]);
                                text = split[2];
                            }
                            Net.HttpRequest request = new Net.HttpRequest();
                            request.setHeader("apikey", "0000000000");
                            request.setHeader("Content-Type", "application/json");
                            request.setContent("{\"prompt\":\""
                                    + text
                                    + "\", \"params\":{\"n\":1, \"width\": " + width + ", \"height\": " + height + "}}");
                            request.setUrl("https://stablehorde.net/api/v2/generate/sync");
                            request.setTimeOut(0);
                            request.setMethod("POST");

                            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                                @Override
                                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                                    String result = httpResponse.getResultAsString();
                                    JsonReader reader = new JsonReader();
                                    JsonValue resultJSON = reader.parse(result);
                                    JsonValue generations = resultJSON.get("generations");
                                    String imgData = generations.get(0).getString("img");
                                    if (generations != null && imgData != null) {
                                        Gdx.app.log("stable diffusion response", imgData.replaceAll("(.{80})", "$1\n"));
                                        ImageObject.load("data:image/png;base64," + imgData);
                                    }

                                }

                                @Override
                                public void failed(Throwable t) {

                                }

                                @Override
                                public void cancelled() {

                                }
                            });

                        } else {
                            ImageObject.load(text);
                        }
                    }
                    ta.setText(null);
                    Overlay.backOverlay();
                }
            }
        };

        TextButton submitButton = new TextButton("Shine!", skin);
        submitButton.setSize(100, 100);
        submitButton.setPosition(SunshineBlue.instance.overlayViewport.getWorldWidth() - 100, SunshineBlue.instance.overlayViewport.getWorldHeight() - 130);
        submitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                tfl.keyTyped(ta, (char) 13);
            }
        });
        stage.addActor(submitButton);

        ta = new TextArea(null, skin, "default");


        Label tfield1 = new Label("JPG,PNG,GIF,IPFS,DATA url", skin);
        tfield1.setPosition(250, SunshineBlue.instance.overlayViewport.getWorldHeight() - 150);
        tfield1.setWidth(270);
        Label tfield2 = new Label("AI> *prompt or *width:height:prompt", skin);
        tfield2.setPosition(250, SunshineBlue.instance.overlayViewport.getWorldHeight() - 170);
        tfield2.setWidth(270);
        stage.addActor(tfield1);
        stage.addActor(tfield2);


        ta.addListener(new ActorGestureListener() {
            @Override
            public boolean longPress(Actor actor, float x, float y) {
                ta.setText(Gdx.app.getClipboard().getContents());

                return true;
            }

        });
        ta.setTextFieldListener(tfl);

        ta.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 130);
        ta.setWidth(SunshineBlue.instance.overlayViewport.getWorldWidth() - 110);
        ta.setHeight(100);

        TextButton clearButton = new TextButton("x", skin);
        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ta.setText("");
            }
        });
        clearButton.setPosition(10, SunshineBlue.instance.overlayViewport.getWorldHeight() - 130);
        clearButton.setSize(20, 20);

//ta.setZIndex(1);
//        ta.setMessageText("Accepts http urls for PNG GIF,and JPG. also base64 Data URI's and IPFS addresses");
        stage.addActor(ta);
        stage.addActor(clearButton);
        stage.setKeyboardFocus(ta);
    }

//    public void setTouchable(Touchable touchable) {
//        this.touchable = touchable;
//    }

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
    public void draw(Batch batch, float delta, boolean bounds) {
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
//        if (touchable != null) Statics.im.addProcessor(touchable);
        stage.setKeyboardFocus(ta);
    }

    @Override
    public void removeInput() {
        SunshineBlue.instance.im.removeProcessor(stage);
//        if (touchable != null) Statics.im.removeProcessor(touchable);
    }

    @Override
    public void setObject(BaseObject bo) {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
