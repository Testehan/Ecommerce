var extraImagesCount=0;

$(document).ready(function(){

    $("input[name='extraImage']").each(function(index){
        extraImagesCount++;
        $(this).change(function(){                       // "this" is here an input type element with name extraImage
             if (!checkFileSize(this)){
                return ;
             }
            showExtraImageThumbnail(this, index);
        });
    });

});

function showExtraImageThumbnail(fileInput, index){
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e){
        $("#extraThumbnail"+index).attr("src", e.target.result)
    }

    reader.readAsDataURL(file);

    if (index >= extraImagesCount-1){
        addExtraImageSection(index + 1);
    }
}

function addExtraImageSection(index){
    // we need to increase index by 1 because index starts at 0, however in the UI you don't want the user to see text starting
    // from 0, like "Extra image 0"...so while the id of the elements can have 0, the other index locations are incremented.
    htmlExtraImage = `
        <div class="col border m-3 p-2" id="divExtraImage${index}">
           <div id="extraImageHeader${index}"><label>Extra image no ${index + 1}</label></div>
           <div class="m-2">
               <img id="extraThumbnail${index}" alt="Extra image ${index + 1} preview" class="img-fluid"
                    src="${defaultThumbnailImageSrc}" />
           </div>
           <div>
               <input type="file" name="extraImage"
                    onChange="showExtraImageThumbnail(this, ${index})"
                    accept="image/png, image/jpeg" />
           </div>
       </div>
    `;

    htmlLinkRemove=`
        <a class="btn fas fa-times-circle fa-2x icon-dark float-right"
            href="javascript:removeExtraImage(${index-1})"
            title="Remove this image"></a>
    `;

    $("#divProductImages").append(htmlExtraImage);

    // we select the previous image section in order to add the remove button
    $("#extraImageHeader" + (index-1)).append(htmlLinkRemove);

    extraImagesCount++;
}

function removeExtraImage(index){
    $("#divExtraImage"+index).remove();
}