# flex project
>오늘도 Flex 해버렸지 뭐야~

# master-redis branch
> 레디스로 구현해보았다.
>
> https://github.com/arothy1/flex/tree/master-redis


## Entity
>Sprinkle(뿌리기)
>
>Pick(줍기)


## 뿌리기
#### 토큰 생성 로직
>Random 클래스 이용
>
>중복 토큰발생 확률이 높다. 토큰 생성 중 무한루프에 빠지지 않게 처리
#### 뿌린돈 분배 로직
>Random 클래스 이용
>
>줍기 대상이 최소 1원이상 가져갈 수 있도록 분배 (돈인줄 알고 주웠는데... 최소 노동비용)
>

## 받기(줍기)
#### 받은돈 저장 로직
>
>특별한 건 없고 유효성 체크사항이 많음.