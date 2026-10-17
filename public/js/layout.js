/**
 * Layout estilo Gmail: navegação pela sidebar, busca global,
 * recolher/expandir menu e avatar com iniciais do usuário.
 */
(function () {
    const MOBILE_BP = 900;

    function isMobile() {
        return window.innerWidth <= MOBILE_BP;
    }

    function closeSidebarMobile() {
        document.body.classList.remove('sidebar-open');
    }

    function applySearch(term) {
        const q = (term || '').toLowerCase().trim();
        document.querySelectorAll('.section.active tbody tr').forEach(tr => {
            tr.style.display = !q || tr.textContent.toLowerCase().includes(q) ? '' : 'none';
        });
    }

    function showSection(name, updateHash = true) {
        const target = document.getElementById(`section-${name}`);
        if (!target) return;

        document.querySelectorAll('.section').forEach(s => s.classList.toggle('active', s === target));
        document.querySelectorAll('.nav-item[data-section]').forEach(a => {
            a.classList.toggle('active', a.dataset.section === name);
        });

        document.body.dataset.section = name;
        if (updateHash) history.replaceState(null, '', `#${name}`);
        closeSidebarMobile();

        const search = document.getElementById('globalSearch');
        if (search) applySearch(search.value);

        const main = document.querySelector('.main');
        if (main) main.scrollTop = 0;

        document.dispatchEvent(new CustomEvent('sectionchange', { detail: name }));
    }

    function initials(name) {
        const parts = (name || '')
            .replace(/\(.*?\)/g, '')
            .replace(/^(Prof\.?|Profa\.?)\s+/i, '')
            .trim()
            .split(/\s+/)
            .filter(Boolean);
        if (parts.length === 0) return '?';
        const first = parts[0][0] || '';
        const last = parts.length > 1 ? parts[parts.length - 1][0] : '';
        return (first + last).toUpperCase();
    }

    function syncAvatar() {
        const nameEl = document.getElementById('userName');
        if (!nameEl) return;
        const text = initials(nameEl.textContent);
        document.querySelectorAll('[data-avatar]').forEach(el => { el.textContent = text; });
    }

    document.addEventListener('DOMContentLoaded', () => {
        // Navegação da sidebar
        document.querySelectorAll('.nav-item[data-section]').forEach(item => {
            item.addEventListener('click', e => {
                e.preventDefault();
                showSection(item.dataset.section);
            });
        });

        // Botão de menu (recolhe no desktop, abre gaveta no mobile)
        const menuBtn = document.getElementById('menuToggle');
        if (menuBtn) {
            menuBtn.addEventListener('click', () => {
                if (isMobile()) {
                    document.body.classList.toggle('sidebar-open');
                } else {
                    document.body.classList.toggle('sidebar-collapsed');
                    localStorage.setItem('sidebar_collapsed', document.body.classList.contains('sidebar-collapsed') ? '1' : '0');
                }
            });
        }
        if (localStorage.getItem('sidebar_collapsed') === '1' && !isMobile()) {
            document.body.classList.add('sidebar-collapsed');
        }

        const scrim = document.getElementById('scrim');
        if (scrim) scrim.addEventListener('click', closeSidebarMobile);

        // Busca global
        const search = document.getElementById('globalSearch');
        if (search) {
            search.addEventListener('input', () => applySearch(search.value));
            document.addEventListener('keydown', e => {
                if (e.key === '/' && document.activeElement.tagName !== 'INPUT') {
                    e.preventDefault();
                    search.focus();
                }
            });
        }

        // Seção inicial (hash da URL ou primeira da sidebar)
        const firstNav = document.querySelector('.nav-item[data-section]');
        const fromHash = location.hash.replace('#', '');
        if (fromHash && document.getElementById(`section-${fromHash}`)) {
            showSection(fromHash, false);
        } else if (firstNav) {
            showSection(firstNav.dataset.section, false);
        }

        // Avatar acompanha o nome do usuário
        const nameEl = document.getElementById('userName');
        if (nameEl) {
            syncAvatar();
            new MutationObserver(syncAvatar).observe(nameEl, { childList: true, characterData: true, subtree: true });
        }

        if (window.lucide) window.lucide.createIcons();
    });

    window.layout = { showSection, applySearch, initials };
})();
