package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgLqsJgSfz;
import glgc.jjgys.model.project.JjgSfz;
import glgc.jjgys.model.projectvo.lqs.SfzVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgLqsJgSfzMapper;
import glgc.jjgys.system.service.JjgLqsJgSfzService;
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
public class JjgLqsJgSfzServiceImpl extends ServiceImpl<JjgLqsJgSfzMapper, JjgLqsJgSfz> implements JjgLqsJgSfzService {

    @Autowired
    private JjgLqsJgSfzMapper jjgLqsJgSfzMapper;

    @Override
    public void exportSD(HttpServletResponse response) {
        String fileName = "收费站清单";
        String sheetName = "收费站清单";
        ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName, new SfzVo()).finish();

    }

    @Override
    public void importSD(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(SfzVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<SfzVo>(SfzVo.class) {
                                @Override
                                public void handle(List<SfzVo> dataList) {
                                    for(SfzVo sfz: dataList)
                                    {
                                        JjgLqsJgSfz jjgSfz = new JjgLqsJgSfz();
                                        BeanUtils.copyProperties(sfz,jjgSfz);
                                        jjgSfz.setProname(proname);
                                        jjgSfz.setCreateTime(new Date());
                                        jjgLqsJgSfzMapper.insert(jjgSfz);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }
}
