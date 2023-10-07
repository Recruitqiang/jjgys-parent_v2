package glgc.jjgys.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.system.Project;
import glgc.jjgys.system.mapper.JjgJgHtdinfoMapper;
import glgc.jjgys.system.mapper.JjgJgProjectinfoMapper;
import glgc.jjgys.system.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-09-20
 */
@Slf4j
@Service
@Transactional
public class JjgJgProjectinfoServiceImpl extends ServiceImpl<JjgJgProjectinfoMapper, JjgJgProjectinfo> implements JjgJgProjectinfoService {

    @Autowired
    private JjgHtdService jjgHtdService;

    @Autowired
    private JjgLqsQlService jjgLqsQlService;

    @Autowired
    private JjgLqsSdService jjgLqsSdService;

    @Autowired
    private JjgLqsLjxService jjgLqsLjxService;

    @Autowired
    private JjgLqsHntlmzdService jjgLqsHntlmzdService;

    @Autowired
    private JjgLqsSfzService jjgLqsSfzService;

    @Autowired
    private JjgJgHtdinfoService jjgJgHtdinfoService;

    @Autowired
    private JjgLqsJgQlService jjgLqsJgQlService;

    @Autowired
    private JjgLqsJgSdService jjgLqsJgSdService;

    @Autowired
    private JjgLqsJgLjxService jjgLqsJgLjxService;

    @Autowired
    private JjgLqsJgHntlmzdService jjgLqsJgHntlmzdService;

    @Autowired
    private JjgLqsJgSfzService jjgLqsJgSfzService;

    @Autowired
    private JjgJgProjectinfoMapper jjgJgProjectinfoMapper;

    @Autowired
    private JjgZdhCzJgfcService jjgZdhCzJgfcService;

    @Autowired
    private JjgFbgcLmgcTlmxlbgcJgfcService jjgFbgcLmgcTlmxlbgcJgfcService;

    @Autowired
    private JjgZdhMcxsJgfcService jjgZdhMcxsJgfcService;

    @Autowired
    private JjgFbgcLmgcLmgzsdsgpsfJgfcService jjgFbgcLmgcLmgzsdsgpsfJgfcService;

    @Autowired
    private JjgZdhGzsdJgfcService jjgZdhGzsdJgfcService;

    @Autowired
    private JjgZdhPzdJgfcService jjgZdhPzdJgfcService;

    @Autowired
    private JjgJgjcsjService jjgJgjcsjService;



    @Override
    public void getlqsData(String proname) {
        //合同段
        QueryWrapper<JjgHtd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgHtd> list = jjgHtdService.list(wrapper);
        for (JjgHtd jjgHtd : list) {
            JjgJgHtdinfo htdinfo = new JjgJgHtdinfo();
            BeanUtils.copyProperties(jjgHtd, htdinfo);
            jjgJgHtdinfoService.save(htdinfo);
        }
        //桥梁
        QueryWrapper<JjgLqsQl> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("proname",proname);
        List<JjgLqsQl> list2 = jjgLqsQlService.list(wrapper2);
        for (JjgLqsQl jjgLqsQl : list2) {
            JjgLqsJgQl jjgLqsJgQl = new JjgLqsJgQl();
            BeanUtils.copyProperties(jjgLqsQl, jjgLqsJgQl);
            jjgLqsJgQlService.save(jjgLqsJgQl);
        }
        //隧道
        QueryWrapper<JjgLqsSd> wrapper3 = new QueryWrapper<>();
        wrapper3.eq("proname",proname);
        List<JjgLqsSd> list3 = jjgLqsSdService.list(wrapper3);
        for (JjgLqsSd jjgLqsSd : list3) {
            JjgLqsJgSd jjgLqsJgSd = new JjgLqsJgSd();
            BeanUtils.copyProperties(jjgLqsSd, jjgLqsJgSd);
            jjgLqsJgSdService.save(jjgLqsJgSd);
        }
        //连接线
        QueryWrapper<JjgLjx> wrapper4 = new QueryWrapper<>();
        wrapper4.eq("proname",proname);
        List<JjgLjx> list4 = jjgLqsLjxService.list(wrapper4);
        for (JjgLjx jjgLjx : list4) {
            JjgLqsJgLjx jjgLqsJgLjx = new JjgLqsJgLjx();
            BeanUtils.copyProperties(jjgLjx, jjgLqsJgLjx);
            jjgLqsJgLjxService.save(jjgLqsJgLjx);
        }

        //混凝土路面及匝道清单
        QueryWrapper<JjgLqsHntlmzd> wrapper5 = new QueryWrapper<>();
        wrapper5.eq("proname",proname);
        List<JjgLqsHntlmzd> list5 = jjgLqsHntlmzdService.list(wrapper5);
        for (JjgLqsHntlmzd jjgLqsHntlmzd : list5) {
            JjgLqsJgHntlmzd jjgLqsJgHntlmzd = new JjgLqsJgHntlmzd();
            BeanUtils.copyProperties(jjgLqsHntlmzd, jjgLqsJgHntlmzd);
            jjgLqsJgHntlmzdService.save(jjgLqsJgHntlmzd);
        }

        //收费站清单
        QueryWrapper<JjgSfz> wrapper6 = new QueryWrapper<>();
        wrapper6.eq("proname",proname);
        List<JjgSfz> list6 = jjgLqsSfzService.list(wrapper6);
        for (JjgSfz jjgSfz : list6) {
            JjgLqsJgSfz jjgLqsJgSfz = new JjgLqsJgSfz();
            BeanUtils.copyProperties(jjgSfz, jjgLqsJgSfz);
            jjgLqsJgSfzService.save(jjgLqsJgSfz);
        }

    }

