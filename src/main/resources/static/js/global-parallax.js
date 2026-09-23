document.addEventListener('DOMContentLoaded', function() {
    // 首页和动态页已有专用视差，避免重复创建图层。
    if (document.getElementById('campusHero') || document.querySelector('.social-parallax')) {
        return;
    }

    var layer = document.createElement('div');
    layer.className = 'global-parallax';
    layer.setAttribute('aria-hidden', 'true');
    layer.innerHTML =
        '<div class="global-parallax__background"></div>' +
        '<div class="global-parallax__wash"></div>' +
        '<div class="global-parallax__character">' +
            '<img src="/images/home/campus-guide-character.png" alt="" draggable="false">' +
        '</div>';
    document.body.prepend(layer);

    var reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var finePointer = window.matchMedia('(pointer: fine)').matches;
    if (reduceMotion || !finePointer) return;

    var root = document.documentElement;
    var targetX = 0;
    var targetY = 0;
    var currentX = 0;
    var currentY = 0;
    var frameId = 0;

    function render() {
        currentX += (targetX - currentX) * 0.07;
        currentY += (targetY - currentY) * 0.07;

        root.style.setProperty('--global-bg-x', (-currentX * 7).toFixed(2) + 'px');
        root.style.setProperty('--global-bg-y', (-currentY * 5).toFixed(2) + 'px');
        root.style.setProperty('--global-character-x', (currentX * 17).toFixed(2) + 'px');
        root.style.setProperty('--global-character-y', (currentY * 12).toFixed(2) + 'px');

        if (Math.abs(targetX - currentX) < 0.001 && Math.abs(targetY - currentY) < 0.001) {
            frameId = 0;
            return;
        }
        frameId = requestAnimationFrame(render);
    }

    function requestRender() {
        if (!frameId) frameId = requestAnimationFrame(render);
    }

    window.addEventListener('pointermove', function(event) {
        targetX = Math.max(-1, Math.min(1, event.clientX / window.innerWidth * 2 - 1));
        targetY = Math.max(-1, Math.min(1, event.clientY / window.innerHeight * 2 - 1));
        requestRender();
    }, { passive: true });

    document.addEventListener('mouseleave', function() {
        targetX = 0;
        targetY = 0;
        requestRender();
    }, { passive: true });
});
