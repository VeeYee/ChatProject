package com.oracle.chatproject.client.control;

import com.oracle.chatproject.client.model.ChatGroup;
import com.oracle.chatproject.client.model.ChatMessage;
import com.oracle.chatproject.client.model.ChatMessageType;
import com.oracle.chatproject.client.model.ChatUser;
import com.oracle.chatproject.client.utils.Toast;
import com.oracle.chatproject.client.view.Head;
import com.oracle.chatproject.client.view.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class CreateGroupControl implements Initializable {

    @FXML
    private AnchorPane pane;
    @FXML
    private TextField groupId;
    @FXML
    private TextField groupName;
    @FXML
    private TextArea groupInfo;
    @FXML
    private ImageView groupImage;
    @FXML
    private Button select;
    @FXML
    private Label sum;  //群的总人数
    @FXML
    private Button create;

    //群成员
    private Set<ChatUser> groupMember = new HashSet<>();

    private ChatUser user;  //我
    private ObjectOutputStream out;  //我的输出流
    private ObjectInputStream in;

    //选择的头像
    private Image selectHead;
    private String url;

    public Button getCreate() {
        return create;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        put();
        MainControl mainControl = (MainControl) ControlCollections.controls.get(MainControl.class);
        this.user = mainControl.getUser();
        this.out = mainControl.getOut();
        this.in = mainControl.getIn();

        groupMember.add(user); //先把自己加入群聊

        //加载我的好友列表，选择好友加入群聊
        ObservableList<String> userList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(userList);
        Set<ChatUser> friends = user.getFriends();
        for(ChatUser c:friends){
            userList.add(c.getNickname()+"  "+c.getUsername());
        }
        listView.setItems(userList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);  //多选
        listView.setPrefSize(218, 323);
        listView.setLayoutX(365.0);
        listView.setLayoutY(85.0);
        listView.setEditable(true);
        pane.getChildren().add(listView);

        //显示已经选中的好友列表
        ObservableList<String> selectedUser = FXCollections.observableArrayList();
        ListView<String> listView2 = new ListView<>(selectedUser);
        selectedUser.add(user.getNickname()+"  "+user.getUsername());  //把自己加进去
        listView2.setItems(selectedUser);
        listView2.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);  //多选
        listView2.setPrefSize(218, 323);
        listView2.setLayoutX(604.0);
        listView2.setLayoutY(85.0);
        listView2.setEditable(true);
        pane.getChildren().add(listView2);

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ChatUser friend = new ChatUser();
                if (event.getButton() == MouseButton.PRIMARY) {
                    String s = listView.getSelectionModel().getSelectedItem();
                    long username = Long.parseLong(s.split("  ")[1]);
                    friend.setUsername(username);
                    groupMember.add(friend);
                    if(!selectedUser.contains(s)){
                        selectedUser.add(s);
                    }
                    sum.setText("已选好友："+groupMember.size());
                }

            }
        });

        listView2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ChatUser friend = new ChatUser();
                //左边单击选中，右键删除
                if (event.getButton() == MouseButton.PRIMARY) {
                    String s = listView2.getSelectionModel().getSelectedItem();
                    long username = Long.parseLong(s.split("  ")[1]);
                    friend.setUsername(username);
                    if (username!=user.getUsername()) {  //不移除自己
                        groupMember.remove(friend);
                        selectedUser.remove(s);
                    }
                    sum.setText("已选好友："+groupMember.size());
                }

            }
        });


        //选择群头像
        select.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = new Stage();
                Head h=new Head();
                h.start(stage);

                //窗口隐藏时执行以下方法
                stage.setOnHidden(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        HeadControl headControl = (HeadControl) ControlCollections.controls.get(HeadControl.class);
                        selectHead = headControl.getSelectImage();
                        groupImage.setImage(selectHead);
                    }
                });
            }
        });
    }

    public void put(){
        ControlCollections.controls.put(getClass(),this);
    }

    //点击创建群聊按钮
    @FXML
    public void create(){
        Toast toast = new Toast(groupId.getScene().getWindow());
        Toast.Level level = Toast.Level.values()[0]; //提示级别

        String id = groupId.getText().trim();
        String name = groupName.getText().trim();
        String info = groupInfo.getText();

        ChatGroup group = new ChatGroup();
        if(id.equals("")){
            toast.show(level, 1000, "群号不能为空!");
            groupId.requestFocus();
        }else if(name.equals("")){
            toast.show(level, 1000, "群名称不能为空!");
            groupName.requestFocus();
        }else{
            group.setGroupId(Long.parseLong(id));
            group.setGroupName(name);
            if(info.equals("")){
                group.setGroupInfo("暂时没有简介！");
            }else{
                group.setGroupInfo(info);
            }
            HeadControl headControl = (HeadControl) ControlCollections.controls.get(HeadControl.class);
            url = headControl.getUrl();
            group.setGroupImage(url);
            group.setGroupMembers(groupMember);
            //封装成标准的Message发送给服务端
            ChatMessage createMsg = new ChatMessage();
            createMsg.setFrom(user);
            createMsg.setGroup(group);
            createMsg.setType(ChatMessageType.CREATEGROUP);
            try {
                out.writeObject(createMsg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
