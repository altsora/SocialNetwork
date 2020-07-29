package sn;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sn.service.MailSenderService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MailSenderTest {

    @Autowired
    private MailSenderService mailSender;

    @Test
    public void mailServiceCreatedTest() {
        Assert.assertNotNull(mailSender);
    }

    @Test
    public void mailSendTest() throws Exception {
        Exception verifiableError = null;
        try {
            mailSender.send("fyrklod@gmail.com", "testMail", "Hello, <b>Fyrklod</b>!");
        } catch (Exception ex){
            ex.printStackTrace();
            verifiableError = ex;
        }

        Assert.assertNull(verifiableError);
    }

}
