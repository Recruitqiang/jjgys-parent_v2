package glgc.jjgys.system.controller;


import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgHtd;
import glgc.jjgys.system.service.JjgDpkshService;
import glgc.jjgys.system.service.JjgFbgcGenerateWordService;
import glgc.jjgys.system.service.JjgHtdService;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
@RequestMapping("/jjg/dpksh")
public class JjgDpkshController {

    @Autowired
    private JjgDpkshService jjgDpkshService;



    @ApiOperation("获取合同段信息")
    @PostMapping("gethtd")
    public Result gethtd(String proname){
        List<JjgHtd> res = jjgDpkshService.gethtd(proname);
        return Result.ok(res);

    }

    @ApiOperation("获取桥梁隧道等数量")
    @PostMapping("getnum")
    public Result getnum(String proname){
        Map<String, Map<String, Map<String, Object>>> res = jjgDpkshService.getnum(proname);
        return Result.ok(res);

    }

    @ApiOperation("单位工程")
    @PostMapping("getdwgc")
    public Result getdwgc(String proname) throws IOException {
        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> getdwgc = jjgDpkshService.getdwgc(proname);
        return Result.ok(getdwgc);

    }

    @ApiOperation("合同段")
    @PostMapping("gethtddata")
    public Result gethtddata(String proname) throws IOException {
        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> gethtddata = jjgDpkshService.gethtddata(proname);
        return Result.ok(gethtddata);

    }

    @ApiOperation("建设项目")
    @PostMapping("getjsxmdata")
    public Result getjsxmdata(String proname) throws IOException {
        Map<String, Map<String, List<Map<String, Object>>>> getjsxmdata = jjgDpkshService.getjsxmdata(proname);
        return Result.ok(getjsxmdata);

    }
}

