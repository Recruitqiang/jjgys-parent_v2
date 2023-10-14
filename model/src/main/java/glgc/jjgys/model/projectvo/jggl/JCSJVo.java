package glgc.jjgys.model.projectvo.jggl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JCSJVo extends BaseRowModel {
    /**
     * 合同段
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "合同段" ,index = 0)
    private String htd;

    /**
     * 单位工程名称
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "单位工程" ,index = 1)
    private String dwgc;

    /**
     * 分部工程
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "分部工程" ,index = 2)
    private String fbgc;

    @ColumnWidth(23)
    @ExcelProperty(value = "抽查项目" ,index = 3)
    private String ccxm;

    @ColumnWidth(23)
    @ExcelProperty(value = "规定值/允许偏差" ,index = 4)
    private String gdz;

    @ColumnWidth(23)
    @ExcelProperty(value = "总点数" ,index = 5)
    private String zds;

    @ColumnWidth(23)
    @ExcelProperty(value = "合格点数" ,index = 6)
    private String hgds;
}
