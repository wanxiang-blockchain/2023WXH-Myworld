package com.macrounion.nt.server.api.controller;

import java.util.List;

import com.macrounion.nt.server.service.entity.Users;
import com.macrounion.nt.server.service.exception.BusinessException;
import com.macrounion.nt.server.service.service.UserService;
import com.macrounion.nt.server.service.utils.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.macrounion.peergine.webClientExt.dtos.BaseRespDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.macrounion.nt.server.service.mapper.UserTuannelMapperExt;
import com.macrounion.server.service.resp.UserTuannelRespDto;

@RestController
@RequestMapping("/api/userTuannel")
public class UserTuannelController {
	
	@Autowired
	private UserTuannelMapperExt userTuannelMapperExt;
	@Autowired
	private UserService userService;
	
	private static final Log log = LogFactory.getLog(UserTuannelController.class);
	
	@GetMapping("/getTuannelInfoByUser")
	public BaseRespDto<List<UserTuannelRespDto>> getTuannelInfoByUser(@RequestParam(value = "clientId", defaultValue = "") String clientId){
		log.error("param is clientId =:"+clientId);
		BaseRespDto<List<UserTuannelRespDto>> resp=new BaseRespDto<List<UserTuannelRespDto>>();
		try{
			resp.setData(userTuannelMapperExt.getTuannelListByThisClientId(clientId));
		}catch(Exception e){
			e.printStackTrace();
			log.error(e);
		}
		return resp;
	}

	@GetMapping("/getUserInfo")
	public BaseRespDto<Users> getUserInfoByClientId(@RequestParam(value = "clientId", defaultValue = "") String clientId){
		if(StringUtils.isEmpty(clientId))
			throw new BusinessException(201,"不存在该用户");
		Users users=userService.findByClientId(clientId);
		if(users==null){
			throw new BusinessException(201,"不存在该用户");
		}
		BaseRespDto<Users> resp=new BaseRespDto<Users>();
		resp.setData(users);
		resp.setCode(200);
		return resp;
	}

	@PostMapping("/validateUser")
	public BaseRespDto<Boolean> validateUser(@RequestBody Users users){
		BaseRespDto<Boolean> resp=new BaseRespDto<>();
		resp.setCode(200);
		resp.setData(true);
		if(users==null || StringUtils.isEmpty(users.getClient_id()) || StringUtils.isEmpty(users.getPassword()))
			throw new BusinessException(201,"参数错误");

		String md5Pwd= Md5Util.getMd5(users.getPassword());
		Users dbUsers=userService.findByClientId(users.getClient_id());
		if(dbUsers==null || !dbUsers.getPassword().equals(md5Pwd)){
			resp.setCode(201);
			resp.setData(false);
			resp.setMsg("用户名密码错误");
		}

		return resp;
	}


}
