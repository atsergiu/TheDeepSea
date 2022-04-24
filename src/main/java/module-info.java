module com.gui.guimap {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.apache.pdfbox;
    opens com.gui to javafx.fxml;
    exports com.gui;
    exports com.gui.controller;
    opens com.gui.controller to javafx.fxml;

    opens com.gui.domain to javafx.base;
}