# KBnB
`Accommodation Reservation Web Backend API Server`
- front server : https://kbnb.herokuapp.com
- backend server : https://backend.kbnb.tk
- api docs : https://backend.kbnb.tk/docs/api.html
    
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
- Infra 구성도
- CI/CD
- 구현 기능 부연 설명
- 프로젝트 진행 일정

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

## Usage Skill

![pic3](https://user-images.githubusercontent.com/12459864/109123499-4106f080-778d-11eb-812d-d94b581727af.png)

## Infra Structure

![kbnb aws infra (1)](https://user-images.githubusercontent.com/12459864/109154112-2e9eae00-77b1-11eb-90a8-883be7acb85a.png)
- 과정 설명
    1. 사용자가 React Server로 접근한다
    1. React Server는 Backend Server의 도메인 ip 주소를 DNS에 요청한다
    1. DNS가 ip주소를 반환해 준다
    1. 해당 ip주소의 433포트로 접근한다
    1. Web Server가 요청을 처리해주는 WAS로 보낸다
    1. WAS는 해당 요청의 URL을 통해 처리해주는 Handler로 매핑시킨 후 DB 정보가 필요하다면 AWS RDS에 쿼리를 보낸다
    1. DB애서 요청 쿼리에 맞는 응답을 보낸다
    1. Handler에서 Http Response 만들어서 Web Server로 보낸다
    1. Web Server는 요청이 들어온 React Server로 응답 정보를 보낸다
    1. React Server는 응답 결과와 HTML을 조합해서 사용자에게 HTML을 전달한다
    
## CI/CD

![kbnb aws infra](https://user-images.githubusercontent.com/12459864/109153345-242fe480-77b0-11eb-9bd6-bce8611bff23.png)

## 구현 기능 부연 설명

- OAuth2 

    ![OAuht2 Flow](https://user-images.githubusercontent.com/12459864/109258919-9222ed00-783e-11eb-8cf9-631f63989ac8.png)

- Query DSL 이용한 숙소 검색
    - API 문서 URL : https://backend.kbnb.tk/docs/api.html#resource-room-get-list-by-condition
    - 위치 조건은 필수값, 나머지는 동적으로 검색 가능
    - 검색 조건 종류
        - 위치 : 위도 경도 범위값으로 숙소 검색
        - 체크인, 체크아웃 날짜 : 해당 날짜에 예약 가능한 숙소 검색
        - 총 인원 수 : 제한 인원 내에 있는 숙소로 검색
        - 최저 최대 비용 : 최저 최대 비용 사이에 있는 숙소 검색
        - 숙소 유형 : 유형에 맞는 숙소 검색
        - 침대 수: 설정 값 이상의 숙소 검색
        - 침실 수: 설정 값 이상의 숙소 검색
        - 욕실 수: 설정 값 이상의 숙소 검색
            
- 예약시 결제 연동 
    - boot pay api 사용
    - API 문서 URL : https://backend.kbnb.tk/docs/api.html#resource-reservation-register
    - 결제 과정
        ![bootpay](https://user-images.githubusercontent.com/12459864/109171617-4d0ea480-77c5-11eb-9dd1-2513389ac157.png)
        
- 숙소 추천 API 로직
    - API 문서 URL : https://backend.kbnb.tk/docs/api.html#resource-room-recommend
    - 추천 내부 로직
        1. 해당 숙소 지난 달 예약률 조회
        1. 해당 숙소 예약률 90%이상인 숙소 확인
        1. 90% 이상일 때 추천 숙소로 응답값 전송

- 호스트 수입 조회 API 로직
    - 로직 설명
    
        ![image](https://user-images.githubusercontent.com/12459864/109259208-18d7ca00-783f-11eb-8c5a-21da073f6d7f.png)
        
        ![20210226140354](https://user-images.githubusercontent.com/12459864/109259301-4290f100-783f-11eb-9070-6c122b02faa0.png)
        
        1.    IncomeRequest로 year, month를 받습니다.
        2.    reservationService.findByHostFilterByYear메소드에 host와 incomeRequest의 year필드를 파라미터로 건네줍니다.
        3.    건네받은 host파라미터를 이용하여 reservationRepository.findByHostWithPayment를 시전합니다. -> 결과값으로 host가 호스트인 모든 예약내역이 payment와 함께 리스트로 생성됩니다.
        4.    해당 예약들을 year파라미터로 year년에 일치하는 예약만 걸러냅니다.
        5.    결과적으로 해당년에 예약된 예약만 리스트로 반환됩니다.(byYear)
        6.    reservationService.separateByMonth메소드에 byYear리스트를 파라미터로 건네줍니다.
        7.    리턴값으로 byYear의 월별 합산치가 incomeResponse에 반환됩니다.
        8.    incomeResponse.setYearlyIncome()을 통해 연별 합산치를 세팅합니다.
        9.    연별, 월별 합산치가 포함된 IncomeResponse를 ResponseEntity로 래핑합니다.

## Project Schedule
- 프로젝트 계획 방법 : git issue, git milestones, git project 활용
- 전체 일정 요약
    ![20210226141128](https://user-images.githubusercontent.com/12459864/109259583-d8c51700-783f-11eb-8c86-fd843c3c6575.png)