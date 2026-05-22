// ========== 全局状态 ==========
var currentPage = 1;
var currentCategory = 0;
var pageSize = 30;
var hasMore = true;
var loading = false;
var currentUserId = null;

// ========== 初始化 ==========
document.addEventListener('DOMContentLoaded', function() {
    loadUserProfile();
    initTabs();
    initCharCount();
    loadFeed(0);
    loadHotPosts();
});

// ========== 用户信息（已有） ==========
function loadUserProfile() {
    fetch('/api/user/current', { credentials: 'include' })
        .then(function(res) { 
            if (!res.ok) { throw new Error('HTTP ' + res.status); }
            return res.json(); 
        })
        .then(function(data) {
            if (data && data.id) {
                currentUserId = data.user_id || data.userId || null;
                document.getElementById('nickname').textContent = data.nickname || '用户';
                document.getElementById('introduction').textContent = data.introduction || '这个人很懒，什么都没写';
                document.getElementById('user_id').textContent = data.user_id || data.userId || '-';
                if (data.gender === 1) {
                    document.getElementById('gender').textContent = '男';
                } else if (data.gender === 0) {
                    document.getElementById('gender').textContent = '女';
                } else {
                    document.getElementById('gender').textContent = '-';
                }
                document.getElementById('major').textContent = data.major || '-';
                document.getElementById('grade').textContent = data.grade || '-';
                document.getElementById('wechat').textContent = data.wechat || '-';
                document.getElementById('qq').textContent = data.qq || '-';
                document.getElementById('email').textContent = data.email || '-';
                document.getElementById('phone').textContent = data.phone || '-';
                if (data.avatar && data.avatar.trim()) {
                    var holder = document.getElementById('avatarPlaceholder');
                    holder.style.backgroundImage = 'url(' + data.avatar + ')';
                    holder.style.backgroundSize = 'cover';
                    holder.style.backgroundPosition = 'center';
                    holder.textContent = '';
                }
            } else {
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

// ========== 分类标签 ==========
function initTabs() {
    var tabs = document.querySelectorAll('.tab-item');
    tabs.forEach(function(tab) {
        tab.addEventListener('click', function() {
            tabs.forEach(function(t) { t.classList.remove('active'); });
            this.classList.add('active');
            var category = parseInt(this.getAttribute('data-category'));
            currentCategory = category;
            currentPage = 1;
            document.getElementById('feedContainer').innerHTML = '';
            loadFeed(category);
        });
    });
}

// ========== 字数统计 ==========
function initCharCount() {
    var textarea = document.getElementById('publishContent');
    var counter = document.getElementById('charCount');
    if (textarea && counter) {
        textarea.addEventListener('input', function() {
            counter.textContent = this.value.length + ' / 200';
        });
    }
}

// ========== 发帖 ==========
function publish() {
    var content = document.getElementById('publishContent').value.trim();
    if (!content) {
        alert('请输入内容');
        return;
    }
    if (content.length > 200) {
        alert('内容不能超过 200 字');
        return;
    }

    var category = parseInt(document.getElementById('publishCategory').value);
    var isAnonymous = document.getElementById('publishAnonymous').checked ? 1 : 0;

    setPublishLoading(true);

    fetch('/api/post/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            content: content,
            category: category,
            isAnonymous: isAnonymous
        }),
        credentials: 'include'
    })
    .then(function(res) { return res.json(); })
    .then(function(data) {
        if (data.success) {
            document.getElementById('publishContent').value = '';
            document.getElementById('charCount').textContent = '0 / 200';
            currentCategory = 0;
            currentPage = 1;
            document.getElementById('feedContainer').innerHTML = '';
            document.querySelectorAll('.tab-item').forEach(function(t) { t.classList.remove('active'); });
            var allTab = document.querySelector('.tab-item[data-category="0"]');
            if (allTab) allTab.classList.add('active');
            loadFeed(0);
        } else {
            alert(data.message || '发布失败');
        }
    })
    .catch(function(err) {
        console.error('发布错误:', err);
        alert('发布失败，请稍后重试');
    })
    .finally(function() {
        setPublishLoading(false);
    });
}

function setPublishLoading(loading) {
    var btn = document.getElementById('publishBtn');
    if (btn) {
        btn.disabled = loading;
        btn.textContent = loading ? '发布中...' : '发布';
        btn.style.opacity = loading ? '0.7' : '1';
    }
}

// ========== 加载帖子列表 ==========
function loadFeed(category) {
    if (loading) return;
    loading = true;

    var url = '/api/post/list?page=' + currentPage + '&size=' + pageSize;
    if (category > 0) {
        url += '&category=' + category;
    }

    fetch(url, { credentials: 'include' })
        .then(function(res) {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.json();
        })
        .then(function(data) {
            var posts = data.data || data || [];
            var total = data.total || posts.length;

            if (!Array.isArray(posts)) { posts = []; }

            if (currentPage === 1) {
                document.getElementById('feedContainer').innerHTML = '';
            }

            posts.forEach(function(post) {
                var card = buildPostCard(post);
                document.getElementById('feedContainer').appendChild(card);
            });

            hasMore = (currentPage * pageSize) < total;
            var loadMoreEl = document.getElementById('loadMore');
            if (loadMoreEl) {
                loadMoreEl.style.display = hasMore ? 'block' : 'none';
            }

            if (posts.length === 0 && currentPage === 1) {
                document.getElementById('feedContainer').innerHTML = '<div class="empty-feed">暂无动态，快来发布第一条吧！</div>';
            }
        })
        .catch(function(err) {
            console.error('加载帖子失败:', err);
            if (currentPage === 1) {
                document.getElementById('feedContainer').innerHTML = '<div class="empty-feed">加载失败，请刷新重试</div>';
            }
        })
        .finally(function() {
            loading = false;
        });
}

function loadMore() {
    if (!hasMore || loading) return;
    currentPage++;
    loadFeed(currentCategory);
}

function refreshFeed() {
    currentPage = 1;
    loading = false;
    document.getElementById('feedContainer').innerHTML = '';
    loadFeed(currentCategory);
}

// ========== 构建帖子卡片 ==========
function buildPostCard(post) {
    var card = document.createElement('div');
    card.className = 'feed-card';

    // 分类标签名
    var categoryNames = ['', '发发牢骚', '吹吹牛皮', '校园互助', '联名请愿'];
    var catName = categoryNames[post.category] || '';

    // 显示名称
    var displayName = post.isAnonymous === 1 ? '匿名用户' : (post.username || '用户');
    var avatarText = post.isAnonymous === 1 ? '匿' : (post.username ? post.username.charAt(0) : '用');

    // 时间格式化
    var timeStr = post.createdAt ? formatTime(post.createdAt) : '';

    // 判断是否自己的帖子
    var isAuthor = currentUserId && post.userId && String(currentUserId) === String(post.userId);
    var deleteButtonHtml = isAuthor ?
        '<button class="footer-btn delete-btn" onclick="deletePost(' + post.id + ', this)">删除</button>' :
        '';

    card.innerHTML = 
        '<div class="feed-header">' +
            '<div class="feed-author">' +
                '<div class="author-avatar-placeholder">' + avatarText + '</div>' +
                '<div class="author-info">' +
                    '<span class="author-name">' + displayName + '</span>' +
                    '<span class="publish-time">' + timeStr + (catName ? ' · ' + catName : '') + '</span>' +
                '</div>' +
            '</div>' +
        '</div>' +
        '<div class="feed-body">' +
            '<div class="feed-text">' + escapeHtml(post.content || '') + '</div>' +
            (post.images ? '<div class="feed-images">' + renderImages(post.images) + '</div>' : '') +
        '</div>' +
        '<div class="feed-footer">' +
            '<button class="footer-btn" onclick="sharePost(' + post.id + ')">转发 ' + (post.shareCount || 0) + '</button>' +
            '<button class="footer-btn" onclick="commentPost(' + post.id + ')">评论 ' + (post.commentCount || 0) + '</button>' +
            '<button class="footer-btn" onclick="likePost(' + post.id + ', this)">点赞 ' + (post.likeCount || 0) + '</button>' +
            deleteButtonHtml +
        '</div>';

    return card;
}

function renderImages(images) {
    if (!images) return '';
    var urls = images.split(',');
    var html = '<div class="feed-image-row">';
    urls.forEach(function(url) {
        html += '<img src="' + url.trim() + '" class="feed-image" onclick="viewImage(\'' + url.trim() + '\')">';
    });
    html += '</div>';
    return html;
}

// ========== 点赞 ==========
function likePost(postId, btn) {
    fetch('/api/post/' + postId + '/like', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    })
    .then(function(res) { return res.json(); })
    .then(function(data) {
        if (data.success) {
            var text = btn.textContent;
            var match = text.match(/\d+/);
            var count = match ? parseInt(match[0]) : 0;
            btn.textContent = '点赞 ' + (count + 1);
            btn.style.color = '#ff6b6b';
        } else {
            alert(data.message || '点赞失败');
        }
    })
    .catch(function(err) {
        console.error('点赞错误:', err);
    });
}

