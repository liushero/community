$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	$.post(
		CONTEXT_PATH + "/follow",
		{"targetId":$(btn).prev().val()},
		function(data) {
			data = $.parseJSON(data);
			if(data.code == 0) {
				if (data.status == 0) {
					$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
					$("#followerCount").text(data.count);
				} else {
					$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
					$("#followerCount").text(data.count);
				}
			} else {
				window.location.href = CONTEXT_PATH + "/login";
			}
		}
	);
}