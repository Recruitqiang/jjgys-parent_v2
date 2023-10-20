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
 * @since 2023-10-15
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgFbgcQlgcZdhpzdVo extends BaseRowModel {


    @ColumnWidth(23)
    @ExcelProperty(value = "桥梁名称" ,index = 0)
    private String qlname;

    /**
     * 起点桩号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "起点桩号" ,index = 1)
    private Double qdzh;

    /**
     * 终点桩号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "终点桩号" ,index = 2)
    private Double zdzh;

    /**
     * 左IRI
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "左IRI" ,index = 3)
    private String ziri;

    /**
     * 右IRI
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "右IRI" ,index = 4)
    private String yiri;



}
