public class TestSkipList extends SkipList {
  public static void main(String[] args) {
    System.out.println("isEmpty(): " + Boolean.toString(testIsEmpty()));
    System.out.println("size(): " + Boolean.toString(testSize()));
    System.out.println("clear(): " + Boolean.toString(testClear()));
    System.out.println("get(int index): " + Boolean.toString(testGet()));
    System.out.println("getQuantile(double quantile): " + Boolean.toString(testGetQuantile()));
  }
}
