package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.model.system.Project;
import glgc.jjgys.model.system.SysRole;
import glgc.jjgys.model.system.SysUser;
import glgc.jjgys.model.system.SysUserRole;
import glgc.jjgys.model.vo.ProjectQueryVo;
import glgc.jjgys.system.mapper.SysUserRoleMapper;
import glgc.jjgys.system.service.ProjectService;
import glgc.jjgys.system.service.SysMenuService;
import glgc.jjgys.system.service.SysRoleService;
import glgc.jjgys.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "项目管理接口")
@RestController
@Transactional
@RequestMapping(value = "/project")
@CrossOrigin//解决跨域
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SysUserService sysUserService;


    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("selectuser")
    public Result selectuser(){
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        List<SysUser> list = sysUserService.list(sysUserQueryWrapper);
        return Result.ok(list);
    }



    /**
     * 新增项目 包括新增合同段信息 路桥隧信息
     */
    @PostMapping("/add")
    public Result add(@RequestBody Project project){
        String proName = project.getProName();
        projectService.addOtherInfo(proName);
        boolean res = projectService.save(project);
        return res ? Result.ok().message("增加成功") : Result.fail().message("增加失败！");

    }

    @ApiOperation("校验项目")
    @GetMapping("checkProname/{proname}")
    public Result checkProname(@PathVariable String proname) {
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<Project> list = projectService.list(wrapper);
        if (list!=null && !list.isEmpty()){
            return Result.ok().message("校验成功");
        }else {
            return Result.fail().message("校验失败");
        }
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable String id){
        projectService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation("批量删除项目信息")
    @Transactional
    @DeleteMapping("removeBatch")
    //传json数组[1,2,3]，用List接收
    public Result removeBeatch(@RequestBody List<String> idList){
        projectService.deleteOtherInfo(idList);
        boolean isSuccess = projectService.removeByIds(idList);
        return Result.ok();
    }

    /**
     * 查询所有的项目
     */
    @GetMapping("findAll")
    public Result findAllProject(){
        List<Project> list = projectService.list();
        return Result.ok(list);
    }

    /**
     * 分页查询
     */
    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody Project project){
        //创建page对象
        Page<Project> pageParam=new Page<>(current,limit);
        //判断projectQueryVo对象是否为空，直接查全部
        if(project == null){
            IPage<Project> pageModel = projectService.page(pageParam,null);
            return Result.ok(pageModel);
        }else {
            //获取条件值，进行非空判断，条件封装
            String proName = project.getProName();
            QueryWrapper<Project> wrapper=new QueryWrapper<>();
            String username = project.getUsername();
            //String username = "wqq";

            QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
            sysUserQueryWrapper.eq("username", username);
            SysUser one = sysUserService.getOne(sysUserQueryWrapper);
            String userid = one.getId().toString();

            QueryWrapper<SysUserRole> sysUserRoleQueryWrapper = new QueryWrapper<>();
            sysUserRoleQueryWrapper.eq("user_id", userid);
            SysUserRole sysUserRole = sysUserRoleMapper.selectOne(sysUserRoleQueryWrapper);
            if (sysUserRole!=null){
                String roleId = sysUserRole.getRoleId();
                QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
                sysRoleQueryWrapper.eq("id", roleId);
                SysRole role = sysRoleService.getOne(sysRoleQueryWrapper);
                String rolecode = role.getRoleCode();

                if (rolecode.equals("YH") || rolecode.equals("Leader")){
                    wrapper.like("participant",username);
                }
                //其实leader就可以分配权限了
            }

            if (!StringUtils.isEmpty(proName)){
                wrapper.like("proname",proName);
            }
            if (!StringUtils.isEmpty(project.getParticipant())){
                wrapper.like("participant",project.getParticipant());
            }
            wrapper.orderByDesc("create_time");
            //调用方法分页查询
            IPage<Project> pageModel = projectService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);

        }
    }
}
