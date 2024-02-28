import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Lyra2 {

    // Algorithm parameters
    private final int blockLengthInLong;
    private final int blockLengthInByte;
    private final int nCols;
    private final int nRows;
    private final int timeCost;
    private final int rowLengthInLong;
    private final int rowLengthInByte;

    public Lyra2() {
        this.blockLengthInLong = Parameters.blockLengthInLong;
        this.blockLengthInByte = Parameters.blockLengthInByte;
        this.nCols = Parameters.nCols;
        this.nRows = Parameters.nRows;
        this.timeCost = Parameters.timeCost;
        this.rowLengthInLong = Parameters.rowLengthInLong;
        this.rowLengthInByte = Parameters.rowLengthInByte;
    }

    public byte[] hash(String pwdString, String saltString, int hashlength) {

        //Bootstrapping phase
        //-------------------
        long[][] matrix = new long[nRows][rowLengthInLong];
        SpongeBlake2B sponge = new SpongeBlake2B();

        int gap = 1;
        int stp = 1;
        int wnd = 2;
        int sqrt = 2;
        int prev0 = 2;
        int prev1 = 0;
        int row1 = 1;
        int row0 = 3;

        byte[] test = getPaddedData(pwdString, saltString, hashlength, timeCost, nRows, nCols);
        long[] buffer = bytesToLongs(getPaddedData(pwdString, saltString, hashlength, timeCost, nRows, nCols));

        //fill matrix with buffer data
        int iterator = 0, x = 0, y = 0;
        while (iterator < buffer.length) {
            matrix[y][x] = buffer[iterator];
            iterator++;
            x++;
            if (x >= rowLengthInLong) {
                y++;
                x = 0;
            }
        }

        for (int i = 0; i < buffer.length; i += blockLengthInLong) {
            sponge.absorbBlock(buffer, i, blockLengthInLong);
        }

        //Setup phase
        //-----------
        sponge.reducedSqueezeRow(matrix[0]);
        sponge.reducedDuplexRow1And2(matrix[1], matrix[0]);
        sponge.reducedDuplexRow1And2(matrix[2], matrix[1]);

        //Filling loop
        for (row0 = 3; row0 < nRows; row0++) {
            sponge.reducedDuplexFillingLoop(matrix[row0], matrix[row1], matrix[prev0], matrix[prev1]);
            prev0 = row0;
            prev1 = row1;
            row1 = (row1 + stp) % wnd;
            if (row1 == 0) {
                wnd *= 2;
                stp = sqrt + gap;
                gap = -gap;
                if (gap == -1) sqrt *= 2;
            }
        }

        //Wandering phase
        //---------------
        for (int wCount = 0; wCount<nRows*timeCost; wCount++){
            row0 = (int) Long.remainderUnsigned(switchEndian(sponge.state[0]),nRows);
            row1 = (int) Long.remainderUnsigned(switchEndian(sponge.state[2]),nRows);

            sponge.reducedDuplexWandering(matrix[row0], matrix[row1], matrix[prev0], matrix[prev1]);

            prev0 = row0;
            prev1 = row1;
        }

        //Wrap-up phase
        //-------------
        sponge.absorbBlock(matrix[row0],0, blockLengthInLong);

        byte[] hash = sponge.squeeze(hashlength);
        return hash;
    }

    private byte[] getPaddedData(String passwordString, String saltString, int kLen, int t, int r, int c) {
        byte[] pwd = passwordString.getBytes();
        byte[] salt = saltString.getBytes();

        int nBlocksInput = ((salt.length + pwd.length + 6 * Integer.BYTES) / blockLengthInByte) + 1;
        byte[] data = new byte[nBlocksInput * blockLengthInByte];

        int iterator = 0;
        //put pwd
        for (byte pwdByte : pwd) {
            data[iterator] = pwdByte;
            iterator++;
        }
        //put salt
        for (byte saltByte : salt) {
            data[iterator] = saltByte;
            iterator++;
        }
        //put len(k)
        for (byte kLenByte : intToBytes(kLen)) {
            data[iterator] = kLenByte;
            iterator++;
        }
        //put len(pwd)
        for (byte pwdLenByte : intToBytes(pwd.length)) {
            data[iterator] = pwdLenByte;
            iterator++;
        }
        //put len(salt)
        for (byte saltLenByte : intToBytes(salt.length)) {
            data[iterator] = saltLenByte;
            iterator++;
        }
        //put T
        for (byte tByte : intToBytes(t)) {
            data[iterator] = tByte;
            iterator++;
        }
        //put R
        for (byte rByte : intToBytes(r)) {
            data[iterator] = rByte;
            iterator++;
        }
        //put C
        for (byte cByte : intToBytes(c)) {
            data[iterator] = cByte;
            iterator++;
        }

        data[iterator] = (byte) 0x80;
        data[data.length - 1] ^= (byte) 0x01;

        return data;
    }

    public static String byteArrayToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02X", b));
            builder.append(" ");
        }
        return builder.toString();
    }

    public static byte[] intToBytes(int i) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(i);
        return buffer.array();
    }

    public static long[] bytesToLongs(byte[] byteArray) {
        int count = byteArray.length / 8;
        long[] longArray = new long[count];
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        for (int i = 0; i < count; i++) {
            longArray[i] = byteBuffer.getLong();
        }
        return longArray;
    }

    public static long switchEndian(final long x) {
        return (x & 0x00000000000000FFL) << 56
                | (x & 0x000000000000FF00L) << 40
                | (x & 0x0000000000FF0000L) << 24
                | (x & 0x00000000FF000000L) << 8
                | (x & 0x000000FF00000000L) >>> 8
                | (x & 0x0000FF0000000000L) >>> 24
                | (x & 0x00FF000000000000L) >>> 40
                | (x & 0xFF00000000000000L) >>> 56;
    }

}
