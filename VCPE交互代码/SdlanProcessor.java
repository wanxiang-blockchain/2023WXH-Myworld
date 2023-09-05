package com.macrounion.nt.server.api.processor;

import com.google.common.collect.Lists;
import com.macrounion.nt.server.api.resp.SdlanResp;
import com.macrounion.nt.server.service.entity.*;
import com.macrounion.nt.server.service.mapper.*;
import com.macrounion.server.service.resp.SdlanDeviceRespDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.macrounion.peergine.webClientExt.dtos.BaseRespDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassNameSdlanProcessor
 * @Description TODO
 * @Author Administrator
 * @Date 2021/1/5 16:07
 * @Version 1.0
 **/
@Component
public class SdlanProcessor {

    @Autowired
    private SdlanDeviceMapperExt sdlanDeviceMapperExt;
    @Autowired
    private SdlanGroupMapperExt sdlanGroupMapperExt;
    @Autowired
    private SdlanNetMapperExt sdlanNetMapperExt;
    @Autowired
    private SdlanMapperExt sdlanMapperExt;
//    @Autowired
//    private NetServerMapperExt netServerMapperExt;
//    @Autowired
//    private NetServerMapper netServerMapper;
    @Autowired
    private PopMapper popMapper;

    public BaseRespDto<List<SdlanResp>> getSdlanByDevice(String clientId,int pageIndex,int pageSize){
        BaseRespDto<List<SdlanResp>> resp=new BaseRespDto<List<SdlanResp>>();

        //取得设备的全通结点
        List<SdlanDevice> clientDeviceList=sdlanDeviceMapperExt.getDeviceByClientIdAndType(clientId);

        //取得设备（客户组）对应的sdlan组
        List<SdlanGroup> lstSdlanGroupClient=sdlanGroupMapperExt.getClientGroupList(clientId);
        List<Integer> lstNetIds= Lists.newArrayList();//设备所在的网络id
        if(!CollectionUtils.isEmpty(lstSdlanGroupClient)) {
//            List<SdlanDevice> lstSdlanDeviceUnique = lstSdlanDevice.stream().collect(Collectors.collectingAndThen(
//                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SdlanDevice::getSdlanGroupId)
//                            .thenComparing(SdlanDevice::getSdlanGroupId))), ArrayList::new
//                    )
//            );
            lstNetIds=lstSdlanGroupClient.stream().map(SdlanGroup::getSdlanNetId).distinct().collect(Collectors.toList());
        }

        List<SdlanNet> netList=Lists.newArrayList();
        if(!CollectionUtils.isEmpty(lstNetIds)) {
            netList = sdlanNetMapperExt.getNetList(lstNetIds, (pageIndex - 1) * pageSize, pageSize);
        }

        List<Integer> lstSdlanIds=Lists.newArrayList();
        if(!CollectionUtils.isEmpty(netList))
            lstSdlanIds=netList.stream().map(SdlanNet::getSdlanId).distinct().collect(Collectors.toList());
        List<Sdlan> lstSdlan=Lists.newArrayList();
        if(!CollectionUtils.isEmpty(lstSdlanIds))
            lstSdlan=sdlanMapperExt.getListByIds(lstSdlanIds);
//        List<Integer> lstNetServerIds=Lists.newArrayList();
//        if(!CollectionUtils.isEmpty(lstSdlan))
//            lstNetServerIds=lstSdlan.stream().map(Sdlan::getNetServerId).distinct().collect(Collectors.toList());
//        List<NetServer> lstNetServer=Lists.newArrayList();
//        if(!CollectionUtils.isEmpty(lstNetServerIds))
//            lstNetServer=netServerMapperExt.getListByIds(lstNetServerIds);

        List<SdlanGroup> lstSdlanGroupService=Lists.newArrayList();
        if(!CollectionUtils.isEmpty(lstNetIds))
            lstSdlanGroupService=sdlanGroupMapperExt.getSeviceGroupList(lstNetIds);//设备所在所有网络的所有服务组
        List<Integer> lstServiceGroupIds=Lists.newArrayList();
        if(!CollectionUtils.isEmpty(lstSdlanGroupService))
            lstServiceGroupIds=lstSdlanGroupService.stream().map(SdlanGroup::getId).distinct().collect(Collectors.toList());

        List<SdlanDevice> lstSdlanDevice=Lists.newArrayList();
        if(!CollectionUtils.isEmpty(lstServiceGroupIds))
            lstSdlanDevice=sdlanDeviceMapperExt.getDeviceListByGroupIds(lstServiceGroupIds,null);
//        Map<Integer,SdlanDevice> mapSdlanDevice=sdlanDeviceMapperExt.getDeviceMapByGroupIds(lstServiceGroupIds,0);

        List<SdlanResp> lstSdlanResp=Lists.newArrayList();
        if(!CollectionUtils.isEmpty(netList)){
            for(SdlanNet sdlanNet : netList){
                SdlanResp dto=new SdlanResp();
                dto.setId(sdlanNet.getId());

                Sdlan sdlan=lstSdlan.stream().filter(p->p.getId().equals(sdlanNet.getSdlanId())).findFirst().orElse(null);

                List<SdlanGroup> sdlanGroupService=Lists.newArrayList();
                if(sdlan!=null){
                    dto.setSdlanId(sdlan.getId());
                    dto.setName(sdlanNet.getSdlanNetName());
                    dto.setPassword(sdlan.getPassword());
                    sdlanGroupService=lstSdlanGroupService.stream().filter(p->p.getSdlanId().equals(sdlan.getId())
                            && p.getSdlanNetId().equals(sdlanNet.getId())
                            && p.getType().equals(1)).collect(Collectors.toList());
                }
                dto.setRemark(sdlanNet.getRemark());
                dto.setProxyIp(null);
                dto.setProxyPort(null);
//                NetServer netServer=new NetServer();
                Pop pop=new Pop();
                if(!CollectionUtils.isEmpty(clientDeviceList)){
                    SdlanGroup sg=lstSdlanGroupClient.stream().filter(p->p.getSdlanNetId().equals(sdlanNet.getId())).findFirst().orElse(null);
                    if(sg!=null){
                        SdlanDevice clientDevice= clientDeviceList.stream().filter(p->p.getSdlanGroupId().equals(sg.getId())).findFirst().orElse(null);
                        if(clientDevice!=null){
//                            netServer=netServerMapper.selectByPrimaryKey(clientDevice.getNetServerId());
                            pop=popMapper.selectByPrimaryKey(clientDevice.getNetServerId());
                            dto.setVirtualSdlanIp(clientDevice.getInventedIp());
                            dto.setSdlanCmd(clientDevice.getAutoArgument());
//                            String autoCmd="-rEA3";
//                            if(clientDevice.getSpeedUp().equals(1))
//                                autoCmd="-rSEA3";
//                            dto.setAutoCmd(autoCmd);
                            String autoCmd="-rE";
                            if(clientDevice.getSpeedUp().equals(1))
                                autoCmd="-rSE";
                            if(sdlan.getEncryptionMode().equals(1))
                                autoCmd=autoCmd+"A3";
                            if(sdlan.getEncryptionMode().equals(2))
                                autoCmd=autoCmd+"A5";
                            if(!StringUtils.isEmpty(sdlan.getCommand()))
                                autoCmd=autoCmd+" "+sdlan.getCommand();

                            dto.setAutoCmd(autoCmd);
                        }
                    }
                }
//                if(netServer!=null)
//                    dto.setNetServerIp(netServer.getIpPort());
                if(pop!=null){
                    if(!StringUtils.isEmpty(pop.getYuming()))
                        dto.setNetServerIp(pop.getYuming());
                    else
                        dto.setNetServerIp(pop.getIp());

                    if(pop.getOnline_status()==0 && pop.getOnline_statusb()==1){
                        if(!StringUtils.isEmpty(pop.getYumingb()))
                            dto.setNetServerIp(pop.getYumingb());
                        else
                            dto.setNetServerIp(pop.getIpb());
                    }
                }
                if(!CollectionUtils.isEmpty(sdlanGroupService)) {
                    String ips= StringUtils.EMPTY;
                    for(SdlanGroup finalSdlanGroupService : sdlanGroupService){
                        List<SdlanDevice> lstCurDevice=lstSdlanDevice.stream().filter(p->p.getSdlanGroupId().equals(finalSdlanGroupService.getId())).collect(Collectors.toList());

                        if(!CollectionUtils.isEmpty(lstCurDevice)){
                            for(SdlanDevice q : lstCurDevice){
                                if(StringUtils.isBlank(q.getServerIp()))
                                    continue;
                                String serverIp=q.getServerIp().replaceAll("(;)+$", "");
                                if(StringUtils.isBlank(serverIp))
                                    continue;
                                if(!StringUtils.isBlank(ips) && !ips.endsWith("|"))
                                    ips=ips+"|"+q.getServerIp()+" "+q.getInventedIp();
                                else
                                    ips=ips+q.getServerIp()+" "+q.getInventedIp();
                            }

                        }

                    }
                    dto.setIps(ips);
                }


                lstSdlanResp.add(dto);
            }
        }

        resp.setData(lstSdlanResp);
        return resp;
    }

    public BaseRespDto<List<SdlanResp>> getProxyByDevice(String clientId,int pageIndex,int pageSize){
        BaseRespDto<List<SdlanResp>> resp=new BaseRespDto<List<SdlanResp>>();

        List<SdlanResp> lstSdlanResp = Lists.newArrayList();
        List<SdlanDeviceRespDto> lstProxyDevice=sdlanDeviceMapperExt.getProxyDeviceList(1,(pageIndex-1)*pageSize,pageSize);

        if(!CollectionUtils.isEmpty(lstProxyDevice)){
            lstProxyDevice.forEach(p->{
                SdlanResp dto=new SdlanResp();
                dto.setId(p.getId());
                dto.setName(p.getDeviceName());
                dto.setPassword(null);
                dto.setRemark(p.getDeviceRemark());
                dto.setProxyIp(p.getPptunIp());
                dto.setProxyPort(p.getPptunPort());
                dto.setIps(null);
                dto.setNetServerIp(null);
                dto.setVirtualSdlanIp(null);
                lstSdlanResp.add(dto);
            });
        }
        resp.setData(lstSdlanResp);
        return resp;

    }
}
