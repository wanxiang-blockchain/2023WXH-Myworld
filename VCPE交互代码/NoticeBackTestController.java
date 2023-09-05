package com.macrounion.nt.server.api.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.macrounion.nt.message.resp.NoticeReturnRespDto;
import com.macrounion.nt.message.utils.SignAlgorithmUtil;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@RestController
@RequestMapping("/api")
public class NoticeBackTestController {
	@GetMapping("/test")
	public String interfaceTest(HttpServletRequest request){
		NoticeReturnRespDto resp=new NoticeReturnRespDto();
		
		System.out.println(request.getQueryString());
		HashMap<String,Object> param=new HashMap<String,Object>();
		param.put("clientId", request.getParameter("clientId"));
		param.put("actionTime", request.getParameter("actionTime"));
		param.put("action", request.getParameter("action"));
		param.put("msg", request.getParameter("msg"));
//		param.put("sign", request.getParameter("sign"));
		
		
		String sign=SignAlgorithmUtil.generateSign(param,"abc");
		
		if(sign.equals(request.getParameter("sign"))){
			resp.setCode(200);
		}else{
			resp.setCode(201);
			resp.setMsg("签名错误！");
		}
		
		return resp.toString();
	}
}
