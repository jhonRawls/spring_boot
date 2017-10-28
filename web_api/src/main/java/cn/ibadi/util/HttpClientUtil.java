package cn.ibadi.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

/**
 * httpclient工具类
 *
 * @author 513416
 *
 */
public class HttpClientUtil {

	private static final Logger logger = Logger.getLogger(HttpClientUtil.class);
	public static final String CHARSET_UTF8 = "UTF-8";


	private HttpClientUtil() {
	}

	private static Request setTimout(Request req, String url) {
		// 默认30秒
		int CONN_TIMEOUT=30000;
		if(url.contains("weixin.qq.com")) { // 腾讯接口，默认10秒
			CONN_TIMEOUT = 10000;
		}  else {	// 蜂鸟接口，默认5秒
			CONN_TIMEOUT = 5000;
			CONN_TIMEOUT = 30000;	// 怒改成 30s，不要问我为什么
		}

	    return req.connectTimeout(CONN_TIMEOUT).socketTimeout(CONN_TIMEOUT);
	}

	/**
	 * 发送HTTP_GET请求
	 *
	 * @see 该方法会自动关闭连接,释放资源
	 * @param url
	 *            请求地址(含参数)
	 * @return 远程主机响应正文
	 */
	public static String sendGetRequest(String url) {
		long begin = System.currentTimeMillis();
		String s = null;
		boolean b = false;
		try {
			s = setTimout(Request.Get(url), url)
					.execute()
					.returnContent()
					.asString(Consts.UTF_8);
			b = true;
			return s;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			logger.debug("fluent api GET[" + b + "] [" + url + "] res[ " + s + "] cost-time[" + (System.currentTimeMillis() - begin) + "]");
		}

		return null;
	}

	/**
	 * 发送HTTP_POST请求
	 *
	 * @see 该方法为sendPostRequest(String,String,boolean,String,String)
	 *      的简化方法
	 * @see 该方法在对请求数据的编码和响应数据的解码时,所采用的字符集均为UTF-8
	 * @see 当isEncoder=true时,其会自动对<code>sendData中的[中文][|][
	 *      ]等特殊字符进行URLEncoder.encode(string,CHARSET_UTF8)
	 * @param isEncoder
	 *            用于指明请求数据是否需要UTF-8编码,true为需要
	 */
	public static String sendPostRequest(String reqURL, String sendData,
			boolean isEncoder) {
		return sendPostRequest(reqURL, sendData, isEncoder, null, null);
	}


