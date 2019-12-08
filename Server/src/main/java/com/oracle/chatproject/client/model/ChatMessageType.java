package com.oracle.chatproject.client.model;

import java.io.Serializable;

public enum ChatMessageType implements Serializable {
    LOGIN,
    LOGOUT,
    REGISTER,
    TEXT,
    SHAKE,
    FINDUSER,
    ADDFRIEND,
    ACCEPTFRIEND,
    CREATEGROUP,
    JOINGROUP,
    GROUPTEXT
}
