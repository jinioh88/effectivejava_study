이팩티브 자바 스터디 - 자세한 사항은 책음 참고해주세요.

# 객체 생성과 파괴
## 생성자 대신 정적 팩터리 메서드를 고려하라
- 클라이언트가 클래스의 인스턴스를 얻는 전통적인 수단은 public 생성자다.
- 그 외에 정적 팩터리 메서드도 제공할 수 있다. 
- 정적 팩토리 메서드가 생성자보다 좋은 장점 다섯 가지
  - 이름을 가질 수 있다. 
    - 이름만 잘 지으면 반환될 객체의 특성을 쉽게 묘사할 수 있다. 
    ```
    new BigInteger(int, int, Random); // 뭘 생성할건지 문서를 볼때까지 잘 모른다.
    BigInteger.probablePrime // 이 값이 소수인 BigInteger를 반환하라는 명확한 뜻이 된다.
    ```
    - 한 클래스에 시그니처가 같은 생성자가 여러 개 필요할 것 같으면, 생성자를 정적 팩터리 메서드로 바꾸고 각각의 차이를 잘 드러내는 이름을 지어주자.
  - 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다. 
    - 생성 비용이 큰 객체가 자주 요청되는 상황이라면 성능을 상당히 끌어올려 준다. 
    - 인스턴스를 통제하면 클래스를 싱글턴으로 만들 수도, 인스턴스화 불가로 만들 수도 있다. 또 불변 값 클래스에서 동치인 인스턴스가 단 하나뿐임을 보장할 수 있다.
  - 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
    - 반환할 객체의 클래스를 자유롭게 선택할 수 있게 하는 유연성을 제공한다. 
  - 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다. 
    - 반환 타입의 하위 타입이기만 하면 어떤 클래스의 객체를 반환하든 상관 없다. 
  - 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다. (다시 보기)
- 단점을 보자
  - 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다. 
    - 상속보다는 컴포지션을 유도하고 있다는 장점으로 받아들일 수 있다.
  - 정적 팩터리 메서드는 프로그래머가 찾기 어렵다. 정적 팩터리 명명 방식이 있다
    - from : 매개 변수를 하나 받아 해당 타입의 인스턴스를 반환
      ```
      Date d = Date.from(instance);
      ```
    - of : 여러 매개변수들을 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
      ```
      Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);
      ``` 
    - valueOf : from과 of의 더 자세한 버전
      ```
      BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);
      ```
    - instance 혹은 getInstance : 매개변수로 명시한 인스턴스를 반환하지만, 같은 인스턴스임을 보장하진 않음
      ```
      StackWalker luke = StackWalker.getInstance(options);
      ```
    - create 혹은 newInstance : instance 혹은 getInstance와 같지만, 매번 새로운 인스턴스를 생성해 반환
      ```
      Object newArray = Array.newInstance(classObject, arrayLen);
      ```
    - getType : getInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메섣드를 정의할 때 씀
      ```
      FileStore fs = Files.getFileStore(path);
      ```
    - newType : newInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 씀
      ```
      BufferedReader br = Files.newBufferedReader(path);
      ```
    - type : getTpye과 newType의 간결한 버전
      ```
      List<Complaint> litany = Collections.list(legacy);
      ```
     
