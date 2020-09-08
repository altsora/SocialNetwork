package sn.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailSenderTest {

    @Autowired
    private MailSenderService mailSender;

    @Test
    public void mailServiceCreatedTest() {
        Assertions.assertNotNull(mailSender);
    }

//    @Test
//    public void mailSendTest()  {
//        Exception verifiableError = null;
//        try {
//            mailSender.send("test-sd1bkelkj@srv1.mail-tester.com", "testMail", "Hello, <b>group!</b>");
//        } catch (Exception ex){
//            ex.printStackTrace();
//            verifiableError = ex;
//        }
//
//        Assertions.assertNull(verifiableError);
//    }

}
