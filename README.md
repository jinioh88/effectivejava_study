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
  
--- 
## 다 쓴 객체 참조를 해제하라
- 자바는 가비지 컬렉터가 알아서 다 쓴 객체를 회수해간다. 
- 이래서 메모리 관리에 신경 쓰지 않아도 된다고 오해할 수 있는데, 절대 사실이 아니다. 
  ```
  public class Stack {
      private Object[] elements;
      private int size = 0;
      private static final int DEFAULT_INITAL_CAPACITY = 16;
  
  
      public Stack() {
          elements = new Object[DEFAULT_INITAL_CAPACITY];
      }
  
      public void push(Object e) {
          ensureCapacity();
          elements[size++] = e;
      }
  
      public Object pop() {
          if(size == 0) {
              throw new EmptyStackException();
          }
          return elements[--size];
      }
  
      private void ensureCapacity() {
          if(elements.length == size) {
              elements = Arrays.copyOf(elements, 2 * size +1);
          }
      }
  }
  ```
  - 위 코드의 문제는 메모리 누수로 이 스택을 사용하는 프로그램을 오래 실행하다 보면 점차 가비지 컬렉션 활동과 메모리 사용량이 늘어나 성능이 저하된다. 
  - 위 코드의 문제는 스택이 커졌다가 줄어들 때 스택에서 꺼내진 객체들을 가비지 컬렉터가 회수하지 않는다. 
    - 이 스택이 그 객체들의 다 쓴 참조를 여전히 가지고 있기 때문이다. 
    - elements 배열의 활성 영역 밖의 참조들이 모두 다쓴 첨조에 속한다. 
  - 객체 참조 하나를 살려두면 가비지 컬렉터는 그 객체뿐 아니라 그 객체가 참조하는 모든 객체를 회수해가지 못한다. 
  - 해법은 다 쓴 참조를 null 처리하면 된다.
      ```
      public Object pop() {
              if(size == 0) {
                  throw new EmptyStackException();
              }
              Object result = elements[--size];
              elements[size] = null;
              
              return result;
      }
      ``` 
  - 그렇다고 모든 객체를 쓰자마자 null 처리하는건 바람직하지 않다.
  - 다 쓴 참조를 해제하는 가장 좋은 방법은 그 참조를 담은 변수를 유효 범위 밖으로 밀어내는 것이다. 
- 캐시 역시 메모리 누수를 일으키는 주범이다. 
  - 객체 참조를 캐시에 넣고 다 쓴 뒤에도 그냥 놔둘 경우이다.
- 메모리 누수의 세 번째 주범은 바로 리스너 혹은 콜백이라 부르는 것이다. 
  - 클라이언트가 콜백을 등록만 하고 명확히 해지하지 않는다면 뭔가 조치해주지 않는 한 콜백은 계속 쌓일 것이다. 
  - 이럴 때 콜백을 약한 참조로 저장하면 가비지 컬렉터가 즉시 수거해간다. 
  
--- 
## finalizer와 cleaner 사용을 피하라
- 자바는 두 가지 객체 소멸자를 제공한다.
- finalizer는 예측할 수 없고, 상황에 따라 위험할 수 있어 일반적으로 불필요하다. 
- 자바 9에서는 finalizer의 대안으로 cleaner를 소개했다. 
  - finalizer보단 덜 위험하지만, 여전히 예측할 수 없고, 느리고, 일반적으로 불필요하다. 
- finalizer와 cleaner는 즉시 수행된다는 보장이 없다. 
- 이 둘의 대안책은 AutoCloseable을 구현해주고, 클라이언트에서 인스턴스를 다 쓰고 나면 close 메서드를 호출하면 된다. 
  - 각 인스턴스는 자신이 닫혔는지 추적하는 것이 좋다. 
  - 즉, close 메서드에서 이 객체는 더 이상 유효하지 않음을 필드에 기록하고, 다른 메서드는 이 필드를 검사해 객체가 닫힌 후에 불렸다면 IllegalStateExceptoin을 던지는 것이다.
  - 자바 라이브러리의 일부 클래스는 안정망 역할의 filnalize를 제공하는데, FileInputStream, FileOutputStream, ThreadPoolExecutor가 대표적이다.

---
## try-finally보다는 try-with-resource를 사용하라
- 자바 라이브러리에는 close 메서드를 호출해 직접 닫아줘야 하는 자원이 많다. 
- 자원 닫기는 클라이언트가 놓치기 쉬워 예측할 수 없는 성능 문제로 이어지기도 한다. 
- 전통적인 방법으로 try-finally가 쓰였다. 
  ```
  static String firstLineofFile(String path) throws IOException {
          BufferedReader br = new BufferedReader(new FileReader(path));
          try {
              return br.readLine();
          } finally {
              br.close();
          }
  }
  ```
- 자바 7에선 try-with-resouces가 나왔다.
  - 이 구조를 사용하려면 해당 자원이 AutoCloseable 인터페이스를 구현해야 한다. 
  ```
  static void copy(String src, String dst) throws IOException {
          try(InputStream in = new FileInputStream(src);
              OutputStream out = new FileOutputStream(dst)) {
              byte[] buf = new byte[1024];
              int n;
              while((n = in.read(buf)) >= 0) {
                  out.write(buf, 0, n);
              }
          }
   }
  ```
  - try-with-resouces 버전이 짧고 읽기 수월하고 문제를 진단하기도 훨씬 좋다. 
  - firstLineofFile이 함수에선 readLine과 close 호출 양쪽에서 예외가 발생하면, close에서 발생한 예외는 숨겨지고 readLine에서 발생한 예외가 기록된다. 
  - try-with-resources에도 catch 절을 쓸 수 있다. 

---
# 모든 객체의 공통 메서드
Object의 구현해야 하는 메서드를 알아보는 챕터다.

