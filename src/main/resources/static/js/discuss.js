function like(btn, entityType, id, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "id": id, "entityUserId": entityUserId, "postId": postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("i").text(data.count);
                $(btn).children("b").text(data.status == 1 ? '赞' : "已赞");
            } else {
                window.location.href = CONTEXT_PATH + "/login";
            }
        }
    );
}