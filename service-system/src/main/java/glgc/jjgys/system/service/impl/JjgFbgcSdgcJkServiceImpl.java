package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgFbgcSdgcJk;
import glgc.jjgys.model.project.JjgLqsSd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.sdgc.JjgFbgcSdgcJkVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcSdgcJkMapper;
import glgc.jjgys.system.service.JjgFbgcSdgcJkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.service.JjgLqsSdService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.JjgFbgcUtils;
import glgc.jjgys.system.utils.RowCopy;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-10-11
 */
@Service
public class JjgFbgcSdgcJkServiceImpl extends ServiceImpl<JjgFbgcSdgcJkMapper, JjgFbgcSdgcJk> implements JjgFbgcSdgcJkService {

    @Value(value = "${jjgys.path.picturepath}")
    private String picturepath;

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

    @Autowired
    private JjgFbgcSdgcJkMapper jjgFbgcSdgcJkMapper;

    @Autowired
    private JjgLqsSdService jjgLqsSdService;

    @Override
    public void generateJdb(CommonInfoVo commonInfoVo) throws IOException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        QueryWrapper<JjgFbgcSdgcJk> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgFbgcSdgcJk> jjgFbgcSdgcJks = jjgFbgcSdgcJkMapper.selectList(wrapper);
        List<JjgFbgcSdgcJk> zfjk = new ArrayList<>();
        List<JjgFbgcSdgcJk> yfjk = new ArrayList<>();

