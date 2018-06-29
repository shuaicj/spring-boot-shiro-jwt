package shuaicj.example.shiro.realm.file;

import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple controller which is protected by shiro.
 *
 * @author shuaicj 2018/06/29
 */
@RestController
public class SecuredController {

    @GetMapping("/admin")
    @RequiresRoles("admin")
    public String admin() {
        return "Hello Admin!";
    }

    @GetMapping("/user")
    @RequiresRoles("user")
    public String user() {
        return "Hello User!";
    }

    @GetMapping("/guest")
    @RequiresRoles("guest")
    @RequiresGuest
    public String guest() {
        return "Hello Guest!";
    }
}
