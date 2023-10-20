package glgc.jjgys.system.controller;


import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgFileInfo;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgFileInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 文件资源表 前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-10-15
 */
@RestController
@RequestMapping("/jjg/file/info")
public class JjgFileInfoController {

    @Autowired
    private JjgFileInfoService jjgFileInfoService;

    @ApiOperation("查看文件列表")
    @GetMapping("filelist")
    public Result getfilelist() {
        List<JjgFileInfo> list = jjgFileInfoService.getfilelist();
        return Result.ok(list);

    }

    //下载
    @ApiOperation("下载")
    @PostMapping("download")
    public void download(HttpServletResponse response, @RequestBody List<JjgFileInfo> list) throws IOException {
        jjgFileInfoService.download(response,list);

    }



}

