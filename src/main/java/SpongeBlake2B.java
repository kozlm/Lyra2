import java.nio.ByteBuffer;


public class SpongeBlake2B {

    long[] state;

    // Algorithm parameters
    private final int blockLengthInLong;
    private final int blockLengthInByte;
    private final int nCols;
    private final int fullRounds;
    private final int halfRounds;


    public long addWordwise(long... longs) {
        long result = 0;
        for (long l : longs) {
            result += Lyra2.switchEndian(l);
        }
        return Lyra2.switchEndian(result);
    }


    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }


    long[] InitiazationVector = {
            0x6a09e667f3bcc908L,
            0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL,
            0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L,
            0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL,
            0x5be0cd19137e2179L};


    public SpongeBlake2B() {
        this.blockLengthInLong = Parameters.blockLengthInLong;
        this.blockLengthInByte = Parameters.blockLengthInByte;
        this.nCols = Parameters.nCols;
        this.fullRounds = Parameters.fullRounds;
        this.halfRounds = Parameters.halfRounds;
        state = new long[16];
        for (int i = 0; i < 8; i++) {
            state[i] = 0;
            state[i + 8] = InitiazationVector[i];
        }
    }


    private void shuffle(int rounds) {
        for (int i = 0; i < rounds; i++) {
            functionG(0, 4, 8, 12);
            functionG(1, 5, 9, 13);
            functionG(2, 6, 10, 14);
            functionG(3, 7, 11, 15);
            functionG(0, 5, 10, 15);
            functionG(1, 6, 11, 12);
            functionG(2, 7, 8, 13);
            functionG(3, 4, 9, 14);
        }
    }


    private void functionG(int a, int b, int c, int d) {
        state[a] = addWordwise(state[a], state[b]);
        state[d] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[d] ^ state[a]), 32));

        state[c] = addWordwise(state[c], state[d]);
        state[b] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[b] ^ state[c]), 24));

        state[a] = addWordwise(state[a], state[b]);
        state[d] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[d] ^ state[a]), 16));

        state[c] = addWordwise(state[c], state[d]);
        state[b] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[b] ^ state[c]), 63));
    }


    public byte[] squeeze(int amount) {
        int iterator = 0;
        byte[] out = new byte[amount];
        //whole blocks
        int numberOfBlocks = amount / blockLengthInByte;
        int rest = amount % blockLengthInByte;
        for (int i = 0; i < numberOfBlocks; i++) {
            for (int j = 0; j < blockLengthInLong; j++) {
                byte[] bytes = longToBytes(state[j]);
                for (int k = 0; k < 8; k++) {
                    out[iterator] = bytes[k];
                    iterator++;
                }
            }
            shuffle(fullRounds);
        }
        //rest
        int longsInRest = rest / 8;
        int restOfRest = rest % 8;
        for (int i = 0; i < longsInRest; i++) {
            byte[] bytes = longToBytes(state[i]);
            for (int j = 0; j < 8; j++) {
                out[iterator] = bytes[j];
                iterator++;
            }
        }
        //remaining bytes
        for (int i = 0; i < restOfRest; i++) {
            byte[] bytes = longToBytes(state[longsInRest]);
            out[iterator] = bytes[i];
            iterator++;
        }
        return out;
    }


    public void absorbBlock(long[] in, int length, int offset) {
        for (int i = 0; i < length; i++) {
            state[i] ^= in[i + offset];
        }
        shuffle(fullRounds);
    }


    public void reducedSqueezeRow(long[] out) {

        for (int i = 0; i < nCols; i++) {
            int iterator = 0;
            for (int j = 0; j < blockLengthInLong; j++) {
                out[iterator] = state[j];
                iterator++;
            }
            shuffle(halfRounds);
        }
    }


    public void reducedDuplexRow1And2(long[] out, long[] in) {
        int iteratorIn = 0;
        for (int i = 0; i < nCols; i++) {
            int iteratorOut = (nCols - 1 - i) * blockLengthInLong;
            for (int j = 0; j < blockLengthInLong; j++) {
                state[j] ^= in[iteratorIn];
                iteratorIn++;
            }
            iteratorIn -= blockLengthInLong;
            shuffle(halfRounds);
            for (int j = 0; j < blockLengthInLong; j++) {
                out[iteratorOut] = state[j] ^ in[iteratorIn];
                iteratorIn++;
                iteratorOut++;
            }
        }
    }


    public void reducedDuplexFillingLoop(long[] row0, long[] row1, long[] prev0, long[] prev1) {
        for (int i = 0; i < nCols; i++) {
            for (int j = 0; j < blockLengthInLong; j++) {
                int offset = i * blockLengthInLong;
                state[j] ^= addWordwise(row1[offset + j], prev0[offset + j], prev1[offset + j]);

            }
            shuffle(halfRounds);
            for (int j = 0; j < blockLengthInLong; j++) {
                int offset = i * blockLengthInLong;
                row0[(nCols - 1 - i) * blockLengthInLong + j]
                        = prev0[offset + j] ^ state[j];
            }
            for (int j = 0; j < blockLengthInLong; j++) {
                int offset = i * blockLengthInLong;
                row1[offset + j] ^= state[(j + 2) % blockLengthInLong];
            }
        }
    }


    public void reducedDuplexWandering(long[] row0, long[] row1, long[] prev0, long[] prev1) {
        for (int i = 0; i < nCols; i++) {
            int col0 = (int) Long.remainderUnsigned(Lyra2.switchEndian(state[4]),
                    nCols);
            int col1 = (int) Long.remainderUnsigned(Lyra2.switchEndian(state[6]),
                    nCols);
            int offset = i * blockLengthInLong;
            for (int j = 0; j < blockLengthInLong; j++) {
                state[j] ^= addWordwise(row0[offset + j], row1[offset + j]
                        , prev0[blockLengthInLong * col0 + j]
                        , prev1[blockLengthInLong * col1 + j]);
            }
            shuffle(halfRounds);
            for (int j = 0; j < blockLengthInLong; j++) {
                row0[offset + j] ^= state[j];
            }
            for (int j = 0; j < blockLengthInLong; j++) {
                row1[offset + j] ^= state[(j + 2) % blockLengthInLong];
            }
        }
    }
}

