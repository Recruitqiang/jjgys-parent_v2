package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgZdhCzJgfc;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wq
 * @since 2023-09-23
 */
public interface JjgZdhCzJgfcService extends IService<JjgZdhCzJgfc> {

    void generateJdb(String proname, String sjz) throws IOException, ParseException;

    void exportcz(HttpServletResponse response, String cdsl) throws IOException;

    void importcz(MultipartFile file, String proname) throws IOException;

}
