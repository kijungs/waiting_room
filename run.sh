# Program : run.sh
# Description : Run the WRS_INS & WRS_DEL algorithm

java -cp ./WRS-2.0.jar:./fastutil-7.2.0.jar wrs.Batch $@
