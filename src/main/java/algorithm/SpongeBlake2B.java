package algorithm;

public class SpongeBlake2B extends Sponge{

    public SpongeBlake2B(int blockLengthInLong, int nCols, int fullRounds, int halfRounds) {
        super(blockLengthInLong, nCols, fullRounds, halfRounds);
    }

    @Override
    public void gFunction(int a, int b, int c, int d) {
        state[a] = Lyra2.addWordwise(state[a], state[b]);
        state[d] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[d] ^ state[a]), 32));

        state[c] = Lyra2.addWordwise(state[c], state[d]);
        state[b] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[b] ^ state[c]), 24));

        state[a] = Lyra2.addWordwise(state[a], state[b]);
        state[d] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[d] ^ state[a]), 16));

        state[c] = Lyra2.addWordwise(state[c], state[d]);
        state[b] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[b] ^ state[c]), 63));
    }
}

