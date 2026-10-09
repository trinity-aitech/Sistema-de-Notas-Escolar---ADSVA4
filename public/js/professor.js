let alunosTurma = [];
let avaliacoesTurma = [];
let notasMap = {};
let turmaAtual = null;

document.addEventListener('DOMContentLoaded', async () => {
    const user = window.schoolApi.getCurrentUser();
    if (user && user.nome) {
        document.getElementById('userName').textContent = user.nome;
    }
    await carregarDadosTurma();
});

function switchTab(tab) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.style.display = 'none');

    if (tab === 'notas') {
        document.querySelector('.tabs button:nth-child(1)').classList.add('active');
        document.getElementById('tab-notas').style.display = 'block';
    } else {
        document.querySelector('.tabs button:nth-child(2)').classList.add('active');
        document.getElementById('tab-frequencia').style.display = 'block';
    }
}

async function carregarDadosTurma() {
    try {
        const turmas = await window.schoolApi.getTurmas();
        if (turmas && turmas.length > 0) {
            turmaAtual = turmas[0];
            document.getElementById('turmaInfo').textContent = `${turmaAtual.serie} - Turma ${turmaAtual.codigo} (${turmaAtual.disciplinaNome})`;

            const tdId = turmaAtual.turmaDisciplinaId;
            const [alunos, avaliacoes, notas] = await Promise.all([
                window.schoolApi.getTurmaAlunos(turmaAtual.id),
                window.schoolApi.getAvaliacoes(tdId),
                window.schoolApi.getNotas(tdId)
            ]);

            alunosTurma = alunos;
            avaliacoesTurma = avaliacoes;

            notasMap = {};
            notas.forEach(n => {
                notasMap[`${n.matriculaId}_${n.periodo}`] = n.valorNota;
            });

            renderizarTabelaNotas();
            renderizarTabelaPresenca();
        }
    } catch (e) {
        console.error('Erro ao carregar dados:', e);
    }
}

