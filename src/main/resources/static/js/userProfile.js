// userProfile.js

const DEFAULT_USER_ID = 1;
// 与 application.properties 中 server.port 一致；若用 IDE 内置预览(63342)跨域调后端，Cookie 不会带上，会话会丢
const API_BASE = window.location.port === '63342' ? 'http://localhost:8081' : '';
let currentUserId = DEFAULT_USER_ID;

if (window.location.port === '63342') {
    console.warn('[userProfile] 当前为 IDE 预览端口，登录 Cookie 无法带到后端，请用浏览器直接打开 http://localhost:8081/user 测试。');
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.edit-btn').addEventListener('click', function() {
        showEditForm();
    });
    loadUserProfile();
});

function requestJson(url, options) {
    return fetch(url, {
        ...options,
        credentials: 'include'
    }).then(async response => {
        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }
        const text = await response.text();
        if (!text || !String(text).trim()) {
            return null;
        }
        try {
            return JSON.parse(text);
        } catch (e) {
            throw new Error('响应不是合法 JSON');
        }
    });
}

function formatDate(value) {
    if (!value) {
        return '';
    }
    const date = new Date(value);
    if (isNaN(date.getTime())) {
        return String(value).split('T')[0];
    }
    return date.toISOString().split('T')[0];
}

function loadUserProfile() {
    requestJson(`${API_BASE}/api/user/current`)
        .then(data => {
            if (!data) {
                showError('未获取到用户信息，请重新登录');
                setTimeout(() => {
                    window.location.href = '/login';
                }, 2000);
                return;
            }
            renderUserProfile(data);
        })
        .catch(error => {
            console.error('加载用户资料失败:', error);
            showError('加载用户资料失败：' + error.message);
            setTimeout(() => {
                window.location.href = '/login';
            }, 2000);
        });
}

function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background-color: #ff4d4f;
        color: white;
        padding: 20px 40px;
        border-radius: 8px;
        font-size: 16px;
        z-index: 9999;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        text-align: center;
    `;
    errorDiv.textContent = message;
    document.body.appendChild(errorDiv);
    
    setTimeout(() => {
        if (errorDiv.parentNode) {
            errorDiv.remove();
        }
    }, 3000);
}

function renderUserProfile(data) {
    const userNo = data.user_id || data.userId || '';
    currentUserId = data.id || DEFAULT_USER_ID;

    document.getElementById('nickname').textContent = data.nickname || '';
    document.getElementById('introduction').textContent = data.introduction || '';
    document.getElementById('birthday').textContent = '生日：' + formatDate(data.birthday);
    document.getElementById('user_id').textContent = '学号：' + userNo;
    document.getElementById('wechat').textContent = '微信: ' + (data.wechat || '');
    document.getElementById('qq').textContent = 'QQ: ' + (data.qq || '');
    document.getElementById('email').textContent = 'Email: ' + (data.email || '');
    document.getElementById('major').textContent = '所在学院/专业：' + (data.major || '');
    document.getElementById('grade').textContent = '所在年级：' + (data.grade || '');
    document.getElementById('phone').textContent = '电话号码：' + (data.phone || '');
    document.getElementById('address').textContent = '地址：' + (data.address || '');
    document.getElementById('gender').textContent = '性别：' + (data.gender === 1 ? '男' : '女');
    if (data.avatar) {
        document.getElementById('avatar').src = data.avatar;
    }

    document.getElementById('system-info').textContent =
        '用户ID: ' + currentUserId +
        ' | 注册于: ' + formatDate(data.createdAt || data.created_at) +
        ' | 更新于: ' + formatDate(data.updatedAt || data.updated_at);
}

// 显示编辑表单
function showEditForm() {
    // 创建编辑表单
    const editForm = document.createElement('div');
    editForm.id = 'edit-form';
    editForm.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 1000;
    `;
    
    // 表单内容
    editForm.innerHTML = `
        <div style="background-color: white; padding: 20px; border-radius: 8px; width: 90%; max-width: 500px; max-height: 80vh; overflow-y: auto;">
            <h2 style="text-align: center; margin-bottom: 20px;">编辑个人资料</h2>
            <form id="profile-form">
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">昵称:</label>
                    <input type="text" id="edit-nickname" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">个人简介:</label>
                    <textarea id="edit-introduction" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px; height: 80px;"></textarea>
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">生日:</label>
                    <input type="date" id="edit-birthday" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">学号:</label>
                    <input type="text" id="edit-user_id" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">微信:</label>
                    <input type="text" id="edit-wechat" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">QQ:</label>
                    <input type="text" id="edit-qq" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">Email:</label>
                    <input type="email" id="edit-email" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">学院/专业:</label>
                    <input type="text" id="edit-major" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">年级:</label>
                    <input type="text" id="edit-grade" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">电话号码:</label>
                    <input type="text" id="edit-phone" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">地址:</label>
                    <input type="text" id="edit-address" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">性别:</label>
                    <div style="margin-top: 5px;">
                        <input type="radio" name="gender" value="1" id="edit-gender-male">
                        <label for="edit-gender-male">男</label>
                        <input type="radio" name="gender" value="0" id="edit-gender-female" style="margin-left: 20px;">
                        <label for="edit-gender-female">女</label>
                    </div>
                </div>
                <div style="margin-bottom: 12px;">
                    <label style="display: block; font-size: 14px; margin-bottom: 5px;">头像URL:</label>
                    <input type="text" id="edit-avatar" style="width: 100%; padding: 6px; border: 1px solid #ddd; border-radius: 4px;">
                </div>
                <div style="display: flex; justify-content: space-between; margin-top: 20px;">
                    <button type="button" id="cancel-edit" style="padding: 8px 16px; background-color: #ccc; border: none; border-radius: 4px; cursor: pointer; font-size: 14px;">取消</button>
                    <button type="submit" style="padding: 8px 16px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 14px;">保存</button>
                </div>
            </form>
        </div>
    `;
    
    document.body.appendChild(editForm);
    
    // 填充表单数据
    populateEditForm();
    
    // 绑定取消按钮事件
    document.getElementById('cancel-edit').addEventListener('click', function() {
        document.body.removeChild(editForm);
    });
    
    // 绑定表单提交事件
    document.getElementById('profile-form').addEventListener('submit', function(e) {
        e.preventDefault();
        saveUserData();
        document.body.removeChild(editForm);
    });
}

