# 실시간 검색어 순위 서비스

## 문제상황

요새 큰 성장세를 기록하고 있는 HCS 라는 웹사이트가 있습니다. **사용자 수**가 계속해서 늘어나면서 **서비스 확대 및 개선**에 대한 needs가 다양하고 강력해지고 있습니다.
사용자들의 의견을 수용하기 위해 웹사이트를 통해 다양한 아이디어를 접수하고 있습니다.
사용자는 웹사이트에 자신의 아이디어 내용을 등록할 수 있고, 자신과 동일한 아이디어가 있는지도 검색이 가능합니다.
경영진과 개발팀은 사용자의 관심도가 높은 주제에 더 쉽게 접근할 수 있으면 좋겠다고 생각하고 있습니다.

## 해결 방안

1.사용자들이 검색한 키워드를 입력 받아서 데이터를 정제하고, 2.**요청이 있을 때** 전체 검색어 중 제일 많이 검색된 실시간 순위 Top 10을 제공하는 **마이크로 서비스**를 만들려고 합니다.

> (주의사항) **해당 서비스는 검색을 직접 수행하지는 않습니다. 검색한 키워드만 입력 받아 실시간 순위를 제공하는 서비스입니다.**

# 요구사항 명세

## 기능적 요구사항 명세

- 검색어를 받아 저장할 수 있어야한다.
- 저장된 검색어를 바탕으로 실시간 순위를 제공할 수 있어야한다.

## 비기능적 요구사항 명세

- 해당 서비스는 실시간성을 띄어야한다.
- 마이크로서비스이기에 확장성 및 기존 검색 서비스와 독립적이며 연동가능해야한다.

# API Spec

## 검색어 저장 API

- POST : /api/v1/searchRank/search?keyword={keyword}

<img width="277" alt="image" src="https://github.com/SWYP-4rd-6/backend/assets/56664567/780c5198-f559-40d9-b6e2-c30f6fc50c92">
<img width="523" alt="image" src="https://github.com/SWYP-4rd-6/backend/assets/56664567/579766bd-bae6-47a5-af89-e78cab3ef4fa">

## 실시간 순위 제공 API

- GET : /api/v1/searchRank/rank

<img width="435" alt="image" src="https://github.com/SWYP-4rd-6/backend/assets/56664567/60ed114e-a571-4583-b07b-c202b50fc600">

# 시스템 구성도

![image](https://github.com/SWYP-4rd-6/backend/assets/56664567/d84df5d4-61a6-47fe-b385-afc48d04b99b)

# 구현

## 1. Redis

실시간성 성격을 띄고 있기 때문에 캐시를 적용하였습니다. Redis 의 sorted set 구조를 사용하였습니다.

### Sorted Set

Redis에서 제공해주는 자료구조 중 하나인 Sorted Set(또는 ZSET)은 Set의 특성을 그대로 가지면서 추가적으로 저장된 value들의 순서도 관리해줍니다.
이 때 이 순서를 위해 각 value에 대해 score를 필요에 맞게 설정할 수 있으며, 이 score를 기반으로 정렬을 수행합니다.

- key: `search_rank`
- value: `keyword`
- score: `count`
- score 는 count 를 기준으로 정렬합니다.

- search API 를 통해 검색어를 받아오면 해당 검색어를 sorted set 에 추가하거나 Score 를 증가시킵니다.
- search_rank 에서 Score 기준 상위 10개의 검색어를 가져옵니다.

## 2. Elastic Search

- Redis 를 사용한다면 단순 검색어 순위를 제공할 수 있지만 시간에 따른 정보를 제공하기 어렵습니다.
- 이를 보완하기 위해 Elastic Search 를 사용하여 검색어를 저장하고, 시간에 따른 정보를 제공합니다.

- 10분을 기준으로 Redis 에 존재하는 검색어를 Elastic Search 에 저장합니다.

- "검색" 이라는 키워드와 실제 LogStash 를 통해 실제 사용되고 있는 스택이기에 선정하였습니다.
- Redis 문제 발생 시 가장 최근 검색어 순위로 대응

# 진행하면서 고민되었던 점, 아쉬웠던 점

## M2

- Elastic, Redis 등을 Docker 를 통해 관리하려했지만 M2 이슈로 많은 시간 소요

## ELK 와 Redis

- Redis 와 Elastic 을 함께 사용하는 구조는 찾아보기 힘들었습니다.
- ELK 스택을 통해 인기 검색어 처리하는 방법이 존재
- ELK 자체가 대용량 데이터에 초점 및 비용적인 문제
- ELK 에 대한 이해..

## Score 계산 알고리즘

- (10분 간 검색량 / 60분 간 평균 검색량) * 10 분 간 검색량 으로 Score 계산
- 평균 검색량이 증가된 경우 및 검색량 자체의 가중치 고려

- Score 계산 알고리즘에 대한 고민
    - 10분 전 검색어 순위 및 Score 적용

# 해당 서비스를 고도화 한다면?

- ELK 스택으로 검색 서비스에서의 로그를 통해 인기 검색어 제공 및 실제 검색 제공
- Score 알고리즘 강화 

