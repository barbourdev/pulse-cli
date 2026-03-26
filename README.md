# pulse

![Java 21](https://img.shields.io/badge/Java-21+-blue)
![Maven](https://img.shields.io/badge/Maven-3.9+-orange)
![License](https://img.shields.io/badge/License-MIT-green)

> [English version](README.en.md)

**pulse** e uma ferramenta rapida de health check de APIs direto no terminal. Aponte para uma lista de endpoints e veja instantaneamente quais estao no ar, fora do ar ou lentos.

Construido com virtual threads do Java 21 para checagens paralelas, output colorido no terminal e configuracao simples via YAML. Perfeito para validar rapidamente seus microservicos, APIs de terceiros ou qualquer endpoint HTTP.

## Por que pulse?

Ferramentas de monitoramento como Grafana, Datadog e UptimeRobot sao otimas para producao, mas exageradas quando voce so quer saber: **"minhas APIs estao de pe?"**

O pulse resolve isso com um unico comando — sem dashboards, sem login, sem setup.

| Diferencial | Descricao |
|-------------|-----------|
| **Zero dependencias** | Nao precisa de Docker, Node ou Python. So Java |
| **Paralelismo real** | Virtual threads do Java 21 — checa 50 APIs no mesmo tempo que checa 1 |
| **Um comando** | `pulse check` e pronto. Resultado em menos de 1 segundo |
| **Universal** | Funciona em Windows, Linux e macOS. Output ASCII compativel com qualquer terminal |
| **3 linhas de YAML** | Sem JSON verboso, sem GUI, sem curva de aprendizado |

## Instalacao

### Windows (PowerShell)

```powershell
git clone https://github.com/barbourdev/pulse-cli.git
cd pulse-cli
powershell -ExecutionPolicy Bypass -File install.ps1
```

### Linux / macOS

```bash
git clone https://github.com/barbourdev/pulse-cli.git
cd pulse-cli
bash install.sh
```

Apos a instalacao, feche e reabra o terminal. Depois use o `pulse` diretamente:

```bash
pulse check --help
```

## Como usar

### Gerar um arquivo de configuracao de exemplo

```bash
pulse init
```

Isso cria um arquivo `apis.yaml` no diretorio atual com APIs de exemplo.

### Verificar APIs a partir de um arquivo

```bash
pulse check
```

Por padrao, le o `apis.yaml` do diretorio atual. Voce pode especificar outro arquivo:

```bash
pulse check --file minhas-apis.yaml
```

### Verificar com timeout personalizado

```bash
pulse check --timeout 3000
```

### Verificar com limiar de lentidao personalizado

```bash
pulse check --slow 300
```

### Exemplo de output

```
  [ OK ]  github            200   120ms
  [FAIL]  local-api         ERR   --
  [SLOW]  jsonplaceholder   200   620ms

  2/3 healthy · 1 down · 1 slow
```

## Formato do apis.yaml

```yaml
apis:
  - name: users-api
    url: http://localhost:8080/actuator/health
  - name: github
    url: https://api.github.com
  - name: minha-api
    url: http://localhost:3000/health
```

## Flags

| Flag | Curto | Padrao | Descricao |
|------|-------|--------|-----------|
| `--file` | `-f` | `apis.yaml` | Caminho para o arquivo YAML de configuracao |
| `--timeout` | `-t` | `5000` | Timeout da requisicao em milissegundos |
| `--slow` | `-s` | `500` | Limiar de lentidao em milissegundos |
| `--help` | `-h` | | Exibe a ajuda |
| `--version` | `-v` | | Exibe a versao |

## Roadmap

### v1.1 — Output e Integracao

| Feature | Descricao |
|---------|-----------|
| `--json` | Output em JSON para integrar com pipelines, Grafana, scripts |
| `--verbose` / `-v` | Mostra URL completa e mensagem de erro detalhada por API |
| `--quiet` / `-q` | Mostra apenas o resumo final (sem listar cada API) |
| `--only-failures` | Filtra e exibe somente APIs que falharam |
| **Exit code 1** | Retorna exit code 1 se alguma API estiver down — essencial para CI/CD |

### v1.2 — Monitoramento Continuo

| Feature | Descricao |
|---------|-----------|
| `pulse watch --interval 30` | Modo watch: re-executa o check a cada N segundos, atualizando o terminal em tempo real |
| `--notify` | Envia notificacao do sistema (Windows toast / OS notification) quando uma API cai |
| `--log <file>` | Salva cada execucao em arquivo de log (append) com timestamp |

### v1.3 — Configuracao Avancada do YAML

| Feature | Descricao |
|---------|-----------|
| `method` | Suportar POST, PUT, HEAD alem de GET |
| `headers` | Headers customizados por API (ex: Authorization, API keys) |
| `body` | Body para requisicoes POST/PUT |
| `expected-status` | Definir qual status e considerado "healthy" por API (ex: 201, 301) |
| `groups` | Agrupar APIs por categoria (producao, staging, externas) |

```yaml
# Exemplo futuro
apis:
  - name: users-api
    url: http://localhost:8080/health
    method: POST
    headers:
      Authorization: Bearer ${TOKEN}
    body: '{"ping": true}'
    expected-status: 201
    group: production
```

### v1.4 — Relatorios

| Feature | Descricao |
|---------|-----------|
| `pulse report --file apis.yaml` | Gera relatorio HTML com tabela colorida, grafico de latencia e historico |
| `--csv` | Exporta resultado em CSV |
| `--markdown` | Exporta resultado em tabela Markdown (util para colar em PRs/issues) |

### v1.5 — Multi-ambiente e Profiles

| Feature | Descricao |
|---------|-----------|
| `pulse check --profile staging` | Profiles para ambientes diferentes (dev, staging, prod) em um unico YAML |
| `pulse diff --prod --staging` | Compara saude entre dois ambientes lado a lado |

```yaml
# Exemplo futuro
profiles:
  production:
    apis:
      - name: users-api
        url: https://api.prod.com/health
  staging:
    apis:
      - name: users-api
        url: https://api.staging.com/health
```

### v2.0 — Avancado

| Feature | Descricao |
|---------|-----------|
| `pulse history` | Armazena historico local (SQLite) de cada check e mostra tendencia de latencia |
| `pulse dashboard` | TUI interativa com painel ao vivo (usando Lanterna ou JLine) |
| `pulse schedule` | Agenda checks automaticos via cron expression |
| **Plugins** | Sistema de plugins para notificacao (Slack, Discord, Telegram, webhook) |

## Licenca

MIT
