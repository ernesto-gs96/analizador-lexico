import java.io.*;
 
public class tipo_nom_val_variables extends Analizador{

    private String tipo_variables;
    private String nom_variables;
    private String val_variables; 

    public String gettipo_variables() {
        return tipo_variables;
    }
 
    public void settipo_variables(String tipo_variables) {
        this.tipo_variables = tipo_variables;
    }
 
    public String getnom_variables() {
        return nom_variables;
    }
 
    public void setnom_variables(String nom_variables) {
        this.nom_variables = nom_variables;
    }
 
    public String getval_variables() {
        return val_variables;
    }
 
    public void setval_variables(String val_variables) {
        this.val_variables = val_variables;
    }
 
}