function sharePost(postId) {
    alert('转发功能待实现');
}

function commentPost(postId) {
    alert('评论功能待实现');
}

function viewImage(url) {
    window.open(url, '_blank');
}

// ========== 删除帖子 ==========
function deletePost(postId, btn) {
    if (!confirm('确定要删除这条帖子吗？删除后无法恢复。')) {
        return;
    }
    var originalText = btn.textContent;
    btn.disabled = true;
    btn.textContent = '删除中...';
    fetch('/api/post/' + postId, {
        method: 'DELETE',
        credentials: 'include'
    })
    .then(function(res) { return res.json(); })
    .then(function(data) {
        if (data.success) {
            var card = btn.closest('.feed-card');
            if (card) {
                card.style.opacity = '0';
                card.style.transition = 'opacity 0.3s ease';
                setTimeout(function() { card.remove(); }, 300);
            }
        } else {
            alert(data.message || '删除失败');
            btn.disabled = false;
            btn.textContent = originalText;
        }
    })
    .catch(function(err) {
        alert('删除失败，请稍后重试');
        btn.disabled = false;
        btn.textContent = originalText;
    });
}

// ========== 点赞榜 ==========
function loadHotPosts() {
    fetch('/api/post/hot', { credentials: 'include' })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            var posts = data.data || data || [];
            if (!Array.isArray(posts)) { posts = []; }

            var html = '';
            posts.forEach(function(post, index) {
                var displayName = post.isAnonymous === 1 ? '匿名用户' : (post.username || '用户');
                var text = (post.content || '').substring(0, 15) + (post.content && post.content.length > 15 ? '...' : '');
                html += '<div class="hot-item">' +
                    '<span class="hot-rank rank-' + (index + 1) + '">' + (index + 1) + '</span>' +
                    '<span class="hot-text" title="' + escapeHtml(post.content || '') + '">' + displayName + ': ' + escapeHtml(text) + '</span>' +
                    '<span class="hot-like-count">' + (post.likeCount || 0) + '赞</span>' +
                '</div>';
            });

            if (posts.length === 0) {
                html = '<div class="hot-item"><span class="hot-text">暂无数据</span></div>';
            }

            document.getElementById('hotList').innerHTML = html;
        })
        .catch(function(err) {
            console.error('加载点赞榜失败:', err);
        });
}

// ========== 工具函数 ==========
function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function formatTime(dateStr) {
    if (!dateStr) return '';
    var d = new Date(dateStr);
    if (isNaN(d.getTime())) {
        var parts = dateStr.split(/[-T:.]/);
        if (parts.length >= 6) {
            d = new Date(parts[0], parts[1] - 1, parts[2], parts[3], parts[4], parts[5]);
        }
    }
    if (isNaN(d.getTime())) return dateStr;

    var now = new Date();
    var diff = now - d;
    if (diff < 60000) return '刚刚';
    if (diff < 3600000) return Math.floor(diff / 60000) + ' 分钟前';
    if (diff < 86400000) return Math.floor(diff / 3600000) + ' 小时前';
    if (diff < 604800000) return Math.floor(diff / 86400000) + ' 天前';

    var y = d.getFullYear();
    var m = ('0' + (d.getMonth() + 1)).slice(-2);
    var day = ('0' + d.getDate()).slice(-2);
    var h = ('0' + d.getHours()).slice(-2);
    var min = ('0' + d.getMinutes()).slice(-2);
    return y + '-' + m + '-' + day + ' ' + h + ':' + min;
}
