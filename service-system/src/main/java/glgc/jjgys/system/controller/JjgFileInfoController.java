package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgFileInfo;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.system.Project;
import glgc.jjgys.system.service.JjgFileInfoService;
import glgc.jjgys.system.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Autowired
    private ProjectService projectService;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

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

    @ApiOperation("查询项目")
    @PostMapping("getxm")
    public Result getxm(){
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        List<Project> list = projectService.list(wrapper);
        return Result.ok(list);
    }

    //上传
    @ApiOperation("上传")
    @PostMapping("upload")
    public Result upload(@RequestParam("file") MultipartFile file, @RequestBody Project project) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.fail();
        }try {
            // 获取文件名
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String filepath = filespath+ File.separator+project.getProName() + File.separator + "设计图";
            // 设置文件存储路径
            Path uploadPath = Paths.get(filepath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // 处理文件上传
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            return Result.ok("文件上传成功!");
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail();
        }
    }

}

