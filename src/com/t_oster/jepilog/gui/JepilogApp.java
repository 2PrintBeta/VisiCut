/*
 * JepilogApp.java
 */
package com.t_oster.jepilog.gui;

import com.t_oster.jepilog.controller.JepilogController;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class JepilogApp extends SingleFrameApplication {

    private JepilogController controller;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        controller = new JepilogController();
        show(new JepilogView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JepilogApp
     */
    public static JepilogApp getApplication() {
        return Application.getInstance(JepilogApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        //Mac Specific
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Jepilog");
        System.setProperty("apple.awt.brushMetalLook", "true");
        System.setProperty("apple.awt.antialiasing", "on");
        System.setProperty("apple.awt.textantialiasing", "on");
        System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
        System.setProperty("com.apple.mrj.application.live-resize", "true");
        System.setProperty("com.apple.macos.smallTabs", "true");
        launch(JepilogApp.class, args);
    }

    public JepilogController getController() {
        return controller;
    }
}
