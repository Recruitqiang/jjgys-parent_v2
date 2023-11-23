package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.zdh.JjgZdhCzJgfcVo;
import glgc.jjgys.model.system.SysRole;
import glgc.jjgys.model.system.SysUser;
import glgc.jjgys.model.system.SysUserRole;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.*;
import glgc.jjgys.system.service.JjgZdhCzJgfcService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.service.SysRoleService;
import glgc.jjgys.system.service.SysUserService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.RowCopy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-09-23
 */
@Slf4j
@Service
public class JjgZdhCzJgfcServiceImpl extends ServiceImpl<JjgZdhCzJgfcMapper, JjgZdhCzJgfc> implements JjgZdhCzJgfcService {

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;

    @Autowired
    private JjgZdhCzJgfcMapper jjgZdhCzJgfcMapper;

    @Autowired
    private JjgJgHtdinfoMapper jjgJgHtdinfoMapper;

    @Autowired
    private JjgLqsJgQlMapper jjgLqsJgQlMapper;

    @Autowired
    private JjgLqsJgSdMapper jjgLqsJgSdMapper;

    @Autowired
    private JjgLqsJgSfzMapper jjgLqsJgSfzMapper;

    @Autowired
    private JjgLqsJgLjxMapper jjgLqsJgLjxMapper;

    @Autowired
    private JjgLqsJgHntlmzdMapper jjgLqsJgHntlmzdMapper;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public void generateJdb(String proname, String sjz, String username) throws IOException, ParseException {
        //需要根据项目名称批量删除

        List<String> htd = jjgZdhCzJgfcMapper.gethtd(proname);
        for (String s : htd) {
            List<Map<String,Object>> lxlist = jjgZdhCzJgfcMapper.selectlx(proname,s);
            for (Map<String, Object> map : lxlist) {
                String zx = map.get("lxbs").toString();
                int num = jjgZdhCzJgfcMapper.selectcdnum(proname,s,zx);
                int cds = 0;
                if (num == 1){
                    cds = 2;
                }else {
                    cds=num;
                }
                handlezxData(proname,s,zx,cds,sjz,username);
            }
        }

    }

