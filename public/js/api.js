/**
 * Módulo de API do Sistema Escolar
 * Comunicação direta com o backend Java e compatível com Vercel e Supabase.
 */

const API_CONFIG = {
    // Se estiver rodando na Vercel com backend remoto, pode configurar API_BASE_URL
    baseUrl: window.API_BASE_URL || '/api',
    // Configurações do Supabase (opcional para conexão direta no frontend)
    supabaseUrl: window.SUPABASE_URL || localStorage.getItem('supabase_url') || '',
    supabaseAnonKey: window.SUPABASE_ANON_KEY || localStorage.getItem('supabase_key') || ''
};

const api = {
    async request(endpoint, options = {}) {
        const url = `${API_CONFIG.baseUrl}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        try {
            const response = await fetch(url, { ...options, headers });
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.erro || `Erro HTTP ${response.status}`);
            }
            return await response.json();
        } catch (error) {
            console.warn(`[API] Falha na chamada ${endpoint}:`, error.message);
            throw error;
        }
    },

    // Autenticação
    async login(email, password) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
    },

    // Turmas e Alunos
    async getTurmas() {
        return this.request('/turmas');
    },

    async getTurmaAlunos(turmaId) {
        return this.request(`/turmas/alunos?turmaId=${encodeURIComponent(turmaId)}`);
    },

    // Notas e Avaliações
    async getAvaliacoes(turmaDisciplinaId) {
        return this.request(`/avaliacoes?turmaDisciplinaId=${encodeURIComponent(turmaDisciplinaId)}`);
    },

    async getNotas(turmaDisciplinaId) {
        return this.request(`/notas?turmaDisciplinaId=${encodeURIComponent(turmaDisciplinaId)}`);
    },

    async salvarNota(avaliacaoId, matriculaId, valorNota, observacao = '') {
        return this.request('/notas', {
            method: 'POST',
            body: JSON.stringify({ avaliacaoId, matriculaId, valorNota: String(valorNota), observacao })
        });
    },

    // Diário e Presença
    async getAulas(turmaDisciplinaId) {
        return this.request(`/aulas?turmaDisciplinaId=${encodeURIComponent(turmaDisciplinaId)}`);
    },

    async getPresencas(aulaId) {
        return this.request(`/presencas?aulaId=${encodeURIComponent(aulaId)}`);
    },

    async salvarPresenca(aulaId, matriculaId, presente, justificativa = '') {
        return this.request('/presencas', {
            method: 'POST',
            body: JSON.stringify({ aulaId, matriculaId, presente: String(presente), justificativa })
        });
    },

    // Boletim e Regras
    async getBoletim(alunoId) {
        return this.request(`/boletim?alunoId=${encodeURIComponent(alunoId)}`);
    },

    async getRegras() {
        return this.request('/regras');
    },

    // Gerenciamento de Sessão Local
    getCurrentUser() {
        const user = localStorage.getItem('usuario_logado');
        return user ? JSON.parse(user) : null;
    },

    setCurrentUser(user) {
        localStorage.setItem('usuario_logado', JSON.stringify(user));
    },

    logout() {
        localStorage.removeItem('usuario_logado');
        window.location.href = '/index.html';
    }
};

window.schoolApi = api;
