package com.summit.fixedLength;

import io.netty.channel.ChannelFuture;

public class NettyServer {
    public static void main(String[] args) {
        ChannelFuture future=null;
        Server server=null;
        try{
            server=new Server();
            future=server.doAccept(9999);
            System.out.println("server started...");
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (null != future){
                try{
                    future.channel().closeFuture().sync();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (null != server){
                server.release();
            }
        }

    }
}
