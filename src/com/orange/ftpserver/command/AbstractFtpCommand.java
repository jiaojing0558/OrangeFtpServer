package com.orange.ftpserver.command;

import java.util.Collection;
import java.util.Map;

import com.orange.ftpserver.context.DefaultFtpRequest;
import com.orange.ftpserver.context.IFtpSession;
import com.orange.ftpserver.exception.FtpCommandException;
import com.orange.ftpserver.server.AbstractFtpServerListener;
import com.orange.ftpserver.server.DefaultServerFactory;

public abstract class AbstractFtpCommand extends FtpOnCommand implements IFtpCommand {
	
	protected IFtpSession ftpSession;
	
	protected FtpRequestCommand ftpCommand;
	protected String[] commandParameter;

	protected AbstractFtpCommand(IFtpSession ftpSession,FtpRequestCommand ftpCommand,String[] parameters){
		this.ftpSession = ftpSession;
		this.ftpCommand = ftpCommand;
		this.commandParameter = parameters;
		DefaultFtpRequest ftpRequest = (DefaultFtpRequest)ftpSession.getRequest();
		ftpRequest.setFtpCommand(this);
	}
	
	protected abstract void exec() throws Exception;
	
	@Override
	public IFtpSession getSession(){
		return ftpSession;
	}
	
	@Override
	public FtpRequestCommand getCommand() {
		return ftpCommand;
	}

	@Override
	public String[] getParameters() {
		return commandParameter;
	}
	
	private void beforeCommand(Collection<AbstractFtpServerListener> values) 
			throws FtpCommandException{
		for(AbstractFtpServerListener serverListener : values){
			switch (ftpCommand) {
				case MKD:
					serverListener.beforeMkdir(ftpSession);
					break;
				case RMD:
					serverListener.beforeRmdir(ftpSession);
					break;
				case STOR:
					serverListener.beforeStore(ftpSession);
					break;
				case DELE:
					serverListener.beforeDelete(ftpSession);
					break;
				default:
					break;
			}
		}
	}
	
	private void onCommand() 
			throws FtpCommandException{
		switch (ftpCommand) {
			case OPEN:
				super.onOpen(ftpSession);
				break;
			case MKD:
				super.onMkdir(ftpSession);
				break;
			case RMD:
				super.onRmdir(ftpSession);
				break;
			case STOR:
				super.onStore(ftpSession);
				break;
			case DELE:
				super.onDelete(ftpSession);
				break;
			default:
				break;
		}
	}
	
	private void afterCommand(Collection<AbstractFtpServerListener> values) 
			throws FtpCommandException{
		for(AbstractFtpServerListener serverListener : values){
			switch (ftpCommand) {
				case MKD:
					serverListener.afterMkdir(ftpSession);
					break;
				case RMD:
					serverListener.afterRmdir(ftpSession);
					break;
				case STOR:
					serverListener.afterStore(ftpSession);
					break;
				case DELE:
					serverListener.afterDelete(ftpSession);
					break;
				default:
					break;
			}
		}
	}
	
	public void executCommand() throws FtpCommandException{
		Map<String,AbstractFtpServerListener> listenerMap = DefaultServerFactory.getFtpContext().getListenerMap();
		Collection<AbstractFtpServerListener> values = listenerMap.values();
		super.beforeCommond(ftpSession);
		beforeCommand(values);
		onCommand();
		afterCommand(values);
		super.afterCommond(ftpSession);
	}
}
