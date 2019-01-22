#!/usr/local/bin/fish

for f in data/test-pa1/valid*core
  set base (basename $f '.core')
  echo '=== Testing ' $base '==='
  java -jar Tokenizer.jar $f > /tmp/res
  diff /tmp/res 'data/test-pa1/results/'$base'.txt'
  echo '========================'
end