    /**
     *  @param proname
     * @param htd
     * @param zx
     * @param cdsl
     * @param sjz
     * @param username
     */
    private void handlezxData(String proname, String htd, String zx, int cdsl, String sjz, String username) throws IOException, ParseException {
        log.info("准备数据......");
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


        if (zx.equals("主线")) {
            List<Map<String, Object>> datazf = new ArrayList<>();
            List<Map<String, Object>> datayf = new ArrayList<>();
            if (rolecode.equals("YH")){
                datazf = jjgZdhCzJgfcMapper.selectzfListyh(proname, htd, zx,username);
                datayf = jjgZdhCzJgfcMapper.selectyfListyh(proname, htd, zx,username);

            }else {
                datazf = jjgZdhCzJgfcMapper.selectzfList(proname, htd, zx);
                datayf = jjgZdhCzJgfcMapper.selectyfList(proname, htd, zx);
            }


            QueryWrapper<JjgJgHtdinfo> wrapperhtd = new QueryWrapper<>();
            wrapperhtd.like("proname", proname);
            wrapperhtd.like("name", htd);
            List<JjgJgHtdinfo> htdList = jjgJgHtdinfoMapper.selectList(wrapperhtd);

            String htdzhq = htdList.get(0).getZhq();
            String htdzhz = htdList.get(0).getZhz();

            //隧道
            List<JjgLqsJgSd> jjgLqsSdzf = jjgLqsJgSdMapper.selectsdzf(proname, htdzhq, htdzhz, "左幅");
            List<JjgLqsJgSd> jjgLqsSdyf = jjgLqsJgSdMapper.selectsdyf(proname, htdzhq, htdzhz, "右幅");

            //桥
            List<JjgLqsJgQl> jjgLqsQlzf = jjgLqsJgQlMapper.selectqlzf(proname, htdzhq, htdzhz, "左幅");
            List<JjgLqsJgQl> jjgLqsQlyf = jjgLqsJgQlMapper.selectqlyf(proname, htdzhq, htdzhz, "右幅");

            List<Map<String, Object>> hpsdzfdata = new ArrayList<>();
            List<Map<String, Object>> hpsdyfdata = new ArrayList<>();
            List<Map<String, Object>> hpqlzfdata = new ArrayList<>();
            List<Map<String, Object>> hpqlyfdata = new ArrayList<>();
            if (rolecode.equals("YH")){
                if (jjgLqsSdzf.size() > 0) {
                    for (JjgLqsJgSd jjgLqsSd : jjgLqsSdzf) {
                        String zhq = String.valueOf((jjgLqsSd.getZhq()));
                        String zhz = String.valueOf((jjgLqsSd.getZhz()));
                        hpsdzfdata.addAll(jjgZdhCzJgfcMapper.selectSdZfDatayh(proname, htd, zx, zhq, zhz,username));
                    }
                }
                if (jjgLqsSdyf.size() > 0) {
                    for (JjgLqsJgSd jjgLqsSd : jjgLqsSdyf) {
                        String zhq = String.valueOf(jjgLqsSd.getZhq());
                        String zhz = String.valueOf(jjgLqsSd.getZhz());
                        hpsdyfdata.addAll(jjgZdhCzJgfcMapper.selectSdyfDatayh(proname, htd, zx, zhq, zhz,username));
                    }
                }
                if (jjgLqsQlzf.size() > 0) {
                    for (JjgLqsJgQl jjgLqsQl : jjgLqsQlzf) {
                        String zhq = String.valueOf(jjgLqsQl.getZhq());
                        String zhz = String.valueOf(jjgLqsQl.getZhz());
                        hpqlzfdata.addAll(jjgZdhCzJgfcMapper.selectQlZfDatayh(proname, htd, zx, zhq, zhz,username));
                    }
                }
                if (jjgLqsQlyf.size() > 0) {
                    for (JjgLqsJgQl jjgLqsQl : jjgLqsQlyf) {
                        String zhq = String.valueOf(jjgLqsQl.getZhq());
                        String zhz = String.valueOf(jjgLqsQl.getZhz());
                        hpqlyfdata.addAll(jjgZdhCzJgfcMapper.selectQlYfDatayh(proname, htd, zx, zhq, zhz,username));
                    }
                }

            }else {
                if (jjgLqsSdzf.size() > 0) {
                    for (JjgLqsJgSd jjgLqsSd : jjgLqsSdzf) {
                        String zhq = String.valueOf((jjgLqsSd.getZhq()));
                        String zhz = String.valueOf((jjgLqsSd.getZhz()));
                        hpsdzfdata.addAll(jjgZdhCzJgfcMapper.selectSdZfData(proname, htd, zx, zhq, zhz));
                    }
                }
                if (jjgLqsSdyf.size() > 0) {
                    for (JjgLqsJgSd jjgLqsSd : jjgLqsSdyf) {
                        String zhq = String.valueOf(jjgLqsSd.getZhq());
                        String zhz = String.valueOf(jjgLqsSd.getZhz());
                        hpsdyfdata.addAll(jjgZdhCzJgfcMapper.selectSdyfData(proname, htd, zx, zhq, zhz));
                    }
                }
                if (jjgLqsQlzf.size() > 0) {
                    for (JjgLqsJgQl jjgLqsQl : jjgLqsQlzf) {
                        String zhq = String.valueOf(jjgLqsQl.getZhq());
                        String zhz = String.valueOf(jjgLqsQl.getZhz());
                        hpqlzfdata.addAll(jjgZdhCzJgfcMapper.selectQlZfData(proname, htd, zx, zhq, zhz));
                    }
                }
                if (jjgLqsQlyf.size() > 0) {
                    for (JjgLqsJgQl jjgLqsQl : jjgLqsQlyf) {
                        String zhq = String.valueOf(jjgLqsQl.getZhq());
                        String zhz = String.valueOf(jjgLqsQl.getZhz());
                        hpqlyfdata.addAll(jjgZdhCzJgfcMapper.selectQlYfData(proname, htd, zx, zhq, zhz));
                    }
                }
            }

            //处理数据
            List<Map<String, Object>> sdzxList = groupByZh(hpsdzfdata);
            List<Map<String, Object>> sdyxList = groupByZh(hpsdyfdata);

            List<Map<String, Object>> qlzxList = groupByZh(hpqlzfdata);
            List<Map<String, Object>> qlyxList = groupByZh(hpqlyfdata);

            List<Map<String, Object>> lmzfList = groupByZh(datazf);
            List<Map<String, Object>> lmyfList = groupByZh(datayf);

            writeExcelData(proname, htd, lmzfList, lmyfList, sdzxList, sdyxList, qlzxList, qlyxList, cdsl, sjz, zx);
        }else if (zx.contains("连接线")){
            List<Map<String,Object>> dataljxzf = new ArrayList<>();
            List<Map<String,Object>> dataljxyf = new ArrayList<>();
            //查询的是摩擦系数表中的连接线
            if (rolecode.equals("YH")){
                dataljxzf = jjgZdhCzJgfcMapper.selectzfListyh(proname,htd,zx,username);
                dataljxyf = jjgZdhCzJgfcMapper.selectyfListyh(proname,htd,zx,username);
            }else {
                dataljxzf = jjgZdhCzJgfcMapper.selectzfList(proname,htd,zx);
                dataljxyf = jjgZdhCzJgfcMapper.selectyfList(proname,htd,zx);
            }

            //连接线
            QueryWrapper<JjgLqsJgLjx> wrapperljx = new QueryWrapper<>();
            wrapperljx.like("proname",proname);
            wrapperljx.like("sshtd",htd);
            List<JjgLqsJgLjx> jjgLjxList = jjgLqsJgLjxMapper.selectList(wrapperljx);

            List<Map<String,Object>> sdmcxs = new ArrayList<>();
            List<Map<String,Object>> qlmcxs = new ArrayList<>();
            if (rolecode.equals("YH")){
                for (JjgLqsJgLjx jjgLjx : jjgLjxList) {
                    String zhq = jjgLjx.getZhq();
                    String zhz = jjgLjx.getZhz();
                    String bz = jjgLjx.getBz();
                    String ljxlf = jjgLjx.getLf();
                    String wz = jjgLjx.getLjxname();
                    List<JjgLqsJgSd> jjgLqssd = jjgLqsJgSdMapper.selectsdList(proname,zhq,zhz,bz,wz,ljxlf);
                    //有可能是单幅，有可能是左右幅都有
                    for (JjgLqsJgSd jjgLqsSd : jjgLqssd) {
                        String lf = jjgLqsSd.getLf();
                        Double sdq = jjgLqsSd.getZhq()+10;
                        String sdz = jjgLqsSd.getZhz().toString();
                        String sdzhq = String.valueOf(sdq);
                        String zhq1 = String.valueOf((jjgLqsSd.getZhq()));
                        String zhz1 = String.valueOf((jjgLqsSd.getZhz()));
                        sdmcxs.addAll(jjgZdhCzJgfcMapper.selectsdczyh(proname,bz,lf,sdzhq,sdz,zx,zhq1,zhz1,username));
                    }

                    List<JjgLqsJgQl> jjgLqsql = jjgLqsJgQlMapper.selectqlList(proname,zhq,zhz,bz,wz,ljxlf);
                    for (JjgLqsJgQl jjgLqsQl : jjgLqsql) {
                        String lf = jjgLqsQl.getLf();
                        Double qlq = jjgLqsQl.getZhq()+10;
                        Double qlz = jjgLqsQl.getZhz();
                        String qlzhq = String.valueOf(qlq);
                        String qlzhz = String.valueOf(qlz);
                        String zhq1 = String.valueOf(jjgLqsQl.getZhq());
                        String zhz1 = String.valueOf(jjgLqsQl.getZhz());
                        qlmcxs.addAll(jjgZdhCzJgfcMapper.selectqlczyh(proname,bz,lf, qlzhq, qlzhz, zx, zhq1, zhz1,username));

                    }
                }

            }else {
                for (JjgLqsJgLjx jjgLjx : jjgLjxList) {
                    String zhq = jjgLjx.getZhq();
                    String zhz = jjgLjx.getZhz();
                    String bz = jjgLjx.getBz();
                    String ljxlf = jjgLjx.getLf();
                    String wz = jjgLjx.getLjxname();
                    List<JjgLqsJgSd> jjgLqssd = jjgLqsJgSdMapper.selectsdList(proname,zhq,zhz,bz,wz,ljxlf);
                    //有可能是单幅，有可能是左右幅都有
                    for (JjgLqsJgSd jjgLqsSd : jjgLqssd) {
                        String lf = jjgLqsSd.getLf();
                        Double sdq = jjgLqsSd.getZhq()+10;
                        String sdz = jjgLqsSd.getZhz().toString();
                        String sdzhq = String.valueOf(sdq);
                        String zhq1 = String.valueOf((jjgLqsSd.getZhq()));
                        String zhz1 = String.valueOf((jjgLqsSd.getZhz()));
                        sdmcxs.addAll(jjgZdhCzJgfcMapper.selectsdcz(proname,bz,lf,sdzhq,sdz,zx,zhq1,zhz1));
                    }

                    List<JjgLqsJgQl> jjgLqsql = jjgLqsJgQlMapper.selectqlList(proname,zhq,zhz,bz,wz,ljxlf);
                    for (JjgLqsJgQl jjgLqsQl : jjgLqsql) {
                        String lf = jjgLqsQl.getLf();
                        Double qlq = jjgLqsQl.getZhq()+10;
                        Double qlz = jjgLqsQl.getZhz();
                        String qlzhq = String.valueOf(qlq);
                        String qlzhz = String.valueOf(qlz);
                        String zhq1 = String.valueOf(jjgLqsQl.getZhq());
                        String zhz1 = String.valueOf(jjgLqsQl.getZhz());
                        qlmcxs.addAll(jjgZdhCzJgfcMapper.selectqlcz(proname,bz,lf, qlzhq, qlzhz, zx, zhq1, zhz1));

                    }
                }
            }


            List<Map<String,Object>> zfqlmcxs = new ArrayList<>();
            List<Map<String,Object>> yfqlmcxs = new ArrayList<>();
            if (qlmcxs.size()>0){
                for (int i = 0; i < qlmcxs.size(); i++) {
                    if (qlmcxs.get(i).get("cd").toString().contains("左幅")){
                        zfqlmcxs.add(qlmcxs.get(i));
                    }
                    if (qlmcxs.get(i).get("cd").toString().contains("右幅")){
                        yfqlmcxs.add(qlmcxs.get(i));
                    }
                }
            }
            List<Map<String,Object>> zfsdmcxs = new ArrayList<>();
            List<Map<String,Object>> yfsdmcxs = new ArrayList<>();
            if (sdmcxs.size()>0){
                for (Map<String, Object> sdmcx : sdmcxs) {
                    if (sdmcx.get("cd").toString().contains("左幅")){
                        zfsdmcxs.add(sdmcx);
                    }
                    if (sdmcx.get("cd").toString().contains("右幅")){
                        yfsdmcxs.add(sdmcx);
                    }
                }
            }

            List<Map<String, Object>> qlzfsj = groupByZh1(zfqlmcxs);
            List<Map<String, Object>> qlyfsj = groupByZh1(yfqlmcxs);
            List<Map<String, Object>> sdzfsj = groupByZh1(zfsdmcxs);
            List<Map<String, Object>> sdyfsj = groupByZh1(yfsdmcxs);

            List<Map<String, Object>> allzfsj = mergeList(dataljxzf);
            List<Map<String, Object>> allyfsj = mergeList(dataljxyf);
            writeExcelData(proname,htd,allzfsj,allyfsj,sdzfsj,sdyfsj,qlzfsj,qlyfsj,cdsl,sjz,zx);

        }else {
            List<Map<String,Object>> datazdzf = new ArrayList<>();
            List<Map<String,Object>> datazdyf = new ArrayList<>();
            if (rolecode.equals("YH")){
                datazdzf = jjgZdhCzJgfcMapper.selectzfListyh(proname,htd,zx,username);
                datazdyf = jjgZdhCzJgfcMapper.selectyfListyh(proname,htd,zx,username);
            }else {
                //匝道
                datazdzf = jjgZdhCzJgfcMapper.selectzfList(proname,htd,zx);
                datazdyf = jjgZdhCzJgfcMapper.selectyfList(proname,htd,zx);
            }


            /**
             * 先去匝道表中查询起始桩号,查出多个，然后根据当前这条数据的起始桩号，去隧道表中查有无隧道数据，附带一个条件bz去和隧道表中的bz匹配
             */
            QueryWrapper<JjgLqsJgHntlmzd> wrapperzd = new QueryWrapper<>();
            wrapperzd.like("proname",proname);
            wrapperzd.like("wz",zx);
            List<JjgLqsJgHntlmzd> zdList = jjgLqsJgHntlmzdMapper.selectList(wrapperzd);

            /**
             * 查出多条数据，分A，B,C匝道
             */
            List<Map<String,Object>> sdmcxs = new ArrayList<>();//左右幅或者单幅，当前匝道下全部的隧道数据
            List<Map<String,Object>> qlmcxs = new ArrayList<>();
            if (rolecode.equals("YH")){
                for (JjgLqsJgHntlmzd jjgLqsHntlmzd : zdList) {
                    String zhq = jjgLqsHntlmzd.getZhq();
                    String zhz = jjgLqsHntlmzd.getZhz();
                    String bz = jjgLqsHntlmzd.getZdlx();
                    String wz = jjgLqsHntlmzd.getWz();
                    String zdlf = jjgLqsHntlmzd.getLf();
                    List<JjgLqsJgSd> jjgLqssd = jjgLqsJgSdMapper.selectsdList(proname,zhq,zhz,bz,wz,zdlf);
                    //有可能是单幅，有可能是左右幅都有
                    for (JjgLqsJgSd jjgLqsSd : jjgLqssd) {
                        String lf = jjgLqsSd.getLf();
                        Double sdq = jjgLqsSd.getZhq()+10;
                        String sdz = jjgLqsSd.getZhz().toString();
                        String sdzhq = String.valueOf(sdq);

                        String zhq1 = String.valueOf((jjgLqsSd.getZhq()));
                        String zhz1 = String.valueOf((jjgLqsSd.getZhz()));
                        sdmcxs.addAll(jjgZdhCzJgfcMapper.selectsdczyh(proname,bz,lf,sdzhq,sdz,zx,zhq1,zhz1,username));
                    }

                    List<JjgLqsJgQl> jjgLqsql = jjgLqsJgQlMapper.selectqlList(proname,zhq,zhz,bz,wz,zdlf);
                    for (JjgLqsJgQl jjgLqsQl : jjgLqsql) {
                        String lf = jjgLqsQl.getLf();
                        Double qlq = jjgLqsQl.getZhq()+10;
                        Double qlz = jjgLqsQl.getZhz();
                        String qlzhq = String.valueOf(qlq);
                        String qlzhz = String.valueOf(qlz);
                        String zhq1 = String.valueOf(jjgLqsQl.getZhq());
                        String zhz1 = String.valueOf(jjgLqsQl.getZhz());
                        qlmcxs.addAll(jjgZdhCzJgfcMapper.selectqlczyh(proname,bz,lf, qlzhq, qlzhz, zx, zhq1, zhz1,username));

                    }

                }

            }else {
                for (JjgLqsJgHntlmzd jjgLqsHntlmzd : zdList) {
                    String zhq = jjgLqsHntlmzd.getZhq();
                    String zhz = jjgLqsHntlmzd.getZhz();
                    String bz = jjgLqsHntlmzd.getZdlx();
                    String wz = jjgLqsHntlmzd.getWz();
                    String zdlf = jjgLqsHntlmzd.getLf();
                    List<JjgLqsJgSd> jjgLqssd = jjgLqsJgSdMapper.selectsdList(proname,zhq,zhz,bz,wz,zdlf);
                    //有可能是单幅，有可能是左右幅都有
                    for (JjgLqsJgSd jjgLqsSd : jjgLqssd) {
                        String lf = jjgLqsSd.getLf();
                        Double sdq = jjgLqsSd.getZhq()+10;
                        String sdz = jjgLqsSd.getZhz().toString();
                        String sdzhq = String.valueOf(sdq);

                        String zhq1 = String.valueOf((jjgLqsSd.getZhq()));
                        String zhz1 = String.valueOf((jjgLqsSd.getZhz()));
                        sdmcxs.addAll(jjgZdhCzJgfcMapper.selectsdcz(proname,bz,lf,sdzhq,sdz,zx,zhq1,zhz1));
                    }

                    List<JjgLqsJgQl> jjgLqsql = jjgLqsJgQlMapper.selectqlList(proname,zhq,zhz,bz,wz,zdlf);
                    for (JjgLqsJgQl jjgLqsQl : jjgLqsql) {
                        String lf = jjgLqsQl.getLf();
                        Double qlq = jjgLqsQl.getZhq()+10;
                        Double qlz = jjgLqsQl.getZhz();
                        String qlzhq = String.valueOf(qlq);
                        String qlzhz = String.valueOf(qlz);
                        String zhq1 = String.valueOf(jjgLqsQl.getZhq());
                        String zhz1 = String.valueOf(jjgLqsQl.getZhz());
                        qlmcxs.addAll(jjgZdhCzJgfcMapper.selectqlcz(proname,bz,lf, qlzhq, qlzhz, zx, zhq1, zhz1));

                    }

                }
            }

            List<Map<String,Object>> zfqlmcxs = new ArrayList<>();
            List<Map<String,Object>> yfqlmcxs = new ArrayList<>();
            if (qlmcxs.size()>0){
                for (int i = 0; i < qlmcxs.size(); i++) {
                    if (qlmcxs.get(i).get("cd").toString().contains("左幅")){
                        zfqlmcxs.add(qlmcxs.get(i));
                    }
                    if (qlmcxs.get(i).get("cd").toString().contains("右幅")){
                        yfqlmcxs.add(qlmcxs.get(i));
                    }
                }
            }
            List<Map<String,Object>> zfsdmcxs = new ArrayList<>();
            List<Map<String,Object>> yfsdmcxs = new ArrayList<>();
            if (sdmcxs.size()>0){
                for (Map<String, Object> sdmcx : sdmcxs) {
                    if (sdmcx.get("cd").toString().contains("左幅")){
                        zfsdmcxs.add(sdmcx);
                    }
                    if (sdmcx.get("cd").toString().contains("右幅")){
                        yfsdmcxs.add(sdmcx);
                    }
                }
            }

            List<Map<String, Object>> qlzfsj = groupByZh1(zfqlmcxs);
            List<Map<String, Object>> qlyfsj = groupByZh1(yfqlmcxs);
            List<Map<String, Object>> sdzfsj = groupByZh1(zfsdmcxs);
            List<Map<String, Object>> sdyfsj = groupByZh1(yfsdmcxs);

            List<Map<String, Object>> allzfsj = mergeList(datazdzf);
            List<Map<String, Object>> allyfsj = mergeList(datazdyf);
            writeExcelData(proname,htd,allzfsj,allyfsj,sdzfsj,sdyfsj,qlzfsj,qlyfsj,cdsl,sjz,zx);

        }

    }

