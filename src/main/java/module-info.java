module com.centerio.eshopfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.centerio.eshopfx to javafx.fxml;
    exports com.centerio.eshopfx;
    exports com.centerio.eshopfx.shop.ui.gui.utils;
    opens com.centerio.eshopfx.shop.ui.gui.utils to javafx.fxml;
    exports com.centerio.eshopfx.shop.ui.gui.view;
    opens com.centerio.eshopfx.shop.ui.gui.view to javafx.fxml;
}