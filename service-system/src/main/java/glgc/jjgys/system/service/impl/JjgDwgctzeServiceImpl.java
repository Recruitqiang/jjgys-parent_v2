package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgDwgctze;
import glgc.jjgys.model.projectvo.jggl.JjgDwgctzeVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgDwgctzeMapper;
import glgc.jjgys.system.service.JjgDwgctzeService;
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
 * @since 2023-09-25
 */
@Service
public class JjgDwgctzeServiceImpl extends ServiceImpl<JjgDwgctzeMapper, JjgDwgctze> implements JjgDwgctzeService {

    @Autowired
    private JjgDwgctzeMapper jjgDwgctzeMapper;

    @Override
    public void export(HttpServletResponse response) {
        String fileName = "单位工程投资额清单";
        String sheetName = "单位工程投资额";
        ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName, new JjgDwgctzeVo()).finish();

    }

    @Override
    public void importtze(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(JjgDwgctzeVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<JjgDwgctzeVo>(JjgDwgctzeVo.class) {
                                @Override
                                public void handle(List<JjgDwgctzeVo> dataList) {
                                    for(JjgDwgctzeVo ql: dataList)
                                    {
                                        JjgDwgctze jjgLqsQl = new JjgDwgctze();
                                        BeanUtils.copyProperties(ql,jjgLqsQl);
                                        jjgLqsQl.setProname(proname);
                                        jjgLqsQl.setCreateTime(new Date());
                                        jjgDwgctzeMapper.insert(jjgLqsQl);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }
}
