package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgJgjcsj;
import glgc.jjgys.model.project.JjgLqsQl;
import glgc.jjgys.system.service.JjgJgjcsjService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-09-25
 */
@RestController
@RequestMapping("/jjg/fbgc/jgjcsj")
public class JjgJgjcsjController {

    @Autowired
    private JjgJgjcsjService jjgJgjcsjService;

    @ApiOperation("导出交工检测数据")
    @GetMapping("exportjgjcdata")
    public void exportjgjcdata(HttpServletResponse response, String proname){
        jjgJgjcsjService.exportjgjcdata(response,proname);
    }

    @ApiOperation("导入交工检测数据")
    @PostMapping("importjgsj")
    public void importjgsj(@RequestParam("file") MultipartFile file,
                           @RequestParam String projectname) {
        jjgJgjcsjService.importjgsj(file,projectname);
    }

    @ApiOperation("生成评定表")
    @PostMapping("generatepdb")
    public void generatepdb(@RequestParam String projectname) {
        jjgJgjcsjService.generatepdb(projectname);
    }


    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgJgjcsj jgjcsj){
        //创建page对象
        Page<JjgJgjcsj> pageParam=new Page<>(current,limit);
        //判断projectQueryVo对象是否为空，直接查全部
        if(jgjcsj == null){
            IPage<JjgJgjcsj> pageModel = jjgJgjcsjService.page(pageParam,null);
            return Result.ok(pageModel);
        }else {
            //获取条件值，进行非空判断，条件封装
            String proname = jgjcsj.getProname();
            QueryWrapper<JjgJgjcsj> wrapper=new QueryWrapper<>();
            wrapper.like("proname",proname);
            wrapper.orderByDesc("create_time");
            //调用方法分页查询
            IPage<JjgJgjcsj> pageModel = jjgJgjcsjService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);

        }
    }

    @ApiOperation("批量删除桥梁清单信息")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean ql = jjgJgjcsjService.removeByIds(idList);
        if(ql){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

}

