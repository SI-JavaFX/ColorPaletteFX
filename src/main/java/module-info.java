module com.si.colorpalettefx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

    opens com.si.colorpalettefx to javafx.fxml;
    opens com.si.colorpalettefx.model to com.fasterxml.jackson.databind;
    exports com.si.colorpalettefx;
    exports com.si.colorpalettefx.model;
}
