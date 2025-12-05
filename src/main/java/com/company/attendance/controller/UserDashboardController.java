package com.company.attendance.controller;

import com.company.attendance.dao.AttendanceDao;
import com.company.attendance.model.AttendanceRecord;
import com.company.attendance.service.Session;
import com.company.attendance.ui.SceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class UserDashboardController {
  @FXML private Label todayLabel;
  @FXML private TableView<AttendanceRecord> myAttendanceTable;
  @FXML private TableColumn<AttendanceRecord, String> colDate;
  @FXML private TableColumn<AttendanceRecord, String> colIn;
  @FXML private TableColumn<AttendanceRecord, String> colOut;
  @FXML private TableColumn<AttendanceRecord, String> colStatus;
  @FXML private TableColumn<AttendanceRecord, String> colNote;

  private final AttendanceDao attendanceDao = new AttendanceDao();
  private Integer selfEmpId;
  @FXML private TextField profileName;
  @FXML private TextField profileDept;
  @FXML private TextField profilePos;
  @FXML private TextField profilePhone;
  @FXML private DatePicker profileHireDate;
  @FXML private TextField profileStatus;

  @FXML
  public void initialize() {
    colDate.setCellValueFactory(c -> c.getValue().dateProperty());
    colIn.setCellValueFactory(c -> c.getValue().checkInProperty());
    colOut.setCellValueFactory(c -> c.getValue().checkOutProperty());
    colStatus.setCellValueFactory(c -> c.getValue().statusProperty());
    colNote.setCellValueFactory(c -> c.getValue().noteProperty());
    selfEmpId = resolveEmployeeId();
    if (todayLabel != null) todayLabel.setText("今天：" + java.time.LocalDate.now().toString());
    refreshTable();
    loadProfile();
  }

  private int resolveEmployeeId() {
    var u = Session.getCurrentUser();
    if (u == null) return 0;
    com.company.attendance.dao.EmployeeDao ed = new com.company.attendance.dao.EmployeeDao();
    var emp = ed.findByUserId(u.getId());
    return emp != null ? emp.getId() : 0;
  }

  @FXML
  public void checkInNow(ActionEvent e) {
    var date = java.time.LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    var time = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    var rec = attendanceDao.findByEmployeeAndDate(selfEmpId, date);
    if (rec == null) {
      AttendanceRecord r = new AttendanceRecord();
      r.setEmployeeId(selfEmpId);
      r.setDate(date);
      r.setCheckIn(time);
      r.setCheckOut("");
      r.setStatus(computeStatus(time, null));
      r.setNote("");
      attendanceDao.insert(r);
    } else {
      if (rec.getCheckIn() == null || rec.getCheckIn().isBlank()) {
        rec.setCheckIn(time);
        rec.setStatus(mergeStatus(rec.getStatus(), computeStatus(time, null)));
        attendanceDao.update(rec);
      }
    }
    refreshTable();
  }

  @FXML
  public void checkOutNow(ActionEvent e) {
    var date = java.time.LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    var time = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    var rec = attendanceDao.findByEmployeeAndDate(selfEmpId, date);
    if (rec == null) {
      AttendanceRecord r = new AttendanceRecord();
      r.setEmployeeId(selfEmpId);
      r.setDate(date);
      r.setCheckIn("");
      r.setCheckOut(time);
      r.setStatus(computeStatus(null, time));
      r.setNote("");
      attendanceDao.insert(r);
    } else {
      rec.setCheckOut(time);
      rec.setStatus(mergeStatus(rec.getStatus(), computeStatus(null, time)));
      attendanceDao.update(rec);
    }
    refreshTable();
  }

  private String computeStatus(String in, String out) {
    java.time.LocalTime start = java.time.LocalTime.of(9, 0);
    java.time.LocalTime end = java.time.LocalTime.of(18, 0);
    java.util.List<String> flags = new java.util.ArrayList<>();
    if (in != null && !in.isBlank()) {
      try { if (java.time.LocalTime.parse(in).isAfter(start)) flags.add("迟到"); } catch (Exception ignored) {}
    }
    if (out != null && !out.isBlank()) {
      try { if (java.time.LocalTime.parse(out).isBefore(end)) flags.add("早退"); } catch (Exception ignored) {}
    }
    if (flags.isEmpty()) return "正常";
    return String.join("/", flags);
  }

  private String mergeStatus(String existing, String added) {
    if (existing == null || existing.isBlank()) return added;
    if (added == null || added.isBlank() || "正常".equals(added)) return existing;
    if (existing.contains(added)) return existing;
    if ("正常".equals(existing)) return added;
    return existing + "/" + added;
  }

  private void refreshTable() {
    myAttendanceTable.setItems(attendanceDao.listByEmployee(selfEmpId));
  }

  private void loadProfile() {
    var u = Session.getCurrentUser();
    if (u == null) return;
    com.company.attendance.dao.EmployeeDao ed = new com.company.attendance.dao.EmployeeDao();
    var emp = ed.findByUserId(u.getId());
    if (emp != null) {
      profileName.setText(emp.getName());
      profileDept.setText(emp.getDepartment());
      profilePos.setText(emp.getPosition());
      profilePhone.setText(emp.getPhone());
      profileStatus.setText(emp.getStatus());
      try {
        if (emp.getHireDate() != null && !emp.getHireDate().isBlank()) {
          profileHireDate.setValue(java.time.LocalDate.parse(emp.getHireDate()));
        }
      } catch (Exception ignored) {}
    }
  }

  @FXML
  public void saveProfile(ActionEvent e) {
    var u = Session.getCurrentUser();
    if (u == null) return;
    com.company.attendance.dao.EmployeeDao ed = new com.company.attendance.dao.EmployeeDao();
    com.company.attendance.model.Employee emp = new com.company.attendance.model.Employee();
    emp.setName(profileName.getText());
    emp.setDepartment(profileDept.getText());
    emp.setPosition(profilePos.getText());
    emp.setPhone(profilePhone.getText());
    emp.setStatus(profileStatus.getText());
    var d = profileHireDate.getValue();
    emp.setHireDate(d == null ? "" : d.toString());
    ed.upsertByUserId(emp, u.getId());
    selfEmpId = resolveEmployeeId();
    refreshTable();
  }

  @FXML
  public void logout(ActionEvent e) {
    Session.clear();
    Stage stage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();
    new SceneController(stage).switchTo("/fxml/LoginView.fxml");
  }
}

