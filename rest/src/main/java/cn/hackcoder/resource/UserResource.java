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
public class UserResource {

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    private ApiProtocol apiProtocol;

    private static final Map<Integer, User> users = Maps.newConcurrentMap();

    public UserResource(ApiProtocol apiProtocol) {
        this.apiProtocol = apiProtocol;
    }

    public Result get() {
        Integer uid = Integer.parseInt(apiProtocol.getParameters().get("uid").get(0));
        return users.get(uid) == null ? Result.fail("未查找到用户信息") : Result.success(users.get(uid));
    }

    public Result post() {
        String body = apiProtocol.getPostBody();
        User user = JsonUtil.fromJson(body, User.class);
        if (user != null && user.getId() != null) {
            users.put(user.getId(), user);
            return Result.success("新建成功");
        } else {
            return Result.fail("数据格式有误");
        }

    }

    public Result delete() {
        Integer uid = Integer.parseInt(apiProtocol.getParameters().get("uid").get(0));
        users.remove(uid);
        return Result.success("删除成功");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        private Integer id;
        private String name;
        private String mobile;

    }
}
