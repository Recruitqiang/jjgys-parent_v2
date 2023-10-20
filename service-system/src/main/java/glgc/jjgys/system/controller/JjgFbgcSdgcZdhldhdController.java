package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgFbgcSdgcZdhldhd;
import glgc.jjgys.model.project.JjgZdhLdhd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.system.service.JjgFbgcSdgcZdhldhdService;
import glgc.jjgys.system.service.OperLogService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-10-23
 */
@RestController
@RequestMapping("/jjg/fbgc/sdgc/zdhldhd")
public class JjgFbgcSdgcZdhldhdController {

    @Autowired
    private JjgFbgcSdgcZdhldhdService jjgFbgcSdgcZdhldhdService;

    @Autowired
    private OperLogService operLogService;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletResponse response, String proname, String htd) throws IOException {
        String fileName = "52隧道雷达厚度.xlsx";
        String p = filespath+ File.separator+proname+File.separator+htd+File.separator+fileName;
        File file = new File(p);
        if (file.exists()){
            JjgFbgcCommonUtils.download(response,p,fileName);
        }
    }

    @ApiOperation("生成雷达厚度鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody CommonInfoVo commonInfoVo) throws Exception {
        jjgFbgcSdgcZdhldhdService.generateJdb(commonInfoVo);

    }

    @ApiOperation("查看雷达厚度鉴定结果")
    @PostMapping("lookJdbjg")
    public Result lookJdbjg(@RequestBody CommonInfoVo commonInfoVo) throws IOException {
        List<Map<String,Object>> jdjg = jjgFbgcSdgcZdhldhdService.lookJdbjg(commonInfoVo);
        return Result.ok(jdjg);

    }

    @ApiOperation("雷达厚度模板文件导出")
    @GetMapping("exportldhd")
    public void exportldhd(HttpServletResponse response,String cdsl) throws IOException {
        jjgFbgcSdgcZdhldhdService.exportldhd(response,cdsl);
    }


    @ApiOperation(value = "雷达厚度数据文件导入")
    @PostMapping("importldhd")
    public Result importldhd(@RequestParam("file") MultipartFile file, CommonInfoVo commonInfoVo) throws IOException, ParseException {
        jjgFbgcSdgcZdhldhdService.importldhd(file,commonInfoVo);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgFbgcSdgcZdhldhd jjgZdhLdhd) {
        //创建page对象
        Page<JjgFbgcSdgcZdhldhd> pageParam = new Page<>(current, limit);
        if (jjgZdhLdhd != null) {
            QueryWrapper<JjgFbgcSdgcZdhldhd> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgZdhLdhd.getProname());
            wrapper.like("htd", jjgZdhLdhd.getHtd());

            //调用方法分页查询
            IPage<JjgFbgcSdgcZdhldhd> pageModel = jjgFbgcSdgcZdhldhdService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }


    @ApiOperation("批量删除雷达厚度数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgFbgcSdgcZdhldhdService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

    @ApiOperation("根据id查询")
    @GetMapping("getmcxs/{id}")
    public Result getmcxs(@PathVariable String id) {
        JjgFbgcSdgcZdhldhd user = jjgFbgcSdgcZdhldhdService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改雷达厚度数据")
    @PostMapping("update")
    public Result update(@RequestBody JjgFbgcSdgcZdhldhd user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgFbgcSdgcZdhldhdService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setTitle("雷达厚度");
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

