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

    params = {id: productId, name: productName, _csrf: csrf}

    $.post(checkUniqueUrl, params, function(response){
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
