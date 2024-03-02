import algorithm.Lyra2;
import com.beust.jcommander.JCommander;
import commandline.LyraArgs;

public class Main {
    public static void main(String[] args) {
        LyraArgs lyraArgs = new LyraArgs();
        JCommander jc = JCommander.newBuilder()
                .addObject(lyraArgs)
                .build();
        jc.parse(args);

        if (lyraArgs.isHelp()) jc.usage();

        Lyra2 lyra = new Lyra2(
                lyraArgs.getBlockLengthInLong(),
                lyraArgs.getnCols(),
                lyraArgs.getnRows(),
                lyraArgs.getTimeCost(),
                lyraArgs.getFullRounds(),
                lyraArgs.getHalfRounds(),
                lyraArgs.getAlgorithm());
        byte[] hash = lyra.hash(
                lyraArgs.getPassword(),
                lyraArgs.getSalt(),
                lyraArgs.getHashLength()
        );
        System.out.println(Lyra2.byteArrayToString(hash));
    }
}
