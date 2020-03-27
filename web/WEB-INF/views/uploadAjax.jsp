<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <link rel="shortcut icon" href="#">
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
    <title>Ajax upload</title>
</head>

<body>




<h1>uplaod Ajax</h1>

<div class='uploadDiv'>
    <input type='file' name='uploadFile' multiple>
</div>

<div class='uploadResult'>
    <ul>

    </ul>

</div>

<button id='uploadBtn'>upload</button>


</body>


<script src="https://code.jquery.com/jquery-3.3.1.min.js"
        integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
        crossorigin="anonymous">

</script>


<script type="text/javascript">
    var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");
    var maxSize = 5242880; //5MB
    function checkExtension(fileName, filesize) {
        if (filesize >= maxSize) {
            alert("file size 초과");
            return false;
        }
        if (regex.test(fileName)) {
            alert("지원하지않는 파일형식 ");
            return false
        }
        return true;
    }
    function showImage(fileCallPath){
        alert(fileCallPath);
    }

    $(document).ready(function () {
        //파일 업로드 후 초기화
        var cloneObj = $(".uploadDiv").clone();
        var uploadResult = $(".uploadResult ul");


        $("#uploadBtn").on("click", function (e) {

            var formData = new FormData();
            var inputFile = $("input[name='uploadFile']");
            var files = inputFile[0].files;
            console.log(files);

            for (var i = 0; i < files.length; i++) {
                if (!checkExtension(files[i].name, files[i].size)) {
                    return false;
                }
                formData.append("uploadFile", files[i]);
            }


            $.ajax({
                url: '/uploadAjaxAction',
                processData: false,
                contentType: false,
                data: formData,
                type: 'POST',
                dataType:'json',
                success: function (result) {
                    console.log(result);

                    showuploadedFile(result);
                    //File 업로드 후 초기화
                    $(".uploadDiv").html(cloneObj.html());
                }

            });//ajax 끗

        });

        //이미지파일이 아닌경우 첨부파일이 아이콘이 나오게 변경 0327
        function showuploadedFile(uploadResultAttr){
            var str= "";

            $(uploadResultAttr).each(function (i,obj) {
                if(!obj.image){
                    var fileCallPath = encodeURIComponent(obj.uploadPath+"/"+obj.uuid+"_"+obj.fileName);
                    str+= "<li><a href='download?fileName='"+fileCallPath+"'>"
                        +"<img src='/resources/img/attach.png'>"+obj.fileName+"</a></li>"
                     //   str += "<li><img src='/resources/img/attach.png'>"+obj.fileName +"</li>"
                }
                else {
                   // str += "<li>" + obj.fileName + "</li>";
                    var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);
                    str+="<li><img src='/display?fileName="+fileCallPath+"'><li>'"
                }
            });
            uploadResult.append(str);
        }
    });
</script>


</html>
<style>
    .uploadResult {
        width: 100%;
        background-color: gray;
    }

    .uploadResult ul {
        display: flex;
        flex-flow: row;
        justify-content: center;
        align-items: center;
    }

    .uploadResult ul li {
        list-style: none;
        padding: 10px;
    }

    .uploadResult ul li img {
        width: 100px;
    }
</style>
