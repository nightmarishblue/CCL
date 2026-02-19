A basic compiler I wrote for a college assignment.

CCL is a basic language with functions, if statements, loops and arithmetic. It compiles into a 3-address code that runs on [this interpreter](https://www.computing.dcu.ie/~davids/courses/CA4003/TACi.jar).

The design decisions I made are recorded in the [journal](./JOURNAL.md).

# How to use the compiler
```sh
./gradlew installDist
./build/install/CCL/bin/CCL <source> [destination] [-p]
```
- If `destination` is omitted, the output is stdout.
- If `-p` is passed, the program will attempt to prune unused variables and functions, which I have not tested thoroughly enough to make a guarantee of correctness on all input.
