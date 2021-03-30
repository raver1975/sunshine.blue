package com.klemstinegroup.sunshinelab.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.backends.gwt.preloader.Preloader;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.FreetypeInjector;
import com.badlogic.gdx.graphics.g2d.freetype.gwt.inject.OnCompletion;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import com.klemstinegroup.sunshinelab.SunshineLab;

public class HtmlLauncher extends GwtApplication {
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
        public Preloader.PreloaderCallback getPreloaderCallback () {
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
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
                return new GwtApplicationConfiguration(true);
                // Fixed size application:
//                return new GwtApplicationConfiguration(480, 320);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new SunshineLab();
        }

        @Override
        public void onModuleLoad () {
                FreetypeInjector.inject(new OnCompletion() {
                        public void run () {
                                // Replace HtmlLauncher with the class name
                                // If your class is called FooBar.java than the line should be FooBar.super.onModuleLoad();
                                HtmlLauncher.super.onModuleLoad();
                        }
                });
        }
}