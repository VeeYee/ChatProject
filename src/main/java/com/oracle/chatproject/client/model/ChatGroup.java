package com.oracle.chatproject.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatGroup implements Serializable {
    private long groupId; //群号
    private String groupName;  //群名称
    private String groupInfo;  //群简介
    private String groupImage;  //群头像
    private Set<ChatUser> groupMembers = new HashSet<>();  //群成员

    @Override
    public String toString() {
        return  "  "+groupName + "   " +
                groupId + '\n' ;
    }


//    @Override
//    public String toString() {
//        return "ChatGroup{" +
//                "groupId=" + groupId +
//                ", groupName='" + groupName + '\'' +
//                ", groupInfo='" + groupInfo + '\'' +
//                ", groupImage='" + groupImage + '\'' +
//                ", groupMembers=" + groupMembers +
//                '}';
//    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public Set<ChatUser> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Set<ChatUser> groupMembers) {
        this.groupMembers = groupMembers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatGroup chatGroup = (ChatGroup) o;
        return groupId == chatGroup.groupId &&
                Objects.equals(groupName, chatGroup.groupName) &&
                Objects.equals(groupInfo, chatGroup.groupInfo) &&
                Objects.equals(groupImage, chatGroup.groupImage) &&
                Objects.equals(groupMembers, chatGroup.groupMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, groupName, groupInfo, groupImage, groupMembers);
    }
}
