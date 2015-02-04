package br.icmc.flexinterface.elderly;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import OpenCOM.ILifeCycle;
import OpenCOM.IMetaInterface;
import OpenCOM.IUnknown;
import OpenCOM.OpenCOMComponent;
import android.util.Log;
import br.icmc.flexinterface.IProfileChecker;

public class ElderlyProfileChecker extends OpenCOMComponent 
	implements IProfileChecker, ILifeCycle, IMetaInterface, IUnknown {

	public static final int LOW_EDUCATION_PROFILE = 0;
	public static final int HIGH_EDUCATION_PROFILE = 1;
	
	public static final int ESTADO_CORRETO = 0;
	public static final int ESTADO_ERRADO = 1;
	
	public static final String FILE_INTERACTION = "interaction.txt";
	public static final String FILE_ENTROPY = "entropy.txt";
	public static final String FILE_DELTAENTROPY = "delta.txt";
	
	private File mInteraction;
	private File mEntropy;
	private File mDeltaEntropy;
	
	private ArrayList<Double> last_entropies = new ArrayList<Double>();
	
	private boolean once = false;
	
	public ElderlyProfileChecker(IUnknown mpIOCM) {
		super(mpIOCM);
	}

	//ILifeCycle
	public boolean startup(Object data) {
		return true;
	}


	public boolean shutdown() {
		return true;
	}

	// IProfileChecker
	public void initChecker(File dir) {
		mInteraction = new File(dir, FILE_INTERACTION);
		
		/*
		mEntropy = new File(dir, FILE_ENTROPY);
		mDeltaEntropy = new File(dir, FILE_DELTAENTROPY);
		*/
		/*
		//Erase the previous interaction data
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(mInteraction, false));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}

	public void finishChecker() {
	}
	
	/*
	 * Cadeia de Markov contando
	@Override
	public int checker() {
		int c;
		int estado_anterior=-1;
		double markov[][] = new double[2][2];
		double trans[][] = new double[2][2];
		double p[] = new double[2];
		double sum[] = new double[2];
		int i=0;
		
		p[ESTADO_CORRETO] = 1;
		p[ESTADO_ERRADO] = 1;
		
		ArrayList<Double> newEntropies = new ArrayList<Double>();
		ArrayList<Double> probRight  = new ArrayList<Double>();
		
		try {
			BufferedReader brInteraction = new BufferedReader(new FileReader(mInteraction));
			BufferedWriter bwEntropy = new BufferedWriter(new FileWriter(mEntropy, true));
			//DataOutputStream bwEntropy = new DataOutputStream(new FileOutputStream(mEntropy, true));
			BufferedWriter bwDeltaEntropy = new BufferedWriter(new FileWriter(mDeltaEntropy, true));
			//DataOutputStream bwDeltaEntropy = new DataOutputStream(new FileOutputStream(mDeltaEntropy, true));
			
			while( (c = brInteraction.read()) != -1) {
				if(c != 65531) { //N�o � backspace - estado 0
					if(estado_anterior == -1) {
						estado_anterior = ESTADO_CORRETO;
						p[ESTADO_CORRETO] = 1.0;
						p[ESTADO_ERRADO] = 0;
						continue;
					}
					
					if(estado_anterior == ESTADO_CORRETO)
						markov[ESTADO_CORRETO][ESTADO_CORRETO] += 1;
					else {
						markov[ESTADO_ERRADO][ESTADO_CORRETO] += 1;
						estado_anterior = ESTADO_CORRETO;
					}
				} else { // erro - estado 1
					if(estado_anterior == -1) {
						estado_anterior = ESTADO_ERRADO;
						p[ESTADO_CORRETO] = 0;
						p[ESTADO_ERRADO] = 1.0;
						continue;
					}
					
					if(estado_anterior == ESTADO_CORRETO) {
						markov[ESTADO_CORRETO][ESTADO_ERRADO] += 1;
						estado_anterior = ESTADO_ERRADO;
					} else {
						markov[ESTADO_ERRADO][ESTADO_ERRADO] += 1;
					}
				}
				
				sum[0] = markov[0][0] + markov[0][1];
				sum[1] = markov[1][0] + markov[1][1];
				
				if(sum[0] == 0)
					sum[0] = 1;
				if(sum[1] == 0)
					sum[1] = 1;
		
				for(int j=0; j<2; j++)
					for(int k=0; k<2; k++)
						trans[j][k] = markov[j][k] / sum[j];
				Log.e("Markov Prob", String.valueOf(i));
				for(int j=0; j<2; j++)
					for(int k=0; k<2; k++)
						Log.e(j + "," + k, String.valueOf(markov[j][k] / sum[j] ));
				
				double entropy = 0;
				double aux;
				for(int j=0; j<2; j++) {
					aux = 0;
					for(int k=0; k<2; k++)
						if(trans[j][k] != 0)
							aux += trans[j][k] * (Math.log(trans[j][k]) / Math.log(2));
					
					entropy -= p[j] * aux;
				}
				
				newEntropies.add(entropy);
				
				aux = p[ESTADO_CORRETO];
				if( (trans[0][0] != 0.0) || (trans[1][0] != 0.0))
					p[ESTADO_CORRETO] = p[ESTADO_CORRETO]*trans[ESTADO_CORRETO][ESTADO_CORRETO] + 
										p[ESTADO_ERRADO]*trans[ESTADO_ERRADO][ESTADO_CORRETO];
				
				if( (trans[0][1] != 0.0) || (trans[1][1] != 0.0))
					p[ESTADO_ERRADO] = aux*trans[ESTADO_CORRETO][ESTADO_ERRADO] + 
										p[ESTADO_ERRADO] * trans[ESTADO_ERRADO][ESTADO_ERRADO];
				
				probRight.add(p[ESTADO_CORRETO]);
				
				//Log.e("p[ESTADO_CORRETO]", String.valueOf(p[0]));
				//Log.e("p[ESTADO_ERRADO]", String.valueOf(p[1]));
			}
			brInteraction.close();
			
			//Novos dados de intera��o foram adicionados
			if(last_entropies.size() != newEntropies.size()) {
				for(int j=last_entropies.size(); j<newEntropies.size(); j++) {
					//bwEntropy.write(newEntropies.get(j).toString());
					//bwEntropy.newLine();
					//bwEntropy.writeDouble(newEntropies.get(j));
					bwEntropy.write(newEntropies.get(j) + ",");
					
					Log.e("Entropy", newEntropies.get(j).toString());
				}
				last_entropies = newEntropies;
			}
			if(once == false) {
				for(int j=1; j<last_entropies.size(); j++) {
					double delta = last_entropies.get(j-1) - last_entropies.get(j);
					Log.e("Delta", String.valueOf(delta));
					bwDeltaEntropy.write(delta + ",");
					//bwDeltaEntropy.writeDouble(last_entropies.get(j-1) - last_entropies.get(j));
				}
				once = true;
			}
			bwEntropy.close();
			bwDeltaEntropy.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		
		return LOW_EDUCATION_PROFILE;
	}
	*/
	
	// Cadeia de Markov acrescimo
	public int checker() {
		int c;
		int estado_anterior=-1;
		int estado_atual;
		double trans[][] = new double[2][2];
		double p[] = new double[2];
		int i=0;
		double delta = 0.5;
		
		ArrayList<Double> newEntropies = new ArrayList<Double>();
		ArrayList<Double> probRight  = new ArrayList<Double>();
		/*
		try {
			BufferedReader brInteraction = new BufferedReader(new FileReader(mInteraction));
			BufferedWriter bwEntropy = new BufferedWriter(new FileWriter(mEntropy, true));
			//DataOutputStream bwEntropy = new DataOutputStream(new FileOutputStream(mEntropy, true));
			BufferedWriter bwDeltaEntropy = new BufferedWriter(new FileWriter(mDeltaEntropy, true));
			//DataOutputStream bwDeltaEntropy = new DataOutputStream(new FileOutputStream(mDeltaEntropy, true));
			
			while( (c = brInteraction.read()) != -1) {
				if(c != 65531)
					estado_atual = ESTADO_CORRETO;
				else
					estado_atual = ESTADO_ERRADO;
				
				bwDeltaEntropy.write(String.valueOf(estado_atual));
				
				if(estado_anterior == -1) {
					estado_anterior = estado_atual;
					continue;
				}
				
				if(trans[estado_anterior][estado_atual] == -1) {
					trans[estado_anterior][estado_atual] = 1;
					trans[estado_anterior][(estado_atual + 1) % 2] = 0;
				} else {
					if(trans[estado_anterior][estado_atual] != 1) {
						trans[estado_anterior][estado_atual] += delta;
						trans[estado_anterior][(estado_atual + 1) % 2] -= delta;
						/*
						for(int j=0; j<2; j++)
							for(int k=0; k<2; k++)
								if((j == estado_anterior) && (k == estado_atual) )
									trans[j][k] = trans[j][k] + delta;
								else
									if( (j == estado_anterior) && (k != estado_atual) )
										trans[j][k] = (1-delta) * trans[j][k];
										*/
						/*
						trans[estado_anterior][estado_atual] += delta;
						trans[estado_anterior][(estado_atual + 1) % 2] -= delta;
						*//*
					}
				}
					
				if(estado_anterior != estado_atual)
					estado_anterior = estado_atual;
					
				double entropy = 0;
				for(int j=0; j<2; j++) {
					for(int k=0; k<2; k++)
						if(trans[j][k] > 0.0)
							entropy -= trans[j][k] * (Math.log(trans[j][k]) / Math.log(2));
				}
				
				newEntropies.add(entropy);
			}
			brInteraction.close();
			
			//Novos dados de intera��o foram adicionados
			if(last_entropies.size() != newEntropies.size()) {
				for(int j=last_entropies.size(); j<newEntropies.size(); j++) {
					//bwEntropy.write(newEntropies.get(j).toString());
					//bwEntropy.newLine();
					//bwEntropy.writeDouble(newEntropies.get(j));
					bwEntropy.write(newEntropies.get(j) + ",");
					
					Log.e("Entropy", newEntropies.get(j).toString());
				}
				last_entropies = newEntropies;
			}
			/*
			if(once == false) {
				for(int j=1; j<last_entropies.size(); j++) {
					double delta = last_entropies.get(j-1) - last_entropies.get(j);
					Log.e("Delta", String.valueOf(delta));
					bwDeltaEntropy.write(delta + ",");
					//bwDeltaEntropy.writeDouble(last_entropies.get(j-1) - last_entropies.get(j));
				}
				once = true;
			}
				*//*
			bwEntropy.close();
			bwDeltaEntropy.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		*/
		return LOW_EDUCATION_PROFILE;
	}
}
