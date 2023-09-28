package glgc.jjgys.model.projectvo.zdh;

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
 * @since 2023-06-17
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgZdhPzdJgfcVo extends BaseRowModel {

    /**
     * 起点桩号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "起点桩号" ,index = 0)
    private String qdzh;

    /**
     * 终点桩号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "终点桩号" ,index = 1)
    private String zdzh;

    /**
     * 左IRI
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "左IRI" ,index = 2)
    private String ziri;

    /**
     * 右IRI
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "右IRI" ,index = 3)
    private String yiri;

    /**
     * 类型标识
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "路线类型" ,index = 4)
    private String lxbs;

    /**
     * 匝道标识
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "匝道标识" ,index = 5)
    private String zdbs;

    @ColumnWidth(23)
    @ExcelProperty(value = "合同段" ,index = 6)
    private String htd;



}
