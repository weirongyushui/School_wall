// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    // 检查是否有记住的用户名，有则自动填充
    loadSavedUsername();
    
    // 绑定表单提交事件
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

// 登录表单提交处理函数
function handleLogin(event) {
    // 阻止默认提交
    event.preventDefault();
    
    // 获取输入值
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const rememberMe = document.querySelector('input[name="remember"]').checked;
    
    // 验证输入
    const validationResult = validateInput(username, password);
    if (!validationResult.valid) {
        showError(validationResult.message);
        return;
    }
    
    // 如果勾选了"记住我"，保存用户名
    if (rememberMe) {
        saveUsername(username);
    } else {
        clearSavedUsername();
    }
    
    // 设置加载状态
    setLoadingState(true);
    
    // 发送请求到后端
    fetch('/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        }),
        credentials: 'include'
    })
    .then(response => {
        console.log('登录响应状态:', response.status);
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            throw new Error('用户名或密码错误');
        } else {
            throw new Error('登录失败，请稍后重试');
        }
    })
    .then(data => {
        console.log('登录响应数据:', data);
        // 登录成功，跳转到用户页面
        if (data.success) {
            console.log('登录成功，跳转到 /user');
            window.location.href = '/user';
        } else {
            console.error('登录失败:', data.message);
            showError(data.message);
            setLoadingState(false);
        }
    })
    .catch(error => {
        console.error('登录错误:', error);
        showError(error.message);
        setLoadingState(false);
    });
}

// 输入验证函数
function validateInput(username, password) {
    // 验证用户名
    if (!username || username.length === 0) {
        return {
            valid: false,
            message: '请输入用户名或学号'
        };
    }
    
    // 学号一般为纯数字，用户名可为字母；统一放宽上限以兼容较长学号
    if (username.length < 3 || username.length > 32) {
        return {
            valid: false,
            message: '用户名或学号长度须在 3～32 个字符之间'
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
    
    return {
        valid: true,
        message: ''
    };
}

// 保存用户名到 localStorage
function saveUsername(username) {
    localStorage.setItem('rememberedUsername', username);
}

// 从 localStorage 加载用户名
function loadSavedUsername() {
    const savedUsername = localStorage.getItem('rememberedUsername');
    if (savedUsername) {
        const usernameInput = document.getElementById('username');
        if (usernameInput) {
            usernameInput.value = savedUsername;
            // 自动勾选"记住我"
            const rememberMeCheckbox = document.querySelector('input[name="remember"]');
            if (rememberMeCheckbox) {
                rememberMeCheckbox.checked = true;
            }
        }
    }
}

// 清除保存的用户名
function clearSavedUsername() {
    localStorage.removeItem('rememberedUsername');
}

// 设置加载状态
function setLoadingState(loading) {
    const loginBtn = document.querySelector('.login-btn');
    if (loginBtn) {
        if (loading) {
            loginBtn.disabled = true;
            loginBtn.textContent = '登录中...';
            loginBtn.style.opacity = '0.7';
            loginBtn.style.cursor = 'not-allowed';
        } else {
            loginBtn.disabled = false;
            loginBtn.textContent = '立即登录';
            loginBtn.style.opacity = '1';
            loginBtn.style.cursor = 'pointer';
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
