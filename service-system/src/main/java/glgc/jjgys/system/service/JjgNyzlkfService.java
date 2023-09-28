package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgNyzlkf;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-09-25
 */
public interface JjgNyzlkfService extends IService<JjgNyzlkf> {

    void export(HttpServletResponse response);

    void importnyzl(MultipartFile file, String proname);

}
