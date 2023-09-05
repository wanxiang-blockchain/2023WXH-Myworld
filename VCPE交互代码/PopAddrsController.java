package com.macrounion.nt.server.api.controller;

import com.google.common.collect.Lists;
import com.macrounion.nt.message.utils.SignAlgorithmUtil;
import com.macrounion.nt.server.api.resp.PopAddrResp;
import com.macrounion.nt.server.service.entity.Pop;
import com.macrounion.nt.server.service.exception.BusinessException;
import com.macrounion.nt.server.service.mapper.PopMapperExt;
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

import java.util.HashMap;
import java.util.List;

/**
 * @ClassNamePopAddrsController
 * @Description TODO
 * @Author Administrator
 * @Date 2020/8/17 9:33
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api")
public class PopAddrsController {

    private static final Log log = LogFactory.getLog(PopAddrsController.class);

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private PopMapperExt popMapperExt;

    @GetMapping("/getPopLists")
    public BaseRespDto<List<PopAddrResp>> getPopLists(
    		@RequestParam(value = "domainName", defaultValue = "")String domainName,
    		@RequestParam(value = "sign", defaultValue = "") String sign,
    		@RequestParam(value = "netType", defaultValue = "0",required = false) Integer netType){
        log.info("domain="+domainName+"   sign="+sign);

        if(StringUtils.isBlank(domainName))
            throw new BusinessException(202,"用户域不能为空");
        if(StringUtils.isBlank(sign))
            throw new BusinessException(203,"签名不正确");

        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("domainName",domainName);
        String strSign= SignAlgorithmUtil.generateSign(map, apiKey);

//        if(!sign.equals(strSign))
//            throw new BusinessException(203,"签名不正确");

        BaseRespDto<List<PopAddrResp>> resp=new BaseRespDto<List<PopAddrResp>>();
        try{
            List<Pop> list=popMapperExt.getByDomain(domainName,netType);
            List<PopAddrResp> respList= Lists.newArrayList();
            List<Pop> otherPops=popMapperExt.findPage(null,null,null,2,1,netType);
            if(!CollectionUtils.isEmpty(otherPops)){
                otherPops.forEach(p->{
                    respList.add(new PopAddrResp(p.getName(),p.getIp(),p.getYuming()));
                });
            }
            if(list!=null && list.size()>0){
                log.info("list.size="+list.size());
                list.stream().forEach(p->{
                    respList.add(new PopAddrResp(p.getName(),p.getIp(),p.getYuming()));
                });
            }else {
                log.info("list.size=0");
            }
            resp.setData(respList);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e);
            throw new BusinessException(201,e.getMessage());
        }
        return resp;
    }
}