## equals는 일반 규약을 지켜 재정의하라
- equals는 다음에 열거한 상황 중 하나에 해당한다면 재정의하지 않는게 최선이다.
  - 각 인스턴스가 본질적으로 고유하다
    -값을 표현하는 게 아니라 동작하는 개체를 표현하는 클래스가 여기 해당한다.
    - Thread가 좋은 예이다.
  - 인스턴스의 '논리적 동치성'을 검사할 일이 없다.
    - 검사할 일이 없다면 Object의 기본 equals 만으로 충분하다.
  - 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다.
  - 클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없다.
    - 혹시 몰라 호출 되는 걸 막고 싶다면 다음 방법을 쓰면 된다. 
    ```
    @Override
    public boolean equals(Object o) {
      throw new AssertionError(); // 호출금지
    }
    ```
- equals 재정의해야 할 때는 언제인가?
  - 객체 식별성(두 객체가 물리적으로 같은지)이 아니라 논리적 동치성을 확인해야 하는데, 상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의되지 않았을 때다.
  - 주로 값 클래스들이 여기 해당한다. 즉 객체가 같은지가 아니라 값이 같은지를 알고 싶어하는 것이다.
  - 값 클래스라 해도, 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 인스턴스 통제 클래스라면 equals를 재정의 안해도 된다.
- equals 규약을 어기면 그 객체를 사용하는 다른 객체들이 어떻게 반응할지 알 수 없다. 규약은 다음과 같다
  - 반사성
  - 대칭성
  - 추이성
  - 일관성
  - null이 아님
- 구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 존재하지 않는다. 
  - 괜찮은 우회 방법으로 상속 대신 컴포지션을 사용하는 조언을 따르면 된다. 
  - 단 추상 클래스의 하위 클래스에서라면 equals 규약을 지키면서 값을 추가할 수 있다. 
    ```
    public class ColorPoint {
        private final Point point;
        private final Color color;
        
        public ColorPoint(int x, int y, Color color) {
            point = new Point(x, y);
            this.color = Objects.requireNonNull(color);
        }
    
    
        @Override
        public boolean equals(Object o) {
            if(!(o instanceof ColorPoint))
                return false;
    
            ColorPoint cp = (ColorPoint) o;
            
            return cp.point.equals(point) && cp.color.equals(color);
        }
    }
    ```
- 클래스가 불변이든 가변이든 equals의 판단에 신뢰할 수 없는 자원이 끼어들게 해선 안된다. 
  - 위 조건을 어기면 일관성이 깨진다.
- 양질의 equals 메서드를 구현 방법 단계는 다음과 같다.
  - == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다. 
    - 단순한 성능 최적화용으로, 비교 작업이 복잡한 상황일 때 값어치를 한다. 
  - instanceof 연산자로 입력이 올바른지 확인한다. 
  - 입력을 올바른 타입으로 형변환한다.
  - 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사한다. 
- float와 double을 제외한 기본 타입 필드는 == 연산자로 비교하고, 참조 타입은 equals 메서드로, float와 double은 각각 정적 메서드 Float.compare(), Double.compare()로 비교한다. 
  - Float.compare(), Double.compare()는 오토박싱을 수반할 수 있으니 성능상 좋지 않다. 
- 배열 필드는 원소 각각을 앞선 지침대로 비교하고, 배열의 모든 원소가 핵심 필드라면 Arrays.equals 메서드들 중 하나를 사용하자.
- 어떤 필드를 먼저 비교하느냐가 equals의 성능을 좌우한다. 
- equals를 다 구현했다면 세 가지만 자문해보자. 
  - 대칭적인가? 추이성이 있는가? 일관적인가?
- 요즘 IDE에서 자동으로 작성해주니 직접하기보다 IDE에 맡기는 편이 낫다.

---
## equals를 재정의하려거든 hashCode도 재정의하라
- equals를 재정의한 클래스 모두에서 hashCode도 재정의해야 한다. 
- 그렇지 않으면 hashCode 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 HashMap이나 HashSet 같은 컬렉션의 원소로 사용할 때 문제를 일으킨다.
- 논리적으로 같은 객체는 같은 해시코드를 반환해야 한다. 
- 논리적으로 같은 두 객체일 지라도 Object의 기본 hashCode 메서드를 사용하면 이 둘이 전혀 다르다고 판단한다. 
- 좋은 해시 함수라면 서로 다른 인스턴스에 다른 해시코드를 반환한다. 
- 성능을 높인다고 해시코드를 계산할 때 핵심 필드를 생략해선 안된다.

---
## toString을 항상 재정의하라
- Object의 기본 toString은 '클래스이름@16진수해시코드'를 반호나한다. 
- toString의 일반 규약은 간결하면서 사람이 읽기 쉬운 형태의 유익한 정보를 반환해야한다. 
- toString 메서드는 객체를 println, printf, 문자열 연결연산(+), assert 구문에 넘길 때, 혹은 디버거가 객체를 출력할 때 자동으로 불린다.
- 실전에서 toString은 그 객체가 가진 주요 정보 모두를 반환하는 게 좋다. 
- 정적 유틸리티 클래스는 toString을 제공할 이유가 없다. 
- IDE에서 자동완성 toString은 유용하다. 

---
## clone 재정의는 주의해서 진행하라 
- Cloneable 인터페이스는 Object의 protected 메서드인 clone의 동작 방식을 결정한다. 
  - Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환하며, 그렇지 않은 클래스의 인스턴스에서 호출하면 CloneNotSupportedException을 던진다.
- clone 메서드의 규약은 다음과 같다
  - x.clone() !- x
  - x.clone().equals(x) // 필수는 아니다. 
