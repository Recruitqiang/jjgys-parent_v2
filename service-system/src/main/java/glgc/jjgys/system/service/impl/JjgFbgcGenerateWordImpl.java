package glgc.jjgys.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spire.doc.*;
import com.spire.doc.documents.*;
import com.spire.xls.collections.WorksheetsCollection;
import glgc.jjgys.model.project.*;
import glgc.jjgys.model.system.Project;
import glgc.jjgys.system.exception.JjgysException;
import glgc.jjgys.system.mapper.JjgFbgcGenerateWordMapper;
import glgc.jjgys.system.service.*;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.spire.doc.fields.TextRange;
import com.spire.xls.CellRange;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class JjgFbgcGenerateWordImpl extends ServiceImpl<JjgFbgcGenerateWordMapper, Object> implements JjgFbgcGenerateWordService {

    @Value(value = "${jjgys.path.filepath}")
    private String filespath;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JjgHtdService jjgHtdService;

    @Autowired
    private JjgLqsQlService jjgLqsQlService;

    @Autowired
    private JjgLqsSdService jjgLqsSdService;

    @Autowired
    private JjgLqsHntlmzdService jjgLqsHntlmzdService;

    @Autowired
    private JjgLqsLjxService jjgLqsLjxService;

    @Autowired
    private JjgLqsSfzService jjgLqsSfzService;

    @Autowired
    private JjgNyzlkfJgService jjgNyzlkfJgService;

    @Autowired
    private JjgWgjcService jjgWgjcService;

    @Override
    public void generateword(String proname) throws IOException {
        String excelFilePath = filespath+File.separator+proname+File.separator+"报告中表格.xlsx";
        File excelf = new File(excelFilePath);
        if (!excelf.exists()){
            throw new JjgysException(20001,"报告中表格不存在,请先生成");
        }
        File f = new File(filespath + File.separator + proname + File.separator + "报告.docx");
        File fdir = new File(filespath + File.separator + proname);
        if (!fdir.exists()) {
            //创建文件根目录
            fdir.mkdirs();
        }
        try {
            //替换项目全称
            QueryWrapper<Project> wrapper = new QueryWrapper<>();
            wrapper.eq("proname", proname);
            Project pro = projectService.getOne(wrapper);

            Document xw = new Document("service-system/src/main/resources/static" + File.separator + "报告.docx");
            xw.replace("{项目全称}", pro.getXmqc(), false, true);
            xw.saveToFile(f.getPath(), FileFormat.Docx_2013);

            //表1.3-1 合同段划分及施工单位、监理单位一览表
            CreateTableData(f, xw, proname);

            //外观检查结果（文字描述）
            CreateTablewgjclwzms(f, xw, proname);


            //读取Excel文件
            Workbook workbook = new Workbook();
            workbook.loadFromFile(excelFilePath);
            WorksheetsCollection sheetnum = workbook.getWorksheets();
            for (int j = 0; j < sheetnum.getCount() - 1; j++) {
                String sheetname = sheetnum.get(j).getName();
                Worksheet sheet = workbook.getWorksheets().get(j);
                boolean ishidden = JjgFbgcCommonUtils.ishidden(excelFilePath, sheetname);
                if (!ishidden) {
                    CellRange allocatedRange = sheet.getAllocatedRange();
                    String s = allocatedRange.get(1, 1).getDisplayedText();
                    log.info("表名：{}", s);
                    TextSelection[] selection = xw.findAllString(s, true, true);
                    System.out.println(selection);
                    if (selection == null) {
                        System.out.println("不存在的表格:" + s);
                        continue;
                    }
                    for (int k = 0; k < selection.length; k++) {
                        TextRange range = selection[k].getAsOneRange();
                        //删除没用数据的行
                        CellRange dataRange = RemoveEmptyRows(sheet, allocatedRange);
                        //复制到Word文档
                        log.info("开始复制{}中的数据到word中", sheetname);
                        log.info("此处共{}处匹配，目前第{}处", selection.length, k + 1);
                        copyToWord(dataRange, xw, f.getPath(), range);
                        log.info("{}中的数据复制完成", sheetname);
                    }
                }
            }

            //外观检查
            CreateTablewgjcl(f, xw, proname);

            //内页资料
            CreateTablenyzl(f, xw, proname);

        } catch (Exception e) {
            if (f.exists()) {
                f.delete();
            }
            throw new JjgysException(20001, "生成报告错误，请检查数据的正确性");
        }

    }

    /**
     *
     * @param f
     * @param xw
     * @param proname
     */
    private void CreateTablewgjclwzms(File f, Document xw, String proname) {
        QueryWrapper<JjgWgjc> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.orderByAsc("htd");
        List<JjgWgjc> list = jjgWgjcService.list(wrapper);
        if (list != null){
            List<JjgWgjc> ljgc = new ArrayList<>();
            List<JjgWgjc> sdgc = new ArrayList<>();
            List<JjgWgjc> qlgc = new ArrayList<>();

            for (JjgWgjc jjgWgjc : list) {
                String dwgc = jjgWgjc.getDwgc();
                if (dwgc.contains("路基工程")){
                    ljgc.add(jjgWgjc);

                }else if (dwgc.contains("隧道工程")){
                    sdgc.add(jjgWgjc);

                }else if (dwgc.contains("桥梁工程")){
                    qlgc.add(jjgWgjc);
                }
            }
            //按分部工程分类
            Map<String, List<JjgWgjc>> groupedData = ljgc.stream()
                    .collect(Collectors.groupingBy(JjgWgjc::getFbgc));
            if (groupedData != null){
                groupedData.forEach((fbgc, ljlist) -> {
                    System.out.println("Key: " + fbgc);//路基土石方
                    if (fbgc.contains("路基土石方")){
                        String tsf = extracted(list, fbgc, ljlist);
                        xw.replace("${路基土石方}", tsf, false, true);
                        xw.saveToFile(f.getPath(), FileFormat.Docx_2013);

                    }else if (fbgc.contains("排水工程")){
                        String ps = extracted(list, fbgc, ljlist);
                        xw.replace("${排水工程}", ps, false, true);
                        xw.saveToFile(f.getPath(), FileFormat.Docx_2013);

                    }else if (fbgc.contains("小桥")){
                        String xq = extracted(list, fbgc, ljlist);
                        xw.replace("${小桥}", xq, false, true);
                        xw.saveToFile(f.getPath(), FileFormat.Docx_2013);

                    }else if (fbgc.contains("涵洞")){
                        String hd = extracted(list, fbgc, ljlist);
                        xw.replace("${涵洞}", hd, false, true);
                        xw.saveToFile(f.getPath(), FileFormat.Docx_2013);

                    }else if (fbgc.contains("支挡工程")){
                        String zd = extracted(list, fbgc, ljlist);
                        xw.replace("${支挡工程}", zd, false, true);
                        xw.saveToFile(f.getPath(), FileFormat.Docx_2013);

                    }
                });
            }
        }
    }

    /**
     *
     * @param list
     * @param fbgc
     * @param ljlist
     * @return
     */
    private String extracted(List<JjgWgjc> list, String fbgc, List<JjgWgjc> ljlist) {
        StringBuilder ljtsfBuilder = new StringBuilder();
        //还要根据构件名称分一下
        Map<String, List<JjgWgjc>> groupedljlist = ljlist.stream()
                .collect(Collectors.groupingBy(JjgWgjc::getGjmc));
        if (groupedljlist!=null){
            groupedljlist.forEach((gjmc, list1) -> {
                int i = 1;
                ljtsfBuilder.append(i+")个别").append(gjmc);
                String bhlxms = "";
                for (JjgWgjc jjgWgjc : list1) {
                    bhlxms = jjgWgjc.getBhlx()+"、";
                }
                ljtsfBuilder.append(",有").append(bhlxms).append("现象");
                //查询一下一共有几个合同段
                Set<String> htd = new HashSet<>();
                for (JjgWgjc jjgWgjc : list) {
                    String fbgc1 = jjgWgjc.getFbgc();
                    String gjmc1 = jjgWgjc.getGjmc();
                    if (fbgc1.equals(fbgc) && gjmc1.equals(gjmc)){
                        htd.add(jjgWgjc.getHtd());
                    }
                }
                ljtsfBuilder.append(",共计").append(htd.size()).append("个合同段");

                ljtsfBuilder.append("。如").append(list1.get(0).getHtd()).append("标").append(list1.get(0).getGjbh()).
                        append(list1.get(0).getBhlx()).append(list1.get(0).getBhsl()).append("条，").append(list1.get(0).getBhdl()).append("。");
                i++;
            });
        }
        String ljtsf = ljtsfBuilder.toString();
        return ljtsf;
    }


    /**
     *
     * @param f
     * @param xw
     * @param proname
     */
    private void CreateTablewgjcl(File f, Document xw, String proname) {
        QueryWrapper<JjgWgjc> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.orderByAsc("htd");
        List<JjgWgjc> list = jjgWgjcService.list(wrapper);

        //按单位工程分类
        if (list != null){
            List<JjgWgjc> ljgc = new ArrayList<>();
            List<JjgWgjc> lmgc = new ArrayList<>();
            List<JjgWgjc> sdgc = new ArrayList<>();
            List<JjgWgjc> qlgc = new ArrayList<>();
            List<JjgWgjc> jagc = new ArrayList<>();
            for (JjgWgjc jjgWgjc : list) {
                String dwgc = jjgWgjc.getDwgc();
                if (dwgc.contains("路基工程")){
                    ljgc.add(jjgWgjc);

                }else if (dwgc.contains("隧道工程")){
                    sdgc.add(jjgWgjc);

                }else if (dwgc.contains("路面工程")){
                    lmgc.add(jjgWgjc);

                }else if (dwgc.contains("桥梁工程")){
                    qlgc.add(jjgWgjc);

                }else if (dwgc.contains("交通安全设施工程")){
                    jagc.add(jjgWgjc);

                }
            }
            String lj = "${路基外观检查}";
            String lm = "${路面外观检查}";
            String ql = "${桥梁工程外观检查}";
            String sd = "${隧道工程外观检查}";
            String ja = "${交通安全设施工程外观检查}";
            TextSelection textSelectionlj = xw.findString(lj, true, true);
            TextSelection textSelectionlm = xw.findString(lm, true, true);
            TextSelection textSelectionql = xw.findString(ql, true, true);
            TextSelection textSelectionsd = xw.findString(sd, true, true);
            TextSelection textSelectionja = xw.findString(ja, true, true);

            List<TextSelection> selection = new ArrayList<>();
            selection.add(textSelectionlj);
            selection.add(textSelectionlm);
            selection.add(textSelectionql);
            selection.add(textSelectionsd);
            selection.add(textSelectionja);

            String[] header = {"合同段", "单位工程", "分部工程","外观检查(病害描述)","备注"};
            if (selection != null){
                for (TextSelection textSelection : selection) {
                    if (textSelection != null){
                        Table table = new Table(xw, true);

                        table.resetCells(ljgc.size() + 1, header.length);
                        Paragraph paragraph = textSelection.getAsOneRange().getOwnerParagraph();
                        Body body = paragraph.ownerTextBody();
                        int index = body.getChildObjects().indexOf(paragraph);

                        //将第一行设置为表格标题
                        TableRow row = table.getRows().get(0);
                        row.isHeader(true);
                        row.setHeight(40);
                        row.setHeightType(TableRowHeightType.Exactly);
                        for (int i = 0; i < header.length; i++) {
                            row.getCells().get(i).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                            Paragraph p = row.getCells().get(i).addParagraph();
                            p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
                            TextRange txtRange = p.appendText(header[i]);
                            txtRange.getCharacterFormat().setBold(true);
                        }
                        //添加数据
                        for (int rowIdx = 0; rowIdx < ljgc.size(); rowIdx++) {
                            TableRow row1 = table.getRows().get(rowIdx + 1); // 第一行已经是表头，所以从第二行开始添加数据
                            row1.setHeightType(TableRowHeightType.Exactly);

                            JjgWgjc rowData = ljgc.get(rowIdx);

                            for (int colIdx = 0; colIdx < header.length; colIdx++) {
                                row1.getCells().get(colIdx).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                                Paragraph p = row1.getCells().get(colIdx).addParagraph();
                                p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
                                // 根据表头的不同，设置相应的数据
                                switch (colIdx) {
                                    case 0:
                                        p.appendText(rowData.getHtd());
                                        break;
                                    case 1:
                                        p.appendText(rowData.getDwgc());
                                        break;
                                    case 2:
                                        p.appendText(rowData.getFbgc());
                                        break;
                                    case 3:
                                        p.appendText(rowData.getBhms());
                                        break;
                                    case 4:
                                        p.appendText(rowData.getBz());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        body.getChildObjects().remove(paragraph);
                        body.getChildObjects().insert(index, table);
                        //列宽自动适应内容
                        table.autoFit(AutoFitBehaviorType.Auto_Fit_To_Contents);
                        xw.saveToFile(f.getPath(), FileFormat.Docx_2013);
                    }
                }


            }

        }


    }

    /**
     *
     * @param f
     * @param xw
     * @param proname
     */
    private void CreateTablenyzl(File f, Document xw, String proname) {
        QueryWrapper<JjgNyzlkfJg> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        wrapper.orderByAsc("htd");
        List<JjgNyzlkfJg> list = jjgNyzlkfJgService.list(wrapper);

        String[] header = {"合同段", "存在问题", "扣分"};

        String s = "${附表19内业资料}";
        TextSelection textSelection = xw.findString(s, true, true);
        // 检查是否找到字符串
        if (textSelection != null) {
            Table table = new Table(xw, true);

            table.resetCells(list.size() + 1, header.length);
            Paragraph paragraph = textSelection.getAsOneRange().getOwnerParagraph();
            Body body = paragraph.ownerTextBody();
            int index = body.getChildObjects().indexOf(paragraph);

            //将第一行设置为表格标题
            TableRow row = table.getRows().get(0);
            row.isHeader(true);
            row.setHeight(40);
            row.setHeightType(TableRowHeightType.Exactly);
            for (int i = 0; i < header.length; i++) {
                row.getCells().get(i).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                Paragraph p = row.getCells().get(i).addParagraph();
                p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
                TextRange txtRange = p.appendText(header[i]);
                txtRange.getCharacterFormat().setBold(true);
            }
            //添加数据
            for (int rowIdx = 0; rowIdx < list.size(); rowIdx++) {
                TableRow row1 = table.getRows().get(rowIdx + 1); // 第一行已经是表头，所以从第二行开始添加数据
                row1.setHeightType(TableRowHeightType.Exactly);

                JjgNyzlkfJg rowData = list.get(rowIdx);

                for (int colIdx = 0; colIdx < header.length; colIdx++) {
                    row1.getCells().get(colIdx).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                    Paragraph p = row1.getCells().get(colIdx).addParagraph();
                    p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);

                    // 根据表头的不同，设置相应的数据
                    switch (colIdx) {
                        case 0:
                            p.appendText(rowData.getHtd());
                            break;
                        case 1:
                            p.appendText(rowData.getWt());
                            break;
                        case 2:
                            p.appendText(rowData.getKf());
                            break;
                        default:
                            break;
                    }
                }
            }

            body.getChildObjects().remove(paragraph);
            body.getChildObjects().insert(index, table);
            //列宽自动适应内容
            table.autoFit(AutoFitBehaviorType.Auto_Fit_To_Contents);
            xw.saveToFile(f.getPath(), FileFormat.Docx_2013);
        }


    }


    /**
     *
     * @param f
     * @param xw
     * @param proname
     */
    private void CreateTableData(File f, Document xw, String proname) {
        //获取数据
        QueryWrapper<JjgHtd> wrapper = new QueryWrapper<>();
        wrapper.eq("proname",proname);
        List<JjgHtd> list = jjgHtdService.list(wrapper);
        List<Map<String,Object>> htdlist = new ArrayList<>();
        if (list != null){
            for (int i = 0; i < list.size(); i++) {
                Map<String,Object> map = new HashMap<>();
                map.put("xh",i+1);
                map.put("htd",list.get(i).getName());
                map.put("zh",list.get(i).getZhq()+"~"+list.get(i).getZhz());
                map.put("tze",list.get(i).getTze());
                map.put("sgdw",list.get(i).getSgdw());
                map.put("jldw",list.get(i).getJldw());
                String lx = list.get(i).getLx();
                String gcnr = "";
                if (lx.contains("桥梁工程")){
                    QueryWrapper<JjgLqsQl> wrapperql = new QueryWrapper<>();
                    wrapperql.eq("proname",proname);
                    wrapperql.eq("htd",list.get(i).getName());
                    List<JjgLqsQl> list1 = jjgLqsQlService.list(wrapperql);
                    if (list1 != null){
                        gcnr = "桥梁:";
                        for (JjgLqsQl jjgLqsQl : list1) {
                            String qlname = jjgLqsQl.getQlname();
                            gcnr+=qlname+"、";
                        }
                    }

                }
                if (lx.contains("隧道工程")){
                    QueryWrapper<JjgLqsSd> wrappersd = new QueryWrapper<>();
                    wrappersd.eq("proname",proname);
                    wrappersd.eq("htd",list.get(i).getName());
                    List<JjgLqsSd> list1 = jjgLqsSdService.list(wrappersd);
                    if (list1 != null){
                        gcnr = "隧道：";
                        for (JjgLqsSd jjgLqsSd : list1) {
                            String sdname = jjgLqsSd.getSdname();
                            gcnr+=sdname+"、";
                        }
                    }

                }
                if (lx.contains("路面工程")){
                    gcnr += "路面工程面层";
                }
                if (lx.contains("交安工程")){
                    gcnr += "标志、标线、护栏、隔离封闭";
                }
                //匝道
                QueryWrapper<JjgLqsHntlmzd> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("proname",proname);
                queryWrapper.eq("htd",list.get(i).getName());
                List<JjgLqsHntlmzd> list1 = jjgLqsHntlmzdService.list(queryWrapper);
                if (list1!=null){
                    for (JjgLqsHntlmzd jjgLqsHntlmzd : list1) {
                        String hntlmname = jjgLqsHntlmzd.getWz();
                        gcnr += hntlmname+"、";
                    }
                }
                //连接线
                QueryWrapper<JjgLjx> ljxWrapper = new QueryWrapper<>();
                ljxWrapper.eq("proname",proname);
                ljxWrapper.eq("htd",list.get(i).getName());
                List<JjgLjx> list2 = jjgLqsLjxService.list(ljxWrapper);
                if (list2!=null){
                    for (JjgLjx ljx : list2) {
                        String name = ljx.getLjxname();
                        gcnr += name+"、";
                    }
                }
                //收费站
                QueryWrapper<JjgSfz> sfzWrapper = new QueryWrapper<>();
                sfzWrapper.eq("proname",proname);
                sfzWrapper.eq("htd",list.get(i).getName());
                List<JjgSfz> list3 = jjgLqsSfzService.list(sfzWrapper);
                if (list3!=null){
                    for (JjgSfz jjgSfz : list3) {
                        String name = jjgSfz.getZdsfzname();
                        gcnr += name+"、";
                    }
                }

                map.put("gcnr",gcnr);
                htdlist.add(map);
            }
        }
        String[] header = {"序号", "施工合同段", "起讫（中心）桩号", "合同造价（万元）", "施工单位","监理单位","主要工程内容"};

        String s = "${表1.3-1一览表}";
        TextSelection textSelection = xw.findString(s, true, true);
        // 检查是否找到字符串
        if (textSelection != null)
        {
            Table table=new Table(xw,true);

            table.resetCells(htdlist.size()+1, header.length);
            Paragraph paragraph=textSelection.getAsOneRange().getOwnerParagraph();
            Body body=paragraph.ownerTextBody();
            int index=body.getChildObjects().indexOf(paragraph);

            //将第一行设置为表格标题
            TableRow row = table.getRows().get(0);
            row.isHeader(true);
            row.setHeight(50);
            row.setHeightType(TableRowHeightType.Exactly);
            for (int i = 0; i < header.length; i++) {
                //row.getCells().get(i).setWidth(400);
                row.getCells().get(i).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                Paragraph p = row.getCells().get(i).addParagraph();
                p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
                TextRange txtRange = p.appendText(header[i]);
                txtRange.getCharacterFormat().setBold(true);
            }
            //添加数据
            for (int rowIdx = 0; rowIdx < htdlist.size(); rowIdx++) {
                TableRow row1 = table.getRows().get(rowIdx+1); // 第一行已经是表头，所以从第二行开始添加数据
                //row1.setHeight(80);
                row1.setHeightType(TableRowHeightType.Exactly);

                Map<String, Object> rowData = htdlist.get(rowIdx);

                for (int colIdx = 0; colIdx < header.length; colIdx++) {
                    /*if (colIdx == 0){
                        row1.getCells().get(colIdx).setWidth(200);
                    }else if (colIdx == 1 || colIdx == 2 || colIdx == 3){
                        row1.getCells().get(colIdx).setWidth(300);
                    }else if (colIdx == 4 || colIdx == 5 ){
                        row1.getCells().get(colIdx).setWidth(400);
                    }else if (colIdx == 6 ){
                        row1.getCells().get(colIdx).setWidth(600);
                    }*/

                    row1.getCells().get(colIdx).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                    Paragraph p = row1.getCells().get(colIdx).addParagraph();
                    p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);

                    // 根据表头的不同，设置相应的数据
                    switch (colIdx) {
                        case 0:
                            p.appendText(rowData.get("xh").toString());
                            break;
                        case 1:
                            p.appendText(rowData.get("htd").toString());
                            break;
                        case 2:
                            p.appendText(rowData.get("zh").toString());
                            break;
                        case 3:
                            p.appendText(rowData.get("tze").toString());
                            break;
                        case 4:
                            p.appendText(rowData.get("sgdw").toString());
                            break;
                        case 5:
                            p.appendText(rowData.get("jldw").toString());
                            break;
                        case 6:
                            p.appendText(rowData.get("gcnr").toString());
                            break;
                        default:
                            break;
                    }

                }
            }

            body.getChildObjects().remove(paragraph);
            body.getChildObjects().insert(index,table);
            //列宽自动适应内容
            table.autoFit(AutoFitBehaviorType.Auto_Fit_To_Contents);
            xw.saveToFile(f.getPath(), FileFormat.Docx_2013);
        }
    }

    /**
     *
     * @param sheet
     * @param allocatedRange
     * @return
     */
    private CellRange RemoveEmptyRows(Worksheet sheet, CellRange allocatedRange) {
        CellRange dataRange = null;
        for (int i = allocatedRange.getLastRow(); i >= 1; i--) {
            boolean isRowEmpty = true;
            for (int col = 1; col <= allocatedRange.getLastColumn(); col++) {
                if (!sheet.getCellRange(i, col).getText().isEmpty()) {
                    isRowEmpty = false;
                    break;
                }
            }
            if (isRowEmpty) {
                sheet.deleteRow(i);
            } else {
                dataRange = sheet.getCellRange(1, 1, i, allocatedRange.getLastColumn());
                break;
            }
        }
        return dataRange;
    }



    /**
     * @param cell
     * @param doc
     * @param path
     */
    private static void copyToWord(CellRange cell, Document doc, String path,TextRange range) {
        //添加表格
        Table table=new Table(doc,true);
        table.resetCells(cell.getRowCount()-1, cell.getColumnCount());
        Paragraph paragraph=range.getOwnerParagraph();
        Body body=paragraph.ownerTextBody();
        int index=body.getChildObjects().indexOf(paragraph);

        for (int r = 2; r <= cell.getRowCount(); r++) {
            for (int c = 1; c <= cell.getLastColumn(); c++) {
                CellRange xCell = cell.get(r, c);
                CellRange mergeArea = xCell.getMergeArea();
                //合并单元格
                if (mergeArea != null && mergeArea.getRow() == r && mergeArea.getColumn() == c) {
                    int rowIndex = mergeArea.getRow();
                    int columnIndex = mergeArea.getColumn();
                    int rowCount = mergeArea.getRowCount();
                    int columnCount = mergeArea.getColumnCount();
                    for (int m = 0; m < rowCount; m++) {
                        table.applyHorizontalMerge(rowIndex - 2 + m, columnIndex - 1, columnIndex + columnCount - 2);
                    }
                    table.applyVerticalMerge(columnIndex - 1, rowIndex - 2, rowIndex + rowCount - 3);
                }
                //复制内容
                TableCell wCell = table.getRows().get(r - 2).getCells().get(c - 1);
                if (!xCell.getDisplayedText().isEmpty()) {
                    range=wCell.addParagraph().appendText(xCell.getDisplayedText());
                    //TextRange textRange = wCell.addParagraph().appendText(xCell.getDisplayedText());
                    copyStyle(range, xCell, wCell);
                } else {
                    wCell.getCellFormat().setBackColor(xCell.getStyle().getColor());
                }
            }
        }
        body.getChildObjects().remove(paragraph);
        body.getChildObjects().insert(index,table);
        doc.saveToFile(path,com.spire.doc.FileFormat.Docx_2013);
    }



    /**
     *
     * @param wTextRange
     * @param xCell
     * @param wCell
     */
    private static void copyStyle(TextRange wTextRange, CellRange xCell, TableCell wCell) {
        //复制字体样式
        wTextRange.getCharacterFormat().setTextColor(xCell.getStyle().getFont().getColor());
        wTextRange.getCharacterFormat().setFontSize((float) xCell.getStyle().getFont().getSize());
        wTextRange.getCharacterFormat().setFontName(xCell.getStyle().getFont().getFontName());
        wTextRange.getCharacterFormat().setBold(xCell.getStyle().getFont().isBold());
        wTextRange.getCharacterFormat().setItalic(xCell.getStyle().getFont().isItalic());
        //复制背景色
        wCell.getCellFormat().setBackColor(xCell.getStyle().getColor());
        //复制排列方式
        switch (xCell.getHorizontalAlignment()) {
            case Left:
                wTextRange.getOwnerParagraph().getFormat().setHorizontalAlignment(HorizontalAlignment.Left);
                break;
            case Center:
                wTextRange.getOwnerParagraph().getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
                break;
            case Right:
                wTextRange.getOwnerParagraph().getFormat().setHorizontalAlignment(HorizontalAlignment.Right);
                break;
            default:
                break;
        }
        switch (xCell.getVerticalAlignment()) {
            case Bottom:
                wCell.getCellFormat().setVerticalAlignment(VerticalAlignment.Bottom);
                break;
            case Center:
                wCell.getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
                break;
            case Top:
                wCell.getCellFormat().setVerticalAlignment(VerticalAlignment.Top);
                break;
            default:
                break;
        }
    }

}
