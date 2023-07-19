module com.centerio.eshopfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.rmi;

    opens com.centerio.eshopfx to javafx.fxml;
    opens com.centerio.eshopfx.shop.entities to javafx.fxml;
    exports com.centerio.eshopfx.shop.domain;
    exports com.centerio.eshopfx;
    exports com.centerio.eshopfx.shop.ui.gui.utils;
    exports com.centerio.eshopfx.shop.entities;
    opens com.centerio.eshopfx.shop.ui.gui.utils to javafx.fxml;
    exports com.centerio.eshopfx.shop.ui.gui.controller;
    opens com.centerio.eshopfx.shop.ui.gui.controller to javafx.fxml;
}