- 쓸데없는 복사를 지양한다는 관점에서 불변 클래스는 굳이 clone 메서드를 제공하지 않는게 좋다. 
- (PhoneNumbwer) super.clone() 같이 재정의한 메서드의 반환 타입은 상위 클래스의 메서드가 반환하는 타입의 하위 타입일 수 있다. 
  - 이 방식으로 클라이언트가 형변환하지 안아도 되게끔 해주자.  
  - super.clone()으로 호출하고 형변환을 해주는 방식이 좋다.
- clone 메서드는 사실상 생성자와 같은 효과를 낸다.
  - clone은 원본 객체에 아무런 해를 끼치지 않는 동시에 복제된 객체의 불변식을 보장해야 한다.
- 배열의 clone은 런타임 타입과 컴파일타임 타입 모두가 원본 배열과 똑같은 배열을 반환한다.
  - 배열을 복제할 때는 배열의 clone 메서드 사용을 권장한다. 
- 복잡한 가변 객체를 복제하는 마지막 방법은 먼저 super.clone을 호출하여 얻은 객체의 모든 필드를 초기 상태로 설정한 다음, 원본 객체의 상태를 다시 생성하는 고수준 메서드들을 호출한다. 
- 생성자에서는 재정의 될 수 있는 메서드를 호출하지 않아야 하는데, CLONE 메서드도 마찬가지다.
  - 만약 clone이 하위 클래스에서 재정의한 메서드를 호출하면, 하위 클래스는 복제 과정에서 자신의 상태를 교정할 기회를 잃게 되어 원본과 복제본의 상태가 달라질 가능성이 크다.
  - public인 clone 메서드에서는 throws 절을 없애야 메서드를 사용하기 편해진다.
- 상속용 클래스는 Cloneable을 구현해선 안된다. 
- Cloneable을 구현한 스레드 안전 클래스를 작성할 때 clone 메서드 역시 적절히 동기화 해줘야 한다. 
  - Object의 clone은 동기화를 신경쓰지 않는다. 
- 요약하면, Cloneable을 구현한 모든 클래스는 clone을 재정의해야 한다. 
  - 접근 제한자는 public으로, 반환 타입은 클래스 자신으로 변경한다. 
  - 가장먼저 super.clone을 호출한 후 필요한 필드 전부를 적절히 수정한다. 
  - 기본 타입 필드와 불변 객체 참조만 갖는 클래스라면 아무 필드도 수정할 필요가 없다. 단 일련 번호나 고유 ID는 비록 기본 타입이나 불변일지라도 수정해줘야한다. 
- Cloneable을 이미 구현한 클래스를 확장한다면 어쩔수 없이 clone을 잘 작동하도록 구현해야 한다. 
  - 그렇지 않은 상황에서는 복사 생성자와 복사 팩터리라는 더 나은 객체 복사 방식을 제공할 수 있다. 
  - 이는 Cloneable/clone 방식보다 나은 면이 많다.  
- 복사 생성자와 복사 팩터리는 해당 클래스가 구현한 '인터페이스'타입의 인스턴스를 인수로 받을 수 있다. 
  - 인터페이스 기반 복사 생성자와 복사 팩터리의 더 정확한 이름은 '변환 생성자', '변환 팩터리'다
  - 이를 이용하면 클라이언트는 원본의 구현 타입에 얽매이지 않고 복제본의 타입을 직접 선택할 수 있다. 
  - HashSet의 객체 s를 TreeSet 타입으로 복제하려면 new TreeSet<>(s)만 하면 끝난다. 
- 복제 기능은 생성자와 팩터리를 이용하는게 최고다. 단 배열만은 clone 메서드를 사용하자. 
  
---
## Comparable을 구현할지 고려하라
- compareTo는 단순 동치성 비교에 더해 순서까지 비교할 수 있으며, 제네릭하다. 
- Comparable을 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서가 있음을 뜻한다. 
  - Comparable을 구현한 객체들은 배열처럼 손쉽게 정렬할 수 있다.
  - 검색, 극단값 계산, 자동 정렬되는 컬렉션 관리도 쉽게 할 수 있다. 
- 알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자. 
- compareTo 메서드의 일반 규약은 equals의 규약과 비슷하다. 
  - 이 객체와 주어진 객체의 순서를 비교한다. 
  - 주어진 객체보다 작으면 음의 정수, 같으면 0, 크면 양의 정수를 반환한다. 
  - 비교할 수 없으면 ClassCastException을 던진다. 
  - 다음은 권고는 아니지만 꼭 지키는게 좋다. (X.compareTo(y)) == 0) == (x.equals(y))
  - 모든 객체에 대해 전역 동치관계를 부여하는 equals와는 달리, compareTo 타입이 다른 객체를 신경 쓰지 않아도 된다. 
    - 타입이 다르면 ClassCastException을 던지면 된다. 
  - compareTo 규약을 지키지 못하면 비교를 활용하는 클래스와 어울리지 못한다. 
- 즉 반사성, 대칭성, 추이성을 충족해야 한다.
  - 기존 클래스를 확장한 구체 클래스에서 새로운 값 컴포넌트를 추가했다면 compareTo 규약을 지킬 방법이 없다. 
  - Comparable을 구현한 클래스를 확장해 값 컴포넌트를 추가하고 싶다면, 확장하는 대신 독립된 클래스를 만들고, 이 클래스에 원래 클래스의 인스턴스를 가리키는 필드를 두자. 
  - 그런 다음 내부 인스턴스를 반환하는 '뷰' 메서드를 제공하면 된다.  
- compareTo 메서드 작성 요령은 equals와 비슷하다.
  - Comparable은 타입을 인수로 받는 제네릭 인터페이스므로 compareTo 메서드의 인수 타입은 컴파일타임에 정해진다. 
- compareTo 메서드는 각 필드가 동치인지를 비교하는 게 아니라 그 순서를 비교한다. 
  - Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 한다면 Comparator를 대신 사용한다. 
