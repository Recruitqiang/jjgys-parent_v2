package glgc.jjgys.model.projectvo.jggl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.metadata.BaseRowModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wq
 * @since 2023-09-25
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgWgkfVo extends BaseRowModel {

    /**
     * 合同段
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "合同段" ,index = 0)
    private String htd;

    /**
     * 单位工程
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

    /**
     * 分部工程扣分
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "分部工程扣分" ,index = 3)
    private String fbgckf;


}
