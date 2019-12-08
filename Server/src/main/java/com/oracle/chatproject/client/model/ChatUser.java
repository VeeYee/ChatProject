package com.oracle.chatproject.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatUser implements Serializable {

  private long username;
  private String password;
  private String nickname;
  private String sex;
  private long age;
  private String image;
  private String signature;
  private Set<ChatUser> friends = new HashSet<>();
  private Set<ChatGroup> groups = new HashSet<>();

  public Set<ChatUser> getFriends() {
    return friends;
  }

  public void setFriends(Set<ChatUser> friends) {
    this.friends = friends;
  }

  public Set<ChatGroup> getGroups() {
    return groups;
  }

  public void setGroups(Set<ChatGroup> groups) {
    this.groups = groups;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChatUser user = (ChatUser) o;
    return username == user.username &&
            age == user.age &&
            Objects.equals(password, user.password) &&
            Objects.equals(nickname, user.nickname) &&
            Objects.equals(sex, user.sex) &&
            Objects.equals(image, user.image) &&
            Objects.equals(signature, user.signature) &&
            Objects.equals(friends, user.friends) &&
            Objects.equals(groups, user.groups);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, nickname, sex, age, image, signature, friends, groups);
  }

  @Override
  public String toString() {
    return "ChatUser{" +
            "username=" + username +
            ", password='" + password + '\'' +
            ", nickname='" + nickname + '\'' +
            ", sex='" + sex + '\'' +
            ", age=" + age +
            ", image='" + image + '\'' +
            ", signature='" + signature + '\'' +
            '}';
  }

  public long getUsername() {
    return username;
  }

  public void setUsername(long username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }


  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }


  public long getAge() {
    return age;
  }

  public void setAge(long age) {
    this.age = age;
  }


  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }


  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

}
