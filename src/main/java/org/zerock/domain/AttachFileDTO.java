package org.zerock.domain;

import lombok.Data;

@Data
public class AttachFileDTO {
    //첨부파일의 정보 목록
    private String fileName;
    private String uploadPath;
    private String uuid;
    private boolean image;
}