function renderizarTabelaNotas() {
    const tbody = document.getElementById('tabelaNotasBody');
    tbody.innerHTML = '';

    alunosTurma.forEach(m => {
        const tr = document.createElement('tr');

        const n1 = notasMap[`${m.id}_1`] ?? '';
        const n2 = notasMap[`${m.id}_2`] ?? '';
        const n3 = notasMap[`${m.id}_3`] ?? '';
        const n4 = notasMap[`${m.id}_4`] ?? '';

        const notasValidas = [n1, n2, n3, n4].filter(v => v !== '' && !isNaN(Number(v))).map(Number);
        const media = notasValidas.length > 0 ? (notasValidas.reduce((a, b) => a + b, 0) / notasValidas.length) : null;

        let badgeStatus = '<span class="badge badge-info">Em Curso</span>';
        if (media !== null) {
            if (media >= 7.0) {
                badgeStatus = '<span class="badge badge-success">Aprovado (Média)</span>';
            } else if (media >= 4.0) {
                badgeStatus = '<span class="badge badge-warning">Em Recuperação</span>';
            } else {
                badgeStatus = '<span class="badge badge-danger">Reprovado por Nota</span>';
            }
        }

        tr.innerHTML = `
            <td><strong>${m.numeroChamada}</strong></td>
            <td><strong>${m.alunoNome}</strong></td>
            <td><code>${m.matriculaNumero}</code></td>
            <td style="text-align: center;">
                <input type="number" step="0.1" min="0" max="10" class="nota-input" id="nota_${m.id}_1" value="${n1}" oninput="recalcularLinha('${m.id}')">
            </td>
            <td style="text-align: center;">
                <input type="number" step="0.1" min="0" max="10" class="nota-input" id="nota_${m.id}_2" value="${n2}" oninput="recalcularLinha('${m.id}')">
            </td>
            <td style="text-align: center;">
                <input type="number" step="0.1" min="0" max="10" class="nota-input" id="nota_${m.id}_3" value="${n3}" oninput="recalcularLinha('${m.id}')">
            </td>
            <td style="text-align: center;">
                <input type="number" step="0.1" min="0" max="10" class="nota-input" id="nota_${m.id}_4" value="${n4}" oninput="recalcularLinha('${m.id}')">
            </td>
            <td style="text-align: center;" id="media_${m.id}" class="media-destaque">
                ${media !== null ? media.toFixed(2) : '-'}
            </td>
            <td style="text-align: center;" id="status_${m.id}">
                ${badgeStatus}
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function recalcularLinha(matriculaId) {
    const n1 = document.getElementById(`nota_${matriculaId}_1`).value;
    const n2 = document.getElementById(`nota_${matriculaId}_2`).value;
    const n3 = document.getElementById(`nota_${matriculaId}_3`).value;
    const n4 = document.getElementById(`nota_${matriculaId}_4`).value;

    const notasValidas = [n1, n2, n3, n4].filter(v => v !== '' && !isNaN(Number(v))).map(Number);
    const media = notasValidas.length > 0 ? (notasValidas.reduce((a, b) => a + b, 0) / notasValidas.length) : null;

    const mediaElem = document.getElementById(`media_${matriculaId}`);
    const statusElem = document.getElementById(`status_${matriculaId}`);

    if (media !== null) {
        mediaElem.textContent = media.toFixed(2);
        if (media >= 7.0) {
            statusElem.innerHTML = '<span class="badge badge-success">Aprovado (Média)</span>';
        } else if (media >= 4.0) {
            statusElem.innerHTML = '<span class="badge badge-warning">Em Recuperação</span>';
        } else {
            statusElem.innerHTML = '<span class="badge badge-danger">Reprovado por Nota</span>';
        }
    } else {
        mediaElem.textContent = '-';
        statusElem.innerHTML = '<span class="badge badge-info">Em Curso</span>';
    }
}

async function salvarTodasAsNotas() {
    let alteradas = 0;
    for (const m of alunosTurma) {
        for (let p = 1; p <= 4; p++) {
            const input = document.getElementById(`nota_${m.id}_${p}`);
            if (input && input.value !== '') {
                const valor = parseFloat(input.value);
                const av = avaliacoesTurma.find(a => a.periodo === p);
                if (av) {
                    await window.schoolApi.salvarNota(av.id, m.id, valor);
                    alteradas++;
                }
            }
        }
    }
    alert(`Sucesso! ${alteradas} notas foram atualizadas.`);
}

function renderizarTabelaPresenca() {
    const tbody = document.getElementById('tabelaPresencaBody');
    tbody.innerHTML = '';

    alunosTurma.forEach(m => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${m.numeroChamada}</strong></td>
            <td><strong>${m.alunoNome}</strong></td>
            <td><code>${m.matriculaNumero}</code></td>
            <td style="text-align: center;">
                <label style="cursor: pointer; display: inline-flex; align-items: center; gap: 0.4rem;">
                    <input type="checkbox" id="presenca_${m.id}" checked style="width: 20px; height: 20px;">
                    <span id="label_presenca_${m.id}">Presente</span>
                </label>
            </td>
            <td>
                <input type="text" id="justificativa_${m.id}" placeholder="Ex: Consulta médica (opcional)">
            </td>
        `;
        tbody.appendChild(tr);

        const cb = tr.querySelector(`#presenca_${m.id}`);
        cb.addEventListener('change', () => {
            document.getElementById(`label_presenca_${m.id}`).textContent = cb.checked ? 'Presente' : 'Faltou';
            document.getElementById(`label_presenca_${m.id}`).style.color = cb.checked ? 'var(--success)' : 'var(--danger)';
        });
    });
}

function marcarTodos(presente) {
    alunosTurma.forEach(m => {
        const cb = document.getElementById(`presenca_${m.id}`);
        if (cb) {
            cb.checked = presente;
            cb.dispatchEvent(new Event('change'));
        }
    });
}

async function salvarChamada() {
    const aulaId = "74444444-4444-4444-4444-444444444444";
    for (const m of alunosTurma) {
        const cb = document.getElementById(`presenca_${m.id}`);
        const just = document.getElementById(`justificativa_${m.id}`).value;
        if (cb) {
            await window.schoolApi.salvarPresenca(aulaId, m.id, cb.checked, just);
        }
    }
    alert('Chamada salva com sucesso!');
}
