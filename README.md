# MyLocation

Aplicativo Android para registro de presença (check-in/check-out) de alunos em estágio, com captura de localização via GPS e exibição em mapa.

## Funcionalidades

- **Login** com matrícula e senha, com suporte a autenticação via Google (Credential Manager)
- **Check-in e Check-out** com captura automática da localização atual do aluno
- **Mapa integrado** (Google Maps) exibindo a posição registrada
- **Geocoding reverso** para converter coordenadas em endereço legível
- **Histórico de registros** com listagem de check-ins e check-outs anteriores
- **Tela de cadastro** de alunos
- **Tela de splash** na inicialização
- Banco de dados local com **Room** pré-populado com alunos de exemplo
- Integração com **Firebase** (Analytics e Realtime Database)

## Estrutura do Projeto

```
app/src/main/java/com/example/mylocation/
├── LoginActivity.java          # Tela de login (matrícula/senha + Google Sign-In)
├── CadastroActivity.java       # Cadastro de novos alunos
├── CheckinActivity.java        # Tela principal de check-in/check-out com mapa
├── activities/
│   ├── HistoricoActivity.java  # Listagem do histórico de registros
│   └── SplashActivity.java     # Tela de splash inicial
├── adapters/
│   └── RegistroAdapter.java    # Adapter para a lista do histórico
├── models/
│   ├── Aluno.java              # Entidade Room: dados do aluno
│   └── RegistroCheckin.java    # Modelo de registro de check-in/check-out
└── utils/
    ├── AlunoDao.java           # DAO Room para operações com alunos
    ├── AlunoDatabase.java      # Helper para consultas assíncronas de alunos
    ├── AppDatabase.java        # Configuração do banco Room + seed inicial
    ├── CheckinManager.java     # Gerenciamento de estado de check-in em memória
    └── SessionManager.java     # Gerenciamento de sessão do usuário (SharedPreferences)
```

## Tecnologias e Dependências

| Biblioteca | Versão | Uso |
|---|---|---|
| Android SDK | compileSdk 36 / minSdk 24 | Base do projeto |
| Room | 2.6.1 | Banco de dados local |
| Google Maps SDK | 18.2.0 | Exibição do mapa |
| FusedLocationProvider | 21.3.0 | Captura de GPS |
| Credential Manager | 1.3.0 | Login com Google |
| Google Identity | 1.1.1 | Token Google ID |
| Firebase BOM | 34.14.0 | Analytics e Realtime Database |
| Material Components | — | UI |
| ConstraintLayout | — | Layouts |

## Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 11
- Conta Google com projeto no [Firebase Console](https://console.firebase.google.com/) e no [Google Cloud Console](https://console.cloud.google.com/) (para Google Maps e Google Sign-In)

## Configuração

1. **Clone o repositório** e abra o projeto no Android Studio.

2. **Firebase:** Adicione o arquivo `google-services.json` gerado no Firebase Console em `app/google-services.json`.

3. **Google Maps:** No Firebase Console (ou Google Cloud Console), ative a API do Google Maps para Android e insira sua chave de API no `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="SUA_CHAVE_AQUI" />
   ```

4. **Sincronize** o Gradle e execute o projeto em um dispositivo ou emulador com API 24+.

## Alunos de Exemplo (seed)

O banco de dados é pré-populado na primeira instalação com três alunos:

| Matrícula | Nome | Curso | Empresa |
|---|---|---|---|
| 2024001 | João Silva | Análise e Desenvolvimento de Sistemas | Empresa Exemplo Ltda |
| 2024002 | Maria Oliveira | Engenharia de Software | Tech Solutions S.A. |
| 2024003 | Carlos Souza | Ciência da Computação | Inovação Digital ME |

## Permissões

O aplicativo solicita as seguintes permissões em tempo de execução:

- `ACCESS_FINE_LOCATION` — localização precisa via GPS
- `ACCESS_COARSE_LOCATION` — localização aproximada (fallback)

## Licença

Projeto acadêmico/educacional. Sem licença definida.
