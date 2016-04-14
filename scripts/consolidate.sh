#!/usr/bin/env bash

mkdir unified

while read line; do

	execute="cat "

	for (( i = 1; i <= 8; i++ )); do
		if [[ -r $1/$i/$line ]]; then
		execute="$execute $1/$i/$line"
		fi
	done

	execute="$execute > unified/$line"	

	#echo "$execute"
	bash -c "$execute"
done < <(find $1 -type f -printf "%f\n" | sort -u)
