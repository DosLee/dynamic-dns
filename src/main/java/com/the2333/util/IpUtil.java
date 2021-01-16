package com.the2333.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述:
 * ip工具类
 *
 * @author lil‘s
 * @create 2021-01-16 15:22
 */
@Slf4j
public class IpUtil {

    /**
     * 获取当前主机IP地址, 使用第三方工具 https://jsonip.com/
     *
     * @return ip
     */
    public static String getCurrentHostIp() {
        String jsonip = "https://jsonip.com/";
        // 接口返回结果
        StringBuilder result = new StringBuilder();
        String res = "";
        try {
            // 使用HttpURLConnection网络请求第三方接口
            URL url = new URL(jsonip);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            log.error("获取主机IP错误", e);
            return res;
        }
        // 正则表达式, 提取xxx.xxx.xxx.xxx, 将IP地址从接口返回结果中提取出来
        String rexp = "(\\d{1,3}\\.){3}\\d{1,3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(result.toString());
        if (mat.find()) {
            res = mat.group();
        }
        return res;
    }
}
