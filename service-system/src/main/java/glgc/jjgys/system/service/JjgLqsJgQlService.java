package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgLqsJgQl;
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
public interface JjgLqsJgQlService extends IService<JjgLqsJgQl> {

    void exportQL(HttpServletResponse response, String projectname);

    void importQL(MultipartFile file, String proname);
}
