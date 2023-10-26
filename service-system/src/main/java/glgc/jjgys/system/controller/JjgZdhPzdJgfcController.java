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
@RequestMapping("/jjg/jgfc/zdh/pzd")
@CrossOrigin
public class JjgZdhPzdJgfcController {

    @Autowired
    private JjgZdhPzdJgfcService jjgZdhPzdJgfcService;

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;

    @ApiOperation("查看平均值")
    @GetMapping("lookpjz")
    public Result lookpjz(@RequestParam String proname) throws IOException {
        List<Map<String,Object>> list = jjgZdhPzdJgfcService.lookpjz(proname);
        return Result.ok(list);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletResponse response, String proname) throws IOException {

        List<Map<String,Object>> htdList = jjgZdhPzdJgfcService.selecthtd(proname);
        List<String> fileName = new ArrayList<>();
        if (htdList!=null){
            for (Map<String, Object> map1 : htdList) {
                String htd = map1.get("htd").toString();
                List<Map<String,Object>> lxlist = jjgZdhPzdJgfcService.selectlx(proname,htd);
                for (Map<String, Object> map : lxlist) {
                    String lxbs = map.get("lxbs").toString();
                    if (lxbs.equals("主线")){
                        fileName.add(htd+File.separator+"18路面平整度");
                    }else {
                        fileName.add(htd+File.separator+"61互通平整度-"+lxbs);
                    }
                }
            }
        }
        String zipname = "车辙鉴定表";
        JjgFbgcCommonUtils.batchDowndFile(response,zipname,fileName,jgfilepath+ File.separator+proname);
    }

    @ApiOperation("生成平整度鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody JgCommonEntity commonInfoVo) throws Exception {
        jjgZdhPzdJgfcService.generateJdb(commonInfoVo);

    }

    @ApiOperation("平整度模板文件导出")
    @GetMapping("exportpzd")
    public void exportpzd(HttpServletResponse response, @RequestParam String cd) throws IOException {
        jjgZdhPzdJgfcService.exportpzd(response,cd);
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

