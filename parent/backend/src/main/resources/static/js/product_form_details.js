$(document).ready(function(){

    $("a[name='linkRemoveDetail'").each(function(index){
        $(this).click(function(){
            removeExtraDivDetailsBy(index);
        });

    });

});

function addNextDetailSection(){
    allDivDetails=$("[id^='divDetail']");   // get all divDetails whos id starts with "divDetail"
    divDetailsCount = allDivDetails.length;
    nextDivDetailId = divDetailsCount;

    htmlDetailSection=`
        <div class="form-inline" id="divDetail${nextDivDetailId}">
            <label class="m-3">Name:</label>
            <input type="text" class="form-control w-25" name="detailNames" maxlength="256" />
            <label class="m-3">Value:</label>
            <input type="text" class="form-control w-25" name="detailValues" maxlength="256" />
        </div>
    `;

    $("#divProductDetails").append(htmlDetailSection);

    previousDivDetailSection=allDivDetails.last();      // take the previous divDetails
    previousDivDetailId = previousDivDetailSection.attr("id");

    htmlLinkRemove=`
        <a class="btn fas fa-times-circle fa-2x icon-dark float-right"
            href="javascript:removeExtraDivDetails('${previousDivDetailId}')"
            title="Remove this Product Detail"></a>
    `;
    previousDivDetailSection.append(htmlLinkRemove);

    $("input[name='detailNames']").last().focus();
}

function removeExtraDivDetails(divDetailId){
    $("#"+divDetailId).remove();
}

function removeExtraDivDetailsBy(index){
    $("#divDetail"+index).remove();
}