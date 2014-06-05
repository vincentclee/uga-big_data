/*
* Copyright 1993-2014 NVIDIA Corporation.  All rights reserved.
*
* Please refer to the NVIDIA end user license agreement (EULA) associated
* with this source code for terms and conditions that govern your use of
* this software. Any use, reproduction, disclosure, or distribution of
* this software and related documentation outside the terms of the EULA
* is strictly prohibited.
*
*/
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define MAX_DEPTH		16
#define INSERTION_SORT	32

////////////////////////////////////////////////////////////////////////////////
// Selection sort used when depth gets too big or the number of elements drops
// below a threshold.
////////////////////////////////////////////////////////////////////////////////
void selection_sort(unsigned int *data, int left, int right) {
	int i, j;
	for (i = left ; i <= right ; ++i) {
		unsigned min_val = data[i];
		int min_idx = i;
		
		// Find the smallest value in the range [left, right].
		for (j = i+1 ; j <= right ; ++j) {
			unsigned val_j = data[j];
			
			if (val_j < min_val) {
				min_idx = j;
				min_val = val_j;
			}
		}
		
		// Swap the values.
		if (i != min_idx) {
			data[min_idx] = data[i];
			data[i] = min_val;
		}
	}
}

////////////////////////////////////////////////////////////////////////////////
// Very basic quicksort algorithm, recursively launching the next level.
////////////////////////////////////////////////////////////////////////////////
void cdp_simple_quicksort(unsigned int *data, int left, int right, int depth) {
	// If we're too deep or there are few elements left, we use an insertion sort...
	if (depth >= MAX_DEPTH || right-left <= INSERTION_SORT) {
		selection_sort(data, left, right);
		return;
	}
	
	unsigned int *lptr = data+left;
	unsigned int *rptr = data+right;
	unsigned int  pivot = data[(left+right)/2];
	
	// Do the partitioning.
	while (lptr <= rptr) {
		// Find the next left- and right-hand values to swap
		unsigned int lval = *lptr;
		unsigned int rval = *rptr;
		
		// Move the left pointer as long as the pointed element is smaller than the pivot.
		while (lval < pivot) {
			lptr++;
			lval = *lptr;
		}
		
		// Move the right pointer as long as the pointed element is larger than the pivot.
		while (rval > pivot) {
			rptr--;
			rval = *rptr;
		}
		
		// If the swap points are valid, do the swap!
		if (lptr <= rptr) {
			*lptr++ = rval;
			*rptr-- = lval;
		}
	}
	
	// Now the recursive part
	int nright = rptr - data;
	int nleft  = lptr - data;
	
	// Launch a new block to sort the left part.
	if (left < (rptr-data)) {
		cdp_simple_quicksort(data, left, nright, depth+1);
	}
	
	// Launch a new block to sort the right part.
	if ((lptr-data) < right) {
		cdp_simple_quicksort(data, nleft, right, depth+1);
	}
}

////////////////////////////////////////////////////////////////////////////////
// Call the quicksort kernel from the host.
////////////////////////////////////////////////////////////////////////////////
void run_qsort(unsigned int *data, unsigned int nitems) {
	// Launch on device
	int left = 0;
	int right = nitems-1;
	printf("Launching kernel on the CPU\n");
	cdp_simple_quicksort(data, left, right, 0);
}

////////////////////////////////////////////////////////////////////////////////
// Initialize data on the host.
////////////////////////////////////////////////////////////////////////////////
void initialize_data(unsigned int *dst, unsigned int nitems) {
	unsigned i;
	// Fixed seed for illustration
	srand(2047);
	
	// Fill dst with random values
	for (i = 0 ; i < nitems ; i++)
		dst[i] = rand() % nitems;
}

////////////////////////////////////////////////////////////////////////////////
// Verify the results.
////////////////////////////////////////////////////////////////////////////////
void check_results(int n, unsigned int *results_h) {
	int i;
	
	for (i = 1 ; i < n ; ++i)
		if (results_h[i-1] > results_h[i]) {
			printf("Invalid item[%u]: %u greater than %u\n", i-1, results_h[i-1], results_h[i]);
			exit(EXIT_FAILURE);
		}
	
	printf("OK\n");
}

////////////////////////////////////////////////////////////////////////////////
// Main entry point.
////////////////////////////////////////////////////////////////////////////////
int main(int argc, char **argv) {
	int i, verbose = 0;
	unsigned int num_items = 1000000;
	
	// Create input data
	unsigned int *h_data = 0;
	
	// Timers
	clock_t begin, end;
	double seconds = 0;
	
	// Allocate CPU memory and initialize data.
	printf("Initializing data\n");
	h_data =(unsigned int *)malloc(num_items*sizeof(unsigned int));
	initialize_data(h_data, num_items);
	if (verbose) {
		for (i = 0; i < num_items; i++)
			printf("Data [%u]: %u\n", i, h_data[i]);
	}
	
	// Execute
	printf("Running quicksort on %u elements\n", num_items);
	begin = clock();
	run_qsort(h_data, num_items);
	end = clock();
	
	// Check result
	printf("Validating results: ");
	check_results(num_items, h_data);
	if (verbose) {
		for (i=0 ; i<num_items ; i++)
			printf("Data [%u]: %u\n", i, h_data[i]);
	}
	
	// Total elapsed time
	seconds = (double)(end - begin) / CLOCKS_PER_SEC;
	printf("Execute elapsed time: %f s\n", seconds);
	
	free(h_data);
	
	exit(EXIT_SUCCESS);
}
