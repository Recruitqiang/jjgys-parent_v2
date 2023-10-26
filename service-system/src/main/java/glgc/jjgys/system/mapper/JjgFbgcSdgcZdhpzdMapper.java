package glgc.jjgys.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import glgc.jjgys.model.project.JjgFbgcSdgcZdhpzd;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wq
 * @since 2023-10-23
 */
@Mapper
public interface JjgFbgcSdgcZdhpzdMapper extends BaseMapper<JjgFbgcSdgcZdhpzd> {

    List<Map<String, Object>> selectlx(String proname, String htd);

    int selectcdnum(String proname, String htd, String zx);

    List<Map<String, Object>> selectzfList(String proname, String htd, String result, String s);

    List<Map<String, Object>> selectyfList(String proname, String htd, String result, String s);

}
