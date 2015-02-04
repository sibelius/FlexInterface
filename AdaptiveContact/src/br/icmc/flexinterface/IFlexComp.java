package br.icmc.flexinterface;

import java.io.File;

import OpenCOM.IUnknown;

public interface IFlexComp extends IUnknown {
	//IFlowScreen
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
	
	//IProfileChecker
	/***
	 * Starts the process of checking interaction data to define the most appropriate user profile
	 * @param interation
	 */
	public void initChecker(File interaction);
	
	/**
	 * Ends the process of checking interaction data
	 */
	public void finishChecker();
	
	/**
	 * This method reads the interaction data and it decides the most appropriate user profile
	 * 
	 * @param patternFile
	 * @return
	 */
	public int checker();
}
