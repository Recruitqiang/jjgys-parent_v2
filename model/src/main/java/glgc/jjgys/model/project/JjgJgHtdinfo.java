package glgc.jjgys.model.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

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
@TableName("jjg_jg_htdinfo")
public class JjgJgHtdinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;

    /**
     * 合同段名称
     */
    @TableField("name")
    private String name;

    /**
     * 施工单位
     */
    @TableField("sgdw")
    private String sgdw;

    /**
     * 监理单位
     */
    @TableField("jldw")
    private String jldw;

    /**
     * ZY
     */
    @TableField("zy")
    private String zy;

    /**
     * 工程部位(起)
     */
    @TableField("zhq")
    private String zhq;

    /**
     * 工程部位(止)
     */
    @TableField("zhz")
    private String zhz;

    /**
     * 合同段类型
     */
    @TableField("lx")
    private String lx;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("proname")
    private String proname;


}
