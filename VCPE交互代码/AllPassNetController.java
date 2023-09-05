package com.macrounion.nt.server.api.controller;

import com.google.common.collect.Lists;
import com.macrounion.nt.server.service.entity.Configuration;
import com.macrounion.nt.server.service.exception.BusinessException;
import com.macrounion.nt.server.service.mapper.NetGroupMapperExt;
import com.macrounion.nt.server.service.service.ConfigService;
import com.macrounion.server.service.resp.NetGroupRespDto;
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

import java.util.List;

/**
 * @ClassNameAllPassNetController
 * @Description TODO
 * @Author Administrator
 * @Date 2019/3/14 9:47
 * @Version 1.0
 **/
@RestController
@RequestMapping("/api/allPassNet")
public class AllPassNetController {

    @Autowired
    private NetGroupMapperExt netGroupMapperExt;
    @Autowired
    private ConfigService configService;

    private static final Log log = LogFactory.getLog(AllPassNetController.class);

    @GetMapping("/getNetGroupByUser")
    public BaseRespDto<List<NetGroupRespDto>> getNetGroupByUser(
            @RequestParam(value = "clientId", defaultValue = "") String clientId,
            @RequestParam(value = "netName", defaultValue = "") String netName,
            @RequestParam(value = "domainName", defaultValue = "") String domainName){
//        log.error("param is clientId =:"+clientId);
        BaseRespDto<List<NetGroupRespDto>> resp=new BaseRespDto<List<NetGroupRespDto>>();
        Configuration configuration=configService.selectConfig();
        if(configuration==null)
            throw new BusinessException(202,"转发路由未找到，请配置");
        try{
            List<NetGroupRespDto> list=netGroupMapperExt.selectByClientId(clientId,netName,domainName);
            if(!CollectionUtils.isEmpty(list)){
                list.forEach(p->{
                    log.info("forwardId "+p.getForwardIp());
                    if(StringUtils.isEmpty(p.getForwardIp()))
                        p.setForwardIp(configuration.getForward_ip());
                });
            }
//            List<NetGroupRespDto> list = Lists.newArrayList();
//            list.add(new NetGroupRespDto("AAA","aaa","10.10.10.90","123","36.99.47.109:55"));
//            list.add(new NetGroupRespDto("AAA","bbb","10.10.10.91","123","36.99.47.109:55"));

            resp.setData(list);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e);
            throw new BusinessException(201,e.getMessage());
        }
        return resp;
    }
}
