function clearFilter(){
    window.location = moduleURL;
}

function showDeleteConfirmModal(link, entityName){
    entityId = link.attr("entityId");   // this comes from  th:categoryId="${category.id}" which is defined on the <a> element of the delete button

    console.log(link);
    console.log($(this));

    $("#confirmText").text("Are you sure you want to delete this " + entityName + " with id " + entityId + " ?")
    $("#yesButton").attr("href",link.attr("href"));
    $("#confirmModal").modal()
}

function handleDetailLinkClick(cssClass, modalId) {
	$(cssClass).on("click", function(e) {
		e.preventDefault();
		linkDetailURL = $(this).attr("href");
		$(modalId).modal("show").find(".modal-content").load(linkDetailURL);
	});
}

function handleDefaultDetailLinkClick() {
	handleDetailLinkClick(".link-detail", "#detailModal");
}