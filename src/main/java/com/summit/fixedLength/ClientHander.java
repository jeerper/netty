package com.summit.fixedLength;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class ClientHander extends ChannelHandlerAdapter  {
    public void  channelRead(ChannelHandlerContext ctx,Object msg){
        try{
            String message = msg.toString();
            System.out.println("from server: "+message);
        }catch (Exception e){
            //用户释放内存，避免内存溢出
            ReferenceCountUtil.release(msg);
        }
    }
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        System.out.println("client exceptionCaught method run ...");
        cause.printStackTrace();
        ctx.close();
    }

}
