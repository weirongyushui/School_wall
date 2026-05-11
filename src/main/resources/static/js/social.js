// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    loadUserProfile();
    initTabs();
    initCharCount();
});

// 加载用户资料
function loadUserProfile() {
    fetch('/api/user/current', { credentials: 'include' })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data && data.id) {
                document.getElementById('nickname').textContent = data.nickname || '用户';
                document.getElementById('user_id').textContent = data.user_id || data.userId || '-';
                document.getElementById('gender').textContent = data.gender === 1 ? '男' : (data.gender === 0 ? '女' : '-');
                document.getElementById('major').textContent = data.major || '-';
                document.getElementById('grade').textContent = data.grade || '-';
            }
        })
        .catch(function(err) { console.log('加载用户信息失败:', err); });
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
