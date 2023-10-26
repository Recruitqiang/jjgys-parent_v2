package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgZdhCzJgfc;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

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

    List<Map<String, Object>> selectlx(String proname, String htd);

    List<Map<String, Object>> lookpjz(String proname) throws IOException;


    List<Map<String, Object>> selecthtd(String proname);

}
