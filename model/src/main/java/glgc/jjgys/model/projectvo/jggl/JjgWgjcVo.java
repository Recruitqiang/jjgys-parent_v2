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
 * 交工的 暂存到这个包中
 * </p>
 *
 * @author wq
 * @since 2023-11-19
 */
@Data
@HeadStyle(fillForegroundColor = 13)
@HeadFontStyle(fontHeightInPoints = 10)
@HeadRowHeight(30)
public class JjgWgjcVo extends BaseRowModel {


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
     * 病害描述
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "病害描述" ,index = 3)
    private String bhms;

    /**
     * 构件名称
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "构件名称" ,index = 4)
    private String gjmc;

    /**
     * 构件编号
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "构件编号" ,index = 5)
    private String gjbh;

    /**
     * 病害类型
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "病害类型" ,index = 6)
    private String bhlx;

    /**
     * 病害数量
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "病害数量" ,index = 7)
    private String bhsl;

    /**
     * 病害定量
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "病害定量" ,index = 8)
    private String bhdl;

    /**
     * 备注
     */
    @ColumnWidth(23)
    @ExcelProperty(value = "备注" ,index = 9)
    private String bz;


}
