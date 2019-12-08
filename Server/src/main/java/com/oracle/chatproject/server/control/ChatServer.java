package com.oracle.chatproject.server.control;

import com.oracle.chatproject.client.model.ChatGroup;
import com.oracle.chatproject.client.model.ChatMessage;
import com.oracle.chatproject.client.model.ChatMessageType;
import com.oracle.chatproject.client.model.ChatUser;

import com.oracle.chatproject.server.dao.UserDAO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 这是聊天软件的服务器类，用来对客户端提供聊天功能的各种服务
 *
 * 本次项目中，服务器充当两个角色
 * 1.业务处理中心，比如处理用户发送过来的登陆，或者注册请求
 * 2.消息中转中心，A跟B聊天，A发送的消息先发送到Server端，Server端解析完毕后，再转发到B
 */
public class ChatServer {
    //定义一个ServerSocket对象，用来让客户端连接
    private ServerSocket server;
    //定义一个Socket套接字，用于获取输入、输出流
    private Socket client;
    //定义一个User数据库操作对象
    private UserDAO dao;
    //定义一个键值对集合，存储所有连接进来的用户的账号和底层对应的输出流（即存着所有在线的用户）
    private Map<Long,ObjectOutputStream> allClients = new HashMap<>();
    //从客户端读回的消息
    private ChatMessage c;

    //1.在动态代码块中将,ServerSocket对象初始化
    {
        try {
            server = new ServerSocket(8888);
            System.out.println("服务器启动成功！");
            dao = new UserDAO();
        } catch (IOException e) {
            System.err.println("服务器启动失败！请检查端口是否被占用！");
        }
    }

