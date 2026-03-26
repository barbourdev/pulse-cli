package barbourdev.com.pulse.http;

import barbourdev.com.pulse.model.ApiResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

/**
 * Responsavel por executar as requisicoes HTTP de health check nas APIs.
 * Utiliza virtual threads (Java 21+) para realizar todas as checagens em paralelo,
 * garantindo rapidez mesmo com muitas APIs configuradas.
 */
public class ApiChecker {

    private final int timeoutMs;
    private final HttpClient client;

    /**
     * Cria uma nova instancia do checker com o timeout especificado.
     * Configura o {@link HttpClient} com timeout de conexao e redirecionamento automatico.
     *
     * @param timeoutMs tempo limite em milissegundos para cada requisicao
     */
    public ApiChecker(int timeoutMs) {
        this.timeoutMs = timeoutMs;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Executa o health check de todas as APIs em paralelo usando virtual threads.
     * Cada API e verificada em uma thread virtual separada via {@link CompletableFuture},
     * e o metodo aguarda todas as respostas antes de retornar.
     *
     * @param apis lista de mapas contendo as chaves "name" e "url" de cada API
     * @return lista de {@link ApiResult} com o resultado de cada checagem, na mesma ordem da entrada
     */
    public List<ApiResult> checkAll(List<Map<String, String>> apis) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<ApiResult>> futures = apis.stream()
                    .map(api -> CompletableFuture.supplyAsync(
                            () -> check(api.get("name"), api.get("url")), executor))
                    .toList();

            return futures.stream()
                    .map(CompletableFuture::join)
                    .toList();
        }
    }

    /**
     * Realiza uma requisicao GET para a URL informada e mede a latencia.
     * Em caso de sucesso, retorna o status HTTP e o tempo de resposta.
     * Em caso de falha (timeout, conexao recusada, etc.), retorna status 0
     * com a flag de timeout ativada e a mensagem de erro.
     *
     * @param name nome identificador da API
     * @param url  URL completa para a requisicao GET
     * @return {@link ApiResult} com os dados da checagem
     */
    private ApiResult check(String name, String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(timeoutMs))
                .GET()
                .build();

        long start = System.currentTimeMillis();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            long latency = System.currentTimeMillis() - start;
            return new ApiResult(name, url, response.statusCode(), latency, false, null);
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - start;
            return new ApiResult(name, url, 0, latency, true, e.getMessage());
        }
    }
}
