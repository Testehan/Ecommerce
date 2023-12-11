var extraImagesCount=0;

$(document).ready(function(){
    $("#shortDescription").richText();
     $("#fullDescription").richText();

    dropdownBrands = $("#brand");
    dropdownCategories = $("#category");

    dropdownBrands.change(function(){          // when a brand is selected
        dropdownCategories.empty();
        getCategories();
    });

    getCategories();        // we need to call this at the initial load of the page, so that categories for first brand from dropdown are also retrieved

    $("input[name='extraImage']").each(function(index){
         extraImagesCount++;
        $(this).change(function(){                       // "this" is here an input type element with name extraImage
            showExtraImageThumbnail(this, index);
        });
    });

});

function getCategories(){
    brandId = dropdownBrands.val();
    url = brandModuleUrl + "/" + brandId + "/categories";

    $.get(url, function(responseJson){
       $.each(responseJson, function(index,category){
            $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
       });
    });
}

function checkUnique(form){
    productId = $("#id").val();
    productName = $("#name").val();
    csrf = $("input[name='_csrf']").val();      // needed because otherwise we get a 403 Forbidden error. If you check with DevTools you will see a forbidden field with this id in the form.

    url = "[[@{/products/check_unique}]]"
    params = {id: productId, name: productName, _csrf: csrf}

    $.post(url, params, function(response){
        if (response == "OK"){
            form.submit();
        } else if (response =="DuplicateName"){
            showWarningModal("There is another product with the same Name " + productName);
        } else {
            showErrorModal("Unknown response from server");
        }

    }).fail(function(){
         showErrorModal("Unknown response from server");
    });

    return false;
}

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