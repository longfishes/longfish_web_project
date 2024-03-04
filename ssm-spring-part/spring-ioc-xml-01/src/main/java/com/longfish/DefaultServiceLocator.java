package com.longfish;

public class DefaultServiceLocator {

  private static final ClientServiceImpl clientService = new ClientServiceImpl();

  public ClientServiceImpl createClientServiceInstance() {
    return clientService;
  }
}
