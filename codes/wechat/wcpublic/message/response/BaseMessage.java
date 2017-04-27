package com.nuoshenggufen.e_treasure.main.web.wcpublic.message.response;

public class BaseMessage {
    // 接收方帐号（收到的OpenID）
    protected String ToUserName;
    // 开发者微信号
    protected String FromUserName;
    // 消息创建时间 （整型）
    protected long CreateTime;
    // 消息类型（text/music/news）
    protected String MsgType;

    // 位0x0001被标志时，星标刚收到的消息
    // private int FuncFlag;
    public String getToUserName ( ) {
        return ToUserName;
    }

    public void setToUserName ( String toUserName ) {
        ToUserName = toUserName;
    }

    public String getFromUserName ( ) {
        return FromUserName;
    }

    public void setFromUserName ( String fromUserName ) {
        FromUserName = fromUserName;
    }

    public long getCreateTime ( ) {
        return CreateTime;
    }

    public void setCreateTime ( long createTime ) {
        CreateTime = createTime;
    }

    public String getMsgType ( ) {
        return MsgType;
    }

    public void setMsgType ( String msgType ) {
        MsgType = msgType;
    }
}
