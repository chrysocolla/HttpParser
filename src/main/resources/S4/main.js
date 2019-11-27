$(function(){
	$("#submit").click(function(){submitForm();});
	$("#phone").blur(function(){checkPhone();});
	$("#logout").click(function(){
	r=confirm("确定要注销吗？");
	if(r==true){
	localStorage.removeItem('userProfile');
	sessionStorage.removeItem('userProfile');
	location.href='index.html';
	}
});
	var dataJsonStr=localStorage.getItem('list');
	dataJsonAll=eval('('+dataJsonStr+')');
	var dataJsonL=localStorage.getItem('userProfile');
	var dataJsonS=sessionStorage.getItem('userProfile');
	if(dataJsonL==null&&dataJsonS==null){
		$("#visit").text("非法访问，请先登录！")
				   .css("color","DarkRed");
		$("#userInfo").text("将在3秒后跳转至登录页面")
					  .css("color","Coral");
		setTimeout("location.href='index.html'",3000)
		}else{
			if(dataJsonL!=null){
			dataJson=eval('('+dataJsonL+')');
			}else{
			dataJson=eval('('+dataJsonS+')');
			}
		$("#userInfo").text(dataJson.userName)
					  .css("fontSize","46px");
					  
		
					  
		/*for(i=0;i<dataJsonAll.info.length;i++){
			var table=document.getElementById('tableBody');
			var _tr_ = document.createElement('tr'),
				_tduserName_ = document.createElement('td'),
				_tdemail_ = document.createElement('td'),
				_tdsignInDate_ = document.createElement('td'),
				_tdlastLoginDate_ = document.createElement('td'),
				_tdlogInTimes_ = document.createElement('td');
			_tduserName_.appendChild(document.createTextNode(dataJsonAll.info[i].userName));
			_tdemail_.appendChild(document.createTextNode(dataJsonAll.info[i].email));
			_tdsignInDate_.appendChild(document.createTextNode(dataJsonAll.info[i].signInDate));
			_tdlastLoginDate_.appendChild(document.createTextNode(dataJsonAll.info[i].lastLoginDate));
			_tdlogInTimes_.appendChild(document.createTextNode(dataJsonAll.info[i].logInTimes));
			if(dataJsonAll.info[i].userName==dataJson.userName){
				_tr_.className="table-info";
			}
			_tr_.appendChild(_tduserName_);
			_tr_.appendChild(_tdemail_);
			_tr_.appendChild(_tdsignInDate_);
			_tr_.appendChild(_tdlastLoginDate_);
			_tr_.appendChild(_tdlogInTimes_);
			table.appendChild(_tr_);
		}*/
	}
});

function checkPhone(){
	var pattern=/^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\d{8}$/;
	if($("#phone").val().length == 0){
	$("#errPhone").text("手机号码不能为空")
				  .css("color","Crimson");
    return false; 
    }
	if(!pattern.test($("#phone").val())){
	$("#errPhone").text("手机号码不合规范，请检查您是否输入正确")
				  .css("color","Crimson");
    return false; 
    }else{ 
    $("#errPhone").text("OK")
				  .css("color","LimeGreen");
    return true; 
    }
}

function submitForm(){
	if(checkPhone()){
		$.ajax({
		url:"https://apis.juhe.cn/mobile/get",
		dataType: 'jsonp',
		jsonpCallback: "onBack",
		data:{phone:$("#phone").val(),key:"cf839a4f885e06780c619696d64c40e3"}
		});
	}
}
function onBack(response){
			$("tbody").empty();
			$('<td>'+response.result.province+'</td>').appendTo('tbody');
			$('<td>'+response.result.city+'</td>').appendTo('tbody');
			$('<td>'+response.result.areacode+'</td>').appendTo('tbody');
			$('<td>'+response.result.zip+'</td>').appendTo('tbody');
			$('<td>'+response.result.company+'</td>').appendTo('tbody');
		}