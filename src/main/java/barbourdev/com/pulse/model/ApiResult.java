package barbourdev.com.pulse.model;

/**
 * Representa o resultado do health check de uma unica API.
 * Armazena o nome, URL, status HTTP, latencia, e informacoes de erro.
 *
 * @param name         nome identificador da API (definido no apis.yaml)
 * @param url          URL completa que foi verificada
 * @param statusCode   codigo HTTP retornado (0 se houve timeout ou erro de conexao)
 * @param latencyMs    tempo de resposta em milissegundos
 * @param timeout      {@code true} se a requisicao excedeu o tempo limite ou falhou na conexao
 * @param errorMessage mensagem de erro capturada, ou {@code null} se a requisicao teve sucesso
 */
public record ApiResult(
        String name,
        String url,
        int statusCode,
        long latencyMs,
        boolean timeout,
        String errorMessage
) {
    /**
     * Verifica se a API esta saudavel.
     * Considera saudavel quando nao houve timeout e o status HTTP esta na faixa 2xx.
     *
     * @return {@code true} se a API respondeu com sucesso
     */
    public boolean isHealthy() {
        return !timeout && statusCode >= 200 && statusCode < 300;
    }

    /**
     * Verifica se a API esta fora do ar.
     * Considera fora do ar quando houve timeout ou o status HTTP e 4xx/5xx.
     *
     * @return {@code true} se a API esta indisponivel
     */
    public boolean isDown() {
        return timeout || statusCode >= 400;
    }

    /**
     * Verifica se a API respondeu de forma lenta, comparando a latencia
     * com o limiar informado.
     *
     * @param threshold limiar em milissegundos acima do qual a API e considerada lenta
     * @return {@code true} se a latencia ultrapassou o limiar (e nao houve timeout)
     */
    public boolean isSlow(long threshold) {
        return !timeout && latencyMs > threshold;
    }
}
