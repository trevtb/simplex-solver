package de.or.simplex;

import java.text.DecimalFormat;

/*	---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
--| Copyright (c) by Tobias Burkard, 2011	      |--
---------------------------------------------------------------------------------
-- --
-- CLASS: SimplexC --
-- --
---------------------------------------------------------------------------------
-- --
-- PROJECT: Simplex-Tableu --
-- --
---------------------------------------------------------------------------------
-- --
-- SYSTEM ENVIRONMENT 					    --
-- OS			Ubuntu 10.04 (Linux 2.6.32)	--
-- SOFTWARE 	JDK 1.6.0				    --
-- --
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------	*/

/**	
*	Dies ist die Hauptklasse des Simplex-Solvers.
*	Sie initiiert den Programmstart, zeichnet das GUI und führt die Berechnungen durch.
*	
* 	@version 0.1 von 04.2011
*
* 	@author Tobias Burkard
* 
* 	@since JDK 1.6.0
**/
public class SimplexC {
	
	static SimplexC mainC_ref;
	private long takenTime = 0L;
	
	/**
	 * Erstellt ein SimplexC objekt und ruft die createGUI()-Methode darauf auf
	 * 
	 * @param args Die Argumente werden nicht beruecksichtigt
	 * @see #createGUI()
	 */
	public static void main (String args[]) {
		mainC_ref = new SimplexC();
		mainC_ref.createGUI();
	} //endmethod main
	
	/**
	 * Erstellt das SimplexGUI-Objekt und ruft darauf die drawGUI()-Methode auf
	 * 
	 * @see SimplexGUI#drawGUI(SimplexC)
	 */
	private void createGUI() {
		SimplexGUI gui_ref = new SimplexGUI();
		gui_ref.drawGUI(mainC_ref);
	} //endmethod createGUI
	
