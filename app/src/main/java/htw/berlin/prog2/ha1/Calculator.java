package htw.berlin.prog2.ha1;

/**
 * Eine Klasse, die das Verhalten des Online Taschenrechners imitiert, welcher auf
 * https://www.online-calculator.com/ aufgerufen werden kann (ohne die Memory-Funktionen)
 * und dessen Bildschirm bis zu zehn Ziffern plus einem Dezimaltrennzeichen darstellen kann.
 * Enthält mit Absicht noch diverse Bugs oder unvollständige Funktionen.
 */
public class Calculator {

    private String screen = "0";

    private double latestValue;

    private String latestOperation = "";

    /**
     * @return den aktuellen Bildschirminhalt als String
     */
    public String readScreen() {
        return screen;
    }

    /**
    * Empfängt den Wert einer gedrückten Zifferntaste. Da man nur eine Taste auf einmal
    * drücken kann, muss der Wert positiv und einstellig sein und zwischen 0 und 9 liegen.
    * Führt in jedem Fall dazu, dass die gerade gedrückte Ziffer auf dem Bildschirm angezeigt
    * oder rechts an die zuvor gedrückte Ziffer angehängt angezeigt wird.
    * Falls der Bildschirm aktuell nur eine "0" anzeigt (und kein Dezimalpunkt vorhanden ist),
    * wird diese zunächst entfernt, bevor die neue Ziffer angezeigt wird. Dies verhindert,
    * dass führende Nullen entstehen.
    * @param digit Die Ziffer, deren Taste gedrückt wurde
    * @throws IllegalArgumentException falls der übergebene Wert nicht zwischen 0 und 9 liegt
    */

    public void pressDigitKey(int digit) {
        if(digit > 9 || digit < 0) throw new IllegalArgumentException();
    
        if(screen.equals("0") && !screen.contains(".")) {
            screen = "";
        }
        screen += digit;
    }

    /**
     * Empfängt den Befehl der C- bzw. CE-Taste (Clear bzw. Clear Entry).
     * Einmaliges Drücken der Taste löscht die zuvor eingegebenen Ziffern auf dem Bildschirm
     * so dass "0" angezeigt wird, jedoch ohne zuvor zwischengespeicherte Werte zu löschen.
     * Wird daraufhin noch einmal die Taste gedrückt, dann werden auch zwischengespeicherte
     * Werte sowie der aktuelle Operationsmodus zurückgesetzt, so dass der Rechner wieder
     * im Ursprungszustand ist.
     */
    public void pressClearKey() {
        screen = "0";
        latestOperation = "";
        latestValue = 0.0;
    }

    /**
     * Empfängt den Wert einer gedrückten binären Operationstaste, also eine der vier Operationen
     * Addition, Substraktion, Division, oder Multiplikation, welche zwei Operanden benötigen.
     * Beim ersten Drücken der Taste wird der Bildschirminhalt nicht verändert, sondern nur der
     * Rechner in den passenden Operationsmodus versetzt.
     * Beim zweiten Drücken nach Eingabe einer weiteren Zahl wird direkt des aktuelle Zwischenergebnis
     * auf dem Bildschirm angezeigt. Wird danach eine weitere Zahl eingegeben und erneut eine Operationstaste gedrückt, wird das 
     * Zwischenergebnis aus der vorherigen Operation automatisch berechnet und als neuer Ausgangswert 
     * verwendet. Dadurch wird die fortlaufende Berechnung von verketteten Operationen ermöglicht. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * @param operation "+" für Addition, "-" für Substraktion, "x" für Multiplikation, "/" für Division
     */
    public void pressBinaryOperationKey(String operation) {
        double currentValue = Double.parseDouble(screen);
    
        if (!latestOperation.isEmpty()) {
            currentValue = applyOperation(latestOperation, latestValue, currentValue);
            screen = String.valueOf(currentValue);
        }
    
        latestValue = currentValue;
        latestOperation = operation;
        screen = "0";
    }

