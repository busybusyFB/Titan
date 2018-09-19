(function() {
	
	var avatar = document.getElementById('avatar');
	var loginForm = document.getElementById('id-login-form');
	var registerForm = document.getElementById('id-register-form');
	var getpwdForm = document.getElementById('id-getpwd-form');
	var loginbBtn = document.getElementById('login-btn');
	var registerBtn = document.getElementById('register-btn');
	var getpwdBtn = document.getElementById('getpwd-btn');
	var jumpToRegister = document.getElementById('jumpToRegister');
	var jumpBackLogin = document.getElementById('jumpbackToLogin');
	var jumpToGetpwd = document.getElementById('jumpToGetpwd');
	var jumpBackLogin2 = document.getElementById('jumpbackToLogin02');
	var cancelBtn = document.getElementById('cancel-btn');
	
	function init() {
			//in login form
			avatar.addEventListener('click', showLoginForm);
			loginbBtn.addEventListener('click', userLogin);
			loginForm.addEventListener('click', hideLoginForm);
			getpwdBtn.addEventListener('click', userGetpwd);
			jumpToRegister.addEventListener('click', showRegisterForm);
			jumpToGetpwd.addEventListener('click', showGetpwdForm);
			//in register form
			registerForm.addEventListener('click', hideRegisterForm);
			registerBtn.addEventListener('click', userRegister);
			jumpBackLogin.addEventListener('click', showLoginForm);
			//in getpwd form
			getpwdForm.addEventListener('click', hideGetpwdForm);
			getpwdBtn.addEventListener('click', userGetpwd);
			jumpBackLogin2.addEventListener('click', showLoginForm);
			cancelBtn.addEventListener('click', hideGetpwdForm);
	}

	function showLoginForm() {
		loginForm.style.display = "block";
		registerForm.style.display = "none";
		getpwdForm.style.display = "none";
		hidewraningMsg()
	}
	function hideLoginForm() {
		if (event.target == loginForm) {
			loginForm.style.display = "none";
		}
	}
	
    function ajax(method, url, data, callback, errorHandler) {
        var xhr = new XMLHttpRequest();

        xhr.open(method, url, true);

        xhr.onload = function() {
        	if (xhr.status === 200) {
        		callback(xhr.responseText);
        	} else {
        		errorHandler();
        	}
        };

        xhr.onerror = function() {
            console.error("The request couldn't be completed.");
            errorHandler();
        };

        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type",
                "application/json;charset=utf-8");
            xhr.send(data);
        }
    }
    
    // Ajax: user login function
	function userLogin() {
		hidewraningMsg();
		var username = document.getElementsByName("uname")[0].value;
		var pwd = document.getElementsByName("psw")[0].value;
		if (username.length == 0) {
			document.getElementById("loginid").innerHTML = '  Cannot be empty!';
			console.log("information not complete");
		} else if (pwd.length == 0){
			document.getElementById("loginpwd").innerHTML = '  Cannot be empty!';
			console.log("information not complete");
		} else {
			console.log("information complete! " + username + " " + pwd);
	        var url = './login';
	        var params = 'user_id=' + username + '&pwd=' + pwd;
	        var req = JSON.stringify({});
	        ajax('GET', url + '?' + params, req,
	                // successful callback
	                function(res) {
	                    var result = JSON.parse(res);
	                    if (result.hasUsrName && result.isPwdCorrect) {
	                    	console.log("success");
	                    	updateCurrentUser(username);
	                    	loginForm.style.display = "none";
	                    } else if (!result.hasUsrName) {
	                    	document.getElementById("loginid").innerHTML = '  This user does not exist, try again!';
	                    	console.log("No such usr name.");
	                    } else if (!result.isPwdCorrect) {
	                    	document.getElementById("loginpwd").innerHTML = '  Wrong password, try again!';
	                    	console.log("Wrong password.");
	                    }
	                },
	                // failed callback
	                function() {
	                    console.log("fail to fetch user info.");
	                });
		}
		
	}
	
	function updateCurrentUser(new_id) {
    	//update user_id and fullname in main.js
    	var mainjs = document.createElement("script");
    	mainjs.src="/Titan/scripts/main.js";
    	user_id = new_id;
    	user_fullname = user_id;
    	document.getElementById("welcome-msg").innerHTML = 'Welcome, ' + user_id;
	}
	
	function showRegisterForm() {
		loginForm.style.display = "none";
		registerForm.style.display = "block";
		getpwdForm.style.display = "none";
		hidewraningMsg()
	}
	function hideRegisterForm() {
		if (event.target == registerForm) {
			registerForm.style.display = "none";
		}
	}

	function userRegister() {
		hidewraningMsg();
		var firstName = document.getElementsByName("registerfirstname")[0].value;
		var lastName = document.getElementsByName("registerlastname")[0].value;
		var userName = document.getElementsByName("registeruname")[0].value;
		var pwd = document.getElementsByName("registerpsw")[0].value;
		if (firstName.length == 0 || lastName.length == 0 || userName.length == 0 || pwd.length == 0) {
			//TODO: give warning
			if (firstName.length == 0) {
				document.getElementById("registerfirstnameLabel").innerHTML = '  Cannot be empty';
			}
			if (lastName.length == 0) {
				document.getElementById("registerlastnameLabel").innerHTML = '  Cannot be empty';
			}
			if (userName == 0) {
				document.getElementById("registeridLabel").innerHTML = '  Cannot be empty';
			}
			if (pwd == 0) {
				document.getElementById("registerpwdLabel").innerHTML = '  Cannot be empty';
			}
		} else {
	        var url = './register';
	        var params = 'user_id=' + userName + '&pwd=' + pwd + '&firstname=' + firstName + '&lastname=' + lastName;
	        var req = JSON.stringify({});
	        ajax('GET', url + '?' + params, req,
	            // successful callback
	        	function(res) {
	            	var result = JSON.parse(res);
	            	if (result.SUCCESS) {
	            		updateCurrentUser(userName);
	            		registerForm.style.display = "none";
	            		console.log("Register success");
	            	} else {
	            		//warning message
	            		document.getElementById("registeridLabel").innerHTML = '  Already used, try a new name';
	            		console.log("Register failure");
	            	}
	            },
	            // failed callback
	            function() {
	            	console.log("fail to register a new user.");
	            });
		}
	}
	
	function showGetpwdForm() {
		loginForm.style.display = "none";
		registerForm.style.display = "none";
		getpwdForm.style.display = "block";
	}
	
	function hideGetpwdForm() {
		if (event.target == getpwdForm || event.target == cancelBtn) {
			getpwdForm.style.display = "none";
		}
	}
	function userGetpwd() {
		//TODO
	}
	
	function hidewraningMsg() {
		document.getElementById("loginid").innerHTML = null;
		document.getElementById("loginpwd").innerHTML = null;
		document.getElementById("registerfirstnameLabel").innerHTML = null;
		document.getElementById("registerlastnameLabel").innerHTML = null;
		document.getElementById("registeridLabel").innerHTML = null;
		document.getElementById("registerpwdLabel").innerHTML = null;
	}
	
	init();
})();
