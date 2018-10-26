#!/usr/local/bin/fish

for f in data/test-pa2/Tests/valid*txt
  set base (basename $f '.txt')
  echo '=== Testing ' $base '==='
  java -jar Parser.jar $f > /tmp/res
  diff /tmp/res 'data/test-pa2/Results/'$base'.txt'
  echo '========================'
end
