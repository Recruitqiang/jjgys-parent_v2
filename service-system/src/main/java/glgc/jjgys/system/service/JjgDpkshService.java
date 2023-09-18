package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgHtd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-03-01
 */
public interface JjgDpkshService extends IService<Object> {


    List<JjgHtd> gethtd(String proname);

    Map<String, Map<String, Map<String, Object>>> getnum(String proname);

    Map<String, Map<String, Map<String, List<Map<String, Object>>>>> getdwgc(String proname) throws IOException;

    Map<String, Map<String, Map<String, List<Map<String, Object>>>>>  gethtddata(String proname) throws IOException;

    Map<String, Map<String, List<Map<String, Object>>>> getjsxmdata(String proname) throws IOException;

}
