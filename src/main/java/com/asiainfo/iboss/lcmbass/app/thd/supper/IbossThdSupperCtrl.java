/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.thd.supper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassThd;
import com.asiainfo.iboss.lcmbass.app.thd.utils.IbossThdUtils;
import com.asiainfo.iboss.lcmbass.app.utils.IbossSpringUtils;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.ProducerType;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年5月7日 下午6:37:35
 */
@Slf4j
public abstract class IbossThdSupperCtrl<T> implements WorkHandler<T>  {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private BeanFactory beanFactory;
	
	@Getter
	@Setter
	protected CfgCbassThd tdMThdCfg;
	
	@Getter
	@Setter
	protected String thdName;
	
	private RingBuffer<T> ringBuffer;
	
	private SequenceBarrier barriers;
	
	/**
	 * 消费者线程
	 * */
	private ExecutorService consumerExecutor =null;
	
	/***
	 * 消费线程管理线程
	 * */
	private WorkerPool<T> workerPool=null;
	
	
	/**
	 * 生产者线程 
	 * */
	private ExecutorService producerExecutor =null;
	
	/**
	 * 生产者管理线程
	 * */
	private ExecutorService producerManagerExecutor=null;
	
	/**
	 * 是否关闭生产者线程，用于控制生产者线程的退出
	 * false:不关闭
	 * true:关闭
	 * */
	private boolean isProducerShuttdown=false;
		
