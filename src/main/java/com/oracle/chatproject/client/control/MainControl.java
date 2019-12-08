package com.oracle.chatproject.client.control;

import com.oracle.chatproject.client.model.ChatGroup;
import com.oracle.chatproject.client.model.ChatMessage;
import com.oracle.chatproject.client.model.ChatMessageType;
import com.oracle.chatproject.client.model.ChatUser;
import com.oracle.chatproject.client.utils.Toast;
import com.oracle.chatproject.client.view.AddFriend;
import com.oracle.chatproject.client.view.Chat;
import com.oracle.chatproject.client.view.CreateGroup;
import com.oracle.chatproject.client.view.GroupChat;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.*;

public class MainControl implements Initializable {
    //需要把我、我点击的好友、我点击的群、以及我的输入输出流传到聊天窗口
    private ChatUser friend;
    private ChatGroup group;
    private ChatUser user;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    //存储每个账号对应的聊天窗口
    private Map<Long, Chat> chats = new HashMap<>();
    //存每个群对应的聊天窗口
    private Map<Long, GroupChat> groupChats = new HashMap<>();
    private TextArea showMessage;  //从聊天窗口获取
    private TextArea groupShowMessage;

    //从添加好友界面获取的控件
    private ImageView find_image;
    private Label find_username;
    private Label find_nickname;
    private Button add;

    //从创建群聊界面获取的控件
    private Button create;

    @FXML
    private ImageView image;
    @FXML
    private Label nickname;
    @FXML
    private TextArea signature;
    @FXML
    private Button closeBtn;

    @FXML
    private TreeView<Object> tree;
    private TreeItem root;
    private TreeItem<ChatUser> friends;
    private TreeItem<ChatGroup> groups;

    public ChatUser getFriend() {
        return friend;
    }

    public ChatGroup getGroup() {
        return group;
    }

