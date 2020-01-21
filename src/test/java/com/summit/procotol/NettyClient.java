package com.summit.procotol;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    public static void main(String[] args) {
        ChannelFuture future=null;
        Client client=null;
        try{
            client=new Client();
            future= client.doRequest("localhost", 9999,new ClientHandler());
            Scanner s=null;
            while (true){
                s=new Scanner(System.in);
                System.out.println("请输入要发送的内容：");
                String line = s.nextLine();
                line= ClientHandler.ProtocolParser.transferTo(line);
                System.out.println("客户端发送的内容是 : " + line);
                future.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes("UTF-8")));
                TimeUnit.SECONDS.sleep(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (null !=future){
                try{
                    future.channel().closeFuture().sync();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (null !=client){
                client.release();
            }
        }
    }
}
