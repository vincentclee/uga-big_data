/*
 * Copyright (c) 2014, UGA Computer Science
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * File Operations
 * 
 * @author Will Henry <henryw14@uga.edu>
 * @author Vincent Lee <vlee@cs.uga.edu>
 * @since April 24, 2014
 * @version 1.0
 */

import java.io.IOException;
import java.util.*;
import java.io.DataInput;
import java.io.DataOutput;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class CompositeWriteable implements Writable {
	String in = "";
	String out = "";
	
	public CompositeWriteable() {}
	
	public CompositeWriteable(String in, String out) {
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException {
		in = WritableUtils.readString(input);
		out = WritableUtils.readString(input);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		WritableUtils.writeString(output, in);
		WritableUtils.writeString(output, out);
	}
	
	@Override
	public String toString() {
		return this.out + " " + this.in;
	}
	
	public boolean equals(CompositeWriteable o) {
		if (in.equals(o.in) && out.equals(o.out))
			return true;
		else
			return false;
	}
}