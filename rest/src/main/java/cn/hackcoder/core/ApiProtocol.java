package cn.hackcoder.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.CharsetUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * convert the netty HttpRequest object to a api protocol object
 */
@Data
public class ApiProtocol {

    private static final Logger logger = LoggerFactory.getLogger(ApiProtocol.class);

    private String version = "1.0";
    private String clientIP;
    private String serverIP;
    private String api;
    private String endpoint;
    private HttpMethod method;
    private Map<String, List<String>> parameters = Maps.newHashMap(); // get 和 post 的键值对都存储在这里
    private String postBody; // post 请求时的非键值对内容


    private void addParameter(String key, String param) {
        List<String> params = Lists.newArrayList(param);
        this.parameters.put(key, params);
    }

    public ApiProtocol(ChannelHandlerContext ctx, HttpRequest req) {

        String uri = req.uri();
        if (uri.length() <= 0) {
            return;
        }

        logger.info(uri);
        this.method = req.method();

        parseEndpoint(uri);
        setIp(ctx, req);
        queryStringHandler(uri);
        requestParametersHandler(req);
        requestBodyHandler(req);

        if (this.parameters.size() > 0) {
            setFields();
        }
    }

    private void parseEndpoint(String uri) {
        String endpoint = uri.split("\\?")[0];
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length());
        }
        this.endpoint = endpoint;

        Set<Map.Entry<String, Api>> set = ApiRoute.apiMap.entrySet();

        for (Map.Entry<String, Api> entry : set) {
            Api api = entry.getValue();
            Pattern pattern = Pattern.compile("^" + api.getRegex() + "$");
            Matcher matcher = pattern.matcher(endpoint);
            if (matcher.find()) {
                this.api = api.getName();
                if (matcher.groupCount() > 0) {
                    for (int i = 0; i < matcher.groupCount(); i++) {
                        addParameter(api.getParameterNames().get(i), matcher.group(i + 1));
                    }
                }
                break;
            }
        }

    }


    public static void main(String[] args) {
        String uri = "/api";
        String endpoint = uri.split("\\?")[0];
        System.out.println(endpoint);
    }

    private void setIp(ChannelHandlerContext ctx, HttpRequest req) {
        String clientIP = req.headers().get("X-Forwarded-For");
        if (clientIP == null) {
            InetSocketAddress remoteSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIP = remoteSocket.getAddress().getHostAddress();
        }
        this.clientIP = clientIP;

        InetSocketAddress serverSocket = (InetSocketAddress) ctx.channel().localAddress();
        this.serverIP = serverSocket.getAddress().getHostAddress();
    }

    /**
     * set this class field's value by {@link #parameters}
     * <p>
     * the code improved log in my blog <a href="http://mengkang.net/614.html">http://mengkang.net/614.html</>
     */
    private void setFields() {
        Field[] fields = this.getClass().getDeclaredFields();

        for (int i = 0, length = fields.length; i < length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();

            if (fieldName.equals("logger")
                    || fieldName.equals("method")
                    || fieldName.equals("parameters")
                    || fieldName.equals("postBody")) {
                continue;
            }

            if (!this.parameters.containsKey(fieldName)) {
                continue;
            }

            Class fieldType = field.getType();
            field.setAccessible(true);
            try {
                if (fieldType == int.class) {
                    field.set(this, Integer.parseInt(this.parameters.get(fieldName).get(0)));
                } else {
                    field.set(this, this.parameters.get(fieldName).get(0));
                }
            } catch (NumberFormatException | IllegalAccessException e) {
                logger.error("field set error", e);
            }

            this.parameters.remove(fieldName);
        }
    }

    /**
     * queryString encode, put all the query key value in {@link #parameters}
     * <p>
     * use netty's {@link QueryStringDecoder} replace my bad encode method <a href="http://mengkang.net/587.html">http://mengkang.net/587.html</a>
     *
     * @param uri
     */
    private void queryStringHandler(String uri) {

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        if (queryStringDecoder.parameters().size() > 0) {
            this.parameters.putAll(queryStringDecoder.parameters());
        }
    }

    /**
     * request parameters put in {@link #parameters}
     *
     * @param req
     */
    private void requestParametersHandler(HttpRequest req) {
        if (req.method().equals(HttpMethod.POST)) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(req);
            try {
                List<InterfaceHttpData> postList = decoder.getBodyHttpDatas();
                for (InterfaceHttpData data : postList) {
                    List<String> values = new ArrayList<String>();
                    MixedAttribute value = (MixedAttribute) data;
                    value.setCharset(CharsetUtil.UTF_8);
                    values.add(value.getValue());
                    this.parameters.put(data.getName(), values);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * request body put in {@link #postBody}
     *
     * @param msg
     */
    private void requestBodyHandler(Object msg) {
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            StringBuilder buf = new StringBuilder();
            buf.append(content.toString(CharsetUtil.UTF_8));
            this.postBody = buf.toString();
        }
    }

}
