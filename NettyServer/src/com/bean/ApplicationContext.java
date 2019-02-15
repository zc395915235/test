package com.bean;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 

public class ApplicationContext {
    public static Map<ChannelId,ChannelHandlerContext> onlineUsers = new ConcurrentHashMap<>();
    public static void add(ChannelId uid,ChannelHandlerContext ctx){
        onlineUsers.put(uid,ctx);
    }
 
    public static void remove(ChannelId uid){
        onlineUsers.remove(uid);
    }
 
    public static ChannelHandlerContext getContext(Integer uid){
        return onlineUsers.get(uid);
    }
    
    
}