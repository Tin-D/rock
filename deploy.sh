#!/usr/bin/env bash

mvn -U clean compile install -P product
cp target/rock*.jar /usr/share/jy/

echo 'restart ...'
supervisorctl restart rock

echo 'ok!'

supervisorctl tail -f rock
