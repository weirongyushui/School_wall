// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    // 绑定表单提交事件
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', handleRegister);
    }
});

// 注册表单提交处理函数
function handleRegister(event) {
    // 阻止默认提交
    event.preventDefault();
    
    // 获取输入值
    const studentId = document.getElementById('studentId').value.trim();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const nickname = document.getElementById('nickname').value.trim();
    const email = document.getElementById('email').value.trim();
    const agreeTerms = document.getElementById('agreeTerms').checked;
    
    // 验证输入
    const validationResult = validateInput(studentId, username, password, confirmPassword, agreeTerms);
    if (!validationResult.valid) {
        showError(validationResult.message);
        return;
    }
    
    // 设置加载状态
    setLoadingState(true);
    
    // 发送请求到后端
    fetch('/api/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            studentId: studentId,
            username: username,
            password: password,
            nickname: nickname,
            email: email
        }),
        credentials: 'include'
    })
    .then(response => {
        console.log('注册响应状态:', response.status);
        if (response.ok) {
            return response.json();
        } else if (response.status === 400) {
            return response.json().then(err => {
                throw new Error(err.message || '注册失败');
            });
        } else if (response.status === 409) {
            throw new Error('用户名或学号已被注册');
        } else {
            throw new Error('注册失败，请稍后重试');
        }
    })
    .then(data => {
        console.log('注册响应数据:', data);
        // 注册成功
        if (data.success) {
            showSuccess('注册成功！即将跳转到登录页面...');
            setTimeout(() => {
                window.location.href = '/login';
            }, 1500);
        } else {
            console.error('注册失败:', data.message);
            showError(data.message);
            setLoadingState(false);
        }
    })
    .catch(error => {
        console.error('注册错误:', error);
        showError(error.message);
        setLoadingState(false);
    });
}

// 输入验证函数
function validateInput(studentId, username, password, confirmPassword, agreeTerms) {
    // 验证学号
    if (!studentId || studentId.length === 0) {
        return {
            valid: false,
            message: '请输入学号'
        };
    }
    
    // 学号应为纯数字
    if (!/^\d+$/.test(studentId)) {
        return {
            valid: false,
            message: '学号必须为纯数字'
        };
    }
    
    if (studentId.length !== 9) {
        return {
            valid: false,
            message: '学号必须为 9 位数字'
        };
    }
    
    // 验证用户名
    if (!username || username.length === 0) {
        return {
            valid: false,
            message: '请输入用户名'
        };
    }
    
    if (username.length < 3 || username.length > 32) {
        return {
            valid: false,
            message: '用户名长度须在 3-32 个字符之间'
        };
    }
    
    // 用户名只能包含字母、数字、下划线
    if (!/^[a-zA-Z0-9_]+$/.test(username)) {
        return {
            valid: false,
            message: '用户名只能包含字母、数字和下划线'
        };
    }
    
    // 验证密码
    if (!password || password.length === 0) {
        return {
            valid: false,
            message: '请输入密码'
        };
    }
    
    if (password.length < 6 || password.length > 20) {
        return {
            valid: false,
            message: '密码长度必须在 6-20 个字符之间'
        };
    }
    
    // 验证确认密码
    if (password !== confirmPassword) {
        return {
            valid: false,
            message: '两次输入的密码不一致'
        };
    }
    
    // 验证协议
    if (!agreeTerms) {
        return {
            valid: false,
            message: '请先阅读并同意用户协议'
        };
    }
    
    // 验证邮箱（如果填写）
    const emailInput = document.getElementById('email');
    if (emailInput && emailInput.value.trim() !== '') {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailInput.value.trim())) {
            return {
                valid: false,
                message: '请输入有效的邮箱地址'
            };
        }
    }
    
    return {
        valid: true,
        message: ''
    };
}

// 设置加载状态
function setLoadingState(loading) {
    const registerBtn = document.querySelector('.register-btn');
    if (registerBtn) {
        if (loading) {
            registerBtn.disabled = true;
            registerBtn.textContent = '注册中...';
            registerBtn.style.opacity = '0.7';
            registerBtn.style.cursor = 'not-allowed';
        } else {
            registerBtn.disabled = false;
            registerBtn.textContent = '立即注册';
            registerBtn.style.opacity = '1';
            registerBtn.style.cursor = 'pointer';
        }
    }
}

// 显示错误提示
function showError(message) {
    // 创建错误提示元素
    let errorElement = document.querySelector('.error-message');
    if (!errorElement) {
        errorElement = document.createElement('div');
        errorElement.className = 'error-message';
        errorElement.style.cssText = `
            background-color: #ffe6e6;
            border: 1px solid #ff4d4f;
            color: #ff4d4f;
            padding: 10px 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            font-size: 14px;
            text-align: center;
            animation: shake 0.3s ease-in-out;
        `;
        
        // 添加到表单顶部
        const cardContent = document.querySelector('.card-content');
        if (cardContent) {
            cardContent.insertBefore(errorElement, cardContent.firstChild);
        }
        
        // 3 秒后自动消失
        setTimeout(() => {
            if (errorElement && errorElement.parentNode) {
                errorElement.remove();
            }
        }, 3000);
    } else {
        errorElement.textContent = message;
    }
    
    // 添加抖动动画
    const style = document.createElement('style');
    if (!document.querySelector('style[data-shake]')) {
        style.setAttribute('data-shake', 'true');
        style.textContent = `
            @keyframes shake {
                0%, 100% { transform: translateX(0); }
                25% { transform: translateX(-10px); }
                75% { transform: translateX(10px); }
            }
        `;
        document.head.appendChild(style);
    }
}

// 显示成功提示
function showSuccess(message) {
    // 创建成功提示元素
    let successElement = document.querySelector('.success-message');
    if (!successElement) {
        successElement = document.createElement('div');
        successElement.className = 'success-message';
        successElement.style.cssText = `
            background-color: #e6ffed;
            border: 1px solid #52c41a;
            color: #237804;
            padding: 10px 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            font-size: 14px;
            text-align: center;
            animation: fadeIn 0.3s ease-in-out;
        `;
        
        // 添加到表单顶部
        const cardContent = document.querySelector('.card-content');
        if (cardContent) {
            cardContent.insertBefore(successElement, cardContent.firstChild);
        }
    } else {
        successElement.textContent = message;
    }
}
