package glgc.jjgys.model.projectvo.sdgc;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;


/**
 * <p>
 * 
 * </p>
 *
 * @author wq
 * @since 2023-10-23
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgFbgcSdgcZdhmcxsVo extends BaseRowModel {


    /**
     * 起点桩号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "起点桩号" ,index = 0)
    private Double qdzh;

    /**
     * 终点桩号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "终点桩号" ,index = 1)
    private Double zdzh;

    /**
     * SFC
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "SFC" ,index = 2)
    private String sfc;

    /**
     * 类型标识
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "路线类型" ,index = 3)
    private String lxbs;

    /**
     * 匝道标识
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "匝道标识" ,index = 4)
    private String zdbs;


}
