package com.aicode.controller;

import com.aicode.common.result.Result;
import com.aicode.common.util.SecurityUtil;
import com.aicode.entity.User;
import com.aicode.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/profile")
    public Result<User> getProfile() {
        return Result.success(authService.getCurrentUser(SecurityUtil.getCurrentUserId()));
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody UpdateProfileRequest req) {
        return Result.success(authService.updateProfile(
                SecurityUtil.getCurrentUserId(), req.getUsername(), req.getAvatar()));
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest req) {
        authService.changePassword(SecurityUtil.getCurrentUserId(),
                req.getOldPassword(), req.getNewPassword());
        return Result.success();
    }

    @Data
    public static class UpdateProfileRequest {
        private String username;
        private String avatar;
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
