function use_profile() {
    $.get('profile', function (res) {
        $("input[name='kcal']").val(res.kcal);
        $("input[name='fat']").val(res.fat);
        $("input[name='carbs']").val(res.carbs);
        $("input[name='protein']").val(res.protein);
    });
}

$(function() {
   $("#use_profile").click(function() {use_profile();});
});