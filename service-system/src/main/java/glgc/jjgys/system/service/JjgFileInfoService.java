package glgc.jjgys.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import glgc.jjgys.model.project.JjgFileInfo;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 文件资源表 服务类
 * </p>
 *
 * @author wq
 * @since 2023-10-15
 */
public interface JjgFileInfoService extends IService<JjgFileInfo> {

    List<JjgFileInfo> getfilelist();


    void download(List<JjgFileInfo> downloadPath) throws IOException;

}