	/**
	 * 发送HTTP_POST请求
	 *
	 * @see 该方法会自动关闭连接,释放资源
	 * @see 当<code>isEncoder=true</code>时,其会自动对<code>sendData</code>中的[中文][|][
	 *      ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
	 * @param reqURL
	 *            请求地址
	 * @param sendData
	 *            请求参数,若有多个参数则应拼接成param11=value11m22=value22m33=value33的形式后,
	 *            传入该参数中
	 * @param isEncoder
	 *            请求数据是否需要encodeCharset编码,true为需要
	 * @param encodeCharset
	 *            编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
	 * @param decodeCharset
	 *            解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
	 * @return 远程主机响应正文
	 */
	private static String sendPostRequest(String reqURL, String sendData,
			boolean isEncoder, String encodeCharset, String decodeCharset) {
		long begin = System.currentTimeMillis();
		String responseContent = null;
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// HttpClient
        CloseableHttpClient httpClient = httpClientBuilder.build();

		HttpPost httpPost = new HttpPost(reqURL);
		httpPost.setHeader(HTTP.CONTENT_TYPE, "text/html");
		boolean b=false;
		try {
			if (isEncoder) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (String str : sendData.split("&")) {
					formParams.add(new BasicNameValuePair(str.substring(0,
							str.indexOf("=")),
							str.substring(str.indexOf("=") + 1)));
				}
				httpPost.setEntity(new StringEntity(URLEncodedUtils.format(
						formParams, encodeCharset == null ? CHARSET_UTF8
								: encodeCharset)));
			} else {
				httpPost.setEntity(new StringEntity(sendData));
			}

			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				responseContent = EntityUtils.toString(entity,
						decodeCharset == null ? CHARSET_UTF8 : decodeCharset);
				EntityUtils.consume(entity);
			}
			b=true;
		} catch (Exception e) {
			logger.error("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
			logger.error("client post error :"+e);
		} finally {
			logger.info("client post result:"+b);
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			httpClient=null;

			logger.debug("fluent api POST-client[" + b + "] [" + reqURL + "] data[ " + sendData + "] res[ " + responseContent + "] cost-time[" + (System.currentTimeMillis() - begin) + "]");
		}
		return responseContent;
	}

	public static String postPlusForm(String url, Map<String,Object> paramMap, Charset charset) {
		long begin = System.currentTimeMillis();
		String s = null;
		boolean b = false;
		try {
			charset = charset != null ? charset : Consts.UTF_8;

			int CONN_TIMEOUT=30000;
			Request req =
				Request.Post(url)
			    .connectTimeout(CONN_TIMEOUT)
			    .socketTimeout(CONN_TIMEOUT)
			    .addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded")
			    .addHeader("charset", CHARSET_UTF8);

			Form form = Form.form();
			String key, value;
			Iterator<String>  iter=paramMap.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				value= paramMap.get(key) + "";
				form.add(key, value);
			}

		    s = req.bodyForm(form.build())
		    	.execute()
			    .returnContent()
			    .asString(charset);

			b = true;
			return s;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.debug("fluent api POST[" + b + "] [" + url + "] form[ " + paramMap + "] res[ " + s + "] cost-time[" + (System.currentTimeMillis() - begin) + "]");
		}

		return null;
	}

	public static String postFormApi(String api, Map<String,Object> paramMap) {
		return postPlusForm(api, paramMap, Consts.UTF_8);
	}

	public static String postJsonApi(String api, String jsonData, Map<String ,String> headMap) {
		return postPlusHeader(api, jsonData, headMap);
	}

	private static String postPlusHeader(String url, String jsonData, Map<String ,String> headMap)  {
		long begin = System.currentTimeMillis();
		String s = null;
		boolean b = false;
		@SuppressWarnings("rawtypes")
		Map map = null;
		try {
			Request req = setTimout(Request.Post(url), url)
			    .addHeader("Content-Type", "application/json")
			    .addHeader("Accept", "application/json")
			    .addHeader("charset", CHARSET_UTF8);

			String key, value;
			Iterator<String> iter = headMap.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				value = headMap.get(key);
				req.addHeader(key, value);
			}

			s = req.bodyString(jsonData, ContentType.APPLICATION_JSON)
			    .execute()
			    .returnContent()
			    .asString(Consts.UTF_8);
			map = JSON.parseObject(s, Map.class);
			if(map.containsKey("result")){
				if(!map.get("result").toString().equals("true") && !url.contains("/api/person/reg")){
					throw new TimeoutException("fn-api error");
				}
			}

			b = true;
		}catch (TimeoutException | IOException e) {
			logger.error("【异常】HttpClientKit Exception", e);
		} finally {
			logger.debug("fluent api POST-Head[" + b + "] [" + url + "] req[" + jsonData + "] head[" + headMap + "] res[ " + s + "] cost-time[" + (System.currentTimeMillis() - begin) + "]");
		}

		return s;
	}

	public static String postJsonApi(String api, String jsonData) {
		return post(api, jsonData);
	}

	private static String post(String url, String jsonData) {
		long begin = System.currentTimeMillis();
		String s = null;
		boolean b = false;
		try {
			s = setTimout(Request.Post(url), url)
			    .addHeader("Content-Type", "application/json;charset=utf-8")
			    .addHeader("Accept", "application/json")
			    .addHeader("charset", CHARSET_UTF8)
			    .bodyString(jsonData, ContentType.APPLICATION_JSON)
			    .execute()
			    .returnContent()
			    .asString(Consts.UTF_8);

			b = true;
			return s;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			logger.debug("fluent api POST[" + b + "] [" + url + "] req[ " + jsonData + "] res[ " + s + "] cost-time[" + (System.currentTimeMillis() - begin) + "]");
		}

		return null;
	}

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("client_id", "GiveUClient");
		map.put("client_secret", "123456");
		map.put("grant_type", "client_credentials");
		String api = "http://lhwebtest:9001/oauth/token";
		String result = HttpClientUtil.postFormApi(api, map);
		System.out.println(result);
	}
}