    @Override
    @Transactional
    public void deleteInfo(List<String> idList) {
        for (String id : idList) {
            QueryWrapper<JjgJgProjectinfo> wrapper=new QueryWrapper<>();
            wrapper.eq("id",id);
            JjgJgProjectinfo project = jjgJgProjectinfoMapper.selectOne(wrapper);
            String proname = project.getProname();

            log.info("删除{}竣工合同段信息",proname);
            QueryWrapper<JjgJgHtdinfo> wrapperhtd = new QueryWrapper<>();
            wrapperhtd.eq("proname",proname);
            jjgJgHtdinfoService.remove(wrapperhtd);

            log.info("删除{}竣工桥梁清单信息",proname);
            QueryWrapper<JjgLqsJgQl> wrapperql = new QueryWrapper<>();
            wrapperql.eq("proname",proname);
            jjgLqsJgQlService.remove(wrapperql);

            log.info("删除{}竣工隧道清单信息",proname);
            QueryWrapper<JjgLqsJgSd> wrappersd = new QueryWrapper<>();
            wrappersd.eq("proname",proname);
            jjgLqsJgSdService.remove(wrappersd);

            log.info("删除{}竣工连接线清单信息",proname);
            QueryWrapper<JjgLqsJgLjx> wrapperljx = new QueryWrapper<>();
            wrapperljx.eq("proname",proname);
            jjgLqsJgLjxService.remove(wrapperljx);

            log.info("删除{}竣工混凝土路面及匝道清单信息",proname);
            QueryWrapper<JjgLqsJgHntlmzd> wrapperzd = new QueryWrapper<>();
            wrapperzd.eq("proname",proname);
            jjgLqsJgHntlmzdService.remove(wrapperzd);

            log.info("删除{}竣工收费站清单信息",proname);
            QueryWrapper<JjgLqsJgSfz> wrappersfz = new QueryWrapper<>();
            wrappersfz.eq("proname",proname);
            jjgLqsJgSfzService.remove(wrappersfz);

            log.info("删除{}竣工车辙信息",proname);
            QueryWrapper<JjgZdhCzJgfc> wrappercz = new QueryWrapper<>();
            wrappercz.eq("proname",proname);
            jjgZdhCzJgfcService.remove(wrappercz);

            log.info("删除{}竣工混凝土路面相邻板高差信息",proname);
            QueryWrapper<JjgFbgcLmgcTlmxlbgcJgfc> wrappergs = new QueryWrapper<>();
            wrappergs.eq("proname",proname);
            jjgFbgcLmgcTlmxlbgcJgfcService.remove(wrappergs);

            log.info("删除{}竣工摩擦系数信息",proname);
            QueryWrapper<JjgZdhMcxsJgfc> wrappermcxs = new QueryWrapper<>();
            wrappermcxs.eq("proname",proname);
            jjgZdhMcxsJgfcService.remove(wrappermcxs);

            log.info("删除{}竣工构造深度（手工铺沙法）信息",proname);
            QueryWrapper<JjgFbgcLmgcLmgzsdsgpsfJgfc> wrapperg = new QueryWrapper<>();
            wrapperg.eq("proname",proname);
            jjgFbgcLmgcLmgzsdsgpsfJgfcService.remove(wrapperg);

            log.info("删除{}竣工构造深度（机器检测法）信息",proname);
            QueryWrapper<JjgZdhGzsdJgfc> wrappergj = new QueryWrapper<>();
            wrappergj.eq("proname",proname);
            jjgZdhGzsdJgfcService.remove(wrappergj);

            log.info("删除{}竣工平整度信息",proname);
            QueryWrapper<JjgZdhPzdJgfc> wrapperpzd = new QueryWrapper<>();
            wrapperpzd.eq("proname",proname);
            jjgZdhPzdJgfcService.remove(wrapperpzd);

            log.info("删除{}竣工交工检测数据信息",proname);
            QueryWrapper<JjgJgjcsj> wrapperjgjc = new QueryWrapper<>();
            wrapperjgjc.eq("proname",proname);
            jjgJgjcsjService.remove(wrapperjgjc);

            log.info("删除{}竣工项目信息",proname);
            QueryWrapper<JjgJgProjectinfo> wrapperxm = new QueryWrapper<>();
            wrapperxm.eq("proname",proname);
            jjgJgProjectinfoMapper.delete(wrapperxm);


        }
    }
}
