package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.sdgc.JjgFbgcSdgcZdhldhdVo;
import glgc.jjgys.model.projectvo.zdh.JjgZdhLdhdVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcSdgcZdhldhdMapper;
import glgc.jjgys.system.mapper.JjgLqsFhlmMapper;
import glgc.jjgys.system.service.JjgFbgcSdgcZdhldhdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.utils.RowCopy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;
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
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-10-23
 */
@Service
public class JjgFbgcSdgcZdhldhdServiceImpl extends ServiceImpl<JjgFbgcSdgcZdhldhdMapper, JjgFbgcSdgcZdhldhd> implements JjgFbgcSdgcZdhldhdService {

    @Autowired
    private JjgFbgcSdgcZdhldhdMapper jjgFbgcSdgcZdhldhdMapper;

    @Autowired
    private JjgLqsFhlmMapper jjgLqsFhlmMapper;

    @Value(value = "${jjgys.path.filepath}")
    private String filepath;


    @Override
    public void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();

        List<Map<String,Object>> lxlist = jjgFbgcSdgcZdhldhdMapper.selectlx(proname,htd);
        int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcSdgcZdhldhdMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        handlezxData(proname,htd,cds,commonInfoVo);

    }

    private void handlezxData(String proname, String htd, int cdsl, CommonInfoVo commonInfoVo) throws IOException, ParseException {
        String[] arr = null;
        if (cdsl == 2) {
            arr = new String[] {"左幅一车道", "左幅二车道", "右幅一车道", "右幅二车道"};
        } else if (cdsl == 3) {
            arr = new String[] {"左幅一车道", "左幅二车道", "左幅三车道", "右幅一车道", "右幅二车道", "右幅三车道"};
        } else if (cdsl == 4) {
            arr = new String[] {"左幅一车道", "左幅二车道", "左幅三车道", "左幅四车道","右幅一车道", "右幅二车道", "右幅三车道", "右幅四车道"};
        } else if (cdsl == 5) {
            arr = new String[] {"左幅一车道", "左幅二车道", "左幅三车道", "左幅四车道", "左幅五车道", "右幅一车道", "右幅二车道", "右幅三车道", "右幅四车道", "右幅五车道"};
        }
        StringBuilder sb = new StringBuilder();
        for (String str : arr) {
            sb.append("\"").append(str).append("\",");
        }
        String result = sb.substring(0, sb.length() - 1); // 去掉最后一个逗号


            List<Map<String,Object>> datazf = jjgFbgcSdgcZdhldhdMapper.selectzfList(proname,htd,result);
            List<Map<String,Object>> datayf = jjgFbgcSdgcZdhldhdMapper.selectyfList(proname,htd,result);


            QueryWrapper<JjgLqsFhlm> wrapperfhlmzf = new QueryWrapper<>();
            wrapperfhlmzf.like("proname",proname);
            wrapperfhlmzf.like("htd",htd);
            wrapperfhlmzf.like("lf","左幅");
            List<JjgLqsFhlm> jjgLqsFhlmzf = jjgLqsFhlmMapper.selectList(wrapperfhlmzf);

            QueryWrapper<JjgLqsFhlm> wrapperfhlmyf = new QueryWrapper<>();
            wrapperfhlmyf.like("proname",proname);
            wrapperfhlmyf.like("htd",htd);
            wrapperfhlmyf.like("lf","右幅");
            List<JjgLqsFhlm> jjgLqsFhlmyf = jjgLqsFhlmMapper.selectList(wrapperfhlmyf);


            List<Map<String,Object>> flmzfdata = new ArrayList<>();
            if (jjgLqsFhlmzf.size()>0){
                for (JjgLqsFhlm jjgLqsFhlm : jjgLqsFhlmzf) {
                    String zhq = String.valueOf(jjgLqsFhlm.getZhq());
                    String zhz = String.valueOf(jjgLqsFhlm.getZhz());
                    flmzfdata.addAll(jjgFbgcSdgcZdhldhdMapper.seletcfhlmzfData(proname,htd,zhq,zhz));

                }

            }
            List<Map<String,Object>> flmyfdata = new ArrayList<>();

            if (jjgLqsFhlmyf.size()>0){
                for (JjgLqsFhlm jjgLqsFhlm : jjgLqsFhlmyf) {
                    String zhq = String.valueOf(jjgLqsFhlm.getZhq());
                    String zhz = String.valueOf(jjgLqsFhlm.getZhz());
                    flmyfdata.addAll(jjgFbgcSdgcZdhldhdMapper.seletcfhlmyfData(proname,htd,zhq,zhz));
                }

            }

            //处理数据
            List<Map<String, Object>> sdzxList = montageld(datazf);
            List<Map<String, Object>> sdyxList = montageld(datayf);

            List<Map<String, Object>> flmzxList = montageld(flmzfdata);
            List<Map<String, Object>> flmyxList = montageld(flmyfdata);

            writeExcelData(proname,htd,sdzxList,sdyxList,flmzxList,flmyxList,cdsl,commonInfoVo);

    }

    /**
     *
     * @param proname
     * @param htd
     * @param sdzxList
     * @param sdyxList
     * @param flmzxList
     * @param flmyxList
     * @param cdsl
     * @param commonInfoVo
     * @throws IOException
     * @throws ParseException
     */
    private void writeExcelData(String proname, String htd, List<Map<String, Object>> sdzxList, List<Map<String, Object>> sdyxList, List<Map<String, Object>> flmzxList, List<Map<String, Object>> flmyxList, int cdsl,CommonInfoVo commonInfoVo) throws IOException, ParseException {
        XSSFWorkbook wb = null;

        String fname="52隧道雷达厚度.xlsx";

        File f = new File(filepath+File.separator+proname+File.separator+htd+File.separator+fname);
        File fdir = new File(filepath + File.separator + proname + File.separator + htd);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        File directory = new File("service-system/src/main/resources/static");
        String reportPath = directory.getCanonicalPath();
        String filename = "";

        if (cdsl == 5){
            filename = "雷达厚度-5车道.xlsx";
        }else if (cdsl == 4){
            filename = "雷达厚度-4车道.xlsx";
        }else if (cdsl == 3){
            filename = "雷达厚度-3车道.xlsx";
        }else if (cdsl == 2){
            filename = "雷达厚度-2车道.xlsx";
        }


        String path = reportPath + File.separator + filename;
        Files.copy(Paths.get(path), new FileOutputStream(f));
        FileInputStream out = new FileInputStream(f);
        wb = new XSSFWorkbook(out);

        List<Map<String,Object>> sdqllmData = new ArrayList<>();


        if (flmzxList.size()>0 && !flmzxList.isEmpty()){
            for (Map<String, Object> map : flmzxList) {
                Map<String,Object> map1 = new HashMap<>();
                map1.put("zh",map.get("zh").toString());
                map1.put("lxbs",map.get("lxbs").toString());
                map1.put("cd",map.get("cd").toString());
                if (map.get("zdbs")!=null){
                    map1.put("zdbs",map.get("zdbs").toString());
                }
                sdqllmData.add(map1);
            }
            List<Map<String, Object>> mapslist = mergedList(flmzxList,cdsl);
            DBtoExcel(proname,htd,mapslist,wb,"复合路面左幅",cdsl,commonInfoVo.getFhlmsjz());
        }

        if (flmyxList.size()>0 && !flmyxList.isEmpty()){
            for (Map<String, Object> map : flmyxList) {
                Map<String,Object> map1 = new HashMap<>();
                map1.put("zh",map.get("zh").toString());
                map1.put("lxbs",map.get("lxbs").toString());
                map1.put("cd",map.get("cd").toString());
                if (map.get("zdbs")!=null){
                    map1.put("zdbs",map.get("zdbs").toString());
                }
                sdqllmData.add(map1);
            }
            List<Map<String, Object>> mapslist = mergedList(flmyxList,cdsl);
            DBtoExcel(proname,htd,mapslist,wb,"复合路面右幅",cdsl,commonInfoVo.getFhlmsjz());
        }

        if (sdzxList.size()>0 && !sdzxList.isEmpty()){
            DBtoExcelLM(proname,htd,sdzxList,sdqllmData,wb,"左幅",cdsl,commonInfoVo.getSjz());
        }

        if (sdyxList.size()>0 && !sdyxList.isEmpty()){
            DBtoExcelLM(proname,htd,sdyxList,sdqllmData,wb,"右幅",cdsl,commonInfoVo.getSjz());
        }



        String[] arr = {"匝道桥","匝道隧道","匝道左幅","匝道右幅","左幅","右幅","桥","隧道","复合路面左幅","复合路面右幅"};
        for (int i = 0; i < arr.length; i++) {
            if (shouldBeCalculate(wb.getSheet(arr[i]))) {
                if (arr[i].equals("隧道")) {
                    calculateBridgeAndTunnelSheet(wb,wb.getSheet(arr[i]),cdsl,commonInfoVo.getSdsjz());
                }else if (arr[i].contains("复合路面")) {
                    calculateBridgeAndTunnelSheet(wb,wb.getSheet(arr[i]),cdsl,commonInfoVo.getFhlmsjz());
                }

            }else {
                wb.setSheetHidden(wb.getSheetIndex(arr[i]),true);
            }
        }

        FileOutputStream fileOut = new FileOutputStream(f);
        wb.write(fileOut);
        fileOut.flush();
        fileOut.close();
        out.close();
        wb.close();

    }

    /**
     *桥梁隧道
     * @param sheet
     */
    private void calculateBridgeAndTunnelSheet(XSSFWorkbook wb,XSSFSheet sheet,int cdsl,String sjz){
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        boolean flag = false;
        FormulaEvaluator e = new XSSFFormulaEvaluator(wb);
        String name = "";
        int mm = 0;
        if (cdsl == 2){
            mm = 41;

        }else if (cdsl ==3 ){
            mm = 41;
        }else if (cdsl == 4){
            mm = 22;

        }else if (cdsl == 5){
            mm = 26;

        }
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            if(flag){
                if(name.equals(row.getCell(0).toString())){
                    rowend = sheet.getRow(i+41);
                }else {
                    fillBridgeAndTunnelTotal(wb,sheet, rowstart, rowend,cdsl,sjz);
                    rowstart = sheet.getRow(i);
                    rowend = sheet.getRow(i+mm);
                    name = rowstart.getCell(0).toString();
                }
                /*rowstart = sheet.getRow(i);
                rowend = sheet.getRow(i+41);
                fillBridgeAndTunnelTotal(wb,sheet, rowstart, rowend,cdsl);
                i += 41;*/
                i += mm;
            }
            if ("工程名称".equals(row.getCell(0).toString())) {
                i+=1;
                rowstart = sheet.getRow(i+1);
                rowend = sheet.getRow(i+(mm+1));
                name = rowstart.getCell(0).toString();
                fillBridgeAndTunnelTotal(wb,sheet, rowstart, rowend,cdsl,sjz);
                i+= (mm+1);
                flag = true;
            }
        }
        //fillBridgeAndTunnelTotal(wb,sheet, rowstart, rowend,cdsl,sjz);

        double value= 0 ;

        sheet.getRow(4).createCell(cdsl*3+7).setCellFormula("SUM("
                +sheet.getRow(5).createCell(cdsl*3+7).getReference()+":"
                +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).createCell(cdsl*3+7).getReference()+")");

        sheet.getRow(4).createCell(cdsl*3+8).setCellFormula("SUM("
                +sheet.getRow(5).createCell(cdsl*3+8).getReference()+":"
                +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).createCell(cdsl*3+8).getReference()+")");

        sheet.getRow(4).createCell(cdsl*3+9).setCellFormula(
                sheet.getRow(4).getCell(cdsl*3+8).getReference()+"*100/"+
                        sheet.getRow(4).getCell(cdsl*3+7).getReference());
        //最大值，最小值
        sheet.getRow(4).createCell(cdsl*3+4).setCellFormula("MAX("
                +sheet.getRow(5).createCell(cdsl*3+4).getReference()+":"
                +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).createCell(cdsl*3+4).getReference()+")");
        value = e.evaluate(sheet.getRow(4).getCell(cdsl*3+4)).getNumberValue();
        sheet.getRow(4).getCell(cdsl*3+4).setCellFormula(null);
        sheet.getRow(4).getCell(cdsl*3+4).setCellValue(value);

        sheet.getRow(4).createCell(cdsl*3+5).setCellFormula("MIN("
                +sheet.getRow(5).createCell(cdsl*3+5).getReference()+":"
                +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).createCell(cdsl*3+5).getReference()+")");
        value = e.evaluate(sheet.getRow(4).getCell(cdsl*3+5)).getNumberValue();
        sheet.getRow(4).getCell(cdsl*3+5).setCellFormula(null);
        sheet.getRow(4).getCell(cdsl*3+5).setCellValue(value);

        sheet.getRow(5).createCell(cdsl*3+7).setCellFormula(sheet.getRow(cdsl*2+1).getCell(cdsl*2+4).getReference());
        value = e.evaluate(sheet.getRow(5).getCell(cdsl*3+7)).getNumberValue();
        sheet.getRow(5).getCell(cdsl*3+7).setCellFormula(null);
        sheet.getRow(5).getCell(cdsl*3+7).setCellValue(value);

        sheet.getRow(5).createCell(cdsl*3+8).setCellFormula(sheet.getRow(cdsl*2+2).getCell(cdsl*2+4).getReference());
        value = e.evaluate(sheet.getRow(5).getCell(cdsl*3+8)).getNumberValue();
        sheet.getRow(5).getCell(cdsl*3+8).setCellFormula(null);
        sheet.getRow(5).getCell(cdsl*3+8).setCellValue(value);

        int hgds = 0;
        for (int i = 5; i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null || row.getCell(cdsl*3+8) == null || "".equals(row.getCell(cdsl*3+8).toString())) {
                continue;
            }

            hgds += Double.valueOf(e.evaluate(row.getCell(cdsl*3+8)).getNumberValue()).intValue();
        }

        sheet.getRow(4).getCell(cdsl*3+8).setCellFormula(null);
        sheet.getRow(4).getCell(cdsl*3+8).setCellValue(hgds);
    }

    /**
     *
     * @param wb
     * @param sheet
     * @param rowstart
     * @param rowend
     */
    private void fillBridgeAndTunnelTotal(XSSFWorkbook wb,XSSFSheet sheet, XSSFRow rowstart,XSSFRow rowend,int cdsl,String sjz) {
        sheet.getRow(rowstart.getRowNum()).getCell(cdsl*2+2).setCellValue("代表值允许偏差（cm）");
        sheet.getRow(rowstart.getRowNum()+1).getCell(cdsl*2+2).setCellValue("合格值允许偏差（cm）");
        sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+2).setCellValue("总点数");
        sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+2).setCellValue("合格数");
        sheet.getRow(rowstart.getRowNum()+4).getCell(cdsl*2+2).setCellValue("设计值(cm)");
        sheet.getRow(rowstart.getRowNum()+5).getCell(cdsl*2+2).setCellValue("平均值(cm)");
        sheet.getRow(rowstart.getRowNum()+6).getCell(cdsl*2+2).setCellValue("代表值(cm)");
        sheet.getRow(rowstart.getRowNum()+7).getCell(cdsl*2+2).setCellValue("均方差");
        sheet.getRow(rowstart.getRowNum()+8).getCell(cdsl*2+2).setCellValue("合格率");
        sheet.getRow(rowstart.getRowNum()+9).getCell(cdsl*2+2).setCellValue("评定结果");

        sheet.getRow(rowstart.getRowNum()).getCell(cdsl*2+4).setCellFormula("-0.05*"+
                sheet.getRow(rowstart.getRowNum()+4).getCell(cdsl*2+4).getReference());//代表值允许偏差（cm）=-0.08*I39
        sheet.getRow(rowstart.getRowNum()+1).getCell(cdsl*2+4).setCellFormula("-0.1*"+
                sheet.getRow(rowstart.getRowNum()+4).getCell(cdsl*2+4).getReference());//合格值允许偏差（cm）=-0.1*I39

        //总点数
        /**cdsl*2+1
         * 2c 5
         * 3c 7
         * 4c 9
         * 5c 11
         */
        System.out.println(rowstart.getCell(2).getReference()+":"+rowend.getCell(cdsl*2+1).getReference());
        sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+4).setCellFormula("COUNT("
                +rowstart.getCell(2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+")");//I37=COUNT(C6:F44)

        XSSFFormulaEvaluator e=new XSSFFormulaEvaluator(wb);

        Cell cell = sheet.getRow(rowstart.getRowNum()+4).getCell(cdsl*2+4);
        double data = 0;
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            data = cell.getNumericCellValue() + e.evaluate(sheet.getRow(rowstart.getRowNum()+1).getCell(cdsl*2+4)).getNumberValue();
        }

        //合格数
        //=IF(K15="合格",(K8-SUM(COUNTIF(C6:H89,"<9.0"))),"")
        sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+4).setCellFormula("IF("
                +sheet.getRow(rowstart.getRowNum()+9).getCell(cdsl*2+4).getReference()+"=\"合格\",("
                +sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+4).getReference()+"-SUM(COUNTIF("
                +rowstart.getCell(2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+",\"<"+ data +"\"))),\"-\")");

        sheet.getRow(rowstart.getRowNum()+4).getCell(cdsl*2+4).setCellValue(Double.parseDouble(sjz));

        //平均值
        sheet.getRow(rowstart.getRowNum()+5).getCell(cdsl*2+4).setCellFormula("IFERROR(AVERAGE("
                +rowstart.getCell(2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+"),\"-\")");//I40=AVERAGE(B6:C44,E6:F44,H6:I27)

        //均方差
        sheet.getRow(rowstart.getRowNum()+7).getCell(cdsl*2+4).setCellFormula("IFERROR(STDEV("
                +rowstart.getCell(2).getReference()+":"
                +rowend.getCell(cdsl*2+1).getReference()+"),\"-\")");

        //代表值(cm)
        sheet.getRow(rowstart.getRowNum()+6).getCell(cdsl*2+4).setCellFormula("IFERROR(IF("
                +sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+4).getReference()+">100,("
                +sheet.getRow(rowstart.getRowNum()+5).getCell(cdsl*2+4).getReference()+"-"
                +sheet.getRow(rowstart.getRowNum()+7).getCell(cdsl*2+4).getReference()+"*1.6449/SQRT("
                +sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+4).getReference()+")),("
                +sheet.getRow(rowstart.getRowNum()+5).getCell(cdsl*2+4).getReference()+"-VLOOKUP("
                +sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+4).getReference()+",Sheet1!A:D,"+(3)+",FALSE)*"
                +sheet.getRow(rowstart.getRowNum()+7).getCell(cdsl*2+4).getReference()+")),\"-\")");
        //I41=IF(I37>100,(I40-I42*1.6449/SQRT(I37)),(I40-VLOOKUP(I37,Sheet1!A:D,3,FALSE)*I42))

        //将代表值放到右边的最大值最小值位置方便在报告中
        sheet.getRow(rowstart.getRowNum()+2).createCell(cdsl*3+4).setCellFormula(sheet.getRow(rowstart.getRowNum()+6).getCell(cdsl*2+4).getReference());
        sheet.getRow(rowstart.getRowNum()+2).createCell(cdsl*3+5).setCellFormula(sheet.getRow(rowstart.getRowNum()+6).getCell(cdsl*2+4).getReference());

        double totalNum = e.evaluate(sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+4)).getNumberValue();//总点数不为0的时候才计算最大值最小值
        if(totalNum != 0){
            //评定结果
            sheet.getRow(rowstart.getRowNum()+9).getCell(cdsl*2+4).setCellFormula("IFERROR(IF("+
                    sheet.getRow(rowstart.getRowNum()+6).getCell(cdsl*2+4).getReference()+">="+
                    sheet.getRow(rowstart.getRowNum()+4).getCell(cdsl*2+4).getReference()+"+"+
                    sheet.getRow(rowstart.getRowNum()).getCell(cdsl*2+4).getReference()+",\"合格\",\"不合格\"),\"-\")");
            //评定结果=IF(I41>=I39+I35,"合格","不合格")
        }else{
            sheet.getRow(rowstart.getRowNum()+9).getCell(cdsl*2+4).setCellValue("-");
            //评定结果=IF(I41>=I39+I35,"合格","不合格")
        }

        //合格率：
        sheet.getRow(rowstart.getRowNum()+8).getCell(cdsl*2+4).setCellFormula("IFERROR("+
                sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+4).getReference()+"/"
                +sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+4).getReference()+"*100,\"-\")");//I43=I38/I37*100

        try{
            double value = e.evaluate(sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+4)).getNumberValue();
            sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+4).setCellFormula(null);
            sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+4).setCellValue(value);
        }catch(Exception e1){
            sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+4).setCellFormula(null);
            sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+4).setCellValue("");
        }

        sheet.getRow(rowstart.getRowNum()).createCell(cdsl*3+7).setCellFormula(sheet.getRow(rowstart.getRowNum()+2).getCell(cdsl*2+4).getReference());
        double value = e.evaluate(sheet.getRow(rowstart.getRowNum()).getCell(cdsl*3+7)).getNumberValue();
        sheet.getRow(rowstart.getRowNum()).createCell(cdsl*3+7).setCellFormula(null);
        sheet.getRow(rowstart.getRowNum()).createCell(cdsl*3+7).setCellValue(value);

        sheet.getRow(rowstart.getRowNum()).createCell(cdsl*3+8).setCellFormula(sheet.getRow(rowstart.getRowNum()+3).getCell(cdsl*2+4).getReference());
        value = e.evaluate(sheet.getRow(rowstart.getRowNum()).getCell(cdsl*3+8)).getNumberValue();
        sheet.getRow(rowstart.getRowNum()).createCell(cdsl*3+8).setCellFormula(null);
        sheet.getRow(rowstart.getRowNum()).createCell(cdsl*3+8).setCellValue(value);

        sheet.getRow(rowstart.getRowNum()).createCell(cdsl*3+9).setCellFormula(
                sheet.getRow(rowstart.getRowNum()).getCell(cdsl*3+8).getReference()+"*100/"+
                        sheet.getRow(rowstart.getRowNum()).getCell(cdsl*3+7).getReference());
    }


    /**
     *
     * @param sheet
     * @return
     */
    private boolean shouldBeCalculate(XSSFSheet sheet) {
        sheet.getRow(5).getCell(0).setCellType(CellType.STRING);
        if(sheet.getRow(5).getCell(0)==null ||"".equals(sheet.getRow(5).getCell(0).getStringCellValue())){
            return false;
        }

        return true;
    }


    /**
     *
     * @param proname
     * @param htd
     * @param data
     * @param sdqllmData
     * @param wb
     * @param sheetname
     * @param cdsl
     * @param sjz
     * @throws ParseException
     */
    private void DBtoExcelLM(String proname, String htd, List<Map<String, Object>> data, List<Map<String, Object>> sdqllmData, XSSFWorkbook wb, String sheetname, int cdsl, String sjz) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        if (data!=null && !data.isEmpty()) {
            int a = 0;
            int b = 0;
            int c = 0;
            int d =0;
            if (cdsl == 2 || cdsl == 3){
                a = 106;
                b = 78;
                c = 44;
                d = 39;
            }else if (cdsl == 4){
                a = 58;
                b = 46;
                c = 28;
                d = 23;
            }else if (cdsl == 5){
                a = 70;
                b = 54;
                c = 32;
                d = 27;
            }
            createTableLM(getNumLM(data,cdsl), wb, sheetname, cdsl);
            XSSFSheet sheet = wb.getSheet(sheetname);

            String name = data.get(0).get("lxbs").toString();
            int index = 0;
            int tableNum = 0;
            String time1 = String.valueOf(data.get(0).get("createTime")) ;
            Date parse = simpleDateFormat.parse(time1);
            String time = outputDateFormat.format(parse);

            fillTitleCellData(sheet, tableNum, proname, htd, name,time,sheetname,cdsl,sjz);

            List<Map<String, Object>> lmdata = handleLmData(data,sdqllmData);

            List<Map<String, Object>> rowAndcol = new ArrayList<>();
            int startRow = -1, endRow = -1, startCol = -1, endCol = -1;
            for (Map<String, Object> lm : lmdata) {
                if (index > a) {
                    tableNum++;
                    fillTitleCellData(sheet, tableNum, proname, htd, name, time, sheetname,cdsl,sjz);
                    index = 0;
                }
                if (!lm.get("ld").toString().equals("") && !lm.get("ld").toString().isEmpty()) {
                    String[] sfc = lm.get("ld").toString().split(",");
                    for (int i = 0; i < sfc.length; i++) {
                        if (index < b) {
                            sheet.getRow(tableNum * c + 5 + index % d).getCell((cdsl+1) * (index / d)).setCellValue((Double.parseDouble(lm.get("zh").toString())));
                            if (sfc[i].equals("-")){
                                sheet.getRow(tableNum * c + 5 + index % d).getCell((cdsl+1) * (index / d) + 1 + i).setCellValue(sfc[i]);
                            }else {
                                sheet.getRow(tableNum * c + 5 + index % d).getCell((cdsl+1) * (index / d) + 1 + i).setCellValue(Double.parseDouble(sfc[i]));
                            }

                        } else {
                            sheet.getRow(tableNum * c + 5 + index % b).getCell((cdsl*2+2)  * (index / b)).setCellValue((Double.parseDouble(lm.get("zh").toString())));
                            if (sfc[i].equals("-")){
                                sheet.getRow(tableNum * c + 5 + index % b).getCell((cdsl*2+2)  * (index / b) + 1 + i).setCellValue(sfc[i]);
                            }else {
                                sheet.getRow(tableNum * c + 5 + index % b).getCell((cdsl*2+2)  * (index / b) + 1 + i).setCellValue(Double.parseDouble(sfc[i]));
                            }

                        }

                    }

                } else {
                    for (int i = 0; i < cdsl; i++) {
                        if (index < b) {
                            sheet.getRow(tableNum * c + 5 + index % d).getCell((cdsl+1) * (index / d)).setCellValue((Double.parseDouble(lm.get("zh").toString())));
                            sheet.getRow(tableNum * c + 5 + index % d).getCell((cdsl+1) * (index / d) + 1 + i).setCellValue(lm.get("lxbs").toString());

                            startRow = tableNum * c + 5 + index % d ;
                            endRow = tableNum * c + 5 + index % d ;

                            startCol = (cdsl+1)  * (index / d) + 1;
                            endCol = (cdsl+1)  * (index / d) + cdsl;

                        } else {
                            sheet.getRow(tableNum * c + 5 + index % b).getCell((cdsl*2+2)  * (index / b)).setCellValue((Double.parseDouble(lm.get("zh").toString())));
                            sheet.getRow(tableNum * c + 5 + index % b).getCell((cdsl*2+2)  * (index / b) + 1 + i).setCellValue(lm.get("lxbs").toString());

                            startRow = tableNum * c + 5 + index % b ;
                            endRow = tableNum * c + 5 + index % b ;

                            startCol = 2*cdsl+3;
                            endCol = 3*cdsl+2;

                        }
                    }
                    //可以在这块记录一个行和列
                    Map<String, Object> map = new HashMap<>();
                    map.put("startRow",startRow);
                    map.put("endRow",endRow);
                    map.put("startCol",startCol);
                    map.put("endCol",endCol);
                    map.put("lxbs",lm.get("lxbs"));
                    map.put("tableNum",tableNum);
                    rowAndcol.add(map);

                }
                index++;

            }

            List<Map<String, Object>> maps = mergeCells(rowAndcol);
            for (Map<String, Object> map : maps) {
                sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
            }
        }
    }
    /**
     * 合并单元格
     * @param rowAndcol
     * @return
     */
    private List<Map<String, Object>> mergeCells(List<Map<String, Object>> rowAndcol) {
        List<Map<String, Object>> result = new ArrayList<>();
        int currentEndRow = -1;
        int currentStartRow = -1;
        int currentStartCol = -1;
        int currentEndCol = -1;
        String currentName = null;
        int currentTableNum = -1;
        for (Map<String, Object> row : rowAndcol) {
            int tableNum = (int) row.get("tableNum");
            int startRow = (int) row.get("startRow");
            int endRow = (int) row.get("endRow");
            int startCol = (int) row.get("startCol");
            int endCol = (int) row.get("endCol");
            String name = (String) row.get("name");
            if (currentName == null || !currentName.equals(name) || currentStartCol != startCol || currentEndCol != endCol || currentTableNum != tableNum) {
                if (currentStartRow != -1) {
                    for (int i = currentStartRow; i <= currentEndRow && i < result.size(); i++) {
                        Map<String, Object> newRow = new HashMap<>();
                        newRow.put("name", currentName);
                        newRow.put("startRow", currentStartRow);
                        newRow.put("endRow", currentEndRow);
                        newRow.put("startCol", currentStartCol);
                        newRow.put("endCol", currentEndCol);
                        newRow.put("tableNum", currentTableNum);
                        newRow.putAll(result.get(i));
                        result.set(i, newRow);
                    }
                }
                currentName = name;
                currentStartCol = startCol;
                currentEndCol = endCol;
                currentTableNum = tableNum;
                currentStartRow = startRow;
                currentEndRow = endRow;
                result.add(row);
            } else {
                Map<String, Object> lastRow = result.get(result.size() - 1);
                lastRow.put("endRow", endRow);
                currentEndRow = endRow;
            }
        }
        if (currentStartRow != -1) {
            for (int i = currentStartRow; i <= currentEndRow && i < result.size(); i++) {
                Map<String, Object> newRow = new HashMap<>();
                newRow.put("name", currentName);
                newRow.put("startRow", currentStartRow);
                newRow.put("endRow", currentEndRow);
                newRow.put("startCol", currentStartCol);
                newRow.put("endCol", currentEndCol);
                newRow.put("tableNum", currentTableNum);
                newRow.putAll(result.get(i));
                result.set(i, newRow);
            }
        }
        return result;
    }


    /**
     *
     * @param data
     * @param sdqllmData
     * @return
     */
    private List<Map<String, Object>> handleLmData(List<Map<String, Object>> data, List<Map<String, Object>> sdqllmData) {
        for (Map<String, Object> datum : data) {
            for (Map<String, Object> zfsdqlDatum : sdqllmData) {
                if (datum.get("zh").toString().equals(zfsdqlDatum.get("zh")) && datum.get("cd").toString().equals(zfsdqlDatum.get("cd"))){
                    datum.put("ld","");
                    datum.put("lxbs",zfsdqlDatum.get("lxbs"));
                }
            }
        }
        Collections.sort(data, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("zh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("zh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });
        return data;
    }

    /**
     *
     * @param sheet
     * @param tableNum
     * @param proname
     * @param htd
     * @param name
     * @param sj
     * @param sheetname
     * @param cdsl
     * @param sjz
     */
    private void fillTitleCellData(XSSFSheet sheet, int tableNum, String proname, String htd, String name, String sj, String sheetname, int cdsl, String sjz) {
        int a = 0;
        int dd = 0;
        if (sheetname.equals("左幅") || sheetname.equals("右幅")){
            if (cdsl == 2){
                dd = 38;
                a = 44;
            }else if (cdsl == 3){
                dd = 38;
                a = 44;
            }else if (cdsl == 4 ){
                dd = 22;
                a = 28;
            }else if(cdsl ==5){
                dd = 26;
                a = 32;
            }
        }else {
            if (cdsl == 2){
                a = 47;
            }else if (cdsl == 3){
                a = 47;
            }else if (cdsl == 4 ){
                a = 28;
            }else if(cdsl ==5){
                a = 32;
            }
        }
        sheet.getRow(tableNum * a + 1).getCell(1).setCellValue(proname);
        sheet.getRow(tableNum * a + 1).getCell(cdsl*2+3).setCellValue(htd);
        sheet.getRow(tableNum * a + 2).getCell(1).setCellValue("隧道路面");
        sheet.getRow(tableNum * a + 2).getCell(cdsl*2+3).setCellValue(sj);
        if (sheetname.equals("左幅") || sheetname.equals("右幅")){
            sheet.getRow(tableNum * a + dd).getCell(cdsl*2+4).setCellValue(Double.parseDouble(sjz));
        }else {
            sheet.getRow(tableNum * a + 9).getCell(cdsl*2+4).setCellValue(Double.parseDouble(sjz));
        }
    }

    /**
     *
     * @param tableNum
     * @param wb
     * @param sheetname
     * @param cdsl
     */
    private void createTableLM(int tableNum, XSSFWorkbook wb, String sheetname, int cdsl) {
        int endrow = 0;
        int printrow = 0;
        int cdnum = 0;
        if (cdsl == 2 ) {
            endrow = 43;
            printrow = 44;
            cdnum = cdsl*2+5;
        }else if (cdsl == 3){
            endrow = 43;
            printrow = 44;
            cdnum = cdsl*2+6;
        } else if (cdsl == 4){
            endrow = 27;
            printrow=28;
            cdnum = 15;
        }else if (cdsl ==5){
            endrow = 31;
            printrow=32;
            cdnum = 18;
        }
        int record = 0;
        record = tableNum;
        for (int i = 1; i < record; i++) {
            RowCopy.copyRows(wb, sheetname, sheetname, 0, endrow, i* (endrow+1));
        }
        if(record >= 1){
            wb.setPrintArea(wb.getSheetIndex(sheetname), 0, cdnum, 0,(record) * printrow-1);
        }
    }

    /**
     *
     * @param data
     * @param cdsl
     * @return
     */
    private int getNumLM(List<Map<String, Object>> data, int cdsl) {
        int a = 0;
        if (cdsl == 2 || cdsl == 3){
            a = 107;
        }else if (cdsl == 4){
            a = 59;
        }else if (cdsl == 5){
            a = 71;
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
            if (value%a==0){
                num += value/a;
            }else {
                num += value/a+1;
            }
        }
        return num;
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
     * @throws ParseException
     */
    private void DBtoExcel(String proname, String htd, List<Map<String, Object>> data, XSSFWorkbook wb, String sheetname, int cdsl, String sjz) throws ParseException {
        int b = 0;
        if (cdsl == 2 || cdsl == 3){
            b=42;
        }else if (cdsl == 4){
            b=23;
        }else if (cdsl == 5){
            b=27;
        }
        if (data!=null && !data.isEmpty()){
            createTable(getNum(data,cdsl),wb,sheetname,cdsl);
            writesdqlzyf(wb,data,proname, htd,sheetname,cdsl,sjz,b);

        }
    }

    /**
     *
     * @param wb
     * @param data
     * @param proname
     * @param htd
     * @param sheetname
     * @param cdsl
     * @param sjz
     * @param b
     * @throws ParseException
     */
    private void writesdqlzyf(XSSFWorkbook wb, List<Map<String, Object>> data, String proname, String htd, String sheetname, int cdsl, String sjz,int b) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        XSSFSheet sheet = wb.getSheet(sheetname);
        String time = String.valueOf(data.get(0).get("createTime")) ;
        Date parse = simpleDateFormat.parse(time);
        String sj = outputDateFormat.format(parse);

        sheet.getRow(1).getCell(1).setCellValue(proname);
        sheet.getRow(2).getCell(cdsl*2+3).setCellValue(sj);
        sheet.getRow(1).getCell(cdsl*2+3).setCellValue(htd);
        sheet.getRow(2).getCell(1).setCellValue("隧道路面");

        String name = data.get(0).get("lxbs").toString();
        int index = 5;
        int tableNum = 0;

        for(int i =0; i < data.size(); i++){
            if (name.equals(data.get(i).get("lxbs"))){
                if(index > (b-1)){
                    tableNum ++;
                    index = 0;
                }
                fillCommonCellData(sheet, tableNum, index, data.get(i),cdsl);
                index ++;
            }else {
                name = data.get(i).get("lxbs").toString();
                tableNum ++;//去掉是因为有空白页
                index = 5;
                fillCommonCellData(sheet, tableNum, index, data.get(i),cdsl);
                index += 1;

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
    private void fillCommonCellData(XSSFSheet sheet, int tableNum, int index, Map<String, Object> row, int cdsl) {
        int b = 0;
        if (cdsl == 2){
            b=42;
        }else if (cdsl == 3){
            b=42;
        }else if (cdsl == 4){
            b=23;
        }else if (cdsl ==5){
            b=27;
        }
        sheet.getRow(tableNum * b + index % b).getCell(0).setCellValue(row.get("lxbs").toString());
        sheet.getRow(tableNum * b + index % b).getCell(1).setCellValue(Double.valueOf(row.get("zh").toString()));

        String[] sfc = row.get("ld").toString().split(",");
        if (!sfc[0].isEmpty()) {
            for (int i = 0 ; i < sfc.length ; i++) {
                if (!sfc[i].equals("-")){
                    sheet.getRow(tableNum * b + index % b).getCell(2+i).setCellValue(Double.parseDouble(sfc[i]));
                }else {
                    sheet.getRow(tableNum * b + index % b).getCell(2+i).setCellValue("-");
                }

            }
        }

    }

    /**
     * 创建隧道匝道的页数
     * @param tableNum
     * @param wb
     * @param sheetname
     * @param cdsl
     */
    private void createTable(int tableNum, XSSFWorkbook wb, String sheetname, int cdsl) {
        int endrow = 0;
        int printrow = 0;
        int cc= 0;
        if (cdsl == 2 || cdsl == 3){
            endrow = 46;
            printrow=47;
            cc=42;
        }else if (cdsl == 4){
            endrow = 27;
            printrow=28;
            cc=23;
        }else if (cdsl ==5){
            endrow = 31;
            printrow=32;
            cc=27;
        }
        int record = 0;
        record = tableNum;
        for (int i = 1; i < record; i++) {
            RowCopy.copyRows(wb, sheetname, sheetname, 5, endrow, (i-1)* cc+printrow);

        }
        if(record >= 1){
            wb.setPrintArea(wb.getSheetIndex(sheetname), 0, cdsl*2+4, 0,(record) * cc+4);

        }
    }

    /**
     *
     * @param data
     * @return
     */
    private int getNum(List<Map<String, Object>> data,int cdsl) {
        int a = 0;
        if (cdsl == 2){
            a = 42;
        }else if (cdsl == 3){
            a = 42;
        }else if (cdsl == 4){
            a = 23;
        }else if (cdsl == 5){
            a = 27;
        }
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> map : data) {
            String name = map.get("name").toString();
            if (resultMap.containsKey(name)) {
                resultMap.put(name, resultMap.get(name) + 1);
            } else {
                resultMap.put(name, 1);
            }
        }
        int num = 0;
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            int value = entry.getValue();
            if (value%a==0){
                num += value/a;
            }else {
                num += value/a+1;
            }
        }
        return num;
    }



    /**
     *
     * @param dataLists
     * @param cds
     * @return
     */
    private List<Map<String, Object>> mergedList(List<Map<String, Object>> dataLists,int cds) {
        //处理拼接的iri
        int cdsl = cds;
        String iris = "-";
        StringBuilder iriBuilder = new StringBuilder();
        for (int i = 0; i < cdsl; i++) {
            if (i == cdsl - 1) {
                iriBuilder.append("-");
            } else {
                iriBuilder.append("-,");
            }
        }
        iris = iriBuilder.toString();

        Map<String, List<Map<String, Object>>> groupedData =
                dataLists.stream()
                        .collect(Collectors.groupingBy(
                                item -> item.get("name") + "-" + item.get("zh")
                        ));
        List<Map<String, Object>> toBeRemoved = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedData.entrySet()) {
            String groupName = entry.getKey(); // 获取分组名字
            List<Map<String, Object>> groupData = entry.getValue(); // 获取分组数据

            if (groupData.size() > 1) {
                Map<String, Object> firstItem = groupData.get(0);
                Map<String, Object> secondItem = groupData.get(1);
                if (firstItem.get("cd").toString().contains("左幅")) {
                    // 将第二条数据的iri拼接在第一条数据的iri后面
                    String iri = firstItem.get("ld").toString() + "," + secondItem.get("ld").toString();
                    firstItem.put("ld", iri);
                } else {
                    // 将第二条数据的iri拼接在第一条数据的iri前面
                    String iri = secondItem.get("ld").toString() + "," + firstItem.get("ld").toString();
                    firstItem.put("ld", iri);
                }
                // 将secondItem条数据删除
                toBeRemoved.add(secondItem);

            } else if (groupData.size() == 1) {
                Map<String, Object> item = groupData.get(0);
                if (item.get("cd").toString().contains("左幅")) {
                    // 在iri后面拼接逗号
                    String iri = item.get("ld").toString() +","+ iris;
                    item.put("ld", iri);
                } else {
                    // 在iri前面拼接逗号
                    String iri = iris +","+ item.get("ld").toString();
                    item.put("ld", iri);
                }
            }
        }

        dataLists.removeAll(toBeRemoved);

        Collections.sort(dataLists, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String name1 = o1.get("lxbs").toString();
                String name2 = o2.get("lxbs").toString();
                // 按照名字进行排序
                int cmp = name1.compareTo(name2);
                if (cmp != 0) {
                    return cmp;
                }
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("zh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("zh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });


        return dataLists;
    }


    /**
     *
     * @param list
     * @return
     */
    private static List<Map<String, Object>> montageld(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()){
            return new ArrayList<>();
        }else {
            Map<String, List<String>> resultMap = new TreeMap<>();
            for (Map<String, Object> map : list) {
                String zh = map.get("zh").toString();
                //String sfc = map.get("ld").toString();
                String sfc = "";
                if (map.get("ld") == null){
                    sfc = "-";
                }else {
                    sfc = map.get("ld").toString();
                }
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
                map.put("zh", entry.getKey());
                map.put("ld", String.join(",", entry.getValue()));
                for (Map<String, Object> item : list) {
                    if (item.get("zh").toString().equals(entry.getKey())) {
                        map.put("lxbs", item.get("lxbs"));
                        map.put("cd", item.get("cd").toString().substring(0,2));
                        map.put("createTime", item.get("createTime"));
                        if (item.get("zdbs") != null){
                            map.put("zdbs", item.get("zdbs"));
                        }
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
                    // 按照名字进行排序
                    int cmp = name1.compareTo(name2);
                    if (cmp != 0) {
                        return cmp;
                    }
                    // 名字相同时按照 qdzh 排序
                    Double qdzh1 = Double.parseDouble(o1.get("zh").toString());
                    Double qdzh2 = Double.parseDouble(o2.get("zh").toString());
                    return qdzh1.compareTo(qdzh2);
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
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + "52隧道雷达厚度.xlsx");
        if (!f.exists()) {
            return new ArrayList<>();
        } else {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));
            List<Map<String, Object>> jgmap = new ArrayList<>();
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                if (!wb.isSheetHidden(wb.getSheetIndex(wb.getSheetAt(j)))) {
                    int zc = 0;
                    int dd = 0;
                    int m = 0;
                    if (wb.getSheetAt(j).getSheetName().equals("左幅") || wb.getSheetAt(j).getSheetName().equals("右幅") ) {
                        zc = cds*3+6;
                        m = cds*3+4;
                        if (cds == 2){
                            dd = 38;
                        }else if (cds == 3){
                            dd = 38;
                        }else if (cds == 4 ){
                            dd = 22;
                        }else if(cds ==5){
                            dd = 26;
                        }
                    }else {
                        zc = cds*3+7;
                        dd = 9;
                    }
                    XSSFSheet slSheet = wb.getSheetAt(j);

                    XSSFCell xmname = slSheet.getRow(1).getCell(1);//项目名
                    XSSFCell htdname = slSheet.getRow(1).getCell(cds*2+3);//合同段名
                    Map map = new HashMap();

                    if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                        slSheet.getRow(4).getCell(zc).setCellType(CellType.STRING);//总点数
                        slSheet.getRow(4).getCell(zc+1).setCellType(CellType.STRING);//合格点数

                        slSheet.getRow(4).getCell(m).setCellType(CellType.STRING);
                        slSheet.getRow(4).getCell(m+1).setCellType(CellType.STRING);

                        double zds = Double.valueOf(slSheet.getRow(4).getCell(zc).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(4).getCell(zc+1).getStringCellValue());
                        String zdsz1 = decf.format(zds);
                        String hgdsz1 = decf.format(hgds);
                        map.put("路面类型", wb.getSheetName(j));
                        map.put("总点数", zdsz1);
                        map.put("设计值", slSheet.getRow(dd).getCell(cds*2+4).getStringCellValue());
                        map.put("合格点数", hgdsz1);
                        map.put("合格率", zds!=0 ? df.format(hgds/zds*100) : 0);
                        map.put("Max", slSheet.getRow(4).getCell(m).getStringCellValue());
                        map.put("Min", slSheet.getRow(4).getCell(m+1).getStringCellValue());
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
        List<Map<String,Object>> lxlist = jjgFbgcSdgcZdhldhdMapper.selectlx(proname,htd);
        int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcSdgcZdhldhdMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        return cds;
    }

    @Override
    public void exportldhd(HttpServletResponse response, String cdsl) throws IOException {
        int cd = Integer.parseInt(cdsl);
        String fileName = "隧道雷达厚度实测数据";
        String[][] sheetNames = {
                {"左幅一车道","左幅二车道","右幅一车道","右幅二车道"},
                {"左幅一车道","左幅二车道","左幅三车道","右幅一车道","右幅二车道","右幅三车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","左幅五车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道","右幅五车道"}
        };
        String[] sheetName = sheetNames[cd-2];
        ExcelUtil.writeExcelMultipleSheets(response, null, fileName, sheetName, new JjgFbgcSdgcZdhldhdVo());

    }

    @Override
    public void importldhd(MultipartFile file, CommonInfoVo commonInfoVo) throws IOException {
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
                        .head(JjgFbgcSdgcZdhldhdVo.class)
                        .headRowNumber(1)
                        .registerReadListener(
                                new ExcelHandler<JjgFbgcSdgcZdhldhdVo>(JjgFbgcSdgcZdhldhdVo.class) {
                                    @Override
                                    public void handle(List<JjgFbgcSdgcZdhldhdVo> dataList) {
                                        for(JjgFbgcSdgcZdhldhdVo ldhdVo: dataList)
                                        {
                                            JjgFbgcSdgcZdhldhd ldhd = new JjgFbgcSdgcZdhldhd();
                                            BeanUtils.copyProperties(ldhdVo,ldhd);
                                            ldhd.setCreatetime(new Date());
                                            ldhd.setProname(commonInfoVo.getProname());
                                            ldhd.setHtd(commonInfoVo.getHtd());
                                           /* ldhd.setZh(Double.parseDouble(ldhdVo.getZh()));
                                            ldhd.setZhz(Double.parseDouble(ldhdVo.getZdzh()));*/
                                            ldhd.setCd(sheetName);
                                            if (sheetName.contains("一")){
                                                ldhd.setVal(1);
                                            }else if (sheetName.contains("二")){
                                                ldhd.setVal(2);
                                            }else if (sheetName.contains("三")){
                                                ldhd.setVal(3);
                                            }else if (sheetName.contains("四")){
                                                ldhd.setVal(4);
                                            }else if (sheetName.contains("五")){
                                                ldhd.setVal(5);
                                            }
                                            jjgFbgcSdgcZdhldhdMapper.insert(ldhd);
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
}
