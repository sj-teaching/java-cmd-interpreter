#!/usr/local/bin/fish

# for f in data/test-pa1/Tests/valid*core
#   set base (basename $f '.core')
#   echo '=== Testing ' $base '==='
#   java -jar Tokenizer.jar $f > /tmp/res
#   diff /tmp/res 'data/test-pa1/Results/'$base'.txt'
#   echo '========================'
# end

# for f in data/test-pa2/Tests/valid*core
#   set base (basename $f '.core')
#   echo '=== Testing ' $base '==='
#   java -jar Core.jar -p $f > /tmp/res
#   diff /tmp/res 'data/test-pa2/Results/'$base'.txt'
#   echo '========================'
# end

for f in data/test-pa3/*.txt
    set base (basename $f '.txt')
    echo '=== Testing ' $base '==='
    java -jar Core.jar -i $f
    echo '========================'
end
