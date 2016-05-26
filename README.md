## Vigenere Cracker

A very basic vigenere cipher cracker using [Kasiski Examination](https://en.wikipedia.org/wiki/Kasiski_examination) and Frequency Analysis 

## Usage

1. Clone the repository
2. cd `Vigenere Cracker`
3. `javac VigenereCracker.java`
4. `java VigenereCracker --inFile [input_file] --outFile [output_file]`
5. The program parses the cipher text present in the input file and writes the message to output file.

## Example

The following cipher is taken from [here](http://www.isical.ac.in/~rcbose/internship/problem1.txt)

`java VigenereCracker --inFile problem1.txt --outFile output.txt`

````
Most probable key length: 6
Guessed Key by Frequency Analysis: RANDOM
Output saved to output.txt
````
