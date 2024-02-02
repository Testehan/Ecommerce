// addToCart
$(document).ready(function() {
	$("#addToCart").on("click", function(event) {
		addToCart();
	});

});

function addToCart(){
    quantity = $("#quantity" + productId).val();      //  productId is set in product_detail javascript part
    url = contextPath + "cart/add/" + productId + "/" + quantity;     // contextPath is set in product_detail javascript part

   $.ajax({
            type: 'POST',
            url: url,
            beforeSend: function(xhr){
                xhr.setRequestHeader(csrfHeaderName, csrfValue);    // csrfHeaderName and csrfValue are initialized in product_detail.html js code
            }
   }).done(function(response){
        showModalDialog("Shopping cart", response);
   }).fail(function(){
        showErrorModal("ERROR -> Could not connect to server or server encountered an error");
   });
}