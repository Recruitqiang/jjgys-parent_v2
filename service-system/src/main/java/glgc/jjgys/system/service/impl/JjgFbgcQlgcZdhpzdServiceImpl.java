package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgFbgcQlgcZdhpzd;
import glgc.jjgys.model.project.JjgLqsQl;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.qlgc.JjgFbgcQlgcZdhpzdVo;
import glgc.jjgys.model.system.SysRole;
import glgc.jjgys.model.system.SysUser;
import glgc.jjgys.model.system.SysUserRole;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcQlgcZdhpzdMapper;
import glgc.jjgys.system.mapper.SysUserRoleMapper;
import glgc.jjgys.system.service.JjgFbgcQlgcZdhpzdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.service.JjgLqsQlService;
import glgc.jjgys.system.service.SysRoleService;
import glgc.jjgys.system.service.SysUserService;
import glgc.jjgys.system.utils.RowCopy;
import org.apache.commons.lang3.StringUtils;
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
 * @since 2023-10-15
 */
@Service
public class JjgFbgcQlgcZdhpzdServiceImpl extends ServiceImpl<JjgFbgcQlgcZdhpzdMapper, JjgFbgcQlgcZdhpzd> implements JjgFbgcQlgcZdhpzdService {

    @Autowired
    private JjgFbgcQlgcZdhpzdMapper jjgFbgcQlgcZdhpzdMapper;

    @Value(value = "${jjgys.path.filepath}")
    private String filepath;

