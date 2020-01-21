package com.summit.fixedLength;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    public static void main(String[] args) {
        Client client=null;
        ChannelFuture future=null;
        try{
            client=new Client();
            future= client.doRequest("localhost", 9999);
            Scanner s=null;
            while (true){
                s=new Scanner(System.in);
                System.out.println("enter message send to server >");
                String line = s.nextLine();
                byte[] bs=new byte[5];
                byte[] temp = line.getBytes("UTF-8");
                if (temp.length<=5){
                    for(int i=0;i<temp.length;i++){
                        bs[i]=temp[i];
                    }
                }
                future.channel().writeAndFlush(Unpooled.copiedBuffer(bs));
                TimeUnit.SECONDS.sleep(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (null !=future){
                try{
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (null != client){
                client.realease();
            }
        }
    }
}
