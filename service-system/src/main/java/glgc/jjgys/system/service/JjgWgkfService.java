package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgWgkf;
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
public interface JjgWgkfService extends IService<JjgWgkf> {

    void export(HttpServletResponse response);

    void importwgkf(MultipartFile file, String proname);

}
