package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgFbgcSdgcZdhcz;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.sdgc.JjgFbgcSdgcZdhczVo;
import glgc.jjgys.model.system.SysRole;
import glgc.jjgys.model.system.SysUser;
import glgc.jjgys.model.system.SysUserRole;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcSdgcZdhczMapper;
import glgc.jjgys.system.mapper.SysUserRoleMapper;
import glgc.jjgys.system.service.JjgFbgcSdgcZdhczService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.service.SysRoleService;
import glgc.jjgys.system.service.SysUserService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.RowCopy;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class JjgFbgcSdgcZdhczServiceImpl extends ServiceImpl<JjgFbgcSdgcZdhczMapper, JjgFbgcSdgcZdhcz> implements JjgFbgcSdgcZdhczService {

    @Autowired
    private JjgFbgcSdgcZdhczMapper jjgFbgcSdgcZdhczMapper;

    @Value(value = "${jjgys.path.filepath}")
    private String filepath;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;


    @Override
    public void generateJdb(CommonInfoVo commonInfoVo) throws IOException, ParseException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        String username = commonInfoVo.getUsername();
        List<Map<String,Object>> lxlist = jjgFbgcSdgcZdhczMapper.selectlx(proname,htd);
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcSdgcZdhczMapper.selectcdnum(proname,htd,zx);
            int cds = 0;
            if (num == 1){
                cds = 2;
            }else {
                cds=num;
            }
            handlezxData(proname,htd,zx,cds,commonInfoVo.getSjz(),username);
        }
        /*int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcSdgcZdhczMapper.selectcdnum(proname,htd,zx);
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
     * @param username
     * @throws IOException
     * @throws ParseException
     */
    private void handlezxData(String proname, String htd, String zx, int cdsl, String sjz, String username) throws IOException, ParseException {
        log.info("准备数据......");
        List<Map<String, Object>> datazf = new ArrayList<>();
        List<Map<String, Object>> datayf = new ArrayList<>();

        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.eq("username", username);
        SysUser one = sysUserService.getOne(sysUserQueryWrapper);
        String userid = one.getId().toString();

        QueryWrapper<SysUserRole> sysUserRoleQueryWrapper = new QueryWrapper<>();
        sysUserRoleQueryWrapper.eq("user_id", userid);
        SysUserRole sysUserRole = sysUserRoleMapper.selectOne(sysUserRoleQueryWrapper);
        String roleId = sysUserRole.getRoleId();

        QueryWrapper<SysRole> sysRoleQueryWrapper = new QueryWrapper<>();
        sysRoleQueryWrapper.eq("id", roleId);
        SysRole role = sysRoleService.getOne(sysRoleQueryWrapper);
        String rolecode = role.getRoleCode();

        if (rolecode.equals("YH")){
            datazf = jjgFbgcSdgcZdhczMapper.selectzfListyh(proname, htd,zx,username);
            datayf = jjgFbgcSdgcZdhczMapper.selectyfListyh(proname, htd,zx,username);

        }else {
            datazf = jjgFbgcSdgcZdhczMapper.selectzfList(proname, htd,zx);
            datayf = jjgFbgcSdgcZdhczMapper.selectyfList(proname, htd,zx);

        }

        List<Map<String, Object>> sdzxList = groupByZh(datazf);
        List<Map<String, Object>> sdyxList = groupByZh(datayf);

        writeExcelData(proname, htd,sdzxList, sdyxList, cdsl, sjz,zx);

    }

    /**
     *
     * @param proname
     * @param htd
     * @param sdzxList
     * @param sdyxList
     * @param cdsl
     * @param sjz
     * @param zx
     * @throws IOException
     * @throws ParseException
     */
    private void writeExcelData(String proname, String htd, List<Map<String, Object>> sdzxList, List<Map<String, Object>> sdyxList, int cdsl, String sjz, String zx) throws IOException, ParseException {
        XSSFWorkbook wb = null;
        String fname="45隧道路面车辙-"+zx+".xlsx";
        File f = new File(filepath+File.separator+proname+File.separator+htd+File.separator+fname);
        File fdir = new File(filepath + File.separator + proname + File.separator + htd);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        try {
            File directory = new File("src/main/resources/static");
            String reportPath = directory.getCanonicalPath();
            String filename = "";
            String sheetsname = "隧道";

            if (cdsl == 5){
                filename = "车辙-5车道.xlsx";
            }else if (cdsl == 4){
                filename = "车辙-4车道.xlsx";
            }else if (cdsl == 3){
                filename = "车辙-3车道.xlsx";
            }else if (cdsl == 2){
                filename = "车辙-2车道.xlsx";
            }
            String path = reportPath + File.separator + filename;
            Files.copy(Paths.get(path), new FileOutputStream(f));
            FileInputStream out = new FileInputStream(f);
            wb = new XSSFWorkbook(out);


            if (sdzxList.size() >0 && !sdzxList.isEmpty()){
                DBtoExcel(proname,htd,sdzxList,wb,"左幅-"+sheetsname,cdsl,sjz);
            }

            if (sdyxList.size() >0 && !sdyxList.isEmpty()){
                DBtoExcel(proname,htd,sdyxList,wb,"右幅-"+sheetsname,cdsl,sjz);
            }

            String[] arr = {"左幅-路面","右幅-路面","左幅-隧道","右幅-隧道","左幅-桥","右幅-桥"};
            for (int i = 0; i < arr.length; i++) {
                if (shouldBeCalculate(wb.getSheet(arr[i]))) {
                    calculateTunnelAndBridgeSheet(wb, wb.getSheet(arr[i]), cdsl);
                    JjgFbgcCommonUtils.updateFormula(wb, wb.getSheet(arr[i]));
                } else {
                    wb.removeSheetAt(wb.getSheetIndex(arr[i]));
                }
            }

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
     * @param wb
     * @param sheet
     * @param cdsl
     */
    private void calculateTunnelAndBridgeSheet(XSSFWorkbook wb,XSSFSheet sheet,int cdsl) {
        log.info("开始计算{}",sheet.getSheetName());
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
            if (!"".equals(row.getCell(0).toString()) && row.getCell(0).toString().contains("质量鉴定表") && flag) {
                flag = false;
            }
            if(flag){
                rowstart = sheet.getRow(i);
                rowend = sheet.getRow(i+26);
                calculateTotalData(sheet, rowstart, rowend, i,cdsl);
                count ++;
                i += 26;
            }
            if ("桩号".equals(row.getCell(0).toString())) {
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
     * @param sheet
     * @param rowstart
     * @param rowend
     * @param i
     * @param cdsl
     */
    public void calculateTotalData(XSSFSheet sheet, XSSFRow rowstart, XSSFRow rowend, int i,int cdsl){
        if (cdsl == 2 || cdsl == 3){
            int a = 0;
            if (cdsl == 2){
                a = 11;
            }else if (cdsl == 3){
                a = 14;
            }
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
            //平均值
            //=IF(ISERROR(AVERAGE(B223:D249,F223:H249,J223:L249,N223:P241)),"",AVERAGE(B223:D249,F223:H249,J223:L249,N223:P241))

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

            sheet.getRow(i+25).getCell(3*cdsl+4).setCellFormula("COUNT("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+")");//=COUNT(E7:F33,H7:I33,K7:L33,N7:O25)
            sheet.getRow(i+26).getCell(3*cdsl+4).setCellFormula("SUM(COUNTIF("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(a).getReference()+"),COUNTIF("
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(a).getReference()+"),COUNTIF("
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(a).getReference()+"),COUNTIF("
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(a).getReference()+"))");//=SUM(COUNTIF(E7:F33,">="&O26),COUNTIF(H7:I33,">="&O26),COUNTIF(K7:L33,">="&O26),COUNTIF(N7:O25,\">=\"&O26))
            sheet.getRow(i+21).getCell(3*cdsl+4).setCellFormula("IF("+sheet.getRow(i+25).getCell(3*cdsl+4).getReference()
                    +"=0,\"-\",MAX("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"))");//=MAX(E7:F33,H7:I33,K7:L33,N7:O25)
            sheet.getRow(i+22).getCell(3*cdsl+4).setCellFormula("IF("+sheet.getRow(i+25).getCell(3*cdsl+4).getReference()
                    +"=0,\"-\",MIN("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +rowend.getCell(3*cdsl+2).getReference()+","
                    +rowstart.getCell(3*cdsl+4).getReference()+":"
                    +sheet.getRow(i+18).getCell(4*cdsl+3).getReference()+"))");//=MIN(E7:F33,H7:I33,K7:L33,N7:O25)
        }else if (cdsl == 4 || cdsl ==5){
            /**2*cdsl+3
             * 4c 6  9
             * 5c 7  11
             */
            //平均值
            sheet.getRow(i+23).getCell(2*cdsl+3).setCellFormula("IF(ISERROR(AVERAGE("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+")),\"-\","+
                    "AVERAGE("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"))");
            //标准差
            sheet.getRow(i+24).getCell(2*cdsl+3).setCellFormula("IF(ISERROR(ROUND(STDEV("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"),3)),\"-\","+
                    "ROUND(STDEV("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"),3))");//=ROUND(STDEV(E7:F33,H7:I33,K7:L33,N7:O25),3)

            //检测点数
            sheet.getRow(i+25).getCell(2*cdsl+3).setCellFormula("COUNT("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+")");//=COUNT(E7:F33,H7:I33,K7:L33,N7:O25)
            //合格点数
            sheet.getRow(i+26).getCell(2*cdsl+3).setCellFormula("SUM(COUNTIF("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(14).getReference() +"),COUNTIF("
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(14).getReference()+ "),COUNTIF("
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+",\"<=\"&"
                    +sheet.getRow(i+19).getCell(14).getReference()+"))");
            //最大值
            sheet.getRow(i+21).getCell(2*cdsl+3).setCellFormula("IF("+sheet.getRow(i+25).getCell(2*cdsl+3).getReference()
                    +"=0,\"-\",MAX("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"))");
            //最小值
            sheet.getRow(i+22).getCell(2*cdsl+3).setCellFormula("IF("+sheet.getRow(i+25).getCell(2*cdsl+3).getReference()
                    +"=0,\"-\",MIN("+rowstart.getCell(1).getReference()+":"
                    +rowend.getCell(cdsl).getReference()+","
                    +rowstart.getCell(cdsl+2).getReference()+":"
                    +rowend.getCell(cdsl*2+1).getReference()+","
                    +rowstart.getCell(2*cdsl+3).getReference()+":"
                    +sheet.getRow(i+18).getCell(3*cdsl+2).getReference()+"))");
        }

    }


    /**
     *
     * @param wb
     * @param sheet
     * @param count
     */
    public void setTunnelAndBrodgeTotal(XSSFWorkbook wb,XSSFSheet sheet, int count,int cdsl){
        FormulaEvaluator e = new XSSFFormulaEvaluator(wb);
        int c = 0;
        String bs1 = "", bs2 = "";
        if (cdsl == 2){
            c = 13;
            bs1 = "G";
            bs2 = "K";
        }else if (cdsl == 3){
            c = 17;
            bs1 = "I";
            bs2 = "N";
        }else if (cdsl ==4){
            c = 16;
            bs1 = "H";
            bs2 = "L";
        }else if (cdsl ==5){
            c = 19;
            bs1 = "I";
            bs2 = "N";
        }
        for(int i = 0; i < count; i++){
            sheet.getRow(i+6).createCell(c).setCellFormula("OFFSET($"+bs1+"$3,((ROW()-7)*33),0,1,1)");
            String name = e.evaluate(sheet.getRow(i+6).getCell(c)).getStringValue();
            sheet.getRow(i+6).getCell(c).setCellFormula(null);
            sheet.getRow(i+6).getCell(c).setCellValue(name);

            for(int j=0;j<6;j++){
                sheet.getRow(i+6).createCell(j+c+1).setCellFormula("OFFSET("+bs2+""+(28+j)+",((ROW()-7)*33),0,1,1)");//R=OFFSET(N28,((ROW()-7)*26),0,1,1)
                double value = e.evaluate(sheet.getRow(i+6).getCell(j+c+1)).getNumberValue();
                sheet.getRow(i+6).getCell(j+c+1).setCellFormula(null);
                if((j == 0 || j == 1) && value < 0.0001){
                    sheet.getRow(i+6).getCell(j+c+1).setCellValue("");
                }
                else{
                    sheet.getRow(i+6).getCell(j+c+1).setCellValue(value);
                }
            }
        }
        sheet.getRow(0).createCell(c+1).setCellFormula("MAX("+sheet.getRow(6).getCell(c+1).getReference()+":"
                +sheet.getRow(6+count).createCell(c+1).getReference()+")");
        double value = e.evaluate(sheet.getRow(3-3).getCell(c+1)).getNumberValue();
        sheet.getRow(0).getCell(c+1).setCellFormula(null);
        if(value < 0.0001){
            sheet.getRow(0).getCell(c+1).setCellValue("");
        }
        else{
            sheet.getRow(0).getCell(c+1).setCellValue(value);
        }

        sheet.getRow(0).createCell(c+2).setCellFormula("MIN("+sheet.getRow(6).getCell(c+2).getReference()+":"
                +sheet.getRow(6+count).createCell(c+2).getReference()+")");
        value = e.evaluate(sheet.getRow(0).getCell(c+2)).getNumberValue();
        sheet.getRow(0).getCell(c+2).setCellFormula(null);
        if(value < 0.0001){
            sheet.getRow(0).getCell(c+2).setCellValue("");
        }
        else{
            sheet.getRow(0).getCell(c+2).setCellValue(value);
        }

        sheet.getRow(0).createCell(c+5).setCellFormula("SUM("+sheet.getRow(6).getCell(c+5).getReference()+":"
                +sheet.getRow(6+count).createCell(c+5).getReference()+")");
        value = e.evaluate(sheet.getRow(3-3).getCell(c+5)).getNumberValue();
        sheet.getRow(0).getCell(c+5).setCellFormula(null);
        sheet.getRow(0).getCell(c+5).setCellValue(value);

        sheet.getRow(0).createCell(c+6).setCellFormula("SUM("+sheet.getRow(6).getCell(c+6).getReference()+":"
                +sheet.getRow(6+count).createCell(c+6).getReference()+")");
        value = e.evaluate(sheet.getRow(0).getCell(c+6)).getNumberValue();
        sheet.getRow(0).getCell(c+6).setCellFormula(null);
        sheet.getRow(0).getCell(c+6).setCellValue(value);

        sheet.getRow(0).createCell(c+7).setCellFormula(
                sheet.getRow(0).getCell(c+6).getReference()+"*100/"+ sheet.getRow(0).getCell(c+5).getReference());
    }

    /**
     *
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
     * @throws ParseException
     */
    private void DBtoExcel(String proname, String htd, List<Map<String, Object>> data, XSSFWorkbook wb, String sheetname, int cdsl, String sjz) throws ParseException {
        log.info("开始写入{}数据",sheetname);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        if (data!=null && !data.isEmpty()){
            createTable2(getNum2(data,cdsl),wb,sheetname,cdsl);
            XSSFSheet sheet = wb.getSheet(sheetname);
            String time = String.valueOf(data.get(0).get("createTime")) ;
            Date parse = simpleDateFormat.parse(time);
            String sj = outputDateFormat.format(parse);
            int a = 0;
            int b = 0;
            if (cdsl == 2){
                a = 10;
                b = 11;
            }else if (cdsl == 4){
                a = 11;
                b = 14;
            }else if (cdsl == 3 || cdsl == 5 ){
                a = 13;
                b = 14;
            }
            sheet.getRow(1).getCell(2).setCellValue(proname);
            sheet.getRow(1).getCell(a).setCellValue(htd);
            sheet.getRow(2).getCell(b).setCellValue(sj);

            String name = data.get(0).get("lxbs").toString();
            int index = 0;
            int tableNum = 0;
            fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
            int z = 0;
            if (cdsl == 2 || cdsl == 3){
                z = 100;

            }else if (cdsl == 4 || cdsl == 5){
                z = 73;
            }
            for (int i = 0; i < data.size(); i++) {
                if (name.equals(data.get(i).get("lxbs"))) {
                    //if(index == 100){
                    if (index % z == 0 && index != 0) {
                        tableNum++;
                        fillTitleCellData(sheet, tableNum, proname, htd, name, sj, sheetname, cdsl, sjz);
                        index = 0;

                    }
                    fillCommonCellData(sheet, tableNum, index, data.get(i), cdsl);
                    index++;

                } else {
                    name = data.get(i).get("lxbs").toString();
                    tableNum++;
                    index = 0;
                    fillTitleCellData(sheet, tableNum, proname, htd, name, sj, sheetname, cdsl, sjz);
                    fillCommonCellData(sheet, tableNum, index, data.get(i), cdsl);
                    index += 1;
                }
            }

        }
        log.info("{}数据写入完成",sheetname);

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
        String[] sfc = row.get("cz").toString().split(",");
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
        String fbgcname = "隧道路面";
        int a = 0,b = 0,c = 0;
        if (cdsl == 2){
            a = 10;
            b = 6;
            c = 11;
        }else if (cdsl == 4){
            a = 11;
            b = 7;
            c = 14;
        }else if (cdsl == 3 || cdsl == 5 ){
            a = 13;
            b = 8;
            c = 14;
        }
        sheet.getRow(tableNum * 33 + 1).getCell(2).setCellValue(proname);
        sheet.getRow(tableNum * 33 + 1).createCell(a-1).setCellType(CellType.STRING);
        sheet.getRow(tableNum * 33 + 1).createCell(a).setCellType(CellType.STRING);
        sheet.getRow(tableNum * 33 + 1).getCell(a-1).setCellValue("合同段：");
        sheet.getRow(tableNum * 33 + 1).getCell(a).setCellValue(htd);
        sheet.getRow(tableNum * 33 + 2).getCell(2).setCellValue("隧道工程");
        sheet.getRow(tableNum * 33 + 2).getCell(a).setCellValue(time);
        sheet.getRow(tableNum * 33 + 2).getCell(b).setCellValue(fbgcname+"("+name+")");
        sheet.getRow(tableNum * 33 + 25).getCell(c).setCellValue(Double.parseDouble(sjz));
    }

    /**
     *
     * @param num
     * @param wb
     * @param sheetname
     * @param cdsl
     */
    private void createTable2(int num, XSSFWorkbook wb, String sheetname, int cdsl) {
        int a  = 0;
        if (cdsl == 2){
            a = 11;
        }else if (cdsl == 3){
            a = 15;
        }else if (cdsl == 4){
            a = 14;
        }else if (cdsl == 5){
            a = 17;
        }
        int record = 0;
        record = num;
        for (int i = 1; i < record; i++) {
            RowCopy.copyRows(wb, sheetname, sheetname, 0, 32, i * 33);
        }
        if(record >= 1){
            wb.setPrintArea(wb.getSheetIndex(sheetname), 0, a, 0,(record) * 33 - 1);
        }
    }


    /**
     *
     * @param data
     * @param cdsl
     * @return
     */
    private int getNum2(List<Map<String, Object>> data,int cdsl) {
        int a = 0;
        if (cdsl == 2 || cdsl ==3){
            a = 100;
        }else if (cdsl == 4 || cdsl == 5){
            a = 73;
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
     *将相同幅的cz拼接
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
                if (map.get("cz") == null){
                    sfc = "-";
                }else {
                    sfc = map.get("cz").toString();
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
                map.put("qdzh", entry.getKey());
                map.put("cz", String.join(",", entry.getValue()));
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

        List<Map<String,Object>> lxlist = jjgFbgcSdgcZdhczMapper.selectlx(proname,htd);
        if (lxlist.size()>0){
            for (Map<String, Object> map : lxlist) {
                String zx = map.get("lxbs").toString();
                int num = jjgFbgcSdgcZdhczMapper.selectcdnum(proname,htd,zx);
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
     */
    private List<Map<String, Object>> lookdata(String proname, String htd, String zx, int cds) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat decf = new DecimalFormat("0.##");
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + "45隧道路面车辙-"+zx+".xlsx");
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
                    Map map = new HashMap();

                    if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                        slSheet.getRow(0).getCell(4*cds+6).setCellType(CellType.STRING);//总点数
                        slSheet.getRow(0).getCell(4*cds+7).setCellType(CellType.STRING);//合格点数

                        slSheet.getRow(5).getCell(cds*4+11).setCellType(CellType.STRING);
                        slSheet.getRow(5).getCell(cds*4+12).setCellType(CellType.STRING);

                        double zds = Double.valueOf(slSheet.getRow(0).getCell(cds*4+6).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(0).getCell(cds*4+7).getStringCellValue());
                        String zdsz1 = decf.format(zds);
                        String hgdsz1 = decf.format(hgds);
                        map.put("路面类型", wb.getSheetName(j));
                        map.put("总点数", zdsz1);
                        map.put("设计值", slSheet.getRow(25).getCell(cds*4+3).getStringCellValue());
                        map.put("合格点数", hgdsz1);
                        map.put("合格率", zds!=0 ? df.format(hgds/zds*100) : 0);
                        map.put("Max",slSheet.getRow(5).getCell(cds*4+11).getStringCellValue());
                        map.put("Min",slSheet.getRow(5).getCell(cds*4+12).getStringCellValue());
                    }
                    jgmap.add(map);
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
        List<Map<String,Object>> lxlist = jjgFbgcSdgcZdhczMapper.selectlx(proname,htd);
        int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcSdgcZdhczMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        return cds;
    }


    @Override
    public void exportcz(HttpServletResponse response, String cdsl) throws IOException {
        int cd = Integer.parseInt(cdsl);
        String fileName = "隧道车辙实测数据";
        String[][] sheetNames = {
                {"左幅一车道","左幅二车道","右幅一车道","右幅二车道"},
                {"左幅一车道","左幅二车道","左幅三车道","右幅一车道","右幅二车道","右幅三车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","左幅五车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道","右幅五车道"}
        };
        String[] sheetName = sheetNames[cd-2];
        ExcelUtil.writeExcelMultipleSheets(response, null, fileName, sheetName, new JjgFbgcSdgcZdhczVo());

    }

    @Override
    public void importcz(MultipartFile file, CommonInfoVo commonInfoVo) throws IOException {
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
                        .head(JjgFbgcSdgcZdhczVo.class)
                        .headRowNumber(1)
                        .registerReadListener(
                                new ExcelHandler<JjgFbgcSdgcZdhczVo>(JjgFbgcSdgcZdhczVo.class) {
                                    @Override
                                    public void handle(List<JjgFbgcSdgcZdhczVo> dataList) {
                                        for(JjgFbgcSdgcZdhczVo czVo: dataList)
                                        {
                                            JjgFbgcSdgcZdhcz cz = new JjgFbgcSdgcZdhcz();
                                            BeanUtils.copyProperties(czVo,cz);
                                            cz.setCreatetime(new Date());
                                            cz.setUsername(commonInfoVo.getUsername());
                                            cz.setProname(commonInfoVo.getProname());
                                            cz.setHtd(commonInfoVo.getHtd());
                                            /*cz.setQdzh(Double.parseDouble(czVo.getQdzh()));
                                            cz.setZdzh(Double.parseDouble(czVo.getZdzh()));*/
                                            cz.setCd(sheetName);
                                            if (sheetName.contains("一")){
                                                cz.setVal(1);
                                            }else if (sheetName.contains("二")){
                                                cz.setVal(2);
                                            }else if (sheetName.contains("三")){
                                                cz.setVal(3);
                                            }else if (sheetName.contains("四")){
                                                cz.setVal(4);
                                            }else if (sheetName.contains("五")){
                                                cz.setVal(5);
                                            }
                                            jjgFbgcSdgcZdhczMapper.insert(cz);
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
        List<Map<String, Object>> selectlx = jjgFbgcSdgcZdhczMapper.selectlx(proname, htd);
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
        String sdmc = StringUtils.substringBetween(value, "-", ".");
        int cds = jjgFbgcSdgcZdhczMapper.selectcdnum(proname,htd,sdmc);
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + value);
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
                    Map map = new HashMap();

                    if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                        slSheet.getRow(0).getCell(4*cds+6).setCellType(CellType.STRING);//总点数
                        slSheet.getRow(0).getCell(4*cds+7).setCellType(CellType.STRING);//合格点数

                        slSheet.getRow(5).getCell(cds*4+11).setCellType(CellType.STRING);
                        slSheet.getRow(5).getCell(cds*4+12).setCellType(CellType.STRING);

                        double zds = Double.valueOf(slSheet.getRow(0).getCell(cds*4+6).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(0).getCell(cds*4+7).getStringCellValue());
                        String zdsz1 = decf.format(zds);
                        String hgdsz1 = decf.format(hgds);
                        map.put("检测项目", sdmc);
                        map.put("路面类型", wb.getSheetName(j));
                        map.put("总点数", zdsz1);
                        map.put("设计值", slSheet.getRow(25).getCell(cds*4+3).getStringCellValue());
                        map.put("合格点数", hgdsz1);
                        map.put("合格率", zds!=0 ? df.format(hgds/zds*100) : 0);
                        map.put("Max",slSheet.getRow(5).getCell(cds*4+11).getStringCellValue());
                        map.put("Min",slSheet.getRow(5).getCell(cds*4+12).getStringCellValue());
                    }
                    jgmap.add(map);
                }
            }
            return jgmap;
        }
    }


}
