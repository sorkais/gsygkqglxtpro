package com.company.attendance.controller;

import com.company.attendance.db.Database;
import com.company.attendance.service.AuthService;
import com.company.attendance.ui.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private Label messageLabel;

  private AuthService authService;

  @FXML
  public void initialize() {
    Database.init();
    authService = new AuthService();
  }

  @FXML
  public void onLogin(ActionEvent e) {
    String u = usernameField.getText();
    String p = passwordField.getText();
    var user = authService.login(u, p);
    if (user == null) {
      messageLabel.setText("用户名或密码错误");
      return;
    }
    Stage stage = (Stage) messageLabel.getScene().getWindow();
    SceneController controller = new SceneController(stage);
    if ("ADMIN".equalsIgnoreCase(user.getRole())) {
      controller.switchTo("/fxml/AdminDashboard.fxml");
    } else {
      controller.switchTo("/fxml/UserDashboard.fxml");
    }
  }

  @FXML
  public void goRegister(ActionEvent e) {
    Stage stage = (Stage) messageLabel.getScene().getWindow();
    new SceneController(stage).switchTo("/fxml/RegisterView.fxml");
  }
}

