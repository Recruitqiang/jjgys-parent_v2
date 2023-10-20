package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgHtd;
import glgc.jjgys.model.project.JjgJanum;
import glgc.jjgys.model.project.JjgPlaninfo;
import glgc.jjgys.model.projectvo.lqs.JjgJanumVo;
import glgc.jjgys.model.projectvo.lqs.JjgPlaninfoVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgJanumMapper;
import glgc.jjgys.system.service.JjgHtdService;
import glgc.jjgys.system.service.JjgJanumService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-10-21
 */
@Service
public class JjgJanumServiceImpl extends ServiceImpl<JjgJanumMapper, JjgJanum> implements JjgJanumService {

    @Autowired
    private JjgJanumMapper jjgJanumMapper;

    @Autowired
    private JjgHtdService jjgHtdService;


    @Override
    public void export(HttpServletResponse response, String proname) {
        String fileName = proname+"交安设施数量";
        String sheetName = "交安设施数量";
        List<JjgJanumVo> list = new ArrayList<>();
        String[] jalist = {"单旋","双旋","附着","门洞","单柱","双柱"};


        List<JjgHtd> gethtd = jjgHtdService.gethtd(proname);
        for (JjgHtd jjgHtd : gethtd) {
            String lx = jjgHtd.getLx();
            String htd = jjgHtd.getName();
            if (lx.contains("交安工程")){
                for (String s : jalist) {
                    JjgJanumVo janum = new JjgJanumVo();
                    janum.setProname(proname);
                    janum.setHtd(htd);
                    janum.setFbgc("交安工程");
                    janum.setZb(s);
                    list.add(janum);
                }
            }
        }
        ExcelUtil.writeExcelWithSheets(response, list, fileName, sheetName, new JjgJanumVo()).finish();


    }

    @Override
    public void importinfo(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(JjgJanumVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<JjgJanumVo>(JjgJanumVo.class) {
                                @Override
                                public void handle(List<JjgJanumVo> dataList) {
                                    for(JjgJanumVo ql: dataList)
                                    {
                                        JjgJanum jjgPlaninfo = new JjgJanum();
                                        BeanUtils.copyProperties(ql,jjgPlaninfo);
                                        jjgPlaninfo.setCreateTime(new Date());
                                        jjgJanumMapper.insert(jjgPlaninfo);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }
}
