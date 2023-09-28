package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.base.JgCommonEntity;
import glgc.jjgys.model.project.JjgZdhMcxs;
import glgc.jjgys.model.project.JjgZdhMcxsJgfc;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgZdhMcxsJgfcService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
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
@RequestMapping("/jjg/jgfc/zdh/mcxs")
@CrossOrigin
public class JjgZdhMcxsJgfcController {

    @Autowired
    private JjgZdhMcxsJgfcService jjgZdhMcxsJgfcService;

    @ApiOperation("生成摩擦系数鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody JgCommonEntity jgCommon) throws Exception {
        jjgZdhMcxsJgfcService.generateJdb(jgCommon);
    }



    @ApiOperation("摩擦系数模板文件导出")
    @GetMapping("exportmcxs")
    public void exportmcxs(HttpServletResponse response, String cdsl) throws IOException {
        jjgZdhMcxsJgfcService.exportmcxs(response,cdsl);
    }

    @ApiOperation(value = "摩擦系数数据文件导入")
    @PostMapping("importmcxs")
    public Result importmcxs(@RequestParam("file") MultipartFile file, String proname) throws IOException {
        jjgZdhMcxsJgfcService.importmcxs(file,proname);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgZdhMcxsJgfc jjgZdhMcxs) {
        //创建page对象
        Page<JjgZdhMcxsJgfc> pageParam = new Page<>(current, limit);
        if (jjgZdhMcxs != null) {
            QueryWrapper<JjgZdhMcxsJgfc> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgZdhMcxs.getProname());
            wrapper.like("htd", jjgZdhMcxs.getHtd());

            //调用方法分页查询
            IPage<JjgZdhMcxsJgfc> pageModel = jjgZdhMcxsJgfcService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @ApiOperation("批量删除摩擦系数数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgZdhMcxsJgfcService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

}

