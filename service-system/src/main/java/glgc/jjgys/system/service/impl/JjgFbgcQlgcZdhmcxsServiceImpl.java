package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.qlgc.JjgFbgcQlgcZdhmcxsVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcQlgcZdhmcxsMapper;
import glgc.jjgys.system.service.JjgFbgcQlgcZdhmcxsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.RowCopy;
import org.apache.commons.lang3.StringUtils;
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
public class JjgFbgcQlgcZdhmcxsServiceImpl extends ServiceImpl<JjgFbgcQlgcZdhmcxsMapper, JjgFbgcQlgcZdhmcxs> implements JjgFbgcQlgcZdhmcxsService {

    @Autowired
    private JjgFbgcQlgcZdhmcxsMapper jjgFbgcQlgcZdhmcxsMapper;

    @Value(value = "${jjgys.path.filepath}")
    private String filepath;

    @Override
    public void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();

        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhmcxsMapper.selectlx(proname,htd);
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhmcxsMapper.selectcdnum(proname,htd,zx);
            int cds = 0;
            if (num == 1){
                cds = 2;
            }else {
                cds=num;
            }
            handlezxData(proname,htd,zx,cds,commonInfoVo.getSjz());
        }
        /*int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhmcxsMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        handlezxData(proname,htd,cds,commonInfoVo.getSjz());*/
    }


    /**
     *
     * @param proname
     * @param htd
     * @param zx
     * @param cdsl
     * @param sjz
     * @throws IOException
     * @throws ParseException
     */
    private void handlezxData(String proname, String htd, String zx, int cdsl, String sjz) throws IOException, ParseException {

        List<Map<String, Object>> datazf = jjgFbgcQlgcZdhmcxsMapper.selectzfList(proname, htd,zx);
        List<Map<String, Object>> datayf = jjgFbgcQlgcZdhmcxsMapper.selectyfList(proname, htd,zx);

        //处理数据

        List<Map<String, Object>> lmzfList = groupByZh(datazf);
        List<Map<String, Object>> lmyfList = groupByZh(datayf);

        writeExcelData(proname, htd, lmzfList, lmyfList, cdsl, sjz,zx);



    }

    /**
     *
     * @param proname
     * @param htd
     * @param qlzxList
     * @param qlyxList
     * @param cdsl
     * @param sjz
     * @param zx
     * @throws IOException
     * @throws ParseException
     */
    private void writeExcelData(String proname, String htd, List<Map<String, Object>> qlzxList, List<Map<String, Object>> qlyxList, int cdsl, String sjz, String zx) throws IOException, ParseException {
        XSSFWorkbook wb = null;
        String fname="36桥面摩擦系数-"+zx+".xlsx";

        File f = new File(filepath+File.separator+proname+File.separator+htd+File.separator+fname);
        File fdir = new File(filepath + File.separator + proname + File.separator + htd);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        try {
            File directory = new File("service-system/src/main/resources/static");
            String reportPath = directory.getCanonicalPath();
            String filename = "";
            String sheetqname = "桥";
            if (cdsl == 5){
                filename = "摩擦系数-5车道.xlsx";
            }else if (cdsl == 4){
                filename = "摩擦系数-4车道.xlsx";
            }else if (cdsl == 3){
                filename = "摩擦系数-3车道.xlsx";
            }else if (cdsl == 2){
                filename = "摩擦系数-2车道.xlsx";
            }

            String path = reportPath + File.separator + filename;
            Files.copy(Paths.get(path), new FileOutputStream(f));

            FileInputStream out = new FileInputStream(f);
            wb = new XSSFWorkbook(out);

            if (qlzxList.size() >0 && !qlzxList.isEmpty()){
                DBtoExcel(proname,htd,qlzxList,wb,"左幅-"+sheetqname,cdsl,sjz);
            }
            if (qlyxList.size() >0 && !qlyxList.isEmpty()){
                DBtoExcel(proname,htd,qlyxList,wb,"右幅-"+sheetqname,cdsl,sjz);
            }


            String[] arr = new String[]{"左幅-路面","右幅-路面","左幅-桥","右幅-桥","左幅-隧道","右幅-隧道"};

            for (int i = 0;i<arr.length;i++){
                if (shouldBeCalculate(wb.getSheet(arr[i]))){
                    calculateSheet(wb,wb.getSheet(arr[i]),cdsl);
                    JjgFbgcCommonUtils.updateFormula(wb, wb.getSheet(arr[i]));
                }else {
                    wb.removeSheetAt(wb.getSheetIndex(arr[i]));
                }
            }
            wb.removeSheetAt(wb.getSheetIndex("保证率系数"));
            System.out.println("成功生成鉴定表");
            FileOutputStream fileOut = new FileOutputStream(f);
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();
            out.close();
            wb.close();
        }catch (Exception e) {
            if(f.exists()){
                f.delete();
            }
            throw new JjgysException(20001, "生成鉴定表错误，请检查数据的正确性");
        }

    }

    /**
     *
     * @param sheet
     */
    private void calculateSheet(XSSFWorkbook wb,XSSFSheet sheet,int cds) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        boolean flag = false;
        XSSFRow recordrow = null;
        String name = "";

        FormulaEvaluator e = new XSSFFormulaEvaluator(wb);

        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            // 下一张表
            if (!"".equals(row.getCell(0).toString()) && row.getCell(0).toString().contains("质量鉴定表") && flag) {
                flag = false;
            }
            if(flag){
                if(i == rowend.getRowNum()){
                    /*
                     * 此处对总计进行计算
                     */
                    calculateTotalData(sheet, rowstart, rowend,e,cds);
                    if(i+38 < sheet.getPhysicalNumberOfRows()){
                        rowstart = sheet.getRow(i+1);
                        rowend = sheet.getRow(i+38);
                    }
                    else{
                        break;
                    }
                }
            }

            if ("桩号".equals(row.getCell(0).toString())) {
                rowstart = sheet.getRow(i+3);
                rowend = sheet.getRow(i+40);
                i += 2;
                flag = true;
                if(recordrow == null){
                    recordrow = rowstart;
                }
                int cdnum = 0;
                if (cds == 2 || cds == 3 ){
                    cdnum = cds+2;
                }else if (cds == 5) {
                    cdnum = 5;
                }else {
                    cdnum = cds+3;
                }
                if (cds == 5){
                    if(!"".equals(name) && !name.equals(sheet.getRow(i-1-2-1).getCell(cdnum).toString())){

                        sheet.getRow(recordrow.getRowNum()-4).createCell(12).setCellFormula("SUM("
                                +recordrow.createCell(12).getReference()+":"
                                +sheet.getRow(i-4-2).createCell(12).getReference()+")");

                        double value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(12)).getNumberValue();
                        sheet.getRow(recordrow.getRowNum()-4).getCell(12).setCellFormula(null);
                        sheet.getRow(recordrow.getRowNum()-4).getCell(12).setCellValue(value);

                        sheet.getRow(recordrow.getRowNum()-4).createCell(13).setCellFormula("SUM("
                                +recordrow.createCell(13).getReference()+":"
                                +sheet.getRow(i-4-2).createCell(13).getReference()+")");
                        value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(13)).getNumberValue();
                        sheet.getRow(recordrow.getRowNum()-4).getCell(13).setCellFormula(null);
                        sheet.getRow(recordrow.getRowNum()-4).getCell(13).setCellValue(value);

                        sheet.getRow(recordrow.getRowNum()-4).createCell(14).setCellFormula(
                                sheet.getRow(recordrow.getRowNum()-4).getCell(13).getReference()+"*100/"
                                        + sheet.getRow(recordrow.getRowNum()-4).getCell(12).getReference());

                        value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(14)).getNumberValue();
                        sheet.getRow(recordrow.getRowNum()-4).getCell(14).setCellFormula(null);
                        sheet.getRow(recordrow.getRowNum()-4).getCell(14).setCellValue(value);

                        recordrow = rowstart;
                    }
                    name = sheet.getRow(i-1-2-1).getCell(cdnum).toString();

                }else {
                    if(!"".equals(name) && !name.equals(sheet.getRow(i-1-2-1).getCell(cdnum).toString())){

                        sheet.getRow(recordrow.getRowNum()-4).createCell(3*cds+3).setCellFormula("SUM("
                                +recordrow.createCell(3*cds+3).getReference()+":"
                                +sheet.getRow(i-4-2).createCell(3*cds+3).getReference()+")");

                        double value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+3)).getNumberValue();
                        sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+3).setCellFormula(null);
                        sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+3).setCellValue(value);

                        sheet.getRow(recordrow.getRowNum()-4).createCell(3*cds+4).setCellFormula("SUM("
                                +recordrow.createCell(3*cds+4).getReference()+":"
                                +sheet.getRow(i-4-2).createCell(3*cds+4).getReference()+")");
                        value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+4)).getNumberValue();
                        sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+4).setCellFormula(null);
                        sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+4).setCellValue(value);

                        sheet.getRow(recordrow.getRowNum()-4).createCell(3*cds+5).setCellFormula(
                                sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+4).getReference()+"*100/"
                                        + sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+3).getReference());

                        value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+5)).getNumberValue();
                        sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+5).setCellFormula(null);
                        sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+5).setCellValue(value);

                        recordrow = rowstart;
                    }
                    name = sheet.getRow(i-1-2-1).getCell(cdnum).toString();
                }

            }
        }
        if (cds == 5){
            //计算最后一座桥或隧道
            sheet.getRow(recordrow.getRowNum()-4).createCell(12)
                    .setCellFormula("SUM(" +recordrow.createCell(12).getReference()+":"
                            +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).createCell(12).getReference()+")");
            double value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(12)).getNumberValue();
            sheet.getRow(recordrow.getRowNum()-4).getCell(12).setCellFormula(null);
            sheet.getRow(recordrow.getRowNum()-4).getCell(12).setCellValue(value);

            sheet.getRow(recordrow.getRowNum()-4).createCell(13).setCellFormula("SUM("
                    +recordrow.createCell(13).getReference()+":"
                    +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).createCell(13).getReference()+")");
            value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(13)).getNumberValue();
            sheet.getRow(recordrow.getRowNum()-4).getCell(13).setCellFormula(null);
            sheet.getRow(recordrow.getRowNum()-4).getCell(13).setCellValue(value);

            sheet.getRow(recordrow.getRowNum()-4).createCell(14).setCellFormula(
                    sheet.getRow(recordrow.getRowNum()-4).getCell(13).getReference()+"*100/"
                            + sheet.getRow(recordrow.getRowNum()-4).getCell(12).getReference());
            value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(14)).getNumberValue();
            sheet.getRow(recordrow.getRowNum()-4).getCell(14).setCellFormula(null);
            sheet.getRow(recordrow.getRowNum()-4).getCell(14).setCellValue(value);


            /**
             * 将本sheet的所有数据汇总到第一行 ok
             */


            sheet.getRow(0).createCell(12).setCellValue("总检测点数");
            sheet.getRow(0).createCell(13).setCellValue("总合格点数");
            sheet.getRow(0).createCell(14).setCellValue("合格率");
            sheet.getRow(0).createCell(15).setCellValue("最大SFC代表值");
            sheet.getRow(0).createCell(16).setCellValue("最小SFC代表值");

            sheet.getRow(1).createCell(12).setCellFormula("SUM("
                    +sheet.getRow(2).getCell(12).getReference()+":"
                    +sheet.getRow(sheet.getLastRowNum()).createCell(12).getReference()+")/2");//W1=SUM(W3:W825)/2
            value = e.evaluate(sheet.getRow(1).getCell(12)).getNumberValue();
            sheet.getRow(1).getCell(12).setCellFormula(null);
            sheet.getRow(1).getCell(12).setCellValue(value);

            sheet.getRow(1).createCell(13).setCellFormula("SUM("
                    +sheet.getRow(2).getCell(13).getReference()+":"
                    +sheet.getRow(sheet.getLastRowNum()).createCell(13).getReference()+")/2");//X1=SUM(X3:X825)/2
            value = e.evaluate(sheet.getRow(1).getCell(13)).getNumberValue();
            sheet.getRow(1).getCell(13).setCellFormula(null);
            sheet.getRow(1).getCell(13).setCellValue(value);

            sheet.getRow(1).createCell(14).setCellFormula(
                    sheet.getRow(1).getCell(13).getReference()+"*100/" //ok
                            +sheet.getRow(1).getCell(12).getReference());//ok
            value = e.evaluate(sheet.getRow(1).getCell(14)).getNumberValue();
            sheet.getRow(1).getCell(14).setCellFormula(null);
            sheet.getRow(1).getCell(14).setCellValue(value);


            sheet.getRow(1).createCell(15).setCellFormula("MAX("
                    +sheet.getRow(2).createCell(15).getReference()+":"
                    +sheet.getRow(sheet.getLastRowNum()).createCell(15).getReference()+")");//W1=MAX(W3:W825)/2
            value = e.evaluate(sheet.getRow(1).getCell(15)).getNumberValue();
            sheet.getRow(1).getCell(15).setCellFormula(null);
            sheet.getRow(1).getCell(15).setCellValue(value);

            sheet.getRow(1).createCell(16).setCellFormula("MIN("
                    +sheet.getRow(2).createCell(16).getReference()+":"
                    +sheet.getRow(sheet.getLastRowNum()).createCell(16).getReference()+")");//X1=MIN(X3:X825)/2
            value = e.evaluate(sheet.getRow(1).getCell(16)).getNumberValue();
            sheet.getRow(1).getCell(16).setCellFormula(null);
            sheet.getRow(1).getCell(16).setCellValue(value);
        }else {
            //计算最后一座桥或隧道
            sheet.getRow(recordrow.getRowNum()-4).createCell(3*cds+3)
                    .setCellFormula("SUM(" +recordrow.createCell(3*cds+3).getReference()+":"
                            +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).createCell(3*cds+3).getReference()+")");
            double value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+3)).getNumberValue();
            sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+3).setCellFormula(null);
            sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+3).setCellValue(value);

            sheet.getRow(recordrow.getRowNum()-4).createCell(3*cds+4).setCellFormula("SUM("
                    +recordrow.createCell(3*cds+4).getReference()+":"
                    +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).createCell(3*cds+4).getReference()+")");
            value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+4)).getNumberValue();
            sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+4).setCellFormula(null);
            sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+4).setCellValue(value);

            sheet.getRow(recordrow.getRowNum()-4).createCell(3*cds+5).setCellFormula(
                    sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+4).getReference()+"*100/"
                            + sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+3).getReference());
            value = e.evaluate(sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+5)).getNumberValue();
            sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+5).setCellFormula(null);
            sheet.getRow(recordrow.getRowNum()-4).getCell(3*cds+5).setCellValue(value);


            /**
             * 将本sheet的所有数据汇总到第一行 ok
             */


            sheet.getRow(0).createCell(3*cds+3).setCellValue("总检测点数");
            sheet.getRow(0).createCell(3*cds+4).setCellValue("总合格点数");
            sheet.getRow(0).createCell(3*cds+5).setCellValue("合格率");
            sheet.getRow(0).createCell(3*cds+6).setCellValue("最大SFC代表值");
            sheet.getRow(0).createCell(3*cds+7).setCellValue("最小SFC代表值");

            sheet.getRow(1).createCell(3*cds+3).setCellFormula("SUM("
                    +sheet.getRow(2).getCell(3*cds+3).getReference()+":"
                    +sheet.getRow(sheet.getLastRowNum()).createCell(3*cds+3).getReference()+")/2");//W1=SUM(W3:W825)/2
            value = e.evaluate(sheet.getRow(1).getCell(3*cds+3)).getNumberValue();
            sheet.getRow(1).getCell(3*cds+3).setCellFormula(null);
            sheet.getRow(1).getCell(3*cds+3).setCellValue(value);

            sheet.getRow(1).createCell(3*cds+4).setCellFormula("SUM("
                    +sheet.getRow(2).getCell(3*cds+4).getReference()+":"
                    +sheet.getRow(sheet.getLastRowNum()).createCell(3*cds+4).getReference()+")/2");//X1=SUM(X3:X825)/2
            value = e.evaluate(sheet.getRow(1).getCell(3*cds+4)).getNumberValue();
            sheet.getRow(1).getCell(3*cds+4).setCellFormula(null);
            sheet.getRow(1).getCell(3*cds+4).setCellValue(value);

            sheet.getRow(1).createCell(3*cds+5).setCellFormula(
                    sheet.getRow(1).getCell(3*cds+4).getReference()+"*100/" //ok
                            +sheet.getRow(1).getCell(3*cds+3).getReference());//ok
            value = e.evaluate(sheet.getRow(1).getCell(3*cds+5)).getNumberValue();
            sheet.getRow(1).getCell(3*cds+5).setCellFormula(null);
            sheet.getRow(1).getCell(3*cds+5).setCellValue(value);


            sheet.getRow(1).createCell(3*cds+6).setCellFormula("MAX("
                    +sheet.getRow(2).createCell(3*cds+6).getReference()+":"
                    +sheet.getRow(sheet.getLastRowNum()).createCell(3*cds+6).getReference()+")");//W1=MAX(W3:W825)/2
            value = e.evaluate(sheet.getRow(1).getCell(3*cds+6)).getNumberValue();
            sheet.getRow(1).getCell(3*cds+6).setCellFormula(null);
            sheet.getRow(1).getCell(3*cds+6).setCellValue(value);

            sheet.getRow(1).createCell(3*cds+7).setCellFormula("MIN("
                    +sheet.getRow(2).createCell(3*cds+7).getReference()+":"
                    +sheet.getRow(sheet.getLastRowNum()).createCell(3*cds+7).getReference()+")");//X1=MIN(X3:X825)/2
            value = e.evaluate(sheet.getRow(1).getCell(3*cds+7)).getNumberValue();
            sheet.getRow(1).getCell(3*cds+7).setCellFormula(null);
            sheet.getRow(1).getCell(3*cds+7).setCellValue(value);
        }


    }

    /**
     *  @param sheet
     * @param rowstart
     * @param rowend
     * @param e
     */
    private void calculateTotalData(XSSFSheet sheet, XSSFRow rowstart, XSSFRow rowend, FormulaEvaluator e,int cds) {
        if (cds == 5){
            //SFC平均值
            sheet.getRow(rowend.getRowNum()-5).getCell(9).setCellFormula("IF(ISERROR(AVERAGE("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(7).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(11).getReference()+")),\"-\","+
                    "AVERAGE("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(7).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(11).getReference()+"))");
            //标准差
            sheet.getRow(rowend.getRowNum()-4).getCell(9).setCellFormula("IF(ISERROR(ROUND(STDEV("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(7).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(11).getReference()+"),3)),\"-\","+
                    "ROUND(STDEV("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(7).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(11).getReference()+"),3))");

            //检测点数
            sheet.getRow(rowend.getRowNum()-1).getCell(7).setCellFormula("COUNT("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(7).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(11).getReference()+")");

            //合格点数
            sheet.getRow(rowend.getRowNum()).getCell(7).setCellFormula("SUM(COUNTIF("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+",\">=\"&"
                    +sheet.getRow(rowend.getRowNum()-6).getCell(9).getReference()+"),COUNTIF("
                    +rowstart.getCell(7).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(11).getReference()+",\">=\"&"
                    +sheet.getRow(rowend.getRowNum()-6).getCell(9).getReference()+"))");

            //SFC代表值
            sheet.getRow(rowend.getRowNum()-3).getCell(9).setCellFormula("IF(ISERROR(ROUND(IF("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(7).getReference()+">100,("
                    +sheet.getRow(rowend.getRowNum()-5).getCell(9).getReference()+"-"
                    +sheet.getRow(rowend.getRowNum()-4).getCell(9).getReference()+"*(1.6449/SQRT("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(7).getReference()+"))),("
                    +sheet.getRow(rowend.getRowNum()-5).getCell(9).getReference()+"-"
                    +sheet.getRow(rowend.getRowNum()-4).getCell(9).getReference()+"*VLOOKUP("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(7).getReference()+",保证率系数!$A:$D,3))),2)),\"-\","+
                    "ROUND(IF("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(7).getReference()+">100,("
                    +sheet.getRow(rowend.getRowNum()-5).getCell(9).getReference()+"-"
                    +sheet.getRow(rowend.getRowNum()-4).getCell(9).getReference()+"*(1.6449/SQRT("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(7).getReference()+"))),("
                    +sheet.getRow(rowend.getRowNum()-5).getCell(9).getReference()+"-"
                    +sheet.getRow(rowend.getRowNum()-4).getCell(9).getReference()+"*VLOOKUP("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(7).getReference()+",保证率系数!$A:$D,3))),2))");

            //结果
            sheet.getRow(rowend.getRowNum()-2).getCell(9).setCellFormula("IF("+sheet.getRow(rowend.getRowNum()-3).getCell(9).getReference()+"<>\"-\","+"IF("
                    +sheet.getRow(rowend.getRowNum()-3).getCell(9).getReference()+">="
                    +sheet.getRow(rowend.getRowNum()-6).getCell(9).getReference()+",\"合格\",\"不合格\"),\"-\")");

            //右上角统计的检测点数
            sheet.getRow(rowend.getRowNum()-4).createCell(12).setCellFormula(sheet.getRow(rowend.getRowNum()-1).getCell(7).getReference());
            double value = e.evaluate(sheet.getRow(rowend.getRowNum()-4).getCell(12)).getNumberValue();
            sheet.getRow(rowend.getRowNum()-4).getCell(12).setCellFormula(null);
            sheet.getRow(rowend.getRowNum()-4).getCell(12).setCellValue(value);

            //右上角统计的合格点数
            sheet.getRow(rowend.getRowNum()-4).createCell(13).setCellFormula(sheet.getRow(rowend.getRowNum()).getCell(7).getReference());
            value = e.evaluate(sheet.getRow(rowend.getRowNum()-4).getCell(13)).getNumberValue();
            sheet.getRow(rowend.getRowNum()-4).getCell(13).setCellFormula(null);
            sheet.getRow(rowend.getRowNum()-4).getCell(13).setCellValue(value);

            /**
             * 右上角
             * 下面计算代表值的变化范围
             */
            value = e.evaluate(sheet.getRow(rowend.getRowNum()-3).getCell(9)).getNumberValue();
            sheet.getRow(rowend.getRowNum()-5).createCell(15);
            sheet.getRow(rowend.getRowNum()-5).createCell(16);
            if(value == 0.0){
                sheet.getRow(rowend.getRowNum()-5).getCell(15).setCellValue("");
                sheet.getRow(rowend.getRowNum()-5).getCell(16).setCellValue("");
            }else{
                sheet.getRow(rowend.getRowNum()-5).getCell(15).setCellValue(value);
                sheet.getRow(rowend.getRowNum()-5).getCell(16).setCellValue(value);
            }
        }else {
            //SFC平均值
            sheet.getRow(rowend.getRowNum()-5).getCell(cds*2+3).setCellFormula("IF(ISERROR(AVERAGE("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(cds+2).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(cds*2+1).getReference()+","
                    +rowstart.getCell(cds*2+3).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(3*cds+2).getReference()+")),\"-\","+
                    "AVERAGE("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(cds+2).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(cds*2+1).getReference()+","
                    +rowstart.getCell(cds*2+3).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(3*cds+2).getReference()+"))");
            //标准差
            sheet.getRow(rowend.getRowNum()-4).getCell(cds*2+3).setCellFormula("IF(ISERROR(ROUND(STDEV("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(cds+2).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(cds*2+1).getReference()+","
                    +rowstart.getCell(cds*2+3).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(3*cds+2).getReference()+"),3)),\"-\","+
                    "ROUND(STDEV("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(cds+2).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(cds*2+1).getReference()+","
                    +rowstart.getCell(cds*2+3).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(3*cds+2).getReference()+"),3))");

            //检测点数
            sheet.getRow(rowend.getRowNum()-1).getCell(cds+3).setCellFormula("COUNT("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+","
                    +rowstart.getCell(cds+2).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(cds*2+1).getReference()+","
                    +rowstart.getCell(cds*2+3).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(3*cds+2).getReference()+")");

            //合格点数
            sheet.getRow(rowend.getRowNum()).getCell(cds+3).setCellFormula("SUM(COUNTIF("+rowstart.getCell(1).getReference()+":"+rowend.getCell(cds).getReference()+",\">=\"&"
                    +sheet.getRow(rowend.getRowNum()-6).getCell(cds*2+3).getReference()+"),COUNTIF("
                    +rowstart.getCell(cds+2).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(cds*2+1).getReference()+",\">=\"&"
                    +sheet.getRow(rowend.getRowNum()-6).getCell(cds*2+3).getReference()+"),COUNTIF("
                    +rowstart.getCell(cds*2+3).getReference()+":"+sheet.getRow(rowend.getRowNum()-7).getCell(3*cds+2).getReference()+",\">=\"&"
                    +sheet.getRow(rowend.getRowNum()-6).getCell(cds*2+3).getReference()+"))");

            //SFC代表值
            sheet.getRow(rowend.getRowNum()-3).getCell(cds*2+3).setCellFormula("IF(ISERROR(ROUND(IF("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(cds+3).getReference()+">100,("
                    +sheet.getRow(rowend.getRowNum()-5).getCell(cds*2+3).getReference()+"-"
                    +sheet.getRow(rowend.getRowNum()-4).getCell(cds*2+3).getReference()+"*(1.6449/SQRT("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(cds+3).getReference()+"))),("
                    +sheet.getRow(rowend.getRowNum()-5).getCell(cds*2+3).getReference()+"-"
                    +sheet.getRow(rowend.getRowNum()-4).getCell(cds*2+3).getReference()+"*VLOOKUP("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(cds+3).getReference()+",保证率系数!$A:$D,3))),2)),\"-\","+
                    "ROUND(IF("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(cds+3).getReference()+">100,("
                    +sheet.getRow(rowend.getRowNum()-5).getCell(cds*2+3).getReference()+"-"
                    +sheet.getRow(rowend.getRowNum()-4).getCell(cds*2+3).getReference()+"*(1.6449/SQRT("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(cds+3).getReference()+"))),("
                    +sheet.getRow(rowend.getRowNum()-5).getCell(cds*2+3).getReference()+"-"
                    +sheet.getRow(rowend.getRowNum()-4).getCell(cds*2+3).getReference()+"*VLOOKUP("
                    +sheet.getRow(rowend.getRowNum()-1).getCell(cds+3).getReference()+",保证率系数!$A:$D,3))),2))");

            //结果
            sheet.getRow(rowend.getRowNum()-2).getCell(cds*2+3).setCellFormula("IF("+sheet.getRow(rowend.getRowNum()-3).getCell(cds*2+3).getReference()+"<>\"-\","+"IF("
                    +sheet.getRow(rowend.getRowNum()-3).getCell(cds*2+3).getReference()+">="
                    +sheet.getRow(rowend.getRowNum()-6).getCell(cds*2+3).getReference()+",\"合格\",\"不合格\"),\"-\")");

            //右上角统计的检测点数
            sheet.getRow(rowend.getRowNum()-4).createCell(3*cds+3).setCellFormula(sheet.getRow(rowend.getRowNum()-1).getCell(cds+3).getReference());
            double value = e.evaluate(sheet.getRow(rowend.getRowNum()-4).getCell(3*cds+3)).getNumberValue();
            sheet.getRow(rowend.getRowNum()-4).getCell(3*cds+3).setCellFormula(null);
            sheet.getRow(rowend.getRowNum()-4).getCell(3*cds+3).setCellValue(value);

            //右上角统计的合格点数
            sheet.getRow(rowend.getRowNum()-4).createCell(3*cds+4).setCellFormula(sheet.getRow(rowend.getRowNum()).getCell(cds+3).getReference());
            value = e.evaluate(sheet.getRow(rowend.getRowNum()-4).getCell(3*cds+4)).getNumberValue();
            sheet.getRow(rowend.getRowNum()-4).getCell(3*cds+4).setCellFormula(null);
            sheet.getRow(rowend.getRowNum()-4).getCell(3*cds+4).setCellValue(value);

            /**
             * 右上角
             * 下面计算代表值的变化范围
             */
            value = e.evaluate(sheet.getRow(rowend.getRowNum()-3).getCell(cds*2+3)).getNumberValue();
            sheet.getRow(rowend.getRowNum()-5).createCell(3*cds+6);
            sheet.getRow(rowend.getRowNum()-5).createCell(3*cds+7);
            if(value == 0.0){
                sheet.getRow(rowend.getRowNum()-5).getCell(3*cds+6).setCellValue("");
                sheet.getRow(rowend.getRowNum()-5).getCell(3*cds+7).setCellValue("");
            }else{
                sheet.getRow(rowend.getRowNum()-5).getCell(3*cds+6).setCellValue(value);
                sheet.getRow(rowend.getRowNum()-5).getCell(3*cds+7).setCellValue(value);
            }
        }
    }

    /**
     * 是否为空
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
     *  @param proname
     * @param htd
     * @param data
     * @param wb
     * @param sheetname
     */
    private void DBtoExcel(String proname, String htd, List<Map<String, Object>> data, XSSFWorkbook wb, String sheetname, int cdsl,String sjz) throws ParseException {
        System.out.println(sheetname+"开始数据写入");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        if (data!=null && !data.isEmpty()){
            createTable2(getNum2(data,cdsl),wb,sheetname,cdsl);
            XSSFSheet sheet = wb.getSheet(sheetname);
            String time = String.valueOf(data.get(0).get("createTime")) ;
            Date parse = simpleDateFormat.parse(time);
            String sj = outputDateFormat.format(parse);
            if (cdsl == 2){
                sheet.getRow(1).getCell(1).setCellValue(proname);
                sheet.getRow(1).getCell(7).setCellValue(htd);
                sheet.getRow(2).getCell(7).setCellValue(sj);
            }else if (cdsl == 5){
                sheet.getRow(1).getCell(1).setCellValue(proname);
                sheet.getRow(1).getCell(9).setCellValue(htd);
                sheet.getRow(2).getCell(9).setCellValue(sj);
            }else {
                sheet.getRow(1).getCell(2).setCellValue(proname);
                sheet.getRow(1).getCell(cdsl*3).setCellValue(htd);
                sheet.getRow(2).getCell(cdsl*3).setCellValue(sj);
            }
            String name = data.get(0).get("lxbs").toString();
            int index = 0;
            int tableNum = 0;

            fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
            if (cdsl == 5){
                for(int i =0; i < data.size(); i++){
                    if (name.equals(data.get(i).get("lxbs"))){
                        if(index % 69 == 0 && index != 0){
                            tableNum ++;
                            fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
                            index = 0;
                        }
                        fillFiveCommonCellData(sheet, tableNum, index, data.get(i),cdsl);
                        index ++;

                    }else {
                        name = data.get(i).get("lxbs").toString();
                        tableNum ++;
                        index = 0;
                        fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
                        fillFiveCommonCellData(sheet, tableNum, index, data.get(i),cdsl);
                        index += 1;
                    }
                }
            }else {
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
        System.out.println(sheetname+"数据写入完成");
    }

    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param row
     */
    private void fillFiveCommonCellData(XSSFSheet sheet, int tableNum, int index, Map<String, Object> row,int cdsl) {
        int a = 1000;
        String[] sfc = row.get("sfc").toString().split(",");
        for (int i = 0 ; i < sfc.length ; i++) {
            if (index < 38){

                sheet.getRow(tableNum * 44 + 6 + index % 38).getCell((cdsl+1) * (index / 38)).setCellValue(Double.valueOf(Double.valueOf(row.get("qdzh").toString()) * a));
                if (sfc[i].equals("-")){
                    sheet.getRow(tableNum * 44 + 6 + index % 38).getCell((cdsl+1) * (index / 38)+1+i).setCellValue(sfc[i]);
                }else {
                    sheet.getRow(tableNum * 44 + 6 + index % 38).getCell((cdsl+1) * (index / 38)+1+i).setCellValue(Double.parseDouble(sfc[i]));
                }

            }else {
                sheet.getRow(tableNum * 44 + 6 + index % 38).getCell(6 * (index / 38)).setCellValue((Double.parseDouble(row.get("qdzh").toString())) * a);
                if (sfc[i].equals("-")){
                    sheet.getRow(tableNum * 44 + 6 + index % 38).getCell(6 * (index / 38)+1+i).setCellValue(sfc[i]);
                }else {
                    sheet.getRow(tableNum * 44 + 6 + index % 38).getCell(6 * (index / 38)+1+i).setCellValue(Double.parseDouble(sfc[i]));
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
        int a = 1000;
        String[] sfc = row.get("sfc").toString().split(",");
        for (int i = 0 ; i < sfc.length ; i++) {
            if (index < 69){
                sheet.getRow(tableNum * 44 + 6 + index % 38).getCell((cdsl+1) * (index / 38)).setCellValue((Double.valueOf(row.get("qdzh").toString())) * a);
                if (sfc[i].equals("-")){
                    sheet.getRow(tableNum * 44 + 6 + index % 38).getCell((cdsl+1) * (index / 38)+1+i).setCellValue(sfc[i]);
                }else {
                    sheet.getRow(tableNum * 44 + 6 + index % 38).getCell((cdsl+1) * (index / 38)+1+i).setCellValue(Double.parseDouble(sfc[i]));
                }
            }else {
                sheet.getRow(tableNum * 44 + 6 + index % 69).getCell((cdsl*2+2) * (index / 69)).setCellValue((Double.parseDouble(row.get("qdzh").toString())) * a);
                if (sfc[i].equals("-")){
                    sheet.getRow(tableNum * 44 + 6 + index % 69).getCell((cdsl*2+2) * (index / 69)+1+i).setCellValue(sfc[i]);
                }else {
                    sheet.getRow(tableNum * 44 + 6 + index % 69).getCell((cdsl*2+2) * (index / 69)+1+i).setCellValue(Double.parseDouble(sfc[i]));
                }
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
     */
    private void fillTitleCellData(XSSFSheet sheet, int tableNum, String proname, String htd, String name,String time,String sheetname,int cdsl,String sjz) {
        String fbgcname = "桥面系";
        if (cdsl ==2){
            sheet.getRow(tableNum * 44 + 1).getCell(1).setCellValue(proname);
            sheet.getRow(tableNum * 44 + 2).getCell(1).setCellValue("桥梁工程");
            sheet.getRow(tableNum * 44 + 1).getCell(7).setCellValue(htd);
            sheet.getRow(tableNum * 44 + 2).getCell(7).setCellValue(time);
            sheet.getRow(tableNum * 44 + 1).getCell(4).setCellValue(fbgcname+"("+name+")");
            sheet.getRow(tableNum * 44 + 37).getCell(cdsl*2+3).setCellValue(Double.parseDouble(sjz));
        }else if (cdsl == 5){
            sheet.getRow(tableNum * 44 + 1).getCell(1).setCellValue(proname);
            sheet.getRow(tableNum * 44 + 2).getCell(1).setCellValue("桥梁工程");
            sheet.getRow(tableNum * 44 + 1).getCell(9).setCellValue(htd);
            sheet.getRow(tableNum * 44 + 2).getCell(9).setCellValue(time);
            sheet.getRow(tableNum * 44 + 1).getCell(5).setCellValue(fbgcname+"("+name+")");
            sheet.getRow(tableNum * 44 + 37).getCell(9).setCellValue(Double.parseDouble(sjz));
        }else {
            sheet.getRow(tableNum * 44 + 1).getCell(2).setCellValue(proname);
            sheet.getRow(tableNum * 44 + 2).getCell(2).setCellValue("桥梁工程");
            if (cdsl == 3){
                sheet.getRow(tableNum * 44 + 1).getCell(5).setCellValue(fbgcname+"("+name+")");
            }
            if (cdsl == 4){
                sheet.getRow(tableNum * 44 + 1).getCell(7).setCellValue(fbgcname+"("+name+")");
            }
            sheet.getRow(tableNum * 44 + 1).getCell(cdsl*3).setCellValue(htd);
            sheet.getRow(tableNum * 44 + 2).getCell(cdsl*3).setCellValue(time);
            sheet.getRow(tableNum * 44 + 37).getCell(cdsl*2+3).setCellValue(Double.parseDouble(sjz));
        }
    }

    /**
     *
     * @param tableNum
     * @param wb
     * @param sheetname
     * @param cdsl
     */
    private void createTable2(int tableNum, XSSFWorkbook wb, String sheetname,int cdsl) {
        if (cdsl == 5){
            int record = 0;
            record = tableNum;
            for (int i = 1; i < record; i++) {
                RowCopy.copyRows(wb, sheetname, sheetname, 0, 43, i* 44);
            }
            if(record >= 1){
                wb.setPrintArea(wb.getSheetIndex(sheetname), 0, 11, 0,(record) * 44-1);//2车道
            }
        }else {
            int record = 0;
            record = tableNum;
            for (int i = 1; i < record; i++) {
                RowCopy.copyRows(wb, sheetname, sheetname, 0, 43, i* 44);
            }
            if(record >= 1){
                wb.setPrintArea(wb.getSheetIndex(sheetname), 0, 3*cdsl+2, 0,(record) * 44-1);//2车道
            }
        }


    }


    /**
     *
     * @param data
     * @param cdsl
     * @return
     */
    private int getNum2(List<Map<String, Object>> data,int cdsl) {
        int c=  0;
        if (cdsl == 5){
            c = 69;
        }else {
            c = 100;
        }
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
            if (value%c==0){
                num += value/c;
            }else {
                num += value/c+1;
            }
        }
        return num;
    }



    /**
     *
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
                if (map.get("sfc") == null){
                    sfc = "-";
                }else {
                    sfc = map.get("sfc").toString();
                }
                //String sfc = map.get("sfc").toString();
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
                List<String> sfcList = entry.getValue();
                /*if (sfcList.size() == 1) {
                    map.put("sfc", sfcList.get(0) + ",-");
                } else {
                    map.put("sfc", String.join(",", sfcList));
                }*/
                map.put("sfc", String.join(",", entry.getValue()));
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
                    double name1 = Double.valueOf(o1.get("qdzh").toString());
                    double name2 = Double.valueOf(o2.get("qdzh").toString());
                    return Double.compare(name1, name2);
                }
            });
            return resultList;
        }
    }



    @Override
    public List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException {

        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        List<Map<String, Object>> mapList = new ArrayList<>();

        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhmcxsMapper.selectlx(proname,htd);
        if (lxlist.size()>0){
            for (Map<String, Object> map : lxlist) {
                String zx = map.get("lxbs").toString();
                int num = jjgFbgcQlgcZdhmcxsMapper.selectcdnum(proname,htd,zx);
                List<Map<String, Object>> looksdjdb = lookdata(proname, htd, zx,num);
                mapList.addAll(looksdjdb);
            }
            return mapList;
        }else {
            return new ArrayList<>();
        }

    }

    /**
     *
     * @param proname
     * @param htd
     * @param zx
     * @param cds
     * @return
     * @throws IOException
     */
    private List<Map<String, Object>> lookdata(String proname, String htd, String zx, int cds) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat decf = new DecimalFormat("0.##");
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + "36桥面摩擦系数-"+zx+".xlsx");
        int pronamecl = 0;
        int htdcl = 0;
        if (cds == 2){
            pronamecl = 1;
            htdcl = 7;
        }else {
            pronamecl = 2;
            if (cds == 5){
                htdcl = 14;
            }else {
                htdcl =  cds*3;
            }
        }
        if (!f.exists()) {
            return new ArrayList<>();
        } else {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));
            List<Map<String, Object>> jgmap = new ArrayList<>();
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                if (!wb.isSheetHidden(wb.getSheetIndex(wb.getSheetAt(j)))) {
                    XSSFSheet slSheet = wb.getSheetAt(j);

                    XSSFCell xmname = slSheet.getRow(1).getCell(pronamecl);//项目名
                    XSSFCell htdname = slSheet.getRow(1).getCell(htdcl);//合同段名


                    if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                        slSheet.getRow(1).getCell(3*cds+3).setCellType(CellType.STRING);//总点数
                        slSheet.getRow(1).getCell(3*cds+4).setCellType(CellType.STRING);//合格点数

                        slSheet.getRow(1).getCell(3*cds+6).setCellType(CellType.STRING);//合格点数
                        slSheet.getRow(1).getCell(3*cds+7).setCellType(CellType.STRING);//合格点数
                        slSheet.getRow(37).getCell(cds*2+3).setCellType(CellType.STRING);
                        double zds = Double.valueOf(slSheet.getRow(1).getCell(3*cds+3).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(1).getCell(3*cds+4).getStringCellValue());
                        String zdsz1 = decf.format(zds);
                        String hgdsz1 = decf.format(hgds);
                        Map map = new HashMap();
                        map.put("路面类型", wb.getSheetName(j));
                        map.put("总点数", zdsz1);
                        map.put("设计值", slSheet.getRow(37).getCell(cds*2+3).getStringCellValue());
                        map.put("合格点数", hgdsz1);
                        map.put("Min", slSheet.getRow(1).getCell(3*cds+7).getStringCellValue());
                        map.put("Max", slSheet.getRow(1).getCell(3*cds+6).getStringCellValue());
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
        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhmcxsMapper.selectlx(proname,htd);
        int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhmcxsMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        return cds;
    }

    @Override
    public void exportmcxs(HttpServletResponse response, String cdsl) throws IOException {
        int cd = Integer.parseInt(cdsl);
        String fileName = "桥面摩擦系数实测数据";
        String[][] sheetNames = {
                {"左幅一车道","左幅二车道","右幅一车道","右幅二车道"},
                {"左幅一车道","左幅二车道","左幅三车道","右幅一车道","右幅二车道","右幅三车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","左幅五车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道","右幅五车道"}
        };
        String[] sheetName = sheetNames[cd-2];
        ExcelUtil.writeExcelMultipleSheets(response, null, fileName, sheetName, new JjgFbgcQlgcZdhmcxsVo());

    }

    @Override
    public void importmcxs(MultipartFile file, CommonInfoVo commonInfoVo) throws IOException {
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
                        .head(JjgFbgcQlgcZdhmcxsVo.class)
                        .headRowNumber(1)
                        .registerReadListener(
                                new ExcelHandler<JjgFbgcQlgcZdhmcxsVo>(JjgFbgcQlgcZdhmcxsVo.class) {
                                    @Override
                                    public void handle(List<JjgFbgcQlgcZdhmcxsVo> dataList) {
                                        for(JjgFbgcQlgcZdhmcxsVo mcxsVo: dataList)
                                        {
                                            JjgFbgcQlgcZdhmcxs mcxs = new JjgFbgcQlgcZdhmcxs();
                                            BeanUtils.copyProperties(mcxsVo,mcxs);
                                            mcxs.setCreatetime(new Date());
                                            mcxs.setProname(commonInfoVo.getProname());
                                            mcxs.setHtd(commonInfoVo.getHtd());
                                            /*mcxs.setQdzh(mcxsVo.getQdzh());
                                            mcxs.setZdzh(mcxsVo.getZdzh());*/
                                            mcxs.setCd(sheetName);
                                            if (sheetName.contains("一")){
                                                mcxs.setVal(1);
                                            }else if (sheetName.contains("二")){
                                                mcxs.setVal(2);
                                            }else if (sheetName.contains("三")){
                                                mcxs.setVal(3);
                                            }else if (sheetName.contains("四")){
                                                mcxs.setVal(4);
                                            }else if (sheetName.contains("五")){
                                                mcxs.setVal(5);
                                            }
                                            jjgFbgcQlgcZdhmcxsMapper.insert(mcxs);
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
        List<Map<String, Object>> selectlx = jjgFbgcQlgcZdhmcxsMapper.selectlx(proname, htd);
        return selectlx;
    }

    @Override
    public List<Map<String, Object>> lookJdb(CommonInfoVo commonInfoVo, String value) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat decf = new DecimalFormat("0.##");
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        //List<Map<String, Object>> selectqlmc = selectqlmc2(proname, htd);

        List<Map<String, Object>> mapList = new ArrayList<>();
        String qlmc = StringUtils.substringBetween(value, "-", ".");
        int cds = jjgFbgcQlgcZdhmcxsMapper.selectcdnum(proname,htd,qlmc);
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + value);
        int pronamecl = 0;
        int htdcl = 0;
        if (cds == 2){
            pronamecl = 1;
            htdcl = 7;
        }else {
            pronamecl = 2;
            if (cds == 5){
                htdcl = 14;
            }else {
                htdcl =  cds*3;
            }
        }
        if (!f.exists()) {
            return new ArrayList<>();
        } else {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));
            List<Map<String, Object>> jgmap = new ArrayList<>();
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                if (!wb.isSheetHidden(wb.getSheetIndex(wb.getSheetAt(j)))) {
                    XSSFSheet slSheet = wb.getSheetAt(j);

                    XSSFCell xmname = slSheet.getRow(1).getCell(pronamecl);//项目名
                    XSSFCell htdname = slSheet.getRow(1).getCell(htdcl);//合同段名


                    if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                        slSheet.getRow(1).getCell(3*cds+3).setCellType(CellType.STRING);//总点数
                        slSheet.getRow(1).getCell(3*cds+4).setCellType(CellType.STRING);//合格点数

                        slSheet.getRow(1).getCell(3*cds+6).setCellType(CellType.STRING);//合格点数
                        slSheet.getRow(1).getCell(3*cds+7).setCellType(CellType.STRING);//合格点数
                        slSheet.getRow(37).getCell(cds*2+3).setCellType(CellType.STRING);
                        double zds = Double.valueOf(slSheet.getRow(1).getCell(3*cds+3).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(1).getCell(3*cds+4).getStringCellValue());
                        String zdsz1 = decf.format(zds);
                        String hgdsz1 = decf.format(hgds);
                        Map map = new HashMap();
                        map.put("检测项目", qlmc);
                        map.put("路面类型", wb.getSheetName(j));
                        map.put("总点数", zdsz1);
                        map.put("设计值", slSheet.getRow(37).getCell(cds*2+3).getStringCellValue());
                        map.put("合格点数", hgdsz1);
                        map.put("Min", slSheet.getRow(1).getCell(3*cds+7).getStringCellValue());
                        map.put("Max", slSheet.getRow(1).getCell(3*cds+6).getStringCellValue());
                        jgmap.add(map);
                    }
                }
            }
            return jgmap;

        }
    }
}
