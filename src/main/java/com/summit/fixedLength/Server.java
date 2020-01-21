package com.summit.fixedLength;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

public class Server {
    //监听线程组，监听客户请求
    private EventLoopGroup acceptorGroup=null;
    //处理客户端相关的线程组，负责处理与客户端的数据通讯
    private EventLoopGroup clientGroup=null;
    //服务启动相关配置信息
    private ServerBootstrap bootstrap=null;

    public Server() {
        init();
    }
    public void init(){
        acceptorGroup=new NioEventLoopGroup();
        bootstrap= new ServerBootstrap();
        clientGroup=new NioEventLoopGroup();
        //绑定线程组
        bootstrap.group(acceptorGroup,clientGroup);
        //设定通讯模式为NIO
        bootstrap.channel(NioServerSocketChannel.class);
        //设定缓冲区大小
        bootstrap.option(ChannelOption.SO_BACKLOG,16*1024)
        // SO_SNDBUF发送缓冲区，SO_RCVBUF接收缓冲区，SO_KEEPALIVE开启心跳监测（保证连接有效）
                .option(ChannelOption.SO_SNDBUF,16*1024)
                .option(ChannelOption.SO_RCVBUF,16*1024)
                .option(ChannelOption.SO_KEEPALIVE,true);
    }
    public ChannelFuture doAccept(int port) throws InterruptedException {
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelHandler[] acceptorHandlers=new ChannelHandler[3];
                // 定长Handler。通过构造参数设置消息长度（单位是字节）。发送的消息长度不足可以使用空格补全。
                acceptorHandlers[0]=new FixedLengthFrameDecoder(3);
                // 字符串解码器Handler，会自动处理channelRead方法的msg参数，将ByteBuf类型的数据转换为字符串对象
                acceptorHandlers[1]=new StringDecoder(Charset.forName("UTF-8"));
                acceptorHandlers[2]=new ServerHander();
                ch.pipeline().addLast(acceptorHandlers);
            }
        });
        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }

    public void release(){
        this.acceptorGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }
}
