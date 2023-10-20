package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgFbgcSdgcZdhcz;
import glgc.jjgys.model.project.JjgZdhCz;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.system.service.JjgFbgcSdgcZdhczService;
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
 * @since 2023-10-23
 */
@RestController
@RequestMapping("/jjg/fbgc/sdgc/zdhcz")
public class JjgFbgcSdgcZdhczController {

    @Autowired
    private JjgFbgcSdgcZdhczService jjgFbgcSdgcZdhczService;

    @Autowired
    private OperLogService operLogService;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletResponse response, String proname, String htd) throws IOException {
        String fileName = "45隧道路面车辙.xlsx";
        String p = filespath+ File.separator+proname+File.separator+htd+File.separator+fileName;
        File file = new File(p);
        if (file.exists()){
            JjgFbgcCommonUtils.download(response,p,fileName);
        }
    }

    @ApiOperation("生成隧道路面车辙鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody CommonInfoVo commonInfoVo) throws Exception {
        jjgFbgcSdgcZdhczService.generateJdb(commonInfoVo);

    }

    @ApiOperation("查看隧道路面车辙鉴定结果")
    @PostMapping("lookJdbjg")
    public Result lookJdbjg(@RequestBody CommonInfoVo commonInfoVo) throws IOException {
        List<Map<String,Object>> jdjg = jjgFbgcSdgcZdhczService.lookJdbjg(commonInfoVo);
        return Result.ok(jdjg);

    }

    @ApiOperation("隧道路面车辙模板文件导出")
    @GetMapping("exportcz")
    public void exportcz(HttpServletResponse response,@RequestParam String cd) throws IOException {
        jjgFbgcSdgcZdhczService.exportcz(response,cd);
    }


    @ApiOperation(value = "隧道路面车辙数据文件导入")
    @PostMapping("importcz")
    public Result importcz(@RequestParam("file") MultipartFile file, CommonInfoVo commonInfoVo) throws IOException, ParseException {
        jjgFbgcSdgcZdhczService.importcz(file,commonInfoVo);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgFbgcSdgcZdhcz jjgZdhCz) {
        //创建page对象
        Page<JjgFbgcSdgcZdhcz> pageParam = new Page<>(current, limit);
        if (jjgZdhCz != null) {
            QueryWrapper<JjgFbgcSdgcZdhcz> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgZdhCz.getProname());
            wrapper.like("htd", jjgZdhCz.getHtd());

            //调用方法分页查询
            IPage<JjgFbgcSdgcZdhcz> pageModel = jjgFbgcSdgcZdhczService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }


    @ApiOperation("批量删除隧道路面车辙数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgFbgcSdgcZdhczService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

    @ApiOperation("根据id查询")
    @GetMapping("getmcxs/{id}")
    public Result getmcxs(@PathVariable String id) {
        JjgFbgcSdgcZdhcz user = jjgFbgcSdgcZdhczService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改隧道路面车辙数据")
    @PostMapping("update")
    public Result update(@RequestBody JjgFbgcSdgcZdhcz user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgFbgcSdgcZdhczService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setTitle("隧道路面车辙");
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

