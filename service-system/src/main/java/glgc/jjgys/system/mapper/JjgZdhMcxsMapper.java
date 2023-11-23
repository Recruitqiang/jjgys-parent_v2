package glgc.jjgys.system.mapper;

import glgc.jjgys.model.project.JjgZdhMcxs;
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
 * @since 2023-06-06
 */
@Mapper
public interface JjgZdhMcxsMapper extends BaseMapper<JjgZdhMcxs> {

    List<Map<String,Object>> selectSdZfData(String proname, String htd, String zhq, String zhz,String zhq1,String zhz1);
    List<Map<String,Object>> selectSdZfDatayh(String proname, String htd, String zhq, String zhz, String zhq1, String zhz1, String username);

    int selectcdnum(String proname, String htd, String zx);

    List<Map<String,Object>> selectSdyfData(String proname, String htd, String zhq, String zhz,String zhq1,String zhz1);
    List<Map<String,Object>> selectSdyfDatayh(String proname, String htd, String zhq, String zhz, String zhq1, String zhz1, String username);

    List<Map<String,Object>>  selectQlZfData(String proname, String htd, String zhq, String zhz,String zhq1,String zhz1);
    List<Map<String,Object>>  selectQlZfDatayh(String proname, String htd, String zhq, String zhz, String zhq1, String zhz1, String username);

    List<Map<String,Object>>  selectQlYfData(String proname, String htd, String zhq, String zhz,String zhq1,String zhz1);
    List<Map<String,Object>>  selectQlYfDatayh(String proname, String htd, String zhq, String zhz, String zhq1, String zhz1, String username);

    List<Map<String,Object>> selectzfList(String proname, String htd,String zx);
    List<Map<String,Object>> selectzfListyh(String proname, String htd, String zx, String username);

    List<Map<String,Object>> selectyfList(String proname, String htd,String zx);
    List<Map<String,Object>> selectyfListyh(String proname, String htd, String zx, String username);

    List<Map<String, Object>> selectlx(String proname, String htd);

    List<Map<String, Object>> selectsdmcxs(String proname,String bz,String lf, String sdzhq, String sdzhz, String zx, String zhq1, String zhz1);
    List<Map<String, Object>> selectsdmcxsyh(String proname, String bz, String lf, String sdzhq, String sdzhz, String zx, String zhq1, String zhz1, String username);

    List<Map<String, Object>> selectqlmcxs(String proname,String bz,String lf, String qlzhq, String qlzhz, String zx, String zhq1, String zhz1);
    List<Map<String, Object>> selectqlmcxsyh(String proname, String bz, String lf, String qlzhq, String qlzhz, String zx, String zhq1, String zhz1, String username);

    int selectnum(String proname, String htd);
}
