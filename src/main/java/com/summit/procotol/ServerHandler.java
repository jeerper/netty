package com.summit.procotol;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

public class ServerHandler extends ChannelHandlerAdapter {
    // 业务处理逻辑
    public void channelRead(ChannelHandlerContext ctx, Object msg ) throws UnsupportedEncodingException {
        String message = msg.toString();
        System.out.println("服务器接收到的消息是 : " + message);
        message = ProtocolParser.parse(message);
        if (null==message){
            System.out.println("客户端响应出错 ");
            return;
        }
        System.out.println("来自客户端 :"+message);
        String line ="server message ";
        line = ClientHandler.ProtocolParser.transferTo(line);
        System.out.println("服务器发送的消息是 : " + line);
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
    }
    // 异常处理逻辑
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        System.out.println("server exceptionCaught method run...");
        cause.printStackTrace();
        ctx.close();
    }
    static class ProtocolParser{
        public static String parse(String message){
            String[] temp = message.split("HEADBODY");
            temp[0] = temp[0].substring(4);
            temp[1] = temp[1].substring(0, (temp[1].length()-4));
            int length = Integer.parseInt(temp[0].substring(temp[0].indexOf(":")+1));
            if(length != temp[1].length()){
                return null;
            }
            return temp[1];
        }
        public static String transferTo(String message){
            message = "HEADcontent-length:" + message.length() + "HEADBODY" + message + "BODY";
            return message;
        }
    }
}
