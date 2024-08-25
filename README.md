# Lyra2 Algorithm implementation in Java

## Overview

This project implements the Lyra2 password hashing algorithm on a single thread in Java.
One of the goals of this project is to follow as many OOP good practices as possible,
while not making the code too complicated.

More information about [Lyra2][2] and the official Lyra2 [documentation][1].

## Features

- **Sponge Algorithms**: Supports both `BlaMka` and `Blake2B` as sponge functions.
- **Configurable Parameters**: Allows customization of the memory matrix size, number of rounds, block size, and more to tailor the hashing process to your needs.
- **High Security**

## Usage
To compile use the command:
```bash
mvn package
```

After compilation use the following syntax:
```bash
java -jar ./target/Lyra2-1.0-SNAPSHOT.jar [options] [password to hash]
```

For more information about the syntax use:
```bash
java -jar ./target/Lyra2-1.0-SNAPSHOT.jar --help
```

### Required Options

- **`-a, --algorithm`**: Sponge algorithm to use.  
  Possible values: `BlaMka`, `Blake2B`.

- **`-k, --hashlength`**: The length of the hashed password in bytes.

- **`-s, --salt`**: The salt to use during hashing.  

### Optional Parameters

- **`-b, --blocks`**: The number of longs (8 bytes) that make up a block in the memory matrix.  
  Default: `12`  
  Range: `[1 - 12]`

- **`-c, --cols`**: The number of columns in the memory matrix.  
  Default: `256`

- **`-f, --fullrounds`**: The number of rounds performed by the regular sponge function.  
  Default: `12`  
  Range: `[1 - 12]`

- **`-h, --halfrounds`**: The number of rounds performed by the reduced sponge function.  
  Default: `6`  
  Range: `[1 - 12]`

- **`-r, --rows`**: The number of rows in the memory matrix.  
  Default: `10`

- **`-t, --timecost`**: The time cost parameter, which adjusts the execution time of the algorithm.  
  Default: `10`

## Example

Here is an example of how to use the Lyra2 algorithm with specific options:

```bash
java -jar ./target/Lyra2-1.0-SNAPSHOT.jar -a Blake2B -s ssss -k 1 -f 12 -h 12 -b 12 -c 256 -r 3 -t 100 'password123'
```

## References

During the development of this project, the following Git repositories were used as conceptual references:

- **[Original implementation][4]** used for planning of the project outline

- **[Lyra2 Java Implementation][3]** by Alexander Lisianoi used for testing and debugging

[1]: https://eprint.iacr.org/2015/136
[2]: https://en.wikipedia.org/wiki/Lyra2
[3]: https://github.com/alisianoi/lyra2-java
[4]: https://github.com/leocalm/Lyra