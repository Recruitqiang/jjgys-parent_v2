package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgFbgcSdgcZdhcz;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
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
 * @since 2023-10-23
 */
public interface JjgFbgcSdgcZdhczService extends IService<JjgFbgcSdgcZdhcz> {

    void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException;

    List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException;

    void exportcz(HttpServletResponse response, String cd) throws IOException;

    void importcz(MultipartFile file, CommonInfoVo commonInfoVo) throws IOException;


    List<Map<String, Object>> selectlx(String proname, String htd);

    List<Map<String, Object>> lookJdb(CommonInfoVo commonInfoVo, String value) throws IOException;
}
