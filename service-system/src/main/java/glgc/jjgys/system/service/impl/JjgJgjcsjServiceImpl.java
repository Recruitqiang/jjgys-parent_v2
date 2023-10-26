package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.projectvo.jggl.JCSJVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgJgHtdinfoMapper;
import glgc.jjgys.system.mapper.JjgJgjcsjMapper;
import glgc.jjgys.system.mapper.JjgLqsJgQlMapper;
import glgc.jjgys.system.mapper.JjgLqsJgSdMapper;
import glgc.jjgys.system.service.JjgDwgctzeService;
import glgc.jjgys.system.service.JjgJgjcsjService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.service.JjgNyzlkfService;
import glgc.jjgys.system.service.JjgWgkfService;
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

    @Autowired
    private JjgWgkfService jjgWgkfService;

    @Autowired
    private JjgDwgctzeService jjgDwgctzeService;

    @Autowired
    private JjgNyzlkfService jjgNyzlkfService;

    @Value(value = "${jjgys.path.jgfilepath}")
    private String jgfilepath;

    @Override
    public void exportjgjcdata(HttpServletResponse response, String proname) {
        QueryWrapper<JjgJgHtdinfo> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgJgHtdinfo> htdinfo = jjgJgHtdinfoMapper.selectList(wrapper);
        List<JCSJVo> result = new ArrayList<>();
        if (htdinfo != null && !htdinfo.isEmpty()){
            for (JjgJgHtdinfo jjgJgHtdinfo : htdinfo) {
                String htd = jjgJgHtdinfo.getName();
                String lx = jjgJgHtdinfo.getLx();
                if (lx.contains("路基工程")){
                    List<JCSJVo> ljdata = getLjdata(htd);
                    result.addAll(ljdata);
                }
                if (lx.contains("路面工程")){
                    List<JCSJVo> lmdata = getLmdata(htd);
                    result.addAll(lmdata);

                }
                if (lx.contains("桥梁工程")){
                    List<JCSJVo> qldata = getQldata(proname,htd);
                    result.addAll(qldata);

                }
                if (lx.contains("隧道工程")){
                    List<JCSJVo> sddata = getSddata(proname,htd);
                    result.addAll(sddata);

                }
                if (lx.contains("交安工程")){
                    List<JCSJVo> jadata = getJadata(htd);
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
                                        JjgJgjcsj jcsj = new JjgJgjcsj();
                                        BeanUtils.copyProperties(ql,jcsj);
                                        jcsj.setProname(projectname);
                                        jcsj.setCreateTime(new Date());
                                        jjgJgjcsjMapper.insert(jcsj);
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

    @Override
    public void generatepdbOld(String proname) {
        QueryWrapper<JjgJgjcsj> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgJgjcsj> jjgJgjcsjs = jjgJgjcsjMapper.selectList(wrapper);
        //filename 合格率

        //先按合同段，然后按单位工程分别写入到不同的工作簿
        if (jjgJgjcsjs != null && !jjgJgjcsjs.isEmpty()){
            List<Map<String,Object>> data = getdata(jjgJgjcsjs);
            Map<String, List<Map<String,Object>>> groupedData = data.stream()
                    .collect(Collectors.groupingBy(m -> m.get("htd").toString()));
            groupedData.forEach((group, grouphtdData) -> {
                try {
                    writeExceldataOld(group,grouphtdData,proname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    /**
     * 旧规范
     * @param htd
     * @param data
     * @param proname
     * @throws IOException
     */
    private void writeExceldataOld(String htd, List<Map<String, Object>> data, String proname) throws IOException  {
        //每个合同段中的
        XSSFWorkbook wb = null;
        File f = new File(jgfilepath + File.separator + proname + File.separator + htd + File.separator + "00评定表.xlsx");
        File fdir = new File(jgfilepath + File.separator + proname + File.separator + htd);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        try {
            File directory = new File("service-system/src/main/resources/static");
            String reportPath = directory.getCanonicalPath();
            String name = "评定表(旧).xlsx";
            String path = reportPath + File.separator + name;
            Files.copy(Paths.get(path), new FileOutputStream(f));
            FileInputStream out = new FileInputStream(f);
            wb = new XSSFWorkbook(out);

            Map<String, List<Map<String,Object>>> groupedData = data.stream()
                    .collect(Collectors.groupingBy(m -> m.get("dwgc").toString()));
            // 对key进行排序
            List<String> sortedKeys = groupedData.keySet().stream()
                    .sorted(new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            // 判断包含关键字的顺序
                            List<String> keywords = Arrays.asList("路面工程", "交安工程","路基工程", "桥", "隧道");
                            for (String keyword : keywords) {
                                if (o1.contains(keyword) && !o2.contains(keyword)) {
                                    return -1; // o1包含关键字，o2不包含，则o1排在前面
                                } else if (!o1.contains(keyword) && o2.contains(keyword)) {
                                    return 1; // o1不包含关键字，o2包含，则o2排在前面
                                }
                            }
                            // 如果没有关键字，按字典序排列
                            return o1.compareTo(o2);
                        }
                    })
                    .collect(Collectors.toList());

            // 按关键字排序后的数据
            Map<String, List<Map<String, Object>>> sortedData = new LinkedHashMap<>();

            // 按排序后的key遍历原始数据
            for (String key : sortedKeys) {
                List<Map<String, Object>> dataList = groupedData.get(key);
                sortedData.put(key, dataList);
            }
            for (String key : sortedData.keySet()) {
                List<Map<String,Object>> list = groupedData.get(key);

                if (key.equals("路基工程")){
                    writeDataOld(wb,list,"分部-路基",proname,htd);
                }else if (key.equals("路面工程")){

                    writeDataOld(wb,list,"分部-路面",proname,htd);
                }else if (key.equals("交安工程")){
                    writeDataOld(wb,list,"分部-交安",proname,htd);
                }else {
                    //桥梁和隧道
                    writeDataOld(wb,list,"分部-"+key,proname,htd);
                }

            }

            //单位工程
            List<Map<String, Object>> dwgclist = new ArrayList<>();
            for (Sheet sheet : wb) {
                String sheetName = sheet.getSheetName();
                // 检查工作表名是否以"分部-"开头
                if (sheetName.startsWith("分部-")) {
                    // 处理工作表数据
                    List<Map<String, Object>> list = processSheetold(sheet);
                    dwgclist.addAll(list);
                }
            }
            writedwgcDataold(wb,dwgclist);

            //合同段
            List<Map<String, Object>> list = processhtdSheetold(proname,wb);
            //查询内页资料扣分
            QueryWrapper<JjgNyzlkf> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("proname",proname);
            queryWrapper.eq("htd",htd);
            List<JjgNyzlkf> list1 = jjgNyzlkfService.list(queryWrapper);
            String kf = "";
            if (list1!=null && list1.size()>0){
                kf = list1.get(0).getKf();
            }else {
                kf = "0";

            }
            writedhtdDataold(wb,list,kf);

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
            throw new JjgysException(20001, "生成评定表错误，请检查数据的正确性");
        }


    }

    private void writedhtdDataold(XSSFWorkbook wb, List<Map<String, Object>> list, String kf) {
        XSSFSheet sheet = wb.getSheet("合同段");
        createdHtdTableold(wb,getNum(list.size()));
        int index = 0;
        int tableNum = 0;
        List<Map<String, Object>> addtzedata = handtzedata(list);
        fillTitleHtdDataold(sheet, tableNum, addtzedata.get(0));
        for (Map<String, Object> datum : addtzedata) {
            if (index > 20){
                tableNum++;
                index = 0;
            }
            fillCommonHtdDataold(sheet,tableNum,index, datum);
            index++;
        }
        calculateHtdSheetold(sheet,kf);
        for (int j = 0; j < wb.getNumberOfSheets(); j++) {
            JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
        }
    }

    private void calculateHtdSheetold(XSSFSheet sheet, String kf) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if ("合计".equals(row.getCell(0).toString())) {

                rowstart = sheet.getRow(i-20);
                rowend = sheet.getRow(i-1);
                row.getCell(4).setCellFormula("SUM("+rowstart.getCell(4).getReference()+":"+rowend.getCell(4).getReference()+")");
                row.getCell(5).setCellFormula("SUM("+rowstart.getCell(5).getReference()+":"+rowend.getCell(5).getReference()+")");
                //合同段实测得分
                sheet.getRow(i+1).getCell(4).setCellFormula(row.getCell(5).getReference()+"/"+row.getCell(4).getReference());
                //内业资料扣分
                sheet.getRow(i+1).getCell(6).setCellValue(Double.valueOf(kf));

                //合同段鉴定得分
                sheet.getRow(i+2).getCell(4).setCellFormula( sheet.getRow(i+1).getCell(4).getReference()+"-"+sheet.getRow(i+1).getCell(6).getReference());
                //质量等级
                sheet.getRow(i+2).getCell(6).setCellFormula("IF("+sheet.getRow(i+2).getCell(4).getReference()+">=75,\"合格\",\"不合格\")");

            }
        }
    }

    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param datum
     */
    private void fillCommonHtdDataold(XSSFSheet sheet, int tableNum, int index, Map<String, Object> datum) {
        sheet.getRow(index + 4).getCell(0).setCellValue(index+1);
        sheet.getRow(index + 4).getCell(1).setCellValue(datum.get("dwgc").toString());
        sheet.getRow(index + 4).getCell(3).setCellValue(Double.valueOf(datum.get("df").toString()));
        sheet.getRow(index + 4).getCell(4).setCellValue(Double.valueOf(datum.get("tze").toString()));

        sheet.getRow(index + 4).getCell(5).setCellFormula(sheet.getRow(index + 4).getCell(3).getReference()+"*"+sheet.getRow(index + 4).getCell(4).getReference());
        sheet.getRow(index + 4).getCell(6).
                setCellFormula("IF("+sheet.getRow(index + 4).getCell(3).getReference()+">75,\"合格\",\"不合格\")");

    }

    /**
     * 给数据加入投资额
     * @param list
     * @return
     */
    private List<Map<String, Object>> handtzedata(List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            String proname = map.get("proname").toString();
            String htd = map.get("htd").toString();
            String dwgc = map.get("dwgc").toString();
            QueryWrapper<JjgDwgctze> queryWrapper =new QueryWrapper<>();
            queryWrapper.eq("proname",proname);
            queryWrapper.eq("htd",htd);
            queryWrapper.eq("name",dwgc);
            List<JjgDwgctze> list1 = jjgDwgctzeService.list(queryWrapper);
            if (list1!=null && list1.size()>0){
                map.put("tze",list1.get(0).getMoney());
            }else {
                map.put("tze","0");
            }
        }
        return list;
    }

    /**
     * 从分部工程的工作簿取数据
     * @param proname
     * @param wb
     * @return
     */
    private List<Map<String,Object>> processhtdSheetold(String proname, XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheet("单位工程");
        List<Map<String,Object>> list = new ArrayList<>();
        Row row;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            if ("单位工程名称：".equals(row.getCell(0).toString()) ) {
                Map map = new HashMap();
                map.put("proname",proname);
                map.put("dwgc",row.getCell(1).getStringCellValue());
                map.put("jsxm",row.getCell(3).getStringCellValue());
                map.put("htd",sheet.getRow(i+6).getCell(0).getStringCellValue());
                map.put("sgdw",sheet.getRow(i+2).getCell(1).getStringCellValue());
                map.put("jldw",sheet.getRow(i+2).getCell(3).getStringCellValue());
                map.put("df",sheet.getRow(i+25).getCell(1).getNumericCellValue());
                list.add(map);
            }
        }
        return list;

    }

    /**
     *
     * @param wb
     * @param data
     * @param sheetname
     * @param proname
     * @param htd
     */
    private void writeDataOld(XSSFWorkbook wb, List<Map<String,Object>> data, String sheetname, String proname, String htd) {
        /*Collections.sort(data, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
                String fbgc1 = (String) obj1.get("fbgc");
                String fbgc2 = (String) obj2.get("fbgc");
                return fbgc1.compareTo(fbgc2);
            }
        });*/
        Collections.sort(data, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
                String fbgc1 = (String) obj1.get("fbgc");
                String fbgc2 = (String) obj2.get("fbgc");
                int result = fbgc1.compareTo(fbgc2);
                if (result != 0) {
                    return result;
                }
                String ccxm1 = (String) obj1.get("ccxm");
                String ccxm2 = (String) obj2.get("ccxm");
                return ccxm1.compareTo(ccxm2);
            }
        });
        copySheet(wb,sheetname);
        XSSFPrintSetup ps = wb.getSheet(sheetname).getPrintSetup();
        ps.setLandscape(true); // 打印方向，true：横向，false：纵向(默认)

        QueryWrapper<JjgJgHtdinfo> wrapperhtd = new QueryWrapper<>();
        wrapperhtd.like("proname", proname);
        wrapperhtd.like("name", htd);
        JjgJgHtdinfo htdlist = jjgJgHtdinfoMapper.selectOne(wrapperhtd);

        XSSFSheet sheet = wb.getSheet(sheetname);
        createTable(wb,gettableNumold(data),sheetname);

        int index = 0;
        int tableNum = 0;
        int startRow = -1, endRow = -1, startCol = -1, endCol = -1, startCols = -1, endCols = -1, startColhgl = -1, endColhgl = -1, startColzl = -1, endColzl = -1, startColjq = -1, endColjq = -1;
        List<Map<String, Object>> rowAndcol = new ArrayList<>();
        List<Map<String, Object>> rowAndcol1 = new ArrayList<>();
        List<Map<String, Object>> rowAndcolhgl = new ArrayList<>();
        List<Map<String, Object>> rowAndcolzl = new ArrayList<>();
        List<Map<String, Object>> rowAndcoljq = new ArrayList<>();
        String ccname = data.get(0).get("ccxm").toString();
        //处理数据，加上外观扣分
        List<Map<String,Object>> hdata = handleData(proname,data);
        Collections.sort(hdata, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
                String fbgc1 = (String) obj1.get("fbgc");
                String fbgc2 = (String) obj2.get("fbgc");
                return fbgc1.compareTo(fbgc2);
            }
        });
        String fbgc = data.get(0).get("fbgc").toString();
        for (Map<String,Object> datum : hdata) {
            if (index % 14 == 0 && index!=0){
                tableNum++;
                fillTitleData(sheet,tableNum,proname,htd,htdlist,datum.get("fbgc").toString());
                index = 0;
            }
            if (fbgc.equals(datum.get("fbgc").toString())){
                fillTitleData(sheet,tableNum,proname,htd,htdlist,datum.get("fbgc").toString());
                if (ccname.equals(datum.get("ccxm").toString())){
                    startRow = tableNum * 22 + 6 + index % 16 ;
                    endRow = tableNum * 22 + 6 + index % 16 ;

                    startCol = 2;
                    endCol = 5;

                    Map<String, Object> map = new HashMap<>();
                    map.put("startRow",startRow);
                    map.put("endRow",endRow);
                    map.put("startCol",startCol);
                    map.put("endCol",endCol);
                    map.put("name",ccname);
                    map.put("tableNum",tableNum);
                    rowAndcol.add(map);

                    startCols = 7;
                    endCols = 16;
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("startRow",startRow);
                    map1.put("endRow",endRow);
                    map1.put("startCol",startCols);
                    map1.put("endCol",endCols);
                    map1.put("name",ccname);
                    map1.put("tableNum",tableNum);
                    rowAndcol1.add(map1);

                    startColhgl = 18;
                    endColhgl = 18;
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("startRow",startRow);
                    map2.put("endRow",endRow);
                    map2.put("startCol",startColhgl);
                    map2.put("endCol",endColhgl);
                    map2.put("name",ccname);
                    map2.put("tableNum",tableNum);
                    rowAndcolhgl.add(map2);

                    startColzl = 19;
                    endColzl = 19;
                    Map<String, Object> map3 = new HashMap<>();
                    map3.put("startRow",startRow);
                    map3.put("endRow",endRow);
                    map3.put("startCol",startColzl);
                    map3.put("endCol",endColzl);
                    map3.put("name",ccname);
                    map3.put("tableNum",tableNum);
                    rowAndcolzl.add(map3);

                    startColjq = 20;
                    endColjq = 20;
                    Map<String, Object> map4 = new HashMap<>();
                    map4.put("startRow",startRow);
                    map4.put("endRow",endRow);
                    map4.put("startCol",startColjq);
                    map4.put("endCol",endColjq);
                    map4.put("name",ccname);
                    map4.put("tableNum",tableNum);
                    rowAndcoljq.add(map4);
                }else {
                    ccname = datum.get("ccxm").toString();startRow = tableNum * 22 + 6 + index % 16 ;
                    endRow = tableNum * 22 + 6 + index % 16 ;

                    startCol = 2;
                    endCol = 5;

                    Map<String, Object> map = new HashMap<>();
                    map.put("startRow",startRow);
                    map.put("endRow",endRow);
                    map.put("startCol",startCol);
                    map.put("endCol",endCol);
                    map.put("name",ccname);
                    map.put("tableNum",tableNum);
                    rowAndcol.add(map);

                    startCols = 7;
                    endCols = 16;
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("startRow",startRow);
                    map1.put("endRow",endRow);
                    map1.put("startCol",startCols);
                    map1.put("endCol",endCols);
                    map1.put("name",ccname);
                    map1.put("tableNum",tableNum);
                    rowAndcol1.add(map1);

                    startColhgl = 18;
                    endColhgl = 18;
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("startRow",startRow);
                    map2.put("endRow",endRow);
                    map2.put("startCol",startColhgl);
                    map2.put("endCol",endColhgl);
                    map2.put("name",ccname);
                    map2.put("tableNum",tableNum);
                    rowAndcolhgl.add(map2);

                    startColzl = 19;
                    endColzl = 19;
                    Map<String, Object> map3 = new HashMap<>();
                    map3.put("startRow",startRow);
                    map3.put("endRow",endRow);
                    map3.put("startCol",startColzl);
                    map3.put("endCol",endColzl);
                    map3.put("name",ccname);
                    map3.put("tableNum",tableNum);
                    rowAndcolzl.add(map3);

                    startColjq = 20;
                    endColjq = 20;
                    Map<String, Object> map4 = new HashMap<>();
                    map4.put("startRow",startRow);
                    map4.put("endRow",endRow);
                    map4.put("startCol",startColjq);
                    map4.put("endCol",endColjq);
                    map4.put("name",ccname);
                    map4.put("tableNum",tableNum);
                    rowAndcoljq.add(map4);
                }
                fillCommonDataOld(sheet,tableNum,index,datum);
                index++;
            }else {
                fbgc = datum.get("fbgc").toString();
                tableNum ++;
                index = 0;
                ccname = datum.get("ccxm").toString();
                fillTitleData(sheet,tableNum,proname,htd,htdlist,datum.get("fbgc").toString());
                if (ccname.equals(datum.get("ccxm").toString())) {
                    startRow = tableNum * 22 + 6 + index % 16;
                    endRow = tableNum * 22 + 6 + index % 16;

                    startCol = 2;
                    endCol = 5;

                    Map<String, Object> map = new HashMap<>();
                    map.put("startRow", startRow);
                    map.put("endRow", endRow);
                    map.put("startCol", startCol);
                    map.put("endCol", endCol);
                    map.put("name", ccname);
                    map.put("tableNum", tableNum);
                    rowAndcol.add(map);

                    startCols = 7;
                    endCols = 16;
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("startRow",startRow);
                    map1.put("endRow",endRow);
                    map1.put("startCol",startCols);
                    map1.put("endCol",endCols);
                    map1.put("name",ccname);
                    map1.put("tableNum",tableNum);
                    rowAndcol1.add(map1);

                    startColhgl = 18;
                    endColhgl = 18;
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("startRow",startRow);
                    map2.put("endRow",endRow);
                    map2.put("startCol",startColhgl);
                    map2.put("endCol",endColhgl);
                    map2.put("name",ccname);
                    map2.put("tableNum",tableNum);
                    rowAndcolhgl.add(map2);

                    startColzl = 19;
                    endColzl = 19;
                    Map<String, Object> map3 = new HashMap<>();
                    map3.put("startRow",startRow);
                    map3.put("endRow",endRow);
                    map3.put("startCol",startColzl);
                    map3.put("endCol",endColzl);
                    map3.put("name",ccname);
                    map3.put("tableNum",tableNum);
                    rowAndcolzl.add(map3);

                    startColjq = 20;
                    endColjq = 20;
                    Map<String, Object> map4 = new HashMap<>();
                    map4.put("startRow",startRow);
                    map4.put("endRow",endRow);
                    map4.put("startCol",startColjq);
                    map4.put("endCol",endColjq);
                    map4.put("name",ccname);
                    map4.put("tableNum",tableNum);
                    rowAndcoljq.add(map4);
                }
                fillCommonDataOld(sheet,tableNum,index,datum);
                index += 1;
            }
            ccname = datum.get("ccxm").toString();

        }

        List<Map<String, Object>> maps = mergeCells(rowAndcol);
        List<Map<String, Object>> mapss = mergeCells(rowAndcol1);
        List<Map<String, Object>> maphgl = mergeCellsRow(rowAndcolhgl);
        List<Map<String, Object>> mapzl = mergeCellsRow(rowAndcolzl);
        List<Map<String, Object>> mapjq = mergeCellsRow(rowAndcoljq);

        for (Map<String, Object> map : maps) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
        }
        for (Map<String, Object> map : mapss) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
        }
        for (Map<String, Object> map : maphgl) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            // 不需要合并单元格的情况
            if (map.get("startRow").equals(map.get("endRow")) && map.get("startCol").equals(map.get("endCol"))) {
                continue;
            } else {
                sheet.addMergedRegion(new CellRangeAddress(
                        Integer.parseInt(map.get("startRow").toString()),
                        Integer.parseInt(map.get("endRow").toString()),
                        Integer.parseInt(map.get("startCol").toString()),
                        Integer.parseInt(map.get("endCol").toString())
                ));
            }
            //sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
        }

        for (Map<String, Object> map : mapzl) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            // 不需要合并单元格的情况
            if (map.get("startRow").equals(map.get("endRow")) && map.get("startCol").equals(map.get("endCol"))) {
                continue;
            } else {
                sheet.addMergedRegion(new CellRangeAddress(
                        Integer.parseInt(map.get("startRow").toString()),
                        Integer.parseInt(map.get("endRow").toString()),
                        Integer.parseInt(map.get("startCol").toString()),
                        Integer.parseInt(map.get("endCol").toString())
                ));
            }
            //sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
        }
        for (Map<String, Object> map : mapjq) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            // 不需要合并单元格的情况
            if (map.get("startRow").equals(map.get("endRow")) && map.get("startCol").equals(map.get("endCol"))) {
                continue;
            } else {
                sheet.addMergedRegion(new CellRangeAddress(
                        Integer.parseInt(map.get("startRow").toString()),
                        Integer.parseInt(map.get("endRow").toString()),
                        Integer.parseInt(map.get("startCol").toString()),
                        Integer.parseInt(map.get("endCol").toString())
                ));
            }
            //sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
        }
        //写完当前工作簿的数据后，就要插入公式计算了
        calculateFbgcSheetOLd(sheet);
        for (int j = 0; j < wb.getNumberOfSheets(); j++) {
            JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
        }
    }

    private List<Map<String, Object>> mergeCellsRow(List<Map<String, Object>> rowAndcol) {
        List<Map<String, Object>> result = new ArrayList<>();
        int currentEndRow = -1;
        int currentStartRow = -1;
        int currentStartCol = -1;
        int currentEndCol = -1;
        String currentNameAndCol = null;
        int currentTableNum = -1;
        for (Map<String, Object> row : rowAndcol) {
            int tableNum = (int) row.get("tableNum");
            int startRow = (int) row.get("startRow");
            int endRow = (int) row.get("endRow");
            int startCol = (int) row.get("startCol");
            int endCol = (int) row.get("endCol");
            String name = (String) row.get("name");
            if (currentNameAndCol == null || !currentNameAndCol.equals(name + "-" + startCol + "-" + endCol) || currentTableNum != tableNum) {
                if (currentStartRow != -1) {
                    for (int i = currentStartRow; i <= currentEndRow && i < result.size(); i++) {
                        Map<String, Object> newRow = new HashMap<>();
                        newRow.put("name", currentNameAndCol.split("-")[0]);
                        newRow.put("startRow", currentStartRow);
                        newRow.put("endRow", currentEndRow);
                        newRow.put("tableNum", currentTableNum);
                        for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
                            String key = entry.getKey();
                            if (!key.equals("startCol") && !key.equals("endCol")) {
                                newRow.put(key, entry.getValue());
                            }
                        }
                        result.set(i, newRow);
                    }
                }
                currentNameAndCol = name + "-" + startCol + "-" + endCol;
                currentStartCol = startCol;
                currentEndCol = endCol;
                currentTableNum = tableNum;
                currentStartRow = startRow;
                currentEndRow = endRow;
                result.add(row);
            } else {
                Map<String, Object> lastRow = result.get(result.size() - 1);
                if (currentEndCol < endCol) {
                    lastRow.put("endCol", endCol);
                    currentEndCol = endCol;
                }
                lastRow.put("endRow", endRow);
                currentEndRow = endRow;
            }
        }
        if (currentStartRow != -1) {
            for (int i = currentStartRow; i <= currentEndRow && i < result.size(); i++) {
                Map<String, Object> newRow = new HashMap<>();
                newRow.put("name", currentNameAndCol.split("-")[0]);
                newRow.put("startRow", currentStartRow);
                newRow.put("endRow", currentEndRow);
                newRow.put("tableNum", currentTableNum);
                for (Map.Entry<String, Object> entry : result.get(i).entrySet()) {
                    String key = entry.getKey();
                    if (!key.equals("startCol") && !key.equals("endCol")) {
                        newRow.put(key, entry.getValue());
                    }
                }
                result.set(i, newRow);
            }
        }

        return result;
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
     * @param proname
     * @param data
     * @return
     */
    private List<Map<String, Object>> handleData(String proname, List<Map<String, Object>> data) {

        for (Map<String, Object> datum : data) {
            QueryWrapper<JjgWgkf> queryWrapper  = new QueryWrapper<>();
            queryWrapper.eq("proname",proname);
            queryWrapper.eq("htd",datum.get("htd").toString());
            queryWrapper.eq("dwgc",datum.get("dwgc").toString());
            queryWrapper.eq("fbgc",datum.get("fbgc").toString());
            List<JjgWgkf> list = jjgWgkfService.list(queryWrapper);
            String fbgckf = "";
            if (list!=null&& !list.equals("")){
                 fbgckf = list.get(0).getFbgckf();
            }else {
                fbgckf = "0";
            }
            datum.put("kf",fbgckf);
        }
        return data;

    }

    /**
     * 计算分部工程的结果
     * @param sheet
     */
    private void calculateFbgcSheetOLd(XSSFSheet sheet) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if ("合计".equals(row.getCell(1).toString())) {
                rowstart = sheet.getRow(i-14);
                rowend = sheet.getRow(i-1);
                row.getCell(19).setCellFormula("SUM("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+")");
                row.getCell(20).setCellFormula("SUM("+rowstart.getCell(20).getReference()+":"+rowend.getCell(20).getReference()+")");
                sheet.getRow(i+1).getCell(6).setCellFormula(row.getCell(20).getReference()+"/"+row.getCell(19).getReference());
                sheet.getRow(i+1).getCell(17).setCellFormula(sheet.getRow(i+1).getCell(6).getReference()+"-"+sheet.getRow(i+1).getCell(10).getReference());
                sheet.getRow(i+1).getCell(20).setCellFormula("IF("+sheet.getRow(i+1).getCell(17).getReference()+">=75,\"合格\",\"不合格\")");
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
    private void fillCommonDataOld(XSSFSheet sheet, int tableNum, int index, Map<String,Object> datum) {
        sheet.getRow(tableNum * 22 + index + 6).getCell(1).setCellValue(1+index);
        sheet.getRow(tableNum * 22 + index + 6).getCell(2).setCellValue(datum.get("ccxm").toString());
        sheet.getRow(tableNum * 22 + index + 6).getCell(6).setCellValue(String.valueOf(datum.get("gdz")));

        sheet.getRow(tableNum * 22 + index + 6).getCell(7).setCellValue(datum.get("filename").toString());
        sheet.getRow(tableNum * 22 + index + 6).getCell(17).setCellValue("\\");
        sheet.getRow(tableNum * 22 + index + 6).getCell(18).setCellValue(datum.get("hgl").toString());
        //权值
        sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue(Double.valueOf(datum.get("qz").toString()));
        //加权得分
        sheet.getRow(tableNum * 22 + index + 6).getCell(20).setCellFormula(sheet.getRow(tableNum * 22 + index + 6).getCell(18).getReference()+"*"+sheet.getRow(tableNum * 22 + index + 6).getCell(19).getReference());

        //扣分
        sheet.getRow(tableNum * 22+6+15).getCell(10).setCellValue(Integer.valueOf(datum.get("kf").toString()));

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
        try {
            File directory = new File("service-system/src/main/resources/static");
            String reportPath = directory.getCanonicalPath();
            String name = "合同段评定表.xlsx";
            String path = reportPath + File.separator + name;
            Files.copy(Paths.get(path), new FileOutputStream(f));
            FileInputStream out = new FileInputStream(f);
            wb = new XSSFWorkbook(out);

            Map<String, List<Map<String,Object>>> groupedData = data.stream()
                    .collect(Collectors.groupingBy(m -> m.get("dwgc").toString()));
            // 对key进行排序
            List<String> sortedKeys = groupedData.keySet().stream()
                    .sorted(new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            // 判断包含关键字的顺序
                            List<String> keywords = Arrays.asList("路面工程", "交安工程","路基工程", "桥", "隧道");
                            for (String keyword : keywords) {
                                if (o1.contains(keyword) && !o2.contains(keyword)) {
                                    return -1; // o1包含关键字，o2不包含，则o1排在前面
                                } else if (!o1.contains(keyword) && o2.contains(keyword)) {
                                    return 1; // o1不包含关键字，o2包含，则o2排在前面
                                }
                            }
                            // 如果没有关键字，按字典序排列
                            return o1.compareTo(o2);
                        }
                    })
                    .collect(Collectors.toList());

            // 按关键字排序后的数据
            Map<String, List<Map<String, Object>>> sortedData = new LinkedHashMap<>();

            // 按排序后的key遍历原始数据
            for (String key : sortedKeys) {
                List<Map<String, Object>> dataList = groupedData.get(key);
                sortedData.put(key, dataList);
            }

            for (String key : sortedData.keySet()) {
                List<Map<String,Object>> list = groupedData.get(key);

                if (key.equals("路基工程")){
                    writeData(wb,list,"分部-路基",proname,htd);
                }else if (key.equals("路面工程")){

                    writeData(wb,list,"分部-路面",proname,htd);
                }else if (key.equals("交安工程")){
                    writeData(wb,list,"分部-交安",proname,htd);
                }else {
                    //桥梁和隧道
                    writeData(wb,list,key,proname,htd);
                }

            }

            //单位工程
            List<Map<String, Object>> dwgclist = new ArrayList<>();
            for (Sheet sheet : wb) {
                String sheetName = sheet.getSheetName();
                // 检查工作表名是否以"分部-"开头
                if (sheetName.startsWith("分部-")) {
                    // 处理工作表数据
                    List<Map<String, Object>> list = processSheet(sheet);
                    dwgclist.addAll(list);
                }
            }
            writedwgcData(wb,dwgclist);

            //合同段
            List<Map<String, Object>> list = processhtdSheet(wb);
            writedhtdData(wb,list);

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
            throw new JjgysException(20001, "生成评定表错误，请检查数据的正确性");
        }

    }

    /**
     *
     * @param wb
     * @param list
     */
    private void writedhtdData(XSSFWorkbook wb, List<Map<String, Object>> list) {
        XSSFSheet sheet = wb.getSheet("合同段");
        createdHtdTable(wb,getNum(list.size()));
        int index = 0;
        int tableNum = 0;
        fillTitleHtdData(sheet, tableNum, list.get(0));
        for (Map<String, Object> datum : list) {
            if (index > 20){
                tableNum++;
                index = 0;
            }
            fillCommonHtdData(sheet,tableNum,index, datum);
            index++;
        }
        calculateHtdSheet(sheet);
        for (int j = 0; j < wb.getNumberOfSheets(); j++) {
            JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
        }
    }

    /**
     *
     * @param wb
     * @param gettableNum
     */
    private void createdHtdTable(XSSFWorkbook wb, int gettableNum) {
        int record = 0;
        record = gettableNum;
        for(int i = 1; i < record; i++){
            if(i < record-1){
                RowCopy.copyRows(wb, "合同段", "合同段", 5, 28, (i - 1) * 24 + 29);
            }
            else{
                RowCopy.copyRows(wb, "合同段", "合同段", 5, 26, (i - 1) * 24 + 29);
            }
        }
        XSSFSheet sheet = wb.getSheet("合同段");
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
        RowCopy.copyRows(wb, "source", "合同段", 0, 2,(record) * 24+2);
        wb.setPrintArea(wb.getSheetIndex("合同段"), 0, 7, 0, (record) * 24+4);
    }

    /**
     *
     * @param size
     * @return
     */
    private int getNum(int size) {
        return size%24 ==0 ? size/24 : size/24+1;
    }


    /**
     *
     * @param sheet
     * @param tableNum
     * @param datum
     */
    private void fillTitleHtdData(XSSFSheet sheet, int tableNum, Map<String, Object> datum) {
        sheet.getRow(tableNum * 29 +1).getCell(1).setCellValue(datum.get("htd").toString());
        sheet.getRow(tableNum * 29 + 1).getCell(5).setCellValue(datum.get("jsxm").toString());
        sheet.getRow(tableNum * 29 + 2).getCell(1).setCellValue(datum.get("sgdw").toString());
        sheet.getRow(tableNum * 29 + 2).getCell(5).setCellValue(datum.get("jldw").toString());

    }

    private void fillTitleHtdDataold(XSSFSheet sheet, int tableNum, Map<String, Object> datum) {
        sheet.getRow(tableNum * 29 +1).getCell(2).setCellValue(datum.get("htd").toString());
        sheet.getRow(tableNum * 29 + 1).getCell(5).setCellValue(datum.get("jsxm").toString());
        sheet.getRow(tableNum * 29 + 2).getCell(2).setCellValue(datum.get("sgdw").toString());
        sheet.getRow(tableNum * 29 + 2).getCell(5).setCellValue(datum.get("jldw").toString());

    }

    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param datum
     */
    private void fillCommonHtdData(XSSFSheet sheet, int tableNum, int index, Map<String, Object> datum) {
        sheet.getRow(index + 5).getCell(0).setCellValue(datum.get("htd").toString());
        sheet.getRow(index + 5).getCell(1).setCellValue(datum.get("dwgc").toString());
        sheet.getRow(index + 5).getCell(4).setCellValue(datum.get("zldj").toString());

    }

    /**
     *
     * @param wb
     * @param gettableNum
     */
    private void createdHtdTableold(XSSFWorkbook wb, int gettableNum) {
        int record = 0;
        record = gettableNum;
        for(int i = 1; i < record; i++){
            if(i < record-1){
                RowCopy.copyRows(wb, "合同段", "合同段", 5, 28, (i - 1) * 24 + 29);
            }
            else{
                RowCopy.copyRows(wb, "合同段", "合同段", 5, 26, (i - 1) * 24 + 29);
            }
        }
        XSSFSheet sheet = wb.getSheet("合同段");
        int lastRowNum = sheet.getLastRowNum();
        int numMergedRegions = sheet.getNumMergedRegions();

        for (int i = numMergedRegions - 1; i >= 0; i--) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            int firstRow = mergedRegion.getFirstRow();
            int lastRow = mergedRegion.getLastRow();

            if (lastRow >= lastRowNum - 3 && lastRow <= lastRowNum) {
                sheet.removeMergedRegion(i);
            }
        }
        RowCopy.copyRows(wb, "source", "合同段", 0, 4,(record) * 23+2);
        wb.setPrintArea(wb.getSheetIndex("合同段"), 0, 7, 0, (record) * 23+5);
    }

    /**
     *
     * @param sheet
     */
    private void calculateHtdSheet(XSSFSheet sheet) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if ("合同段质量等级".equals(row.getCell(0).toString())) {
                rowstart = sheet.getRow(i-21);
                rowend = sheet.getRow(i-1);
                /*row.getCell(1).setCellFormula("IF(COUNTIF("+rowstart.getCell(2).getReference() +":"+rowend.getCell(2).getReference()
                        +",\"<>合格\")=0,\"合格\", \"不合格\")");*///=IF(COUNTIF(C64:C81,"<>合格")=0, "合格", "不合格")
                row.getCell(1).setCellFormula("IF(COUNTIF("+rowstart.getCell(4).getReference()
                        +":"+rowend.getCell(4).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(4).getReference()
                        +":"+rowend.getCell(4).getReference()+"),\"合格\", \"不合格\")");//=IF(COUNTIF(T7:T21, "合格") = COUNTA(T7:T21), "√", "")

            }
        }
    }
    /**
     *
     * @param wb
     * @return
     */
    private List<Map<String,Object>> processhtdSheet(XSSFWorkbook wb) {
        XSSFSheet sheet = wb.getSheet("单位工程");
        List<Map<String,Object>> list = new ArrayList<>();
        Row row;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            if ("单位工程名称：".equals(row.getCell(0).toString()) ) {
                Map map = new HashMap();
                map.put("dwgc",row.getCell(1).getStringCellValue());
                map.put("jsxm",row.getCell(3).getStringCellValue());
                map.put("htd",sheet.getRow(i+6).getCell(0).getStringCellValue());
                map.put("sgdw",sheet.getRow(i+2).getCell(1).getStringCellValue());
                map.put("jldw",sheet.getRow(i+2).getCell(3).getStringCellValue());
                map.put("zldj",sheet.getRow(i+24).getCell(1).getStringCellValue());
                list.add(map);
            }
        }
        return list;

    }

    /**
     *
     * @param wb
     * @param data
     */
    private void writedwgcDataold(XSSFWorkbook wb, List<Map<String, Object>> data) {
        if (data.size()>0&&data!=null){
            XSSFSheet sheet = wb.getSheet("单位工程");
            createdwgcTable(wb,getdwgcNum(data));

            int index = 0;
            int tableNum = 0;
            List<Map<String, Object>> sj = hadnld(data);
            String fbgc = sj.get(0).get("dwgc").toString();
            for (Map<String, Object> datum : sj) {
                if (fbgc.equals(datum.get("dwgc"))){
                    fillTitleDwgcData(sheet,tableNum,datum);
                    fillCommonDwgcDataold(sheet,tableNum,index,datum);
                    index++;
                }else {
                    fbgc = datum.get("dwgc").toString();
                    tableNum ++;
                    index = 0;
                    fillTitleDwgcData(sheet,tableNum,datum);
                    fillCommonDwgcDataold(sheet,tableNum,index,datum);
                    index += 1;
                }
            }
            calculateDwgcSheetold(sheet);
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
            }
        }


    }

    /**
     *
     * @param data
     * @return
     */
    private List<Map<String, Object>> hadnld(List<Map<String, Object>> data) {
        String proname = data.get(0).get("proname").toString();
        for (Map<String, Object> datum : data) {
            //扣分
            QueryWrapper<JjgWgkf> queryWrapper  = new QueryWrapper<>();
            queryWrapper.eq("proname",proname);
            queryWrapper.eq("htd",datum.get("htd").toString());
            queryWrapper.eq("dwgc",datum.get("dwgc").toString());
            queryWrapper.eq("fbgc",datum.get("fbgc").toString());
            List<JjgWgkf> list = jjgWgkfService.list(queryWrapper);
            String fbgckf ="";
            if (list!=null && !list.equals("")){
                fbgckf = list.get(0).getFbgckf();
            }else {
                fbgckf="0";
            }
            datum.put("kf",fbgckf);

            //权值
            String dwgc = datum.get("dwgc").toString();
            String fbgc = datum.get("fbgc").toString();
            if (dwgc.equals("路基工程") && fbgc.equals("路基土石方")){
                datum.put("qz","3");
            }else if(dwgc.equals("路基工程") && fbgc.equals("排水工程")){
                datum.put("qz","1");
            }else if(dwgc.equals("路基工程") && fbgc.equals("小桥")){
                datum.put("qz","2");
            }else if(dwgc.equals("路基工程") && fbgc.equals("涵洞")){
                datum.put("qz","1");
            }else if(dwgc.equals("路基工程") && fbgc.equals("支挡工程")){
                datum.put("qz","2");
            } else if(dwgc.equals("路面工程") && fbgc.equals("路面面层")){
                datum.put("qz","1");
            }else if(fbgc.equals("桥梁下部")){
                datum.put("qz","2");
            }else if(fbgc.equals("桥梁上部")){
                datum.put("qz","3");
            }else if(fbgc.equals("衬砌")){
                datum.put("qz","3");
            }else if(fbgc.equals("总体")){
                datum.put("qz","1");
            }else if(fbgc.equals("标志")){
                datum.put("qz","1");
            }else if(fbgc.equals("标线")){
                datum.put("qz","1");
            }else if(fbgc.equals("桥面系")){
                datum.put("qz","2");
            }else if(fbgc.equals("防护栏")){
                datum.put("qz","2");
            }else{
                datum.put("qz","0");
            }
        }
        return data;


    }

    /**
     *
     * @param wb
     * @param data
     */
    private void writedwgcData(XSSFWorkbook wb, List<Map<String, Object>> data) {
        if (data.size()>0&&data!=null){
            XSSFSheet sheet = wb.getSheet("单位工程");
            createdwgcTable(wb,getdwgcNum(data));

            int index = 0;
            int tableNum = 0;
            String fbgc = data.get(0).get("dwgc").toString();
            for (Map<String, Object> datum : data) {
                if (fbgc.equals(datum.get("dwgc"))){
                    fillTitleDwgcData(sheet,tableNum,datum);
                    fillCommonDwgcData(sheet,tableNum,index,datum);
                    index++;
                }else {
                    fbgc = datum.get("dwgc").toString();
                    tableNum ++;
                    index = 0;
                    fillTitleDwgcData(sheet,tableNum,datum);
                    fillCommonDwgcData(sheet,tableNum,index,datum);
                    index += 1;
                }

            }
            calculateDwgcSheet(sheet);
            for (int j = 0; j < wb.getNumberOfSheets(); j++) {
                JjgFbgcCommonUtils.updateFormula(wb, wb.getSheetAt(j));
            }
        }


    }


    /**
     *
     * @param sheet
     */
    private void calculateDwgcSheet(XSSFSheet sheet) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if ("单位工程质量等级".equals(row.getCell(0).toString())) {
                rowstart = sheet.getRow(i-18);
                rowend = sheet.getRow(i-1);
                /*row.getCell(1).setCellFormula("IF(COUNTIF("+rowstart.getCell(2).getReference() +":"+rowend.getCell(2).getReference()
                        +",\"<>合格\")=0,\"合格\", \"不合格\")");*///=IF(COUNTIF(C64:C81,"<>合格")=0, "合格", "不合格")
                row.getCell(1).setCellFormula("IF(COUNTIF("+rowstart.getCell(2).getReference()
                        +":"+rowend.getCell(2).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(2).getReference()
                        +":"+rowend.getCell(2).getReference()+"),\"合格\", \"不合格\")");//=IF(COUNTIF(T7:T21, "合格") = COUNTA(T7:T21), "√", "")

            }
        }
    }

    /**
     *
     * @param sheet
     */
    private void calculateDwgcSheetold(XSSFSheet sheet) {
        XSSFRow row = null;
        XSSFRow rowstart = null;
        XSSFRow rowend = null;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if ("合计".equals(row.getCell(1).toString())) {
                rowstart = sheet.getRow(i-18);
                rowend = sheet.getRow(i-1);
                row.getCell(3).setCellFormula("SUM("+rowstart.getCell(3).getReference()+":"+rowend.getCell(3).getReference()+")");
                row.getCell(4).setCellFormula("SUM("+rowstart.getCell(4).getReference()+":"+rowend.getCell(4).getReference()+")");
                sheet.getRow(i+1).getCell(1).setCellFormula(row.getCell(4).getReference()+"/"+row.getCell(3).getReference());
                sheet.getRow(i+1).getCell(5).setCellFormula("IF("+sheet.getRow(i+1).getCell(1).getReference()+">=75,\"合格\",\"不合格\")");

            }
        }
    }


    /**
     *
     * @param sheet
     * @param tableNum
     * @param datum
     */
    private void fillTitleDwgcData(XSSFSheet sheet, int tableNum, Map<String, Object> datum) {
        sheet.getRow(tableNum * 28 +1).getCell(1).setCellValue(datum.get("dwgc").toString());
        sheet.getRow(tableNum * 28 +1).getCell(3).setCellValue(datum.get("proname").toString());
        sheet.getRow(tableNum * 28 +2).getCell(1).setCellValue(datum.get("proname").toString());
        sheet.getRow(tableNum * 28 +2).getCell(3).setCellValue(datum.get("gcbw").toString());
        sheet.getRow(tableNum * 28 +3).getCell(1).setCellValue(datum.get("sgbw").toString());
        sheet.getRow(tableNum * 28 +3).getCell(3).setCellValue(datum.get("jldw").toString());


    }
    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param datum
     */
    private void fillCommonDwgcDataold(XSSFSheet sheet, int tableNum, int index, Map<String, Object> datum) {
        sheet.getRow(tableNum * 28 + index + 7).getCell(0).setCellValue(datum.get("htd").toString());
        sheet.getRow(tableNum * 28 + index + 7).getCell(1).setCellValue(datum.get("fbgc").toString());
        sheet.getRow(tableNum * 28 + index + 7).getCell(2).setCellValue(Double.valueOf(datum.get("scfs").toString()));
        sheet.getRow(tableNum * 28 + index + 7).getCell(3).setCellValue(Double.valueOf(datum.get("qz").toString()));
        sheet.getRow(tableNum * 28 + index + 7).getCell(4).
                setCellFormula(sheet.getRow(tableNum * 28 + index + 7).getCell(2).getReference()+"*"+sheet.getRow(tableNum * 28 + index + 7).getCell(3).getReference());
    }


    /**
     *
     * @param sheet
     * @param tableNum
     * @param index
     * @param datum
     */
    private void fillCommonDwgcData(XSSFSheet sheet, int tableNum, int index, Map<String, Object> datum) {
        sheet.getRow(tableNum * 28 + index + 7).getCell(0).setCellValue(datum.get("htd").toString());
        sheet.getRow(tableNum * 28 + index + 7).getCell(1).setCellValue(datum.get("fbgc").toString());
        sheet.getRow(tableNum * 28 + index + 7).getCell(2).setCellValue(datum.get("sfhg").toString());

    }

    /**
     *
     * @param data
     * @return
     */
    private int getdwgcNum(List<Map<String, Object>> data) {
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> map : data) {
            String name = map.get("dwgc").toString();
            if (resultMap.containsKey(name)) {
                resultMap.put(name, resultMap.get(name) + 1);
            } else {
                resultMap.put(name, 1);
            }
        }
        int num = 0;
        for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
            int value = entry.getValue();
            if (value%18==0){
                num += value/18;
            }else {
                num += value/18+1;
            }
        }
        return num;

    }

    /**
     * 单位工程
     * @param wb
     * @param gettableNum
     */
    private void createdwgcTable(XSSFWorkbook wb, int gettableNum) {
        for(int i = 1; i < gettableNum; i++){
            RowCopy.copyRows(wb, "单位工程", "单位工程", 0, 27, i*28);
        }
        if(gettableNum > 1){
            wb.setPrintArea(wb.getSheetIndex("单位工程"), 0, 5, 0, gettableNum*28-1);
        }

    }

    /**
     *
     * @param sheet
     * @return
     */
    private List<Map<String,Object>> processSheetold(Sheet sheet) {
        List<Map<String,Object>> list = new ArrayList<>();

        Row row;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            if ("合同段：".equals(row.getCell(0).toString()) ) {
                Map map = new HashMap();
                map.put("htd",row.getCell(2).getStringCellValue());
                map.put("fbgc",row.getCell(8).getStringCellValue());
                map.put("proname",row.getCell(15).getStringCellValue());

                map.put("gcbw",sheet.getRow(i+1).getCell(2).getStringCellValue());
                map.put("sgbw",sheet.getRow(i+1).getCell(8).getStringCellValue());
                map.put("jldw",sheet.getRow(i+1).getCell(15).getStringCellValue());

                map.put("scfs",sheet.getRow(i+20).getCell(6).getNumericCellValue());
                String s = sheet.getSheetName().substring(sheet.getSheetName().indexOf("-")+1);
                if (s.equals("路面") || s.equals("路基") || s.equals("交安"))
                {
                    map.put("dwgc",s+"工程");
                }else {
                    map.put("dwgc",s);
                }
                list.add(map);
            }
        }
        return list;
    }


    /**
     *
     * @param sheet
     * @return
     */
    private List<Map<String,Object>> processSheet(Sheet sheet) {
        List<Map<String,Object>> list = new ArrayList<>();

        Row row;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getPhysicalNumberOfRows(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            if ("合同段：".equals(row.getCell(0).toString()) ) {
                Map map = new HashMap();
                map.put("htd",row.getCell(2).getStringCellValue());
                map.put("fbgc",row.getCell(8).getStringCellValue());
                map.put("jsxm",row.getCell(15).getStringCellValue());
                map.put("proname",row.getCell(15).getStringCellValue());
                map.put("gcbw",sheet.getRow(i+1).getCell(2).getStringCellValue());
                map.put("sgbw",sheet.getRow(i+1).getCell(8).getStringCellValue());
                map.put("jldw",sheet.getRow(i+1).getCell(15).getStringCellValue());
                if (sheet.getRow(i+20).getCell(16).getStringCellValue().equals("√")){
                    map.put("sfhg","合格");
                }else {
                    map.put("sfhg","不合格");
                }

                map.put("dwgc",sheet.getSheetName().substring(sheet.getSheetName().indexOf("-")+1));
                list.add(map);
            }
        }
        return list;
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
        /*Collections.sort(data, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
                String fbgc1 = (String) obj1.get("fbgc");
                String fbgc2 = (String) obj2.get("fbgc");
                return fbgc1.compareTo(fbgc2);
            }
        });*/
        Collections.sort(data, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> obj1, Map<String, Object> obj2) {
                String fbgc1 = (String) obj1.get("fbgc");
                String fbgc2 = (String) obj2.get("fbgc");
                int result = fbgc1.compareTo(fbgc2);
                if (result != 0) {
                    return result;
                }
                String ccxm1 = (String) obj1.get("ccxm");
                String ccxm2 = (String) obj2.get("ccxm");
                return ccxm1.compareTo(ccxm2);
            }
        });
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
        int startRow = -1, endRow = -1, startCol = -1, endCol = -1, startCols = -1, endCols = -1, startColhgl = -1, endColhgl = -1, startColzl = -1, endColzl = -1;
        List<Map<String, Object>> rowAndcol = new ArrayList<>();
        List<Map<String, Object>> rowAndcol1 = new ArrayList<>();
        List<Map<String, Object>> rowAndcolhgl = new ArrayList<>();
        List<Map<String, Object>> rowAndcolzl = new ArrayList<>();
        String ccname = data.get(0).get("ccxm").toString();
        String fbgc = data.get(0).get("fbgc").toString();
        for (Map<String,Object> datum : data) {
            if (index % 15 == 0 && index!=0){
                tableNum++;
                fillTitleData(sheet,tableNum,proname,htd,htdlist,datum.get("fbgc").toString());
                index = 0;
            }
            if (fbgc.equals(datum.get("fbgc").toString())){
                fillTitleData(sheet,tableNum,proname,htd,htdlist,datum.get("fbgc").toString());
                if (ccname.equals(datum.get("ccxm").toString())){
                    startRow = tableNum * 22 + 6 + index % 16 ;
                    endRow = tableNum * 22 + 6 + index % 16 ;

                    startCol = 2;
                    endCol = 5;

                    Map<String, Object> map = new HashMap<>();
                    map.put("startRow",startRow);
                    map.put("endRow",endRow);
                    map.put("startCol",startCol);
                    map.put("endCol",endCol);
                    map.put("name",ccname);
                    map.put("tableNum",tableNum);
                    rowAndcol.add(map);

                    startCols = 7;
                    endCols = 16;
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("startRow",startRow);
                    map1.put("endRow",endRow);
                    map1.put("startCol",startCols);
                    map1.put("endCol",endCols);
                    map1.put("name",ccname);
                    map1.put("tableNum",tableNum);
                    rowAndcol1.add(map1);

                    startColhgl = 17;
                    endColhgl = 18;
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("startRow",startRow);
                    map2.put("endRow",endRow);
                    map2.put("startCol",startColhgl);
                    map2.put("endCol",endColhgl);
                    map2.put("name",ccname);
                    map2.put("tableNum",tableNum);
                    rowAndcolhgl.add(map2);

                    startColzl = 19;
                    endColzl = 20;
                    Map<String, Object> map3 = new HashMap<>();
                    map3.put("startRow",startRow);
                    map3.put("endRow",endRow);
                    map3.put("startCol",startColzl);
                    map3.put("endCol",endColzl);
                    map3.put("name",ccname);
                    map3.put("tableNum",tableNum);
                    rowAndcolzl.add(map3);
                }else {
                    ccname = datum.get("ccxm").toString();startRow = tableNum * 22 + 6 + index % 16 ;
                    endRow = tableNum * 22 + 6 + index % 16 ;

                    startCol = 2;
                    endCol = 5;

                    Map<String, Object> map = new HashMap<>();
                    map.put("startRow",startRow);
                    map.put("endRow",endRow);
                    map.put("startCol",startCol);
                    map.put("endCol",endCol);
                    map.put("name",ccname);
                    map.put("tableNum",tableNum);
                    rowAndcol.add(map);

                    startCols = 7;
                    endCols = 16;
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("startRow",startRow);
                    map1.put("endRow",endRow);
                    map1.put("startCol",startCols);
                    map1.put("endCol",endCols);
                    map1.put("name",ccname);
                    map1.put("tableNum",tableNum);
                    rowAndcol1.add(map1);

                    startColhgl = 17;
                    endColhgl = 18;
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("startRow",startRow);
                    map2.put("endRow",endRow);
                    map2.put("startCol",startColhgl);
                    map2.put("endCol",endColhgl);
                    map2.put("name",ccname);
                    map2.put("tableNum",tableNum);
                    rowAndcolhgl.add(map2);

                    startColzl = 19;
                    endColzl = 20;
                    Map<String, Object> map3 = new HashMap<>();
                    map3.put("startRow",startRow);
                    map3.put("endRow",endRow);
                    map3.put("startCol",startColzl);
                    map3.put("endCol",endColzl);
                    map3.put("name",ccname);
                    map3.put("tableNum",tableNum);
                    rowAndcolzl.add(map3);
                }

                fillCommonData(sheet,tableNum,index,datum);
                index++;
            }else {
                fbgc = datum.get("fbgc").toString();
                tableNum ++;
                index = 0;
                fillTitleData(sheet,tableNum,proname,htd,htdlist,datum.get("fbgc").toString());
                ccname = datum.get("ccxm").toString();
                if (ccname.equals(datum.get("ccxm").toString())) {
                    startRow = tableNum * 22 + 6 + index % 16;
                    endRow = tableNum * 22 + 6 + index % 16;

                    startCol = 2;
                    endCol = 5;

                    Map<String, Object> map = new HashMap<>();
                    map.put("startRow", startRow);
                    map.put("endRow", endRow);
                    map.put("startCol", startCol);
                    map.put("endCol", endCol);
                    map.put("name", ccname);
                    map.put("tableNum", tableNum);
                    rowAndcol.add(map);

                    startCols = 7;
                    endCols = 16;
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("startRow",startRow);
                    map1.put("endRow",endRow);
                    map1.put("startCol",startCols);
                    map1.put("endCol",endCols);
                    map1.put("name",ccname);
                    map1.put("tableNum",tableNum);
                    rowAndcol1.add(map1);

                    startColhgl = 17;
                    endColhgl = 18;
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("startRow",startRow);
                    map2.put("endRow",endRow);
                    map2.put("startCol",startColhgl);
                    map2.put("endCol",endColhgl);
                    map2.put("name",ccname);
                    map2.put("tableNum",tableNum);
                    rowAndcolhgl.add(map2);

                    startColzl = 19;
                    endColzl = 20;
                    Map<String, Object> map3 = new HashMap<>();
                    map3.put("startRow",startRow);
                    map3.put("endRow",endRow);
                    map3.put("startCol",startColzl);
                    map3.put("endCol",endColzl);
                    map3.put("name",ccname);
                    map3.put("tableNum",tableNum);
                    rowAndcolzl.add(map3);
                }
                fillCommonData(sheet,tableNum,index,datum);
                index += 1;
            }
            ccname = datum.get("ccxm").toString();

        }
        List<Map<String, Object>> maps = mergeCells(rowAndcol);
        List<Map<String, Object>> mapss = mergeCells(rowAndcol1);
        List<Map<String, Object>> maphgl = mergeCells(rowAndcolhgl);
        List<Map<String, Object>> mapzl = mergeCells(rowAndcolzl);

        for (Map<String, Object> map : maps) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
        }
        for (Map<String, Object> map : mapss) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
        }
        for (Map<String, Object> map : maphgl) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
        }

        for (Map<String, Object> map : mapzl) {
            int startRow1 = Integer.parseInt(map.get("startRow").toString());
            int endRow1 = Integer.parseInt(map.get("endRow").toString());
            int startCol1 = Integer.parseInt(map.get("startCol").toString());
            int endCol1 = Integer.parseInt(map.get("endCol").toString());
            CellRangeAddress newRegion = new CellRangeAddress(startRow1, endRow1, startCol1, endCol1);
            // 检查是否存在重叠的合并区域
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (int i = mergedRegions.size() - 1; i >= 0; i--) {
                CellRangeAddress mergedRegion = mergedRegions.get(i);
                if (mergedRegion.intersects(newRegion)){
                    sheet.removeMergedRegion(i);
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(map.get("startRow").toString()), Integer.parseInt(map.get("endRow").toString()), Integer.parseInt(map.get("startCol").toString()), Integer.parseInt(map.get("endCol").toString())));
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
                row.getCell(8).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"不合格\")>0,\"\",\"√\")");//=IF(COUNTIF(T29:U43,"不合格")>0,"","√")
                row.getCell(10).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"不合格\")>0,\"×\",\"\")");//=IF(COUNTIF(T29:U43,"不合格")>0,"","√")

                //row.getCell(8).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+"),\"√\", \"\")");//=IF(COUNTIF(T7:T21, "合格") = COUNTA(T7:T21), "√", "")
                //row.getCell(10).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+"),\"\", \"×\")");//=IF(COUNTIF(T7:T21, "不合格") = COUNTA(T7:T21), "", "×")

                row.getCell(16).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"不合格\")>0,\"\",\"√\")");//=IF(COUNTIF(T29:U43,"不合格")>0,"","√")
                row.getCell(19).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"不合格\")>0,\"×\",\"\")");

                //row.getCell(16).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+"),\"√\", \"\")");//=IF(COUNTIF(T7:T21, "合格") = COUNTA(T7:T21), "√", "")
                //row.getCell(19).setCellFormula("IF(COUNTIF("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+",\"合格\")=COUNTA("+rowstart.getCell(19).getReference()+":"+rowend.getCell(19).getReference()+"),\"\", \"×\")");//=IF(COUNTIF(T7:T21, "不合格") = COUNTA(T7:T21), "", "×")
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
        sheet.getRow(tableNum * 22 + index + 6).getCell(17).setCellValue(datum.get("hgl").toString());
        if (datum.get("ccxm").toString().contains("*")){
            Double value = Double.valueOf(datum.get("hgl").toString());
            if (value == 100){
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("合格");
            }else {
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("不合格");
            }
        }else if (datum.get("ccxm").toString().contains("△")){
            Double value = Double.valueOf(datum.get("hgl").toString());
            if (value >= 95){
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("合格");
            }else {
                sheet.getRow(tableNum * 22 + index + 6).getCell(19).setCellValue("不合格");
            }
        }else {
            Double value = Double.valueOf(datum.get("hgl").toString());
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
     * 分部
     * @param data
     * @return
     */
    private int gettableNumold(List<Map<String,Object>> data) {
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
            if (value%14==0){
                num += value/14;
            }else {
                num += value/14+1;
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

            if (fbgc.equals("路基土石方") && ccxm.equals("△沉降")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基压实度沉降质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路基土石方") && ccxm.equals("△压实度（沙砾）")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路基土石方") && ccxm.equals("△压实度（灰土）")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路基土石方") && ccxm.equals("△弯沉")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基弯沉质量鉴定表》检测"+zds+"个评定单元,合格"+hgds+"个评定单元");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路基土石方") && ccxm.equals("边坡")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《路基边坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("排水工程") && ccxm.equals("断面尺寸")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《结构（断面）尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("排水工程") && ccxm.equals("铺砌厚度")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《排水铺砌厚度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("小桥") && ccxm.equals("*混凝土强度")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《小桥砼强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("小桥") && ccxm.equals("△断面尺寸")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《小桥结构尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("涵洞") && ccxm.equals("*混凝土强度")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《涵洞砼强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("涵洞") && ccxm.equals("结构尺寸")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《涵洞结构尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("支挡工程") && ccxm.equals("*混凝土强度")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《支挡工程砼强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("支挡工程") && ccxm.equals("△断面尺寸")){
                map.put("sheetname","分部-路基");
                map.put("filename","详见《支挡工程结构尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }
            //桥梁和隧道
            else if (fbgc.equals("桥梁上部") && ccxm.equals("*混凝土强度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥梁上部墩台砼强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥梁上部") && ccxm.equals("主要结构尺寸")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥梁上部主要结构尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥梁上部") && ccxm.equals("钢筋保护层厚度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥梁上部钢筋保护层厚度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥梁下部") && ccxm.equals("*混凝土强度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥梁下部墩台砼强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥梁下部") && ccxm.equals("主要结构尺寸")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥梁下部主要结构尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥梁下部") && ccxm.equals("钢筋保护层厚度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥梁下部钢筋保护层厚度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥梁下部") && ccxm.equals("*竖直度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥梁下部墩台垂直度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }

            else if (fbgc.equals("桥面系") && ccxm.equals("沥青桥平整度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥面系平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥面系") && ccxm.equals("混凝土桥平整度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥面系平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥面系") && ccxm.equals("沥青桥面构造深度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥面系构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            } else if (fbgc.equals("桥面系") && ccxm.equals("混凝土桥面构造深度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《混凝土桥面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥面系") && ccxm.equals("摩擦系数")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《桥面系摩擦系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥面系") && ccxm.equals("沥青桥面横坡")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《沥青桥面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("桥面系") && ccxm.equals("混凝土桥面横坡")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《混凝土路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }

            else if (fbgc.equals("隧道路面") && ccxm.equals("沥青路面压实度（隧道路面）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《沥青路面压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("沥青路面弯沉")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","0");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("沥青路面车辙（隧道路面）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《隧道路面车辙质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("沥青路面渗水系数（隧道路面）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《路面渗水系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("混凝土路面强度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《混凝土路面弯拉强度鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("混凝土路面相邻板高差")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《混凝土路面相邻板高差质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("平整度（隧道路面沥青）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("平整度（隧道路面混凝土）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("构造深度（隧道路面）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《隧道路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("摩擦系数（隧道路面）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《隧道路面摩擦系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("厚度（隧道路面混凝土）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《混凝土路面厚度质量鉴定表（钻芯法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("厚度（隧道路面沥青）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《沥青路面厚度质量鉴定表（钻芯法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("厚度（隧道路面雷达）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《隧道路面厚度质量鉴定表（雷达法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("横坡（隧道路面沥青）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《沥青路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("隧道路面") && ccxm.equals("横坡（隧道路面混凝土）")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《混凝土路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("总体") && ccxm.equals("△净空")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《净空质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("总体") && ccxm.equals("宽度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《隧道宽度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("衬砌") && ccxm.equals("*衬砌强度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《混凝土强度质量鉴定表（回弹法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("衬砌") && ccxm.equals("△衬砌厚度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《隧道衬砌厚度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("衬砌") && ccxm.equals("大面平整度")){
                map.put("sheetname","分部-"+data.getDwgc());
                map.put("filename","详见《混凝土大面平整度试验检测鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }
            else if (fbgc.equals("路面面层") && ccxm.equals("△沥青路面压实度（路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("△沥青路面压实度（隧道路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面压实度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("△沥青路面弯沉")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《路面弯沉质量鉴定结果汇总表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面车辙（路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面车辙质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面车辙（隧道路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《隧道路面车辙质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面渗水系数（路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面渗水系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("沥青路面渗水系数（隧道路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面渗水系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("*混凝土路面强度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面强度鉴定结果汇总表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("混凝土路面相邻板高差")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面相邻板高差质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（沥青路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（混凝土路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("平整度（桥面系混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（桥面系沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（隧道路面混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("平整度（隧道路面沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《平整度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（混凝土路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（沥青路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（桥面系混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（桥面系沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《桥面系构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（隧道路面混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("构造深度（隧道路面沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《隧道路面构造深度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("摩擦系数（沥青路面面层）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面摩擦系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("摩擦系数（桥面系）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《桥面系摩擦系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("摩擦系数（隧道路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《隧道路面摩擦系数质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("△厚度（混凝土路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面厚度质量鉴定表（钻芯法） 》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("△厚度（沥青路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面厚度质量鉴定表（钻芯法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("△厚度（路面雷达法）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《路面厚度质量鉴定表（雷达法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("△厚度（隧道路面混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面厚度质量鉴定表（钻芯法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("△厚度（隧道路面沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面厚度质量鉴定表（钻芯法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("△厚度（隧道路面雷达法）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《隧道路面厚度质量鉴定表（雷达法）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","3");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            } else if (fbgc.equals("路面面层") && ccxm.equals("横坡（混凝土路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（沥青路面）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（桥面系混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（桥面系沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（隧道路面混凝土）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《混凝土路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("路面面层") && ccxm.equals("横坡（隧道路面沥青）")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《沥青路面横坡质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }

            else if (fbgc.equals("标志") && ccxm.equals("立柱竖直度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交通标志板安装质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("标志") && ccxm.equals("标志板净空")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交通标志板安装质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("标志") && ccxm.equals("标志板厚度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交通标志板安装质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","1");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("标志") && ccxm.equals("标志面反光膜等级及逆射光系数")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交通标志板安装质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            } else if (fbgc.equals("标线") && ccxm.equals("△反光标线逆反射系数")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路交通标线施工质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("标线") && ccxm.equals("△标线厚度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路交通标线施工质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("波形梁板基地金属厚度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路防护栏施工质量鉴定表（波形梁钢护栏）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("*波形梁钢护栏立柱壁厚")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路防护栏施工质量鉴定表（波形梁钢护栏）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("*波形梁钢护栏立柱埋入深度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路防护栏施工质量鉴定表（波形梁钢护栏）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("*波形梁钢护栏横梁中心高度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《道路防护栏施工质量鉴定表（波形梁钢护栏）》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("*混凝土护栏强度")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交安工程砼护栏强度质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
                result.add(map);
            }else if (fbgc.equals("防护栏") && ccxm.equals("△砼护栏断面尺寸")){
                map.put("sheetname","分部-路面");
                map.put("filename","详见《交安砼护栏断面尺寸质量鉴定表》检测"+zds+"点,合格"+hgds+"点");
                map.put("qz","2");
                map.put("hgl",Double.valueOf(zds)!=0?df.format(Double.valueOf(hgds)/Double.valueOf(zds)*100):0);
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
    private void exportExcel(HttpServletResponse response, String proname, List<JCSJVo> result) {
        try {
            String fileName = proname+"检测数据录入表";
            String sheetName1 = "检测数据";
            ExcelUtil.writeExcelWithSheets(response, result, fileName, sheetName1, new JCSJVo()).finish();
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
    private List<JCSJVo> getSddata(String proname, String htd) {
        String[] ccxm1 = {"*衬砌强度", "△衬砌厚度", "大面平整度"};
        String[] ccxm2 = {"宽度", "△净空"};
        String[] ccxm3 = {"沥青路面压实度（隧道路面）", "沥青路面弯沉","沥青路面车辙（隧道路面）","沥青路面渗水系数（隧道路面）","混凝土路面强度","混凝土路面相邻板高差","平整度（隧道路面沥青）",
                "平整度（隧道路面混凝土）","构造深度（隧道路面）","摩擦系数（隧道路面）","厚度（隧道路面混凝土）","厚度（隧道路面沥青）","厚度（隧道路面雷达）","横坡（隧道路面沥青）","横坡（隧道路面混凝土）"};
        List<JCSJVo> result = new ArrayList<>();
        QueryWrapper<JjgLqsJgSd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgLqsJgSd> htdinfo = jjgLqsJgSdMapper.selectList(wrapper);
        if (htdinfo!=null && !htdinfo.isEmpty()){
            for (JjgLqsJgSd jjgLqsJgSd : htdinfo) {
                String sdname = jjgLqsJgSd.getSdname();
                for (String ccxm : ccxm1) {
                    JCSJVo jgjcsj = new JCSJVo();
                    jgjcsj.setHtd(htd);
                    jgjcsj.setDwgc(sdname);
                    jgjcsj.setFbgc("衬砌");
                    jgjcsj.setCcxm(ccxm);
                    /*Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", sdname);
                    map.put("fbgc", "衬砌");
                    map.put("ccxm", ccxm);*/
                    result.add(jgjcsj);
                }
                for (String ccxm : ccxm2) {
                    /*Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", sdname);
                    map.put("fbgc", "总体");
                    map.put("ccxm", ccxm);*/
                    JCSJVo jgjcsj = new JCSJVo();
                    jgjcsj.setHtd(htd);
                    jgjcsj.setDwgc(sdname);
                    jgjcsj.setFbgc("总体");
                    jgjcsj.setCcxm(ccxm);
                    result.add(jgjcsj);
                }
                for (String ccxm : ccxm3) {
                    /*Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", sdname);
                    map.put("fbgc", "隧道路面");
                    map.put("ccxm", ccxm);*/
                    JCSJVo jgjcsj = new JCSJVo();
                    jgjcsj.setHtd(htd);
                    jgjcsj.setDwgc(sdname);
                    jgjcsj.setFbgc("隧道路面");
                    jgjcsj.setCcxm(ccxm);

                    result.add(jgjcsj);
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
    private List<JCSJVo> getQldata(String proname, String htd) {
        String[] ccxm1 = {"*混凝土强度", "主要结构尺寸", "钢筋保护层厚度"};
        String[] ccxm2 = {"*混凝土强度", "主要结构尺寸","钢筋保护层厚度","*竖直度"};
        String[] ccxm3 = {"沥青桥平整度", "混凝土桥平整度","沥青桥面构造深度","混凝土桥面构造深度","摩擦系数","沥青桥面横坡","混凝土桥面横坡"};
        List<JCSJVo> result = new ArrayList<>();
        QueryWrapper<JjgLqsJgQl> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.eq("htd",htd);
        List<JjgLqsJgQl> htdinfo = jjgLqsJgQlMapper.selectList(wrapper);
        if (htdinfo!=null && !htdinfo.isEmpty()){
            for (JjgLqsJgQl jjgLqsJgQl : htdinfo) {
                String qlname = jjgLqsJgQl.getQlname();
                for (String ccxm : ccxm1) {
                   /* Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", qlname);
                    map.put("fbgc", "上部");
                    map.put("ccxm", ccxm);*/
                    JCSJVo jgjcsj = new JCSJVo();
                    jgjcsj.setHtd(htd);
                    jgjcsj.setDwgc(qlname);
                    jgjcsj.setFbgc("桥梁上部");
                    jgjcsj.setCcxm(ccxm);
                    result.add(jgjcsj);
                }
                for (String ccxm : ccxm2) {
                    /*Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", qlname);
                    map.put("fbgc", "下部");
                    map.put("ccxm", ccxm);*/
                    JCSJVo jgjcsj = new JCSJVo();
                    jgjcsj.setHtd(htd);
                    jgjcsj.setDwgc(qlname);
                    jgjcsj.setFbgc("桥梁下部");
                    jgjcsj.setCcxm(ccxm);
                    result.add(jgjcsj);
                }
                for (String ccxm : ccxm3) {
                    /*Map<String,Object> map = new HashMap<>();
                    map.put("htd", htd);
                    map.put("dwgc", qlname);
                    map.put("fbgc", "桥面系");
                    map.put("ccxm", ccxm);*/
                    JCSJVo jgjcsj = new JCSJVo();
                    jgjcsj.setHtd(htd);
                    jgjcsj.setDwgc(qlname);
                    jgjcsj.setFbgc("桥面系");
                    jgjcsj.setCcxm(ccxm);
                    result.add(jgjcsj);
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
    private List<JCSJVo> getJadata(String htd) {
        String[] ccxm1 = {"立柱竖直度", "标志板净空", "标志板厚度", "标志面反光膜等级及逆射光系数"};
        String[] ccxm2 = {"△反光标线逆反射系数", "△标线厚度"};
        String[] ccxm3 = {"*波形梁板基地金属厚度", "*波形梁钢护栏立柱壁厚","*波形梁钢护栏立柱埋入深度","*波形梁钢护栏横梁中心高度","*混凝土护栏强度","△砼护栏断面尺寸"};
        List<JCSJVo> result = new ArrayList<>();
        for (String ccxm : ccxm1) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "交通安全设施");
            map.put("fbgc", "标志");
            map.put("ccxm", ccxm);*/
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("交通安全设施");
            jgjcsj.setFbgc("标志");
            jgjcsj.setCcxm(ccxm);
            result.add(jgjcsj);
        }
        for (String ccxm : ccxm2) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "交通安全设施");
            map.put("fbgc", "标线");
            map.put("ccxm", ccxm);*/
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("交通安全设施");
            jgjcsj.setFbgc("标线");
            jgjcsj.setCcxm(ccxm);
            result.add(jgjcsj);
        }
        for (String ccxm : ccxm3) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "交通安全设施");
            map.put("fbgc", "防护栏");
            map.put("ccxm", ccxm);*/
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("交通安全设施");
            jgjcsj.setFbgc("防护栏");
            jgjcsj.setCcxm(ccxm);
            result.add(jgjcsj);
        }
        return result;

    }

    /**
     *
     * @param htd
     * @return
     */
    private List<JCSJVo> getLmdata(String htd) {
        List<JCSJVo> result = new ArrayList<>();
        String[] ccxm = {"△沥青路面压实度（路面面层）", "△沥青路面压实度（隧道路面）", "△沥青路面弯沉", "沥青路面车辙（路面面层）", "沥青路面车辙（隧道路面）",
                "沥青路面渗水系数（路面面层）","沥青路面渗水系数（隧道路面）","*混凝土路面强度","混凝土路面相邻板高差","平整度（沥青路面面层）","平整度（混凝土路面）","平整度（桥面系混凝土）",
                "平整度（桥面系沥青）","平整度（隧道路面混凝土）","平整度（隧道路面沥青）","构造深度（混凝土路面）","构造深度（沥青路面面层）","构造深度（桥面系混凝土）","构造深度（桥面系沥青）",
                "构造深度（隧道路面混凝土）","构造深度（隧道路面沥青）","摩擦系数（沥青路面面层）","摩擦系数（桥面系）","摩擦系数（隧道路面）","△厚度（混凝土路面）","△厚度（沥青路面）","△厚度（路面雷达法）"
                ,"△厚度（隧道路面混凝土）","△厚度（隧道路面沥青）","△厚度（隧道路面雷达法）","横坡（混凝土路面）","横坡（沥青路面）","横坡（桥面系混凝土）","横坡（桥面系沥青）","横坡（隧道路面混凝土）","横坡（隧道路面沥青）"};

        for (String s : ccxm) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路面工程");
            map.put("fbgc", "路面面层");
            map.put("ccxm", s);*/
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("路面工程");
            jgjcsj.setFbgc("路面面层");
            jgjcsj.setCcxm(s);
            result.add(jgjcsj);
        }
        return result;
    }

    /**
     *
     * @param htd
     * @return
     */
    private List<JCSJVo> getLjdata(String htd){
        String[] ccxm1 = {"△沉降", "△压实度（沙砾）", "△压实度（灰土）", "△弯沉", "边坡"};
        String[] ccxm2 = {"断面尺寸", "铺砌厚度"};
        String[] ccxm3 = {"*混凝土强度", "△断面尺寸"};
        String[] ccxm4 = {"*混凝土强度", "结构尺寸"};
        List<JCSJVo> result = new ArrayList<>();
        for (String ccxm : ccxm1) {
            //Map<String,Object> map = new HashMap<>();
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("路基工程");
            jgjcsj.setFbgc("路基土石方");
            jgjcsj.setCcxm(ccxm);
            /*map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "路基土石方");
            map.put("ccxm", ccxm);*/
            result.add(jgjcsj);
        }
        for (String ccxm : ccxm2) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "排水工程");
            map.put("ccxm", ccxm);*/
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("路基工程");
            jgjcsj.setFbgc("排水工程");
            jgjcsj.setCcxm(ccxm);
            result.add(jgjcsj);
        }
        for (String ccxm : ccxm3) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "小桥");
            map.put("ccxm", ccxm);*/
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("路基工程");
            jgjcsj.setFbgc("小桥");
            jgjcsj.setCcxm(ccxm);
            result.add(jgjcsj);
        }
        for (String ccxm : ccxm4) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "涵洞");
            map.put("ccxm", ccxm);*/
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("路基工程");
            jgjcsj.setFbgc("涵洞");
            jgjcsj.setCcxm(ccxm);
            result.add(jgjcsj);
        }
        for (String ccxm : ccxm3) {
            /*Map<String,Object> map = new HashMap<>();
            map.put("htd", htd);
            map.put("dwgc", "路基工程");
            map.put("fbgc", "支挡工程");
            map.put("ccxm", ccxm);*/
            JCSJVo jgjcsj = new JCSJVo();
            jgjcsj.setHtd(htd);
            jgjcsj.setDwgc("路基工程");
            jgjcsj.setFbgc("支挡工程");
            jgjcsj.setCcxm(ccxm);
            result.add(jgjcsj);
        }
        return result;

    }

}
