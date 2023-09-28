package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgJgHtdinfo;
import glgc.jjgys.model.project.JjgJgjcsj;
import glgc.jjgys.model.project.JjgLqsJgQl;
import glgc.jjgys.model.project.JjgLqsJgSd;
import glgc.jjgys.model.projectvo.jggl.JCSJVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgJgHtdinfoMapper;
import glgc.jjgys.system.mapper.JjgJgjcsjMapper;
import glgc.jjgys.system.mapper.JjgLqsJgQlMapper;
import glgc.jjgys.system.mapper.JjgLqsJgSdMapper;
import glgc.jjgys.system.service.JjgJgjcsjService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.RowCopy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
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
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-09-25
 */
@Service
public class JjgJgjcsjServiceImpl extends ServiceImpl<JjgJgjcsjMapper, JjgJgjcsj> implements JjgJgjcsjService {

    @Autowired
    private JjgJgHtdinfoMapper jjgJgHtdinfoMapper;

    @Autowired
    private JjgLqsJgSdMapper jjgLqsJgSdMapper;

    @Autowired
    private JjgLqsJgQlMapper jjgLqsJgQlMapper;

    @Autowired
    private JjgJgjcsjMapper jjgJgjcsjMapper;

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;

    @Override
    public void exportjgjcdata(HttpServletResponse response, String proname) {
        QueryWrapper<JjgJgHtdinfo> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgJgHtdinfo> htdinfo = jjgJgHtdinfoMapper.selectList(wrapper);
        List<Map<String,Object>> result = new ArrayList<>();
        if (htdinfo != null && !htdinfo.isEmpty()){
            for (JjgJgHtdinfo jjgJgHtdinfo : htdinfo) {
                String htd = jjgJgHtdinfo.getName();
                String lx = jjgJgHtdinfo.getLx();
                if (lx.contains("路基工程")){
                    List<Map<String, Object>> ljdata = getLjdata(htd);
                    result.addAll(ljdata);
                }
                if (lx.contains("路面工程")){
                    List<Map<String, Object>> lmdata = getLmdata(htd);
                    result.addAll(lmdata);

                }
                if (lx.contains("桥梁工程")){
                    List<Map<String, Object>> qldata = getQldata(proname,htd);
                    result.addAll(qldata);

                }
                if (lx.contains("隧道工程")){
                    List<Map<String, Object>> sddata = getSddata(proname,htd);
                    result.addAll(sddata);

                }
                if (lx.contains("交安工程")){
                    List<Map<String, Object>> jadata = getJadata(htd);
                    result.addAll(jadata);

                }
            }
        }
        //往excel中写入
        exportExcel(response,proname,result);
    }

