package commandline;

import algorithm.Lyra2;
import com.beust.jcommander.Parameter;

public class LyraArgs {
    @Parameter(names = "--help", help = true, hidden = true)
    private boolean help;

    @Parameter(
            description = "[password to hash]",
            required = true
    )
    private String password;


    @Parameter(
            names = {"-a", "--algorithm"},
            description = "sponge algorithm ('BlaMka' or 'Blake2B')",
            required = true
    )
    private Lyra2.SpongeAlgorithm algorithm;

    @Parameter(
            names = {"-s", "--salt"},
            description = "salt used during hashing",
            required = true
    )
    private String salt;

    @Parameter(
            names = {"-k", "--hashlength"},
            description = "length of the hashed password in bytes",
            required = true
    )
    private int hashLength;

    @Parameter(
            names = {"-f", "--fullrounds"},
            description = "number of rounds performed for regular sponge function [1 - 12]"
    )
    private int fullRounds = 12;

    @Parameter(
            names = {"-h", "--halfrounds"},
            description = "number of rounds performed for reduced sponge function [1 - 12]"
    )
    private int halfRounds = 6;

    @Parameter(
            names = {"-b", "--blocks"},
            description = "number of longs (8 bytes) that make up a block [1 - 12]"
    )
    private int blockLengthInLong = 12;

    @Parameter(
            names = {"-c", "--cols"},
            description = "number of columns in the memory matrix"
    )
    private int nCols = 256;

    @Parameter(
            names = {"-r", "--rows"},
            description = "number of rows in the memory matrix"
    )
    private int nRows = 10;

    @Parameter(
            names = {"-t", "--timecost"},
            description = "time cost"
    )
    private int timeCost = 10;

    public boolean isHelp() {
        return help;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public int getHashLength() {
        return hashLength;
    }

    public int getFullRounds() {
        return fullRounds;
    }

    public Lyra2.SpongeAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getHalfRounds() {
        return halfRounds;
    }

    public int getBlockLengthInLong() {
        return blockLengthInLong;
    }

    public int getnCols() {
        return nCols;
    }

    public int getnRows() {
        return nRows;
    }

    public int getTimeCost() {
        return timeCost;
    }


}
