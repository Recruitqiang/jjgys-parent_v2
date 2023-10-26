package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgFbgcJtaqssJabx;
import glgc.jjgys.model.project.JjgJgHtdinfo;
import glgc.jjgys.model.project.JjgJgjcsj;
import glgc.jjgys.model.project.JjgLqsQl;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.system.service.JjgJgHtdinfoService;
import glgc.jjgys.system.service.JjgJgjcsjService;
import glgc.jjgys.system.service.OperLogService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-09-25
 */
@RestController
@RequestMapping("/jjg/fbgc/jgjcsj")
public class JjgJgjcsjController {

    @Autowired
    private JjgJgjcsjService jjgJgjcsjService;

    @Autowired
    private OperLogService operLogService;


    @Autowired
    private JjgJgHtdinfoService jjgJgHtdinfoService;

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;

    @ApiOperation("导出交工检测数据")
    @GetMapping("exportjgjcdata")
    public void exportjgjcdata(HttpServletResponse response, String proname){
        jjgJgjcsjService.exportjgjcdata(response,proname);
    }

    @ApiOperation("导入交工检测数据")
    @PostMapping("importjgsj")
    public void importjgsj(@RequestParam("file") MultipartFile file,
                           @RequestParam String projectname) {
        jjgJgjcsjService.importjgsj(file,projectname);
    }

    @ApiOperation("生成新规范评定表")
    @PostMapping("generatepdbNew")
    public void generatepdbNew(@RequestParam String proname) {
        jjgJgjcsjService.generatepdb(proname);
    }

    @ApiOperation("生成旧规范评定表")
    @PostMapping("generatepdb")
    public void generatepdbOld(@RequestParam String proname) {
        jjgJgjcsjService.generatepdbOld(proname);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam String proname) throws IOException {
        String fileName = "00评定表.xlsx";
        QueryWrapper<JjgJgHtdinfo> wrapper=new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgJgHtdinfo> list = jjgJgHtdinfoService.list(wrapper);

        /*String proname = htds.get("proname").toString();
        String[] htdss = htds.get("htds").toString().replace("[", "").replace("]", "").split(",");*/
        List<String> pathname = new ArrayList<>();
        for (JjgJgHtdinfo s : list) {
            String p = jgfilepath + File.separator + proname + File.separator + s.getName() + File.separator + fileName;
            File file = new File(p);
            if (file.exists()) {
                pathname.add(p);
            }
        }
        String zipName = "评定表";
        JjgFbgcCommonUtils.downloadDifferentPathFile(request, response, zipName, jgfilepath + File.separator + proname, pathname);
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgJgjcsj jgjcsj){
        //创建page对象
        Page<JjgJgjcsj> pageParam=new Page<>(current,limit);
        //判断projectQueryVo对象是否为空，直接查全部
        if(jgjcsj == null){
            IPage<JjgJgjcsj> pageModel = jjgJgjcsjService.page(pageParam,null);
            return Result.ok(pageModel);
        }else {
            //获取条件值，进行非空判断，条件封装
            String proname = jgjcsj.getProname();
            QueryWrapper<JjgJgjcsj> wrapper=new QueryWrapper<>();
            wrapper.like("proname",proname);
            wrapper.orderByDesc("create_time");
            //调用方法分页查询
            IPage<JjgJgjcsj> pageModel = jjgJgjcsjService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);

        }
    }

    @ApiOperation("批量删除信息")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean ql = jjgJgjcsjService.removeByIds(idList);
        if(ql){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

    @ApiOperation("根据id查询")
    @GetMapping("getjcsj/{id}")
    public Result getjcsj(@PathVariable String id) {
        JjgJgjcsj user = jjgJgjcsjService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改数据")
    @PostMapping("update")
    public Result update(@RequestBody JjgJgjcsj user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgJgjcsjService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setFbgc(user.getFbgc());
            sysOperLog.setTitle("竣工数据");
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

