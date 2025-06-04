# Re:Use (리유즈)

## 📄 프로젝트 개요
**프로젝트명**: Re:Use (리유즈)  
**프로젝트 기간**: 2025.02.07 ~ 2025.02.18  
**팀원 구성**:  
- 백엔드: 김민석, 홍진우, 박성진  
- 프론트엔드: 표상혁, 백욱진, 송보민  

**프로젝트 설명**:  
Re:Use는 사용자 간 중고 물품을 쉽고 안전하게 거래할 수 있도록 돕는 웹사이트입니다.  
- 판매자는 간편하게 물품을 등록하고  
- 구매자는 원하는 물품을 찾아 댓글로 문의한 후 거래를 진행할 수 있습니다.

---

## 🔖 기능 요약

1. **상품 관리 (Products)**  
   - 전체 상품 조회 (페이징, 검색 지원)  
   - 상품 상세 조회  
   - 상품 등록 (AWS S3 이미지 업로드)  
   - 상품 수정 / 삭제  

2. **찜 목록 (Wishlist)**  
   - 상품 찜 추가 / 삭제  
   - 사용자별 찜 목록 조회  

3. **장바구니 (Cart)**  
   - 상품 담기 / 삭제  
   - 장바구니 목록 조회  
   - 모의 결제(장바구니 → 주문 테이블)  

4. **인증/보안**  
   - JWT 기반 로그인/회원가입 (Spring Security)  
   - AccessToken/RefreshToken 자동 재발급  

5. **실시간 알림 & 인기 상품 추천**  
   - Kafka 기반 알림(채팅, 문의)  
   - Redis 캐싱을 활용한 인기 상품 랭킹  

---

## 🛠 개발 환경 및 기술 스택

- **언어 & 프레임워크**:  
  - Java 17, Spring Boot 3.x  
  - React (프론트엔드 팀 구현)  
- **데이터베이스**:  
  - MySQL (AWS RDS)  
- **인증/인가**:  
  - Spring Security + JWT  
- **파일 스토리지**:  
  - AWS S3 (이미지 업로드)  
- **메시징 & 캐싱**:  
  - Kafka (알림), Redis (인기 상품 캐싱)  
- **서비스 레지스트리 & API Gateway**:  
  - Eureka Server/Client, Eureka Gateway  
- **CI/CD & 배포**:  
  - GitHub Actions, AWS EC2  
- **빌드 도구**:  
  - Maven  

---

## 📁 프로젝트 구조

```
re-use-backend/
├── src/
│   ├── main/
│   │   ├── java/com/reuse/
│   │   │   ├── controller/
│   │   │   │   ├── ProductsController.java
│   │   │   │   ├── WishlistController.java
│   │   │   │   └── CartController.java
│   │   │   ├── service/
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── WishlistService.java
│   │   │   │   └── CartService.java
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── WishlistRepository.java
│   │   │   │   └── CartRepository.java
│   │   │   ├── config/
│   │   │   │   ├── S3Config.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── JwtTokenProvider.java
│   │   │   ├── dto/
│   │   │   │   ├── ProductDto.java
│   │   │   │   ├── ProductReqDto.java
│   │   │   │   ├── WishlistDto.java
│   │   │   │   └── CartDto.java
│   │   │   ├── entity/
│   │   │   │   ├── ProductEntity.java
│   │   │   │   ├── WishlistEntity.java
│   │   │   │   └── CartEntity.java
│   │   │   └── exception/
│   │   │       ├── CustomException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── static/
│   └── test/
├── .github/
│   └── workflows/
│       └── ci-cd.yml
├── pom.xml
└── README.md
```

---

## 🚀 환경 설정 및 실행 방법

1. **환경 변수 설정**  
   `application.yml` 또는 환경 변수로 다음 항목을 설정해야 합니다:
   ```
   spring:
     datasource:
       url: jdbc:mysql://<RDS_ENDPOINT>:3306/<DB_NAME>?useSSL=false&serverTimezone=Asia/Seoul
       username: <DB_USERNAME>
       password: <DB_PASSWORD>

   cloud:
     aws:
       credentials:
         access-key: <AWS_ACCESS_KEY>
         secret-key: <AWS_SECRET_KEY>
       region:
         static: ap-northeast-2
       s3:
         bucket: <S3_BUCKET_NAME>

   jwt:
     secret: <JWT_SECRET_KEY>
     access-expiration: 600000   # 10분
     refresh-expiration: 259200000 # 3일
   ```

2. **프로젝트 빌드 및 실행**  
   ```bash
   # 저장소 클론
   git clone https://github.com/<사용자명>/re-use-backend.git
   cd re-use-backend

   # 의존성 설치 및 빌드
   mvn clean install

   # 애플리케이션 실행
   mvn spring-boot:run
   ```
   - 기본 포트: `8080`
   - 성공 시 `http://localhost:8080/actuator/health` 접근 가능

