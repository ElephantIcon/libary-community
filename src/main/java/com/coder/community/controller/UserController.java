package com.coder.community.controller;

import com.coder.community.annotation.LoginRequired;
import com.coder.community.entity.User;
import com.coder.community.service.UserService;
import com.coder.community.util.CommunityUtil;
import com.coder.community.util.CookieUtil;
import com.coder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        // 1 判断头像文件是否为空
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片！");
            return "site/setting";
        }
        // 2 获取头像图片的 名字, 判断图片格式
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确");
            return "site/setting";
        }

        // 3 生成随机文件名，确定文件存放的路径
        filename = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + filename); // 开启一个文件，确定文件存放路径
        try {
            // .transferTo(dest) - 将收到的文件传输到给定的目标文件
            // 参数：dest – 目标文件（通常为绝对文件）
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

        // 4 更新当前用户的头像路径(web访问路径)
        // 如：http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * 获取图片
     * @param fileName 图片名
     * @param response 响应对象
     */
    @RequestMapping(path = "header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片的格式
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName)
        ) {
            // 设置一个缓冲区 buffer
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePsw(String oldPassword, String newPassword, String repeatPassword, Model model, HttpServletRequest request) {
        User user = hostHolder.getUser();
        // 1 验证原密码
        // 1.1获取加密密码
        String oldPsw = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPsw)) {
            model.addAttribute("oldMsg", "原密码不正确!");
            return "site/setting";
        }
        // 2 检查新密码
        if (newPassword.length() < 4 || newPassword.length() > 24) {
            model.addAttribute("newMsg", "新密码长度不能小于8或大于24！");
            return "site/setting";
        }
        if (!newPassword.equals(repeatPassword)) {
            model.addAttribute("repeatMsg", "两次输入的密码不一致！");
            return "site/setting";
        }
        // 3 更新密码 存入库
        String newPsw = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPsw);
        // 4 退出登录,重新登录
        // 4.1 从Cookie中获取凭证ticket, 并登出
        String ticket = CookieUtil.getValue(request, "ticket");
        userService.logout(ticket);

        // 5 返回消息
        model.addAttribute("msg", "您的密码已经修改成功！");
        model.addAttribute("target", "/login");

        return "/site/operate-result";
    }

}
