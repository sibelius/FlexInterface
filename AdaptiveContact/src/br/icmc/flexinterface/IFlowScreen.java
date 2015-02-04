package br.icmc.flexinterface;

import OpenCOM.IUnknown;

/**
 * Interface IFlowScreen
 * @author Sibelius Seraphini
 */
public interface IFlowScreen extends IUnknown {
	/**
	 * Define a tela atual
	 * @param screen
	 */
	public void setCurrentScreen(String screen);
	
	public void setCurrentScreen(int screen);
	
	/**
	 * Retorna a tela atual
	 * @return currentScreen
	 */
	public String getCurrentScreen();
	
	/**
	 * Retorna a próxima tela e a torna a tela atual
	 * @return nextScreen
	 */
	public String nextScreen();
	
	/**
	 * Retorna a tela anterior e a torna a tela atual
	 * @return
	 */
	public String previousScreen();
}
