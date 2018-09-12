#!/usr/local/bin/fish

for f in data/valid*core
  set base (basename $f '.core')
  echo '=== Testing ' $base '==='
  java -jar Tokenizer.jar $f > /tmp/res
  diff /tmp/res 'data/results/'$base'.txt'
  echo '========================'
end
