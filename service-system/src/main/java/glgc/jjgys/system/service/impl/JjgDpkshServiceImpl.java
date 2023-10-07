package glgc.jjgys.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.system.mapper.JjgDpkshMapper;
import glgc.jjgys.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-03-01
 */
@Service
public class JjgDpkshServiceImpl extends ServiceImpl<JjgDpkshMapper,Object> implements JjgDpkshService {

    @Autowired
    private JjgHtdService jjgHtdService;
    @Autowired
    private JjgLqsQlService jjgLqsQlService;
    @Autowired
    private JjgLqsSdService jjgLqsSdService;
    @Autowired
    private JjgFbgcJtaqssJabzService jjgFbgcJtaqssJabzService;

    @Autowired
    private JjgFbgcLjgcHdgqdService jjgFbgcLjgcHdgqdService;

    @Autowired
    private JjgFbgcLjgcHdjgccService jjgFbgcLjgcHdjgccService;

    @Autowired
    private JjgFbgcLjgcLjbpService jjgFbgcLjgcLjbpService;

    @Autowired
    private JjgFbgcLjgcLjcjService jjgFbgcLjgcLjcjService;

    @Autowired
    private JjgFbgcLjgcLjtsfysdHtService jjgFbgcLjgcLjtsfysdHtService;

    @Autowired
    private JjgFbgcLjgcLjwcService jjgFbgcLjgcLjwcService;

    @Autowired
    private JjgFbgcLjgcLjwcLcfService jjgFbgcLjgcLjwcLcfService;

    @Autowired
    private JjgFbgcLjgcPsdmccService jjgFbgcLjgcPsdmccService;

    @Autowired
    private JjgFbgcLjgcPspqhdService jjgFbgcLjgcPspqhdService;

    @Autowired
    private JjgFbgcLjgcXqgqdService jjgFbgcLjgcXqgqdService;

    @Autowired
    private JjgFbgcLjgcXqjgccService jjgFbgcLjgcXqjgccService;

    @Autowired
    private JjgFbgcLjgcZddmccService jjgFbgcLjgcZddmccService;

    @Autowired
    private JjgFbgcLjgcZdgqdService jjgFbgcLjgcZdgqdService;


    @Autowired
    private JjgFbgcLmgcGslqlmhdzxfService jjgFbgcLmgcGslqlmhdzxfService;

    @Autowired
    private JjgFbgcLmgcHntlmhdzxfService jjgFbgcLmgcHntlmhdzxfService;

    @Autowired
    private JjgFbgcLmgcHntlmqdService jjgFbgcLmgcHntlmqdService;

    @Autowired
    private JjgFbgcLmgcLmgzsdsgpsfService jjgFbgcLmgcLmgzsdsgpsfService;

    @Autowired
    private JjgFbgcLmgcLmhpService jjgFbgcLmgcLmhpService;

    @Autowired
    private JjgFbgcLmgcLmssxsService jjgFbgcLmgcLmssxsService;

    @Autowired
    private JjgFbgcLmgcLmwcService jjgFbgcLmgcLmwcService;

    @Autowired
    private JjgFbgcLmgcLmwcLcfService jjgFbgcLmgcLmwcLcfService;

    @Autowired
    private JjgFbgcLmgcLqlmysdService jjgFbgcLmgcLqlmysdService;

    @Autowired
    private JjgFbgcLmgcTlmxlbgcService jjgFbgcLmgcTlmxlbgcService;

    @Autowired
    private JjgFbgcJtaqssJabxService jjgFbgcJtaqssJabxService;

    @Autowired
    private JjgFbgcJtaqssJabxfhlService jjgFbgcJtaqssJabxfhlService;


    @Autowired
    private JjgFbgcJtaqssJathldmccService jjgFbgcJtaqssJathldmccService;

    @Autowired
    private JjgFbgcJtaqssJathlqdService jjgFbgcJtaqssJathlqdService;

    @Autowired
    private JjgFbgcQlgcXbTqdService jjgFbgcQlgcXbTqdService;

    @Autowired
    private JjgFbgcQlgcXbJgccService jjgFbgcQlgcXbJgccService;

    @Autowired
    private JjgFbgcQlgcXbBhchdService jjgFbgcQlgcXbBhchdService;

    @Autowired
    private JjgFbgcQlgcXbSzdService jjgFbgcQlgcXbSzdService;

    @Autowired
    private JjgFbgcQlgcSbTqdService jjgFbgcQlgcSbTqdService;

    @Autowired
    private JjgFbgcQlgcSbJgccService jjgFbgcQlgcSbJgccService;

    @Autowired
    private JjgFbgcQlgcSbBhchdService jjgFbgcQlgcSbBhchdService;

    @Autowired
    private JjgFbgcQlgcQmpzdService jjgFbgcQlgcQmpzdService;

    @Autowired
    private JjgFbgcQlgcQmhpService jjgFbgcQlgcQmhpService;

    @Autowired
    private JjgFbgcQlgcQmgzsdService jjgFbgcQlgcQmgzsdService;


    @Autowired
    private JjgFbgcSdgcCqtqdService jjgFbgcSdgcCqtqdService;

    @Autowired
    private JjgFbgcSdgcDmpzdService jjgFbgcSdgcDmpzdService;

    @Autowired
    private JjgFbgcSdgcZtkdService jjgFbgcSdgcZtkdService;

    @Autowired
    private JjgFbgcSdgcCqhdService jjgFbgcSdgcCqhdService;

    @Autowired
    private JjgFbgcSdgcSdlqlmysdService jjgFbgcSdgcSdlqlmysdService;

    @Autowired
    private JjgFbgcSdgcLmssxsService jjgFbgcSdgcLmssxsService;

    @Autowired
    private JjgFbgcSdgcHntlmqdService jjgFbgcSdgcHntlmqdService;

    @Autowired
    private JjgFbgcSdgcTlmxlbgcService jjgFbgcSdgcTlmxlbgcService;

    @Autowired
    private JjgFbgcSdgcSdhpService jjgFbgcSdgcSdhpService;

    @Autowired
    private JjgFbgcSdgcSdhntlmhdzxfService jjgFbgcSdgcSdhntlmhdzxfService;

    @Autowired
    private JjgFbgcSdgcGssdlqlmhdzxfService jjgFbgcSdgcGssdlqlmhdzxfService;

    @Autowired
    private JjgFbgcSdgcLmgzsdsgpsfService jjgFbgcSdgcLmgzsdsgpsfService;

    @Autowired
    private JjgLookProjectPlanService jjgLookProjectPlanService;


    @Override
    public List<JjgHtd> gethtd(String proname) {
        List<JjgHtd> gethtd = jjgHtdService.gethtd(proname);
        return gethtd;
    }

    @Override
    public Map<String, Map<String, Map<String, Object>>> getnum(String proname){
        Map<String, Map<String, Map<String, Object>>> getqlnum = getqlnum(proname);

        Map<String, Map<String, Map<String, Object>>> getsdnum = getsdnum(proname);
        Map<String, Map<String, Map<String, Object>>> getja = getja(proname);

        Map<String, Map<String, Map<String, Object>>> mergedMap = new HashMap<>();
        mergeIntoMap(mergedMap, getqlnum);
        mergeIntoMap(mergedMap, getsdnum);
        mergeIntoMap(mergedMap, getja);
        /**
         {
         建设项目={桥={中桥=0, 大桥=0, 小桥=7, 特大桥=0}, 交安={单柱=106, 双悬=1, 门架=5, 单悬=20, 附着=27, 双柱=19}, 隧道={中隧道=2, 短隧道=0, 特长隧道=0, 长隧道=2}},
         TJ-4={桥={中桥=0, 大桥=0, 小桥=1, 特大桥=0}},
         LJ-1={交安={单柱=106, 双悬=1, 门架=5, 单悬=20, 附着=27, 双柱=19}},
         TJ-3={桥={中桥=0, 大桥=0, 小桥=2, 特大桥=0}, 隧道={中隧道=2, 短隧道=0, 特长隧道=0, 长隧道=0}},
         TJ-2={桥={中桥=0, 大桥=0, 小桥=2, 特大桥=0}, 隧道={中隧道=0, 短隧道=0, 特长隧道=0, 长隧道=2}},
         TJ-1={桥={中桥=0, 大桥=0, 小桥=2, 特大桥=0}}
         }
         */
        return mergedMap;
    }

    @Override
    public Map<String, Map<String, Map<String, List<Map<String, Object>>>>> getdwgc(String proname) throws IOException {
        /**
         * {路面工程=
         * {单位工程合格率=
         * {LJ-1=[{zds=118.0, hgl=88.98, hgds=105.0}]},
         * 单位工程指标完成率={
         * LJ-1=[{fbgcname=路基工程, zs=150.0, proname=陕西高速, jcs=109, htdname=LJ-1}]}},
         * 交安工程={单位工程合格率={LJ-1=[{zds=3455.0, hgl=96.96, hgds=3350.0}]}},
         * 路基工程={单位工程合格率={LJ-2=[{zds=0.0, hgl=0, hgds=0.0}], LJ-1=[{zds=578.0, hgl=90.31, hgds=522.0}]},
         * 单位工程指标完成率={LJ-1=[{fbgcname=路基工程, zs=150.0, proname=陕西高速, jcs=109, htdname=LJ-1}]}},
         * 桥梁工程={单位工程合格率={LJ-1=[{zds=711.0, hgl=96.34, hgds=685.0}]}},
         * 隧道工程={单位工程合格率={LJ-1=[{zds=409.0, hgl=94.38, hgds=386.0}]}}}
         */

        //合格率
        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> getdwgchgl = getdwgchgl(proname);
        //完成率
        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> getdwgcwcl = getdwgcwcl(proname);

        getdwgcwcl.forEach((key, value) -> getdwgchgl.merge(key, value, (v1, v2) -> {
            // 对于具有重复的键的情况，合并内部Map中的值
            v2.forEach((innerKey, innerValue) -> v1.merge(innerKey, innerValue, (l1, l2) -> {
                // 对于具有重复的内部键的情况，合并列表中的元素
                l1.putAll(l2);
                return l1;
            }));
            return v1;
        }));
        System.out.println(getdwgchgl);
        return getdwgchgl;
    }

    @Override
    public Map<String, Map<String, Map<String, List<Map<String, Object>>>>>  gethtddata(String proname) throws IOException {
        //合同段合格率
        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> gethtdhgldata = gethtdhgldata(proname);

        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> gethtdwcldata = gethtdwcldata(proname);

        gethtdwcldata.forEach((key, value) -> gethtdhgldata.merge(key, value, (v1, v2) -> {
            // 对于具有重复的键的情况，合并内部Map中的值
            v2.forEach((innerKey, innerValue) -> v1.merge(innerKey, innerValue, (l1, l2) -> {
                // 对于具有重复的内部键的情况，合并列表中的元素
                l1.putAll(l2);
                return l1;
            }));
            return v1;
        }));
        /**
         * {LJ-2={
         * 合同段合格率={
         * 路面工程=[{zds=118.0, htd=LJ-1, lx=路面工程, hgds=105.0}],
         * 隧道工程=[{zds=409.0, htd=LJ-1, lx=隧道工程, hgds=386.0}],
         * 交安工程=[{zds=3455.0, htd=LJ-1, lx=交安工程, hgds=3350.0}],
         * 桥梁工程=[{zds=711.0, htd=LJ-1, lx=桥梁工程, hgds=685.0}],
         * 路基工程=[{zds=578.0, htd=LJ-1, lx=路基工程, hgds=522.0}]},
         * 合同段指标完成率={
         * 路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}},
         * LJ-1={
         * 合同段合格率={路面工程=[{zds=118.0, htd=LJ-1, lx=路面工程, hgds=105.0}], 隧道工程=[{zds=409.0, htd=LJ-1, lx=隧道工程, hgds=386.0}], 交安工程=[{zds=3455.0, htd=LJ-1, lx=交安工程, hgds=3350.0}], 桥梁工程=[{zds=711.0, htd=LJ-1, lx=桥梁工程, hgds=685.0}], 路基工程=[{zds=578.0, htd=LJ-1, lx=路基工程, hgds=522.0}]}, 合同段指标完成率={路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}}, LM-1={合同段指标完成率={路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}}}
         */
        return gethtdhgldata;


    }

    @Override
    public Map<String, Map<String, List<Map<String, Object>>>> getjsxmdata(String proname) throws IOException {
        Map<String, Map<String, List<Map<String, Object>>>> result = new HashMap<>();
        Map<String, Map<String, List<Map<String, Object>>>> rehgl = new HashMap<>();
        Map<String, List<Map<String, Object>>> getjsxmzhdata = getjsxmzhdata(proname);
        rehgl.put("建设项目合格率",getjsxmzhdata);

        List<Map<String, Object>> getjsxmzhwcldata = getjsxmzhwcldata(proname);

        Map<String, List<Map<String, Object>>> groupByFbgc = getjsxmzhwcldata.stream()
                .collect(Collectors.groupingBy(m -> m.get("fbgc").toString()));

        Map<String, Map<String, List<Map<String, Object>>>> rewcl = new HashMap<>();
        rewcl.put("建设项目实测指标完成率",groupByFbgc);


        result.putAll(rehgl);
        result.putAll(rewcl);
        System.out.println(result);
        /**
         * {建设项目实测指标完成率={
         * 路面工程=[{wcl=0, jhs=0, wcs=0, fbgc=路面工程, zblx=△}, {wcl=0, jhs=0, wcs=0, fbgc=路面工程, zblx=*}, {wcl=1175.00, jhs=20, wcs=235, fbgc=路面工程, zblx=其余指标}, {wcl=1175.00, jhs=20, wcs=235, fbgc=路面工程, zblx=合计指标}],
         * 隧道工程=[{wcl=0, jhs=0, wcs=0, fbgc=隧道工程, zblx=△}, {wcl=0, jhs=0, wcs=0, fbgc=隧道工程, zblx=*}, {wcl=0, jhs=0, wcs=0, fbgc=隧道工程, zblx=其余指标}, {wcl=0, jhs=0, wcs=0, fbgc=隧道工程, zblx=合计指标}],
         * 交安工程=[{wcl=0, jhs=0, wcs=0, fbgc=交安工程, zblx=△}, {wcl=0, jhs=0, wcs=0, fbgc=交安工程, zblx=*}, {wcl=0, jhs=0, wcs=0, fbgc=交安工程, zblx=其余指标}, {wcl=0, jhs=0, wcs=0, fbgc=交安工程, zblx=合计指标}],
         * 桥梁工程=[{wcl=0, jhs=0, wcs=0, fbgc=桥梁工程, zblx=*}, {wcl=0, jhs=0, wcs=0, fbgc=桥梁工程, zblx=其余指标}, {wcl=0, jhs=0, wcs=0, fbgc=桥梁工程, zblx=合计指标}],
         * 路基工程=[{wcl=0, jhs=0, wcs=0, fbgc=路基工程, zblx=△}, {wcl=52.50, jhs=40, wcs=21, fbgc=路基工程, zblx=*}, {wcl=67.50, jhs=40, wcs=27, fbgc=路基工程, zblx=其余指标}, {wcl=32.00, jhs=150, wcs=48, fbgc=路基工程, zblx=合计指标}]},
         *
         * 建设项目合格率={
         * 路面工程=[{fbgc=路面工程, zds=8.0, zblx=△, hgl=100.00, hgds=8.0}, {fbgc=路面工程, zds=4.0, zblx=*, hgl=100.00, hgds=4.0}, {fbgc=路面工程, zds=102.0, zblx=其余指标, hgl=87.25, hgds=89.0}, {fbgc=路面工程, zds=114.0, zblx=合计指标, hgl=88.60, hgds=101.0}],
         * 隧道工程=[{fbgc=隧道工程, zds=225.0, zblx=△, hgl=94.22, hgds=212.0}, {fbgc=隧道工程, zds=40.0, zblx=*, hgl=100.00, hgds=40.0}, {fbgc=隧道工程, zds=144.0, zblx=其余指标, hgl=93.06, hgds=134.0}, {fbgc=隧道工程, zds=409.0, zblx=合计指标, hgl=94.38, hgds=386.0}],
         * 交安工程=[{fbgc=交安工程, zds=1866.0, zblx=△, hgl=97.48, hgds=1819.0}, {fbgc=交安工程, zds=18.0, zblx=*, hgl=50.00, hgds=9.0}, {fbgc=交安工程, zds=1571.0, zblx=其余指标, hgl=96.88, hgds=1522.0}, {fbgc=交安工程, zds=3455.0, zblx=合计指标, hgl=96.96, hgds=3350.0}],
         * 路基工程=[{fbgc=路基工程, zds=119.0, zblx=△, hgl=83.19, hgds=99.0}, {fbgc=路基工程, zds=269.0, zblx=*, hgl=100.00, hgds=269.0}, {fbgc=路基工程, zds=190.0, zblx=其余指标, hgl=81.05, hgds=154.0}, {fbgc=路基工程, zds=578.0, zblx=合计指标, hgl=90.31, hgds=522.0}],
         * 桥梁工程=[{fbgc=桥梁工程, zds=418.0, zblx=*, hgl=98.80, hgds=413.0}, {fbgc=桥梁工程, zds=293.0, zblx=其余指标, hgl=92.83, hgds=272.0}, {fbgc=桥梁工程, zds=711.0, zblx=合计指标, hgl=96.34, hgds=685.0}]}}
         */


        return result;

    }