    @Override
    public void importpzd(MultipartFile file, CommonInfoVo commonInfoVo) throws IOException {
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
                        .head(JjgFbgcQlgcZdhpzdVo.class)
                        .headRowNumber(1)
                        .registerReadListener(
                                new ExcelHandler<JjgFbgcQlgcZdhpzdVo>(JjgFbgcQlgcZdhpzdVo.class) {
                                    @Override
                                    public void handle(List<JjgFbgcQlgcZdhpzdVo> dataList) {
                                        for(JjgFbgcQlgcZdhpzdVo pzdVo: dataList)
                                        {
                                            JjgFbgcQlgcZdhpzd pzd = new JjgFbgcQlgcZdhpzd();
                                            BeanUtils.copyProperties(pzdVo,pzd);
                                            pzd.setCreatetime(new Date());
                                            pzd.setUsername(commonInfoVo.getUsername());
                                            pzd.setProname(commonInfoVo.getProname());
                                            pzd.setHtd(commonInfoVo.getHtd());
                                            pzd.setQdzh(pzdVo.getQdzh());
                                            pzd.setZdzh(pzdVo.getZdzh());
                                            if (sheetName.contains("一")){
                                                pzd.setVal(1);
                                            }else if (sheetName.contains("二")){
                                                pzd.setVal(2);
                                            }else if (sheetName.contains("三")){
                                                pzd.setVal(3);
                                            }else if (sheetName.contains("四")){
                                                pzd.setVal(4);
                                            }else if (sheetName.contains("五")){
                                                pzd.setVal(5);
                                            }
                                            pzd.setCd(sheetName);
                                            jjgFbgcQlgcZdhpzdMapper.insert(pzd);
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
    public void exportpzd(HttpServletResponse response, String cd) throws IOException {
        int cds = Integer.parseInt(cd);
        String fileName = "平整度实测数据";
        String[][] sheetNames = {
                {"左幅一车道","左幅二车道","右幅一车道","右幅二车道"},
                {"左幅一车道","左幅二车道","左幅三车道","右幅一车道","右幅二车道","右幅三车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道"},
                {"左幅一车道","左幅二车道","左幅三车道","左幅四车道","左幅五车道","右幅一车道","右幅二车道","右幅三车道","右幅四车道","右幅五车道"}
        };
        String[] sheetName = sheetNames[cds-2];
        ExcelUtil.writeExcelMultipleSheets(response, null, fileName, sheetName, new JjgFbgcQlgcZdhpzdVo());

    }

    @Override
    public List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) throws IOException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        List<Map<String, Object>> mapList = new ArrayList<>();

        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhpzdMapper.selectlx(proname,htd);
        if (lxlist.size()>0){
            for (Map<String, Object> map : lxlist) {
                String zx = map.get("qlname").toString();
                int num = jjgFbgcQlgcZdhpzdMapper.selectcdnum(proname,htd,zx);
                List<Map<String, Object>> looksdjdb = lookdata(proname, htd, zx,num);
                mapList.addAll(looksdjdb);
            }
            return mapList;
        }else {
            return new ArrayList<>();
        }
    }

    /**
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
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + "33桥面平整度-"+zx+".xlsx");
        if (!f.exists()) {
            return new ArrayList<>();
        } else {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));
            List<Map<String, Object>> jgmap = new ArrayList<>();
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                if (!wb.isSheetHidden(wb.getSheetIndex(wb.getSheetAt(j)))) {
                    XSSFSheet slSheet = wb.getSheetAt(j);

                    XSSFCell xmname = slSheet.getRow(1).getCell(5);//项目名
                    XSSFCell htdname = slSheet.getRow(1).getCell(cds*4+4);//合同段名


                    if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                        slSheet.getRow(5).getCell(cds*4+9).setCellType(CellType.STRING);//总点数
                        slSheet.getRow(5).getCell(cds*4+10).setCellType(CellType.STRING);//合格点数

                        slSheet.getRow(5).getCell(cds*4+11).setCellType(CellType.STRING);
                        slSheet.getRow(5).getCell(cds*4+12).setCellType(CellType.STRING);
                        slSheet.getRow(3).getCell(5).setCellType(CellType.STRING);
                        double zds = Double.valueOf(slSheet.getRow(5).getCell(cds*4+9).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(5).getCell(cds*4+10).getStringCellValue());
                        String zdsz1 = decf.format(zds);
                        String hgdsz1 = decf.format(hgds);
                        Map map = new HashMap();
                        map.put("路面类型", wb.getSheetName(j));
                        map.put("总点数", zdsz1);
                        map.put("设计值", slSheet.getRow(3).getCell(5).getStringCellValue());
                        map.put("合格点数", hgdsz1);
                        map.put("合格率", zds!=0 ? df.format(hgds/zds*100) : 0);
                        map.put("Max",slSheet.getRow(5).getCell(cds*4+11).getStringCellValue());
                        map.put("Min",slSheet.getRow(5).getCell(cds*4+12).getStringCellValue());
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
        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhpzdMapper.selectlx(proname,htd);
        int cds = 0;
        int maxNum = 2; // 添加一个变量用来保存最大值
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhpzdMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        return cds;
    }


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
        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhpzdMapper.selectlx(proname,htd);
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhpzdMapper.selectcdnum(proname,htd,zx);
            int cds = 0;
            if (num == 1){
                cds = 2;
            }else {
                cds=num;
            }
            handlezxData(proname,htd,zx,cds,commonInfoVo.getSjz(),username);
        }
        /*int cds = 0;
        int maxNum = 2;
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("lxbs").toString();
            int num = jjgFbgcQlgcZdhpzdMapper.selectcdnum(proname,htd,zx);
            if (num > maxNum) { // 如果当前num大于maxNum，则更新maxNum的值
                maxNum = num;
            }
        }
        cds = maxNum;
        handlezxData(proname,htd,cds,commonInfoVo.getSjz());*/


    }

    /**
     * @param proname
     * @param htd
     * @param zx
     * @param cdsl
     * @param sjz
     * @param username
     */
    private void handlezxData(String proname, String htd, String zx, int cdsl, String sjz, String username) throws IOException, ParseException {
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

        List<Map<String,Object>> datazf = new ArrayList<>();
        List<Map<String,Object>> datayf = new ArrayList<>();
        if (rolecode.equals("YH")){
            datazf = jjgFbgcQlgcZdhpzdMapper.selectzfListyh(proname,htd,zx,result,username);
            datayf = jjgFbgcQlgcZdhpzdMapper.selectyfListyh(proname,htd,zx,result,username);

        }else {
            datazf = jjgFbgcQlgcZdhpzdMapper.selectzfList(proname,htd,zx,result);
            datayf = jjgFbgcQlgcZdhpzdMapper.selectyfList(proname,htd,zx,result);

        }


        List<Map<String, Object>> lmzfList = montageIRI(datazf);
        Collections.sort(lmzfList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("qdzh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("qdzh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });
        List<Map<String, Object>> lmyfList = montageIRI(datayf);
        Collections.sort(lmyfList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("qdzh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("qdzh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });

        double zdzh = Double.parseDouble(lmzfList.get(0).get("qdzh").toString());
        double finzdzh = Double.parseDouble(lmzfList.get(lmzfList.size()-1).get("qdzh").toString());
        List<Map<String, Object>> lmzf = decrementNumberByStep(zdzh,finzdzh,lmzfList,cdsl);

        double yzdzh = Double.parseDouble(lmyfList.get(0).get("qdzh").toString());
        double yfinzdzh = Double.parseDouble(lmyfList.get(lmyfList.size()-1).get("qdzh").toString());
        List<Map<String, Object>> lmyf = decrementNumberByStep(yzdzh,yfinzdzh,lmyfList,cdsl);
        Collections.sort(lmzf, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("qdzh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("qdzh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });
        Collections.sort(lmyf, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                // 名字相同时按照 qdzh 排序
                Double qdzh1 = Double.parseDouble(o1.get("qdzh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("qdzh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });
        writeExcelData(proname,htd,lmzf,lmyf,cdsl,sjz,zx);
    }

    @Autowired
    private JjgLqsQlService jjgLqsQlService;

    /**
     *
     * @param proname
     * @param htd
     * @param lmzfList
     * @param lmyfList
     * @param cdsl
     * @param sjz
     * @param zx
     * @throws IOException
     */
    private void writeExcelData(String proname, String htd, List<Map<String, Object>> lmzfList, List<Map<String, Object>> lmyfList, int cdsl, String sjz, String zx) throws IOException, ParseException {
        XSSFWorkbook wb = null;
        String fname="33桥面平整度-"+zx+".xlsx";
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

            if (cdsl == 5){
                filename = "平整度-5车道.xlsx";
            }else if (cdsl == 4){
                filename = "平整度-4车道.xlsx";
            }else if (cdsl == 3){
                filename = "平整度-3车道.xlsx";
            }else if (cdsl == 2){
                filename = "平整度-2车道.xlsx";
            }

            String path = reportPath + File.separator + filename;
            Files.copy(Paths.get(path), new FileOutputStream(f));
            FileInputStream out = new FileInputStream(f);
            wb = new XSSFWorkbook(out);

            lmzfList.addAll(lmyfList);
            if (lmzfList.size()>0 && !lmzfList.isEmpty()){
                List<Map<String,Object>> addList = addMissingData(lmzfList,cdsl);
                List<Map<String, Object>> mapslist = mergedList(addList,cdsl);


                String lxbs = mapslist.get(0).get("lxbs").toString();
                QueryWrapper<JjgLqsQl> wrapper = new QueryWrapper<>();
                wrapper.eq("name",lxbs);
                List<JjgLqsQl> list = jjgLqsQlService.list(wrapper);
                String sheetmame = "";
                if (list.get(0).getPzlx().contains("水泥")){
                    sheetmame = "混凝土桥";
                }else {
                    sheetmame = "沥青桥";
                }

                DBtoExcel(proname,htd,addList,wb,sheetmame,cdsl,sjz,zx);
            }

            String[] arr = {"混凝土收费站","混凝土匝道隧道","沥青匝道隧道","混凝土匝道桥","沥青匝道桥","沥青匝道","混凝土匝道","混凝土隧道","沥青隧道","混凝土桥","沥青桥","混凝土路面","沥青路面"};
            //String[] arr = {"混凝土隧道","沥青隧道","混凝土桥","沥青桥","混凝土路面","沥青路面"};
            for (int i = 0; i < arr.length; i++) {
                if (shouldBeCalculate(wb.getSheet(arr[i]))) {
                    calculateAsphaltPavementSheet(wb,wb.getSheet(arr[i]),cdsl);
                }else {
                    wb.setSheetHidden(wb.getSheetIndex(arr[i]),true);
                    //wb.removeSheetAt(wb.getSheetIndex(arr[i]));
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
     * @param dataLists
     * @param cds
     * @return
     */
    private List<Map<String, Object>> mergedList(List<Map<String, Object>> dataLists,int cds) {
        //处理拼接的iri
        //int cdsl = cds * 2;
        String iris = "-";
        StringBuilder iriBuilder = new StringBuilder();
        for (int i = 0; i < cds; i++) {
            if (i == cds - 1) {
                iriBuilder.append("-");
            } else {
                iriBuilder.append("-,");
            }
        }
        iris = iriBuilder.toString();

        Map<String, List<Map<String, Object>>> groupedData =
                dataLists.stream()
                        .collect(Collectors.groupingBy(
                                item -> item.get("name") + "-" + item.get("qdzh") + "-" + item.get("zdzh")
                        ));
        List<Map<String, Object>> toBeRemoved = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedData.entrySet()) {
            String groupName = entry.getKey(); // 获取分组名字
            List<Map<String, Object>> groupData = entry.getValue(); // 获取分组数据

            if (groupData.size() > 1) {
                Map<String, Object> firstItem = groupData.get(0);
                Map<String, Object> secondItem = groupData.get(1);
                if (firstItem.get("cd").equals("左幅")) {
                    // 将第二条数据的iri拼接在第一条数据的iri后面
                    String iri = firstItem.get("iri").toString() + "," + secondItem.get("iri").toString();
                    firstItem.put("iri", iri);
                } else {
                    // 将第二条数据的iri拼接在第一条数据的iri前面
                    String iri = secondItem.get("iri").toString() + "," + firstItem.get("iri").toString();
                    firstItem.put("iri", iri);
                }
                // 将secondItem条数据删除
                toBeRemoved.add(secondItem);

            } else if (groupData.size() == 1) {
                Map<String, Object> item = groupData.get(0);
                if (item.get("cd").equals("左幅")) {
                    // 在iri后面拼接逗号
                    String iri = item.get("iri").toString() +","+ iris;
                    item.put("iri", iri);
                } else {
                    // 在iri前面拼接逗号
                    String iri = iris +","+ item.get("iri").toString();
                    item.put("iri", iri);
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
                Double qdzh1 = Double.parseDouble(o1.get("qdzh").toString());
                Double qdzh2 = Double.parseDouble(o2.get("qdzh").toString());
                return qdzh1.compareTo(qdzh2);
            }
        });


        return dataLists;
    }

    /**
     * 沥青路面
     * @param wb
     * @param sheet
     */
    private void calculateAsphaltPavementSheet(XSSFWorkbook wb, XSSFSheet sheet,int cdsl) {
        XSSFRow row = null;
        boolean flag = false;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        XSSFRow rowtotal = null;
        String name = "";
        FormulaEvaluator e = new XSSFFormulaEvaluator(wb);
        sheet.setColumnHidden(4,true);
        for (int i = 0; i <= sheet.getPhysicalNumberOfRows()-7; i++) {
            row = sheet.getRow(i);
            //如果当前行 row 为空行或第一列为空，则跳过本次循环，处理下一行数据
            if (row == null) {
                continue;
            }
            if (!"".equals(row.getCell(0).toString()) && row.getCell(0).toString().contains("质量鉴定表") && flag) {
                flag = false;
            }
            //如果当前行 row 的第一列不为空且与上一次读取到的不同，则说明当前行 row 是一个新的路段数据开始行，记录下路段数据的起始行 rowstart
            if (!"".equals(row.getCell(0).toString()) && !name.equals(row.getCell(0).toString()) && flag) {
                rowstart = sheet.getRow(i+1);
                name = rowstart.getCell(0).toString();
                rowend = sheet.getRow(getCellEndRow(sheet, rowstart.getRowNum(), 0));
                //rowstart.getRowNum() + 3作为起始行，总点数
                //获取连续的数据行 rowstart 到路段数据结束行 rowend。
                calculateTotalData(sheet, rowstart.getRowNum() + 3, rowstart, rowend, 2,e,cdsl);
            }
            if ("桩号".equals(row.getCell(0).toString())) {
                rowtotal = sheet.getRow(i + 1);
                i += 2;
                rowstart = sheet.getRow(i + 1);
                name = rowstart.getCell(0).toString();
                rowend = sheet.getRow(getCellEndRow(sheet, rowstart.getRowNum(), 0));
                calculateTotalData(sheet, rowstart.getRowNum() + 3, rowstart, rowend, 2,e,cdsl);
                flag = true;
            }
        }
        setExtraWholeTotalData(sheet, sheet.getRow(5),e,cdsl);

    }

    /**
     *
     * @param sheet
     * @param rowtotal
     * @param e
     */
    private void setExtraWholeTotalData(XSSFSheet sheet, XSSFRow rowtotal, FormulaEvaluator e,int cdsl) {
        /**
         * 2c 19  cdsl*4+11
         * 3c 21
         * 4c 25
         * 5c 29
         */
        rowtotal.createCell(cdsl*4+9).setCellFormula(
                "SUM("
                        + sheet.getRow(rowtotal.getRowNum() + 1).createCell(cdsl*4+9)
                        .getReference()
                        + ":"
                        + sheet.getRow(sheet.getPhysicalNumberOfRows() - 1)
                        .createCell(cdsl*4+9).getReference() + ")");
        double value = e.evaluate(rowtotal.getCell(cdsl*4+9)).getNumberValue();
        rowtotal.getCell(cdsl*4+9).setCellFormula(null);
        rowtotal.getCell(cdsl*4+9).setCellValue(value);

        rowtotal.createCell(cdsl*4+10).setCellFormula(
                "SUM("
                        + sheet.getRow(rowtotal.getRowNum() + 1).createCell(cdsl*4+10)
                        .getReference()
                        + ":"
                        + sheet.getRow(sheet.getPhysicalNumberOfRows() - 1)
                        .createCell(cdsl*4+10).getReference() + ")");
        value = e.evaluate(rowtotal.getCell(cdsl*4+10)).getNumberValue();
        rowtotal.getCell(cdsl*4+10).setCellFormula(null);
        rowtotal.getCell(cdsl*4+10).setCellValue(value);

        rowtotal.createCell(cdsl*4+11).setCellFormula(
                rowtotal.getCell(cdsl*4+10).getReference() + "/"
                        + rowtotal.getCell(cdsl*4+9).getReference() + "*100");
        value = e.evaluate(rowtotal.getCell(cdsl*4+11)).getNumberValue();
        rowtotal.getCell(cdsl*4+11).setCellFormula(null);
        rowtotal.getCell(cdsl*4+11).setCellValue(value);

        rowtotal.createCell(cdsl*4+12).setCellFormula(
                "MIN("
                        + sheet.getRow(rowtotal.getRowNum() + 1).createCell(cdsl*4+12)
                        .getReference()
                        + ":"
                        + sheet.getRow(sheet.getPhysicalNumberOfRows() - 1)
                        .createCell(cdsl*4+12).getReference() + ")");
        value = e.evaluate(rowtotal.getCell(cdsl*4+12)).getNumberValue();
        rowtotal.getCell(cdsl*4+12).setCellFormula(null);
        rowtotal.getCell(cdsl*4+12).setCellValue(value);

        rowtotal.createCell(cdsl*4+11).setCellFormula(
                "MAX("
                        + sheet.getRow(rowtotal.getRowNum() + 1).createCell(cdsl*4+11)
                        .getReference()
                        + ":"
                        + sheet.getRow(sheet.getPhysicalNumberOfRows() - 1)
                        .createCell(cdsl*4+11).getReference() + ")");
        value = e.evaluate(rowtotal.getCell(cdsl*4+11)).getNumberValue();
        rowtotal.getCell(cdsl*4+11).setCellFormula(null);
        rowtotal.getCell(cdsl*4+11).setCellValue(value);

    }

    /**
     * 计算每一个桩号内的总结数据
     */
    private void calculateTotalData(XSSFSheet sheet, int rownum, XSSFRow rowstart, XSSFRow rowend, int num, FormulaEvaluator e,int cdsl) {

        if (num == 1) {
            if (rowend.getRowNum() <= getCellEndRow(sheet, rowstart.getRowNum(), 5)) {
                fillTotalData(sheet, rownum, rowstart, rowend, 0, cdsl*4+7, false,e,cdsl);
            } else {
                if (rowstart.getCell(5).getCellType() == Cell.CELL_TYPE_FORMULA || rowstart.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    fillTotalData(sheet, rownum, rowstart, rowend, 0, cdsl*4+7, true,e,cdsl);
                } else {
                    int record = getCellEndRow(sheet, rowstart.getRowNum(), 5);
                    boolean flag = true;
                    while (rowend.getRowNum() > record) {
                        if (sheet.getRow(record + 1).getCell(5).getCellType() == Cell.CELL_TYPE_FORMULA || sheet.getRow(record + 1).getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            fillTotalData(sheet, rownum, rowstart, rowend, 0, cdsl*4+7, true,e,cdsl);
                            flag = false;
                            break;
                        }
                        record = getCellEndRow(sheet, record + 1, 5);
                    }
                    if (flag) {
                        fillTotalData(sheet, rownum, rowstart, rowend, 0, cdsl*4+7, false,e,cdsl);
                    }
                }
            }
        } else if (num == 2) {
            if (rowend.getRowNum() <= getCellEndRow(sheet, rowstart.getRowNum(), 5)) {
                fillTotalData(sheet, rownum, rowstart, rowend, 0, cdsl*4+7, true,e,cdsl);
            } else {
                if (rowstart.getCell(5).getCellType() == Cell.CELL_TYPE_FORMULA || rowstart.getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    fillTotalData(sheet, rownum, rowstart, rowend, 0, cdsl*4+7, true,e,cdsl);
                } else {
                    int record = getCellEndRow(sheet, rowstart.getRowNum(), 5);
                    boolean flag = true;
                    while (rowend.getRowNum() > record) {
                        if (sheet.getRow(record + 1).getCell(5).getCellType() == Cell.CELL_TYPE_FORMULA || sheet.getRow(record + 1).getCell(5).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            fillTotalData(sheet, rownum, rowstart, rowend, 0, cdsl*4+7, true,e,cdsl);
                            flag = false;
                            break;
                        }
                        record = getCellEndRow(sheet, record + 1, 5);
                    }
                    if (flag) {
                        fillTotalData(sheet, rownum, rowstart, rowend, 0, cdsl*4+7, true,e,cdsl);
                    }
                }
            }
            if (rowend.getRowNum() <= getCellEndRow(sheet, rowstart.getRowNum(), 9)) {
                fillTotalData(sheet, rownum, rowstart, rowend, 1, cdsl*4+8, true,e,cdsl);
            } else {
                if (rowstart.getCell(9).getCellType() == Cell.CELL_TYPE_FORMULA || rowstart.getCell(9).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    fillTotalData(sheet, rownum, rowstart, rowend, 1, cdsl*4+8, true,e,cdsl);
                } else {
                    int record = getCellEndRow(sheet, rowstart.getRowNum(), 9);
                    boolean flag = true;
                    while (rowend.getRowNum() > record) {
                        if (sheet.getRow(record + 1).getCell(9).getCellType() == Cell.CELL_TYPE_FORMULA || sheet.getRow(record + 1).getCell(9).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            fillTotalData(sheet, rownum, rowstart, rowend, 1, cdsl*4+8, true,e,cdsl);
                            flag = false;
                            break;
                        }
                        record = getCellEndRow(sheet, record + 1, 9);
                    }
                    if (flag) {
                        fillTotalData(sheet, rownum, rowstart, rowend, 1, cdsl*4+8, true,e,cdsl);
                    }
                }
            }
        }
    }

    /**
     * 根据给定的单元格起始行号，得到合并单元格的最后一行行号 如果给定的初始行号不是合并单元格，那么函数返回初始行号
     * @param sheet
     * @param cellstartrow
     * @param cellstartcol
     * @return
     */
    private int getCellEndRow(XSSFSheet sheet, int cellstartrow, int cellstartcol) {
        int sheetmergerCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetmergerCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            if (cellstartrow >= ca.getFirstRow() && cellstartrow <= ca.getLastRow() && cellstartcol >= ca.getFirstColumn() && cellstartcol <= ca.getLastColumn()) {
                return ca.getLastRow();
            }
        }
        return cellstartrow;


    }

    /**
     * 将结果数据填到表格的右部
     * @param sheet
     * @param rownum
     * @param rowstart
     * @param rowend
     * @param left_right
     * @param fillcolum
     * @param hasdata
     */
    private void fillTotalData(XSSFSheet sheet, int rownum, XSSFRow rowstart, XSSFRow rowend, int left_right, int fillcolum, boolean hasdata,FormulaEvaluator e,int cdsl) {
        /**
         * 5c z:5-14 y:15-24
         * 4c z:5-12 y:13-20
         * 3c z:5-10 y:11-16
         * 2c z:5-8  y:9-12
         */
        if (left_right == 0){
            int a = 5, b =0;
            if (cdsl == 5){
                b = 14;
            }else if (cdsl == 4){
                b = 12;
            }else if (cdsl == 3){
                b = 10;
            }else if (cdsl == 2){
                b = 8;
            }
            if (hasdata) {
                sheet.getRow(rownum)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                "COUNT("
                                + rowstart.getCell(a + left_right * a * 2)
                                .getReference()
                                + ":"
                                + rowend.getCell(b + left_right * a * 2)
                                .getReference() + ")"+",\"-\")");// 总点数P=COUNT(F8:I17)

                sheet.getRow(rownum + 1)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                sheet.getRow(rownum).getCell(fillcolum)
                                        .getReference()
                                + "-COUNTIF("
                                + rowstart.getCell(a + left_right * a * 2)
                                .getReference()
                                + ":"
                                + rowend.getCell(b + left_right * a * 2)
                                .getReference() + ",\">\"&$F$4)"+",\"-\")");// 合格数P=P11-COUNTIF(F8:I17,">"&$F$4)

                sheet.getRow(rownum + 2)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                "AVERAGE("
                                + rowstart.getCell(a + left_right * a * 2)
                                .getReference()
                                + ":"
                                + rowend.getCell(b + left_right * a * 2)
                                .getReference() + ")"+",\"-\")");// 平均值P=AVERAGE(F8:I17)
                sheet.getRow(rownum + 3)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                "STDEV("
                                + rowstart.getCell(a + left_right * a * 2)
                                .getReference()
                                + ":"
                                + rowend.getCell(b + left_right * a * 2)
                                .getReference() + ")"+",\"-\")");// 均方差P=STDEV(F8:I17)

                sheet.getRow(rownum + 4)
                        .getCell(fillcolum)
                        .setCellFormula("IF("+sheet.getRow(rownum)
                                .getCell(fillcolum).getReference()+"=0,\"-\","+
                                "MIN("
                                + rowstart.getCell(a + left_right * a * 2)
                                .getReference()
                                + ":"
                                + rowend.getCell(b + left_right * a * 2)
                                .getReference() + ")"+")");// 最小值P=MIN(F8:I17)=IF(MIN(P92,Q92)=0,"",MIN(P92,Q92))

                sheet.getRow(rownum + 5)
                        .getCell(fillcolum)
                        .setCellFormula("IF("+sheet.getRow(rownum)
                                .getCell(fillcolum).getReference()+"=0,\"-\","+
                                "MAX("
                                + rowstart.getCell(a + left_right * a * 2)
                                .getReference()
                                + ":"
                                + rowend.getCell(b + left_right * a * 2)
                                .getReference() + ")"+")");// 最大值P=MAX(F8:I17)

                sheet.getRow(rownum + 6)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                sheet.getRow(rownum + 1).getCell(fillcolum)
                                        .getReference()
                                + "*100/"
                                + sheet.getRow(rownum)
                                .getCell(fillcolum).getReference()+",\"-\")");// 合格率P=P12*100/P11
                if(left_right == 1){
                    fillExtraTotalData(sheet, rownum - 1, hasdata,e,cdsl);
                }

            } else {
                sheet.getRow(rownum).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 1).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 2).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 3).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 4).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 5).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 6).getCell(fillcolum).setCellValue("-");
                if(left_right == 1){
                    fillExtraTotalData(sheet, rownum - 1, true,e,cdsl);
                }
            }
        }else if (left_right == 1){
            /**
             * 5c z:5-14 y:15-24
             * 4c z:5-12 y:13-20
             * 3c z:5-10 y:11-16
             * 2c z:5-8  y:9-12
             */
            int a = 5, b =0;
            if (cdsl == 5){
                b = 14;
            }else if (cdsl == 4){
                b = 12;
            }else if (cdsl == 3){
                b = 10;
            }else if (cdsl == 2){
                b = 8;
            }
            if (hasdata) {
                sheet.getRow(rownum)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                "COUNT("
                                + rowstart.getCell(cdsl * 2+5)
                                .getReference()
                                + ":"
                                + rowend.getCell(4*cdsl+4)
                                .getReference() + ")"+",\"-\")");// 总点数P=COUNT(F8:I17)

                sheet.getRow(rownum + 1)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                sheet.getRow(rownum).getCell(fillcolum)
                                        .getReference()
                                + "-COUNTIF("
                                + rowstart.getCell(cdsl * 2+5)
                                .getReference()
                                + ":"
                                + rowend.getCell(4*cdsl+4)
                                .getReference() + ",\">\"&$F$4)"+",\"-\")");// 合格数P=P11-COUNTIF(F8:I17,">"&$F$4)

                sheet.getRow(rownum + 2)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                "AVERAGE("
                                + rowstart.getCell(cdsl * 2+5)
                                .getReference()
                                + ":"
                                + rowend.getCell(4*cdsl+4)
                                .getReference() + ")"+",\"-\")");// 平均值P=AVERAGE(F8:I17)
                sheet.getRow(rownum + 3)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                "STDEV("
                                + rowstart.getCell(cdsl * 2+5)
                                .getReference()
                                + ":"
                                + rowend.getCell(4*cdsl+4)
                                .getReference() + ")"+",\"-\")");// 均方差P=STDEV(F8:I17)

                sheet.getRow(rownum + 4)
                        .getCell(fillcolum)
                        .setCellFormula("IF("+sheet.getRow(rownum)
                                .getCell(fillcolum).getReference()+"=0,\"-\","+
                                "MIN("
                                + rowstart.getCell(cdsl * 2+5)
                                .getReference()
                                + ":"
                                + rowend.getCell(4*cdsl+4)
                                .getReference() + ")"+")");// 最小值P=MIN(F8:I17)=IF(MIN(P92,Q92)=0,"",MIN(P92,Q92))

                sheet.getRow(rownum + 5)
                        .getCell(fillcolum)
                        .setCellFormula("IF("+sheet.getRow(rownum)
                                .getCell(fillcolum).getReference()+"=0,\"-\","+
                                "MAX("
                                + rowstart.getCell(cdsl * 2+5)
                                .getReference()
                                + ":"
                                + rowend.getCell(4*cdsl+4)
                                .getReference() + ")"+")");// 最大值P=MAX(F8:I17)

                sheet.getRow(rownum + 6)
                        .getCell(fillcolum)
                        .setCellFormula("IFERROR("+
                                sheet.getRow(rownum + 1).getCell(fillcolum)
                                        .getReference()
                                + "*100/"
                                + sheet.getRow(rownum)
                                .getCell(fillcolum).getReference()+",\"-\")");// 合格率P=P12*100/P11
                if(left_right == 1){
                    fillExtraTotalData(sheet, rownum - 1, hasdata,e,cdsl);
                }

            } else {
                sheet.getRow(rownum).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 1).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 2).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 3).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 4).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 5).getCell(fillcolum).setCellValue("-");
                sheet.getRow(rownum + 6).getCell(fillcolum).setCellValue("-");
                if(left_right == 1){
                    fillExtraTotalData(sheet, rownum - 1, true,e,cdsl);
                }
            }
        }

    }

    /**
     * 在表格右边外面的位置，填写统计数据
     * @param sheet
     * @param rownum
     * @param hasdata
     * @param e
     */
    private void fillExtraTotalData(XSSFSheet sheet, int rownum, boolean hasdata, FormulaEvaluator e,int cdsl) {
        if (hasdata) {
            sheet.getRow(rownum)
                    .createCell(cdsl*4+9)
                    .setCellFormula(
                            "IF("
                                    + sheet.getRow(rownum + 1).getCell(cdsl*4+7)
                                    .getReference()
                                    + "=\"-\",0,"
                                    + sheet.getRow(rownum + 1).getCell(cdsl*4+7)
                                    .getReference()
                                    + ")+IF("
                                    + sheet.getRow(rownum + 1).getCell(cdsl*4+8)
                                    .getReference()
                                    + "=\"-\",0,"
                                    + sheet.getRow(rownum + 1).getCell(cdsl*4+8)
                                    .getReference() + ")");
            double value = e.evaluate(sheet.getRow(rownum).getCell(cdsl*4+9)).getNumberValue();
            sheet.getRow(rownum).getCell(cdsl*4+9).setCellFormula(null);
            sheet.getRow(rownum).getCell(cdsl*4+9).setCellValue(value);
            // "IF("+sheet.getRow(rownum+1).getCell(15).getReference()+"=\"-\""+"+"
            // +sheet.getRow(rownum+1).getCell(16).getReference());//总点数R=P31+Q31=IF(P323="-",0,P323)+IF(Q323="-",0,Q323)

            sheet.getRow(rownum)
                    .createCell(cdsl*4+10)
                    .setCellFormula(
                            "IF("
                                    + sheet.getRow(rownum + 2).getCell(cdsl*4+7)
                                    .getReference()
                                    + "=\"-\",0,"
                                    + sheet.getRow(rownum + 2).getCell(cdsl*4+7)
                                    .getReference()
                                    + ")+IF("
                                    + sheet.getRow(rownum + 2).getCell(cdsl*4+8)
                                    .getReference()
                                    + "=\"-\",0,"
                                    + sheet.getRow(rownum + 2).getCell(cdsl*4+8)
                                    .getReference() + ")");
            value = e.evaluate(sheet.getRow(rownum).getCell(cdsl*4+10)).getNumberValue();
            sheet.getRow(rownum).getCell(cdsl*4+10).setCellFormula(null);
            sheet.getRow(rownum).getCell(cdsl*4+10).setCellValue(value);

            sheet.getRow(rownum)
                    .createCell(cdsl*4+11)
                    .setCellFormula(
                            "IF("+ sheet.getRow(rownum).getCell(cdsl*4+9).getReference()+"=0,0,"+
                                    sheet.getRow(rownum).getCell(cdsl*4+10).getReference()
                                    + "/"
                                    + sheet.getRow(rownum).getCell(cdsl*4+9)
                                    .getReference() + "*100)");// 合格率T=S30/R30*100

            sheet.getRow(rownum)
                    .createCell(cdsl*4+12)
                    .setCellFormula(
                            "IF(MIN("
                                    + sheet.getRow(rownum + 5).getCell(cdsl*4+7)
                                    .getReference()
                                    + ","
                                    + sheet.getRow(rownum + 5).getCell(cdsl*4+8)
                                    .getReference()
                                    + ")=0,\"\",MIN("
                                    + sheet.getRow(rownum + 5).getCell(cdsl*4+7)
                                    .getReference()
                                    + ","
                                    + sheet.getRow(rownum + 5).getCell(cdsl*4+8)
                                    .getReference() + "))");
            value = e.evaluate(sheet.getRow(rownum).getCell(cdsl*4+12)).getNumberValue();
            sheet.getRow(rownum).getCell(cdsl*4+12).setCellFormula(null);
            if(value < 0.0001){
                sheet.getRow(rownum).getCell(cdsl*4+12).setCellValue("");
            }
            else{
                sheet.getRow(rownum).getCell(cdsl*4+12).setCellValue(value);
            }

            sheet.getRow(rownum)
                    .createCell(cdsl*4+11)
                    .setCellFormula(
                            "IF(MAX("
                                    + sheet.getRow(rownum + 6).getCell(cdsl*4+7)
                                    .getReference()
                                    + ","
                                    + sheet.getRow(rownum + 6).getCell(cdsl*4+8)
                                    .getReference()
                                    + ")=0,\"\",MAX("
                                    + sheet.getRow(rownum + 6).getCell(cdsl*4+7)
                                    .getReference()
                                    + ","
                                    + sheet.getRow(rownum + 6).getCell(cdsl*4+8)
                                    .getReference() + "))");
            value = e.evaluate(sheet.getRow(rownum).getCell(cdsl*4+11)).getNumberValue();
            sheet.getRow(rownum).getCell(cdsl*4+11).setCellFormula(null);
            if(value < 0.0001){
                sheet.getRow(rownum).getCell(cdsl*4+11).setCellValue("");
            }
            else{
                sheet.getRow(rownum).getCell(cdsl*4+11).setCellValue(value);
            }
        } else {
            sheet.getRow(rownum).createCell(cdsl*4+9).setCellValue("-");
            sheet.getRow(rownum).createCell(cdsl*4+10).setCellValue("-");
            sheet.getRow(rownum).createCell(cdsl*4+11).setCellValue("-");
            sheet.getRow(rownum).createCell(cdsl*4+12).setCellValue("-");
            sheet.getRow(rownum).createCell(cdsl*4+11).setCellValue("-");
        }

    }

    /**
     *
     * @param sheet
     * @return
     */
    private boolean shouldBeCalculate(XSSFSheet sheet) {
        sheet.getRow(7).getCell(0).setCellType(CellType.STRING);
        if(sheet.getRow(7).getCell(0)==null || "".equals(sheet.getRow(7).getCell(0).getStringCellValue())){
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
     * @param zx
     * @throws ParseException
     */
    private void DBtoExcel(String proname, String htd, List<Map<String, Object>> data, XSSFWorkbook wb, String sheetname, int cdsl, String sjz, String zx) throws ParseException {
        int b = 0;
        if (cdsl == 2){
            b=40;
        }else if (cdsl == 3){
            b=20;
        }else if (cdsl == 4 || cdsl ==5){
            b=30;
        }
        if (data!=null && !data.isEmpty()){
            //List<Map<String, Object>> maps = mergedList(data,cdsl);
            createTable(getNum(data,cdsl),wb,sheetname,cdsl);
            writesdqlzyf(wb,data,proname, htd,sheetname,cdsl,sjz,b,zx);

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
     * @param zx
     * @throws ParseException
     */
    private void writesdqlzyf(XSSFWorkbook wb, List<Map<String, Object>> data, String proname, String htd, String sheetname, int cdsl, String sjz, int b, String zx) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat  = new SimpleDateFormat("yyyy.MM.dd");
        XSSFSheet sheet = wb.getSheet(sheetname);
        String time = String.valueOf(data.get(0).get("createTime")) ;
        Date parse = simpleDateFormat.parse(time);
        String sj = outputDateFormat.format(parse);

        sheet.getRow(1).getCell(5).setCellValue(proname);
        sheet.getRow(2).getCell(5).setCellValue("隧道工程");
        sheet.getRow(3).getCell(5).setCellValue(Double.parseDouble(sjz));

        sheet.getRow(3).getCell(cdsl*4+4).setCellValue(sj);
        sheet.getRow(1).getCell(cdsl*4+4).setCellValue(htd);
        String name = data.get(0).get("lxbs").toString();
        String fbgcname = "隧道路面";
        sheet.getRow(2).getCell(cdsl*4+4).setCellValue(fbgcname+"("+name+")");
        int index = 0;
        int tableNum = 0;

        for(int i =0; i < data.size(); i++){
            if (name.equals(data.get(i).get("lxbs"))){
                if(index > b){
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

    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param row
     * @param cdsl
     */
    private void fillCommonCellData(XSSFSheet sheet, int tableNum, int index, Map<String, Object> row, int cdsl) {
        int a = 0;
        int b = 0;
        if (cdsl == 2){
            a = 47;
            b=40;
        }else if (cdsl == 3){
            a = 27;
            b=20;
        }else if (cdsl == 4 || cdsl ==5){
            a = 37;
            b=30;
        }
        double n = Double.valueOf(row.get("zdzh").toString()) / 1000;
        int m = (int) n;
        sheet.getRow(tableNum * a + 7 + index % b).getCell(0).setCellValue(m);
        sheet.getRow(tableNum * a + 7 + index % b).getCell(4).setCellValue(Double.valueOf(row.get("zdzh").toString()));
        String[] sfc = row.get("iri").toString().split(",");
        if (!sfc[0].isEmpty()) {
            for (int i = 0 ; i < sfc.length ; i++) {
                if (!sfc[i].equals("-")){
                    sheet.getRow(tableNum * a + 7 + index % b).getCell(5+(i*2)).setCellValue(Double.parseDouble(sfc[i]));
                }else {
                    sheet.getRow(tableNum * a + 7 + index % b).getCell(5+(i*2)).setCellValue(sfc[i]);
                }

            }
        }

    }


    /**
     *
     * @param sheet
     * @param lmdata
     * @param proname
     * @param htd
     * @param name
     * @param sj
     * @param sheetname
     * @param cdsl
     * @param sjz
     */
    private void writezyf(XSSFSheet sheet, List<Map<String, Object>> lmdata, String proname, String htd, String name, String sj, String sheetname, int cdsl, String sjz) {

        int index = 0;
        int tableNum = 0;
        int a = 0;
        int b = 0;
        if (cdsl == 2){
            a = 47;
            b=40;
        }else if (cdsl == 3){
            a = 27;
            b=20;
        }else if (cdsl == 4 || cdsl ==5){
            a = 37;
            b=30;
        }
        for (Map<String, Object> lm : lmdata) {
            if(index > (b-1)){
                tableNum ++;
                fillTitleCellData(sheet, tableNum, proname, htd, name,sj,sheetname,cdsl,sjz);
                index = 0;
            }
            if (!lm.get("iri").toString().equals("") && !lm.get("iri").toString().isEmpty()) {

                double n = Double.valueOf(lm.get("zdzh").toString()) / 1000;
                int m = (int) n;
                sheet.getRow(tableNum * a + 7 + index % b).getCell(0).setCellValue(m);
                sheet.getRow(tableNum * a + 7 + index % b).getCell(4).setCellValue(Double.valueOf(lm.get("zdzh").toString()));
                String[] sfc = lm.get("iri").toString().split(",");
                for (int i = 0 ; i < sfc.length ; i++) {
                    if (lm.get("cd").equals("左幅")){
                        if (!sfc[i].equals("-")) {
                            sheet.getRow(tableNum * a + 7 + index % b).getCell(5+(i*2)).setCellValue(Double.parseDouble(sfc[i]));
                        }else {
                            sheet.getRow(tableNum * a + 7 + index % b).getCell(5+(i*2)).setCellValue(sfc[i]);
                        }
                    }else {
                        if (!sfc[i].equals("-")){
                            sheet.getRow(tableNum * a + 7 + index % b).getCell(2*cdsl+5+(i*2)).setCellValue(Double.parseDouble(sfc[i]));
                        }else {
                            sheet.getRow(tableNum * a + 7 + index % b).getCell(2*cdsl+5+(i*2)).setCellValue(sfc[i]);
                        }

                    }
                }
            }
            index++;
        }
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
        if (cdsl == 2){
            a = 47;
        }else if (cdsl == 3){
            a = 27;
        }else if (cdsl == 4 || cdsl ==5){
            a = 37;
        }
        String fbgcname = "桥面系";
        sheet.getRow(tableNum * a + 1).getCell(5).setCellValue(proname);
        sheet.getRow(tableNum * a + 1).getCell(cdsl*4+4).setCellValue(htd);
        sheet.getRow(tableNum * a + 2).getCell(5).setCellValue("路面工程");
        sheet.getRow(tableNum * a + 3).getCell(cdsl*4+4).setCellValue(sj);
        sheet.getRow(tableNum * a + 2).getCell(cdsl*4+4).setCellValue(fbgcname+"("+name+")");
        sheet.getRow(tableNum * a + 3).getCell(5).setCellValue(Double.parseDouble(sjz));
    }

    /**
     * 创建页
     * @param tableNum
     * @param wb
     * @param sheetname
     * @param cdsl
     */
    private void createTable(int tableNum, XSSFWorkbook wb, String sheetname, int cdsl) {
        int endrow = 0;
        int printrow = 0;
        if (cdsl == 2){
            endrow = 46;
            printrow=47;
        }else if (cdsl == 3){
            endrow = 26;
            printrow=27;
        }else if (cdsl == 4 || cdsl ==5){
            endrow = 36;
            printrow=37;
        }
        int record = 0;
        record = tableNum;
        for (int i = 1; i < record; i++) {
            RowCopy.copyRows(wb, sheetname, sheetname, 0, endrow, i* (endrow+1));
        }
        if(record >= 1){
            wb.setPrintArea(wb.getSheetIndex(sheetname), 0, cdsl*4+8, 0,(record) * printrow-1);
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
            a = 40;

        }else if (cdsl == 3){
            a = 20;

        }else if (cdsl == 4 || cdsl == 5){
            a = 30;
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
     * @param dataList
     * @return
     */
    private List<Map<String, Object>> addMissingData(List<Map<String, Object>> dataList,int cdsl) {

        List<Map<String, Object>> zf = new ArrayList<>();
        List<Map<String, Object>> yf = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : dataList) {
            if (stringObjectMap.get("cd").toString().contains("左幅")){
                zf.add(stringObjectMap);
            }
            if (stringObjectMap.get("cd").toString().contains("右幅")){
                yf.add(stringObjectMap);
            }
        }

        List<Map<String, Object>> groupaddz = groupadd(zf,cdsl);
        List<Map<String, Object>> groupaddy = groupadd(yf,cdsl);

        groupaddz.addAll(groupaddy);
        return groupaddz;

    }

    /**
     *
     * @param dataList
     * @param cdsl
     * @return
     */
    private List<Map<String, Object>> groupadd(List<Map<String, Object>> dataList,int cdsl) {
        // 按照 name 进行分组
        Map<String, List<Map<String, Object>>> groupMap = new HashMap<>();
        for (Map<String, Object> data : dataList) {
            String name = (String) data.get("lxbs");
            if (groupMap.containsKey(name)) {
                groupMap.get(name).add(data);
            } else {
                List<Map<String, Object>> list = new ArrayList<>();
                list.add(data);
                groupMap.put(name, list);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        // 遍历 groupMap 中的所有键值对
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupMap.entrySet()) {
            String key = entry.getKey();
            List<Map<String, Object>> valueList = entry.getValue();
            int size = valueList.size();
            double zdzh = Double.parseDouble(valueList.get(0).get("qdzh").toString());
            double finzdzh = Double.parseDouble(valueList.get(size-1).get("qdzh").toString());
            List<Map<String, Object>> maps = decrementNumberByStep(zdzh,finzdzh,valueList,cdsl);
            result.addAll(maps);
        }
        return result;

    }


    /**
     * 添加桩号
     * @param zdzh
     * @param finzdzh
     * @param mapList
     * @return
     */
    private List<Map<String, Object>> decrementNumberByStep(double zdzh,double finzdzh, List<Map<String, Object>> mapList,int cds) {
        //int cdsl = cds * 2;
        String iri = "-";
        StringBuilder iriBuilder = new StringBuilder();
        for (int i = 0; i < cds; i++) {
            if (i == cds - 1) {
                iriBuilder.append("-");
            } else {
                iriBuilder.append("-,");
            }

        }
        iri = iriBuilder.toString();

        List<Map<String, Object>> zhlist = new ArrayList<>();
        List<Map<String, Object>> finzdzhlist = new ArrayList<>();
        int c = (int)zdzh;
        int a = c/1000;
        int b = a*1000;//49000

        for (int i = c; i>b;i-=100){
            Map map = new HashMap();
            map.put("cd",mapList.get(0).get("cd"));
            map.put("createTime",mapList.get(0).get("createTime"));
            map.put("zdbs",mapList.get(0).get("zdbs"));
            map.put("iri",iri);
           /* map.put("name",mapList.get(0).get("name"));
            map.put("pzlx",mapList.get(0).get("pzlx"));*/
            map.put("qdzh",Double.parseDouble(String.valueOf(i-100)));
            map.put("zdzh",Double.parseDouble(String.valueOf(i)));
            zhlist.add(map);
        }

        int m = (int)finzdzh;
        int n = m/1000+1;
        int v = n*1000;

        for (int i = m+100; i<v;i+=100){
            Map map = new HashMap();
            map.put("cd",mapList.get(0).get("cd"));
            map.put("createTime",mapList.get(0).get("createTime"));
            map.put("zdbs",mapList.get(0).get("zdbs"));
            map.put("iri",iri);
            /*map.put("name",mapList.get(0).get("name"));
            map.put("pzlx",mapList.get(0).get("pzlx"));*/
            map.put("zdzh",Double.parseDouble(String.valueOf(i+100)));
            map.put("qdzh",Double.parseDouble(String.valueOf(i)));
            finzdzhlist.add(map);
        }

        mapList.addAll(zhlist);
        mapList.addAll(finzdzhlist);

        Collections.sort(mapList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                String name1 = o1.get("zdzh").toString();
                String name2 = o2.get("zdzh").toString();
                return name1.compareTo(name2);
            }
        });
        return mapList;

    }

    /**
     *将相同幅的IRI拼接
     * @param list
     * @return
     */
    private static List<Map<String, Object>> montageIRI(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()){
            return new ArrayList<>();
        }else {
            Map<String, List<String>> resultMapz = new TreeMap<>();
            for (Map<String, Object> map : list) {
                String qdzh = map.get("qdzh").toString();
                //String ziri = map.get("ziri").toString();
                //String yiri = map.get("yiri").toString();
                String ziri = "";
                //String yiri = "";
                if (map.get("ziri") == null){
                    ziri = "-";
                }else {
                    ziri = map.get("ziri").toString();
                }
                /*if (map.get("yiri") == null){
                    yiri = "-";
                }else {
                    yiri = map.get("yiri").toString();
                }*/
                if (resultMapz.containsKey(qdzh)) {
                    resultMapz.get(qdzh).add(ziri);
                    //resultMapz.get(qdzh).add(yiri);
                } else {
                    List<String> sfcList = new ArrayList<>();
                    sfcList.add(ziri);
                    //sfcList.add(yiri);
                    resultMapz.put(qdzh, sfcList);
                }
            }

            List<Map<String, Object>> resultList = new LinkedList<>();
            for (Map.Entry<String, List<String>> entry : resultMapz.entrySet()) {
                Map<String, Object> map = new TreeMap<>();
                map.put("qdzh", entry.getKey());
                map.put("iri", String.join(",", entry.getValue()));
                for (Map<String, Object> item : list) {
                    if (item.get("qdzh").toString().equals(entry.getKey())) {
                        map.put("qdzh", item.get("qdzh"));
                        map.put("zdzh", item.get("zdzh"));
                        map.put("pzlx", item.get("pzlx"));
                        map.put("name", item.get("name"));
                        map.put("cd", item.get("cd").toString().substring(0,2));
                        map.put("createTime", item.get("createTime"));
                        break;
                    }
                }
                resultList.add(map);
            }
            return resultList;
        }
    }


    @Override
    public List<Map<String, Object>> selectlx(String proname, String htd) {
        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhpzdMapper.selectlx(proname,htd);
        return lxlist;
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
        int cds = jjgFbgcQlgcZdhpzdMapper.selectcdnum(proname,htd,qlmc);
        File f = new File(filepath + File.separator + proname + File.separator + htd + File.separator + value);
        if (!f.exists()) {
            return new ArrayList<>();
        } else {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(f));
            List<Map<String, Object>> jgmap = new ArrayList<>();
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                if (!wb.isSheetHidden(wb.getSheetIndex(wb.getSheetAt(j)))) {
                    XSSFSheet slSheet = wb.getSheetAt(j);

                    XSSFCell xmname = slSheet.getRow(1).getCell(5);//项目名
                    XSSFCell htdname = slSheet.getRow(1).getCell(cds*4+4);//合同段名


                    if (proname.equals(xmname.toString()) && htd.equals(htdname.toString())) {
                        slSheet.getRow(5).getCell(cds*4+9).setCellType(CellType.STRING);//总点数
                        slSheet.getRow(5).getCell(cds*4+10).setCellType(CellType.STRING);//合格点数

                        slSheet.getRow(5).getCell(cds*4+11).setCellType(CellType.STRING);
                        slSheet.getRow(5).getCell(cds*4+12).setCellType(CellType.STRING);
                        slSheet.getRow(3).getCell(5).setCellType(CellType.STRING);
                        double zds = Double.valueOf(slSheet.getRow(5).getCell(cds*4+9).getStringCellValue());
                        double hgds = Double.valueOf(slSheet.getRow(5).getCell(cds*4+10).getStringCellValue());
                        String zdsz1 = decf.format(zds);
                        String hgdsz1 = decf.format(hgds);
                        Map map = new HashMap();
                        map.put("检测项目", qlmc);
                        map.put("路面类型", wb.getSheetName(j));
                        map.put("总点数", zdsz1);
                        map.put("设计值", slSheet.getRow(3).getCell(5).getStringCellValue());
                        map.put("合格点数", hgdsz1);
                        map.put("合格率", zds!=0 ? df.format(hgds/zds*100) : 0);
                        map.put("Max",slSheet.getRow(5).getCell(cds*4+11).getStringCellValue());
                        map.put("Min",slSheet.getRow(5).getCell(cds*4+12).getStringCellValue());
                        jgmap.add(map);
                    }
                }
            }
            return jgmap;
        }
    }
}
