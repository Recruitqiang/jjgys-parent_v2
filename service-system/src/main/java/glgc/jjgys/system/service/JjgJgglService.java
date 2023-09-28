package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-03-01
 */
public interface JjgJgglService extends IService<Object> {

    void exportnew(HttpServletResponse response);

    void importnew(MultipartFile file, String projectname) throws IOException;

    void exportold(HttpServletResponse response);

    void importold(MultipartFile file, String projectname) throws IOException;

}
