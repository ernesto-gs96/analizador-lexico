import java.io.*;
import java.util.*;//LIBRERIA PARA USAR StringTokenizer
import java.text.*;//LIBRERIA PARA USAR Collator
import java.lang.*;//LIBERIA PARA USAR 

public class Analizador {
	
	public static String[] palabras_reservadas = {"import", "public","class","static","void","throws","private","int","new","for","if","return","this","switch","package","else","default","char","break","float","double","short","long","byte","TRUE","FALSE","void","main"};
	public static String[] tipo_dato={"int","byte","short","long","float","double",""};
	public static String[] simbolos_especialesI={"<",">","<=",">=","!=","==","++","+=","--","-="};
    public static String[] simbolos_especialesII={"+","-","*","/","%","="};
   	public static String[] valboolean={"FALSE","TRUE"};
    public static String[] comentarios={"//","/*","*/"};
	public static tipo_nom_val_variables[] variables = new tipo_nom_val_variables[5];
	public static int num_variable=0;

	public static void main(String[] args) throws IOException {
		
		String archivo="programa.java";
		int valido;

		imprimir(archivo);//UNICAMENTE IMPRIME EL .JAVA
		valido=Analizar(archivo);

		if(valido==1)
			System.out.println("\n\nNO HAY ERRORES DE ANALISIS LEXICO EN LAS CONSTANTES NUMERICAS DEL ARCHIVO: "+archivo);

	}
	
	public static void imprimir(String archivo) throws FileNotFoundException, IOException {//FUNCION PARA IMPRIMIR EL .JAVA

    	String linea;
    	int num_linea=1,i;
    	FileReader f = new FileReader(archivo);
        BufferedReader a = new BufferedReader(f);

        System.out.println("Contenido del archivo "+archivo+":\n");
    	while((linea = a.readLine())!=null)
        {
        	System.out.println(num_linea+" "+linea);
        	num_linea++;
        }
    }

    public static int Analizar(String archivo) throws FileNotFoundException, IOException {//FUNCION PARA IMPRIMIR EL .JAVA

    	String linea, encontrada, ax, ay="";
    	int i,num_linea=1,aux=1,estado=1,valido=1,on=1,est_prog=1,com1=1,com2=1;
    	char ult_pos,a;

    	tipo_nom_val_variables temp = new tipo_nom_val_variables();
    	 
    	Collator comparador1 = Collator.getInstance();
    	comparador1.setStrength(Collator.PRIMARY);//No se distingue si la letra es mayúscula, minúscula o si esta acentuada. (A = a = á)
        Collator comparador2 = Collator.getInstance();
    	comparador2.setStrength(Collator.IDENTICAL);//Dos letras son iguales únicamente si su código lo es, aunque se vean iguales. Hay letras que se pueden codificar de distintas maneras, por ejemplo las letras acentuadas.
        
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);

