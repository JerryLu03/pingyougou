package com.baidu.listener;

import com.baidu.service.staticpage.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class PageListener implements MessageListener {

    @Resource
    private StaticPageService staticPageService;

    /**
     * 获取消息-消费消息
     * @param message
     */
    @Override
    public void onMessage(Message message) {

        try {
            //获取消息
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            System.out.println("消费者service-page获取id:"+id);
            //消费消息
            staticPageService.getHtml(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
