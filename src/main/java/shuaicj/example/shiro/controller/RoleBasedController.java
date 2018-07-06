package shuaicj.example.shiro.controller;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A role-based controller which is protected by shiro.
 *
 * @author shuaicj 2018/06/29
 */
@RestController
public class RoleBasedController {

    @GetMapping("/admin-only")
    @RequiresRoles("admin")
    public String adminOnly() {
        return "Hello Admin!";
    }

    @GetMapping("/user-only")
    @RequiresRoles("user")
    public String userOnly() {
        return "Hello User!";
    }

    @GetMapping("/admin-or-user")
    @RequiresRoles(logical = Logical.OR, value = {"admin", "user"})
    public String adminOrUser() {
        return "Hello Admin/User!";
    }

    @GetMapping("/public-to-all")
    public String publicToAll() {
        return "Hello Stranger!";
    }
}
