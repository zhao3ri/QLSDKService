package com.qinglan.sdk.server.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * timestamp | node |increment 
 */
public class IdWorker {
	private final static Logger logger = LoggerFactory.getLogger(IdWorker.class);
	private final long workerId;
	private final static long twepoch = 1361753741828L;
	private long sequence = 0L;
	private final static long workerIdBits = 4L;
	public final static long maxWorkerId = -1L ^ -1L << workerIdBits; //1111(2)=15
	private final static long sequenceBits = 10L;

	private final static long workerIdShift = sequenceBits;
	private final static long timestampLeftShift = sequenceBits + workerIdBits; //14
	public final static long sequenceMask = -1L ^ -1L << sequenceBits; //1111111111(2)=1023
	
	private long lastTimestamp = -1L;
	
	public final static IdWorker INSTANCE = new IdWorker(1);

	private IdWorker(final long workerId) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		this.workerId = workerId;
	}
	
	public synchronized long nextId() {
		long timestamp = this.timeGen();
		if (this.lastTimestamp == timestamp) {
			this.sequence = (this.sequence + 1) & sequenceMask;
			if (this.sequence == 0) { timestamp = this.tilNextMillis(this.lastTimestamp); }
		} else { this.sequence = 0;}
		
		if (timestamp < this.lastTimestamp) {
			logger.error(String.format("Clock moved backwards.Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
		    throw new RuntimeException(String.format("Clock moved backwards.Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
		}

		this.lastTimestamp = timestamp;
		return ((timestamp - twepoch << timestampLeftShift)) | (this.workerId << workerIdShift) | (this.sequence);
	}

	private long tilNextMillis(final long lastTimestamp) {
		long timestamp = this.timeGen();
		while (timestamp <= lastTimestamp) { timestamp = this.timeGen(); }
		return timestamp;
	}

	private long timeGen() {
		return System.currentTimeMillis();
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 3 ; i++) {
			System.out.println(IdWorker.INSTANCE.nextId());
		}
	}
}
