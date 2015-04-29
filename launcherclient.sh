#!/bin/bash


# Change this to your netid
netid=sxs137732
echo $netid

#
# Root directory of your project
PROJDIR=$tcp

#
# This assumes your config file is named "config.txt"
# and is located in your project directory
#
CONFIG=tcp/config2.txt

#
# Directory your java classes are in
#
BINDIR=$PROJDIR/bin

#
# Your main project class
#
PROG=tcpclient

n=1

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*edu$/d" |
(
    read i
    echo $i
    n = 0
    while read line 
    do
        host=$( echo $line | awk '{ print $1 }' )
        port=$( echo $line | awk '{ print $2 }' )
        echo $host
        echo $port
        ssh $netid@$host java $PROG $port &

        n=$(( n + 1 ))
    done
   
)
