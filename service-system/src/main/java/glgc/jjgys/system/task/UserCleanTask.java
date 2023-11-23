package glgc.jjgys.system.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.model.system.SysUser;
import glgc.jjgys.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UserCleanTask {

    @Autowired
    private SysUserService sysUserService;

    // 定时任务方法，每天凌晨1点执行一次
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanExpiredUser() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        List<SysUser> list = sysUserService.list();
        for (SysUser sysUser : list) {
            LocalDateTime expire = sysUser.getExpire();
            if (expire.plusMinutes(1).isBefore(now)) {
                sysUserService.removeById(sysUser.getId());
            }
        }
    }

}
