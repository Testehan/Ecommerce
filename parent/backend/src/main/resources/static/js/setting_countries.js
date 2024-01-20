var buttonLoad;
var dropdownCountry;

var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;

var labelCountryName;
var fieldCountryName;
var fieldCountryCode;

$(document).ready(function(){
    buttonLoad = $("#buttonLoadCountries");
    dropdownCountry = $("#dropdownCountries");

    buttonAddCountry = $("#buttonAddCountry");
    buttonUpdateCountry = $("#buttonUpdateCountry");
    buttonDeleteCountry = $("#buttonDeleteCountry");

    labelCountryName = $("#labelCountryName");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");

    buttonLoad.click(function(){
        loadCountries();
    });

    dropdownCountry.on("change", function(){
        changeFormStateToSelectedCountry();
    });

    buttonAddCountry.click(function(){
        if (buttonAddCountry.val() == "Add"){
            addCountry();
        } else{
            changeFormStateToNewCountry();
        }
    });

    buttonUpdateCountry.click(function(){
        updateCountry();
    });

    buttonDeleteCountry.click(function(){
        deleteCountry();
    });
});

function loadCountries(){
    url = contextPath + "countries/list";
    $.get(url, function(responseJson){
        dropdownCountry.empty();

        $.each(responseJson, function(index, country){
            optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropdownCountry);
        });
    }).done(function(){
        buttonLoad.val("Refresh Country List");
        showToastMessage("All countries have been loaded");
    }).fail(function(){
        showToastMessage("ERROR -> Could not connect to server or server encountered an error");
    });
}

function changeFormStateToSelectedCountry(){
    buttonAddCountry.prop("value", "New");
    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected country:");
    selectedCountryName = $("#dropdownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    countryCode = dropdownCountry.val().split("-")[1];
    fieldCountryCode.val(countryCode);

}

function addCountry(){
    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();

    jsonData = {name : countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr){
            xhr.setRequestHeader(csrfHeaderName, csrfValue);    // csrfHeaderName and csrfValue are initialized in settings.html js code
        },
        data: JSON.stringify(jsonData),
        contentType: "application/json"
    }).done(function(countryId){
        selectNewlyAddedCountry(countryId, countryCode, countryName);
        showToastMessage("The new country has been added!");
    }).fail(function(){
        showToastMessage("ERROR -> Could not connect to server or server encountered an error");
    });
}

function selectNewlyAddedCountry(countryId, countryCode, countryName){
    optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropdownCountry);

    $("#dropdownCountries option[value='" + optionValue + "']").prop("selected", true);

    fieldCountryName.val("");
    fieldCountryCode.val("").focus();
}

function changeFormStateToNewCountry(){
    buttonAddCountry.val("Add");
    labelCountryName.text("Country name:");

    buttonUpdateCountry.prop("disabled", true);
    buttonDeleteCountry.prop("disabled", true);

    fieldCountryName.val("").focus();
    fieldCountryCode.val("");
}

function updateCountry(){
    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();
    countryId = dropdownCountry.val().split("-")[0];

    jsonData = {id:countryId, name : countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr){
            xhr.setRequestHeader(csrfHeaderName, csrfValue);    // csrfHeaderName and csrfValue are initialized in settings.html js code
        },
        data: JSON.stringify(jsonData),
        contentType: "application/json"
    }).done(function(countryId){
        $("#dropdownCountries option:selected").val(countryId + "-" + countryCode);
        $("#dropdownCountries option:selected").text(countryName);

        showToastMessage("The new country has been updated!");
        changeFormStateToNewCountry();

    }).fail(function(){
        showToastMessage("ERROR -> Could not connect to server or server encountered an error");
    });
}

function deleteCountry(){
    optionValue = dropdownCountry.val();
    countryId = optionValue.split("-")[0];
    url = contextPath + "countries/delete/" + countryId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function(xhr){
            xhr.setRequestHeader(csrfHeaderName, csrfValue);    // csrfHeaderName and csrfValue are initialized in settings.html js code
        }
    }).done(function(){
        $("#dropdownCountries option[value='" + optionValue + "']").remove();
        changeFormStateToNewCountry();
        showToastMessage("Country have been deleted");
    }).fail(function(){
        showToastMessage("ERROR -> Could not connect to server or server encountered an error");
    });
}

function showToastMessage(message){
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}
