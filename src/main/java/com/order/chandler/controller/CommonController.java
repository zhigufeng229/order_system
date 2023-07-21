package com.order.chandler.controller;

import com.order.chandler.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * 处理上传、下载文件
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${order.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file  这个名字不能更改
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file 是一个临时文件，需要转存到特定位置，否则在请求结束后会被删除
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称被重复名称覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);

        //判断目录是否存在，如果不存在就创建
        if (!dir.exists()){
            dir.mkdirs();
        }


        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 下载文件
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流，读取图片文件
            FileInputStream inputStream = new FileInputStream(new File(basePath + name));

            //输出流，返回读取到的图片文件
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1){
                outputStream.write(buf, 0, len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
