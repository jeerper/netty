package com.summit.netty;

import io.netty.channel.ChannelFuture;

public class NettyServer {
    public static void main(String[] args) {
        ChannelFuture future=null;
        Server server=null;
        try{
            server=new Server();
            future=server.doAccept(9999,new ServerHandler());
            System.out.println("server started");
            // 关闭连接的。
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (null != future){
                try{
                    future.channel().closeFuture().sync();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            if (null != server){
                server.release();
            }
        }
    }
}
