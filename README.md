Repo for CSC1098 Compiler Construction. The design decisions I made are recorded in the [journal](./JOURNAL.md).
# How to use
```sh
./gradlew installDist
./build/install/CCL/bin/CCL <source> [destination] [-p]
```
- If `destination` is omitted, the output is stdout.
- If `-p` is passed, the program will attempt to prune unused variables and functions, which I have not tested thoroughly enough to make a guarantee of correctness on all input.