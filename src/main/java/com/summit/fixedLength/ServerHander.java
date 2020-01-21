package com.summit.fixedLength;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

public class ServerHander extends ChannelHandlerAdapter {
    // 业务处理逻辑
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws UnsupportedEncodingException {
        String message = msg.toString();
        System.out.println("from client: "+message.trim());
        String line="ok ";
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
    }
    //异常处理逻辑
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        System.out.println("server exceptionCaught method run...");
       // cause.printStackTrace();
        ctx.close();
    }
}
