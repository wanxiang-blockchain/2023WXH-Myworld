package com.macrounion.atv.socket;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.macrounion.atv.listener.ItuConnectCallback;
import com.macrounion.atv.service.entity.Sendto;
import com.macrounion.atv.service.service.SendtoService;
import com.macrounion.atv.task.SendTask;
import com.macrounion.device.connect.socket.config.StringSocketConfig;
import com.macrounion.device.manager.itu.ItuAlarmManager;
import com.macrounion.device.manager.itu.ItuHistoryManager;
import com.macrounion.device.server.out.dto.DataViewAlarmOutDto;
import com.macrounion.device.utils.XmlUtils;

/**
 * 连接输出端socket
 * 
 * @author Administrator
 *
 */
@Component
public class SendConnectionProcessor {
	private Logger logger = LoggerFactory.getLogger(SendConnectionProcessor.class);

	private ItuHistoryManager historyManager;
	private ItuAlarmManager alarmManager;
	
	@Autowired
	private SendtoService sendtoService;
	@Autowired
	private ItuConnectCallback ituConnectCallback;
	
	
	private List<Sendto> configs;
	
	
	public List<Sendto> getConfigs() {
		return configs;
	}

	@PostConstruct
	public void init(){
		start();
	}

	@PreDestroy
	public void destory(){
		close();
	}
	
	public boolean close() {
		if(historyManager!=null)
			historyManager.stop();
		if(alarmManager!=null)
			alarmManager.stop();
		return true;
	}

	public boolean start() {
		configs = sendtoService.getAllSend();
		if(CollectionUtils.isEmpty(configs)) {
			configs = Lists.newArrayList();
			return true;
		}
		List<Sendto> historys = configs.parallelStream().filter(c->{return c.getMode().toLowerCase().equals("socket") && c.getDataType().equals(0);}).collect(Collectors.toList());
		startHistory(historys);
		List<Sendto> alarms = configs.parallelStream().filter(c->{return c.getMode().toLowerCase().equals("socket") && c.getDataType().equals(1);}).collect(Collectors.toList());
		startAlarm(alarms);
		startHistoryHttp(configs.parallelStream().filter(c->{return c.getMode().toLowerCase().equals("http") && c.getDataType().equals(0);}).collect(Collectors.toList()));
		return true;
	}

	public void sendHistory(String sendId,String content) {
		if(StringUtils.isEmpty(sendId))
			historyManager.send(content);
		else {
			Sendto dt = configs.parallelStream().filter(c->c.getId().equals(sendId)).findFirst().orElse(null);
			if(historyManager == null || dt == null || !dt.getMode().toLowerCase().equals("socket"))
				return;
			historyManager.send(sendId, content);
		}
	}
	
	/**
	 * 发送告警
	 * @param dto
	 */
	public void sendAlarm(DataViewAlarmOutDto dto) {
		if(alarmManager == null)
			return;
//		OutDto d = new OutDto();
//		BeanUtils.copyProperties(dto, d);
		String str  =XmlUtils.beanToXml(dto).replace("<OMMHISTORY>", "").replace("</OMMHISTORY>", "");
//		System.out.println(str);
		alarmManager.getDevice().forEach(value->{
            value.send(str);
        });
	}
	
	/**
	 * 告警
	 * @param alarms
	 */
	private void startAlarm(List<Sendto> alarms) {
		alarmManager = new ItuAlarmManager(convertFromDto(alarms));
		alarmManager.getDevice().forEach(itu->{
			itu.setConnectCallback(ituConnectCallback);
		});
		alarmManager.start();
	}

	private void startHistory(List<Sendto> historys) {
		if(CollectionUtils.isEmpty(historys))
			return ;
		historyManager = new ItuHistoryManager(convertFromDto(historys));
		historyManager.getDevice().forEach(itu->{
			itu.setConnectCallback(ituConnectCallback);
		});
		historyManager.start();
		historys.forEach(h->{
			try {
				quartz(h);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void startHistoryHttp(List<Sendto> historys) {
		if(CollectionUtils.isEmpty(historys))
			return ;
		historys.forEach(h->{
			try {
				quartz(h);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		});
	}

	private List<StringSocketConfig> convertFromDto(List<Sendto> dtos){
		if(CollectionUtils.isEmpty(dtos))
			return Lists.newArrayList();
		List<StringSocketConfig> configs = Lists.newArrayList();
		dtos.forEach(a->{
			StringSocketConfig c = new StringSocketConfig();
			int port = 0;
			try {
				port = Integer.parseInt(a.getPort());
				c.setIp(a.getIp()).setPort(port);
				if(!StringUtils.isEmpty(a.getCharset())) {
					try {
						c.setCharset(Charset.forName(a.getCharset()));
					} catch (Exception e) {	}
				}
				c.setName(a.getId());
				c.setId(a.getId());
				configs.add(c);
			} catch (Exception e) {
			}
		});
		return configs;
	}
	// 任务调度
    @Autowired
    private Scheduler scheduler;
    
	private void quartz(Sendto dto) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(SendTask.class).withIdentity(dto.getId(), dto.getIp()).build();
        //创建触发器,每5秒钟执行一次
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(dto.getId(), dto.getIp())
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(dto.getTimes()).repeatForever())
        .build();
 
//        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
//        Scheduler scheduler = schedulerFactory.getScheduler();
 
        //将任务及其触发器放入调度器
        scheduler.scheduleJob(jobDetail, trigger);
        //调度器开始调度任务
        scheduler.start();
    }
}
