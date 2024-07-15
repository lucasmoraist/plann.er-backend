# Plann.er - Backend
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

## Descrição
O Projeto Journey tem como objetivo ajudar os usuários a organizar suas viagens, sejam elas a trabalho ou lazer. O sistema permite que os usuários criem e gerenciem viagens de forma eficiente, fornecendo funcionalidades para o planejamento detalhado de cada dia da viagem.

### Funcionalidades
1. Criação de viagens
    - **Nome da Viagem:** Um título descritivo para a viagem.
    - **Data de Início:** A data de início da viagem.
    - **Data de Fim:** A data de término da viagem.
    - **Convidados:** Amigos/Colegas que irão com você.
    - **Dados do criador da viagem:** Registro de nome e email de quem está criando a viagem.
```json
{
  "destionation": "Florianópolis, SC",
  "starts_at": "2024-06-25T21:51:54.7342",
  "ends_at": "2024-06-25T21:51:54.7342",
  "emails_to_invite": ["mayk.brito@rocketseat.com"],
  "owner_name": "Fernanda Kipper",
  "owner_email": "fernanda.kipper@rocketseat.com"
}
```
2. Planejamento de atividades
- Dentro de cada viagem, o usuário pode adicionar atividades planejadas para cada dia específico.
- Podendo salvar links importantes como de reserva e regras.

## Instruções de instalação
### Pré requisitos
- Java 17 ou superior
- IDE (Eclipse, Intellij, VSCode)
- Maven 3.2.5 ou superior
- Docker

### Etapas
1. Clone o repositório na sua máquina
2. Abra o seu prompt de comando dentro dele
3. Execute o arquivo docker para criar o container que ficará o banco de dados
```bash
docker-compose up -d
```
4. Faça o build da aplicação
```bash
mvn clean package -Dspring.profiles.active=open
```
5. Execute o arquivo jar gerado pelo build
```bash
java -jar target/planner-0.0.1-SNAPSHOT.jar --spring.profiles.active=open
```

## Instruções de Uso
1. Com seu projeto em execução, abra sua ferramente para testes de requisições (Insomnia ou Postman)
2. Importe o arquivo `Testes Insomnia` que está na raiz do proejeo e já será possível realizar os testes

## Contribuição
Contribuições são bem-vindas! Sinta-se à vontade para enviar pull requests com melhorias, correções de bugs ou novos recursos.

## Contatos
<a href = "mailto:luksmnt1101@gmail.com">
  <img src="https://img.shields.io/badge/-Gmail-%23333?style=for-the-badge&logo=gmail&logoColor=white" target="_blank">
</a>
<a href="https://www.linkedin.com/in/lucas-morais-152672219/" target="_blank">
  <img src="https://img.shields.io/badge/-LinkedIn-%230077B5?style=for-the-badge&logo=linkedin&logoColor=white" target="_blank">
</a>
