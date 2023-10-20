package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgLjfrnum;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-10-21
 */
public interface JjgLjfrnumService extends IService<JjgLjfrnum> {

    void export(HttpServletResponse response, String proname);

    void importlj(MultipartFile file, String proname);

}
