package glgc.jjgys.system.mapper;

import glgc.jjgys.model.project.JjgFbgcQlgcZdhpzd;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wq
 * @since 2023-10-15
 */
@Mapper
public interface JjgFbgcQlgcZdhpzdMapper extends BaseMapper<JjgFbgcQlgcZdhpzd> {

    List<Map<String, Object>> selectlx(String proname, String htd);

    int selectcdnum(String proname, String htd, String zx);

    List<Map<String, Object>> selectzfList(String proname, String htd, String zx, String result);

    List<Map<String, Object>> selectyfList(String proname, String htd, String zx, String result);

}
