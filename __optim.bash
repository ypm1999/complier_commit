set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./lib/antlr-4.7.2-complete.jar:./bin com.mxcomplier.Main"
#cat > program.txt   # save everything in stdin to program.txt
$CCHK 
#cat out.txt