- compareTo 메서드에서 필드의 값을 비교할 때 <와 > 션산자는 쓰지말자. 구닥다리 방식이다. 
- 클래스 핵심 필드가 여러개라면 가장 핵심 필드부터 배교해나가자. 
- 자바 8에서는 Comparator 인터페이스가 일련의 비교자 생성 메서드와 팀을 꾸려 메서드 연쇄 방식으로 비교자를 생성할 수 있게 되었다. 
  ```
  private static final Comparator<PhoneNumber> COMPARATOR = Comparator.comparingInt((PhoneNumber pn) -> pn.areaCode).thenComparingInt(pn -> pn.prefix);
  ```
- 객체 참조용 비교자 생성메서드 comparing도 준비돼 있다.
- 이따금 값의 차로 비교하는 부분이 있는데 다음처럼 하면 안된다.
  ``` 
  static Comparator<Object> hashCodeOrder = new Comparator<Object>() {
          @Override
          public int compare(Object o1, Object o2) {
              return o1.hashCode() - o2.hashCode();
          }
  };
  ```
  - 이 방식은 정수 오버플로를 일으키거나 부동소수점 계산 방시게 따른 오류를 낼 수 있다. 
  - 다음 두 방식 중 하나를 택하자
  ```
  static Comparator<Object> hashCodeOrder = new Comparator<Object>() {
          @Override
          public int compare(Object o1, Object o2) {
              return Integer.compare(o1.hashCode(), o2.hashCode());
          }
  };
  
  // 또는
  static Comparator<Object> hashCodeOrder = Comparator.comparingInt(Object::hashCode);
  ``` 
  
---
# 클래스와 인터페이스
## 클래스와 멤버의 접근 권한을 최소화로하라
- 잘 설계된 컴포넌트는 모든 내부 구현을 완벽히 숨겨, 구현과 API를 깔끔히 분리한다. 
- 정보은닉의 장점은 다음과 같다.
  - 시스템 개발 속도를 높인다. 여러 컴포넌트를 병렬로 개발할 수 있기 때문이다.
  - 시스템 관리 비용을 낮춘다. 
  - 정보은닉 자체가 성능을 높여주진 않지만, 성능 최적화에 도움을 준다. 
  - 소프트웨어 재사용성을 높인다. 
  - 큰 시스템을 제작하는 난의도를 낮춘다. 
- 자바는 정보 은닉을 위한 다양한 장치를 제공한다. 
  - 그 중 접근 제어 메커니즘은 클래스, 인터페이스, 멤버의 접근성을 명시한다. 
  - 각 요소의 접근성은 그 요소가 선언된 위치와 접근 제한자로 정해진다. 
- 기본 원칙은 모든 클래스와 멤버 접근성을 가능한 한 좁혀야 한다. 
- 톱레벨 클래스와 인터페이스에 부여할 수 있는 접근 수준은 package-private와 public 두 가지다. 
- 한 클래스에서만 사용하는 package-private 톱레벨 클래스나 인터페이스는 이를 사용하는 클래스 안에 private static으로 중첩시켜보자.
  - private static으로 중첩시키면 바깥 클래스 하나에서만 접근할 수 있다. 
- public일 필요가 없는 클래스의 접근 수준을 package-private 톱레벨 클래스로 좁히는 일이 중요하다. 
  - public 클래스는 그 패키지의 API인 반면, package-private 톱레벨 클래스는 내부 구현에 속하기 때문이다. 
- 멤버에 접근할 수 있는 수준은 네 가지다.
  - private: 멤버를 선언한 톱레벨 클래스에서만 접근할 수 있다. 
  - package-private: 멤버가 소속된 패키지 안의 모든 클래스에서 접근할 수 있다. 접근 제한자를 명시하지 않았을 때의 접근 수준.
  - protected: package-private의 접근 범위를 포함하며, 이 멤버를 선언한 클래스의 하위 클래스에서도 접근할 수 있다. 
  - public: 모든 곳에서 접근가능하다.
- 클래스의 공개 API를 세심히 서계한 후, 그 외의 모든 멤버는 private로 만들자. 
  - 그런 다음 오직 같은 패키지의 다른 클래스가 접근해야 하는 멤버에 한해 private 제한자를 package-private로 풀어주자. 
  - 풀어주는 일이 자주 하게 된다면 시스템에서 컴포넌트를 더 분해해야 하는 것은 아닌지 고민해보자. 
- protected 멤버는 공개 API이므로 영원히 주원되야 한다. 따라서 protected 멤버의 수는 적을 수록 좋다. 
- public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다.
  - 필드가 가변 객체를 참조하거나, final이 아닌 인스턴스 필드를 public으로 선언하면 그 필드에 담을 수 있는 값을 제한할 힘을 잃게 된다. 
  - public 가변 필드를 갖는 클래스는 일반적으로 스레드에 안전하지 않다. 
- 정적 필드도 마찬가지이나 예외로 클래스가 표현하는 추상 개념을 완성하는 데 꼭 필요한 구성요소로써의 상수라면 public static final 필드로 공개해도 좋다. 
  - public static final이 참조하는 객체는 불변이어야 한다. 
- 길이가 0이 아닌 배열은 모두 변경가능 하니 주의하자
  - 따라서 클래스에서 public static final 배열 필드를 두거나 이 필드를 반환하는 접근자 메서드를 제공해선 안된다. 
  - 접근자를 두게 되면 클라이언트에서 그 배열의 내용을 수정할 수 있게 된다. 
  - 해결책은 2가지다
  - 첫째, public 배열을 private로 만들고 public 불변 리스트를 추가하는 것이다.
    ```
    private static final Thing[] PRIVATE_VALUES = {...};
    public static final List<Thing> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));
    ```
  - 두번째, 배열을 private로 만들고 그 복사본을 반환하는 public 메서드를 추가하는 방법이다(방어적 복사).
    ```
    private static final Thing[] PRIVATE_VALUES = {...};
    public static final Thin[] values() {
        return PRIVATE_VALUES.clone();
    }
    ```
