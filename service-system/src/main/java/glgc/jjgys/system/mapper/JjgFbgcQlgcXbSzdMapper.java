package glgc.jjgys.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import glgc.jjgys.model.project.JjgFbgcQlgcXbSzd;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wq
 * @since 2023-03-22
 */
@Mapper
public interface JjgFbgcQlgcXbSzdMapper extends BaseMapper<JjgFbgcQlgcXbSzd> {

    int selectnums(String proname, String htd);

    int selectnumname(String proname);

}
