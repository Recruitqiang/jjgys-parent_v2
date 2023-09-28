package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgLqsJgSd;
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
public interface JjgLqsJgSdService extends IService<JjgLqsJgSd> {

    void exportSD(HttpServletResponse response, String projectname);

    void importSD(MultipartFile file, String proname);
}
