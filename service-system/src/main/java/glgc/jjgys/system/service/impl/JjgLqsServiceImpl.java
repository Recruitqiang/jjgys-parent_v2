package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.lqs.*;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.*;
import glgc.jjgys.system.service.JjgHtdService;
import glgc.jjgys.system.service.JjgLqsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JjgLqsServiceImpl implements JjgLqsService {

    @Autowired
    private JjgHtdService jjgHtdService;

    @Autowired
    private JjgLqsQlMapper jjgLqsQlMapper;

    @Autowired
    private JjgLqsSdMapper jjgLqsSdMapper;

    @Autowired
    private JjgLqsFhlmMapper jjgLqsFhlmMapper;

    @Autowired
    private JjgLqsHntlmzdMapper jjgLqsHntlmzdMapper;

    @Autowired
    private JjgLqsSfzMapper jjgLqsSfzMapper;

    @Autowired
    private JjgLqsLjxMapper jjgLqsLjxMapper;

    @Autowired
    private JjgLqsLjxqlMapper jjgLqsLjxqlMapper;

    @Autowired
    private JjgLqsLjxsdMapper jjgLqsLjxsdMapper;

    @Autowired
    private JjgLqsLjxhntlmMapper jjgLqsLjxhntlmMapper;

    @Autowired
    private JjgJanumMapper jjgJanumMapper;

    @Autowired
    private JjgLookProjectPlanMapper jjgLookProjectPlanMapper;

    @Autowired
    private JjgLjfrnumMapper jjgLjfrnumMapper;

    //路桥隧文件导出
    @Override
    public void exportLqs(HttpServletResponse response,String projectname) {
        List<JjgPlaninfoVo> list = new ArrayList<>();

        /**
         * 先获取到项目下的所有合同段，然后判断这个合同段的类型
         * 根据类型不同，产生不同的指标
         */
        String[] ljlist = {"涵洞砼强度","涵洞结构尺寸","路基土石方压实度沙砾","路基土石方压实度灰土","压实度沉降","路基弯沉贝克曼梁法","路基弯沉落锤法","路基边坡","排水断面尺寸","排水铺砌厚度","小桥砼强度","小桥结构尺寸","支挡砼强度","支挡断面尺寸"};
        String[] jalist = {"交安标线","交安标志","交安波形防护栏","交安砼护栏强度","交安砼护栏断面尺寸"};
        String[] lmlist = {"高速沥青路面厚度钻芯法","混凝土路面厚度钻芯法","混凝土路面强度","路面构造深度手工铺砂法","路面横坡","沥青路面渗水系数","路面弯沉贝克曼梁法","路面弯沉落锤法","沥青路面压实度","砼路面相邻板高差"};

        List<JjgHtd> gethtd = jjgHtdService.gethtd(projectname);
        for (JjgHtd jjgHtd : gethtd) {
            String lx = jjgHtd.getLx();
            String htd = jjgHtd.getName();
            String[] arr = lx.split(",");
            for (String s : arr) {
                if (s.equals("路基工程")){
                    for (String s1 : ljlist) {
                        JjgPlaninfoVo jjgPlaninfoVo = new JjgPlaninfoVo();
                        jjgPlaninfoVo.setProname(projectname);
                        jjgPlaninfoVo.setHtd(htd);
                        jjgPlaninfoVo.setFbgc(s);
                        jjgPlaninfoVo.setZb(s1);
                        list.add(jjgPlaninfoVo);
                    }
                }else if (s.equals("路面工程")){
                    for (String s1 : lmlist) {
                        JjgPlaninfoVo jjgPlaninfoVo = new JjgPlaninfoVo();
                        jjgPlaninfoVo.setProname(projectname);
                        jjgPlaninfoVo.setHtd(htd);
                        jjgPlaninfoVo.setFbgc(s);
                        jjgPlaninfoVo.setZb(s1);
                        list.add(jjgPlaninfoVo);
                    }
                }else if (s.equals("交安工程")){
                    for (String s1 : jalist) {
                        JjgPlaninfoVo jjgPlaninfoVo = new JjgPlaninfoVo();
                        jjgPlaninfoVo.setProname(projectname);
                        jjgPlaninfoVo.setHtd(htd);
                        jjgPlaninfoVo.setFbgc(s);
                        jjgPlaninfoVo.setZb(s1);
                        list.add(jjgPlaninfoVo);
                    }
                }
            }
        }
        //交安的数据
        List<JjgJanumVo> listja = new ArrayList<>();
        String[] jasheet = {"单旋","双旋","附着","门洞","单柱","双柱"};

        for (JjgHtd jjgHtd : gethtd) {
            String lx = jjgHtd.getLx();
            String htd = jjgHtd.getName();
            if (lx.contains("交安工程")){
                for (String s : jasheet) {
                    JjgJanumVo janum = new JjgJanumVo();
                    janum.setProname(projectname);
                    janum.setHtd(htd);
                    janum.setFbgc("交安工程");
                    janum.setZb(s);
                    listja.add(janum);
                }
            }
        }
        //路基附容的数据
        List<JjgLjfrnumVo> listljfr = new ArrayList<>();
        String[] ljsheet = {"支挡","涵洞","通道","小桥"};
        for (JjgHtd jjgHtd : gethtd) {
            String lx = jjgHtd.getLx();
            String htd = jjgHtd.getName();
            if (lx.contains("路基工程")){
                for (String s : ljsheet) {
                    JjgLjfrnumVo janum = new JjgLjfrnumVo();
                    janum.setProname(projectname);
                    janum.setHtd(htd);
                    janum.setFbgc("路基工程");
                    janum.setZb(s);
                    listljfr.add(janum);
                }
            }
        }

        try {
            String fileName = projectname+"路桥隧数据文件";
            String sheetName1 = "桥梁清单";
            String sheetName2 = "隧道清单";
            String sheetName3 = "复合路面清单";
            String sheetName4 = "混凝土路面及匝道清单";
            String sheetName5 = "收费站清单";
            String sheetName6 = "连接线清单";
            /*String sheetName7 = "连接线桥梁清单";
            String sheetName8 = "连接线隧道清单";
            String sheetName9 = "连接线混凝土路面清单";*/
            String sheetName10 = "项目指标计划清单";
            String sheetName11 = "交安数量清单";
            String sheetName12 = "路基附容数量清单";
            ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName1, new QlVo())
                    .write(null, sheetName2, new SdVo())
                    .write(null, sheetName3, new FhlmVo())
                    .write(null, sheetName4, new HntlmzdVo())
                    .write(null, sheetName5, new SfzVo())
                    .write(null, sheetName6, new LjxVo())
                    /*.write(null, sheetName7, new LjxqlVo())
                    .write(null, sheetName8, new LjxsdVo())
                    .write(null, sheetName9, new LjxhntlmVo())*/
                    .write(list, sheetName10, new JjgPlaninfoVo())
                    .write(listja, sheetName11, new JjgJanumVo())
                    .write(listljfr, sheetName12, new JjgLjfrnumVo())
                    .finish();
        } catch (Exception e) {
            throw new JjgysException(20001,"导出失败");
        }

    }



    @Override
    public void importlqs(MultipartFile file, String projectname) throws IOException {
        ExcelReader excelReader = EasyExcel.read(file.getInputStream()).build();
        ReadSheet sheetql = EasyExcel.readSheet(0).head(QlVo.class).registerReadListener(new ExcelHandler<QlVo>(QlVo.class) {
            @Override
            public void handle(List<QlVo> dataList) {
                for(QlVo ql: dataList)
                {
                    JjgLqsQl jjgLqsQl = new JjgLqsQl();
                    BeanUtils.copyProperties(ql,jjgLqsQl);
                    jjgLqsQl.setZhq(Double.valueOf(ql.getZhq()));
                    jjgLqsQl.setZhz(Double.valueOf(ql.getZhz()));
                    jjgLqsQl.setProname(projectname);
                    jjgLqsQl.setCreateTime(new Date());
                    jjgLqsQlMapper.insert(jjgLqsQl);
                }
            }
        }).build();
        ReadSheet sheetsd = EasyExcel.readSheet(1).head(SdVo.class).registerReadListener(new ExcelHandler<SdVo>(SdVo.class) {
            @Override
            public void handle(List<SdVo> dataList) {
                for(SdVo sd: dataList)
                {
                    JjgLqsSd jjgLqsSd = new JjgLqsSd();
                    BeanUtils.copyProperties(sd,jjgLqsSd);
                    jjgLqsSd.setZhq(Double.valueOf(sd.getZhq()));
                    jjgLqsSd.setZhz(Double.valueOf(sd.getZhz()));
                    jjgLqsSd.setProname(projectname);
                    jjgLqsSd.setCreateTime(new Date());
                    jjgLqsSdMapper.insert(jjgLqsSd);
                }
            }
        }).build();
        ReadSheet sheetfhlm = EasyExcel.readSheet(2).head(FhlmVo.class).registerReadListener(new ExcelHandler<FhlmVo>(FhlmVo.class) {
            @Override
            public void handle(List<FhlmVo> dataList) {
                for(FhlmVo fhlm: dataList)
                {
                    JjgLqsFhlm jjgLqsFhlm = new JjgLqsFhlm();
                    BeanUtils.copyProperties(fhlm,jjgLqsFhlm);
                    jjgLqsFhlm.setZhq(Double.valueOf(fhlm.getZhq()));
                    jjgLqsFhlm.setZhz(Double.valueOf(fhlm.getZhz()));
                    jjgLqsFhlm.setProname(projectname);
                    jjgLqsFhlm.setCreateTime(new Date());
                    jjgLqsFhlmMapper.insert(jjgLqsFhlm);
                }
            }
        }).build();
        ReadSheet sheethntlmzd = EasyExcel.readSheet(3).head(HntlmzdVo.class).registerReadListener(new ExcelHandler<HntlmzdVo>(HntlmzdVo.class) {
            @Override
            public void handle(List<HntlmzdVo> dataList) {
                for (HntlmzdVo hntlmzdVo : dataList) {
                    JjgLqsHntlmzd jjgLqsHntlmzd = new JjgLqsHntlmzd();
                    BeanUtils.copyProperties(hntlmzdVo, jjgLqsHntlmzd);
                    jjgLqsHntlmzd.setProname(projectname);
                    jjgLqsHntlmzd.setCreateTime(new Date());
                    jjgLqsHntlmzdMapper.insert(jjgLqsHntlmzd);
                }
            }
        }).build();
        ReadSheet sheetsfz = EasyExcel.readSheet(4).head(SfzVo.class).registerReadListener(new ExcelHandler<SfzVo>(SfzVo.class) {
            @Override
            public void handle(List<SfzVo> dataList) {
                for(SfzVo sfz: dataList)
                {
                    JjgSfz jjgSfz = new JjgSfz();
                    BeanUtils.copyProperties(sfz,jjgSfz);
                    jjgSfz.setProname(projectname);
                    jjgSfz.setCreateTime(new Date());
                    jjgLqsSfzMapper.insert(jjgSfz);
                }
            }
        }).build();
        ReadSheet sheetljx = EasyExcel.readSheet(5).head(LjxVo.class).registerReadListener(new ExcelHandler<LjxVo>(LjxVo.class) {
            @Override
            public void handle(List<LjxVo> dataList) {
                for(LjxVo ljx: dataList)
                {
                    JjgLjx jjgLjx = new JjgLjx();
                    BeanUtils.copyProperties(ljx,jjgLjx);
                    jjgLjx.setProname(projectname);
                    jjgLjx.setCreateTime(new Date());
                    jjgLqsLjxMapper.insert(jjgLjx);
                }
            }
        }).build();
        ReadSheet sheetxmjh = EasyExcel.readSheet(6).head(JjgPlaninfoVo.class).registerReadListener(new ExcelHandler<JjgPlaninfoVo>(JjgPlaninfoVo.class) {
            @Override
            public void handle(List<JjgPlaninfoVo> dataList) {
                for(JjgPlaninfoVo jh: dataList)
                {
                    JjgPlaninfo jjgjh = new JjgPlaninfo();
                    BeanUtils.copyProperties(jh,jjgjh);
                    jjgjh.setProname(projectname);
                    jjgjh.setCreateTime(new Date());
                    jjgLookProjectPlanMapper.insert(jjgjh);
                }
            }
        }).build();

        ReadSheet sheetja = EasyExcel.readSheet(7).head(JjgJanumVo.class).registerReadListener(new ExcelHandler<JjgJanumVo>(JjgJanumVo.class) {
            @Override
            public void handle(List<JjgJanumVo> dataList) {
                for(JjgJanumVo ja: dataList)
                {
                    JjgJanum jjgja = new JjgJanum();
                    BeanUtils.copyProperties(ja,jjgja);
                    jjgja.setProname(projectname);
                    jjgja.setCreateTime(new Date());
                    jjgJanumMapper.insert(jjgja);
                }
            }
        }).build();

        ReadSheet sheetlj = EasyExcel.readSheet(8).head(JjgLjfrnumVo.class).registerReadListener(new ExcelHandler<JjgLjfrnumVo>(JjgLjfrnumVo.class) {
            @Override
            public void handle(List<JjgLjfrnumVo> dataList) {
                for(JjgLjfrnumVo lj: dataList)
                {
                    JjgLjfrnum jjgljfr = new JjgLjfrnum();
                    BeanUtils.copyProperties(lj,jjgljfr);
                    jjgljfr.setProname(projectname);
                    jjgljfr.setCreateTime(new Date());
                    jjgLjfrnumMapper.insert(jjgljfr);
                }
            }
        }).build();
        /*ReadSheet sheetljxql = EasyExcel.readSheet(6).head(LjxqlVo.class).registerReadListener(new ExcelHandler<LjxqlVo>(LjxqlVo.class) {
            @Override
            public void handle(List<LjxqlVo> dataList) {
                for(LjxqlVo ljxqlVo: dataList)
                {
                    JjgLjxQl jjgLjxQl = new JjgLjxQl();
                    BeanUtils.copyProperties(ljxqlVo,jjgLjxQl);
                    jjgLjxQl.setZhq(Double.valueOf(ljxqlVo.getZhq()));
                    jjgLjxQl.setZhz(Double.valueOf(ljxqlVo.getZhz()));
                    jjgLjxQl.setProname(projectname);
                    jjgLjxQl.setCreateTime(new Date());
                    jjgLqsLjxqlMapper.insert(jjgLjxQl);
                }
            }
        }).build();
        ReadSheet sheetljxsd = EasyExcel.readSheet(7).head(LjxsdVo.class).registerReadListener(new ExcelHandler<LjxsdVo>(LjxsdVo.class) {
            @Override
            public void handle(List<LjxsdVo> dataList) {
                for(LjxsdVo ljxsdVo: dataList)
                {
                    JjgLjxSd jjgLjxSd = new JjgLjxSd();
                    BeanUtils.copyProperties(ljxsdVo,jjgLjxSd);
                    jjgLjxSd.setZhq(Double.valueOf(ljxsdVo.getZhq()));
                    jjgLjxSd.setZhz(Double.valueOf(ljxsdVo.getZhz()));
                    jjgLjxSd.setProname(projectname);
                    jjgLjxSd.setCreateTime(new Date());
                    jjgLqsLjxsdMapper.insert(jjgLjxSd);
                }
            }
        }).build();
        ReadSheet sheetljxhntlm = EasyExcel.readSheet(8).head(LjxhntlmVo.class).registerReadListener(new ExcelHandler<LjxhntlmVo>(LjxhntlmVo.class) {
            @Override
            public void handle(List<LjxhntlmVo> dataList) {
                for(LjxhntlmVo ljxhntlmVo: dataList)
                {
                    JjgLjxhntlm jjgLjxhntlm = new JjgLjxhntlm();
                    BeanUtils.copyProperties(ljxhntlmVo,jjgLjxhntlm);
                    jjgLjxhntlm.setZhq(Double.valueOf(ljxhntlmVo.getZhq()));
                    jjgLjxhntlm.setZhz(Double.valueOf(ljxhntlmVo.getZhz()));
                    jjgLjxhntlm.setProname(projectname);
                    jjgLjxhntlm.setCreateTime(new Date());
                    jjgLqsLjxhntlmMapper.insert(jjgLjxhntlm);
                }
            }
        }).build();*/
        excelReader.read(sheetql,sheetsd,sheetfhlm,sheethntlmzd,sheetsfz,sheetljx,sheetxmjh,sheetja,sheetlj);
        excelReader.finish();

    }

}