    /**
     * Empfängt den Wert einer gedrückten unären Operationstaste, also eine der drei Operationen
     * Quadratwurzel, Prozent, Inversion, welche nur einen Operanden benötigen.
     * Beim Drücken der Taste wird direkt die Operation auf den aktuellen Zahlenwert angewendet und
     * der Bildschirminhalt mit dem Ergebnis aktualisiert.
     * @param operation "√" für Quadratwurzel, "%" für Prozent, "1/x" für Inversion
     */
    public void pressUnaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
        var result = switch(operation) {
            case "√" -> Math.sqrt(Double.parseDouble(screen));
            case "%" -> Double.parseDouble(screen) / 100;
            case "1/x" -> 1 / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };
        screen = Double.toString(result);
        if(screen.equals("NaN")) screen = "Error";
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);

    }

    /**
    * Empfängt den Befehl der gedrückten Dezimaltrennzeichentaste, im Englischen üblicherweise ".".
    * Fügt beim ersten Drücken dem aktuellen Bildschirminhalt das Dezimaltrennzeichen auf der rechten
    * Seite hinzu und aktualisiert den Bildschirm entsprechend. Zahlen, die danach eingegeben werden,
    * erscheinen rechts vom Punkt und werden als Nachkommastellen interpretiert.
    * Falls der Bildschirm leer ist oder nur eine "0" enthält, wird das Trennzeichen als "0." angezeigt,
    * um eine gültige Dezimalzahl darzustellen.
    */
    public void pressDotKey() {
        if(!screen.contains(".")) {
            screen = (screen.equals("0") || screen.isEmpty()) ? "0." : screen + ".";
        }
    }
    /**
     * Empfängt den Befehl der gedrückten Vorzeichenumkehrstaste ("+/-").
     * Zeigt der Bildschirm einen positiven Wert an, so wird ein "-" links angehängt, der Bildschirm
     * aktualisiert und die Inhalt fortan als negativ interpretiert.
     * Zeigt der Bildschirm bereits einen negativen Wert mit führendem Minus an, dann wird dieses
     * entfernt und der Inhalt fortan als positiv interpretiert.
     */
    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen;
    }

    /**
     * Empfängt den Befehl der gedrückten "="-Taste.
     * Wurde zuvor keine Operationstaste gedrückt, passiert nichts.
     * Wurde zuvor eine binäre Operationstaste gedrückt und zwei Operanden eingegeben, wird das
     * Ergebnis der Operation angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * Wird die Taste weitere Male gedrückt (ohne andere Tasten dazwischen), so wird die letzte
     * Operation (ggf. inklusive letztem Operand) erneut auf den aktuellen Bildschirminhalt angewandt
     * und das Ergebnis direkt angezeigt.
     */
    public void pressEqualsKey() {
        var result = switch(latestOperation) {
            case "+" -> latestValue + Double.parseDouble(screen);
            case "-" -> latestValue - Double.parseDouble(screen);
            case "x" -> latestValue * Double.parseDouble(screen);
            case "/" -> latestValue / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };
        screen = Double.toString(result);
        if(screen.equals("Infinity")) screen = "Error";
        if(screen.endsWith(".0")) screen = screen.substring(0,screen.length()-2);
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
    } 
    /**
    * Führt eine binäre Rechenoperation auf zwei gegebenen Operanden aus.
    * Unterstützt Addition, Subtraktion, Multiplikation und Division.
    * Bei einer Division durch null wird der Wert Double.POSITIVE_INFINITY zurückgegeben,
    * um Rechenfehler zu vermeiden.
    * @param operation Die Rechenoperation als String: "+" für Addition, "-" für Subtraktion,
    *                  "x" für Multiplikation oder "/" für Division.
    * @param left Der linke Operand der Rechenoperation.
    * @param right Der rechte Operand der Rechenoperation.
    * @return Das Ergebnis der Rechenoperation.
    * @throws IllegalArgumentException falls eine unbekannte Operation übergeben wird.
    */

    private double applyOperation(String operation, double left, double right) {
        return switch (operation) {
            case "+" -> left + right;
            case "-" -> left - right;
            case "x" -> left * right;
            case "/" -> right == 0 ? Double.POSITIVE_INFINITY : left / right;
            default -> throw new IllegalArgumentException("Unknown operation: " + operation);
        };
    }

}