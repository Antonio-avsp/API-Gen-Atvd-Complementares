package com.pi.apigenatvdcomplementares.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pi.apigenatvdcomplementares.dto.AlunoCsvResponseDTO;
import com.pi.apigenatvdcomplementares.dto.AlunoDTO;
import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;

/**
 * Serviço responsável por processar arquivos CSV de cadastro em lote de alunos.
 *
 * Formato esperado do CSV:
 * - Primeira linha: cabeçalho (ignorado)
 * - Colunas obrigatórias (em ordem): nome, email, matricula, cursoId
 * - Separador: vírgula (,)
 *
 * Exemplo de CSV válido:
 * nome,email,matricula,cursoId
 * João Silva,joao@email.com,2024001,1
 * Maria Souza,maria@email.com,2024002,1
 *
 * Regras de negócio:
 * - Se o e-mail não tiver usuário cadastrado, o sistema cria automaticamente
 * um usuário com perfil ALUNO e senha padrão igual à matrícula.
 * - Se o usuário já existir com perfil diferente de ALUNO, a linha falha.
 * - Apenas COORDENADOR e SUPER_ADMIN podem usar este endpoint.
 */
@Service
public class AlunoCsvService {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String SEPARADOR = ",";
    private static final int COL_NOME = 0;
    private static final int COL_EMAIL = 1;
    private static final int COL_MATRICULA = 2;
    private static final int COL_CURSO_ID = 3;
    private static final int TOTAL_COLUNAS = 4;

    /**
     * Processa o arquivo CSV e cadastra os alunos linha por linha.
     * Cada linha é tratada de forma independente — falhas individuais não
     * interrompem o processamento das demais linhas.
     *
     * @param arquivo arquivo CSV enviado via multipart
     * @return resultado detalhado do processamento
     */
    public AlunoCsvResponseDTO processarCsv(MultipartFile arquivo) {
        AlunoCsvResponseDTO resultado = new AlunoCsvResponseDTO();

        validarArquivo(arquivo);

        List<String[]> linhas = lerLinhasCsv(arquivo);

        // Começa no índice 1 para pular o cabeçalho
        for (int i = 1; i < linhas.size(); i++) {
            int numeroLinha = i + 1; // linha real no arquivo (1-based, incluindo cabeçalho)
            String[] campos = linhas.get(i);
            String emailCampo = obterCampoSeguro(campos, COL_EMAIL);
            String matriculaCampo = obterCampoSeguro(campos, COL_MATRICULA);

            try {
                processarLinha(campos, numeroLinha);
                resultado.adicionarSucesso(numeroLinha, emailCampo, matriculaCampo);
            } catch (Exception e) {
                resultado.adicionarFalha(numeroLinha, emailCampo, matriculaCampo, e.getMessage());
            }
        }

        return resultado;
    }

    // ── Processamento de cada linha ───────────────────────────────────────────

    private void processarLinha(String[] campos, int numeroLinha) {
        validarColunas(campos, numeroLinha);

        String nome = campos[COL_NOME].trim();
        String email = campos[COL_EMAIL].trim();
        String matricula = campos[COL_MATRICULA].trim();
        String cursoIdStr = campos[COL_CURSO_ID].trim();

        validarCamposObrigatorios(nome, email, matricula, cursoIdStr, numeroLinha);

        Long cursoId = parsearCursoId(cursoIdStr, numeroLinha);

        criarUsuarioSeNecessario(nome, email, matricula, numeroLinha);

        AlunoDTO dto = new AlunoDTO();
        dto.setNome(nome);
        dto.setEmail(email);
        dto.setMatricula(matricula);
        dto.setCursoId(cursoId);

        alunoService.salvarAluno(dto);
    }

    /**
     * Cria o usuário com perfil ALUNO se o e-mail ainda não estiver cadastrado.
     * Se o usuário existir com perfil diferente de ALUNO, lança exceção.
     * Senha padrão = matrícula (aluno deve trocar no primeiro acesso).
     */
    private void criarUsuarioSeNecessario(String nome, String email, String matricula, int numeroLinha) {
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(passwordEncoder.encode(matricula));
            novoUsuario.setPerfil(PerfilUsuario.ALUNO);
            usuarioRepository.save(novoUsuario);
            return;
        }

        // Usuário já existe — valida se tem perfil ALUNO
        Usuario usuarioExistente = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Linha " + numeroLinha + ": erro ao buscar usuário com e-mail '" + email + "'."));

        if (usuarioExistente.getPerfil() != PerfilUsuario.ALUNO) {
            throw new RuntimeException(
                    "Linha " + numeroLinha + ": o usuário '" + email
                            + "' já existe com perfil " + usuarioExistente.getPerfil().name()
                            + ". Apenas usuários com perfil ALUNO podem ser cadastrados como alunos.");
        }
    }

    // ── Leitura do CSV ────────────────────────────────────────────────────────

    private List<String[]> lerLinhasCsv(MultipartFile arquivo) {
        List<String[]> linhas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {

            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    linhas.add(linha.split(SEPARADOR, -1));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao ler o arquivo CSV: " + e.getMessage());
        }

        if (linhas.size() < 2) {
            throw new IllegalArgumentException(
                    "O arquivo CSV deve ter pelo menos uma linha de cabeçalho e uma linha de dados.");
        }

        return linhas;
    }

    // ── Validações ────────────────────────────────────────────────────────────

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("O arquivo CSV não pode estar vazio.");
        }
        String nomeArquivo = arquivo.getOriginalFilename();
        if (nomeArquivo == null || !nomeArquivo.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("O arquivo deve ter extensão .csv.");
        }
    }

    private void validarColunas(String[] campos, int numeroLinha) {
        if (campos.length < TOTAL_COLUNAS) {
            throw new IllegalArgumentException(
                    String.format("Linha %d: esperado %d colunas (nome, email, matricula, cursoId), mas encontrado %d.",
                            numeroLinha, TOTAL_COLUNAS, campos.length));
        }
    }

    private void validarCamposObrigatorios(
            String nome, String email, String matricula, String cursoIdStr, int numeroLinha) {

        if (nome.isEmpty()) {
            throw new IllegalArgumentException("Linha " + numeroLinha + ": campo 'nome' não pode estar vazio.");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Linha " + numeroLinha + ": campo 'email' não pode estar vazio.");
        }
        if (matricula.isEmpty()) {
            throw new IllegalArgumentException("Linha " + numeroLinha + ": campo 'matricula' não pode estar vazio.");
        }
        if (cursoIdStr.isEmpty()) {
            throw new IllegalArgumentException("Linha " + numeroLinha + ": campo 'cursoId' não pode estar vazio.");
        }
    }

    private Long parsearCursoId(String cursoIdStr, int numeroLinha) {
        try {
            return Long.parseLong(cursoIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Linha %d: 'cursoId' deve ser um número inteiro válido, mas foi '%s'.",
                            numeroLinha, cursoIdStr));
        }
    }

    private String obterCampoSeguro(String[] campos, int indice) {
        if (campos == null || indice >= campos.length) {
            return "";
        }
        return campos[indice].trim();
    }
}