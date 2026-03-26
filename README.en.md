# pulse

![Java 21](https://img.shields.io/badge/Java-21+-blue)
![Maven](https://img.shields.io/badge/Maven-3.9+-orange)
![License](https://img.shields.io/badge/License-MIT-green)

> [Versao em portugues](README.md)

**pulse** is a fast, terminal-based API health check tool. Point it at a list of endpoints and instantly see which ones are up, down, or slow.

Built with Java 21 virtual threads for parallel checks, colored terminal output, and a simple YAML config. Perfect for quickly validating your microservices, third-party APIs, or any HTTP endpoint.

## Installation

### Windows (PowerShell)

```powershell
git clone https://github.com/barbour/pulse-cli.git
cd pulse-cli
powershell -ExecutionPolicy Bypass -File install.ps1
```

### Linux / macOS

```bash
git clone https://github.com/barbour/pulse-cli.git
cd pulse-cli
bash install.sh
```

After installation, close and reopen your terminal. Then you can use `pulse` directly:

```bash
pulse check --help
```

## Usage

### Generate a sample config

```bash
pulse init
```

This creates an `apis.yaml` file in the current directory with example APIs.

### Check APIs from a file

```bash
pulse check
```

By default, it reads `apis.yaml` from the current directory. You can specify a different file:

```bash
pulse check --file my-apis.yaml
```

### Check with custom timeout

```bash
pulse check --timeout 3000
```

### Check with custom slow threshold

```bash
pulse check --slow 300
```

### Example output

```
  [ OK ]  github            200   120ms
  [FAIL]  local-api         ERR   --
  [SLOW]  jsonplaceholder   200   620ms

  2/3 healthy · 1 down · 1 slow
```

## apis.yaml format

```yaml
apis:
  - name: users-api
    url: http://localhost:8080/actuator/health
  - name: github
    url: https://api.github.com
  - name: my-api
    url: http://localhost:3000/health
```

## Flags

| Flag | Short | Default | Description |
|------|-------|---------|-------------|
| `--file` | `-f` | `apis.yaml` | Path to the YAML config file |
| `--timeout` | `-t` | `5000` | Request timeout in milliseconds |
| `--slow` | `-s` | `500` | Slow threshold in milliseconds |
| `--help` | `-h` | | Show help |
| `--version` | `-V` | | Show version |

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

## License

MIT
