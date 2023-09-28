package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgFbgcLmgcTlmxlbgc;
import glgc.jjgys.model.project.JjgFbgcLmgcTlmxlbgcJgfc;
import glgc.jjgys.model.projectvo.lmgc.JjgFbgcLmgcTlmxlbgcJgfcVo;
import glgc.jjgys.model.projectvo.lmgc.JjgFbgcLmgcTlmxlbgcVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcLmgcTlmxlbgcJgfcMapper;
import glgc.jjgys.system.service.JjgFbgcLmgcTlmxlbgcJgfcService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.RowCopy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-09-23
 */
@Service
public class JjgFbgcLmgcTlmxlbgcJgfcServiceImpl extends ServiceImpl<JjgFbgcLmgcTlmxlbgcJgfcMapper, JjgFbgcLmgcTlmxlbgcJgfc> implements JjgFbgcLmgcTlmxlbgcJgfcService {

    @Autowired
    private JjgFbgcLmgcTlmxlbgcJgfcMapper jjgFbgcLmgcTlmxlbgcJgfcMapper;

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;


    @Override
    public void exportxlbgs(HttpServletResponse response) {
        String fileName = "砼路面相邻板高差实测数据";
        String sheetName = "实测数据";
        ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName, new JjgFbgcLmgcTlmxlbgcJgfcVo()).finish();

    }

    @Override
    public void importxlbgs(MultipartFile file, String proname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(JjgFbgcLmgcTlmxlbgcJgfcVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<JjgFbgcLmgcTlmxlbgcJgfcVo>(JjgFbgcLmgcTlmxlbgcJgfcVo.class) {
                                @Override
                                public void handle(List<JjgFbgcLmgcTlmxlbgcJgfcVo> dataList) {
                                    for(JjgFbgcLmgcTlmxlbgcJgfcVo tlmxlbgcVo: dataList)
                                    {
                                        JjgFbgcLmgcTlmxlbgcJgfc tlmxlbgc = new JjgFbgcLmgcTlmxlbgcJgfc();
                                        BeanUtils.copyProperties(tlmxlbgcVo,tlmxlbgc);
                                        tlmxlbgc.setCreatetime(new Date());
                                        tlmxlbgc.setProname(proname);
                                        tlmxlbgc.setFbgc("路面工程");
                                        jjgFbgcLmgcTlmxlbgcJgfcMapper.insert(tlmxlbgc);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }

    @Override
    public void generateJdb(String proname) throws IOException, ParseException {
        List<String> htds =  jjgFbgcLmgcTlmxlbgcJgfcMapper.gethtd(proname);
        for (String htd : htds) {
            gethtdjdb(proname,htd);
        }

    }

    private void gethtdjdb(String proname, String htd) throws IOException, ParseException {
        XSSFWorkbook wb = null;
        //获取数据
        QueryWrapper<JjgFbgcLmgcTlmxlbgcJgfc> wrapper=new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        wrapper.orderByAsc("zh");
        List<JjgFbgcLmgcTlmxlbgcJgfc> data = jjgFbgcLmgcTlmxlbgcJgfcMapper.selectList(wrapper);
        File f = new File(jgfilepath+File.separator+proname+File.separator+htd+File.separator+"17混凝土路面相邻板高差.xlsx");
        if (data == null || data.size()==0){
            return;
        }else {
            File fdir = new File(jgfilepath+File.separator+proname+File.separator+htd);
            if(!fdir.exists()){
                //创建文件根目录
                fdir.mkdirs();
            }
            File directory = new File("service-system/src/main/resources/static");
            String reportPath = directory.getCanonicalPath();
            String path =reportPath +File.separator+ "混凝土路面相邻板高差.xlsx";
            Files.copy(Paths.get(path), new FileOutputStream(f));
            FileInputStream out = new FileInputStream(f);
            wb = new XSSFWorkbook(out);
            createTable(gettableNum(data.size()),wb);
            if(DBtoExcel(data,wb)){
                for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                    if (shouldBeCalculate(wb.getSheetAt(j))) {
                        calculateHeightDifferenceSheet(wb.getSheetAt(j));
                        getTunnelTotal(wb.getSheetAt(j));
                    }
                }

                for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                    JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
                }

                FileOutputStream fileOut = new FileOutputStream(f);
                wb.write(fileOut);
                fileOut.flush();
                fileOut.close();

            }
            out.close();
            wb.close();
        }
    }

    /**
     *
     * @param sheet
     */
    private void getTunnelTotal(XSSFSheet sheet) {
        XSSFRow row = null;
        boolean flag = false;
        XSSFRow startrow = null;
        XSSFRow endrow = null;
        String name = "";
        for (int i = sheet.getFirstRowNum(); i <= sheet
                .getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if ("合计".equals(row.getCell(0).toString())) {
                endrow = sheet.getRow(i-1);
                startrow.createCell(8).setCellFormula("COUNT("
                        +startrow.getCell(3).getReference()+":"
                        +endrow.getCell(3).getReference()+")");//=COUNT(D7:D36)
                startrow.createCell(9).setCellFormula("COUNTIF("
                        +startrow.getCell(4).getReference()+":"
                        +endrow.getCell(4).getReference()+",\"√\")");//=COUNTIF(E7:E36,"√")
                startrow.createCell(10).setCellFormula(startrow.getCell(9).getReference()+"*100/"
                        +startrow.getCell(8).getReference());
                break;
            }
            if(flag && row.getCell(1) != null && !"".equals(row.getCell(1).toString())){
                if(!name.equals(row.getCell(1).toString().substring(0, row.getCell(1).toString().indexOf("K"))) && !"".equals(name)){
                    endrow = sheet.getRow(i-1);
                    startrow.createCell(8).setCellFormula("COUNT("
                            +startrow.getCell(3).getReference()+":"
                            +endrow.getCell(3).getReference()+")");//=COUNT(D7:D36)
                    startrow.createCell(9).setCellFormula("COUNTIF("
                            +startrow.getCell(4).getReference()+":"
                            +endrow.getCell(4).getReference()+",\"√\")");//=COUNTIF(E7:E36,"√")
                    startrow.createCell(10).setCellFormula(startrow.getCell(9).getReference()+"*100/"
                            +startrow.getCell(8).getReference());
                    name = row.getCell(1).toString().substring(0, row.getCell(1).toString().indexOf("K"));
                    startrow = row;
                }
                if("".equals(name)){
                    /*
                     * 隧道要分左右幅统计，但渗水系数没有分开统计，所以要根据桩号的z/y来判断
                     */
                    name = row.getCell(1).toString().substring(0, row.getCell(1).toString().indexOf("K"));
                    //System.out.println("name1 = "+name);
                    startrow = row;
                }

            }
            if ("序号".equals(row.getCell(0).toString())) {
                flag = true;
                i++;
            }

        }

    }

    /**
     *
     * @param sheet
     */
    private void calculateHeightDifferenceSheet(XSSFSheet sheet) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        boolean flag = false;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            // 可以计算
            if (row.getCell(3).getCellType() == Cell.CELL_TYPE_NUMERIC
                    && flag) {
                row.getCell(4).setCellFormula(
                        "IF(" + row.getCell(3).getReference()
                                + "=\"\",\"\",IF("
                                + row.getCell(3).getReference()
                                + "<=$C$4,\"√\",\"\"))");// E=IF(D7="","",IF(D7<=$C$4,"√",""))
                row.getCell(6).setCellFormula(
                        "IF(" + row.getCell(3).getReference()
                                + ">$C$4,\"×\",\"\")");// G=IF(D7>$C$4,"×","")
                rowend = row;
            }
            // 可以计算啦
            if ("序号".equals(row.getCell(0).toString())) {
                flag = true;
                i++;
                rowstart = sheet.getRow(i + 1);
                rowend = rowstart;
            }
            if ("合计".equals(row.getCell(0).toString())) {

                row.getCell(3).setCellFormula(
                        "COUNT(" + rowstart.getCell(3).getReference() + ":"
                                + rowend.getCell(3).getReference() + ")");// =COUNT(D7:D30)
                row.getCell(5).setCellFormula(
                        "COUNTIF(" + rowstart.getCell(4).getReference() + ":"
                                + rowend.getCell(4).getReference() + ",\"√\")");// =COUNTIF(E7:F30,"√")
                row.getCell(7).setCellFormula(
                        row.getCell(5).getReference() + "/"
                                + row.getCell(3).getReference() + "*100");// =F36/D36*100
                row.createCell(8).setCellFormula(
                        "MAX(" + rowstart.getCell(3).getReference() + ":"
                                + rowend.getCell(3).getReference() + ")");// =COUNT(D7:D30)
                row.createCell(9).setCellFormula(
                        "MIN(" + rowstart.getCell(3).getReference() + ":"
                                + rowend.getCell(3).getReference() + ")");// =COUNT(D7:D30)
            }
        }
    }

    /**
     *
     * @param size
     * @return
     */
    private int gettableNum(int size) {
        return size%30 <= 29 ? size/30+1 : size/30+2;
    }

    /**
     *
     * @param tableNum
     * @param wb
     */
    private void createTable(int tableNum, XSSFWorkbook wb) {
        int record = 0;
        record = tableNum;
        for (int i = 1; i < record; i++) {
            if(i < record-1){
                RowCopy.copyRows(wb, "相邻板高差", "相邻板高差", 6, 35, (i - 1) * 30 + 36);
            }
            else{
                RowCopy.copyRows(wb, "相邻板高差", "相邻板高差", 6, 34, (i - 1) * 30 + 36);
            }
        }
        if(record == 1){
            wb.getSheet("相邻板高差").shiftRows(36, 36, -1);
        }
        RowCopy.copyRows(wb, "source", "相邻板高差", 0, 0,(record) * 30 + 5);
        wb.setPrintArea(wb.getSheetIndex("相邻板高差"), 0, 7, 0,(record) * 30 + 5);
    }


    /**
     *
     * @param data
     * @param wb
     * @return
     * @throws ParseException
     */
    private boolean DBtoExcel(List<JjgFbgcLmgcTlmxlbgcJgfc> data, XSSFWorkbook wb) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        XSSFSheet sheet = wb.getSheet("相邻板高差");
        int index = 6;
        sheet.getRow(1).getCell(2).setCellValue(data.get(0).getProname());
        sheet.getRow(1).getCell(6).setCellValue(data.get(0).getHtd());
        sheet.getRow(2).getCell(2).setCellValue(data.get(0).getFbgc());
        sheet.getRow(2).getCell(6).setCellValue("路面面层");
        sheet.getRow(3).getCell(2).setCellValue(Double.valueOf(data.get(0).getBgcgdz()));
        String date = simpleDateFormat.format(data.get(0).getJcsj());
        for(int i =1; i < data.size(); i++){
            date = JjgFbgcCommonUtils.getLastDate(date, simpleDateFormat.format(data.get(i).getJcsj()));
        }
        sheet.getRow(3).getCell(6).setCellValue(date);
        for(int i =0; i < data.size(); i++){
            sheet.addMergedRegion(new CellRangeAddress(index, index+3, 0, 0));
            sheet.getRow(index).getCell(0).setCellValue(i+1);

            sheet.addMergedRegion(new CellRangeAddress(index, index+3, 1, 2));
            sheet.getRow(index).getCell(1).setCellValue(data.get(i).getQywzmc()+ data.get(i).getZh());

            sheet.getRow(index).getCell(3).setCellValue(Double.parseDouble(data.get(i).getScz1()));
            sheet.addMergedRegion(new CellRangeAddress(index, index, 4, 5));
            sheet.addMergedRegion(new CellRangeAddress(index, index, 6, 7));
            sheet.getRow(index+1).getCell(3).setCellValue(Double.parseDouble(data.get(i).getScz2()));
            sheet.addMergedRegion(new CellRangeAddress(index+1, index+1, 4, 5));
            sheet.addMergedRegion(new CellRangeAddress(index+1, index+1, 6, 7));
            sheet.getRow(index+2).getCell(3).setCellValue(Double.parseDouble(data.get(i).getScz3()));
            sheet.addMergedRegion(new CellRangeAddress(index+2, index+2, 4, 5));
            sheet.addMergedRegion(new CellRangeAddress(index+2, index+2, 6, 7));
            sheet.getRow(index+3).getCell(3).setCellValue(Double.parseDouble(data.get(i).getScz4()));
            sheet.addMergedRegion(new CellRangeAddress(index+3, index+3, 4, 5));
            sheet.addMergedRegion(new CellRangeAddress(index+3, index+3, 6, 7));
            index += 4;
        }
        return true;


    }

    /**
     *
     * @param sheet
     * @return
     */
    private boolean shouldBeCalculate(XSSFSheet sheet) {
        String title = null;
        title = sheet.getRow(0).getCell(0).getStringCellValue();
        if (title.endsWith("鉴定表")) {
            return true;
        }
        return false;
    }
}
