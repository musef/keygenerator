package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;


public class KeyGenerator implements Runnable {

	private static KeyGenerator keyg;
	
	// semilla de generacion de clave (entera): entre 3 y 6
	private static String seed;
	// cadena de caracteres para generar las claves aleatorias del
	// diccionario: minimo 10 y maximo 50
	private static String chain;
	// longitud de las claves generadas : entre 4 y 12
	private static int keylength;
	// nombre del fichero
	private static String filename;
	// raiz del nombre del fichero
	private static String rootFile;
	// tamaño del fichero
	private static long filesize;
	// mensajes de la aplicacion
	private static String messages;
	// confirmación de la operacion de generar ficheros
	private static boolean response;
	
	private JFrame window;
	
	// diccionarios
	
	// de uso en la aplicacion
	private static String[]dic;
	// ingles
	private static String[] dicEN = {
			"It is recommended previously to estimate the generated files by clicking on Estimation. "
					+ System.lineSeparator()+"Also, consider that the execution time can be quite high "
							+ "and will increase during the generation of the files.",  
			"Estimation", 
			"Generate files",
			"Press to estimate the generation result",
			"Press to start generating the wordlist",
			"Wordlist generates file(s) in text format with the all the generated keys with a selected length, "
					+ "by permutation with repetition of the characters included in the chain.",
			"Seed",
			"key length",
			"file size (bytes)",
			"Chain to process",
			"REQUIRED: between 3 and 6 characters in length",
			"REQUIRED: between 4 and 12 characters in length, and at least one more than the size of the seed",
			"REQUIRED: file size must be greater than 10000 bytes",
			"REQUIRED: between 10 and 100 characters in length",
			"Warning",
			"Are you sure you want to start the "+ System.lineSeparator()+"key generation process?"+ System.lineSeparator()+
            "This process could last a long time "+System.lineSeparator()+"and even block your computer.",
            "- Seed size out of bounds (between 3 and 6 characters)",
            "- Key length out of bounds (between 4 and 12 characters) or is not greater than the seed",
            "- The key generation chain out of bounds (between 10 and 100 characters)",
            "- File size out of bounds (must be greater than 10000 bytes)",
            "An error occurred while recording the dictionary:",
	        "File generation starting: ",
	        "File generation finished: ",
	        "File generation stopped by the user.",
	        "WARNING: too many files could blocked your operating system.",
	        "This process will generate keys for a total of ", 
	        " that will be stored in ",
	        " files of length aprox. of ",
	        "English",
	        "Español",
			};
	// español
	private static String[] dicES = {
			"Se recomienda previamente realizar la estimación de los ficheros generados pulsando en Estimación. "
					+ System.lineSeparator()+"Valore también que el tiempo de ejecución puede ser bastante elevado y se incrementará"
							+ " durante la generación de los ficheros.", 
			"Estimación", 
			"Generar ficheros",
			"Pulse para estimar el resultado de la generación",
			"Pulse para comenzar a generar el wordlist",
			"Wordlist genera fichero/s en formato texto con todas las claves generadas con una longitud deseada, "
					+ "mediante la permutación con repetición de los caracteres incluídos en una cadena.",
			"Semilla",
			"Longitud de claves",
			"Tamaño del fichero en bytes",
			"Cadena a procesar",
			"OBLIGATORIO: entre 3 y 6 caracteres de longitud",
			"OBLIGATORIO: entre 4 y 12 caracteres de longitud, y al menos uno más que el tamaño de la semilla",
			"OBLIGATORIO: el tamaño del fichero debe ser mayor que 10000 bytes",
			"OBLIGATORIO: entre 10 y 100 caracteres de longitud",
			"Advertencia",
			"¿Seguro que desea iniciar el proceso"+ System.lineSeparator()+"de generación de claves?"+ System.lineSeparator()+
	                "Este proceso podría durar bastante "+System.lineSeparator()+"tiempo e incluso bloquear su ordenador.",
	        "- Longitud de semilla fuera de los límites (entre 3 y 6 caracteres)",
	        "- Longitud de clave fuera de los límites (entre 4 y 12 caracteres) o no es mayor que la semilla",
	        "- La cadena generadora de claves está fuera de los límites (entre 10 y 100 caracteres)",
	        "- El tamaño de fichero está fuera de los límites (debe ser mayor de 10000 bytes)",
	        "Se ha producido un error grabando el diccionario:",
	        "Inicio de generación ficheros: ",
	        "Final de generación ficheros: ",
	        "Generación de ficheros abortada.",
	        "PRECAUCION: Demasiados ficheros pudieran colapsar el sistema.",
	        "Este proceso generará claves por un total de ", 
	        " bytes que serán guardadas en ",
	        " ficheros de longitud aprox. de ",
	        "English",
	        "Español",
			};

	
	
