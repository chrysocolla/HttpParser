$(function(){
$("#submit").click(function(){submitForm();});
$("#userName").blur(function(){checkUserName();});
$("#email").blur(function(){checkEmail();});
$("#userPassword").blur(function(){checkPassword();});
$("#userConfirmPassword").blur(function(){confirmPassword();});
}
);
function checkForm(){ 
    var nametip = checkUserName(); 
    var emailtip = checkEmail();
    var passtip = checkPassword();  
    var conpasstip = confirmPassword(); 
    return nametip && passtip && conpasstip && emailtip; 
} 

function submitForm(){
//	var username=document.getElementById('userName');
//	var email=document.getElementById('email');
//	var userpasswd = document.getElementById('userPassword');
	upData={'userName':$("#userName").val().toString(),'email':$("#email").val(),'userPassword':$("#userPassword").val(),'logInTimes':1,'signInDate':Date(),'lastLoginDate':Date()};
	if(checkForm()){
		var dataJsonStr=localStorage.getItem('list');
		dataJson=eval('('+dataJsonStr+')');
		dataJson.info.push(upData);
		localStorage.list=JSON.stringify(dataJson);
		sessionStorage.userProfile=JSON.stringify(upData);
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
//	var errname = document.getElementById('errName'); 
	var pattern = /^[\u4E00-\u9FA5A-Za-z0-9_]{3,15}$/;
	//取出已注册的用户名并比较
	var dataJsonStr=localStorage.getItem('list');
	dataJson=eval('('+dataJsonStr+')');
	var repeat=null;
	if($("#userName").val().length == 0){ 
    $("#errName").text("用户名不能为空")
				 .css("color","Crimson");
    return false; 
	}
	if(!pattern.test($("#userName").val())){ 
	$("#errName").text("用户名不合规范")
				 .css("color","Crimson");
    return false; 
    }
	if(dataJson.info.length){
		for(i=0;i<dataJson.info.length;i++){
			if(dataJson.info[i].userName==$("#userName").val()){
				repeat=1;
			}
		}
	}
	if(repeat==1){
	$("#errName").text("用户名已被占用")
				 .css("color","Crimson");
	return false;
	}
    else{ 
    $("#errName").text("OK")
				 .css("color","LimeGreen");
    return true; 
    } 
}
function checkEmail(){
	//初始化list
	var Data={"info":[]};
	var initData=JSON.stringify(Data);
	if(localStorage.list==undefined){
		localStorage.list=initData;
	}
	//取出用户名并初始化模式
//	var email=document.getElementById('email');
//	var erremail=document.getElementById('errEmail');
	var pattern=/^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
	//取出已注册的用户名并比较
	var dataJsonStr=localStorage.getItem('list');
	dataJson=eval('('+dataJsonStr+')');
	var repeat=null;
	if($("#email").val().length == 0){
	$("#errEmail").text("电子邮箱不能为空")
				  .css("color","Crimson");
    return false; 
    }
	if(!pattern.test($("#email").val())){
	$("#errEmail").text("电子邮箱不合规范，请检查您是否输入正确")
				  .css("color","Crimson");
    return false; 
    }
	if(dataJson.info.length){
		for(i=0;i<dataJson.info.length;i++){
			if(dataJson.info[i].email==$("#email").val()){
				repeat=1;
			}
		}
	}
	if(repeat==1){
	$("#errEmail").text("邮箱已被注册")
				  .css("color","Crimson");
	return false;
	}
    else{ 
    $("#errEmail").text("OK")
				  .css("color","LimeGreen");
    return true; 
    } 
}

function checkPassword(){ 
//    var userpasswd = document.getElementById('userPassword'); 
//    var errpasswd = document.getElementById('errPassword'); 
    var pattern = /(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,30}/; 
    if($("#userPassword").val().length == 0){
	$("#errPassword").text("密码不能为空")
					 .css("color","Crimson");
    return false; 
    }
    else if(!pattern.test($("#userPassword").val())){ 
    $("#errPassword").text("密码不合规范")
					 .css("color","Crimson");
    return false; 
    } 
    else{ 
    $("#errPassword").text("OK")
					 .css("color","LimeGreen");
    return true; 
    } 
} 
function confirmPassword(){ 
    if(($("#userPassword").val())!=($("#userConfirmPassword").val()) || $("#userConfirmPassword").val().length == 0){ 
    $("#errConPass").text("上下密码不一致")
					.css("color","Crimson");
    return false; 
    } 
    else{ 
	$("#errConPass").text("OK")
					.css("color","LimeGreen");
    return true; 
    }    
} 