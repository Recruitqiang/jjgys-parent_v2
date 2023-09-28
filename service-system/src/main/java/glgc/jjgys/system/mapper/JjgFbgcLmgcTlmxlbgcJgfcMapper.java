package glgc.jjgys.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import glgc.jjgys.model.project.JjgFbgcLmgcTlmxlbgcJgfc;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wq
 * @since 2023-09-23
 */
@Mapper
public interface JjgFbgcLmgcTlmxlbgcJgfcMapper extends BaseMapper<JjgFbgcLmgcTlmxlbgcJgfc> {

    List<String> gethtd(String proname);

}
