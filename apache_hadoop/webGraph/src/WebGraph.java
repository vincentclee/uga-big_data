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
 * Part 1
 * 
 * @author Will Henry <henryw14@uga.edu>
 * @author Vincent Lee <vlee@cs.uga.edu>
 * @since April 24, 2014
 * @version 1.0
 */

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class WebGraph {
	public static class Map extends Mapper<LongWritable, Text, Text, CompositeWriteable> {
		private final static IntWritable one = new IntWritable(1);
		private Text in = new Text();
		private Text out = new Text();
		
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line, " ");
			
			out = new Text(tokenizer.nextToken());
			in = new Text(tokenizer.nextToken());
			//must keep track of where to/from so as not to duplicate count
			context.write(out, new CompositeWriteable(in.toString(), "out"));
			context.write(in,new CompositeWriteable(out.toString(), "in")); 
		}
	}
	
	public static class Reduce extends Reducer<Text, CompositeWriteable, Text, CompositeWriteable> {
		public void reduce(Text key, Iterable<CompositeWriteable> values, Context context) throws IOException, InterruptedException {
			HashMap<String,String> seen = new HashMap<String,String>();
			int inSum = 0;
			int outSum = 0;
			String degree =null;
			System.out.println("key: " + key);
			for (CompositeWriteable val : values) {
				degree = seen.get(val.in+val.out);
				if (degree == null) {
					seen.put(val.in + val.out, val.out);
					if (val.out.equals("out"))
						outSum += 1;
					else
						inSum += 1;
				} else if (!degree.equals(val.out)) {
					if (val.out.equals("out"))
						outSum += 1;
					else
						inSum += 1;
					seen.put(val.in+val.out, val.out);
				}
			}
			context.write(key, new CompositeWriteable(Integer.toString(inSum),Integer.toString(outSum)));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = new Job(conf, "Part1: in-degree and out-degree");
		job.setJarByClass(WebGraph.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(CompositeWriteable.class);
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.waitForCompletion(true);
	}
}
