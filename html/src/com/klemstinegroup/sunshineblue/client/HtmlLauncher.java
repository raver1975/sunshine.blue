package com.klemstinegroup.sunshineblue.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.inject.OnCompletion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Panel;
import com.klemstinegroup.sunshineblue.SunshineBlue;
import com.klemstinegroup.sunshineblue.engine.Statics;
import com.klemstinegroup.sunshineblue.engine.overlays.ImageOverlay;

public class HtmlLauncher extends GwtApplication {
    private static HtmlLauncher instance;
    private String query;

    @Override
    protected void adjustMeterPanel(Panel meterPanel, Style meterStyle) {
        meterPanel.setStyleName("gdx-meter");
        meterPanel.addStyleName("nostripes");
        Style meterPanelStyle = meterPanel.getElement().getStyle();
        meterPanelStyle.setProperty("backgroundColor", "#ff0000");
        meterPanelStyle.setProperty("padding", "0px");
        meterStyle.setProperty("backgroundColor", "#ffffff");
        meterStyle.setProperty("backgroundImage", "none");
    }

    @Override
    public Preloader.PreloaderCallback getPreloaderCallback() {
//                final DockPanel preloaderPanel = new DockPanel();
//                preloaderPanel.setStyleName("gdx-preloader");
//
//                final Panel meterPanel = new SimplePanel();
//                final InlineHTML meter = new InlineHTML();
//
//                final Style meterStyle = meter.getElement().getStyle();
//                meterStyle.setWidth(100, Style.Unit.PCT);
//                adjustMeterPanel(meterPanel, meterStyle);
//                meterPanel.add(meter);
//                preloaderPanel.add(meterPanel,DockPanel.CENTER);
//                final Image logo = new Image(GWT.getHostPageBaseURL() + "ic_launcher-web.png");
//                logo.setStyleName("logo");
//                preloaderPanel.add(logo,DockPanel.SOUTH);
//                getRootPanel().add(preloaderPanel);
//                return new Preloader.PreloaderCallback() {
//
//                        @Override
//                        public void error (String file) {
//                                System.out.println("error: " + file);
//                        }
//
//                        @Override
//                        public void update (Preloader.PreloaderState state) {
//                                meterStyle.setWidth(100f * state.getProgress(), Style.Unit.PCT);
//
//                        }
//
//                };
        return createPreloaderPanel(GWT.getHostPageBaseURL() + "ic_launcher-web.png");
    }


    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);
        return config;
        // Resizable application, uses available space in browser
        // Fixed size application:
//                return new GwtApplicationConfiguration(480, 320);
    }

    @Override
    public ApplicationListener createApplicationListener() {

        if (!query.startsWith("Q")) {
            query = "current";
        }
        SunshineBlue sun = new SunshineBlue(new NativeGWT(), query);
        return sun;
    }

    native void getQuery()/*-{
        var query = $wnd.location.search.substring(1);
        console.log("query",query);
        var self = this;
        self.@com.klemstinegroup.sunshineblue.client.HtmlLauncher::queryReturn(Ljava/lang/String;)(query);
    }-*/;

    public void queryReturn(String query) {
        this.query = query;
    }

    @Override
    public void onModuleLoad() {
        FreetypeInjector.inject(new OnCompletion() {
            public void run() {
                // Replace HtmlLauncher with the class name
                // If your class is called FooBar.java than the line should be FooBar.super.onModuleLoad();
                HtmlLauncher.super.onModuleLoad();
            }
        });
        getQuery();
        setLoadingListener(new LoadingListener() {
            @Override
            public void beforeSetup() {

            }

            @Override
            public void afterSetup() {

                setupCopyListener();
            }
        });
    }

    native void setupCopyListener() /*-{
        var self = this;

        var isSafari = navigator.appVersion.search('Safari') != -1 && navigator.appVersion.search('Chrome') == -1 && navigator.appVersion.search('CrMo') == -1 && navigator.appVersion.search('CriOS') == -1;
        var isIe = (navigator.userAgent.toLowerCase().indexOf("msie") != -1 || navigator.userAgent.toLowerCase().indexOf("trident") != -1);

        var ieClipboardDiv = $doc.getElementById('#ie-clipboard-contenteditable');
        var hiddenInput = $doc.getElementById("hidden-input");
        var getTextToCopy = $entry(function(){
            return self.@com.klemstinegroup.sunshineblue.client.HtmlLauncher::copy()();
        });
        var pasteText = $entry(function(text){
            self.@com.klemstinegroup.sunshineblue.client.HtmlLauncher::paste(Ljava/lang/String;)(text);
        });

        var focusHiddenArea = function() {
            // In order to ensure that the browser will fire clipboard events, we always need to have something selected
            hiddenInput.value = '';
            hiddenInput.focus();
            hiddenInput.select();
        };

        // Focuses an element to be ready for copy/paste (used exclusively for IE)
        var focusIeClipboardDiv = function() {
            ieClipboardDiv.focus();
            var range = document.createRange();
            range.selectNodeContents((ieClipboardDiv.get(0)));
            var selection = window.getSelection();
            selection.removeAllRanges();
            selection.addRange(range);
        };

        // For IE, we can get/set Text or URL just as we normally would,
        // but to get HTML, we need to let the browser perform the copy or paste
        // in a contenteditable div.
        var ieClipboardEvent = function(clipboardEvent) {
            var clipboardData = window.clipboardData;
            if (clipboardEvent == 'cut' || clipboardEvent == 'copy') {
                clipboardData.setData('Text', getTextToCopy());
                focusIeClipboardDiv();
                setTimeout(function() {
                    focusHiddenArea();
                    ieClipboardDiv.empty();
                }, 0);
            }
            if (clipboardEvent == 'paste') {
                var clipboardText = clipboardData.getData('Text');
                ieClipboardDiv.empty();
                setTimeout(function() {
                    pasteText(clipboardText);
                    ieClipboardDiv.empty();
                    focusHiddenArea();
                }, 0);
            }
        };

        // For every broswer except IE, we can easily get and set data on the clipboard
        var standardClipboardEvent = function(clipboardEvent, event) {
            var clipboardData = event.clipboardData;
            if (clipboardEvent == 'cut' || clipboardEvent == 'copy') {
                clipboardData.setData('text/plain', getTextToCopy());
            }
            if (clipboardEvent == 'paste') {
                pasteText(clipboardData.getData('text/plain'));
            }
        };

        ['cut', 'copy', 'paste'].forEach(function (event) {
            $doc.addEventListener(event,function (e) {
                console.log(event);
                if(isIe) {
                    ieClipboardEvent(event);
                } else {
                    standardClipboardEvent(event, e);
                    focusHiddenArea();
                    e.preventDefault();
                }
            })
        })
    }-*/;

    private void paste(String text) {
        consoleLog("in paste" + text);
        getClipboard().setContents(text);
        Gdx.app.log("cliboard paste", text);
        if (SunshineBlue.instance.overlay instanceof ImageOverlay) {
            Actor focusedActor = ((ImageOverlay) SunshineBlue.instance.overlay).stage.getKeyboardFocus();
            if (focusedActor != null && focusedActor instanceof TextArea) {
                TextArea ta = ((TextArea) focusedActor);
                ta.setText(text);
            }
        }
    }

    private String copy() {
        return getClipboard().getContents();
    }


}