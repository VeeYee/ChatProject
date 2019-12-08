package com.oracle.chatproject.client.model;

import lombok.*;

import java.io.Serializable;

/**
 * 封装一个消息类型
 * 因为本次聊天软件中的任何业务请求都是通过发送标准的消息对象
 * （类似快递必须封装成标准的快递包裹）
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatMessage implements Serializable {
    private ChatUser from;
    private ChatUser to;
    private ChatGroup group;
    private String content;
    private String time;
    private ChatMessageType type;
}
