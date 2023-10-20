package glgc.jjgys.model.projectvo.jggl;

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
 * @since 2023-09-25
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgNyzlkfVo extends BaseRowModel {

    /**
     * 合同段
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "合同段" ,index = 0)
    private String htd;

    /**
     * 存在问题
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "存在问题" ,index = 1)
    private String wt;

    /**
     * 扣分
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "扣分" ,index = 2)
    private String kf;


}
