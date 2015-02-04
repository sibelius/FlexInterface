package br.icmc.flexinterface;

import java.io.File;

import OpenCOM.IUnknown;

/**
 * IProfileChecker - This interface is responsible for dealing with the interation data and decide the most appropriate 
 * user profile
 * 
 * @author Sibelius Seraphini
 */
public interface IProfileChecker extends IUnknown {
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
