package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.project.JjgJgProjectinfo;
import glgc.jjgys.model.system.Project;
import glgc.jjgys.model.vo.ProjectQueryVo;
import glgc.jjgys.system.service.JjgJgProjectinfoService;
import glgc.jjgys.system.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-09-20
 */
@RestController
@Transactional
@RequestMapping("/jjg/jggl")
public class JjgJgProjectinfoController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JjgJgProjectinfoService jjgJgProjectinfoService;

    @ApiOperation("校验项目")
    @GetMapping("checkProname/{proname}")
    public Result checkProname(@PathVariable String proname) {
        QueryWrapper<JjgJgProjectinfo> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgJgProjectinfo> list = jjgJgProjectinfoService.list(wrapper);
        if (list!=null && !list.isEmpty()){
            return Result.ok().message("校验成功");
        }else {
            return Result.fail().message("校验失败,该项目已存在");
        }
    }

    @PostMapping("/add")
    public Result add(@RequestBody JjgJgProjectinfo project){
        String proname = project.getProname();
        //如果交工中有该项目，同步一下路桥隧数据，若没有只创建项目就行
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<Project> list = projectService.list(wrapper);
        if (list!=null && !list.isEmpty()){
            //同步路桥隧数据
            jjgJgProjectinfoService.getlqsData(proname);
        }
        jjgJgProjectinfoService.save(project);
        return Result.ok();

    }

    @ApiOperation("批量删除项目信息")
    @Transactional
    @DeleteMapping("removeBatch")
    //传json数组[1,2,3]，用List接收
    public Result removeBeatch(@RequestBody List<String> idList){
        jjgJgProjectinfoService.deleteInfo(idList);
        return Result.ok();
    }

    /**
     * 分页查询
     */
    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody ProjectQueryVo projectQueryVo){
        //创建page对象
        Page<JjgJgProjectinfo> pageParam=new Page<>(current,limit);
        //判断projectQueryVo对象是否为空，直接查全部
        if(projectQueryVo == null){
            IPage<JjgJgProjectinfo> pageModel = jjgJgProjectinfoService.page(pageParam,null);
            return Result.ok(pageModel);
        }else {
            //获取条件值，进行非空判断，条件封装
            String proName = projectQueryVo.getProName();
            QueryWrapper<JjgJgProjectinfo> wrapper=new QueryWrapper<>();
            if (!StringUtils.isEmpty(proName)){
                wrapper.like("proname",proName);
            }
            wrapper.orderByDesc("create_time");
            //调用方法分页查询
            IPage<JjgJgProjectinfo> pageModel = jjgJgProjectinfoService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);

        }
    }


}

