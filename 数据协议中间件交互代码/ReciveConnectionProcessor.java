package com.macrounion.atv.socket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.macrounion.atv.listener.DataReceiveListener;
import com.macrounion.atv.socket.config.SocketConfig;
import com.macrounion.device.connect.socket.handler.ChannelHandlerFactory;
import com.macrounion.device.manager.assertview.AssertView4AlarmManager;
import com.macrounion.device.manager.assertview.AssertView4HistoryManager;
import com.macrounion.device.queue.DoQueuet;
import com.macrounion.device.queue.Queuet;
import com.macrounion.device.resolver.MuDeviceAttrResolver;
import com.macrounion.device.resolver.dto.MuDeviceAttrDetailDto;
import com.macrounion.device.resolver.dto.MuDeviceAttrDto;
import com.macrounion.device.resolver.itu.DDRAlarmResolver;
import com.macrounion.device.resolver.itu.MuOMMAlarmResolver;
import com.macrounion.device.resolver.itu.MuOMMHistoryResolver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

/**
 * 设备，开启socket
 * 
 * @author Administrator
 *
 */
@Component
public class ReciveConnectionProcessor {
	private Logger logger = LoggerFactory.getLogger(ReciveConnectionProcessor.class);
	@Autowired
	private SocketConfig socketConfig;
	@Autowired
	private DataReceiveListener dataReceiveListener;
	
	private int idleReaderIdleTime=1;
	private ExecutorService receiverThreadPool = Executors.newCachedThreadPool();

	private AssertView4HistoryManager assertView4HistoryManager;
	private AssertView4AlarmManager assertView4AlarmManager;
	private MuOMMHistoryResolver assertView4HistoryResolver = new MuOMMHistoryResolver(MuDeviceAttrDto.Source.ASSERT_VIEW);
	private MuOMMAlarmResolver assertView4AlarmResolver = new MuOMMAlarmResolver(MuDeviceAttrDto.Source.ASSERT_VIEW);
	private DDRAlarmResolver assetViewAlarmResolver =new DDRAlarmResolver(MuDeviceAttrDto.Source.ASSERT_VIEW);
	
	@PostConstruct
	public void init(){
		start();
		startQueue();
	}

	@PreDestroy
	public void destory(){
		close();
	}
	@Autowired
	private Queuet<MuDeviceAttrDto> queue;
	
	/**
	 * 报警数据频繁发送，缓存一段时间处理
	 * @return
	 */
	public boolean startQueue() {
		Predicate<List<MuDeviceAttrDto>> func = (d)->{
			if(CollectionUtils.isEmpty(d))
				return true;
			Map<String,List<MuDeviceAttrDto>> alarmMap =  new HashMap<>();
		    d.stream().collect(Collectors.groupingBy(MuDeviceAttrDto::getCode,Collectors.toList())).forEach(alarmMap::put);
		    alarmMap.forEach((x,dtos)->{
		    	MuDeviceAttrDto mt = new MuDeviceAttrDto();
	    		BeanUtils.copyProperties(dtos.get(0), mt);
	    		List<MuDeviceAttrDetailDto> details = Lists.newArrayList();
		    	dtos.forEach(detail->{
		    		details.addAll(detail.getDetails());
		    	});
		    	mt.setDetails(details);
		    	dataReceiveListener.receive("",mt);
		    });
            return true;
		};
		//允许失败3次
		new DoQueuet<MuDeviceAttrDto>(queue,func);
		return true;
	}

	private void assertViewReceive(String channelKey,MuDeviceAttrDto attrDto, String msg,String remoteAdress) {
		receiverThreadPool.execute(()->{
//			MuDeviceAttrDto attrDto = resolver.resolver(msg);
			attrDto.setRemoteAdress(remoteAdress);
			List<MuDeviceAttrDto> dtos = attrDto.convertGroupByDetails("^\\d[-](.*?)", attrDto);
			for(MuDeviceAttrDto d:dtos) {
				if(!d.getType().equals(MuDeviceAttrDto.Type.ALARM)) {
					dataReceiveListener.receive(channelKey,d);
				} else {
					queue.put(d);
				}
			}
		});
	}

	
	public boolean close() {
		if(assertView4HistoryManager!=null)
			assertView4HistoryManager.stop();
		if(assertView4AlarmManager!=null)
			assertView4AlarmManager.stop();
		return true;
	}

	public boolean start() {
		startSendto();
		startDataFrom();
		if(socketConfig==null)
			return true;
		startAssertView4();
		return true;
	}

	/**
	 * 数据来源连接
	 */
	private void startDataFrom() {
		// TODO Auto-generated method stub
		
	}

