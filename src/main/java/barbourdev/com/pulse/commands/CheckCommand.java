package barbourdev.com.pulse.commands;

import barbourdev.com.pulse.http.ApiChecker;
import barbourdev.com.pulse.model.ApiResult;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Comando {@code pulse check} — executa o health check das APIs.
 * Le o arquivo YAML com a lista de APIs, faz as requisicoes em paralelo
 * e exibe o resultado formatado com cores no terminal.
 */
@Command(
        name = "check",
        description = "Check health of APIs"
)
public class CheckCommand implements Runnable {

    // -- Cores ANSI --
    private static final String RESET   = "\u001b[0m";
    private static final String BOLD    = "\u001b[1m";
    private static final String DIM     = "\u001b[2m";
    private static final String GREEN   = "\u001b[32m";
    private static final String RED     = "\u001b[31m";
    private static final String YELLOW  = "\u001b[33m";
    private static final String CYAN    = "\u001b[36m";
    private static final String WHITE   = "\u001b[37m";
    private static final String BG_GREEN  = "\u001b[42m";
    private static final String BG_RED    = "\u001b[41m";
    private static final String BG_YELLOW = "\u001b[43m";
    private static final String BLACK   = "\u001b[30m";

    /** Caminho para o arquivo YAML contendo a lista de APIs. */
    @Option(names = {"-f", "--file"}, description = "Path to apis.yaml file", defaultValue = "apis.yaml")
    private String file;

    /** Tempo limite em milissegundos para cada requisicao HTTP. */
    @Option(names = {"-t", "--timeout"}, description = "Timeout in milliseconds (default: 5000)", defaultValue = "5000")
    private int timeout;

    /** Limiar em milissegundos acima do qual uma API e considerada lenta. */
    @Option(names = {"-s", "--slow"}, description = "Slow threshold in milliseconds (default: 500)", defaultValue = "500")
    private int slowThreshold;

    /**
     * Executa o fluxo principal do comando check:
     * <ol>
     *   <li>Valida se o arquivo YAML existe</li>
     *   <li>Carrega a lista de APIs do arquivo</li>
     *   <li>Dispara as checagens em paralelo via {@link ApiChecker}</li>
     *   <li>Exibe cada resultado com cores (verde=OK, vermelho=FAIL, amarelo=SLOW)</li>
     *   <li>Imprime o resumo final com totais de healthy, down e slow</li>
     * </ol>
     */
    @Override
    public void run() {
        Path filePath = Path.of(file);
        if (!Files.exists(filePath)) {
            System.err.println();
            System.err.println("  " + RED + BOLD + " ERROR " + RESET + "  File not found: " + WHITE + BOLD + file + RESET);
            System.err.println("  " + DIM + "Run 'pulse init' to create a sample apis.yaml" + RESET);
            System.err.println();
            return;
        }

        List<Map<String, String>> apis = loadApis(filePath);
        if (apis == null || apis.isEmpty()) {
            System.err.println();
            System.err.println("  " + RED + BOLD + " ERROR " + RESET + "  No APIs found in " + WHITE + BOLD + file + RESET);
            System.err.println();
            return;
        }

        printHeader(apis.size());

        ApiChecker checker = new ApiChecker(timeout);
        List<ApiResult> results = checker.checkAll(apis);

        int healthy = 0, down = 0, slow = 0;
        int maxNameLen = Math.max(
                results.stream().mapToInt(r -> r.name().length()).max().orElse(10),
                4 // minimo "NAME".length
        );

        // -- Header da tabela --
        String headerFmt = "  " + DIM + "%-6s  %-" + maxNameLen + "s  %6s  %8s" + RESET + "%n";
        System.out.printf(headerFmt, "STATE", "NAME", "STATUS", "LATENCY");
        printSeparator(maxNameLen);

        // -- Linhas --
        for (ApiResult r : results) {
            if (r.isDown()) {
                down++;
                printRow(BG_RED, "FAIL", r.name(), r.statusCode() == 0 ? "ERR" : String.valueOf(r.statusCode()), "--", maxNameLen);
            } else if (r.isSlow(slowThreshold)) {
                slow++;
                healthy++;
                printRow(BG_YELLOW, "SLOW", r.name(), String.valueOf(r.statusCode()), r.latencyMs() + "ms", maxNameLen);
            } else {
                healthy++;
                printRow(BG_GREEN, " OK ", r.name(), String.valueOf(r.statusCode()), r.latencyMs() + "ms", maxNameLen);
            }
        }

        // -- Resumo --
        printSeparator(maxNameLen);
        printSummary(healthy, down, slow, results.size());
    }

