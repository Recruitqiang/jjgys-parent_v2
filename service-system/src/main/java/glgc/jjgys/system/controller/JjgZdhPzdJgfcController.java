package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.base.JgCommonEntity;
import glgc.jjgys.model.project.JjgZdhPzd;
import glgc.jjgys.model.project.JjgZdhPzdJgfc;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgZdhPzdJgfcService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/jjg/jgfc/zdh/pzd")
@CrossOrigin
public class JjgZdhPzdJgfcController {

    @Autowired
    private JjgZdhPzdJgfcService jjgZdhPzdJgfcService;

    @ApiOperation("生成平整度鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody JgCommonEntity commonInfoVo) throws Exception {
        jjgZdhPzdJgfcService.generateJdb(commonInfoVo);

    }

    @ApiOperation("平整度模板文件导出")
    @GetMapping("exportpzd")
    public void exportpzd(HttpServletResponse response, String cdsl) throws IOException {
        jjgZdhPzdJgfcService.exportpzd(response,cdsl);
    }


    @ApiOperation(value = "平整度数据文件导入")
    @PostMapping("importpzd")
    public Result importpzd(@RequestParam("file") MultipartFile file, String proname) throws IOException, ParseException {
        jjgZdhPzdJgfcService.importpzd(file,proname);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgZdhPzdJgfc jjgZdhpzd) {
        //创建page对象
        Page<JjgZdhPzdJgfc> pageParam = new Page<>(current, limit);
        if (jjgZdhpzd != null) {
            QueryWrapper<JjgZdhPzdJgfc> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgZdhpzd.getProname());
            wrapper.like("htd", jjgZdhpzd.getHtd());

            //调用方法分页查询
            IPage<JjgZdhPzdJgfc> pageModel = jjgZdhPzdJgfcService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgZdhPzdJgfcService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }
    }


}