    //2. 在类的构造器里面开启对外服务的方法（调用accept()方法）
    public ChatServer(){
        //服务器启动成功后，构造器里准备开启对外服务
        try{
            //服务器要对多个用户提供连接服务，所以这里必须开启一个死循环不停的接受客户端的用户连接
            while (true) {
                //调用accept()方法接受客户端的连接
                client = server.accept();
                //建立每个客户端的输入输出流
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                System.out.println(client.getInetAddress().getHostAddress()+"连接进来了！");

                /**为了保障每个客户端连接进来都可以独立和服务器发送消息而不影响其他客户端
                 * 所以每个连接进来的客户端都必须开启一个线程独立运行，接受客户端的消息代码
                 * 并把属于这个客户的输入输出流传递给线程类
                 */
                MessageReceivedThread receivedThread = new MessageReceivedThread(out,in);
                receivedThread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 封装一个线程类，这个类用来不停地接受用户发过来的消息
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class MessageReceivedThread extends Thread{
        private ObjectOutputStream out;
        private ObjectInputStream in;

        @Override
        public void run(){
            //死循环，不停地读客户端发过来的消息
            while (true) {
                try {
                    //当前代码执行一次，代表服务器读取到了客户端发送过来了一条消息
                    c = (ChatMessage) in.readObject();
                    //拆包裹：使用一个条件分支结构对不同的消息做不同的处理
                    switch (c.getType()){
                        case LOGIN:{
                            System.out.println("客户端发来登录请求..");
                            /** 把登录时传过来的用户信息传入dao方法中
                             *  会返回一个数据库里的用户对象
                             *  如果存在将会返回一个完整的user对象，不存在将返回null
                             */
                            //登录的结果，封装成一个ChatMessage;请求登录的用户也封装成一个ChatUser
                            ChatMessage loginResult = new ChatMessage();
                            ChatUser user = null;
                            if(allClients.containsKey(c.getFrom().getUsername())){
                                //用户在线时，写回一个空对象
                                loginResult.setFrom(null);
                                loginResult.setContent("online");
                            }else{
                                user = dao.login(c.getFrom());
                                //将查询返回的用户对象存储到消息对象中，返回给客户端
                                loginResult.setFrom(user);
                                loginResult.setContent("offline");
                            }
                            /** 应该在登录成功的时候，将当前用户的账号和当前线程的输出流
                             *  存到服务器集合中，方便后期服务器能找到这个人并转发信息给他
                             */
                            if(user!=null){
                                allClients.put(user.getUsername(),out);
                            }
                            System.out.println("登录的用户:"+user);
                            //消息封装后，就可以使用当前线程的输出流将这个登录结果输出
                            System.out.println("在线用户:"+allClients);
                            out.writeObject(loginResult);
                            out.flush();
                            break;
                        }
                        case LOGOUT:{
                            System.out.println("客户端发来下线请求..");
                            allClients.remove(c.getFrom().getUsername());
                            System.out.println("下线的用户:"+c.getFrom());
                            System.out.println("下线后的在线用户:"+allClients);
                            break;
                        }
                        case REGISTER:{
                            System.out.println("客户端发来注册请求..");
                            boolean added = dao.register(c.getFrom());
                            ChatMessage registerResult = new ChatMessage();
                            if(added){
                                registerResult.setFrom(c.getFrom());
                                System.out.println(c.getFrom().getNickname()+"注册成功！");
                            }else{
                                //注册不成功，写回一个空对象
                                registerResult.setFrom(null);
                                System.out.println("注册失败！");
                            }
                            out.writeObject(registerResult);
                            out.flush();
                            break;
                        }
                        case SHAKE:{
                            System.out.println("服务器收到一条抖动消息："+c);
                        }
                        case TEXT:{
                            System.out.println("服务器收到一条文本消息："+c);
                            //消息时间取服务器的时间
                            c.setTime(new Date().toLocaleString());
                            //获取接受消息的对象
                            long to = c.getTo().getUsername();
                            //对方在集合里，说明对方获得了输出流，即已经上线！
                            if(allClients.containsKey(to)){
                                //获取消息接收方的输出流，这条消息只有他能收到。
                                ObjectOutputStream out = allClients.get(to);
                                out.writeObject(c);
                                out.flush();
                                System.out.println("对方在线，服务器将转发消息！");
                                System.out.println("所有在线用户的集合：" + allClients);
                            }else{
                                //else说明对方不在线，这里可以书写额外的代码将消息暂存到数据库
                                //等用户登录后再提取
                                System.out.println("对方不在线，服务器不转发消息！");
                            }
                            break;
                        }
                        case GROUPTEXT:{
                            System.out.println("服务器收到一条群聊消息："+c);
                            //消息时间取服务器的时间
                            c.setTime(new Date().toLocaleString());
                            //获取接受消息的对象  所有在线的群成员
                            for(ChatUser u : c.getGroup().getGroupMembers()){
                                ObjectOutputStream friendOut = allClients.get(u.getUsername());
                                if(u.getUsername()!=c.getFrom().getUsername() && friendOut!=null){
                                    c.setType(ChatMessageType.GROUPTEXT);
                                    c.setTo(u);
                                    friendOut.writeObject(c);
                                    friendOut.flush();
                                }
                            }
//                            long to = c.getGroup().getGroupId();
//                            //对方在集合里，说明对方获得了输出流，即已经上线！
//                            if(allClients.containsKey(to)){
//                                //获取消息接收方的输出流，这条消息只有他能收到。
//                                ObjectOutputStream out = allClients.get(to);
//                                out.writeObject(c);
//                                out.flush();
//                                System.out.println("对方在线，服务器将转发消息！");
//                                System.out.println("所有在线用户的集合：" + allClients);
//                            }else{
//                                //else说明对方不在线，这里可以书写额外的代码将消息暂存到数据库
//                                //等用户登录后再提取
//                                System.out.println("对方不在线，服务器不转发消息！");
//                            }
                            break;
                        }
                        case FINDUSER:{
                            System.out.println("客户端发来查找用户请求...");
                            ChatUser user = dao.findUser(Long.parseLong(c.getContent()));
                            ChatMessage findResult = new ChatMessage();
                            findResult.setFrom(user);
                            findResult.setType(ChatMessageType.FINDUSER);
                            out.writeObject(findResult);
                            out.flush();
                            System.out.println("服务器返回查询结果"+user);
                            break;
                        }
                        case ADDFRIEND:{
                            System.out.println("客户端发来添加好友的请求...");
                            long me = c.getFrom().getUsername();
                            long friend = Long.parseLong(c.getContent());
                            ChatMessage addResult = new ChatMessage();

                            ChatUser isFriend = dao.isFriend(me, friend);  //判断是否已成为朋友
                            if (isFriend == null) {  //不是朋友 可以添加
                                //互为好友，需要添加两条记录
                                if (dao.addFriend(me, friend) && dao.addFriend(friend, me)) {
                                    //查找我的信息，和我添加的好友的信息  添加到数据库之后再查询
                                    ChatUser i = dao.findUser(me);
                                    ChatUser u = dao.findUser(friend);
                                    addResult.setFrom(i);
                                    addResult.setTo(u);
                                    addResult.setContent("success");  //成功插入数据库，添加成功
                                    addResult.setType(ChatMessageType.ADDFRIEND);
                                    System.out.println("服务器消息：好友添加成功！");

                                    //在朋友那边，更新信息
                                    ObjectOutputStream friendOut = allClients.get(friend);  //朋友的流接受我的好友请求
                                    ChatMessage acceptResult = new ChatMessage();
                                    if(friendOut!=null) {  //朋友在线 才发送请求
                                        acceptResult.setFrom(i);
                                        acceptResult.setTo(u);
                                        acceptResult.setType(ChatMessageType.ACCEPTFRIEND);
                                        friendOut.writeObject(acceptResult);
                                        friendOut.flush();
                                    }
                                }
                            } else {  //已成为好友，不可重复添加
                                addResult.setContent("isFriend");
                                addResult.setType(ChatMessageType.ADDFRIEND);
                            }
                            out.writeObject(addResult); //我的流写添加结果
                            out.flush();
                            break;
                        }
                        case CREATEGROUP:{
                            System.out.println("客户端发来创建群聊的请求");
                            ChatGroup group = c.getGroup();
                            if(dao.addGroup(group)){
                                Set<ChatUser> members = group.getGroupMembers();
                                for (ChatUser u:members){  //把群成员添加到群
                                    dao.addGroupMember(u.getUsername(),group.getGroupId());
                                }
                                System.out.println("群聊创建成功！");
                                c.setContent("success");
                                //查询该用户，更新该用户的群聊
                                c.setFrom(dao.findUser(c.getFrom().getUsername()));
                            }else{
                                System.out.println("群聊创建失败，该群已存在！");
                                c.setContent("fail");
                            }
                            out.writeObject(c);
                            out.flush();
                            //发给群里其他用户
                            for(ChatUser u : c.getGroup().getGroupMembers()){
                                ObjectOutputStream friendOut = allClients.get(u.getUsername());
                                if(u.getUsername()!=c.getFrom().getUsername() && friendOut!=null){
                                    c.setType(ChatMessageType.JOINGROUP);
                                    c.setTo(u);
                                    friendOut.writeObject(c);
                                    friendOut.flush();
                                }
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("客户端已下线");
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}