        if (jjgFbgcSdgcJks!=null && jjgFbgcSdgcJks.size()>0){
            for (JjgFbgcSdgcJk jjgFbgcSdgcJk : jjgFbgcSdgcJks) {
                String zh = jjgFbgcSdgcJk.getZh();
                if (zh.contains("ZK")){
                    zfjk.add(jjgFbgcSdgcJk);
                }else if (zh.contains("YK")){
                    yfjk.add(jjgFbgcSdgcJk);
                }
            }
        }
        //处理桩号
        List<Double> zfzh = new ArrayList<>();
        if (zfjk!=null && zfjk.size()>0){
            for (JjgFbgcSdgcJk jk : zfjk) {
                String zh = jk.getZh();
                double douzh = convertToDouble(zh);
                zfzh.add(douzh);
            }

        }
        List<Double> yfzh = new ArrayList<>();
        if (yfjk!=null && yfjk.size()>0){
            for (JjgFbgcSdgcJk jk : yfjk) {
                String zh = jk.getZh();
                double douzh = convertToDouble(zh);
                yfzh.add(douzh);
            }
        }
        //拿着桩号，取隧道表中查找 是哪个隧道的
        List<JjgLqsSd> zlist = jjgLqsSdService.selectzfsd(proname, htd, "左幅");
        if (zlist!=null && zlist.size()>0){
            for (JjgLqsSd jjgLqsSd : zlist) {
                Double zhq = jjgLqsSd.getZhq();
                Double zhz = jjgLqsSd.getZhz();
                boolean isrange = getzhRang(zfzh,zhq,zhz);
                if (isrange){
                    String sdname = jjgLqsSd.getSdname();
                    DBExceldata(commonInfoVo,sdname,zfjk,"左幅");
                }
            }
        }
        List<JjgLqsSd> ylist = jjgLqsSdService.selectyfsd(proname, htd, "右幅");
        if (ylist!=null && ylist.size()>0){
            for (JjgLqsSd jjgLqsSd : ylist) {
                Double zhq = jjgLqsSd.getZhq();
                Double zhz = jjgLqsSd.getZhz();
                boolean isrange = getzhRang(yfzh,zhq,zhz);
                if (isrange){
                    String sdname = jjgLqsSd.getSdname();
                    DBExceldata(commonInfoVo,sdname,yfjk,"右幅");
                }
            }
        }



    }

    /**
     *
     * @param commonInfoVo
     * @param sdname
     * @param data
     * @param lf
     */
    private void DBExceldata(CommonInfoVo commonInfoVo, String sdname, List<JjgFbgcSdgcJk> data, String lf) throws IOException {
        Collections.sort(data, new Comparator<JjgFbgcSdgcJk>() {
            @Override
            public int compare(JjgFbgcSdgcJk o1, JjgFbgcSdgcJk o2) {
                // 先按zh排序
                int zhComparison = o1.getZh().compareTo(o2.getZh());
                if(zhComparison != 0){
                    return zhComparison;
                }
                // 名字相同时按照 dh 排序
                Double d1 = Double.parseDouble(o1.getDh());
                Double d2 = Double.parseDouble(o2.getDh());
                return d1.compareTo(d2);
            }
        });
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        File f = new File(filespath+File.separator+proname+File.separator+htd+File.separator+"42隧道断面测量坐标表-"+sdname+".xlsx");
        //健壮性判断如果没有数据返回"请导入数据"
        if (data == null || data.size()==0){
            return;
        }else {
            XSSFWorkbook wb = null;
            //存放鉴定表的目录
            File fdir = new File(filespath+File.separator+proname+File.separator+htd);
            if(!fdir.exists()){
                //创建文件根目录
                fdir.mkdirs();
            }
            try {
                File directory = new File("service-system/src/main/resources/static");
                String reportPath = directory.getCanonicalPath();
                String name = "隧道断面测量坐标表.xlsx";
                String path =reportPath + File.separator+name;
                Files.copy(Paths.get(path), new FileOutputStream(f));
                FileInputStream out = new FileInputStream(f);
                wb = new XSSFWorkbook(out);
                createTable(wb,gettableNum(data),lf);
                if(DBtoExcel(wb,data,proname,htd,data.get(0).getFbgc(),lf,sdname)){
                    //设置公式,计算合格点数
                    calculateSheet(wb,wb.getSheet(lf));
                    for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                        JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
                    }
                    JjgFbgcCommonUtils.deletejkEmptySheets(wb);
                    FileOutputStream fileOut = new FileOutputStream(f);
                    wb.write(fileOut);
                    fileOut.flush();
                    fileOut.close();
                }
                out.close();
                wb.close();
            }catch (Exception e) {
                if(f.exists()){
                    f.delete();
                }
                throw new JjgysException(20001, "生成鉴定表错误，请检查数据的正确性");
            }


        }

    }

    /**
     * 计算
     * @param wb
     * @param sheet
     */
    private void calculateSheet(XSSFWorkbook wb, XSSFSheet sheet) {
        XSSFFormulaEvaluator e = new XSSFFormulaEvaluator(wb);
        XSSFRow row = null;
        int tableNum = 0;
        int satiNum = 0;
        ArrayList<XSSFRow> record = new ArrayList<XSSFRow>();
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if("小计".equals(row.getCell(0).toString())){
                row.getCell(4).setCellFormula("COUNT("+
                        sheet.getRow(tableNum*53+5).getCell(4).getReference()+":"+
                        sheet.getRow(tableNum*53+30).getCell(4).getReference()+","+
                        sheet.getRow(tableNum*53+5).getCell(9).getReference()+":"+
                        sheet.getRow(tableNum*53+29).getCell(9).getReference()+")");
                row.getCell(7).setCellFormula("COUNTIF("+
                        sheet.getRow(tableNum*53+5).getCell(4).getReference()+":"+
                        sheet.getRow(tableNum*53+30).getCell(4).getReference()+",\">=0\")+"+
                        "COUNTIF("+sheet.getRow(tableNum*53+5).getCell(9).getReference()+":"+
                        sheet.getRow(tableNum*53+29).getCell(9).getReference()+",\">=0\")"
                );
                record.add(row);
                tableNum += 1;
                if(e.evaluateFormulaCell(row.getCell(4)) == e.evaluateFormulaCell(row.getCell(7))){
                    satiNum += 1;
                    row.getCell(9).setCellValue("合格");
                }
                else{
                    row.getCell(9).setCellValue("不合格");
                }
            }
            if("合计".equals(row.getCell(0).toString())){
                row.getCell(4).setCellValue(tableNum);
                row.getCell(7).setCellValue(satiNum);
                row.getCell(9).setCellFormula(row.getCell(7).getReference()+"/"+
                        row.getCell(4).getReference()+"*100");
            }
        }
    }

    /**
     * 填写数据
     * @param wb
     * @param data
     * @param proname
     * @param htd
     * @param fbgc
     * @param sheetname
     * @param sdname
     * @return
     */
    private boolean DBtoExcel(XSSFWorkbook wb, List<JjgFbgcSdgcJk> data, String proname, String htd, String fbgc, String sheetname, String sdname) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        XSSFSheet sheet = wb.getSheet(sheetname);
        int index = 0;
        int tableNum = 0;
        String jcsj = simpleDateFormat.format(data.get(0).getJcsj());
        //填写表头
        fillTitleCellData(sheet,tableNum,proname,htd,fbgc,jcsj);
        String zh = data.get(0).getZh();
        for (JjgFbgcSdgcJk datum : data) {
            if (zh.equals(datum.getZh())) {

                if( index % 51== 0 && index != 0){
                    tableNum ++;
                    index=0;
                    fillTitleCellData(sheet,tableNum,proname,htd,fbgc,jcsj);

                }
                //填写中间下方的普通单元格
                fillCommonCellData(sheet, tableNum, index, datum,sdname);
                index++;
                fillExcelPicture(wb,sheet,tableNum,datum.getPath());
            }else {
                fillExcelPicture(wb,sheet,tableNum+1,datum.getPath());
                zh = datum.getZh();
                tableNum++;
                index = 0;
                fillTitleCellData(sheet, tableNum, proname, htd, fbgc,jcsj);
                fillCommonCellData(sheet, tableNum, index, datum, sdname);
            }


        }
        return true;


    }

    private void fillExcelPicture(XSSFWorkbook wb,XSSFSheet sheet, int tableNum, String filepath){
        BufferedImage bufferImg = null;
        //先把读进来的图片放到一个ByteArrayOutputStream中，以便产生ByteArray
        try {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            bufferImg = ImageIO.read(new File(filepath));
            ImageIO.write(bufferImg, "png", byteArrayOut);
            //画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
            XSSFDrawing patriarch = sheet.createDrawingPatriarch();
            //anchor主要用于设置图片的属性
            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, -100, 100,(short) 2, (32+53*tableNum), (short) 9, (49+53*tableNum));

            //插入图片
            patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), Workbook.PICTURE_TYPE_JPEG));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param row
     * @param sdname
     */
    private void fillCommonCellData(XSSFSheet sheet, int tableNum, int index, JjgFbgcSdgcJk row, String sdname) {
        sheet.getRow(tableNum*53+index+5).getCell(0).setCellValue(row.getZh()+sdname);
        if (index < 26){
            sheet.getRow(tableNum * 53 + 5 + index % 26).getCell(1).setCellValue(Double.valueOf(row.getDh()));
            sheet.getRow(tableNum * 53 + 5 + index % 26).getCell(2).setCellValue(Double.valueOf(row.getXz()));
            sheet.getRow(tableNum * 53 + 5 + index % 26).getCell(3).setCellValue(Double.valueOf(row.getZz()));
            sheet.getRow(tableNum * 53 + 5 + index % 26).getCell(4).setCellValue(Double.valueOf(row.getPcz()));

        }else {
            sheet.getRow(tableNum * 53 + 5 + index % 26).getCell(6).setCellValue(Double.valueOf(row.getDh()));
            sheet.getRow(tableNum * 53 + 5 + index % 26).getCell(7).setCellValue(Double.valueOf(row.getXz()));
            sheet.getRow(tableNum * 53 + 5 + index % 26).getCell(8).setCellValue(Double.valueOf(row.getZz()));
            sheet.getRow(tableNum * 53 + 5 + index % 26).getCell(9).setCellValue(Double.valueOf(row.getPcz()));
        }

    }

    /**
     * 填写表头
     * @param sheet
     * @param tableNum
     * @param proname
     * @param htd
     * @param fbgc
     * @param jcsj
     */
    private void fillTitleCellData(XSSFSheet sheet, int tableNum, String proname, String htd, String fbgc, String jcsj) {
        sheet.getRow(tableNum * 53 + 1).getCell(0).setCellValue("项目名称："+proname);
        sheet.getRow(tableNum * 53 + 1).getCell(8).setCellValue(htd);
        sheet.getRow(tableNum * 53 + 2).getCell(0).setCellValue("分部工程名称："+fbgc);
        sheet.getRow(tableNum * 53 + 2).getCell(8).setCellValue(jcsj);


    }

    /**
     * 获得页数
     * @param data
     * @return
     */
    private int gettableNum(List<JjgFbgcSdgcJk> data) {
        Set set = new HashSet<>();
        for (JjgFbgcSdgcJk datum : data) {
            String zh = datum.getZh();
            set.add(zh);
        }
        return set.size();
    }

    /**
     *
     * @param wb
     * @param tableNum
     * @param sheetname
     * @throws IOException
     */
    public void createTable(XSSFWorkbook wb, int tableNum, String sheetname) throws IOException {
        int record = 0;
        record = tableNum;
        for (int i = 1; i < record; i++) {
            RowCopy.copyRows(wb, sheetname, sheetname, 0, 52, i * 53);

        }
        //wb.getSheet(sheetname).shiftRows(record * 53-2, record * 53-1, -1);
        XSSFSheet sheet = wb.getSheet(sheetname);

        int lastRowNum = sheet.getLastRowNum();
        int numMergedRegions = sheet.getNumMergedRegions();
        for (int i = numMergedRegions - 1; i >= 0; i--) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            int firstRow = mergedRegion.getFirstRow();
            int lastRow = mergedRegion.getLastRow();

            if (lastRow >= lastRowNum - 2 && lastRow <= lastRowNum) {
                sheet.removeMergedRegion(i);
            }
        }
        RowCopy.copyRows(wb, "source", sheetname, 0, 1,(record) * 53 -2);
        wb.setPrintArea(wb.getSheetIndex(sheetname), 0, 9, 0, record * 53-1);
    }


    /**
     *
     * @param zfzh
     * @param zhq
     * @param zhz
     * @return
     */
    private boolean getzhRang(List<Double> zfzh, Double zhq, Double zhz) {
        for (Double num : zfzh) {
            if (num < zhq || num > zhz) {
                return false;
            }
        }
        return true;
    }


    public static double convertToDouble(String str) {

        String before = StringUtils.substringBetween(str,"K", "+");
        String after = StringUtils.substringAfter(str, "+");

        double b = Double.valueOf(before)*1000;
        Double aft = Double.valueOf(after);
        return b+aft;
    }

    @Override
    public List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat decf = new DecimalFormat("0.##");
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        QueryWrapper<JjgLqsSd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgLqsSd> list = jjgLqsSdService.list(wrapper);
        List<Map<String,Object>> mapList = new ArrayList<>();
        if (list!=null && list.size()>0){
            for (JjgLqsSd jjgLqsSd : list) {
                String sdname = jjgLqsSd.getSdname();
                String lf = jjgLqsSd.getLf();
                //获取鉴定表文件
                File f = new File(filespath+File.separator+proname+File.separator+htd+File.separator+"42隧道断面测量坐标表-"+sdname+".xlsx");
                if(!f.exists()){
                    continue;
                }else {
                    //创建工作簿
                    XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(f));
                    //读取工作表
                    XSSFSheet slSheet = xwb.getSheet(lf);
                    if (slSheet != null) {
                        int lastRowNum = slSheet.getLastRowNum();
                        slSheet.getRow(lastRowNum).getCell(4).setCellType(CellType.STRING);
                        slSheet.getRow(lastRowNum).getCell(7).setCellType(CellType.STRING);
                        slSheet.getRow(lastRowNum).getCell(9).setCellType(CellType.STRING);
                        double zds = Double.valueOf(slSheet.getRow(lastRowNum).getCell(4).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(lastRowNum).getCell(7).getStringCellValue());
                        double hgl = Double.valueOf(slSheet.getRow(lastRowNum).getCell(9).getStringCellValue());

                        String zdsz = decf.format(zds);
                        String hgdsz = decf.format(hgds);
                        String hglz = df.format(hgl);
                        Map<String,Object> jgmap = new HashMap<>();
                        jgmap.put("总点数", zdsz);
                        jgmap.put("sdname", sdname);
                        jgmap.put("合格点数", hgdsz);
                        jgmap.put("合格率", hglz);
                        mapList.add(jgmap);
                    }

                }
            }

        }
        return mapList;
    }

    @Override
    public void exportjk(HttpServletResponse response, String proname, String htd) {
        //导出文件夹
        String fileName = "净空实测数据";
        File file = new File(filespath + File.separator + proname+File.separator + "temp"+ File.separator + fileName + ".xlsx");
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (!file.exists()) {
            ExcelUtil.saveLocal(filespath + File.separator + proname+File.separator + "temp"+ File.separator + fileName, new JjgFbgcSdgcJkVo(), "实测数据");
            System.out.println("生成表格" + file.getName());
        }
        //压缩
        String zipName = htd;
        String downloadName = null;
        try {
            downloadName = URLEncoder.encode(zipName + ".zip", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        response.setHeader("Content-disposition", "attachment; filename=" + downloadName);
        response.setContentType("application/zip;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        try {
            JjgFbgcUtils.zipFile(filespath + File.separator + proname+File.separator + "temp",response.getOutputStream());
        } catch (ZipException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JjgFbgcUtils.deleteDirAndFiles(new File(filespath + File.separator + proname+File.separator + "temp"));

    }

    @Override
    public void importjk(MultipartFile file, CommonInfoVo commonInfoVo) {
        //解压文件
        File file1=JjgFbgcUtils.multipartFileToFile(file);
        ZipFile zipFile= null;
        String tempath = filespath+File.separator+commonInfoVo.getProname();
        try {
            zipFile = new ZipFile(file1);
            zipFile.setFileNameCharset("GBK");
            JjgFbgcUtils.createDirectory("净空实测数据", tempath);
            zipFile.extractAll(tempath + File.separator + "净空实测数据");
        } catch (ZipException e) {
            throw new RuntimeException(e);
        }

        File unzipfile = new File(tempath + File.separator + "净空实测数据");
        File[] filest = unzipfile.listFiles();
        for (File f : filest) {
            File[] files1 = f.listFiles();
            for (File f1 : files1) {
                if (f1.getName().contains(".xlsx")){
                    //如果是xlxs
                    EasyExcel.read(f1.getPath())
                            .sheet(0)
                            .head(JjgFbgcSdgcJkVo.class)
                            .headRowNumber(1)
                            .registerReadListener(
                                    new ExcelHandler<JjgFbgcSdgcJkVo>(JjgFbgcSdgcJkVo.class) {
                                        @Override
                                        public void handle(List<JjgFbgcSdgcJkVo> dataList) {
                                            int rowNumber=2;
                                            for(JjgFbgcSdgcJkVo jkVo: dataList)
                                            {
                                                if (StringUtils.isEmpty(jkVo.getZh())) {
                                                    throw new JjgysException(20001, "第"+rowNumber+"行的数据中，桩号为空，请修改后重新上传");
                                                }


                                                if (!StringUtils.isNumeric(jkVo.getDh()) || StringUtils.isEmpty(jkVo.getDh())) {
                                                    throw new JjgysException(20001, "第"+rowNumber+"行的数据中，点号值有误，请修改后重新上传");
                                                }
                                                if (!StringUtils.isNumeric(jkVo.getXz()) || StringUtils.isEmpty(jkVo.getXz())) {
                                                    throw new JjgysException(20001, "第"+rowNumber+"行的数据中，x值有误，请修改后重新上传");
                                                }
                                                if (!StringUtils.isNumeric(jkVo.getZz()) || StringUtils.isEmpty(jkVo.getZz())) {
                                                    throw new JjgysException(20001, "第"+rowNumber+"行的数据中，z值有误，请修改后重新上传");
                                                }
                                                if (!StringUtils.isNumeric(jkVo.getPcz()) || StringUtils.isEmpty(jkVo.getPcz())) {
                                                    throw new JjgysException(20001, "第"+rowNumber+"行的数据中，偏差值有误，请修改后重新上传");
                                                }
                                                JjgFbgcSdgcJk jk = new JjgFbgcSdgcJk();
                                                BeanUtils.copyProperties(jkVo,jk);
                                                jk.setCreatetime(new Date());
                                                jk.setProname(commonInfoVo.getProname());
                                                jk.setHtd(commonInfoVo.getHtd());
                                                jk.setFbgc(commonInfoVo.getFbgc());
                                                jk.setPath(tempath + File.separator + "净空实测数据" + File.separator+"TJ-1"+ File.separator+jk.getZh()+".png");
                                                jjgFbgcSdgcJkMapper.insert(jk);
                                                rowNumber++;
                                            }
                                        }
                                    }
                            ).doRead();
                }
            }
        }
    }
}
