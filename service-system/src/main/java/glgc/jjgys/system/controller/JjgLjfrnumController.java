package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgJanum;
import glgc.jjgys.model.project.JjgLjfrnum;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.system.service.JjgLjfrnumService;
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
 * @since 2023-10-21
 */
@RestController
@RequestMapping("/jjg/ljfrnum")
public class JjgLjfrnumController {

    @Autowired
    private JjgLjfrnumService jjgLjfrnumService;

    @Autowired
    private OperLogService operLogService;

    @ApiOperation("文件导出")
    @GetMapping("export/{projectname}")
    public void export(HttpServletResponse response, @PathVariable String projectname){
        jjgLjfrnumService.export(response,projectname);
    }


    @ApiOperation(value = "信息导入")
    @PostMapping("importlj")
    public Result importlj(@RequestParam("file") MultipartFile file, @RequestParam String proname) {
        jjgLjfrnumService.importlj(file,proname);
        return Result.ok();
    }


    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgLjfrnum janum){
        //创建page对象
        Page<JjgLjfrnum> pageParam=new Page<>(current,limit);
        //判断projectQueryVo对象是否为空，直接查全部
        if(janum == null){
            IPage<JjgLjfrnum> pageModel = jjgLjfrnumService.page(pageParam,null);
            return Result.ok(pageModel);
        }else {
            //获取条件值，进行非空判断，条件封装
            String name = janum.getProname();
            QueryWrapper<JjgLjfrnum> wrapper=new QueryWrapper<>();
            wrapper.eq("proname",name);
            wrapper.orderByDesc("create_time");
            //调用方法分页查询
            IPage<JjgLjfrnum> pageModel = jjgLjfrnumService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);

        }
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean ql = jjgLjfrnumService.removeByIds(idList);
        if(ql){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }
    }

    @ApiOperation("根据id查询")
    @GetMapping("getxmjd/{id}")
    public Result getxmjd(@PathVariable String id) {
        JjgLjfrnum user = jjgLjfrnumService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改")
    @PostMapping("update")
    public Result update(@RequestBody JjgLjfrnum user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgLjfrnumService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setFbgc(user.getFbgc());
            sysOperLog.setTitle("路基数量信息");
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