- 자바 9에서  모듈 시스템이라는 개념이 도입되면서 두 가지 암묵적 접근 수준이 추가됐다.
  - 모듈은 패키지들의 묶음이다. 
  - 모듈은 자신에 속하는 패키지 중 공개할 것들을 선언한다. 
  - protected 혹은 public 멤버라도 해당 패키지를 공개하지 않았담녀 모듈 외부에선 접근할 수 없다. 
  - 모듈 시스템을 활용하면 클래스를 외부에 공개하지 않으면서 같은 모듈을 이루는 패키지 사이에서 자유롭게 공유할 수 있다. 
- 꼭 필요한 경우가 아니라면 당분간은 모듈을 사용하지 말자.  

---
## public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라
- 객체지향에선 필드를 모두 private로 바꾸고 public 접근자(getter)를 추가한다.
  ```
  public class Point {
      private double x;
      private double y;
  
  
      public Point(double x, double y) {
          this.x = x;
          this.y = y;
      }
      
      public double getX() {
          return x;
      }
      
      public void setX(double x) {
          this.x = x;
      }
      
      public double getY() {
          return y;
      }
      
      public void setY(double y) {
          this.y = y;
      }
  } 
  ```
- 패키지 바깥에서 접근할 수 있는 클래스라면 접근자를 제공함으로써 클래스 내부 표현 방식을 언제든 바꿀 수 있는 유연성을 얻을 수 있다.
  - public 클래스가 필드를 공개하면 이를 사용하는 클라이언트가 생겨날 것이므로 내부 표현 방식을 마음대로 바꿀 수 없게 된다. 
- 하지만 package-private 클래스 혹은 private 중첩 클래스라면 데이터 필드를 노출한다 해도 하등의 문제가 없다. 
  - 이 방식은 접근자 방식보다 훨씬 깔끔하다.  
  
---
## 변경 가능성을 최소화하라
- 불변 클래스는 가변 클래스보다 설계하고 구현하고 사용하기 쉬어며, 오류가 생길 여지도 적고 훨씬 안전하다. 
- 클래스를 불변으로 만들려면 다음 다섯 가지 규칙을 따르면 된다. 
  - 객체의 생태를 변경하는 메서드를 제공하지 않는다. 
  - 클래스를 확장할 수 없도록 한다. 
  - 모든 필드를 final로 선언한다. 
    - 설계자의 의도를 명확히 드러내는 방법이다. 
    - 새로 생성된 인스턴스를 동기화 없이 다른 스레드로 건네도 문제없이 동작하게끔 보장하는 데도 필요하다. 
  - 모든 필드를 private로 선언한다. 
    - public final로만 선언해도 불변 객체가 되지만, 이렇게 하면 다음 릴리스에서 내부 표현을 바꾸지 못하므로 권하지 않는다. 
  - 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다. 
    - 클래스에 가변 객체를 참조하는 필드가 하나라도 있다면 클라이언트에서 그 객체의 참조를 얻을 수 없도록 해야 한다. 
```
public class Complex {
    private final double re;
    private final double im;


    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double realPart() {
        return re;
    }

    public double imaginaryPart() {
        return im;
    }

    public Complex plus(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }

    public Complex minus(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }

    public Complex times(Complex c) {
        return new Complex(re * c.re - im * c.im, re * c.im + im * c.re);
    }
    
    public Complex dividedBy(Complex c) {
        double tmp = c.re * c.re + c.im * c.im;
        return new Complex((re * c.re + im * c.im) / tmp, (im * c.re - re * c.im) / tmp);
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof Complex))
            return false;

        Complex complex = (Complex) o;

        if(Double.compare(complex.re, re) != 0)
            return false;
        return Double.compare(complex.im, im) == 0;
    }
    
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(re);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(im);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Complex{" + "re=" + re + ", im=" + im + '}';
    }
}  
```
- 위 사칙연산 메서드에서는 인스턴스 자신은 수정하지 않고 새로운 Complex 인스턴스를 만들어 반환하고 있다. 
  - 이처럼 피연산자에 함수를 적용해 그 결과를 반환하지만, 피연산자 자체는 그대로인 프로그래밍 패턴을 함수형 프로그래밍이라 한다. 
- 함수 이름도 add 같은 동사가 아닌 plus 같은 전치사를 사용한 명명 규칙을 따르면 이 메서드가 객체의 값을 변경하지 않는다는 사실을 강조하는 의도다. 
- 불변 객체는 생성된 시점의 상태를 파괴될 때까지 그대로 간직한다. 
- 불변객체는 근본적으로 스레드 안전하여 따로 동기화 할 필요 없다. 
  - 불변 클래스라면 한번 만든 인스턴스를 최대한 재사용하기를 권한다. 
  - 가장 쉬운 재활용 방법은 자주 쓰이는 값들을 상수(public static fianl)로 제공하는 것이다. 
- 불변 클래스는 같은 인스턴스를 중복 생성하지 않게 해주는 정적 팩터리를 제공할 수 있다. 
  - 새로운 클래스를 설계할 때도 public 생성자 대신 정적 팩터리를 만들어두면, 클라이언트를 수정하지 않고도 필요에 따라 캐시 기능을 나중에 덧붙일 수 있다. 
- 불변 객체를 공유할 수 있다는건 방어적 복사도 필요 없다는 결론이 나온다. 
  - 따라서 불변 객체는 clone 메서드나 복사 생성자를 제공하지 않는게 좋다. 
  - String의 복사생성자는 자바 초창기때 잘못 만들어진 것이므로, 되도록 사용을 하지 말아야 한다. 
- 불변 객체는 자유롭게 공유할 수 있음은 물론, 불변 객체끼리는 내부 데이터를 공유할 수 있다. 
- 객체를 만들 때 다른 불변 객체들을 구성요소로 사용하면 이점이 많다. 
- 불변 객체는 그 자체로 실패 원자성을 제공한다. 
  - 메서드에서 예외가 발생한 후에도 그 객체는 여전히 유효한 상태여야 한다는 성질이다. 
