package com.coder.community;

import com.coder.community.service.AlphaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1() {
        // Object obj = alphaService.save1();
        // System.out.println("obj = " + obj);
    }

    @Test
    public void testSave2() {
         /*Object obj = alphaService.save2();
         System.out.println("obj = " + obj);*/
    }
}
