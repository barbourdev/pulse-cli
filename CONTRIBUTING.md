# Contribuindo com o pulse

Obrigado por considerar contribuir com o pulse! Toda ajuda e bem-vinda.

## Como contribuir

### Reportando bugs

1. Verifique se o bug ja nao foi reportado nas [issues](https://github.com/barbourdev/pulse-cli/issues)
2. Abra uma nova issue usando o template de **Bug Report**
3. Inclua: versao do Java, sistema operacional, passos para reproduzir e o comportamento esperado

### Sugerindo features

1. Confira o [Roadmap no README](README.md#roadmap) — talvez sua ideia ja esteja planejada
2. Abra uma issue usando o template de **Feature Request**
3. Descreva o problema que a feature resolve e como voce imagina a solucao

### Enviando codigo

1. Faca um fork do repositorio
2. Crie uma branch a partir da `main`:
   ```bash
   git checkout -b feat/minha-feature
   ```
3. Faca suas alteracoes seguindo as convencoes do projeto
4. Teste localmente:
   ```bash
   mvnd package
   java -jar target/pulse.jar check
   ```
5. Commit com mensagem descritiva em portugues:
   ```bash
   git commit -m "feat: adiciona suporte a headers customizados"
   ```
6. Push para seu fork e abra um Pull Request

## Convencoes

### Commits

Usamos o padrao [Conventional Commits](https://www.conventionalcommits.org/) em portugues:

| Prefixo | Uso |
|---------|-----|
| `feat:` | Nova funcionalidade |
| `fix:` | Correcao de bug |
| `docs:` | Alteracao em documentacao |
| `refactor:` | Refatoracao sem mudanca de comportamento |
| `chore:` | Tarefas de manutencao (build, deps, etc) |

### Codigo

- Java 21+
- Javadoc em portugues nos metodos publicos
- Nomes de classes e metodos em ingles (padrao Java)
- Mensagens de output para o usuario em ingles

### Estrutura do projeto

```
src/main/java/barbourdev/com/pulse/
├── Main.java              # Entry point e CLI config
├── commands/              # Subcomandos (check, init)
├── http/                  # Cliente HTTP e checagem
└── model/                 # Modelos de dados
```

## Primeira contribuicao?

Procure issues marcadas com `good first issue` — sao tarefas simples e ideais para comecar.

## Duvidas?

Abra uma issue com a tag `question` ou entre em contato pelo GitHub.
