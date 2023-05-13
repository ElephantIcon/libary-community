package com.coder.community.service;

import com.coder.community.dao.AlphaDao;
import com.coder.community.dao.DiscussPostMapper;
import com.coder.community.dao.UserMapper;
import com.coder.community.entity.DiscussPost;
import com.coder.community.entity.User;
import com.coder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

@Service
//@Scope("prototype") "Singleton"
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /*public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init() {
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("销毁AlphaService");
    }*/

    public String find() {
        return alphaDao.select();
    }


    /**
     * REQUIRED   支持当前事务（外部事物），如果不存在则创建新事务
     * REQUIRED_NE  创建一个新事物，并且暂停当前事务（外务事务）
     * NESTED       如果当前存在事务（外务事务），则嵌套在该事务中执行（独立的提交和回滚），否则就会和Required一样。
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 1 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        // 2 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("Hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        Integer.valueOf("abc");

        return "ok";
    }

    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute((TransactionCallback<Object>) status -> {
            // 1 新增用户
            User user = new User();
            user.setUsername("beta");
            user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
            user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
            user.setEmail("beta@qq.com");
            user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
            user.setCreateTime(new Date());
            userMapper.insertUser(user);
            // 2 新增帖子
            DiscussPost post = new DiscussPost();
            post.setUserId(user.getId());
            post.setTitle("你好");
            post.setContent("我是新人！");
            post.setCreateTime(new Date());
            discussPostMapper.insertDiscussPost(post);

            Integer.valueOf("abc");

            return "ok";
        });
    }

}
