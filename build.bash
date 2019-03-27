set -e
cd "$(dirname "$0")"
mkdir -p bin
find ./src/src -name *.java | javac -d bin -classpath "src/lib/antlr-4.7.2-complete.jar" @/dev/stdin
