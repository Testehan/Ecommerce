$(document).ready(function() {
	$(".linkMinus").on("click", function(event) {
		event.preventDefault();
        decreaseQuantity($(this));
	});

	$(".linkPlus").on("click", function(event) {
		event.preventDefault();
		increaseQuantity($(this));

	});
});

function decreaseQuantity(link) {
    productId = $(this).attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) - 1;

	if (newQuantity > 0) {
		quantityInput.val(newQuantity);
	} else {
		showWarningModal('Minimum quantity is 1');
	}
}

function increaseQuantity(link) {
    productId = $(this).attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) + 1;

	if (newQuantity <= 5) {
		quantityInput.val(newQuantity);
	} else {
		showWarningModal('Maximum quantity is 5');
	}
}