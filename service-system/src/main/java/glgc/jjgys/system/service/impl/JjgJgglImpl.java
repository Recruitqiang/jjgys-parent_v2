package glgc.jjgys.system.service.impl;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.jggl.JCSJVo;
import glgc.jjgys.model.projectvo.jggl.JjgDwgctzeVo;
import glgc.jjgys.model.projectvo.jggl.JjgNyzlkfVo;
import glgc.jjgys.model.projectvo.jggl.JjgWgkfVo;
import glgc.jjgys.model.projectvo.lqs.*;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.*;
import glgc.jjgys.system.service.*;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.RowCopy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class JjgJgglImpl extends ServiceImpl<JjgJgglMapper, Object> implements JjgJgglService {

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
    private JjgDwgctzeMapper jjgDwgctzeMapper;

    @Autowired
    private JjgWgkfMapper jjgWgkfMapper;

    @Autowired
    private JjgNyzlkfMapper jjgNyzlkfMapper;


    @Override
    public void exportnew(HttpServletResponse response) {
        try {
            String fileName = "竣工路桥隧数据文件";
            String sheetName1 = "桥梁清单";
            String sheetName2 = "隧道清单";
            String sheetName3 = "连接线清单";
            String sheetName4 = "混凝土路面及匝道清单";
            String sheetName5 = "收费站清单";
            ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName1, new QlVo())
                    .write(null, sheetName2, new SdVo())
                    .write(null, sheetName3, new FhlmVo())
                    .write(null, sheetName4, new HntlmzdVo())
                    .write(null, sheetName5, new SfzVo())
                    .finish();
        } catch (Exception e) {
            throw new JjgysException(20001,"导出失败");
        }

    }

    @Override
    public void importnew(MultipartFile file, String projectname) throws IOException {
        ExcelReader excelReader = EasyExcel.read(file.getInputStream()).build();
        ReadSheet sheetql = EasyExcel.readSheet(0).head(QlVo.class).registerReadListener(new ExcelHandler<QlVo>(QlVo.class) {
            @Override
            public void handle(List<QlVo> dataList) {
                for(QlVo ql: dataList)
                {
                    JjgLqsJgQl jjgLqsQl = new JjgLqsJgQl();
                    BeanUtils.copyProperties(ql,jjgLqsQl);
                    jjgLqsQl.setZhq(Double.valueOf(ql.getZhq()));
                    jjgLqsQl.setZhz(Double.valueOf(ql.getZhz()));
                    jjgLqsQl.setProname(projectname);
                    jjgLqsQl.setCreateTime(new Date());
                    jjgLqsJgQlMapper.insert(jjgLqsQl);
                }
            }
        }).build();
        ReadSheet sheetsd = EasyExcel.readSheet(1).head(SdVo.class).registerReadListener(new ExcelHandler<SdVo>(SdVo.class) {
            @Override
            public void handle(List<SdVo> dataList) {
                for(SdVo sd: dataList)
                {
                    JjgLqsJgSd jjgLqsSd = new JjgLqsJgSd();
                    BeanUtils.copyProperties(sd,jjgLqsSd);
                    jjgLqsSd.setZhq(Double.valueOf(sd.getZhq()));
                    jjgLqsSd.setZhz(Double.valueOf(sd.getZhz()));
                    jjgLqsSd.setProname(projectname);
                    jjgLqsSd.setCreateTime(new Date());
                    jjgLqsJgSdMapper.insert(jjgLqsSd);
                }
            }
        }).build();

        ReadSheet sheethntlmzd = EasyExcel.readSheet(2).head(HntlmzdVo.class).registerReadListener(new ExcelHandler<HntlmzdVo>(HntlmzdVo.class) {
            @Override
            public void handle(List<HntlmzdVo> dataList) {
                for (HntlmzdVo hntlmzdVo : dataList) {
                    JjgLqsJgHntlmzd jjgLqsHntlmzd = new JjgLqsJgHntlmzd();
                    BeanUtils.copyProperties(hntlmzdVo, jjgLqsHntlmzd);
                    jjgLqsHntlmzd.setProname(projectname);
                    jjgLqsHntlmzd.setCreateTime(new Date());
                    jjgLqsJgHntlmzdMapper.insert(jjgLqsHntlmzd);
                }
            }
        }).build();
        ReadSheet sheetsfz = EasyExcel.readSheet(3).head(SfzVo.class).registerReadListener(new ExcelHandler<SfzVo>(SfzVo.class) {
            @Override
            public void handle(List<SfzVo> dataList) {
                for(SfzVo sfz: dataList)
                {
                    JjgLqsJgSfz jjgSfz = new JjgLqsJgSfz();
                    BeanUtils.copyProperties(sfz,jjgSfz);
                    jjgSfz.setProname(projectname);
                    jjgSfz.setCreateTime(new Date());
                    jjgLqsJgSfzMapper.insert(jjgSfz);
                }
            }
        }).build();
        ReadSheet sheetljx = EasyExcel.readSheet(4).head(LjxVo.class).registerReadListener(new ExcelHandler<LjxVo>(LjxVo.class) {
            @Override
            public void handle(List<LjxVo> dataList) {
                for(LjxVo ljx: dataList)
                {
                    JjgLqsJgLjx jjgLjx = new JjgLqsJgLjx();
                    BeanUtils.copyProperties(ljx,jjgLjx);
                    jjgLjx.setProname(projectname);
                    jjgLjx.setCreateTime(new Date());
                    jjgLqsJgLjxMapper.insert(jjgLjx);
                }
            }
        }).build();

        excelReader.read(sheetql,sheetsd,sheethntlmzd,sheetsfz,sheetljx);
        excelReader.finish();

    }

    @Override
    public void exportold(HttpServletResponse response) {
        try {
            String fileName = "竣工路桥隧数据文件";
            String sheetName1 = "单位工程投资额";
            String sheetName2 = "桥梁清单";
            String sheetName3 = "隧道清单";
            String sheetName4 = "连接线清单";
            String sheetName5 = "混凝土路面及匝道清单";
            String sheetName6 = "收费站清单";
            String sheetName7 = "外观扣分";
            String sheetName8 = "内页资料扣分";
            ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName1, new JjgDwgctzeVo())
                    .write(null, sheetName2, new QlVo())
                    .write(null, sheetName3, new SdVo())
                    .write(null, sheetName4, new LjxVo())
                    .write(null, sheetName5, new HntlmzdVo())
                    .write(null, sheetName6, new SfzVo())
                    .write(null, sheetName7, new JjgWgkfVo())
                    .write(null, sheetName8, new JjgNyzlkfVo())
                    .finish();
        } catch (Exception e) {
            throw new JjgysException(20001,"导出失败");
        }
    }

    @Override
    public void importold(MultipartFile file, String projectname) throws IOException {
        ExcelReader excelReader = EasyExcel.read(file.getInputStream()).build();
        ReadSheet sheettze = EasyExcel.readSheet(0).head(JjgDwgctzeVo.class).registerReadListener(new ExcelHandler<JjgDwgctzeVo>(JjgDwgctzeVo.class) {
            @Override
            public void handle(List<JjgDwgctzeVo> dataList) {
                for(JjgDwgctzeVo ql: dataList)
                {
                    JjgDwgctze jjgDwgctze = new JjgDwgctze();
                    BeanUtils.copyProperties(ql,jjgDwgctze);
                    jjgDwgctze.setProname(projectname);
                    jjgDwgctze.setCreateTime(new Date());
                    jjgDwgctzeMapper.insert(jjgDwgctze);
                }
            }
        }).build();
        ReadSheet sheetql = EasyExcel.readSheet(1).head(QlVo.class).registerReadListener(new ExcelHandler<QlVo>(QlVo.class) {
            @Override
            public void handle(List<QlVo> dataList) {
                for(QlVo ql: dataList)
                {
                    JjgLqsJgQl jjgLqsQl = new JjgLqsJgQl();
                    BeanUtils.copyProperties(ql,jjgLqsQl);
                    jjgLqsQl.setZhq(Double.valueOf(ql.getZhq()));
                    jjgLqsQl.setZhz(Double.valueOf(ql.getZhz()));
                    jjgLqsQl.setProname(projectname);
                    jjgLqsQl.setCreateTime(new Date());
                    jjgLqsJgQlMapper.insert(jjgLqsQl);
                }
            }
        }).build();
        ReadSheet sheetsd = EasyExcel.readSheet(2).head(SdVo.class).registerReadListener(new ExcelHandler<SdVo>(SdVo.class) {
            @Override
            public void handle(List<SdVo> dataList) {
                for(SdVo sd: dataList)
                {
                    JjgLqsJgSd jjgLqsSd = new JjgLqsJgSd();
                    BeanUtils.copyProperties(sd,jjgLqsSd);
                    jjgLqsSd.setZhq(Double.valueOf(sd.getZhq()));
                    jjgLqsSd.setZhz(Double.valueOf(sd.getZhz()));
                    jjgLqsSd.setProname(projectname);
                    jjgLqsSd.setCreateTime(new Date());
                    jjgLqsJgSdMapper.insert(jjgLqsSd);
                }
            }
        }).build();

        ReadSheet sheetljx = EasyExcel.readSheet(3).head(LjxVo.class).registerReadListener(new ExcelHandler<LjxVo>(LjxVo.class) {
            @Override
            public void handle(List<LjxVo> dataList) {
                for(LjxVo ljx: dataList)
                {
                    JjgLqsJgLjx jjgLjx = new JjgLqsJgLjx();
                    BeanUtils.copyProperties(ljx,jjgLjx);
                    jjgLjx.setProname(projectname);
                    jjgLjx.setCreateTime(new Date());
                    jjgLqsJgLjxMapper.insert(jjgLjx);
                }
            }
        }).build();

        ReadSheet sheethntlmzd = EasyExcel.readSheet(4).head(HntlmzdVo.class).registerReadListener(new ExcelHandler<HntlmzdVo>(HntlmzdVo.class) {
            @Override
            public void handle(List<HntlmzdVo> dataList) {
                for (HntlmzdVo hntlmzdVo : dataList) {
                    JjgLqsJgHntlmzd jjgLqsHntlmzd = new JjgLqsJgHntlmzd();
                    BeanUtils.copyProperties(hntlmzdVo, jjgLqsHntlmzd);
                    jjgLqsHntlmzd.setProname(projectname);
                    jjgLqsHntlmzd.setCreateTime(new Date());
                    jjgLqsJgHntlmzdMapper.insert(jjgLqsHntlmzd);
                }
            }
        }).build();
        ReadSheet sheetsfz = EasyExcel.readSheet(5).head(SfzVo.class).registerReadListener(new ExcelHandler<SfzVo>(SfzVo.class) {
            @Override
            public void handle(List<SfzVo> dataList) {
                for(SfzVo sfz: dataList)
                {
                    JjgLqsJgSfz jjgSfz = new JjgLqsJgSfz();
                    BeanUtils.copyProperties(sfz,jjgSfz);
                    jjgSfz.setProname(projectname);
                    jjgSfz.setCreateTime(new Date());
                    jjgLqsJgSfzMapper.insert(jjgSfz);
                }
            }
        }).build();


        ReadSheet sheetwg = EasyExcel.readSheet(6).head(JjgWgkfVo.class).registerReadListener(new ExcelHandler<JjgWgkfVo>(JjgWgkfVo.class) {
            @Override
            public void handle(List<JjgWgkfVo> dataList) {
                for(JjgWgkfVo ljx: dataList)
                {
                    JjgWgkf jjgLjx = new JjgWgkf();
                    BeanUtils.copyProperties(ljx,jjgLjx);
                    jjgLjx.setProname(projectname);
                    jjgLjx.setCreateTime(new Date());
                    jjgWgkfMapper.insert(jjgLjx);
                }
            }
        }).build();

        ReadSheet sheetnyzl = EasyExcel.readSheet(7).head(JjgNyzlkfVo.class).registerReadListener(new ExcelHandler<JjgNyzlkfVo>(JjgNyzlkfVo.class) {
            @Override
            public void handle(List<JjgNyzlkfVo> dataList) {
                for(JjgNyzlkfVo ljx: dataList)
                {
                    JjgNyzlkf jjgLjx = new JjgNyzlkf();
                    BeanUtils.copyProperties(ljx,jjgLjx);
                    jjgLjx.setProname(projectname);
                    jjgLjx.setCreateTime(new Date());
                    jjgNyzlkfMapper.insert(jjgLjx);
                }
            }
        }).build();

        excelReader.read(sheettze,sheetql,sheetsd,sheetljx,sheethntlmzd,sheetsfz,sheetwg,sheetnyzl);
        excelReader.finish();

    }


}
