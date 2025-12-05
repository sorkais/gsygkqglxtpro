package com.company.attendance.db;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Database {
  private static Connection conn;

  public static synchronized Connection get() {
    if (conn == null) init();
    return conn;
  }

  public static void init() {
    try {
      Path dataDir = Path.of("data");
      if (!Files.exists(dataDir)) Files.createDirectories(dataDir);
      String url = "jdbc:sqlite:" + dataDir.resolve("attendance.db").toAbsolutePath();
      conn = DriverManager.getConnection(url);
      try (Statement st = conn.createStatement()) {
        st.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT UNIQUE NOT NULL, " +
            "password_hash TEXT NOT NULL, " +
            "role TEXT NOT NULL, " +
            "created_at TEXT NOT NULL)");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS employees (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "department TEXT, " +
            "position TEXT, " +
            "phone TEXT, " +
            "hire_date TEXT, " +
            "status TEXT)");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS attendance (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "employee_id INTEGER NOT NULL, " +
            "date TEXT NOT NULL, " +
            "check_in TEXT, " +
            "check_out TEXT, " +
            "status TEXT, " +
            "note TEXT, " +
            "FOREIGN KEY(employee_id) REFERENCES employees(id))");
      }
      try (Statement st = conn.createStatement()) {
        ResultSet rs = st.executeQuery("PRAGMA table_info(employees)");
        boolean hasUserId = false;
        while (rs.next()) {
          if ("user_id".equalsIgnoreCase(rs.getString("name"))) { hasUserId = true; break; }
        }
        if (!hasUserId) {
          st.executeUpdate("ALTER TABLE employees ADD COLUMN user_id INTEGER");
        }
      }
      try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE username=?")) {
        ps.setString(1, "admin");
        ResultSet rs = ps.executeQuery();
        if (rs.next() && rs.getInt(1) == 0) {
          String hash = BCrypt.withDefaults().hashToString(12, "admin123".toCharArray());
          try (PreparedStatement ins = conn.prepareStatement("INSERT INTO users(username,password_hash,role,created_at) VALUES(?,?,?,datetime('now'))")) {
            ins.setString(1, "admin");
            ins.setString(2, hash);
            ins.setString(3, "ADMIN");
            ins.executeUpdate();
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

