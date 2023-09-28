package glgc.jjgys.system.service;

import glgc.jjgys.model.project.JjgJgProjectinfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-09-20
 */
public interface JjgJgProjectinfoService extends IService<JjgJgProjectinfo> {

    void getlqsData(String proname);

    void deleteInfo(List<String> idList);

}
