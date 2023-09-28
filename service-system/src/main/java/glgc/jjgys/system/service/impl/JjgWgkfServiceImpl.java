package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgDwgctze;
import glgc.jjgys.model.project.JjgWgkf;
import glgc.jjgys.model.projectvo.jggl.JjgDwgctzeVo;
import glgc.jjgys.model.projectvo.jggl.JjgWgkfVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgWgkfMapper;
import glgc.jjgys.system.service.JjgWgkfService;
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
public class JjgWgkfServiceImpl extends ServiceImpl<JjgWgkfMapper, JjgWgkf> implements JjgWgkfService {

    @Autowired
    private JjgWgkfMapper jjgWgkfMapper;

    @Override
    public void export(HttpServletResponse response) {
        String fileName = "外观扣分";
        String sheetName = "外观扣分";
        ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName, new JjgWgkfVo()).finish();

    }

    @Override
    public void importwgkf(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(JjgWgkfVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<JjgWgkfVo>(JjgWgkfVo.class) {
                                @Override
                                public void handle(List<JjgWgkfVo> dataList) {
                                    for(JjgWgkfVo ql: dataList)
                                    {
                                        JjgWgkf jjgLqsQl = new JjgWgkf();
                                        BeanUtils.copyProperties(ql,jjgLqsQl);
                                        jjgLqsQl.setProname(proname);
                                        jjgLqsQl.setCreateTime(new Date());
                                        jjgWgkfMapper.insert(jjgLqsQl);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }
}