	private String channelAttrKey = "id";
	private void startAssertView4() {
		if(socketConfig.getAssertView4()==null)
			return;

		socketConfig.getAssertView4().getHistorys().forEach(config -> config.setChannelAttrKey(channelAttrKey).setIdleUnit(TimeUnit.MINUTES).setIdleReaderIdleTime(idleReaderIdleTime));
		assertView4HistoryManager = new AssertView4HistoryManager(socketConfig.getAssertView4().getHistorys());
		assertView4HistoryManager.setChannelHandlerFactory(new ItuChannelHandlerFactory(assertView4HistoryResolver));

		assertView4HistoryManager.start();

		socketConfig.getAssertView4().getAlarms().forEach(config -> config.setChannelAttrKey(channelAttrKey).setIdleUnit(TimeUnit.MINUTES).setIdleReaderIdleTime(idleReaderIdleTime));
		assertView4AlarmManager = new AssertView4AlarmManager(socketConfig.getAssertView4().getAlarms());
		List<MuDeviceAttrResolver> alarmResovers=Lists.newArrayList();
		alarmResovers.add(assetViewAlarmResolver);
		alarmResovers.add(assertView4AlarmResolver);
		assertView4AlarmManager.setChannelHandlerFactory(new ItuChannelHandlerFactory(alarmResovers));
//		assertView4AlarmManager.setChannelHandlerFactory(new ItuChannelHandlerFactory(assertView4AlarmResolver));

		assertView4AlarmManager.start();
	}

	/**
	 * 输出数据连接
	 */
	private void startSendto() {
		
	}

	class ItuChannelHandlerFactory implements ChannelHandlerFactory{
		private MuDeviceAttrResolver resolver;

		private List<MuDeviceAttrResolver> alarmRosvers;

		public ItuChannelHandlerFactory(MuDeviceAttrResolver resolver) {
			this.resolver = resolver;
		}

		public ItuChannelHandlerFactory(List<MuDeviceAttrResolver> resolvers){
			this.alarmRosvers=resolvers;
		}

		@Override
		public ChannelHandlerAdapter channelHandler() {
//			return new ItuSimpleChannelInboundHandler(this.resolver) ;
			if(CollectionUtils.isEmpty(this.alarmRosvers))
				return new ItuSimpleChannelInboundHandler(this.resolver) ;
			else
				return new ItuSimpleChannelInboundHandler(this.alarmRosvers) ;
		}
	}

	class ItuSimpleChannelInboundHandler extends SimpleChannelInboundHandler{
		private MuDeviceAttrResolver resolver;
		private List<MuDeviceAttrResolver> alarmRosvers;

		public ItuSimpleChannelInboundHandler(MuDeviceAttrResolver resolver) {
			this.resolver = resolver;
		}

		public ItuSimpleChannelInboundHandler(List<MuDeviceAttrResolver> alarmRosvers) {
			this.alarmRosvers = alarmRosvers;
		}

		/**
		 * 设置channelAttrKey对应的值
		 * @param ctx
		 * @throws Exception
		 */
		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			Channel channel = ctx.channel();
			channel.attr(AttributeKey.valueOf(channelAttrKey)).set(UUID.randomUUID().toString());
			super.channelRegistered(ctx);
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if(evt instanceof IdleStateEvent){
				heartBeatError((IdleStateEvent) evt);
				ctx.close();
			}
			super.userEventTriggered(ctx, evt);
		}
		

		private String getChannelKey(ChannelHandlerContext channelHandlerContext){
			Channel channel = channelHandlerContext.channel();
			String channelKey = (String) channel.attr(AttributeKey.valueOf(channelAttrKey)).get();
			return channelKey;
		}
		
		private String getRemoteAddress(ChannelHandlerContext channelHandlerContext) {
			try {
				String re = channelHandlerContext.channel().remoteAddress().toString();
				String address = re.substring(1);
				address  = address.substring(0, address.indexOf(":"));
//				System.out.println("remoteAddress->"+address);
				return address;
			}catch(Exception e) {
				return "";
			}
		}

		@Value("${datalog:0}")
		private int datalog;
		@Override
		protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
			MuDeviceAttrResolver wicResolver = this.resolver;
			MuDeviceAttrDto dto = null;
			if(datalog != 0)
				logger.error((String) msg);
			if(wicResolver != null)
				dto = this.resolver.resolver((String) msg);
			else if(wicResolver == null && !CollectionUtils.isEmpty(this.alarmRosvers)){
				for(MuDeviceAttrResolver r : this.alarmRosvers){
					dto = r.resolver((String) msg);
					if(dto!=null){
//						wicResolver=r;
						break;
					}
				}
			}
				
			assertViewReceive(getChannelKey(ctx),dto,(String) msg,getRemoteAddress(ctx));
		}
	}

	/**
	 * 网关心跳异常
	 * @param event
	 */
	private void heartBeatError(IdleStateEvent event){
		switch (event.state()){
			case READER_IDLE:
//				logger.error("READER_IDLE");
				break;
			case WRITER_IDLE:
				logger.error("WRITER_IDLE");
				break;
			case ALL_IDLE:
				logger.error("ALL_IDLE");
				break;
		}
	}
//	/**
//	 * 发送命令
//	 * @param currentData
//	 * @param cmd
//	 */
//	public Boolean sendCmd(CurrentData currentData, String cmd) {
//		StringBuilder content = new StringBuilder("<OMMHISTORY>");
//		content.append("<IP>"+currentData.getSpotIp()+"</IP>");
//		content.append("<CH"+currentData.getChannelCode()+">");
//		content.append("<N>"+currentData.getChannelName()+"</N>");
//		content.append("<V>"+cmd+"</V>");
//		content.append("<U>"+currentData.getUnit()+"</U>");
//		content.append("</CH"+currentData.getChannelCode()+"></OMMHISTORY>");
//		ChannelFuture channelFuture = assertView4HistoryManager.sendBySocketKey(currentData.getSocketKey(), content.toString());
//
//		return channelFuture!=null;
//	}

}
