#!/usr/bin/bash

for f in `ls data/valid*core`
do
  base=`basename $f '.core'`
  echo '=== Testing ' $base '==='
  java -jar Tokenizer.jar $f > /tmp/res
  diff /tmp/res 'data/results/'$base'.txt'
  echo '========================'
done
