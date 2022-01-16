package run.tere.plugin.icerush.wrapped.nbapi;

import com.xxmicloxx.NoteBlockAPI.model.CustomInstrument;
import com.xxmicloxx.NoteBlockAPI.model.Layer;
import com.xxmicloxx.NoteBlockAPI.model.Song;

import java.io.File;
import java.util.HashMap;

public class WrappedSong extends Song {

    private short loopStartTick;

    public WrappedSong(short loopStartTick, float speed, HashMap<Integer, Layer> layerHashMap, short songHeight, short length, String title, String author, String originalAuthor, String description, File songFile, int firstcustominstrument, CustomInstrument[] customInstrumentsArray, boolean isStereo) {
        super(speed, layerHashMap, songHeight, length, title, author, originalAuthor, description, songFile, firstcustominstrument, customInstrumentsArray, isStereo);
        this.loopStartTick = loopStartTick;
    }

    public short getLoopStartTick() {
        return loopStartTick;
    }

}
