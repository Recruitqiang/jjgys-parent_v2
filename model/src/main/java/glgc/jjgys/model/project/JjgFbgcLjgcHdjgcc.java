package glgc.jjgys.model.project;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wq
 * @since 2023-02-08
 */
@Data
@ApiModel(description = "涵洞结构尺寸")
@TableName("jjg_fbgc_ljgc_hdjgcc")
public class JjgFbgcLjgcHdjgcc implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;


    @ApiModelProperty(value = "桩号")
    @TableField("zh")
    private String zh;

    @ApiModelProperty(value = "部位")
    @TableField("bw")
    private String bw;

    @ApiModelProperty(value = "类别")
    @TableField("lb")
    private String lb;

    @ApiModelProperty(value = "设计值(mm)")
    @TableField("sjz")
    private String sjz;

    @ApiModelProperty(value = "实测值(mm)")
    @TableField("scz")
    private String scz;

    @ApiModelProperty(value = "允许误差+(mm)")
    @TableField("yxwcz")
    private String yxwcz;

    @ApiModelProperty(value = "允许误差-(mm)")
    @TableField("yxwcf")
    private String yxwcf;


    @ApiModelProperty(value = "检测时间")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    @TableField("jcsj")
    private Date jcsj;


    @TableField("createTime")
    private Date createtime;

    @ApiModelProperty(value = "合同段")
    @TableField("htd")
    private String htd;

    @TableField("username")
    private String username;

    @ApiModelProperty(value = "项目名称")
    @TableField("proname")
    private String proname;

    @ApiModelProperty(value = "分部工程")
    @TableField("fbgc")
    private String fbgc;


}
