package com.baidu.listener;

import com.baidu.service.search.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class ItemSearchListener implements MessageListener {

    @Resource
    private ItemSearchService itemSearchService;

    /**
     * 获取消息体-消费消息
     * @param message
     */
    @Override
    public void onMessage(Message message) {

        try {
            //获取消息体
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("消费者service-search获取的id："+id);

            //消费消息
            itemSearchService.addItemToSolr(Long.parseLong(id));

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
