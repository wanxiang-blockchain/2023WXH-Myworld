package com.macrounion.atv.http;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.macrounion.atv.cache.ChannelCache;
import com.macrounion.atv.cache.CurrentDataCache;
import com.macrounion.atv.dto.resp.HttpAlarmDatas;
import com.macrounion.atv.dto.resp.HttpAlarmRespDto;
import com.macrounion.atv.dto.resp.HttpDataRespDto;
import com.macrounion.atv.dto.resp.HttpHistoryDataRespDto;
import com.macrounion.atv.dto.resp.HttpHistoryRespDto;
import com.macrounion.atv.processor.DeviceProcessor;
import com.macrounion.atv.service.entity.Channel;
import com.macrounion.atv.service.entity.CurrentData;
import com.macrounion.atv.service.entity.Device;
import com.macrounion.atv.service.entity.Sendto;
import com.macrounion.atv.service.service.SendtoService;
import com.macrounion.base.service.utils.JsonUtil;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

/**
 * 连接输出端Http
 * 
 * @author Administrator
 *
 */
@Component
public class SendHttpProcessor {
	private Logger logger = LoggerFactory.getLogger(SendHttpProcessor.class);
	
	private List<Sendto> https;
	
	//历史
	private List<Sendto> httpHistorys = Lists.newArrayList();
	//告警
	private List<Sendto> httpAlarms = Lists.newArrayList();
	
	@Autowired
	private SendtoService sendtoService;
	
	@Autowired
	private CurrentDataCache currentDataCache;
	@Autowired
	private ChannelCache channelCache;
	@Autowired
	private DeviceProcessor deviceProcessor;
	
	
	@PostConstruct
	public void init(){
		start();
	}

