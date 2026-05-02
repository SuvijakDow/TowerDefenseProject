package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // สร้าง Label แสดงข้อความ
        Label label = new Label("Tower Defense Ready!");

        // ใส่ Label ลงใน Layout (StackPane)
        StackPane root = new StackPane(label);

        // สร้าง Scene (ฉาก) ขนาด 400x300
        Scene scene = new Scene(root, 400, 300);

        // ตั้งชื่อหน้าต่างและแสดงผล
        primaryStage.setTitle("My Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}