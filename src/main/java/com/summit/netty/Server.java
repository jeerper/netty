package com.summit.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    // 监听线程组，监听客户端请求
    private EventLoopGroup acceptorGroup=null;
    // 处理客户端相关操作线程组，负责处理与客户端的数据通讯
    private EventLoopGroup clientGroup=null;
    // 服务启动相关配置信息
    private ServerBootstrap bootstrap=null;

    public Server() {
        init();
    }
    public void init(){
        // 初始化线程组,构建线程组的时候，如果不传递参数，则默认构建的线程组线程数是CPU核心数量。
        acceptorGroup=new NioEventLoopGroup();
        clientGroup=new NioEventLoopGroup();
        // 初始化服务的配置
        bootstrap=new ServerBootstrap();
        // 绑定线程组
        bootstrap.group(acceptorGroup,clientGroup);
        // 设定通讯模式为NIO， 同步非阻塞
        bootstrap.channel(NioServerSocketChannel.class);
        // 设定缓冲区大小， 缓存区的单位是字节。
        bootstrap.option(ChannelOption.SO_BACKLOG,1024);
        // SO_SNDBUF发送缓冲区，SO_RCVBUF接收缓冲区，SO_KEEPALIVE开启心跳监测（保证连接有效）
        bootstrap.option(ChannelOption.SO_SNDBUF,16*1024)
                .option(ChannelOption.SO_RCVBUF,16*1024)
                .option(ChannelOption.SO_KEEPALIVE,true);
    }
    public ChannelFuture doAccept(int port, final ChannelHandler... acceptorHandlers) throws InterruptedException {
        /**
         * childHandler是服务的Bootstrap独有的方法。是用于提供处理对象的。
         * 可以一次性增加若干个处理逻辑。是类似责任链模式的处理方式。
         * 增加A，B两个处理逻辑，在处理客户端请求数据的时候，根据A-》B顺序依次处理。
         *
         * ChannelInitializer - 用于提供处理器的一个模型对象。
         * 其中定义了一个方法，initChannel方法。
         * 方法是用于初始化处理逻辑责任链条的。
         * 可以保证服务端的Bootstrap只初始化一次处理器，尽量提供处理逻辑的重用。
         * 避免反复的创建处理器对象。节约资源开销。
         */
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sch) throws Exception {
                sch.pipeline().addLast(acceptorHandlers);
            }
        });
        // bind方法 - 绑定监听端口的。ServerBootstrap可以绑定多个监听端口。 多次调用bind方法即可
        // sync - 开始监听逻辑。 返回一个ChannelFuture。 返回结果代表的是监听成功后的一个对应的未来结果
        // 可以使用ChannelFuture实现后续的服务器和客户端的交互。
        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }
    /**
     * shutdownGracefully - 方法是一个安全关闭的方法。可以保证不放弃任何一个已接收的客户端请求。
     */
    public void release(){
        this.acceptorGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }

}
