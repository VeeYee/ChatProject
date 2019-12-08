package com.oracle.chatproject.client.control;

import com.oracle.chatproject.client.model.ChatMessage;
import com.oracle.chatproject.client.model.ChatMessageType;
import com.oracle.chatproject.client.model.ChatUser;
import com.oracle.chatproject.client.utils.Toast;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class AddFriendControl implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private Button find;
    @FXML
    private ImageView find_image;
    @FXML
    private Label find_username;
    @FXML
    private Label find_nickname;
    @FXML
    private Button add;

    private ChatUser user;  //我
    private ObjectOutputStream out;  //我的输出流
    private ChatUser findUser;  //我要添加的好友

    public ImageView getFind_image() {
        return find_image;
    }

    public Label getFind_username() {
        return find_username;
    }

    public Label getFind_nickname() {
        return find_nickname;
    }

    public Button getAdd() {
        return add;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MainControl mainControl = (MainControl) ControlCollections.controls.get(MainControl.class);
        this.user = mainControl.getUser();
        this.out = mainControl.getOut();

        //初始状态，这些控件不可见
        this.find_image.setVisible(false);
        this.find_username.setVisible(false);
        this.find_nickname.setVisible(false);
        this.add.setVisible(false);
    }

    //点击搜索按钮，查找好友
    @FXML
    public void find(){
        ControlCollections.controls.put(getClass(),this);
        String theUsername = username.getText().trim(); //账号
        ChatMessage findUserMsg = new ChatMessage();
        findUserMsg.setContent(theUsername); //将要查找的账号传给服务器
        findUserMsg.setType(ChatMessageType.FINDUSER);
        try {
            out.writeObject(findUserMsg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //点击确认添加，添加好友
    @FXML
    public void add(){
        //ControlCollections.controls.put(getClass(),this);
        Toast toast = new Toast(find_username.getScene().getWindow());
        Toast.Level level = Toast.Level.values()[0]; //提示级别
        String a[] = find_username.getText().trim().split("  ");
        Long me = user.getUsername();
        Long friend = Long.parseLong(a[1]);
        if(me.equals(friend)){
            toast.show(level, 1000, "不能添加自己为好友！");
        }else {
            ChatMessage addUserMsg = new ChatMessage();
            addUserMsg.setFrom(user);
            addUserMsg.setContent(friend+"");  //将好友的账号传到服务器
            addUserMsg.setType(ChatMessageType.ADDFRIEND);
            try {
                out.writeObject(addUserMsg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
