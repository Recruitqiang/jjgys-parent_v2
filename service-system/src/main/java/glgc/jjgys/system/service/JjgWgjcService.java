package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgWgjc;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-11-19
 */
public interface JjgWgjcService extends IService<JjgWgjc> {

    void export(HttpServletResponse response, String projectname);

    void importwgjc(MultipartFile file, String proname);

}
