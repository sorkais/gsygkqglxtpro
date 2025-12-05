package com.company.attendance.dao;

import com.company.attendance.db.Database;
import com.company.attendance.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDao {
  public User findByUsername(String username) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=?")) {
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
          User u = new User();
          u.setId(rs.getInt("id"));
          u.setUsername(rs.getString("username"));
          u.setPasswordHash(rs.getString("password_hash"));
          u.setRole(rs.getString("role"));
          u.setCreatedAt(rs.getString("created_at"));
          return u;
        }
        return null;
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public boolean insert(String username, String passwordHash, String role) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("INSERT INTO users(username,password_hash,role,created_at) VALUES(?,?,?,datetime('now'))")) {
        ps.setString(1, username);
        ps.setString(2, passwordHash);
        ps.setString(3, role);
        return ps.executeUpdate() > 0;
      }
    } catch (Exception e) {
      return false;
    }
  }

  public Integer insertReturnId(String username, String passwordHash, String role) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("INSERT INTO users(username,password_hash,role,created_at) VALUES(?,?,?,datetime('now'))", java.sql.Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, username);
        ps.setString(2, passwordHash);
        ps.setString(3, role);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) return rs.getInt(1);
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  public java.util.List<User> listAll() {
    java.util.ArrayList<User> list = new java.util.ArrayList<>();
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("SELECT * FROM users ORDER BY id DESC")) {
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          User u = new User();
          u.setId(rs.getInt("id"));
          u.setUsername(rs.getString("username"));
          u.setPasswordHash(rs.getString("password_hash"));
          u.setRole(rs.getString("role"));
          u.setCreatedAt(rs.getString("created_at"));
          list.add(u);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return list;
  }

  public boolean updateRole(int id, String role) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("UPDATE users SET role=? WHERE id=?")) {
        ps.setString(1, role);
        ps.setInt(2, id);
        return ps.executeUpdate() > 0;
      }
    } catch (Exception e) {
      return false;
    }
  }

  public boolean resetPassword(int id, String newHash) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps = c.prepareStatement("UPDATE users SET password_hash=? WHERE id=?")) {
        ps.setString(1, newHash);
        ps.setInt(2, id);
        return ps.executeUpdate() > 0;
      }
    } catch (Exception e) {
      return false;
    }
  }

  public boolean deleteUser(int id) {
    try {
      Connection c = Database.get();
      try (PreparedStatement ps1 = c.prepareStatement("DELETE FROM attendance WHERE employee_id IN (SELECT id FROM employees WHERE user_id=?)");
           PreparedStatement ps2 = c.prepareStatement("DELETE FROM employees WHERE user_id=?");
           PreparedStatement ps3 = c.prepareStatement("DELETE FROM users WHERE id=?")) {
        ps1.setInt(1, id);
        ps1.executeUpdate();
        ps2.setInt(1, id);
        ps2.executeUpdate();
        ps3.setInt(1, id);
        return ps3.executeUpdate() > 0;
      }
    } catch (Exception e) {
      return false;
    }
  }
}

