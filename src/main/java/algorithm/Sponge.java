package algorithm;

public abstract class Sponge {
    long[] state;

    // Algorithm parameters
    private final int blockLengthInLong;
    private final int blockLengthInByte;
    private final int nCols;
    private final int fullRounds;
    private final int halfRounds;


    long[] InitiazationVector = {
            0x6a09e667f3bcc908L,
            0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL,
            0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L,
            0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL,
            0x5be0cd19137e2179L};


    public Sponge(int blockLengthInLong, int nCols, int fullRounds, int halfRounds) {
        this.blockLengthInLong = blockLengthInLong;
        this.blockLengthInByte = blockLengthInLong * 8;
        this.nCols = nCols;
        this.fullRounds = fullRounds;
        this.halfRounds = halfRounds;

        state = new long[16];
        for (int i = 0; i < 8; i++) {
            state[i] = 0;
            state[i + 8] = Lyra2.switchEndian(InitiazationVector[i]);
        }
    }


    private void shuffle(int rounds) {
        for (int i = 0; i < rounds; i++) {
            gFunction(0, 4, 8, 12);
            gFunction(1, 5, 9, 13);
            gFunction(2, 6, 10, 14);
            gFunction(3, 7, 11, 15);
            gFunction(0, 5, 10, 15);
            gFunction(1, 6, 11, 12);
            gFunction(2, 7, 8, 13);
            gFunction(3, 4, 9, 14);
        }
    }


    public abstract void gFunction(int a, int b, int c, int d);


    public byte[] squeezeBytes(int amount) {
        int iterator = 0;
        byte[] out = new byte[amount];
        //whole blocks
        int numberOfBlocks = amount / blockLengthInByte;
        int rest = amount % blockLengthInByte;
        for (int i = 0; i < numberOfBlocks; i++) {
            for (int j = 0; j < blockLengthInLong; j++) {
                byte[] bytes = Lyra2.longToBytes(state[j]);
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
            byte[] bytes = Lyra2.longToBytes(state[i]);
            for (int j = 0; j < 8; j++) {
                out[iterator] = bytes[j];
                iterator++;
            }
        }
        //remaining bytes
        for (int i = 0; i < restOfRest; i++) {
            byte[] bytes = Lyra2.longToBytes(state[longsInRest]);
            out[iterator] = bytes[i];
            iterator++;
        }
        return out;
    }


    public void absorbBlock(long[] block) {
        for (int i = 0; i < block.length; i++) {
            state[i] ^= block[i];
        }
        shuffle(fullRounds);
    }


    public void reducedSqueezeRow0(long[] out) {
        for (int i = 0; i < nCols; i++) {
            System.arraycopy(state, 0, out, (nCols - 1 - i) * blockLengthInLong, blockLengthInLong);
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
                state[j] ^= Lyra2.addWordwise(row1[offset + j], prev0[offset + j], prev1[offset + j]);

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


    public void reducedDuplexVisitationLoop(long[] row0, long[] row1, long[] prev0, long[] prev1) {
        for (int i = 0; i < nCols; i++) {
            int col0 = (int) Long.remainderUnsigned(Lyra2.switchEndian(state[4]), nCols);
            int col1 = (int) Long.remainderUnsigned(Lyra2.switchEndian(state[6]), nCols);
            int offset = i * blockLengthInLong;
            for (int j = 0; j < blockLengthInLong; j++) {
                state[j] ^= Lyra2.addWordwise(row0[offset + j], row1[offset + j]
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
