package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgFbgcLmgcLmgzsdsgpsf;
import glgc.jjgys.model.project.JjgFbgcLmgcLmgzsdsgpsfJgfc;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgFbgcLmgcLmgzsdsgpsfJgfcService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-09-23
 */
@RestController
@RequestMapping("/jjg/jgfc/lmgzsdsgpsf")
public class JjgFbgcLmgcLmgzsdsgpsfJgfcController {

    @Autowired
    private JjgFbgcLmgcLmgzsdsgpsfJgfcService jjgFbgcLmgcLmgzsdsgpsfJgfcService;

    @ApiOperation("生成构造深度手工铺沙法鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(String proname) throws Exception {
        jjgFbgcLmgcLmgzsdsgpsfJgfcService.generateJdb(proname);

    }

    @ApiOperation("构造深度手工铺沙法模板文件导出")
    @GetMapping("exportlmgzsdsgpsf")
    public void exportlmgzsdsgpsf(HttpServletResponse response){
        jjgFbgcLmgcLmgzsdsgpsfJgfcService.exportlmgzsdsgpsf(response);
    }


    @ApiOperation(value = "构造深度手工铺沙法数据文件导入")
    @PostMapping("importlmgzsdsgpsf")
    public Result importlmgzsdsgpsf(@RequestParam("file") MultipartFile file, String proname) {
        jjgFbgcLmgcLmgzsdsgpsfJgfcService.importlmgzsdsgpsf(file,proname);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgFbgcLmgcLmgzsdsgpsfJgfc jjgFbgcLmgcLmgzsdsgpsf){
        //创建page对象
        Page<JjgFbgcLmgcLmgzsdsgpsfJgfc> pageParam=new Page<>(current,limit);
        if (jjgFbgcLmgcLmgzsdsgpsf != null){
            QueryWrapper<JjgFbgcLmgcLmgzsdsgpsfJgfc> wrapper=new QueryWrapper<>();
            wrapper.like("proname",jjgFbgcLmgcLmgzsdsgpsf.getProname());
            wrapper.like("htd",jjgFbgcLmgcLmgzsdsgpsf.getHtd());
            wrapper.like("fbgc",jjgFbgcLmgcLmgzsdsgpsf.getFbgc());
            Date jcsj = jjgFbgcLmgcLmgzsdsgpsf.getJcsj();
            if (!StringUtils.isEmpty(jcsj)){
                wrapper.like("jcsj",jcsj);
            }
            //调用方法分页查询
            IPage<JjgFbgcLmgcLmgzsdsgpsfJgfc> pageModel = jjgFbgcLmgcLmgzsdsgpsfJgfcService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @ApiOperation("批量删构造深度手工铺沙法数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgFbgcLmgcLmgzsdsgpsfJgfcService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }
    }



}

