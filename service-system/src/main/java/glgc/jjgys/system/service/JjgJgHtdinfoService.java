package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgJgHtdinfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-09-22
 */
public interface JjgJgHtdinfoService extends IService<JjgJgHtdinfo> {

    void addhtd(JjgJgHtdinfo jjgHtd);

}
