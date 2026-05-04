# BiddingGo 

<br>

<p align="center">
<img src="https://github.com/user-attachments/assets/1e36c230-8248-44f6-b5f0-971a78c39e9b" width="300"/>
</p>

<br>

<p align="center">
<img src="https://github.com/user-attachments/assets/e839d356-1bb6-477b-a87f-feeed40d6e67"  width="1200"/>
</p>


# Bidding mate

<table>
  
  <tr>
    <th>김진혁</th>
    <th>박선우</th>
    <th>윤정윤</th>
    <th>이민경</th>
    <th>이애은</th>
    <th>한규진
  </tr>
  
  <tr>
    <td align="center"><img width="150" height="210" style="object-fit: cover;" alt="김진혁" src="https://github.com/user-attachments/assets/d44abc12-4269-4be2-a6a9-f76a531a963f" /></td>
    <td align="center"><img width="150" height="210" style="object-fit: cover;" alt="박선우" src="https://github.com/user-attachments/assets/cf47d2b7-7633-4f17-8cbe-8b6429ceef67" /></td>   
    <td align="center"><img width="150" height="210" style="object-fit: cover;" alt="윤정윤" src="https://github.com/user-attachments/assets/5cde1533-77e1-4783-8ce9-d48ec236c25d" /></td>   
    <td align="center"><img width="150" height="210" style="object-fit: cover;" alt="이민경" src="https://github.com/user-attachments/assets/f62ebf05-846f-462e-b4d3-3fa3c58ce4fb" /></td>  
    <td align="center"><img width="150" height="210" style="object-fit: cover;" alt="이애은" src="https://github.com/user-attachments/assets/79be4278-e3e9-44c5-af71-57d1831e9406" /></td>   
    <td align="center"><img width="150" height="210" style="object-fit: cover;" alt="한규진" src="https://github.com/user-attachments/assets/45b114ef-0e30-4bcc-94d2-dfd548aa9b7c" /></td>  
  </tr>

  <tr>
    <td align="center">
      <a href="https://github.com/jin605"><img src="https://img.shields.io/badge/github-181717.svg?style=for-the-badge&logoColor=white" /></a>
    </td> 
    <td align="center">
      <a href="https://github.com/melly8954"><img src="https://img.shields.io/badge/github-181717.svg?style=for-the-badge&logoColor=white" /></a>
    </td>
    <td align="center">
      <a href="https://github.com/penep0"><img src="https://img.shields.io/badge/github-181717.svg?style=for-the-badge&logoColor=white" /></a>
    </td>
    <td align="center">
      <a href="https://github.com/alskung1101"><img src="https://img.shields.io/badge/github-181717.svg?style=for-the-badge&logoColor=white" /></a>
    </td>
    <td align="center">
      <a href="https://github.com/nueeaeel"><img src="https://img.shields.io/badge/github-181717.svg?style=for-the-badge&logoColor=white" /></a>
    </td>
    <td align="center">
      <a href="https://github.com/softworldqjin"><img src="https://img.shields.io/badge/github-181717.svg?style=for-the-badge&logoColor=white" /></a>
    </td>
  </tr>
</table>
    
---


# 목차
<br/>

