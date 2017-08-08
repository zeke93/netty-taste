package cn.hackcoder.resource;

import cn.hackcoder.core.ApiProtocol;
import cn.hackcoder.core.Result;
import cn.hackcoder.utils.ConfigUtil;
import cn.hackcoder.utils.JsonUtil;
import com.google.common.collect.Maps;
import com.sun.tools.internal.jxc.gen.config.Config;
import jdk.nashorn.internal.ir.RuntimeNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by linzhichao on 2017/4/19.
 */
public class TokenResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenResource.class);

    private ApiProtocol apiProtocol;

    private static HttpClient httpClient;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(ConfigUtil.getInt("httpclient.connection.poolsize"));
        cm.setDefaultMaxPerRoute(ConfigUtil.getInt("httpclient.connection.poolsize.perroute"));
        httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }

    public TokenResource(ApiProtocol apiProtocol) {
        this.apiProtocol = apiProtocol;
    }

    public Result get() {
        String param = apiProtocol.getParameters().get("param").get(0);
        StringEntity entity = new StringEntity(param, ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8));
        HttpPost httpPost = new HttpPost("http://localhost:" + ConfigUtil.getString("android.server.port"));
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).build();
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(entity);
        String content = null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            LOGGER.info("Get Http Content {} By {}.", content, param);
        } catch (Exception e) {
            LOGGER.error("请求异常,param:{}", param, e);
        }
        return Result.success(content);
    }


}
