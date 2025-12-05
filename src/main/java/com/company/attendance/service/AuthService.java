package com.company.attendance.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.company.attendance.dao.UserDao;
import com.company.attendance.model.User;

public class AuthService {
  private final UserDao userDao = new UserDao();

  public boolean register(String username, String password, String role) {
    if (username == null || username.isBlank() || password == null || password.isBlank()) return false;
    if (!"ADMIN".equals(role) && !"USER".equals(role)) role = "USER";
    if (userDao.findByUsername(username) != null) return false;
    String hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
    Integer uid = userDao.insertReturnId(username, hash, role);
    if (uid != null) {
      com.company.attendance.dao.EmployeeDao ed = new com.company.attendance.dao.EmployeeDao();
      com.company.attendance.model.Employee emp = new com.company.attendance.model.Employee();
      emp.setName("");
      emp.setDepartment("");
      emp.setPosition("");
      emp.setPhone("");
      emp.setHireDate("");
      emp.setStatus("在职");
      ed.insertWithUserId(emp, uid);
      return true;
    }
    return false;
  }

  public User login(String username, String password) {
    User u = userDao.findByUsername(username);
    if (u == null) return null;
    var res = BCrypt.verifyer().verify(password.toCharArray(), u.getPasswordHash());
    if (res.verified) Session.setCurrentUser(u);
    if (res.verified) {
      com.company.attendance.dao.EmployeeDao ed = new com.company.attendance.dao.EmployeeDao();
      var linked = ed.findByUserId(u.getId());
      if (linked == null) {
        var byName = ed.findByName(u.getUsername());
        if (byName != null) {
          ed.linkEmployeeToUserById(byName.getId(), u.getId());
        } else {
          com.company.attendance.model.Employee emp = new com.company.attendance.model.Employee();
          emp.setName("");
          emp.setDepartment("");
          emp.setPosition("");
          emp.setPhone("");
          emp.setHireDate("");
          emp.setStatus("在职");
          ed.insertWithUserId(emp, u.getId());
        }
      }
    }
    return res.verified ? u : null;
  }
}

