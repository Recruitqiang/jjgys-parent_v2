package glgc.jjgys.model.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wq
 * @since 2023-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jjg_fbgc_sdgc_jk")
public class JjgFbgcSdgcJk implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;

    @TableField("jcsj")
    private Date jcsj;

    /**
     * 桩号
     */
    @TableField("zh")
    private String zh;

    @TableField("dh")
    private String dh;

    @TableField("xz")
    private String xz;

    @TableField("zz")
    private String zz;

    @TableField("pcz")
    private String pcz;

    @TableField("path")
    private String path;

    @TableField("createTime")
    private Date createtime;

    @TableField("htd")
    private String htd;

    @TableField("proname")
    private String proname;

    @TableField("fbgc")
    private String fbgc;

    @TableField("username")
    private String username;


}
