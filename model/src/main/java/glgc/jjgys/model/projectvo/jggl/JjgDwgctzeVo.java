package glgc.jjgys.model.projectvo.jggl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
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
@EqualsAndHashCode(callSuper = false)
@TableName("jjg_dwgctze")
public class JjgDwgctzeVo extends BaseRowModel {

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
    @ExcelProperty(value = "单位工程名称（要与清单中的一致）" ,index = 1)
    private String name;

    /**
     * 投资额
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "投资额（万元）" ,index = 2)
    private String money;


}
