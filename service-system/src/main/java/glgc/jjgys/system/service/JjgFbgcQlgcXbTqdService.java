package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgFbgcQlgcXbTqd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
 * @since 2023-03-22
 */
public interface JjgFbgcQlgcXbTqdService extends IService<JjgFbgcQlgcXbTqd> {

    void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException;

    List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException;

    void importqlxbtqd(MultipartFile file, CommonInfoVo commonInfoVo);

    void exportqlxbtqd(HttpServletResponse response);

    List<Map<String, Object>> lookjg(CommonInfoVo commonInfoVo);

    int selectnum(String proname, String htd);

    int selectnumname(String proname);

}
