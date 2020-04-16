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
    
        private Elvis() {
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
    
        private Elvis() {
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
    
---
## 인스턴스화를 막으려거든 private 생성자를 사용하라
- 정적 멤버만 담은 유틸리티 클래스는 인스턴스로 만들어 쓰려고 설게한게 아니다. 
- 추상 클래스로 만드는 것으로는 인스턴스화를 막을 수 없다. 
  - 하위 클래스를 만들어 인스턴스화하면 그만이다.
- private 생성자를 추가하면 클래스의 인스턴스화를 막을 수 있다.
  ```
  public class UtiltyClass {
      // 기본 생성자가 만드는걸 막음
      private UtiltyClass() {
          // error를 던질 필요까진 없음
          throw new AssertionError();
      }
  }
  ```
  - 이 방식은 상속을 불가능하게 하는 효과도 있다. 
  - 모든 생성자는 상위 클래스의 생성자를 호출하게 되는데 이를 private로 선언해서 하위 클래스가 상위 클래스의 생성자에 접근할 길이 없어진다. 
  
---
## 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
- 많은 클래스스가 하나 이상의 자원에 의존한다. 
```
// 정적 유틸리티를 잘못 사용한 예
public class SpellChecker {
    private static final Lexicon dictionary = new Lexicon();
    
    private SpellChecker() {}
    
    public static boolean isValid(String word) {
        return true;
    }
}
```

```
// 싱글턴을 잘못 사용한 예
public class SpellChecker {
    private final Lexicon dictionary = new Lexicon();
    
    private SpellChecker(...) {}
    
    public static SpellChecker INSTANCE = new SpellChecker();
    
    public static boolean isValid(String word) {
        return true;
    }
}
```

- 위 두 방식 모두 사전을 단 하나만 사용한다고 가정한다는 점에서 훌륭하지 않다.
  - 실전에선 여러 사전이 있을 텐데 사전 하나로 이 모든 쓰임에 대응할 수 있기를 바라는건 순진한 생각이다. 
- 필드에서 final 한정자를 제거하고 다른 사전으로 교체하는 메서드를 추가할 수 있지만, 아쉽게도 어색하고 오류를 내기쉬우며 멀티스레드 환경에서 쓸 수 업다. 
  - 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.
- 인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식을 사용하면 된다.(의존 객체 주입의 한 형태)
  ```
  public class SpellChecker {
      private final Lexicon dictionary;
      
      private SpellChecker(Lexicon dictionary) {
          this.dictionary = Objects.requireNonNull(dictionary);
      }
      
      public static boolean isValid(String word) {
          return true;
      }
  }
  ```
- 위 예제는 dictionary라는 딱 하나의 자원만 사용하지만, 자원이 몇 개든 의존 관계가 어떻든 상관없이 잘 동작한다.
- 불변도 보장하여 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있기도 하다. 
- 의존객체 주입은 생성자, 정적 팩터리, 빌더 모두에 똑같이 응용할 수 있다.
- 이 패턴의 쓸만한 변형은 팩터리 메서드 패턴을 구현하는 것이다. 
- 클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면 싱글턴과 정저 유틸리티 클래스는 사용하지 않는게 좋다.
  - 이 자원들을 클래스가 직접 만들게 해서도안된다.

---
## 불필요한 객체 생성을 피하라
- 똑같은 기능의 객체를 매번 생성하기보단 객체 하나를 재사용하는 편이 나을 때가 많다. 
- 다음 코드는 하지 말아야할 극단적인 예다
  - String s = new String("bikini");
  - 위 문장은 실행 될 때마다 String 인스턴스를 새로 만든다. 
  - String s = "bikini"; 
  - 위와 같이 하면 새로운 인스턴스를 매번 만드는 대신 하나의 String 인스턴스를 사용한다. 
    - 이 방식은 같은 가상 머신 안에서 이와 똑같은 문자열 리터럴을 사용하는 모든 코드가 같은 객체를 재사용함이 보장된다. 
- 정적 팩터리 매서드를 제공하는 불변 클래스에서는 정적 팩터리 메서드를 사용해 불필요한 객체 생성을 피할 수 잇다. 
- 생성 비용이 아주 비싼 객체는 캐싱하여 재사용하길 권한다. 
  ```
  // 성능 전 코드
  public class RomanNumberals {
      static boolean isRomanUmberal(String s) {
          return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
                  + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
      }
  }
  
  // 재사용하는 코드
  public class RomanNumberals {
      private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})"
              + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
      
      static boolean isRomanUmberal(String s) {
          return ROMAN.matcher(s).matches();
      }
  }
  ```
  - 속도개선 뿐만아니라 Pattern 인스턴스를 static final 필드로 끄집어내고 이름을 지어주어 코드 의미도 명확해졌다.
- 개선된 방식의 클래스가 초기화 된 후 isRomanUmberal 메서드를 한 번도 호출하지 않는다면 ROMAN 필드는 쓸데없이 초기화 된 꼴이다.
  - 위 문제를 지연 초기화로 불필요한 초기화를 없앨 수 있지만 권하진 않는다. 
  - 코드만 복잡해지고 성능은 크게 개선되지 않을 때가 많기 때문이다.
- 객체가 불변이면 재사용해도 안전함이 명백하다. 
- 불필요한 객체를 만들어내는 또 다른 예로 오토박싱을 들 수 있다. 
  ```
  private static long sum() {
          Long sum = 0L;
          for(long i = 0; i <= Integer.MAX_VALUE; i++) {
              sum += i;
          }
          return sum;
  }
  ```
  - 위 코드는 불필요한 Long 인스턴스가 계속 만들어진다. sum 객체를 Long 대신 long으로 바꿔야 한다. 
  - 박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자. 
- 단순히 객체 생성을 피하고자 개인만의 객체 풀을 만들지 말자.
  - 데이터베이스 연결 같이 생성 비용이 워낙 크면 재사용하는 편이 낫다.
  - 일반적으론 자체 객체 풀은 코드를 헛갈리게 만들고 메모리 사용량을 늘리고 성능을 떨어뜨린다. 
  - 최근 JVM의 가비지 컬렉터는 상당히 잘 최적화가 잘되있어 가벼운 객체용을 다룰 땐 직접 만든 객체 풀보다 훨씬 빠르다. 
  
