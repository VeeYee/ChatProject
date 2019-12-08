package com.oracle.chatproject.client.control;

import com.oracle.chatproject.client.model.*;
import com.oracle.chatproject.client.utils.Toast;
import com.oracle.chatproject.client.view.Main;

import com.oracle.chatproject.client.view.Register;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginControl implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginBtn;
    @FXML
    private Button registerBtn;
    @FXML
    private ImageView head;

    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ChatUser user; //发送登录请求的用户

    //传到主界面及聊天界面
    public ChatUser getUser() {
        return user;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    /**
     * 控制器默认的初始化方法，用来执行一些组件额外的初始化业务，这个方法会在ui组件渲染打开前执行。
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**登录界面底层持有的socket对象应该在构造器最后一行初始化
         * （先渲染界面，然后再建立底层通讯）
         */
        try{
            client = new Socket("localhost",8888);
            /**
             * 因为为了更好的传递和处理消息
             * 所以项目中的任何消息都会封装成一个标准的ChatMessage
             * 所以，底层socket必须提供出序列化流（能将java对象写入通道的流）
             */
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

        }catch (IOException e){
            //一旦创建socket时出现异常，说明链接服务器失败，这里应该使用swing的ui技术弹出错误提示框
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("网络连接失败");
            alert.show();
        }
    }

    /** 点击登录按钮，会执行以下方法*/
    @FXML
    public void login(){
        //1.先获取用户在ui的输入框中输入的数据
        String theUsername = username.getText().trim();
        String thePassword = password.getText().trim();

        //3. 将登陆必须的数据封装成一个Message对象
        ChatMessage loginMessage = new ChatMessage();
        user = new ChatUser();  //消息来源是ChatUser类型
        user.setUsername(Long.parseLong(theUsername));
        user.setPassword(thePassword);
        loginMessage.setFrom(user); //来自谁的消息（谁发送的登录请求）
        loginMessage.setType(ChatMessageType.LOGIN);  //消息类型为登录
        /**
         *  4. 使用登陆界面持有的socket底层的输出流（序列化流），
         *  将刚刚封装好的消息对象发送出去
         */
        try {
            out.writeObject(loginMessage);
            out.flush();
        }catch (IOException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("消息发送失败，请检查网络！");
            alert.show();
        }

        //5. 消息发送出去之后，就是用当前Socket的输入流读取服务器回发的登陆结果的消息
        try {
            ChatMessage loginResult = (ChatMessage) in.readObject();
            Toast toast = new Toast(loginBtn.getScene().getWindow());
            Toast.Level level = Toast.Level.values()[2]; //提示级别
            if (loginResult.getFrom() != null) { //可以登录
                    user = loginResult.getFrom();
                    ControlCollections.controls.put(getClass(), this);
                    //打开主窗口，并隐藏当前登录窗口
                    Main m = new Main();
                    m.start(new Stage());
                    loginBtn.getScene().getWindow().hide();  //隐藏当前登录窗口
                    System.out.println(user.getNickname() + "登陆成功！");
            } else {
                //用户为null的两种情况：用户名或密码错误，用户在线
                if(loginResult.getContent().equals("online")){
                    //该用户已在线，不可重复登录
                    toast.show(level, 1000, "您已经登录，请勿重复登录！");
                }else{
                    toast.show(level, 1000, "用户名或密码错误！");
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
         catch (ClassNotFoundException e1) {
             e1.printStackTrace();
        }
    }

    //注册
    @FXML
    public void register(){
        //用户注册时，需要使用自己的输入、输出流
        ControlCollections.controls.put(getClass(),this);
        Stage stage = new Stage();
        Register r = new Register();
        r.start(stage);
        registerBtn.getScene().getWindow().hide();
    }
}
