package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgLqsJgHntlmzd;
import glgc.jjgys.model.project.JjgLqsJgSfz;
import glgc.jjgys.model.project.JjgSfz;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.system.service.JjgLqsJgSfzService;
import glgc.jjgys.system.service.JjgLqsSfzService;
import glgc.jjgys.system.service.OperLogService;
import io.swagger.annotations.Api;
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
 * @since 2023-09-22
 */
@Api(tags = "竣工匝道")
@RestController
@RequestMapping("/project/jg/sfz")
public class JjgLqsJgSfzController {

    @Autowired
    private JjgLqsJgSfzService jjgLqsJgSfzService;

    @Autowired
    private OperLogService operLogService;

    @ApiOperation("收费站清单文件导出")
    @GetMapping("export")
    public void exportSD(HttpServletResponse response){
        jjgLqsJgSfzService.exportSD(response);
    }

    @ApiOperation(value = "收费站清单导入")
    @PostMapping("import")
    public Result importQL(@RequestParam("file") MultipartFile file, @RequestParam String proname) {
        jjgLqsJgSfzService.importSD(file,proname);
        return Result.ok();
    }

    /**
     * 分页查询
     * @param current
     * @param limit
     * @param jjgSfz
     * @return
     */
    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgLqsJgSfz jjgSfz){
        //创建page对象
        Page<JjgLqsJgSfz> pageParam=new Page<>(current,limit);
        //判断projectQueryVo对象是否为空，直接查全部
        if(jjgSfz == null){
            IPage<JjgLqsJgSfz> pageModel = jjgLqsJgSfzService.page(pageParam,null);
            return Result.ok(pageModel);
        }else {
            //获取条件值，进行非空判断，条件封装
            String sdname = jjgSfz.getZdsfzname();
            QueryWrapper<JjgLqsJgSfz> wrapper=new QueryWrapper<>();
            if (!StringUtils.isEmpty(sdname)){
                wrapper.like("name",sdname);
            }
            wrapper.like("proname",jjgSfz.getProname());
            wrapper.orderByDesc("create_time");
            //调用方法分页查询
            IPage<JjgLqsJgSfz> pageModel = jjgLqsJgSfzService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);

        }
    }

    @ApiOperation("批量删除收费站清单信息")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean ql = jjgLqsJgSfzService.removeByIds(idList);
        if(ql){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

    @ApiOperation("根据id查询")
    @GetMapping("getJabx/{id}")
    public Result getJabx(@PathVariable String id) {
        JjgLqsJgSfz user = jjgLqsJgSfzService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改竣工收费站数据")
    @PostMapping("update")
    public Result update(@RequestBody JjgLqsJgSfz user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgLqsJgSfzService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setFbgc("-");
            sysOperLog.setTitle("竣工收费站数据");
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

