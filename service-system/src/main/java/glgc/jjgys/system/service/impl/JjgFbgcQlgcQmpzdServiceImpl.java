package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgFbgcQlgcQmpzd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.qlgc.JjgFbgcQlgcQmpzdVo;
import glgc.jjgys.model.system.SysRole;
import glgc.jjgys.model.system.SysUser;
import glgc.jjgys.model.system.SysUserRole;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcQlgcQmpzdMapper;
import glgc.jjgys.system.mapper.SysUserRoleMapper;
import glgc.jjgys.system.service.JjgFbgcQlgcQmpzdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.service.SysRoleService;
import glgc.jjgys.system.service.SysUserService;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import glgc.jjgys.system.utils.RowCopy;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-03-20
 */
@Service
public class JjgFbgcQlgcQmpzdServiceImpl extends ServiceImpl<JjgFbgcQlgcQmpzdMapper, JjgFbgcQlgcQmpzd> implements JjgFbgcQlgcQmpzdService {

    @Autowired
    private JjgFbgcQlgcQmpzdMapper jjgFbgcQlgcQmpzdMapper;

    @Value(value = "${jjgys.path.filepath}")
    private String filepath;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public void generateJdb(CommonInfoVo commonInfoVo) throws IOException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        String fbgc = commonInfoVo.getFbgc();
        String username = commonInfoVo.getUsername();
        List<Map<String,Object>> qlmclist = jjgFbgcQlgcQmpzdMapper.selectqlmc(proname,htd,fbgc);
        if (qlmclist.size()>0){
            for (Map<String, Object> m : qlmclist)
            {
                for (String k : m.keySet()){
                    String qlmc = m.get(k).toString();
                    DBtoExcelql(proname,htd,fbgc,qlmc,qlmclist.size(),username);
                }
            }
        }

    }

    /**
     *
     * @param proname
     * @param htd
     * @param fbgc
     * @param qlmc
     * @param qlsize
     * @param username
     * @throws IOException
     */
    private void DBtoExcelql(String proname, String htd, String fbgc, String qlmc, int qlsize, String username) throws IOException {
        XSSFWorkbook wb = null;
        QueryWrapper<JjgFbgcQlgcQmpzd> wrapper=new QueryWrapper<>();
        wrapper.like("proname",proname);
        wrapper.like("htd",htd);
        wrapper.like("fbgc",fbgc);
        wrapper.like("qm",qlmc);
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
            wrapper.eq("username", username);
        }
        wrapper.orderByAsc("zh");
        List<JjgFbgcQlgcQmpzd> data = jjgFbgcQlgcQmpzdMapper.selectList(wrapper);
        //鉴定表要存放的路径
        File f = new File(filepath+File.separator+proname+File.separator+htd+File.separator+"34桥面平整度3米直尺法-"+qlmc+".xlsx");
        if (data == null || data.size()==0){
            return;
        }else {
            File fdir = new File(filepath + File.separator + proname + File.separator + htd);
            if (!fdir.exists()) {
                //创建文件根目录
                fdir.mkdirs();
            }
            try {
                File directory = new File("service-system/src/main/resources/static");
                String reportPath = directory.getCanonicalPath();
                String path = reportPath + File.separator + "平整度3米直尺法.xlsx";
                Files.copy(Paths.get(path), new FileOutputStream(f));
                FileInputStream out = new FileInputStream(f);
                wb = new XSSFWorkbook(out);
                createTable(gettableNum(qlsize),wb);
                if(DBtoExcel(data,wb,qlmc)){
                    calculateTheSheet(wb.getSheet("平整度"));

                    for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                        JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
                    }

                    JjgFbgcCommonUtils.deleteEmptySheets(wb);
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
     *
     * @param sheet
     */
    private void calculateTheSheet(XSSFSheet sheet) {
        XSSFRow row = null;
        boolean flag = false;
        String name = "";
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        //System.out.println("sheet.getPhysicalNumberOfRows() = "+ sheet.getPhysicalNumberOfRows());
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if(flag && !"".equals(row.getCell(0).toString()) && name.equals(row.getCell(0).toString())){
                rowend = sheet.getRow(i+8);
                calculateConclution(sheet, i);
                i+=8;
            }
            if(flag && !"".equals(row.getCell(0).toString()) && !name.equals(row.getCell(0).toString())){
                rowstart.createCell(6).setCellFormula("COUNT("
                        +rowstart.getCell(3).getReference()+":"
                        +rowend.getCell(3).getReference()+")");//G=COUNT(D6:D59)总点数
                rowstart.createCell(7).setCellFormula("COUNTIF("
                        +rowstart.getCell(4).getReference()+":"
                        +rowend.getCell(4).getReference()+",\"√\")");//H=COUNTIF(E6:E59,"√")合格点数
                rowstart.createCell(8).setCellFormula(
                        rowstart.getCell(7).getReference()+"/"
                                +rowstart.getCell(6).getReference()+"*100");//合格率
                rowstart = sheet.getRow(i);
                name = rowstart.getCell(0).toString();
                rowend = sheet.getRow(i+8);
                calculateConclution(sheet, i);
                i+=8;
            }
            if ("桩号".equals(row.getCell(0).toString())) {
                flag = true;
                i += 1;
                rowstart = sheet.getRow(i + 1);
                rowend = sheet.getRow(i+9);
                name = rowstart.getCell(0).toString();
                calculateConclution(sheet, i+1);
                i+=9;
            }
        }
        rowend = sheet.getRow(sheet.getPhysicalNumberOfRows()-1);
        rowstart.createCell(6).setCellFormula("COUNT("
                +rowstart.getCell(3).getReference()+":"
                +rowend.getCell(3).getReference()+")");//G=COUNT(D6:D59)总点数
        rowstart.createCell(7).setCellFormula("COUNTIF("
                +rowstart.getCell(4).getReference()+":"
                +rowend.getCell(4).getReference()+",\"√\")");//H=COUNTIF(E6:E59,"√")合格点数
        rowstart.createCell(8).setCellFormula(
                rowstart.getCell(7).getReference()+"/"
                        +rowstart.getCell(6).getReference()+"*100");//合格率

        sheet.getRow(2).getCell(7).setCellFormula("COUNT("
                +sheet.getRow(5).getCell(3).getReference()+":"
                +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).getCell(3).getReference()+")");//G=COUNT(D6:D59)总点数
        sheet.getRow(3).getCell(7).setCellFormula("COUNTIF("
                +sheet.getRow(5).getCell(4).getReference()+":"
                +sheet.getRow(sheet.getPhysicalNumberOfRows()-1).getCell(4).getReference()+",\"√\")");//H=COUNTIF(E6:E59,"√")合格点数
        sheet.getRow(4).getCell(7).setCellFormula(
                sheet.getRow(3).getCell(7).getReference()+"/"
                        +sheet.getRow(2).getCell(7).getReference()+"*100");//合格率

    }

    /**
     *
     * @param sheet
     * @param index
     */
    private void calculateConclution(XSSFSheet sheet, int index) {
        for(int i=0; i<9;i++){
            sheet.getRow(index+i).getCell(4).setCellFormula("IF(AND("
                    +sheet.getRow(index+i).getCell(3).getReference()+"<="
                    +sheet.getRow(index+i).getCell(2).getReference()+","
                    +sheet.getRow(index+i).getCell(2).getReference()+">0),\"√\",\"\")");//E6=IF(AND(D6<=C6,C6>0),"√","")
            sheet.getRow(index+i).getCell(5).setCellFormula("IF(AND("
                    +sheet.getRow(index+i).getCell(3).getReference()+">"
                    +sheet.getRow(index+i).getCell(2).getReference()+","
                    +sheet.getRow(index+i).getCell(2).getReference()+">0),\"×\",\"\")");//F6=IF(AND(D6>C6,C6>0),"×","")
        }
    }

    /**
     * 写入数据
     * @param data
     * @param wb
     * @param qlmc
     * @return
     */
    private boolean DBtoExcel(List<JjgFbgcQlgcQmpzd> data, XSSFWorkbook wb,String qlmc) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        String lmlx = data.get(0).getLmlx();
        XSSFSheet sheet = wb.getSheet("平整度");
        if (lmlx.contains("水泥")){
            sheet.getRow(0).getCell(0).setCellValue("混凝土水泥路面平整度质量鉴定表");
        }else if (lmlx.contains("沥青")){
            sheet.getRow(0).getCell(0).setCellValue("沥青路面平整度质量鉴定表");
        }

        sheet.getRow(1).getCell(1).setCellValue(data.get(0).getProname());
        sheet.getRow(1).getCell(4).setCellValue(data.get(0).getHtd());
        sheet.getRow(2).getCell(1).setCellValue(qlmc);
        sheet.getRow(2).getCell(4).setCellValue(simpleDateFormat.format(data.get(0).getJcsj()));
        int index = 5;
        for(int i=0;i<data.size();i++){
            sheet.getRow(index).getCell(0).setCellValue(data.get(i).getQm()+data.get(i).getZh());
            sheet.getRow(index).getCell(1).setCellValue(data.get(i).getWz());
            sheet.getRow(index).getCell(2).setCellValue(Integer.parseInt(data.get(i).getSjz()));
            sheet.getRow(index+1).getCell(2).setCellValue(Integer.parseInt(data.get(i+1).getSjz()));
            sheet.getRow(index+2).getCell(2).setCellValue(Integer.parseInt(data.get(i+2).getSjz()));
            sheet.getRow(index).getCell(3).setCellValue(Double.parseDouble(data.get(i).getScz()));
            sheet.getRow(index+1).getCell(3).setCellValue(Double.parseDouble(data.get(i+1).getScz()));
            sheet.getRow(index+2).getCell(3).setCellValue(Double.parseDouble(data.get(i+2).getScz()));

            sheet.getRow(index+3).getCell(1).setCellValue(data.get(i+3).getWz());
            sheet.getRow(index+3).getCell(2).setCellValue(Integer.parseInt(data.get(i+3).getSjz()));
            sheet.getRow(index+1+3).getCell(2).setCellValue(Integer.parseInt(data.get(i+1+3).getSjz()));
            sheet.getRow(index+2+3).getCell(2).setCellValue(Integer.parseInt(data.get(i+2+3).getSjz()));
            sheet.getRow(index+3).getCell(3).setCellValue(Double.parseDouble(data.get(i+3).getScz()));
            sheet.getRow(index+1+3).getCell(3).setCellValue(Double.parseDouble(data.get(i+1+3).getScz()));
            sheet.getRow(index+2+3).getCell(3).setCellValue(Double.parseDouble(data.get(i+2+3).getScz()));

            sheet.getRow(index+6).getCell(1).setCellValue(data.get(i+6).getWz());
            sheet.getRow(index+6).getCell(2).setCellValue(Integer.parseInt(data.get(i+6).getSjz()));
            sheet.getRow(index+1+6).getCell(2).setCellValue(Integer.parseInt(data.get(i+1+6).getSjz()));
            sheet.getRow(index+2+6).getCell(2).setCellValue(Integer.parseInt(data.get(i+2+6).getSjz()));
            sheet.getRow(index+6).getCell(3).setCellValue(Double.parseDouble(data.get(i+6).getScz()));
            sheet.getRow(index+1+6).getCell(3).setCellValue(Double.parseDouble(data.get(i+1+6).getScz()));
            sheet.getRow(index+2+6).getCell(3).setCellValue(Double.parseDouble(data.get(i+2+6).getScz()));

            index += 9;
            i += 8;
        }
        return true;
    }

    /**
     *
     * @param tableNum
     * @param wb
     */
    private void createTable(int tableNum, XSSFWorkbook wb) {
        for(int i = 1; i < tableNum; i++){
            RowCopy.copyRows(wb, "平整度", "平整度", 5, 31, i*32);
        }
        if(tableNum >= 1){
            wb.setPrintArea(wb.getSheetIndex("平整度"), 0, 5, 0, tableNum*27+4);
        }
    }

    /**
     *
     * @param size
     * @return
     */
    private int gettableNum(int size) {
        return size%3 == 0 ? size/3 : size/3+1;
    }

    @Override
    public List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        //String fbgc = commonInfoVo.getFbgc();
        String title = "平整度质量鉴定表";
        String sheetname = "平整度";

        List<Map<String, Object>> mapList = new ArrayList<>();

        List<Map<String,Object>> qlmclist = jjgFbgcQlgcQmpzdMapper.selectqlmc2(proname,htd);
        if (qlmclist.size()>0){
            for (Map<String, Object> m : qlmclist) {
                for (String k : m.keySet()){
                    String qlmc = m.get(k).toString();
                    Map<String, Object> lookqljdb = lookqljdb(proname, htd, qlmc, sheetname,title);
                    mapList.add(lookqljdb);
                }
            }
            return mapList;
        }else {
            return null;
        }
    }

    /**
     *
     * @param proname
     * @param htd
     * @param qlmc
     * @param sheetname
     * @param title
     * @return
     * @throws IOException
     */
    private Map<String, Object> lookqljdb(String proname, String htd, String qlmc, String sheetname, String title) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat decf = new DecimalFormat("0.##");
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + "34桥面平整度3米直尺法-"+qlmc+".xlsx");
        if (!f.exists()) {
            return null;
        } else {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));
            XSSFSheet slSheet = wb.getSheet(sheetname);
            XSSFCell bt = slSheet.getRow(0).getCell(0);//标题
            XSSFCell xmname = slSheet.getRow(1).getCell(1);//项目名
            XSSFCell htdname = slSheet.getRow(1).getCell(4);//合同段名
            Map<String, Object> jgmap = new HashMap<>();
            if (proname.equals(xmname.toString()) && title.equals(bt.toString()) && htd.equals(htdname.toString())) {

                slSheet.getRow(2).getCell(7).setCellType(CellType.STRING);//总点数
                slSheet.getRow(3).getCell(7).setCellType(CellType.STRING);//合格点数
                slSheet.getRow(4).getCell(7).setCellType(CellType.STRING);//合格率
                slSheet.getRow(5).getCell(2).setCellType(CellType.STRING);//合格率
                double zds = Double.valueOf(slSheet.getRow(2).getCell(7).getStringCellValue());
                double hgds = Double.valueOf(slSheet.getRow(3).getCell(7).getStringCellValue());
                double hgl = Double.valueOf(slSheet.getRow(4).getCell(7).getStringCellValue());
                double sjz = Double.valueOf(slSheet.getRow(5).getCell(2).getStringCellValue());
                String zdsz1 = decf.format(zds);
                String hgdsz1 = decf.format(hgds);
                String hglz1 = df.format(hgl);
                jgmap.put("检测项目", qlmc);
                jgmap.put("检测点数", zdsz1);
                jgmap.put("合格点数", hgdsz1);
                jgmap.put("合格率", hglz1);
                jgmap.put("yxpc", sjz);
            }
            return jgmap;
        }
    }

    @Override
    public void exportQmpzd(HttpServletResponse response) {
        String fileName = "09桥面平整度三米直尺法实测数据";
        String sheetName = "实测数据";
        ExcelUtil.writeExcelWithSheets(response, null, fileName, sheetName, new JjgFbgcQlgcQmpzdVo()).finish();

    }

    @Override
    public void importQmpzd(MultipartFile file, CommonInfoVo commonInfoVo) {
        try {
            EasyExcel.read(file.getInputStream())
                    .sheet(0)
                    .head(JjgFbgcQlgcQmpzdVo.class)
                    .headRowNumber(1)
                    .registerReadListener(
                            new ExcelHandler<JjgFbgcQlgcQmpzdVo>(JjgFbgcQlgcQmpzdVo.class) {
                                @Override
                                public void handle(List<JjgFbgcQlgcQmpzdVo> dataList) {
                                    int rowNumber=2;
                                    for(JjgFbgcQlgcQmpzdVo qlgcQmpzdVo: dataList)
                                    {
                                        if (StringUtils.isEmpty(qlgcQmpzdVo.getZh())) {
                                            throw new JjgysException(20001, "第"+rowNumber+"行的数据中，桩号为空，请修改后重新上传");
                                        }
                                        if (StringUtils.isEmpty(qlgcQmpzdVo.getQm())) {
                                            throw new JjgysException(20001, "第"+rowNumber+"行的数据中，桥名为空，请修改后重新上传");
                                        }
                                        if (StringUtils.isEmpty(qlgcQmpzdVo.getWz())) {
                                            throw new JjgysException(20001, "第"+rowNumber+"行的数据中，位置为空，请修改后重新上传");
                                        }
                                        if (!StringUtils.isNumeric(qlgcQmpzdVo.getSjz()) || StringUtils.isEmpty(qlgcQmpzdVo.getSjz())) {
                                            throw new JjgysException(20001, "第"+rowNumber+"行的数据中，设计值有误，请修改后重新上传");
                                        }
                                        if (!StringUtils.isNumeric(qlgcQmpzdVo.getScz()) || StringUtils.isEmpty(qlgcQmpzdVo.getScz())) {
                                            throw new JjgysException(20001, "第"+rowNumber+"行的数据中，实测值有误，请修改后重新上传");
                                        }
                                        JjgFbgcQlgcQmpzd qlgcQmpzd = new JjgFbgcQlgcQmpzd();
                                        BeanUtils.copyProperties(qlgcQmpzdVo,qlgcQmpzd);
                                        qlgcQmpzd.setCreatetime(new Date());
                                        qlgcQmpzd.setUsername(commonInfoVo.getUsername());
                                        qlgcQmpzd.setProname(commonInfoVo.getProname());
                                        qlgcQmpzd.setHtd(commonInfoVo.getHtd());
                                        qlgcQmpzd.setFbgc(commonInfoVo.getFbgc());
                                        jjgFbgcQlgcQmpzdMapper.insert(qlgcQmpzd);
                                        rowNumber++;
                                    }
                                }
                            }
                    ).doRead();
        } catch (IOException e) {
            throw new JjgysException(20001,"解析excel出错，请传入正确格式的excel");
        }

    }

    @Override
    public List<Map<String, Object>> selectqlmc(String proname, String htd, String fbgc) {
        List<Map<String,Object>> qlmclist = jjgFbgcQlgcQmpzdMapper.selectqlmc(proname,htd,fbgc);
        return qlmclist;
    }

    @Override
    public List<Map<String, Object>> lookjg(CommonInfoVo commonInfoVo, String value) throws IOException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat decf = new DecimalFormat("0.##");
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + value);
        if (!f.exists()) {
            return null;
        } else {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));
            XSSFSheet slSheet = wb.getSheet("平整度");
            XSSFCell bt = slSheet.getRow(0).getCell(0);//标题
            XSSFCell xmname = slSheet.getRow(1).getCell(1);//项目名
            XSSFCell htdname = slSheet.getRow(1).getCell(4);//合同段名
            Map<String, Object> jgmap = new HashMap<>();
            List list =new ArrayList();
            if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {

                slSheet.getRow(2).getCell(7).setCellType(CellType.STRING);//总点数
                slSheet.getRow(3).getCell(7).setCellType(CellType.STRING);//合格点数
                slSheet.getRow(4).getCell(7).setCellType(CellType.STRING);//合格率
                slSheet.getRow(5).getCell(2).setCellType(CellType.STRING);//合格率
                double zds = Double.valueOf(slSheet.getRow(2).getCell(7).getStringCellValue());
                double hgds = Double.valueOf(slSheet.getRow(3).getCell(7).getStringCellValue());
                double hgl = Double.valueOf(slSheet.getRow(4).getCell(7).getStringCellValue());
                double sjz = Double.valueOf(slSheet.getRow(5).getCell(2).getStringCellValue());
                String zdsz1 = decf.format(zds);
                String hgdsz1 = decf.format(hgds);
                String hglz1 = df.format(hgl);
                jgmap.put("检测项目", StringUtils.substringBetween(value, "-", "."));
                jgmap.put("检测点数", zdsz1);
                jgmap.put("合格点数", hgdsz1);
                jgmap.put("合格率", hglz1);
                jgmap.put("yxpc", sjz);
            }
            list.add(jgmap);
            return list;
        }

    }

    @Override
    public int selectnum(String proname, String htd) {
        int selectnum = jjgFbgcQlgcQmpzdMapper.selectnum(proname, htd);
        return selectnum;
    }

    @Override
    public int selectnumname(String proname) {
        int selectnum = jjgFbgcQlgcQmpzdMapper.selectnumname(proname);
        return selectnum;
    }
}
