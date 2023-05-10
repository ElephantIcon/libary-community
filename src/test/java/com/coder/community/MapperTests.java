package com.coder.community;

import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.dao.LoginTicketMapper;
import com.coder.community.dao.UserMapper;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.LoginTicket;
import com.coder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println("user = " + user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        User user1 = userMapper.selectByEmail("nowcoder1@sina.com");
        System.out.println(user1);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123123");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        int rows = userMapper.updateStatus(150, 1);
        System.out.println("rows = " + rows);

        rows = userMapper.updateHeader(150, "http://images.nowcoder.com/head/150t.png");
        System.out.println("rows = " + rows);

        rows = userMapper.updatePassword(150, "hello");
        System.out.println("rows = " + rows);
    }

    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        System.out.println("discussPosts = " + discussPosts.size());
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int rows = discussPostMapper.selectDiscussPostRows(149);
        System.out.println("rows = " + rows);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
//        LoginTicket loginTicket = loginTicketMapper.seltectByTicket("abc");
//        System.out.println("loginTicket = " + loginTicket);
//
//        loginTicketMapper.updateStatus("abc", 1);
//        loginTicket = loginTicketMapper.seltectByTicket("abc");
//        System.out.println("update after loginTicket = " + loginTicket);
    }
}
