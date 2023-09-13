package glgc.jjgys.system.service;

import glgc.jjgys.model.projectvo.ljgc.CommonInfoVo;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lhy
 * @since 2023-07-01
 */
public interface JjgFbgcLjgcService {

    void exportljgc(HttpServletResponse response,String workpath) ;
    void importljgc( CommonInfoVo commonInfoVo,String workpath) ;
    void generateJdb(CommonInfoVo commonInfoVo) throws Exception;
    void download(HttpServletResponse response,String filename,String workpath);


    void getFile(String s, String htd);
}