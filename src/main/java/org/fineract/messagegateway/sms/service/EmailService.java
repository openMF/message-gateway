package org.fineract.messagegateway.sms.service;

import org.fineract.messagegateway.MessageGateway;
import org.fineract.messagegateway.sms.data.EmailRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;


@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.host}")
    private String smtpHost;

    @Value("${spring.mail.port}")
    private int smtpPort;

    @Value("${spring.mail.username}")
    private String smtpUsername;

    @Value("${spring.mail.password}")
    private String smtpPassword;




    public void sendEmail(EmailRequestDTO emailRequest, String callbackUrl) {
        boolean flag = false;
        String error = null;
        try{
        ((JavaMailSenderImpl) mailSender).setHost(smtpHost);
        ((JavaMailSenderImpl) mailSender).setPort(smtpPort);
        ((JavaMailSenderImpl) mailSender).setUsername(smtpUsername);
        ((JavaMailSenderImpl) mailSender).setPassword(smtpPassword);
        Properties mailProperties = ((JavaMailSenderImpl) mailSender).getJavaMailProperties();
        mailProperties.put("mail.smtp.connectiontimeout", 5000);
        mailProperties.put("mail.smtp.timeout", 5000);
        mailProperties.put("mail.smtp.writetimeout", 5000);
        ((JavaMailSenderImpl) mailSender).setJavaMailProperties(mailProperties);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailRequest.getTo().toArray(new String[0]));
        message.setSubject(emailRequest.getSubject());
        message.setText(emailRequest.getBody());
        mailSender.send(message);
        flag = true;

    } catch (Exception e) {
        error = e.getMessage();
    }
        RestTemplate restTemplate = new RestTemplate();

        if(flag)
        {
            String requestBody = "Email sent successfully to {to}";
            restTemplate.postForObject(callbackUrl, requestBody.replace("{to}",
                    emailRequest.getTo().toString()), String.class);
            logger.info("Email sent to: " + emailRequest.getTo());
        }
        else {
            String requestBody = "Email could not be sent to {to} because of {error} ";
            requestBody = requestBody.replace("{error}",error);
            restTemplate.postForObject(callbackUrl, requestBody.replace("{to}",
                    emailRequest.getTo().toString()), String.class);
            logger.info("Email failed to be sent because {}", error);

        }



    }
    private ClientHttpRequestFactory createTimeoutRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return factory;
    }


    }
