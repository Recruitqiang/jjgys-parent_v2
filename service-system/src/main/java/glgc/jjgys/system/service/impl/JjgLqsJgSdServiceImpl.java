package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgLqsJgSd;
import glgc.jjgys.model.project.JjgLqsSd;
import glgc.jjgys.model.projectvo.lqs.SdVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgLqsJgSdMapper;
import glgc.jjgys.system.service.JjgLqsJgSdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-09-22
 */
@Service
public class JjgLqsJgSdServiceImpl extends ServiceImpl<JjgLqsJgSdMapper, JjgLqsJgSd> implements JjgLqsJgSdService {

    @Autowired
    private JjgLqsJgSdMapper jjgLqsJgSdMapper;

    @Override
    public void exportSD(HttpServletResponse response, String projectname) {
        String fileName = projectname+"隧道清单";
        String sheetName = "隧道清单";
        ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName, new SdVo()).finish();
    }

    @Override
    public void importSD(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(SdVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<SdVo>(SdVo.class) {
                                @Override
                                public void handle(List<SdVo> dataList) {
                                    for(SdVo sd: dataList)
                                    {
                                        JjgLqsJgSd jjgLqsSd = new JjgLqsJgSd();
                                        BeanUtils.copyProperties(sd,jjgLqsSd);
                                        jjgLqsSd.setZhq(Double.valueOf(sd.getZhq()));
                                        jjgLqsSd.setZhz(Double.valueOf(sd.getZhz()));
                                        jjgLqsSd.setProname(proname);
                                        jjgLqsSd.setCreateTime(new Date());
                                        jjgLqsJgSdMapper.insert(jjgLqsSd);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }
}