    @Override
    public void importjgsj(MultipartFile file, String projectname) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(JCSJVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<JCSJVo>(JCSJVo.class) {
                                @Override
                                public void handle(List<JCSJVo> dataList) {
                                    for(JCSJVo ql: dataList)
                                    {
                                        JjgJgjcsj jjgLqsQl = new JjgJgjcsj();
                                        BeanUtils.copyProperties(ql,jjgLqsQl);
                                        jjgLqsQl.setProname(projectname);
                                        jjgLqsQl.setCreateTime(new Date());
                                        jjgJgjcsjMapper.insert(jjgLqsQl);
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }

    @Override
    public void generatepdb(String projectname) {
        QueryWrapper<JjgJgjcsj> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",projectname);
        List<JjgJgjcsj> jjgJgjcsjs = jjgJgjcsjMapper.selectList(wrapper);
        //filename 合格率

        //先按合同段，然后按单位工程分别写入到不同的工作簿
        if (jjgJgjcsjs != null && !jjgJgjcsjs.isEmpty()){
            List<Map<String,Object>> data = getdata(jjgJgjcsjs);
            Map<String, List<Map<String,Object>>> groupedData = data.stream()
                    .collect(Collectors.groupingBy(m -> m.get("htd").toString()));
            groupedData.forEach((group, grouphtdData) -> {
                try {
                    writeExceldata(group,grouphtdData,projectname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     *
     * @param htd
     * @param data
     * @param proname
     */
    private void writeExceldata(String htd, List<Map<String,Object>> data, String proname) throws IOException {
        //每个合同段中的
        XSSFWorkbook wb = null;
        File f = new File(jgfilepath + File.separator + proname + File.separator + htd + File.separator + "00评定表.xlsx");
        File fdir = new File(jgfilepath + File.separator + proname + File.separator + htd);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        File directory = new File("service-system/src/main/resources/static");
        String reportPath = directory.getCanonicalPath();
        String name = "合同段评定表.xlsx";
        String path = reportPath + File.separator + name;
        Files.copy(Paths.get(path), new FileOutputStream(f));
        FileInputStream out = new FileInputStream(f);
        wb = new XSSFWorkbook(out);

        Map<String, List<Map<String,Object>>> groupedData = data.stream()
                .collect(Collectors.groupingBy(m -> m.get("dwgc").toString()));
        for (String key : groupedData.keySet()) {
            List<Map<String,Object>> list = groupedData.get(key);

            if (key.equals("路基工程")){
                writeData(wb,list,key,proname,htd);
            }else if (key.equals("路面工程")){
                writeData(wb,list,key,proname,htd);
            }else if (key.equals("交安工程")){
                writeData(wb,list,key,proname,htd);
            }else {
                //桥梁和隧道
                writeData(wb,list,key,proname,htd);
            }

        }
    }

    /**
     *
     * @param wb
     * @param data
     * @param sheetname
     * @param proname
     * @param htd
     */
    private void writeData(XSSFWorkbook wb, List<Map<String,Object>> data, String sheetname, String proname, String htd) {
        copySheet(wb,sheetname);
        XSSFPrintSetup ps = wb.getSheet(sheetname).getPrintSetup();
        ps.setLandscape(true); // 打印方向，true：横向，false：纵向(默认)

        QueryWrapper<JjgJgHtdinfo> wrapperhtd = new QueryWrapper<>();
        wrapperhtd.like("proname", proname);
        wrapperhtd.like("name", htd);
        JjgJgHtdinfo htdlist = jjgJgHtdinfoMapper.selectOne(wrapperhtd);

        XSSFSheet sheet = wb.getSheet(sheetname);
        createTable(wb,gettableNum(data),sheetname);

        int index = 0;
        int tableNum = 0;
        String fbgc = data.get(0).get("fbgc").toString();
        for (Map<String,Object> datum : data) {
            if (fbgc.equals(datum.get("fbgc").toString())){
                fillTitleData(sheet,tableNum,proname,htd,htdlist,datum.get("fbgc").toString());
                fillCommonData(sheet,tableNum,index,datum);
                index++;
            }else {
                fbgc = datum.get("fbgc").toString();
                tableNum ++;
                index = 0;
                fillTitleData(sheet,tableNum,proname,htd,htdlist,datum.get("fbgc").toString());
                fillCommonData(sheet,tableNum,index,datum);
                index += 1;
            }

        }
        //写完当前工作簿的数据后，就要插入公式计算了
        calculateFbgcSheet(sheet);
        for (int j = 0; j < wb.getNumberOfSheets(); j++) {
            JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
        }

    }

    /**
     * 计算分部工程的结果
     * @param sheet
     */
    private void calculateFbgcSheet(XSSFSheet sheet) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if ("实测项目是否全部合格".equals(row.getCell(0).toString())) {
                rowstart = sheet.getRow(i-15);
                rowend = sheet.getRow(i-1);
                row.getCell(8).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+"),\"√\", \"\")");//=IF(COUNTIF(T7:T21, "合格") = COUNTA(T7:T21), "√", "")
                row.getCell(10).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+"),\"\", \"×\")");//=IF(COUNTIF(T7:T21, "不合格") = COUNTA(T7:T21), "", "×")

                row.getCell(16).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+"),\"√\", \"\")");//=IF(COUNTIF(T7:T21, "合格") = COUNTA(T7:T21), "√", "")
                row.getCell(19).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+"),\"\", \"×\")");//=IF(COUNTIF(T7:T21, "不合格") = COUNTA(T7:T21), "", "×")
            }
        }

    }

    /**
     * 分部
     * @param sheet
     * @param tableNum
     * @param index
     * @param datum
     */
    private void fillCommonData(XSSFSheet sheet, int tableNum, int index, Map<String,Object> datum) {
        sheet.getRow(tableNum * 22 + index + 6).getCell(1).setCellValue(1+index);
        sheet.getRow(tableNum * 22 + index + 6).getCell(2).setCellValue(datum.get("ccxm").toString());
        sheet.getRow(tableNum * 22 + index + 6).getCell(6).setCellValue(String.valueOf(datum.get("gdz")));

        sheet.getRow(tableNum * 22 + index + 6).getCell(7).setCellValue(datum.get("filename").toString());
        sheet.getRow(tableNum * 22 + index + 6).getCell(17).setCellValue(datum.get("合格率").toString());
        if (datum.get("ccname").toString().contains("*")){
            Double value = Double.valueOf(datum.get("合格率").toString());
            if (value == 100){
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("合格");
            }else {
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("不合格");
            }
        }else if (datum.get("ccname").toString().contains("△")){
            Double value = Double.valueOf(datum.get("合格率").toString());
            if (value >= 95){
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("合格");
            }else {
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("不合格");
            }
        }else {
            Double value = Double.valueOf(datum.get("合格率").toString());
            if (value >= 80){
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("合格");
            }else {
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("不合格");
            }
        }
    }

    /**
     * 分部
     * @param sheet
     * @param tableNum
     * @param proname
     * @param htd
     * @param htdlist
     * @param fbgc
     */
    private void fillTitleData(XSSFSheet sheet, int tableNum, String proname, String htd,JjgJgHtdinfo htdlist,String fbgc){
        sheet.getRow(tableNum * 22 +1).getCell(2).setCellValue(htd);
        sheet.getRow(tableNum * 22 +1).getCell(8).setCellValue(fbgc);
        sheet.getRow(tableNum * 22 +2).getCell(2).setCellValue(getgcbw(htdlist.getZhq(),htdlist.getZhz()));
        sheet.getRow(tableNum * 22 +2).getCell(8).setCellValue(htdlist.getSgdw());
        sheet.getRow(tableNum * 22 +2).getCell(15).setCellValue(htdlist.getJldw());
        sheet.getRow(tableNum * 22 +1).getCell(15).setCellValue(proname);
    }

    /**
     *分部
     * @param zhq
     * @param zhz
     * @return
     */
    private String getgcbw(String zhq, String zhz) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        double a = Double.valueOf(zhq);
        double b = Double.valueOf(zhz);

        int aa = (int) a / 1000;
        int bb = (int) b / 1000;
        double cc = a % 1000;
        double dd = b % 1000;
        String result = "K"+aa+"+"+decimalFormat.format(cc)+"--"+"K"+bb+"+"+decimalFormat.format(dd);

        return result;
    }

    /**
     * 分部
     * @param wb
     * @param gettableNum
     * @param sheetname
     */
    private void createTable(XSSFWorkbook wb,int gettableNum,String sheetname) {
        for(int i = 1; i < gettableNum; i++){
            RowCopy.copyRows(wb, sheetname, sheetname, 0, 21, i*22);
        }
        if(gettableNum > 1){
            wb.setPrintArea(wb.getSheetIndex(sheetname), 0, 20, 0, gettableNum*22-1);
        }
    }

    /**
     * 分部
     * @param data
     * @return
     */
    private int gettableNum(List<Map<String,Object>> data) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String,Object> map : data) {
            String name = map.get("fbgc").toString();
            if (resultMap.containsKey(name)) {
                resultMap.put(name, resultMap.get(name) + 1);
            } else {
                resultMap.put(name, 1);
            }
        }
        int num = 0;
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            int value = entry.getValue();
            if (value%15==0){
                num += value/15;
            }else {
                num += value/15+1;
            }
        }
        return num;
    }

    /**
     *
     * @param wb
     * @param sheetname
     */
    private void copySheet(XSSFWorkbook wb,String sheetname) {
        String sourceSheetName = "模板";
        String targetSheetName = sheetname;
        XSSFSheet sourceSheet = wb.getSheet(sourceSheetName);

        // 创建新的工作表，并将源工作表中的内容复制到新工作表
        XSSFSheet targetSheet = wb.createSheet(targetSheetName);
        copySheetInfo(wb,sourceSheet, targetSheet);


    }

    /**
     *
     * @param wb
     * @param sourceSheet
     * @param targetSheet
     */
    private static void copySheetInfo(XSSFWorkbook wb, Sheet sourceSheet, Sheet targetSheet) {
        int maxColumnNum = 0;
        for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
            Row sourceRow = sourceSheet.getRow(i);
            Row newRow = targetSheet.createRow(i);
            if (sourceRow != null) {
                copyRow(sourceRow, newRow, wb);
                if (sourceRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = sourceRow.getLastCellNum();
                }
            }
        }

        // 复制列宽
        for (int j = 0; j < maxColumnNum; j++) {
            targetSheet.setColumnWidth(j, sourceSheet.getColumnWidth(j));
        }

        // 复制合并单元格
        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sourceSheet.getMergedRegion(i);
            targetSheet.addMergedRegion(mergedRegion);
        }

        // 复制打印区域
        PrintSetup sourcePrintSetup = sourceSheet.getPrintSetup();
        PrintSetup targetPrintSetup = targetSheet.getPrintSetup();
        targetPrintSetup.setLandscape(sourcePrintSetup.getLandscape());
        targetPrintSetup.setPaperSize(sourcePrintSetup.getPaperSize());
        targetPrintSetup.setFitWidth(sourcePrintSetup.getFitWidth());
        targetPrintSetup.setFitHeight(sourcePrintSetup.getFitHeight());
        targetPrintSetup.setScale(sourcePrintSetup.getScale());

        // 复制页眉
        Header sourceHeader = sourceSheet.getHeader();
        Header targetHeader = targetSheet.getHeader();
        targetHeader.setCenter(sourceHeader.getCenter());
        targetHeader.setLeft(sourceHeader.getLeft());
        targetHeader.setRight(sourceHeader.getRight());

        // 复制页脚
        Footer sourceFooter = sourceSheet.getFooter();
        Footer targetFooter = targetSheet.getFooter();
        targetFooter.setCenter(sourceFooter.getCenter());
        targetFooter.setLeft(sourceFooter.getLeft());
        targetFooter.setRight(sourceFooter.getRight());


    }

    /**
     *
     * @param sourceRow
     * @param newRow
     * @param wb
     */
    private static void copyRow(Row sourceRow, Row newRow, Workbook wb) {
        newRow.setHeight(sourceRow.getHeight());
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            Cell newCell = newRow.createCell(i);
            if (sourceCell != null) {
                CellStyle sourceCellStyle = sourceCell.getCellStyle();
                CellStyle newCellStyle = wb.createCellStyle();
                newCellStyle.cloneStyleFrom(sourceCellStyle);
                newCell.setCellStyle(newCellStyle);

                CellType cellType = sourceCell.getCellTypeEnum();

                switch (cellType) {
                    case BOOLEAN:
                        newCell.setCellValue(sourceCell.getBooleanCellValue());
                        break;
                    case STRING:
                        newCell.setCellValue(sourceCell.getRichStringCellValue());
                        break;
                    case NUMERIC:
                        newCell.setCellValue(sourceCell.getNumericCellValue());
                        break;
                    case FORMULA:
                        newCell.setCellFormula(sourceCell.getCellFormula());
                        break;
                    case BLANK:
                        // do nothing
                        break;
                    default:
                        // do nothing
                        break;
                }
            }
        }
    }

    /**
     *
     * @param jjgJgjcsjs
     * @return
     */
    private List<Map<String, Object>> getdata(List<JjgJgjcsj> jjgJgjcsjs) {
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String, Object>> result = new ArrayList<>();
        for (JjgJgjcsj data : jjgJgjcsjs) {
            String ccxm = data.getCcxm();
            String hgds = data.getHgds();
            String zds = data.getZds();
            String fbgc = data.getFbgc();

            Map<String, Object> map = new HashMap<>();
            map.put("htd",data.getHtd());
            map.put("dwgc",data.getDwgc());
            map.put("fbgc",fbgc);
            map.put("ccxm",ccxm);
            map.put("gdz",data.getGdz());

            if (fbgc.equals("路基土石方") && ccxm.equals("沉降")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基压实度沉降质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路基土石方") && ccxm.equals("压实度（沙砾）")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路基土石方") && ccxm.equals("压实度（灰土）")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路基土石方") && ccxm.equals("弯沉")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基弯沉质量鉴定表》检测"+zds+"个评定单元,合格"+hgds+"个评定单元");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路基土石方") && ccxm.equals("边坡")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基边坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("排水工程") && ccxm.equals("断面尺寸")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《结构（断面）尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("排水工程") && ccxm.equals("铺砌厚度")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《排水铺砌厚度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("小桥") && ccxm.equals("混凝土强度")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《小桥砼强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("小桥") && ccxm.equals("断面尺寸")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《小桥结构尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("涵洞") && ccxm.equals("混凝土强度")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《涵洞砼强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("涵洞") && ccxm.equals("结构尺寸")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《涵洞结构尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("支挡工程") && ccxm.equals("混凝土强度")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《支挡工程砼强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("支挡工程") && ccxm.equals("断面尺寸")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《支挡工程结构尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面压实度（路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面压实度（隧道路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面弯沉")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《路面弯沉质量鉴定结果汇总表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面车辙（路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面车辙质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面车辙（隧道路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《隧道路面车辙质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面渗水系数（路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面渗水系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面渗水系数（隧道路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面渗水系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("混凝土路面强度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面强度鉴定结果汇总表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("混凝土路面相邻板高差")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面相邻板高差质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（沥青路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（混凝土路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("平整度（桥面系混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（桥面系沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（隧道路面混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（隧道路面沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（混凝土路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（沥青路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（桥面系混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（桥面系沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《桥面系构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（隧道路面混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（隧道路面沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《隧道路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("摩擦系数（沥青路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面摩擦系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("摩擦系数（桥面系）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《桥面系摩擦系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("摩擦系数（隧道路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《隧道路面摩擦系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("厚度（混凝土路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面厚度质量鉴定表（钻芯法） 》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("厚度（沥青路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面厚度质量鉴定表（钻芯法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("厚度（路面雷达法）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《路面厚度质量鉴定表（雷达法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("厚度（隧道路面混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面厚度质量鉴定表（钻芯法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("厚度（隧道路面沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面厚度质量鉴定表（钻芯法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("厚度（隧道路面雷达法）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《隧道路面厚度质量鉴定表（雷达法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("横坡（混凝土路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（沥青路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（桥面系混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（桥面系沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（隧道路面混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（隧道路面沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }

            else if (fbgc.equals("标志") && ccxm.equals("立柱竖直度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交通标志板安装质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("标志") && ccxm.equals("标志板净空")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交通标志板安装质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("标志") && ccxm.equals("标志板厚度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交通标志板安装质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("标志") && ccxm.equals("标志面反光膜等级及逆射光系数")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交通标志板安装质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            } else if (fbgc.equals("标线") && ccxm.equals("反光标线逆反射系数")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路交通标线施工质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("标线") && ccxm.equals("标线厚度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路交通标线施工质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("波形梁板基地金属厚度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路防护栏施工质量鉴定表（波形梁钢护栏）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("波形梁钢护栏立柱壁厚")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路防护栏施工质量鉴定表（波形梁钢护栏）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("波形梁钢护栏立柱埋入深度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路防护栏施工质量鉴定表（波形梁钢护栏）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("波形梁钢护栏横梁中心高度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路防护栏施工质量鉴定表（波形梁钢护栏）》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("混凝土护栏强度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交安工程砼护栏强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("混凝土护栏断面尺寸")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交安砼护栏断面尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)):0);
                result.add(map);
            }
        }
        return result;
    }

    /**
     *
     * @param response
     * @param proname
     * @param result
     */
    private void exportExcel(HttpServletResponse response, String proname, List<Map<String, Object>> result) {
        try {
            String fileName = proname+"检测数据录入表";
            String sheetName1 = "检测数据";
            ExcelUtil.writeExcelWithSheetsJCdata(response, result, fileName, sheetName1, new JCSJVo())
                    .finish();
        } catch (Exception e) {
            throw new JjgysException(20001,"导出失败");
        }

    }

    /**
     *
     * @param proname
     * @param htd
     * @return
     */
    private List<Map<String, Object>> getSddata(String proname, String htd) {
        String[] ccxm1 = {"*衬砌强度", "△衬砌厚度", "大面平整度"};
        String[] ccxm2 = {"宽度", "△净空"};
        String[] ccxm3 = {"沥青路面压实度（隧道路面）", "沥青路面弯沉","沥青路面车辙（隧道路面）","沥青路面渗水系数（隧道路面）","混凝土路面强度","混凝土路面相邻板高差","平整度（隧道路面沥青）",
                "平整度（隧道路面混凝土）","构造深度（隧道路面）","摩擦系数（隧道路面）","厚度（隧道路面混凝土）","厚度（隧道路面沥青）","厚度（隧道路面雷达）","横坡（隧道路面沥青）","横坡（隧道路面混凝土）"};
        List<Map<String,Object>> result = new ArrayList<>();
        QueryWrapper<JjgLqsJgSd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgLqsJgSd> htdinfo = jjgLqsJgSdMapper.selectList(wrapper);
        if (htdinfo!=null && !htdinfo.isEmpty()){
            for (JjgLqsJgSd jjgLqsJgSd : htdinfo) {
                String sdname = jjgLqsJgSd.getSdname();
                for (String ccxm : ccxm1) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", sdname);
                    map.put("fbgc", "衬砌");
                    map.put("ccxm", ccxm);
                    result.add(map);
                }
                for (String ccxm : ccxm2) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", sdname);
                    map.put("fbgc", "总体");
                    map.put("ccxm", ccxm);
                    result.add(map);
                }
                for (String ccxm : ccxm3) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", sdname);
                    map.put("fbgc", "隧道路面");
                    map.put("ccxm", ccxm);
                    result.add(map);
                }

            }
        }
        return result;
    }

    /**
     *
     * @param proname
     * @param htd
     * @return
     */
    private List<Map<String, Object>> getQldata(String proname, String htd) {
        String[] ccxm1 = {"*混凝土强度", "主要结构尺寸", "钢筋保护层厚度"};
        String[] ccxm2 = {"*混凝土强度", "主要结构尺寸","钢筋保护层厚度","*竖直度"};
        String[] ccxm3 = {"沥青桥平整度", "混凝土桥平整度","沥青桥面构造深度","混凝土桥面构造深度","摩擦系数","沥青桥面横坡","混凝土桥面横坡"};
        List<Map<String,Object>> result = new ArrayList<>();
        QueryWrapper<JjgLqsJgQl> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgLqsJgQl> htdinfo = jjgLqsJgQlMapper.selectList(wrapper);
        if (htdinfo!=null && !htdinfo.isEmpty()){
            for (JjgLqsJgQl jjgLqsJgQl : htdinfo) {
                String qlname = jjgLqsJgQl.getQlname();
                for (String ccxm : ccxm1) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", qlname);
                    map.put("fbgc", "上部");
                    map.put("ccxm", ccxm);
                    result.add(map);
                }
                for (String ccxm : ccxm2) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", qlname);
                    map.put("fbgc", "下部");
                    map.put("ccxm", ccxm);
                    result.add(map);
                }
                for (String ccxm : ccxm3) {
                    Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", qlname);
                    map.put("fbgc", "桥面系");
                    map.put("ccxm", ccxm);
                    result.add(map);
                }
            }
        }
        return result;
    }

    /**
     *
     * @param htd
     * @return
     */
    private List<Map<String, Object>> getJadata(String htd) {
        String[] ccxm1 = {"立柱竖直度", "标志板净空", "标志板厚度", "标志面反光膜等级及逆射光系数"};
        String[] ccxm2 = {"△反光标线逆反射系数", "△标线厚度"};
        String[] ccxm3 = {"*波形梁板基地金属厚度", "*波形梁钢护栏立柱壁厚","*波形梁钢护栏立柱埋入深度","*波形梁钢护栏横梁中心高度","*混凝土护栏强度","△砼护栏断面尺寸"};
        List<Map<String,Object>> result = new ArrayList<>();
        for (String ccxm : ccxm1) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "交通安全设施");
            map.put("fbgc", "标志");
            map.put("ccxm", ccxm);
            result.add(map);
        }
        for (String ccxm : ccxm2) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "交通安全设施");
            map.put("fbgc", "标线");
            map.put("ccxm", ccxm);
            result.add(map);
        }
        for (String ccxm : ccxm3) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "交通安全设施");
            map.put("fbgc", "防护栏");
            map.put("ccxm", ccxm);
            result.add(map);
        }
        return result;

    }

    /**
     *
     * @param htd
     * @return
     */
    private List<Map<String, Object>> getLmdata(String htd) {
        List<Map<String,Object>> result = new ArrayList<>();
        String[] ccxm = {"△沥青路面压实度（路面面层）", "△沥青路面压实度（隧道路面）", "△沥青路面弯沉", "沥青路面车辙（路面面层）", "沥青路面车辙（隧道路面）",
                "沥青路面渗水系数（路面面层）","沥青路面渗水系数（隧道路面）","*混凝土路面强度","混凝土路面相邻板高差","平整度（沥青路面面层）","平整度（混凝土路面）","平整度（桥面系混凝土）",
                "平整度（桥面系沥青）","平整度（隧道路面混凝土）","平整度（隧道路面沥青）","构造深度（混凝土路面）","构造深度（沥青路面面层）","构造深度（桥面系混凝土）","构造深度（桥面系沥青）",
                "构造深度（隧道路面混凝土）","构造深度（隧道路面沥青）","摩擦系数（沥青路面面层）","摩擦系数（桥面系）","摩擦系数（隧道路面）","△厚度（混凝土路面）","△厚度（沥青路面）","△厚度（路面雷达法）"
                ,"△厚度（隧道路面混凝土）","△厚度（隧道路面沥青）","△厚度（隧道路面雷达法）","横坡（混凝土路面）","横坡（沥青路面）","横坡（桥面系混凝土）","横坡（桥面系沥青）","横坡（隧道路面混凝土）","横坡（隧道路面沥青）"};

        for (String s : ccxm) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路面工程");
            map.put("fbgc", "路面面层");
            map.put("ccxm", s);
            result.add(map);
        }
        return result;
    }

    /**
     *
     * @param htd
     * @return
     */
    private List<Map<String,Object>> getLjdata(String htd){
        String[] ccxm1 = {"△沉降", "△压实度（沙砾）", "△压实度（灰土）", "△弯沉", "边坡"};
        String[] ccxm2 = {"断面尺寸", "铺砌厚度"};
        String[] ccxm3 = {"*混凝土强度", "△断面尺寸"};
        String[] ccxm4 = {"*混凝土强度", "结构尺寸"};
        List<Map<String,Object>> result = new ArrayList<>();
        for (String ccxm : ccxm1) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "路基土石方");
            map.put("ccxm", ccxm);
            result.add(map);
        }
        for (String ccxm : ccxm2) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "排水工程");
            map.put("ccxm", ccxm);
            result.add(map);
        }
        for (String ccxm : ccxm3) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "小桥");
            map.put("ccxm", ccxm);
            result.add(map);
        }
        for (String ccxm : ccxm4) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "涵洞");
            map.put("ccxm", ccxm);
            result.add(map);
        }
        for (String ccxm : ccxm3) {
            Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "支挡工程");
            map.put("ccxm", ccxm);
            result.add(map);
        }
        return result;

    }

}
