package com.example.app.domain.aggregation.user;

import com.example.app.domain.entity.user.Account;
import com.example.app.domain.entity.user.Profile;
import com.example.app.domain.entity.user.Role;

public class User {
  private final Long userId;
  private Account account;
  private Profile profile;
  private int version;

  private User(Long userId, Account account, Profile profile, int version) {
    this.userId = userId;
    this.account = account;
    this.profile = profile;
    this.version = version;
  }

  public static User reconstitute(Long userId, Account account, Profile profile, int version) {
    return new User(userId, account, profile, version);
  }

  public static User create(String email, String passwordHash, Role role, String displayName) {
    return new User(
        null, new Account(email, passwordHash, role, false, false), new Profile(displayName), 0);
  }

  public void activate() {
    this.account = new Account(account.email(), account.passwordHash(), account.role(), true, true);
  }

  public void changePassword(String newHash) {
    this.account =
        new Account(
            account.email(),
            newHash,
            account.role(),
            account.isActive(),
            account.isEmailVerified());
  }

  public Long getUserId() {
    return userId;
  }

  public Account getAccount() {
    return account;
  }

  public Profile getProfile() {
    return profile;
  }

  public int getVersion() {
    return version;
  }
}
