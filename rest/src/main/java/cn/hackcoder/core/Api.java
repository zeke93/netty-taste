package cn.hackcoder.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * api mapping object
 */
@Data
@NoArgsConstructor
public class Api {

    private String name;  // endpoint
    private String regex;
    private List<String> parameterNames = Lists.newArrayList();
    private Set<String> httpMethod = Sets.newHashSet();
    private String resource;

    public void setName(String name) {
        this.name = name;

        String[] strings = name.split("/");
        StringBuilder sb = new StringBuilder();

        for (int i = 0, len = strings.length; i < len; i++) {
            if (strings[i].length() == 0) {
                continue;
            }
            sb.append("/");
            if (strings[i].startsWith(":")) {
                parameterNames.add(strings[i].substring(1));
                sb.append("([^/]+)");
            } else {
                sb.append(strings[i]);
            }
        }

        this.regex = sb.toString();
    }


    public void addHttpMethod(String httpMethod) {
        this.httpMethod.add(httpMethod);
    }

}
