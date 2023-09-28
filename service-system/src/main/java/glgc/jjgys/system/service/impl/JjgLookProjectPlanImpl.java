package glgc.jjgys.system.service.impl;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgHtd;
import glgc.jjgys.model.project.JjgPlaninfo;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.lqs.JjgPlaninfoVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgLookProjectPlanMapper;
import glgc.jjgys.system.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class JjgLookProjectPlanImpl extends ServiceImpl<JjgLookProjectPlanMapper, JjgPlaninfo> implements JjgLookProjectPlanService {


    @Autowired
    private JjgHtdService jjgHtdService;

    @Autowired
    private JjgFbgcLjgcHdgqdService jjgFbgcLjgcHdgqdService;

    @Autowired
    private JjgFbgcLjgcHdjgccService jjgFbgcLjgcHdjgccService;

    @Autowired
    private JjgFbgcLjgcLjtsfysdSlService jjgFbgcLjgcLjtsfysdSlService;

    @Autowired
    private JjgFbgcLjgcLjtsfysdHtService jjgFbgcLjgcLjtsfysdHtService;

    @Autowired
    private JjgFbgcLjgcLjwcService jjgFbgcLjgcLjwcService;

    @Autowired
    private JjgFbgcLjgcLjwcLcfService jjgFbgcLjgcLjwcLcfService;

    @Autowired
    private JjgFbgcLjgcLjcjService jjgFbgcLjgcLjcjService;

    @Autowired
    private JjgFbgcLjgcLjbpService jjgFbgcLjgcLjbpService;

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
    private JjgFbgcJtaqssJabxService jjgFbgcJtaqssJabxService;

    @Autowired
    private JjgFbgcJtaqssJabxfhlService jjgFbgcJtaqssJabxfhlService;

    @Autowired
    private JjgFbgcJtaqssJabzService jjgFbgcJtaqssJabzService;

    @Autowired
    private JjgFbgcJtaqssJathldmccService jjgFbgcJtaqssJathldmccService;

    @Autowired
    private JjgFbgcJtaqssJathlqdService jjgFbgcJtaqssJathlqdService;

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
    private JjgLookProjectPlanMapper jjgLookProjectPlanMapper;

    @Autowired
    private JjgZdhCzService jjgZdhCzService;

    @Autowired
    private JjgZdhPzdService jjgZdhPzdService;

    @Autowired
    private JjgZdhMcxsService jjgZdhMcxsService;

    @Autowired
    private JjgZdhGzsdService jjgZdhGzsdService;

    @Autowired
    private JjgFbgcQlgcSbTqdService jjgFbgcQlgcSbTqdService;

    @Autowired
    private JjgFbgcQlgcSbJgccService jjgFbgcQlgcSbJgccService;

    @Autowired
    private JjgFbgcQlgcSbBhchdService jjgFbgcQlgcSbBhchdService;

    @Autowired
    private JjgFbgcQlgcXbTqdService jjgFbgcQlgcXbTqdService;

    @Autowired
    private JjgFbgcQlgcXbJgccService jjgFbgcQlgcXbJgccService;

    @Autowired
    private JjgFbgcQlgcXbBhchdService jjgFbgcQlgcXbBhchdService;

    @Autowired
    private JjgFbgcQlgcXbSzdService jjgFbgcQlgcXbSzdService;

    @Autowired
    private JjgFbgcQlgcQmpzdService jjgFbgcQlgcQmpzdService;

    @Autowired
    private JjgFbgcQlgcQmhpService jjgFbgcQlgcQmhpService;

    @Autowired
    private JjgFbgcQlgcQmgzsdService jjgFbgcQlgcQmgzsdService;

    @Autowired
    private JjgFbgcSdgcCqhdService jjgFbgcSdgcCqhdService;

    @Autowired
    private JjgFbgcSdgcCqtqdService jjgFbgcSdgcCqtqdService;

    @Autowired
    private JjgFbgcSdgcDmpzdService jjgFbgcSdgcDmpzdService;

    @Autowired
    private JjgFbgcSdgcGssdlqlmhdzxfService jjgFbgcSdgcGssdlqlmhdzxfService;

    @Autowired
    private JjgFbgcSdgcHntlmqdService jjgFbgcSdgcHntlmqdService;

    @Autowired
    private JjgFbgcSdgcLmgzsdsgpsfService jjgFbgcSdgcLmgzsdsgpsfService;

    @Autowired
    private JjgFbgcSdgcLmssxsService jjgFbgcSdgcLmssxsService;

    @Autowired
    private JjgFbgcSdgcSdhntlmhdzxfService jjgFbgcSdgcSdhntlmhdzxfService;

    @Autowired
    private JjgFbgcSdgcSdhpService jjgFbgcSdgcSdhpService;

    @Autowired
    private JjgFbgcSdgcSdlqlmysdService jjgFbgcSdgcSdlqlmysdService;

    @Autowired
    private JjgFbgcSdgcTlmxlbgcService jjgFbgcSdgcTlmxlbgcService;

    @Autowired
    private JjgFbgcSdgcZtkdService jjgFbgcSdgcZtkdService;






    @Override
    public List<Map<String,Object>> lookplan(CommonInfoVo commonInfoVo) {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        DecimalFormat df = new DecimalFormat("0.00");
        //当前合同下的项目计划情况
        List<Map<String,Object>> planList = jjgLookProjectPlanMapper.selectplan(proname,htd);
        /**
         * [{fbgc=涵洞砼强度, num=40, htd=LJ-1, proname=陕西高速},
         * {fbgc=涵洞结构尺寸, num=40, htd=LJ-1, proname=陕西高速},
         * {fbgc=排水断面尺寸, num=50, htd=LJ-1, proname=陕西高速},
         * {fbgc=排水铺砌厚度, num=20, htd=LJ-1, proname=陕西高速}]
         */
        List<Map<String,Object>> resultmap = new ArrayList<>();
        for (Map<String, Object> map : planList) {
            String zb = map.get("zb").toString();
            if (zb.contains("涵洞砼强度")){
                int selectnum = jjgFbgcLjgcHdgqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("涵洞结构尺寸")){
                int selectnum = jjgFbgcLjgcHdjgccService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("路基土石方压实度")){
                int selectnum1 = jjgFbgcLjgcLjtsfysdSlService.selectnum(proname, htd);
                int selectnum2 = jjgFbgcLjgcLjtsfysdHtService.selectnum(proname, htd);
                int selectnum = selectnum1+selectnum2;
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            } else if (zb.contains("压实度沉降")){
                int selectnum = jjgFbgcLjgcLjcjService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("路基弯沉")){
                int selectnum1 = jjgFbgcLjgcLjwcService.selectnum(proname, htd);
                int selectnum2 = jjgFbgcLjgcLjwcLcfService.selectnum(proname, htd);
                int selectnum = selectnum1+selectnum2;
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            } else if (zb.contains("路基边坡")){
                int selectnum = jjgFbgcLjgcLjbpService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("排水断面尺寸")){
                int selectnum = jjgFbgcLjgcPsdmccService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("排水铺砌厚度")){
                int selectnum = jjgFbgcLjgcPspqhdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("小桥砼强度")){
                int selectnum = jjgFbgcLjgcXqgqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("小桥结构尺寸")){
                int selectnum = jjgFbgcLjgcXqjgccService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("支挡砼强度")){
                int selectnum = jjgFbgcLjgcZdgqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("支挡断面尺寸")){
                int selectnum = jjgFbgcLjgcZddmccService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            } else if (zb.contains("交安标线")){
                int selectnum = jjgFbgcJtaqssJabxService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("交安标志")){
                int selectnum = jjgFbgcJtaqssJabzService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("交安波形防护栏")){
                int selectnum = jjgFbgcJtaqssJabxfhlService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("交安砼护栏强度")){
                int selectnum = jjgFbgcJtaqssJathlqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("交安砼护栏断面尺寸")){
                int selectnum = jjgFbgcJtaqssJathldmccService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("沥青路面压实度")){
                int selectnum = jjgFbgcLmgcLqlmysdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("路面弯沉")){
                int selectnum1 = jjgFbgcLmgcLmwcService.selectnum(proname, htd);
                int selectnum2 = jjgFbgcLmgcLmwcLcfService.selectnum(proname, htd);
                int selectnum = selectnum1+selectnum2;
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("沥青路面车辙")){
                int selectnum = jjgZdhCzService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("沥青路面渗水系数")){
                int selectnum = jjgFbgcLmgcLmssxsService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("混凝土路面强度")) {
                int selectnum = jjgFbgcLmgcHntlmqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs", selectnum);
                map.put("jdl", num != 0 ? df.format(selectnum / num) : 0);
                resultmap.add(map);
            }else if (zb.contains("砼路面相邻板高差")){
                int selectnum = jjgFbgcLmgcTlmxlbgcService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("平整度")){
                int selectnum = jjgZdhPzdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("抗滑")){
                int selectnum1 = jjgZdhMcxsService.selectnum(proname, htd);
                int selectnum2 = jjgZdhGzsdService.selectnum(proname, htd);
                int selectnum3 = jjgFbgcLmgcLmgzsdsgpsfService.selectnum(proname, htd);
                int selectnum = selectnum1+selectnum2+selectnum3;
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            } else if (zb.contains("路面厚度")){
                int selectnum1 = jjgFbgcLmgcGslqlmhdzxfService.selectnum(proname, htd);
                int selectnum2 = jjgFbgcLmgcHntlmhdzxfService.selectnum(proname, htd);
                int selectnum = selectnum1+selectnum2;
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("路面横坡")){
                int selectnum = jjgFbgcLmgcLmhpService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥梁上部砼强度")){
                int selectnum = jjgFbgcQlgcSbTqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥梁上部主要结构尺寸")){
                int selectnum = jjgFbgcQlgcSbJgccService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥梁上部保护层厚度")){
                int selectnum = jjgFbgcQlgcSbBhchdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥梁下部墩台砼强度")){
                int selectnum = jjgFbgcQlgcXbTqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥梁下部主要结构尺寸")){
                int selectnum = jjgFbgcQlgcXbJgccService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥梁下部保护层厚度")){
                int selectnum = jjgFbgcQlgcXbBhchdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥梁下部竖直度实")){
                int selectnum = jjgFbgcQlgcXbSzdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥面平整度")){
                int selectnum = jjgFbgcQlgcQmpzdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥面横坡")){
                int selectnum = jjgFbgcQlgcQmhpService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("桥面构造深度")){
                int selectnum = jjgFbgcQlgcQmgzsdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道衬砌砼强度")){
                int selectnum = jjgFbgcSdgcCqtqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道衬砌厚度")){
                int selectnum = jjgFbgcSdgcCqhdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道大面平整度")){
                int selectnum = jjgFbgcSdgcDmpzdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道总体宽度")){
                int selectnum = jjgFbgcSdgcZtkdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道沥青路面压实度")){
                int selectnum = jjgFbgcSdgcSdlqlmysdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道沥青路面渗水系数")){
                int selectnum = jjgFbgcSdgcLmssxsService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道混凝土路面强度")){
                int selectnum = jjgFbgcSdgcHntlmqdService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道砼路面相邻板高差")){
                int selectnum = jjgFbgcSdgcTlmxlbgcService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道路面构造深度")){
                int selectnum = jjgFbgcSdgcLmgzsdsgpsfService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("高速隧道沥青路面厚度钻芯法")){
                int selectnum = jjgFbgcSdgcGssdlqlmhdzxfService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道混凝土路面厚度钻芯法")){
                int selectnum = jjgFbgcSdgcSdhntlmhdzxfService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }else if (zb.contains("隧道横坡")){
                int selectnum = jjgFbgcSdgcSdhpService.selectnum(proname, htd);
                Double num = Double.valueOf(map.get("num").toString());
                map.put("jcs",selectnum);
                map.put("jdl",num!=0?df.format(selectnum/num):0);
                resultmap.add(map);
            }
        }
        return resultmap;
    }

    @Override
    public void exportxmjd(HttpServletResponse response, String proname) {
        String fileName = proname+"项目指标计划清单";
        String sheetName = "项目指标计划清单";
        List<JjgPlaninfoVo> list = new ArrayList<>();

        /**
         * 先获取到项目下的所有合同段，然后判断这个合同段的类型
         * 根据类型不同，产生不同的指标
         */
        String[] ljlist = {"涵洞砼强度","涵洞结构尺寸","路基土石方压实度","压实度沉降","路基弯沉","路基边坡","排水断面尺寸","排水铺砌厚度","小桥砼强度","小桥结构尺寸","支挡砼强度","支挡断面尺寸"};
        String[] jalist = {"交安标线","交安标志","交安波形防护栏","交安砼护栏强度","交安砼护栏断面尺寸"};
        String[] lmlist = {"沥青路面压实度","沥青路面弯沉","沥青路面车辙","沥青路面渗水系数","砼路面强度","砼路面相邻板高差","平整度","抗滑", "路面厚度", "路面横坡"};
        String[] qllist = {"桥梁上部砼强度","桥梁上部主要结构尺寸","桥梁上部保护层厚度","桥梁下部墩台砼强度","桥梁下部主要结构尺寸","桥梁下部保护层厚度","桥梁下部竖直度实","桥面平整度","桥面横坡","桥面构造深度"};
        String[] sdlist = {"隧道衬砌砼强度","隧道衬砌厚度","隧道大面平整度","隧道总体宽度","隧道沥青路面压实度","隧道沥青路面渗水系数","隧道混凝土路面强度","隧道砼路面相邻板高差","隧道路面构造深度","高速隧道沥青路面厚度钻芯法","隧道混凝土路面厚度钻芯法","隧道横坡"};

        List<JjgHtd> gethtd = jjgHtdService.gethtd(proname);
        for (JjgHtd jjgHtd : gethtd) {
            String lx = jjgHtd.getLx();
            String htd = jjgHtd.getName();
            String[] arr = lx.split(",");
            for (String s : arr) {
                if (s.equals("路基工程")){
                    for (String s1 : ljlist) {
                        JjgPlaninfoVo jjgPlaninfoVo = new JjgPlaninfoVo();
                        jjgPlaninfoVo.setProname(proname);
                        jjgPlaninfoVo.setHtd(htd);
                        jjgPlaninfoVo.setFbgc(s);
                        jjgPlaninfoVo.setZb(s1);
                        list.add(jjgPlaninfoVo);
                    }
                }else if (s.equals("路面工程")){
                    for (String s1 : lmlist) {
                        JjgPlaninfoVo jjgPlaninfoVo = new JjgPlaninfoVo();
                        jjgPlaninfoVo.setProname(proname);
                        jjgPlaninfoVo.setHtd(htd);
                        jjgPlaninfoVo.setFbgc(s);
                        jjgPlaninfoVo.setZb(s1);
                        list.add(jjgPlaninfoVo);
                    }
                }else if (s.equals("交安工程")){
                    for (String s1 : jalist) {
                        JjgPlaninfoVo jjgPlaninfoVo = new JjgPlaninfoVo();
                        jjgPlaninfoVo.setProname(proname);
                        jjgPlaninfoVo.setHtd(htd);
                        jjgPlaninfoVo.setFbgc(s);
                        jjgPlaninfoVo.setZb(s1);
                        list.add(jjgPlaninfoVo);
                    }
                }else if (s.equals("桥梁工程")){
                    for (String s1 : qllist) {
                        JjgPlaninfoVo jjgPlaninfoVo = new JjgPlaninfoVo();
                        jjgPlaninfoVo.setProname(proname);
                        jjgPlaninfoVo.setHtd(htd);
                        jjgPlaninfoVo.setFbgc(s);
                        jjgPlaninfoVo.setZb(s1);
                        list.add(jjgPlaninfoVo);
                    }
                }else if (s.equals("隧道工程")){
                    for (String s1 : sdlist) {
                        JjgPlaninfoVo jjgPlaninfoVo = new JjgPlaninfoVo();
                        jjgPlaninfoVo.setProname(proname);
                        jjgPlaninfoVo.setHtd(htd);
                        jjgPlaninfoVo.setFbgc(s);
                        jjgPlaninfoVo.setZb(s1);
                        list.add(jjgPlaninfoVo);
                    }
                }
            }
        }
        ExcelUtil.writeExcelWithSheets(response, list, fileName, sheetName, new JjgPlaninfoVo()).finish();

    }

    @Override
    public void importxmjd(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(JjgPlaninfoVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<JjgPlaninfoVo>(JjgPlaninfoVo.class) {
                                @Override
                                public void handle(List<JjgPlaninfoVo> dataList) {
                                    for(JjgPlaninfoVo ql: dataList)
                                    {
                                        JjgPlaninfo jjgPlaninfo = new JjgPlaninfo();
                                        BeanUtils.copyProperties(ql,jjgPlaninfo);
                                        jjgPlaninfo.setCreateTime(new Date());
                                        System.out.println(jjgPlaninfo);
                                        jjgLookProjectPlanMapper.insert(jjgPlaninfo);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }
}