	public void start() {
		List<Sendto> configs = sendtoService.getAllSend();
		if(CollectionUtils.isEmpty(configs))
			return;
		https = configs.parallelStream().filter(c->c.getMode().toLowerCase().endsWith("http")).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(https))
			return;
		httpHistorys = https.parallelStream().filter(c->c.getDataType().equals(0)).collect(Collectors.toList());
		httpAlarms = https.parallelStream().filter(c->c.getDataType().equals(1)).collect(Collectors.toList());
	}

	public synchronized void sendHistory(String sendId, List<Device> ds) {
		Sendto dt = httpHistorys.parallelStream().filter(c->c.getId().equals(sendId)).findFirst().orElse(null);		
		if(CollectionUtils.isEmpty(httpHistorys) || dt == null || !dt.getMode().toLowerCase().equals("http"))
			return;
		List<HttpHistoryRespDto> dtos = convertDtos(ds);
		sendHistorys(dt,dtos);
	}
	
	private void sendHistorys(Sendto dt, List<HttpHistoryRespDto> dtos ) {
		if(CollectionUtils.isEmpty(httpHistorys) || CollectionUtils.isEmpty(dtos))
			return;
		if(dtos.size() > 50) {
			httpJsons(dt,dtos.subList(0, 50),null,0);
			sendHistorys(dt,dtos.subList(50, dtos.size()));
    	}else
    		httpJsons(dt,dtos,null,0);
	}

	/**
	 * 调用http接口
	 * @param dt 目标接口
	 * @param subList -历史数据
	 * @param alarm -告警数据（包括恢复）
	 * @param type 0-历史接口，1-告警接口
	 */
	public void httpJsons(Sendto dt, List<HttpHistoryRespDto> subList,HttpAlarmRespDto alarm,int type) {
		List<Sendto> https = (type ==0 ?(dt == null?this.httpHistorys:Arrays.asList(dt)):this.httpAlarms);
		String typeStr  = type ==0 ?"历史":"告警";
		https.forEach(http->{
			try{
				String data = JsonUtil.serialize(type ==0 ?subList:alarm);
				HttpRequest request = new HttpRequest(http.getIp())
						.contentType(HttpUtil.getContentTypeByRequestBody(data))
						.charset(http.getCharset());
				String result= request.method(Method.POST).timeout(5000).body(data).execute().body();
				if(!result.trim().toUpperCase().equals("SUCCESS")) {
					//失败
					logger.error("调用"+typeStr+"通知接口："+http.getIp()+"失败，返回值："+result);
					sendtoService.updateStatus(http.getId(), 0);
				}else {
					sendtoService.updateStatus(http.getId(), 1);
				}
			}catch(Exception e){
				logger.error("调用"+typeStr+"通知接口："+http.getIp()+"失败，异常："+e.getMessage());
				sendtoService.updateStatus(http.getId(), 0);
			}
		});
		
	}

	/**
	 * 格式化历史数据
	 * @param ds
	 * @return
	 */
	private List<HttpHistoryRespDto> convertDtos(List<Device> ds) {
		List<HttpHistoryRespDto> dtos = Lists.newArrayList();
		ds.forEach(d->{
			HttpHistoryRespDto dto = new HttpHistoryRespDto();
			dto.setDeviceCode(d.getDeviceCode());
			dto.setName(d.getName());
			dto.setProId(deviceProcessor.getProId());
			dto.setCurrentDatas(convertDatas(d));
			if(dto.getCurrentDatas() != null)
				dtos.add(dto);
		});
		return dtos;
	}

	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private List<HttpHistoryDataRespDto> convertDatas(Device d) {
		if(d == null)
			return null;
		List<CurrentData> datas = new ArrayList<CurrentData>(currentDataCache.getCurrentData(d.getId()));
		if(CollectionUtils.isEmpty(datas))
			return null;
		List<HttpHistoryDataRespDto> channelDatas = Lists.newArrayList();
		datas.forEach(data->{
			if(data != null) {
				Channel c = channelCache.getChannel(data.getDeviceId(), data.getChannelCode());
				if(c != null && (d.getSendType().equals(0) || c.getHistoryData().equals(1))) {
					HttpHistoryDataRespDto dto = new HttpHistoryDataRespDto();
					BeanUtils.copyProperties(data, dto);
					dto.setUpdateTime(df.format(data.getUpdateTime() == null?new Date():data.getUpdateTime()));
					dto.setName(c.getName());
					channelDatas.add(dto);
				}
			}
		});
		return channelDatas;
	}
	private ExecutorService alarmThreadPool = Executors.newCachedThreadPool();
	/**
	 * 发送告警/恢复
	 * @param dto
	 */
	public void sendAlarm(Device device,HttpAlarmDatas dto) {
		if(device==null || CollectionUtils.isEmpty(httpAlarms))
			return;
		alarmThreadPool.execute(()->{
			if(dto == null)
				return;
			if(!CollectionUtils.isEmpty(dto.getAlarms())) {
				HttpAlarmRespDto alarms = convertAlarms(device,dto.getAlarms(),1);
				httpJsons(null,null,alarms,1);
			}
			if(!CollectionUtils.isEmpty(dto.getRecovers())) {
				HttpAlarmRespDto alarms = convertAlarms(device,dto.getRecovers(),0);
				httpJsons(null,null,alarms,1);
			}
		});
		
	}

	/**
	 * 格式化HTTP告警接口通知数据对象
	 * @param datas 当前数据
	 * @param status 0-告警恢复，1-告警
	 * @return
	 */
	private HttpAlarmRespDto convertAlarms(Device device,List<CurrentData> datas,int status) {
		HttpAlarmRespDto dto = new HttpAlarmRespDto();
		dto.setStatus(status);
		dto.setProId(deviceProcessor.getProId());
		dto.setDeviceCode(device.getDeviceCode());
		dto.setName(device.getName());
		List<HttpDataRespDto> currentDatas = Lists.newArrayList();
		datas.forEach(d->{
			Channel c = channelCache.getChannel(d.getDeviceId(), d.getChannelCode());
			if(c!=null && (c.getAlarmData().equals(1))) {
				HttpDataRespDto data = new HttpDataRespDto();
				BeanUtils.copyProperties(d, data);
				data.setName(c.getName());
				data.setUpdateTime(df.format(d.getUpdateTime() == null?new Date():d.getUpdateTime()));
				currentDatas.add(data);
			}
		});
		dto.setCurrentDatas(currentDatas);
		return dto;
	}
	
}
