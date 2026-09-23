document.addEventListener('DOMContentLoaded', () => {
    const hero = document.getElementById('campusHero');
    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)');

    if (!hero || reducedMotion.matches || !window.matchMedia('(pointer: fine)').matches) {
        return;
    }

    let targetX = 0;
    let targetY = 0;
    let currentX = 0;
    let currentY = 0;
    let frameId = 0;

    const render = () => {
        currentX += (targetX - currentX) * 0.075;
        currentY += (targetY - currentY) * 0.075;
        hero.style.setProperty('--hero-bg-x', (-currentX * 7).toFixed(2) + 'px');
        hero.style.setProperty('--hero-bg-y', (-currentY * 5).toFixed(2) + 'px');
        hero.style.setProperty('--hero-character-x', (currentX * 18).toFixed(2) + 'px');
        hero.style.setProperty('--hero-character-y', (currentY * 13).toFixed(2) + 'px');

        const settled = Math.abs(targetX - currentX) < 0.001
            && Math.abs(targetY - currentY) < 0.001;
        if (settled) {
            frameId = 0;
            return;
        }
        frameId = requestAnimationFrame(render);
    };

    const requestRender = () => {
        if (!frameId) frameId = requestAnimationFrame(render);
    };

    hero.addEventListener('pointermove', (event) => {
        const bounds = hero.getBoundingClientRect();
        targetX = Math.max(-1, Math.min(1, ((event.clientX - bounds.left) / bounds.width - 0.5) * 2));
        targetY = Math.max(-1, Math.min(1, ((event.clientY - bounds.top) / bounds.height - 0.5) * 2));
        requestRender();
    }, { passive: true });

    hero.addEventListener('pointerleave', () => {
        targetX = 0;
        targetY = 0;
        requestRender();
    }, { passive: true });
});
