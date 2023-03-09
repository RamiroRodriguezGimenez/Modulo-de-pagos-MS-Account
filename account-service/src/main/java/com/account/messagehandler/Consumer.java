package com.account.messagehandler;

import com.account.dto.request.TransactionRequest;
import com.account.service.AccountService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.gson.Gson;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.converter.GsonMessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
public class Consumer {

    @Autowired
    AccountService accountService;

    Logger logger =  LoggerFactory.getLogger(Consumer.class);

    @JmsListener(destination = "Send Money")
    public void recivemessage(String message) throws JMSException {

        TransactionRequest transaction = new Gson().fromJson(message,TransactionRequest.class);
        logger.info("mensaje recibido");
        accountService.executeSendMoney(transaction);

    }
}
