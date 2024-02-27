public class Parameters {

    public static int nCols = 256;

    public static int timeCost = 100;

    public static int nRows = 10;

    public static int fullRounds = 12;

    public static int halfRounds = 12;

    public static int blockLengthInLong = 8;

    public static int blockLengthInByte = blockLengthInLong * 8;

    public static int rowLengthInLong = nCols * blockLengthInLong;

    public static int rowLengthInByte = rowLengthInLong * 8;

}

