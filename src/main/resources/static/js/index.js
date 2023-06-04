$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	// 发送AJAX请求之前，将CSRF令牌设置到请求的消息头中
	// const token = $("meta[name='_csrf']").attr("content");
	// const header = $("meta[name='_csrf_header']").attr("content");
	// $(document).ajaxSend(function (e, xhr, options) {
	// 	xhr.setRequestHeader(header, token);
	// })

	// 获取标题和内容
	const title = $("#recipient-name").val(), content = $("#message-text").val();
	if (title === "" || content === "") {
		alert("标题和内容不能为空!");
		$("#recipient-name").val(title);
		$("#message-text").val(content);
	} else {
		// 发送异步请求
		$("#publishModal").modal("hide");
		$.post(
			CONTEXT_PATH + "/discuss/add",
			{"title":title, "content": content},
			function (data) {
				data = $.parseJSON(data);
				/*console.log(typeof data);
                console.log(data.code);
                console.log(data.msg);*/
				// 在提示框中显示返回消息
				$("#hintBody").text(data.msg);

				// 提示框div显示
				$("#hintModal").modal("show");
				// 两秒后隐藏提示框
				setTimeout(function(){
					$("#hintModal").modal("hide");
					// 刷新页面
					if (data.code === 0) {
						window.location.reload();
					}
				}, 2000);
			}
		);
	}
}