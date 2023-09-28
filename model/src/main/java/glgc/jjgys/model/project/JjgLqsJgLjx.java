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
@TableName("jjg_lqs_jg_ljx")
public class JjgLqsJgLjx implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;

    @TableField("name")
    private String ljxname;

    @TableField("htd")
    private String htd;

    @TableField("lf")
    private String lf;

    @TableField("zhq")
    private String zhq;

    @TableField("zhz")
    private String zhz;

    @TableField("bz")
    private String bz;

    @TableField("sshtd")
    private String ljxlmsshtd;

    @TableField("create_time")
    private Date createTime;

    @TableField("proname")
    private String proname;


}
