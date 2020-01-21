package com.summit.procotol;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        try{
            String message = msg.toString();
            System.out.println("客户端接收到服务器的消息是 : " +message);
            message=ProtocolParser.parse(message);
            if(null == message){
                System.out.println("服务器响应出错");
                return ;
            }
            System.out.println("来自服务器 : " + message);
        }catch (Exception e){
        // 用于释放缓存。避免内存溢出
            ReferenceCountUtil.release(msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client exceptionCaught method run...");
        // cause.printStackTrace();
        ctx.close();
    }
    public  static class ProtocolParser{
        public static String parse(String meaasge){
            String[] temp=meaasge.split("HEADBODY");
            temp[0] =temp[0].substring(4);
            temp[1] =temp[1].substring(0,(temp[1].length()-4));
            int length = Integer.parseInt(temp[0].substring(temp[0].indexOf(":")+1));
            if (length !=temp[1].length()){
                return null;
            }
            return temp[1];
        }
        public static String transferTo(String message){
            message="HEADcontent-length:" + message.length() + " HEADBODY " + message + "BODY";
            return message;
        }

    }
}

