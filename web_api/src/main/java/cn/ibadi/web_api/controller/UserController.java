package cn.ibadi.web_api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.ibadi.vo.User;

@RestController
@RequestMapping("user")
public class UserController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String signIn() {
		logger.info("访问登录页面");
		return "signIn";
	}
	
	@RequestMapping(value = "/post_test")
	public void postTest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		  System.out.println("---------获取请求数据方式1-------------");  
	        // 获取指定的请求数据  
	        String value = request.getParameter("username");  
	        if (value != null && !value.trim().equals("")) {  
	            System.out.println(value);  
	        }  
	  
	        System.out.println("---------获取请求数据方式2-------------");  
	        // 获取所有的请求数据  
	        Enumeration e = request.getParameterNames();  
	        while (e.hasMoreElements()) {  
	            String paramName = (String) e.nextElement();  
	            String value2 = request.getParameter(paramName);  
	            System.out.println(paramName + "=" + value2);  
	        }  
	  
	        System.out.println("---------获取请求数据方式3-------------");  
	        // 获取所有的请求数据，同名的只能获取一次，就是第一次  
	        String[] values = request.getParameterValues("username");  
	        for (int i = 0; values != null && i < values.length; i++) {  
	            System.out.println(values[i]);  
	        }  
	  
	        System.out.println("---------获取请求数据方式4-------------");  
	        // 这个特别实用，框架的模型驱动，这个Map的value肯定是String数组类型，因为有同名的请求数据  
	        // 实际开发中是不会 request.getParameter("username");用这种方式的，都是要创建一个model的  
	        Map<String, String[]> map = request.getParameterMap();  
	        User user = new User();  
//	        try {  
//	            // 用map中的数据填充bean  
//	            BeanUtils.populate(user, map);  
//	        } catch (IllegalAccessException e1) {  
//	            e1.printStackTrace();  
//	        } catch (InvocationTargetException e1) {  
//	            e1.printStackTrace();  
//	        }  
	        System.out.println(user.getPassword());  
	  
	        System.out.println("---------获取请求数据方式5-------------");  
	        // request.getInputStream();是上传文件的时候获取数据的方式  
	        // 普通数据是获取不到的  
	        InputStream in = request.getInputStream();  
	        
	        String text = IOUtils.toString(request.getInputStream(), "UTF-8");
	     
	        System.out.println(text);
		System.out.println("----------------");
	}
}
