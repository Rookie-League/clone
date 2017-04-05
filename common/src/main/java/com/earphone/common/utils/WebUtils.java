package com.earphone.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletResponse;

import com.earphone.common.constant.Charset;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description
 * @author yaojiamin
 * @createTime 2016-4-8 上午11:18:22
 */
public class WebUtils {
    private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);

    public static String get(String url, Map<String, String> headers) throws Exception {
        logger.info("GetURL={}", new Object[]{url});
        trustAllHosts();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        setConnectionAttribute(connection, "GET", headers);
        connection.getOutputStream().flush();
        connection.getOutputStream().close();
        isRequestSuccess(connection);
        return readContent(connection);
    }

    private static void setConnectionAttribute(HttpURLConnection connection, String type, Map<String, String> headers)
            throws Exception {
        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        // 设置访问超时时间及读取网页流的超市时间,毫秒值
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        // 忽略缓存
        connection.setUseCaches(false);
        connection.setRequestMethod(type);
    }

    private static String readContent(HttpURLConnection connection)
            throws IOException {
        BufferedReader reader = getReader(connection);
        StringBuilder content = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            content.append(line);
        }
        logger.info("GetHTTP Response={}", content);
        return content.toString();
    }

    private static BufferedReader getReader(HttpURLConnection connection)
            throws IOException {
        return new BufferedReader(new InputStreamReader(connection.getInputStream(), parseCharset(connection.getContentType())));
    }

    private static boolean isRequestSuccess(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }

    public static String get(String url) throws Exception {
        return get(url, getHeaders);
    }

    private static final HttpClient httpClient = new HttpClient();

    public static String post(String url, Map<String, Object> args, Map<String, String> headers)
            throws Exception {
        logger.info("PostURL={}#Arguments={}", new Object[]{url, JSONUtils.toJSON(args)});
        trustAllHosts();
        PostMethod postMethod = initialWithHeader(url, headers);
        if (Objects.nonNull(args) && !args.isEmpty()) {
            postMethod.setRequestBody(args.entrySet().stream().map(entry -> new NameValuePair(entry.getKey(), JSONUtils.toJSON(entry.getValue()))).collect(Collectors.toList()).toArray(new NameValuePair[0]));
        }
        httpClient.executeMethod(postMethod);
        try {
            String response = postMethod.getResponseBodyAsString();//new String(postMethod.getResponseBody(), parseCharset(headers.get("Content-Type")));
            logger.info("PostHTTP Response={}", new Object[]{response});
            return response;
        } catch (Exception e) {
            throw e;
        }
    }

    private static PostMethod initialWithHeader(String url, Map<String, String> headers) {
        PostMethod postMethod = new PostMethod(url);
        for (Entry<String, String> entry : headers.entrySet()) {
            postMethod.addRequestHeader(entry.getKey(), entry.getValue());
        }
        return postMethod;
    }

    private static String parseCharset(String contentType) {
        int index;
        if (StringUtils.isNotBlank(contentType) && (index = contentType.indexOf("charset")) != -1) {
            return contentType.substring(index + 8);
        }
        return Charset.UTF_8.getCharset();
    }

    private static final Map<String, String> postHeaders = new TreeMap<String, String>() {
        private static final long serialVersionUID = -228754478108596704L;

        {
            put("Accept", "application/json");
            put("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        }
    };
    private static final Map<String, String> getHeaders = new TreeMap<String, String>() {
        private static final long serialVersionUID = -8979851233350551398L;

        {
            put("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        }
    };

    public static String post(String url, Map<String, Object> args) throws Exception {
        return post(url, args, postHeaders);
    }

    public static void printResult(HttpServletResponse response, String message) {
        response.setContentType("application/json; charset=utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.println(message);
            writer.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    // Create a trust manager that does not validate certificate chains
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }};

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}