package com.company.attendance.model;

public class User {
  private Integer id;
  private String username;
  private String passwordHash;
  private String role;
  private String createdAt;

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public String getCreatedAt() { return createdAt; }
  public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

