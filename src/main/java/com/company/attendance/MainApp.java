package com.company.attendance;

import com.company.attendance.ui.SceneController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
  @Override
  public void start(Stage stage) {
    SceneController controller = new SceneController(stage);
    controller.switchTo("/fxml/LoginView.fxml");
    stage.setTitle("公司员工考勤管理系统");
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}

