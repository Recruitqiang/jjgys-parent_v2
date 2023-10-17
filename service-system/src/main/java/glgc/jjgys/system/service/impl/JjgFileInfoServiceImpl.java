package glgc.jjgys.system.service.impl;

import glgc.jjgys.model.project.JjgFileInfo;
import glgc.jjgys.system.mapper.JjgFileInfoMapper;
import glgc.jjgys.system.service.JjgFileInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import glgc.jjgys.system.utils.JjgFbgcCommonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * 文件资源表 服务实现类
 * </p>
 *
 * @author wq
 * @since 2023-10-15
 */
@Service
public class JjgFileInfoServiceImpl extends ServiceImpl<JjgFileInfoMapper, JjgFileInfo> implements JjgFileInfoService {

    @Value(value = "${jjgys.path.file}")
    private String filepath;

    @Override
    public List<JjgFileInfo> getfilelist() {
        List<JjgFileInfo> fileList = new ArrayList<>();
        File directory = new File(filepath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                JjgFileInfo jjgFileInfo = new JjgFileInfo();
                jjgFileInfo.setPath(file.getAbsolutePath());
                jjgFileInfo.setName(file.getName());
                jjgFileInfo.setFileName(file.getName());
                jjgFileInfo.setIsFile(file.isFile());
                jjgFileInfo.setIsDir(file.isDirectory());
                if (file.isDirectory()) {
                    jjgFileInfo.setChildren(getChildFiles(file.getAbsolutePath()));
                }
                fileList.add(jjgFileInfo);
            }
        }
        return fileList;

    }

    @Override
    public void download(HttpServletResponse response, List<JjgFileInfo> downloadPath) throws IOException {
        if (downloadPath.size() == 1 && downloadPath.get(0).getIsDir()){
            File file = new File(downloadPath.get(0).getPath());
            String zipFileName = downloadPath.get(0).getName() + ".zip";
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFileName));
            zipDirectory(file, downloadPath.get(0).getName(), zipOut);
            zipOut.close();
        } else {
            // Download file
            if (downloadPath.size() == 1){
                // Download single file
                JjgFbgcCommonUtils.download(response,downloadPath.get(0).getPath(),downloadPath.get(0).getName());

                /*File file = new File(downloadPath.get(0).getPath());
                FileInputStream fileIn = new FileInputStream(file);
                fileIn.close();*/
            }else {
                downloadBatch(downloadPath);
            }
        }

    }

    /**
     *
     * @param files
     * @throws IOException
     */
    public void downloadBatch(List<JjgFileInfo> files) throws IOException {
        String zipFileName = "文件.zip";
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFileName));

        byte[] buffer = new byte[4096];
        for (JjgFileInfo fileInfo : files) {
            if (fileInfo.getIsDir()) {
                zipDirectory(new File(fileInfo.getPath()), fileInfo.getFileName(), zipOut);
            } else {
                File file = new File(fileInfo.getPath());
                FileInputStream fileIn = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(fileInfo.getFileName());
                zipOut.putNextEntry(zipEntry);

                int length;
                while ((length = fileIn.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, length);
                }

                fileIn.close();
            }
        }
        zipOut.close();

    }

    /**
     *
     * @param folder
     * @param parentFolder
     * @param zipOut
     * @throws IOException
     */
    private void zipDirectory(File folder, String parentFolder, ZipOutputStream zipOut) throws IOException {
        byte[] buffer = new byte[4096];
        File[] files = folder.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zipOut);
                continue;
            }

            FileInputStream fileIn = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
            zipOut.putNextEntry(zipEntry);

            int length;
            while ((length = fileIn.read(buffer)) > 0) {
                zipOut.write(buffer, 0, length);
            }

            fileIn.close();
        }
    }

    /**
     *
     * @param directoryPath
     * @return
     */
    private List<JjgFileInfo> getChildFiles(String directoryPath) {
        List<JjgFileInfo> fileList = new ArrayList<>();
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                JjgFileInfo jjgFileInfo = new JjgFileInfo();
                jjgFileInfo.setPath(file.getAbsolutePath());
                jjgFileInfo.setName(file.getName());
                jjgFileInfo.setFileName(file.getName());
                jjgFileInfo.setIsFile(file.isFile());
                jjgFileInfo.setIsDir(file.isDirectory());
                if (file.isDirectory()) {
                    jjgFileInfo.setChildren(getChildFiles(file.getAbsolutePath()));
                }
                fileList.add(jjgFileInfo);
            }
        }
        return fileList;
    }
}
