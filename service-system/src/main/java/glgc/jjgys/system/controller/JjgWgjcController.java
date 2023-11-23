package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgWgjc;
import glgc.jjgys.model.project.JjgWgkf;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.system.service.JjgWgjcService;
import glgc.jjgys.system.service.OperLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-11-19
 */
@RestController
@RequestMapping("/jjg/wgjc")
public class JjgWgjcController {

    @Autowired
    private JjgWgjcService jjgWgjcService;

    @Autowired
    private OperLogService operLogService;

    @ApiOperation("外观检查文件导出")
    @GetMapping("export")
    public void export(HttpServletResponse response, @RequestParam String projectname){
        jjgWgjcService.export(response,projectname);
    }

    @ApiOperation(value = "外观检查数据文件导入")
    @PostMapping("importwgjc")
    public Result importwgkf(@RequestParam("file") MultipartFile file, String proname) {
        jjgWgjcService.importwgjc(file,proname);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgWgjc jjgWgjc) {
        //创建page对象
        Page<JjgWgjc> pageParam = new Page<>(current, limit);
        if (jjgWgjc != null) {
            QueryWrapper<JjgWgjc> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgWgjc.getProname());
            //调用方法分页查询
            IPage<JjgWgjc> pageModel = jjgWgjcService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @ApiOperation("批量删除外观检查数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgWgjcService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

    @ApiOperation("根据id查询")
    @GetMapping("getJabx/{id}")
    public Result getJabx(@PathVariable String id) {
        JjgWgjc user = jjgWgjcService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改外观检查数据")
    @PostMapping("update")
    public Result update(@RequestBody JjgWgjc user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgWgjcService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setFbgc("-");
            sysOperLog.setTitle("竣工外观扣分数据");
            sysOperLog.setBusinessType("修改");
            sysOperLog.setOperName(JwtHelper.getUsername(request.getHeader("token")));
            sysOperLog.setOperIp(IpUtil.getIpAddress(request));
            sysOperLog.setOperTime(new Date());
            operLogService.saveSysLog(sysOperLog);
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

}

