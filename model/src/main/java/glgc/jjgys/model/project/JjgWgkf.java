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
 * @since 2023-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jjg_wgkf")
public class JjgWgkf implements Serializable {

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
     * 分部工程扣分
     */
    @TableField("fbgckf")
    private String fbgckf;

    @TableField("proname")
    private String proname;

    @TableField("create_time")
    private Date createTime;


}
