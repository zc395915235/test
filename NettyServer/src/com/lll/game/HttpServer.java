package com.lll.game;
 
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import com.Impl.ServerConfigImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture; 
import io.netty.channel.ChannelInitializer; 
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup; 
import io.netty.channel.nio.NioEventLoopGroup; 
import io.netty.channel.socket.SocketChannel; 
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;



public class HttpServer implements ServerConfigImpl {
    private static Log log = LogFactory.getLog(HttpServer.class);
    
    public static void main(String[] args) throws Exception {
        HttpServer server = new HttpServer();
        log.info("服务已启动...");
        server.start(SERVER_PORT);
    }
    
    public void start(int port) throws Exception {
    	//配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel e) throws Exception {
//                                   //字符串类解析
                            		//e.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            		//设置解码为UTF-8
                            		//e.pipeline().addLast(new StringDecoder(Charset.forName("utf-8")));
                            		//设置编码为UTF-8
                            		e.pipeline().addLast(new StringEncoder(Charset.forName("utf-8")));
                                	e.pipeline().addLast(new ServerHandler());
                                }
                            }).option(ChannelOption.SO_BACKLOG, 128) //最大客户端连接数为128
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //绑定端口，同步等待成功
            System.out.println(SERVER_HOST);
            ChannelFuture f = b.bind(SERVER_HOST,port).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
        	//优雅退出，释放线程池资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}