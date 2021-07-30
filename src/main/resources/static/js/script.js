const toggleSiderbar=()=>{
if($(".sidebar").is(":visible")){
    $(".sidebar").css("display", "none");
     $(".content").css("margin-left","2%");
   }else{
  $(".sidebar").css("display", "block");
  $(".content").css("margin-left", "17%");
}
};