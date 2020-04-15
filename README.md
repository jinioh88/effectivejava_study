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
     
---
## 생성자에 매개변수가 많다면 빌더를 고려하라
- 매개변수가 많은 클래스의 인스턴스를 만들때, 이런 생성자는 사용자가 설정하길 원치 않는 매개변수까지 포함해야 한다. 
- 점층적 생성자 패턴도 쓸 수는 있지만, 매개변수 갯수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다. 
- 자바 빈즈 패턴인 세터 메서드는 코드가 길어지긴 했지만 인스턴스를 만들기 쉽고 읽기 쉬운 코드가 된다. 
  - 단점이라면 객체 하나를 만들려면 메서드를 여러 개 호출해야 하고, 객체가 완전히 생성되기 전까지 이로간성이 무너진다. 
  - 이로 인해 클래스를 불변으로 만들 수 없으며 스레드 안전성을 얻으려면 프로그래머가 추가 작업을 해줘야 한다. 
- 빌더 패턴은 점층적 생성자 패턴의 안전성과 자바빈즈 패턴의 가독성을 겸비했다. 
  - 빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어두는 게 보통이다. 
  - 빌더의 세터 메서드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출할 수 있다. 
    - 이런 방식을 플루언트 API 혹은 메서드 연쇄라 한다. 
- 빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다. 
  - 각 계층의 클래스에 관련 빌더를 멤버로 정의하자
  - 추상 클래스는 추상 빌더
  - 구체 클래스는 구체 빌더를 갖게한다. 
- 하위 클새스의 메서드가 상위 클래스의 메서드가 정의한 반환 타입이 아닌, 그 하위 타입을 반환하는 기능을 공변반환 타이핑이라 한다. 
  - 이를 이용하면 클라이언트가 형변환에 신경 쓰지 않고도 빌더를 사용할 수 있다. 
- 빌더 생성 비용이 크지는 않지만 성능에 민감한 상황에선 문제가 될 수 있다. 
- 매개 변수가 4개 이상은 되어야 값어치를 한다. 
  - API는 시간이 지날수록 매개변수가 많아지는 경향이 있음을 명심

---
## private 생성자나 열거 타입으로 싱글턴임을 보증하라
- 싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다. 
- 클래스를 싱글턴으로 만들면 이를 사용하는 클라이언트를 테스트하기 어려워질 수 있다. 
- 싱글턴을 만드는 방식은 보통 둘 중 하나다.
  - 두 방식 모두 생성자는 private로 감춰두고, 유일한 인스턴스에 접근할 수 있는 수단으로 public static 멤버를 하나 마련해둔다.
  - public static final 필드 방식의 싱글턴
    ```
    public class Elvis {
        public static final Elvis INSTANCE = new Elvis();
    
        public Elvis() {
        }
        
        public void leaveTheBuilding() {
            System.out.println("leave");
        }
    }
    ``` 
    - private 생성자는 public static final 필드인 Elvis.INSTANCE를 초기화할 때 딱 한번만 호출한다. 
      - 리플렉션으로 우회하는 방법이 있는데, 이러한 공격을 방어하려면 생성자를 수정하여 두 번째 객체가 생성되려 할 때 예외를 던지면 된다. 
    - 해당 클래스가 싱글턴임이 API에 명백히 드러난다. 
    - 간결하다.
  - 두번째 방법은 정적 팩터리 메서드를 public static 멤버로 제공하면 된다.
    ```
    public class Elvis {
        private static final Elvis INSTANCE = new Elvis();
    
        public Elvis() {
        }
        
        public static Elvis getInstance() {
            return INSTANCE;
        }
    
        public void leaveTheBuilding() {
            System.out.println("leave");
        }
    }
    ```
    - API를 바꾸지 않고도 싱글턴이 아니게 변경할 수 잇다. 
    - 원하면 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다. 
    - 정적 팩터리의 메서드 참조를 공급자로 사용할 수 있다. 
    - 위 의 장점들으 필요하지 않는다면 public 필드 방식이 더 좋다.
  - 싱글턴 클래스를 직렬화하려면 단순히 Serializable을 구현함을 넘어 모든 인스턴스 필드를 일시적이라고 선언하고 readResolve 메서드를 제공해야 한다. 
    - 이렇게 안하면 역직렬화할 때마다 새로운 인스턴스가 만들어진다. 
    ```
    private Object readResolve() {
            return INSTANCE;
    }
    ```
- 조금 부자연스러워 보일 순 있으나 대부분 상황에서 원소가 하나뿐인 열거타입이 싱글턴을 만드는 가장 좋은 방법이다. 
  - 단 말들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 사용할 수 없다. 
  ```
  public enum Elvis {
      INSTANCE;
  
      public void leaveTheBuilding() {
          System.out.println("leave..");
      }
  }
  ```
    