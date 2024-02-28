module com.example.laborator7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    //requires org.mindrot.jbcrypt;

    requires java.sql;
    requires java.desktop;
    //requires jbcrypt;
    //requires jbcrypt;

    opens com.example.laborator7 to javafx.fxml;
    exports com.example.laborator7;
    opens com.example.laborator7.domain to javafx.base;
    exports com.example.laborator7.service.serviceForPerson;

}