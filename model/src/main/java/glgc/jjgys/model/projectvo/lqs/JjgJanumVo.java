package glgc.jjgys.model.projectvo.lqs;

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
 * @since 2023-10-21
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgJanumVo extends BaseRowModel {

    @ColumnWidth(23)
    @ExcelProperty(value = "项目名称" ,index = 0)
    private String proname;

    @ColumnWidth(23)
    @ExcelProperty(value = "合同段" ,index = 1)
    private String htd;

    @ColumnWidth(23)
    @ExcelProperty(value = "分部工程" ,index = 2)
    private String fbgc;

    @ColumnWidth(23)
    @ExcelProperty(value = "指标" ,index = 3)
    private String zb;

    @ColumnWidth(23)
    @ExcelProperty(value = "数量" ,index = 4)
    private String num;


}
