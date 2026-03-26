package barbourdev.com.pulse.commands;

import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Comando {@code pulse init} — gera um arquivo {@code apis.yaml} de exemplo
 * no diretorio atual para o usuario comecar a configurar suas APIs.
 */
@Command(
        name = "init",
        description = "Generate a sample apis.yaml in the current directory"
)
public class InitCommand implements Runnable {

    private static final String RESET  = "\u001b[0m";
    private static final String BOLD   = "\u001b[1m";
    private static final String DIM    = "\u001b[2m";
    private static final String GREEN  = "\u001b[32m";
    private static final String YELLOW = "\u001b[33m";
    private static final String RED    = "\u001b[31m";
    private static final String CYAN   = "\u001b[36m";
    private static final String WHITE  = "\u001b[37m";

    /** Conteudo de exemplo do arquivo apis.yaml gerado pelo comando init. */
    private static final String SAMPLE = """
            apis:
              - name: github
                url: https://api.github.com
              - name: jsonplaceholder
                url: https://jsonplaceholder.typicode.com/posts/1
              - name: local-api
                url: http://localhost:8080/health
            """;

    /**
     * Executa o comando init.
     * Verifica se ja existe um {@code apis.yaml} no diretorio atual.
     * Se nao existir, cria o arquivo com o conteudo de exemplo.
     * Se ja existir, exibe um aviso e nao sobrescreve.
     */
    @Override
    public void run() {
        Path target = Path.of("apis.yaml");

        System.out.println();

        if (Files.exists(target)) {
            System.out.println("  " + YELLOW + BOLD + " WARN " + RESET + "  apis.yaml already exists. Skipping.");
            System.out.println();
            return;
        }

        try {
            Files.writeString(target, SAMPLE);
            System.out.println("  " + GREEN + BOLD + " DONE " + RESET + "  Created " + WHITE + BOLD + "apis.yaml" + RESET);
            System.out.println();
            System.out.println("  " + DIM + "Next steps:" + RESET);
            System.out.println("  " + CYAN + "1." + RESET + " Edit apis.yaml with your API endpoints");
            System.out.println("  " + CYAN + "2." + RESET + " Run " + WHITE + BOLD + "pulse check" + RESET);
            System.out.println();
        } catch (IOException e) {
            System.err.println("  " + RED + BOLD + " ERROR " + RESET + "  " + e.getMessage());
            System.err.println();
        }
    }
}
