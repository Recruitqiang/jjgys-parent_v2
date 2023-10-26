package glgc.jjgys.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import glgc.jjgys.model.project.JjgFbgcLmgcLmgzsdsgpsfJgfc;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wq
 * @since 2023-09-23
 */
@Mapper
public interface JjgFbgcLmgcLmgzsdsgpsfJgfcMapper extends BaseMapper<JjgFbgcLmgcLmgzsdsgpsfJgfc> {

    List<String> gethtd(String proname);

    List<Map<String, Object>> selecthtd(String proname);
}
