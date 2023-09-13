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



    @Test
    public void hdgqdsc() throws IOException {
        /*List<JjgHtd> s = jjgDpkshService.gethtd("陕西高速");
        System.out.println(s);*/
        jjgDpkshService.getjsxmdata("陕西高速");
    }


}
