package glgc.jjgys.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import glgc.jjgys.common.result.Result;
import glgc.jjgys.common.utils.IpUtil;
import glgc.jjgys.common.utils.JwtHelper;
import glgc.jjgys.model.project.JjgFbgcSdgcHntlmqd;
import glgc.jjgys.model.project.JjgFbgcSdgcJk;
import glgc.jjgys.model.project.JjgLqsSd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.system.SysOperLog;
import glgc.jjgys.model.system.SysRole;
import glgc.jjgys.model.system.SysUser;
import glgc.jjgys.model.system.SysUserRole;
import glgc.jjgys.system.mapper.SysUserRoleMapper;
import glgc.jjgys.system.service.*;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.JjgFbgcUtils;
import io.swagger.annotations.ApiOperation;
import net.lingala.zip4j.exception.ZipException;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
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
 * @since 2023-10-11
 */
@RestController
@RequestMapping("/jjg/fbgc/sdgc/jk")
public class JjgFbgcSdgcJkController {

    @Autowired
    private JjgFbgcSdgcJkService jjgFbgcSdgcJkService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;


    @Autowired
    private JjgLqsSdService jjgLqsSdService;

    @Autowired
    private OperLogService operLogService;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadExport(HttpServletRequest request,HttpServletResponse response, String proname, String htd) throws IOException {
        QueryWrapper<JjgLqsSd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgLqsSd> sdlist = jjgLqsSdService.list(wrapper);
        List list = new ArrayList<>();
        for (int i=0;i<sdlist.size();i++){
            list.add(sdlist.get(i).getSdname());
        }
        String zipName = "42隧道断面测量坐标表";
        JjgFbgcCommonUtils.batchDownloadFile(request,response,zipName,list,filespath+File.separator+proname+File.separator+htd);
    }

    @ApiOperation("生成净空鉴定表")
    @PostMapping("generateJdb")
    public void generateJdb(@RequestBody CommonInfoVo commonInfoVo) throws Exception {
        jjgFbgcSdgcJkService.generateJdb(commonInfoVo);

    }

    @ApiOperation("查看净空鉴定结果")
    @PostMapping("lookJdbjg")
    public Result lookJdbjg(@RequestBody CommonInfoVo commonInfoVo) throws IOException {
        List<Map<String,Object>> jdjg = jjgFbgcSdgcJkService.lookJdbjg(commonInfoVo);
        return Result.ok(jdjg);

    }

    @ApiOperation("净空模板文件导出")
    @GetMapping("exportjk")
    public void exportjk(HttpServletResponse response, @RequestParam String proname, @RequestParam String htd){
        jjgFbgcSdgcJkService.exportjk(response,proname,htd);


    }


    @ApiOperation(value = "净空数据文件导入")
    @PostMapping("importjk")
    public Result importjk(@RequestParam("file") MultipartFile file, CommonInfoVo commonInfoVo) {
        jjgFbgcSdgcJkService.importjk(file,commonInfoVo);
        return Result.ok();
    }

    @PostMapping("findQueryPage/{current}/{limit}")
    public Result findQueryPage(@PathVariable long current,
                                @PathVariable long limit,
                                @RequestBody JjgFbgcSdgcJk jjgFbgcSdgcJk){
        //创建page对象
        Page<JjgFbgcSdgcJk> pageParam=new Page<>(current,limit);
        if (jjgFbgcSdgcJk != null){
            QueryWrapper<JjgFbgcSdgcJk> wrapper=new QueryWrapper<>();
            wrapper.like("proname",jjgFbgcSdgcJk.getProname());
            wrapper.like("htd",jjgFbgcSdgcJk.getHtd());
            wrapper.like("fbgc",jjgFbgcSdgcJk.getFbgc());

            String username = jjgFbgcSdgcJk.getUsername();
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
            Date jcsj = jjgFbgcSdgcJk.getJcsj();
            if (!StringUtils.isEmpty(jcsj)) {
                wrapper.like("jcsj", jcsj);
            }
            if (!StringUtils.isEmpty(jjgFbgcSdgcJk.getZh())) {
                wrapper.like("zh", jjgFbgcSdgcJk.getZh());
            }
            //调用方法分页查询
            IPage<JjgFbgcSdgcJk> pageModel = jjgFbgcSdgcJkService.page(pageParam, wrapper);
            //返回
            return Result.ok(pageModel);
        }
        return Result.ok().message("无数据");
    }

    @ApiOperation("批量净空数据")
    @DeleteMapping("removeBatch")
    public Result removeBeatch(@RequestBody List<String> idList){
        //本地文件
        List<String> path = new ArrayList<>();
        for (String s : idList) {
            JjgFbgcSdgcJk byId = jjgFbgcSdgcJkService.getById(s);
            path.add(byId.getPath());

        }
        deleteLocalFile(path);

        boolean hd = jjgFbgcSdgcJkService.removeByIds(idList);
        if(hd){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }
    }

    @ApiOperation("全部删除")
    @DeleteMapping("removeAll")
    public Result removeAll(@RequestBody CommonInfoVo commonInfoVo){
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        String username = commonInfoVo.getUsername();
        QueryWrapper<JjgFbgcSdgcJk> queryWrapper = new QueryWrapper<>();
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
        List<JjgFbgcSdgcJk> list = jjgFbgcSdgcJkService.list(queryWrapper);
        if (list!=null){
            List<String> path = new ArrayList<>();
            for (JjgFbgcSdgcJk jk : list) {
                String jkpath = jk.getPath();
                path.add(jkpath);
            }
            deleteLocalFile(path);
        }
        boolean remove = jjgFbgcSdgcJkService.remove(queryWrapper);
        if(remove){
            return Result.ok();
        } else {
            return Result.fail().message("删除失败！");
        }

    }


    private void deleteLocalFile(List<String> path) {
        for (String imagePath : path) {
            File file = new File(imagePath);
            if (file.exists()) {
                if (file.isDirectory()) {
                    // Delete folder if it is empty
                    if (file.list().length == 0) {
                        file.delete();
                        System.out.println("Empty folder " + file.getAbsolutePath() + " deleted.");
                    } else {
                        System.out.println("Skipping non-empty folder " + file.getAbsolutePath() + ".");
                    }
                } else {
                    // Delete image file
                    file.delete();
                    System.out.println("Image " + file.getAbsolutePath() + " deleted.");
                }
            } else {
                System.out.println("File " + file.getAbsolutePath() + " does not exist.");
            }
        }
    }

    @ApiOperation("根据id查询")
    @GetMapping("getHntlmqd/{id}")
    public Result getHntlmqd(@PathVariable String id) {
        JjgFbgcSdgcJk user = jjgFbgcSdgcJkService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation("修改混凝土路面强度数据")
    @PostMapping("update")
    public Result update(@RequestBody JjgFbgcSdgcJk user) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        boolean is_Success = jjgFbgcSdgcJkService.updateById(user);
        if(is_Success) {
            SysOperLog sysOperLog = new SysOperLog();
            sysOperLog.setProname(user.getProname());
            sysOperLog.setHtd(user.getHtd());
            sysOperLog.setFbgc(user.getFbgc());
            sysOperLog.setTitle("净空数据");
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

