module com.centerio.eshopfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.rmi;
    requires com.centerio.eshopfx.common;
    requires java.logging;

    exports com.centerio.eshopfx to javafx.graphics;
    opens com.centerio.eshopfx to javafx.fxml;

    exports com.centerio.eshopfx.gui.components;
    opens com.centerio.eshopfx.gui.components to javafx.fxml;
    exports com.centerio.eshopfx.gui.utils;
    opens com.centerio.eshopfx.gui.utils to javafx.fxml;
    exports com.centerio.eshopfx.gui.controller;
    opens com.centerio.eshopfx.gui.controller to javafx.fxml;
}