package glgc.jjgys.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import glgc.jjgys.model.project.JjgJgHtdinfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wq
 * @since 2023-09-22
 */
@Mapper
public interface JjgJgHtdinfoMapper extends BaseMapper<JjgJgHtdinfo> {

    JjgJgHtdinfo selectInfo(String proname, String htd);
}