	public static void main(String[] args) {

		// valores por defecto
		dic=dicEN;
		keylength=8; 
		chain="0123456789abcdefghijklmnopqrstuvwxyz&*+-%"; 
		seed="seed";
		rootFile="keygenerator";
		filename=rootFile;
		filesize=2048000;
		response=false;
		messages=dic[0];
		
		
		// arrancamos la aplicación
		keyg=new KeyGenerator();	
		keyg.run();
		
	}

	
	/**
	 * Arrancamos el modo grafico
	 */
	@Override
	public void run() {
		
		createWindow();				

	}
	

	/**
	 * Este método crea el entorno gráfico
	 */
	private void createWindow() {
	
		
		// creamos la ventana principal
		Dimension dim=new Dimension(500, 500);
		window=new JFrame();
		window.setSize(dim);
		window.setMinimumSize(dim);
		window.setMaximumSize(dim);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		
		// titulo de la ventana
		window.setTitle("Wordlist generator");
		// layout
		BorderLayout layout=new BorderLayout();
		layout.setHgap(10);
		window.setLayout(layout);

		// por ultimo, creamos unos margenes laterales en la ventana principal
		window.add(new JLabel("        "),BorderLayout.EAST);
		window.add(new JLabel("        "),BorderLayout.WEST);

		// leyenda al pie de la ventana principal
		window.add(new JLabel("             copyright fmsdevelopment - musef2904@gmail.com"),BorderLayout.SOUTH);
		
		// generamos el panel principal y lo añadimos a la ventana
		JPanel mainPanel=getMainPanel();
		
		window.add(mainPanel,BorderLayout.CENTER);
		
		// creamos el contenedor para el texto explicativo en cabeza
		String text=dic[5];
		JTextPane textpanel=new JTextPane();
		textpanel.setBackground(new java.awt.Color(238,238,238));
		textpanel.setText(text);
		

		JButton esp=new JButton("ES");
		esp.setMinimumSize(new Dimension(35,35));
		esp.setMaximumSize(new Dimension(35,35));
		esp.setMargin(new Insets(0,0,0,0));
		esp.setBackground(Color.ORANGE);
		esp.setForeground(Color.WHITE);
		esp.setToolTipText(dic[29]);
		esp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("ES")) {
					// cambiamos el idioma al español
					window.setVisible(false);		
					// tomamos el diccionario español
					dic=dicES;
					messages=dic[0];
					// cambiamos el texto en North
					textpanel.setText(dic[5]);			
					// cambiamos el panel principal
					window.getContentPane().remove(3);
					JPanel mainPanel=getMainPanel();									
					window.add(mainPanel,3);
					window.setVisible(true);
				}
				
			}
		});

		JButton eng=new JButton("EN");
		eng.setMinimumSize(new Dimension(35,35));
		eng.setMaximumSize(new Dimension(35,35));
		eng.setMargin(new Insets(0,0,0,0));
		eng.setBackground(Color.BLUE);
		eng.setForeground(Color.WHITE);
		eng.setToolTipText(dic[28]);
		eng.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("EN")) {
					// cambiamos el idioma al inglés
					window.setVisible(false);
					mainPanel.setVisible(false);
					// tomamos el diccionario inglés
					dic=dicEN;
					messages=dic[0];
					// cambiamos el texto en North
					textpanel.setText(dic[5]);					
					// cambiamos el panel principal
					window.getContentPane().remove(3);
					JPanel mainPanel=getMainPanel();									
					window.add(mainPanel,3);
					window.setVisible(true);
				}
				
			}
		});
		
		
		// creamos el panel para los botones de seleccion de idioma
		JPanel langpanel=new JPanel();
		langpanel.setMinimumSize(new Dimension(110,50));
		langpanel.setMaximumSize(new Dimension(110,50));
		langpanel.setLayout(new BoxLayout(langpanel, BoxLayout.X_AXIS));
		langpanel.add(esp);
		langpanel.add(new JLabel(" - "));
		langpanel.add(eng);
		langpanel.setVisible(true);
		
		// creamos el panel que agrupa a botones y texto en NORTH
		JPanel panelNorth=new JPanel();
		panelNorth.setLayout(new BoxLayout(panelNorth, BoxLayout.Y_AXIS));
		panelNorth.setMinimumSize(new Dimension(400,75));
		panelNorth.setMaximumSize(new Dimension(400,75));

		// añadimos componentes al panel norte
		panelNorth.add(langpanel);
		panelNorth.add(textpanel);
		
		// añadimos el panel a la ventana
		window.add(panelNorth,BorderLayout.NORTH);
		
		
		// mostramos la ventana principal con todos los componentes
		window.setVisible(true);		
	}
	
	
	/**
	 * Este método muestra la vista del panel principal
	 * @return
	 */
	private JPanel getMainPanel() {
		
		// en el centro habrá un panel principal con el formulario
		JPanel mainPanel=new JPanel();	
		BoxLayout blayout=new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
		mainPanel.setLayout(blayout);
		mainPanel.setAlignmentX(0);
		//mainPanel.setBackground(Color.white);
		mainPanel.setForeground(Color.DARK_GRAY);
		
		// creamos los componentes y los mainPaneles que iran en el mainPanel principal mainPanel
		
		// mainPanel componentes
		JPanel sup=new JPanel();
		GridLayout grid=new GridLayout(2, 2);
		sup.setLayout(grid);
		sup.setMaximumSize(new Dimension(400,70));
		
		sup.add(new JLabel(dic[6]));
		sup.add(new JLabel(dic[7]));
		
		JTextField seedInput=new JTextField();
		seedInput.setForeground(Color.blue);
		seedInput.setMinimumSize(new Dimension(200,35));
		seedInput.setMaximumSize(new Dimension(200,35));
		seedInput.setToolTipText(dic[9]);
		seedInput.setText(seed);	// valor por defecto
		sup.add(seedInput);
			
		JComboBox<String> keyInput=new JComboBox<String>();
		for (int n=4;n<13;n++) {
			keyInput.addItem(String.valueOf(n));			
		}
		keyInput.setSelectedIndex(4);	// valor por defecto
		keyInput.setMinimumSize(new Dimension(200,35));
		keyInput.setMaximumSize(new Dimension(200,35));
		keyInput.setToolTipText(dic[10]);
		keyInput.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				// toma el valor seleccionado
				int selectedvalue=Integer.parseInt(keyInput.getSelectedItem().toString());
				// toma la longitud de la semilla
				int seedLength=seedInput.getText().length();
				// si semilla es igual o mayor que el keylength, lo cambia al maximo valor
				if (selectedvalue<=seedLength) keyInput.setSelectedIndex(8);
				// si es correcto, cambia el static con el nuevo valor
				else keylength=selectedvalue;
				
			}
		});		
		sup.add(keyInput);
	
		// mainPanel componentes2
		JPanel sup2=new JPanel();
		GridLayout grid2=new GridLayout(1, 1);
		sup2.setLayout(grid2);
		sup2.setMaximumSize(new Dimension(400,70));
		
		sup2.add(new JLabel(dic[8]));
		
		JTextField filesizeInput=new JTextField();
		filesizeInput.setForeground(Color.blue);
		filesizeInput.setMinimumSize(new Dimension(200,35));
		filesizeInput.setMaximumSize(new Dimension(200,35));
		filesizeInput.setToolTipText(dic[11]);
		filesizeInput.setText(String.valueOf(filesize));	// valor por defecto
		sup2.add(filesizeInput);
		
		// mainPanel titulo de la cadena
		JPanel chainTitle=new JPanel();
		chainTitle.setLayout(new GridLayout(1,1));
		chainTitle.setMinimumSize(new Dimension(400,35));
		chainTitle.setMaximumSize(new Dimension(400,35));
		chainTitle.add(new JLabel(dic[9]));
		
		// mainPanel de cadena
		JTextField chainInput=new JTextField();
		chainInput.setMinimumSize(new Dimension(400,35));
		chainInput.setMaximumSize(new Dimension(400,35));
		chainInput.setToolTipText(dic[12]);
		chainInput.setText(chain);	// valor por defecto
		
		// mainPanel de mensajes
		JTextPane mess=new JTextPane();
		mess.setBackground(new java.awt.Color(238, 238, 238));
		mess.setForeground(Color.RED);
		mess.setMinimumSize(new Dimension(400,100));
		mess.setMaximumSize(new Dimension(400,100));
		mess.setText(messages);
		mess.setVisible(true);
		
		// mainPanel de botones
		JPanel buttons=new JPanel();
		buttons.setLayout(new FlowLayout());
		
		// boton
		JButton button1=new JButton(dic[1]);
		button1.setMinimumSize(new Dimension(210,35));
		button1.setMaximumSize(new Dimension(210,35));
		button1.setToolTipText(dic[3]);
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				boolean verify=true;
				String message="";
				
				
				if (e.getActionCommand().equals(dic[1])) {
					
					// verificamos parametros
					seed=seedInput.getText().trim();
					if (seed==null || seed.length()<3 || seed.length()>6) {
						message+=dic[16]+System.lineSeparator();
						verify=false;
					}
					
					if (keylength<4 || keylength>12 || keylength<=seed.length()) {
						message+=dic[17]+System.lineSeparator();
						verify=false;
					}
					
					chain=chainInput.getText().trim();
					if (chain==null || chain.length()<10 || chain.length()>100) {
						message+=dic[18]+System.lineSeparator();
						verify=false;
					}
					try {
						filesize=Long.parseLong(filesizeInput.getText().trim());						
					} catch (NumberFormatException nf) {
						filesize=0;
					}

					if (filesize<10000) {
						message+=dic[19]+System.lineSeparator();
						verify=false;
					}
					
					
					if (verify) {
						// lanzamos el generador de diccionario para calcular el tamaño estimado
						try {
							// solo en el caso de confirmacion
							startGeneration(false);							
							// generamos el mensaje
							mainPanel.remove(mess);
							mess.setText(messages);
							mainPanel.add(mess);
						} catch (IOException ex) {
							System.err.println(dic[20]+ex.getStackTrace());
							System.exit(0);			
							
						}						
					} else {
						// generamos el mensaje
						mainPanel.remove(mess);
						messages=message;
						mess.setText(messages);
						mainPanel.add(mess);						
					}
				}
			}
		});
		buttons.add(button1);
		
		// boton
		JButton button2=new JButton(dic[2]);
		button2.setMinimumSize(new Dimension(210,35));
		button2.setMaximumSize(new Dimension(210,35));
		button2.setToolTipText(dic[4]);
		button2.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(dic[2])) {
					
					boolean verify=true;
					String message="";
					
					// generación del mensaje de confirmación
					JDialog box=new JDialog(window, dic[14], true);
					JOptionPane optionPane = new JOptionPane(
			                dic[15],
			                JOptionPane.QUESTION_MESSAGE,
			                JOptionPane.YES_NO_OPTION);
					optionPane.addPropertyChangeListener(new PropertyChangeListener() {						
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if (evt.getNewValue()==(Integer)0) {
								// solo en el caso de responder si, se procesaría el evento
								response=true;
							}
							// ocultamos la ventana
							box.setVisible(false);
						}
					});
					
					box.setSize(350, 150);
					box.setLocationRelativeTo(window);
					box.getContentPane().add(optionPane);
					box.setVisible(true);
					
					// verificamos parametros
					seed=seedInput.getText().trim();
					if (seed==null || seed.length()<3 || seed.length()>6) {
						message+=dic[16]+System.lineSeparator();
						verify=false;
					}
					
					if (keylength<4 || keylength>12 || keylength<=seed.length()) {
						message+=dic[17]+System.lineSeparator();
						verify=false;
					}
					
					chain=chainInput.getText().trim();
					if (chain==null || chain.length()<10 || chain.length()>100) {
						message+=dic[18]+System.lineSeparator();
						verify=false;
					}
					
					filesize=Long.parseLong(filesizeInput.getText().trim());
					if (filesize<10000) {
						message+=dic[19]+System.lineSeparator();
						verify=false;
					}
					
					if (verify) {
						// lanzamos el generador de diccionario
						try {
							// solo se lanza si existe confirmación
							if (response) {
								// generamos el mensaje
								mainPanel.remove(mess);
								messages=dic[21]+new Date();
								mess.setText(messages);
								mainPanel.add(mess);
								startGeneration(true);
								mainPanel.remove(mess);
								messages+=System.lineSeparator()+dic[22]+new Date();
								mess.setText(messages);
								mainPanel.add(mess);
							} else {
								mainPanel.remove(mess);
								messages=dic[23];
								mess.setText(messages);
								mainPanel.add(mess);
							}
							// se restaura a false la confirmación
							response=false;
						} catch (IOException ex) {
							System.err.println(dic[20]+ex.getStackTrace());
							System.exit(0);			
							
						}	
					} else {
						// generamos el mensaje
						mainPanel.remove(mess);
						messages=message;
						mess.setText(messages);
						mainPanel.add(mess);						
					}
				}
				
			}
		});
		buttons.add(button2);

		
		// añadimos los componentes al mainPanel principal
		// separador
		mainPanel.add(new JLabel(" "));
		mainPanel.add(sup);
		// separador
		mainPanel.add(new JLabel(" "));
		mainPanel.add(sup2);	
		// separador
		mainPanel.add(new JLabel(" "));
		mainPanel.add(chainTitle);
		mainPanel.add(chainInput);
		// separador
		mainPanel.add(new JLabel(" "));		
		mainPanel.add(buttons);
		// separador
		mainPanel.add(new JLabel(" "));
		mainPanel.add(mess);
		// separador
		mainPanel.add(new JLabel(" "));		
		
		// hacemos visible el mainPanel y lo añadimos
		mainPanel.setVisible(true);
		
		return mainPanel;
	}
	
	
	
	/**
	 * En caso de process==true
	 * Este método generará un diccionario de claves, de keylength de longitud, conteniendo una semilla
	 * entera 'seed' y con la permutación con repetición de todos los caracteres de 'chain',
	 * que irán rellenando los espacios en blanco diferencia entre keylength y tamaño de seed.
	 * 
	 * En caso de process==false
	 * Este método calculara cuantos bytes y en cuantos ficheros se generarán con los datos introducidos
	 * en el formulario. Esto se hace para valorar si se puede bloquear el ordenador o el sistema
	 * operativo con la generación masiva de ficheros (demasiados ficheros serían problemáticos)
	 * 
	 * Primero se creará un array de seeds diferentes en keylength de tamaño
	 * (por ejemplo: seedXX, XseedX, XXseed)
	 * Luego se iterarán cada uno de los elementos del array de seeds con las permutaciones de 'chain'
	 * (por ejemplo: seed00, 0seed0, 00seed, seed01... hasta 99seed)(suponiendo chain solo numérico)
	 * Se creará un fichero con el wordlist generado.
	 * 
	 * @param process boolean => true genera ficheros, false = calcula cuantos ficheros
	 * @throws IOException 
	 */
	private void startGeneration(boolean process) throws IOException {
	
		
		// relleno estandar
		String refilling="                ";
		
		int posfinal=keylength-seed.length();
		
		int matrix1length=posfinal+1;
		
		String[] mainmatrix=new String[matrix1length];
		
		// creamos variables para el relleno delantero y el trasero
		String fill1="";
		String fill2="";

		for (int pos=0;pos<matrix1length;pos++) {
			
			// creamos el relleno delantero y trasero con longitud variable
			if (pos>0) fill1=refilling.substring(0,pos); else fill1="";
			if (pos<(posfinal+1)) fill2=refilling.substring(pos,posfinal); else fill2="";
			
			// creamos las distintas semillas permutadas de tamaño keylength
			mainmatrix[pos]=fill1+seed+fill2;

		}
		
		if (process) {
			// generamos los bloques de permutaciones y el diccionario
			getBlockPermutations(chain,posfinal,mainmatrix);			
		} else {
			// realizamos el calculo de ficheros y tamaño
			//long result=estimatedBlockPermutations(chain, posfinal, mainmatrix);

			// base: la longitud de la cadena
			double base=(double)chain.length();
			// exponente: las posiciones a variar
			double exp=(double)posfinal;
			
			// num. de permutaciones con repeticion
			double perm=Math.pow(base, exp);
			
			// permutaciones * posiciones de la semilla * longitud clave+endline
			long result=(long)perm*(mainmatrix.length)*(keylength+1);
					
			// comprobamos los ficheros dividiendo entre el tamaño del fichero 
			int numfiles=(int) Math.round((result/filesize));
			
			// redondeamos hacia arriba
			if (result>(numfiles*filesize)) numfiles++;
			
			
			String mess1=dic[25]+result+dic[26]+numfiles+dic[27]+filesize+" bytes.";
			String mess2= (numfiles>50) ? dic[24] : "";
			
			messages=mess1+System.lineSeparator()+mess2;
		}

	}
	
	
	
	/**
	 * Devuelve una cadena de addings de longitud, con las permutaciones con repetición
	 * de los elementos de la cadena chain
	 * @param chain
	 * @param addings
	 * @return
	 * @throws IOException 
	 */
	private void getBlockPermutations(String chain, int addings, String[] mainMatrix) throws IOException {
		
		int posFinal=chain.length();
		
		// creamos una matriz de chars con el chain
		char[] matrix=chain.toCharArray();
		
		// matriz de trabajo
		char[] mtrxwork=new char[addings];
		
		// matriz de posiciones
		int[] posic= new int[addings];
		

		for (int n=0;n<addings;n++) {
			// la rellenamos integramente con el primer caracter
			mtrxwork[n]=matrix[0];
			// la rellenamos integramente con ceros
			posic[n]=0;
		}
		
		
		do {

			// creamos la matriz de trabajo con las posiciones obtenidas
			// desde el secuenciador de indices
			for (int n=0;n<addings;n++) {
				mtrxwork[n]=matrix[posic[n]];
			}

			// generamos las palabras para el diccionario
			// combinando la matriz de caracteres generada y la matriz
			// de la semilla permutada
			filename=createCombinedWord(mtrxwork,mainMatrix,filename);	
		
			// incrementamos la posicion
			posic[addings-1]=posic[addings-1]+1;
			if (posic[addings-1]>=posFinal) posic=procesa(posic,addings,posFinal);
				
			
		} while (posic!=null);


	}
	

	/**
	 * Este metodo recibo una matriz con string conteniendo espacios,
	 * y un array con caracteres que sustituyen a esos espacios, de tal
	 * forma que:
	 * anne123 / 1anne23 / 12anne3 / 123anne (ejemplo)
	 * @param mtrxwork
	 * @param mainmatrix
	 */
	private String createCombinedWord(char[] mtrxwork,String[] matrix, String filename) throws IOException {
		
		StringBuffer buff=new StringBuffer();
		// procesamos word por word del mainmatrix, los cuales tienen
		// espacios por aqui y por alla y hay que reemplazarlos
		for (int j=0;j<matrix.length;j++) {
			String[] mainmatrix=matrix.clone();
			for (int k=0;k<mtrxwork.length;k++) {
				// reemplazamos cada espacio por su char en mtrxwork
				mainmatrix[j]=mainmatrix[j].replaceFirst(" ", String.valueOf(mtrxwork[k]));
			}			
			
			buff.append(mainmatrix[j]+System.lineSeparator());
			
		}
		
		return writeDictionary(buff,filename);
		
	}
	
	
	/**
	 * Esta funcion recibe una matriz de contadores
	 * y aumenta el contador desde el final hacia el
	 * principio de la matriz, controlando el límite
	 * para una matriz de addings elementos en un valor
	 * máximo de maxlimit
	 * @param matrix
	 * @return
	 */
	private int[] procesa(int[] matrix, int addings,int maxlimit) {
		
		boolean contpross=true;
		int pos=addings-1;
		
		do {
			// si la posicion actual ha alcanzado el limite
			if (matrix[pos]>=maxlimit) {
											
				if (pos==0 && matrix[pos]>=maxlimit) {
					// condicion de salida
					// estamos en el primer caracter y estamos
					// en el valor limite
					matrix=null;
					contpross=false;
				} else {
					// ponemos el indice de caracter a cero
					// y decrementamos el siguiente
					// que entrara en bucle do-while controlando su
					// idoneidad
					matrix[pos]=0;
					--pos;
					matrix[pos]=matrix[pos]+1;
				}
				
			} else {
				// salimos del bucle si no estamos en el limite
				contpross=false;
			}
			
		} while (contpross);
		
		// retornamos la matriz con los indices indicadores
		// o null si es el final de todo el bucle
		return matrix;
	}
	
	

	/**
	 * Graba en el fichero wordlist las claves generadas que se van acumulando
	 * con cada iteracion
	 *  
	 * @param buff
	 * @throws IOException
	 */
	
	private String writeDictionary(StringBuffer buff,String filename) throws IOException {
		
		// tomamos el nombre del fichero que nos indican
		File dic=new File(filename);
		if (dic.exists()) {
			if (dic.length()>filesize) {
				String time=String.valueOf(new Date().getTime());
				// una vez que existe el fichero, al pasar de determinado tamaño,
				// el fichero se va llamando rootFile+timestamp
				filename=rootFile+time;
				dic=new File(filename);
			}
		}
		
		// creamos el fichero, grabamos y cerramos
		FileWriter fw=new FileWriter(dic, true);
		BufferedWriter bw=new BufferedWriter(fw);
		PrintWriter pw=new PrintWriter(bw);
		pw.write(buff.toString());
		pw.flush();
		pw.close();

		return filename;
	}	

	

	
	
	
}
