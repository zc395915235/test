package com.lll.game;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bean.ApplicationContext;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	private static Log log = LogFactory.getLog(ServerHandler.class);

	private ChannelHandlerContext ctx;

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		ApplicationContext.add(ctx.channel().id(), ctx);
		System.out.println(ctx.channel().id() + "进来了");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		ApplicationContext.remove(ctx.channel().id());
		System.out.println(ctx.channel().id() + "离开了");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
		super.channelActive(ctx);
		System.out.println("有客户端连接：" + ctx.channel().remoteAddress().toString());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");

		for (Map.Entry<ChannelId,ChannelHandlerContext> entry : ApplicationContext.onlineUsers.entrySet()) {
			
			ChannelHandlerContext c = entry.getValue();
			//if(c == ctx) continue;
			SubscribeReq resq = new SubscribeReq();

			log.info("向"+c.channel().id()+"发送"+ body );
			ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
			c.writeAndFlush(resp);
		}		
		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
        System.out.println("与客户端断开连接:"+cause.getMessage());
        cause.printStackTrace();
		ctx.close();
	}
}
