package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgJgHtdinfo;
import glgc.jjgys.model.project.JjgLqsJgQl;
import glgc.jjgys.model.project.JjgLqsJgSd;
import glgc.jjgys.model.project.JjgWgkf;
import glgc.jjgys.model.projectvo.jggl.JCSJVo;
import glgc.jjgys.model.projectvo.jggl.JjgWgkfVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgJgHtdinfoMapper;
import glgc.jjgys.system.mapper.JjgLqsJgQlMapper;
import glgc.jjgys.system.mapper.JjgLqsJgSdMapper;
import glgc.jjgys.system.mapper.JjgWgkfMapper;
import glgc.jjgys.system.service.JjgWgkfService;
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
 * @since 2023-09-25
 */
@Service
public class JjgWgkfServiceImpl extends ServiceImpl<JjgWgkfMapper, JjgWgkf> implements JjgWgkfService {

    @Autowired
    private JjgWgkfMapper jjgWgkfMapper;

    @Autowired
    private JjgJgHtdinfoMapper jjgJgHtdinfoMapper;

    @Autowired
    private JjgLqsJgQlMapper jjgLqsJgQlMapper;

    @Autowired
    private JjgLqsJgSdMapper jjgLqsJgSdMapper;

    @Override
    public void export(HttpServletResponse response, String proname) {
        QueryWrapper<JjgJgHtdinfo> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgJgHtdinfo> htdinfo = jjgJgHtdinfoMapper.selectList(wrapper);
        List<JjgWgkfVo> result = new ArrayList<>();
        if (htdinfo != null && !htdinfo.isEmpty()){
            for (JjgJgHtdinfo jjgJgHtdinfo : htdinfo) {
                String htd = jjgJgHtdinfo.getName();
                String lx = jjgJgHtdinfo.getLx();
                if (lx.contains("路基工程")){
                    List<JjgWgkfVo> ljdata = getLjdata(htd);
                    result.addAll(ljdata);
                }
                if (lx.contains("路面工程")){
                    //List<JjgWgkfVo> lmdata = getLmdata(htd);
                    JjgWgkfVo jjgWgkfVo = new JjgWgkfVo();
                    jjgWgkfVo.setHtd(htd);
                    jjgWgkfVo.setDwgc("路面工程");
                    jjgWgkfVo.setFbgc("路面面层");
                    result.add(jjgWgkfVo);

                }
                if (lx.contains("桥梁工程")){
                    List<JjgWgkfVo> qldata = getQldata(proname,htd);
                    result.addAll(qldata);

                }
                if (lx.contains("隧道工程")){
                    List<JjgWgkfVo> sddata = getSddata(proname,htd);
                    result.addAll(sddata);

                }
                if (lx.contains("交安工程")){
                    List<JjgWgkfVo> jadata = getJadata(htd);
                    result.addAll(jadata);

                }
            }
        }
        //往excel中写入
        exportExcel(response,proname,result);

    }

    private void exportExcel(HttpServletResponse response, String proname, List<JjgWgkfVo> result) {
        try {
            String fileName = proname+"外观扣分";
            String sheetName = "外观扣分";
            ExcelUtil.writeExcelWithSheets(response, result, fileName, sheetName, new JjgWgkfVo()).finish();
        } catch (Exception e) {
            throw new JjgysException(20001,"导出失败");
        }
    }

    private List<JjgWgkfVo> getJadata(String htd) {
        String[] fbgc = {"标志", "标线", "防护栏"};
        List<JjgWgkfVo> result = new ArrayList<>();
        for (String f : fbgc) {
            JjgWgkfVo jgjcsj = new JjgWgkfVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("交通安全设施");
            jgjcsj.setFbgc(f);
            result.add(jgjcsj);
        }
        return result;

    }

    private List<JjgWgkfVo> getSddata(String proname, String htd) {
        String[] fbgc = {"衬砌", "总体", "隧道路面"};
        List<JjgWgkfVo> result = new ArrayList<>();
        QueryWrapper<JjgLqsJgSd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgLqsJgSd> htdinfo = jjgLqsJgSdMapper.selectList(wrapper);
        if (htdinfo!=null && !htdinfo.isEmpty()) {
            for (JjgLqsJgSd jjgLqsJgSd : htdinfo) {
                String sdname = jjgLqsJgSd.getSdname();
                for (String f : fbgc) {
                    JjgWgkfVo jgjcsj = new JjgWgkfVo();
                    jgjcsj.setHtd(htd);
                    jgjcsj.setDwgc(sdname);
                    jgjcsj.setFbgc(f);
                    result.add(jgjcsj);
                }
            }
        }
        return result;

    }

    private List<JjgWgkfVo> getQldata(String proname, String htd) {
        String[] fbgc = {"桥梁上部", "桥梁下部", "桥面系"};
        List<JjgWgkfVo> result = new ArrayList<>();

        QueryWrapper<JjgLqsJgQl> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgLqsJgQl> htdinfo = jjgLqsJgQlMapper.selectList(wrapper);
        if (htdinfo!=null && !htdinfo.isEmpty()){
            for (JjgLqsJgQl jjgLqsJgQl : htdinfo) {
                String qlname = jjgLqsJgQl.getQlname();
                for (String f : fbgc) {
                    JjgWgkfVo jgjcsj = new JjgWgkfVo();
                    jgjcsj.setHtd(htd);
                    jgjcsj.setDwgc(qlname);
                    jgjcsj.setFbgc(f);
                    result.add(jgjcsj);
                }
            }
        }
        return result;

    }

    /**
     *
     * @param htd
     * @return
     */
    private List<JjgWgkfVo> getLjdata(String htd){
        String[] fbgc = {"路基土石方", "排水工程", "小桥", "涵洞", "支挡工程"};
        List<JjgWgkfVo> result = new ArrayList<>();
        for (String f : fbgc) {
            JjgWgkfVo jgjcsj = new JjgWgkfVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("路基工程");
            jgjcsj.setFbgc(f);
            result.add(jgjcsj);
        }
        return result;

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
