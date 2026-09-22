// ========== 全局状态 ==========
var currentPage = 1;        // 当前页码
var currentCategory = 0;    // 当前分类（0=全部）
var pageSize = 30;          // 每页条数
var hasMore = true;         // 是否还有更多数据
var loading = false;        // 加载中标记，防止重复请求
var currentUserId = null;   // 当前登录用户学号

// ========== 初始化 ==========
document.addEventListener('DOMContentLoaded', function() {
    loadUserProfile();
    initTabs();
    initCharCount();
    loadFeed(0);
    loadHotPosts();
});

// ========== 用户信息 ==========
// 加载当前登录用户资料并渲染左侧卡片；未登录时显示提示
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
// 点击分类标签切换后重置分页并重新加载列表
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
// 输入框实时显示已输入字数
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
// 校验正文和分类后调用 /api/post/create，成功后刷新列表
function publish() {
    // TODO(P1-多图发布): 先收集文件并用 FormData 调上传接口；全部上传成功后，将返回的 URL 数组
    // 连同正文一起提交 create。上传期间禁用发布按钮，失败时保留正文并提示具体文件错误。
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

// 发布按钮加载态（防连点）
function setPublishLoading(loading) {
    var btn = document.getElementById('publishBtn');
    if (btn) {
        btn.disabled = loading;
        btn.textContent = loading ? '发布中...' : '发布';
        btn.style.opacity = loading ? '0.7' : '1';
    }
}

// ========== 加载帖子列表 ==========
// 分页加载帖子（可带分类筛选），追加到动态流容器
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

// 加载下一页
function loadMore() {
    if (!hasMore || loading) return;
    currentPage++;
    loadFeed(currentCategory);
}

// 重置分页并刷新列表
function refreshFeed() {
    currentPage = 1;
    loading = false;
    document.getElementById('feedContainer').innerHTML = '';
    loadFeed(currentCategory);
}

// ========== 构建帖子卡片 ==========
// 由帖子数据拼装卡片 DOM（匿名帖隐藏真实昵称，正文做 XSS 转义）
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

    // 删除权限和点赞状态都以后端计算结果为准。
    var deleteButtonHtml = post.canDelete ?
        '<button class="footer-btn delete-btn" onclick="deletePost(' + post.id + ', this)">删除</button>' :
        '';
    var liked = post.liked === true;

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
            '<button class="footer-btn like-btn' + (liked ? ' liked' : '') + '" data-liked="' + liked + '" aria-pressed="' + liked + '" onclick="likePost(' + post.id + ', this)">点赞 ' + (post.likeCount || 0) + '</button>' +
            deleteButtonHtml +
        '</div>';

    return card;
}

// 渲染帖子配图（images 为逗号分隔的 URL）
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
// 未点赞时 POST，已点赞时 DELETE；计数始终采用服务端返回值。
function likePost(postId, btn) {
    if (btn.disabled) return;

    var currentlyLiked = btn.dataset.liked === 'true';
    var currentCount = parseInt(btn.textContent.match(/\d+/)?.[0] || '0', 10);
    btn.disabled = true;

    fetch('/api/post/' + postId + '/like', {
        method: currentlyLiked ? 'DELETE' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    })
    .then(function(res) {
        if (!res.ok) { throw new Error('HTTP ' + res.status); }
        return res.json();
    })
    .then(function(data) {
        if (data.success) {
            // 新接口直接返回最终状态；兼容旧接口时按本次操作推导，
            // 避免缺少 liked 字段被误判为“始终未点赞”，导致连续发送 POST。
            var liked = typeof data.liked === 'boolean' ? data.liked : !currentlyLiked;
            var fallbackCount = Math.max(0, currentCount + (liked ? 1 : -1));
            var count = Number.isFinite(Number(data.likeCount))
                ? Number(data.likeCount)
                : fallbackCount;
            btn.dataset.liked = String(liked);
            btn.setAttribute('aria-pressed', String(liked));
            btn.classList.toggle('liked', liked);
            btn.textContent = '点赞 ' + count;
        } else {
            alert(data.message || '点赞失败');
        }
    })
    .catch(function(err) {
        console.error('点赞错误:', err);
        alert('操作失败，请稍后重试');
    })
    .finally(function() {
        btn.disabled = false;
    });
}

// 转发帖子（待实现）
function sharePost(postId) {
    // TODO(P2): 先实现复制帖子详情链接；需要站内转发时再增加转发数据模型。
    alert('转发功能待实现');
}

// 评论帖子（待实现）
function commentPost(postId) {
    // TODO(P1-评论交互): 打开评论抽屉/详情页后分页加载评论；提交成功后插入服务端返回的评论，
    // 同步更新帖子卡片评论数。回复时携带父评论 id；仅当 comment.canDelete=true 时显示删除按钮。
    // 所有昵称、正文都用 textContent 创建节点，避免把用户内容拼进 innerHTML。
    alert('评论功能待实现');
}

// 新窗口查看原图
function viewImage(url) {
    window.open(url, '_blank');
}

// ========== 删除帖子 ==========
// 确认后调用 DELETE 接口软删自己的帖子，成功后淡出移除卡片
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
// 加载热门帖子（按点赞数），渲染右侧榜单
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
// HTML 转义，防止 XSS
function escapeHtml(str) {
    var div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

// 时间格式化：1 小时内显示相对时间，超过 7 天显示完整日期
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
