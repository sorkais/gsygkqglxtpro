package com.company.attendance.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {
  private final Stage stage;

  public SceneController(Stage stage) {
    this.stage = stage;
  }

  public void switchTo(String fxmlPath) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
      Parent root = loader.load();
      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
      stage.setScene(scene);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

