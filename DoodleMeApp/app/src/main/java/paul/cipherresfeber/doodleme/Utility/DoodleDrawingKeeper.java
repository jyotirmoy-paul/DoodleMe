package paul.cipherresfeber.doodleme.Utility;

import android.graphics.Bitmap;

import java.io.Serializable;

public interface DoodleDrawingKeeper extends Serializable {
    public void keepResult(String doodleName, boolean couldGuess, Bitmap userDrawing);
}
