package com.company.attendance.controller;

import com.company.attendance.service.AuthService;
import com.company.attendance.ui.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
  @FXML private TextField usernameField;
  @FXML private PasswordField passwordField;
  @FXML private ChoiceBox<String> roleChoice;
  @FXML private Label messageLabel;

  private final AuthService authService = new AuthService();

  @FXML
  public void initialize() {
    roleChoice.setItems(FXCollections.observableArrayList("普通用户", "管理员"));
    roleChoice.getSelectionModel().selectFirst();
  }

  @FXML
  public void onRegister(ActionEvent e) {
    String role = roleChoice.getValue();
    String mapped = "管理员".equals(role) ? "ADMIN" : "USER";
    boolean ok = authService.register(usernameField.getText(), passwordField.getText(), mapped);
    messageLabel.setText(ok ? "注册成功" : "注册失败：用户名已存在");
  }

  @FXML
  public void goLogin(ActionEvent e) {
    Stage stage = (Stage) messageLabel.getScene().getWindow();
    new SceneController(stage).switchTo("/fxml/LoginView.fxml");
  }
}

