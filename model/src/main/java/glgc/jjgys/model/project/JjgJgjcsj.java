package glgc.jjgys.model.project;

import com.alibaba.excel.metadata.BaseRowModel;
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
 * @since 2023-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jjg_jgjcsj")
public class JjgJgjcsj  implements Serializable{

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
     * 抽查项目
     */
    @TableField("ccxm")
    private String ccxm;

    /**
     * 规定值/允许偏差
     */
    @TableField("gdz")
    private String gdz;

    /**
     * 总点数
     */
    @TableField("zds")
    private String zds;

    /**
     * 合格点数
     */
    @TableField("hgds")
    private String hgds;

    @TableField("proname")
    private String proname;

    @TableField("create_time")
    private Date createTime;



}
