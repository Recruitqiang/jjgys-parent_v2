package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.qlgc.JjgFbgcQlgcZdhgzsdVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcQlgcZdhgzsdMapper;
import glgc.jjgys.system.service.JjgFbgcQlgcZdhgzsdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.RowCopy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-10-23
 */
@Service
@Slf4j
public class JjgFbgcQlgcZdhgzsdServiceImpl extends ServiceImpl<JjgFbgcQlgcZdhgzsdMapper, JjgFbgcQlgcZdhgzsd> implements JjgFbgcQlgcZdhgzsdService {

    @Autowired
    private JjgFbgcQlgcZdhgzsdMapper jjgFbgcQlgcZdhgzsdMapper;

    @Value(value = "${jjgys.path.filepath}")
    private String filepath;

    @Override
    public void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        //handlezxData(proname,htd,commonInfoVo.getSjz());
        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhgzsdMapper.selectlx(proname,htd);
        /*int cds = 0;
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhgzsdMapper.selectcdnum(proname,htd,zx);
            if (num == 1){
                cds = 2;
            }else {
                cds=num;
            }

        }*/
        int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhgzsdMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        handlezxData(proname,htd,cds,commonInfoVo.getSjz());

    }

    /**
     *  @param proname
     * @param htd
     * @param sjz
     */
    private void handlezxData(String proname, String htd, int cdsl, String sjz) throws IOException, ParseException {
        log.info("准备数据......");

        /**
         * 还需要判断时几车道，待处理
         * 先将左幅的数据归类，里面还包含左1，左2，左3，左4
         * 每个左1，2中的桩号都是一样的
         * 所以要取出桩号，桩号还需要处理一下，然后和基础数据中比对，分为主线，桥梁和隧道，然后分别写入到工作簿中。
         */
        List<Map<String, Object>> datazf = jjgFbgcQlgcZdhgzsdMapper.selectzfList(proname, htd);
        List<Map<String, Object>> datayf = jjgFbgcQlgcZdhgzsdMapper.selectyfList(proname, htd);



        List<Map<String, Object>> lmzfList = groupByZh(datazf);
        List<Map<String, Object>> lmyfList = groupByZh(datayf);

        writeExcelData(proname, htd, lmzfList, lmyfList, cdsl, sjz);


    }

    /**
     *
     * @param proname
     * @param htd
     * @param cdsl
     * @param sjz
     * @throws IOException
     * @throws ParseException
     */
    private void writeExcelData(String proname, String htd, List<Map<String, Object>> qlzxList, List<Map<String, Object>> qlyxList, int cdsl, String sjz) throws IOException, ParseException {
        XSSFWorkbook wb = null;
        String fname="37桥面构造深度.xlsx";

        File f = new File(filepath+File.separator+proname+File.separator+htd+File.separator+fname);
        File fdir = new File(filepath + File.separator + proname + File.separator + htd);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        File directory = new File("service-system/src/main/resources/static");
        String reportPath = directory.getCanonicalPath();
        String filename = "";
        String sheetlmname = "桥";

        if (cdsl == 5){
            filename = "构造深度-5车道.xlsx";
        }else if (cdsl == 4){
            filename = "构造深度-4车道.xlsx";
        }else if (cdsl == 3){
            filename = "构造深度-3车道.xlsx";
        }else if (cdsl == 2){
            filename = "构造深度-2车道.xlsx";
        }

        String path = reportPath + File.separator + filename;
        Files.copy(Paths.get(path), new FileOutputStream(f));
        FileInputStream out = new FileInputStream(f);
        wb = new XSSFWorkbook(out);

        if (qlzxList.size() >0 && !qlzxList.isEmpty()){

            DBtoExcel(proname,htd,qlzxList,wb,"左幅-"+sheetlmname,cdsl,sjz);
        }
        if (qlyxList.size() >0 && !qlyxList.isEmpty()){

            DBtoExcel(proname,htd,qlyxList,wb,"右幅-"+sheetlmname,cdsl,sjz);
        }

        String[] arr = {"右幅-匝道路面","左幅-匝道路面","右幅-匝道隧道","左幅-匝道隧道","左幅-匝道桥","右幅-匝道桥","左幅-路面","右幅-路面","左幅-隧道","右幅-隧道","左幅-桥","右幅-桥"};
        for (int i = 0; i < arr.length; i++) {
            if (shouldBeCalculate(wb.getSheet(arr[i]))) {
                calculateTunnelAndBridgeSheet(wb, wb.getSheet(arr[i]), cdsl);
                calculateTotalForEvaluate(wb, wb.getSheet(arr[i]), cdsl);
                JjgFbgcCommonUtils.updateFormula(wb, wb.getSheet(arr[i]));
            } else {
                wb.removeSheetAt(wb.getSheetIndex(arr[i]));
            }
        }
        //}
        //wb.removeSheetAt(wb.getSheetIndex("保证率系数"));

        FileOutputStream fileOut = new FileOutputStream(f);
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        out.close();
        wb.close();
    }

    /**
     * 计算沥青左右幅隧道,桥梁，匝道桥，匝道隧道
     * @param wb
     * @param sheet
     */
    private void calculateTunnelAndBridgeSheet(XSSFWorkbook wb,XSSFSheet sheet,int cdsl) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        boolean flag = false;
        int count = 0;

        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            // 下一张表
            if (!"".equals(row.getCell(3-3).toString()) && row.getCell(3-3).toString().contains("质量鉴定表") && flag) {
                flag = false;
            }
            if(flag){
                rowstart = sheet.getRow(i);
                rowend = sheet.getRow(i+26);
                calculateTotalData(sheet, rowstart, rowend, i,cdsl);
                count ++;
                i += 26;
            }
            if ("桩号".equals(row.getCell(3-3).toString())) {
                rowstart = sheet.getRow(i+3);
                rowend = sheet.getRow(i+29);
                calculateTotalData(sheet, rowstart, rowend, i+3,cdsl);
                i += 29;
                count ++;
                flag = true;
            }
        }
        setTunnelAndBrodgeTotal(wb,sheet, count,cdsl);
    }

    /**
     *
     * @param wb
     * @param sheet
     * @param count
     */
    public void setTunnelAndBrodgeTotal(XSSFWorkbook wb,XSSFSheet sheet, int count,int cdsl){
        FormulaEvaluator e = new XSSFFormulaEvaluator(wb);
        String m = "";
        String n = "";
        if (cdsl == 2){
            m="G";
            n="K";
        }else if (cdsl == 3){
            m="I";
            n="N";
        }else if (cdsl == 4){
            m="K";
            n="Q";
        }else if (cdsl == 5){
            m="M";
            n="T";
        }
        for(int i = 0; i < count; i++){
            sheet.getRow(i+6).createCell(4*cdsl+5).setCellFormula("OFFSET($"+m+"$3,((ROW()-7)*33),0,1,1)");//Q==OFFSET($H$3,((ROW()-7)*33),0,1,1)
            String name = e.evaluate(sheet.getRow(i+6).getCell(4*cdsl+5)).getStringValue();
            sheet.getRow(i+6).getCell(4*cdsl+5).setCellFormula(null);
            sheet.getRow(i+6).getCell(4*cdsl+5).setCellValue(name);

            for(int j=0;j<6;j++){
                sheet.getRow(i+6).createCell(j+(4*cdsl+6)).setCellFormula("OFFSET("+n+""+(28+j)+",((ROW()-7)*33),0,1,1)");//R=OFFSET(N28,((ROW()-7)*26),0,1,1)
                double value = e.evaluate(sheet.getRow(i+6).getCell(j+(4*cdsl+6))).getNumberValue();
                sheet.getRow(i+6).getCell(j+(4*cdsl+6)).setCellFormula(null);
                if((j == 0 || j == 1) && value < 0.0001){
                    sheet.getRow(i+6).getCell(j+(4*cdsl+6)).setCellValue("");
                }
                else{
                    sheet.getRow(i+6).getCell(j+(4*cdsl+6)).setCellValue(value);
                }
            }
        }
        sheet.getRow(3-3).createCell(4*cdsl+6).setCellFormula("MAX("+sheet.getRow(6).getCell(4*cdsl+6).getReference()+":"
                +sheet.getRow(6+count).createCell(4*cdsl+6).getReference()+")");
        double value = e.evaluate(sheet.getRow(3-3).getCell(4*cdsl+6)).getNumberValue();
        sheet.getRow(3-3).getCell(4*cdsl+6).setCellFormula(null);
        if(value < 0.0001){
            sheet.getRow(3-3).getCell(4*cdsl+6).setCellValue("");
        }
        else{
            sheet.getRow(3-3).getCell(4*cdsl+6).setCellValue(value);
        }

        sheet.getRow(3-3).createCell(4*cdsl+7).setCellFormula("MIN("+sheet.getRow(6).getCell(4*cdsl+7).getReference()+":"
                +sheet.getRow(6+count).createCell(4*cdsl+7).getReference()+")");
        value = e.evaluate(sheet.getRow(3-3).getCell(4*cdsl+7)).getNumberValue();
        sheet.getRow(3-3).getCell(4*cdsl+7).setCellFormula(null);
        if(value < 0.0001){
            sheet.getRow(3-3).getCell(4*cdsl+7).setCellValue("");
        }
        else{
            sheet.getRow(3-3).getCell(4*cdsl+7).setCellValue(value);
        }

        sheet.getRow(3-3).createCell(4*cdsl+10).setCellFormula("SUM("+sheet.getRow(6).getCell(4*cdsl+10).getReference()+":"
                +sheet.getRow(6+count).createCell(4*cdsl+10).getReference()+")");
        value = e.evaluate(sheet.getRow(3-3).getCell(4*cdsl+10)).getNumberValue();
        sheet.getRow(3-3).getCell(4*cdsl+10).setCellFormula(null);
        sheet.getRow(3-3).getCell(4*cdsl+10).setCellValue(value);

        sheet.getRow(3-3).createCell(4*cdsl+11).setCellFormula("SUM("+sheet.getRow(6).getCell(4*cdsl+11).getReference()+":"
                +sheet.getRow(6+count).createCell(4*cdsl+11).getReference()+")");
        value = e.evaluate(sheet.getRow(3-3).getCell(4*cdsl+11)).getNumberValue();
        sheet.getRow(3-3).getCell(4*cdsl+11).setCellFormula(null);
        sheet.getRow(3-3).getCell(4*cdsl+11).setCellValue(value);
    }

    /**
     * 统计每一座桥及隧道的数据，以备评定时使用
     * @param sheet
     */
    private void calculateTotalForEvaluate(XSSFWorkbook wb,XSSFSheet sheet,int cdsl){
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        String name = "";
        XSSFFormulaEvaluator e = new XSSFFormulaEvaluator(wb);


        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            if (row.getCell(4*cdsl+5) != null && !"".equals(e.evaluate(row.getCell(4*cdsl+5)).getStringValue()) && rowstart != null) {
                rowend = sheet.getRow(i-1);
                rowstart.createCell(4*cdsl+12).setCellFormula("SUM("+rowstart.getCell(4*cdsl+10).getReference()
                        +":"+rowend.getCell(4*cdsl+10).getReference()+")");
                double value = e.evaluate(rowstart.getCell(4*cdsl+12)).getNumberValue();
                rowstart.getCell(4*cdsl+12).setCellFormula(null);
                rowstart.getCell(4*cdsl+12).setCellValue(value);

                rowstart.createCell(4*cdsl+13).setCellFormula("SUM("+rowstart.getCell(4*cdsl+11).getReference()
                        +":"+rowend.getCell(4*cdsl+11).getReference()+")");
                value = e.evaluate(rowstart.getCell(4*cdsl+13)).getNumberValue();
                rowstart.getCell(4*cdsl+13).setCellFormula(null);
                rowstart.getCell(4*cdsl+13).setCellValue(value);

                rowstart.createCell(4*cdsl+14).setCellFormula(rowstart.getCell(4*cdsl+13).getReference()+"*100/"
                        +rowstart.getCell(4*cdsl+12).getReference());
                value = e.evaluate(rowstart.getCell(4*cdsl+14)).getNumberValue();
                rowstart.getCell(4*cdsl+14).setCellFormula(null);
                rowstart.getCell(4*cdsl+14).setCellValue(value);

                rowstart = row;
                name = e.evaluate(row.getCell(4*cdsl+5)).getStringValue();
            }
            else if(row.getCell(4*cdsl+5) != null && "".equals(e.evaluate(row.getCell(4*cdsl+5)).getStringValue()) && rowstart != null){
                rowend = sheet.getRow(i);
            }
            if (row.getCell(4*cdsl+5) != null && !"".equals(e.evaluate(row.getCell(4*cdsl+5)).getStringValue()) && rowstart == null) {
                rowstart = row;
                rowend = rowstart;
                name = e.evaluate(row.getCell(4*cdsl+5)).getStringValue();
            }
        }
        if(rowend == null){
            rowend = rowstart;
        }
        rowstart.createCell(4*cdsl+12).setCellFormula("SUM("+rowstart.getCell(4*cdsl+10).getReference()
                +":"+rowend.getCell(4*cdsl+10).getReference()+")");
        double value = e.evaluate(rowstart.getCell(4*cdsl+12)).getNumberValue();
        rowstart.getCell(4*cdsl+12).setCellFormula(null);
        rowstart.getCell(4*cdsl+12).setCellValue(value);

        rowstart.createCell(4*cdsl+13).setCellFormula("SUM("+rowstart.getCell(4*cdsl+11).getReference()
                +":"+rowend.getCell(4*cdsl+11).getReference()+")");
        value = e.evaluate(rowstart.getCell(4*cdsl+13)).getNumberValue();
        rowstart.getCell(4*cdsl+13).setCellFormula(null);
        rowstart.getCell(4*cdsl+13).setCellValue(value);

        rowstart.createCell(4*cdsl+14).setCellFormula(rowstart.getCell(4*cdsl+13).getReference()+"*100/"
                +rowstart.getCell(4*cdsl+12).getReference());
        value = e.evaluate(rowstart.getCell(4*cdsl+14)).getNumberValue();
        rowstart.getCell(4*cdsl+14).setCellFormula(null);
        rowstart.getCell(4*cdsl+14).setCellValue(value);
    }

    /**
     *
     * @param sheet
     * @param rowstart
     * @param rowend
     * @param i
     */
    public void calculateTotalData(XSSFSheet sheet, XSSFRow rowstart, XSSFRow rowend, int i,int cdsl){
        //平均值
        sheet.getRow(i+23).getCell(3*cdsl+4).setCellFormula("IF(ISERROR(AVERAGE("+rowstart.getCell(1).getReference()+":"
                +rowend.getCell(cdsl).getReference()+","
                +rowstart.getCell(cdsl+2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+","
                +rowstart.getCell(2*cdsl+3).getReference()+":"
                +rowend.getCell(3*cdsl+2).getReference()+","
                +rowstart.getCell(3*cdsl+4).getReference()+":"
                +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+")),\"-\","+

                "AVERAGE("+rowstart.getCell(1).getReference()+":"
                +rowend.getCell(cdsl).getReference()+","
                +rowstart.getCell(cdsl+2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+","
                +rowstart.getCell(2*cdsl+3).getReference()+":"
                +rowend.getCell(3*cdsl+2).getReference()+","
                +rowstart.getCell(3*cdsl+4).getReference()+":"
                +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"))");

        //标准差
        sheet.getRow(i+24).getCell(3*cdsl+4).setCellFormula("IF(ISERROR(ROUND(STDEV("+rowstart.getCell(1).getReference()+":"
                +rowend.getCell(cdsl).getReference()+","
                +rowstart.getCell(cdsl+2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+","
                +rowstart.getCell(2*cdsl+3).getReference()+":"
                +rowend.getCell(3*cdsl+2).getReference()+","
                +rowstart.getCell(3*cdsl+4).getReference()+":"
                +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"),3)),\"-\","+
                "ROUND(STDEV("+rowstart.getCell(1).getReference()+":"
                +rowend.getCell(cdsl).getReference()+","
                +rowstart.getCell(cdsl+2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+","
                +rowstart.getCell(2*cdsl+3).getReference()+":"
                +rowend.getCell(3*cdsl+2).getReference()+","
                +rowstart.getCell(3*cdsl+4).getReference()+":"
                +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"),3))");//=ROUND(STDEV(E7:F33,H7:I33,K7:L33,N7:O25),3)

        //检测点数
        sheet.getRow(i+25).getCell(3*cdsl+4).setCellFormula("COUNT("+rowstart.getCell(1).getReference()+":"
                +rowend.getCell(cdsl).getReference()+","
                +rowstart.getCell(cdsl+2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+","
                +rowstart.getCell(2*cdsl+3).getReference()+":"
                +rowend.getCell(3*cdsl+2).getReference()+","
                +rowstart.getCell(3*cdsl+4).getReference()+":"
                +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+")");//=COUNT(E7:F33,H7:I33,K7:L33,N7:O25)

        //合格点数
        sheet.getRow(i+26).getCell(3*cdsl+4).setCellFormula("SUM(COUNTIF("+rowstart.getCell(1).getReference()+":"
                +rowend.getCell(cdsl).getReference()+",\">=\"&"
                +sheet.getRow(i+19).getCell(4*cdsl+3).getReference()+"),COUNTIF("
                +rowstart.getCell(cdsl+2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+",\">=\"&"
                +sheet.getRow(i+19).getCell(4*cdsl+3).getReference()+"),COUNTIF("
                +rowstart.getCell(2*cdsl+3).getReference()+":"
                +rowend.getCell(3*cdsl+2).getReference()+",\">=\"&"
                +sheet.getRow(i+19).getCell(4*cdsl+3).getReference()+"),COUNTIF("
                +rowstart.getCell(3*cdsl+4).getReference()+":"
                +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+",\">=\"&"
                +sheet.getRow(i+19).getCell(4*cdsl+3).getReference()+"))");

        //最大值
        sheet.getRow(i+21).getCell(3*cdsl+4).setCellFormula("IF("+sheet.getRow(i+25).getCell(3*cdsl+4).getReference()
                +"=0,\"-\",MAX("+rowstart.getCell(1).getReference()+":"
                +rowend.getCell(cdsl).getReference()+","
                +rowstart.getCell(cdsl+2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+","
                +rowstart.getCell(2*cdsl+3).getReference()+":"
                +rowend.getCell(3*cdsl+2).getReference()+","
                +rowstart.getCell(3*cdsl+4).getReference()+":"
                +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"))");
        //最小值
        sheet.getRow(i+22).getCell(3*cdsl+4).setCellFormula("IF("+sheet.getRow(i+25).getCell(3*cdsl+4).getReference()
                +"=0,\"-\",MIN("+rowstart.getCell(1).getReference()+":"
                +rowend.getCell(cdsl).getReference()+","
                +rowstart.getCell(cdsl+2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+","
                +rowstart.getCell(2*cdsl+3).getReference()+":"
                +rowend.getCell(3*cdsl+2).getReference()+","
                +rowstart.getCell(3*cdsl+4).getReference()+":"
                +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"))");
    }




    /**
     *判断sheet是否为空
     * @param sheet
     * @return
     */
    private boolean shouldBeCalculate(XSSFSheet sheet) {
        sheet.getRow(6).getCell(0).setCellType(CellType.STRING);
        if(sheet.getRow(6).getCell(0)==null ||"".equals(sheet.getRow(6).getCell(0).getStringCellValue())){
            return false;
        }

        return true;
    }

    /**
     *
     * @param proname
     * @param htd
     * @param data
     * @param wb
     * @param sheetname
     * @param cdsl
     * @param sjz
     */
    private void DBtoExcel(String proname, String htd, List<Map<String, Object>> data, XSSFWorkbook wb, String sheetname, int cdsl, String sjz) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        if (data!=null && !data.isEmpty()){
            createTable(getNum(data),wb,sheetname,cdsl);
            XSSFSheet sheet = wb.getSheet(sheetname);
            String time = String.valueOf(data.get(0).get("createTime")) ;
            Date parse = simpleDateFormat.parse(time);
            String sj = outputDateFormat.format(parse);
            sheet.getRow(1).getCell(2).setCellValue(proname);
            sheet.getRow(1).getCell(cdsl*3+4).setCellValue(htd);
            sheet.getRow(2).getCell(cdsl*3+4).setCellValue(sj);

            String name = data.get(0).get("lxbs").toString();
            int index = 0;
            int tableNum = 0;

            fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
            for(int i =0; i < data.size(); i++){
                if (name.equals(data.get(i).get("lxbs"))){
                    if(index == 100){
                        tableNum ++;
                        fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
                        index = 0;

                    }
                    fillCommonCellData(sheet, tableNum, index, data.get(i),cdsl);
                    index ++;

                }else {
                    name = data.get(i).get("lxbs").toString();
                    tableNum ++;
                    index = 0;
                    fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
                    fillCommonCellData(sheet, tableNum, index, data.get(i),cdsl);
                    index += 1;
                }
            }
        }

    }

    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param row
     * @param cdsl
     */
    private void fillCommonCellData(XSSFSheet sheet, int tableNum, int index, Map<String, Object> row,int cdsl) {
        String[] sfc = row.get("mtd").toString().split(",");
        for (int i = 0 ; i < sfc.length ; i++) {
            sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)).setCellValue(Double.valueOf(row.get("qdzh").toString()));
            if (sfc[i].equals("-")){
                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)+1+i).setCellValue("-");
            }else {
                sheet.getRow(tableNum * 33 + 6 + index % 27).getCell((cdsl+1) * (index / 27)+1+i).setCellValue(Double.parseDouble(sfc[i]));
            }

        }
    }


    /**
     *
     * @param sheet
     * @param tableNum
     * @param proname
     * @param htd
     * @param name
     * @param time
     * @param sheetname
     * @param cdsl
     * @param sjz
     */
    private void fillTitleCellData(XSSFSheet sheet, int tableNum, String proname, String htd, String name,String time,String sheetname,int cdsl,String sjz) {
        String fbgcname = "桥面系";
        sheet.getRow(tableNum * 33 + 1).getCell(2).setCellValue(proname);
        sheet.getRow(tableNum * 33 + 1).createCell(cdsl*3+4).setCellType(CellType.STRING);
        sheet.getRow(tableNum * 33 + 1).getCell(cdsl*3+4).setCellValue(htd);
        sheet.getRow(tableNum * 33 + 2).getCell(2).setCellValue("路面工程");
        sheet.getRow(tableNum * 33 + 2).getCell(cdsl*3+4).setCellValue(time);
        sheet.getRow(tableNum * 33 + 2).getCell(cdsl*2+2).setCellValue(fbgcname+"("+name+")");
        sheet.getRow(tableNum * 33 + 25).getCell(cdsl*4+3).setCellValue(Double.parseDouble(sjz));
    }

    /**
     *
     * @param num
     * @param wb
     * @param sheetname
     * @param cdsl
     */
    private void createTable(int num, XSSFWorkbook wb, String sheetname, int cdsl) {
        int record = 0;
        record = num;
        for (int i = 1; i < record; i++) {
            RowCopy.copyRows(wb, sheetname, sheetname, 0, 32, i * 33);
        }
        if(record >= 1){
            wb.setPrintArea(wb.getSheetIndex(sheetname), 0, 4*cdsl+3, 0,(record) * 33 - 1);
        }
    }

    /**
     *
     * @param data
     * @return
     */
    private int getNum(List<Map<String, Object>> data) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> map : data) {
            String name = map.get("lxbs").toString();
            if (resultMap.containsKey(name)) {
                resultMap.put(name, resultMap.get(name) + 1);
            } else {
                resultMap.put(name, 1);
            }
        }
        int num = 0;
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            int value = entry.getValue();
            if (value%100==0){
                num += value/100;
            }else {
                num += value/100+1;
            }
        }
        return num;
    }

    /**
     *将相同幅的mtd拼接
     * @param list
     * @return
     */
    private static List<Map<String, Object>> groupByZh(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()){
            return new ArrayList<>();
        }else {
            Map<String, List<String>> resultMap = new TreeMap<>();
            for (Map<String, Object> map : list) {
                String zh = map.get("qdzh").toString();
                String sfc = "";
                if (map.get("mtd") == null){
                    sfc = "-";
                }else {
                    sfc = map.get("mtd").toString();
                }
                //String sfc = map.get("mtd").toString();
                if (resultMap.containsKey(zh)) {
                    resultMap.get(zh).add(sfc);
                } else {
                    List<String> sfcList = new ArrayList<>();
                    sfcList.add(sfc);
                    resultMap.put(zh, sfcList);
                }
            }
            List<Map<String, Object>> resultList = new LinkedList<>();
            for (Map.Entry<String, List<String>> entry : resultMap.entrySet()) {
                Map<String, Object> map = new TreeMap<>();
                map.put("qdzh", entry.getKey());
                map.put("mtd", String.join(",", entry.getValue()));
                for (Map<String, Object> item : list) {
                    if (item.get("qdzh").toString().equals(entry.getKey())) {
                        map.put("lxbs", item.get("lxbs"));
                        map.put("createTime", item.get("createTime"));
                        break;
                    }
                }
                resultList.add(map);
            }
            Collections.sort(resultList, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    String name1 = o1.get("lxbs").toString();
                    String name2 = o2.get("lxbs").toString();
                    return name1.compareTo(name2);
                }
            });

            return resultList;
        }
    }

    @Override
    public List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat decf = new DecimalFormat("0.##");
        int cds = getcds(proname,htd);
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + "37桥面构造深度.xlsx");
        if (!f.exists()) {
            return new ArrayList<>();
        } else {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));
            List<Map<String, Object>> jgmap = new ArrayList<>();
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                if (!wb.isSheetHidden(wb.getSheetIndex(wb.getSheetAt(j)))) {
                    XSSFSheet slSheet = wb.getSheetAt(j);

                    XSSFCell xmname = slSheet.getRow(1).getCell(2);//项目名
                    XSSFCell htdname = slSheet.getRow(1).getCell(cds*3+4);//合同段名


                    if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                        slSheet.getRow(0).getCell(4*cds+10).setCellType(CellType.STRING);//总点数
                        slSheet.getRow(0).getCell(4*cds+11).setCellType(CellType.STRING);//合格点数

                        slSheet.getRow(0).getCell(4*cds+6).setCellType(CellType.STRING);//max
                        slSheet.getRow(0).getCell(4*cds+7).setCellType(CellType.STRING);//min
                        slSheet.getRow(25).getCell(cds*4+3).setCellType(CellType.STRING);

                        double zds = Double.valueOf(slSheet.getRow(0).getCell(4*cds+10).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(0).getCell(4*cds+11).getStringCellValue());
                        String zdsz1 = decf.format(zds);
                        String hgdsz1 = decf.format(hgds);
                        Map map = new HashMap();
                        map.put("路面类型", wb.getSheetName(j));
                        map.put("总点数", zdsz1);
                        map.put("设计值", slSheet.getRow(25).getCell(cds*4+3).getStringCellValue());
                        map.put("合格点数", hgdsz1);
                        map.put("最大值", slSheet.getRow(0).getCell(4*cds+6).getStringCellValue());
                        map.put("最小值", slSheet.getRow(0).getCell(4*cds+7).getStringCellValue());
                        jgmap.add(map);
                    }
                }
            }
            return jgmap;
        }
    }

    /**
     *
     * @param proname
     * @param htd
     * @return
     */
    private int getcds(String proname, String htd) {
        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhgzsdMapper.selectlx(proname,htd);
        int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhgzsdMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        return cds;
    }


    @Override
    public void exportgzsd(HttpServletResponse response, String cdsl) throws IOException {
        int cd = Integer.parseInt(cdsl);
        String fileName = "桥面构造深度实测数据";
        String[][] sheetNames = {
                {"左幅一车道","左幅二车道","右幅一车道","右幅二车道"},
                {"左幅一车道","左幅二车道","左幅三车道","右幅一车道","右幅二车道","右幅三车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","左幅五车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道","右幅五车道"}
        };
        String[] sheetName = sheetNames[cd-2];
        ExcelUtil.writeExcelMultipleSheets(response, null, fileName, sheetName, new JjgFbgcQlgcZdhgzsdVo());

    }

    @Override
    public void importgzsd(MultipartFile file, CommonInfoVo commonInfoVo) throws IOException {
        // 获取文件输入流
        InputStream inputStream = file.getInputStream();
        // 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        int number = workbook.getNumberOfSheets();
        for (int i = 0; i < number; i++) {
            String sheetName = workbook.getSheetName(i);
            int sheetIndex = workbook.getSheetIndex(workbook.getSheetAt(i));
            try {
                EasyExcel.read(file.getInputStream())
                        .sheet(sheetIndex)
                        .head(JjgFbgcQlgcZdhgzsdVo.class)
                        .headRowNumber(1)
                        .registerReadListener(
                                new ExcelHandler<JjgFbgcQlgcZdhgzsdVo>(JjgFbgcQlgcZdhgzsdVo.class) {
                                    @Override
                                    public void handle(List<JjgFbgcQlgcZdhgzsdVo> dataList) {
                                        for(JjgFbgcQlgcZdhgzsdVo gzsdVo: dataList)
                                        {
                                            JjgFbgcQlgcZdhgzsd gzsd = new JjgFbgcQlgcZdhgzsd();
                                            BeanUtils.copyProperties(gzsdVo,gzsd);
                                            gzsd.setCreatetime(new Date());
                                            gzsd.setProname(commonInfoVo.getProname());
                                            gzsd.setHtd(commonInfoVo.getHtd());
                                            gzsd.setQdzh(gzsdVo.getQdzh());
                                            gzsd.setZdzh(gzsdVo.getZdzh());
                                            gzsd.setCd(sheetName);
                                            if (sheetName.contains("一")){
                                                gzsd.setVal(1);
                                            }else if (sheetName.contains("二")){
                                                gzsd.setVal(2);
                                            }else if (sheetName.contains("三")){
                                                gzsd.setVal(3);
                                            }else if (sheetName.contains("四")){
                                                gzsd.setVal(4);
                                            }else if (sheetName.contains("五")){
                                                gzsd.setVal(5);
                                            }
                                            gzsd.setCd(sheetName);
                                            jjgFbgcQlgcZdhgzsdMapper.insert(gzsd);
                                        }
                                    }
                                }
                        ).doRead();
            } catch (IOException e) {
                throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
            }
        }

        // 关闭输入流
        inputStream.close();

    }

    @Override
    public List<Map<String, Object>> selectlx(String proname, String htd) {
        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhgzsdMapper.selectlx(proname,htd);
        return lxlist;
    }
}
