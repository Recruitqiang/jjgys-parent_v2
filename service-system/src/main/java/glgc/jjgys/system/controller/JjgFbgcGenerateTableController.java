package glgc.jjgys.system.controller;


import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgFbgcGenerateTablelService;
import glgc.jjgys.system.service.JjgHtdService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-03-01
 */
@RestController
@RequestMapping("/jjg/fbgc/generate/table")
public class JjgFbgcGenerateTableController {

    @Autowired
    private JjgHtdService jjgHtdService;

    @Autowired
    private JjgFbgcGenerateTablelService jjgFbgcGenerateTablelService;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

    @ApiOperation("生成评定表")
    @PostMapping("generatePdb")
    //public Result generatePdb(@RequestParam(value = "proname") String proname ,@RequestBody List<String> htds) throws IOException {
    public Result generatePdb(@RequestBody Map<String,Object> htds) throws IOException {
        if (htds.containsKey("proname") && htds.containsKey("htds")){
            String proname = htds.get("proname").toString();
            String[] htdss = htds.get("htds").toString().replace("[", "").replace("]", "").split(",");
            for (String htd : htdss) {
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(htd.trim());
                jjgFbgcGenerateTablelService.generatePdb(commonInfoVo);
            }
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    @RequestMapping(value = "/downloadpdb", method = RequestMethod.POST)
    public void downloadpdb(HttpServletRequest request,HttpServletResponse response, @RequestBody Map<String,Object> htds) throws IOException {
        String fileName = "00评定表.xlsx";
        String proname = htds.get("proname").toString();
        String[] htdss = htds.get("htds").toString().replace("[", "").replace("]", "").split(",");
        List<String> pathname = new ArrayList<>();
        for (String s : htdss) {
            String p = filespath+ File.separator+proname+File.separator+s.trim()+File.separator+fileName;
            File file = new File(p);
            if (file.exists()){
                pathname.add(p);
            }
        }
        String zipName = "评定表";
        JjgFbgcCommonUtils.downloadDifferentPathFile(request,response,zipName,filespath+ File.separator+proname,pathname);

    }


    @ApiOperation("生成建设项目质量评定表")
    @PostMapping("generateJSZLPdb")
    public Result generateJSZLPdb(@RequestBody CommonInfoVo commonInfoVo) throws IOException {
        jjgFbgcGenerateTablelService.generateJSZLPdb(commonInfoVo);
        return Result.ok();
    }


    @RequestMapping(value = "/downloadjsxm", method = RequestMethod.GET)
    public void downloadjsxm(HttpServletResponse response, String proname) throws IOException {
        String fileName = "建设项目质量评定表.xlsx";
        String p = filespath+ File.separator+proname+File.separator+fileName;
        File file = new File(p);
        if (file.exists()){
            JjgFbgcCommonUtils.download(response,p,fileName);
        }
    }

    @ApiOperation("报告中表格")
    @PostMapping("generateBGZBG")
    public Result generateBGZBG(@RequestBody CommonInfoVo commonInfoVo) throws IOException {
        String proname = commonInfoVo.getProname();
        jjgFbgcGenerateTablelService.generateBGZBG(proname);
        return Result.ok();
    }

    @RequestMapping(value = "/downloadbgzbg", method = RequestMethod.GET)
    public void downloadbgzbg(HttpServletResponse response, @RequestParam String proname) throws IOException {
        System.out.println(proname);
        String fileName = "报告中表格.xlsx";
        String p = filespath+ File.separator+proname+File.separator+fileName;
        File file = new File(p);
        if (file.exists()){
            JjgFbgcCommonUtils.download(response,p,fileName);
        }
    }
}

