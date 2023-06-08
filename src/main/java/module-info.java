module com.centerio.eshopfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.centerio.eshopfx to javafx.fxml;
    exports com.centerio.eshopfx;
    exports com.centerio.eshopfx.shop.entities;
    exports com.centerio.eshopfx.shop.ui.gui.utils;
    opens com.centerio.eshopfx.shop.ui.gui.utils to javafx.fxml;
    exports com.centerio.eshopfx.shop.ui.gui.controller;
    opens com.centerio.eshopfx.shop.ui.gui.controller to javafx.fxml;
}