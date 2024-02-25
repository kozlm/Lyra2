import java.nio.ByteBuffer;


public class SpongeBlake2B {

    long[] state;

    /////////////////////////////////////////
    private final int BLOCK_LENGTH_IN_LONG;
    private final int BLOCK_LENGTH_IN_BYTES;
    private final int N_COLS;
    private final int FULL_ROUNDS;
    private final int HALF_ROUNDS;
    /////////////////////////////////////////


    public long addWordwise(long... longs) {
        long result = 0;
        for (long l : longs) {
            result += switchEndian(l);
        }
        return switchEndian(result);
    }


    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
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
        this.BLOCK_LENGTH_IN_LONG = Parameters.BLOCK_LENGTH_IN_LONG;
        this.BLOCK_LENGTH_IN_BYTES = Parameters.BLOCK_LENGTH_IN_BYTES;
        this.N_COLS = Parameters.N_COLS;
        this.FULL_ROUNDS = Parameters.FULL_ROUNDS;
        this.HALF_ROUNDS = Parameters.HALF_ROUNDS;
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
        state[d] = switchEndian(Long.rotateRight(switchEndian(state[d] ^ state[a]), 32));

        state[c] = addWordwise(state[c], state[d]);
        state[b] = switchEndian(Long.rotateRight(switchEndian(state[b] ^ state[c]), 24));

        state[a] = addWordwise(state[a], state[b]);
        state[d] = switchEndian(Long.rotateRight(switchEndian(state[d] ^ state[a]), 16));

        state[c] = addWordwise(state[c], state[d]);
        state[b] = switchEndian(Long.rotateRight(switchEndian(state[b] ^ state[c]), 63));
    }


    public void squeeze(byte[] out, int amount) {
        int iterator = 0;
        //whole blocks
        int numberOfBlocks = amount / BLOCK_LENGTH_IN_BYTES;
        int rest = amount % BLOCK_LENGTH_IN_BYTES;
        for (int i = 0; i < numberOfBlocks; i++) {
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                byte[] bytes = longToBytes(state[j]);
                for (int k = 0; k < 8; k++) {
                    out[iterator] = bytes[k];
                    iterator++;
                }
            }
            shuffle(FULL_ROUNDS);
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
    }


    public void absorbBlock(long[] in, int length, int offset) {
        for (int i = 0; i < length; i++) {
            state[i] ^= in[i + offset];
        }
        shuffle(FULL_ROUNDS);
    }


    public void reducedSqueezeRow(long[] out) {

        for (int i = 0; i < N_COLS; i++) {
            int iterator = 0;
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                out[iterator] = state[j];
                iterator++;
            }
            shuffle(HALF_ROUNDS);
        }
    }


    public void reducedDuplexRow1And2(long[] out, long[] in) {
        int iteratorIn = 0;
        for (int i = 0; i < N_COLS; i++) {
            int iteratorOut = (N_COLS - 1 - i) * BLOCK_LENGTH_IN_LONG;
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                state[j] ^= in[iteratorIn];
                iteratorIn++;
            }
            iteratorIn -= BLOCK_LENGTH_IN_LONG;
            shuffle(HALF_ROUNDS);
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                out[iteratorOut] = state[j] ^ in[iteratorIn];
                iteratorIn++;
                iteratorOut++;
            }
        }
    }


    public void reducedDuplexFillingLoop(long[] row1, long[] row0, long[] prev0, long[] prev1) {
        for (int i = 0; i < N_COLS; i++) {
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                int offset = i * BLOCK_LENGTH_IN_LONG;
                state[j] ^= addWordwise(row1[offset + j], prev0[offset + j], prev1[offset + j]);

            }
            shuffle(HALF_ROUNDS);
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                int offset = i * BLOCK_LENGTH_IN_LONG;
                row0[(N_COLS - 1 - i) * BLOCK_LENGTH_IN_LONG + j]
                        = prev0[offset + j] ^ state[j];
            }
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                int offset = i * BLOCK_LENGTH_IN_LONG;
                row1[offset + j] ^= state[(j + 2) % BLOCK_LENGTH_IN_LONG];
            }
        }
    }


    public void reducedDuplexWandering(long[] row1, long[] row0, long[] prev0, long[] prev1) {
        for (int i = 0; i < N_COLS; i++) {
            int col0 = (int) Long.remainderUnsigned(switchEndian(state[4]),
                    N_COLS);
            int col1 = (int) Long.remainderUnsigned(switchEndian(state[6]),
                    N_COLS);
            int offset = i * BLOCK_LENGTH_IN_LONG;
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                state[j] ^= addWordwise(row0[offset + j], row1[offset + j]
                        , prev0[BLOCK_LENGTH_IN_LONG * col0 + j]
                        , prev1[BLOCK_LENGTH_IN_LONG * col1 + j]);
            }
            shuffle(HALF_ROUNDS);
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                row0[offset + j] ^= state[j];
            }
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                row1[offset + j] ^= state[(j + 2) % BLOCK_LENGTH_IN_LONG];
            }
        }
    }
}

