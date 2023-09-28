package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgLqsJgSfz;
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
public interface JjgLqsJgSfzService extends IService<JjgLqsJgSfz> {

    void exportSD(HttpServletResponse response);

    void importSD(MultipartFile file, String proname);
}