- 불변 클래스도 단점이 있다.
  - 값이 다르면 반드시 독립된 객체로 만들어야 한다는 것이다. 
- 불변 클래스임을 보장한는 가장쉬운 방법은 final 클래스로 선언하는 것이다. 
  - 더 유연하게 하려면 모든 생성자를 private 혹은 package-private로 만들고 public 정적 팩터리를 제공하는 방법이다. 
- 직렬화할 때 추가로 주의할 점이 있다. 
  - Serialziable을 구현하는 불변 클르새의 내부에 가변 객체를 참조하는 필드가 있다면 readObject나 readResolve 메서드를 반드시 제공하거나, ObjectOutputStream.wirteUnshared와 ObjectInputStream.readUnshared 메서드를 사용해야 한다. 
- 게터가 있다로 해서 무조건 세터를 만들지는 말자. 
- 클래스는 꼭 필요한 경우가 아니라면 불변이어야 한다. 
- 단순한 값 객체는 항상 불변으로 만들자. 
- 불변으로 만들 수 없는 클래스라도 변경할 수 있는 부분을 최소한으로 줄이자. 
  - 꼭 변경해야할 필드를 뺀 나머지 모두를 final로 선언하자. 
  - 다른 합당한 이유가 없다면 모든 필드는 private final 이어야 한다. 
- 생성자는 불변식 설정이 모두 완료된, 초기화가 완벽히 끝난 상태의 객체를 생성해야 한다. 
  - 확실한 이유가 없다면 생성자와 정적 팩토리 외에는 그 어떤 초기화 메서드도 public으로 제공해선 안된다.  
  
--- 
## 상속보다는 컴포지션을 사용하라
- 일반적인 구체 클래스를 패키지 경계를 넘어, 즉 다른 패키지의 구체 클래스를 상속하는 일은 위험하하다. 
- 메서드 호출과 달리 상속은 캡슐화를 깨뜨린다. 
  - 상위 클래스가 어떻게 구현되느냐에 따라 하위 클래스의 동작에 이상이 생길 수 있다. 
  - 상위 클래스 설계자가 확장을 충분히 고려하고 문서화도 제대로 해두지 않으면 하위 클래스는 상위 클래스의 변화에 발맞춰 수정돼야만 한다. 
- 기존 클래스를 확장하는 대신, 새루운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 하면 된다. 
  - 이런 설계를 컴포지션이라 한다. 
  - 새 클래스의 인스턴스 메서드들은 기존 클래스의 대응하는 메서드를 호출해 그 결과를 반환한다. 
  - 이 방식을 전달이라 하며, 새 클래스의 메서드들을 전달 메서드라 부른다. 
  - 그 결과 새로운 클래스는 기존 클래스의 내부 구현방식의 영향에서 벗어나며, 심지어 기존 클래스에 새로운 메서드가 추가되더라도 전혀 영향받지 않는다. 
  ```
  public class InstrumentedSet<E> extends ForwardingSet<E> {
      private int addCount = 0;
  
      public InstrumentedSet(Set<E> s) {
          super(s);
      }
  
  
      @Override
      public boolean add(E e) {
          addCount++;
          return super.add(e);
      }
  
  
      @Override
      public boolean addAll(Collection<? extends E> c) {
          addCount++;
          return super.addAll(c);
      }
  
      public int getAddCount() {
          return addCount;
      }
  }
  
  public class ForwardingSet<E> implements Set<E> {
      private final Set<E> s;
  
  
      public ForwardingSet(Set<E> s) {
          this.s = s;
      }
  
      public void clear() {
          s.clear();
      }
  
      public boolean contains(Object o) {
          return s.contains(o);
      }
  
  
      @Override
      public Iterator<E> iterator() {
          return s.iterator();
      }
  
  
      public boolean isEmpty() {
          return s.isEmpty();
      }
  
      public int size() {
          return s.size();
      }
  
      public boolean add(E e) {
          return s.add(e);
      }
  
      public boolean remove(Object o) {
          return s.remove(o);
      }
  
      public boolean containsAll(Collection<?> c) {
          return s.containsAll(c);
      }
  
      public boolean addAll(Collection<? extends E> c) {
          return s.addAll(c);
      }
  
      public boolean removeAll(Collection<?> c) {
          return s.removeAll(c);
      }
  
      public boolean retainAll(Collection<?> c) {
          return s.retainAll(c);
      }
  
      public Object[] toArray() {
          return s.toArray();
      }
  
      public <T> T[] toArray(T[] a) {
          return s.toArray(a);
      }
  
      @Override
      public boolean equals(Object o) {
          if(this == o)
              return true;
          if(!(o instanceof ForwardingSet))
              return false;
  
          ForwardingSet<?> that = (ForwardingSet<?>) o;
  
          return s != null ? s.equals(that.s) : that.s == null;
      }
  
      @Override
      public String toString() {
          return "ForwardingSet{" + "s=" + s + '}';
      }
  
      @Override
      public int hashCode() {
          return s != null ? s.hashCode() : 0;
      }
  }
  ```
  - 상속 방식은 구체 클래스 각각을 따로 확장해야 하며, 지원하고 싶은 상위 클래스의 생성자 각각에 대응하는 생성자를 별도로 지정해줘야 한다. 
  - 하지만 위에서 보인 컴포지션 방식은 한 번만 구현해두면 어떠한 Set 구현체라도 계측할 수 있으며, 기존 생성자들과도 함께 사용할 수 있다.
