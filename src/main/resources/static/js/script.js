
const toggleSiderbar = () => {
  if ($(".sidebar").is(":visible")) {
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "2%");
  } else {
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "17%");
  }
};
const search = () => {
  let query = $("#search_input").val();
  if (query == "") {
    $(".search_result").hide();
  } else {
    let url=`http://localhost:8080/search/${query}`;
    fetch(url).then((response)=>{
      return response.json();
    }).then((data)=>{
//console.log(data);
let text=`<div class='list-group'>`;
if(data.length > 0){
data.forEach((contact)=>{
text+=`<a href='/user/contact/view/${contact.id}' class='list-group-item list-group-item-action'>${contact.firstName} ${contact.lastName}</a>`;
});
}
else{
  text+=`<li class='list-group-item'>Data not found. Please try again...</li>`;
 }
text+=`</div>`;

$(".search_result ").html(text);
    $(".search_result").show();

    });
  }
};
