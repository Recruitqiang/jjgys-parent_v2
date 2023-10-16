package glgc.jjgys.model.project;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;

import glgc.jjgys.model.system.SysMenu;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 文件资源表
 * </p>
 *
 * @author wq
 * @since 2023-10-15
 */
@Data
public class JjgFileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源路径
     */
    @TableField("path")
    private String path;

    /**
     * 资源原始名称
     */
    @TableField("name")
    private String name;

    /**
     * 资源名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 是否文件
     */
    @TableField("is_file")
    private Boolean isFile;


    /**
     * 是否目录
     */
    @TableField("is_dir")
    private Boolean isDir;

    @TableField(exist = false)
    private List<JjgFileInfo> children;


}
