package algorithm;

public class SpongeBlaMka extends Sponge{
    public SpongeBlaMka(int blockLengthInLong, int nCols, int fullRounds, int halfRounds) {
        super(blockLengthInLong, nCols, fullRounds, halfRounds);
    }

    private long blaMkaFunction(long a, long b){
        return Lyra2.switchEndian(
                Lyra2.switchEndian(a) + Lyra2.switchEndian(b) +
                        2 * (Lyra2.leastSignificantWord(Lyra2.switchEndian(a)) * Lyra2.leastSignificantWord(Lyra2.switchEndian(b)))
        );
    }

    @Override
    public void gFunction(int a, int b, int c, int d) {
        state[a] = blaMkaFunction(state[a], state[b]);
        state[d] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[d] ^ state[a]), 32));

        state[c] = blaMkaFunction(state[c], state[d]);
        state[b] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[b] ^ state[c]), 24));

        state[a] = blaMkaFunction(state[a], state[b]);
        state[d] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[d] ^ state[a]), 16));

        state[c] = blaMkaFunction(state[c], state[d]);
        state[b] = Lyra2.switchEndian(Long.rotateRight(Lyra2.switchEndian(state[b] ^ state[c]), 63));
    }
}
