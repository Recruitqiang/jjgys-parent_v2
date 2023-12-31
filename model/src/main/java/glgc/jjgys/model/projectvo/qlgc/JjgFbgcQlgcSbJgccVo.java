package glgc.jjgys.model.projectvo.qlgc;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.metadata.BaseRowModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wq
 * @since 2023-03-20
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgFbgcQlgcSbJgccVo extends BaseRowModel {

    /**
     * 检测时间
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "检测日期" ,index = 0)
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date jcsj;

    /**
     * 桥梁名称
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "桥梁名称" ,index = 1)
    private String qlmc;

    /**
     * 梁板号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "梁板号" ,index = 2)
    private String lbh;

    /**
     * 部位
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "部位" ,index = 3)
    private String bw;

    /**
     * 类别
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "类别" ,index = 4)
    private String lb;

    /**
     * 设计值(mm)
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "设计值(mm)" ,index = 5)
    private String sjz;

    /**
     * 实测值(mm)
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "实测值(mm)" ,index = 6)
    private String scz;

    /**
     * 允许偏差+(mm)
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "允许偏差+(mm)" ,index = 7)
    private String yxwcz;

    /**
     * 允许偏差-(mm)
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "允许偏差-(mm)" ,index = 8)
    private String yxwcf;

}
