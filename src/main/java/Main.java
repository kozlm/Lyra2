import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Lyra2 lyra = new Lyra2();
        byte[] hash = lyra.hash("p", "s", 5);
        System.out.println(Lyra2.byteArrayToString(hash));
    }
}