// 填充编辑表单数据
function populateEditForm() {
    // 从页面获取当前数据并填充到表单
    document.getElementById('edit-nickname').value = document.getElementById('nickname').textContent;
    document.getElementById('edit-introduction').value = document.getElementById('introduction').textContent;
    
    // 提取生日日期
    const birthdayText = document.getElementById('birthday').textContent;
    const birthday = birthdayText.replace('生日：', '');
    document.getElementById('edit-birthday').value = birthday;
    
    // 提取学号
    const userIdText = document.getElementById('user_id').textContent;
    const userId = userIdText.replace('学号：', '');
    document.getElementById('edit-user_id').value = userId;
    
    // 提取社交信息
    const wechatText = document.getElementById('wechat').textContent;
    const wechat = wechatText.replace('微信: ', '');
    document.getElementById('edit-wechat').value = wechat;
    
    const qqText = document.getElementById('qq').textContent;
    const qq = qqText.replace('QQ: ', '');
    document.getElementById('edit-qq').value = qq;
    
    const emailText = document.getElementById('email').textContent;
    const email = emailText.replace('Email: ', '');
    document.getElementById('edit-email').value = email;
    
    // 提取详细档案
    const majorText = document.getElementById('major').textContent;
    const major = majorText.replace('所在学院/专业：', '');
    document.getElementById('edit-major').value = major;
    
    const gradeText = document.getElementById('grade').textContent;
    const grade = gradeText.replace('所在年级：', '');
    document.getElementById('edit-grade').value = grade;
    
    const phoneText = document.getElementById('phone').textContent;
    const phone = phoneText.replace('电话号码：', '');
    document.getElementById('edit-phone').value = phone;
    
    const addressText = document.getElementById('address').textContent;
    const address = addressText.replace('地址：', '');
    document.getElementById('edit-address').value = address;
    
    // 提取性别
    const genderText = document.getElementById('gender').textContent;
    const gender = genderText.replace('性别：', '');
    if (gender === '男') {
        document.getElementById('edit-gender-male').checked = true;
    } else if (gender === '女') {
        document.getElementById('edit-gender-female').checked = true;
    }
    
    // 提取头像URL
    document.getElementById('edit-avatar').value = document.getElementById('avatar').src;
}

// 保存用户数据
function saveUserData() {
    const userData = {
        id: currentUserId,
        user_id: document.getElementById('edit-user_id').value,
        nickname: document.getElementById('edit-nickname').value,
        introduction: document.getElementById('edit-introduction').value,
        birthday: document.getElementById('edit-birthday').value,
        wechat: document.getElementById('edit-wechat').value,
        qq: document.getElementById('edit-qq').value,
        email: document.getElementById('edit-email').value,
        major: document.getElementById('edit-major').value,
        grade: document.getElementById('edit-grade').value,
        phone: document.getElementById('edit-phone').value,
        address: document.getElementById('edit-address').value,
        gender: document.getElementById('edit-gender-male').checked ? 1 : 0,
        avatar: document.getElementById('edit-avatar').value
    };

    requestJson(`${API_BASE}/api/user/update`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
    .then(data => {
        if (!data) {
            throw new Error('后端未返回用户数据');
        }
        renderUserProfile(data);
        alert('保存成功！');
    })
    .catch(error => {
        console.error('保存失败:', error);
        alert('保存失败，请确认后端已启动(8080)并重试');
    });
}
