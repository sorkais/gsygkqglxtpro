package com.company.attendance.dao;

import com.company.attendance.db.Database;
import com.company.attendance.model.AttendanceRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AttendanceDao {
  public boolean insert(AttendanceRecord r) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("INSERT INTO attendance(employee_id,date,check_in,check_out,status,note) VALUES(?,?,?,?,?,?)")) {
        ps.setInt(1, r.getEmployeeId());
        ps.setString(2, r.getDate());
        ps.setString(3, r.getCheckIn());
        ps.setString(4, r.getCheckOut());
        ps.setString(5, r.getStatus());
        ps.setString(6, r.getNote());
        return ps.executeUpdate() > 0;
      }
    } catch (Exception e) {
      return false;
    }
  }

  public boolean update(AttendanceRecord r) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("UPDATE attendance SET employee_id=?, date=?, check_in=?, check_out=?, status=?, note=? WHERE id=?")) {
        ps.setInt(1, r.getEmployeeId());
        ps.setString(2, r.getDate());
        ps.setString(3, r.getCheckIn());
        ps.setString(4, r.getCheckOut());
        ps.setString(5, r.getStatus());
        ps.setString(6, r.getNote());
        ps.setInt(7, r.getId());
        return ps.executeUpdate() > 0;
      }
    } catch (Exception e) {
      return false;
    }
  }

  public ObservableList<AttendanceRecord> listAll() {
    ObservableList<AttendanceRecord> list = FXCollections.observableArrayList();
    try {
      Connection c = Database.get();
      try (Statement st = c.createStatement()) {
        ResultSet rs = st.executeQuery("SELECT * FROM attendance ORDER BY date DESC, id DESC");
        while (rs.next()) {
          AttendanceRecord r = new AttendanceRecord();
          r.setId(rs.getInt("id"));
          r.setEmployeeId(rs.getInt("employee_id"));
          r.setDate(rs.getString("date"));
          r.setCheckIn(rs.getString("check_in"));
          r.setCheckOut(rs.getString("check_out"));
          r.setStatus(rs.getString("status"));
          r.setNote(rs.getString("note"));
          list.add(r);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return list;
  }

  public ObservableList<AttendanceRecord> listByEmployee(int empId) {
    ObservableList<AttendanceRecord> list = FXCollections.observableArrayList();
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("SELECT * FROM attendance WHERE employee_id=? ORDER BY date DESC, id DESC")) {
        ps.setInt(1, empId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          AttendanceRecord r = new AttendanceRecord();
          r.setId(rs.getInt("id"));
          r.setEmployeeId(rs.getInt("employee_id"));
          r.setDate(rs.getString("date"));
          r.setCheckIn(rs.getString("check_in"));
          r.setCheckOut(rs.getString("check_out"));
          r.setStatus(rs.getString("status"));
          r.setNote(rs.getString("note"));
          list.add(r);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return list;
  }

  public AttendanceRecord findByEmployeeAndDate(int empId, String date) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("SELECT * FROM attendance WHERE employee_id=? AND date=? LIMIT 1")) {
        ps.setInt(1, empId);
        ps.setString(2, date);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
          AttendanceRecord r = new AttendanceRecord();
          r.setId(rs.getInt("id"));
          r.setEmployeeId(rs.getInt("employee_id"));
          r.setDate(rs.getString("date"));
          r.setCheckIn(rs.getString("check_in"));
          r.setCheckOut(rs.getString("check_out"));
          r.setStatus(rs.getString("status"));
          r.setNote(rs.getString("note"));
          return r;
        }
        return null;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean delete(int id) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("DELETE FROM attendance WHERE id=?")) {
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
      }
    } catch (Exception e) {
      return false;
    }
  }
}