        while((linea = b.readLine())!=null) {//LEE EL ARCHIVO POR LINEA
        	
        	StringTokenizer cadena = new StringTokenizer(linea);

        	com1=1;
        	while (cadena.hasMoreTokens())//LEE LA LINEA POR CADENA HASTA QUE ACABE LA LINEA
        	{
        		on=1;//NUEVO TOKEN NUEVO CICLO
        		encontrada = cadena.nextToken();//SE PASA EL TOKEN A UN STRING

        		if(encontrada.equals("//") || encontrada.equals("/*")){
        			
        			if(encontrada.equals("//"))
        				com1=0;
        			if(encontrada.equals("/*"))
        				com2=0;
        			//System.out.println("COMENTATARIO = "+encontrada+" COM1="+com1+" COM2="+com2);
        		}		
        		if(encontrada.equals("*/")){
        			if(com2==0){
        				on=0;
        				com2=1;
        			}
        		}

        		if(com1==1 && com2==1){
        			//System.out.println("SI ENTRO");
        		while(on!=0){//EL CASO 2 NECESITA EL TOKEN ANTERIOR
        			//System.out.println("encontrada = "+encontrada+" estado = "+estado);
	        		switch(estado){
	        			case 1://PALABRAS RESERVADAS
	        			//System.out.println("ENCONTRADA = "+encontrada+" ESTADO = "+estado);
	        				for(i=0; i<palabras_reservadas.length; i++){//COMPARAR PALABRA ENCONTRADA CON LAS RESERVADAS
	        					if(comparador1.equals(palabras_reservadas[i], encontrada)){//COMPARACION PRIMATY
	        						if(comparador2.equals(palabras_reservadas[i], encontrada)){//COMPARACION IDENTICAL
	        							i=palabras_reservadas.length;
	        							estado=2;
	        							on=1;
	        						}
	        						else
	        						{
	        							est_prog=0;//PARA SABER SI EL PROGRAMA NO TIENE ERRORES
	        							on=0;//DETENER EL CICLO
	        							estado=1;//PROXIMO ESTADO
	        							System.out.println("ERROR EN PALABRA RESERVADA "+encontrada+" EN LA LINEA "+num_linea);
	        						}
	        				
	        					}
	        					else{
	        						on=1;
	        						estado=6;
	        					}
	        				}
	        			//System.out.println("ESTADO PROXIMO = "+estado);
	        			break;

	        			case 2://TIPO DE DATO
	        			//System.out.println("ENCONTRADA = "+encontrada+" ESTADO = "+estado);
	        				for(i=0;i<tipo_dato.length;i++){//SE BUSCA SI ES UN TIPO DE DATO
								if(tipo_dato[i].equals(encontrada)){
									temp.settipo_variables(tipo_dato[i]);//SE GUARDA EL TIPO DE LA VARIABLE;
									variables[num_variable]=temp;
									estado=3;
									on=0;
									i=tipo_dato.length;//PARA TERMINAR EL CICLO
								}
								else{
									estado=1;
									on=0;
								}
							}
							//System.out.println("ESTADO PROXIMO = "+estado);
	        				break;

	        			case 3://NOMBRE DE VARIABLE
	        			//System.out.println("ENCONTRADA = "+encontrada+" ESTADO = "+estado);
        					ult_pos=encontrada.charAt(encontrada.length()-1);//ULTIMA POSICION DE ENCONTRADA
							if(ult_pos==',' || ult_pos==';'){
								//System.out.println("IF");
								ax = encontrada.substring(0,encontrada.length()-1);//SE ELIMNA LA ULTMA POSICION PARA NO AFECTAR LA OPERACION
								encontrada=ax;
								valido=val_nom_var(encontrada,num_linea);//SE VALIDA SI ES CORRECTO EL NOMBRE DE LA VARIABLE
								if(valido==0)
									est_prog=0;//PARA SABER SI EL PROGRAMA NO TIENE ERRORES
								if(ult_pos==','){
									estado=3;//PERMANCE EN EL ESTADO 3
									ay+=variables[num_variable++].gettipo_variables();//EL SIGUIENTE NOMBRE ES EL MISMO TIPO DE DATO
									temp.settipo_variables(ay);//SE GUARDA EL TIPO DE LA VARIABLE;
									variables[num_variable]=temp;
								}
								else{
									estado=1;
								}
										
							}
							else{
								//System.out.println("ELSE");
								valido=val_nom_var(encontrada,num_linea);//SE VALIDA SI ES CORRECTO EL NOMBRE DE LA VARIABLE
								estado=4;
							}

							on=0;
							//System.out.println("ESTADO PROXIMO = "+estado);
	        				break;

	        			case 4://SIGNO =
	        			//System.out.println("ENCONTRADA = "+encontrada+" ESTADO = "+estado);
	        				if(encontrada.equals("="))
	        					estado=5;
	        				on=0;
	        				//System.out.println("ESTADO PROXIMO = "+estado);
	        				break;

	        			case 5://VALOR DE VARIABLE
	        			//System.out.println("ENCONTRADA = "+encontrada+" ESTADO = "+estado);
	        				ult_pos=encontrada.charAt(encontrada.length()-1);//ULTIMA POSICION DE ENCONTRADA
							if(ult_pos==',' || ult_pos==';'){
								ax = encontrada.substring(0,encontrada.length()-1);//SE ELIMNA LA ULTMA POSICION PARA NO AFECTAR LA OPERACION
								encontrada=ax;
								//System.out.println("PASO");
								valido=val_val_var(encontrada,num_linea);//SE VALIDA SI ES CORRECTO EL VALOR DE LA VARIABLE
								if(valido==0)
									est_prog=0;//PARA SABER SI EL PROGRAMA NO TIENE ERRORES
								if(ult_pos==','){
									estado=3;//PERMANCE EN EL ESTADO 3 
									ay+=variables[num_variable++].gettipo_variables();//EL SIGUIENTE NOMBRE ES EL MISMO TIPO DE DATO
									temp.settipo_variables(ay);//SE GUARDA EL TIPO DE LA VARIABLE;
									variables[num_variable]=temp;
								}
								else{
									estado=1;
								}
										
							}
							else{
								valido=val_val_var(encontrada,num_linea);//SE VALIDA SI ES CORRECTO EL VALOR DE LA VARIABLE
							}
							on=0;

							//System.out.println("ESTADO PROXIMO = "+estado);
	        				break;

	        			case 6://CASO DE SIMBOLOS
	        				on=0;
	        				estado=1;
	        				a=encontrada.charAt(0);//PRIMERA POSICION
	        				aux=buscador_sim_especial(a);
	        				if(aux==0){
	        					valido=simbolos(encontrada,num_linea);
	        					if(valido==0)
	        						est_prog=0;
	        				}
	        				
	        				break;
	        		}//FIN SWITCH
	        		//System.out.println("ESTADO FINAL: "+estado);
        		}//FIN WHILE 3
        		
        	}//FIN IF
        	
        		
        		
        	
        		}//FIN WHILE 2
        	num_linea++;//CONTADOR DE NUMERO DE LINEAS
        }//FIN WHILE 1
        
