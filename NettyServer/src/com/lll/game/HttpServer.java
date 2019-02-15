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
        log.info("����������...");
        server.start(SERVER_PORT);
    }
    
    public void start(int port) throws Exception {
    	//���÷���˵�NIO�߳���
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel e) throws Exception {
//                                   //�ַ��������
                            		//e.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            		//���ý���ΪUTF-8
                            		//e.pipeline().addLast(new StringDecoder(Charset.forName("utf-8")));
                            		//���ñ���ΪUTF-8
                            		e.pipeline().addLast(new StringEncoder(Charset.forName("utf-8")));
                                	e.pipeline().addLast(new ServerHandler());
                                }
                            }).option(ChannelOption.SO_BACKLOG, 128) //���ͻ���������Ϊ128
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //�󶨶˿ڣ�ͬ���ȴ��ɹ�
            System.out.println(SERVER_HOST);
            ChannelFuture f = b.bind(SERVER_HOST,port).sync();
            //�ȴ�����˼����˿ڹر�
            f.channel().closeFuture().sync();
        } finally {
        	//�����˳����ͷ��̳߳���Դ
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}