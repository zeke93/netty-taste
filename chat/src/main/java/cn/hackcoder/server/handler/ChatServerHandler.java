package cn.hackcoder.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by thinsky on 2017/4/9.
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    private static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        group.forEach(ch -> {
            if (ch == channel) {
                ch.writeAndFlush(String.format("[you]: %s\n", msg));
            } else {
                ch.writeAndFlush(String.format("[%s]: %s \n", channel.remoteAddress(), msg));
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        group.forEach(ch -> {
            ch.writeAndFlush(String.format("[%s]: " + "is comming \n", channel.remoteAddress()));

        });
        group.add(channel);
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        group.forEach(ch -> {
            ch.writeAndFlush(String.format("[%s]: " + "is leaving \n", channel.remoteAddress()));

        });
        group.remove(channel);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("[{}] online", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("[{}] offline", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("[{}] exit the room!", ctx.channel().remoteAddress());
        ctx.close().sync();
    }
}

