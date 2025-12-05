package com.company.attendance.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Employee {
  private final SimpleIntegerProperty id = new SimpleIntegerProperty();
  private final SimpleStringProperty name = new SimpleStringProperty();
  private final SimpleStringProperty department = new SimpleStringProperty();
  private final SimpleStringProperty position = new SimpleStringProperty();
  private final SimpleStringProperty phone = new SimpleStringProperty();
  private final SimpleStringProperty hireDate = new SimpleStringProperty();
  private final SimpleStringProperty status = new SimpleStringProperty();

  public int getId() { return id.get(); }
  public void setId(int v) { id.set(v); }
  public SimpleIntegerProperty idProperty() { return id; }
  public String getName() { return name.get(); }
  public void setName(String v) { name.set(v); }
  public SimpleStringProperty nameProperty() { return name; }
  public String getDepartment() { return department.get(); }
  public void setDepartment(String v) { department.set(v); }
  public SimpleStringProperty departmentProperty() { return department; }
  public String getPosition() { return position.get(); }
  public void setPosition(String v) { position.set(v); }
  public SimpleStringProperty positionProperty() { return position; }
  public String getPhone() { return phone.get(); }
  public void setPhone(String v) { phone.set(v); }
  public SimpleStringProperty phoneProperty() { return phone; }
  public String getHireDate() { return hireDate.get(); }
  public void setHireDate(String v) { hireDate.set(v); }
  public SimpleStringProperty hireDateProperty() { return hireDate; }
  public String getStatus() { return status.get(); }
  public void setStatus(String v) { status.set(v); }
  public SimpleStringProperty statusProperty() { return status; }
}

