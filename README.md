# Resale Platform — Платформа объявлений

Веб-приложение для размещения и управления объявлениями с возможностью комментирования. Разработано на Spring Boot 3.

## Функциональность

- Регистрация и аутентификация пользователей (HTTP Basic Auth)
- Управление объявлениями: создание, редактирование, удаление, просмотр
- Комментирование объявлений
- Загрузка и обновление изображений объявлений (JPEG/PNG/GIF) и аватаров пользователей
- Ролевая модель: USER (обычный пользователь) и ADMIN (полный доступ)
- Валидация данных на уровне DTO (Jakarta Validation)
- Swagger UI с полной документацией API

## Технологический стек

| Компонент | Технология |
|---|---|
| Язык | Java 17 |
| Фреймворк | Spring Boot 3.5, Spring MVC, Spring Security |
| База данных | PostgreSQL 15 |
| ORM | Spring Data JPA, Hibernate |
| Миграции БД | Liquibase |
| Маппинг DTO | MapStruct 1.5.5 |
| Валидация | Jakarta Validation (Hibernate Validator) |
| Документация API | OpenAPI / Swagger UI (SpringDoc 2.7) |
| Контейнеризация | Docker |
| Сборка | Maven |
| Тестирование | JUnit 5, Mockito, MockMvc, H2 |

## Запуск

### Требования

- Java 17+
- PostgreSQL (или Docker)
- Maven (или `./mvnw`)

### 1. Создание базы данных

```sql
CREATE DATABASE resale_platform_db;
CREATE USER resale_admin WITH PASSWORD 'resaledb';
GRANT ALL PRIVILEGES ON DATABASE resale_platform_db TO resale_admin;
```

Или через Docker:

```bash
docker run -d --name postgres -e POSTGRES_DB=resale_platform_db -e POSTGRES_USER=resale_admin -e POSTGRES_PASSWORD=resaledb -p 5432:5432 postgres:15
```

### 2. Сборка и запуск

```bash
./mvnw clean spring-boot:run
```

Приложение запустится на `http://localhost:8080`.

Миграции Liquibase выполнятся автоматически при старте.

### 3. Swagger UI

После запуска документация API доступна по адресу:

```
http://localhost:8080/swagger-ui.html
```

## Структура проекта

```
src/
├── main/java/ru/skypro/homework/
│   ├── HomeworkApplication.java        # Точка входа
│   ├── config/                         # Конфигурация Security, CORS, UserDetailsService
│   ├── controller/                     # REST-контроллеры (Auth, Ad, Comment, User)
│   ├── dto/                            # DTO (Java records) для запросов и ответов
│   │   ├── auth/                       # Login, Register
│   │   ├── ad/                         # AdDto, AdsDto, CreateOrUpdateAdDto, ExtendedAdDto
│   │   ├── comment/                    # CommentDto, CommentsDto, CreateOrUpdateCommentDto
│   │   └── user/                       # UserDto, UpdateUserDto, NewPasswordDto
│   ├── exception/                      # Исключения + глобальный обработчик (@RestControllerAdvice)
│   ├── mapper/                         # MapStruct-мапперы (AdMapper, CommentMapper, UserMapper)
│   ├── model/                          # JPA-сущности (Ad, Comment) + user/ (User, Role)
│   ├── repository/                     # Spring Data JPA репозитории
│   └── service/                        # Бизнес-логика
│       ├── AdService.java
│       ├── AuthService.java            # Интерфейс
│       ├── CommentService.java
│       ├── ImageService.java
│       ├── UserService.java
│       ├── ValidatorService.java       # Проверка прав доступа
│       └── impl/AuthServiceImpl.java
├── main/resources/
│   ├── application.properties
│   └── liquibase/                      # Миграции БД
│       ├── db.changelog-master.yaml
│       └── scripts/
│           ├── 001-create-users-table.sql
│           ├── 002-create-ads-table.sql
│           └── 003-create-comments-table.sql
└── test/java/ru/skypro/homework/
    ├── controller/                     # Интеграционные тесты контроллеров (MockMvc)
    └── service/                        # Юнит-тесты сервисов (Mockito)
```

## Конфигурация

### Файловое хранилище

В `application.properties` задаются директории для хранения изображений:

```properties
images.dir.path=uploads/images      # директория для картинок объявлений
avatars.dir.path=uploads/avatars    # директория для аватаров пользователей
```

Имена файлов формируются по шаблону `{UUID}.{расширение}` (например, `a1b2c3d4-e5f6.jpg`).

