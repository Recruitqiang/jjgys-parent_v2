package glgc.jjgys.model.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2023-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jjg_fbgc_sdgc_hntlmqd")
public class JjgFbgcSdgcHntlmqd implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;

    /**
     * 检测时间
     */
    @TableField("jcsj")
    private Date jcsj;

    /**
     * 隧道名称
     */
    @TableField("sdmc")
    private String sdmc;

    /**
     * 桩号
     */
    @TableField("zh")
    private String zh;

    /**
     * 试样平均直径(mm)
     */
    @TableField("sypjzj")
    private String sypjzj;

    /**
     * 试样平均厚度(mm)
     */
    @TableField("sypjhd")
    private String sypjhd;

    /**
     * 极限荷载(N)
     */
    @TableField("jxhz")
    private String jxhz;

    /**
     * 路面强度规定值
     */
    @TableField("lmqdgdz")
    private String lmqdgdz;

    @TableField("proname")
    private String proname;

    @TableField("fbgc")
    private String fbgc;

    @TableField("htd")
    private String htd;

    @TableField("username")
    private String username;

    @TableField("createTime")
    private Date createtime;


}
