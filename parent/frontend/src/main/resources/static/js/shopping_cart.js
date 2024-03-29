decimalSeparator = decimalPointType == 'COMMA' ? ',' : '.';             // decimalPointType and thousandsPointType values from from the template file
thousandsSeparator = thousandsPointType == 'COMMA' ? ',' : '.';

$(document).ready(function() {
	$(".linkMinus").on("click", function(event) {
		event.preventDefault();
        decreaseQuantity($(this));
	});

	$(".linkPlus").on("click", function(event) {
		event.preventDefault();
		increaseQuantity($(this));
	});

	$(".linkRemove").on("click", function(event) {
        event.preventDefault();
    	removeProduct($(this));
    });
});

function decreaseQuantity(link) {
    productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) - 1;

	if (newQuantity > 0) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		showWarningModal('Minimum quantity is 1');
	}
}

function increaseQuantity(link) {
    productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) + 1;

	if (newQuantity <= 5) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity);
	} else {
		showWarningModal('Maximum quantity is 5');
	}
}

function updateQuantity(productId, quantity) {
	url = contextPath + "cart/update/" + productId + "/" + quantity;

	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(updatedSubtotal) {
		updateSubtotal(updatedSubtotal, productId);
		updateTotal();
	}).fail(function() {
		showErrorModal("Error while updating product quantity.");
	});
}

function updateSubtotal(updatedSubtotal, productId) {
	$("#subtotal" + productId).text(formatCurrency(updatedSubtotal));
}

function formatCurrency(amount) {
	return $.number(amount, decimalDigits, decimalSeparator, thousandsSeparator);
}

function updateTotal() {
	total = 0.0;
	productCount = 0;

	$(".subtotal").each(function(index, element) {
		productCount++;
		total += parseFloat(clearCurrencyFormat(element.innerHTML));
	});

	if (productCount < 1) {
		showEmptyShoppingCart();
	} else {
		formattedTotal = $.number(total, 2);
		$("#total").text(formatCurrency(formattedTotal));
	}
}

function clearCurrencyFormat(numberString) {
	result = numberString.replaceAll(thousandsSeparator, "");
	return result.replaceAll(decimalSeparator, ".");
}

function showEmptyShoppingCart() {
	$("#sectionTotal").hide();
	$("#sectionEmptyCartMessage").removeClass("d-none");
}

function removeProduct(link) {
	url = link.attr("href");

	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		rowNumber = link.attr("rowNumber");
		removeProductHTML(rowNumber);
		updateTotal();
		updateCountNumbers();

		showModalDialog("Shopping Cart", response);

	}).fail(function() {
		showErrorModal("Error while removing product.");
	});
}

function removeProductHTML(rowNumber) {
	$("#row" + rowNumber).remove();
	$("#blankLine" + rowNumber).remove();
}

function updateCountNumbers() {
	$(".divCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	});
}