	/**
	 * Fuehrt die gesamte Berechnung mit Hilfe des Simplex-Algorithmus durch.
	 * Erkennt auch, ob der duale oder primale Simplex-Alogrithmus noetig ist
	 * und optimiert die Ausgangstabelle am Schluss im Bezug auf Nachkommastellen und
	 * behebt Rundungsfehler.
	 * 
	 * @param tableEin_ref Die Ursprungstabelle, also das erste Simplex-Tableu
	 * @return Die Ergebnistabelle, das letzte Simplex-Tableu
	 */
	String[][] calculate(String[][] tableEin_ref) {
		int iterations = 0;
		int pivotSpalte = 0;
		int pivotZeile = 1;
		boolean hasValidFunction = true;
		boolean needsDouble = false;
		double[][] matrixOne_ref = new double[5][4];
		double[][] matrixTwo_ref = new double[5][4];
		String[][] names_ref = new String[2][5];
		boolean alive;
		
		/**
		 * String-Matrix in Double-Matrix umwandeln
		 */
		for (int i=0; i<5; i++) {
			names_ref[0][i] = tableEin_ref[5][i];
			names_ref[1][i] = tableEin_ref[6][i];
			for (int j=0; j<4; j++) {
				matrixOne_ref[i][j] = Double.parseDouble(tableEin_ref[i][j]);
			} //endfor
		} //endfor
		
		/**
		 * Unnoetige (leere) Zeilen in der Matrix finden
		 */
		int count = 0;
		boolean[] badLines_ref = new boolean[5];
		if (matrixOne_ref[0][0] == 0.0 && matrixOne_ref[0][1] == 0.0 && matrixOne_ref[0][2] == 0.0 && matrixOne_ref[0][3] == 0.0) {
			hasValidFunction = false;
		} //endif
		for (int i=0; i<5; i++) {
			if (matrixOne_ref[i][0] == 0.0 && matrixOne_ref[i][1] == 0.0 && matrixOne_ref[i][2] == 0.0 && matrixOne_ref[i][3] == 0.0) {
				badLines_ref[i] = true;
				count++;
			} //endif
		} //endfor
		
		/**
		 * Feststellen, ob die Matrix ueber eine valide Zielfunktion verfuegt.
		 */
		if (!hasValidFunction) {
			String[][] errorMsg_ref = new String[1][2];
			errorMsg_ref[0][0] = "error";
			errorMsg_ref[0][1] = "invalid_function";
			return errorMsg_ref;
		}
		/**
		 * Falls die Matrix genug uebrige Zeilen hat, verbleibende Zeilen
		 * in neue (kleinere) Matrix ueberfuehren.
		 * Falls nicht, Fehlermeldung an GUI uebergeben.
		 */
		if (count > 0 && count < 4) {
			double[][] tempArray_ref = new double[5-count][4];
			matrixTwo_ref = new double[5-count][4];
			int num = 0;
			for (int i=0; i<5; i++) {
				if (!badLines_ref[i]) {
					for (int j=0; j<4; j++) {
						tempArray_ref[num][j] = matrixOne_ref[i][j];
					} //endfor
					num++;
				} //endif
			} //endfor
			matrixOne_ref = tempArray_ref;
		} else if (count >= 4) {
			String[][] errorMsg_ref = new String[1][2];
			errorMsg_ref[0][0] = "error";
			if (count == 4) {
				errorMsg_ref[0][1] = "keine_nb";
			} else if (count == 5) {
				errorMsg_ref[0][1] = "leere_matrix";
			} //endif
			return errorMsg_ref;
		} //endif
		
		/**
		 * Ueberpruefen, ob eine Optimierung ueberhaupt moeglich ist,
		 * also ob negative Koeffizienten in der Zielfunktion existieren.
		 * Falls ja, die "alive"-Variable, welche die Rechenschleife am Leben haelt,
		 * auf "true" setzen.
		 */
		if (matrixOne_ref[0][0] < 0.0 || matrixOne_ref[0][1] < 0.0 || matrixOne_ref[0][2] < 0.0) {
			alive = true;
		} else {
			alive = false;
		} //endif
		
		/**
		 * In der folgenden Schleife werden alle Berechnungen durchgefuehrt
		 */
		int ml = matrixOne_ref.length;
		takenTime = System.nanoTime();
		while (alive) {
			
			/**
			 * Pruefe, ob die Schleife bereits 100x durchlaufen wurde
			 * Falls ja, wird der NotAus betaetigt.
			 */
			if (iterations == 100) {
				break;
			} //endif
			
			/**
			 * Ueberpruefen, ob der primale oder duale Simplex-Algorithmus benoetigt wird.
			 * Falls "needsDouble" auf "true" gesetzt wird, wird der duale Algorithmus verwendet.
			 */
			for (int i=1; i<ml; i++) {
				if (matrixOne_ref[i][3] < 0.0) {
					needsDouble = true;
				} //endif
			} //endfor
			
			if (needsDouble) {
				/**
				 * Den kleinsten Wert der rechten Seite (ohne Zielfunktionszeile) finden.
				 * Die Zeile, in welcher der Wert steht, ist die Pivotzeile.
				 */
				double minWert = matrixOne_ref[1][3];
				pivotZeile = 1;
				for (int i=1; i<ml; i++) {
					if (matrixOne_ref[i][3] < 0.0 && matrixOne_ref[i][3] < minWert) {
						minWert = matrixOne_ref[i][3];
						pivotZeile = i;
					} //endif
				} //endfor
				
				/**
				 * Die Berechnung kann nur fortgeführt werden, wenn in der Pivotzeile
				 * negative Elemente existieren, durch die geteilt werden kann.
				 * Ueberpruefe dies.
				 */
				boolean isValid = false;
				for (int i=0; i<4; i++) {
					if (matrixOne_ref[pivotZeile][i] < 0.0) {
						isValid = true;
						minWert = matrixOne_ref[0][i] / matrixOne_ref[pivotZeile][i];
					} //endif
				} //endfor
				
				/**
				 * Suchen der Pivotspalte.
				 * Falls im letzten Schritt "isValid" auf "false" belassen wurde,
				 * existiert keine Loesungsmenge fuer das Ungleichungssystem.
				 * Gib bei bedarf eine entsprechende Fehlermeldung an das GUI zurueck.
				 */
				if (isValid) {
					for (int i=0; i<4; i++) {
						if (matrixOne_ref[pivotZeile][i] < 0.0 && minWert > matrixOne_ref[0][i] / matrixOne_ref[pivotZeile][i]) {
							minWert = matrixOne_ref[0][i] / matrixOne_ref[pivotZeile][i];
							pivotSpalte = i;
						} //endif
					} //endfor
				} else {
					String[][] errorString_ref = new String[1][2];
					errorString_ref[0][0] = "error";
					errorString_ref[0][1] = "loesung_leer";
					
					return errorString_ref;
				} //endif	
			} else {
				/**
				 * Suchen der Pivotspalte.
				 */
				double pivot = matrixOne_ref[0][0];
				pivotSpalte = 0;
				for (int i=1; i<3; i++) {
					if (matrixOne_ref[0][i] < pivot && matrixOne_ref[0][i] < 0.0) {
						pivotSpalte = i;
						pivot = matrixOne_ref[0][i];
					} //endif
				} //endfor
				
				/**
				 * Ueberpruefe, ob in der Pivotspalte teilbare Elemente > 0 existieren.
				 * Dies ist noetig, um den Algorithmus fortzufuehren.
				 */
				boolean isValid = false;
				for (int i=1; i<ml; i++) {
					if (matrixOne_ref[i][pivotSpalte] > 0.0) {
						isValid = true;
					} //endif
				} //endfor
				
				/**
				 * Falls valide Elemente existieren, finde die Pivotzeile.
				 * Falls nicht, hat das Ungleichungssystem eine unbeschraenkte Loesung.
				 * Gib dies bei bedarf als Fehlermeldung an das GUI weiter.
				 * Zuerst wird irgendein Element > 0 zum Vergleichen ausgewaehlt.
				 */
				if (isValid) {
					double wert = 0.0;
					for (int i=1; i<ml; i++) {
						if (matrixOne_ref[i][pivotSpalte] > 0.0) {
							wert = matrixOne_ref[i][3] / matrixOne_ref[i][pivotSpalte];
							pivotZeile = i;
						} //endif
					} //endfor
					for (int i=1; i<ml; i++) {
						if ((matrixOne_ref[i][pivotSpalte] > 0.0) && (matrixOne_ref[i][3] / matrixOne_ref[i][pivotSpalte]) < wert) {
							wert = matrixOne_ref[i][3] / matrixOne_ref[i][pivotSpalte];
							pivotZeile = i;
						} //endif	
					} //endfor
				} else {
					String[][] errorString_ref = new String[1][2];
					errorString_ref[0][0] = "error";
					errorString_ref[0][1] = "loesung_unb";
					
					return errorString_ref;
				} //endif
			} //endif
			
			/**
			 * Vertauschen der Variablen von Pivotzeile und Pivotspalte
			 */
			String tempS_ref = names_ref[0][pivotSpalte+1];
			String tempZ_ref = names_ref[1][pivotZeile];
			names_ref[1][pivotZeile] = tempS_ref;
			names_ref[0][pivotSpalte+1] = tempZ_ref;
			
			/**
			 * Als naechstes wird das Pivotelement festgelegt.
			 * Das Element wird in der neuen Tabelle durch seinen Kehrwert ersetzt.
			 * Alle Elemente der Pivotspalte in der neuen Tabelle mit Kehrwert*-1 multiplizieren
			 * Alle Elemente der Pivotzeile in der neuen Tabelle mit Kehrwert multiplizieren
			 */
			double pivotElement = matrixOne_ref[pivotZeile][pivotSpalte];
			matrixTwo_ref[pivotZeile][pivotSpalte] = 1.0 / pivotElement;
			for (int i=0; i<ml; i++) {
				if (i != pivotZeile) {
					matrixTwo_ref[i][pivotSpalte] = matrixOne_ref[i][pivotSpalte] * (-1.0 * matrixTwo_ref[pivotZeile][pivotSpalte]);
				} //endif
			} //endfor
			for (int i=0; i<4; i++) {
				if (i != pivotSpalte) {
					matrixTwo_ref[pivotZeile][i] = matrixOne_ref[pivotZeile][i] * matrixTwo_ref[pivotZeile][pivotSpalte];
				} //endif
			} //endfor
		
			/**
			 * Restliche Elemente berechnen, dies geschieht wie folgt:
			 * Element_neu = Element_alt - (Zahl_aus_Pivotzeile(neue Tabelle) * Element_aus_Pivotspalte(alte Tabelle) 
			 */
			for (int i=0; i<ml; i++) {
				if (i != pivotZeile) {
					for (int j=0; j<4; j++) {
						if (j != pivotSpalte) {
							matrixTwo_ref[i][j] = matrixOne_ref[i][j] - (matrixTwo_ref[pivotZeile][j]*matrixOne_ref[i][pivotSpalte]);
						} //endif
					} //endfor
				} //endif
			} //endfor
			
			/**
			 * Wird folgende Schleife + System.out-Statement unkommentiert,
			 * so wird jede Zwischentabelle auf der Kommandozeile ausgegeben.
			 */
			for (int i=0; i<matrixTwo_ref.length; i++) {
				System.out.println("");
				for (int j=0; j<matrixTwo_ref[i].length; j++) {
					System.out.print(matrixTwo_ref[i][j] + " | ");
				} //endfor
			} //endfor
			System.out.println("\n");
			
			/**
			 * Neue Matrix in alte Matrix kopieren und Array fuer neue Tabelle leeren
			 */
			matrixOne_ref = matrixTwo_ref;
			matrixTwo_ref = new double[matrixTwo_ref.length][4];
			
			/**
			 * Ueberpruefen ob noch negative Koeffizienten in der Zielfunktionszeile existieren.
			 * Falls nein, "alive" auf "false" setzen und somit Rechenschleife unterbrechen
			 */
			if (matrixOne_ref[0][0] < 0.0 || matrixOne_ref[0][1] < 0.0 || matrixOne_ref[0][2] < 0.0) {
				alive = true;
			} else {
				alive = false;
			} //endif
			
			iterations++;
		} //endwhile
		
		takenTime = System.nanoTime() - takenTime;
		
		/**
		 * String-Array fuer das GUI vorbereiten, Werte und Variablennamen
		 * hineinkopieren und Array im Bezug auf Rundungsfehler und
		 * Nachkommastellen formatieren.
		 * Dies geschieht nur, wenn der NotAus nicht betaetigt wurde.
		 */
		String[][] tempList_ref = new String[7][5];
		if (iterations < 100) {
			for (int i=0; i<5; i++) {
				tempList_ref[5][i] = names_ref[0][i];
				tempList_ref[6][i] = names_ref[1][i];
			} //endfor
			for (int i=0; i<5; i++) {
				for (int j=0; j<4; j++) {
					tempList_ref[i][j] = "0.000";
				} //endfor
			} //endfor
			DecimalFormat df_ref = new DecimalFormat("0.000");
			for (int i=0; i<ml; i++) {
				for (int j=0; j<4; j++) {
					tempList_ref[i][j] = df_ref.format(matrixOne_ref[i][j]);
				} //endfor
			} //endfor
			
			/**
			 * Alle Kommata (,) in der String-Matrix gegen Punkte (.) ersetzen
			 * Dies hat lediglich optische Gruende und keinen Einfluss auf die Funktionalitaet.
			 */
			for (int i=0; i<5; i++) {
				for (int j=0; j<5; j++) {
					if (tempList_ref[i][j] != null) {
						char[] tempCharA_ref = tempList_ref[i][j].toCharArray();
						String tempS_ref = "";
						for (int k=0; k<tempCharA_ref.length; k++) {
							if (tempCharA_ref[k] == ',') {
								tempS_ref += ".";
							} else {
								tempS_ref += tempCharA_ref[k];
							} //endif
						} //endfor
						tempList_ref[i][j] = tempS_ref;
					} //endif
				} //endfor
			} //endfor
		} else {
			tempList_ref = new String[1][2];
			tempList_ref[0][0] = "error";
			tempList_ref[0][1] = "iterations";
		} //endif
		
		return tempList_ref;
	} //endmethod calculate
	
	public long getTakenTime() {
		return takenTime;
	} //endmethod getTakenTime
	
	public void resetTakenTime() {
		takenTime = 0L;
	} //endmethod resetTakenTime

} //endclass SimplexC