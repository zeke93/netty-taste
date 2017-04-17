package cn.hackcoder.client;

import cn.hackcoder.client.init.ClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by thinsky on 2017/4/9.
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private String host;
    private int port;
    private boolean stop;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        EventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer());

        try {
            Channel channel = bootstrap.connect(host, port).sync().channel();

            while (true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String input;
                while ((input = reader.readLine()) != null) {
                    if ("quit".equals(input)) {
                        System.exit(1);
                    }
                    channel.writeAndFlush(input);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8099;
        if (args != null && args.length >= 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        logger.info("start connection server\n");
        new Client(host, port).run();
    }
}
