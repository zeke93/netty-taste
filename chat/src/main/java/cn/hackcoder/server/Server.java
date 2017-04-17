package cn.hackcoder.server;

import cn.hackcoder.client.handler.ChatClientHandler;
import cn.hackcoder.server.handler.ChatServerHandler;
import cn.hackcoder.server.init.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by thinsky on 2017/4/9.
 */
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        EventLoopGroup acceptor = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(acceptor, worker)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer());

        try {
            Channel channel = bootstrap.bind(port).sync().channel();
            logger.info("server start running in port:{}", port);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptor.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
            }
        }

        new Server(port).run();

    }
}
