package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.base.JgCommonEntity;
import glgc.jjgys.model.project.JjgZdhGzsdJgfc;
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
public interface JjgZdhGzsdJgfcService extends IService<JjgZdhGzsdJgfc> {


    void generateJdb(JgCommonEntity commonInfoVo) throws IOException, ParseException;

    void exportgzsd(HttpServletResponse response, String cdsl) throws IOException;

    void importgzsd(MultipartFile file, String proname) throws IOException;

}