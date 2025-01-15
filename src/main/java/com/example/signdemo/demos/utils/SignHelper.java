package com.example.signdemo.demos.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 签名工具类
 */
public class SignHelper {

    /**
     * 签名
     *
     * @param params 参数
     * @param secret 密钥
     * @return 签名
     */
    public static String sign(Map<String, String> params, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        // 通过TreeMap排序
        Map<String, String> sortedParams = new TreeMap<>(params);

        // 构建参数字符串
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // builder转string
        String stringToSign = stringBuilder.toString();
        // url解码
        stringToSign = urlDecode(stringToSign);
        // url编码
        stringToSign = urlEncode(stringToSign);

        // 计算签名
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hash = sha256Hmac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));

        // 计算完成，返回Base64编码签名
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 验证签名
     *
     * @param params 签名参数map
     * @param secret 密钥
     * @return 签名是否正确
     */
    public static boolean verifySign(Map<String, String> params, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        if (!params.containsKey("signature")) {
            throw new IllegalArgumentException("缺少signature字段");
        }

        String waitSign = params.get("signature");
        String sign = sign(params, secret);

        return sign.equals(waitSign);
    }

    /**
     * URL解码（utf-8）
     *
     * @param str 解码字符串
     * @return 解码结果
     */
    private static String urlDecode(String str) {
        try {
            return java.net.URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("URL解码失败", e);
        }
    }

    /**
     * URL编码（utf-8）
     *
     * @param str 编码字符串
     * @return 编码结果
     */
    private static String urlEncode(String str) {
        try {
            // 标准URL编码，java编码中' '会被编码为'+'，这里改为 '%20'
            return java.net.URLEncoder.encode(str, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            throw new RuntimeException("URL编码失败", e);
        }
    }
}
