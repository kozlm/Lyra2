package algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Lyra2Test {

    @Test
    void hashBlake2b() {
        Lyra2 lyra = new Lyra2(8, 256, 3, 100, 12, 12, Lyra2.SpongeAlgorithm.Blake2B);
        assertArrayEquals(lyra.hash(
                "p",
                "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                1),
                new byte[]{(byte) 0xe0});
        Lyra2 lyra1 = new Lyra2(8, 256, 100, 100, 12, 12, Lyra2.SpongeAlgorithm.Blake2B);
        assertArrayEquals(lyra1.hash(
                        "p",
                        "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                        10),
                new byte[]{
                        (byte) 0xc8, (byte) 0xfe, (byte) 0xfd, (byte) 0x14, (byte) 0x30,
                        (byte) 0x15, (byte) 0x55, (byte) 0x2e, (byte) 0x6e, (byte) 0x6d,
        });
        Lyra2 lyra2 = new Lyra2(12, 256, 3, 100, 12, 12, Lyra2.SpongeAlgorithm.Blake2B);
        assertArrayEquals(lyra2.hash(
                        "p",
                        "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                        1),
                new byte[]{(byte) 0xf5});
        Lyra2 lyra3 = new Lyra2(12, 256, 10, 10, 12, 12, Lyra2.SpongeAlgorithm.Blake2B);
        assertArrayEquals(lyra3.hash(
                        "pppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp",
                        "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                        10),
                new byte[]{
                        (byte) 0xf6, (byte) 0x13, (byte) 0x18, (byte) 0x97, (byte) 0x0e,
                        (byte) 0xf6, (byte) 0xde, (byte) 0xe8, (byte) 0xea, (byte) 0xdc,
                });
    }

    @Test
    void hashBlaMka() {
        Lyra2 lyra = new Lyra2(12, 96, 3, 100, 12, 12, Lyra2.SpongeAlgorithm.BlaMka);
        assertArrayEquals(lyra.hash(
                        "p",
                        "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                        1),
                new byte[]{(byte) 0xb9});
        Lyra2 lyra1 = new Lyra2(12, 96, 3, 10, 12, 12, Lyra2.SpongeAlgorithm.BlaMka);
        assertArrayEquals(lyra1.hash(
                        "password",
                        "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                        10),
                new byte[]{
                        (byte) 0x1b, (byte) 0xbd, (byte) 0x06, (byte) 0xd1, (byte) 0x1d,
                        (byte) 0x75, (byte) 0x8f, (byte) 0xb1, (byte) 0xa2, (byte) 0x6d,
                });
        Lyra2 lyra2 = new Lyra2(8, 96, 3, 100, 12, 1, Lyra2.SpongeAlgorithm.BlaMka);
        assertArrayEquals(lyra2.hash(
                        "p",
                        "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                        1),
                new byte[]{(byte) 0x89});
        Lyra2 lyra3 = new Lyra2(8, 96, 100, 100, 12, 1, Lyra2.SpongeAlgorithm.BlaMka);
        assertArrayEquals(lyra3.hash(
                        "p",
                        "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                        10),
                new byte[]{
                        (byte) 0xb3, (byte) 0x5b, (byte) 0xc9, (byte) 0xd4, (byte) 0xb5,
                        (byte) 0xb4, (byte) 0xad, (byte) 0x5f, (byte) 0xcf, (byte) 0x29,
                });
    }

    @Test
    void leastSignificantWord() {
        long whole = 0x4DD564D55DEC564DL;
        assertEquals(Lyra2.leastSignificantWord(whole), 0x000000005DEC564DL);
    }

    @Test
    void byteArrayToString() {
        byte[] bytes = new byte[]{
                (byte) 0xc7, (byte) 0x6a, (byte) 0x1b, (byte) 0x21, (byte) 0x7d,
        };
        assertEquals(Lyra2.byteArrayToString(bytes), "C7 6A 1B 21 7D ");
    }

    @Test
    void addWordwise() {
        long a = 0, b = 1, c = 2, d = 3;
        assertEquals(Lyra2.addWordwise(a, b), 1);
        assertEquals(Lyra2.addWordwise(a, b, c, d), 6);
    }

    @Test
    void longToBytes() {
        long x = 856478654845L;
        assertArrayEquals(Lyra2.longToBytes(x), new byte[]{
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xc7,
                (byte) 0x6a, (byte) 0x1b, (byte) 0x21, (byte) 0x7d,
        });
    }

    @Test
    void intToBytes() {
        int x = 856478654;
        assertArrayEquals(Lyra2.intToBytes(x), new byte[]{(byte) 0xbe, (byte) 0xd3, (byte) 0x0c, (byte) 0x33});
    }

    @Test
    void bytesToLongs() {
        byte[] bytes = {
                (byte) 0xdc, (byte) 0x74, (byte) 0xed, (byte) 0x8e,
                (byte) 0x45, (byte) 0xc7, (byte) 0xd5, (byte) 0x56,
                (byte) 0xdc, (byte) 0x74, (byte) 0xed, (byte) 0x8e,
                (byte) 0x45, (byte) 0xc7, (byte) 0xd5, (byte) 0x56,
        };
        assertArrayEquals(Lyra2.bytesToLongs(bytes), new long[]{-2561161092755106474L, -2561161092755106474L});
    }

    @Test
    void switchEndian() {
        long a = 4, b = 12413425;
        long a1 = 288230376151711744L, b1 = -1051101230316650496L;
        assertEquals(Lyra2.switchEndian(a), a1);
        assertEquals(Lyra2.switchEndian(b), b1);
    }
}