### Spring Security

- Аутентификация: HTTP Basic Auth
- Шифрование паролей: BCrypt
- CSRF: отключён (REST API)

### Тестирование

```properties
# application-test.properties (автоматически при тестировании)
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

## REST API Endpoints

### Авторизация

| Метод | URL | Auth | Описание |
|---|---|---|---|
| `POST` | `/login` | Нет | Вход в систему (Basic Auth) |
| `POST` | `/register` | Нет | Регистрация нового пользователя |

### Пользователи

| Метод | URL | Auth | Описание |
|---|---|---|---|
| `GET` | `/users/me` | USER, ADMIN | Информация о текущем пользователе |
| `PATCH` | `/users/me` | USER, ADMIN | Обновить профиль (firstName, lastName, phone) |
| `POST` | `/users/set_password` | USER, ADMIN | Сменить пароль |
| `PATCH` | `/users/me/image` | USER, ADMIN | Обновить аватар (multipart/form-data, JPEG/PNG/GIF) |
| `GET` | `/users/image/{fileName}` | Нет | Получить изображение аватара |

### Объявления

| Метод | URL | Auth | Описание |
|---|---|---|---|
| `GET` | `/ads` | Нет | Все объявления |
| `GET` | `/ads/me` | USER, ADMIN | Мои объявления |
| `GET` | `/ads/{id}` | Нет | Детали объявления |
| `POST` | `/ads` | USER, ADMIN | Создать объявление (multipart: properties + image) |
| `PATCH` | `/ads/{id}` | USER, ADMIN | Обновить объявление |
| `DELETE` | `/ads/{id}` | USER, ADMIN | Удалить объявление |
| `PATCH` | `/ads/{id}/image` | USER, ADMIN | Обновить изображение (multipart, JPEG/PNG/GIF) |
| `GET` | `/ads/image/{fileName}` | Нет | Получить изображение объявления |

### Комментарии

| Метод | URL | Auth | Описание |
|---|---|---|---|
| `GET` | `/ads/{id}/comments` | Нет | Комментарии объявления |
| `POST` | `/ads/{id}/comments` | USER, ADMIN | Добавить комментарий |
| `PATCH` | `/ads/{adId}/comments/{id}` | USER, ADMIN | Обновить комментарий |
| `DELETE` | `/ads/{adId}/comments/{id}` | USER, ADMIN | Удалить комментарий |

## Модель безопасности

| Роль | Чтение | Создание | Редактирование | Удаление |
|---|---|---|---|---|
| Гость (нет auth) | Объявления, комментарии, изображения | Нет | Нет | Нет |
| USER | Все + свои объявления | Объявления, комментарии | Свои объявления, свои комментарии | Свои объявления, свои комментарии |
| ADMIN | Все | Объявления, комментарии | Любые объявления, любые комментарии | Любые объявления, любые комментарии |

Проверка прав доступа выполняется через `ValidatorService`:

- **Владелец объявления**: `ad.author.email == userDetails.username`
- **Владелец комментария**: `comment.author.email == userDetails.username`
- **ADMIN**: пропускает все проверки прав

## Схема базы данных

```mermaid
erDiagram
    users {
        int id PK "автоинкремент"
        varchar email UK "логин (уникальный)"
        varchar password "BCrypt hash"
        varchar first_name "имя"
        varchar last_name "фамилия"
        varchar phone "+7 XXX XXX-XX-XX"
        varchar role "USER | ADMIN"
        varchar image "путь к аватару"
    }

    ads {
        int id PK "автоинкремент"
        varchar title "заголовок (4-32 символов)"
        int price "цена (0+)"
        text description "описание (8-64 символа)"
        varchar image "путь к картинке"
        int author_id FK "ссылка на users.id"
    }

    comments {
        int id PK "автоинкремент"
        varchar text "текст комментария"
        bigint created_at "unix timestamp (мс)"
        int author_id FK "ссылка на users.id"
        int ad_id FK "ссылка на ads.id"
    }

    users ||--o{ ads : "создаёт"
    users ||--o{ comments : "пишет"
    ads ||--o{ comments : "имеет"
```

## Архитектура

```mermaid
graph TD
    Client["Клиент (React / Swagger UI)"]

    subgraph "Spring Boot Application"
        AC["AuthController<br/>POST /login, /register"]
        ADC["AdController<br/>GET/POST/PATCH/DELETE /ads"]
        CC["CommentController<br/>GET/POST/PATCH/DELETE /ads/{id}/comments"]
        UC["UserController<br/>GET/PATCH /users/me"]

        AS["AuthService"]
        ADS["AdService"]
        CS["CommentService"]
        US["UserService"]
        IS["ImageService"]
        VS["ValidatorService"]

        AM["AdMapper"]
        CM["CommentMapper"]
        UM["UserMapper"]
    end

    subgraph "Data Layer"
        UR["UserRepository"]
        ADR["AdRepository"]
        CR["CommentRepository"]
    end

    DB[("PostgreSQL")]
    FS["Файловая система<br/>uploads/images/<br/>uploads/avatars/"]

    Client --> AC
    Client --> ADC
    Client --> CC
    Client --> UC

    AC --> AS
    ADC --> ADS
    CC --> CS
    UC --> US

    ADS --> IS
    US --> IS
    ADS --> VS
    CS --> VS

    ADS --> AM
    CS --> CM
    US --> UM

    AS --> UR
    ADS --> ADR
    CS --> CR
    US --> UR

    UR --> DB
    ADR --> DB
    CR --> DB

    IS --> FS
```

## Диаграммы последовательности

### Регистрация нового пользователя

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthServiceImpl
    participant UR as UserRepository
    participant PE as PasswordEncoder

    C->>AC: POST /register (Register DTO)
    AC->>AS: register(register)
    AS->>UR: findByEmail(email)
    alt Пользователь уже существует
        UR-->>AS: Optional.of(user)
        AS-->>AC: false
        AC-->>C: 400 Bad Request
    else Email свободен
        UR-->>AS: Optional.empty()
        AS->>PE: encode(password)
        PE-->>AS: BCrypt hash
        AS->>UR: save(User)
        UR-->>AS: User saved
        AS-->>AC: true
        AC-->>C: 201 Created
    end
```

### Вход в систему

```mermaid
sequenceDiagram
    participant C as Client
    participant AC as AuthController
    participant AS as AuthServiceImpl
    participant UR as UserRepository
    participant PE as PasswordEncoder

    C->>AC: POST /login (Login DTO)
    AC->>AS: login(username, password)
    AS->>UR: findByEmail(username)
    alt Пользователь не найден
        UR-->>AS: Optional.empty()
        AS-->>AC: false
        AC-->>C: 401 Unauthorized
    else Пользователь найден
        UR-->>AS: User
        AS->>PE: matches(password, user.password)
        alt Пароль неверный
            PE-->>AS: false
            AS-->>AC: false
            AC-->>C: 401 Unauthorized
        else Пароль верный
            PE-->>AS: true
            AS-->>AC: true
            AC-->>C: 200 OK
        end
    end
```

### Создание объявления

```mermaid
sequenceDiagram
    participant C as Client
    participant ADC as AdController
    participant ADS as AdService
    participant IS as ImageService
    participant AM as AdMapper
    participant ADR as AdRepository

    C->>ADC: POST /ads (multipart: properties + image)
    ADC->>ADS: createAd(userDetails, properties, image)
    ADS->>IS: saveImage(image)
    IS-->>ADS: fileName (UUID)
    ADS->>AM: toEntity(properties)
    AM-->>ADS: Ad (без автора)
    ADS->>ADS: ad.setAuthor(user)
    ADS->>ADS: ad.setImage(fileName)
    ADS->>ADR: save(ad)
    ADR-->>ADS: Ad with ID
    ADS->>AM: toDto(ad)
    AM-->>ADS: AdDto
    ADS-->>ADC: AdDto
    ADC-->>C: 201 Created (AdDto)
```

### Обновление объявления (с проверкой прав)

```mermaid
sequenceDiagram
    participant C as Client
    participant ADC as AdController
    participant ADS as AdService
    participant VS as ValidatorService
    participant AM as AdMapper
    participant ADR as AdRepository

    C->>ADC: PATCH /ads/{id} (CreateOrUpdateAdDto)
    ADC->>ADS: updateAd(userDetails, id, dto)
    ADS->>ADR: findById(id)
    alt Объявление не найдено
        ADR-->>ADS: Optional.empty()
        ADS-->>ADC: throw ResourceNotFoundException
        ADC-->>C: 404 Not Found
    else Объявление найдено
        ADR-->>ADS: Ad
        ADS->>VS: checkAdOwnership(ad, userDetails)
        alt Нет прав (не автор и не ADMIN)
            VS-->>ADS: throw ForbiddenException
            ADS-->>ADC: 403 Forbidden
            ADC-->>C: 403 Forbidden
        else Права есть
            VS-->>ADS: ok
            ADS->>AM: updateAd(dto, ad)
            AM-->>ADS: Ad (обновлён)
            ADS->>ADR: save(ad)
            ADS->>AM: toDto(ad)
            AM-->>ADS: AdDto
            ADS-->>ADC: AdDto
            ADC-->>C: 200 OK (AdDto)
        end
    end
```

### Добавление комментария

```mermaid
sequenceDiagram
    participant C as Client
    participant CC as CommentController
    participant CS as CommentService
    participant ADR as AdRepository
    participant CR as CommentRepository
    participant UR as UserRepository
    participant CM as CommentMapper

    C->>CC: POST /ads/{id}/comments (CreateOrUpdateCommentDto)
    CC->>CS: createComment(adId, dto, userDetails)
    CS->>ADR: findById(adId)
    alt Объявление не найдено
        ADR-->>CS: Optional.empty()
        CS-->>CC: throw ResourceNotFoundException
        CC-->>C: 404 Not Found
    else Объявление найдено
        ADR-->>CS: Ad
        CS->>UR: findByEmail(userDetails.username)
        UR-->>CS: User
        CS->>CM: toEntity(dto)
        CM-->>CS: Comment
        CS->>CS: comment.setAd(ad)
        CS->>CS: comment.setAuthor(user)
        CS->>CR: save(comment)
        CR-->>CS: Comment with ID
        CS->>CM: toDto(comment)
        CM-->>CS: CommentDto
        CS-->>CC: CommentDto
        CC-->>C: 200 OK (CommentDto)
    end
```

### Обновление аватара пользователя

```mermaid
sequenceDiagram
    participant C as Client
    participant UC as UserController
    participant US as UserService
    participant IS as ImageService
    participant UR as UserRepository

    C->>UC: PATCH /users/me/image (MultipartFile)
    UC->>US: uploadAvatar(userDetails, image)
    US->>UR: findByEmail(email)
    UR-->>US: User
    alt Уже есть аватар
        US->>IS: updateAvatar(oldFileName, image)
        IS->>IS: delete old file
        IS->>IS: validate + save new file
        IS-->>US: ImageResult(fileName, bytes)
    else Первый аватар
        US->>IS: saveAvatar(image)
        IS->>IS: validate (JPEG/PNG/GIF)
        IS->>IS: save to uploads/avatars/{UUID}.{ext}
        IS-->>US: fileName
    end
    US->>US: user.setImage(fileName)
    US->>UR: save(user)
    UR-->>US: saved
    US-->>UC: void
    UC-->>C: 200 OK
```

### Удаление объявления (с проверкой прав)

```mermaid
sequenceDiagram
    participant C as Client
    participant ADC as AdController
    participant ADS as AdService
    participant VS as ValidatorService
    participant ADR as AdRepository

    C->>ADC: DELETE /ads/{id}
    ADC->>ADS: deleteAd(userDetails, id)
    ADS->>ADR: findById(id)
    alt Объявление не найдено
        ADR-->>ADS: Optional.empty()
        ADS-->>ADC: throw ResourceNotFoundException
        ADC-->>C: 404 Not Found
    else Объявление найдено
        ADR-->>ADS: Ad
        ADS->>VS: checkAdOwnership(ad, userDetails)
        alt Нет прав
            VS-->>ADS: throw ForbiddenException
            ADS-->>ADC: 403 Forbidden
            ADC-->>C: 403 Forbidden
        else Права есть (автор или ADMIN)
            VS-->>ADS: ok
            ADS->>ADR: deleteById(id)
            ADS-->>ADC: void
            ADC-->>C: 204 No Content
        end
    end
```

## Тестирование

```bash
./mvnw test
```

Для тестов используется H2 in-memory база данных. Контроллер-тесты используют `@WebMvcTest` + `MockMvc` с отключёнными security filters (`addFilters = false`).

| Тип тестов | Кол-во | Фреймворк |
|---|---|---|
| Юнит-тесты сервисов | 50 | Mockito |
| Интеграционные тесты контроллеров | 34 | MockMvc + Spring Security Test |
| **Итого** | **84** | |

## Docker

```bash
docker build -t resale-platform .
docker run -p 8080:8080 resale-platform
```

## OpenAPI

Swagger UI: `http://localhost:8080/swagger-ui.html`

OpenAPI JSON: `http://localhost:8080/v3/api-docs`
