package item4;

public class UtiltyClass {
    // 기본 생성자가 만드는걸 막음
    private UtiltyClass() {
        // error를 던질 필요까진 없음
        throw new AssertionError();
    }
}