1. [프로젝트 개요](#1--프로젝트-개요)
2. [요구사항 정의서](#2--요구사항-정의서)
3. [기술 스택](#3--기술-스택)
4. [ERD](#4--erd)
5. [테이블 정의서](#5--테이블-정의서)
6. [시스템 아키텍처](#6--시스템-아키텍처)
7. [API 명세서](#7--api-명세서)
8. [테스트 계획 및 결과 보고서](#8--테스트-계획-및-결과-보고서)
9. [팀 notion](#9--팀-notion)
10. [발표 자료](#10--발표자료)
11. [회고](#11--회고)

<br/>

---


# 1.  프로젝트 개요
<br/> 

## 📍 프로젝트 소개
**Biddinggo는 실시간 경매 기반의 중고 및 리셀 거래 플랫폼입니다.**  
사용자는 상품을 등록하고 다양한 경매 방식(일반 경매, 연장 경매, 타임딜 등)을 통해 판매할 수 있으며, 구매자는 실시간 입찰을 통해 원하는 상품을 경쟁적으로 구매할 수 있습니다.  

또한, 실시간 알림(SSE) 기반으로 입찰 상황을 즉시 반영하여 사용자에게 몰입감 있는 거래 경험을 제공합니다.  
특히, **빅크리 경매(Vickrey Auction, 2등 가격 경매)** 방식을 도입하여 보다 공정하고 전략적인 가격 형성을 지원합니다.

---

## 📍 배경 (이 서비스가 필요한 이유)

### 1. 기존 중고 거래 플랫폼의 가격 결정 한계
- 번개장터, 당근마켓 등은 정가 기반 거래로 적정 가격 형성이 어려움  
- 협상 과정이 비효율적이며 사용자 경험 저하  

### 2. 리셀 시장 성장과 공정한 거래 필요성 증가
- 한정판 및 인기 상품은 가격 변동성이 큼  
- 경매 방식이 가장 합리적인 가격 형성 구조지만 지원 플랫폼이 제한적  

### 3. 기존 경매 플랫폼의 UX 및 기능 한계
- UI/UX가 직관적이지 않고 사용성이 떨어짐  
- 실시간 입찰 경험 부족 및 시스템 반응 속도 문제  

---

## 📍 기존 서비스와의 차별점

### 📌 기존 서비스

#### KREAM
- 패션/스니커즈 특화 플랫폼  
- 모든 거래에 검수 필수 → 거래 속도 느림  
- 경매 방식이 아닌 호가 기반 거래  

#### 코베이옥션
- 전통적인 경매 플랫폼  
- UI/UX가 올드하고 접근성이 낮음  
- 실시간성 및 사용자 경험 부족  

---

### ✅ Biddinggo의 차별점

#### 1. 빅크리(Vickrey) 기반 경매 시스템
- 최고 입찰자가 낙찰되지만, 실제 결제 금액은 두 번째로 높은 입찰가로 결정  
- 과도한 가격 경쟁 방지  
- 사용자가 자신의 최대 지불 의사를 정직하게 반영하도록 유도  

#### 2. AI 기반 상품 예측가 제공
- 과거 거래 데이터 및 유사 상품 데이터를 기반으로 적정 가격 제시  
- 판매자/구매자 모두 합리적인 의사결정 가능  

#### 3. AI 기반 유사 상품 검색
- 이미지 및 텍스트 기반 유사도 분석  
- 검색 효율 및 사용자 경험 향상  

#### 4. 다양한 경매 옵션 제공
- 연장 경매 (마감 직전 입찰 시 자동 연장)  
- 타임딜 경매 (짧은 시간 집중 경쟁)  
- 검수 경매 (신뢰 기반 거래)  

#### 5. 실시간 입찰 시스템 (SSE 기반)
- 입찰 상태 및 알림을 실시간 반영  
- 사용자 간 경쟁 상황을 즉각적으로 체감 가능  

#### 6. 100% 에스크로 기반 안전 거래
- 거래 금액을 중간 보관 후 완료 시 정산  
- 구매자/판매자 자산 보호  

#### 7. 현대적인 UI/UX
- 직관적인 경매 흐름 제공  
- 기존 경매 서비스 대비 사용성 개선  

---

## 📍 기대 효과

- **합리적인 가격 형성**
  - 빅크리 경매 구조를 통한 공정한 시장 가격 결정  

- **거래 신뢰도 향상**
  - 에스크로 + 검수 옵션 기반 안전 거래 환경  

- **사용자 참여도 증가**
  - 실시간 입찰 및 알림으로 몰입도 상승  

- **판매 효율 극대화**
  - 다양한 경매 옵션을 통한 유연한 판매 전략  

- **플랫폼 경쟁력 확보**
  - AI + 실시간 시스템 + 경매 구조 결합
<br/>

---

# 2.  요구사항 정의서
![](img/Srs.png)<br/>
🔗[ 요구사항 정의서 ](https://docs.google.com/spreadsheets/d/16YGKpTcpo310JjvUu9Q1Nm9atCI-XYx5TB2GD3ocx3E/edit?gid=1901741334#gid=1901741334)
<br/>

---

# 3.  기술 스택
<br/>

## 🔧 Backend
<p>
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
  <img src="https://img.shields.io/badge/oauth2-EB5424?style=for-the-badge&logo=oauth&logoColor=white">
  <img src="https://img.shields.io/badge/jwt-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
  <img src="https://img.shields.io/badge/mybatis-000000?style=for-the-badge&logo=apache&logoColor=white">
  <img src="https://img.shields.io/badge/flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white">
</p>

## 🗄️ Database
<p>
  <img src="https://img.shields.io/badge/mariadb-003545?style=for-the-badge&logo=mariadb&logoColor=white">
  <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
  <img src="https://img.shields.io/badge/postgresql-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">
  <img src="https://img.shields.io/badge/pgvector-000000?style=for-the-badge&logo=postgresql&logoColor=white">
</p>

## 🚀 Infra
<p>
  <img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/docker--compose-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <img src="https://img.shields.io/badge/cloudflare%20r2-F38020?style=for-the-badge&logo=cloudflare&logoColor=white">
  <img src="https://img.shields.io/badge/vercel-000000?style=for-the-badge&logo=vercel&logoColor=white">
  <img src="https://img.shields.io/badge/kt%20cloud-FF0000?style=for-the-badge&logo=icloud&logoColor=white">
  <img src="https://img.shields.io/badge/ghcr-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white">
</p>

## 🔌 API
<p>
  <img src="https://img.shields.io/badge/toss%20payments-0064FF?style=for-the-badge&logo=tosspayments&logoColor=white">
  <img src="https://img.shields.io/badge/openai-412991?style=for-the-badge&logo=openai&logoColor=white">
</p>

## ⚙️ CI/CD
<p>
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/ghcr-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/github%20actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
</p>

## 🤝 Collaboration
<p>
  <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">
  <img src="https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white">
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/erdcloud-0B4F6C?style=for-the-badge&logo=icloud&logoColor=white">
  <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
</p>
<br/>

---

# 4.  ERD
![](img/erd.png)<br/>
🔗[ ERD ](https://www.erdcloud.com/d/zfbRbiMEYW5M3uegM)
<br/>

---

# 5.  테이블 정의서
![](img/table.PNG)<br/>
🔗[MariaDB 테이블 정의서](https://docs.google.com/spreadsheets/d/16YGKpTcpo310JjvUu9Q1Nm9atCI-XYx5TB2GD3ocx3E/edit?gid=114956902#gid=114956902)

🔗[Supabase 테이블 정의서](https://docs.google.com/spreadsheets/d/16YGKpTcpo310JjvUu9Q1Nm9atCI-XYx5TB2GD3ocx3E/edit?gid=1153156764#gid=1153156764)
<br/>

---

# 6.  시스템 아키텍처
<br/>

![](https://github.com/user-attachments/assets/15f9e2e3-2915-4a29-8dea-22d367af813c)<br/>
<br/>

---
# 7.  API 명세서
![](img/table.PNG)<br/>
🔗[API 명세서](https://www.notion.so/3441072487c38020a8c7f55914b4c234?v=3441072487c3818cbbfa000c9265993d&source=copy_link)
<br/>

---

# 8.  테스트 계획 및 결과 보고서
<br/>

🔗[테스트 계획 및 결과 보고서](https://docs.google.com/spreadsheets/d/16YGKpTcpo310JjvUu9Q1Nm9atCI-XYx5TB2GD3ocx3E/edit?gid=1106294099#gid=1106294099)
<br/>

---

# 9.  팀 notion
<br/>

🔗[팀 notion](https://www.notion.so/2-2fb1072487c380368604fa96626503a4?source=copy_link)
<br/>

---

# 10.  발표자료
<br/>

🔗[발표자료](docs/biddinggo-presentation.pdf)
<br/>

---

# 11.  회고
<br/>

#### 김진혁
> 첫 개발 프로젝트를 팀장을 맡아 진행함에 큰 트러블 없이 계획대로 일찍 개발이 완료된 점이 팀원 모두가 정말 잘 도와준 것 같아서 이 글을 통해서라도 감사의 인사를 드립니다. 이번 프로젝트는 2월부터 시작이되어 4월 20일에 마감이 되었는데, 2월 당시만 하더라도 프론트엔드는 커녕 스프링 조차 처음써보는 단계였습니다. 자바는 익숙해도 스프링을 처음이라 전체 프로젝트 구조를 어떻게 짜야 될 지 고민이 많았습니다. 그럴 때 일수록 매일을 찾아보고 물어보면서 한 스탭씩 나아갔 던 것 같습니다. 프로젝트 내내 매번이 새로운 기술을 쓰고 해보지 않은 코드를 짜는 것이 마치 어둠 속을 천천히 가는게 아닌 전력 질주를 하는 느낌이라 두려움도 컸습니다. 뒤돌아서 보면 그렇게 무작정 코드를 짜고 좌절했던 경험들이 가장 빠르게 프로젝트를 이해하는 길임을 느낍니다. 이번 프로젝트에선 인증인가(oauth2), 관리자 공지사항, sse, 프론트엔드를 담당했습니다. 저희 프로젝트는 jwt 기반 oauth2와 관리자는 자체 로그인으로 구현을 기획했습니다. 가장 어려웠던 부분은 OAuth2 자체를 붙이는 것보다, 로그인 이후 인증 흐름을 JWT 필터와 Spring Security 필터 체인에 안정적으로 녹여내는 과정이었습니다. OAuth2 인증 성공 후 access/refresh 토큰을 어떤 지점에서 발급하고 어떤 방식으로 전달할지 설계하는 것부터, 필터 순서에 따라 인증이 누락되거나 401이 반복되는 문제를 해결하는 데 많은 시행착오가 있었습니다. SSE도 단순 연결이 아니라 운영 관점의 안정성이 핵심이었습니다. SseEmitter 기반으로 사용자별 emitter를 ConcurrentHashMap<memberId, SseEmitter>로 관리하고, onCompletion, onTimeout, onError 콜백에서 즉시 제거하도록 구성해 누수를 방지했습니다. 또한 전송 실패 시 stale emitter를 정리하고, JWT 인증 이후 사용자 컨텍스트 기준으로 이벤트를 발행해 권한 없는 구독을 막았습니다. 이 경험을 통해 기능 구현 자체보다 인증과 실시간 통신의 전체 라이프사이클을 설계하고 안정화하는 역량이 더 중요하다는 것을 배웠습니다.
 
#### 박선우
> 6명의 인원으로 진행된 협업 프로젝트는 처음이라 걱정이 많았습니다. 다행히 팀장님의 빠른 방향 정리와 팀원들과의 많은 회의와 소통을 통해 시스템 구현 전 ERD 설계, 테이블 정의, API 명세까지 안정적으로 진행할 수 있었습니다. 특히, Git 브랜치 전략과 커밋 컨벤션을 함께 정의하고 적용하며 GitHub 이슈와 PR을 통해 협업을 진행해본 경험이 인상 깊었습니다. 실제 개발자들의 협업 프로세스를 미리 경험해볼 수 있었다는 점에서 개인적으로 재미있고 의미 있는 과정이었습니다. 구현 과정에서는 공통 예외 처리와 응답 래퍼 구조를 팀원들에게 공유하며 코드를 재사용 가능한 형태로 만드는 경험을 할 수 있었습니다. 이 과정에서 팀원들이 코드를 쉽게 이해할 수 있도록 어떻게 설명해주면 좋을지 아니면 더 단순하고 직관적인 구조로 개선할 수는 없는지에 대해 많은 고민을 해볼 수 있었습니다. 또한, 외부 API(Toss Payments, Cloudflare R2 등)를 연동하면서 애플리케이션이 외부 시스템과 통신하는 구조를 이해할 수 있었으며 특히, R2의 presigned URL 기반 업로드 흐름을 팀원들의 설명을 통해서 개념을 명확히 정리할 수 있었습니다. 경매 서비스의 특성상 발생하는 동시성 문제도 파악하게 되었습니다. 이는 비관적 락을 사용하여 해결했지만 일부 로직에는 아직 적용하지 못한 아쉬움이 남습니다. 이 점은 향후 개선해야 할 중요한 포인트라 생각합니다. 구현 단계까지만 고려하고 있던 저에게 팀원들이 구성한 CI/CD 기반 배포 과정은 행운이였습니다. 익숙하지 않은 영역이었지만, 아키텍처 구조를 바탕으로 설명해준 덕분에 배포 흐름을 보다 명확하게 이해할 수 있었습니다. 두 달 동안 BiddingGo 프로젝트를 진행하며 쉽지 않은 순간들도 있었지만, 함께 배우고 성장하는 모습을 지켜보는 과정이 개인적으로 즐겁고 의미 있는 경험이었습니다. 말 많고 걱정 많은 저랑 함께 해준 팀원들께 감사드립니다. 

#### 윤정윤
> 개발을 진행하면서 더 크게 막혔던 부분은 기술보다는 도메인 자체였습니다. 팀원 모두가 실제 경매 경험이 없었기 때문에, 경매가 어떤 흐름으로 돌아가는지부터 다시 이해해야 했습니다. 낙찰은 어떤 기준으로 결정되는지, 비크리 경매는 무엇인지 같은 기본적인 개념조차 명확하지 않은 상태에서 개발을 진행하려다 보니, 구현을 하면서도 이 로직이 맞는지 확신할 수 없는 상황이 반복되었습니다. 이때 도메인을 제대로 이해하지 못하면 코드가 아무리 맞아도 서비스는 틀릴 수 있다는 것을 체감했고 이후에는 기능 구현보다 먼저 도메인과 서비스 전체 흐름에 대한 이해에 더 많은 시간을 투자해야 하겠다는 생각을 했습니다.
 협업 과정에서는 소통의 중요성을 계속 느끼게 되었습니다. 경매라는 익숙하지 않은 도메인을 다루다 보니 입찰 조건, 종료 방식, 예외 처리 등에서 팀원 간의 기준이 조금씩 달랐습니다. 또한 각자 이전에 경험한 개발 방식이 달랐기 때문에 코드 스타일이나 설계 방식에서도 차이가 있었습니다. 이런 상태에서는 기능 구현보다 기준을 맞추는 과정이 더 중요했고, 이를 위해 지속적인 소통이 필요했습니다.
 흥미로웠던 점은 소통이 중요하다는 것을 알고 있는 것과 실제로 잘 이루어지는 것은 전혀 다른 문제라는 것입니다. 우리 팀은 비교적 원활하게 소통이 이루어졌는데, 그 이유는 질문을 해도 부담이 없고, 의견을 말해도 충돌이 아니라 논의로 이어지는 분위기가 있었기 때문이라고 생각합니다. 결국 협업의 효율은 개인의 역량보다도 팀의 분위기와 문화에 크게 영향을 받는다는 것을 느꼈습니다.
 이 프로젝트를 통해 개발을 바라보는 관점도 많이 바뀌었습니다. 이전에는 기능을 구현하고 기술을 사용하는 것 자체에 집중했다면, 이제는 유저가 어떻게 사용하는지, 도메인이 어떻게 동작하는지, 그리고 팀이 어떻게 협업하는지를 함께 고려하게 되었습니다. 결국 개발은 단순히 코드를 작성하는 것이 아니라, 문제를 이해하고 구조를 설계하는 과정이라는 것을 이번 경험을 통해 확실하게 느낄 수 있었습니다.

#### 이민경
> 스프링 개발 경험이 부족했던 저에게 이번 프로젝트는 처음부터 쉽지 않은 도전이었습니다. 익숙하지 않은 개념과 구조 때문에 막히는 순간도 많았지만 포기하지 않고 끝까지 해내고 싶다는 마음으로 하나씩 해결해 나갔고 그 과정 자체가 저에게는 무엇보다 의미 있는 시간이었습니다.
경매 서비스 특성상 포인트, 입찰, 회원 등 여러 데이터가 복잡하게 연결되어 있어 로직을 설계하는 과정이 특히 어려웠습니다. 하지만 데이터 무결성을 유지하기 위해 고민하며 쿼리를 개선해 나가면서 백엔드 로직에 대한 이해를 점점 깊게 쌓을 수 있었습니다. 그중에서도 낙찰 스케줄러를 구현하며 마감 직전 발생할 수 있는 동시성 문제를 해결했던 경험은 단순히 기능을 구현하는 것을 넘어 시스템을 안정적으로 설계하는 것의 중요성을 직접 느끼게 해준 계기였습니다.
또 GitHub의 PR과 코드 리뷰를 통한 협업 경험으로 인해 제가 작성한 코드를 팀원들과 공유하고 서로 피드백을 주고받는 과정에서 더 나은 구조를 고민할 수 있었습니다. 특히 혼자였다면 놓쳤을 부분들을 팀원들의 시선으로 다시 바라보게 되면서 기술적인 성장뿐 아니라 타인의 코드를 이해하고 제 생각을 논리적으로 전달하는 능력도 함께 키울 수 있었습니다. 두 달 동안 BiddingGo 프로젝트를 진행하면서 쉽지 않은 순간도 많았지만 그때마다 함께 고민해준 팀원들 덕분에 끝까지 완주할 수 있었습니다. 늘 책임감을 가지고 함께해 준 팀원들에게 진심으로 감사드립니다.

#### 이애은
> 이번 프로젝트에서는 경매 서비스의 핵심 로직인 입찰과 낙찰, 그리고 결제 프로세스를 구현하며 서비스 전반의 흐름을 깊이 이해할 수 있었습니다. 특히 실시간으로 변화하는 경매 특성상 데이터의 정합성을 보장하는 것이 매우 중요했기 때문에, 이를 해결하기 위해 비관적 락과 트랜잭션 처리에 대해 다양한 방식으로 고민하고 적용해보았습니다. 단순한 기능 구현을 넘어, 실제 서비스에서 발생할 수 있는 동시성 문제를 어떻게 안정적으로 제어할 것인지에 대해 한층 더 깊이 있는 학습을 할 수 있었던 경험이었습니다.
또한 초반에는 처음 접해보는 기술 스택을 활용해야 했기 때문에 낯설고 어려운 부분도 있었지만, 팀원분들과 적극적으로 소통하며 문제를 해결해 나가는 과정에서 기술뿐만 아니라 협업의 중요성도 함께 배울 수 있었습니다.
개발 단계에만 머무르지 않고, CI/CD 환경을 구축하고 실제 배포까지 경험해본 점 또한 의미 있었습니다. 코드 작성부터 빌드, 배포, 운영까지 이어지는 전체 개발 라이프사이클을 직접 경험할 수 있었고, 서비스 개발의 흐름을 보다 입체적으로 이해할 수 있었습니다.
모두가 각자의 자리에서 최선을 다해준 덕분에 의미 있는 결과를 만들어낼 수 있었다고 생각합니다. 우리 팀원분들 모두 Biddinggo를 위해 고생 많으셨습니다.

#### 한규진
> 개발을 진행할 때는 인지하지 못했지만, 다시 코드를 돌아보며 예외 발생 시 데이터 일관성까지 충분히 고려하지 못했다는 점을 알게 되었습니다. 특히 회원 탈퇴 기능에서 여러 작업이 함께 수행되는 과정에서, 중간에 예외가 발생하면 일부 데이터만 반영된 상태로 남을 수 있다는 점을 확인했습니다. 이를 통해 하나의 요청 안에서 여러 작업이 이루어질 때는 일관성을 보장하는 설계가 필요하며, 프론트와 백엔드가 동일한 결과를 인식하도록 구성해야 한다는 점을 느꼈습니다. 그렇지 않을 경우 사용자에게 혼란을 줄 수 있고, 잘못된 상태에서 추가 요청이 발생할 수 있기 때문입니다.
또한 요청이 서버를 거쳐 데이터에 반영되고 다시 화면에 전달되는 전체 흐름을 경험하면서 웹 애플리케이션의 동작 구조를 더 잘 이해할 수 있었습니다. 데이터 조회와 응답을 구성하는 과정에서도, 어떤 데이터를 어떻게 가져오고 조합하느냐에 따라 결과가 달라질 수 있다는 점을 알게 되었습니다.
마지막으로 GitHub Issue와 PR을 통해 작업 단위를 나누고 변경 사항을 공유하면서, 작업 의도와 영향 범위를 팀원들과 명확히 전달하는 것이 협업의 효율과 코드 이해도를 높이는 데 중요하다는 점도 경험했습니다.
