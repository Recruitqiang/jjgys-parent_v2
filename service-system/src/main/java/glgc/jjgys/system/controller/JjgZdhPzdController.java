package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgZdhMcxs;
import glgc.jjgys.model.project.JjgZdhMcxsJgfc;
import glgc.jjgys.model.project.JjgZdhPzd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.model.system.SysRole;
import glgc.jjgys.model.system.SysUser;
import glgc.jjgys.model.system.SysUserRole;
import glgc.jjgys.system.mapper.SysUserRoleMapper;
import glgc.jjgys.system.service.JjgZdhPzdService;
import glgc.jjgys.system.service.OperLogService;
import glgc.jjgys.system.service.SysRoleService;
import glgc.jjgys.system.service.SysUserService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wq
 * @since 2023-06-17
 */
@RestController
@RequestMapping("/jjg/zdh/pzd")
public class JjgZdhPzdController {

    @Autowired
    private JjgZdhPzdService jjgZdhPzdService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private OperLogService operLogService;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletResponse response, String proname, String htd) throws IOException {
        List<Map<String,Object>> lxlist = jjgZdhPzdService.selectlx(proname,htd);
        List<String> fileName = new ArrayList<>();
        for (Map<String, Object> map : lxlist) {
            String lxbs = map.get("lxbs").toString();
            if (lxbs.equals("主线")){
                fileName.add("18路面平整度");;
            }else {
                fileName.add("61互通平整度-"+lxbs);
            }
        }
        String zipname = "平整度鉴定表";
        JjgFbgcCommonUtils.batchDowndFile(response,zipname,fileName,filespath+File.separator+proname+File.separator+htd);
        /*String fileName = ".xlsx";
        String p = filespath+ File.separator+proname+File.separator+htd+File.separator+fileName;
        File file = new File(p);
        if (file.exists()){
            JjgFbgcCommonUtils.download(response,p,fileName);
        }*/
    }

    /**
     * 沥青隧道路面平整度规定值
     * 沥青桥面平整度规定值
     * 沥青路面平整度规定值
     *
     * 混凝土隧道路面平整度规定值
     * 混凝土桥面平整度规定值
     * 混凝土路面平整度规定值
     *
     */

    @ApiOperation("生成平整度鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody CommonInfoVo commonInfoVo) throws Exception {
        jjgZdhPzdService.generateJdb(commonInfoVo);

    }

    @ApiOperation("全部删除")
    @DeleteMapping("removeAll")
    public Result removeAll(@RequestBody CommonInfoVo commonInfoVo){
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        String username = commonInfoVo.getUsername();
        QueryWrapper<JjgZdhPzd> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("proname",proname);
        queryWrapper.eq("htd",htd);
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.eq("username", username);
        SysUser one = sysUserService.getOne(sysUserQueryWrapper);
        String userid = one.getId().toString();

        QueryWrapper<SysUserRole> sysUserRoleQueryWrapper = new QueryWrapper<>();
        sysUserRoleQueryWrapper.eq("user_id", userid);
        SysUserRole sysUserRole = sysUserRoleMapper.selectOne(sysUserRoleQueryWrapper);
        String roleId = sysUserRole.getRoleId();

        QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
        sysRoleQueryWrapper.eq("id", roleId);
        SysRole role = sysRoleService.getOne(sysRoleQueryWrapper);
        String rolecode = role.getRoleCode();

        if (rolecode.equals("YH")){
            queryWrapper.eq("username", username);
        }
        boolean remove = jjgZdhPzdService.remove(queryWrapper);
        if(remove){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }

    @ApiOperation("查看平整度鉴定结果")
    @PostMapping("lookJdbjg")
    public Result lookJdbjg(@RequestBody CommonInfoVo commonInfoVo) throws IOException {
        List<Map<String,Object>> jdjg = jjgZdhPzdService.lookJdbjg(commonInfoVo);
        return Result.ok(jdjg);

    }

    @ApiOperation("平整度模板文件导出")
    @GetMapping("exportpzd")
    public void exportpzd(HttpServletResponse response,@RequestParam String cd) throws IOException {
        jjgZdhPzdService.exportpzd(response,cd);
    }


    @ApiOperation(value = "平整度数据文件导入")
    @PostMapping("importpzd")
    public Result importpzd(@RequestParam("file") MultipartFile file, CommonInfoVo commonInfoVo) throws IOException, ParseException {
        jjgZdhPzdService.importpzd(file,commonInfoVo);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgZdhPzd jjgZdhpzd) {
        //创建page对象
        Page<JjgZdhPzd> pageParam = new Page<>(current, limit);
        if (jjgZdhpzd != null) {
            QueryWrapper<JjgZdhPzd> wrapper = new QueryWrapper<>();
            wrapper.like("proname", jjgZdhpzd.getProname());
            wrapper.like("htd", jjgZdhpzd.getHtd());

            String username = jjgZdhpzd.getUsername();
            QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
            sysUserQueryWrapper.eq("username", username);
            SysUser one = sysUserService.getOne(sysUserQueryWrapper);
            String userid = one.getId().toString();

            QueryWrapper<SysUserRole> sysUserRoleQueryWrapper = new QueryWrapper<>();
            sysUserRoleQueryWrapper.eq("user_id", userid);
            SysUserRole sysUserRole = sysUserRoleMapper.selectOne(sysUserRoleQueryWrapper);
            String roleId = sysUserRole.getRoleId();

            QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
            sysRoleQueryWrapper.eq("id", roleId);
            SysRole role = sysRoleService.getOne(sysRoleQueryWrapper);
            String rolecode = role.getRoleCode();

            if (rolecode.equals("YH")){
                wrapper.eq("username", username);
            }
            if (!StringUtils.isEmpty(jjgZdhpzd.getQdzh())) {
                wrapper.like("qdzh", jjgZdhpzd.getQdzh());
            }
            if (!StringUtils.isEmpty(jjgZdhpzd.getZdzh())) {
                wrapper.like("zdzh", jjgZdhpzd.getZdzh());
            }

            //调用方法分页查询
            IPage<JjgZdhPzd> pageModel = jjgZdhPzdService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        boolean hd = jjgZdhPzdService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }
    }

    @ApiOperation("根据id查询")
    @GetMapping("getpzd/{id}")
    public Result getpzd(@PathVariable String id) {
        JjgZdhPzd user = jjgZdhPzdService.getById(id);
        return Result.ok(user);
    }

    @PostMapping("update")
    public Result update(@RequestBody JjgZdhPzd user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgZdhPzdService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setTitle("平整度");
            sysOperLog.setBusinessType("修改");
            sysOperLog.setOperName(JwtHelper.getUsername(request.getHeader("token")));
            sysOperLog.setOperIp(IpUtil.getIpAddress(request));
            sysOperLog.setOperTime(new Date());
            operLogService.saveSysLog(sysOperLog);
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

}