    private List<Map<String,Object>> getjsxmzhwcldata(String proname) {
        DecimalFormat df = new DecimalFormat("0.00");
        QueryWrapper<JjgPlaninfo> wrapper=new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgPlaninfo> list = jjgLookProjectPlanService.list(wrapper);
        Map<String, List<JjgPlaninfo>> groupedData = list.stream()
                .collect(Collectors.groupingBy(JjgPlaninfo::getFbgc));
        List<Integer> lj1 = new ArrayList<>();
        List<Integer> lj2 = new ArrayList<>();
        List<Integer> lj3 = new ArrayList<>();
        List<Integer> lm1 = new ArrayList<>();
        List<Integer> lm2 = new ArrayList<>();
        List<Integer> lm3 = new ArrayList<>();
        List<Integer> ja1 = new ArrayList<>();
        List<Integer> ja2 = new ArrayList<>();
        List<Integer> ja3 = new ArrayList<>();
        List<Integer> ql1 = new ArrayList<>();
        List<Integer> ql2 = new ArrayList<>();
        List<Integer> sd1 = new ArrayList<>();
        List<Integer> sd2 = new ArrayList<>();
        List<Integer> sd3 = new ArrayList<>();

        groupedData.forEach((group, grouphtdData) -> {
            //group是分部工程 grouphtdData是List<JjgPlaninfo>
            if (group.equals("路基工程")){
                for (JjgPlaninfo grouphtdDatum : grouphtdData) {
                    String zb = grouphtdDatum.getZb();
                    //△
                    if (zb.equals("路基土石方压实度灰土") || zb.equals("路基土石方压实度沙砾") || zb.equals("路基弯沉贝克曼梁法") || zb.equals("路基弯沉落锤法") || zb.equals("支挡断面尺寸")){
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        lj1.add(dnum);

                        //*
                    }else if (zb.equals("涵洞砼强度") || zb.equals("小桥砼强度") || zb.equals("支挡砼强度") ){
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        lj2.add(dnum);

                    }else {
                        //其余指标
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        lj3.add(dnum);
                    }
                }
            }
            if (group.equals("路面工程")){
                for (JjgPlaninfo grouphtdDatum : grouphtdData) {
                    String zb = grouphtdDatum.getZb();
                    //△
                    if (zb.equals("沥青路面压实度") || zb.equals("路面弯沉贝克曼梁法") || zb.equals("路面弯沉落锤法") || zb.equals("高速沥青路面厚度钻芯法") || zb.equals("混凝土路面厚度钻芯法")){
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        lm1.add(dnum);

                    }else if (zb.equals("混凝土路面强度")){
                        //*
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        lm2.add(dnum);

                    }else {
                        //其余指标
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        lm3.add(dnum);
                    }
                }
            }
            if (group.equals("交安工程")){
                for (JjgPlaninfo grouphtdDatum : grouphtdData) {
                    String zb = grouphtdDatum.getZb();
                    //△
                    if (zb.equals("交安标线") || zb.equals("交安砼护栏断面尺寸") ){
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        ja1.add(dnum);


                    }else if (zb.equals("交安波形防护栏") || zb.equals("交安砼护栏强度")){
                        //*
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        ja2.add(dnum);

                    }else {
                        //其余指标
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        ja3.add(dnum);

                    }
                }
            }
            if (group.equals("桥梁工程")){
                for (JjgPlaninfo grouphtdDatum : grouphtdData) {
                    String zb = grouphtdDatum.getZb();
                   /* //△
                    if (zb.equals("交安标线") || zb.equals("交安砼护栏断面尺寸") ){


                    }else*/
                    if (zb.equals("桥梁下部墩台砼强度") || zb.equals("桥梁下部墩台垂直度") || zb.equals("桥梁上部砼强度")){
                        //*
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        ql1.add(dnum);

                    }else {
                        //其余指标
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        ql2.add(dnum);
                    }
                }
            }
            if (group.equals("隧道工程")){
                for (JjgPlaninfo grouphtdDatum : grouphtdData) {
                    String zb = grouphtdDatum.getZb();
                    //△
                    if (zb.equals("隧道衬砌厚度") || zb.equals("隧道厚度") ){
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        sd1.add(dnum);

                    }else if (zb.equals("隧道衬砌强度")){
                        //*
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        sd2.add(dnum);

                    }else {
                        //其余指标
                        String num = grouphtdDatum.getNum();
                        int dnum = Integer.valueOf(num);
                        sd3.add(dnum);
                    }
                }
            }
        });
        //BigDecimal a = new BigDecimal();
        int ljall = 0;
        List<Map<String,Object>> re = new ArrayList<>();
        if (lj1 !=null && !lj1.isEmpty()){
            int num1 = jjgFbgcLjgcLjtsfysdHtService.selectnumname(proname);
            int num2 = jjgFbgcLjgcLjcjService.selectnumname(proname);
            int num3 = jjgFbgcLjgcLjwcService.selectnumname(proname);
            int num5 = jjgFbgcLjgcLjwcLcfService.selectnumname(proname);
            int num4 = jjgFbgcLjgcZddmccService.selectnumname(proname);
            ljall+=(num1+num2+num3+num4+num5);
            int i = sumList(lj1);
            Map map = new HashMap<>();
            map.put("fbgc","路基工程");
            map.put("zblx","△");
            map.put("jhs",i);
            map.put("wcs",num1+num2+num3+num4+num5);
            map.put("wcl",i!=0 ? df.format((double) (num1+num2+num3+num4+num5)/i*100) : 0);
            re.add(map);
        }else {
            Map map = new HashMap<>();
            map.put("fbgc","路基工程");
            map.put("zblx","△");
            map.put("jhs",0);
            map.put("wcs",0);
            map.put("wcl",0);
            re.add(map);
        }
        if (lj2 !=null && !lj2.isEmpty()){
            int num1 = jjgFbgcLjgcHdgqdService.selectnumname(proname);
            int num2 = jjgFbgcLjgcXqgqdService.selectnumname(proname);
            int num3 = jjgFbgcLjgcZdgqdService.selectnumname(proname);
            int zs = num1+num2+num3;
            ljall+=(num1+num2+num3);
            int i = sumList(lj2);
            Map map = new HashMap<>();
            map.put("fbgc","路基工程");
            map.put("zblx","*");
            map.put("jhs",i);
            map.put("wcs",num1+num2+num3);
            map.put("wcl",i != 0 ? df.format((double) zs/i*100) : 0);

            re.add(map);
        }else {
            Map map = new HashMap<>();
            map.put("fbgc","路基工程");
            map.put("zblx","*");
            map.put("jhs",0);
            map.put("wcs",0);
            map.put("wcl",0);
            re.add(map);
        }
        if (lj3 !=null && !lj3.isEmpty()){
            int num2 = jjgFbgcLjgcHdjgccService.selectnumname(proname);
            int num7 = jjgFbgcLjgcLjbpService.selectnumname(proname);
            int num8 = jjgFbgcLjgcPsdmccService.selectnumname(proname);
            int num9 = jjgFbgcLjgcPspqhdService.selectnumname(proname);
            int num11 = jjgFbgcLjgcXqjgccService.selectnumname(proname);
            ljall+=(num2+num7+num8+num9+num11);
            int i = sumList(lj2);
            Map map = new HashMap<>();
            map.put("fbgc","路基工程");
            map.put("zblx","其余指标");
            map.put("jhs",i);
            map.put("wcs",num2+num7+num8+num9+num11);
            map.put("wcl",i!=0 ? df.format((double)(num2+num7+num8+num9+num11)/i*100) : 0);

            re.add(map);
        }else {
            Map map = new HashMap<>();
            map.put("fbgc","路基工程");
            map.put("zblx","其余指标");
            map.put("jhs",0);
            map.put("wcs",0);
            map.put("wcl", 0);
            re.add(map);
        }
        Map map = new HashMap<>();
        map.put("fbgc","路基工程");
        map.put("zblx","合计指标");
        map.put("jhs",sumList(lj1)+sumList(lj2)+sumList(lj3));
        map.put("wcs",ljall);
        map.put("wcl",(sumList(lj1)+sumList(lj2)+sumList(lj3))!=0 ? df.format((double) ljall/(sumList(lj1)+sumList(lj2)+sumList(lj3))*100) : 0);

        re.add(map);

        int lmall = 0;
        if (lm1 !=null && !lm1.isEmpty()){
            int num1 = jjgFbgcLmgcLqlmysdService.selectnumname(proname);
            int num2 = jjgFbgcLmgcLmwcService.selectnumname(proname);
            int num3 = jjgFbgcLmgcLmwcLcfService.selectnumname(proname);
            int num4 = jjgFbgcLmgcGslqlmhdzxfService.selectnumname(proname);
            int num5 = jjgFbgcLmgcHntlmhdzxfService.selectnumname(proname);
            lmall+=(num1+num2+num3+num4+num5);
            int i = sumList(lm1);
            Map map1 = new HashMap<>();
            map1.put("fbgc","路面工程");
            map1.put("zblx","△");
            map1.put("jhs",i);
            map1.put("wcs",num1+num2+num3+num4+num5);
            map1.put("wcl",i!=0 ? df.format((double)(num1+num2+num3+num4+num5)/i*100) : 0);

            re.add(map1);
        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","路面工程");
            map1.put("zblx","△");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        if (lm2 !=null && !lm2.isEmpty()){
            int num3 = jjgFbgcLmgcHntlmqdService.selectnumname(proname);
            lmall+=num3;
            int i = sumList(lm2);
            Map map1 = new HashMap<>();
            map1.put("fbgc","路面工程");
            map1.put("zblx","*");
            map1.put("jhs",i);
            map1.put("wcs",num3);
            map1.put("wcl",i!=0 ? df.format((double)num3/i*100) : 0);

            re.add(map1);

        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","路面工程");
            map1.put("zblx","*");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        if (lm3 !=null && !lm3.isEmpty()){
            int num4 = jjgFbgcLmgcLmgzsdsgpsfService.selectnumname(proname);
            int num5 = jjgFbgcLmgcLmhpService.selectnumname(proname);
            int num6 = jjgFbgcLmgcLmssxsService.selectnumname(proname);
            int num10 = jjgFbgcLmgcTlmxlbgcService.selectnumname(proname);
            lmall+=(num4+num5+num6+num10);
            int zs = num4+num5+num6+num10;
            int i = sumList(lm3);
            Map map1 = new HashMap<>();
            map1.put("fbgc","路面工程");
            map1.put("zblx","其余指标");
            map1.put("jhs",i);
            map1.put("wcs",num4+num5+num6+num10);
            map1.put("wcl",i!=0 ? df.format((double)zs/i*100) : 0);
            System.out.println(map1);
            re.add(map1);
        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","路面工程");
            map1.put("zblx","其余指标");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        Map mapm = new HashMap<>();
        mapm.put("fbgc","路面工程");
        mapm.put("zblx","合计指标");
        mapm.put("jhs",sumList(lm1)+sumList(lm2)+sumList(lm3));
        mapm.put("wcs",lmall);
        mapm.put("wcl",(sumList(lm1)+sumList(lm2)+sumList(lm3))!=0 ? df.format((double)lmall/(sumList(lm1)+sumList(lm2)+sumList(lm3))*100) : 0);

        re.add(mapm);

        int jaall = 0;
        if (ja1 !=null && !ja1.isEmpty()){
            int num1 = jjgFbgcJtaqssJabxService.selectnumname(proname);
            int num5 = jjgFbgcJtaqssJathldmccService.selectnumname(proname);
            jaall+=(num1+num5);
            int i = sumList(ja1);
            Map map1 = new HashMap<>();
            map1.put("fbgc","交安工程");
            map1.put("zblx","△");
            map1.put("jhs",i);
            map1.put("wcs",num1+num5);
            map1.put("wcl",i!=0 ? df.format((double)(num5+num1)/i*100) : 0);

            re.add(map1);
        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","交安工程");
            map1.put("zblx","△");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        if (ja2 !=null && !ja2.isEmpty()){
            int num3 = jjgFbgcJtaqssJabxfhlService.selectnumname(proname);
            int num4 = jjgFbgcJtaqssJathlqdService.selectnumname(proname);
            jaall+=(num3+num4);
            int i = sumList(ja2);
            Map map1 = new HashMap<>();
            map1.put("fbgc","交安工程");
            map1.put("zblx","*");
            map1.put("jhs",i);
            map1.put("wcs",num3+num4);
            map1.put("wcl",i!=0 ? df.format((double)(num4+num3)/i*100) : 0);

            re.add(map1);
        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","交安工程");
            map1.put("zblx","*");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        if (ja3 !=null && !ja3.isEmpty()){
            int num2 = jjgFbgcJtaqssJabzService.selectnumname(proname);
            jaall+=num2;
            int i = sumList(ja3);
            Map map1 = new HashMap<>();
            map1.put("fbgc","交安工程");
            map1.put("zblx","其余指标");
            map1.put("jhs",i);
            map1.put("wcs",num2);
            map1.put("wcl",i!=0 ? df.format((double)num2/i*100) : 0);

            re.add(map1);
        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","交安工程");
            map1.put("zblx","其余指标");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        Map mapj = new HashMap<>();
        mapj.put("fbgc","交安工程");
        mapj.put("zblx","合计指标");
        mapj.put("jhs",sumList(ja1)+sumList(ja2)+sumList(ja3));
        mapj.put("wcs",jaall);
        mapj.put("wcl",(sumList(ja1)+sumList(ja2)+sumList(ja3))!=0 ? df.format((double)jaall/(sumList(ja1)+sumList(ja2)+sumList(ja3))*100) : 0);

        re.add(mapj);

        int qlall = 0;
        if (ql1 !=null && !ql1.isEmpty()){
            int num1 = jjgFbgcQlgcXbTqdService.selectnumname(proname);
            int num4 = jjgFbgcQlgcXbSzdService.selectnumname(proname);
            int num5 = jjgFbgcQlgcSbTqdService.selectnumname(proname);
            qlall+=(num1+num4+num5);
            int i = sumList(ql1);
            Map map1 = new HashMap<>();
            map1.put("fbgc","桥梁工程");
            map1.put("zblx","*");
            map1.put("jhs",i);
            map1.put("wcs",num1+num4+num5);
            map1.put("wcl",i!=0 ? df.format((double)(num1+num4+num5)/i*100) : 0);

            re.add(map1);

        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","桥梁工程");
            map1.put("zblx","*");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        if (ql2 !=null && !ql2.isEmpty()){
            int num2 = jjgFbgcQlgcXbJgccService.selectnumname(proname);
            int num3 = jjgFbgcQlgcXbBhchdService.selectnumname(proname);
            int num6 = jjgFbgcQlgcSbJgccService.selectnumname(proname);
            int num7 = jjgFbgcQlgcSbBhchdService.selectnumname(proname);
            int num8 = jjgFbgcQlgcQmpzdService.selectnumname(proname);
            int num9 = jjgFbgcQlgcQmhpService.selectnumname(proname);
            int num10 = jjgFbgcQlgcQmgzsdService.selectnumname(proname);
            qlall+=(num2+num3+num6+num7+num8+num9+num10);
            int i = sumList(ql2);
            Map map1 = new HashMap<>();
            map1.put("fbgc","桥梁工程");
            map1.put("zblx","其余指标");
            map1.put("jhs",i);
            map1.put("wcs",num2+num3+num6+num7+num8+num9+num10);
            map1.put("wcl",i!=0 ? df.format((double)(num2+num3+num6+num7+num8+num9+num10)/i*100) : 0);

            re.add(map1);
        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","桥梁工程");
            map1.put("zblx","其余指标");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl", 0);
            re.add(map1);

        }
        Map mapq = new HashMap<>();
        mapq.put("fbgc","桥梁工程");
        mapq.put("zblx","合计指标");
        mapq.put("jhs",sumList(ql1)+sumList(ql2));
        mapq.put("wcs",qlall);
        mapq.put("wcl",(sumList(ql1)+sumList(ql2))!=0 ? df.format((double)qlall/(sumList(ql1)+sumList(ql2))*100) : 0);

        re.add(mapq);

        int sdall = 0;
        if (sd1 !=null && !sd1.isEmpty()){
            int num3 = jjgFbgcSdgcCqhdService.selectnumname(proname);
            int num9 = jjgFbgcSdgcSdhntlmhdzxfService.selectnumname(proname);
            int num10 = jjgFbgcSdgcGssdlqlmhdzxfService.selectnumname(proname);
            sdall+=(num3+num9+num10);
            int i = sumList(sd1);
            Map map1 = new HashMap<>();
            map1.put("fbgc","隧道工程");
            map1.put("zblx","△");
            map1.put("jhs",i);
            map1.put("wcs",num3+num9+num10);
            map1.put("wcl",i!=0 ? df.format((double)(num3+num9+num10)/i*100) : 0);

            re.add(map1);

        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","隧道工程");
            map1.put("zblx","△");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        if (sd2 !=null && !sd2.isEmpty()){
            int num12 = jjgFbgcSdgcCqtqdService.selectnumname(proname);
            sdall+=num12;
            int i = sumList(sd2);
            Map map1 = new HashMap<>();
            map1.put("fbgc","隧道工程");
            map1.put("zblx","*");
            map1.put("jhs",i);
            map1.put("wcs",num12);
            map1.put("wcl",i!=0 ? df.format((double)num12/i*100) : 0);

            re.add(map1);

        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","隧道工程");
            map1.put("zblx","*");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        if (sd3 !=null && !sd3.isEmpty()){
            int num1 = jjgFbgcSdgcDmpzdService.selectnumname(proname);
            int num2 = jjgFbgcSdgcZtkdService.selectnumname(proname);
            int num4 = jjgFbgcSdgcSdlqlmysdService.selectnumname(proname);
            int num5 = jjgFbgcSdgcLmssxsService.selectnumname(proname);
            int num6 = jjgFbgcSdgcHntlmqdService.selectnumname(proname);
            int num7 = jjgFbgcSdgcTlmxlbgcService.selectnumname(proname);
            int num8 = jjgFbgcSdgcSdhpService.selectnumname(proname);
            int num11 = jjgFbgcSdgcLmgzsdsgpsfService.selectnumname(proname);
            sdall+=(num1+num2+num4+num5+num6+num7+num8+num11);
            int i = sumList(sd3);
            Map map1 = new HashMap<>();
            map1.put("fbgc","隧道工程");
            map1.put("zblx","其余指标");
            map1.put("jhs",i);
            map1.put("wcs",num1+num2+num4+num5+num6+num7+num8+num11);
            map1.put("wcl",i!=0 ? df.format((double)(num1+num2+num4+num5+num6+num7+num8+num11)/i*100) : 0);

            re.add(map1);
        }else {
            Map map1 = new HashMap<>();
            map1.put("fbgc","隧道工程");
            map1.put("zblx","其余指标");
            map1.put("jhs",0);
            map1.put("wcs",0);
            map1.put("wcl",0);
            re.add(map1);
        }
        Map maps = new HashMap<>();
        maps.put("fbgc","隧道工程");
        maps.put("zblx","合计指标");
        maps.put("jhs",sumList(sd1)+sumList(sd2)+sumList(sd3));
        maps.put("wcs",sdall);
        maps.put("wcl",(sumList(sd1)+sumList(sd2)+sumList(sd3))!=0 ? df.format((double)sdall/(sumList(sd1)+sumList(sd2)+sumList(sd3))*100) : 0);

        re.add(maps);

        return re;

    }

    public int sumList(List<Integer> list) {
        int sum = 0;
        for (int num : list) {
            sum += num;
        }
        return sum;
    }


    private Map<String,List<Map<String, Object>>> getjsxmzhdata(String proname) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        //建设项目合格率
        List<JjgHtd> gethtd = jjgHtdService.gethtd(proname);

        List<String> ljlist = new ArrayList<>();
        List<String> lmlist = new ArrayList<>();
        List<String> sdlist = new ArrayList<>();
        List<String> qllist = new ArrayList<>();
        List<String> jalist = new ArrayList<>();
        for (JjgHtd jjgHtd : gethtd) {
            String htd = jjgHtd.getName();
            String lx = jjgHtd.getLx();
            if (lx.contains("路基工程")){
                ljlist.add(htd);
            }
            if (lx.contains("路面工程")){
                lmlist.add(htd);
            }
            if (lx.contains("桥梁工程")){
                qllist.add(htd);
            }
            if (lx.contains("隧道工程")){
                sdlist.add(htd);
            }
            if (lx.contains("交安工程")){
                jalist.add(htd);
            }
        }

        List<Map<String, Object>> getlj = getlj(proname, df, ljlist);
        List<Map<String, Object>> getlm = getlm(proname, df, lmlist);
        List<Map<String, Object>> getql = getql(proname, df, qllist);
        List<Map<String, Object>> getsd = getsd(proname, df, sdlist);
        List<Map<String, Object>> getja = getjazb(proname, df, jalist);

        Map<String,List<Map<String, Object>>> result = new HashMap<>();
        result.put("路基工程",getlj);
        result.put("路面工程",getlm);
        result.put("桥梁工程",getql);
        result.put("隧道工程",getsd);
        result.put("交安工程",getja);
        return result;

    }

    private List<Map<String, Object>> getjazb(String proname, DecimalFormat df, List<String> jalist) throws IOException {
        double ja1zds = 0,ja1hgds = 0,ja2zds = 0,ja2hgds = 0,ja3zds = 0,ja3hgds = 0;
        for (String s : jalist) {
            CommonInfoVo commonInfoVo = new CommonInfoVo();
            commonInfoVo.setProname(proname);
            commonInfoVo.setHtd(s);
            //============================△=========================================
            List<Map<String, Object>> list1 = jjgFbgcJtaqssJabxService.lookJdbjg(commonInfoVo);
            if (list1 !=null && !list1.isEmpty()){
                for (Map<String, Object> map : list1) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ja1zds+=Double.valueOf(z);
                    ja1hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list4 = jjgFbgcJtaqssJathldmccService.lookJdbjg(commonInfoVo);
            if (list4 !=null && !list4.isEmpty()){
                for (Map<String, Object> map : list4) {
                    String z = map.get("检测总点数").toString();
                    String h = map.get("合格点数").toString();
                    ja1zds+=Double.valueOf(z);
                    ja1hgds+=Double.valueOf(h);
                }
            }
            //============================*=========================================
            List<Map<String, Object>> list2 = jjgFbgcJtaqssJabxfhlService.lookJdbjg(commonInfoVo);
            if (list2 !=null && !list2.isEmpty()){
                for (Map<String, Object> map : list2) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ja2zds+=Double.valueOf(z);
                    ja2hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list5 = jjgFbgcJtaqssJathlqdService.lookJdbjg(commonInfoVo);
            if (list5 !=null && !list5.isEmpty()){
                for (Map<String, Object> map : list5) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ja2zds+=Double.valueOf(z);
                    ja2hgds+=Double.valueOf(h);
                }
            }
            //============================其余指标=========================================
            List<Map<String, Object>> list3 = jjgFbgcJtaqssJabzService.lookJdbjg(commonInfoVo);
            if (list3 !=null && !list3.isEmpty()){
                for (Map<String, Object> map : list3) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ja3zds+=Double.valueOf(z);
                    ja3hgds+=Double.valueOf(h);
                }
            }
        }
        List<Map<String,Object>> ljmaplist = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("zblx","△");
        map1.put("fbgc","交安工程");
        map1.put("zds",ja1zds);
        map1.put("hgds",ja1hgds);
        map1.put("hgl",ja1zds!=0 ? df.format(ja1hgds/ja1zds*100) : 0);

        Map<String,Object> map2 = new HashMap<>();
        map2.put("zblx","*");
        map2.put("fbgc","交安工程");
        map2.put("zds",ja2zds);
        map2.put("hgds",ja2hgds);
        map2.put("hgl",ja2zds!=0 ? df.format(ja2hgds/ja2zds*100) : 0);

        Map<String,Object> map3 = new HashMap<>();
        map3.put("zblx","其余指标");
        map3.put("fbgc","交安工程");
        map3.put("zds",ja3zds);
        map3.put("hgds",ja3hgds);
        map3.put("hgl",ja3zds!=0 ? df.format(ja3hgds/ja3zds*100) : 0);

        Map<String,Object> map4 = new HashMap<>();
        map4.put("zblx","合计指标");
        map4.put("fbgc","交安工程");
        map4.put("zds",ja1zds+ja2zds+ja3zds);
        map4.put("hgds",ja1hgds+ja2hgds+ja3hgds);
        map4.put("hgl",(ja1zds+ja2zds+ja3zds)!=0 ? df.format((ja1hgds+ja2hgds+ja3hgds)/(ja1zds+ja2zds+ja3zds)*100) : 0);

        ljmaplist.add(map1);
        ljmaplist.add(map2);
        ljmaplist.add(map3);
        ljmaplist.add(map4);

        return ljmaplist;

    }

    private List<Map<String, Object>> getsd(String proname, DecimalFormat df, List<String> sdlist) throws IOException {
        double sd1zds = 0,sd1hgds = 0,sd2zds = 0,sd2hgds = 0,sd3zds = 0,sd3hgds = 0;
        for (String s : sdlist) {
            CommonInfoVo commonInfoVo = new CommonInfoVo();
            commonInfoVo.setProname(proname);
            commonInfoVo.setHtd(s);
            //============================△=========================================
            List<Map<String, Object>> list4 = jjgFbgcSdgcCqhdService.lookJdbjg(commonInfoVo);
            if (list4 !=null && !list4.isEmpty()){
                for (Map<String, Object> map : list4) {
                    String z = map.get("检测总点数").toString();
                    String h = map.get("合格点数").toString();
                    sd1zds+=Double.valueOf(z);
                    sd1hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list10 = jjgFbgcSdgcSdhntlmhdzxfService.lookJdbjg(commonInfoVo);
            if (list10 !=null && !list10.isEmpty()){
                for (Map<String, Object> map : list10) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    sd1zds+=Double.valueOf(z);
                    sd1hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list11 = jjgFbgcSdgcGssdlqlmhdzxfService.lookJdbjg(commonInfoVo);
            if (list11 !=null && !list11.isEmpty()) {
                for (Map<String, Object> map : list11) {
                    String z = map.get("总厚度检测点数").toString();
                    String h = map.get("总厚度合格点数").toString();
                    sd1zds += Double.valueOf(z);
                    sd1hgds += Double.valueOf(h);
                }
            }
            //净空

            //============================*=========================================
            List<Map<String, Object>> list1 = jjgFbgcSdgcCqtqdService.lookJdbjg(commonInfoVo);
            if (list1 !=null && !list1.isEmpty()){
                for (Map<String, Object> map : list1) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    sd2zds+=Double.valueOf(z);
                    sd2hgds+=Double.valueOf(h);
                }
            }
            //============================其余指标=========================================
            List<Map<String, Object>> list2 = jjgFbgcSdgcDmpzdService.lookJdbjg(commonInfoVo);
            if (list2 !=null && !list2.isEmpty()){
                for (Map<String, Object> map : list2) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    sd3zds+=Double.valueOf(z);
                    sd3hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list3 = jjgFbgcSdgcZtkdService.lookJdbjg(commonInfoVo);
            if (list3 !=null && !list3.isEmpty()){
                for (Map<String, Object> map : list3) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    sd3zds+=Double.valueOf(z);
                    sd3hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list5 = jjgFbgcSdgcSdlqlmysdService.lookJdbjg(commonInfoVo);
            if (list5 !=null && !list5.isEmpty()){
                for (Map<String, Object> map : list5) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    sd3zds+=Double.valueOf(z);
                    sd3hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list6 = jjgFbgcSdgcLmssxsService.lookJdbjg(commonInfoVo);
            if (list6 !=null && !list6.isEmpty()){
                for (Map<String, Object> map : list6) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    sd3zds+=Double.valueOf(z);
                    sd3hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list7 = jjgFbgcSdgcHntlmqdService.lookJdbjg(commonInfoVo);
            if (list7 !=null && !list7.isEmpty()){
                for (Map<String, Object> map : list7) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    sd3zds+=Double.valueOf(z);
                    sd3hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list8 = jjgFbgcSdgcTlmxlbgcService.lookJdbjg(commonInfoVo);
            if (list8 !=null && !list8.isEmpty()){
                for (Map<String, Object> map : list8) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    sd3zds+=Double.valueOf(z);
                    sd3hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list9 = jjgFbgcSdgcSdhpService.lookJdbjg(commonInfoVo);
            if (list9 !=null && !list9.isEmpty()){
                for (Map<String, Object> map : list9) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    sd3zds+=Double.valueOf(z);
                    sd3hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list12 = jjgFbgcSdgcLmgzsdsgpsfService.lookJdbjg(commonInfoVo);
            if (list12 !=null && !list12.isEmpty()){
                for (Map<String, Object> map : list12) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    sd3zds+=Double.valueOf(z);
                    sd3hgds+=Double.valueOf(h);
                }
            }
        }
        List<Map<String,Object>> ljmaplist = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("zblx","△");
        map1.put("fbgc","隧道工程");
        map1.put("zds",sd1zds);
        map1.put("hgds",sd1hgds);
        map1.put("hgl",sd1zds!=0 ? df.format(sd1hgds/sd1zds*100) : 0);

        Map<String,Object> map2 = new HashMap<>();
        map2.put("zblx","*");
        map2.put("fbgc","隧道工程");
        map2.put("zds",sd2zds);
        map2.put("hgds",sd2hgds);
        map2.put("hgl",sd2zds!=0 ? df.format(sd2hgds/sd2zds*100) : 0);

        Map<String,Object> map3 = new HashMap<>();
        map3.put("zblx","其余指标");
        map3.put("fbgc","隧道工程");
        map3.put("zds",sd3zds);
        map3.put("hgds",sd3hgds);
        map3.put("hgl",sd3zds!=0 ? df.format(sd3hgds/sd3zds*100) : 0);

        Map<String,Object> map4 = new HashMap<>();
        map4.put("zblx","合计指标");
        map4.put("fbgc","隧道工程");
        map4.put("zds",sd1zds+sd2zds+sd3zds);
        map4.put("hgds",sd1hgds+sd2hgds+sd3hgds);
        map4.put("hgl",(sd1zds+sd2zds+sd3zds)!=0 ? df.format((sd1hgds+sd2hgds+sd3hgds)/(sd1zds+sd2zds+sd3zds)*100) : 0);

        ljmaplist.add(map1);
        ljmaplist.add(map2);
        ljmaplist.add(map3);
        ljmaplist.add(map4);

        return ljmaplist;


    }

    private List<Map<String, Object>> getql(String proname, DecimalFormat df, List<String> qllist) throws IOException {
        double ql1zds = 0,ql1hgds = 0,ql2zds = 0,ql2hgds = 0,ql3zds = 0,ql3hgds = 0;
        for (String s : qllist) {
            CommonInfoVo commonInfoVo = new CommonInfoVo();
            commonInfoVo.setProname(proname);
            commonInfoVo.setHtd(s);
            //============================*=========================================
            List<Map<String, Object>> list1 = jjgFbgcQlgcXbTqdService.lookJdbjg(commonInfoVo);
            if (list1 !=null && !list1.isEmpty()){
                for (Map<String, Object> map : list1) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ql1zds+=Double.valueOf(z);
                    ql1hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list6 = jjgFbgcQlgcSbTqdService.lookJdbjg(commonInfoVo);
            if (list6 !=null && !list6.isEmpty()){
                for (Map<String, Object> map : list6) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ql1zds+=Double.valueOf(z);
                    ql1hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list5 = jjgFbgcQlgcXbSzdService.lookJdbjg(commonInfoVo);
            if (list5 !=null && !list5.isEmpty()){
                for (Map<String, Object> map : list5) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ql1zds+=Double.valueOf(z);
                    ql1hgds+=Double.valueOf(h);
                }
            }
            //============================其余指标=========================================
            List<Map<String, Object>> list2 = jjgFbgcQlgcXbJgccService.lookJdbjg(commonInfoVo);
            if (list2 !=null && !list2.isEmpty()){
                for (Map<String, Object> map : list2) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ql2zds+=Double.valueOf(z);
                    ql2hgds+=Double.valueOf(h);
                }
            }


            List<Map<String, Object>> list4 = jjgFbgcQlgcXbBhchdService.lookJdbjg(commonInfoVo);
            if (list4 !=null && !list4.isEmpty()){
                for (Map<String, Object> map : list4) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ql2zds+=Double.valueOf(z);
                    ql2hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list7 = jjgFbgcQlgcSbJgccService.lookJdbjg(commonInfoVo);
            if (list7 !=null && !list7.isEmpty()){
                for (Map<String, Object> map : list7) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ql2zds+=Double.valueOf(z);
                    ql2hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list8 = jjgFbgcQlgcSbBhchdService.lookJdbjg(commonInfoVo);
            if (list8 !=null && !list8.isEmpty()){
                for (Map<String, Object> map : list8) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ql2zds+=Double.valueOf(z);
                    ql2hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list9 = jjgFbgcQlgcQmpzdService.lookJdbjg(commonInfoVo);
            if (list9 !=null && !list9.isEmpty()){
                for (Map<String, Object> map : list9) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    ql2zds+=Double.valueOf(z);
                    ql2hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list10 = jjgFbgcQlgcQmhpService.lookJdbjg(commonInfoVo);
            if (list10 !=null && !list10.isEmpty()){
                for (Map<String, Object> map : list10) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    ql2zds+=Double.valueOf(z);
                    ql2hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, Object>> list11 = jjgFbgcQlgcQmgzsdService.lookJdbjg(commonInfoVo);
            if (list11 !=null && !list11.isEmpty()){
                for (Map<String, Object> map : list11) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    ql2zds+=Double.valueOf(z);
                    ql2hgds+=Double.valueOf(h);
                }
            }
        }
        List<Map<String,Object>> ljmaplist = new ArrayList<>();
        Map<String,Object> map2 = new HashMap<>();
        map2.put("zblx","*");
        map2.put("fbgc","桥梁工程");
        map2.put("zds",ql1zds);
        map2.put("hgds",ql1hgds);
        map2.put("hgl",ql1zds!=0 ? df.format(ql1hgds/ql1zds*100) : 0);

        Map<String,Object> map3 = new HashMap<>();
        map3.put("zblx","其余指标");
        map3.put("fbgc","桥梁工程");
        map3.put("zds",ql2zds);
        map3.put("hgds",ql2hgds);
        map3.put("hgl",ql2zds!=0 ? df.format(ql2hgds/ql2zds*100) : 0);

        Map<String,Object> map4 = new HashMap<>();
        map4.put("zblx","合计指标");
        map4.put("fbgc","桥梁工程");
        map4.put("zds",ql1zds+ql2zds);
        map4.put("hgds",ql1hgds+ql2hgds);
        map4.put("hgl",(ql1zds+ql2zds)!=0 ? df.format((ql1hgds+ql2hgds)/(ql1zds+ql2zds)*100) : 0);

        ljmaplist.add(map2);
        ljmaplist.add(map3);
        ljmaplist.add(map4);

        return ljmaplist;
    }

    private List<Map<String, Object>> getlm(String proname, DecimalFormat df, List<String> lmlist) throws IOException {
        double lm1zds = 0,lm1hgds = 0,lm2zds = 0,lm2hgds = 0,lm3zds = 0,lm3hgds = 0;
        for (String s : lmlist) {
            CommonInfoVo commonInfoVo = new CommonInfoVo();
            commonInfoVo.setProname(proname);
            commonInfoVo.setHtd(s);

            //============================△=========================================
            List<Map<String, Object>> list10 = jjgFbgcLmgcLqlmysdService.lookJdbjg(commonInfoVo);
            if (list10 !=null && !list10.isEmpty()){
                for (Map<String, Object> map : list10) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    lm1zds+=Double.valueOf(z);
                    lm1hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list8 = jjgFbgcLmgcLmwcService.lookJdbjg(commonInfoVo);
            if (list8 !=null && !list8.isEmpty()){
                for (Map<String, Object> map : list8) {
                    String z = map.get("检测单元数").toString();
                    String h = map.get("合格单元数").toString();
                    lm1zds+=Double.valueOf(z);
                    lm1hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list9 = jjgFbgcLmgcLmwcLcfService.lookJdbjg(commonInfoVo);
            if (list9 !=null && !list9.isEmpty()){
                for (Map<String, Object> map : list9) {
                    String z = map.get("检测单元数").toString();
                    String h = map.get("合格单元数").toString();
                    lm1zds+=Double.valueOf(z);
                    lm1hgds+=Double.valueOf(h);
                }
            }
            /*List<Map<String, Object>> list1 = jjgFbgcLmgcGslqlmhdzxfService.lookJdbjg(commonInfoVo);
                if (!list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("上面层厚度检测点数").toString();
                        String h = map.get("上面层厚度合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }*/
            List<Map<String, Object>> list2 = jjgFbgcLmgcHntlmhdzxfService.lookJdbjg(commonInfoVo);
            if (list2 !=null && !list2.isEmpty()){
                for (Map<String, Object> map : list2) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    lm1zds+=Double.valueOf(z);
                    lm1hgds+=Double.valueOf(h);
                }
            }

            //============================*=========================================
            List<Map<String, Object>> list4 = jjgFbgcLmgcHntlmqdService.lookJdbjg(commonInfoVo);
            if (list4 !=null && !list4.isEmpty()){
                for (Map<String, Object> map : list4) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    lm2zds+=Double.valueOf(z);
                    lm2hgds+=Double.valueOf(h);
                }
            }
            //============================其余指标=========================================
            List<Map<String, Object>> list5 = jjgFbgcLmgcLmgzsdsgpsfService.lookJdbjg(commonInfoVo);
            if (list5 !=null && !list5.isEmpty()){
                for (Map<String, Object> map : list5) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    lm3zds+=Double.valueOf(z);
                    lm3hgds+=Double.valueOf(h);
                }
            }

            List<Map<String, String>> list6 = jjgFbgcLmgcLmhpService.lookJdbjg(commonInfoVo);
            if (list6 !=null && !list6.isEmpty()){
                for (Map<String, String> map : list6) {
                    if (!map.isEmpty()){
                        String z = map.get("检测点数");
                        String h = map.get("合格点数");
                        lm3zds+=Double.valueOf(z);
                        lm3hgds+=Double.valueOf(h);
                    }

                }
            }

            List<Map<String, Object>> list7 = jjgFbgcLmgcLmssxsService.lookJdbjg(commonInfoVo);
            if (list7 !=null && !list7.isEmpty()){
                for (Map<String, Object> map : list7) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    lm3zds+=Double.valueOf(z);
                    lm3hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list11 = jjgFbgcLmgcTlmxlbgcService.lookJdbjg(commonInfoVo);
            if (list11 !=null && !list11.isEmpty()){
                for (Map<String, Object> map : list11) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    lm3zds+=Double.valueOf(z);
                    lm3hgds+=Double.valueOf(h);
                }
            }

        }
        List<Map<String,Object>> ljmaplist = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("zblx","△");
        map1.put("fbgc","路面工程");
        map1.put("zds",lm1zds);
        map1.put("hgds",lm1hgds);
        map1.put("hgl",lm1zds!=0 ? df.format(lm1hgds/lm1zds*100) : 0);

        Map<String,Object> map2 = new HashMap<>();
        map2.put("zblx","*");
        map2.put("fbgc","路面工程");
        map2.put("zds",lm2zds);
        map2.put("hgds",lm2hgds);
        map2.put("hgl",lm2zds!=0 ? df.format(lm2hgds/lm2zds*100) : 0);

        Map<String,Object> map3 = new HashMap<>();
        map3.put("zblx","其余指标");
        map3.put("fbgc","路面工程");
        map3.put("zds",lm3zds);
        map3.put("hgds",lm3hgds);
        map3.put("hgl",lm3zds!=0 ? df.format(lm3hgds/lm3zds*100) : 0);

        Map<String,Object> map4 = new HashMap<>();
        map4.put("zblx","合计指标");
        map4.put("fbgc","路面工程");
        map4.put("zds",lm1zds+lm2zds+lm3zds);
        map4.put("hgds",lm1hgds+lm2hgds+lm3hgds);
        map4.put("hgl",(lm1zds+lm2zds+lm3zds)!=0 ? df.format((lm1hgds+lm2hgds+lm3hgds)/(lm1zds+lm2zds+lm3zds)*100) : 0);

        ljmaplist.add(map1);
        ljmaplist.add(map2);
        ljmaplist.add(map3);
        ljmaplist.add(map4);

        return ljmaplist;
    }

    private List<Map<String,Object>> getlj(String proname, DecimalFormat df, List<String> ljlist) throws IOException {
        double lj1zds = 0,lj1hgds = 0,lj2zds = 0,lj2hgds = 0,lj3zds = 0,lj3hgds = 0;
        for (String s : ljlist) {
            CommonInfoVo commonInfoVo = new CommonInfoVo();
            commonInfoVo.setProname(proname);
            commonInfoVo.setHtd(s);
            List<Map<String, Object>> list5 = jjgFbgcLjgcLjtsfysdHtService.lookJdbjg(commonInfoVo);
            if (list5 !=null && !list5.isEmpty()) {
                for (Map<String, Object> map : list5) {
                    String z = map.get("检测点数").toString();
                    String h = map.get("合格点数").toString();
                    lj1zds += Double.valueOf(z);
                    lj1hgds += Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list6 = jjgFbgcLjgcLjwcService.lookJdbjg(commonInfoVo);
            if (list6 !=null && !list6.isEmpty()) {
                for (Map<String, Object> map : list6) {
                    String z = map.get("检测单元数").toString();
                    String h = map.get("合格单元数").toString();
                    lj1zds += Double.valueOf(z);
                    lj1hgds += Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list7 = jjgFbgcLjgcLjwcLcfService.lookJdbjg(commonInfoVo);
            if (list7 !=null && !list7.isEmpty()) {
                for (Map<String, Object> map : list7) {
                    String z = map.get("检测单元数").toString();
                    String h = map.get("合格单元数").toString();
                    lj1zds += Double.valueOf(z);
                    lj1hgds += Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list12 = jjgFbgcLjgcZddmccService.lookJdbjg(commonInfoVo);
            if (list12 !=null && !list12.isEmpty()) {
                for (Map<String, Object> map : list12) {
                    String z = map.get("检测总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj1zds += Double.valueOf(z);
                    lj1hgds += Double.valueOf(h);
                }
            }
            //==================================================================================================
            List<Map<String, Object>> list1 = jjgFbgcLjgcHdgqdService.lookJdbjg(commonInfoVo);
            if (list1 !=null && !list1.isEmpty()){
                for (Map<String, Object> map : list1) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj2zds+=Double.valueOf(z);
                    lj2hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list10 = jjgFbgcLjgcXqgqdService.lookJdbjg(commonInfoVo);
            if (list10 !=null && !list10.isEmpty()) {
                for (Map<String, Object> map : list10) {
                    String z = map.get("检测总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj2zds += Double.valueOf(z);
                    lj2hgds += Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list13 = jjgFbgcLjgcZdgqdService.lookJdbjg(commonInfoVo);
            if (list13 !=null && !list13.isEmpty()) {
                for (Map<String, Object> map : list13) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj2zds += Double.valueOf(z);
                    lj2hgds += Double.valueOf(h);
                }
            }
            //==================================================================================================
            List<Map<String, Object>> list3 = jjgFbgcLjgcLjbpService.lookJdbjg(commonInfoVo);
            if (list3 !=null && !list3.isEmpty()) {
                for (Map<String, Object> map : list3) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj3zds += Double.valueOf(z);
                    lj3hgds += Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list8 = jjgFbgcLjgcPsdmccService.lookJdbjg(commonInfoVo);
            if (list8 !=null && !list8.isEmpty()) {
                for (Map<String, Object> map : list8) {
                    String z = map.get("检测总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj3zds += Double.valueOf(z);
                    lj3hgds += Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list9 = jjgFbgcLjgcPspqhdService.lookJdbjg(commonInfoVo);
            if (list9 !=null && !list9.isEmpty()) {
                for (Map<String, Object> map : list9) {
                    String z = map.get("检测总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj3zds += Double.valueOf(z);
                    lj3hgds += Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list11 = jjgFbgcLjgcXqjgccService.lookJdbjg(commonInfoVo);
            if (list11 !=null && !list11.isEmpty()) {
                for (Map<String, Object> map : list11) {
                    String z = map.get("检测总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj3zds += Double.valueOf(z);
                    lj3hgds += Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list2 = jjgFbgcLjgcHdjgccService.lookJdbjg(commonInfoVo);
            if (list2 !=null && !list2.isEmpty()){
                for (Map<String, Object> map : list2) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj3zds+=Double.valueOf(z);
                    lj3hgds+=Double.valueOf(h);
                }
            }
            List<Map<String, Object>> list4 = jjgFbgcLjgcLjcjService.lookJdbjg(commonInfoVo);
            if (list4 !=null && !list4.isEmpty()) {
                for (Map<String, Object> map : list4) {
                    String z = map.get("总点数").toString();
                    String h = map.get("合格点数").toString();
                    lj3zds += Double.valueOf(z);
                    lj3hgds += Double.valueOf(h);
                }
            }

        }
        //△ 压实度，弯沉，支挡断面尺寸
        //* 小桥砼强度，涵洞砼强度，支挡砼强度
        //其余指标 路基边坡 排水断面尺寸 排水铺砌厚度 小桥主要结构尺寸 涵洞结构尺寸
        List<Map<String,Object>> ljmaplist = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("zblx","△");
        map1.put("fbgc","路基工程");
        map1.put("zds",lj1zds);
        map1.put("hgds",lj1hgds);
        map1.put("hgl",lj1zds!=0 ? df.format(lj1hgds/lj1zds*100) : 0);

        Map<String,Object> map2 = new HashMap<>();
        map2.put("zblx","*");
        map2.put("fbgc","路基工程");
        map2.put("zds",lj2zds);
        map2.put("hgds",lj2hgds);
        map2.put("hgl",lj2zds!=0 ? df.format(lj2hgds/lj2zds*100) : 0);

        Map<String,Object> map3 = new HashMap<>();
        map3.put("zblx","其余指标");
        map3.put("fbgc","路基工程");
        map3.put("zds",lj3zds);
        map3.put("hgds",lj3hgds);
        map3.put("hgl",lj3zds!=0 ? df.format(lj3hgds/lj3zds*100) : 0);

        Map<String,Object> map4 = new HashMap<>();
        map4.put("zblx","合计指标");
        map4.put("fbgc","路基工程");
        map4.put("zds",lj1zds+lj2zds+lj3zds);
        map4.put("hgds",lj1hgds+lj2hgds+lj3hgds);
        map4.put("hgl",(lj1zds+lj2zds+lj3zds)!=0 ? df.format((lj1hgds+lj2hgds+lj3hgds)/(lj1zds+lj2zds+lj3zds)*100) : 0);

        ljmaplist.add(map1);
        ljmaplist.add(map2);
        ljmaplist.add(map3);
        ljmaplist.add(map4);

        return ljmaplist;
    }

    /**
     *
     * @param proname
     * @return
     */
    private Map<String, Map<String, Map<String, List<Map<String, Object>>>>> gethtdwcldata(String proname) {
        Map<String,Map<String, List<JjgPlaninfo>>> tempmap = new HashMap<>();
        QueryWrapper<JjgPlaninfo> wrapper=new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgPlaninfo> list = jjgLookProjectPlanService.list(wrapper);
        Map<String, List<JjgPlaninfo>> groupedData = list.stream()
                .collect(Collectors.groupingBy(JjgPlaninfo::getHtd));
        groupedData.forEach((group, grouphtdData) -> {
            Map<String, List<JjgPlaninfo>> grouplx = grouphtdData.stream()
                    .collect(Collectors.groupingBy(JjgPlaninfo::getFbgc));
            tempmap.put(group,grouplx);

        });
        List<Map<String, Object>> zslist = new ArrayList<>();
        tempmap.forEach((htdname, data) -> {
            data.forEach((fbgcname, htddata) -> {
                Map<String, Object> mapz = new HashMap<>();
                double zs = 0;
                for (JjgPlaninfo htddatum : htddata) {
                    String num = htddatum.getNum();
                    zs += Double.valueOf(num);
                }
                //[{fbgcname=LJ-2, zs=15.0, proname=陕西高速, htdname=路面工程}, {fbgcname=LJ-1, zs=150.0, proname=陕西高速, htdname=路基工程}, {fbgcname=LM-1, zs=5.0, proname=陕西高速, htdname=路面工程}]
                mapz.put("proname", proname);
                mapz.put("fbgcname", fbgcname);
                mapz.put("htdname", htdname);
                mapz.put("zs", zs);
                zslist.add(mapz);
            });
        });
        for (Map<String, Object> map : zslist) {
            String htd = map.get("htdname").toString();
            String fbgcname = map.get("fbgcname").toString();
            if (fbgcname.equals("路基工程")){
                int num1 = jjgFbgcLjgcHdgqdService.selectnum(proname, htd);
                int num2 = jjgFbgcLjgcHdjgccService.selectnum(proname, htd);
                int num3 = jjgFbgcLjgcLjtsfysdHtService.selectnum(proname, htd);
                int num4 = jjgFbgcLjgcLjcjService.selectnum(proname, htd);
                int num5 = jjgFbgcLjgcLjwcService.selectnum(proname, htd);
                int num6 = jjgFbgcLjgcLjwcLcfService.selectnum(proname, htd);
                int num7 = jjgFbgcLjgcLjbpService.selectnum(proname, htd);
                int num8 = jjgFbgcLjgcPsdmccService.selectnum(proname, htd);
                int num9 = jjgFbgcLjgcPspqhdService.selectnum(proname, htd);
                int num10 = jjgFbgcLjgcXqgqdService.selectnum(proname, htd);
                int num11 = jjgFbgcLjgcXqjgccService.selectnum(proname, htd);
                int num12 = jjgFbgcLjgcZdgqdService.selectnum(proname, htd);
                int num13 = jjgFbgcLjgcZddmccService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5+num6+num7+num8+num9+num10+num11+num12+num13);
            }
            if (fbgcname.equals("路面工程")){
                int num1 = jjgFbgcLmgcGslqlmhdzxfService.selectnum(proname, htd);
                int num2 = jjgFbgcLmgcHntlmhdzxfService.selectnum(proname, htd);
                int num3 = jjgFbgcLmgcHntlmqdService.selectnum(proname, htd);
                int num4 = jjgFbgcLmgcLmgzsdsgpsfService.selectnum(proname, htd);
                int num5 = jjgFbgcLmgcLmhpService.selectnum(proname, htd);
                int num6 = jjgFbgcLmgcLmssxsService.selectnum(proname, htd);
                int num7 = jjgFbgcLmgcLmwcService.selectnum(proname, htd);
                int num8 = jjgFbgcLmgcLmwcLcfService.selectnum(proname, htd);
                int num9 = jjgFbgcLmgcLqlmysdService.selectnum(proname, htd);
                int num10 = jjgFbgcLmgcTlmxlbgcService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5+num6+num7+num8+num9+num10);
            }
            if (fbgcname.equals("交安工程")){
                int num1 = jjgFbgcJtaqssJabxService.selectnum(proname, htd);
                int num2 = jjgFbgcJtaqssJabzService.selectnum(proname, htd);
                int num3 = jjgFbgcJtaqssJabxfhlService.selectnum(proname, htd);
                int num4 = jjgFbgcJtaqssJathlqdService.selectnum(proname, htd);
                int num5 = jjgFbgcJtaqssJathldmccService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5);
            }
            if (fbgcname.equals("桥梁工程")){
                int num1 = jjgFbgcQlgcXbTqdService.selectnum(proname, htd);
                int num2 = jjgFbgcQlgcXbJgccService.selectnum(proname, htd);
                int num3 = jjgFbgcQlgcXbBhchdService.selectnum(proname, htd);
                int num4 = jjgFbgcQlgcXbSzdService.selectnum(proname, htd);
                int num5 = jjgFbgcQlgcSbTqdService.selectnum(proname, htd);
                int num6 = jjgFbgcQlgcSbJgccService.selectnum(proname, htd);
                int num7 = jjgFbgcQlgcSbBhchdService.selectnum(proname, htd);
                int num8 = jjgFbgcQlgcQmpzdService.selectnum(proname, htd);
                int num9 = jjgFbgcQlgcQmhpService.selectnum(proname, htd);
                int num10 = jjgFbgcQlgcQmgzsdService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5+num6+num7+num8+num9+num10);
            }
            if (fbgcname.equals("隧道工程")){
                int num1 = jjgFbgcSdgcDmpzdService.selectnum(proname, htd);
                int num2 = jjgFbgcSdgcZtkdService.selectnum(proname, htd);
                int num3 = jjgFbgcSdgcCqhdService.selectnum(proname, htd);
                int num4 = jjgFbgcSdgcSdlqlmysdService.selectnum(proname, htd);
                int num5 = jjgFbgcSdgcLmssxsService.selectnum(proname, htd);
                int num6 = jjgFbgcSdgcHntlmqdService.selectnum(proname, htd);
                int num7 = jjgFbgcSdgcTlmxlbgcService.selectnum(proname, htd);
                int num8 = jjgFbgcSdgcSdhpService.selectnum(proname, htd);
                int num9 = jjgFbgcSdgcSdhntlmhdzxfService.selectnum(proname, htd);
                int num10 = jjgFbgcSdgcGssdlqlmhdzxfService.selectnum(proname, htd);
                int num11 = jjgFbgcSdgcLmgzsdsgpsfService.selectnum(proname, htd);
                int num12 = jjgFbgcSdgcCqtqdService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5+num6+num7+num8+num9+num10+num11+num12);
            }
        }
        Map<String,Map<String, Map<String, List<Map<String, Object>>>>> dd = new HashMap<>();
        Map<String, Map<String, List<Map<String, Object>>>> te = new HashMap<>();
        Map<String, List<Map<String, Object>>> result = zslist.stream()
                .collect(Collectors.groupingBy(map -> (String) map.get("htdname")));
        result.forEach((group, grouphtdData) -> {
            Map<String, List<Map<String, Object>>> htdname = grouphtdData.stream().collect(Collectors.groupingBy(map -> (String) map.get("fbgcname")));
            te.put("合同段指标完成率",htdname);
            dd.put(group,te);
        });
        //[{fbgcname=LJ-2, zs=15.0, proname=陕西高速, htdname=路面工程}, {fbgcname=LJ-1, zs=150.0, proname=陕西高速, htdname=路基工程}, {fbgcname=LM-1, zs=5.0, proname=陕西高速, htdname=路面工程}]
        /**
         * {LJ-2={合同段指标完成率={路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}},
         * LJ-1={合同段指标完成率={路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}},
         * LM-1={合同段指标完成率={路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}}}
         */

        /**
         * {
         * LJ-2={
         * 路面工程=[
         * JjgPlaninfo(id=5, proname=陕西高速, htd=LJ-2, fbgc=路面工程, zb=路面弯沉, num=5, createTime=null),
         * JjgPlaninfo(id=6, proname=陕西高速, htd=LJ-2, fbgc=路面工程, zb=路面车辙, num=5, createTime=null),
         * JjgPlaninfo(id=7, proname=陕西高速, htd=LJ-2, fbgc=路面工程, zb=压实度, num=5, createTime=null)
         * ]},
         * LJ-1={
         * 路基工程=[JjgPlaninfo(id=1, proname=陕西高速, htd=LJ-1, fbgc=路基工程, zb=涵洞砼强度, num=40, createTime=Thu Sep 07 00:00:00 CST 2023), JjgPlaninfo(id=2, proname=陕西高速, htd=LJ-1, fbgc=路基工程, zb=涵洞结构尺寸, num=40, createTime=Thu Sep 07 00:00:00 CST 2023), JjgPlaninfo(id=3, proname=陕西高速, htd=LJ-1, fbgc=路基工程, zb=排水断面尺寸, num=50, createTime=Thu Sep 07 00:00:00 CST 2023), JjgPlaninfo(id=4, proname=陕西高速, htd=LJ-1, fbgc=路基工程, zb=排水铺砌厚度, num=20, createTime=Thu Sep 07 00:00:00 CST 2023)]},
         * LM-1={
         * 路面工程=[JjgPlaninfo(id=8, proname=陕西高速, htd=LM-1, fbgc=路面工程, zb=压实度, num=5, createTime=null)]}}
         */
        return dd;

    }

    /**
     *
     * @param proname
     * @return
     * @throws IOException
     */
    private Map<String,Map<String, Map<String, List<Map<String, Object>>>>> gethtdhgldata(String proname) throws IOException {
        List<JjgHtd> gethtd = jjgHtdService.gethtd(proname);
        List<Map<String,Object>> res = new ArrayList<>();
        for (JjgHtd jjgHtd : gethtd) {
            String htd = jjgHtd.getName();
            String lx = jjgHtd.getLx();

            if (lx.contains("路基工程")){
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(htd);
                List<Map<String, Object>> list1 = jjgFbgcLjgcHdgqdService.lookJdbjg(commonInfoVo);
                if (list1 !=null && !list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list2 = jjgFbgcLjgcHdjgccService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list3 = jjgFbgcLjgcLjbpService.lookJdbjg(commonInfoVo);
                if (list3 !=null && !list3.isEmpty()) {
                    for (Map<String, Object> map : list3) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list4 = jjgFbgcLjgcLjcjService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()) {
                    for (Map<String, Object> map : list4) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list5 = jjgFbgcLjgcLjtsfysdHtService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()) {
                    for (Map<String, Object> map : list5) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list6 = jjgFbgcLjgcLjwcService.lookJdbjg(commonInfoVo);
                if (list6 !=null && !list6.isEmpty()) {
                    for (Map<String, Object> map : list6) {
                        String z = map.get("检测单元数").toString();
                        String h = map.get("合格单元数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list7 = jjgFbgcLjgcLjwcLcfService.lookJdbjg(commonInfoVo);
                if (list7 !=null && !list7.isEmpty()) {
                    for (Map<String, Object> map : list7) {
                        String z = map.get("检测单元数").toString();
                        String h = map.get("合格单元数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list8 = jjgFbgcLjgcPsdmccService.lookJdbjg(commonInfoVo);
                if (list8 !=null && !list8.isEmpty()) {
                    for (Map<String, Object> map : list8) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list9 = jjgFbgcLjgcPspqhdService.lookJdbjg(commonInfoVo);
                if (list9 !=null && !list9.isEmpty()) {
                    for (Map<String, Object> map : list9) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list10 = jjgFbgcLjgcXqgqdService.lookJdbjg(commonInfoVo);
                if (list10 !=null && !list10.isEmpty()) {
                    for (Map<String, Object> map : list10) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list11 = jjgFbgcLjgcXqjgccService.lookJdbjg(commonInfoVo);
                if (list11 !=null && !list11.isEmpty()) {
                    for (Map<String, Object> map : list11) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list12 = jjgFbgcLjgcZddmccService.lookJdbjg(commonInfoVo);
                if (list12 !=null && !list12.isEmpty()) {
                    for (Map<String, Object> map : list12) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list13 = jjgFbgcLjgcZdgqdService.lookJdbjg(commonInfoVo);
                if (list13 !=null && !list13.isEmpty()) {
                    for (Map<String, Object> map : list13) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }

                Map<String,Object> temmap = new HashMap<>();
                temmap.put("htd",htd);
                temmap.put("lx","路基工程");
                temmap.put("zds",zds);
                temmap.put("hgds",hgds);
                res.add(temmap);
            }

            if (lx.contains("路面工程")){
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(htd);
                /*List<Map<String, Object>> list1 = jjgFbgcLmgcGslqlmhdzxfService.lookJdbjg(commonInfoVo);
                if (!list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("上面层厚度检测点数").toString();
                        String h = map.get("上面层厚度合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }*/
                List<Map<String, Object>> list2 = jjgFbgcLmgcHntlmhdzxfService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list3 = jjgFbgcLmgcHntlmhdzxfService.lookJdbjg(commonInfoVo);
                if (list3 !=null &&!list3.isEmpty()){
                    for (Map<String, Object> map : list3) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list4 = jjgFbgcLmgcHntlmqdService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()){
                    for (Map<String, Object> map : list4) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list5 = jjgFbgcLmgcLmgzsdsgpsfService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()){
                    for (Map<String, Object> map : list5) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, String>> list6 = jjgFbgcLmgcLmhpService.lookJdbjg(commonInfoVo);
                if (list6 !=null && !list6.isEmpty()){
                    for (Map<String, String> map : list6) {
                        if (!map.isEmpty()){
                            String z = map.get("检测点数");
                            String h = map.get("合格点数");
                            zds+=Double.valueOf(z);
                            hgds+=Double.valueOf(h);
                        }

                    }
                }

                List<Map<String, Object>> list7 = jjgFbgcLmgcLmssxsService.lookJdbjg(commonInfoVo);
                if (list7 !=null && !list7.isEmpty()){
                    for (Map<String, Object> map : list7) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list8 = jjgFbgcLmgcLmwcService.lookJdbjg(commonInfoVo);
                if (list8 !=null && !list8.isEmpty()){
                    for (Map<String, Object> map : list8) {
                        String z = map.get("检测单元数").toString();
                        String h = map.get("合格单元数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list9 = jjgFbgcLmgcLmwcLcfService.lookJdbjg(commonInfoVo);
                if (list9 !=null && !list9.isEmpty()){
                    for (Map<String, Object> map : list9) {
                        String z = map.get("检测单元数").toString();
                        String h = map.get("合格单元数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list10 = jjgFbgcLmgcLqlmysdService.lookJdbjg(commonInfoVo);
                if (list10 !=null && !list10.isEmpty()){
                    for (Map<String, Object> map : list10) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list11 = jjgFbgcLmgcTlmxlbgcService.lookJdbjg(commonInfoVo);
                if (list11 !=null && !list11.isEmpty()){
                    for (Map<String, Object> map : list11) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                Map<String,Object> temmap = new HashMap<>();
                temmap.put("htd",htd);
                temmap.put("lx","路面工程");
                temmap.put("zds",zds);
                temmap.put("hgds",hgds);
                res.add(temmap);

            }

            if (lx.contains("隧道工程")){
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(htd);
                List<Map<String, Object>> list1 = jjgFbgcSdgcCqtqdService.lookJdbjg(commonInfoVo);
                if (list1 !=null && !list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list2 = jjgFbgcSdgcDmpzdService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list3 = jjgFbgcSdgcZtkdService.lookJdbjg(commonInfoVo);
                if (list3 !=null && !list3.isEmpty()){
                    for (Map<String, Object> map : list3) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list4 = jjgFbgcSdgcCqhdService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()){
                    for (Map<String, Object> map : list4) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list5 = jjgFbgcSdgcSdlqlmysdService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()){
                    for (Map<String, Object> map : list5) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list6 = jjgFbgcSdgcLmssxsService.lookJdbjg(commonInfoVo);
                if (list6 !=null && !list6.isEmpty()){
                    for (Map<String, Object> map : list6) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list7 = jjgFbgcSdgcHntlmqdService.lookJdbjg(commonInfoVo);
                if (list7 !=null && !list7.isEmpty()){
                    for (Map<String, Object> map : list7) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list8 = jjgFbgcSdgcTlmxlbgcService.lookJdbjg(commonInfoVo);
                if (list8 !=null && !list8.isEmpty()){
                    for (Map<String, Object> map : list8) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list9 = jjgFbgcSdgcSdhpService.lookJdbjg(commonInfoVo);
                if (list9 !=null && !list9.isEmpty()){
                    for (Map<String, Object> map : list9) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list10 = jjgFbgcSdgcSdhntlmhdzxfService.lookJdbjg(commonInfoVo);
                if (list10 !=null && !list10.isEmpty()){
                    for (Map<String, Object> map : list10) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list11 = jjgFbgcSdgcGssdlqlmhdzxfService.lookJdbjg(commonInfoVo);
                if (list11 !=null && !list11.isEmpty()){
                    for (Map<String, Object> map : list11) {
                        String z = map.get("总厚度检测点数").toString();
                        String h = map.get("总厚度合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list12 = jjgFbgcSdgcLmgzsdsgpsfService.lookJdbjg(commonInfoVo);
                if (list12 !=null && !list12.isEmpty()){
                    for (Map<String, Object> map : list12) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                Map<String,Object> temmap = new HashMap<>();
                temmap.put("htd",htd);
                temmap.put("lx","隧道工程");
                temmap.put("zds",zds);
                temmap.put("hgds",hgds);
                res.add(temmap);

            }

            if (lx.contains("桥梁工程")){
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(htd);
                List<Map<String, Object>> list1 = jjgFbgcQlgcXbTqdService.lookJdbjg(commonInfoVo);
                if (list1 !=null && !list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list2 = jjgFbgcQlgcXbJgccService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }


                List<Map<String, Object>> list4 = jjgFbgcQlgcXbBhchdService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()){
                    for (Map<String, Object> map : list4) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list5 = jjgFbgcQlgcXbSzdService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()){
                    for (Map<String, Object> map : list5) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list6 = jjgFbgcQlgcSbTqdService.lookJdbjg(commonInfoVo);
                if (list6 !=null && !list6.isEmpty()){
                    for (Map<String, Object> map : list6) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list7 = jjgFbgcQlgcSbJgccService.lookJdbjg(commonInfoVo);
                if (list7 !=null && !list7.isEmpty()){
                    for (Map<String, Object> map : list7) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list8 = jjgFbgcQlgcSbBhchdService.lookJdbjg(commonInfoVo);
                if (list8 !=null && !list8.isEmpty()){
                    for (Map<String, Object> map : list8) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list9 = jjgFbgcQlgcQmpzdService.lookJdbjg(commonInfoVo);
                if (list9 !=null && !list9.isEmpty()){
                    for (Map<String, Object> map : list9) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list10 = jjgFbgcQlgcQmhpService.lookJdbjg(commonInfoVo);
                if (list10 !=null && !list10.isEmpty()){
                    for (Map<String, Object> map : list10) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list11 = jjgFbgcQlgcQmgzsdService.lookJdbjg(commonInfoVo);
                if (list11 !=null && !list11.isEmpty()){
                    for (Map<String, Object> map : list11) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                Map<String,Object> temmap = new HashMap<>();
                temmap.put("htd",htd);
                temmap.put("lx","桥梁工程");
                temmap.put("zds",zds);
                temmap.put("hgds",hgds);
                res.add(temmap);
            }

            if (lx.contains("交安工程")){
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(htd);
                List<Map<String, Object>> list1 = jjgFbgcJtaqssJabxService.lookJdbjg(commonInfoVo);
                if (list1 !=null && !list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list2 = jjgFbgcJtaqssJabxfhlService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list3 = jjgFbgcJtaqssJabzService.lookJdbjg(commonInfoVo);
                if (list3 !=null && !list3.isEmpty()){
                    for (Map<String, Object> map : list3) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list4 = jjgFbgcJtaqssJathldmccService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()){
                    for (Map<String, Object> map : list4) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list5 = jjgFbgcJtaqssJathlqdService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()){
                    for (Map<String, Object> map : list5) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                Map<String,Object> temmap = new HashMap<>();
                temmap.put("htd",htd);
                temmap.put("lx","交安工程");
                temmap.put("zds",zds);
                temmap.put("hgds",hgds);
                res.add(temmap);
            }

        }
        Map<String,Map<String, Map<String, List<Map<String, Object>>>>> dd = new HashMap<>();
        Map<String, Map<String, List<Map<String, Object>>>> te = new HashMap<>();
        Map<String, List<Map<String, Object>>> result = res.stream()
                .collect(Collectors.groupingBy(map -> (String) map.get("htd")));

        result.forEach((group, grouphtdData) -> {
            Map<String, List<Map<String, Object>>> lxname = grouphtdData.stream().collect(Collectors.groupingBy(map -> (String) map.get("lx")));
            te.put("合同段合格率",lxname);
            dd.put(group,te);
        });

        /**
         * {LJ-2={合同段合格率={
         * 路面工程=[{zds=118.0, htd=LJ-1, lx=路面工程, hgds=105.0}],
         * 隧道工程=[{zds=409.0, htd=LJ-1, lx=隧道工程, hgds=386.0}],
         * 交安工程=[{zds=3455.0, htd=LJ-1, lx=交安工程, hgds=3350.0}],
         * 桥梁工程=[{zds=711.0, htd=LJ-1, lx=桥梁工程, hgds=685.0}],
         * 路基工程=[{zds=578.0, htd=LJ-1, lx=路基工程, hgds=522.0}]}},
         *
         * LJ-1={合同段合格率={
         * 路面工程=[{zds=118.0, htd=LJ-1, lx=路面工程, hgds=105.0}], 隧
         * 道工程=[{zds=409.0, htd=LJ-1, lx=隧道工程, hgds=386.0}],
         * 交安工程=[{zds=3455.0, htd=LJ-1, lx=交安工程, hgds=3350.0}],
         * 桥梁工程=[{zds=711.0, htd=LJ-1, lx=桥梁工程, hgds=685.0}],
         * 路基工程=[{zds=578.0, htd=LJ-1, lx=路基工程, hgds=522.0}]}}}
         */
        return dd;
    }

    /**
     *
     * @param proname
     * @return
     */
    private Map<String,Map<String, Map<String, List<Map<String, Object>>>>> getdwgcwcl(String proname) {
        Map<String,Map<String, List<JjgPlaninfo>>> tempmap = new HashMap<>();
        QueryWrapper<JjgPlaninfo> wrapper=new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgPlaninfo> list = jjgLookProjectPlanService.list(wrapper);
        Map<String, List<JjgPlaninfo>> groupedData = list.stream()
                .collect(Collectors.groupingBy(JjgPlaninfo::getFbgc));
        groupedData.forEach((group, grouphtdData) -> {
            Map<String, List<JjgPlaninfo>> grouplx = grouphtdData.stream()
                                        .collect(Collectors.groupingBy(JjgPlaninfo::getHtd));
            tempmap.put(group,grouplx);

        });
        List<Map<String, Object>> zslist = new ArrayList<>();
        tempmap.forEach((fbgcname, data) -> {
            data.forEach((htdname, htddata) -> {
                Map<String, Object> mapz = new HashMap<>();
                double zs = 0;
                for (JjgPlaninfo htddatum : htddata) {
                    String num = htddatum.getNum();
                    zs += Double.valueOf(num);
                }
                mapz.put("proname", proname);
                mapz.put("fbgcname", fbgcname);
                mapz.put("htdname", htdname);
                mapz.put("zs", zs);
                zslist.add(mapz);
            });
        });
        for (Map<String, Object> map : zslist) {
            String htd = map.get("htdname").toString();
            String fbgcname = map.get("fbgcname").toString();
            if (fbgcname.equals("路基工程")){
                int num1 = jjgFbgcLjgcHdgqdService.selectnum(proname, htd);
                int num2 = jjgFbgcLjgcHdjgccService.selectnum(proname, htd);
                int num3 = jjgFbgcLjgcLjtsfysdHtService.selectnum(proname, htd);
                int num4 = jjgFbgcLjgcLjcjService.selectnum(proname, htd);
                int num5 = jjgFbgcLjgcLjwcService.selectnum(proname, htd);
                int num6 = jjgFbgcLjgcLjwcLcfService.selectnum(proname, htd);
                int num7 = jjgFbgcLjgcLjbpService.selectnum(proname, htd);
                int num8 = jjgFbgcLjgcPsdmccService.selectnum(proname, htd);
                int num9 = jjgFbgcLjgcPspqhdService.selectnum(proname, htd);
                int num10 = jjgFbgcLjgcXqgqdService.selectnum(proname, htd);
                int num11 = jjgFbgcLjgcXqjgccService.selectnum(proname, htd);
                int num12 = jjgFbgcLjgcZdgqdService.selectnum(proname, htd);
                int num13 = jjgFbgcLjgcZddmccService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5+num6+num7+num8+num9+num10+num11+num12+num13);
            }
            if (fbgcname.equals("路面工程")){
                int num1 = jjgFbgcLmgcGslqlmhdzxfService.selectnum(proname, htd);
                int num2 = jjgFbgcLmgcHntlmhdzxfService.selectnum(proname, htd);
                int num3 = jjgFbgcLmgcHntlmqdService.selectnum(proname, htd);
                int num4 = jjgFbgcLmgcLmgzsdsgpsfService.selectnum(proname, htd);
                int num5 = jjgFbgcLmgcLmhpService.selectnum(proname, htd);
                int num6 = jjgFbgcLmgcLmssxsService.selectnum(proname, htd);
                int num7 = jjgFbgcLmgcLmwcService.selectnum(proname, htd);
                int num8 = jjgFbgcLmgcLmwcLcfService.selectnum(proname, htd);
                int num9 = jjgFbgcLmgcLqlmysdService.selectnum(proname, htd);
                int num10 = jjgFbgcLmgcTlmxlbgcService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5+num6+num7+num8+num9+num10);
            }
            if (fbgcname.equals("交安工程")){
                int num1 = jjgFbgcJtaqssJabxService.selectnum(proname, htd);
                int num2 = jjgFbgcJtaqssJabzService.selectnum(proname, htd);
                int num3 = jjgFbgcJtaqssJabxfhlService.selectnum(proname, htd);
                int num4 = jjgFbgcJtaqssJathlqdService.selectnum(proname, htd);
                int num5 = jjgFbgcJtaqssJathldmccService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5);
            }
            if (fbgcname.equals("桥梁工程")){
                int num1 = jjgFbgcQlgcXbTqdService.selectnum(proname, htd);
                int num2 = jjgFbgcQlgcXbJgccService.selectnum(proname, htd);
                int num3 = jjgFbgcQlgcXbBhchdService.selectnum(proname, htd);
                int num4 = jjgFbgcQlgcXbSzdService.selectnum(proname, htd);
                int num5 = jjgFbgcQlgcSbTqdService.selectnum(proname, htd);
                int num6 = jjgFbgcQlgcSbJgccService.selectnum(proname, htd);
                int num7 = jjgFbgcQlgcSbBhchdService.selectnum(proname, htd);
                int num8 = jjgFbgcQlgcQmpzdService.selectnum(proname, htd);
                int num9 = jjgFbgcQlgcQmhpService.selectnum(proname, htd);
                int num10 = jjgFbgcQlgcQmgzsdService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5+num6+num7+num8+num9+num10);
            }
            if (fbgcname.equals("隧道工程")){
                int num1 = jjgFbgcSdgcDmpzdService.selectnum(proname, htd);
                int num2 = jjgFbgcSdgcZtkdService.selectnum(proname, htd);
                int num3 = jjgFbgcSdgcCqhdService.selectnum(proname, htd);
                int num4 = jjgFbgcSdgcSdlqlmysdService.selectnum(proname, htd);
                int num5 = jjgFbgcSdgcLmssxsService.selectnum(proname, htd);
                int num6 = jjgFbgcSdgcHntlmqdService.selectnum(proname, htd);
                int num7 = jjgFbgcSdgcTlmxlbgcService.selectnum(proname, htd);
                int num8 = jjgFbgcSdgcSdhpService.selectnum(proname, htd);
                int num9 = jjgFbgcSdgcSdhntlmhdzxfService.selectnum(proname, htd);
                int num10 = jjgFbgcSdgcGssdlqlmhdzxfService.selectnum(proname, htd);
                int num11 = jjgFbgcSdgcLmgzsdsgpsfService.selectnum(proname, htd);
                int num12 = jjgFbgcSdgcCqtqdService.selectnum(proname, htd);
                map.put("jcs",num1+num2+num3+num4+num5+num6+num7+num8+num9+num10+num11+num12);
            }
        }
        Map<String,Map<String, Map<String, List<Map<String, Object>>>>> dd = new HashMap<>();
        Map<String, Map<String, List<Map<String, Object>>>> te = new HashMap<>();
        Map<String, List<Map<String, Object>>> result = zslist.stream()
                .collect(Collectors.groupingBy(map -> (String) map.get("fbgcname")));
        result.forEach((group, grouphtdData) -> {
            Map<String, List<Map<String, Object>>> htdname = grouphtdData.stream().collect(Collectors.groupingBy(map -> (String) map.get("htdname")));
            te.put("单位工程指标完成率",htdname);
            dd.put(group,te);
        });

        return dd;
    }

    /**
     *
     * @param proname
     * @return
     * @throws IOException
     */
    private Map<String, Map<String, Map<String, List<Map<String, Object>>>>> getdwgchgl(String proname) throws IOException {
        List<JjgHtd> gethtd = jjgHtdService.gethtd(proname);
        List<String> lj = new ArrayList<>();
        List<String> lm = new ArrayList<>();
        List<String> ql = new ArrayList<>();
        List<String> sd = new ArrayList<>();
        List<String> ja = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        Map<String,Map<String,Map<String,List<Map<String, Object>>>>> result = new HashMap<>();
        //Map<String,Map<String,Map<String,Map<String, Object>>>> result = new HashMap<>();
        for (JjgHtd jjgHtd : gethtd) {
            String htd = jjgHtd.getName();
            String lx = jjgHtd.getLx();
            if (lx.contains("路基工程")){
                lj.add(htd);
            }
            if (lx.contains("路面工程")){
                lm.add(htd);
            }
            if (lx.contains("桥梁工程")){
                ql.add(htd);
            }
            if (lx.contains("隧道工程")){
                sd.add(htd);
            }
            if (lx.contains("交安工程")){
                ja.add(htd);
            }
        }
        if (lj !=null && !lj.isEmpty()){
            /*Map<String,Map<String,Object>> ljmap = new HashMap<>();
            Map<String,Map<String,Map<String,Object>>> lj1= new HashMap<>();
            Map<String,Map<String,Map<String,Map<String,Object>>>> ljresult = new HashMap<>();*/

            //Map<String,Map<String, Object>> ljmap = new HashMap<>();
            Map<String,List<Map<String, Object>>> ljmap = new HashMap<>();
            //Map<String,Map<String,Map<String, Object>>> lj1= new HashMap<>();
            Map<String,Map<String,List<Map<String, Object>>>> lj1= new HashMap<>();
            //Map<String,Map<String,Map<String,Map<String, Object>>>> ljresult = new HashMap<>();
            Map<String,Map<String,Map<String,List<Map<String, Object>>>>> ljresult = new HashMap<>();
            //double zds = 0,hgds = 0;
            for (String s : lj) {
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(s);
                List<Map<String, Object>> list1 = jjgFbgcLjgcHdgqdService.lookJdbjg(commonInfoVo);
                if (list1 !=null && !list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list2 = jjgFbgcLjgcHdjgccService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list3 = jjgFbgcLjgcLjbpService.lookJdbjg(commonInfoVo);
                if (list3 !=null && !list3.isEmpty()) {
                    for (Map<String, Object> map : list3) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list4 = jjgFbgcLjgcLjcjService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()) {
                    for (Map<String, Object> map : list4) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list5 = jjgFbgcLjgcLjtsfysdHtService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()) {
                    for (Map<String, Object> map : list5) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list6 = jjgFbgcLjgcLjwcService.lookJdbjg(commonInfoVo);
                if (list6 !=null && !list6.isEmpty()) {
                    for (Map<String, Object> map : list6) {
                        String z = map.get("检测单元数").toString();
                        String h = map.get("合格单元数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list7 = jjgFbgcLjgcLjwcLcfService.lookJdbjg(commonInfoVo);
                if (list7 !=null && !list7.isEmpty()) {
                    for (Map<String, Object> map : list7) {
                        String z = map.get("检测单元数").toString();
                        String h = map.get("合格单元数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list8 = jjgFbgcLjgcPsdmccService.lookJdbjg(commonInfoVo);
                if (list8 !=null && !list8.isEmpty()) {
                    for (Map<String, Object> map : list8) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list9 = jjgFbgcLjgcPspqhdService.lookJdbjg(commonInfoVo);
                if (list9 !=null && !list9.isEmpty()) {
                    for (Map<String, Object> map : list9) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list10 = jjgFbgcLjgcXqgqdService.lookJdbjg(commonInfoVo);
                if (list10 !=null && !list10.isEmpty()) {
                    for (Map<String, Object> map : list10) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list11 = jjgFbgcLjgcXqjgccService.lookJdbjg(commonInfoVo);
                if (list11 !=null && !list11.isEmpty()) {
                    for (Map<String, Object> map : list11) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list12 = jjgFbgcLjgcZddmccService.lookJdbjg(commonInfoVo);
                if (list12 !=null && !list12.isEmpty()) {
                    for (Map<String, Object> map : list12) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list13 = jjgFbgcLjgcZdgqdService.lookJdbjg(commonInfoVo);
                if (list13 !=null && !list13.isEmpty()) {
                    for (Map<String, Object> map : list13) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds += Double.valueOf(z);
                        hgds += Double.valueOf(h);
                    }
                }

                Map<String,Object> map1 = new HashMap<>();
                map1.put("zds",zds);
                map1.put("hgds",hgds);
                map1.put("hgl",zds!=0 ? df.format(hgds/zds*100) : 0);
                List<Map<String, Object>> temp = new ArrayList<>();
                temp.add(map1);
                ljmap.put(s,temp);
            }
            lj1.put("单位工程合格率",ljmap);
            ljresult.put("路基工程",lj1);
            //lj1.put("单位工程合格率",ljresult);
            result.putAll(ljresult);
        }

        if (lm !=null && !lm.isEmpty()){
            Map<String,List<Map<String, Object>>> lmmap = new HashMap<>();
            Map<String,Map<String,List<Map<String, Object>>>> lm1= new HashMap<>();
            Map<String,Map<String,Map<String,List<Map<String, Object>>>>> lmresult = new HashMap<>();

            for (String s : lm) {
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(s);
                /*List<Map<String, Object>> list1 = jjgFbgcLmgcGslqlmhdzxfService.lookJdbjg(commonInfoVo);
                if (!list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("上面层厚度检测点数").toString();
                        String h = map.get("上面层厚度合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }*/
                List<Map<String, Object>> list2 = jjgFbgcLmgcHntlmhdzxfService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list3 = jjgFbgcLmgcHntlmhdzxfService.lookJdbjg(commonInfoVo);
                if (list3 !=null &&!list3.isEmpty()){
                    for (Map<String, Object> map : list3) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list4 = jjgFbgcLmgcHntlmqdService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()){
                    for (Map<String, Object> map : list4) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list5 = jjgFbgcLmgcLmgzsdsgpsfService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()){
                    for (Map<String, Object> map : list5) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, String>> list6 = jjgFbgcLmgcLmhpService.lookJdbjg(commonInfoVo);
                if (list6 !=null && !list6.isEmpty()){
                    for (Map<String, String> map : list6) {
                        if (!map.isEmpty()){
                            String z = map.get("检测点数");
                            String h = map.get("合格点数");
                            zds+=Double.valueOf(z);
                            hgds+=Double.valueOf(h);
                        }

                    }
                }

                List<Map<String, Object>> list7 = jjgFbgcLmgcLmssxsService.lookJdbjg(commonInfoVo);
                if (list7 !=null && !list7.isEmpty()){
                    for (Map<String, Object> map : list7) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list8 = jjgFbgcLmgcLmwcService.lookJdbjg(commonInfoVo);
                if (list8 !=null && !list8.isEmpty()){
                    for (Map<String, Object> map : list8) {
                        String z = map.get("检测单元数").toString();
                        String h = map.get("合格单元数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list9 = jjgFbgcLmgcLmwcLcfService.lookJdbjg(commonInfoVo);
                if (list9 !=null && !list9.isEmpty()){
                    for (Map<String, Object> map : list9) {
                        String z = map.get("检测单元数").toString();
                        String h = map.get("合格单元数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list10 = jjgFbgcLmgcLqlmysdService.lookJdbjg(commonInfoVo);
                if (list10 !=null && !list10.isEmpty()){
                    for (Map<String, Object> map : list10) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }
                List<Map<String, Object>> list11 = jjgFbgcLmgcTlmxlbgcService.lookJdbjg(commonInfoVo);
                if (list11 !=null && !list11.isEmpty()){
                    for (Map<String, Object> map : list11) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                Map<String,Object> map1 = new HashMap<>();
                map1.put("zds",zds);
                map1.put("hgds",hgds);
                map1.put("hgl",zds!=0 ? df.format(hgds/zds*100) : 0);
                List<Map<String, Object>> temp = new ArrayList<>();
                temp.add(map1);
                lmmap.put(s,temp);
                //lmmap.put(s,map1);
            }
            lm1.put("单位工程合格率",lmmap);
            lmresult.put("路面工程",lm1);
            result.putAll(lmresult);
        }


        if (ja != null && !ja.isEmpty()){

            //Map<String,Map<String, Object>> jamap = new HashMap<>();
            Map<String,List<Map<String, Object>>> jamap = new HashMap<>();
            //Map<String,Map<String,Map<String, Object>>> ja1= new HashMap<>();
            Map<String,Map<String,List<Map<String, Object>>>> ja1= new HashMap<>();
            //Map<String,Map<String,Map<String,Map<String, Object>>>> jaresult = new HashMap<>();
            Map<String,Map<String,Map<String,List<Map<String, Object>>>>> jaresult = new HashMap<>();


            for (String s : ja) {
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(s);
                List<Map<String, Object>> list1 = jjgFbgcJtaqssJabxService.lookJdbjg(commonInfoVo);
                if (list1 !=null && !list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list2 = jjgFbgcJtaqssJabxfhlService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list3 = jjgFbgcJtaqssJabzService.lookJdbjg(commonInfoVo);
                if (list3 !=null && !list3.isEmpty()){
                    for (Map<String, Object> map : list3) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list4 = jjgFbgcJtaqssJathldmccService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()){
                    for (Map<String, Object> map : list4) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list5 = jjgFbgcJtaqssJathlqdService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()){
                    for (Map<String, Object> map : list5) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                Map<String,Object> map1 = new HashMap<>();
                map1.put("zds",zds);
                map1.put("hgds",hgds);
                map1.put("hgl",zds!=0 ? df.format(hgds/zds*100) : 0);
                List<Map<String, Object>> temp = new ArrayList<>();
                temp.add(map1);
                jamap.put(s,temp);
                //jalistmap.add(map1);
                //jamap.put(s,map1);
            }
            ja1.put("单位工程合格率",jamap);
            jaresult.put("交安工程",ja1);
            result.putAll(jaresult);

        }

        if (ql != null && !ql.isEmpty()){

            Map<String,List<Map<String, Object>>> qlmap = new HashMap<>();
            Map<String,Map<String,List<Map<String, Object>>>> ql1= new HashMap<>();
            Map<String,Map<String,Map<String,List<Map<String, Object>>>>> qlresult = new HashMap<>();


            for (String s : ql) {
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(s);
                List<Map<String, Object>> list1 = jjgFbgcQlgcXbTqdService.lookJdbjg(commonInfoVo);
                if (list1 !=null && !list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list2 = jjgFbgcQlgcXbJgccService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }


                List<Map<String, Object>> list4 = jjgFbgcQlgcXbBhchdService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()){
                    for (Map<String, Object> map : list4) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list5 = jjgFbgcQlgcXbSzdService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()){
                    for (Map<String, Object> map : list5) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list6 = jjgFbgcQlgcSbTqdService.lookJdbjg(commonInfoVo);
                if (list6 !=null && !list6.isEmpty()){
                    for (Map<String, Object> map : list6) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list7 = jjgFbgcQlgcSbJgccService.lookJdbjg(commonInfoVo);
                if (list7 !=null && !list7.isEmpty()){
                    for (Map<String, Object> map : list7) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list8 = jjgFbgcQlgcSbBhchdService.lookJdbjg(commonInfoVo);
                if (list8 !=null && !list8.isEmpty()){
                    for (Map<String, Object> map : list8) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list9 = jjgFbgcQlgcQmpzdService.lookJdbjg(commonInfoVo);
                if (list9 !=null && !list9.isEmpty()){
                    for (Map<String, Object> map : list9) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list10 = jjgFbgcQlgcQmhpService.lookJdbjg(commonInfoVo);
                if (list10 !=null && !list10.isEmpty()){
                    for (Map<String, Object> map : list10) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list11 = jjgFbgcQlgcQmgzsdService.lookJdbjg(commonInfoVo);
                if (list11 !=null && !list11.isEmpty()){
                    for (Map<String, Object> map : list11) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }



                Map<String,Object> map1 = new HashMap<>();
                map1.put("zds",zds);
                map1.put("hgds",hgds);
                map1.put("hgl",zds!=0 ? df.format(hgds/zds*100) : 0);
                List<Map<String, Object>> temp = new ArrayList<>();
                temp.add(map1);
                qlmap.put(s,temp);
            }
            ql1.put("单位工程合格率",qlmap);
            qlresult.put("桥梁工程",ql1);
            result.putAll(qlresult);

        }

        if (sd != null && !sd.isEmpty()){
            Map<String,List<Map<String, Object>>> sdmap = new HashMap<>();
            Map<String,Map<String,List<Map<String, Object>>>> sd1= new HashMap<>();
            Map<String,Map<String,Map<String,List<Map<String, Object>>>>> sdresult = new HashMap<>();


            for (String s : sd) {
                double zds = 0,hgds = 0;
                CommonInfoVo commonInfoVo = new CommonInfoVo();
                commonInfoVo.setProname(proname);
                commonInfoVo.setHtd(s);
                List<Map<String, Object>> list1 = jjgFbgcSdgcCqtqdService.lookJdbjg(commonInfoVo);
                if (list1 !=null && !list1.isEmpty()){
                    for (Map<String, Object> map : list1) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list2 = jjgFbgcSdgcDmpzdService.lookJdbjg(commonInfoVo);
                if (list2 !=null && !list2.isEmpty()){
                    for (Map<String, Object> map : list2) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list3 = jjgFbgcSdgcZtkdService.lookJdbjg(commonInfoVo);
                if (list3 !=null && !list3.isEmpty()){
                    for (Map<String, Object> map : list3) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list4 = jjgFbgcSdgcCqhdService.lookJdbjg(commonInfoVo);
                if (list4 !=null && !list4.isEmpty()){
                    for (Map<String, Object> map : list4) {
                        String z = map.get("检测总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list5 = jjgFbgcSdgcSdlqlmysdService.lookJdbjg(commonInfoVo);
                if (list5 !=null && !list5.isEmpty()){
                    for (Map<String, Object> map : list5) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list6 = jjgFbgcSdgcLmssxsService.lookJdbjg(commonInfoVo);
                if (list6 !=null && !list6.isEmpty()){
                    for (Map<String, Object> map : list6) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list7 = jjgFbgcSdgcHntlmqdService.lookJdbjg(commonInfoVo);
                if (list7 !=null && !list7.isEmpty()){
                    for (Map<String, Object> map : list7) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list8 = jjgFbgcSdgcTlmxlbgcService.lookJdbjg(commonInfoVo);
                if (list8 !=null && !list8.isEmpty()){
                    for (Map<String, Object> map : list8) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list9 = jjgFbgcSdgcSdhpService.lookJdbjg(commonInfoVo);
                if (list9 !=null && !list9.isEmpty()){
                    for (Map<String, Object> map : list9) {
                        String z = map.get("总点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list10 = jjgFbgcSdgcSdhntlmhdzxfService.lookJdbjg(commonInfoVo);
                if (list10 !=null && !list10.isEmpty()){
                    for (Map<String, Object> map : list10) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list11 = jjgFbgcSdgcGssdlqlmhdzxfService.lookJdbjg(commonInfoVo);
                if (list11 !=null && !list11.isEmpty()){
                    for (Map<String, Object> map : list11) {
                        String z = map.get("总厚度检测点数").toString();
                        String h = map.get("总厚度合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }

                List<Map<String, Object>> list12 = jjgFbgcSdgcLmgzsdsgpsfService.lookJdbjg(commonInfoVo);
                if (list12 !=null && !list12.isEmpty()){
                    for (Map<String, Object> map : list12) {
                        String z = map.get("检测点数").toString();
                        String h = map.get("合格点数").toString();
                        zds+=Double.valueOf(z);
                        hgds+=Double.valueOf(h);
                    }
                }


                Map<String,Object> map1 = new HashMap<>();
                map1.put("zds",zds);
                map1.put("hgds",hgds);
                map1.put("hgl",zds!=0 ? df.format(hgds/zds*100) : 0);

                List<Map<String, Object>> temp = new ArrayList<>();
                temp.add(map1);
                sdmap.put(s,temp);
            }
            sd1.put("单位工程合格率",sdmap);
            sdresult.put("隧道工程",sd1);
            result.putAll(sdresult);

        }

        return result;
    }


    /**
     *
     * @param destinationMap
     * @param sourceMap
     */
    private static void mergeIntoMap(Map<String, Map<String, Map<String, Object>>> destinationMap, Map<String, Map<String, Map<String, Object>>> sourceMap) {
        for (String key : sourceMap.keySet()) {
            Map<String, Map<String, Object>> innerMap = sourceMap.get(key);

            // 如果目标 Map 已经包含相同的 key，则合并 innerMap
            if (destinationMap.containsKey(key)) {
                Map<String, Map<String, Object>> destinationInnerMap = destinationMap.get(key);
                mergeInnerMaps(destinationInnerMap, innerMap);
            }
            // 否则，将 innerMap 添加到目标 Map
            else {
                destinationMap.put(key, innerMap);
            }
        }
    }

    /**
     *
     * @param destinationInnerMap
     * @param sourceInnerMap
     */
    private static void mergeInnerMaps(Map<String, Map<String, Object>> destinationInnerMap, Map<String, Map<String, Object>> sourceInnerMap) {
        for (String innerKey : sourceInnerMap.keySet()) {
            Map<String, Object> innerValue = sourceInnerMap.get(innerKey);

            // 如果目标 innerMap 已经包含相同的 innerKey，则合并 innerValue
            if (destinationInnerMap.containsKey(innerKey)) {
                Map<String, Object> destinationInnerValue = destinationInnerMap.get(innerKey);
                destinationInnerValue.putAll(innerValue);
            }
            // 否则，将 innerValue 添加到目标 innerMap
            else {
                destinationInnerMap.put(innerKey, innerValue);
            }
        }
    }

    /**
     *
     * @param proname
     * @return
     */
    private Map<String, Map<String, Map<String, Object>>> getja(String proname) {
        Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
        Map<String,Object> map = new HashMap();
        Map<String, Map<String, Object>> re = new HashMap<>();

        QueryWrapper<JjgFbgcJtaqssJabz> wrapper = new QueryWrapper<>();
        wrapper.eq("proname", proname);
        List<JjgFbgcJtaqssJabz> list = jjgFbgcJtaqssJabzService.list(wrapper);

        Map<String, List<JjgFbgcJtaqssJabz>> groupedData = list.stream()
                .collect(Collectors.groupingBy(JjgFbgcJtaqssJabz::getLzlx));
        groupedData.forEach((group, groupData) -> {
            map.put(group,groupData.size());

        });
        re.put("交安",map);
        data.put("建设项目",re);

        //按合同段
        Map<String, List<JjgFbgcJtaqssJabz>> grouphtd = list.stream()
                .collect(Collectors.groupingBy(JjgFbgcJtaqssJabz::getHtd));
        grouphtd.forEach((group, grouphtdData) -> {
            Map<String,Object> map1 = new HashMap();
            Map<String, Map<String, Object>> re1 = new HashMap<>();
            AtomicInteger dxb1 = new AtomicInteger();
            AtomicInteger sxb1 = new AtomicInteger();
            AtomicInteger dz1 = new AtomicInteger();
            AtomicInteger sz1 = new AtomicInteger();
            AtomicInteger mj1 = new AtomicInteger();
            Map<String, List<JjgFbgcJtaqssJabz>> grouplx = grouphtdData.stream()
                    .collect(Collectors.groupingBy(JjgFbgcJtaqssJabz::getLzlx));
            grouplx.forEach((group1, groupData) -> {
                map1.put(group1,groupData.size());
                /*if (group1.equals("单悬臂")){
                    dxb1.getAndIncrement();
                }else if (group1.equals("双悬臂")){
                    sxb1.getAndIncrement();
                }else if (group1.equals("单柱")){
                    dz1.getAndIncrement();
                }else if (group1.equals("双柱")){
                    sz1.getAndIncrement();
                }else if (group1.equals("门架")){
                    mj1.getAndIncrement();
                }*/
                re1.put("交安",map1);
                data.put(group,re1);
                    });
            /*map1.put("单悬臂",dxb1);
            map1.put("双悬臂",sxb1);
            map1.put("单柱",dz1);
            map1.put("双柱",sz1);
            map1.put("门架",mj1);*/

        });

        return data;
    }

    /**
     *
     * @param proname
     * @return
     */
    private Map<String, Map<String, Map<String, Object>>> getsdnum(String proname) {
        Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
        Map<String,Object> map = new HashMap();
        Map<String, Map<String, Object>> re = new HashMap<>();

        QueryWrapper<JjgLqsSd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname", proname);
        List<JjgLqsSd> list = jjgLqsSdService.list(wrapper);
        Set<String> uniqueNames = new HashSet<>();
        List<JjgLqsSd> deduplicatedList = new ArrayList<>();

        for (JjgLqsSd obj : list) {
            // 获取name属性的值
            String name = obj.getSdname();
            // 如果name是唯一的，则将其加入HashSet集合
            if (uniqueNames.add(name)) {
                // 将找到的对象加入新的列表
                deduplicatedList.add(obj);
            }
        }


        int tcsd = 0, csd = 0,zsd = 0, dsd = 0;
        for (JjgLqsSd jjgLqsSd : deduplicatedList) {
            String sdqc = jjgLqsSd.getSdqc();
            Double sdc = Double.valueOf(sdqc);
            if (sdc >= 3000){
                tcsd++;

            }else if (sdc < 3000 && sdc >= 1000){
                csd++;

            }else if (sdc < 1000 && sdc >= 500){
                zsd++;

            }else if (sdc < 500){
                dsd++;
            }
        }
        map.put("特长隧道",tcsd);
        map.put("长隧道",csd);
        map.put("中隧道",zsd);
        map.put("短隧道",dsd);
        re.put("隧道",map);
        data.put("建设项目",re);

        Map<String, List<JjgLqsSd>> groupedData = deduplicatedList.stream()
                .collect(Collectors.groupingBy(JjgLqsSd::getHtd));

        groupedData.forEach((group, groupData) -> {
            Map<String,Object> map1 = new HashMap();
            Map<String, Map<String, Object>> re1 = new HashMap<>();
            int tcsd1 = 0, csd1 = 0,zsd1 = 0, dsd1 = 0;
            for(JjgLqsSd jjgLqsSd : groupData) {
                String sdqc = jjgLqsSd.getSdqc();
                Double sdc = Double.valueOf(sdqc);
                if (sdc >= 3000){
                    tcsd1++;

                }else if (sdc < 3000 && sdc >= 1000){
                    csd1++;

                }else if (sdc < 1000 && sdc >= 500){
                    zsd1++;

                }else if (sdc < 500){
                    dsd1++;
                }
            }
            map1.put("特长隧道",tcsd1);
            map1.put("长隧道",csd1);
            map1.put("中隧道",zsd1);
            map1.put("短隧道",dsd1);
            re1.put("隧道",map1);
            data.put(group,re1);
        });
        return data;

    }

    /**
     *
     * @param proname
     * @return
     */
    private Map<String, Map<String, Map<String, Object>>> getqlnum(String proname) {
        Map<String, Map<String, Map<String, Object>>> data = new HashMap<>();
        Map<String,Object> map = new HashMap();
        Map<String, Map<String, Object>> re = new HashMap<>();



        QueryWrapper<JjgLqsQl> wrapper = new QueryWrapper<>();
        //wrapper.select("DISTINCT qlname").eq("proname", proname);
        wrapper.eq("proname", proname);
        List<JjgLqsQl> list = jjgLqsQlService.list(wrapper);

        Set<String> uniqueNames = new HashSet<>();
        List<JjgLqsQl> deduplicatedList = new ArrayList<>();

        for (JjgLqsQl obj : list) {
            // 获取name属性的值
            String name = obj.getQlname();
            // 如果name是唯一的，则将其加入HashSet集合
            if (uniqueNames.add(name)) {
                // 将找到的对象加入新的列表
                deduplicatedList.add(obj);
            }
        }

        int tdq = 0, dq = 0,zq = 0, xq = 0;
        for (JjgLqsQl jjgLqsQl : deduplicatedList) {
            String qlname = jjgLqsQl.getQlname();
            String dkkj = jjgLqsQl.getDkkj();
            if (dkkj == null || dkkj.equals("")){
                dkkj = "5";
            }
            Double kj = Double.valueOf(dkkj);
            if (kj >= 5 && kj < 20){
                xq++;
            }else if (kj >= 20 && kj < 40){
                zq++;
            }else if (kj >= 40 && kj <= 150){
                dq++;
            }else if (kj > 150){
                tdq++;
            }
        }
        map.put("特大桥", tdq);
        map.put("大桥", dq);
        map.put("中桥", zq);
        map.put("小桥", xq);
        re.put("桥",map);
        data.put("建设项目",re);

        Map<String, List<JjgLqsQl>> groupedData = deduplicatedList.stream()
                .collect(Collectors.groupingBy(JjgLqsQl::getHtd));
        groupedData.forEach((group, groupData) -> {
            Map<String,Object> map1 = new HashMap();

            Map<String, Map<String, Object>> re1 = new HashMap<>();
            AtomicInteger tdq1 = new AtomicInteger();
            AtomicInteger dq1 = new AtomicInteger();
            AtomicInteger zq1 = new AtomicInteger();
            AtomicInteger xq1 = new AtomicInteger();
            for(JjgLqsQl ql : groupData) {
                String dkkj = ql.getDkkj();
                if (dkkj == null || dkkj.equals("")){
                    dkkj = "5";
                }
                Double kj = Double.valueOf(dkkj);
                if (kj >= 5 && kj < 20){
                    xq1.getAndIncrement();
                }else if (kj >= 20 && kj < 40){
                    zq1.getAndIncrement();
                }else if (kj >= 40 && kj <= 150){
                    dq1.getAndIncrement();
                }else if (kj > 150){
                    tdq1.getAndIncrement();
                }
            }
            map1.put("特大桥", tdq1);
            map1.put("大桥", dq1);
            map1.put("中桥", zq1);
            map1.put("小桥", xq1);
            re1.put("桥",map1);
            data.put(group,re1);

        });

        return data;
    }


}