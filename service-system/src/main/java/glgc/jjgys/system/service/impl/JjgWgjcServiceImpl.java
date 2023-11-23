package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgWgjc;
import glgc.jjgys.model.project.JjgWgkf;
import glgc.jjgys.model.projectvo.jggl.JjgWgjcVo;
import glgc.jjgys.model.projectvo.jggl.JjgWgkfVo;
import glgc.jjgys.model.projectvo.sdgc.JjgFbgcSdgcZtkdVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgWgjcMapper;
import glgc.jjgys.system.service.JjgWgjcService;
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
 * @since 2023-11-19
 */
@Service
public class JjgWgjcServiceImpl extends ServiceImpl<JjgWgjcMapper, JjgWgjc> implements JjgWgjcService {

    @Autowired
    private JjgWgjcMapper jjgWgjcMapper;

    @Override
    public void export(HttpServletResponse response, String projectname) {
        String fileName = projectname+"外观检查";
        String sheetName = "外观检查";
        ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName, new JjgWgjcVo()).finish();

    }

    @Override
    public void importwgjc(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(JjgWgjcVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<JjgWgjcVo>(JjgWgjcVo.class) {
                                @Override
                                public void handle(List<JjgWgjcVo> dataList) {
                                    for(JjgWgjcVo ql: dataList)
                                    {
                                        JjgWgjc jjgLqsQl = new JjgWgjc();
                                        BeanUtils.copyProperties(ql,jjgLqsQl);
                                        jjgLqsQl.setProname(proname);
                                        jjgLqsQl.setCreateTime(new Date());
                                        jjgWgjcMapper.insert(jjgLqsQl);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }
}
