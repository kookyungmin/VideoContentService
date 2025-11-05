# VideoContentService

Redis를 활용한 헥사고날 아키텍처 기반 비디오 컨텐츠 서비스

<!-- prettier-ignore-start -->
![SpringBoot](https://shields.io/badge/springboot-black?logo=springboot&style=for-the-badge%22)
![Docker](https://shields.io/badge/docker-black?logo=docker&style=for-the-badge%22)
![Mysql](https://shields.io/badge/mysql-black?logo=mysql&style=for-the-badge%22)
![Redis](https://shields.io/badge/redis-black?logo=redis&style=for-the-badge%22)
![MongoDB](https://shields.io/badge/mongodb-black?logo=mongodb&style=for-the-badge%22)
<!-- prettier-ignore-end -->

### System Requirements

- [java] 17
- [springboot] 3.3.0
- [docker] 20.10.12
- [mysql] 8
- [redis] 7.2.5
- [mongodb] 6.0.16

## 프로젝트 구조

```
vcs
├── adapter
│     ├── in                      
│     │    ├── api                // Rest API Controller
│     │    │     ├── advice       // ControllerAdvice
│     │    │     ├── attribute    // Header, Parameter Attribute
│     │    │     └── dto          // Request, Response DTO
│     │    └── resolver           // MethodArgumentResolver
│     └── out                     // PersistenceAdapter
│          ├── jpa                // JpaRepository
│          │     ├── channel
│          │     ├── subscribe
│          │     ├── user
│          │     └── video
│          ├── mongo              // MongoRepository
│          │     └── comment
│          └── redis              // RedisRepository
│                ├── channel
│                └── user
├── application                   // Service Application
│     ├── listener                // Subscribe Message Listener
│     ├── port                    // In/Out Port
│     │     ├── in
│     │     └── out
│     └── schedule                // Scheduled Task
├── common                        // Common Utils
├── config                        // Configuration
├── domain                        // Domain
│     ├── channel
│     ├── comment
│     ├── message
│     ├── user
│     └── video
└── exception                      // Custom Exception
```        
---

## 관련 라이브러리
### Embedded Redis[^1]
https://github.com/codemonstur/embedded-redis

### Embedded Mongo for Spring 3.x[^2]
https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo.spring

---

## Local 실행 환경
### MySQL, Redis, MongoDB start
`/bin/docker-compose-up.sh`

### MySQL, Redis, MongoDB stop
`/bin/docker-compose-down.sh`

### Spring Boot application 실행
`./gradlew bootRun`

## docker 실행 상태에서 DB/Redis 접근
### MySQL
`docker exec -it vcs-mysql bash` \
`mysql -u local -p`

### Redis
`docker exec -it vcs-redis sh` \
`redis-cli`

### MongoDB
`docker exec -it vcs-mongodb sh` \
`mongosh -u local -p local`