        b.close();
        return est_prog;
	}//FIN FUNCION ANALIZADOR

	public static int val_nom_var(String encontrada, int num_linea){

		int i,j,estado=1,valido=1;
		char a,b;
		String aux;

		tipo_nom_val_variables temp = new tipo_nom_val_variables();

		for(i=0;i<encontrada.length();i++)
		{
			if(valido==1){
				switch(estado){//AUTOMOTA DE VALIDACION DE NOMBRE DE VARIABLE
					case 1:
						a=encontrada.charAt(i);//PRIMERA POSICION DE ENCONTRADA
						valido=buscador_sim_especial(a);//SE REVISA QUE NO SEA UN SIMBOLO ESPECIAL

						if(a>='0' && a<='9')//SI LA PRIMERA POSICION ES UN NUMERO
							estado=3;

						estado=2;
						break;

					case 2:
						a=encontrada.charAt(i);//SIGUIENTE POSICION DE LA VARIABLE
						valido=buscador_sim_especial(a);//SE REVISA QUE NO SEA UN SIMBOLO ESPECIAL
						estado=2;
						break;

					case 3:
						a=encontrada.charAt(i);//SIGUIENTE POSICION DE LA VARIABLE
						valido=buscador_sim_especial(a);//SE REVISA QUE NO SEA UN SIMBOLO ESPECIAL

						if(a>='0' && a<='9'){//SI CONTINUA SIENDO UN NUMERO
							estado=3;
							if(i==encontrada.length()-1)//SI SE ACOMPLETO EL STRING
								valido=0;//ES INVALIDO POR QUE EL NOMBRE DE UNA VARIABLE NO PUEDE SER UN NUMERO
						}

						estado=2;
						break;
				}
			}
			else{
				System.out.println("EL NOMBRE: "+encontrada+" NO VALIDO, LINEA: "+num_linea);//ERROR SI NO ES VALIDO EL NOMBRE DE LA VARIABLE
				i=encontrada.length();//SI ESTA MAL TERMINA EL CICLO;
			}
				

				for(j=0;j<palabras_reservadas.length;j++)//BUSCA SI EL NOMBRE DE LA VARIABLE NO ES IGUAL AL DE UNA PALABRA RESERVADA
					if(palabras_reservadas[j].equals(encontrada))
						valido=0;
		}

		temp.setnom_variables(encontrada);//SE GUARDA EL NOMBRE DE LA VARIABLE;
		variables[num_variable]=temp;

		return valido;
	}

	public static int buscador_sim_especial(char a){

		int j,valido=1;
		char b;
		String aux;

		for (j=0;j<simbolos_especialesI.length;j++){//BUSCA SI LA PRIMERA POSICION DE LA VARIABLE NO ES IGUAL A UN SIMBOLO ESPECIAL 1
			aux=simbolos_especialesI[j];//SE GUARDA EL SIMBOLO ESPECIAL EN UN STRING
			b=aux.charAt(0);//SE GUARDA LA PRIMERA POSICION DEL STRING
			if(a==b)
				valido=0;
		}

		for (j=0;j<simbolos_especialesII.length;j++){//BUSCA SI LA PRIMERA POSICION DE LA VARIABLE NO ES IGUAL A UN SIMBOLO ESPECIAL 2
			aux=simbolos_especialesII[j];//SE GUARDA EL SIMBOLO ESPECIAL EN UN STRING
			b=aux.charAt(0);//SE GUARDA LA PRIMERA POSICION DEL STRING
			if(a==b)
				valido=0;
		}

		return valido;

	}

	public static int val_val_var(String encontrada, int num_linea){

		int i,estado=1,valido=1;
		char a;

		for (i=0;i<encontrada.length();i++) {
			switch(estado){
				case 1:
				a=encontrada.charAt(i);//PRIMERA POSICION DE ENCONTRADA
				if(a=='+' || a=='-'){
					valido=1;
					estado=2;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				if(a=='0'){
					valido=1;
					estado=3;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					if(a>='1' && a<='9') {
						valido=1;
						estado=6;
						break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
					}
					else
						valido=0;
					
				break;

			case 2://DESPUES DEL SIGNO
				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='1' && a<='9'){
					valido=1;
					estado=6;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;
				if(a=='0'){
					valido=1;
					estado=3;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;
				break;

			case 3://OCTAL
				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='0' && a<='7'){
					valido=1;
					estado=4;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				if(a=='x' || a=='X'){
					valido=1;
					estado=5;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				break;

			case 4://ESTADO DE ACEPTACION OCATAL

				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='0' && a<='7'){
					valido=1;
					estado=4;
				}
				else
					valido=0;
				break;

			case 5://ESTADO DE ACEPTACION HEXADECIAMAL
				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='0' && a<='z'){
					if(a>='0' && a<='9'){
						valido=1;
						estado=5;
						break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
					}
					else
						valido=0;

					if(a>='a' && a<='f'){
						valido=1;
						estado=5;
						break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
					}
					else
						valido=0;

					if(a>='A' && a<='F'){
						valido=1;
						estado=5;
						break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
					}
					else
						valido=0;
				}
				else
					valido=0;
				

				break;

			case 6://ESTADO DE ACEPTACION ENTEROS
				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='0' && a<='9'){
					valido=1;
					estado=6;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				if(a=='.'){
					valido=1;
					estado=7;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				if(a=='e' || a=='E'){
					valido=1;
					estado=8;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;
				break;

			case 7://ESTADO DE ACEPTACION DECIMAL SIN EXPONENCIAL
				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='0' && a<='9'){
					valido=1;
					estado=7;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				if(a=='e' || a=='E'){
					valido=1;
					estado=8;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				break;

			case 8://DECIMAL CON EXPONENCIAL
				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='0' && a<='9'){
					valido=1;
					estado=10;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;
				if(a=='+' || a=='-'){
					valido=1;
					estado=9;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				break;

			case 9:
				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='0' && a<='9'){
					valido=1;
					estado=10;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				break;

			case 10://ESTADO DE ACEPTACION DECIMAL CON EXPONENCIAL
				a=encontrada.charAt(i);//SIGUIENTE POSICION DE ENCONTRADA
				if(a>='0' && a<='9'){
					valido=1;
					estado=10;
					break;//SE ROMPE EL CICLO PARA NO ENTRAR EN LOS OTROS IF
				}
				else
					valido=0;

				break;
			
			}//FIN SWITCH

		}//FIN FOR

			if(valido==0)
				System.out.println("EL VALOR: "+encontrada+" NO VALIDO, LINEA: "+num_linea);//ERROR SI NO ES VALIDO EL NOMBRE DE LA VARIABLE

		return valido;
	}//FIN FUNCION val_val_var

	public static int simbolos(String encontrada, int num_linea){

		int j,valido=1;
		char b;
		String aux;

		for (j=0;j<simbolos_especialesI.length;j++){//BUSCA SI LA PRIMERA POSICION DE LA VARIABLE NO ES IGUAL A UN SIMBOLO ESPECIAL 1
			aux=simbolos_especialesI[j];//SE GUARDA EL SIMBOLO ESPECIAL EN UN STRING
			if(aux.equals(encontrada))
				return 1;
			else
				valido=0;
		}

		for (j=0;j<simbolos_especialesII.length;j++){//BUSCA SI LA PRIMERA POSICION DE LA VARIABLE NO ES IGUAL A UN SIMBOLO ESPECIAL 2
			aux=simbolos_especialesII[j];//SE GUARDA EL SIMBOLO ESPECIAL EN UN STRING
			if(aux.equals(encontrada))
				return 1;
			else
				valido=0;
		}

		if(valido==0)
			System.out.println("SIMBOLO ESPECIAL: "+encontrada+" MAL IMPLENTADO EN LA LINEA "+num_linea);

		return valido;
	}

}