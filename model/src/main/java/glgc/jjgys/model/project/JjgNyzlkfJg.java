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
 * @since 2023-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jjg_nyzlkf_jg")
public class JjgNyzlkfJg implements Serializable {

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
     * 存在问题
     */
    @TableField("wt")
    private String wt;

    /**
     * 扣分
     */
    @TableField("kf")
    private String kf;

    @TableField("proname")
    private String proname;

    @TableField("create_time")
    private Date createTime;


}