    public ChatUser getUser() {
        return user;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public Map<Long, Chat> getChats() {
        return chats;
    }

    public Map<Long, GroupChat> getGroupChats() {
        return groupChats;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        signature.setWrapText(true);  //个性签名自动换行
        signature.setEditable(false);

        this.friend = new ChatUser();
        this.group = new ChatGroup();
        LoginControl loginControl = (LoginControl) ControlCollections.controls.get(LoginControl.class);
        //获取当前对象的输入输出流
        this.out = loginControl.getOut();
        this.in = loginControl.getIn();
        //获取LoginControl里面的变量user
        this.user = loginControl.getUser();
        nickname.setText(user.getNickname());
        signature.setText(user.getSignature());
        image.setImage(new Image(user.getImage()));
        //System.out.println("主界面--"+user.getUsername()+"的输出流是："+out);

        root = new TreeItem("列表");

        friends = new TreeItem("好友列表", new ImageView("images/friends.png"));
        for (ChatUser u : user.getFriends()) {
            TreeItem f = new TreeItem();
            ChatUser addUser = new ChatUser();
            addUser.setNickname(u.getNickname());
            addUser.setUsername(u.getUsername());
            addUser.setSignature(u.getSignature());
            f.setValue(addUser);
            f.setGraphic(new ImageView(new Image(u.getImage(), 55, 55, false, false)));
            friends.getChildren().add(f);
        }

        groups = new TreeItem("群聊列表", new ImageView("images/group.png"));
        for(ChatGroup p : user.getGroups()){
            TreeItem g = new TreeItem();
            ChatGroup addGroup = new ChatGroup();
            addGroup.setGroupName(p.getGroupName());
            addGroup.setGroupId(p.getGroupId());
            g.setValue(addGroup);
            g.setGraphic(new ImageView(new Image(p.getGroupImage(), 55, 55, false, false)));
            groups.getChildren().add(g);
        }

        root.getChildren().add(friends);
        root.getChildren().add(groups);
        tree.setRoot(root);
        tree.setShowRoot(false);

        put();

        ReceivedThread received = new ReceivedThread();
        received.start();
    }

    public void put(){
        ControlCollections.controls.put(getClass(), this);
    }



    /** 双击点击好友，打开与好友的聊天框*/
    @FXML
    public void choiceFriend(MouseEvent event) {
        if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
            //获取我点击的好友或群聊
            if (tree.getSelectionModel().getSelectedItem().isLeaf()) {
                String parent = (String)tree.getSelectionModel().getSelectedItem().getParent().getValue();
//                System.out.println(parent);
                if(parent.equals("好友列表")) {
                    ChatUser u = (ChatUser) tree.getSelectionModel().getSelectedItem().getValue(); //我点击的好友对象
                    //在我的所有好友中找到我点击的好友，可以获取该好友的完整信息
                    for (ChatUser f : user.getFriends()) {
                        if (f.getUsername() == u.getUsername()) { //通过账号找到朋友的完整信息
                            friend = f;
                            break;
                        }
                    }
                    /** 我打开好友的聊天窗口，聊天窗口不重复打开*/
                    Stage stage = new Stage();
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            //窗口关闭，从集合中移除
                            chats.remove(u.getUsername());
                        }
                    });
                    //聊天框没有打开过时，创建聊天框，并放入集合
                    if (!chats.containsKey(u.getUsername())) {
                        Chat c = new Chat();
                        c.start(stage);
                        chats.put(u.getUsername(), c);
//                    System.out.println(user.getNickname()+"打开了与"+u.getNickname()+"的聊天框！");
                    }
                }
                if(parent.equals("群聊列表")){
                    ChatGroup g = (ChatGroup) tree.getSelectionModel().getSelectedItem().getValue(); //我点击的群对象
                    //在我的所有好友中找到我点击的好友，可以获取该好友的完整信息
                    for (ChatGroup p : user.getGroups()) {
                        if (p.getGroupId() == g.getGroupId()) { //通过群号找到群
                            group = p;
                            break;
                        }
                    }
                    /** 打开群聊天窗口，聊天窗口不重复打开*/
                    Stage stage = new Stage();
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            //窗口关闭，从集合中移除
                            groupChats.remove(g.getGroupId());
                        }
                    });
                    //聊天框没有打开过时，创建聊天框，并放入集合
                    if (!groupChats.containsKey(g.getGroupId())) {
                        GroupChat c = new GroupChat();
                        c.start(stage);
                        groupChats.put(g.getGroupId(), c);
                    }
                }
            }
        }
    }


    /** 退出功能，用户下线*/
    @FXML
    public void logout(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
        ChatMessage logoutMsg = new ChatMessage();
        logoutMsg.setFrom(user);
        logoutMsg.setType(ChatMessageType.LOGOUT);
        try {
            out.writeObject(logoutMsg);  //向服务器传下线请求
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 点击添加好友按钮，添加好友*/
    @FXML
    public void addFriend(){
        AddFriend a = new AddFriend();
        a.start(new Stage());
    }

    /** 点击创建群聊按钮*/
    @FXML
    public void creatGroup(){
        CreateGroup c = new CreateGroup();
        c.start(new Stage());
    }

    /** 开启一个接受服务器转发过来消息的线程
     *  需要在收到消息之后弹出聊天窗口
     */
    private class ReceivedThread extends Thread {
        @Override
        public void run(){
            while (true){
                //死循环不断接受服务器的消息
                try {
                    //1.读取服务器转发的消息，并获取发送此消息的对象
                    ChatMessage getMsg = (ChatMessage) in.readObject();
//                    long from = getMsg.getFrom().getUsername();
//                    long to = getMsg.getTo().getUsername();
//                    System.out.println("服务器转发的消息--"+getMsg.getFrom().getNickname()+"发给我的消息："+getMsg.getContent());

                    //2. 在接收消息方的聊天框中添加消息
//                    Chat friendChat = new Chat();
//                    chats.put(to, friendChat);
//                    openChat(from);
                    //friendChat.start(new Stage());
                    //获取 在别人那里打开的我的聊天框
//                    ChatControl chatControl = (ChatControl) ControlCollections.controls.get(ChatControl.class);
//                    showMessage = chatControl.getShowMessage();
//                    String u = chatControl.getMe().getNickname();
//                    System.out.println(u+"的聊天框");

//                    2.消息接收方弹出来自消息发送方的对话框，不重复弹出
//                    Chat friendChat = null;
//                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                        @Override
//                        public void handle(WindowEvent event) {
//                            chats.remove(from);  //窗口关闭，从集合中移除
//                        }
//                    });
//                    //聊天框没有打开过时，创建聊天框，并放入集合
//                    if (!chats.containsKey(from)) {
//                    //friendChat = new Chat();
//                    stage.show();
//                    //friendChat.start(stage);
//                    chats.put(from, friendChat);
//                    }

                    //3.解析收到消息的类型
                    switch (getMsg.getType()){  //接收消息方的视角
                        case TEXT:{
                            System.out.println("服务器转发的消息--"+getMsg.getFrom().getNickname()+"发给我的消息："+getMsg.getContent());
//                            Platform.runLater(new Runnable(){
//                                @Override
//                                public void run(){  //当线程读到了一条文本消息时，弹出一个聊天窗口
//                                    //与我聊天的朋友是给我发消息的对象
//                                    friend = getMsg.getFrom();
//                                    Stage stage = new Stage();
//                                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                                        @Override
//                                        public void handle(WindowEvent event) {
//                                            //窗口关闭，从集合中移除
//                                            chats.remove(getMsg.getFrom().getUsername());
//                                        }
//                                    });
//                                    //聊天框没有打开过时，创建聊天框，并放入集合
//                                    if (!chats.containsKey(getMsg.getFrom().getUsername())) {
//                                        Chat c = new Chat();
//                                        c.start(stage);
//                                        chats.put(getMsg.getFrom().getUsername(),c);
//                                    }
//                                }
//                            });
                            ChatControl chatControl = (ChatControl) ControlCollections.controls.get(ChatControl.class);
//                            System.out.println(chatControl);
                            showMessage = chatControl.getShowMessage();  //获取从对方那里打开的我的聊天框
                            //将我发送的消息(由服务器转发过来的消息)写到接受对象的聊天框中
                            String content = getMsg.getFrom().getNickname()+"   "+getMsg.getTime()+"\n"+getMsg.getContent()+"\r\n\n";
                            showMessage.appendText(content);
                            break;
                        }
                        case SHAKE:{
                            ChatControl chatControl = (ChatControl) ControlCollections.controls.get(ChatControl.class);
                            showMessage = chatControl.getShowMessage();
                            //....谁给你发送了一条窗口抖动
                            String content = getMsg.getFrom().getNickname()+"   "+getMsg.getTime()+"\n" +
                                    getMsg.getContent()+"\r\n\n";
                            //System.out.println("抖动消息是:"+content);
                            showMessage.appendText(content);
                            //对方收到窗口抖动
                            chatControl.shakeWindow();
                            break;
                        }
                        case GROUPTEXT:{
                            GroupChatControl groupChatControl = (GroupChatControl) ControlCollections.controls.get(GroupChatControl.class);
                            groupShowMessage = groupChatControl.getGroupShowMessage();  //获取从对方那里打开的群聊框
                            //将我发送的消息(由服务器转发过来的消息)写到接受对象的聊天框中
                            String content = getMsg.getFrom().getNickname()+"   "+getMsg.getTime()+"\n"+getMsg.getContent()+"\r\n\n";
//                            if(groupChats.containsKey((Long)groupShowMessage.getUserData())){
                                groupShowMessage.appendText(content);
//                            }
                            break;
                        }
                        case FINDUSER:{
                            ChatUser user = getMsg.getFrom();
                            AddFriendControl addFriendControl = (AddFriendControl) ControlCollections.controls.get(AddFriendControl.class);
                            find_image = addFriendControl.getFind_image();
                            find_username = addFriendControl.getFind_username();
                            find_nickname = addFriendControl.getFind_nickname();
                            add = addFriendControl.getAdd();
                            if(user!= null){ //找到该用户
                                //在其他线程更新JavaFX的线程上的信息的时候使用此方法，否则会报错
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新JavaFX的主线程的代码放在此处(AddFriend中的主线程)
                                        find_image.setVisible(true);
                                        find_username.setVisible(true);
                                        find_nickname.setVisible(true);
                                        add.setVisible(true);
                                        find_image.setImage(new Image(user.getImage()));
                                        find_username.setText("账号  "+user.getUsername());
                                        find_nickname.setText("昵称  "+user.getNickname());
                                    }
                                });
                            }else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新JavaFX的主线程的代码放在此处
                                        find_image.setVisible(false);
                                        find_username.setVisible(false);
                                        find_nickname.setVisible(false);
                                        add.setVisible(false);
                                        Toast toast = new Toast(find_username.getScene().getWindow());
                                        Toast.Level level = Toast.Level.values()[0]; //提示级别
                                        toast.show(level, 1000, "该账号不存在!");
                                    }
                                });
                            }
                            break;
                        }
                        //发起添加好友请求的用户，服务器返回以下消息
                        case ADDFRIEND:{
                            System.out.println("我收到添加好友的结果");
                            Toast toast = new Toast(find_username.getScene().getWindow());
                            Toast.Level level = Toast.Level.values()[2]; //提示级别
                            String result = getMsg.getContent();
                            if(result.equals("isFriend")) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.show(level, 1000, "你们已经成为好友，请勿重复添加！");
                                    }
                                });
                            }else{
                                //添加成功
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.show(level, 1000, "添加成功！");
                                        find_username.getScene().getWindow().hide();
                                        //在好友列表增加我新添加的好友
                                        TreeItem f = new TreeItem();
                                        ChatUser addUser = getMsg.getTo();
                                        f.setValue(addUser);
                                        f.setGraphic(new ImageView(new Image(getMsg.getTo().getImage(), 55, 55, false, false)));
                                        friends.getChildren().add(f);
                                        //我的好友列表更新了，同时在主界面更新我的信息（好友信息）
                                        //这样点击好友时才能获取到我刚添加的好友信息
                                        user = getMsg.getFrom();
                                    }
                                });
                            }
                            break;
                        }
                        //收到别人好友请求的对象，服务器返回以下消息
                        case ACCEPTFRIEND:{
                            System.out.println("我收到一条好友申请");
                            TreeItem f = new TreeItem();
                            ChatUser addUser = getMsg.getFrom();
                            f.setValue(addUser);
                            f.setGraphic(new ImageView(new Image(getMsg.getFrom().getImage(), 55, 55, false, false)));
                            friends.getChildren().add(f);
                            user = getMsg.getTo();  //更新我的好友列表
                            break;
                        }
                        case CREATEGROUP:{
                            CreateGroupControl createGroupControl = (CreateGroupControl) ControlCollections.controls.get(CreateGroupControl.class);
                            create = createGroupControl.getCreate();
                            switch (getMsg.getContent()){
                                case "success":
                                    System.out.println("添加成功！");
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            create.getScene().getWindow().hide();
                                            TreeItem g = new TreeItem();
                                            ChatGroup addGroup = getMsg.getGroup();
                                            g.setValue(addGroup);
                                            g.setGraphic(new ImageView(new Image(addGroup.getGroupImage(), 55, 55, false, false)));
                                            groups.getChildren().add(g);
                                            user = getMsg.getFrom();
                                        }
                                    });
                                    break;
                                case "fail":
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = new Toast(create.getScene().getWindow());
                                            Toast.Level level = Toast.Level.values()[2]; //提示级别
                                            toast.show(level, 1000, "添加失败，该群号已存在！");
                                        }
                                    });
                                    break;
                            }
                            break;
                        }
                        case JOINGROUP:{  //被邀请的人更新群聊列表
                            System.out.println("我加入了群聊");
                            TreeItem g = new TreeItem();
                            ChatGroup addGroup = getMsg.getGroup();
                            g.setValue(addGroup);
                            g.setGraphic(new ImageView(new Image(addGroup.getGroupImage(), 55, 55, false, false)));
                            groups.getChildren().add(g);
                            user = getMsg.getTo();
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
    