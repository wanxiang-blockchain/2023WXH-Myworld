package com.macrounion.atv.mqtt;

import com.macrounion.atv.cache.SendtoDeviceCache;
import com.macrounion.atv.mqtt.handle.CPEDataSubscribe;
import com.macrounion.atv.mqtt.handle.N1DataSubscribe;
import com.macrounion.atv.mqtt.handle.PopDataSubscribe;
import com.macrounion.atv.mqtt.handle.PopStatusSubscribe;
import com.macrounion.atv.service.entity.SendtoDevice;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 */
@Component
public class MqttConsumerCallBack implements MqttCallback {

    @Autowired
	private PopDataSubscribe popDataSubscribe;

    @Autowired
    private N1DataSubscribe n1DataSubscribe;
    @Autowired
    private CPEDataSubscribe cpeDataSubscribe;

    @Autowired
    private SendtoDeviceCache sendtoDeviceCache;

    @Autowired
    private PopStatusSubscribe popStatusSubscribe;

    /**
     * 客户端断开连接的回调
     * @param throwable
     * @return void
     * @date 2021/7/30 17:14
     */
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("与服务器断开连接，可重连");
    }

    /**
     * 消息到达的回调
     * @param topic
     * @param message
     * @return void
     * @date 2021/7/30 17:14
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(String.format("接收消息主题 : %s",topic));
        System.out.println(String.format("接收消息Qos : %d",message.getQos()));
        System.out.println(String.format("接收消息内容 : %s",new String(message.getPayload())));
        System.out.println(String.format("接收消息retained : %b",message.isRetained()));

        String msg= new String(message.getPayload());

        try {
            if(topic.contains("/pop/msg/put")) {
                popDataSubscribe.handlePopData(topic,msg);
                return;
            }
            if(topic.contains("/device/netinfo")){
//                n1DataSubscribe.handleData(topic,msg);
                cpeDataSubscribe.handleCpeData(topic,msg);
                return;
            }
            if(topic.contains("/pop/status/put")){
                popStatusSubscribe.handlePopStatusData(topic,msg);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息发布成功的回调
     * @param iMqttDeliveryToken
     * @return void
     * @date 2021/7/30 17:14
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}

