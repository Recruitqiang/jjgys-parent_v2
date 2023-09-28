package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgDwgctze;
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
public interface JjgDwgctzeService extends IService<JjgDwgctze> {

    void export(HttpServletResponse response);

    void importtze(MultipartFile file, String proname);

}
