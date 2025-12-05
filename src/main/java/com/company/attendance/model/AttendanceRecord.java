package com.company.attendance.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class AttendanceRecord {
  private final SimpleIntegerProperty id = new SimpleIntegerProperty();
  private final SimpleIntegerProperty employeeId = new SimpleIntegerProperty();
  private final SimpleStringProperty date = new SimpleStringProperty();
  private final SimpleStringProperty checkIn = new SimpleStringProperty();
  private final SimpleStringProperty checkOut = new SimpleStringProperty();
  private final SimpleStringProperty status = new SimpleStringProperty();
  private final SimpleStringProperty note = new SimpleStringProperty();

  public int getId() { return id.get(); }
  public void setId(int v) { id.set(v); }
  public SimpleIntegerProperty idProperty() { return id; }
  public int getEmployeeId() { return employeeId.get(); }
  public void setEmployeeId(int v) { employeeId.set(v); }
  public SimpleIntegerProperty employeeIdProperty() { return employeeId; }
  public String getDate() { return date.get(); }
  public void setDate(String v) { date.set(v); }
  public SimpleStringProperty dateProperty() { return date; }
  public String getCheckIn() { return checkIn.get(); }
  public void setCheckIn(String v) { checkIn.set(v); }
  public SimpleStringProperty checkInProperty() { return checkIn; }
  public String getCheckOut() { return checkOut.get(); }
  public void setCheckOut(String v) { checkOut.set(v); }
  public SimpleStringProperty checkOutProperty() { return checkOut; }
  public String getStatus() { return status.get(); }
  public void setStatus(String v) { status.set(v); }
  public SimpleStringProperty statusProperty() { return status; }
  public String getNote() { return note.get(); }
  public void setNote(String v) { note.set(v); }
  public SimpleStringProperty noteProperty() { return note; }
}

