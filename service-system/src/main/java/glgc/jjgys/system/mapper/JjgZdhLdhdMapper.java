package glgc.jjgys.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import glgc.jjgys.model.project.JjgZdhLdhd;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wq
 * @since 2023-07-01
 */
@Mapper
public interface JjgZdhLdhdMapper extends BaseMapper<JjgZdhLdhd> {

    List<Map<String, Object>> selectlx(String proname, String htd);

    int selectcdnum(String proname, String htd, String zx);

    List<Map<String, Object>> selectzfList(String proname, String htd, String zx,String result);
    List<Map<String, Object>> selectzfListyh(String proname, String htd, String zx, String result, String username);

    List<Map<String, Object>> selectyfList(String proname, String htd, String zx,String result);
    List<Map<String, Object>> selectyfListyh(String proname, String htd, String zx, String result, String username);

    List<Map<String, Object>> selectSdZfData(String proname, String htd, String zhq, String zhz,String result);
    List<Map<String, Object>> selectSdZfDatayh(String proname, String htd, String zhq, String zhz, String result, String username);

    List<Map<String, Object>> selectSdyfData(String proname, String htd, String zhq, String zhz,String result);
    List<Map<String, Object>> selectSdyfDatayh(String proname, String htd, String zhq, String zhz, String result, String username);

    List<Map<String, Object>> selectQlZfData(String proname, String htd, String zhq, String zhz,String result);
    List<Map<String, Object>> selectQlZfDatayh(String proname, String htd, String zhq, String zhz, String result, String username);

    List<Map<String, Object>> selectQlYfData(String proname, String htd, String zhq, String zhz,String result);
    List<Map<String, Object>> selectQlYfDatayh(String proname, String htd, String zhq, String zhz, String result, String username);

    List<Map<String, Object>> seletcfhlmzfData(String proname, String htd, String zhq, String zhz);
    List<Map<String, Object>> seletcfhlmzfDatayh(String proname, String htd, String zhq, String zhz, String username);

    List<Map<String, Object>> seletcfhlmyfData(String proname, String htd, String zhq, String zhz);
    List<Map<String, Object>> seletcfhlmyfDatayh(String proname, String htd, String zhq, String zhz, String username);

    List<Map<String, Object>> selectsdldhd(String proname, String bz, String lf, String zx, String sdzhq, String sdzhz);
    List<Map<String, Object>> selectsdldhdyh(String proname, String bz, String lf, String zx, String sdzhq, String sdzhz, String username);

    List<Map<String, Object>> selectqlldhd(String proname, String bz, String lf, String zx, String qlzhq, String qlzhz);
    List<Map<String, Object>> selectqlldhdyh(String proname, String bz, String lf, String zx, String qlzhq, String qlzhz, String username);
}
