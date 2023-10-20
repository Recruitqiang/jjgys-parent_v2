package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.base.JgCommonEntity;
import glgc.jjgys.model.project.JjgZdhCzJgfc;
import glgc.jjgys.model.project.JjgZdhGzsd;
import glgc.jjgys.model.project.JjgZdhGzsdJgfc;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgZdhGzsdJgfcService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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
@RequestMapping("/jjg/jgfc/zdh/gzsd")
@CrossOrigin
public class JjgZdhGzsdJgfcController {

    @Autowired
    private JjgZdhGzsdJgfcService jjgZdhGzsdJgfcService;

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletResponse response, String proname, String htd) throws IOException {
        List<Map<String,Object>> lxlist = jjgZdhGzsdJgfcService.selectlx(proname,htd);
        List<String> fileName = new ArrayList<>();
        for (Map<String, Object> map : lxlist) {
            String lxbs = map.get("lxbs").toString();
            if (lxbs.equals("主线")){
                fileName.add("20路面构造深度");;
            }else {
                fileName.add("63互通构造深度-"+lxbs);
            }
        }
        String zipname = "构造深度鉴定表";
        JjgFbgcCommonUtils.batchDowndFile(response,zipname,fileName,jgfilepath+ File.separator+proname+File.separator+htd);
    }

    @ApiOperation("生成构造深度鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody JgCommonEntity commonInfoVo) throws Exception {
        jjgZdhGzsdJgfcService.generateJdb(commonInfoVo);

    }

    @ApiOperation("构造深度模板文件导出")
    @GetMapping("exportgzsd")
    public void exportgzsd(HttpServletResponse response,@RequestParam String cd) throws IOException {
        jjgZdhGzsdJgfcService.exportgzsd(response,cd);
    }


    @ApiOperation(value = "构造深度数据文件导入")
    @PostMapping("importgzsd")
    public Result importgzsd(@RequestParam("file") MultipartFile file, String proname) throws IOException, ParseException {
        jjgZdhGzsdJgfcService.importgzsd(file,proname);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgZdhGzsdJgfc jjgZdhGzsd) {
        //创建page对象
        Page<JjgZdhGzsdJgfc> pageParam = new Page<>(current, limit);
        if (jjgZdhGzsd != null) {
            QueryWrapper<JjgZdhGzsdJgfc> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgZdhGzsd.getProname());
            wrapper.like("htd", jjgZdhGzsd.getHtd());

            //调用方法分页查询
            IPage<JjgZdhGzsdJgfc> pageModel = jjgZdhGzsdJgfcService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }


    @ApiOperation("批量删除构造深度数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgZdhGzsdJgfcService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }


}

