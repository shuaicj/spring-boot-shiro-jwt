package shuaicj.example.shiro.realm.file.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A permission-based controller which is protected by shiro.
 *
 * @author shuaicj 2018/07/05
 */
@RestController
public class PermissionBasedController {

    @PostMapping("/read")
    @RequiresPermissions("files:read")
    public String read() {
        return "read files";
    }

    @PostMapping("/write")
    @RequiresPermissions("files:write")
    public String write() {
        return "write files";
    }

    @PostMapping("/archive")
    @RequiresPermissions("files:archive")
    public String archive() {
        return "archive files";
    }

    @PostMapping("/read-log")
    @RequiresPermissions("files:read:log")
    public String readLog() {
        return "read log files";
    }

    @PostMapping("/write-log")
    @RequiresPermissions("files:write:log")
    public String writeLog() {
        return "write log files";
    }

    @PostMapping("/archive-log")
    @RequiresPermissions("files:archive:log")
    public String archiveLog() {
        return "archive log files";
    }
}
