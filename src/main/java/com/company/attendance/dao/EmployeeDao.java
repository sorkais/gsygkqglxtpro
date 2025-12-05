package com.company.attendance.dao;

import com.company.attendance.db.Database;
import com.company.attendance.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class EmployeeDao {
  public ObservableList<Employee> listAll() {
    ObservableList<Employee> list = FXCollections.observableArrayList();
    try {
      Connection c = Database.get();
      try (Statement st = c.createStatement()) {
        ResultSet rs = st.executeQuery("SELECT * FROM employees ORDER BY id DESC");
        while (rs.next()) {
          Employee e = new Employee();
          e.setId(rs.getInt("id"));
          e.setName(rs.getString("name"));
          e.setDepartment(rs.getString("department"));
          e.setPosition(rs.getString("position"));
          e.setPhone(rs.getString("phone"));
          e.setHireDate(rs.getString("hire_date"));
          e.setStatus(rs.getString("status"));
          list.add(e);
        }
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return list;
  }

  public boolean insert(Employee e) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("INSERT INTO employees(name,department,position,phone,hire_date,status) VALUES(?,?,?,?,?,?)")) {
        ps.setString(1, e.getName());
        ps.setString(2, e.getDepartment());
        ps.setString(3, e.getPosition());
        ps.setString(4, e.getPhone());
        ps.setString(5, e.getHireDate());
        ps.setString(6, e.getStatus());
        return ps.executeUpdate() > 0;
      }
    } catch (Exception ex) {
      return false;
    }
  }

  public Integer insertReturnId(Employee e) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("INSERT INTO employees(name,department,position,phone,hire_date,status) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, e.getName());
        ps.setString(2, e.getDepartment());
        ps.setString(3, e.getPosition());
        ps.setString(4, e.getPhone());
        ps.setString(5, e.getHireDate());
        ps.setString(6, e.getStatus());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);
        return null;
      }
    } catch (Exception ex) {
      return null;
    }
  }

  public Employee findByName(String name) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("SELECT * FROM employees WHERE name=?")) {
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
          Employee e = new Employee();
          e.setId(rs.getInt("id"));
          e.setName(rs.getString("name"));
          e.setDepartment(rs.getString("department"));
          e.setPosition(rs.getString("position"));
          e.setPhone(rs.getString("phone"));
          e.setHireDate(rs.getString("hire_date"));
          e.setStatus(rs.getString("status"));
          return e;
        }
        return null;
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public Employee findByUserId(int userId) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("SELECT * FROM employees WHERE user_id=?")) {
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
          Employee e = new Employee();
          e.setId(rs.getInt("id"));
          e.setName(rs.getString("name"));
          e.setDepartment(rs.getString("department"));
          e.setPosition(rs.getString("position"));
          e.setPhone(rs.getString("phone"));
          e.setHireDate(rs.getString("hire_date"));
          e.setStatus(rs.getString("status"));
          return e;
        }
        return null;
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public boolean insertWithUserId(Employee e, int userId) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("INSERT INTO employees(name,department,position,phone,hire_date,status,user_id) VALUES(?,?,?,?,?,?,?)")) {
        ps.setString(1, e.getName());
        ps.setString(2, e.getDepartment());
        ps.setString(3, e.getPosition());
        ps.setString(4, e.getPhone());
        ps.setString(5, e.getHireDate());
        ps.setString(6, e.getStatus());
        ps.setInt(7, userId);
        return ps.executeUpdate() > 0;
      }
    } catch (Exception ex) {
      return false;
    }
  }

  public boolean upsertByUserId(Employee e, int userId) {
    Employee existing = findByUserId(userId);
    if (existing == null) return insertWithUserId(e, userId);
    e.setId(existing.getId());
    return update(e);
  }

  public boolean linkEmployeeToUserById(int empId, int userId) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("UPDATE employees SET user_id=? WHERE id=?")) {
        ps.setInt(1, userId);
        ps.setInt(2, empId);
        return ps.executeUpdate() > 0;
      }
    } catch (Exception ex) {
      return false;
    }
  }

  public boolean update(Employee e) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("UPDATE employees SET name=?,department=?,position=?,phone=?,hire_date=?,status=? WHERE id=?")) {
        ps.setString(1, e.getName());
        ps.setString(2, e.getDepartment());
        ps.setString(3, e.getPosition());
        ps.setString(4, e.getPhone());
        ps.setString(5, e.getHireDate());
        ps.setString(6, e.getStatus());
        ps.setInt(7, e.getId());
        return ps.executeUpdate() > 0;
      }
    } catch (Exception ex) {
      return false;
    }
  }

  public boolean delete(int id) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("DELETE FROM employees WHERE id=?")) {
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
      }
    } catch (Exception ex) {
      return false;
    }
  }
}