    /**
     * Imprime o banner/header da CLI com informacoes da execucao.
     *
     * @param apiCount quantidade de APIs que serao verificadas
     */
    private void printHeader(int apiCount) {
        System.out.println();
        System.out.println("  " + CYAN + BOLD + "PULSE" + RESET + DIM + " v1.0.0" + RESET
                + "  " + DIM + "|" + RESET
                + "  " + WHITE + apiCount + " apis" + RESET
                + "  " + DIM + "|" + RESET
                + "  " + WHITE + "timeout " + timeout + "ms" + RESET
                + "  " + DIM + "|" + RESET
                + "  " + WHITE + "slow > " + slowThreshold + "ms" + RESET);
        System.out.println();
    }

    /**
     * Imprime uma linha separadora com tracos proporcionais ao tamanho da tabela.
     *
     * @param maxNameLen tamanho maximo do nome das APIs para alinhar corretamente
     */
    private void printSeparator(int maxNameLen) {
        int totalWidth = 6 + 2 + maxNameLen + 2 + 6 + 2 + 8 + 2;
        System.out.print("  " + DIM);
        System.out.print("-".repeat(totalWidth));
        System.out.println(RESET);
    }

    /**
     * Imprime uma linha da tabela com badge colorido, nome da API, status e latencia.
     *
     * @param bgColor    cor de fundo ANSI para o badge de estado
     * @param state      texto do badge (OK, FAIL, SLOW)
     * @param name       nome da API
     * @param status     codigo HTTP ou "ERR"
     * @param latency    latencia formatada ou "--"
     * @param maxNameLen tamanho maximo do nome para alinhamento
     */
    private void printRow(String bgColor, String state, String name, String status, String latency, int maxNameLen) {
        String badge = bgColor + WHITE + BOLD + " " + state + " " + RESET;

        String color;
        if (bgColor.equals(BG_RED)) color = RED;
        else if (bgColor.equals(BG_YELLOW)) color = YELLOW;
        else color = GREEN;

        System.out.printf("  %s  " + color + "%-" + maxNameLen + "s" + RESET
                        + "  " + WHITE + "%6s" + RESET
                        + "  " + DIM + "%8s" + RESET + "%n",
                badge, name, status, latency);
    }

    /**
     * Imprime o resumo final com contadores coloridos de APIs healthy, down e slow.
     *
     * @param healthy total de APIs saudaveis
     * @param down    total de APIs fora do ar
     * @param slow    total de APIs lentas
     * @param total   total de APIs verificadas
     */
    private void printSummary(int healthy, int down, int slow, int total) {
        System.out.println();
        StringBuilder sb = new StringBuilder();
        sb.append("  ");

        if (down == 0 && slow == 0) {
            sb.append(GREEN).append(BOLD).append("All systems operational").append(RESET);
        } else {
            sb.append(WHITE).append(BOLD).append(healthy).append("/").append(total).append(" healthy").append(RESET);
            if (down > 0) sb.append("  ").append(RED).append(BOLD).append(down).append(" down").append(RESET);
            if (slow > 0) sb.append("  ").append(YELLOW).append(BOLD).append(slow).append(" slow").append(RESET);
        }

        System.out.println(sb);
        System.out.println();
    }

    /**
     * Carrega a lista de APIs a partir do arquivo YAML informado.
     * Espera a estrutura {@code apis: [{name: ..., url: ...}]}.
     *
     * @param filePath caminho do arquivo YAML
     * @return lista de mapas com as chaves "name" e "url", ou {@code null} em caso de erro
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> loadApis(Path filePath) {
        try (InputStream in = new FileInputStream(filePath.toFile())) {
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(in);
            return (List<Map<String, String>>) data.get("apis");
        } catch (Exception e) {
            System.err.println();
            System.err.println("  " + RED + BOLD + " ERROR " + RESET + "  " + e.getMessage());
            System.err.println();
            return null;
        }
    }
}
