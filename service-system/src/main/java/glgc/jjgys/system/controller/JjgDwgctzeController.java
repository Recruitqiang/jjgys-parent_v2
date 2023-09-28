package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgDwgctze;
import glgc.jjgys.model.project.JjgFbgcJtaqssJabx;
import glgc.jjgys.model.project.JjgLqsJgSfz;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.system.service.JjgDwgctzeService;
import glgc.jjgys.system.service.OperLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
 * @since 2023-09-25
 */
@RestController
@RequestMapping("/jjg/dwgctze")
public class JjgDwgctzeController {

    @Autowired
    private JjgDwgctzeService jjgDwgctzeService;

    @Autowired
    private OperLogService operLogService;

    @ApiOperation("单位工程投资额文件导出")
    @GetMapping("export")
    public void export(HttpServletResponse response){
        jjgDwgctzeService.export(response);
    }

    @ApiOperation(value = "单位工程投资额数据文件导入")
    @PostMapping("importtze")
    public Result importtze(@RequestParam("file") MultipartFile file,String proname) {
        jjgDwgctzeService.importtze(file,proname);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgDwgctze jjgDwgctze) {
        //创建page对象
        Page<JjgDwgctze> pageParam = new Page<>(current, limit);
        if (jjgDwgctze != null) {
            QueryWrapper<JjgDwgctze> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgDwgctze.getProname());
            //调用方法分页查询
            IPage<JjgDwgctze> pageModel = jjgDwgctzeService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @ApiOperation("批量删除单位工程投资额数")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgDwgctzeService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

    @ApiOperation("根据id查询")
    @GetMapping("getJabx/{id}")
    public Result getJabx(@PathVariable String id) {
        JjgDwgctze user = jjgDwgctzeService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改竣工投资额数据")
    @PostMapping("update")
    public Result update(@RequestBody JjgDwgctze user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgDwgctzeService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setFbgc("-");
            sysOperLog.setTitle("竣工投资额数据");
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

