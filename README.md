[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/AR7CADm8)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=20982190)
# Proposta de aplicativo

## Equipe
* **Nome do Aluno:** José Rian Mendes Lima - 552303

---

## Título do Projeto
Onde Estacionei?

---

## Descrição do Projeto
Quem nunca passou minutos preciosos procurando o carro em um estacionamento gigante de shopping, em uma rua desconhecida ou no meio da multidão de um grande evento? Aquele momento de incerteza pode ser frustrante e consumir um tempo valioso. O "Onde Estacionei?" é um aplicativo que surge para resolver exatamente esse problema de forma simples e eficiente. 

O objetivo do "Onde Estacionei?" é ser um assistente de estacionamento minimalista e confiável. Com apenas um toque, o usuário pode salvar a localização exata do seu veículo. Ao retornar, o aplicativo exibe a posição do carro e a localização atual do usuário em um mapa interativo, traçando a melhor rota a pé para o reencontro. O público-alvo são todos os motoristas que frequentam grandes centros urbanos, shoppings, aeroportos e eventos, onde encontrar o carro pode se tornar um desafio.

---

## Funcionalidades Principais

- [x] **Salvar Localização com Um Toque:** Salva a localização GPS atual com um único botão na tela principal.

- [x] **Visualização em Mapa Interativo:** Exibe a localização do carro e a posição atual do usuário utilizando a API do Google Maps.
      
- [x] **Navegação a Pé/de Carro:** Traça e exibe a rota de caminhada em tempo real entre o usuário e o veículo.
      
- [x] **Endereço por Geocodificação Reversa:** Converte coordenadas GPS em endereços legíveis utilizando o Geocoder nativo.
      
- [x] **Adição de Notas e Detalhes:** Permite salvar informações extras (ex: andar, número da vaga) via diálogos.
      
- [x] **Histórico de Locais:** Gerenciamento completo (Listagem e Exclusão) dos registros salvos no banco de dados local.
      
- [x] **Suporte a Tema Escuro/Claro:** Interface adaptável utilizando Material 3 e MaterialTheme.
      
- [x] **App Widget (Bônus):** Widget para a tela inicial que permite salvar a localização rapidamente sem abrir o app.

---

> [!WARNING]
> Daqui em diante o README.md só deve ser preenchido no momento da entrega final.

##  Tecnologias: 

O projeto foi desenvolvido seguindo a arquitetura **MVVM (Model-View-ViewModel)** e utiliza as seguintes tecnologias:

* **Linguagem:** Kotlin (2.0.21)
* **UI:** Jetpack Compose (com Material 3)
* **Banco de Dados:** Room (Persistência local do histórico)
* **Preferências:** DataStore (Configurações simples de UI)
* **Rede:** Retrofit & OkHttp (Consumo da API do OpenRouteService para rotas)
* **Mapas e Localização:** * Google Maps Compose (Integração de Mapas)
    * Play Services Location (Fused Location Provider)
    * OpenRouteService API (Cálculo de rotas)
* **Gerenciamento de Permissões:** Accompanist Permissions
* **Navegação:** Navigation Compose
* **Tarefas em Segundo Plano:** WorkManager (Utilizado pelo Widget)
* **Componentes Adicionais:** Glance (Criação do App Widget), Core Splashscreen.

---

## Instruções para Execução

Para rodar o projeto localmente, siga os passos abaixo:

Clone o repositório:

```bash
git clone git@github.com:profBruno-UFC-Qx/classroom-mobile-final-onde-estacionei.git
```

Navegue para o diretório:

```bash
cd classroom-mobile-final-onde-estacionei/ondeestacionei/
```
Abra no android studio via atalho ou usando:

```bash
studio .
```

Configuração das Chaves de API: Por questões de segurança e para evitar que as chaves sejam revogadas automaticamente pelos provedores (Google/ORS) ao serem detectadas em repositórios públicos, as chaves de API serão fornecidas no dia da apresentação.

Ao obter as chaves, localize o arquivo `local.properties` e adicione as chaves do Google Maps e do OpenRouteService ao final do arquivo:

```.properties
MAPS_API_KEY=CHAVE
ORS_API_KEY=CHAVE
```
