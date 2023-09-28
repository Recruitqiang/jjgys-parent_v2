package glgc.jjgys.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import glgc.jjgys.model.project.JjgLqsJgQl;
import glgc.jjgys.model.project.JjgLqsQl;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wq
 * @since 2023-09-22
 */
@Mapper
public interface JjgLqsJgQlMapper extends BaseMapper<JjgLqsJgQl> {

    List<JjgLqsJgQl> selectqlzf(String proname, String htdzhq, String htdzhz, String lf);

    List<JjgLqsJgQl> selectqlyf(String proname, String htdzhq, String htdzhz, String lf);

    List<JjgLqsJgQl> selectqlList(String proname, String zhq, String zhz, String bz, String wz, String zdlf);

}
