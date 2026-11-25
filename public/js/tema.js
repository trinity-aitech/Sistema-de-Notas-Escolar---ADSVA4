/**
 * Alternancia entre tema claro e escuro.
 * Carregado no <head> para aplicar o tema antes da pagina aparecer.
 * Sem escolha salva, segue o tema do sistema operacional.
 */
(function () {
    const CHAVE = 'tema_preferido';

    function temaSalvo() {
        try {
            return localStorage.getItem(CHAVE);
        } catch (e) {
            return null;
        }
    }

    function temaDoSistema() {
        return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    function aplicar(tema) {
        document.documentElement.setAttribute('data-theme', tema);
        const botao = document.getElementById('temaToggle');
        if (botao) {
            botao.title = tema === 'dark' ? 'Usar tema claro' : 'Usar tema escuro';
            botao.setAttribute('aria-label', botao.title);
        }
    }

    function alternar() {
        const novo = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
        try {
            localStorage.setItem(CHAVE, novo);
        } catch (e) {
            // Sem armazenamento disponivel: o tema vale so para esta visita
        }
        aplicar(novo);
    }

    aplicar(temaSalvo() || temaDoSistema());

    // Acompanha o sistema operacional enquanto o usuario nao escolher um tema
    if (window.matchMedia) {
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', e => {
            if (!temaSalvo()) aplicar(e.matches ? 'dark' : 'light');
        });
    }

    document.addEventListener('DOMContentLoaded', () => {
        const botao = document.getElementById('temaToggle');
        if (botao) botao.addEventListener('click', alternar);
        aplicar(document.documentElement.getAttribute('data-theme'));
    });

    window.tema = { alternar, aplicar };
})();
