cat /tmp/samekeyword.lower.txt | java -jar target/jieba-analysis-1.0.1_1.0.3-SNAPSHOT-jar-with-dependencies.jar  | sed 's/ ,/,/g' >  /tmp/samekeyword.lower.seg.txt
