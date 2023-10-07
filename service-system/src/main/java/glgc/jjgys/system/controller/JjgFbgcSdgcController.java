package glgc.jjgys.system.controller;

import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgFbgcQlgcService;
import glgc.jjgys.system.service.JjgFbgcSdgcService;
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
@RequestMapping("/jjg/fbgc/sdgc")
@CrossOrigin
public class JjgFbgcSdgcController {

    @Autowired
    private JjgFbgcSdgcService jjgFbgcSdgcService;

    @Autowired
    private OperLogService operLogService;


    @Value(value = "${jjgys.path.filepath}")
    private String filespath;


    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletResponse response, String proname, String htd) throws IOException {
        String fileName = "隧道工程鉴定表.zip";

        String workpath=filespath+ File.separator+proname+File.separator+htd;
        jjgFbgcSdgcService.download(response,fileName,proname,htd,filespath+ File.separator+proname+File.separator+htd);
        try {
            fileName=URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        response.setHeader("Content-disposition", "attachment; filename=" +fileName);
        response.setContentType("application/zip;charset=utf-8");
        //response.setCharacterEncoding("utf-8");
        try {
            JjgFbgcUtils.zipFile(workpath+"/隧道工程",response.getOutputStream());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JjgFbgcUtils.deleteDirAndFiles(new File(workpath+"/隧道工程"));

    }
    @ApiOperation("生成隧道工程鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody CommonInfoVo commonInfoVo) throws Exception {
        jjgFbgcSdgcService.generateJdb(commonInfoVo);

    }
    @ApiOperation("隧道工程模板文件导出")
    @GetMapping("exportsdgc")
    public void exportsdgc(HttpServletResponse response) {
        jjgFbgcSdgcService.exportsdgc(response,filespath);
        String zipName = "隧道工程指标模板文件";
        String downloadName = null;

        try {
            downloadName = URLEncoder.encode(zipName + ".zip", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


        response.setHeader("Content-disposition", "attachment; filename=" + downloadName);
        response.setContentType("application/zip;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        try {
            JjgFbgcUtils.zipFile(filespath+"/隧道工程",response.getOutputStream());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JjgFbgcUtils.deleteDirAndFiles(new File(filespath+"/隧道工程"));

    }
    @ApiOperation("隧道工程模板数据文件导入")
    @PostMapping("importsdgc")
    public Result importsdgc(@RequestParam("file") MultipartFile file, CommonInfoVo commonInfoVo) {
        File file1=JjgFbgcUtils.multipartFileToFile(file);
        ZipFile zipFile= null;
        String tempath = filespath+File.separator+commonInfoVo.getProname();
        try {
            zipFile = new ZipFile(file1);
            zipFile.setFileNameCharset("GBK");
            JjgFbgcUtils.createDirectory("暂存", tempath);
            zipFile.extractAll(tempath + "/暂存");
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }

        jjgFbgcSdgcService.importsdgc(commonInfoVo,tempath+"/暂存");
        file1.delete();
        return Result.ok();

    }
}
