package edu.asu.diging.gilesecosystem.util.service;

public interface ISystemMessageHandler {

    public abstract void handleError(String msg, Exception exception);

}
