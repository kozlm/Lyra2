public class Main {
    public static void main(String[] args) {
        Lyra2 lyra = new Lyra2(8, 256, 10, 100, 12, 6);
        byte[] hash = lyra.hash("p", "s", 8);
        System.out.println(Lyra2.byteArrayToString(hash));
    }
}
