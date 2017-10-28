package cn.ibadi.web_api;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.ibadi.util.HttpClientUtil;
import cn.ibadi.util.XmlMaps;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebApiApplicationTests {

	@Test
	public void contextLoads() {
	}

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<>();
		map.put("client_id", "GiveUClient");
		map.put("client_secret", "123456");
		map.put("grant_type", "client_credentials");
		String api = "http://127.0.0.1:8011/user/post_test";
		String requestBody = new XmlMaps(map).toXml();
		String result = HttpClientUtil.sendPostRequest(api, requestBody, false);
		System.out.println(result);
	}
}
