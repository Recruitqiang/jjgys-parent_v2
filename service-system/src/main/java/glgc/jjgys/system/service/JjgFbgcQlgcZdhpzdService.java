package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgFbgcQlgcZdhpzd;
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
 * @since 2023-10-15
 */
public interface JjgFbgcQlgcZdhpzdService extends IService<JjgFbgcQlgcZdhpzd> {

    void importpzd(MultipartFile file, CommonInfoVo commonInfoVo) throws IOException;

    void exportpzd(HttpServletResponse response, String cd) throws IOException;

    List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException;

    void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException;

    List<Map<String, Object>> selectlx(String proname, String htd);

}
