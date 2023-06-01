package com.coder.community.util;

import com.coder.community.entity.User;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * 持有用户对象，用于代替session对象
 */
@Component
public class HostHolder {

    private final ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
