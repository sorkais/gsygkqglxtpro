package com.company.attendance.controller;

import com.company.attendance.dao.AttendanceDao;
import com.company.attendance.dao.EmployeeDao;
import com.company.attendance.model.AttendanceRecord;
import com.company.attendance.model.Employee;
import com.company.attendance.service.Session;
import com.company.attendance.ui.SceneController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class AdminDashboardController {
  @FXML private TableView<Employee> employeeTable;
  @FXML private TableColumn<Employee, Number> colId;
  @FXML private TableColumn<Employee, String> colName;
  @FXML private TableColumn<Employee, String> colDept;
  @FXML private TableColumn<Employee, String> colPos;
  @FXML private TableColumn<Employee, String> colPhone;

  @FXML private TextField empName;
  @FXML private TextField empDept;
  @FXML private TextField empPos;
  @FXML private TextField empPhone;

  @FXML private TableView<AttendanceRecord> attendanceTable;
  @FXML private TableColumn<AttendanceRecord, Number> attColId;
  @FXML private TableColumn<AttendanceRecord, Number> attColEmp;
  @FXML private TableColumn<AttendanceRecord, String> attColDate;
  @FXML private TableColumn<AttendanceRecord, String> attColIn;
  @FXML private TableColumn<AttendanceRecord, String> attColOut;
  @FXML private TableColumn<AttendanceRecord, String> attColStatus;
  @FXML private TableColumn<AttendanceRecord, String> attColNote;

  @FXML private TextField attEmpId;
  @FXML private DatePicker attDate;
  @FXML private TextField attCheckIn;
  @FXML private TextField attCheckOut;
  @FXML private TextField attStatus;

  private final EmployeeDao employeeDao = new EmployeeDao();
  private final AttendanceDao attendanceDao = new AttendanceDao();
  private Integer editingEmployeeId;
  private Integer editingAttendanceId;
  @FXML private javafx.scene.control.TableView<com.company.attendance.model.User> userTable;
  @FXML private javafx.scene.control.TableColumn<com.company.attendance.model.User, Number> userColId;
  @FXML private javafx.scene.control.TableColumn<com.company.attendance.model.User, String> userColUsername;
  @FXML private javafx.scene.control.TableColumn<com.company.attendance.model.User, String> userColRole;
  @FXML private javafx.scene.control.TableColumn<com.company.attendance.model.User, String> userColCreated;

  @FXML
  public void initialize() {
    colId.setCellValueFactory(c -> c.getValue().idProperty());
    colName.setCellValueFactory(c -> c.getValue().nameProperty());
    colDept.setCellValueFactory(c -> c.getValue().departmentProperty());
    colPos.setCellValueFactory(c -> c.getValue().positionProperty());
    colPhone.setCellValueFactory(c -> c.getValue().phoneProperty());
    refreshEmployees(null);

    attColId.setCellValueFactory(c -> c.getValue().idProperty());
    attColEmp.setCellValueFactory(c -> c.getValue().employeeIdProperty());
    attColDate.setCellValueFactory(c -> c.getValue().dateProperty());
    attColIn.setCellValueFactory(c -> c.getValue().checkInProperty());
    attColOut.setCellValueFactory(c -> c.getValue().checkOutProperty());
    attColStatus.setCellValueFactory(c -> c.getValue().statusProperty());
    attColNote.setCellValueFactory(c -> c.getValue().noteProperty());
    refreshAttendance(null);
    if (userTable != null) {
      userColId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()));
      userColUsername.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUsername()));
      userColRole.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRole()));
      userColCreated.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCreatedAt()));
      refreshUsers(null);
    }
  }

  @FXML
  public void saveEmployee(ActionEvent e) {
    Employee emp = new Employee();
    emp.setName(empName.getText());
    emp.setDepartment(empDept.getText());
    emp.setPosition(empPos.getText());
    emp.setPhone(empPhone.getText());
    emp.setHireDate("");
    emp.setStatus("在职");
    if (editingEmployeeId != null) {
      emp.setId(editingEmployeeId);
      employeeDao.update(emp);
      editingEmployeeId = null;
    } else {
      employeeDao.insert(emp);
    }
    empName.clear(); empDept.clear(); empPos.clear(); empPhone.clear();
    refreshEmployees(null);
  }

  @FXML
  public void editSelected(ActionEvent e) {
    Employee sel = employeeTable.getSelectionModel().getSelectedItem();
    if (sel == null) return;
    editingEmployeeId = sel.getId();
    empName.setText(sel.getName());
    empDept.setText(sel.getDepartment());
    empPos.setText(sel.getPosition());
    empPhone.setText(sel.getPhone());
  }

  @FXML
  public void deleteSelected(ActionEvent e) {
    Employee sel = employeeTable.getSelectionModel().getSelectedItem();
    if (sel == null) return;
    employeeDao.delete(sel.getId());
    refreshEmployees(null);
  }

  @FXML
  public void refreshEmployees(ActionEvent e) {
    ObservableList<Employee> list = employeeDao.listAll();
    employeeTable.setItems(list);
  }

  @FXML
  public void saveAttendance(ActionEvent e) {
    try {
      AttendanceRecord r = new AttendanceRecord();
      if (attEmpId.getText() == null || attEmpId.getText().isBlank()) {
        Alert a = new Alert(Alert.AlertType.ERROR, "请填写员工编号", ButtonType.OK);
        a.showAndWait();
        return;
      }
      int empId;
      try { empId = Integer.parseInt(attEmpId.getText()); } catch (Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR, "员工编号必须为数字", ButtonType.OK);
        a.showAndWait();
        return;
      }
      r.setEmployeeId(empId);
      var date = attDate.getValue() == null ? java.time.LocalDate.now() : attDate.getValue();
      r.setDate(date.format(DateTimeFormatter.ISO_DATE));
      String in = attCheckIn.getText() == null ? "" : attCheckIn.getText().trim();
      String out = attCheckOut.getText() == null ? "" : attCheckOut.getText().trim();
      if (!in.isEmpty() && !isValidTime(in)) {
        Alert a = new Alert(Alert.AlertType.ERROR, "上班时间格式错误，应为HH:mm", ButtonType.OK);
        a.showAndWait();
        return;
      }
      if (!out.isEmpty() && !isValidTime(out)) {
        Alert a = new Alert(Alert.AlertType.ERROR, "下班时间格式错误，应为HH:mm", ButtonType.OK);
        a.showAndWait();
        return;
      }
      r.setCheckIn(in);
      r.setCheckOut(out);
      r.setStatus(attStatus.getText());
      r.setNote("");
      Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确认保存这条考勤记录？", ButtonType.OK, ButtonType.CANCEL);
      var res = confirm.showAndWait();
      if (res.isEmpty() || res.get() != ButtonType.OK) return;
      if (editingAttendanceId != null) {
        r.setId(editingAttendanceId);
        attendanceDao.update(r);
        editingAttendanceId = null;
      } else {
        attendanceDao.insert(r);
      }
      attEmpId.clear(); attCheckIn.clear(); attCheckOut.clear(); attStatus.clear();
      refreshAttendance(null);
    } catch (Exception ex) {
    }
  }

  @FXML
  public void deleteAttendanceSelected(ActionEvent e) {
    AttendanceRecord sel = attendanceTable.getSelectionModel().getSelectedItem();
    if (sel == null) return;
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "确认删除选中的考勤记录？", ButtonType.OK, ButtonType.CANCEL);
    var res = confirm.showAndWait();
    if (res.isEmpty() || res.get() != ButtonType.OK) return;
    attendanceDao.delete(sel.getId());
    refreshAttendance(null);
  }

  @FXML
  public void refreshAttendance(ActionEvent e) {
    attendanceTable.setItems(attendanceDao.listAll());
  }

  @FXML
  public void editAttendanceSelected(ActionEvent e) {
    AttendanceRecord sel = attendanceTable.getSelectionModel().getSelectedItem();
    if (sel == null) return;
    editingAttendanceId = sel.getId();
    attEmpId.setText(Integer.toString(sel.getEmployeeId()));
    try { attDate.setValue(java.time.LocalDate.parse(sel.getDate())); } catch (Exception ignored) {}
    attCheckIn.setText(sel.getCheckIn() == null ? "" : sel.getCheckIn());
    attCheckOut.setText(sel.getCheckOut() == null ? "" : sel.getCheckOut());
    attStatus.setText(sel.getStatus() == null ? "" : sel.getStatus());
  }

  private boolean isValidTime(String t) {
    try {
      java.time.LocalTime.parse(t, java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @FXML
  public void logout(ActionEvent e) {
    Session.clear();
    Stage stage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
    new SceneController(stage).switchTo("/fxml/LoginView.fxml");
  }

  @FXML
  public void refreshUsers(ActionEvent e) {
    com.company.attendance.dao.UserDao ud = new com.company.attendance.dao.UserDao();
    var list = javafx.collections.FXCollections.observableArrayList(ud.listAll());
    userTable.setItems(list);
  }

  @FXML
  public void makeAdmin(ActionEvent e) {
    var u = userTable.getSelectionModel().getSelectedItem();
    if (u == null) return;
    com.company.attendance.dao.UserDao ud = new com.company.attendance.dao.UserDao();
    ud.updateRole(u.getId(), "ADMIN");
    refreshUsers(null);
  }

  @FXML
  public void makeUser(ActionEvent e) {
    var u = userTable.getSelectionModel().getSelectedItem();
    if (u == null) return;
    com.company.attendance.dao.UserDao ud = new com.company.attendance.dao.UserDao();
    ud.updateRole(u.getId(), "USER");
    refreshUsers(null);
  }

  @FXML
  public void resetPassword(ActionEvent e) {
    var u = userTable.getSelectionModel().getSelectedItem();
    if (u == null) return;
    com.company.attendance.dao.UserDao ud = new com.company.attendance.dao.UserDao();
    String hash = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, "123456".toCharArray());
    ud.resetPassword(u.getId(), hash);
  }

  @FXML
  public void deleteUser(ActionEvent e) {
    var u = userTable.getSelectionModel().getSelectedItem();
    if (u == null) return;
    com.company.attendance.dao.UserDao ud = new com.company.attendance.dao.UserDao();
    ud.deleteUser(u.getId());
    refreshUsers(null);
  }
}

