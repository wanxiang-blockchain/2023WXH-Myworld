package com.macrounion.atv.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author: xct
 * @Date: 2021/7/30 17:06
 * @Description:
 */

@Configuration
public class MqttConsumerConfig {
    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.inbound.url}")
    private String hostUrl;

    @Value("${mqtt.inbound.clientId}")
    private String clientId;

    @Value("#{'${mqtt.inbound.topics}'.split(',')}")
    private String[] topics;

//    @Value("#{'${mqtt.inbound.qos}'.split(',')}")
//    private int[] qos;

    @Value("${mqtt.useMqtt}")
    private Boolean useMqtt;

    /**
     * 客户端对象
     */
    private MqttClient client;

    @Autowired
    private MqttConsumerCallBack mqttConsumerCallBack;

    /**
     * 在bean初始化后连接到服务器
     * @author xct
     * @param
     * @return void
     * @date 2021/7/30 16:48
     */
    @PostConstruct
    public void init(){
        if(useMqtt){
            connect();
        }
    }

    /**
     * 客户端连接服务端
     * @author xct
     * @param
     * @return void
     * @date 2021/7/30 16:01
     */
    public void connect(){
        try {
            //创建MQTT客户端对象
            client = new MqttClient(hostUrl,clientId,new MemoryPersistence());
            //连接设置
            MqttConnectOptions options = new MqttConnectOptions();
            //是否清空session，设置为false表示服务器会保留客户端的连接记录，客户端重连之后能获取到服务器在客户端断开连接期间推送的消息
            //设置为true表示每次连接到服务端都是以新的身份
            options.setCleanSession(true);
            //设置连接用户名
            options.setUserName(username);
            //设置连接密码
            options.setPassword(password.toCharArray());
            //设置超时时间，单位为秒
            options.setConnectionTimeout(30);
            //设置心跳时间 单位为秒，表示服务器每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线
            options.setKeepAliveInterval(60);
            //设置遗嘱消息的话题，若客户端和服务器之间的连接意外断开，服务器将发布客户端的遗嘱信息
            options.setWill("willTopic",(clientId + "与服务器断开连接").getBytes(),0,false);
            //设置回调
            client.setCallback(mqttConsumerCallBack);
            client.connect(options);
            //订阅主题
            //消息等级，和主题数组一一对应，服务端将按照指定等级给订阅了主题的客户端推送消息
            int[] qos = new int[topics.length];
            for(int i = 0; i < qos.length; ++i) {
                qos[i] = 1;
            }
            //主题
//            String[] topics = {"topic1","topic2"};
          	//订阅主题

            subscribe(topics,qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String[] topics, int[] qos) throws MqttException {
        client.subscribe(topics, qos);
    }

    /**
     * 断开连接
     * @author xct
     * @param
     * @return void
     * @date 2021/8/2 09:30
     */
    public void disConnect(){
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    /**
     * 订阅主题
     * @author xct
     * @param topic
     * @param qos
     * @return void
     * @date 2021/7/30 17:12
     */
    public void subscribe(String topic,int qos){
        try {
            client.subscribe(topic,qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
