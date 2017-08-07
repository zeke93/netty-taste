package cn.hackcoder.resource;

import cn.hackcoder.core.ApiProtocol;
import cn.hackcoder.core.Result;
import cn.hackcoder.utils.JsonUtil;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by linzhichao on 2017/4/19.
 */
public class TokenResource {

    private static final Logger logger = LoggerFactory.getLogger(TokenResource.class);

    private ApiProtocol apiProtocol;


    public TokenResource(ApiProtocol apiProtocol) {
        this.apiProtocol = apiProtocol;
    }

    public Result get() {
        String param = apiProtocol.getParameters().get("param").get(0);
        return Result.success(param);
    }


}
