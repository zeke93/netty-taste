package cn.hackcoder.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by thinsky on 2017/4/9.
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info("connection is active");
    }

    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("{}", msg);
    }
}