	/**
	 * 关闭线程
	 * */
	public synchronized void stopThd() {
		isProducerShuttdown=true;
		if(producerExecutor !=null) {
			producerExecutor.shutdown();
			while(!producerExecutor.isTerminated()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if(producerManagerExecutor !=null) {
			producerManagerExecutor.shutdown();
			while(!producerManagerExecutor.isTerminated()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if(workerPool!=null) {
			workerPool.halt();
		}
		if(consumerExecutor!=null) {
			consumerExecutor.shutdown();
			while(!consumerExecutor.isTerminated()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	/**
	 * 启动线程
	 * @throws Exception 
	 * */
	public synchronized void startThd() throws Exception {
		startConsumer();
		startProducer();
		
	}

	/**
	 * 启动消费线程
	 * @throws Exception 
	 * */
	@SuppressWarnings("unchecked")
	private void startConsumer() throws Exception {
		if(tdMThdCfg==null) {
			log.error("没有线程名={}的线程配置，请检查TD_M_THD_CFG表相关配置。线程启动失败！",thdName);
			throw new Exception("没有线程名="+thdName+"的线程配置，请检查TD_M_THD_CFG表相关配置。线程启动失败！");
		}
		String thdConsumerClass = tdMThdCfg.getThdConsumerClass();
		String thdExceptionHandlerClass = tdMThdCfg.getThdExceptionHandlerClass();
		Integer bufferSize = tdMThdCfg.getBufferSize();
		String thdDtoClass = tdMThdCfg.getThdDtoClass();
		String waitStrategyName = tdMThdCfg.getWaitStrategy();
		Integer thdConsumerSize = tdMThdCfg.getThdConsumerSize();
		Integer thdConsumerMaxPoolSize = tdMThdCfg.getThdConsumerMaxPoolSize();
		if(workerPool!=null && workerPool.isRunning()) {
			throw new Exception("消费者管理线程已经启动，不允许重复启动，请先关闭消费者管理线程");
		}
		
		if(consumerExecutor!=null && !consumerExecutor.isTerminated()) {
			throw new Exception("消费者线程已经启动，不允许重复启动，请先关闭线程！");
		}
		consumerExecutor =  Executors.newFixedThreadPool(thdConsumerMaxPoolSize);
				
		WaitStrategy waitStrategy = IbossThdUtils.getWaitStrategyByName(waitStrategyName);
		EventFactory<?> eventFactory = IbossThdUtils.getEventFactoryNoException(thdDtoClass);
	
		IbossThdSupperConsumer<T> [] consumers=new IbossThdSupperConsumer[thdConsumerSize];
		for(int i=0;i<thdConsumerSize;i++) {
			consumers[i]=IbossSpringUtils.getBeanByClassFullName(thdConsumerClass,beanFactory);
			consumers[i].setTdMThdCfg(tdMThdCfg);
		}
		
		IbossThdSupperExceptionHandler<T> exceptionHandler=IbossSpringUtils.getBeanByTypeAndName(thdExceptionHandlerClass, IbossThdSupperExceptionHandler.class, applicationContext);
		
		if(ringBuffer==null) {
			ringBuffer=(RingBuffer<T>) RingBuffer.create(ProducerType.MULTI,eventFactory,  bufferSize, waitStrategy);	
		}
		if(barriers==null) {
			barriers = ringBuffer.newBarrier();
		}
		if(workerPool==null) {
			workerPool = new WorkerPool<T>(ringBuffer, barriers,exceptionHandler,consumers);
		}
		ringBuffer.addGatingSequences(workerPool.getWorkerSequences()); 
		workerPool.start(consumerExecutor);  
		
	}
	
	/**
	 * 启动生产线程
	 * @throws Exception 
	 * */
	private void startProducer() throws Exception {
		if(tdMThdCfg==null) {
			log.error("没有线程名={}的线程配置，请检查TD_M_THD_CFG表相关配置。线程启动失败！",thdName);
			return;
		}
		
		Integer thdProducerSize = tdMThdCfg.getThdProducerSize();
		Integer thdProducerMaxPoolSize = tdMThdCfg.getThdProducerMaxPoolSize();
		Integer thdProducerSleepTime = tdMThdCfg.getThdProducerSleepTime();
		String thdProducerClass = tdMThdCfg.getThdProducerClass();
		if(thdProducerSize<1 || thdProducerMaxPoolSize<1 || StringUtils.isBlank(thdProducerClass)) {
			log.info("线程name={}不需要创建生产者线程！跳过！",thdName);
			return;
		}
		
		if(producerExecutor!=null && !producerExecutor.isTerminated()) {
			throw new Exception("生产者线程已经启动，不允许重复启动，请先关闭线程！");
		}
		producerExecutor = Executors.newFixedThreadPool(thdProducerMaxPoolSize);
		
		
		IbossThdSupperProducer[] producers=new IbossThdSupperProducer[thdProducerSize];
		for(int i=0;i<thdProducerSize;i++) {
			producers[i]=IbossSpringUtils.getBeanByClassFullName(thdProducerClass,beanFactory);
			producers[i].setTdMThdCfg(tdMThdCfg);
		}
		
		
		Runnable producerManagerThd=new Runnable() {
			@Override
			public void run() {
				while(!isProducerShuttdown) {
					try {
						for(int i=0;i<thdProducerSize;i++) {
							producerExecutor.submit(producers[i]);
						}
					} catch (Exception e) {
						log.error("生产者线程name={}，创建出错！{}",thdName,e.getMessage());
					}
					//休眠
					try {
						Thread.sleep(thdProducerSleepTime);
					} catch (InterruptedException e) {
						log.error("生产者线程name={}，休眠出错：",thdName);
					}
				}
			}	
		};
		if(producerManagerExecutor!=null && !producerManagerExecutor.isTerminated()) {
			log.warn("管理线程已经启动，不允许重复启动");
			throw new Exception("管理线程已经启动，不允许重复启动！");
		}
		producerManagerExecutor=Executors.newSingleThreadExecutor();
		isProducerShuttdown=false;
		producerManagerExecutor.submit(producerManagerThd);
	}

	//每调用一次，生产一条数据
	@Override
	public void onEvent(T event){
		//可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
		long sequence = ringBuffer.next();
		try {
			//用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
			T order = ringBuffer.get(sequence);
			//获取要通过事件传递的业务数据
			BeanUtils.copyProperties(order, event);
		}catch (Exception e) {
			log.warn("ringBuffer数据转换出错！{}",e.getMessage());
		} finally {
			//发布事件
			//注意，最后的 ringBuffer.publish 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
			ringBuffer.publish(sequence);
		}
	}
}
