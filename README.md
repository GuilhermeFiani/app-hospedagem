# Aplicativo de Hospedagens

### Aluno: Guilherme Siqueira Fiani

## Visão geral

Este é um aplicativo Android a ser desenvolvido em Java com persistência local via **RoomDB**, funcionando como um ecossistema onde usuários podem tanto oferecer espaços (como anfitriões) quanto reservá-los (como hóspedes).

Com o objetivo de entregar um produto funcional e coeso dentro do prazo estipulado, foi imposto um escopo realista para o desenvolvimento inicial do projeto. As funcionalidades primárias focam nos requisitos essenciais para a operação do sistema, visando a construção de um Produto Mínimo Viável (MVP).

## Tipos de usuários

A seguir estão os possíveis tipos de usuário e suas respectivas ações dentro do sistema:

1. **Anfitrião:**
Usuário responsável por cadastrar e gerenciar informações de hospedagens, além de divulgar notificações de desconto destinadas aos visitantes.

2. **Hóspede:**
Usuário que utiliza o aplicativo para buscar hospedagens disponíveis e realizar reservas. Ao efetuar uma reserva, o hóspede recebe uma notificação de confirmação

Cada tipo de usuário possui um fluxo de acesso diferenciado no sistema, com permissões específicas baseadas em seu papel no cadastro.

## Requisitos funcionais do MVP

Para o escopo do trabalho final, os seguintes requisitos foram definidos:

**Autenticação e Autorização**:  
+ RF-1. O usuário deve ser capaz de criar uma conta no aplicativo.  
+ RF-2. O usuário deve ser capaz de realizar login com suas credenciais, e o sistema deve guardar o hash da senha de forma segura.  
+ RF-3. O sistema deve implementar controle de acesso para garantir que cada tipo de usuário (anfitrião/hóspede) acesse apenas as funcionalidades permitidas para seu papel.

**Gerenciamento de Hospedagens (Anfitrião)**  
+ RF-4. O anfitrião deve ser capaz de criar, editar e remover hospedagens que estejam associadas ao seu perfil.

**Notificações**  
+ RF-5. O anfitrião deve ser capaz de criar e configurar notificações de desconto ou promoções para suas hospedagens, visíveis para os hóspedes.
+ RF-6. O hóspede deve ser notificado ao confirmar uma reserva com sucesso.

**Busca e Reservas (Hóspede)**
+ RF-7. O hóspede deve ser capaz de buscar hospedagens usando a cidade como parâmetro, por exemplo.
+ RF-8. O hóspede deve ser capaz de reservar uma hospedagem e visualizar o histórico de reservas.

## Testes e validação

Os testes elaborados serão voltados para:

- Validação de **datas inválidas**.
- Verificação de **campos obrigatórios vazios** em formulários.
- Restrição de acesso a telas ou ações que dependem do tipo de usuário.

## Tecnologias Utilizadas

- **Linguagem:** Java
- **Persistência:** RoomDB
- **Interface:** Android XML com fragmentos
- **Segurança:** Hash de senha
- **Recursos Android:** Notificações, câmera (para foto de perfil), navegação por fragmentos e recursos visuais (como strings, cores e imagens).

---
