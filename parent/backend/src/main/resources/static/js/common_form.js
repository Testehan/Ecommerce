    $(document).ready(function(){
        $("#buttonCancel").on("click", function(){
            window.location=moduleUrl
        });

        $("#fileImage").change(function(){
            fileSize = this.files[0].size;

            if (fileSize>1048576){
                this.setCustomValidity("You must choose a file with a size LESS than 1 MB");
                this.reportValidity();
            } else {
                this.setCustomValidity("");
                showImageThumbnail(this);       // this i assume refers to the file input
            }
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