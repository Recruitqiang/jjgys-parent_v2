package glgc.jjgys.model.projectvo.sdgc;

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
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wq
 * @since 2023-10-11
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgFbgcSdgcJkVo extends BaseRowModel {


    @ColumnWidth(23)
    @ExcelProperty(value = "检测时间" ,index = 0)
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date jcsj;

    /**
     * 桩号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "桩号" ,index = 1)
    private String zh;

    @ColumnWidth(23)
    @ExcelProperty(value = "点号" ,index = 2)
    private String dh;

    @ColumnWidth(23)
    @ExcelProperty(value = "X" ,index = 3)
    private String xz;

    @ColumnWidth(23)
    @ExcelProperty(value = "Z" ,index = 4)
    private String zz;

    @ColumnWidth(23)
    @ExcelProperty(value = "偏差值" ,index = 5)
    private String pcz;




}
