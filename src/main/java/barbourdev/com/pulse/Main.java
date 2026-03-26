package barbourdev.com.pulse;

import barbourdev.com.pulse.commands.CheckCommand;
import barbourdev.com.pulse.commands.InitCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Classe principal da CLI pulse.
 * Registra os subcomandos {@code check} e {@code init} e delega a execucao ao Picocli.
 */
@Command(
        name = "pulse",
        version = "pulse 1.0.0",
        description = "API health check tool for the terminal",
        subcommands = {
                CheckCommand.class,
                InitCommand.class
        }
)
public class Main implements Runnable {

    private static final String RESET  = "\u001b[0m";
    private static final String BOLD   = "\u001b[1m";
    private static final String DIM    = "\u001b[2m";
    private static final String CYAN   = "\u001b[36m";
    private static final String GREEN  = "\u001b[32m";
    private static final String YELLOW = "\u001b[33m";
    private static final String WHITE  = "\u001b[37m";

    @Option(names = {"-h", "-H",  "--help"}, usageHelp = true, description = "Show help")
    private boolean help;

    @Option(names = {"-v", "-V", "--version"}, versionHelp = true, description = "Show version")
    private boolean version;

    /**
     * Executado quando o usuario roda {@code pulse} sem nenhum subcomando.
     * Exibe a mensagem de ajuda personalizada com os comandos disponiveis.
     */
    @Override
    public void run() {
        printHelp();
    }

    /**
     * Imprime o help customizado com cores e formatacao moderna.
     */
    private void printHelp() {
        System.out.println();
        System.out.println("  " + RESET + CYAN + BOLD + "PULSE" + RESET + DIM + " v1.0.0" + RESET);
        System.out.println("  " + BOLD + DIM + ">_barbourdev" + RESET);
        System.out.println("  " + WHITE + "Fast API health check tool for the terminal" + RESET);
        System.out.println();
        System.out.println("  " + BOLD + "USAGE" + RESET);
        System.out.println("    " + DIM + "$" + RESET + " pulse " + CYAN + "<command>" + RESET + " [options]");
        System.out.println();
        System.out.println("  " + BOLD + "COMMANDS" + RESET);
        System.out.println("    " + GREEN + "check" + RESET + "   Run health check on your APIs");
        System.out.println("    " + GREEN + "init" + RESET + "    Generate a sample apis.yaml in the current directory");
        System.out.println();
        System.out.println("  " + BOLD + "CHECK OPTIONS" + RESET);
        System.out.println("    " + YELLOW + "-f" + RESET + ", " + YELLOW + "--file" + RESET + " <path>     Path to apis.yaml file " + DIM + "(default: apis.yaml)" + RESET);
        System.out.println("    " + YELLOW + "-t" + RESET + ", " + YELLOW + "--timeout" + RESET + " <ms>   Request timeout in milliseconds " + DIM + "(default: 5000)" + RESET);
        System.out.println("    " + YELLOW + "-s" + RESET + ", " + YELLOW + "--slow" + RESET + " <ms>      Slow threshold in milliseconds " + DIM + "(default: 500)" + RESET);
        System.out.println();
        System.out.println("  " + BOLD + "GLOBAL OPTIONS" + RESET);
        System.out.println("    " + YELLOW + "-h" + RESET + ", " + YELLOW + "--help" + RESET + "            Show this help message");
        System.out.println("    " + YELLOW + "-v" + RESET + ", " + YELLOW + "--version" + RESET + "         Show version");
        System.out.println();
        System.out.println("  " + BOLD + "EXAMPLES" + RESET);
        System.out.println("    " + DIM + "$" + RESET + " pulse init");
        System.out.println("    " + DIM + "$" + RESET + " pulse check");
        System.out.println("    " + DIM + "$" + RESET + " pulse check -f my-apis.yaml");
        System.out.println("    " + DIM + "$" + RESET + " pulse check --timeout 3000 --slow 300");
        System.out.println();
        System.out.println("  " + DIM + "Docs: https://github.com/barbourdev/pulse-cli" + RESET);
        System.out.println();
    }

    /**
     * Ponto de entrada da aplicacao.
     * Inicializa o Picocli, interpreta os argumentos da linha de comando
     * e encerra o processo com o codigo de saida apropriado.
     *
     * @param args argumentos recebidos da linha de comando
     */
    public static void main(String[] args) {
        Main app = new Main();
        CommandLine cmd = new CommandLine(app);

        CommandLine.IParameterExceptionHandler paramHandler = (ex, cmdArgs) -> {
            System.err.println();
            System.err.println("  \u001b[31m\u001b[1m ERROR \u001b[0m  " + ex.getMessage());
            System.err.println();
            System.err.println("  \u001b[2mRun 'pulse -h' for usage information\u001b[0m");
            System.err.println();
            return 2;
        };

        CommandLine.IExecutionExceptionHandler execHandler = (ex, commandLine, parseResult) -> {
            System.err.println();
            System.err.println("  \u001b[31m\u001b[1m ERROR \u001b[0m  " + ex.getMessage());
            System.err.println();
            return 1;
        };

        cmd.setParameterExceptionHandler(paramHandler);
        cmd.setExecutionExceptionHandler(execHandler);
        for (CommandLine sub : cmd.getSubcommands().values()) {
            sub.setParameterExceptionHandler(paramHandler);
            sub.setExecutionExceptionHandler(execHandler);
        }

        int exitCode = cmd.execute(args);
        System.exit(exitCode);
    }
}
