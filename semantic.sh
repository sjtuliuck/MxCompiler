set -e
cd "$(dirname "$0")"
export CCHK="java -classpath ./lib/antlr-4.8-complete.jar:./bin Main"
cat > test.mx   # save everything in stdin to program.txt
${CCHK}