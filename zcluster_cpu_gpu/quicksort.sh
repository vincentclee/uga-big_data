#!/bin/bash
NOW=$(date +"%Y%m%d%H%M")

cd quicksort
export PATH=/usr/local/cuda/5.0.35/cuda/bin:${PATH}
export LD_LIBRARY_PATH=/usr/local/cuda/5.0.35/lib64:${LD_LIBRARY_PATH}
./quicksort > output_quicksort-$NOW
