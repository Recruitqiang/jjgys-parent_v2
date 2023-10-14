package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgZdhCz;
import glgc.jjgys.model.project.JjgZdhCzJgfc;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgZdhCzJgfcService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
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
@RequestMapping("/jjg/jgfc/zdh/cz")
public class JjgZdhCzJgfcController {

    @Autowired
    private JjgZdhCzJgfcService jjgZdhCzJgfcService;

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;

    @ApiOperation("生成车辙鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(String proname,String sjz) throws Exception {
        jjgZdhCzJgfcService.generateJdb(proname,sjz);

    }

    @ApiOperation("车辙模板文件导出")
    @GetMapping("exportcz")
    public void exportcz(HttpServletResponse response, @RequestParam String cd) throws IOException {
        jjgZdhCzJgfcService.exportcz(response,cd);
    }


    @ApiOperation(value = "车辙数据文件导入")
    @PostMapping("importcz")
    public Result importcz(@RequestParam("file") MultipartFile file,String proname) throws IOException {
        jjgZdhCzJgfcService.importcz(file,proname);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgZdhCzJgfc jjgZdhCz) {
        //创建page对象
        Page<JjgZdhCzJgfc> pageParam = new Page<>(current, limit);
        if (jjgZdhCz != null) {
            QueryWrapper<JjgZdhCzJgfc> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgZdhCz.getProname());
            //wrapper.like("htd", jjgZdhCz.getHtd());

            //调用方法分页查询
            IPage<JjgZdhCzJgfc> pageModel = jjgZdhCzJgfcService.page(pageParam, wrapper);
            System.out.println(pageModel);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @ApiOperation("批量删除竣工车辙数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgZdhCzJgfcService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

}

