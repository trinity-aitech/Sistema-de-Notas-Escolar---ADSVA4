document.addEventListener('DOMContentLoaded', async () => {
    const user = window.schoolApi.getCurrentUser();
    let alunoId = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbb01"; // Lucas Mendes

    if (user && user.perfil === 'ALUNO') {
        if (user.nome) {
            document.getElementById('userName').textContent = user.nome;
            document.getElementById('alunoNomeCompleto').textContent = user.nome;
        }
        if (user.alunoId) {
            alunoId = user.alunoId;
        }
    }

    await carregarBoletim(alunoId);
});

async function carregarBoletim(alunoId) {
    try {
        const boletins = await window.schoolApi.getBoletim(alunoId);
        const tbody = document.getElementById('tabelaBoletimBody');
        tbody.innerHTML = '';

        if (!boletins || boletins.length === 0) {
            tbody.innerHTML = '<tr><td colspan="10" style="text-align: center;">Nenhuma nota lançada até o momento.</td></tr>';
            return;
        }

        let mediaGeralSoma = 0;
        let totalFrequenciaSoma = 0;
        let totalAulasSoma = 0;
        let totalFaltasSoma = 0;
        let temAlertaLdb = false;

        boletins.forEach(b => {
            mediaGeralSoma += b.mediaFinal;
            totalFrequenciaSoma += b.percentualFrequencia;
            totalAulasSoma += b.totalAulas;
            totalFaltasSoma += b.totalFaltas;
            if (b.alertaInfrequenciaLdb) temAlertaLdb = true;

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
                    ${b.percentualFrequencia < 75.0 ? ' ❌' : ' ✔️'}
                </td>
                <td style="text-align: center;">
                    <span class="badge ${badgeClasse}">${b.situacaoDescricao}</span>
                </td>
            `;
            tbody.appendChild(tr);

            document.getElementById('matriculaVal').textContent = b.matricula;
            document.getElementById('turmaVal').textContent = `${b.turmaCodigo} - Fundamental`;
        });

        const count = boletins.length;
        const mediaGeral = mediaGeralSoma / count;
        const freqGeral = totalFrequenciaSoma / count;

        document.getElementById('mediaGeralVal').textContent = mediaGeral.toFixed(2);
        document.getElementById('frequenciaVal').textContent = `${freqGeral.toFixed(1)}%`;
        document.getElementById('totalAulasVal').textContent = totalAulasSoma;
        document.getElementById('totalFaltasVal').textContent = totalFaltasSoma;

        const freqBar = document.getElementById('frequenciaBar');
        freqBar.style.width = `${Math.min(100, freqGeral)}%`;

        if (freqGeral < 75.0) {
            freqBar.className = 'progress-bar danger';
        } else if (freqGeral < 80.0) {
            freqBar.className = 'progress-bar warning';
        } else {
            freqBar.className = 'progress-bar success';
        }

        if (temAlertaLdb) {
            document.getElementById('alertaLdbContainer').style.display = 'flex';
        }
    } catch (e) {
        console.error('Erro ao buscar boletim:', e);
    }
}
