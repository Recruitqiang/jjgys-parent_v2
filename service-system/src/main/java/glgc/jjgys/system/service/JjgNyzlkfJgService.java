package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgNyzlkfJg;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-11-17
 */
public interface JjgNyzlkfJgService extends IService<JjgNyzlkfJg> {

    void export(HttpServletResponse response);

    void importnyzl(MultipartFile file, String proname);

}
