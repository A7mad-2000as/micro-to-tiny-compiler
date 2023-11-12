# MICRO-TINY Compiler

## Description
This repository contains a compiler which translates MICRO high-level language code to TINY low-level language code. The compiler is written in Java and employs ANTLR for lexical analysis generation.

## Usage
The repository currently does not use a build system, therefore the only way to build the code is to manually add the ANTLR jar files. All relevant instructions can be found [here](https://www.antlr.org/).  

The program takes a single command line argument specifying the path of the MICRO source file, it writes the resulting TINY code to the standard output.  

The repoository contains a C source file `tinyNew.c` and a corresponding built file `tiny.exe` which represents a simulator for executing TINY code to verify the correctness of the compiled program. the `tiny.exe` file takes a file path which corresponds to a TINY code as a command line arguemnt.  

Sample input and output files are also avaiable in the `input` and `output` directories for manual verification.

