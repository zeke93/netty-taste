package cn.hackcoder.server;

import cn.hackcoder.server.init.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by linzhichao on 2017/4/17.
 */
public class FileServer {

    private static final Logger logger = LoggerFactory.getLogger(FileServer.class);
    private int port;
    private String url;

    public FileServer(int port, String url) {
        this.port = port;
        this.url = url;
    }

    public void run() {
        EventLoopGroup acceptor = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(acceptor, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerInitializer());

        try {
            ChannelFuture cf = bootstrap.bind("127.0.0.1", port).sync();
            logger.info("Http文件目录服务器启动：http://127.0.0.1:" + port + url);
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptor.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new FileServer(8080, "/").run();
    }
}