3. **프론트엔드 연동**  
   - 프론트 저장소: [Re:Use-Frontend](https://github.com/<사용자명>/re-use-frontend)  
   - React 앱을 `npm install && npm start`로 실행  
   - `proxy` 설정으로 `http://localhost:8080`에 API 요청

---

## 📌 주요 API 명세

### 1. 상품 (Products)
- **GET /api/products**  
  - 설명: 전체 상품 목록 조회  
  - Query Params:  
    - `page` (int, 기본값: 0)  
    - `size` (int, 기본값: 10)  
    - `sort` (예: `createdDate,desc`)  
    - `keyword` (String, 선택)  
  - 응답: `Page<ProductDto>`

- **GET /api/products/{id}**  
  - 설명: 특정 상품 상세 조회  
  - Path Variable: `id` (Long)  
  - 응답: `ProductDto`

- **POST /api/products** (인증 필요)  
  - 설명: 상품 등록  
  - Request (multipart/form-data):  
    - `title` (String)  
    - `description` (String)  
    - `price` (Integer)  
    - `category` (String)  
    - `file` (MultipartFile)  
  - 응답: `ProductDto`

- **PUT /api/products/{id}** (인증 & 작성자만)  
  - 설명: 상품 수정  
  - Request (multipart/form-data, 선택):  
    - `title`, `description`, `price`, `category`, `file`  
  - 응답: `ProductDto`

- **DELETE /api/products/{id}** (인증 & 작성자만)  
  - 설명: 상품 삭제  
  - 응답: HTTP 204 No Content

---

### 2. 찜 목록 (Wishlist)
- **POST /api/wishlist/{productId}** (인증 필요)  
  - 설명: 해당 상품을 찜 목록에 추가  
  - Path Variable: `productId` (Long)  
  - 응답: `"찜 목록에 추가되었습니다."`

- **DELETE /api/wishlist/{productId}** (인증 필요)  
  - 설명: 찜 목록에서 해당 상품 제거  
  - Path Variable: `productId` (Long)  
  - 응답: `"찜 목록에서 제거되었습니다."`

- **GET /api/wishlist** (인증 필요)  
  - 설명: 현재 사용자의 찜 목록 조회  
  - 응답: `List<ProductDto>`

---

### 3. 장바구니 (Cart)
- **POST /api/cart/{productId}** (인증 필요)  
  - 설명: 상품 장바구니 담기  
  - Path Variable: `productId` (Long)  
  - 응답: `"장바구니에 담겼습니다."`

- **DELETE /api/cart/{productId}** (인증 필요)  
  - 설명: 장바구니에서 상품 삭제  
  - Path Variable: `productId` (Long)  
  - 응답: `"장바구니에서 제거되었습니다."`

- **GET /api/cart** (인증 필요)  
  - 설명: 현재 사용자의 장바구니 목록 조회  
  - 응답: `List<CartDto>`

---

## 📝 예외 처리 및 로깅
- **GlobalExceptionHandler** (`@ControllerAdvice`)를 통해 일관된 JSON 형식의 에러 응답 제공  
- 주요 예외:  
  - `ProductNotFoundException` → HTTP 404  
  - `CustomException` (비즈니스 로직 에러) → 해당 상태 코드  
  - `MethodArgumentNotValidException` (유효성 검사 실패) → HTTP 400  

---

## 🤝 협업 및 코드 관리
- **버전 관리**: Git, GitHub Flow  
- **코드 리뷰**: Pull Request 기반 리뷰 진행  
- **API 문서화**: Notion API 명세서 공유  
- **CI/CD**: GitHub Actions → AWS EC2 자동 배포 파이프라인 구축  
- **커뮤니케이션**: Notion, Slack, Gather Town  

---

## 🔮 향후 개선 방향
1. **이미지 최적화**:  
   - S3 업로드 전 서버 측 이미지 리사이징/압축  
   - CloudFront CDN 연동으로 로딩 속도 개선  

2. **보안 강화**:  
   - XSS, CSRF 방어 로직 추가  
   - 입력 값 철저한 검증 및 Sanitization  

3. **테스트 커버리지 확대**:  
   - JUnit + Mockito 기반 단위/통합 테스트  
   - S3 통합 테스트  

4. **배포 및 모니터링**:  
   - AWS CloudWatch, Prometheus, Grafana 등을 이용한 모니터링  
   - Blue-Green 배포 방식 도입  

---

## 📜 라이선스
이 프로젝트는 [MIT License](LICENSE) 하에 배포됩니다.

