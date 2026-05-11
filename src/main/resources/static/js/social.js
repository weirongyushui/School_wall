// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    loadUserProfile();
    initTabs();
    initCharCount();
});

// 加载用户资料
function loadUserProfile() {
    fetch('/api/user/current', { credentials: 'include' })
        .then(function(res) { 
            if (!res.ok) {
                throw new Error('HTTP ' + res.status);
            }
            return res.json(); 
        })
        .then(function(data) {
            if (data && data.id) {
                // 昵称
                document.getElementById('nickname').textContent = data.nickname || '用户';
                
                // 个人简介
                document.getElementById('introduction').textContent = data.introduction || '这个人很懒，什么都没写';
                
                // 学号
                document.getElementById('user_id').textContent = data.user_id || data.userId || '-';
                
                // 性别
                if (data.gender === 1) {
                    document.getElementById('gender').textContent = '男';
                } else if (data.gender === 0) {
                    document.getElementById('gender').textContent = '女';
                } else {
                    document.getElementById('gender').textContent = '-';
                }
                
                // 学院/专业
                document.getElementById('major').textContent = data.major || '-';
                
                // 年级
                document.getElementById('grade').textContent = data.grade || '-';
                
                // 微信
                document.getElementById('wechat').textContent = data.wechat || '-';
                
                // QQ
                document.getElementById('qq').textContent = data.qq || '-';
                
                // 邮箱
                document.getElementById('email').textContent = data.email || '-';
                
                // 电话
                document.getElementById('phone').textContent = data.phone || '-';
                
                // 头像（如果有头像 URL，更新占位符）
                if (data.avatar && data.avatar.trim()) {
                    var avatarPlaceholder = document.getElementById('avatarPlaceholder');
                    avatarPlaceholder.style.backgroundImage = 'url(' + data.avatar + ')';
                    avatarPlaceholder.style.backgroundSize = 'cover';
                    avatarPlaceholder.style.backgroundPosition = 'center';
                    avatarPlaceholder.textContent = '';
                }
            } else {
                // 未登录或获取失败
                document.getElementById('nickname').textContent = '未登录';
                document.getElementById('introduction').textContent = '请先登录';
            }
        })
        .catch(function(err) { 
            console.log('加载用户信息失败:', err);
            document.getElementById('nickname').textContent = '加载失败';
            document.getElementById('introduction').textContent = '请刷新重试';
        });
}

// 初始化分类标签
function initTabs() {
    var tabs = document.querySelectorAll('.tab-item');
    tabs.forEach(function(tab) {
        tab.addEventListener('click', function() {
            tabs.forEach(function(t) { t.classList.remove('active'); });
            this.classList.add('active');
        });
    });
}

// 初始化字数统计
function initCharCount() {
    var textarea = document.getElementById('publishContent');
    var counter = document.getElementById('charCount');
    if (textarea && counter) {
        textarea.addEventListener('input', function() {
            var len = this.value.length;
            counter.textContent = len + ' / 200';
        });
    }
}
