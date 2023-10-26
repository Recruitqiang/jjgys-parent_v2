package glgc.jjgys.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spire.doc.*;
import com.spire.doc.documents.HorizontalAlignment;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.TextSelection;
import com.spire.doc.documents.VerticalAlignment;
import com.spire.presentation.Cell;
import com.spire.xls.WorksheetVisibility;
import com.spire.xls.collections.WorksheetsCollection;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcGenerateWordMapper;
import glgc.jjgys.system.service.JjgFbgcGenerateWordService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.spire.doc.fields.TextRange;
import com.spire.xls.CellRange;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


@Slf4j
@Service
public class JjgFbgcGenerateWordImpl extends ServiceImpl<JjgFbgcGenerateWordMapper, Object> implements JjgFbgcGenerateWordService {

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

    @Override
    public void generateword(String proname) throws IOException {
        String excelFilePath = filespath+File.separator+proname+File.separator+"报告中表格.xlsx";
        File f = new File(filespath + File.separator + proname + File.separator + "报告.docx");
        File fdir = new File(filespath + File.separator + proname);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        Document xw = null;
        try {
            File directory = new File("service-system/src/main/resources/static");
            String reportPath = directory.getCanonicalPath();
            String name = "报告.docx";
            String path = reportPath + File.separator + name;
            Files.copy(Paths.get(path), new FileOutputStream(f));
            FileInputStream out = new FileInputStream(f);
            xw = new Document(out);
            try {
                //读取Excel文件
                Workbook workbook = new Workbook();
                workbook.loadFromFile(excelFilePath);
                WorksheetsCollection sheetnum = workbook.getWorksheets();

                //xw.replace("项目全称", proname, true, true);
                int cnt=sheetnum.getCount()-1;

                //xw.saveToFile(f.getPath(), FileFormat.Docx);
                //for (int j = 0; j < sheetnum.getCount()-1; j++) {
                for (int j = 0; j < 2; j++) {
                    String sheetname = sheetnum.get(j).getName();
                    Worksheet sheet = workbook.getWorksheets().get(j);
                    boolean ishidden = JjgFbgcCommonUtils.ishidden(excelFilePath, sheetname);
                    if (!ishidden) {
                        CellRange allocatedRange = sheet.getAllocatedRange();
                        // text= allocatedRange.getDisplayedText();
                        String s=allocatedRange.get(1,1).getDisplayedText();
                        //String[] s1=s.split("\\s+");
                        //log.info("总共复制{}个表，目前第{}个",cnt,j+1);
                        log.info("表名：{}",s);
                        TextSelection[] selection = xw.findAllString(s, true, true);
                        System.out.println(selection);
                        if(selection==null){
                            System.out.println("不存在的表格:"+s);
                            continue;
                        }
                        for(int k=0;k<selection.length;k++){
                            TextRange range=selection[k].getAsOneRange();

                            //删除没用数据的行
                            CellRange dataRange = RemoveEmptyRows(sheet,allocatedRange);
                            //复制到Word文档
                            log.info("开始复制{}中的数据到word中",sheetname);
                            log.info("此处共{}处匹配，目前第{}处",selection.length,k+1);
                            copyToWord(dataRange, xw,f.getPath(),range);

                            log.info("{}中的数据复制完成",sheetname);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            out.close();
            xw.close();
        }catch (Exception e) {
            if(f.exists()){
                f.delete();
            }
            throw new JjgysException(20001, "生成报告错误，请检查数据的正确性");
        }

    }

    /**
     *
     * @param sheet
     * @param allocatedRange
     * @return
     */
    private CellRange RemoveEmptyRows(Worksheet sheet, CellRange allocatedRange) {
        CellRange dataRange = null;
        for (int i = allocatedRange.getLastRow(); i >= 1; i--) {
            boolean isRowEmpty = true;
            for (int col = 1; col <= allocatedRange.getLastColumn(); col++) {
                if (!sheet.getCellRange(i, col).getText().isEmpty()) {
                    isRowEmpty = false;
                    break;
                }
            }
            if (isRowEmpty) {
                sheet.deleteRow(i);
            } else {
                //System.out.println("有文字的行 "+i);
                dataRange = sheet.getCellRange(1, 1, i, allocatedRange.getLastColumn());
                break;
            }
        }
        return dataRange;
    }



    /**
     * @param cell
     * @param doc
     * @param path
     */
    private static void copyToWord(CellRange cell, Document doc, String path,TextRange range) {
        //添加表格
        Table table=new Table(doc,true);
        table.resetCells(cell.getRowCount()-1, cell.getColumnCount());
        Paragraph paragraph=range.getOwnerParagraph();
        Body body=paragraph.ownerTextBody();
        int index=body.getChildObjects().indexOf(paragraph);

        /*for (int r = 2; r <= cell.getRowCount(); r++) {
            for (int c = 1; c <= cell.getLastColumn(); c++) {
                CellRange xCell = cell.get(r, c);
                CellRange mergeArea = xCell.getMergeArea();
                //合并单元格
                if (mergeArea != null && mergeArea.getRow() == r && mergeArea.getColumn() == c) {
                    int rowIndex = mergeArea.getRow();
                    int columnIndex = mergeArea.getColumn();
                    int rowCount = mergeArea.getRowCount();
                    int columnCount = mergeArea.getColumnCount();
                    for (int m = 0; m < rowCount; m++) {
                        table.applyHorizontalMerge(rowIndex - 2 + m, columnIndex - 1, columnIndex + columnCount - 2);
                    }
                    table.applyVerticalMerge(columnIndex - 1, rowIndex - 2, rowIndex + rowCount - 3);
                }
                //复制内容
                TableCell wCell = table.getRows().get(r - 2).getCells().get(c - 1);
                if (!xCell.getDisplayedText().isEmpty()) {
                    range=wCell.addParagraph().appendText(xCell.getDisplayedText());
                    //TextRange textRange = wCell.addParagraph().appendText(xCell.getDisplayedText());
                    copyStyle(range, xCell, wCell);
                } else {
                    wCell.getCellFormat().setBackColor(xCell.getStyle().getColor());
                }
            }
        }
        body.getChildObjects().remove(paragraph);*/
        body.getChildObjects().insert(index,table);
        doc.saveToFile(path,com.spire.doc.FileFormat.Docx);
    }



    /**
     *
     * @param wTextRange
     * @param xCell
     * @param wCell
     */
    private static void copyStyle(TextRange wTextRange, CellRange xCell, TableCell wCell) {
        //复制字体样式
        wTextRange.getCharacterFormat().setTextColor(xCell.getStyle().getFont().getColor());
        wTextRange.getCharacterFormat().setFontSize((float) xCell.getStyle().getFont().getSize());
        wTextRange.getCharacterFormat().setFontName(xCell.getStyle().getFont().getFontName());
        wTextRange.getCharacterFormat().setBold(xCell.getStyle().getFont().isBold());
        wTextRange.getCharacterFormat().setItalic(xCell.getStyle().getFont().isItalic());
        //复制背景色
        wCell.getCellFormat().setBackColor(xCell.getStyle().getColor());
        //复制排列方式
        switch (xCell.getHorizontalAlignment()) {
            case Left:
                wTextRange.getOwnerParagraph().getFormat().setHorizontalAlignment(HorizontalAlignment.Left);
                break;
            case Center:
                wTextRange.getOwnerParagraph().getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
                break;
            case Right:
                wTextRange.getOwnerParagraph().getFormat().setHorizontalAlignment(HorizontalAlignment.Right);
                break;
            default:
                break;
        }
        switch (xCell.getVerticalAlignment()) {
            case Bottom:
                wCell.getCellFormat().setVerticalAlignment(VerticalAlignment.Bottom);
                break;
            case Center:
                wCell.getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                break;
            case Top:
                wCell.getCellFormat().setVerticalAlignment(VerticalAlignment.Top);
                break;
            default:
                break;
        }
    }

}
