package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgLjx;
import glgc.jjgys.model.project.JjgLqsJgLjx;
import glgc.jjgys.model.projectvo.lqs.LjxVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgLqsJgLjxMapper;
import glgc.jjgys.system.service.JjgLqsJgLjxService;
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
public class JjgLqsJgLjxServiceImpl extends ServiceImpl<JjgLqsJgLjxMapper, JjgLqsJgLjx> implements JjgLqsJgLjxService {

    @Autowired
    private JjgLqsJgLjxMapper jjgLqsJgLjxMapper;

    @Override
    public void export(HttpServletResponse response) {
        String fileName = "连接线清单";
        String sheetName = "连接线清单";
        ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName, new LjxVo()).finish();
    }

    @Override
    public void importLjx(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(LjxVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<LjxVo>(LjxVo.class) {
                                @Override
                                public void handle(List<LjxVo> dataList) {
                                    for(LjxVo ljx: dataList)
                                    {
                                        JjgLqsJgLjx jjgLjx = new JjgLqsJgLjx();
                                        BeanUtils.copyProperties(ljx,jjgLjx);
                                        jjgLjx.setProname(proname);
                                        jjgLjx.setCreateTime(new Date());
                                        jjgLqsJgLjxMapper.insert(jjgLjx);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }


    }
}
