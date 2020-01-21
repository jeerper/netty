package com.summit.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;

public class ClientHandler extends ChannelHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception {
        try{
            ByteBuf readBuffer=(ByteBuf)msg;
            byte[] tempDatas=new byte[readBuffer.readableBytes()];
            readBuffer.readBytes(tempDatas);
            System.out.println("from server : " + new String(tempDatas, "UTF-8"));
        }finally {
            // 用于释放缓存。避免内存溢出
            ReferenceCountUtil.release(msg);
        }
    }
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        System.out.println("client exceptionCaught method run...");
        ctx.close();
    }
}
