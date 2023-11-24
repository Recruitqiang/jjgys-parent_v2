package glgc.jjgys.model.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wq
 * @since 2023-11-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jjg_wgjc")
public class JjgWgjc implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;

    /**
     * 合同段
     */
    @TableField("htd")
    private String htd;

    /**
     * 单位工程
     */
    @TableField("dwgc")
    private String dwgc;

    /**
     * 分部工程
     */
    @TableField("fbgc")
    private String fbgc;

    /**
     * 病害描述
     */
    @TableField("bhms")
    private String bhms;

    /**
     * 构件名称
     */
    @TableField("gjmc")
    private String gjmc;

    @TableField("gjmc2")
    private String gjmc2;

    /**
     * 构件编号
     */
    @TableField("gjbh")
    private String gjbh;

    /**
     * 病害类型
     */
    @TableField("bhlx")
    private String bhlx;

    /**
     * 病害数量
     */
    @TableField("bhsl")
    private String bhsl;

    /**
     * 病害定量
     */
    @TableField("bhdl")
    private String bhdl;

    /**
     * 备注
     */
    @TableField("bz")
    private String bz;

    @TableField("proname")
    private String proname;

    @TableField("create_time")
    private Date createTime;


}
