package com.summit.procotol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;


public class Client {
    //处理请求和处理服务端响应的线程组
    private EventLoopGroup group=null;
    //服务启动相关的配置信息
    private Bootstrap bootstrap=null;

    public Client() {
        init();
    }
    private  void init(){
        group=new NioEventLoopGroup();
        bootstrap=new Bootstrap();
        //绑定线程组
        bootstrap.group(group);
        //设置通讯模式为NIO
        bootstrap.channel(NioSocketChannel.class);
    }
    public ChannelFuture doRequest(String host, int port, final ChannelHandler... handlers) throws InterruptedException {
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                ch.pipeline().addLast(handlers);
            }
        });
        ChannelFuture future = this.bootstrap.connect(host, port).sync();
        return future;
    }
    public void release(){
        this.group.shutdownGracefully();
    }
}
