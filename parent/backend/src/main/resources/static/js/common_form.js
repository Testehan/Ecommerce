    $(document).ready(function(){
        $("#buttonCancel").on("click", function(){
            window.location=moduleUrl
        });

        $("#fileImage").change(function(){
            if (!checkFileSize(this)){
                return ;
            }
            showImageThumbnail(this);       // this i assume refers to the file input
        });
    });

    function showImageThumbnail(fileInput){
        var file = fileInput.files[0];
        var reader = new FileReader();
        reader.onload = function(e){
            $("#thumbnail").attr("src", e.target.result)
        }

        reader.readAsDataURL(file);
    }

    function checkFileSize(fileInput){
        fileSize = fileInput.files[0].size;

        if (fileSize > MAX_FILE_SIZE){      // configured in various places depending on the needs
            fileInput.setCustomValidity("You must choose a file with a size LESS than " + (MAX_FILE_SIZE/1000000) + " MB");
            fileInput.reportValidity();
            return false;
        } else {
            fileInput.setCustomValidity("");
            return true;
        }

    }

    function showModalDialog(title, message){
        $("#modalTitle").text(title);
        $("#modalBody").text(message);

        $("#modalDialog").modal();
    }

    function showErrorModal(message){
        showModalDialog("Error", message);
    }

    function showWarningModal(message){
        showModalDialog("Warning", message);
    }