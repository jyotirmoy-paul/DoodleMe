package paul.cipherresfeber.doodleme.CustomData;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ResultData implements Serializable {

    String doodleName;
    boolean couldGuess;
    Bitmap userDrawing;

    public ResultData(String doodleName, boolean couldGuess, Bitmap userDrawing) {
        this.doodleName = doodleName;
        this.couldGuess = couldGuess;
        this.userDrawing = userDrawing;
    }

    public String getDoodleName() {
        return doodleName;
    }

    public boolean isCouldGuess() {
        return couldGuess;
    }

    public Bitmap getUserDrawing() {
        return userDrawing;
    }

}
