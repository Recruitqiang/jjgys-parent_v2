package glgc.jjgys.system.service.impl;

import com.alibaba.excel.EasyExcel;
import glgc.jjgys.common.excel.ExcelUtil;
import glgc.jjgys.model.project.JjgFbgcQlgcZdhpzd;
import glgc.jjgys.model.project.JjgZdhPzd;
import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;
import glgc.jjgys.model.projectvo.qlgc.JjgFbgcQlgcZdhpzdVo;
import glgc.jjgys.model.projectvo.zdh.JjgZdhPzdVo;
import glgc.jjgys.system.easyexcel.ExcelHandler;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcQlgcZdhpzdMapper;
import glgc.jjgys.system.service.JjgFbgcQlgcZdhpzdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import java.util.*;

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
    public List<Map<String, Object>> lookJdbjg(CommonInfoVo commonInfoVo) {
        return null;
    }

    @Override
    public void generateJdb(CommonInfoVo commonInfoVo) throws IOException {
        String proname = commonInfoVo.getProname();
        String htd = commonInfoVo.getHtd();
        List<Map<String,Object>> lxlist = jjgFbgcQlgcZdhpzdMapper.selectlx(proname,htd);
        for (Map<String, Object> map : lxlist) {
            String zx = map.get("qlname").toString();
            int num = jjgFbgcQlgcZdhpzdMapper.selectcdnum(proname,htd,zx);
            int cds = 0;
            if (num == 1){
                cds = 2;
            }else {
                cds=num;
            }
            handlezxData(proname,htd,zx,cds,commonInfoVo.getSjz());
        }

    }

    /**
     *
     * @param proname
     * @param htd
     * @param zx
     * @param cdsl
     * @param sjz
     */
    private void handlezxData(String proname, String htd, String zx, int cdsl, String sjz) throws IOException {

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

        List<Map<String,Object>> datazf = jjgFbgcQlgcZdhpzdMapper.selectzfList(proname,htd,zx,result);
        List<Map<String,Object>> datayf = jjgFbgcQlgcZdhpzdMapper.selectyfList(proname,htd,zx,result);

        List<Map<String, Object>> lmzfList = montageIRI(datazf);
        List<Map<String, Object>> lmyfList = montageIRI(datayf);

        double zdzh = Double.parseDouble(lmzfList.get(0).get("qdzh").toString());
        double finzdzh = Double.parseDouble(lmzfList.get(lmzfList.size()-1).get("qdzh").toString());
        List<Map<String, Object>> lmzf = decrementNumberByStep(zdzh,finzdzh,lmzfList,cdsl);

        double yzdzh = Double.parseDouble(lmyfList.get(0).get("qdzh").toString());
        double yfinzdzh = Double.parseDouble(lmyfList.get(lmyfList.size()-1).get("qdzh").toString());
        List<Map<String, Object>> lmyf = decrementNumberByStep(yzdzh,yfinzdzh,lmyfList,cdsl);

        writeExcelData(proname,htd,lmzf,lmyf,cdsl,sjz,zx);
    }

    private void writeExcelData(String proname, String htd, List<Map<String, Object>> lmzfList, List<Map<String, Object>> lmyfList, int cdsl, String sjz, String zx) throws IOException {
        XSSFWorkbook wb = null;
        String fname="33桥面平整度-"+zx+".xlsx";
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
            /*List<Map<String,Object>> addList = addMissingData(lmzfList,cdsl);
            String sheetmame = "";
            if (zx.equals("主线")){
                sheetmame = "沥青路面";
                DBtoExcelLM(proname,htd,addList,sdqlData,wb,sheetmame,cdsl,sjz,zx);
            }else {
                sheetmame = "沥青匝道";
                DBtoExcelZD(proname,htd,addList,sdqlData,wb,sheetmame,cdsl,sjz,zx);
            }*/

        }

    }


    /**
     * 添加桩号
     * @param zdzh
     * @param finzdzh
     * @param mapList
     * @return
     */
    private List<Map<String, Object>> decrementNumberByStep(double zdzh,double finzdzh, List<Map<String, Object>> mapList,int cds) {
        int cdsl = cds * 2;
        String iri = "-";
        StringBuilder iriBuilder = new StringBuilder();
        for (int i = 0; i < cdsl; i++) {
            if (i == cdsl - 1) {
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
            map.put("name",mapList.get(0).get("name"));
            map.put("pzlx",mapList.get(0).get("pzlx"));
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
            map.put("name",mapList.get(0).get("name"));
            map.put("pzlx",mapList.get(0).get("pzlx"));
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
                String yiri = "";
                if (map.get("ziri") == null){
                    ziri = "-";
                }else {
                    ziri = map.get("ziri").toString();
                }
                if (map.get("yiri") == null){
                    yiri = "-";
                }else {
                    yiri = map.get("yiri").toString();
                }
                if (resultMapz.containsKey(qdzh)) {
                    resultMapz.get(qdzh).add(ziri);
                    resultMapz.get(qdzh).add(yiri);
                } else {
                    List<String> sfcList = new ArrayList<>();
                    sfcList.add(ziri);
                    sfcList.add(yiri);
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
}
