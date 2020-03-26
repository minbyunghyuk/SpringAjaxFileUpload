package org.zerock.controller;

import lombok.extern.log4j.Log4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.AttachFileDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@Log4j
public class UploadController {

    @GetMapping("/uploadForm")
    public void uploadFormLoad() {
        log.info("upload form");
    }

    @PostMapping("/uploadFormAction")
    public void uploadFormPost(MultipartFile[] uploadFile, Model model) {
        String UploadFolder = "D:\\upload";

        for (MultipartFile multipartfile : uploadFile) {
            log.info("-------------------------------------------------");
            log.info("upload File Name : " + multipartfile.getOriginalFilename());
            log.info("upload File size : " + multipartfile.getSize());
            log.info("upload file Content : " + multipartfile.getContentType());

            File savefile = new File(UploadFolder, multipartfile.getOriginalFilename());

            try {
                multipartfile.transferTo(savefile);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    @GetMapping("/uploadAjax")
    public void uploadAjax() {
        log.info("upload ajax");
    }

//    @PostMapping("/uploadAjaxAction")
//    public void uploadAjaxPost(MultipartFile[] uploadFile) {
//        log.info("update ajax post................");
//        String uploadFolder = "D:\\upload";
//        //make	folder
//        File uploadpath= new File(uploadFolder , getFolder());
//
//        log.info("uploadpath :" +uploadpath);
//
//        if(uploadpath.exists()==false) {
//            uploadpath.mkdirs();
//        }
//
//        // make yyyy/mm/dd folder
//
//        for (MultipartFile multipartFile : uploadFile) {
//            log.info("-----------------------------------------");
//            log.info("Upload File Name: " + multipartFile.getOriginalFilename());
//            log.info("Upload File Size : " + multipartFile.getSize());
//
//            String uploadFileName = multipartFile.getOriginalFilename();
//
//            // IE has file path
//            uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\") + 1);
//
//            log.info("only file name: " + uploadFileName);
//            //File saveFile = new File(uploadFolder, uploadFileName);
//            //기존 폴더를 직접지정한것에서 수정한 경로로 설정
//            UUID uuid = UUID.randomUUID();
//            uploadFileName = uuid.toString()+"_"+uploadFileName;
//            File saveFile = new  File(uploadpath,uploadFileName);
//
//            try {
//                multipartFile.transferTo(saveFile);
//                //transferTo ->  파일데이터를 지정한 파일로 저장
//
//                //만약 image type이면 섬네일 생성
//                if(checkImageType(saveFile))
//                {
//                    FileOutputStream thubail= new FileOutputStream(new File(uploadpath, "s_"+uploadFileName));
//                    Thumbnailator.createThumbnail(multipartFile.getInputStream(),thubail,100,100);
//                    thubail.close();
//
//                }
//            } catch (Exception e) {
//                log.error(e.getMessage());
//            } // end catch
//        } // end for
//    }

    //첨부파일의 문제 : 중복된이름의 파일처리 , 한폴더내의 너무많은 파일의생성
    //중복된파일의 처리 -> 현재시간을 밀리세컨드 까지 구분해서 파일이름을 생성 후 저장 , UUID를 이용해서 중복이 발생할 가능성이 거의없는 문자열생성 후 저장
    //한폴더내의 너무많은 파일의 생성 -> 하나의 폴더의 생성도리수있는 파일개수에대한문제 -> 속도저하 및 개수제한문제 -> yyyy/mm/dd단단위 폴더를 생성해서 저장

    @PostMapping(value = "/uploadAjaxAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile) {
        List<AttachFileDTO> list = new ArrayList<AttachFileDTO>();
        String uploadFodler = "D:\\upload";

        String uploadFolderPath = getFolder();
        File uploadPath = new File(uploadFodler, uploadFolderPath);
        if (uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }
        for(MultipartFile multipartFile : uploadFile){
            AttachFileDTO attachFileDTO = new AttachFileDTO();
            String uploadFileName= multipartFile.getOriginalFilename();

            // ID has File path
            uploadFileName=uploadFileName.substring(uploadFileName.lastIndexOf("//")+1);
            log.info("lastindexof");
            attachFileDTO.setFileName(uploadFileName);
            UUID uuid= UUID.randomUUID();
            uploadFileName= uuid.toString()+"_" + uploadFileName;

            try {
                File saveFile=new File(uploadPath,uploadFileName);
                multipartFile.transferTo(saveFile);

                attachFileDTO.setUuid(uuid.toString());
                attachFileDTO.setUploadPath(uploadFolderPath);
                //image file check
                if(checkImageType(saveFile)){
                    attachFileDTO.setImage(true);
                    System.out.println("11111True");
                    FileOutputStream thnumbnail = new FileOutputStream(new File(uploadPath, "s_"+uploadFileName));
                    Thumbnailator.createThumbnail(multipartFile.getInputStream(),thnumbnail,100,100);
                    thnumbnail.close();
                }
                list.add(attachFileDTO);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        return new ResponseEntity<List<AttachFileDTO>>(list, HttpStatus.OK) ;
    }

    @GetMapping("/display")
    @ResponseBody
    public ResponseEntity<byte[]> getFile(String fileName){
        log.info("fileName"+ fileName);
        File file = new File("D:\\upload\\"+fileName) ;
        log.info("file: "+ file);

        ResponseEntity<byte[]>result = null;
        try{
            HttpHeaders headers= new HttpHeaders();
            headers.add("Content-type",Files.probeContentType(file.toPath()));
            result = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }




    private String getFolder() {
        //오늘의 날짜경로를 문자열로 생성후 생성된경로가 폴더경로로 수정된뒤에 반환된다.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String str = sdf.format(date);
        return str.replace("-", File.separator);
    }


    private boolean checkImageType(File file) {
        try {
            String contentType = Files.probeContentType(file.toPath());
            return contentType.startsWith("image");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


}
