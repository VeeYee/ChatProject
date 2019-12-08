package com.oracle.chatproject.client.control;

import com.oracle.chatproject.client.model.ChatMessage;
import com.oracle.chatproject.client.model.ChatMessageType;
import com.oracle.chatproject.client.model.ChatUser;
import com.oracle.chatproject.client.utils.Toast;
import com.oracle.chatproject.client.view.Head;
import com.oracle.chatproject.client.view.Login;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterControl implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField nickname;
    @FXML
    private ToggleGroup sex;
    @FXML
    private RadioButton man,female;
    @FXML
    private TextField age;
    @FXML
    private Button selectImage;
    @FXML
    private ImageView myHead;
    @FXML
    private TextArea signature;
    @FXML
    private Button submitRegister;
    @FXML
    private Button backLogin;

    //从登录界面获取的流
    private ObjectOutputStream out;
    private ObjectInputStream in;

    //选择的头像
    private Image selectHead;
    private String url;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.man.setUserData("m");
        this.female.setUserData("f");

        //获取输入、输出流
        LoginControl loginControl = (LoginControl) ControlCollections.controls.get(LoginControl.class);
        this.out = loginControl.getOut();
        this.in = loginControl.getIn();

//        //从选头像界面获得头像
//        HeadControl headControl = (HeadControl) ControlCollections.controls.get(HeadControl.class);

        //返回登录
        backLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Login l=new Login();
                l.start(new Stage());
                backLogin.getScene().getWindow().hide();
            }
        });
        //选择头像
        selectImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = new Stage();
                Head h=new Head();
                stage.setX(1100);
                stage.setY(100);
                h.start(stage);

                //窗口隐藏时执行以下方法
                stage.setOnHidden(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        HeadControl headControl = (HeadControl) ControlCollections.controls.get(HeadControl.class);
                        selectHead = headControl.getSelectImage();
//                        System.out.println("选择的头像是："+selectHead);
                        myHead.setImage(selectHead);
                    }
                });
            }
        });
    }

    /* 点击注册按钮,执行以下方法*/
    @FXML
    public void register(){
        Toast toast = new Toast(submitRegister.getScene().getWindow());
        Toast.Level level = Toast.Level.values()[0]; //提示级别

        ChatUser user = new ChatUser();
        String theUsername = username.getText().trim();
        String thePassword = password.getText().trim();
        String theNickname = nickname.getText();
        String theSex = (String) sex.getSelectedToggle().getUserData();
        String theAge = age.getText();
        String theSignature = signature.getText();

        if(theUsername.equals("")){
            toast.show(level, 1000, "账号不能为空!");
            username.requestFocus();
        }else if(thePassword.equals("")){
            toast.show(level, 1000, "密码不能为空!");
            password.requestFocus();
        }else if(theNickname.equals("")){
            toast.show(level, 1000, "昵称不能为空!");
            nickname.requestFocus();
        }else {
            //请求注册的用户
            user.setUsername(Long.parseLong(theUsername));
            user.setPassword(thePassword);
            user.setNickname(theNickname);
            user.setSex(theSex);
            HeadControl headControl = (HeadControl) ControlCollections.controls.get(HeadControl.class);
            url = headControl.getUrl();
            user.setImage(url);
            if(theAge.equals("")){
                user.setAge(0);
            }else {
                user.setAge(Integer.parseInt(theAge));
            }
            if(theSignature.equals("")){
                user.setSignature("这个人很懒，什么都没有留下~");
            }else{
                user.setSignature(theSignature);
            }
            //封装成标准的Message发送给服务端
            ChatMessage registerMsg = new ChatMessage();
            registerMsg.setFrom(user);
            registerMsg.setType(ChatMessageType.REGISTER);

            //将消息发送给服务端
            try {
                out.writeObject(registerMsg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ChatMessage registerResult = (ChatMessage) in.readObject();
                if (registerResult.getFrom() != null) {
                    submitRegister.getScene().getWindow().hide();
                    Login login = new Login(); //跳转到登录页面
                    login.start(new Stage());
                    System.out.println(registerResult.getFrom().getNickname() + "注册成功！");
                } else {
                    Toast.Level level2 = Toast.Level.values()[2];
                    toast.show(level2, 1000, "该账号已被注册!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
