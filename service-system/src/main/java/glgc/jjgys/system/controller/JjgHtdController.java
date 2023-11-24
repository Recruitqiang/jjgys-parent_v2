package glgc.jjgys.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgHtd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.system.Project;
import glgc.jjgys.system.service.JjgHtdService;
import glgc.jjgys.system.utils.JjgFbgcUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "合同段")
@RestController
@Transactional
@RequestMapping("/project/info/htd")
public class JjgHtdController {

    @Autowired
    private JjgHtdService jjgHtdService;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;


    @ApiOperation("批量删除合同段信息")
    @Transactional
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        jjgHtdService.removeData(idList);
        jjgHtdService.removeByIds(idList);
        return Result.ok();

    }

    @ApiOperation("添加合同段")
    @PostMapping("save")
    public Result save(@RequestBody JjgHtd jjgHtd) {
        boolean res = jjgHtdService.addhtd(jjgHtd);
        return Result.ok();
    }

    @ApiOperation("校验合同段")
    @GetMapping("checkHtd")
    public Result checkProname(@RequestBody CommonInfoVo commonInfoVo) {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        QueryWrapper<JjgHtd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgHtd> list = jjgHtdService.list(wrapper);
        if (list!=null && !list.isEmpty()){
            return Result.ok().message("校验成功");
        }else {
            return Result.fail().message("校验失败");
        }
    }



    @ApiOperation("删除")
    @PostMapping("remove")
    public Result remove(@RequestBody JjgHtd jjgHtd) {
        jjgHtdService.removeById(jjgHtd.getId());
        return Result.ok();
    }

    @ApiOperation("合同段模板文件导出")
    @GetMapping("exporthtd")
    public void exporthtd(HttpServletResponse response, @RequestParam String proname, String[] htd) {
        String filepath = System.getProperty("user.dir");
        jjgHtdService.exporthtd(response,filepath,proname,htd);
        String zipName = proname+"合同段模板文件";
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
            JjgFbgcUtils.zipFile(filepath+"/"+proname+"合同段模板文件",response.getOutputStream());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JjgFbgcUtils.deleteDirAndFiles(new File(filepath+"/"+proname+"合同段模板文件"));

    }
    @ApiOperation("合同段模板数据文件导入")
    @PostMapping("importhtd")
    public Result importhtd(@RequestParam("file") MultipartFile file, @RequestParam String proname) {

        CommonInfoVo commonInfoVo=new CommonInfoVo();
        commonInfoVo.setProname(proname);
        String filepath = System.getProperty("user.dir");
        File file1=JjgFbgcUtils.multipartFileToFile(file);
        ZipFile zipFile= null;
        try {
            zipFile = new ZipFile(file1);
            zipFile.setFileNameCharset("GBK");
            JjgFbgcUtils.createDirectory("暂存", filepath);
            zipFile.extractAll(filepath + "/暂存");
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }

        jjgHtdService.importhtd(file,commonInfoVo,filepath+"/暂存");
        file1.delete();
        return Result.ok();

    }

    @ApiOperation("生成合同段鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestParam String proname,String[] htds) throws Exception {
        CommonInfoVo commonInfoVo=new CommonInfoVo();
        commonInfoVo.setProname(proname);
        jjgHtdService.generateJdb(commonInfoVo,htds);

    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletResponse response, @RequestParam String proname,String[] htds) throws Exception {
        //生成鉴定表
        CommonInfoVo commonInfoVo=new CommonInfoVo();
        commonInfoVo.setProname(proname);
        jjgHtdService.generateJdb(commonInfoVo,htds);
        String workpath=filespath+ File.separator+proname+File.separator+"合同段鉴定表";
        //复制鉴定表文件
        boolean a = jjgHtdService.generateZip(proname,htds);
        if (a){
            //下载
            String fileName = proname+"合同段鉴定表.zip";
            fileName=URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" +fileName);
            response.setContentType("application/zip;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            //压缩文件
            try {
                JjgFbgcUtils.zipFile(workpath,response.getOutputStream());
            } catch (ZipException e) {
                e.printStackTrace();
            }
            JjgFbgcUtils.deleteDirAndFiles(new File(workpath));
        }

    }

    /**
     * 分页查询
     */
    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgHtd jjgHtd){
        //创建page对象
        Page<JjgHtd> pageParam=new Page<>(current,limit);
        //判断projectQueryVo对象是否为空，直接查全部
        if(jjgHtd == null){
            IPage<JjgHtd> pageModel = jjgHtdService.page(pageParam,null);
            return Result.ok(pageModel);
        }else {
            //获取条件值，进行非空判断，条件封装
            String jjgHtdName = jjgHtd.getName();
            QueryWrapper<JjgHtd> htdWrapper=new QueryWrapper<>();
            if (!StringUtils.isEmpty(jjgHtdName)){
                htdWrapper.like("name",jjgHtdName);
            }
            htdWrapper.like("proname",jjgHtd.getProname());
            htdWrapper.orderByDesc("create_time");
            //调用方法分页查询
            IPage<JjgHtd> pageModel = jjgHtdService.page(pageParam, htdWrapper);
            //返回
            return Result.ok(pageModel);

        }
    }

}
