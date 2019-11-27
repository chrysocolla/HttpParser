$(function(){
$("#submit").click(function(){submitForm();});
$("#userName").blur(function(){checkUserName();});
$("#userPassword").focus(function(){checkUserName();})
				  .blur(function(){checkPassword();});
}
);
var flag=null;
function checkForm(){ 
	var nametip = checkUserName(); 
	var passtip = checkPassword();  
	return nametip && passtip; 
} 
function submitForm(){
	if(checkForm()){
		//var checkBox=document.getElementById('checkbox');
		var dataJsonStr=localStorage.getItem('list');
		dataJson=eval('('+dataJsonStr+')');
		dataJson.info[flag].logInTimes=Number(dataJson.info[flag].logInTimes)+1;
		dataJson.info[flag].lastLoginDate=Date();
		localStorage.list=JSON.stringify(dataJson);
		if($("#checkbox").is(":checked")==true){
			sessionStorage.removeItem('userProfile');
			localStorage.userProfile=JSON.stringify(dataJson.info[flag]);
		}else{
			localStorage.removeItem('userProfile');
			sessionStorage.userProfile=JSON.stringify(dataJson.info[flag]);
		}
		location.href='main.html';
	}
}
function checkUserName(){
	//初始化list
	var Data={"info":[]};
	var initData=JSON.stringify(Data);
	if(localStorage.list==undefined){
		localStorage.list=initData;
	}
	//取出用户名并初始化模式
//	var username=document.getElementById('userName');
//	var errname =document.getElementById('errName'); 
	//取出已注册的用户名并比较
	var dataJsonStr=localStorage.getItem('list');
	dataJson=eval('('+dataJsonStr+')');
	var Namerepeat=null;
	var Emailrepeat=null;
	if($("#userName").val().length== 0){ 
	$("#errName").text("用户名/电子邮箱不能为空")
				 .css("color","Crimson");
//    errname.innerHTML="用户名/电子邮箱不能为空";
//    errname.className="error";
    return false; 
	}
	if(dataJson.info.length){
		for(i=0;i<dataJson.info.length;i++){
			if(dataJson.info[i].userName==$("#userName").val()){
				Namerepeat=1;
				flag=i;
			}
		}
	}
	if(dataJson.info.length){
		for(i=0;i<dataJson.info.length;i++){
			if(dataJson.info[i].email==$("#userName").val()){
				Emailrepeat=1;
				flag=i;
			}
		}
	}
	if(Namerepeat==1||Emailrepeat==1){
	$("#errName").text("OK")
				 .css("color","LimeGreen");
	return true;
	}else{ 
	$("#errName").text("不存在此用户/电子邮箱")
				 .css("color","Crimson");
	return false; 
	}
}
function checkPassword(){
//    var userpasswd = document.getElementById('userPassword'); 
//    var errpasswd = document.getElementById('errPassword'); 
	var dataJsonStr=localStorage.getItem('list');
	dataJson=eval('('+dataJsonStr+')');
	if($("#userPassword").val().length == 0){
	$("#errPassword").text("密码不能为空")
					 .css("color","Crimson");
	return false; 
	}
	if($("#userPassword").val()==dataJson.info[flag].userPassword){ 
	$("#errPassword").text("OK")
					 .css("color","LimeGreen");
	return true; 
	}else{
	$("#errPassword").text("密码不匹配")
					 .css("color","Crimson");
	return false; 
	}
}
