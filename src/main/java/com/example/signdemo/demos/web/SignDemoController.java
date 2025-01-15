package com.example.signdemo.demos.web;

import com.google.gson.reflect.TypeToken;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import com.example.signdemo.demos.utils.SignHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 签名示例
 */
@Controller
public class SignDemoController {
    /**
     * 测试访问
     * @return 网页
     */
    @RequestMapping("/")
    public String html() {
        return "index.html";
    }

    /**
     * 请求大会员 - get请求
     *
     * @return 结果
     */
    @GetMapping("/request/hti-member/get")
    @ResponseBody
    public String RequestHtiMemberGet() {
        Gson gson = new Gson();
        RestTemplate restTemplate = new RestTemplate();

        // 参数声明
        String version = "1";
        String appId = "请填写APPID";
        String secret = "请填写密钥";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String signatureNonce = UUID.randomUUID().toString();
        String signature;

        // get请求参数
        Map<String, String> params = new HashMap<>();
        params.put("id", "12312");

        // 签名参数
        Map<String, String> signParams = new TreeMap<>();
        signParams.put("app-id", appId);
        signParams.put("timestamp", timestamp);
        signParams.put("signature-nonce", signatureNonce);
        signParams.put("params", gson.toJson(params));

        try {
            // 计算签名
            signature = SignHelper.sign(signParams, secret);
        } catch (Exception e) {
            return "签名生成失败: " + e.getMessage();
        }

        // 请求头参数
        Map<String, String> headers = new HashMap<>();
        headers.put("version", version);
        headers.put("app-id", appId);
        headers.put("timestamp", timestamp);
        headers.put("signature", signature);
        headers.put("signature-nonce", signatureNonce);
        headers.put("Content-Type", "application/json");

        try {
            String url = UriComponentsBuilder.fromHttpUrl("请填写请求地址").queryParam("id", appId).toUriString();

            HttpEntity<String> requestEntity = new HttpEntity<>(createHeaders(headers));

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            return responseEntity.getBody();
        } catch (Exception e) {
            return "请求失败: " + e.getMessage();
        }
    }

    /**
     * 请求大会员 - post请求 - 简单json请求
     *
     * @return 结果
     */
    @PostMapping("/request/hti-member/post-json")
    @ResponseBody
    public String RequestHtiMemberPostJson() {
        Gson gson = new Gson();
        RestTemplate restTemplate = new RestTemplate();

        // 参数声明
        String version = "1";
        String appId = "请填写APPID";
        String secret = "请填写密钥";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String signatureNonce = UUID.randomUUID().toString();
        String signature;

        String idCard = "460006199999999999";

        // 请求参数
        Map<String, String> params = new HashMap<>();
        params.put("id_card", idCard);

        // 签名参数
        Map<String, String> signParams = new TreeMap<>();
        signParams.put("app-id", appId);
        signParams.put("timestamp", timestamp);
        signParams.put("signature-nonce", signatureNonce);
        signParams.put("params", gson.toJson(params));

        try {
            // 计算签名
            signature = SignHelper.sign(signParams, secret);
        } catch (Exception e) {
            return "签名生成失败: " + e.getMessage();
        }

        // 请求头参数
        Map<String, String> headers = new HashMap<>();
        headers.put("version", version);
        headers.put("app-id", appId);
        headers.put("timestamp", timestamp);
        headers.put("signature", signature);
        headers.put("signature-nonce", signatureNonce);
        headers.put("Content-Type", "application/json");

        try {
            String requestBody = gson.toJson(params);

            return restTemplate.postForObject("请填写请求地址", new HttpEntity<>(requestBody, createHeaders(headers)), String.class);
        } catch (Exception e) {
            return "请求失败: " + e.getMessage();
        }
    }

    /**
     * 请求大会员 - post请求 - 复杂数组请求
     *
     * @return 结果
     */
    @PostMapping("/request/hti-member/post-array")
    @ResponseBody
    public String RequestHtiMemberPostArray() {
        Gson gson = new Gson();
        RestTemplate restTemplate = new RestTemplate();

        // 参数声明
        String version = "1";
        String appId = "请填写APPID";
        String secret = "请填写密钥";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String signatureNonce = UUID.randomUUID().toString();
        String signature;

        // 请求参数
        List<Map<String, Object>> requestBodyList;
        try {
            requestBodyList = readJsonFile();
        } catch (IOException e) {
            return "读取JSON文件失败: " + e.getMessage();
        }

        // 签名参数
        Map<String, String> signParams = new LinkedHashMap<>(); // 使用 TreeMap 确保参数按字典顺序排列
        signParams.put("app-id", appId);
        signParams.put("timestamp", timestamp);
        signParams.put("signature-nonce", signatureNonce);
        signParams.put("params", gson.toJson(requestBodyList));

        try {
            // 计算签名
            signature = SignHelper.sign(signParams, secret);
        } catch (Exception e) {
            return "签名生成失败: " + e.getMessage();
        }

        // 请求头参数
        Map<String, String> headers = new HashMap<>();
        headers.put("version", version);
        headers.put("app-id", appId);
        headers.put("timestamp", timestamp);
        headers.put("signature", signature);
        headers.put("signature-nonce", signatureNonce);
        headers.put("Content-Type", "application/json");

        try {
            String requestBody = gson.toJson(requestBodyList);

            return restTemplate.postForObject("请填写请求地址", new HttpEntity<>(requestBody, createHeaders(headers)), String.class);
        } catch (Exception e) {
            return "请求失败: " + e.getMessage();
        }
    }

    /**
     * 创建请求头
     *
     * @param headers 请求头map
     *
     * @return HttpHeaders
     */
    private HttpHeaders createHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }

        return httpHeaders;
    }

    /**
     * 读取并解析 JSON 文件
     *
     * @return 解析后的 JSON 数据
     * @throws IOException 如果文件读取失败
     */
    private List<Map<String, Object>> readJsonFile() throws IOException {
        Resource resource = new ClassPathResource("member-data.json");

        String jsonContent = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()), StandardCharsets.UTF_8);

        return new Gson().fromJson(
                jsonContent,
                new TypeToken<List<Map<String, Object>>>() {}.getType()
        );
    }
}
