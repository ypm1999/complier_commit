set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./src/lib/antlr-4.7.2-complete.jar:./bin com.mxcomplier.MidTerm"
#cat > program.txt   # save everything in stdin to program.txt
$CCHK 
#cat out.txt

