package com.macrounion.nt.server.api.controller;

import com.google.common.collect.Lists;
import com.macrounion.nt.message.utils.SignAlgorithmUtil;
import com.macrounion.nt.server.api.processor.SdlanProcessor;
import com.macrounion.nt.server.api.resp.SdlanResp;
import com.macrounion.nt.server.service.entity.Sdlan;
import com.macrounion.nt.server.service.entity.SdlanDevice;
import com.macrounion.nt.server.service.entity.SdlanGroup;
import com.macrounion.nt.server.service.entity.SdlanNet;
import com.macrounion.nt.server.service.exception.BusinessException;
import com.macrounion.nt.server.service.mapper.SdlanDeviceMapperExt;
import com.macrounion.nt.server.service.mapper.SdlanGroupMapperExt;
import com.macrounion.nt.server.service.mapper.SdlanMapperExt;
import com.macrounion.nt.server.service.mapper.SdlanNetMapperExt;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.macrounion.peergine.webClientExt.dtos.BaseRespDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassNameSdlanController
 * @Description TODO
 * @Author Administrator
 * @Date 2021/1/5 9:33
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api")
public class SdlanController {

    private static final Log log = LogFactory.getLog(SdlanController.class);

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private SdlanProcessor sdlanProcessor;

    @GetMapping("/getSdlanByDevice")
    public BaseRespDto<List<SdlanResp>> getSdlanByDevice(@RequestParam(value = "clientId", defaultValue = "")String clientId,
                                                         @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                         @RequestParam(value = "sign", defaultValue = "") String sign){
//        if(StringUtils.isBlank(sign))
//            throw new BusinessException(203,"签名不正确");
//
//        HashMap<String,Object> map=new HashMap<String,Object>();
//        map.put("clientId",clientId);
//        map.put("pageIndex",pageIndex);
//        map.put("pageSize",pageSize);
//        String strSign= SignAlgorithmUtil.generateSign(map, apiKey);
//
//        if(!sign.equals(strSign))
//            throw new BusinessException(203,"签名不正确");

        return sdlanProcessor.getSdlanByDevice(clientId,pageIndex,pageSize);
    }

    @GetMapping("/getProxyByDevice")
    public BaseRespDto<List<SdlanResp>> getProxyByDevice(@RequestParam(value = "clientId", defaultValue = "")String clientId,
                                                         @RequestParam(value = "pageIndex", defaultValue = "1") int pageIndex,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                @RequestParam(value = "sign", defaultValue = "") String sign){
//        if(StringUtils.isBlank(sign))
//            throw new BusinessException(203,"签名不正确");
//
//        HashMap<String,Object> map=new HashMap<String,Object>();
//        map.put("clientId",clientId);
//        map.put("pageIndex",pageIndex);
//        map.put("pageSize",pageSize);
//        String strSign= SignAlgorithmUtil.generateSign(map, apiKey);
//
//        if(!sign.equals(strSign))
//            throw new BusinessException(203,"签名不正确");

        return sdlanProcessor.getProxyByDevice(clientId,pageIndex,pageSize);
    }

}
