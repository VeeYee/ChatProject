package com.oracle.chatproject.server.dao;

import com.oracle.chatproject.client.model.ChatGroup;
import com.oracle.chatproject.client.model.ChatUser;

import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class UserDAO {
    private Connection con;
    private Statement  sta;

    /** 获取连接*/
    public Connection getCon() {
        try {
            con= DriverManager.getConnection(properties.get("url").toString(),properties.get("username").toString(),properties.get("password").toString());
            sta=con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public Statement getSta() {
        try {
            return getCon().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PreparedStatement getPre(String sql) {
        try {
            return getCon().prepareCall(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PreparedStatement pre;
    private Properties  properties;
    {
        properties=new Properties();
        try {
            //System.out.println(getClass().getClassLoader().getResource("jdbc.properties"));
            properties.load(getClass().getClassLoader().getResourceAsStream("jdbc.properties"));
            Class.forName(properties.get("driverClassName").toString());
            con= DriverManager.getConnection(properties.get("url").toString(),properties.get("username").toString(),properties.get("password").toString());
            sta=con.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库登陆方法，根据传入的用户名和密码查询，返回一个用户对象
     * @param user
     * @return
     */
    public  ChatUser  login(ChatUser  user){
        ChatUser u=null;
        PreparedStatement pre=getPre("select * from chatuser where username=? and password=?");
        try {
            pre.setLong(1,user.getUsername());
            pre.setString(2,user.getPassword());
            ResultSet rs=pre.executeQuery();
            if(rs.next()){
              u=new ChatUser();
              u.setUsername(rs.getLong("username"));
              u.setNickname(rs.getString("nickname"));
              u.setSex(rs.getString("sex"));
              u.setAge(rs.getInt("age"));
              u.setImage(rs.getString("image"));
              u.setSignature(rs.getString("signature"));
              //获取该用户的好友信息
              Set<ChatUser> friends = getAllFriends(user.getUsername());
              u.setFriends(friends);
              //获取用户的群信息
              Set<ChatGroup> groups = getAllGroups(user.getUsername());
              u.setGroups(groups);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            return u;
        }
    }

    //根据我的账号查找我的所有好友信息
    public Set<ChatUser> getAllFriends(long username){
        Set<ChatUser> friends = new HashSet<>();
        PreparedStatement pre = getPre("select * from chatfriend where me_username = ?");
        try{
            pre.setLong(1,username);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){
                ChatUser f = getMyFriend(rs.getLong("friend_username"));
                friends.add(f); //添加到朋友集合
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return friends;  //返回朋友集合
        }
    }

    //根据好友的账号查找好友的消息
    public ChatUser getMyFriend(long username){
        ChatUser friend = null;
        PreparedStatement pre = getPre("select * from chatuser where username = ?");
        try{
            pre.setLong(1,username);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){
                friend = new ChatUser();
                friend.setUsername(rs.getLong("username"));
                friend.setNickname(rs.getString("nickname"));
                friend.setSex(rs.getString("sex"));
                friend.setAge(rs.getInt("age"));
                friend.setImage(rs.getString("image"));
                friend.setSignature(rs.getString("signature"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return friend;  //返回我的好友
        }
    }

    /**
     * 注册新用户的方法，根据传入的注册用户信息向数据库添加一个新的用户
     * @param user
     * @return
     */
    public boolean register(ChatUser user){
        System.out.println(user);
        boolean added=false;
        PreparedStatement pre=getPre("insert into chatuser(username,password,nickname,sex,age,image,signature) values(?,?,?,?,?,?,?)");
        try {
            pre.setLong(1,user.getUsername());
            pre.setString(2,user.getPassword());
            pre.setString(3,user.getNickname());
            pre.setString(4,user.getSex());
            pre.setInt(5,(int)user.getAge());
            pre.setString(6,user.getImage());
            pre.setString(7,user.getSignature());
            added=pre.executeUpdate()>0?true:false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return added;
    }

//    /**
//     * 查询除自己账号以外的所有聊天用户
//     */
//    public Set<ChatUser> getAllUsers(long username){
//        Set<ChatUser> users = new HashSet<>();
//        PreparedStatement pre = getPre("select * from chatuser where username != ?");
//        try{
//            pre.setLong(1,username);
//            ResultSet rs = pre.executeQuery();
//            while (rs.next()){
//                ChatUser u = new ChatUser();
//                u.setUsername(rs.getLong("username"));
//                u.setNickname(rs.getString("nickname"));
//                u.setSex(rs.getString("sex"));
//                u.setAge(rs.getInt("age"));
//                u.setImage(rs.getString("image"));
//                u.setSignature(rs.getString("signature"));
//                users.add(u); //添加到朋友集合
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            return users;  //返回朋友集合
//        }
//    }

    /** 根据账号查找用户,用于添加好友时搜索*/
    public ChatUser findUser(Long username){
        ChatUser user = null;
        PreparedStatement pre = getPre("select * from chatuser where username = ?");
        try{
            pre.setLong(1,username);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){
                user = new ChatUser();
                user.setUsername(rs.getLong("username"));
                user.setNickname(rs.getString("nickname"));
                user.setSex(rs.getString("sex"));
                user.setAge(rs.getInt("age"));
                user.setImage(rs.getString("image"));
                user.setSignature(rs.getString("signature"));
                //获取该用户的好友信息
                Set<ChatUser> friends = getAllFriends(user.getUsername());
                user.setFriends(friends);
                //获取好友的群信息
                Set<ChatGroup> groups = getAllGroups(user.getUsername());
                user.setGroups(groups);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return user;  //返回我的好友
        }
    }

    //判断是否已经成为好友，结果不null代表已经成为好友
    public ChatUser isFriend(long me,long friend){
        ChatUser user = null;
        PreparedStatement pre = getPre("select * from chatfriend where me_username = ? and friend_username = ?");
        try{
            pre.setLong(1,me);
            pre.setLong(2,friend);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){
                user = new ChatUser();
                user.setUsername(rs.getLong("friend_username"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return user;  //返回我的好友
        }
    }

    /** 确认添加好友，根据我的账号和朋友的账号更新表
     *  在好友关系表中添加好友信息
     */
    public boolean addFriend(long me,long friend){
        boolean added = false;
        PreparedStatement pre=getPre("insert into chatfriend(me_username,friend_username) values(?,?)");
        try {
            pre.setLong(1,me);
            pre.setLong(2,friend);
            added=pre.executeUpdate()>0?true:false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return added;
    }

    //创建群聊，添加群信息
    public boolean addGroup(ChatGroup group){
        boolean added = false;
        PreparedStatement pre=getPre("insert into chatgroup(groupid,groupname,groupinfo,groupimage) values(?,?,?,?)");
        try {
            pre.setLong(1,group.getGroupId());
            pre.setString(2,group.getGroupName());
            pre.setString(3,group.getGroupInfo());
            pre.setString(4,group.getGroupImage());
            added=pre.executeUpdate()>0?true:false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return added;
    }

    //插入群中的所有用户
    public boolean addGroupMember(long userId,long groupId){
        boolean added = false;
        PreparedStatement pre=getPre("insert into user_group(userid,groupid) values(?,?)");
        try {
            pre.setLong(1,userId);
            pre.setLong(2,groupId);
            added=pre.executeUpdate()>0?true:false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return added;
    }

    //根据我的账号查找我的所有群聊
    public Set<ChatGroup> getAllGroups(long username){
        Set<ChatGroup> groups = new HashSet<>();
        PreparedStatement pre = getPre("select * from user_group where userid = ?");
        try{
            pre.setLong(1,username);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){
                ChatGroup f = getMyGroup(rs.getLong("groupid"));
                groups.add(f); //添加到群聊集合
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return groups;  //返回群聊集合
        }
    }

    //根据好友的账号查找好友的消息
    public ChatGroup getMyGroup(long groupId){
        ChatGroup group = null;
        PreparedStatement pre = getPre("select * from chatgroup where groupid = ?");
        try{
            pre.setLong(1, groupId);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){
                group = new ChatGroup();
                group.setGroupId(groupId);
                group.setGroupName(rs.getString("groupname"));
                group.setGroupInfo(rs.getString("groupinfo"));
                group.setGroupImage(rs.getString("groupimage"));
                Set<ChatUser> members = getGroupMember(groupId);
                group.setGroupMembers(members);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return group;  //返回我的群
        }
    }

    //根据群号找到所有的成员
    public Set<ChatUser> getGroupMember(long groupId){
        Set<ChatUser> users = new HashSet<>();
        PreparedStatement pre = getPre("select * from user_group where groupid = ?");
        try{
            pre.setLong(1, groupId);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){
                ChatUser u = new ChatUser();
//                u.setUsername(rs.getLong("userid"));
                u = getMyFriend(rs.getLong("userid"));
//                u.setGroupName(rs.getString("groupname"));
//                u.setGroupInfo(rs.getString("groupinfo"));
//                u.setGroupImage(rs.getString("groupimage"));
                users.add(u);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return users;  //返回群成员
        }
    }








//    /* 更新用户的在线状态*/
//    public boolean updateStatus(Long username, int status){
//        boolean updated = false;
//        PreparedStatement pre = getPre("update chatuser set status=? where username=? ");
//        try{
//            pre.setInt(1,status);
//            pre.setLong(2,username);
//            updated = pre.executeUpdate()>0?true:false;
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//        return updated;
//    }
}
