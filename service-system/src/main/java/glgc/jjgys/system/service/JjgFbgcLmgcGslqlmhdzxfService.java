package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgFbgcLmgcGslqlmhdzxf;
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
 * @since 2023-04-25
 */
public interface JjgFbgcLmgcGslqlmhdzxfService extends IService<JjgFbgcLmgcGslqlmhdzxf> {

    void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException;

    List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException;

    void exportlqlmhd(HttpServletResponse response);

    void importLqlmhd(MultipartFile file, CommonInfoVo commonInfoVo);

    int selectnum(String proname, String htd);

    int selectnumname(String proname);

}
