# KBnB
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