- 상속은 반드시 하위 클래스가 상위 클래스의 '진짜' 하위 타입인 상황에서만 써야한다. is-a 관계일 때만 상속해야 한다. 
- 컴포지션 대신 상속을 사용하기로 결정하기 전에 마지막으로 자문해야 할 질문이 있다.
 - 확장하려는 클래스의 API에 아무런 결함이 없는가?
 - 컴포지션으로는 이 결함을 숨기는 새로운 API를 설계할 수 있지만, 상속은 상위 클래스의 API를 '그 결함까지도' 그대로 승계한다.  
  
---
## 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라(다시보기)
- 상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지 문서로 남겨야 한다. 
  - 호출되는 메서드가 재정의 가능 메서드라면 그 사실을 호출하는 메서드의 API 설명에 적시해야 한다.
  
---
## 추상 클래스보다는 인터페이스를 우선하라
- 자바가 제공하는 다중 구현 메커니즘은 인터페이스와 추상 클래스 두 가지다. 
- 자바 8부터 인터페이스도 디폴트 메서드를 제공할 수 있게 되어, 이제는 두 메커니즘 모두 인스턴스 메서드를 구현 형태로 제공할 수 있다. 
- 기존 클래스에도 손쉽게 새로운 인터페이스를 구현해넣을 수 있다. 
  - 반면 기존 클래스 위에 새로운 추상 클래스를 끼워넣기는 어러운게 일반적이다. 
- 인터페이스는 믹스인 정의에 안성맞춤이다. 
  - 믹스인이란 클래스가 구현할 수 있는 타입으로, 믹스인을 구현한 클래스에 원래의 '주된 타입' 외에도 특정 선택적 행위를 제공한다고 선언하는 효과를 준다.
  - 이처럼 대상 타입의 주된 기능에 선택적 기능을 '혼합'한다고 해서 믹스인이라 부른다. 
- 추상 클래스로는 기존 클래스에 덧씌울 수 없기 때문에 믹스인을 정의할 수 없다. 
- 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다.(다시보기)
- 래퍼 클래스 관용구와 함께 사용하면 인터페이스는 기능을 향상시키는 안전하고 강력한 수단이 된다 
  - 타입을 추상클래스로 정의해두면 그 타입에 기능을 추가하는 방법은 상속뿐이다. 
- 인터페이스의 메서드 중 구현 방법이 명백한 것이 있다면, 그 구현을 디폴트 메서드로 제공해 프로그래머의 일감을 덜어줄 수 있다. 
  - 디폴트 메서드를 제공할 때는 상속하려는 사람을 위한 설명을 @impleSpec 자바독 태그를 붙여 문서화해야 한다. 
- 디폴트 메서드로 equals와 hashCode 같은 Objec싀 메서드를 제공하면 안된다. 
- 또 인터페이스는 인스턴스 필드를 가질 수 없고 public이 아닌 정적 멤버도 가질 수 없다(단 private 정적 메서드는 예외).
- 한편, 인터페이스와 추상 골격 구현 클래스를 함께 제공하는 식으로 인터페이스와 추상 클래스의 장점을 모두 취하는 방법도 있다. (다시보기)
  - 인터페이스로는 타입을 정의하고, 필요하다면 디폴트 메서드 몇 개도 함께제공한다.
  - 골격 구현 클래스는 나머지 메서드들까지 구현한다. 
  - 이 방법은 템플릿 메서드 패턴이다.  

---
## 인터페이스는 구현하는 쪽을 생각해 설계하라
- 디폴트 메서드를 선언하면, 그 인터페이스를 구현한 후 디폴트 메서드를 재정의하지 않은 모든 클래스에서 디폴트 구현이 쓰이게 된다. 
- 자바는 기존 인터페이스에 메서드를 추가하는 길이 열렸지만 모든 기존 구현체들과 매끄럽게 연동되리라는 보장은 없다. 
- 디폴트 메서드는 기존 구현체에 런타임 오류를 일으킬 수 있다. 
- 기존 인터페이스에 디폴트 메서드로 새 메서드를 추가하는 일이 꼭 필요한 경우가 아니라면 피해야 한다. 
- 반면 새로운 인터페이스를 만드는 경우라면 표준적인 메서드 구현을 제공하는 데 아주 유용한 수단이며, 그 인터페이스를 더 쉽게 궇녀해 활용할 수 잇게 해준다. 
- 디폴트 메서드는 인터페이스로부터 메서드를 제거하거나 기존 메서드의 시그니처를 수정하는 용도가 아님을 명심해야 한다. 

--- 
## 인터페이스는 타입을 정의하는 용도로만 사용하라
- 인터페이스는 자신을 구현한 클래스의 인스턴스를 참조할 수 있는 타입 역할을 한다. 
  - 즉 클래스가 어떤 인터페이스를 구현한다는 것은 자신의 인스턴스로 무엇을 할 수 있는지를 클라이언트에 얘기해주는 것이다.
  - 인터페이스는 오직 이 용도로만 사용해야 한다. 
- 이 지침에 맞지 않은 예로 소위 상수 인터페이스가 있다.
  ```
  // 안티패턴
  public interface PhysicalConstants {
      static final double AVOGARDOS_NUMBER = 6.022_140_847e23;
      static final double BOLZMANN_CONSTANT = 1.380_533_32e-23;
      static final double ELECTRON_MASS = 9.102_292_56e-31;
  }
  ```
  - 클래스 내부에서 사용하는 상수는 외부 인터페이스가 아닌 내부 구현에 해당한다. 
  - 이는 이 내부 구현을 클래스의 API로 노출하는 행위다. 
  - final이 아닌 클래스가 상수 인터페이스를 구현한다면 모든 하위 클래스의 이름공간이 그 인터페이스가 정의한 상수들로 오염된다. 
