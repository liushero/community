package com.futurhero.community.controller;

import com.futurhero.community.annotation.LoginRequired;
import com.futurhero.community.bean.User;
import com.futurhero.community.service.FollowService;
import com.futurhero.community.service.LikeService;
import com.futurhero.community.service.UserService;
import com.futurhero.community.util.CommunityUtil;
import com.futurhero.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/setting")
    @LoginRequired
    public String getSetting() {
        return "/site/setting";
    }

    @GetMapping("/profile/{userId}")
    @LoginRequired
    public String getProfile(Model model, @PathVariable("userId") int userId) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("likeCount", likeService.findUserLikeCount(userId));
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId));
        model.addAttribute("followerCount", followService.getFollowerCount(userId));
        model.addAttribute("followStatus", followService.getFollowUserStatus(hostHolder.getUser().getId(), userId));
        return "/site/profile";
    }

    @PostMapping("/upload")
    @LoginRequired
    public String uploadHeader(Model model, MultipartFile headerImage) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (suffix.isEmpty()) {
            model.addAttribute("error", "图片格式不正确");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.getStringUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        User user = hostHolder.getUser();
        // headerUrl = http://localhost:8080/community/user/header/{fileName}
        userService.setHeaderUrlById(user.getId(), domain + contextPath + "/user/header/" + fileName);
        return "redirect:/index";
    }

    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                // 语法糖，自动关闭流
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("读取头像失败: " + e.getMessage());
        }
    }

    @PostMapping("/password")
    @LoginRequired
    public String modifyPassword(Model model, String origin, String now) {
        User user = hostHolder.getUser();
        String password = CommunityUtil.getMD5(origin, user.getSalt());
        if (!password.equals(user.getPassword())) {
            model.addAttribute("originError", "原密码不正确");
            return "/site/setting";
        }
        password = CommunityUtil.getMD5(now, user.getSalt());
        if (password.equals(user.getPassword())) {
            model.addAttribute("nowError", "新密码不能与原密码相同");
            return "/site/setting";
        }
        userService.setPasswordById(user.getId(), password);
        // 修改密码后退出登录
        return "redirect:/logout";
    }
}
