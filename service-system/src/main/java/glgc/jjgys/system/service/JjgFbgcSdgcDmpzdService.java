package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgFbgcSdgcDmpzd;
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
 * @since 2023-03-26
 */
public interface JjgFbgcSdgcDmpzdService extends IService<JjgFbgcSdgcDmpzd> {

    void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException;

    List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException;

    void exportsddmpzd(HttpServletResponse response);

    void importsddmpzd(MultipartFile file, CommonInfoVo commonInfoVo);

    List<Map<String, Object>> lookjg(CommonInfoVo commonInfoVo);

    int selectnum(String proname, String htd);

    int selectnumname(String proname);

}
