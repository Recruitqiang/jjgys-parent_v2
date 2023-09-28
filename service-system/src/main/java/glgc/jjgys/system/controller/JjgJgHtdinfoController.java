package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgHtd;
import glgc.jjgys.model.project.JjgJgHtdinfo;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.service.JjgHtdService;
import glgc.jjgys.system.service.JjgJgHtdinfoService;
import glgc.jjgys.system.utils.JjgFbgcUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-09-22
 */
@Api(tags = "竣工合同段")
@RestController
@RequestMapping("/project/jg/htd")
public class JjgJgHtdinfoController {

    @Autowired
    private JjgJgHtdinfoService jjgJgHtdinfoService;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;


    @ApiOperation("批量删除合同段信息")
    @Transactional
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        jjgJgHtdinfoService.removeByIds(idList);
        return Result.ok();

    }

    @ApiOperation("添加合同段")
    @PostMapping("save")
    public Result save(@RequestBody JjgJgHtdinfo jjgHtd) {
        jjgJgHtdinfoService.addhtd(jjgHtd);
        return Result.ok();
    }



    @ApiOperation("删除")
    @PostMapping("remove")
    public Result remove(@RequestBody JjgJgHtdinfo jjgHtd) {
        jjgJgHtdinfoService.removeById(jjgHtd.getId());
        return Result.ok();
    }


    /**
     * 分页查询
     */
    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgJgHtdinfo jjgHtd){
        //创建page对象
        Page<JjgJgHtdinfo> pageParam=new Page<>(current,limit);
        //判断projectQueryVo对象是否为空，直接查全部
        if(jjgHtd == null){
            IPage<JjgJgHtdinfo> pageModel = jjgJgHtdinfoService.page(pageParam,null);
            return Result.ok(pageModel);
        }else {
            //获取条件值，进行非空判断，条件封装
            String jjgHtdName = jjgHtd.getName();
            QueryWrapper<JjgJgHtdinfo> htdWrapper=new QueryWrapper<>();
            if (!StringUtils.isEmpty(jjgHtdName)){
                htdWrapper.like("name",jjgHtdName);
            }
            htdWrapper.like("proname",jjgHtd.getProname());
            htdWrapper.orderByDesc("create_time");
            //调用方法分页查询
            IPage<JjgJgHtdinfo> pageModel = jjgJgHtdinfoService.page(pageParam, htdWrapper);
            //返回
            return Result.ok(pageModel);

        }
    }

}