- 특정 클래스나 인터페이스와 강하게 연관된 상수라면 그 클래스나 인터페이스 자체에 추가해야 한다. 
- 열거 타입으로 나타내기 적합한 상수라면 열거 타입으로 만들어 공개하면 된다. 
- 그것도 아니면, 인스턴스화할 수 없는 유틸리티 클래스에 담아 공개하자. 
  ```
  public class PhysicalConstants {
      private PhysicalConstants() {
      }
      
      static final double AVOGARDOS_NUMBER = 6.022_140_847e23;
      static final double BOLZMANN_CONSTANT = 1.380_533_32e-23;
      static final double ELECTRON_MASS = 9.102_292_56e-31;
  }
  ```
  
---
## 태그 달린 클래스보다는 클래스 계층구조를 활용하라
- 태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적이다. 
  ```
  public class Figure {
      enum Shape { RECTANGLE, CIRCLE };
      
      // 태그 필드
      final Shape shape;
      
      // RECTANGLE일 때만 쓰임
      double length;
      double width;
      
      // CIRCLE일 때만 쓰임
      double radius;
  
  
      public Figure(double radius) {
          shape = Shape.CIRCLE;
          this.radius = radius;
      }
      
      public Figure(double length, double width) {
          shape = Shape.RECTANGLE;
          this.length = length;
          this.width = width;
      }
      
      double area() {
          switch(shape) {
              case RECTANGLE:
                  return length * width;
              case CIRCLE:
                  return Math.PI * (radius * radius);
                  default:
                      throw new AssertionError(shape);
          }
      }
  }
  ```
- 자바와 같은 객체지향 언어는 타입 하나로 다양한 의미의 객체를 표현하는 수단을 제공한다.
  - 바로 클래스 계층구조를 활용한는 서브타이핑이다. 
- 태그 달린 클래스를 클래스 계층구조로 바꾼느 방법을 알아보자
  - 먼저 계층구조의 루트가 될 추상 클래스를 정의하고, 태그 값에 따라 동작이 달라지는 메서드들ㅇ르 루트 클래스의 추상 메서드로 선언한다. 
  - 그런 다음 태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스에 일반 메서드로 추가한다.
  - 모든 하위 클래스에서 사용하는 공통 데이터 필드들도 모두 루트 클래스로 올린다. 
  - 다음으로 루트 클래스를 확장한 구체 클래스를 의미별로 하나씩 정의한다. 
    - 각 하위 클래스에는 각자의 의미에 해당하는 데이터 필드를 넣는다. 
  - 그런 다음 루트 클래스가 정의한 추상 메서드를 각자의 의미에 맞게 구현한다. 
  ```
  public abstract class Figure {
      abstract double area();
  }

  public class Circle extends Figure {
      final double radius;
      
      public Circle(double radius1) {
          this.radius = radius1;
      }
  
  
      @Override
      double area() {
          return Math.PI * (radius * radius);
      }
  }
  
  public class Rectangle extends Figure {
      final double length;
      final double width;
      
      public Rectangle(double length, double width) {
          this.length = length;
          this.width = width;
      }
  
  
      @Override
      double area() {
          return length * width;
      }
  }
  ```

---
## 멤버 클래스는 되도록 static으로 만들라
- 중첩 클래스란 다른 크래스 안에 정의된 클래스를 말한다. 
- 중첩 클래스는 자신을 감싼 바깥 클래스에서만 쓰여야 하며, 그 외의 쓰임새가 있다면 톱레벨 클래스로 만들어야 한다. 
- 중첩 클래스의 종류는 정적 멤버 클래스, 멤버 클래스, 익명 클래스, 지역 클래스 이렇게 네 가지다. 
  - 이 중 첫 번째를 제외한 나머지는 내부 클래스에 해당한다. 
- 정적 멤버 클래스는 다른 클래스 안에 선언되고, 바깥 클래스의 private 멤버에도 접근할 수 있다.
- 정적 멤버 클래스는 흔히 바깥 클래스와 함께 쓰일 때만 유용한 public 도우미 클래스로 쓰인다.
- 비정적 멤버 클래스의 인스턴스는 바깥 클래스의 인스턴스와 암묵적으로 연결된다. 
  - 비정적 멤버 클래스의 인스턴스 메서드에서 정규화된 this를 사용해 바깥 인스턴스의 메서드를 호출하거나 바깥 인스턴스의 참조를 가져올 수 있다. 
  - 정규화된 this란 클래스명.this 형태로 바깥 클래스의 이름을 명시하는 용법을 말한다. 
- 따라서 중첩 클래스의 인스턴스가 바깥 인스턴스와 독립적으로 존재할 수 있다면 정적 멤버 클래스로 만들어야 한다. 
- 비정적 멤버 클래스의 인스턴스와 바깥 인스턴스 사이의 관계는 멤버 클래스가 인스턴스화될 때 확립되며, 더 이상 변경할 수 없다. 
- 비정적 멤버 클래스는 어댑터를 정의할 때 자주 쓰인다. 
  - 즉 어떤 클래스의 인스턴스를 감싸 마치 다른 클래스의 인스턴스처럼 보이게 하는 뷰로 사용하는 것이다. 
- 멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙여 정적 멤버 클래스로 만들자. 
  - static을 생략하면 바깥 인스턴스로의 숨은 외부 참조를 갖게 된다. 
  - 이 참조를 저장하려면 시간과 공간이 소비된다. 
  - 더 심각한 문제는 가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못한다. 
- private 정적 멤버 클래스는 흔히 바깥 클래스가 표현하는 객체의 한 부분을 나타낼 때 쓴다. 
- 중첩 클래스가 한 메서드 안에서만 쓰이면서 그 인스턴스를 생성하는 지점이 단 한 곳이고 해당 타입으로 쓰기에 적합한 클래스나 인터페이스가 이미 있다면 익명 클래스로 만들고, 그렇지 않으면 지역 클래스로 만들자.

---
## 톱레벨 클래스는 한 파일에 하나만 담으라
- 소스파일 하나에는 방드시 톱레벨 클래스를 하나만 담자. 

  