package com.oracle.chatproject.client.control;

import com.oracle.chatproject.client.model.ChatGroup;
import com.oracle.chatproject.client.model.ChatMessage;
import com.oracle.chatproject.client.model.ChatMessageType;
import com.oracle.chatproject.client.model.ChatUser;
import com.oracle.chatproject.client.view.GroupChat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.*;

public class GroupChatControl implements Initializable {

    private ChatGroup group;
    private ChatUser me;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Map<Long, GroupChat> groupChats = new HashMap<>();

    @FXML
    private Pane pane;
    @FXML
    private Label myGroup;
    @FXML
    private TextArea groupShowMessage;
    @FXML
    private TextArea groupEditMessage;
    @FXML
    private Label sum;

    public TextArea getGroupShowMessage() {
        return groupShowMessage;
    }

    private ChatUser simpleMe;
    private ChatGroup simpleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        MainControl mainControl = (MainControl) ControlCollections.controls.get(MainControl.class);
        //获取MainControl类中的变量
        this.group = mainControl.getGroup();
        this.me = mainControl.getUser();
        this.out = mainControl.getOut();
        this.groupChats = mainControl.getGroupChats();
        //在聊天框上显示好友的名字
        myGroup.setText(group.getGroupName());

//        //标识是哪个群的聊天框
//        this.groupEditMessage.setUserData(group.getGroupId());

        //为了减少传递的数据量，可以只传我和朋友的昵称和账号，不需要传好友信息
        simpleMe = new ChatUser();
        simpleMe.setNickname(me.getNickname());
        simpleMe.setUsername(me.getUsername());

        simpleGroup = new ChatGroup();
        simpleGroup.setGroupName(group.getGroupName());
        simpleGroup.setGroupId(group.getGroupId());
        simpleGroup.setGroupMembers(group.getGroupMembers());

        put();

        //显示群成员
        ObservableList<String> userList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(userList);
        Set<ChatUser> members = group.getGroupMembers();
        for(ChatUser c:members){
            userList.add(c.getNickname()+"  "+c.getUsername());
        }
        listView.setItems(userList);
        listView.setPrefSize(189, 413);
        listView.setLayoutX(604.0);
        listView.setLayoutY(82.0);
        listView.setEditable(true);
        pane.getChildren().add(listView);

        sum.setText("群成员 "+group.getGroupMembers().size());
    }

    public void put(){
        ControlCollections.controls.put(getClass(),this);
    }

    /** 点击发送按钮，会执行以下方法*/
    @FXML
    public void sendMessage(){
        /** 1. 把编辑好的消息显示到自己的聊天框*/
        String editMsg = groupEditMessage.getText();
        String content = "我"+"   "+new Date().toLocaleString()+"\n"+editMsg+"\r\n\n";
        groupShowMessage.appendText(content);
        groupEditMessage.setText("");

        /** 2. 把消息封装成一个标准的message类，发给服务器(消息时间应该从服务器取)*/
        ChatMessage sendMsg = new ChatMessage();
        sendMsg.setContent(editMsg);
        sendMsg.setFrom(simpleMe);
        sendMsg.setGroup(simpleGroup);
        sendMsg.setType(ChatMessageType.GROUPTEXT);
        try {
            out.writeObject(sendMsg);
            out.flush();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("消息发送失败，请检查网络连接！");
            alert.show();
        }
    }
}
