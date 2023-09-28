package glgc.jjgys.system.service.impl;

import glgc.jjgys.model.project.JjgJgHtdinfo;
import glgc.jjgys.system.mapper.JjgJgHtdinfoMapper;
import glgc.jjgys.system.service.JjgJgHtdinfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class JjgJgHtdinfoServiceImpl extends ServiceImpl<JjgJgHtdinfoMapper, JjgJgHtdinfo> implements JjgJgHtdinfoService {

    @Autowired
    private JjgJgHtdinfoMapper jjgJgHtdinfoMapper;

    @Override
    public void addhtd(JjgJgHtdinfo jjgHtd) {
        List<String> lx = handle(jjgHtd.getLx());
        String join = StringUtils.join(lx, ",");
        jjgHtd.setLx(join);
        jjgJgHtdinfoMapper.insert(jjgHtd);
    }

    /**
     * 处理多合同段类型前端传到后端的对象，转换成字符串存
     * @param a
     * @return
     */
    public static List<String> handle(String a){
        String a1 = StringUtils.strip(a.toString(), "[]");
        String[] split = a1.split(",");
        List<String> list = new ArrayList<>();
        for (String s : split) {
            String str= s.replace("\"", "");
            list.add(str);
            System.out.println(str);
        }
        return list;
    }

}
