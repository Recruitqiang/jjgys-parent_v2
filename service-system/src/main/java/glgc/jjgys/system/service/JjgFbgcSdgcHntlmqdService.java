package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgFbgcSdgcHntlmqd;
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
 * @since 2023-05-04
 */
public interface JjgFbgcSdgcHntlmqdService extends IService<JjgFbgcSdgcHntlmqd> {

    void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException;

    List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException;

    void exportsdhntlmqd(HttpServletResponse response);

    void importsdhntlmqd(MultipartFile file, CommonInfoVo commonInfoVo);

    List<Map<String, Object>> selectsdmc(String proname, String htd, String fbgc);

    List<Map<String, Object>> lookjg(CommonInfoVo commonInfoVo, String value) throws IOException;

    int selectnum(String proname, String htd);

    int selectnumname(String proname);
}
