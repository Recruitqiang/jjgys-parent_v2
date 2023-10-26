package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgFbgcLmgcTlmxlbgc;
import glgc.jjgys.model.project.JjgFbgcLmgcTlmxlbgcJgfc;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgFbgcLmgcTlmxlbgcJgfcService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
 * @since 2023-09-23
 */
@RestController
@RequestMapping("/jjg/jgfc/tlmxlbgc")
public class JjgFbgcLmgcTlmxlbgcJgfcController {

    @Autowired
    private JjgFbgcLmgcTlmxlbgcJgfcService jjgFbgcLmgcTlmxlbgcJgfcService;

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletResponse response, String proname) throws IOException {
        List<Map<String,Object>> htdList = jjgFbgcLmgcTlmxlbgcJgfcService.selecthtd(proname);
        List<String> fileName = new ArrayList<>();
        if (htdList!=null){
            for (Map<String, Object> map1 : htdList) {
                String htd = map1.get("htd").toString();
                fileName.add(htd+File.separator+"17混凝土路面相邻板高差");
            }
        }
        String zipname = "混凝土路面相邻板高差";
        JjgFbgcCommonUtils.batchDowndFile(response,zipname,fileName,jgfilepath+ File.separator+proname);
    }

    @ApiOperation("生成相邻板高差鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody CommonInfoVo commonInfoVo) throws Exception {
        String proname = commonInfoVo.getProname();
        jjgFbgcLmgcTlmxlbgcJgfcService.generateJdb(proname);

    }

    @ApiOperation("相邻板高差模板文件导出")
    @GetMapping("exportxlbgs")
    public void exportxlbgs(HttpServletResponse response){
        jjgFbgcLmgcTlmxlbgcJgfcService.exportxlbgs(response);
    }


    @ApiOperation(value = "相邻板高差数据文件导入")
    @PostMapping("importxlbgs")
    public Result importxlbgs(@RequestParam("file") MultipartFile file, String proname) {
        jjgFbgcLmgcTlmxlbgcJgfcService.importxlbgs(file,proname);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgFbgcLmgcTlmxlbgcJgfc jjgFbgcLmgcTlmxlbgc){
        //创建page对象
        Page<JjgFbgcLmgcTlmxlbgcJgfc> pageParam=new Page<>(current,limit);
        if (jjgFbgcLmgcTlmxlbgc != null){
            QueryWrapper<JjgFbgcLmgcTlmxlbgcJgfc> wrapper=new QueryWrapper<>();
            wrapper.like("proname",jjgFbgcLmgcTlmxlbgc.getProname());

            Date jcsj = jjgFbgcLmgcTlmxlbgc.getJcsj();
            if (!StringUtils.isEmpty(jcsj)){
                wrapper.like("jcsj",jcsj);
            }
            //调用方法分页查询
            IPage<JjgFbgcLmgcTlmxlbgcJgfc> pageModel = jjgFbgcLmgcTlmxlbgcJgfcService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @ApiOperation("批量删除相邻板高差数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgFbgcLmgcTlmxlbgcJgfcService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }
    }

}

