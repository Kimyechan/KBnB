# KBnB
<<<<<<< HEAD
Room reservation spring web server project
![20210225125501](https://user-images.githubusercontent.com/50402288/109100781-e196e980-7768-11eb-9f84-b2ea84f2bc9d.png)

## Dependencies

    asciidoctor 'org.springframework.restdocs:spring-restdocs-asciidoctor'
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
    compile group: 'io.jsonwebtoken', name: 'jjwt', version: '0.9.1'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'mysql:mysql-connector-java'
    implementation 'com.querydsl:querydsl-jpa'
    implementation group: 'org.springframework.cloud', name: 'spring-cloud-starter-aws', version: '2.2.1.RELEASE'

    compile group: 'org.apache.httpcomponents', name: 'httpclient', version: '+'
    compile group: 'com.google.code.gson', name: 'gson', version: '+'
    compile group: 'commons-io', name: 'commons-io', version: '+'

    implementation 'org.modelmapper:modelmapper:2.3.2'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-hateoas'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    annotationProcessor 'org.projectlombok:lombok'
    compileOnly 'org.projectlombok:lombok'
    compileOnly 'org.springframework.boot:spring-boot-configuration-processor'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
    testImplementation 'com.h2database:h2:1.4.199 '
    
## Tech/Framework used
- Java 11
- Spring boot 2.0
- Oauth2
- Spring Security
- Spring HATEOAS
- Spring Rest Docs
- Query dsl
- AWS EC2
- AWS S3
- JPA
- MySql
- JWT
- H2

## CI/CD
- Heroku
- GitHub Action

## Contribute
[Kimyechan](https://github.com/Kimyechan)

[Hansol](https://github.com/Hansol-Jeong)

## Provide APIs
[back-end APIs](http://3.34.198.174:8080/docs/api.html)

## Credits
[Oauth & Security setting](https://github.com/callicoder/spring-boot-react-oauth2-social-login-demo)
[Use of Rest Docs](https://docs.spring.io/spring-restdocs/docs/current/reference/html5/)
=======
Accommodation Reservation Web Backend API Server

## Table of Contents
- 프로젝트 실행 방법
- 프로젝트 설계
    - UseCase Diagram
    - ERD
- 프로젝트 컨벤션
    - Commit 
    - Sprint
- 사용 기술
    - Infra
    - development
    - documentation
- CI/CD
- 구현된 기능

## How to run project
### Register secret config file
- bootpay.yml (결제 시스템 설정)
    ```yaml
    boot-pay:
      applicationId: [어플리케이션 ID] 
      privateKey: [어플리케이션 private key]
    ```
- database.yml (DB 설정)
    ```yaml
    spring:
      datasource:
        url: [DB URL]
        username: [DB user name]
        password: [DB password]
    ```
- oauth2.yml (Google OAuth2 로그인 설정)

    `기존 application.yml 설정을 덮어주세요` 
    
    ```yaml
    security:
        oauth2:
          client:
            registration:
              google:
                clientId: [Google OAuth2 Client Id]
                clientSecret: [Google OAuth2 Client Secret]
                redirectUri: "{baseUrl}/oauth2/callback/{registrationId}"
                scope:
                  - email
                  - profile
    ```
  
- secret.yml (AWS S3 키 설정)
    
    `기존 application.yml 설정을 덮어주세요` 
    
    ```yaml
    cloud:
      aws:
        credentials:
          accessKey: [AWS S3 Access Key]
          secretKey: [AWS S3 Secret Key]
        s3:
          bucket: [버킷 이름]
          region:
            static: [버킷 지역]
          stack:
            auto: false
          credentials:
            instanceProfile: true
    ```
### Run Application
- build
    - linux 
        ```
        ./gradlew build
        ```
    - window
        ```
        gradlew.bat build
        ```
- run
    ```
    java -jar ./build/lib/[jar file name]
    ```

## Design
### Usecase Diagram

![K B B USECASE (3)](https://user-images.githubusercontent.com/12459864/109120339-2cc0f480-7789-11eb-86e6-7103febeffe2.png)

- 일반 사용자 사용 기능(로그인하지 않은 사용자)
    - 유저
        - 회원가입 
        - 로그인
    - 숙소
        - 숙소 검색
        - 숙소 상세 보기
        - 추천 숙소 여부 조회
    - 댓글
        - 댓글 리스트 조회
- 고객 사용 기능(로그인한 사용자)
    - 일반 사용자 기능 전부
    - 유저
        - 유저 정보 보기
        - 유저 정보 수정
    - 예약
        - 예약하기
        - 예약 취소하기
        - 예약 상세보기
        - 예약 리스트 보기
    - 댓글
        - 댓글 작성
    - 호스트
        - 숙소 등록
        - 숙소 수입 관리
        - 숙소 예약 관리
            
- 위치 정보 시스템 (Google Map)
    - 숙소 검색시 사용
- OAuth2 서버 (Google)
    - 로그인 및 회원가입시 사용
- 결제 시스템  
    - 예약시 결제 연동
    - 예약취소시 결제 취소 연동
    
### ERD

![K-B B DATABASE (2)](https://user-images.githubusercontent.com/12459864/109127898-44e94180-7792-11eb-8e0f-2b91bd0c3eb9.png)
>>>>>>> f7099f1272b8ce5078383034232d129001b0cd28

## Project Convention
### Git Commit
#### Prefix
- Feat: 새로운 기능 추가
- Fix: 버그 수정
- Docs: 문서 수정
- Style: 코드 포맷팅
- Refactor: 코드 리펙토링
- Test: 테스트 코드 추가
- Chore: 빌드 업무 수정
- Conf: 설정 파일 수정
- BREAKING CHANGE: 모두가 봐야하는 커밋

#### Body
- 조금의 설명이라도 붙이는 것을 권장 
- Issue tagging 사용 의무 : solved #{number}

### Project Develop Plan
- Sprint 주기 4일
- 매일 개발 시작전 10분 Scrum
- Code Review는 Pull Request 발생시에 바로 시작
- 기능 개발 끝나면 바로 Deploy

<<<<<<< HEAD
=======
## Usage Skill

![pic3](https://user-images.githubusercontent.com/12459864/109123499-4106f080-778d-11eb-812d-d94b581727af.png)
>>>>>>> f7099f1272b8ce5078383034232d129001b0cd28
