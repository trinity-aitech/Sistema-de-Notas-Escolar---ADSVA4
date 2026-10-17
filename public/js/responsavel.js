document.addEventListener('DOMContentLoaded', async () => {
    const user = window.schoolApi.getCurrentUser();
    let alunoId = "4"; // Lucas Mendes por padrão (ID 4 no esquema UML)

    if (user && user.perfil === 'RESPONSAVEL') {
        if (user.nome) {
            document.getElementById('userName').textContent = `${user.nome} (Responsável)`;
        }
        if (user.alunoVinculadoId) {
            alunoId = user.alunoVinculadoId;
        }
    }

    await carregarDadosFilho(alunoId);
});

async function carregarDadosFilho(alunoId) {
    try {
        const boletins = await window.schoolApi.getBoletim(alunoId);
        const tbody = document.getElementById('tabelaBoletimResponsavelBody') || document.querySelector('#tabelaBoletimResponsavel tbody');
        tbody.innerHTML = '';

        if (!boletins || boletins.length === 0) {
            tbody.innerHTML = '<tr><td colspan="10" style="text-align: center;">Nenhum registro encontrado.</td></tr>';
            return;
        }

        let somaMedia = 0;
        let somaFreq = 0;
        let totalFaltas = 0;
        let totalAulas = 0;

        boletins.forEach(b => {
            somaMedia += b.mediaFinal;
            somaFreq += b.percentualFrequencia;
            totalFaltas += b.totalFaltas;
            totalAulas += b.totalAulas;

            document.getElementById('filhoNome').textContent = b.alunoNome;
            document.getElementById('filhoMatricula').textContent = b.matricula;
            document.getElementById('filhoTurma').textContent = `${b.turmaCodigo} - Ensino Fundamental`;

            const n1 = b.notasPeriodos[0] !== null && b.notasPeriodos[0] !== undefined ? b.notasPeriodos[0].toFixed(1) : '-';
            const n2 = b.notasPeriodos[1] !== null && b.notasPeriodos[1] !== undefined ? b.notasPeriodos[1].toFixed(1) : '-';
            const n3 = b.notasPeriodos[2] !== null && b.notasPeriodos[2] !== undefined ? b.notasPeriodos[2].toFixed(1) : '-';
            const n4 = b.notasPeriodos[3] !== null && b.notasPeriodos[3] !== undefined ? b.notasPeriodos[3].toFixed(1) : '-';
            const recup = b.notaRecuperacao !== null && b.notaRecuperacao !== undefined ? b.notaRecuperacao.toFixed(1) : '-';

            let badgeClasse = 'badge-success';
            if (b.situacaoCodigo.includes('REPROVADO')) {
                badgeClasse = 'badge-danger';
            } else if (b.situacaoCodigo === 'RECUPERACAO') {
                badgeClasse = 'badge-warning';
            }

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>${b.disciplinaNome}</strong></td>
                <td style="text-align: center;">${n1}</td>
                <td style="text-align: center;">${n2}</td>
                <td style="text-align: center;">${n3}</td>
                <td style="text-align: center;">${n4}</td>
                <td style="text-align: center; font-weight: 600;">${b.mediaAnual.toFixed(2)}</td>
                <td style="text-align: center;">${recup}</td>
                <td style="text-align: center; font-weight: 700; color: var(--primary);">${b.mediaFinal.toFixed(2)}</td>
                <td style="text-align: center; font-weight: 600;">
                    ${b.percentualFrequencia.toFixed(1)}%
                </td>
                <td style="text-align: center;">
                    <span class="badge ${badgeClasse}">${b.situacaoDescricao}</span>
                </td>
            `;
            tbody.appendChild(tr);
        });

        const count = boletins.length;
        const mediaGeral = somaMedia / count;
        const freqGeral = somaFreq / count;

        document.getElementById('mediaFilho').textContent = mediaGeral.toFixed(2);
        document.getElementById('frequenciaFilho').textContent = `${freqGeral.toFixed(1)}%`;
        document.getElementById('faltasFilho').textContent = totalFaltas;
        document.getElementById('aulasFilho').textContent = totalAulas;

        const freqBar = document.getElementById('frequenciaBarFilho');
        freqBar.style.width = `${Math.min(100, freqGeral)}%`;

        const alertaBox = document.getElementById('alertaFrequenciaBox');
        if (freqGeral < 75.0) {
            freqBar.className = 'progress-bar danger';
            alertaBox.className = 'alert alert-danger';
            alertaBox.innerHTML = `
                <i data-lucide="alert-octagon" style="color: #dc2626; flex-shrink: 0; width: 24px; height: 24px;"></i>
                <div>
                    <strong>ALERTA CRÍTICO: Frequência Abaixo do Mínimo Legal (${freqGeral.toFixed(1)}%)</strong>
                    <p>O aluno atingiu nível de infrequência que acarreta <strong>reprovação por faltas segundo o Art. 24 da LDB</strong>. Favor contatar a direção imediatamente.</p>
                </div>
            `;
            document.getElementById('badgeSituacaoFilho').className = 'badge badge-danger';
            document.getElementById('badgeSituacaoFilho').innerHTML = '<i data-lucide="alert-triangle" style="width: 14px; height: 14px; margin-right: 4px;"></i> Risco de Reprovação';
        } else if (freqGeral < 80.0) {
            freqBar.className = 'progress-bar warning';
            alertaBox.className = 'alert alert-warning';
            alertaBox.innerHTML = `
                <i data-lucide="alert-triangle" style="color: #d97706; flex-shrink: 0; width: 24px; height: 24px;"></i>
                <div>
                    <strong>Aviso de Atenção: Frequência em Limite de Alerta (${freqGeral.toFixed(1)}%)</strong>
                    <p>A presença do estudante está próxima do corte de 75% estipulado pela LDB. Novas faltas não justificadas podem comprometer a aprovação.</p>
                </div>
            `;
            document.getElementById('badgeSituacaoFilho').className = 'badge badge-warning';
            document.getElementById('badgeSituacaoFilho').innerHTML = '<i data-lucide="alert-circle" style="width: 14px; height: 14px; margin-right: 4px;"></i> Atenção à Frequência';
        } else {
            freqBar.className = 'progress-bar success';
            document.getElementById('badgeSituacaoFilho').className = 'badge badge-success';
            document.getElementById('badgeSituacaoFilho').innerHTML = '<i data-lucide="check-circle" style="width: 14px; height: 14px; margin-right: 4px;"></i> Situação Regular';
        }

        if (window.lucide) {
            window.lucide.createIcons();
        }

        if (window.simpleDatatables && document.getElementById('tabelaBoletimResponsavel') && !window.respDataTable) {
            window.respDataTable = new simpleDatatables.DataTable('#tabelaBoletimResponsavel', {
                searchable: false,
                paging: false,
                sortable: true
            });
        }
    } catch (e) {
        console.error('Erro ao carregar dados do responsável:', e);
    }
}

function contatarCoordenacao() {
    if (window.Swal) {
        Swal.fire({
            icon: 'info',
            title: 'Atendimento à Família',
            text: 'Sua solicitação de contato foi registrada e enviada para a coordenação pedagógica.',
            confirmButtonColor: '#2563eb'
        });
    } else {
        alert('Mensagem enviada para a coordenação pedagógica da turma!');
    }
}