    /**
     * 合并匝道的sfc值，需要根据name和qdzh
     * @param list
     * @return
     */
    private static List<Map<String, Object>> mergeList(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()){
            return new ArrayList<>();
        }else {
            Map<String, Map<String, List<String>>> map = new HashMap<>();
            for (Map<String, Object> item : list) {
                String zdbs = String.valueOf(item.get("zdbs"));
                String qdzh = String.valueOf(item.get("qdzh"));
                //String sfc = String.valueOf(item.get("cz"));
                String sfc = "";
                if (item.get("cz") == null){
                    sfc = "-";
                }else {
                    sfc = item.get("cz").toString();
                }
                if (map.containsKey(zdbs)) {
                    Map<String, List<String>> mapItem = map.get(zdbs);
                    if (mapItem.containsKey(qdzh)) {
                        mapItem.get(qdzh).add(sfc);
                    } else {
                        List<String> sfcList = new ArrayList<>();
                        sfcList.add(sfc);
                        mapItem.put(qdzh, sfcList);
                    }
                } else {
                    Map<String, List<String>> mapItem = new HashMap<>();
                    List<String> sfcList = new ArrayList<>();
                    sfcList.add(sfc);
                    mapItem.put(qdzh, sfcList);
                    map.put(zdbs, mapItem);
                }
            }
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, Map<String, List<String>>> entry : map.entrySet()) {
                Map<String, Object> values = new HashMap<>();
                values.put("zdbs", entry.getKey());
                Map<String, List<String>> mapValue = entry.getValue();
                for (Map.Entry<String, List<String>> innerEntry : mapValue.entrySet()) {
                    String qdzh = innerEntry.getKey();
                    List<String> sfcList = innerEntry.getValue();
                    String sfc = String.join(",", sfcList);
                    values.put("qdzh", qdzh);
                    values.put("cz", sfc);
                    // 遍历整个list，查找相同的name和createTime
                    boolean flag = true;
                    for (Map<String, Object> item : list) {
                        if (!String.valueOf(item.get("qdzh")).equals(qdzh) || !String.valueOf(item.get("zdbs")).equals(entry.getKey())) {
                            continue;
                        }
                        if (flag) { // 第一次找到匹配的元素，将name和createTime保存到values中
                            values.put("name", item.get("name"));
                            values.put("createTime", item.get("createTime"));
                            flag = false;
                        }
                        // 如果name和createTime不同，则跳出循环
                        if (!item.get("name").equals(values.get("name")) || !item.get("createTime").equals(values.get("createTime"))) {
                            flag = false;
                            break;
                        }
                    }

                    result.add(new HashMap<>(values));
                }
            }
            Collections.sort(result, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    String name1 = o1.get("zdbs").toString();
                    String name2 = o2.get("zdbs").toString();
                    // 按照名字进行排序
                    int cmp = name1.compareTo(name2);
                    if (cmp != 0) {
                        return cmp;
                    }
                    // 名字相同时按照 qdzh 排序
                    Double qdzh1 = Double.parseDouble(o1.get("qdzh").toString());
                    Double qdzh2 = Double.parseDouble(o2.get("qdzh").toString());
                    return qdzh1.compareTo(qdzh2);
                }
            });
            return result;

        }
    }

    /**
     *
     * @param list
     * @return
     */
    private static List<Map<String, Object>> groupByZh1(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()){
            return new ArrayList<>();
        }else {
            Map<String, List<String>> resultMap = new TreeMap<>();
            for (Map<String, Object> map : list) {
                String zh = map.get("qdzh").toString();
                String sfc = "";
                if (map.get("cz") == null){
                    sfc = "-";
                }else {
                    sfc = map.get("cz").toString();
                }
                if (resultMap.containsKey(zh)) {
                    resultMap.get(zh).add(sfc);
                } else {
                    List<String> sfcList = new ArrayList<>();
                    sfcList.add(sfc);
                    resultMap.put(zh, sfcList);
                }
            }
            List<Map<String, Object>> resultList = new LinkedList<>();
            for (Map.Entry<String, List<String>> entry : resultMap.entrySet()) {
                Map<String, Object> map = new TreeMap<>();
                map.put("qdzh", entry.getKey());
                map.put("cz", String.join(",", entry.getValue()));
                for (Map<String, Object> item : list) {
                    if (item.get("qdzh").toString().equals(entry.getKey())) {
                        map.put("name", item.get("name"));
                        map.put("createTime", item.get("createTime"));
                        map.put("zdbs", item.get("zdbs"));
                        break;
                    }
                }
                resultList.add(map);
            }
            Collections.sort(resultList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    double name1 = Double.valueOf(o1.get("qdzh").toString());
                    double name2 = Double.valueOf(o2.get("qdzh").toString());
                    return Double.compare(name1, name2);
                }
            });

            return resultList;
        }
    }

    /**
     *将相同幅的cz拼接
     * @param list
     * @return
     */
    private static List<Map<String, Object>> groupByZh(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()){
            return new ArrayList<>();
        }else {
            Map<String, List<String>> resultMap = new TreeMap<>();
            for (Map<String, Object> map : list) {
                String zh = map.get("qdzh").toString();
                String sfc = "";
                if (map.get("cz") == null){
                    sfc = "-";
                }else {
                    sfc = map.get("cz").toString();
                }
                if (resultMap.containsKey(zh)) {
                    resultMap.get(zh).add(sfc);
                } else {
                    List<String> sfcList = new ArrayList<>();
                    sfcList.add(sfc);
                    resultMap.put(zh, sfcList);
                }
            }
            List<Map<String, Object>> resultList = new LinkedList<>();
            for (Map.Entry<String, List<String>> entry : resultMap.entrySet()) {
                Map<String, Object> map = new TreeMap<>();
                map.put("qdzh", entry.getKey());
                map.put("cz", String.join(",", entry.getValue()));
                for (Map<String, Object> item : list) {
                    if (item.get("qdzh").toString().equals(entry.getKey())) {
                        map.put("name", item.get("name"));
                        map.put("createTime", item.get("createTime"));
                        break;
                    }
                }
                resultList.add(map);
            }
            Collections.sort(resultList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    double name1 = Double.valueOf(o1.get("qdzh").toString());
                    double name2 = Double.valueOf(o2.get("qdzh").toString());
                    return Double.compare(name1, name2);
                }
            });

            return resultList;
        }
    }

    /**
     *
     * @param proname
     * @param htd
     * @param lmzflist
     * @param lmyflist
     * @param sdzxList
     * @param sdyxList
     * @param qlzxList
     * @param qlyxList
     * @param cdsl
     * @param sjz
     * @param zx
     */
    private void writeExcelData(String proname, String htd, List<Map<String, Object>> lmzflist, List<Map<String, Object>> lmyflist, List<Map<String, Object>> sdzxList, List<Map<String, Object>> sdyxList, List<Map<String, Object>> qlzxList, List<Map<String, Object>> qlyxList, int cdsl, String sjz, String zx) throws IOException, ParseException {
        XSSFWorkbook wb = null;
        String fname="";
        if (zx.equals("主线")){
            fname = "14路面车辙.xlsx";
        }else {
            fname = "69互通车辙-"+zx+".xlsx";
        }

        File f = new File(jgfilepath+File.separator+proname+File.separator+htd+File.separator+fname);
        File fdir = new File(jgfilepath + File.separator + proname + File.separator + htd);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        try {
            File directory = new File("service-system/src/main/resources/static");
            String reportPath = directory.getCanonicalPath();
            String filename = "";
            String sheetlmname = "";
            String sheetqname = "";
            String sheetsname = "";

            if (cdsl == 5){
                filename = "车辙-5车道.xlsx";
            }else if (cdsl == 4){
                filename = "车辙-4车道.xlsx";
            }else if (cdsl == 3){
                filename = "车辙-3车道.xlsx";
            }else if (cdsl == 2){
                filename = "车辙-2车道.xlsx";
            }
            sheetlmname="路面";
            sheetqname="桥";
            sheetsname="隧道";
            String path = reportPath + File.separator + filename;
            Files.copy(Paths.get(path), new FileOutputStream(f));
            FileInputStream out = new FileInputStream(f);
            wb = new XSSFWorkbook(out);

            List<Map<String,Object>> zfsdqlData = new ArrayList<>();
            List<Map<String,Object>> yfsdqlData = new ArrayList<>();

            if (sdzxList.size() >0 && !sdzxList.isEmpty()){
                for (Map<String, Object> map : sdzxList) {
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("zh",map.get("qdzh").toString());
                    map1.put("name",map.get("name").toString());
                    map1.put("cd","左幅");
                    if (map.get("zdbs")!=null){
                        map1.put("zdbs",map.get("zdbs").toString());
                    }
                    zfsdqlData.add(map1);
                }
                DBtoExcel(proname,htd,sdzxList,wb,"左幅-"+sheetsname,cdsl,sjz,zx);
            }

            if (sdyxList.size() >0 && !sdyxList.isEmpty()){
                for (Map<String, Object> map : sdyxList) {
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("zh",map.get("qdzh").toString());
                    map1.put("name",map.get("name").toString());
                    map1.put("cd","右幅");
                    if (map.get("zdbs")!=null){
                        map1.put("zdbs",map.get("zdbs").toString());
                    }
                    yfsdqlData.add(map1);
                }
                DBtoExcel(proname,htd,sdyxList,wb,"右幅-"+sheetsname,cdsl,sjz,zx);
            }
            if (qlzxList.size() >0 && !qlzxList.isEmpty()){
                for (Map<String, Object> map : qlzxList) {
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("zh",map.get("qdzh").toString());
                    map1.put("name",map.get("name").toString());
                    map1.put("cd","左幅");
                    if (map.get("zdbs")!=null){
                        map1.put("zdbs",map.get("zdbs").toString());
                    }
                    zfsdqlData.add(map1);
                }
                DBtoExcel(proname,htd,qlzxList,wb,"左幅-"+sheetqname,cdsl,sjz,zx);
            }
            if (qlyxList.size() >0 && !qlyxList.isEmpty()){
                for (Map<String, Object> map : qlyxList) {
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("zh",map.get("qdzh").toString());
                    map1.put("name",map.get("name").toString());
                    map1.put("cd","右幅");
                    if (map.get("zdbs")!=null){
                        map1.put("zdbs",map.get("zdbs").toString());
                    }
                    yfsdqlData.add(map1);
                }
                DBtoExcel(proname,htd,qlyxList,wb,"右幅-"+sheetqname,cdsl,sjz,zx);
            }


            List<Map<String, Object>> zsdql = sortList(zfsdqlData);
            List<Map<String, Object>> ysdql = sortList(yfsdqlData);

            /**
             * 这块需要分别将隧道和桥梁的桩号取出，给写入路面的时候使用
             */

            if (lmzflist.size() >0 && !lmzflist.isEmpty()){
                if (zx.equals("主线")){
                    DBtoExcelLm(proname,htd,lmzflist,zsdql,wb,"左幅-"+sheetlmname,cdsl,sjz);
                }else {
                    DBtoExcelZd(proname,htd,lmzflist,zsdql,wb,"左幅-"+sheetlmname,cdsl,sjz,zx);
                }

            }
            if (lmyflist.size() >0 && !lmyflist.isEmpty()){
                if (zx.equals("主线")){
                    DBtoExcelLm(proname,htd,lmyflist,ysdql,wb,"右幅-"+sheetlmname,cdsl,sjz);
                }else {
                    DBtoExcelZd(proname,htd,lmyflist,ysdql,wb,"右幅-"+sheetlmname,cdsl,sjz,zx);
                }
            }
            String[] arr = {"左幅-路面","右幅-路面","左幅-隧道","右幅-隧道","左幅-桥","右幅-桥"};
            for (int i = 0; i < arr.length; i++) {
                if (shouldBeCalculate(wb.getSheet(arr[i]))) {
                    /*if (arr[i].contains("路面")) {
                        calculatePavementSheet(wb, wb.getSheet(arr[i]), cdsl);
                    } else {*/
                        calculateTunnelAndBridgeSheet(wb, wb.getSheet(arr[i]), cdsl);
                    //}
                    JjgFbgcCommonUtils.updateFormula(wb, wb.getSheet(arr[i]));
                } else {
                    wb.removeSheetAt(wb.getSheetIndex(arr[i]));
                }
            }

            FileOutputStream fileOut = new FileOutputStream(f);
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();
            out.close();
            wb.close();
        }catch (Exception e) {
            if(f.exists()){
                f.delete();
            }
            throw new JjgysException(20001, "生成鉴定表错误，请检查数据的正确性");
        }


    }

    /**
     *
     * @param wb
     * @param sheet
     * @param cdsl
     */
    private void calculateTunnelAndBridgeSheet(XSSFWorkbook wb,XSSFSheet sheet,int cdsl) {
        log.info("开始计算{}",sheet.getSheetName());
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        boolean flag = false;
        int count = 0;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            // 下一张表
            if (!"".equals(row.getCell(0).toString()) && row.getCell(0).toString().contains("质量鉴定表") && flag) {
                flag = false;
            }
            if(flag){
                rowstart = sheet.getRow(i);
                rowend = sheet.getRow(i+26);
                calculateTotalData(sheet, rowstart, rowend, i,cdsl);
                count ++;
                i += 26;
            }
            if ("桩号".equals(row.getCell(0).toString())) {
                rowstart = sheet.getRow(i+3);
                rowend = sheet.getRow(i+29);
                calculateTotalData(sheet, rowstart, rowend, i+3,cdsl);
                i += 29;
                count ++;
                flag = true;
            }
        }
        setTunnelAndBrodgeTotal(wb,sheet, count,cdsl);
    }

    /**
     *
     * @param wb
     * @param sheet
     * @param count
     */
    public void setTunnelAndBrodgeTotal(XSSFWorkbook wb,XSSFSheet sheet, int count,int cdsl){
        FormulaEvaluator e = new XSSFFormulaEvaluator(wb);
        int c = 0;
        String bs1 = "", bs2 = "";
        if (cdsl == 2){
            c = 12;
            bs1 = "G";
            bs2 = "K";
        }else if (cdsl == 3){
            c = 16;
            bs1 = "I";
            bs2 = "N";
        }else if (cdsl ==4){
            c = 15;
            bs1 = "H";
            bs2 = "L";
        }else if (cdsl ==5){
            c = 18;
            bs1 = "I";
            bs2 = "N";
        }
        for(int i = 0; i < count; i++){
            sheet.getRow(i+6).createCell(c).setCellFormula("OFFSET($"+bs1+"$3,((ROW()-7)*33),0,1,1)");
            String name = e.evaluate(sheet.getRow(i+6).getCell(c)).getStringValue();
            sheet.getRow(i+6).getCell(c).setCellFormula(null);
            sheet.getRow(i+6).getCell(c).setCellValue(name);

            for(int j=0;j<6;j++){
                sheet.getRow(i+6).createCell(j+c+1).setCellFormula("OFFSET("+bs2+""+(28+j)+",((ROW()-7)*33),0,1,1)");//R=OFFSET(N28,((ROW()-7)*26),0,1,1)
                double value = e.evaluate(sheet.getRow(i+6).getCell(j+c+1)).getNumberValue();
                sheet.getRow(i+6).getCell(j+c+1).setCellFormula(null);
                if((j == 0 || j == 1) && value < 0.0001){
                    sheet.getRow(i+6).getCell(j+c+1).setCellValue("");
                }
                else{
                    sheet.getRow(i+6).getCell(j+c+1).setCellValue(value);
                }
            }
        }

        //统计
        sheet.getRow(0).createCell(c+1).setCellValue("最大值");
        sheet.getRow(0).createCell(c+2).setCellValue("最小值");
        sheet.getRow(0).createCell(c+3).setCellValue("平均值");
        sheet.getRow(0).createCell(c+4).setCellValue("标准差");
        sheet.getRow(0).createCell(c+5).setCellValue("检测点数");
        sheet.getRow(0).createCell(c+6).setCellValue("合格点数");
        sheet.getRow(0).createCell(c+7).setCellValue("合格率");
        //sheet.getRow(0).createCell(c+5).setCellValue("平均值");

        sheet.getRow(1).createCell(c+1).setCellFormula("MAX("+sheet.getRow(6).getCell(c+1).getReference()+":"
                +sheet.getRow(6+count).createCell(c+1).getReference()+")");
        double value = e.evaluate(sheet.getRow(1).getCell(c+1)).getNumberValue();
        sheet.getRow(1).getCell(c+1).setCellFormula(null);
        if(value < 0.0001){
            sheet.getRow(1).getCell(c+1).setCellValue("");
        }
        else{
            sheet.getRow(1).getCell(c+1).setCellValue(value);
        }

        sheet.getRow(1).createCell(c+2).setCellFormula("MIN("+sheet.getRow(6).getCell(c+2).getReference()+":"
                +sheet.getRow(6+count).createCell(c+2).getReference()+")");
        value = e.evaluate(sheet.getRow(1).getCell(c+2)).getNumberValue();
        sheet.getRow(1).getCell(c+2).setCellFormula(null);
        if(value < 0.0001){
            sheet.getRow(1).getCell(c+2).setCellValue("");
        }
        else{
            sheet.getRow(1).getCell(c+2).setCellValue(value);
        }

        sheet.getRow(1).createCell(c+3).setCellFormula("AVERAGE("+sheet.getRow(6).getCell(c+3).getReference()+":"
                +sheet.getRow(6+count).createCell(c+3).getReference()+")");

        sheet.getRow(1).createCell(c+5).setCellFormula("SUM("+sheet.getRow(6).getCell(c+5).getReference()+":"
                +sheet.getRow(6+count).createCell(c+5).getReference()+")");
        value = e.evaluate(sheet.getRow(1).getCell(c+5)).getNumberValue();
        sheet.getRow(1).getCell(c+5).setCellFormula(null);
        sheet.getRow(1).getCell(c+5).setCellValue(value);

        sheet.getRow(1).createCell(c+6).setCellFormula("SUM("+sheet.getRow(6).getCell(c+6).getReference()+":"
                +sheet.getRow(6+count).createCell(c+6).getReference()+")");
        value = e.evaluate(sheet.getRow(1).getCell(c+6)).getNumberValue();
        sheet.getRow(1).getCell(c+6).setCellFormula(null);
        sheet.getRow(1).getCell(c+6).setCellValue(value);

        sheet.getRow(1).createCell(c+7).setCellFormula(
                sheet.getRow(1).getCell(c+6).getReference()+"*100/"+ sheet.getRow(1).getCell(c+5).getReference());
    }

    /**
     *
     * @param wb
     * @param sheet
     * @param cdsl
     */
    private void calculatePavementSheet(XSSFWorkbook wb, XSSFSheet sheet, int cdsl) {
        log.info("开始计算{}",sheet.getSheetName());
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        boolean flag = false;
        int count = 0;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            // 下一张表
            if (!"".equals(row.getCell(3-3).toString()) && row.getCell(3-3).toString().contains("质量鉴定表") && flag) {
                flag = false;
            }
            if(flag){
                rowstart = sheet.getRow(i);
                rowend = sheet.getRow(i+26);
                calculateTotalData(sheet, rowstart, rowend, i,cdsl);
                count ++;
                i += 26;
            }
            if ("桩号".equals(row.getCell(3-3).toString())) {
                if(!flag){
                    rowstart = sheet.getRow(i+3);
                    rowend = sheet.getRow(i+29);
                    calculateTotalData(sheet, rowstart, rowend, i+3,cdsl);
                    i += 29;
                    count ++;
                    flag = true;
                }
            }
        }
        setPavementTotal(wb,sheet, count,cdsl);
        log.info("{}计算完成",sheet.getSheetName());
    }

    /**
     *
     * @param wb
     * @param sheet
     * @param count
     */
    public void setPavementTotal(XSSFWorkbook wb,XSSFSheet sheet, int count,int cdsl){
        int a = 0;
        int b = 0;
        if (cdsl ==2 || cdsl ==3){
            a = 3*cdsl+4;
            b = 3*cdsl+7;
        }else if (cdsl ==4 || cdsl ==5){
            a = 2*cdsl+3;
            b = 2*cdsl+8;
        }

        FormulaEvaluator e = new XSSFFormulaEvaluator(wb);
        for(int i = 27; i < sheet.getPhysicalNumberOfRows(); i+=33){
            sheet.getRow(i+4).createCell(b).setCellFormula(sheet.getRow(i).getCell(a).getReference());
            sheet.getRow(i+4).createCell(b+1).setCellFormula(sheet.getRow(i+1).getCell(a).getReference());
            sheet.getRow(i+4).createCell(b+2).setCellFormula(sheet.getRow(i+2).getCell(a).getReference());
            sheet.getRow(i+4).createCell(b+3).setCellFormula(sheet.getRow(i+3).getCell(a).getReference());
            sheet.getRow(i+4).createCell(b+4).setCellFormula(sheet.getRow(i+4).getCell(a).getReference());
            sheet.getRow(i+4).createCell(b+5).setCellFormula(sheet.getRow(i+5).getCell(a).getReference());

            /*sheet.getRow(i+4).createCell(b).setCellFormula(sheet.getRow(i+4).getCell(a).getReference());
            sheet.getRow(i+4).createCell(b+1).setCellFormula(sheet.getRow(i+5).getCell(a).getReference());*/


        }
        //统计
        sheet.getRow(0).createCell(b).setCellValue("最大值");
        sheet.getRow(0).createCell(b+1).setCellValue("最小值");
        sheet.getRow(0).createCell(b+2).setCellValue("平均值");
        sheet.getRow(0).createCell(b+3).setCellValue("标准差");
        sheet.getRow(0).createCell(b+4).setCellValue("检测点数");
        sheet.getRow(0).createCell(b+5).setCellValue("合格点数");
        sheet.getRow(0).createCell(b+6).setCellValue("合格率");

        //合计总点数
        sheet.getRow(1).createCell(b).setCellType(CellType.STRING);
        sheet.getRow(27).createCell(b).setCellType(CellType.STRING);
        sheet.getRow(1).createCell(b).setCellFormula("MAX("+sheet.getRow(27).getCell(b).getReference()+":"
                +sheet.getRow(sheet.getLastRowNum()).createCell(b).getReference()+")");
        sheet.getRow(1).createCell(b+1).setCellType(CellType.STRING);
        sheet.getRow(27).createCell(b+1).setCellType(CellType.STRING);
        sheet.getRow(1).createCell(b+1).setCellFormula("MIN("+sheet.getRow(27).getCell(b+1).getReference()+":"
                +sheet.getRow(sheet.getLastRowNum()).createCell(b+1).getReference()+")");
        //平均值
        sheet.getRow(1).createCell(b+2).setCellType(CellType.STRING);
        sheet.getRow(27).createCell(b+2).setCellType(CellType.STRING);
        sheet.getRow(1).createCell(b+2).setCellFormula("AVERAGE("+sheet.getRow(27).getCell(b+2).getReference()+":"
                +sheet.getRow(sheet.getLastRowNum()).createCell(b+2).getReference()+")");

        sheet.getRow(1).createCell(b+4).setCellType(CellType.STRING);
        sheet.getRow(27).createCell(b+4).setCellType(CellType.STRING);
        sheet.getRow(sheet.getLastRowNum()).createCell(b+4).setCellType(CellType.STRING);
        sheet.getRow(1).createCell(b+4).setCellFormula("SUM("+sheet.getRow(27).getCell(b+4).getReference()+":"
                +sheet.getRow(sheet.getLastRowNum()).createCell(b+4).getReference()+")");

        sheet.getRow(1).createCell(b+5).setCellType(CellType.STRING);
        sheet.getRow(27).createCell(b+5).setCellType(CellType.STRING);
        sheet.getRow(sheet.getLastRowNum()).createCell(b+5).setCellType(CellType.STRING);
        sheet.getRow(1).createCell(b+5).setCellFormula("SUM("+sheet.getRow(27).getCell(b+5).getReference()+":"
                +sheet.getRow(sheet.getLastRowNum()).createCell(b+5).getReference()+")");

        sheet.getRow(1).createCell(b+6).setCellFormula(
                sheet.getRow(1).getCell(b+5).getReference()+"*100/"+ sheet.getRow(1).getCell(b+4).getReference());
    }

    /**
     *
     * @param sheet
     * @param rowstart
     * @param rowend
     * @param i
     */
    public void calculateTotalData(XSSFSheet sheet, XSSFRow rowstart, XSSFRow rowend, int i,int cdsl){
        if (cdsl == 2 || cdsl == 3){
            int a = 0;
            if (cdsl == 2){
                a = 11;
            }else if (cdsl == 3){
                a = 14;
            }
            sheet.getRow(i+23).getCell(3*cdsl+4).setCellFormula("IF(ISERROR(AVERAGE("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+")),\"-\","+
                    "AVERAGE("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"))");
            //平均值
            //=IF(ISERROR(AVERAGE(B223:D249,F223:H249,J223:L249,N223:P241)),"",AVERAGE(B223:D249,F223:H249,J223:L249,N223:P241))

            sheet.getRow(i+24).getCell(3*cdsl+4).setCellFormula("IF(ISERROR(ROUND(STDEV("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"),3)),\"-\","+
                    "ROUND(STDEV("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"),3))");//=ROUND(STDEV(E7:F33,H7:I33,K7:L33,N7:O25),3)

            sheet.getRow(i+25).getCell(3*cdsl+4).setCellFormula("COUNT("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+")");//=COUNT(E7:F33,H7:I33,K7:L33,N7:O25)
            sheet.getRow(i+26).getCell(3*cdsl+4).setCellFormula("SUM(COUNTIF("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(a).getReference()+"),COUNTIF("
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(a).getReference()+"),COUNTIF("
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(a).getReference()+"),COUNTIF("
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(a).getReference()+"))");//=SUM(COUNTIF(E7:F33,">="&O26),COUNTIF(H7:I33,">="&O26),COUNTIF(K7:L33,">="&O26),COUNTIF(N7:O25,\">=\"&O26))
            sheet.getRow(i+21).getCell(3*cdsl+4).setCellFormula("IF("+sheet.getRow(i+25).getCell(3*cdsl+4).getReference()
                    +"=0,\"-\",MAX("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"))");//=MAX(E7:F33,H7:I33,K7:L33,N7:O25)
            sheet.getRow(i+22).getCell(3*cdsl+4).setCellFormula("IF("+sheet.getRow(i+25).getCell(3*cdsl+4).getReference()
                    +"=0,\"-\",MIN("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"))");//=MIN(E7:F33,H7:I33,K7:L33,N7:O25)
        }else if (cdsl == 4 || cdsl ==5){
            /**2*cdsl+3
             * 4c 6  9
             * 5c 7  11
             */
            //平均值
            sheet.getRow(i+23).getCell(2*cdsl+3).setCellFormula("IF(ISERROR(AVERAGE("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+")),\"-\","+
                    "AVERAGE("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"))");
            //标准差
            sheet.getRow(i+24).getCell(2*cdsl+3).setCellFormula("IF(ISERROR(ROUND(STDEV("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"),3)),\"-\","+
                    "ROUND(STDEV("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"),3))");//=ROUND(STDEV(E7:F33,H7:I33,K7:L33,N7:O25),3)

            //检测点数
            sheet.getRow(i+25).getCell(2*cdsl+3).setCellFormula("COUNT("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+")");//=COUNT(E7:F33,H7:I33,K7:L33,N7:O25)
            //合格点数
            sheet.getRow(i+26).getCell(2*cdsl+3).setCellFormula("SUM(COUNTIF("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(14).getReference() +"),COUNTIF("
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(14).getReference()+ "),COUNTIF("
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(14).getReference()+"))");
            //最大值
            sheet.getRow(i+21).getCell(2*cdsl+3).setCellFormula("IF("+sheet.getRow(i+25).getCell(2*cdsl+3).getReference()
                    +"=0,\"-\",MAX("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"))");
            //最小值
            sheet.getRow(i+22).getCell(2*cdsl+3).setCellFormula("IF("+sheet.getRow(i+25).getCell(2*cdsl+3).getReference()
                    +"=0,\"-\",MIN("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"))");
        }

    }

    /**
     *判断sheet是否为空
     * @param sheet
     * @return
     */
    private boolean shouldBeCalculate(XSSFSheet sheet) {
        sheet.getRow(6).getCell(0).setCellType(CellType.STRING);
        if(sheet.getRow(6).getCell(0)==null ||"".equals(sheet.getRow(6).getCell(0).getStringCellValue())){
            return false;
        }

        return true;
    }

    /**
     *
     * @param proname
     * @param htd
     * @param data
     * @param zfsdqlData
     * @param wb
     * @param sheetname
     * @param cdsl
     * @param sjz
     * @param zx
     */
    private void DBtoExcelZd(String proname, String htd, List<Map<String, Object>> data, List<Map<String, Object>> zfsdqlData, XSSFWorkbook wb, String sheetname, int cdsl, String sjz, String zx) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        if (data!=null && !data.isEmpty()){
            //createTable(getNum1(data),wb,sheetname,cdsl);
            createTable2(getNum2(data,cdsl),wb,sheetname,cdsl);
            XSSFSheet sheet = wb.getSheet(sheetname);
            sheet.getRow(1).getCell(2).setCellValue(proname);
            String name = data.get(0).get("zdbs").toString()+"匝道";
            int index = 0;
            int tableNum = 0;
            String time1 = String.valueOf(data.get(0).get("createTime")) ;
            Date parse = simpleDateFormat.parse(time1);
            String time = outputDateFormat.format(parse);

            fillTitleCellData(sheet, tableNum, proname, htd, name,time,sheetname,cdsl,sjz);

            /**
             *处理数据，将data和zfsdqlData中相同的qdzh的name为空
             * 还得查一下收费站的数据
             */
            List<Map<String, Object>> lmdata = handlezdData(proname,data,zfsdqlData,zx);

            if (lmdata.size()>0) {
                List<Map<String, Object>> rowAndcol = new ArrayList<>();
                int startRow = -1, endRow = -1, startCol = -1, endCol = -1;

                String zdbs = lmdata.get(0).get("zdbs").toString();
                for (Map<String, Object> lm : lmdata) {
                    if (lm.get("zdbs").toString().equals(zdbs)) {
                        if (index > 99) {
                            tableNum++;
                            fillTitleCellData(sheet, tableNum, proname, htd, lm.get("zdbs").toString() + "匝道", time, sheetname, cdsl, sjz);
                            index = 0;
                        }
                        if (!lm.get("cz").toString().equals("") && !lm.get("cz").toString().isEmpty()) {
                            String[] sfc = lm.get("cz").toString().split(",");
                            for (int i = 0; i < sfc.length; i++) {
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue((Double.parseDouble(lm.get("qdzh").toString())));
                                if (sfc[i].equals("-")){
                                    sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl + 1) * (index / 27) + 1 + i).setCellValue("-");
                                }else {
                                    sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl + 1) * (index / 27) + 1 + i).setCellValue(Double.parseDouble(sfc[i]));
                                }

                            }
                        } else {
                            for (int i = 0; i < cdsl; i++) {
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue((Double.parseDouble(lm.get("qdzh").toString())));
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue(lm.get("name").toString());

                                startRow = tableNum * 33 + 6 + index % 27 ;
                                endRow = tableNum * 33 + 6 + index % 27 ;

                                startCol = (cdsl+1)  * (index / 27) + 1;
                                endCol = (cdsl+1)  * (index / 27) + cdsl;
                            }
                            //可以在这块记录一个行和列
                            Map<String, Object> map = new HashMap<>();
                            map.put("startRow", startRow);
                            map.put("endRow", endRow);
                            map.put("startCol", startCol);
                            map.put("endCol", endCol);
                            map.put("name", lm.get("name"));
                            map.put("tableNum", tableNum);
                            rowAndcol.add(map);

                        }
                        index++;
                    } else {
                        zdbs = lm.get("zdbs").toString();
                        tableNum++;
                        index = 0;
                        fillTitleCellData(sheet, tableNum, proname, htd, zdbs + "匝道", time, sheetname, cdsl, sjz);
                        if (index > 99) {
                            tableNum++;
                            fillTitleCellData(sheet, tableNum, proname, htd, zdbs + "匝道", time, sheetname, cdsl, sjz);
                            index = 0;
                        }
                        if (!lm.get("cz").toString().equals("") && !lm.get("cz").toString().isEmpty()) {
                            String[] sfc = lm.get("cz").toString().split(",");
                            for (int i = 0; i < sfc.length; i++) {
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue((Double.parseDouble(lm.get("qdzh").toString())));
                                if (sfc[i].equals("-")){
                                    sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue("-");
                                }else {
                                    sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue(Double.parseDouble(sfc[i]));
                                }

                            }
                        } else {
                            for (int i = 0; i < cdsl; i++) {
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue((Double.parseDouble(lm.get("qdzh").toString())));
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue(lm.get("name").toString());

                                startRow = tableNum * 33 + 6 + index % 27 ;
                                endRow = tableNum * 33 + 6 + index % 27 ;

                                startCol = (cdsl+1)  * (index / 27) + 1;
                                endCol = (cdsl+1)  * (index / 27) + cdsl;
                            }
                            //可以在这块记录一个行和列
                            Map<String, Object> map = new HashMap<>();
                            map.put("startRow", startRow);
                            map.put("endRow", endRow);
                            map.put("startCol", startCol);
                            map.put("endCol", endCol);
                            map.put("name", lm.get("name"));
                            map.put("tableNum", tableNum);
                            rowAndcol.add(map);

                        }
                        index++;
                    }
                }
                List<Map<String, Object>> maps = mergeCells(rowAndcol);
                for (Map<String, Object> map : maps) {
                    sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
                }
            }

        }
    }

    /**
     *
     * @param proname
     * @param data
     * @param zfsdqlData
     * @param zx
     * @return
     */
    private List<Map<String, Object>> handlezdData(String proname,List<Map<String, Object>> data, List<Map<String, Object>> zfsdqlData,String zx) {
        /**
         * data是全部的数据，zfsdqlData是包含桥梁和隧道的数据，还有可能为空
         */
        String name = data.get(0).get("name").toString();
        String lf = "";
        if (name.contains("左幅")) {
            lf = "左幅";
        } else if (name.contains("右幅")) {
            lf = "右幅";
        }
        QueryWrapper<JjgLqsJgSfz> wrapper = new QueryWrapper<>();
        wrapper.like("proname", proname);
        wrapper.like("sshtmc", zx);
        wrapper.like("lf", lf);
        List<JjgLqsJgSfz> jjgSfzs = jjgLqsJgSfzMapper.selectList(wrapper);//(id=1, zdsfzname=淮宁湾收费站, htd=土建2标, lf=左幅, zhq=930.0, zhz=1250.0, pzlx=水泥混凝土, sszd=E, sshtmc=淮宁湾立交, proname=陕西高速, createTime=Tue Jun 13 21:03:29 CST 2023)
        List<Map<String, Object>> sfzlist = new ArrayList<>();
        for (int i = 0; i < jjgSfzs.size(); i++) {
            double zhq = jjgSfzs.get(i).getZhq();
            double zhz = jjgSfzs.get(i).getZhz();
            String zdsfzname = jjgSfzs.get(i).getZdsfzname();
            String sszd = jjgSfzs.get(i).getSszd();
            sfzlist.addAll(incrementByTen(zhq, zhz, zdsfzname, sszd));
        }

        if (zfsdqlData.size() > 0) {
            for (Map<String, Object> datum : data) {
                for (Map<String, Object> zfsdqlDatum : zfsdqlData) {
                    if (datum.get("zdbs").toString().equals(zfsdqlDatum.get("zdbs")) && datum.get("qdzh").toString().equals(zfsdqlDatum.get("zh"))) {
                        datum.put("cz", "");
                        datum.put("name", zfsdqlDatum.get("name"));
                    }
                }
            }
        }
        //收费站的数据是没有的，所以是直接加入到data中
        if (sfzlist.size() > 0) {
            data.addAll(sfzlist);
        }
        Collections.sort(data, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String name1 = o1.get("zdbs").toString();
                String name2 = o2.get("zdbs").toString();
                // 按照名字进行排序
                int cmp = name1.compareTo(name2);
                if (cmp != 0) {
                    return cmp;
                }
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("qdzh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("qdzh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });
        return data;

    }

    /**
     * 收费站桩号加10
     * @param start
     * @param end
     * @param zdsfzname
     * @param sszd
     * @return
     */
    private List<Map<String,Object>> incrementByTen(double start, double end,String zdsfzname,String sszd) {
        List<Map<String,Object>> result = new ArrayList<>();
        if (start <= end) {
            for (double i = start; i <= end; i += 10) {
                Map map = new HashMap();
                map.put("qdzh",i);
                map.put("name",zdsfzname);
                map.put("zdbs",sszd);
                map.put("cz","");
                result.add(map);
            }
            return result;
        } else {
            return new ArrayList();
        }
    }


    /**
     *
     * @param proname
     * @param htd
     * @param data
     * @param zfsdqlData
     * @param wb
     * @param sheetname
     * @param cdsl
     * @param sjz
     */
    private void DBtoExcelLm(String proname, String htd, List<Map<String, Object>> data, List<Map<String, Object>> zfsdqlData, XSSFWorkbook wb, String sheetname, int cdsl, String sjz) throws ParseException {
        log.info("开始写入{}数据",sheetname);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        if (data!=null && !data.isEmpty()){
            createTable2(getNum2(data,cdsl),wb,sheetname,cdsl);
            XSSFSheet sheet = wb.getSheet(sheetname);
            sheet.getRow( 1).getCell(2).setCellValue(proname);
            int a = 0;
            if (cdsl == 2){
                a = 10;
            }else if (cdsl == 4){
                a = 11;
            }else if (cdsl == 3 || cdsl == 5 ){
                a = 13;
            }
            sheet.getRow( 1).getCell(a).setCellValue(htd);

            String name = data.get(0).get("name").toString();
            int index = 0;
            int tableNum = 0;
            String time1 = String.valueOf(data.get(0).get("createTime")) ;
            Date parse = simpleDateFormat.parse(time1);
            String time = outputDateFormat.format(parse);
            fillTitleCellData(sheet, tableNum, proname, htd, name,time,sheetname,cdsl,sjz);
            List<Map<String, Object>> lmdata = handleLmData(data,zfsdqlData);
            List<Map<String, Object>> rowAndcol = new ArrayList<>();
            int startRow = -1, endRow = -1, startCol = -1, endCol = -1;
            if (cdsl == 2 || cdsl == 3){
                for (Map<String, Object> lm : lmdata) {
                    if (index > 99) {
                        tableNum++;
                        fillTitleCellData(sheet, tableNum, proname, htd, name, time, sheetname,cdsl,sjz);
                        index = 0;
                    }
                    if (!lm.get("cz").toString().equals("") && !lm.get("cz").toString().isEmpty()) {
                        String[] sfc = lm.get("cz").toString().split(",");
                        for (int i = 0; i < sfc.length; i++) {
                            sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue((Double.parseDouble(lm.get("qdzh").toString())));
                            if (sfc[i].equals("-")){
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue("-");
                            }else {
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue(Double.parseDouble(sfc[i]));
                            }

                        }

                    } else {
                        for (int i = 0; i < cdsl; i++) {
                            sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue((Double.parseDouble(lm.get("qdzh").toString())));
                            sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue(lm.get("name").toString());

                            startRow = tableNum * 33 + 6 + index % 27 ;
                            endRow = tableNum * 33 + 6 + index % 27 ;

                            startCol = (cdsl+1)  * (index / 27) + 1;
                            endCol = (cdsl+1)  * (index / 27) + cdsl;

                        }
                        //可以在这块记录一个行和列
                        Map<String, Object> map = new HashMap<>();
                        map.put("startRow",startRow);
                        map.put("endRow",endRow);
                        map.put("startCol",startCol);
                        map.put("endCol",endCol);
                        map.put("name",lm.get("name"));
                        map.put("tableNum",tableNum);
                        rowAndcol.add(map);
                    }
                    index++;
                }
            }else if (cdsl == 4 || cdsl == 5){
                for (Map<String, Object> lm : lmdata) {
                    if (index % 73 == 0 && index != 0) {
                        tableNum++;
                        fillTitleCellData(sheet, tableNum, proname, htd, name, time, sheetname,cdsl,sjz);
                        index = 0;
                    }
                    if (!lm.get("cz").toString().equals("") && !lm.get("cz").toString().isEmpty()) {
                        String[] sfc = lm.get("cz").toString().split(",");
                        for (int i = 0; i < sfc.length; i++) {
                            sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue((Double.parseDouble(lm.get("qdzh").toString())));
                            if (sfc[i].equals("-")){
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue("-");
                            }else {
                                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue(Double.parseDouble(sfc[i]));
                            }

                        }

                    } else {
                        for (int i = 0; i < cdsl; i++) {
                            sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue((Double.parseDouble(lm.get("qdzh").toString())));
                            sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27) + 1 + i).setCellValue(lm.get("name").toString());

                            startRow = tableNum * 33 + 6 + index % 27 ;
                            endRow = tableNum * 33 + 6 + index % 27 ;

                            startCol = (cdsl+1)  * (index / 27) + 1;
                            endCol = (cdsl+1)  * (index / 27) + cdsl;

                        }
                        //可以在这块记录一个行和列
                        Map<String, Object> map = new HashMap<>();
                        map.put("startRow",startRow);
                        map.put("endRow",endRow);
                        map.put("startCol",startCol);
                        map.put("endCol",endCol);
                        map.put("name",lm.get("name"));
                        map.put("tableNum",tableNum);
                        rowAndcol.add(map);

                    }
                    index++;
                }
            }

            List<Map<String, Object>> maps = mergeCells(rowAndcol);
            for (Map<String, Object> map : maps) {
                sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
            }
        }
        log.info("{}数据写入完成",sheetname);
    }

    /**
     *
     * @param data
     * @param zfsdqlData
     * @return
     */
    private List<Map<String, Object>> handleLmData(List<Map<String, Object>> data, List<Map<String, Object>> zfsdqlData) {
        for (Map<String, Object> datum : data) {
            for (Map<String, Object> zfsdqlDatum : zfsdqlData) {
                if (datum.get("qdzh").toString().equals(zfsdqlDatum.get("zh"))){
                    datum.put("cz","");
                    datum.put("name",zfsdqlDatum.get("name"));
                }
            }
        }
        Collections.sort(data, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("qdzh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("qdzh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });
        return data;

    }

    /**
     * 合并单元格
     * @param rowAndcol
     * @return
     */
    private List<Map<String, Object>> mergeCells(List<Map<String, Object>> rowAndcol) {
        List<Map<String, Object>> result = new ArrayList<>();
        int currentEndRow = -1;
        int currentStartRow = -1;
        int currentStartCol = -1;
        int currentEndCol = -1;
        String currentName = null;
        int currentTableNum = -1;
        for (Map<String, Object> row : rowAndcol) {
            int tableNum = (int) row.get("tableNum");
            int startRow = (int) row.get("startRow");
            int endRow = (int) row.get("endRow");
            int startCol = (int) row.get("startCol");
            int endCol = (int) row.get("endCol");
            String name = (String) row.get("name");
            if (currentName == null || !currentName.equals(name) || currentStartCol != startCol || currentEndCol != endCol || currentTableNum != tableNum) {
                if (currentStartRow != -1) {
                    for (int i = currentStartRow; i <= currentEndRow && i < result.size(); i++) {
                        Map<String, Object> newRow = new HashMap<>();
                        newRow.put("name", currentName);
                        newRow.put("startRow", currentStartRow);
                        newRow.put("endRow", currentEndRow);
                        newRow.put("startCol", currentStartCol);
                        newRow.put("endCol", currentEndCol);
                        newRow.put("tableNum", currentTableNum);
                        newRow.putAll(result.get(i));
                        result.set(i, newRow);
                    }
                }
                currentName = name;
                currentStartCol = startCol;
                currentEndCol = endCol;
                currentTableNum = tableNum;
                currentStartRow = startRow;
                currentEndRow = endRow;
                result.add(row);
            } else {
                Map<String, Object> lastRow = result.get(result.size() - 1);
                lastRow.put("endRow", endRow);
                currentEndRow = endRow;
            }
        }
        if (currentStartRow != -1) {
            for (int i = currentStartRow; i <= currentEndRow && i < result.size(); i++) {
                Map<String, Object> newRow = new HashMap<>();
                newRow.put("name", currentName);
                newRow.put("startRow", currentStartRow);
                newRow.put("endRow", currentEndRow);
                newRow.put("startCol", currentStartCol);
                newRow.put("endCol", currentEndCol);
                newRow.put("tableNum", currentTableNum);
                newRow.putAll(result.get(i));
                result.set(i, newRow);
            }
        }
        return result;
    }

    /**
     *
     * @param data
     * @return
     */
    private List<Map<String, Object>> sortList(List<Map<String, Object>> data) {
        Collections.sort(data, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("zh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("zh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });
        return data;
    }

    /**
     *
     * @param proname
     * @param htd
     * @param data
     * @param wb
     * @param sheetname
     * @param cdsl
     * @param sjz
     * @param zx
     * @throws ParseException
     */
    private void DBtoExcel(String proname, String htd, List<Map<String, Object>> data, XSSFWorkbook wb, String sheetname, int cdsl, String sjz, String zx) throws ParseException {
        log.info("开始写入{}数据",sheetname);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        if (data!=null && !data.isEmpty()){
            createTable2(getNum2(data,cdsl),wb,sheetname,cdsl);
            XSSFSheet sheet = wb.getSheet(sheetname);
            String time = String.valueOf(data.get(0).get("createTime")) ;
            Date parse = simpleDateFormat.parse(time);
            String sj = outputDateFormat.format(parse);
            int a = 0;
            int b = 0;
            if (cdsl == 2){
                a = 10;
                b = 11;
            }else if (cdsl == 4){
                a = 11;
                b = 14;
            }else if (cdsl == 3 || cdsl == 5 ){
                a = 13;
                b = 14;
            }
            sheet.getRow(1).getCell(2).setCellValue(proname);
            sheet.getRow(1).getCell(a).setCellValue(htd);
            sheet.getRow(2).getCell(b).setCellValue(sj);

            String name = data.get(0).get("name").toString();
            int index = 0;
            int tableNum = 0;
            fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
            int z = 0;
            if (cdsl == 2 || cdsl == 3){
                z = 100;

            }else if (cdsl == 4 || cdsl == 5){
                z = 73;
            }
            for (int i = 0; i < data.size(); i++) {
                if (name.equals(data.get(i).get("name"))) {
                    //if(index == 100){
                    if (index % z == 0 && index != 0) {
                        tableNum++;
                        fillTitleCellData(sheet, tableNum, proname, htd, name, sj, sheetname, cdsl, sjz);
                        index = 0;

                    }
                    fillCommonCellData(sheet, tableNum, index, data.get(i), cdsl, zx);
                    index++;

                } else {
                    name = data.get(i).get("name").toString();
                    tableNum++;
                    index = 0;
                    fillTitleCellData(sheet, tableNum, proname, htd, name, sj, sheetname, cdsl, sjz);
                    fillCommonCellData(sheet, tableNum, index, data.get(i), cdsl, zx);
                    index += 1;
                }
            }

        }
        log.info("{}数据写入完成",sheetname);

    }

    /**
     *
     * @param sheet
     * @param tableNum
     * @param proname
     * @param htd
     * @param name
     * @param time
     * @param sheetname
     * @param cdsl
     * @param sjz
     */
    private void fillTitleCellData(XSSFSheet sheet, int tableNum, String proname, String htd, String name,String time,String sheetname,int cdsl,String sjz) {
        String fbgcname = "";
        if (sheetname.contains("隧道")){
            fbgcname = "隧道路面";
        }else if (sheetname.contains("桥")){
            fbgcname = "桥面系";
        }else {
            fbgcname = "路面面层";
        }
        int a = 0,b = 0,c = 0;
        if (cdsl == 2){
            a = 10;
            b = 6;
            c = 11;
        }else if (cdsl == 4){
            a = 11;
            b = 7;
            c = 14;
        }else if (cdsl == 3 || cdsl == 5 ){
            a = 13;
            b = 8;
            c = 14;
        }
        sheet.getRow(tableNum * 33 + 1).getCell(2).setCellValue(proname);
        sheet.getRow(tableNum * 33 + 1).createCell(a-1).setCellType(CellType.STRING);
        sheet.getRow(tableNum * 33 + 1).createCell(a).setCellType(CellType.STRING);
        sheet.getRow(tableNum * 33 + 1).getCell(a-1).setCellValue("合同段：");
        sheet.getRow(tableNum * 33 + 1).getCell(a).setCellValue(htd);
        sheet.getRow(tableNum * 33 + 2).getCell(2).setCellValue("路面工程");
        sheet.getRow(tableNum * 33 + 2).getCell(a).setCellValue(time);
        sheet.getRow(tableNum * 33 + 2).getCell(b).setCellValue(fbgcname+"("+name+")");
        sheet.getRow(tableNum * 33 + 25).getCell(c).setCellValue(Double.parseDouble(sjz));
    }


    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param row
     * @param cdsl
     * @param zx
     */
    private void fillCommonCellData(XSSFSheet sheet, int tableNum, int index, Map<String, Object> row,int cdsl,String zx) {
        String[] sfc = row.get("cz").toString().split(",");
        for (int i = 0 ; i < sfc.length ; i++) {
            sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue(Double.valueOf(row.get("qdzh").toString()));
            if (sfc[i].equals("-")){
                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)+1+i).setCellValue("-");
            }else {
                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)+1+i).setCellValue(Double.parseDouble(sfc[i]));

            }
        }
    }


    private int getNum2(List<Map<String, Object>> data,int cdsl) {
        int a = 0;
        if (cdsl == 2 || cdsl ==3){
            a = 100;
        }else if (cdsl == 4 || cdsl == 5){
            a = 73;
        }
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> map : data) {
            String name = map.get("name").toString();
            if (resultMap.containsKey(name)) {
                resultMap.put(name, resultMap.get(name) + 1);
            } else {
                resultMap.put(name, 1);
            }
        }
        int num = 0;
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            int value = entry.getValue();
            if (value%a==0){
                num += value/a;
            }else {
                num += value/a+1;
            }
        }
        return num;
    }


    private void createTable2(int num, XSSFWorkbook wb, String sheetname, int cdsl) {
        int a  = 0;
        if (cdsl == 2){
            a = 11;
        }else if (cdsl == 3){
            a = 15;
        }else if (cdsl == 4){
            a = 14;
        }else if (cdsl == 5){
            a = 17;
        }
        int record = 0;
        record = num;
        for (int i = 1; i < record; i++) {
            RowCopy.copyRows(wb, sheetname, sheetname, 0, 32, i * 33);
        }
        if(record >= 1){
            wb.setPrintArea(wb.getSheetIndex(sheetname), 0, a, 0,(record) * 33 - 1);
        }
    }


    @Override
    public void exportcz(HttpServletResponse response, String cdsl) throws IOException {
        int cd = Integer.parseInt(cdsl);
        String fileName = "车辙实测数据";
        String[][] sheetNames = {
                {"左幅一车道","左幅二车道","右幅一车道","右幅二车道"},
                {"左幅一车道","左幅二车道","左幅三车道","右幅一车道","右幅二车道","右幅三车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","左幅五车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道","右幅五车道"}
        };
        String[] sheetName = sheetNames[cd-2];
        ExcelUtil.writeExcelMultipleSheets(response, null, fileName, sheetName, new JjgZdhCzJgfcVo());

    }

    @Override
    public void importcz(MultipartFile file, String proname, String username) throws IOException {
        // 获取文件输入流
        InputStream inputStream = file.getInputStream();
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        int number = workbook.getNumberOfSheets();
        for (int i = 0; i < number; i++) {
            String sheetName = workbook.getSheetName(i);
            int sheetIndex = workbook.getSheetIndex(workbook.getSheetAt(i));
            try {
                EasyExcel.read(file.getInputStream())
                        .sheet(sheetIndex)
                        .head(JjgZdhCzJgfcVo.class)
                        .headRowNumber(1)
                        .registerReadListener(
                                new ExcelHandler<JjgZdhCzJgfcVo>(JjgZdhCzJgfcVo.class) {
                                    @Override
                                    public void handle(List<JjgZdhCzJgfcVo> dataList) {
                                        for(JjgZdhCzJgfcVo czVo: dataList)
                                        {
                                            JjgZdhCzJgfc cz = new JjgZdhCzJgfc();
                                            BeanUtils.copyProperties(czVo,cz);
                                            cz.setCreatetime(new Date());
                                            cz.setProname(proname);
                                            cz.setQdzh(Double.parseDouble(czVo.getQdzh()));
                                            cz.setZdzh(Double.parseDouble(czVo.getZdzh()));
                                            cz.setCd(sheetName);
                                            cz.setUsername(username);
                                            if (sheetName.contains("一")){
                                                cz.setVal(1);
                                            }else if (sheetName.contains("二")){
                                                cz.setVal(2);
                                            }else if (sheetName.contains("三")){
                                                cz.setVal(3);
                                            }else if (sheetName.contains("四")){
                                                cz.setVal(4);
                                            }else if (sheetName.contains("五")){
                                                cz.setVal(5);
                                            }
                                            jjgZdhCzJgfcMapper.insert(cz);
                                        }
                                    }
                                }
                        ).doRead();
            } catch (IOException e) {
                throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
            }
        }

        // 关闭输入流
        inputStream.close();

    }

    @Override
    public List<Map<String, Object>> selectlx(String proname, String htd) {
        List<Map<String,Object>> lxlist = jjgZdhCzJgfcMapper.selectlx(proname,htd);
        return lxlist;
    }

    @Override
    public List<Map<String, Object>> lookpjz(String proname) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        //先查有哪些合同段
        List<Map<String,Object>> htdList = jjgZdhCzJgfcMapper.selecthtd(proname);
        List<Map<String,Object>> reultlist = new ArrayList<>();
        if (htdList!=null){
            for (Map<String, Object> map : htdList) {
                String htd = map.get("htd").toString();
                String lxbs = map.get("lxbs").toString();
                int num = jjgZdhCzJgfcMapper.selectcdnum(proname,htd,lxbs);
                int cds = num;
                int c = 0;
                if (cds == 2){
                    c = 12;
                }else if (cds == 3){
                    c = 16;
                }else if (cds ==4){
                    c = 15;
                }else if (cds ==5){
                    c = 18;
                }

                File f;
                if (lxbs.equals("主线")){
                    f = new File(jgfilepath + File.separator + proname + File.separator + htd + File.separator + "14路面车辙.xlsx");
                }else {
                    f = new File(jgfilepath + File.separator + proname + File.separator + htd + File.separator + "69互通车辙-"+lxbs+".xlsx");
                }
                if (!f.exists()) {
                    return new ArrayList<>();
                } else {
                    XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));

                    List<Map<String,Object>> templist = new ArrayList<>();
                    for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                        if (!wb.isSheetHidden(wb.getSheetIndex(wb.getSheetAt(j)))) {
                            XSSFSheet slSheet = wb.getSheetAt(j);
                            XSSFCell xmname = slSheet.getRow(1).getCell(2);//项目名
                            XSSFCell htdname = slSheet.getRow(1).getCell(cds*3+4);//合同段名
                            if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                                //slSheet.getRow(1).getCell(cds*4+13).setCellType(CellType.STRING);
                                slSheet.getRow(1).getCell(c+3).setCellType(CellType.STRING);
                                Map tempmap = new HashMap();
                                tempmap.put("检测项目", lxbs);
                                tempmap.put("路面类型", wb.getSheetName(j));
                                tempmap.put("平均值", slSheet.getRow(1).getCell(c+3).getStringCellValue());
                                templist.add(tempmap);
                            }

                        }
                    }
                    if (templist!=null){
                        List<Double> list = new ArrayList<>();
                        for (Map<String, Object> stringObjectMap : templist) {
                            String pjz = stringObjectMap.get("平均值").toString();
                            list.add(Double.valueOf(pjz));
                        }
                        double sum = 0;
                        for (Double value : list) {
                            sum += value;
                        }
                        double average = sum / list.size();
                        Map tempmap = new HashMap();
                        tempmap.put("proname",proname);
                        tempmap.put("htd",htd);
                        tempmap.put("lxbs",lxbs);
                        tempmap.put("lmlx","沥青路面");
                        tempmap.put("pjz",df.format(average));
                        reultlist.add(tempmap);
                    }
                }
            }
        }
        return reultlist;
    }

    @Override
    public List<Map<String, Object>> selecthtd(String proname) {
        List<Map<String,Object>> htdList = jjgZdhCzJgfcMapper.selecthtd(proname);
        return htdList;
    }
}
