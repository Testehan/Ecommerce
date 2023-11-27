$(document).ready(function(){
    $("#logoutLink").on("click",function(e){
        e.preventDefault();
        // "this" is the Logout button element.
        document.logoutForm.submit();

    });
});