package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgLqsJgLjx;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-09-22
 */
public interface JjgLqsJgLjxService extends IService<JjgLqsJgLjx> {

    void export(HttpServletResponse response);

    void importLjx(MultipartFile file, String proname);
}
