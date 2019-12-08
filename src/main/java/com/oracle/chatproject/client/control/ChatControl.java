package com.oracle.chatproject.client.control;

import com.oracle.chatproject.client.model.ChatMessage;
import com.oracle.chatproject.client.model.ChatMessageType;
import com.oracle.chatproject.client.model.ChatUser;
import com.oracle.chatproject.client.view.Chat;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ChatControl implements Initializable {

    private ChatUser friend;
    private ChatUser me;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Map<Long,Chat> chats = new HashMap<>();

    private ChatUser simpleMe;
    private ChatUser simpleFriend;

    @FXML
    private Label myFriend;
    @FXML
    private TextArea showMessage;
    @FXML
    private TextArea editMessage;

    public TextArea getShowMessage() {
        return showMessage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MainControl mainControl = (MainControl) ControlCollections.controls.get(MainControl.class);
        //获取MainControl类中的变量
        this.friend = mainControl.getFriend();
        this.me = mainControl.getUser();
        this.out = mainControl.getOut();
        this.in = mainControl.getIn();
        this.chats = mainControl.getChats();
        //在聊天框上显示好友的名字
        myFriend.setText(friend.getNickname());
        put();

        //为了减少传递的数据量，可以只传我和朋友的昵称和账号，不需要传好友信息
        simpleMe = new ChatUser();
        simpleMe.setNickname(me.getNickname());
        simpleMe.setUsername(me.getUsername());

        simpleFriend = new ChatUser();
        simpleFriend.setNickname(friend.getNickname());
        simpleFriend.setUsername(friend.getUsername());
    }

    public void put(){
        ControlCollections.controls.put(getClass(),this);
    }

    /** 点击发送按钮，会执行以下方法*/
    @FXML
    public void sendMessage(){
        /** 1. 把编辑好的消息显示到自己的聊天框*/
        String editMsg = editMessage.getText();
        //String content = me.getNickname()+"   "+new Date().toLocaleString()+"\n"+editMsg+"\r\n\n";
        String content = "我"+"   "+new Date().toLocaleString()+"\n"+editMsg+"\r\n\n";
        showMessage.appendText(content);
        editMessage.setText("");

        /** 2. 把消息封装成一个标准的message类，发给服务器(消息时间应该从服务器取)*/
        ChatMessage sendMsg = new ChatMessage();
        sendMsg.setContent(editMsg);
        sendMsg.setFrom(simpleMe);
        sendMsg.setTo(simpleFriend);
        sendMsg.setType(ChatMessageType.TEXT);
        try {
            out.writeObject(sendMsg);
            out.flush();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("消息发送失败，请检查网络连接！");
            alert.show();
        }
    }

    /** 点击窗口抖动按钮， 会执行以下方法*/
    @FXML
    public void sendShake(){
//        String content = me.getNickname()+"   "+new Date().toLocaleString()+"\n"+"你发送了一个窗口抖动。"+"\r\n\n";
        String content = "我"+"   "+new Date().toLocaleString()+"\n"+"你发送了一个窗口抖动。"+"\r\n\n";
        showMessage.appendText(content);

        /** 把消息封装成一个标准的ChatMessage类，发给服务器*/
        ChatMessage sendMsg = new ChatMessage();
        sendMsg.setContent("给你发送了一个窗口抖动。");
        sendMsg.setFrom(simpleMe);
        sendMsg.setTo(simpleFriend);
        sendMsg.setType(ChatMessageType.SHAKE);
        //自己的窗口抖动
        shakeWindow();
        try {
            out.writeObject(sendMsg);
            out.flush();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("抖动发送失败，请检查网络连接！");
            alert.show();
        }
    }


//    private class ReceivedThread extends Thread {
//        @Override
//        public void run(){
//            while (true){
//                //死循环不断接受服务器的消息
//                try {
//                    //1.读取服务器转发的消息，并获取发送此消息的对象
//                    ChatMessage getMsg = (ChatMessage) in.readObject();
//                    long from = getMsg.getFrom().getUsername();
//                    System.out.println("服务器转发的消息--"+getMsg.getFrom().getNickname()+"发给我的消息："+getMsg.getContent());
//
//                    //2.消息接收方弹出来自消息发送方的对话框，不重复弹出
//                    //Chat friendChat = null;
////                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
////                        @Override
////                        public void handle(WindowEvent event) {
////                            chats.remove(from);  //窗口关闭，从集合中移除
////                        }
////                    });
////                    //聊天框没有打开过时，创建聊天框，并放入集合
////                    if (!chats.containsKey(from)) {
////                    //friendChat = new Chat();
////                    stage.show();
////                    //friendChat.start(stage);
////                    chats.put(from, friendChat);
////                    }
//
//                    //3.解析收到消息的类型
//                    switch (getMsg.getType()){
//                        case TEXT:{
//                            //将我发送的消息(由服务器转发过来的消息)写到接受对象的聊天框中
//                            String content = getMsg.getFrom().getNickname()+"   "+getMsg.getTime()+"\n"+getMsg.getContent()+"\r\n\n";
//                            showMessage.appendText(content);
//                            break;
//                        }
//                        case SHAKE:{
//                            String content = getMsg.getFrom().getNickname()+"   "+getMsg.getTime()+"\n"+getMsg.getContent()+"\r\n\n";
//                            showMessage.appendText(content);
//                            shakeWindow();
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    //窗口抖动的方法
    @FXML
    public void shakeWindow(){
        new Thread() {
            @Override
            public void run() {
                double startX = showMessage.getScene().getWindow().getX();
                double startY = showMessage.getScene().getWindow().getY();
                int fudu = 2;
                int delay = 30;
                for (int n = 0; n < 8; n++) {
                    showMessage.getScene().getWindow().setX(startX - fudu);
                    showMessage.getScene().getWindow().setY(startY);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showMessage.getScene().getWindow().setX(startX + fudu);
                    showMessage.getScene().getWindow().setY(startY);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showMessage.getScene().getWindow().setX(startX);
                    showMessage.getScene().getWindow().setY(startY - fudu);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showMessage.getScene().getWindow().setX(startX);
                    showMessage.getScene().getWindow().setY(startY + fudu);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
