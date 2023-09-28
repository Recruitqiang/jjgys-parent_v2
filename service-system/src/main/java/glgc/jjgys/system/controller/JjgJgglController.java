package glgc.jjgys.system.controller;

import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgFbgcJagcService;
import glgc.jjgys.system.service.JjgJgglService;
import glgc.jjgys.system.service.OperLogService;
import glgc.jjgys.system.utils.JjgFbgcUtils;
import io.swagger.annotations.ApiOperation;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/jjg/fbgc/jagc")
public class JjgJgglController {

    @Autowired
    private JjgJgglService jjgJgglService;


    //新规范
    @ApiOperation("路桥隧文件导出(新标准)")
    @GetMapping("exportnew")
    public void exportnew(HttpServletResponse response){
        jjgJgglService.exportnew(response);
    }

    //新规范
    @ApiOperation("路桥隧文件导入(新标准)")
    @PostMapping("importnew")
    public void importnew(@RequestParam("file") MultipartFile file,
                          @RequestParam String projectname) throws IOException {
        jjgJgglService.importnew(file,projectname);
    }

    //新规范
    @ApiOperation("路桥隧文件导出(旧标准)")
    @GetMapping("exportold")
    public void exportold(HttpServletResponse response){
        jjgJgglService.exportold(response);
    }

    //新规范
    @ApiOperation("路桥隧文件导入(旧标准)")
    @PostMapping("importold")
    public void importold(@RequestParam("file") MultipartFile file,
                          @RequestParam String projectname) throws IOException {
        jjgJgglService.importold(file,projectname);
    }



}
