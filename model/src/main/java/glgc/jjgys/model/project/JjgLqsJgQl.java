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
 * @since 2023-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jjg_lqs_jg_ql")
public class JjgLqsJgQl implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;

    @TableField("qlname")
    private String qlname;

    @TableField("htd")
    private String htd;

    @TableField("lf")
    private String lf;

    @TableField("zhq")
    private Double zhq;

    @TableField("zhz")
    private Double zhz;

    @TableField("qlqc")
    private String qlqc;

    @TableField("dkkj")
    private String dkkj;

    @TableField("pzlx")
    private String pzlx;

    @TableField("bz")
    private String bz;

    @TableField("wz")
    private String wz;

    @TableField("create_time")
    private Date createTime;

    @TableField("proname")
    private String proname;


}
