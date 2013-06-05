package org.waag.ah.zeromq;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.jeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ZMQContext {
	final static Logger logger = LoggerFactory.getLogger(ZMQContext.class);
	private ZMQ.Context zmq;
	
	@PostConstruct
	private void create() {
		zmq = ZMQ.context(1);
	}
	
	@PreDestroy
	private void close() {
		zmq.term();
	}
	
	public final ZMQ.Socket getSocket(int type) {
		return zmq.socket(type);
	}
}
