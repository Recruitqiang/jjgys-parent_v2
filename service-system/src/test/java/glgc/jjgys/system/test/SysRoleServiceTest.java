package glgc.jjgys.system.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.system.SysRole;
import glgc.jjgys.system.mapper.JjgFbgcJtaqssJabxMapper;
import glgc.jjgys.system.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.system.service.impl.JjgFbgcLjgcZdgqdServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class SysRoleServiceTest {

    @Autowired
    private JjgDpkshService jjgDpkshService;

    @Autowired
    private JjgFbgcLmgcLmhpService jjgFbgcLmgcLmhpService;

    @Autowired
    private JjgJgProjectinfoService jjgJgProjectinfoService;

    @Autowired
    private JjgFbgcGenerateTablelService jjgFbgcGenerateTablelService;



    @Test
    public void hdgqdsc() throws Exception {
        jjgFbgcGenerateTablelService.generateBGZBG("延黄高速");
        //Map<String, Map<String, List<Map<String, Object>>>> s = jjgDpkshService.getjsxmdata("陕西高速");


        //jjgJgProjectinfoService.getlqsData("陕西高速");
        /*CommonInfoVo commonInfoVo = new CommonInfoVo();
        commonInfoVo.setHtd("LJ-1");
        commonInfoVo.setProname("陕西高速");
        jjgFbgcLmgcLmhpService.generateJdb(commonInfoVo);*/
        //jjgDpkshService.getjsxmdata("陕西高速");
        //Map<String, Map<String, Map<String, List<Map<String, Object>>>>> ss = jjgDpkshService.getdwgc("陕西高速");
        //Map<String, Map<String, Map<String, List<Map<String, Object>>>>> s = jjgDpkshService.gethtddata("陕西高速");
        //System.out.println(s);
        //System.out.println(ss);
        //jjgDpkshService.getjsxmdata("陕西高速");
    }

    /**
     * {LJ-2=
     * {    合同段合格率={
     *      路面工程=[{zds=118.0, htd=LJ-1, lx=路面工程, hgds=105.0}],
     *      隧道工程=[{zds=409.0, htd=LJ-1, lx=隧道工程, hgds=386.0}],
     *      交安工程=[{zds=3455.0, htd=LJ-1, lx=交安工程, hgds=3350.0}],
     *      桥梁工程=[{zds=711.0, htd=LJ-1, lx=桥梁工程, hgds=685.0}],
     *      路基工程=[{zds=578.0, htd=LJ-1, lx=路基工程, hgds=522.0}]},
     *      合同段指标完成率={
     *      路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}},
     *
     *LJ-1={合同段合格率={路面工程=[{zds=118.0, htd=LJ-1, lx=路面工程, hgds=105.0}],
         * 隧道工程=[{zds=409.0, htd=LJ-1, lx=隧道工程, hgds=386.0}],
         * 交安工程=[{zds=3455.0, htd=LJ-1, lx=交安工程, hgds=3350.0}],
         * 桥梁工程=[{zds=711.0, htd=LJ-1, lx=桥梁工程, hgds=685.0}],
         * 路基工程=[{zds=578.0, htd=LJ-1, lx=路基工程, hgds=522.0}]},
     * 合同段指标完成率={路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}},
     *
     * LM-1={合同段指标完成率={路面工程=[{fbgcname=路面工程, zs=5.0, proname=陕西高速, jcs=0, htdname=LM-1}]}}}
     */


}
