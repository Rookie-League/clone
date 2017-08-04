package com.earphone.common.utils;

import com.earphone.common.constant.Charset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static com.earphone.common.utils.JSONExtend.asJSON;
import static com.earphone.common.utils.JSONExtend.asPrettyJSON;
import static java.util.stream.Collectors.toList;
import static org.apache.http.conn.ssl.NoopHostnameVerifier.INSTANCE;

/**
 * @author yaojiamin
 * @description
 * @createTime 2016-4-8 上午11:18:22
 */
@Slf4j
public final class WebExtend {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslsf = null;
    private static PoolingHttpClientConnectionManager cm = null;

    static {
        try {
            // 全部信任 不做身份鉴定
            SSLContextBuilder builder = new SSLContextBuilder().loadTrustMaterial(null, (x509Certificates, s) -> true);
            String[] supportedProtocols = new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"};
            sslsf = new SSLConnectionSocketFactory(builder.build(), supportedProtocols,null, INSTANCE);
            RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.create();
            registryBuilder.register(HTTP, new PlainConnectionSocketFactory());
            registryBuilder.register(HTTPS, sslsf);
            Registry<ConnectionSocketFactory> registry = registryBuilder.build();
            cm = new PoolingHttpClientConnectionManager(registry);
            //max connection
            cm.setMaxTotal(200);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static CloseableHttpClient getHttpClient() throws Exception {
        return HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(cm).setConnectionManagerShared(true).build();
    }

    private static final Map<String, String> POST_HEADERS = new TreeMap<String, String>() {
        private static final long serialVersionUID = -228754478108596704L;

        {
            put("Accept", "application/json");
            put("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        }
    };
    private static final Map<String, String> GET_HEADERS = new TreeMap<String, String>() {
        private static final long serialVersionUID = -8979851233350551398L;

        {
            put("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        }
    };

    public static String get(String url) throws Exception {
        return get(url, GET_HEADERS);
    }

    public static String get(String url, Map<String, String> headers) throws Exception {
        log.info("GetURL={}", new Object[]{url});
        if (!url.startsWith(HTTPS)) {
            String result = Request.Get(url).setHeaders(asHeaders(headers)).execute().returnContent().asString();
            log.info("Response={}", result);
            return result;
        }
        CloseableHttpClient httpclient = getHttpClient();
        HttpGet httpGet = new HttpGet(url);
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        httpGet.setHeaders(asHeaders(headers));
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            log.info(response.getStatusLine().toString());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            log.info("Response={}", result);
            return result;
        }
    }

    public static String post(String url, Map<String, Object> args) throws Exception {
        return post(url, args, POST_HEADERS);
    }

    public static String post(String url, Map<String, Object> args, Map<String, String> headers) throws Exception {
        log.info("PostURL={}\nArguments={}", url, asPrettyJSON(args));
        if (!url.startsWith(HTTPS)) {
            String result = Request.Post(url).setHeaders(asHeaders(headers)).bodyForm(asForm(args)).execute().returnContent().asString();
            log.info("Response={}", result);
            return result;
        }
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        CloseableHttpClient httpclient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(asForm(args)));
        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            log.info("Response={}", result);
            return result;
        }
    }

    private static Header[] asHeaders(Map<String, String> headers) {
        return headers.entrySet().stream().map(WebExtend::asHeader).collect(toList()).toArray(new Header[0]);
    }

    private static BasicHeader asHeader(Entry<String, String> entry) {
        return new BasicHeader(entry.getKey(), entry.getValue());
    }

    private static List<NameValuePair> asForm(Map<String, Object> args) {
        return args.entrySet().stream().map(WebExtend::asNameValuePair).collect(toList());
    }

    private static NameValuePair asNameValuePair(Entry<String, Object> entry) {
        return new BasicNameValuePair(entry.getKey(), asJSON(entry.getValue()));
    }

    private static String parseCharset(String contentType) {
        int index;
        if (StringUtils.isNotBlank(contentType) && (index = contentType.indexOf("charset")) != -1) {
            return contentType.substring(index + 8);
        }
        return Charset.UTF8.getValue();
    }

    public static String realIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringExtend.isNotBlank(ip) && "unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}