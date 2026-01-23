# Product Manager

![Quarkus](https://img.shields.io/badge/Quarkus-3.6.4-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple)
![PatternFly](https://img.shields.io/badge/PatternFly-5.0-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)

Sistema de gerenciamento de produtos desenvolvido com Quarkus, Kotlin, Qute Templates e PatternFly Design System.

## ✨ Funcionalidades

- ✅ CRUD completo de produtos
- ✅ Busca por nome
- ✅ Interface responsiva com PatternFly
- ✅ Validações de formulário
- ✅ Arquitetura DDD (Domain-Driven Design)
- ✅ Hot reload em desenvolvimento

## 🛠️ Tecnologias

- **Backend**: Quarkus 3.6.4 + Kotlin 1.9.22
- **Frontend**: Qute Templates + PatternFly 5
- **Database**: PostgreSQL 15
- **Build**: Gradle 8.5

## 📋 Pré-requisitos

- Java 17 ou superior
- Docker e Docker Compose
- Git

## 🚀 Como Executar

### 1. Clone o repositório
```bash
git clone https://github.com/SEU_USUARIO/product-manager.git
cd product-manager
```

### 2. Inicie o banco de dados
```bash
docker-compose up -d
```

### 3. Execute a aplicação
```bash
./gradlew quarkusDev
```

### 4. Acesse no navegador
```
http://localhost:8080
```

## 📁 Estrutura do Projeto
```
product-manager/
├── src/main/
│   ├── kotlin/com/example/product/
│   │   ├── domain/              # Modelos e repositórios
│   │   ├── application/         # Serviços de negócio
│   │   └── infrastructure/      # Controllers REST
│   └── resources/
│       ├── templates/           # Templates Qute
│       └── META-INF/resources/  # CSS/JS estáticos
├── docker-compose.yml
└── build.gradle.kts
```

## 🎨 Screenshots

### Home
Página inicial com informações do sistema

### Lista de Produtos
Tabela responsiva com busca e ações

### Formulário
Cadastro/edição de produtos com validação

## 🔧 Configuração

### Banco de Dados

Edite `src/main/resources/application.properties`:
```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/productdb
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
```

### Porta do Servidor
```properties
quarkus.http.port=8080
```

## 📦 Build de Produção
```bash
# Gerar JAR
./gradlew build

# Executar
java -jar build/quarkus-app/quarkus-run.jar
```

## 🧪 Testes
```bash
./gradlew test
```

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👤 Autor

Seu Nome
- GitHub: [@seu-usuario](https://github.com/seu-usuario)
- LinkedIn: [seu-perfil](https://linkedin.com/in/seu-perfil)

## 🙏 Agradecimentos

- [Quarkus](https://quarkus.io/)
- [PatternFly](https://www.patternfly.org/)
- [Red Hat](https://www.redhat.com/)
